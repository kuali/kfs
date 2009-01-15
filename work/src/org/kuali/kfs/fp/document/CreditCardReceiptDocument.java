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
package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.businessobject.BasicFormatWithLineDescriptionAccountingLineParser;
import org.kuali.kfs.fp.businessobject.CreditCardDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerInputTypeService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.CurrencyFormatter;

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
        creditCardReceiptDetail.setFinancialDocumentTypeCode(SpringContext.getBean(GeneralLedgerInputTypeService.class).getGeneralLedgerInputTypeByDocumentClass(this.getClass()).getInputTypeCode());
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
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTotalDollarAmount()
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
     * @see org.kuali.rice.kns.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getCreditCardReceipts());

        return managedLists;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getAccountingLineParser()
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
     * @see org.kuali.rice.kns.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.rice.kns.document.FinancialDocument,org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        
        if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            KualiDecimal depositTotal = calculateCreditCardReceiptTotal();
            GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
            
            Bank offsetBank = getOffsetBank();
            if (ObjectUtils.isNull(offsetBank)) {
                success = false;
                GlobalVariables.getErrorMap().putError("newCreditCardReceipt.financialDocumentCreditCardTypeCode", KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_BANK_MUST_EXIST_WHEN_BANK_ENHANCEMENT_ENABLED, new String[] { KFSParameterKeyConstants.ENABLE_BANK_SPECIFICATION_IND, KFSParameterKeyConstants.DEFAULT_BANK_BY_DOCUMENT_TYPE });
            }
            else {
                success &= glpeService.populateBankOffsetGeneralLedgerPendingEntry(offsetBank, depositTotal, this, getPostingYear(), sequenceHelper, bankOffsetEntry, KFSConstants.CREDIT_CARD_RECEIPTS_LINE_ERRORS);
               
                // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it 
                if (success) {
                    AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
                    bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleUtil.formatProperty(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET));
                    getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                    sequenceHelper.increment();

                    GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
                    success &= glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                    getGeneralLedgerPendingEntries().add(offsetEntry);
                    sequenceHelper.increment();
                }
            }
        }
        
        return success;
    }
    
    /**
     * Returns the default bank code for Credit Card Receipt documents.
     */
    private Bank getOffsetBank() {
        return SpringContext.getBean(BankService.class).getDefaultBankByDocType(CreditCardReceiptDocument.class);
    }
    
}
