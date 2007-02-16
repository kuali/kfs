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

import java.util.HashMap;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ApplicationParameterException;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.GenerateGeneralLedgerDocumentPendingEntriesRule;
import org.kuali.kfs.rules.AccountingDocumentRuleUtil;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.document.CashReceiptFamilyBase;
import org.kuali.module.financial.document.CreditCardReceiptDocument;

import static org.kuali.Constants.DOCUMENT_PROPERTY_NAME;
import static org.kuali.KeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_TOTAL_INVALID;
import static org.kuali.PropertyConstants.CREDIT_CARD_RECEIPTS_TOTAL;

/**
 * Business rules applicable to Credit Card Receipt documents.
 */
public class CreditCardReceiptDocumentRule extends CashReceiptFamilyRule implements GenerateGeneralLedgerDocumentPendingEntriesRule<AccountingDocument> {
    /**
     * For Credit Card Receipt documents, the document is balanced if the sum total of credit card receipts equals the sum total of
     * the accounting lines.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument FinancialDocument) {
        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) FinancialDocument;

        // make sure the document is in balance
        boolean isValid = ccr.getSourceTotal().equals(ccr.getTotalDollarAmount());

        if (!isValid) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.NEW_CREDIT_CARD_RECEIPT, KeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_OUT_OF_BALANCE);
        }

        return isValid;
    }

    /**
     * Overrides to call super and then make sure the minimum number of credit card receipt lines exist on this document.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        
        isValid &= isMinimumNumberOfCreditCardReceiptsMet(document);
        
        if (isValid) {
            isValid &= validateAccountingLineTotal((CashReceiptFamilyBase) document);
            isValid &= !CreditCardReceiptDocumentRuleUtil.areCashTotalsInvalid((CreditCardReceiptDocument) document);
        }
        
        if (isValid) {
            isValid &= validateCreditCardReceipts((CreditCardReceiptDocument) document);
        }

        return isValid;
    }
    
    /**
     * This method is a helper that checks to make sure that at least one credit card receipt line exists for the document.
     * 
     * @param document
     * @return boolean
     */
    private boolean isMinimumNumberOfCreditCardReceiptsMet(Document document) {
        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) document;

        if (ccr.getCreditCardReceipts().size() == 0) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.NEW_CREDIT_CARD_RECEIPT, KeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_REQ_NUMBER_RECEIPTS_NOT_MET);
            return false;
        }
        return true;
    }

    /**
     * Validates all the CreditCardReceipts in the given Document.
     * 
     * @param creditCardReceiptDocument
     * @return boolean
     */
    private boolean validateCreditCardReceipts(CreditCardReceiptDocument creditCardReceiptDocument) {
        GlobalVariables.getErrorMap().addToErrorPath(DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;
        for (int i = 0; i < creditCardReceiptDocument.getCreditCardReceipts().size(); i++) {
            String propertyName = PropertyConstants.CREDIT_CARD_RECEIPT + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            isValid &= CreditCardReceiptDocumentRuleUtil.validateCreditCardReceipt(creditCardReceiptDocument.getCreditCardReceipt(i));
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(DOCUMENT_PROPERTY_NAME);
        return isValid;
    }

    /**
     * Generates bank offset GLPEs for deposits, if enabled.
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateDocumentGeneralLedgerPendingEntries(AccountingDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        CreditCardReceiptDocument ccrDoc = (CreditCardReceiptDocument) financialDocument;
        if (ccrDoc.isBankCashOffsetEnabled()) {
            KualiDecimal depositTotal = ccrDoc.calculateCreditCardReceiptTotal();
            // todo: what if the total is 0? e.g., 5 minus 5, should we generate a 0 amount GLPE and offset? I think the other rules
            // combine to prevent a 0 total, though.
            GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
            success &= AccountingDocumentRuleUtil.populateBankOffsetGeneralLedgerPendingEntry(getOffsetBankAccount(), depositTotal, ccrDoc, ccrDoc.getPostingYear(), sequenceHelper, bankOffsetEntry, Constants.CREDIT_CARD_RECEIPTS_LINE_ERRORS);
            // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at all if not
            // successful.
            if (success) {
                bankOffsetEntry.setTransactionLedgerEntryDescription(AccountingDocumentRuleUtil.formatProperty(KeyConstants.CreditCardReceipt.DESCRIPTION_GLPE_BANK_OFFSET));
                ccrDoc.getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                sequenceHelper.increment();

                GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(bankOffsetEntry);
                success &= populateOffsetGeneralLedgerPendingEntry(ccrDoc.getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                // unsuccessful offsets may be added, but that's consistent with the offsets for regular GLPEs (i.e., maybe neither
                // should?)
                ccrDoc.getGeneralLedgerPendingEntries().add(offsetEntry);
                sequenceHelper.increment();
            }
        }
        return success;
    }

    /**
     * @return the Credit Card Receipt's flexible offset bank account, as configured in the APC.
     * 
     * @throws ApplicationParameterException if the CCR offset BankAccount is not defined in the APC.
     */
    private BankAccount getOffsetBankAccount() {
        final String scriptName = CreditCardReceiptDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_CREDIT_CARD_RECEIPT_SECURITY_GROUPING;
        final String parameter = CreditCardReceiptDocumentRuleConstants.CASH_OFFSET_BANK_ACCOUNT;
        final String[] parameterValues = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(scriptName, parameter);
        if (parameterValues.length != 2) {
            throw new ApplicationParameterException(scriptName, parameter, "invalid parameter format: must be 'bankCode;bankAccountNumber'");
        }
        final String bankCode = parameterValues[0];
        final String bankAccountNumber = parameterValues[1];
        final Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(PropertyConstants.FINANCIAL_DOCUMENT_BANK_CODE, bankCode);
        primaryKeys.put(PropertyConstants.FIN_DOCUMENT_BANK_ACCOUNT_NUMBER, bankAccountNumber);
        final BankAccount offsetBankAccount = (BankAccount) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(BankAccount.class, primaryKeys);
        if (ObjectUtils.isNull(offsetBankAccount)) {
            throw new ApplicationParameterException(scriptName, parameter, "invalid parameter contents: bank " + bankCode + " account " + bankAccountNumber + " does not exist.");
        }
        return offsetBankAccount;
    }
}
