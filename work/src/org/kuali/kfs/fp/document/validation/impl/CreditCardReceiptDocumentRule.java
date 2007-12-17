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

import static org.kuali.kfs.KFSConstants.DOCUMENT_PROPERTY_NAME;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.GenerateGeneralLedgerDocumentPendingEntriesRule;
import org.kuali.kfs.rules.AccountingDocumentRuleUtil;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.document.CashReceiptFamilyBase;
import org.kuali.module.financial.document.CreditCardReceiptDocument;

/**
 * Business rules applicable to Credit Card Receipt documents.
 */
public class CreditCardReceiptDocumentRule extends CashReceiptFamilyRule implements GenerateGeneralLedgerDocumentPendingEntriesRule<AccountingDocument> {
    /**
     * For Credit Card Receipt documents, the document is balanced if the sum total of credit card receipts equals the sum total of
     * the accounting lines.
     * 
     * @param financialDocument submitted accoutting document
     * @return true if cash credit receipt document equals the cash credit document total dollar amount
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument finanacialDocument) {
        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) finanacialDocument;

        // make sure the document is in balance
        boolean isValid = ccr.getSourceTotal().equals(ccr.getTotalDollarAmount());

        if (!isValid) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.NEW_CREDIT_CARD_RECEIPT, KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_OUT_OF_BALANCE);
        }

        return isValid;
    }

    /**
     * Overrides to call super and then make sure the minimum number of credit card receipt lines exist on this document.
     * 
     * @param document submitted document
     * @return true if super method returns true and there is at least one credit card receipt
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
     * @param document submitted document
     * @return boolean return true if there is at least one credit card receipt
     */
    private boolean isMinimumNumberOfCreditCardReceiptsMet(Document document) {
        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) document;

        if (ccr.getCreditCardReceipts().size() == 0) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.NEW_CREDIT_CARD_RECEIPT, KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_REQ_NUMBER_RECEIPTS_NOT_MET);
            return false;
        }
        return true;
    }

    /**
     * Validates all the CreditCardReceipts in the given Document.
     * 
     * @param creditCardReceiptDocument submitted credit card receipt document
     * @return true if all credit cards are valid (i.e. each credit card receipt has a non-zero amount and has valid 
     *         credit card vendor and type references) 
     */
    private boolean validateCreditCardReceipts(CreditCardReceiptDocument creditCardReceiptDocument) {
        GlobalVariables.getErrorMap().addToErrorPath(DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;
        for (int i = 0; i < creditCardReceiptDocument.getCreditCardReceipts().size(); i++) {
            String propertyName = KFSPropertyConstants.CREDIT_CARD_RECEIPT + "[" + i + "]";
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
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class for keep track of sequence for GLPEs
     * @return true if generation of GLPE's is successful for credit card receipt document
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
            final BankAccount offsetBankAccount = getOffsetBankAccount();
            if (ObjectUtils.isNull(offsetBankAccount)) {
                success = false;
                GlobalVariables.getErrorMap().putError("newCreditCardReceipt.financialDocumentCreditCardTypeCode", KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_BANK_MUST_EXIST_WHEN_FLEXIBLE, new String[] { KFSConstants.SystemGroupParameterNames.FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG, CreditCardReceiptDocumentRuleConstants.CASH_OFFSET_BANK_ACCOUNT });
            }
            else {
                success &= AccountingDocumentRuleUtil.populateBankOffsetGeneralLedgerPendingEntry(offsetBankAccount, depositTotal, ccrDoc, ccrDoc.getPostingYear(), sequenceHelper, bankOffsetEntry, KFSConstants.CREDIT_CARD_RECEIPTS_LINE_ERRORS);
                // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at all if not
                // successful.
                if (success) {
                    bankOffsetEntry.setTransactionLedgerEntryDescription(AccountingDocumentRuleUtil.formatProperty(KFSKeyConstants.CreditCardReceipt.DESCRIPTION_GLPE_BANK_OFFSET));
                    ccrDoc.getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                    sequenceHelper.increment();

                    GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(bankOffsetEntry);
                    success &= populateOffsetGeneralLedgerPendingEntry(ccrDoc.getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                    // unsuccessful offsets may be added, but that's consistent with the offsets for regular GLPEs (i.e., maybe
                    // neither
                    // should?)
                    ccrDoc.getGeneralLedgerPendingEntries().add(offsetEntry);
                    sequenceHelper.increment();
                }
            }
        }
        return success;
    }

    /**
     * Returns a credit cards flexible offset bank account
     * 
     * @return the Credit Card Receipt's flexible offset bank account, as configured in the APC.
     * @throws ApplicationParameterException if the CCR offset BankAccount is not defined in the APC.
     */
    private BankAccount getOffsetBankAccount() {
        final String[] parameterValues = SpringContext.getBean(ParameterService.class).getParameterValues(CreditCardReceiptDocument.class, CreditCardReceiptDocumentRuleConstants.CASH_OFFSET_BANK_ACCOUNT).toArray(new String[] {});
        if (parameterValues.length != 2) {
            throw new RuntimeException(CreditCardReceiptDocument.class.getSimpleName() + "/" + CreditCardReceiptDocumentRuleConstants.CASH_OFFSET_BANK_ACCOUNT + ": invalid parameter format: must be 'bankCode;bankAccountNumber'");
        }
        final String bankCode = parameterValues[0];
        final String bankAccountNumber = parameterValues[1];
        final Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_CODE, bankCode);
        primaryKeys.put(KFSPropertyConstants.FIN_DOCUMENT_BANK_ACCOUNT_NUMBER, bankAccountNumber);
        return (BankAccount) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BankAccount.class, primaryKeys);
    }
    /**
     * We are overriding here and always returning true since we don't care about 
     * cash drawers for this doc type but want the other rules in the super class.
     * @see org.kuali.module.financial.rules.CashReceiptFamilyRule#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        return true;
}
}
