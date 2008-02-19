/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.core.document.Document;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSKeyConstants.CashReceipt;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.bo.AdvanceDepositDetail;
import org.kuali.module.financial.document.AdvanceDepositDocument;
import org.kuali.module.financial.document.CashReceiptFamilyBase;

/**
 * Business rules applicable to Advance Deposit documents.
 */
public class AdvanceDepositDocumentRule extends CashReceiptFamilyRule {
    /**
     * For Advance Deposit documents, the document is balanced if the sum total of advance deposits equals the sum total of the
     * accounting lines.
     * 
     * @param financialDocument submitted financial document
     * @return true if document is balanced (i.e. sum of advance deposits equals the sum total of accounting lines)
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument financialDocument) {
        AdvanceDepositDocument ad = (AdvanceDepositDocument) financialDocument;

        // make sure the document is in balance
        boolean isValid = ad.getSourceTotal().equals(ad.getTotalDollarAmount());

        if (!isValid) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.NEW_ADVANCE_DEPOSIT, KFSKeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_OUT_OF_BALANCE);
        }

        return isValid;
    }

    /**
     * Overrides to call super and then make sure the minimum number of deposit lines exist on this document.
     * 
     * @param document submitted document
     * @return true if associated rules are all valid
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        if (isValid) {
            isValid = isMinimumNumberOfAdvanceDepositsMet(document);
        }

        return isValid;
    }

    /**
     * This method is a helper that checks to make sure that at least one deposit line exists for the document.
     * 
     * @param document submitted document
     * @return boolean true if the there is a least one deposit line for document
     */
    private boolean isMinimumNumberOfAdvanceDepositsMet(Document document) {
        AdvanceDepositDocument ad = (AdvanceDepositDocument) document;

        if (ad.getAdvanceDeposits().size() == 0) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX, KFSKeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_REQ_NUMBER_DEPOSITS_NOT_MET);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Overrides to validate all of the deposits associated with this document.
     * 
     * @param document submitted document
     * @return true if associated rules are all valid
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        if (isValid) {
            isValid &= validateAccountingLineTotal((CashReceiptFamilyBase) document);
            isValid &= validateAdvanceDeposits((AdvanceDepositDocument) document);
        }

        return isValid;
    }

    /**
     * Validates all of the Advance Deposits in the given Document, adding global errors for invalid items. It just uses the
     * DataDictionary validation.
     * 
     * @param advanceDepositDocument submitted Advance Deposit Document
     * @return boolean true if all advance deposits are valid (i.e. advance deposit amount is not 0, bank and bank account number
     *         are not blank)
     */
    private boolean validateAdvanceDeposits(AdvanceDepositDocument advanceDepositDocument) {
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;
        for (int i = 0; i < advanceDepositDocument.getAdvanceDeposits().size(); i++) {
            String propertyName = KFSPropertyConstants.ADVANCE_DEPOSIT_DETAIL + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            isValid &= AdvanceDepositDocumentRuleUtil.validateAdvanceDeposit(advanceDepositDocument.getAdvanceDepositDetail(i));
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }

        // don't bother checking the total if some deposits are broken
        if (isValid && advanceDepositDocument.getTotalAdvanceDepositAmount().isZero()) {
            isValid = false;
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ADVANCE_DEPOSIT_DETAIL, CashReceipt.ERROR_ZERO_TOTAL, "Advance Deposit Total");
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;
    }
}
