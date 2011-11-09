/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.fp.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This class represents a cashiering-related transaction used in the cash management document
 */
public class CashieringTransaction extends TransientBusinessObjectBase {
    public static final String DETAIL_DOCUMENT_TYPE = "CMD";

    private String campusCode;
    private String referenceFinancialDocumentNumber;

    // money in properties
    private List<Check> moneyInChecks;
    private CoinDetail moneyInCoin;
    private CurrencyDetail moneyInCurrency;
    private CashieringItemInProcess newItemInProcess;
    private List<Check> baselineChecks;
    private Check newCheck;
    private KualiDecimal checkTotal;

    // money out properties
    private CoinDetail moneyOutCoin;
    private CurrencyDetail moneyOutCurrency;
    private List<CashieringItemInProcess> openItemsInProcess;

    private java.util.Date transactionStarted;
    private java.util.Date transactionEnded;

    // incrementers for detail lines
    private Integer nextCheckSequenceId;


    /**
     * Constructs a CashieringTransaction
     */
    public CashieringTransaction(String campusCode, String referenceFinancialDocumentNumber) {
        super();
        this.campusCode = campusCode;
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
        this.transactionStarted = SpringContext.getBean(DateTimeService.class).getCurrentDate();

        moneyInCoin = new CoinDetail();
        moneyInCurrency = new CurrencyDetail();

        moneyOutCoin = new CoinDetail();
        moneyOutCurrency = new CurrencyDetail();

        newItemInProcess = new CashieringItemInProcess();
        moneyInChecks = new ArrayList<Check>();
        newCheck = new CheckBase();
        baselineChecks = new ArrayList<Check>();
        openItemsInProcess = new ArrayList<CashieringItemInProcess>();
        nextCheckSequenceId = new Integer(1);
    }

    /**
     * Gets the moneyInChecks attribute.
     * 
     * @return Returns the moneyInChecks.
     */
    public List<Check> getMoneyInChecks() {
        return moneyInChecks;
    }

    /**
     * Sets the moneyInChecks attribute value.
     * 
     * @param moneyInChecks The moneyInChecks to set.
     */
    public void setMoneyInChecks(List<Check> moneyInChecks) {
        this.moneyInChecks = moneyInChecks;
    }

    /**
     * Retrieves a specific check from the list, by array index
     * 
     * @param index the index of the checks array to retrieve the check from
     * @return a Check
     */
    public Check getMoneyInCheck(int index) {
        if (index >= moneyInChecks.size()) {
            for (int i = moneyInChecks.size(); i <= index; i++) {
                moneyInChecks.add(createNewCheck());
            }
        }
        return moneyInChecks.get(index);
    }

    /**
     * Gets the moneyInCoin attribute.
     * 
     * @return Returns the moneyInCoin.
     */
    public CoinDetail getMoneyInCoin() {
        return moneyInCoin;
    }

    /**
     * Sets the moneyInCoin attribute value.
     * 
     * @param moneyInCoin The moneyInCoin to set.
     */
    public void setMoneyInCoin(CoinDetail moneyInCoin) {
        this.moneyInCoin = moneyInCoin;
    }

    /**
     * Gets the moneyInCurrency attribute.
     * 
     * @return Returns the moneyInCurrency.
     */
    public CurrencyDetail getMoneyInCurrency() {
        return moneyInCurrency;
    }

    /**
     * Sets the moneyInCurrency attribute value.
     * 
     * @param moneyInCurrency The moneyInCurrency to set.
     */
    public void setMoneyInCurrency(CurrencyDetail moneyInCurrency) {
        this.moneyInCurrency = moneyInCurrency;
    }

    /**
     * Gets the moneyOutCoin attribute.
     * 
     * @return Returns the moneyOutCoin.
     */
    public CoinDetail getMoneyOutCoin() {
        return moneyOutCoin;
    }

    /**
     * Sets the moneyOutCoin attribute value.
     * 
     * @param moneyOutCoin The moneyOutCoin to set.
     */
    public void setMoneyOutCoin(CoinDetail moneyOutCoin) {
        this.moneyOutCoin = moneyOutCoin;
    }

    /**
     * Gets the transactionEnded attribute.
     * 
     * @return Returns the transactionEnded.
     */
    public java.util.Date getTransactionEnded() {
        return transactionEnded;
    }

    /**
     * Sets the transactionEnded attribute value.
     * 
     * @param transactionEnded The transactionEnded to set.
     */
    public void setTransactionEnded(java.util.Date transactionEnded) {
        this.transactionEnded = transactionEnded;
    }

    /**
     * Gets the transactionStarted attribute.
     * 
     * @return Returns the transactionStarted.
     */
    public java.util.Date getTransactionStarted() {
        return transactionStarted;
    }

