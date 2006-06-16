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
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.financial.bo.ServiceBillingControl;
import static org.kuali.module.financial.rules.ServiceBillingDocumentRuleConstants.RESTRICTED_OBJECT_TYPE_CODES;
import static org.kuali.module.financial.rules.ServiceBillingDocumentRuleConstants.SERVICE_BILLING_DOCUMENT_SECURITY_GROUPING;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

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
     * @see TransactionalDocumentRuleBase#accountIsAccessible(TransactionalDocument, AccountingLine)
     */
    @Override
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
            ServiceBillingControl control = SpringServiceLocator.getServiceBillingControlService().getByPrimaryId(chartOfAccountsCode, accountNumber);
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
     * @see org.kuali.module.financial.rules.InternalBillingDocumentRule#getObjectTypeRule()
     */
    @Override
    protected KualiParameterRule getObjectTypeRule() {
        return KualiParameterRule.and(super.getObjectTypeRule(), getParameterRule(SERVICE_BILLING_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES));
    }

    /**
     * Sets extra accounting line field in explicit GLPE. IB doesn't have this field.
     * 
     * @see TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        String description = accountingLine.getFinancialDocumentLineDescription();
        if (StringUtils.isNotBlank(description)) {
            explicitEntry.setTransactionLedgerEntryDescription(description);
        }
    }
}