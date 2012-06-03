/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
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

import org.kuali.kfs.fp.businessobject.CreditCardDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the business object that represents the CreditCardReceipt document in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since a Credit Card Receipt is a one sided transactional
 * document, only accepting funds into the university, the accounting line data will be held in the source accounting line data
 * structure only.
 */
public class CreditCardReceiptDocument extends CashReceiptFamilyBase implements Copyable, AmountTotaling {
    public static final String CREDIT_CARD_RECEIPT_DOCUMENT_TYPE_CODE = "CCR";

    // holds details about each credit card receipt
    protected List<CreditCardDetail> creditCardReceipts = new ArrayList<CreditCardDetail>();

    // incrementers for detail lines
    protected Integer nextCcCrLineNumber = new Integer(1);

    // monetary attributes
    protected KualiDecimal totalCreditCardAmount = KualiDecimal.ZERO;
    protected String creditCardReceiptBankCode;
    protected Bank bank;
    
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
        creditCardReceiptDetail.setDocumentNumber(this.getDocumentNumber());
        creditCardReceiptDetail.setFinancialDocumentTypeCode(SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass()));
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
     * @see org.kuali.rice.krad.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getCreditCardReceipts());

        return managedLists;
    }
    
    /**
     * Generates bank offset GLPEs for deposits, if enabled.
     * 
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class for keep track of sequence for GLPEs
     * @return true if generation of GLPE's is successful for credit card receipt document
     * 
     * @see org.kuali.rice.krad.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.rice.krad.document.FinancialDocument,org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        
        if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            final KualiDecimal bankOffsetAmount = glpeService.getOffsetToCashAmount(this).negated();
            GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
            
            Bank offsetBank = getOffsetBank();
            if (ObjectUtils.isNull(offsetBank)) {
                success = false;
                GlobalVariables.getMessageMap().putError("newCreditCardReceipt.financialDocumentCreditCardTypeCode", KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_BANK_MUST_EXIST_WHEN_BANK_ENHANCEMENT_ENABLED, new String[] { KFSParameterKeyConstants.ENABLE_BANK_SPECIFICATION_IND, KFSParameterKeyConstants.DEFAULT_BANK_BY_DOCUMENT_TYPE });
            }
            else {
                success &= glpeService.populateBankOffsetGeneralLedgerPendingEntry(offsetBank, bankOffsetAmount, this, getPostingYear(), sequenceHelper, bankOffsetEntry, KFSConstants.CREDIT_CARD_RECEIPTS_LINE_ERRORS);
               
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
     * Assigns default bank code
     */
    public void initiateDocument() {
        // default bank code
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(this.getClass());
        if (defaultBank != null) {
            this.creditCardReceiptBankCode = defaultBank.getBankCode();
            this.bank = defaultBank;
        }
    }
    
    /**
     * Returns the default bank code for Credit Card Receipt documents.
     */
    protected Bank getOffsetBank() {
        return SpringContext.getBean(BankService.class).getByPrimaryId(creditCardReceiptBankCode);
    }
    
    /**
     * Gets the creditCardReceiptBankCode attribute. 
     * @return Returns the creditCardReceiptBankCode.
     */
    public String getCreditCardReceiptBankCode() {
        return creditCardReceiptBankCode;
    }


    /**
     * Sets the creditCardReceiptBankCode attribute value.
     * @param creditCardReceiptBankCode The creditCardReceiptBankCode to set.
     */
    public void setCreditCardReceiptBankCode(String creditCardReceiptBankCode) {
        this.creditCardReceiptBankCode = creditCardReceiptBankCode;
    }


    /**
     * Gets the bank attribute. 
     * @return Returns the bank.
     */
    public Bank getBank() {
        return bank;
    }


    /**
     * Sets the bank attribute value.
     * @param bank The bank to set.
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }


    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
            this.getCapitalAssetManagementModuleService().generateCapitalAssetLock(this,documentTypeName);
        }        
    }
    
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        this.getCapitalAssetManagementModuleService().deleteDocumentAssetLocks(this); 
    }
}
