/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.financial.bo.ServiceBillingControl;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

/**
 * Business rule(s) applicable to Service Billing documents. They differ from {@link InternalBillingDocumentRule} by not routing for
 * fiscal officer approval. Instead, they route straight to final, by a formal pre-agreement between the service provider and the
 * department being billed, based on the service provider's ability to provide documentation for all transactions. These agreements
 * are configured in the Service Billing Control table by workgroup and income account number. This class enforces those agreements.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ServiceBillingDocumentRule extends InternalBillingDocumentRule {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(TransactionalDocument,
     *      AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean success = true;
        success &= super.processCustomAddAccountingLineBusinessRules(document, accountingLine);
        // This short-circuiting pattern (in all these rule methods) is the eDoc policy, not because it is necessary, but to
        // provide the user with less helpful information and to force him to retry his submit twice. It's not throwing an
        // exception because this pattern (without the short-circuiting logic) originally implemented the opposite policy.
        if (success) {
            success &= validateOrganizationDocumentNumber(accountingLine);
        }
        return success;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(TransactionalDocument,
     *      AccountingLine)
     */
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean success = true;
        success &= super.processCustomReviewAccountingLineBusinessRules(document, accountingLine);
        if (success) {
            success &= validateOrganizationDocumentNumber(accountingLine);
        }
        return success;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(TransactionalDocument,
     *      AccountingLine, AccountingLine)
     */
    public boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument document,
            AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        boolean success = true;
        success &= super.processCustomUpdateAccountingLineBusinessRules(document, originalAccountingLine, updatedAccountingLine);
        if (success) {
            success &= validateOrganizationDocumentNumber(updatedAccountingLine);
        }
        return success;
    }

    /**
     * Validates the org doc nbr. This could be done by the DD if AccountingLine had a org doc nbr field. Using ref nbr for now,
     * which is longer than org doc nbr.
     * 
     * @param accountingLine
     * @return whether the org doc nbr is valid
     */
    private boolean validateOrganizationDocumentNumber(AccountingLine accountingLine) {
        // todo: add organizationDocumentNumber to AccountingLine, database schema, and DD instead of using referenceNumber?
        String orgDocNbr = accountingLine.getReferenceNumber();
        Integer maxLength = new Integer(10);
        if (StringUtils.isNotBlank(orgDocNbr) && orgDocNbr.length() > maxLength.intValue()) {
            String attributeLabel = SpringServiceLocator.getDataDictionaryService().getAttributeShortLabel(
                    SourceAccountingLine.class.getName(), PropertyConstants.REFERENCE_NUMBER);
            reportError(PropertyConstants.REFERENCE_NUMBER, KeyConstants.ERROR_MAX_LENGTH, new String[] { attributeLabel,
                    maxLength.toString() });
            return false;
        }
        return true;
    }

    /**
     * @see TransactionalDocumentRuleBase#accountIsAccessible(TransactionalDocument, AccountingLine)
     */
    protected boolean accountIsAccessible(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            // todo: separate rule with SB specific error message explaining about the control table instead/additionally?
            // The use from hasAccessibleAccountingLines() is not important for SB, which routes straight to final.
            return accountingLine.isTargetAccountingLine() || serviceBillingIncomeAccountIsAccessible(accountingLine);
        }
        else {
            return super.accountIsAccessible(transactionalDocument, accountingLine);
        }
    }

    /**
     * Checks the account and user against the SB control table.
     * 
     * @param accountingLine from the income section
     * @return whether the current user is authorized to use the given account in the SB income section
     */
    private boolean serviceBillingIncomeAccountIsAccessible(AccountingLine accountingLine) {
        // todo: assert accountingLine.isSourceAccountingLine();
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String accountNumber = accountingLine.getAccountNumber();
        // Handle empty key because hasAccessibleAccountingLines() may not validate beforehand.
        if (!StringUtils.isEmpty(chartOfAccountsCode) && !StringUtils.isEmpty(accountNumber)) {
            KualiUser currentUser = GlobalVariables.getUserSession().getKualiUser();
            ServiceBillingControl control = SpringServiceLocator.getServiceBillingControlService().getByPrimaryId(
                    chartOfAccountsCode, accountNumber);
            if (control != null) {
                try {
                    // todo: isMember(String) instead of going through KualiGroupService?
                    KualiGroup group = SpringServiceLocator.getKualiGroupService().getByGroupName(control.getWorkgroupName());
                    if (currentUser.isMember(group)) {
                        return true;
                    }
                }
                catch (GroupNotFoundException e) {
                    LOG.error("invalid workgroup in SB control for " + chartOfAccountsCode + accountNumber, e);
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }

    /**
     * Sets extra accounting line fields in explicit GLPE. IB doesn't have these fields.
     * 
     * @see TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        explicitEntry.setTransactionLedgerEntryDescription(accountingLine.getFinancialDocumentLineDescription());
        // todo: add organizationDocumentNumber to AccountingLine, database schema, and DD instead of using referenceNumber?
        explicitEntry.setOrganizationDocumentNumber(accountingLine.getReferenceNumber());
        explicitEntry.setReferenceFinancialDocumentNumber(null);
    }
}