    /**
     * Sets the transactionStarted attribute value.
     * 
     * @param transactionStarted The transactionStarted to set.
     */
    public void setTransactionStarted(java.util.Date transactionStarted) {
        this.transactionStarted = transactionStarted;
    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute value.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * Sets the moneyOutCurrency attribute value.
     * 
     * @param moneyOutCurrency The moneyOutCurrency to set.
     */
    public void setMoneyOutCurrency(CurrencyDetail moneyOutCurrency) {
        this.moneyOutCurrency = moneyOutCurrency;
    }

    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     * 
     * @return Returns the referenceFinancialDocumentNumber.
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute value.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    /**
     * Gets the moneyOutCurrency attribute.
     * 
     * @return Returns the moneyOutCurrency.
     */
    public CurrencyDetail getMoneyOutCurrency() {
        return moneyOutCurrency;
    }

    /**
     * Gets the newItemInProcess attribute.
     * 
     * @return Returns the newItemInProcess.
     */
    public CashieringItemInProcess getNewItemInProcess() {
        return newItemInProcess;
    }

    /**
     * Sets the newItemInProcess attribute value.
     * 
     * @param newItemInProcess The newItemInProcess to set.
     */
    public void setNewItemInProcess(CashieringItemInProcess newItemInProcess) {
        this.newItemInProcess = newItemInProcess;
    }

    /**
     * Gets the openItemsInProcess attribute.
     * 
     * @return Returns the openItemsInProcess.
     */
    public List<CashieringItemInProcess> getOpenItemsInProcess() {
        return openItemsInProcess;
    }

    /**
     * Sets the openItemsInProcess attribute value.
     * 
     * @param openItemsInProcess The openItemsInProcess to set.
     */
    public void setOpenItemsInProcess(List<CashieringItemInProcess> openItemsInProcess) {
        this.openItemsInProcess = openItemsInProcess;
    }

    /**
     * This method returns a single open item in process
     * 
     * @return a cashiering item in process
     */
    public CashieringItemInProcess getOpenItemInProcess(int index) {
        extendOpenItemsList(index);
        return this.openItemsInProcess.get(index);
    }

    /**
     * make the open items in process list bigger, so it doesn't return a null value
     * 
     * @param minSize the minsize to make the list
     */
    private void extendOpenItemsList(int minSize) {
        while (this.openItemsInProcess.size() <= minSize) {
            this.openItemsInProcess.add(new CashieringItemInProcess());
        }
    }

    /**
     * Gets the newCheck attribute.
     * 
     * @return Returns the newCheck.
     */
    public Check getNewCheck() {
        return newCheck;
    }

    /**
     * Sets the newCheck attribute value.
     * 
     * @param newCheck The newCheck to set.
     */
    public void setNewCheck(Check newCheck) {
        this.newCheck = newCheck;
    }

    /**
     * This method will make sure that all of the various currency, coin, check, and item in process detail records are populated
     * with the correct info.
     */
    public void prepareForSave() {
        moneyInCoin.setDocumentNumber(this.referenceFinancialDocumentNumber);
        moneyInCoin.setFinancialDocumentTypeCode(DETAIL_DOCUMENT_TYPE);
        moneyInCoin.setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);

        moneyInCurrency.setDocumentNumber(this.referenceFinancialDocumentNumber);
        moneyInCurrency.setFinancialDocumentTypeCode(DETAIL_DOCUMENT_TYPE);
        moneyInCurrency.setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);

        moneyOutCoin.setDocumentNumber(this.referenceFinancialDocumentNumber);
        moneyOutCoin.setFinancialDocumentTypeCode(DETAIL_DOCUMENT_TYPE);
        moneyOutCoin.setCashieringStatus(KFSConstants.CurrencyCoinSources.DEPOSITS);

        moneyOutCurrency.setDocumentNumber(this.referenceFinancialDocumentNumber);
        moneyOutCurrency.setFinancialDocumentTypeCode(DETAIL_DOCUMENT_TYPE);
        moneyOutCurrency.setCashieringStatus(KFSConstants.CurrencyCoinSources.DEPOSITS);

