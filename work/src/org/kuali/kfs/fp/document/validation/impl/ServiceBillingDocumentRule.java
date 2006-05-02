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
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.SpringServiceLocator;

/**
 * Business rule(s) applicable to Service Billing documents.
 * They differ from {@link InternalBillingDocumentRule} by not routing for fiscal officer approval.
 * Instead, they route straight to final, by a formal pre-agreement between the service provider and the department being billed,
 * based on the service provider's ability to provide documentation for all transactions.  These agreements are configured
 * in the Service Billing Control table by workgroup and income account number.  This class enforces those agreements.
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
        // provide the user with less helpful information and to force him to retry his submit twice.  It's not throwing an
        // exception because this pattern (without the short-circuiting logic) originally implemented the opposite policy.
        if (success) {
            success &= processCommonCustomAccountingLineBusinessRules(accountingLine);
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
            success &= processCommonCustomAccountingLineBusinessRules(accountingLine);
        }
        return success;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(TransactionalDocument,
     *      AccountingLine, AccountingLine)
     */
    public boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument document,
                                                                  AccountingLine originalAccountingLine,
                                                                  AccountingLine updatedAccountingLine)
    {
        boolean success = true;
        success &= super.processCustomUpdateAccountingLineBusinessRules(document, originalAccountingLine, updatedAccountingLine);
        if (success) {
            success &= processCommonCustomAccountingLineBusinessRules(updatedAccountingLine);
        }
        return success;
    }

    /**
     * Processes rules common to the three custom accounting line rule methods.
     *
     * @param accountingLine
     *
     * @return whether the rule succeeds
     */
    private boolean processCommonCustomAccountingLineBusinessRules(AccountingLine accountingLine) {
        boolean success = true;
        success &= validateOrganizationDocumentNumber(accountingLine);
        if (success) {
            success &= validateIncomeAccount(accountingLine);
        }
        return success;
    }

    private boolean validateOrganizationDocumentNumber(AccountingLine accountingLine) {
        // todo: add organizationDocumentNumber to AccountingLine, database schema, and DD instead of using referenceNumber?
        String orgDocNbr = accountingLine.getReferenceNumber();
        Integer maxLength = new Integer(10);
        if (StringUtils.isNotBlank(orgDocNbr) && orgDocNbr.length() > maxLength.intValue()) {
            String attributeLabel = SpringServiceLocator.getDataDictionaryService().getAttributeShortLabel(
                SourceAccountingLine.class.getName(), PropertyConstants.REFERENCE_NUMBER);
            reportError(PropertyConstants.REFERENCE_NUMBER, KeyConstants.ERROR_MAX_LENGTH,
                new String[]{attributeLabel, maxLength.toString()});
            return false;
        }
        return true;
    }

    private boolean validateIncomeAccount(AccountingLine accountingLine) {
        // todo: SB control table check
        return true;
    }

    /**
     * Sets extra accounting line fields in explicit GLPE.
     * IB doesn't have these fields.
     *
     * @see TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine, GeneralLedgerPendingEntry)
     */
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument,
                                                              AccountingLine accountingLine,
                                                              GeneralLedgerPendingEntry explicitEntry)
    {
        explicitEntry.setTransactionLedgerEntryDescription(accountingLine.getFinancialDocumentLineDescription());
        // todo: add organizationDocumentNumber to AccountingLine, database schema, and DD instead of using referenceNumber?
        explicitEntry.setOrganizationDocumentNumber(accountingLine.getReferenceNumber());
        explicitEntry.setReferenceFinancialDocumentNumber(null);
    }

    /**
     * Sets extra accounting line field in offset GLPE.
     * The offset description remains {@link org.kuali.Constants#GL_PE_OFFSET_STRING}.
     *
     * @see TransactionalDocumentRuleBase#customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry)
     */
    protected boolean customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument,
                                                               AccountingLine accountingLine,
                                                               GeneralLedgerPendingEntry explicitEntry,
                                                               GeneralLedgerPendingEntry offsetEntry)
    {
        // todo: add organizationDocumentNumber to AccountingLine, database schema, and DD instead of using referenceNumber?
        explicitEntry.setOrganizationDocumentNumber(accountingLine.getReferenceNumber());
        explicitEntry.setReferenceFinancialDocumentNumber(null);
        return true;
    }
}