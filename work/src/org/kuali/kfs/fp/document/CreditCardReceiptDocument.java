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
package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.format.CurrencyFormatter;

/**
 * This is the business object that represents the CreditCardReceipt document in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since a Credit Card Receipt is a one sided transactional document,
 * only accepting funds into the university, the accounting line data will be held in the source accounting line data structure
 * only.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CreditCardReceiptDocument extends CashReceiptDocument {
    // holds details about each credit card receipt
    private List creditCardReceipts = new ArrayList();

    // incrementers for detail lines
    private Integer nextCreditCardCashReceiptLineNumber = new Integer(1);

    // monetary attributes
    private KualiDecimal totalCreditCardAmount = new KualiDecimal(0);
    
    /**
     * Default constructor that calls super.
     */
    public CreditCardReceiptDocument() {
        super();
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
     * Sets the total credit card amount which is the sum of all credit 
     * card receipts on this document.
     * 
     * @param creditCardAmount
     */
    public void setTotalCashAmount(KualiDecimal creditCardAmount) {
        this.totalCreditCardAmount = creditCardAmount;
    }

    /**
     * Gets the list of credit card receips which is a list 
     * of CreditCardDetail business objects.
     * 
     * @return List
     */
    public List getCreditCardReceipts() {
        return creditCardReceipts;
    }

    /**
     * Sets the credit card receipts list.
     * 
     * @param creditCardReceipts
     */
    public void setChecks(List creditCardReceipts) {
        this.creditCardReceipts = creditCardReceipts;
    }

    /**
     * Adds a new credit card receipt to the list.
     * 
     * @param credit card receipt
     */
//    public void addCheck(CreditCardReceiptDetail creditCardReceiptDetail) {
//        creditCardReceiptDetail.setSequenceId(this.nextCreditCardCashReceiptLineNumber);
//
//        this.creditCardReceipts.add(creditCardReceiptDetail);
//
//        this.nextCreditCardCashReceiptLineNumber = new Integer(this.nextCreditCardCashReceiptLineNumber.intValue() + 1);
//
//        this.totalCreditCardAmount = this.totalCreditCardAmount.add(creditCardReceiptDetail.getAmount());
//    }

    /**
     * Retrieve a particular credit card receipt at a given index in the list of credit card receipts.
     * 
     * @param index
     * @return CreditCardReceiptDetail
     */
//    public CreditCardReceiptDetail getCreditCardReceiptDetail(int index) throws InstantiationException {
//        while (this.creditCardReceipts.size() <= index) {
//            creditCardReceipts.add(new CreditCardReceiptDetail());
//        }
//        return (CreditCardReceiptDetail) creditCardReceipts.get(index);
//    }
    
    /**
     * Total for a Cash Receipt according to the spec should be the sum of the 
     * amounts on accounting lines belonging to object codes having the 'income' object type, 
     * less the sum of the amounts on accounting lines belonging to object codes 
     * having the 'expense' object type.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceTotal()
     */
//    public KualiDecimal getSourceTotal() {
//        CashReceiptDocumentRule crDocRule = (CashReceiptDocumentRule) SpringServiceLocator.getKualiRuleService().
//            getBusinessRulesInstance(this, AccountingLineRule.class);
//        KualiDecimal total = new KualiDecimal(0);
//        AccountingLineBase al = null;
//        Iterator iter = sourceAccountingLines.iterator();
//        while (iter.hasNext()) {
//            al = (AccountingLineBase) iter.next();
//            
//            KualiDecimal amount = al.getAmount();
//            if (amount != null) {
//                if(crDocRule.isDebit(al)) {
//                    total = total.subtract(amount);
//                } else if(crDocRule.isCredit(al)) {
//                    total = total.add(amount);
//                }
//            }
//        }
//        return total;
//    }

    /**
     * Cash Receipts only have source lines, so this should always return 0.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetTotal()
     */
//    public KualiDecimal getTargetTotal() {
//        return new KualiDecimal(0);
//    }

    /**
     * This method removes a check from the list and updates the total appropriately.
     * 
     * @param index
     */
//    public void removeCheck(int index) {
//        Check check = (Check) checks.remove(index);
//
//        // if the totalCheckAmount goes negative, bring back to zero.
//        this.totalCheckAmount = this.totalCheckAmount.subtract(check.getAmount());
//        if (this.totalCheckAmount.isNegative()) {
//            totalCheckAmount = KualiDecimal.ZERO;
//        }
//    }

    /**
     * Gets the depositDate attribute.
     * 
     * @return Returns the depositDate.
     */
//    public Timestamp getDepositDate() {
//        return depositDate;
//    }
//
//    /**
//     * Sets the depositDate attribute value.
//     * 
//     * @param depositDate The depositDate to set.
//     */
//    public void setDepositDate(Timestamp depositDate) {
//        this.depositDate = depositDate;
//    }
//
//    /**
//     * Gets the nextCheckSequenceId attribute.
//     * 
//     * @return Returns the nextCheckSequenceId.
//     */
//    public Integer getNextCheckSequenceId() {
//        return nextCheckSequenceId;
//    }
//
//    /**
//     * Sets the nextCheckSequenceId attribute value.
//     * 
//     * @param nextCheckSequenceId The nextCheckSequenceId to set.
//     */
//    public void setNextCheckSequenceId(Integer nextCheckSequenceId) {
//        this.nextCheckSequenceId = nextCheckSequenceId;
//    }
//
//    /**
//     * Gets the totalCheckAmount attribute.
//     * 
//     * @return Returns the totalCheckAmount.
//     */
//    public KualiDecimal getTotalCheckAmount() {
//        return totalCheckAmount;
//    }
//
//    /**
//     * This method returns the check total amount as a currency formatted string.
//     * 
//     * @return String
//     */
//    public String getCurrencyFormattedTotalCheckAmount() {
//        return (String) new CurrencyFormatter().format(totalCheckAmount);
//    }
//
//    /**
//     * Sets the totalCheckAmount attribute value.
//     * 
//     * @param totalCheckAmount The totalCheckAmount to set.
//     */
//    public void setTotalCheckAmount(KualiDecimal totalCheckAmount) {
//        if(totalCheckAmount == null) {
//            this.totalCheckAmount = new KualiDecimal(0);
//        } else {
//            this.totalCheckAmount = totalCheckAmount;
//        }
//    }
//
//    /**
//     * Gets the totalCoinAmount attribute.
//     * 
//     * @return Returns the totalCoinAmount.
//     */
//    public KualiDecimal getTotalCoinAmount() {
//        return totalCoinAmount;
//    }
//
//    /**
//     * This method returns the coin total amount as a currency formatted string.
//     * 
//     * @return String
//     */
//    public String getCurrencyFormattedTotalCoinAmount() {
//        return (String) new CurrencyFormatter().format(totalCoinAmount);
//    }
//
//    /**
//     * Sets the totalCoinAmount attribute value.
//     * 
//     * @param totalCoinAmount The totalCoinAmount to set.
//     */
//    public void setTotalCoinAmount(KualiDecimal totalCoinAmount) {
//        this.totalCoinAmount = totalCoinAmount;
//    }
//
//    /**
//     * This method returns the overall total of the document - coin plus check plus cash.
//     * 
//     * @return KualiDecimal
//     */
//    public KualiDecimal getSumTotalAmount() {
//        return totalCoinAmount.add(totalCheckAmount).add(totalCashAmount);
//    }
//
//    /**
//     * Retrieves the summed total amount in a currency format with commas.
//     * 
//     * @return String
//     */
//    public String getCurrencyFormattedSumTotalAmount() {
//        return (String) new CurrencyFormatter().format(getSumTotalAmount());
//    }
//
//    /**
//     * Overrides the base implementation to return an empty string.
//     * 
//     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
//     */
//    public String getSourceAccountingLinesSectionTitle() {
//        return Constants.EMPTY_STRING;
//    }
//
//    /**
//     * Overrides the base implementation to return an empty string.
//     * 
//     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLinesSectionTitle()
//     */
//    public String getTargetAccountingLinesSectionTitle() {
//        return Constants.EMPTY_STRING;
//    }
//
//    /**
//     * @return sum of the amounts of the current list of checks
//     */
//    public KualiDecimal calculateCheckTotal() {
//        KualiDecimal total = KualiDecimal.ZERO;
//        for (Iterator i = getChecks().iterator(); i.hasNext();) {
//            Check c = (Check) i.next();
//            if (null != c.getAmount()) {
//                total = total.add(c.getAmount());
//            }
//        }
//        return total;
//    }
//
//    /**
//     * Override to set the document status to VERIFIED ("V") when the document is FINAL. When the Cash Management document that this
//     * is associated with is FINAL approved, this status will be set to APPROVED ("A") to be picked up by the GL for processing.
//     * That's done in the handleRouteStatusChange() method in the CashManagementDocument.
//     * 
//     * @see org.kuali.core.document.Document#handleRouteStatusChange(java.lang.String)
//     */
//    public void handleRouteStatusChange(String newRouteStatus) {
//        // Workflow Status of Final --> Kuali Doc Status of Verified
//        if (EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(newRouteStatus)
//                || EdenConstants.ROUTE_HEADER_APPROVED_CD.equals(newRouteStatus)
//                || EdenConstants.ROUTE_HEADER_FINAL_CD.equals(newRouteStatus)) {
//            this.getDocumentHeader().setFinancialDocumentStatusCode(
//                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
//            SpringServiceLocator.getDocumentService().updateDocument(this);
//        }
//    }
//
//    /**
//     * @see org.kuali.core.document.DocumentBase#prepareForSave()
//     */
//    public void prepareForSave() {
//        super.prepareForSave();
//
//        // clear check list if mode is checkTotal
//        if (CHECK_ENTRY_TOTAL.equals(getCheckEntryMode())) {
//            getChecks().clear();
//        }
//        // update total if mode is checkDetail
//        else {
//            setTotalCheckAmount(calculateCheckTotal());
//        }
//    }
//
//    /**
//     * @see org.kuali.core.document.DocumentBase#processAfterRetrieve()
//     */
//    public void processAfterRetrieve() {
//        super.processAfterRetrieve();
//
//        // set to checkTotal mode if no checks
//        List checkList = getChecks();
//        if (ObjectUtils.isNull(checkList) || checkList.isEmpty()) {
//            setCheckEntryMode(CHECK_ENTRY_TOTAL);
//        }
//        // set to checkDetail mode if checks (and update the checkTotal, while you're here)
//        else {
//            setCheckEntryMode(CHECK_ENTRY_DETAIL);
//            setTotalCheckAmount(calculateCheckTotal());
//        }
//    }
//
//    /**
//     * @see org.kuali.core.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
//     */
//    public List buildListOfDeletionAwareLists() {
//        List managedLists = super.buildListOfDeletionAwareLists();
//        managedLists.add(getChecks());
//
//        return managedLists;
//    }
}