        newItemInProcess.setCampusCode(this.campusCode);
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pkMap = new LinkedHashMap();
        pkMap.put("campusCode", this.campusCode);
        pkMap.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
        pkMap.put("transactionStarted", this.transactionStarted);
        return pkMap;
    }

    /**
     * Gets the checks attribute.
     * 
     * @return Returns the checks.
     */
    public List getChecks() {
        return getMoneyInChecks();
    }

    /**
     * Sets the checks attribute value.
     * 
     * @param checks The checks to set.
     */
    public void setChecks(List checks) {
        moneyInChecks = new ArrayList<Check>();
        for (Object o : checks) {
            moneyInChecks.add((Check) o);
        }
    }

    /**
     * Gets the number of checks, since Sun doesn't have a direct getter for collection size
     * 
     * @return the number of checks
     */
    public int getCheckCount() {
        int count = 0;
        if (moneyInChecks != null) {
            count = moneyInChecks.size();
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

        this.moneyInChecks.add(check);

        this.nextCheckSequenceId = new Integer(this.nextCheckSequenceId.intValue() + 1);
    }

    /**
     * Retrieve a particular check at a given index in the list of checks.
     * 
     * @param index
     * @return Check
     */
    public Check getCheck(int index) {
        while (this.moneyInChecks.size() <= index) {
            moneyInChecks.add(createNewCheck());
        }
        return (Check) moneyInChecks.get(index);
    }

    /**
     * @param checks
     * @return Map containing Checks from the given List, indexed by their sequenceId
     */
    private Map buildCheckMap(List checks) {
        Map checkMap = new HashMap();

        for (Iterator i = checks.iterator(); i.hasNext();) {
            Check check = (Check) i.next();
            Integer sequenceId = check.getSequenceId();

            Object oldCheck = checkMap.put(sequenceId, check);

            // verify that sequence numbers are unique...
            if (oldCheck != null) {
                throw new IllegalStateException("sequence id collision detected for sequence id " + sequenceId);
            }
        }

        return checkMap;
    }

    /**
     * This method removes a check from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeCheck(int index) {
        Check check = (Check) moneyInChecks.remove(index);
        KualiDecimal newTotalCheckAmount = getTotalCheckAmount().subtract(check.getAmount());
        // if the totalCheckAmount goes negative, bring back to zero.
        if (newTotalCheckAmount.isNegative()) {
            newTotalCheckAmount = KualiDecimal.ZERO;
        }
    }

    public KualiDecimal getTotalCheckAmount() {
        KualiDecimal result = KualiDecimal.ZERO;
        for (Check c : moneyInChecks) {
            if (c != null && c.getAmount() != null) {
                result = result.add(c.getAmount());
            }
        }
        return result;
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

    public Check createNewCheck() {
        Check newCheck = new CheckBase();
        newCheck.setFinancialDocumentTypeCode(DETAIL_DOCUMENT_TYPE);
        newCheck.setCashieringStatus(KFSConstants.CheckSources.CASH_MANAGEMENT);
        return newCheck;
    }

    /**
     * This method calculates how much money has been paid back in all items in process
     * 
     * @return the calculated amount
     */
    public KualiDecimal getPaidBackItemsInProcessAmount() {
        KualiDecimal amount = KualiDecimal.ZERO;
        if (this.openItemsInProcess != null) {
            for (CashieringItemInProcess itemInProcess : this.openItemsInProcess) {
                if (itemInProcess.getCurrentPayment() != null && itemInProcess.getCurrentPayment().isGreaterThan(KualiDecimal.ZERO)) {
                    amount = amount.add(itemInProcess.getCurrentPayment());
                }
            }
        }
        return amount;
    }

    /**
     * @return current List of baseline checks for use in update detection
     */
    public List getBaselineChecks() {
        return baselineChecks;
    }

    /**
     * Sets the current List of baseline checks to the given List
     * 
     * @param baselineChecks
     */
    public void setBaselineChecks(List baselineChecks) {
        this.baselineChecks = baselineChecks;
    }

    /**
     * @param index
     * @return true if a baselineCheck with the given index exists
     */
    public boolean hasBaselineCheck(int index) {
        boolean has = false;

        if ((index >= 0) && (index <= baselineChecks.size())) {
            has = true;
        }

        return has;
    }

    /**
     * Implementation creates empty Checks as a side-effect, so that Struts' efforts to set fields of lines which haven't been
     * created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * @return baseline Check at the given index
     */
    public Check getBaselineCheck(int index) {
        while (baselineChecks.size() <= index) {
            baselineChecks.add(this.createNewCheck());
        }
        return (Check) baselineChecks.get(index);
    }

    /**
     * This method calcuates how much money has come in to the "Money In" side of the transaction
     * 
     * @return the amount calculated
     */
    public KualiDecimal getMoneyInTotal() {
        KualiDecimal result = KualiDecimal.ZERO;
        result = result.add(this.moneyInCurrency.getTotalAmount());
        result = result.add(this.moneyInCoin.getTotalAmount());
        result = result.add(this.getTotalCheckAmount());
        if (this.newItemInProcess.isPopulated()) {
            result = result.add(this.newItemInProcess.getItemAmount());
        }
        return result;
    }

    /**
     * This method calculates how much money has gone out through the "Money Out" side of the transaction
     * 
     * @return the amount calculated
     */
    public KualiDecimal getMoneyOutTotal() {
        KualiDecimal result = KualiDecimal.ZERO;
        result = result.add(this.moneyOutCurrency.getTotalAmount());
        result = result.add(this.moneyOutCoin.getTotalAmount());
        result = result.add(this.getPaidBackItemsInProcessAmount());
        return result;
    }

    /**
     * @param checkTotal
     * @deprecated
     */
    public void setCheckTotal(KualiDecimal checkTotal) {
        this.checkTotal = checkTotal;
    }

}
