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
package org.kuali.kfs.gl.businessobject;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Add a simple balance inquiry menu item to KFS main menu, ann extension of
 * KualiLookupableImpl to support account balance lookups
 */
public class CurrentAccountBalance extends Balance {

    transient private String universityFiscalPeriodCode;
    transient private AccountingPeriod accountingPeriod;
    transient private KualiDecimal currentBudget;
    transient private KualiDecimal beginningFundBalance;
    transient private KualiDecimal beginningCurrentAssets;
    transient private KualiDecimal beginningCurrentLiabilities;
    transient private KualiDecimal totalIncome;
    transient private KualiDecimal totalExpense;
    transient private KualiDecimal encumbrances;
    transient private KualiDecimal budgetBalanceAvailable;
    transient private KualiDecimal cashExpenditureAuthority;
    transient private KualiDecimal currentFundBalance;


    /**
     * Instantiate a CurrentAccountBalance object using an implementation
     * of a Transaction object.
     *
     * This is useful for following conventions in the testing framework
     * where the test fixture uses a dummy PendingGeneralLedgerEntry object
     * for use in various test cases.
     *
     *
     * @param t The transaction object from which the parent Balance class
     *          will initilialize with.
     */
    public CurrentAccountBalance(Transaction t){
        super(t);

        // These are not set by the parent Balance class, so we will do so here
        setUniversityFiscalPeriodCode(t.getUniversityFiscalPeriodCode());
        setAccount(t.getAccount());
    }

    /**
     * Constructs a CurrentAccountBalance instance.
     */
    public CurrentAccountBalance() {
        super();
        this.resetAmounts();
    }

    /**
     * Reset all amounts of instance variables to KualiDecimal.ZERO.
     */
    public void resetAmounts() {
        this.setAccountLineAnnualBalanceAmount(KualiDecimal.ZERO);
        this.setBeginningBalanceLineAmount(KualiDecimal.ZERO);
        this.setContractsGrantsBeginningBalanceAmount(KualiDecimal.ZERO);
        this.setMonth1Amount(KualiDecimal.ZERO);
        this.setMonth2Amount(KualiDecimal.ZERO);
        this.setMonth3Amount(KualiDecimal.ZERO);
        this.setMonth4Amount(KualiDecimal.ZERO);
        this.setMonth5Amount(KualiDecimal.ZERO);
        this.setMonth6Amount(KualiDecimal.ZERO);
        this.setMonth7Amount(KualiDecimal.ZERO);
        this.setMonth8Amount(KualiDecimal.ZERO);
        this.setMonth9Amount(KualiDecimal.ZERO);
        this.setMonth10Amount(KualiDecimal.ZERO);
        this.setMonth11Amount(KualiDecimal.ZERO);
        this.setMonth12Amount(KualiDecimal.ZERO);
        this.setMonth13Amount(KualiDecimal.ZERO);
        //transient columns
        this.setCurrentBudget(KualiDecimal.ZERO);
        this.setBeginningFundBalance(KualiDecimal.ZERO);
        this.setBeginningCurrentAssets(KualiDecimal.ZERO);
        this.setBeginningCurrentLiabilities(KualiDecimal.ZERO);
        this.setTotalIncome(KualiDecimal.ZERO);
        this.setTotalExpense(KualiDecimal.ZERO);
        this.setEncumbrances(KualiDecimal.ZERO);
        this.setBudgetBalanceAvailable(KualiDecimal.ZERO);
        this.setCashExpenditureAuthority(KualiDecimal.ZERO);
        this.setCurrentFundBalance(KualiDecimal.ZERO);

    }

    /**
     * Gets the universityFiscalPeriodCode attribute.
     *
     * @return Returns the universityFiscalPeriodCode.
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute value.
     *
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
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
     * Gets the totalIncome attribute.
     *
     * @return Returns the totalIncome.
     */
    public KualiDecimal getTotalIncome() {
        return totalIncome;
    }

