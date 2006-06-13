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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.rule.AccountingLineRule;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.module.financial.rules.CashReceiptDocumentRule;

/**
 * This is the business object that represents the CashReceiptDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since a Cash Receipt is a one sided transactional document,
 * only accepting funds into the university, the accounting line data will be held in the source accounting line data structure
 * only.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CashReceiptDocument extends TransactionalDocumentBase {
    public static final String CHECK_ENTRY_DETAIL = "individual";
    public static final String CHECK_ENTRY_TOTAL = "totals";

    private AccountingPeriod accountingPeriod; // represented by the posting year and posting period code
    private String campusLocationCode; // TODO Needs to be an actual object - also need to clarify this
    private Timestamp depositDate;

    // child object containers - for all the different reconciliation detail sections
    private String checkEntryMode = CHECK_ENTRY_DETAIL;
    private List checks = new ArrayList();

    // incrementers for detail lines
    private Integer nextCheckSequenceId = new Integer(1);

    // monetary attributes
    private KualiDecimal totalCashAmount = new KualiDecimal(0);
    private KualiDecimal totalCheckAmount = new KualiDecimal(0);
    private KualiDecimal totalCoinAmount = new KualiDecimal(0);

    /**
     * Initializes the array lists and line incrementers.
     */
    public CashReceiptDocument() {
        super();
        
        setCampusLocationCode( Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_CAMPUS_LOCATION_CODE );
    }

    /**
     * Gets the accountingPeriod attribute.
     * 
     * @return Returns the accountingPeriod.
     */
    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    /**
     * Sets the accountingPeriod attribute value.
     * 
     * @param accountingPeriod The accountingPeriod to set.
     */
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    /**
     * Gets the campusLocationCode attribute.
     * 
     * @return Returns the campusLocationCode.
     */
    public String getCampusLocationCode() {
        return campusLocationCode;
    }

    /**
     * Sets the campusLocationCode attribute value.
     * 
     * @param campusLocationCode The campusLocationCode to set.
     */
    public void setCampusLocationCode(String campusLocationCode) {
        this.campusLocationCode = campusLocationCode;
    }

    /**
     * Gets the totalCashAmount attribute.
     * 
     * @return Returns the totalCashAmount.
     */
    public KualiDecimal getTotalCashAmount() {
        return totalCashAmount;
    }

    /**
     * This method returns the cash total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCashAmount() {
        return (String) new CurrencyFormatter().format(totalCashAmount);
    }

    /**
     * Sets the totalCashAmount attribute value.
     * 
     * @param cashAmount The totalCashAmount to set.
     */
    public void setTotalCashAmount(KualiDecimal cashAmount) {
        this.totalCashAmount = cashAmount;
    }


    /**
     * @param checkEntryMode
     */
    public void setCheckEntryMode(String checkEntryMode) {
        this.checkEntryMode = checkEntryMode;
    }

    /**
     * @return checkEntryMode
     */
    public String getCheckEntryMode() {
        return checkEntryMode;
    }


    /**
     * Gets the checks attribute.
     * 
     * @return Returns the checks.
     */
    public List getChecks() {
        return checks;
    }

    /**
     * Sets the checks attribute value.
     * 
     * @param checks The checks to set.
     */
    public void setChecks(List checks) {
        this.checks = checks;
    }

    /**
     * Gets the number of checks, since Sun doesn't have a direct getter for collection size
     */
    public int getCheckCount() {
        int count = 0;
        if (checks != null) {
            count = checks.size();
        }
        return count;
    }


    /**
     * Adds a new check to the list.
     * 
     * @param check
     */
    public void addCheck(Check check) {
        check.setSequenceId(this.nextCheckSequenceId);

        this.checks.add(check);

        this.nextCheckSequenceId = new Integer(this.nextCheckSequenceId.intValue() + 1);

        KualiDecimal tca = this.totalCheckAmount;
        this.totalCheckAmount = this.totalCheckAmount.add(check.getAmount());
    }

    /**
     * Retrieve a particular check at a given index in the list of checks.
     * 
     * @param index
     * @return Check
     */
    public Check getCheck(int index) throws IllegalAccessException, InstantiationException {
        while (this.checks.size() <= index) {
            checks.add(new CheckBase());
        }
        return (Check) checks.get(index);
    }
    
    /**
     * Total for a Cash Receipt according to the spec should be the sum of the 
     * amounts on accounting lines belonging to object codes having the 'income' object type, 
     * less the sum of the amounts on accounting lines belonging to object codes 
     * having the 'expense' object type.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceTotal()
     */
    public KualiDecimal getSourceTotal() {
        CashReceiptDocumentRule crDocRule = (CashReceiptDocumentRule) SpringServiceLocator.getKualiRuleService().
            getBusinessRulesInstance(this, AccountingLineRule.class);
        KualiDecimal total = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            
            KualiDecimal amount = al.getAmount();
            if (amount != null) {
                if(crDocRule.isDebit(al)) {
                    total = total.subtract(amount);
                } else if(crDocRule.isCredit(al)) {
                    total = total.add(amount);
                }
            }
        }
        return total;
    }

    /**
     * Cash Receipts only have source lines, so this should always return 0.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetTotal()
     */
    public KualiDecimal getTargetTotal() {
        return new KualiDecimal(0);
    }

    /**
     * This method removes a check from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeCheck(int index) {
        Check check = (Check) checks.remove(index);

        // if the totalCheckAmount goes negative, bring back to zero.
        this.totalCheckAmount = this.totalCheckAmount.subtract(check.getAmount());
        if (this.totalCheckAmount.isNegative()) {
            totalCheckAmount = KualiDecimal.ZERO;
        }
    }

    /**
     * Gets the depositDate attribute.
     * 
     * @return Returns the depositDate.
     */
    public Timestamp getDepositDate() {
        return depositDate;
    }

    /**
     * Sets the depositDate attribute value.
     * 
     * @param depositDate The depositDate to set.
     */
    public void setDepositDate(Timestamp depositDate) {
        this.depositDate = depositDate;
    }

    /**
     * Gets the nextCheckSequenceId attribute.
     * 
     * @return Returns the nextCheckSequenceId.
     */
    public Integer getNextCheckSequenceId() {
        return nextCheckSequenceId;
    }

    /**
     * Sets the nextCheckSequenceId attribute value.
     * 
     * @param nextCheckSequenceId The nextCheckSequenceId to set.
     */
    public void setNextCheckSequenceId(Integer nextCheckSequenceId) {
        this.nextCheckSequenceId = nextCheckSequenceId;
    }

    /**
     * Gets the totalCheckAmount attribute.
     * 
     * @return Returns the totalCheckAmount.
     */
    public KualiDecimal getTotalCheckAmount() {
        return totalCheckAmount;
    }

    /**
     * This method returns the check total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCheckAmount() {
        return (String) new CurrencyFormatter().format(totalCheckAmount);
    }

    /**
     * Sets the totalCheckAmount attribute value.
     * 
     * @param totalCheckAmount The totalCheckAmount to set.
     */
    public void setTotalCheckAmount(KualiDecimal totalCheckAmount) {
        if(totalCheckAmount == null) {
            this.totalCheckAmount = new KualiDecimal(0);
        } else {
            this.totalCheckAmount = totalCheckAmount;
        }
    }

    /**
     * Gets the totalCoinAmount attribute.
     * 
     * @return Returns the totalCoinAmount.
     */
    public KualiDecimal getTotalCoinAmount() {
        return totalCoinAmount;
    }

    /**
     * This method returns the coin total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCoinAmount() {
        return (String) new CurrencyFormatter().format(totalCoinAmount);
    }

    /**
     * Sets the totalCoinAmount attribute value.
     * 
     * @param totalCoinAmount The totalCoinAmount to set.
     */
    public void setTotalCoinAmount(KualiDecimal totalCoinAmount) {
        this.totalCoinAmount = totalCoinAmount;
    }

    /**
     * This method returns the overall total of the document - coin plus check plus cash.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getSumTotalAmount() {
        return totalCoinAmount.add(totalCheckAmount).add(totalCashAmount);
    }

    /**
     * Retrieves the summed total amount in a currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedSumTotalAmount() {
        return (String) new CurrencyFormatter().format(getSumTotalAmount());
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
    }

    /**
     * @return sum of the amounts of the current list of checks
     */
    public KualiDecimal calculateCheckTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Iterator i = getChecks().iterator(); i.hasNext();) {
            Check c = (Check) i.next();
            if (null != c.getAmount()) {
                total = total.add(c.getAmount());
            }
        }
        return total;
    }

    /**
     * Override to set the document status to VERIFIED ("V") when the document is FINAL. When the Cash Management document that this
     * is associated with is FINAL approved, this status will be set to APPROVED ("A") to be picked up by the GL for processing.
     * That's done in the handleRouteStatusChange() method in the CashManagementDocument.
     * 
     * @see org.kuali.core.document.Document#handleRouteStatusChange()
     */
    public void handleRouteStatusChange() {
        // Workflow Status of Final --> Kuali Doc Status of Verified
        if (getDocumentHeader().getWorkflowDocument().stateIsApproved() || getDocumentHeader().getWorkflowDocument().stateIsProcessed() || getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
            this.getDocumentHeader().setFinancialDocumentStatusCode(
                    Constants.DocumentStatusCodes.CashReceipt.VERIFIED);
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#prepareForSave()
     */
    public void prepareForSave() {
        super.prepareForSave();

        // clear check list if mode is checkTotal
        if (CHECK_ENTRY_TOTAL.equals(getCheckEntryMode())) {
            getChecks().clear();
        }
        // update total if mode is checkDetail
        else {
            setTotalCheckAmount(calculateCheckTotal());
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#processAfterRetrieve()
     */
    public void processAfterRetrieve() {
        super.processAfterRetrieve();

        // set to checkTotal mode if no checks
        List checkList = getChecks();
        if (ObjectUtils.isNull(checkList) || checkList.isEmpty()) {
            setCheckEntryMode(CHECK_ENTRY_TOTAL);
        }
        // set to checkDetail mode if checks (and update the checkTotal, while you're here)
        else {
            setCheckEntryMode(CHECK_ENTRY_DETAIL);
            setTotalCheckAmount(calculateCheckTotal());
        }
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getChecks());

        return managedLists;
    }
}