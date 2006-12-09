/*
 * Copyright 2006 The Kuali Foundation.
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

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.KeyConstants.CashReceipt;
import org.kuali.core.document.Document;
import org.kuali.core.document.FinancialDocument;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.financial.bo.AdvanceDepositDetail;
import org.kuali.module.financial.document.AdvanceDepositDocument;
import org.kuali.module.financial.document.CashReceiptFamilyBase;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

/**
 * Business rules applicable to Advance Deposit documents.
 * 
 * 
 */
public class AdvanceDepositDocumentRule extends CashReceiptFamilyRule implements GenerateGeneralLedgerDocumentPendingEntriesRule {
    /**
     * For Advance Deposit documents, the document is balanced if the sum total of advance deposits equals the sum total of the
     * accounting lines.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        AdvanceDepositDocument ad = (AdvanceDepositDocument) transactionalDocument;

        // make sure the document is in balance
        boolean isValid = ad.getSourceTotal().equals(ad.getSumTotalAmount());

        if (!isValid) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.NEW_ADVANCE_DEPOSIT, KeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_OUT_OF_BALANCE);
        }

        return isValid;
    }

    /**
     * Overrides to call super and then make sure the minimum number of deposit lines exist on this document.
     * 
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
     * @param document
     * @return boolean
     */
    private boolean isMinimumNumberOfAdvanceDepositsMet(Document document) {
        AdvanceDepositDocument ad = (AdvanceDepositDocument) document;

        if (ad.getAdvanceDeposits().size() == 0) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX, KeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_REQ_NUMBER_DEPOSITS_NOT_MET);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Overrides to validate all of the deposits associated with this document.
     * 
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
     * @param advanceDepositDocument
     * @return boolean
     */
    private boolean validateAdvanceDeposits(AdvanceDepositDocument advanceDepositDocument) {
        GlobalVariables.getErrorMap().addToErrorPath(Constants.DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;
        for (int i = 0; i < advanceDepositDocument.getAdvanceDeposits().size(); i++) {
            String propertyName = PropertyConstants.ADVANCE_DEPOSIT_DETAIL + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            isValid &= AdvanceDepositDocumentRuleUtil.validateAdvanceDeposit(advanceDepositDocument.getAdvanceDepositDetail(i));
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }

        // don't bother checking the total if some deposits are broken
        if (isValid && advanceDepositDocument.getTotalAdvanceDepositAmount().isZero()) {
            isValid = false;
            GlobalVariables.getErrorMap().putError(PropertyConstants.ADVANCE_DEPOSIT_DETAIL, CashReceipt.ERROR_ZERO_TOTAL, "Advance Deposit Total");
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(Constants.DOCUMENT_PROPERTY_NAME);
        return isValid;
    }

    /**
     * Generates bank offset GLPEs for deposits, if enabled.
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateDocumentGeneralLedgerPendingEntries(FinancialDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        final AdvanceDepositDocument advanceDepositDocument = ((AdvanceDepositDocument) financialDocument);
        if (advanceDepositDocument.isBankCashOffsetEnabled()) {
            int displayedDepositNumber = 1;
            for (AdvanceDepositDetail detail : advanceDepositDocument.getAdvanceDeposits()) {
                detail.refreshReferenceObject(PropertyConstants.FINANCIAL_DOCUMENT_BANK_ACCOUNT);

                GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
                if (!TransactionalDocumentRuleUtil.populateBankOffsetGeneralLedgerPendingEntry(detail.getFinancialDocumentBankAccount(), detail.getFinancialDocumentAdvanceDepositAmount(), advanceDepositDocument, advanceDepositDocument.getPostingYear(), sequenceHelper, bankOffsetEntry, Constants.ADVANCE_DEPOSITS_LINE_ERRORS)) {
                    success = false;
                    continue; // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at
                    // all.
                }
                bankOffsetEntry.setTransactionLedgerEntryDescription(TransactionalDocumentRuleUtil.formatProperty(KeyConstants.AdvanceDeposit.DESCRIPTION_GLPE_BANK_OFFSET, displayedDepositNumber++));
                advanceDepositDocument.getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                sequenceHelper.increment();

                GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(bankOffsetEntry);
                success &= populateOffsetGeneralLedgerPendingEntry(advanceDepositDocument.getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                advanceDepositDocument.getGeneralLedgerPendingEntries().add(offsetEntry);
                sequenceHelper.increment();
            }
        }
        return success;
    }
}
