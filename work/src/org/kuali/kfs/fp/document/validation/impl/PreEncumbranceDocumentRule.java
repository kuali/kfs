/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.rules;

import static org.kuali.core.util.AssertionUtils.assertThat;
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_PRE_ENCUMBRANCE;
import static org.kuali.kfs.KFSPropertyConstants.REFERENCE_NUMBER;
import static org.kuali.kfs.KFSPropertyConstants.REVERSAL_DATE;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.document.Document;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.service.HomeOriginationService;
import org.kuali.module.financial.document.PreEncumbranceDocument;

/**
 * Business rule(s) applicable to PreEncumbrance documents.
 */
public class PreEncumbranceDocumentRule extends AccountingDocumentRuleBase {

    /**
     * This method performs business rule checks on the accounting line being added to the document to ensure the accounting line
     * is valid and appropriate for the document.  Currently, this method calls isRequiredReferenceFieldsValid() 
     * associated with the new accounting line.  
     * 
     * @param financialDocument The document the new line is being added to.
     * @param accountingLine The new accounting line being added.
     * @return True if the business rules all pass, false otherwise.
     * 
     * @see FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        return isRequiredReferenceFieldsValid(accountingLine);
    }

    /**
     * This method performs business rule checks on the accounting line being updated to the document to ensure the accounting line
     * is valid and appropriate for the document.  Currently, this method calls isRequiredReferenceFieldsValid() 
     * associated with the updated accounting line.  
     * 
     * @param transactionalDocument The document the accounting line being updated resides within.
     * @param accountingLine The original accounting line.
     * @param updatedAccoutingLine The updated version of the accounting line.
     * @return True if the business rules all pass for the update, false otherwise.
     * 
     * @see FinancialDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return isRequiredReferenceFieldsValid(updatedAccountingLine);
    }

    /**
     * This method performs business rule checks on the accounting line being provided to ensure the accounting line
     * is valid and appropriate for the document.  
     * 
     * @param transactionalDocument The document associated with the accounting line being validated.
     * @param accountingLine The accounting line being validated.
     * @return True if the business rules all pass, false otherwise.
     * 
     * @see FinancialDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomReviewAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        return isRequiredReferenceFieldsValid(accountingLine);
    }

    /**
     * This method checks that there is a value in a disencumbrance line's reference number field. This cannot be 
     * done by the DataDictionary validation because not all documents require it. It does not validate the 
     * existence of the referenced document.
     * 
     * @param accountingLine The accounting line the reference field is being retrieved from.
     * @return True if the required external encumbrance reference field is valid, false otherwise.
     */
    private boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;

        if (accountingLine.isTargetAccountingLine()) {
            BusinessObjectEntry boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(TargetAccountingLine.class.getName());
            if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
                putRequiredPropertyError(boe, REFERENCE_NUMBER);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * This method performs custom route business rule checks on the document being routed.  The rules include 
     * confirming that the reversal date is valid for routing.
     * 
     * @param document The document being routed.
     * @return True if all the business rules pass, false otherwise.
     * 
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
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
     * @param preEncumbranceDocument The document the reversal date is retrieved from.
     * @return True if this document does not have a reversal date earlier than the current date, false otherwise.
     */
    private boolean isReversalDateValidForRouting(PreEncumbranceDocument preEncumbranceDocument) {
        java.sql.Date reversalDate = preEncumbranceDocument.getReversalDate();
        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        return accountingDocumentRuleUtil.isValidReversalDate(reversalDate, DOCUMENT_ERROR_PREFIX + REVERSAL_DATE);
    }

    /**
     * PreEncumbrance documents require at least one source and one target accounting line for routing.
     * 
     * @param financialDocument The document being routed.
     * @return True if the number of source and target accounting lines are both greater than zero, false otherwise.
     * 
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isAccountingLinesRequiredNumberForRoutingMet(org.kuali.kfs.document.AccountingDocument)
     */
    @Override
    protected boolean isAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        if (0 == financialDocument.getSourceAccountingLines().size() && 0 == financialDocument.getTargetAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * PreEncumbrance documents don't need to balance in any way.
     * 
     * @param financialDocument The document whose bablance is being validated.
     * @return Always returns true.
     * 
     * @see FinancialDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument financialDocument) {
        return true;
    }
}
