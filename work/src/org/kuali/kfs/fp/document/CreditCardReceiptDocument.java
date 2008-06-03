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
package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.BasicFormatWithLineDescriptionAccountingLineParser;
import org.kuali.module.financial.bo.CreditCardDetail;
import org.kuali.module.financial.rules.CreditCardReceiptDocumentRuleConstants;

/**
 * This is the business object that represents the CreditCardReceipt document in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since a Credit Card Receipt is a one sided transactional
 * document, only accepting funds into the university, the accounting line data will be held in the source accounting line data
 * structure only.
 */
public class CreditCardReceiptDocument extends CashReceiptFamilyBase implements Copyable, AmountTotaling {
    // holds details about each credit card receipt
    private List<CreditCardDetail> creditCardReceipts = new ArrayList<CreditCardDetail>();

    // incrementers for detail lines
    private Integer nextCcCrLineNumber = new Integer(1);

    // monetary attributes
    private KualiDecimal totalCreditCardAmount = KualiDecimal.ZERO;

    /**
     * Default constructor that calls super.
     */
    public CreditCardReceiptDocument() {
        super();
    }


    @Override
    public boolean documentPerformsSufficientFundsCheck() {
        return false;
    }

    /**
     * Gets the total credit card amount.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTotalCreditCardAmount() {
        return totalCreditCardAmount;
    }

    /**
     * This method returns the credit card total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCreditCardAmount() {
        return (String) new CurrencyFormatter().format(totalCreditCardAmount);
    }

    /**
     * Sets the total credit card amount which is the sum of all credit card receipts on this document.
     * 
     * @param creditCardAmount
     */
    public void setTotalCreditCardAmount(KualiDecimal creditCardAmount) {
        this.totalCreditCardAmount = creditCardAmount;
    }

    /**
     * Gets the list of credit card receipts which is a list of CreditCardDetail business objects.
     * 
     * @return List
     */
    public List<CreditCardDetail> getCreditCardReceipts() {
        return creditCardReceipts;
    }

    /**
     * Sets the credit card receipts list.
     * 
     * @param creditCardReceipts
     */
    public void setCreditCardReceipts(List<CreditCardDetail> creditCardReceipts) {
        this.creditCardReceipts = creditCardReceipts;
    }

    /**
     * Adds a new credit card receipt to the list.
     * 
     * @param creditCardReceiptDetail
     */
    public void addCreditCardReceipt(CreditCardDetail creditCardReceiptDetail) {
        // these three make up the primary key for a credit card detail record
        prepareNewCreditCardReceipt(creditCardReceiptDetail);

        // add the new detail record to the list
        this.creditCardReceipts.add(creditCardReceiptDetail);

        // increment line number
        this.nextCcCrLineNumber = new Integer(this.nextCcCrLineNumber.intValue() + 1);

        // update the overall amount
        this.totalCreditCardAmount = this.totalCreditCardAmount.add(creditCardReceiptDetail.getCreditCardAdvanceDepositAmount());
    }

    /**
     * This is a helper method that automatically populates document specfic information into the credit card receipt
     * (CreditCardDetail) instance.
     * 
     * @param creditCardReceiptDetail
     */
    public final void prepareNewCreditCardReceipt(CreditCardDetail creditCardReceiptDetail) {
        creditCardReceiptDetail.setFinancialDocumentLineNumber(this.nextCcCrLineNumber);
        creditCardReceiptDetail.setFinancialDocumentColumnTypeCode(KFSConstants.CreditCardReceiptConstants.CASH_RECEIPT_CREDIT_CARD_RECEIPT_COLUMN_TYPE_CODE);
        creditCardReceiptDetail.setDocumentNumber(this.getDocumentNumber());
        creditCardReceiptDetail.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(this.getClass()));
    }

    /**
     * Retrieve a particular credit card receipt at a given index in the list of credit card receipts.
     * 
     * @param index
     * @return CreditCardReceiptDetail
     */
    public CreditCardDetail getCreditCardReceipt(int index) {
        while (this.creditCardReceipts.size() <= index) {
            creditCardReceipts.add(new CreditCardDetail());
        }
        return creditCardReceipts.get(index);
    }

    /**
     * This method removes a credit card receipt from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeCreditCardReceipt(int index) {
        CreditCardDetail creditCardReceiptDetail = creditCardReceipts.remove(index);
        this.totalCreditCardAmount = this.totalCreditCardAmount.subtract(creditCardReceiptDetail.getCreditCardAdvanceDepositAmount());
    }

    /**
     * @return Integer
     */
    public Integer getNextCcCrLineNumber() {
        return nextCcCrLineNumber;
    }

    /**
     * @param nextCcCrLineNumber
     */
    public void setNextCcCrLineNumber(Integer nextCcCrLineNumber) {
        this.nextCcCrLineNumber = nextCcCrLineNumber;
    }

    /**
     * This method returns the overall total of the document - the credit card total.
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTotalDollarAmount()
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return this.totalCreditCardAmount;
    }

    /**
     * This method returns the sum of all of the credit card receipts for this document.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal calculateCreditCardReceiptTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (CreditCardDetail detail : getCreditCardReceipts()) {
            if (null != detail.getCreditCardAdvanceDepositAmount()) {
                total = total.add(detail.getCreditCardAdvanceDepositAmount());
            }
        }
        return total;
    }


    /**
     * Overrides super to call super and then also add in the new list of credit card receipts that have to be managed.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getCreditCardReceipts());

        return managedLists;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new BasicFormatWithLineDescriptionAccountingLineParser();
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
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        if (isBankCashOffsetEnabled()) {
            KualiDecimal depositTotal = calculateCreditCardReceiptTotal();
            GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
            // todo: what if the total is 0? e.g., 5 minus 5, should we generate a 0 amount GLPE and offset? I think the other rules
            // combine to prevent a 0 total, though.
            GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
            final BankAccount offsetBankAccount = getOffsetBankAccount();
            if (ObjectUtils.isNull(offsetBankAccount)) {
                success = false;
                GlobalVariables.getErrorMap().putError("newCreditCardReceipt.financialDocumentCreditCardTypeCode", KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_BANK_MUST_EXIST_WHEN_FLEXIBLE, new String[] { KFSConstants.SystemGroupParameterNames.FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG, CreditCardReceiptDocumentRuleConstants.CASH_OFFSET_BANK_ACCOUNT });
            }
            else {
                success &= glpeService.populateBankOffsetGeneralLedgerPendingEntry(offsetBankAccount, depositTotal, this, getPostingYear(), sequenceHelper, bankOffsetEntry, KFSConstants.CREDIT_CARD_RECEIPTS_LINE_ERRORS);
                // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at all if not
                // successful.
                if (success) {
                    AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
                    bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleUtil.formatProperty(KFSKeyConstants.CreditCardReceipt.DESCRIPTION_GLPE_BANK_OFFSET));
                    getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                    sequenceHelper.increment();

                    GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
                    success &= glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                    // unsuccessful offsets may be added, but that's consistent with the offsets for regular GLPEs (i.e., maybe
                    // neither
                    // should?)
                    getGeneralLedgerPendingEntries().add(offsetEntry);
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
}