    /**
     * Sets the totalIncome attribute value.
     *
     * @param totalIncome The totalIncome to set.
     */
    public void setTotalIncome(KualiDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    /**
     * Gets the totalExpense attribute.
     *
     * @return Returns the totalExpense.
     */
    public KualiDecimal getTotalExpense() {
        return totalExpense;
    }

    /**
     * Sets the totalExpense attribute value.
     *
     * @param totalExpense The totalExpense to set.
     */
    public void setTotalExpense(KualiDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    /**
     * Gets the currentBudget attribute.
     * @return Returns the currentBudget.
     */
    public KualiDecimal getCurrentBudget() {
        return currentBudget;
    }

    /**
     * Sets the currentBudget attribute value.
     * @param currentBudget The currentBudget to set.
     */
    public void setCurrentBudget(KualiDecimal currentBudget) {
        this.currentBudget = currentBudget;
    }

    /**
     * Gets the beginningFundBalance attribute.
     * @return Returns the beginningFundBalance.
     */
    public KualiDecimal getBeginningFundBalance() {
        return beginningFundBalance;
    }

    /**
     * Sets the beginningFundBalance attribute value.
     * @param beginningFundBalance The beginningFundBalance to set.
     */
    public void setBeginningFundBalance(KualiDecimal beginningFundBalance) {
        this.beginningFundBalance = beginningFundBalance;
    }

    /**
     * Gets the beginningCurrentAssets attribute.
     * @return Returns the beginningCurrentAssets.
     */
    public KualiDecimal getBeginningCurrentAssets() {
        return beginningCurrentAssets;
    }

    /**
     * Sets the beginningCurrentAssets attribute value.
     * @param beginningCurrentAssets The beginningCurrentAssets to set.
     */
    public void setBeginningCurrentAssets(KualiDecimal beginningCurrentAssets) {
        this.beginningCurrentAssets = beginningCurrentAssets;
    }

    /**
     * Gets the beginningCurrentLiabilities attribute.
     * @return Returns the beginningCurrentLiabilities.
     */
    public KualiDecimal getBeginningCurrentLiabilities() {
        return beginningCurrentLiabilities;
    }

    /**
     * Sets the beginningCurrentLiabilities attribute value.
     * @param beginningCurrentLiabilities The beginningCurrentLiabilities to set.
     */
    public void setBeginningCurrentLiabilities(KualiDecimal beginningCurrentLiabilities) {
        this.beginningCurrentLiabilities = beginningCurrentLiabilities;
    }

    /**
     * Gets the encumbrances attribute.
     * @return Returns the encumbrances.
     */
    public KualiDecimal getEncumbrances() {
        return encumbrances;
    }

    /**
     * Sets the encumbrances attribute value.
     * @param encumbrances The encumbrances to set.
     */
    public void setEncumbrances(KualiDecimal encumbrances) {
        this.encumbrances = encumbrances;
    }

    /**
     * Gets the budgetBalanceAvailable attribute.
     * @return Returns the budgetBalanceAvailable.
     */
    public KualiDecimal getBudgetBalanceAvailable() {
        return budgetBalanceAvailable;
    }

    /**
     * Sets the budgetBalanceAvailable attribute value.
     * @param budgetBalanceAvailable The budgetBalanceAvailable to set.
     */
    public void setBudgetBalanceAvailable(KualiDecimal budgetBalanceAvailable) {
        this.budgetBalanceAvailable = budgetBalanceAvailable;
    }

    /**
     * Gets the cashExpenditureAuthority attribute.
     * @return Returns the cashExpenditureAuthority.
     */
    public KualiDecimal getCashExpenditureAuthority() {
        return cashExpenditureAuthority;
    }

    /**
     * Sets the cashExpenditureAuthority attribute value.
     * @param cashExpenditureAuthority The cashExpenditureAuthority to set.
     */
    public void setCashExpenditureAuthority(KualiDecimal cashExpenditureAuthority) {
        this.cashExpenditureAuthority = cashExpenditureAuthority;
    }

    /**
     * Gets the currentFundBalance attribute.
     * @return Returns the currentFundBalance.
     */
    public KualiDecimal getCurrentFundBalance() {
        return currentFundBalance;
    }

    /**
     * Sets the currentFundBalance attribute value.
     * @param currentFundBalance The currentFundBalance to set.
     */
    public void setCurrentFundBalance(KualiDecimal currentFundBalance) {
        this.currentFundBalance = currentFundBalance;
    }
}
