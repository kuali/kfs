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

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import static org.kuali.PropertyConstants.REFERENCE_NUMBER;
import static org.kuali.PropertyConstants.REFERENCE_ORIGIN_CODE;
import static org.kuali.PropertyConstants.REVERSAL_DATE;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.financial.document.PreEncumbranceDocument;
import static org.kuali.module.financial.rules.PreEncumbranceDocumentRuleConstants.PRE_ENCUMBRANCE_DOCUMENT_SECURITY_GROUPING;
import static org.kuali.module.financial.rules.PreEncumbranceDocumentRuleConstants.RESTRICTED_OBJECT_TYPE_CODES;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;


/**
 * Business rule(s) applicable to PreEncumbrance documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class PreEncumbranceDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * @see TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        return isRequiredReferenceFieldsValid(accountingLine);
    }

    /**
     * @see TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return isRequiredReferenceFieldsValid(updatedAccountingLine);
    }

    /**
     * @see TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        return isRequiredReferenceFieldsValid(accountingLine);
    }

    /**
     * This method checks that values exist in a disencumbrance line's two reference fields. This cannot be done by the
     * DataDictionary validation because not all documents require them.
     * 
     * @param accountingLine
     * 
     * @return True if all of the required external encumbrance reference fields are valid, false otherwise.
     */
    private boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;

        if (isTargetAccountingLine(accountingLine)) {
            BusinessObjectEntry boe = SpringServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(TargetAccountingLine.class);
            if (StringUtils.isEmpty(accountingLine.getReferenceOriginCode())) {
                putRequiredPropertyError(boe, REFERENCE_ORIGIN_CODE);
                valid = false;
            }
            if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
                putRequiredPropertyError(boe, REFERENCE_NUMBER);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * @see TransactionalDocumentRuleBase#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        KualiParameterRule combinedRule = KualiParameterRule.and(getGlobalObjectTypeRule(), getParameterRule(PRE_ENCUMBRANCE_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES));
        AttributeReference direct = createObjectCodeAttributeReference(accountingLine);
        AttributeReference indirect = createObjectTypeAttributeReference(accountingLine);
        boolean allowed = indirectRuleSucceeds(combinedRule, direct, indirect);
        if (allowed) {
            allowed &= super.isObjectTypeAllowed(accountingLine);
        }
        return allowed;
    }

    /**
     * Pre Encumbrance document specific business rule checks for the "route document" event.
     * 
     * @param document
     * 
     * @return boolean True if the rules checks passed, false otherwise.
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean valid = isReversalDateValidForRouting((PreEncumbranceDocument) document);
        // this short-circuiting is just because it's the current eDocs policy (altho I think it's user-unfriendly)
        if (valid) {
            // super is to call isAccountingLinesRequiredNumberForRoutingMet()
            valid &= super.processCustomRouteDocumentBusinessRules(document);
        }
        return valid;
    }

    /**
     * If a PreEncumbrance document has a reversal date, it must not be earlier than the current date to route.
     * 
     * @param preEncumbranceDocument
     * 
     * @return boolean True if this document does not have a reversal date earlier than the current date, false otherwise.
     */
    private boolean isReversalDateValidForRouting(PreEncumbranceDocument preEncumbranceDocument) {
        Timestamp reversalDate = preEncumbranceDocument.getReversalDate();
        return TransactionalDocumentRuleUtil.isValidReversalDate(reversalDate, DOCUMENT_ERROR_PREFIX + REVERSAL_DATE);
    }

    /**
     * PreEncumbrance documents require at least one accounting line in either section for routing.
     * 
     * @param transactionalDocument
     * 
     * @return boolean True if the number of accounting lines are valid for routing, false otherwise.
     */
    @Override
    protected boolean isAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        if (0 == transactionalDocument.getSourceAccountingLines().size() && 0 == transactionalDocument.getTargetAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * PreEncumbrance documents don't need to balance in any way.
     * 
     * @see TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * This method contains Pre Encumbrance document specific GLPE explicit entry attribute assignments.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_CODE.PRE_ENCUMBRANCE);

        // set the reversal date to what was chosen by the user in the interface
        PreEncumbranceDocument peDoc = (PreEncumbranceDocument) transactionalDocument;
        if (peDoc.getReversalDate() != null) {
            explicitEntry.setFinancialDocumentReversalDate(new java.sql.Date(peDoc.getReversalDate().getTime()));
        }
        explicitEntry.setTransactionEntryProcessedTs(null);
        if (isSourceAccountingLine(accountingLine)) {
            explicitEntry.setTransactionEncumbranceUpdateCode(ENCUMBRANCE_UPDATE_CODES.ENCUMBRANCE);
        }
        else {
            assert isTargetAccountingLine(accountingLine) : accountingLine;
            explicitEntry.setTransactionEncumbranceUpdateCode(ENCUMBRANCE_UPDATE_CODES.DISENCUMBRANCE);
            explicitEntry.setReferenceFinancialSystemOriginationCode(accountingLine.getReferenceOriginCode());
            explicitEntry.setReferenceFinancialDocumentNumber(accountingLine.getReferenceNumber());
            explicitEntry.setReferenceFinancialDocumentTypeCode(REFERENCE_DOCUMENT_TYPE_CODES.PRE_ENCUMBRANCE);
        }
    }

    /**
     * This method contains Pre Encumbrance document specific GLPE offset entry attribute assignments.
     * 
     * @see TransactionalDocumentRuleBase#customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry, GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean success = true;
        offsetEntry.setFinancialObjectTypeCode(explicitEntry.getFinancialObjectTypeCode());
        OffsetDefinition offsetDefinition = SpringServiceLocator.getOffsetDefinitionService().getByPrimaryId(transactionalDocument.getPostingYear(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        if (ObjectUtils.isNull(offsetDefinition)) {
            success = false; // The superclass already puts an error for a missing offset definition, so do nothing here.
        }
        else {
            offsetEntry.setFinancialSubObjectCode(offsetDefinition.getFinancialSubObjectCode());
        }
        return success;
    }

    /**
     * limits only to expense object type codes
     * 
     * @see IsDebitUtils#isDebitConsideringSection(TransactionalDocumentRuleBase, TransactionalDocument,
     *      AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        // if not expense, or positive amount on an error-correction, or negative amount on a non-error-correction, throw exception
        if (!isExpense(accountingLine) || (isErrorCorrection(transactionalDocument) == accountingLine.getAmount().isPositive())) {
            throw new IllegalStateException(IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage);
        }

        return !IsDebitUtils.isDebitConsideringSection(this, transactionalDocument, accountingLine);
    }
}
