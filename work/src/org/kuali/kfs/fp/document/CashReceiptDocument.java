/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/document/CashReceiptDocument.java,v $
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
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.module.gl.util.SufficientFundsItem;

/**
 * This is the business object that represents the CashReceiptDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since a Cash Receipt is a one sided transactional document,
 * only accepting funds into the university, the accounting line data will be held in the source accounting line data structure
 * only.
 * 
 * 
 */
public class CashReceiptDocument extends CashReceiptFamilyBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashReceiptDocument.class);

    public static final String CHECK_ENTRY_DETAIL = "individual";
    public static final String CHECK_ENTRY_TOTAL = "totals";

    // child object containers - for all the different reconciliation detail sections
    private String checkEntryMode = CHECK_ENTRY_DETAIL;
    private List checks = new ArrayList();

    // incrementers for detail lines
    private Integer nextCheckSequenceId = new Integer(1);

    // monetary attributes
    private KualiDecimal totalCashAmount = KualiDecimal.ZERO;
    private KualiDecimal totalCheckAmount = KualiDecimal.ZERO;
    private KualiDecimal totalCoinAmount = KualiDecimal.ZERO;

    /**
     * Initializes the array lists and line incrementers.
     */
    public CashReceiptDocument() {
        super();

        setCampusLocationCode(Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_CAMPUS_LOCATION_CODE);
    }

    /**
     * Gets the totalCashAmount attribute.
     * 
     * @return Returns the totalCashAmount.
     */
    public KualiDecimal getTotalCashAmount() {
        if (totalCashAmount == null) {
            setTotalCashAmount(KualiDecimal.ZERO);
        }
        return totalCashAmount;
    }

    /**
     * This method returns the cash total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCashAmount() {
        return (String) new CurrencyFormatter().format(getTotalCashAmount());
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
     * 
     * @return the number of checks
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

        setTotalCheckAmount(getTotalCheckAmount().add(check.getAmount()));
    }

    /**
     * Retrieve a particular check at a given index in the list of checks.
     * 
     * @param index
     * @return Check
     */
    public Check getCheck(int index) {
        while (this.checks.size() <= index) {
            checks.add(new CheckBase());
        }
        return (Check) checks.get(index);
    }


    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#checkSufficientFunds()
     */
    @Override
    public List<SufficientFundsItem> checkSufficientFunds() {
        LOG.debug("checkSufficientFunds() started");

        // This document does not do sufficient funds checking
        return new ArrayList<SufficientFundsItem>();
    }

    /**
     * Override to set the document status to VERIFIED ("V") when the document is FINAL. When the Cash Management document that this
     * is associated with is FINAL approved, this status will be set to APPROVED ("A") to be picked up by the GL for processing.
     * That's done in the handleRouteStatusChange() method in the CashManagementDocument.
     * 
     * @see org.kuali.core.document.Document#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        // Workflow Status of PROCESSED --> Kuali Doc Status of Verified
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            this.getDocumentHeader().setFinancialDocumentStatusCode(Constants.DocumentStatusCodes.CashReceipt.VERIFIED);
        }
    }


    /**
     * This method removes a check from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeCheck(int index) {
        Check check = (Check) checks.remove(index);
        KualiDecimal newTotalCheckAmount = getTotalCheckAmount().subtract(check.getAmount());
        // if the totalCheckAmount goes negative, bring back to zero.
        if (newTotalCheckAmount.isNegative()) {
            newTotalCheckAmount = KualiDecimal.ZERO;
        }
        setTotalCheckAmount(newTotalCheckAmount);
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
        if (totalCheckAmount == null) {
            setTotalCheckAmount(KualiDecimal.ZERO);
        }
        return totalCheckAmount;
    }

    /**
     * This method returns the check total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCheckAmount() {
        return (String) new CurrencyFormatter().format(getTotalCheckAmount());
    }

    /**
     * Sets the totalCheckAmount attribute value.
     * 
     * @param totalCheckAmount The totalCheckAmount to set.
     */
    public void setTotalCheckAmount(KualiDecimal totalCheckAmount) {
        this.totalCheckAmount = totalCheckAmount;
    }

    /**
     * Gets the totalCoinAmount attribute.
     * 
     * @return Returns the totalCoinAmount.
     */
    public KualiDecimal getTotalCoinAmount() {
        if (totalCoinAmount == null) {
            setTotalCoinAmount(KualiDecimal.ZERO);
        }
        return totalCoinAmount;
    }

    /**
     * This method returns the coin total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalCoinAmount() {
        return (String) new CurrencyFormatter().format(getTotalCoinAmount());
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
    @Override
    public KualiDecimal getSumTotalAmount() {
        KualiDecimal sumTotalAmount = getTotalCoinAmount().add(getTotalCheckAmount()).add(getTotalCashAmount());
        return sumTotalAmount;
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
     * @see org.kuali.core.document.DocumentBase#prepareForSave()
     */
    @Override
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
    @Override
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
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getChecks());

        return managedLists;
    }
}