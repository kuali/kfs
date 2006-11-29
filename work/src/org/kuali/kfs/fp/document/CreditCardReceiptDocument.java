/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/document/CreditCardReceiptDocument.java,v $
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
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.module.financial.bo.BasicFormatWithLineDescriptionAccountingLineParser;
import org.kuali.module.financial.bo.CreditCardDetail;

/**
 * This is the business object that represents the CreditCardReceipt document in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since a Credit Card Receipt is a one sided transactional
 * document, only accepting funds into the university, the accounting line data will be held in the source accounting line data
 * structure only.
 * 
 * 
 */
public class CreditCardReceiptDocument extends CashReceiptFamilyBase {
    // holds details about each credit card receipt
    private List<CreditCardDetail> creditCardReceipts = new ArrayList<CreditCardDetail>();

    // incrementers for detail lines
    private Integer nextCcCrLineNumber = new Integer(1);

    // monetary attributes
    private KualiDecimal totalCreditCardAmount = new KualiDecimal(0);

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
        creditCardReceiptDetail.setFinancialDocumentColumnTypeCode(Constants.CreditCardReceiptConstants.CASH_RECEIPT_CREDIT_CARD_RECEIPT_COLUMN_TYPE_CODE);
        creditCardReceiptDetail.setDocumentNumber(this.getDocumentNumber());
        creditCardReceiptDetail.setFinancialDocumentTypeCode(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(this.getClass()));
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
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getSumTotalAmount() {
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
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new BasicFormatWithLineDescriptionAccountingLineParser();
    }
}