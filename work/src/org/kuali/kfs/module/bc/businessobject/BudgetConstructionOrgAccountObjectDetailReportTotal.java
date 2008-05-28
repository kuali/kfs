/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.bo;

import java.math.BigDecimal;

/**
 * Budget Construction Organization Account Summary Report Business Object.
 */
public class BudgetConstructionOrgAccountObjectDetailReportTotal {

    private BudgetConstructionAccountBalance budgetConstructionAccountBalance;

    // Total parts
    
    private BigDecimal totalObjectPositionCsfLeaveFteQuantity;
    private BigDecimal totalObjectPositionFullTimeEquivalencyQuantity;
    private Integer totalObjectFinancialBeginningBalanceLineAmount;
    private BigDecimal totalObjectAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedFteQuantity;
    private Integer totalObjectAccountLineAnnualBalanceAmount;
    private Integer totalObjectAmountChange;
    
    private BigDecimal totalLevelPositionCsfLeaveFteQuantity;
    private BigDecimal totalLevelPositionFullTimeEquivalencyQuantity;
    private Integer totalLevelFinancialBeginningBalanceLineAmount;
    private BigDecimal totalLevelAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalLevelAppointmentRequestedFteQuantity;
    private Integer totalLevelAccountLineAnnualBalanceAmount;
    private Integer totalLevelAmountChange;

    private Integer grossFinancialBeginningBalanceLineAmount;
    private Integer grossAccountLineAnnualBalanceAmount;
    private Integer grossAmountChange;

    private BigDecimal typePositionCsfLeaveFteQuantity;
    private BigDecimal typePositionFullTimeEquivalencyQuantity;
    private Integer typeFinancialBeginningBalanceLineAmount;
    private BigDecimal typeAppointmentRequestedCsfFteQuantity;
    private BigDecimal typeAppointmentRequestedFteQuantity;
    private Integer typeAccountLineAnnualBalanceAmount;
    private Integer typeAmountChange;

    //subFund total
    private Integer subFundRevenueFinancialBeginningBalanceLineAmount;
    private Integer subFundRevenueAccountLineAnnualBalanceAmount;
    private Integer subFundRevenueAmountChange;
    
    private Integer subFundExpenditureFinancialBeginningBalanceLineAmount;
    private Integer subFundExpenditureAccountLineAnnualBalanceAmount;
    private Integer subFundExpenditureAmountChange;
    
    private Integer subFundDifferenceFinancialBeginningBalanceLineAmount;
    private Integer subFundDifferenceAccountLineAnnualBalanceAmount;
    private Integer subFundDifferenceAmountChange;
    
    //account total
    private Integer accountRevenueFinancialBeginningBalanceLineAmount;
    private Integer accountRevenueAccountLineAnnualBalanceAmount;
    private Integer accountRevenueAmountChange;
    
    private Integer accountExpenditureFinancialBeginningBalanceLineAmount;
    private Integer accountExpenditureAccountLineAnnualBalanceAmount;
    private Integer accountExpenditureAmountChange;
    
    private Integer accountDifferenceFinancialBeginningBalanceLineAmount;
    private Integer accountDifferenceAccountLineAnnualBalanceAmount;
    private Integer accountDifferenceAmountChange;
    
   
    
    public Integer getGrossAccountLineAnnualBalanceAmount() {
        return grossAccountLineAnnualBalanceAmount;
    }
    public void setGrossAccountLineAnnualBalanceAmount(Integer grossAccountLineAnnualBalanceAmount) {
        this.grossAccountLineAnnualBalanceAmount = grossAccountLineAnnualBalanceAmount;
    }
    public Integer getGrossFinancialBeginningBalanceLineAmount() {
        return grossFinancialBeginningBalanceLineAmount;
    }
    public void setGrossFinancialBeginningBalanceLineAmount(Integer grossFinancialBeginningBalanceLineAmount) {
        this.grossFinancialBeginningBalanceLineAmount = grossFinancialBeginningBalanceLineAmount;
    }
    public Integer getTypeAccountLineAnnualBalanceAmount() {
        return typeAccountLineAnnualBalanceAmount;
    }
    public void setTypeAccountLineAnnualBalanceAmount(Integer typeAccountLineAnnualBalanceAmount) {
        this.typeAccountLineAnnualBalanceAmount = typeAccountLineAnnualBalanceAmount;
    }
    public Integer getTypeAmountChange() {
        return typeAmountChange;
    }
    public void setTypeAmountChange(Integer typeAmountChange) {
        this.typeAmountChange = typeAmountChange;
    }
    public BigDecimal getTypeAppointmentRequestedCsfFteQuantity() {
        return typeAppointmentRequestedCsfFteQuantity;
    }
    public void setTypeAppointmentRequestedCsfFteQuantity(BigDecimal typeAppointmentRequestedCsfFteQuantity) {
        this.typeAppointmentRequestedCsfFteQuantity = typeAppointmentRequestedCsfFteQuantity;
    }
    public BigDecimal getTypeAppointmentRequestedFteQuantity() {
        return typeAppointmentRequestedFteQuantity;
    }
    public void setTypeAppointmentRequestedFteQuantity(BigDecimal typeAppointmentRequestedFteQuantity) {
        this.typeAppointmentRequestedFteQuantity = typeAppointmentRequestedFteQuantity;
    }
    public Integer getTypeFinancialBeginningBalanceLineAmount() {
        return typeFinancialBeginningBalanceLineAmount;
    }
    public void setTypeFinancialBeginningBalanceLineAmount(Integer typeFinancialBeginningBalanceLineAmount) {
        this.typeFinancialBeginningBalanceLineAmount = typeFinancialBeginningBalanceLineAmount;
    }
    public BigDecimal getTypePositionFullTimeEquivalencyQuantity() {
        return typePositionFullTimeEquivalencyQuantity;
    }
    public void setTypePositionFullTimeEquivalencyQuantity(BigDecimal typePositionFullTimeEquivalencyQuantity) {
        this.typePositionFullTimeEquivalencyQuantity = typePositionFullTimeEquivalencyQuantity;
    }
    public BigDecimal getTypePositionCsfLeaveFteQuantity() {
        return typePositionCsfLeaveFteQuantity;
    }
    public void setTypePositionCsfLeaveFteQuantity(BigDecimal typePositionCsfLeaveFteQuantity) {
        this.typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity;
    }
    public Integer getGrossAmountChange() {
        return grossAmountChange;
    }
    public void setGrossAmountChange(Integer grossAmountChange) {
        this.grossAmountChange = grossAmountChange;
    }
    public Integer getTotalLevelAccountLineAnnualBalanceAmount() {
        return totalLevelAccountLineAnnualBalanceAmount;
    }
    public void setTotalLevelAccountLineAnnualBalanceAmount(Integer totalLevelAccountLineAnnualBalanceAmount) {
        this.totalLevelAccountLineAnnualBalanceAmount = totalLevelAccountLineAnnualBalanceAmount;
    }
    public Integer getTotalLevelAmountChange() {
        return totalLevelAmountChange;
    }
    public void setTotalLevelAmountChange(Integer totalLevelAmountChange) {
        this.totalLevelAmountChange = totalLevelAmountChange;
    }
    public BigDecimal getTotalLevelAppointmentRequestedCsfFteQuantity() {
        return totalLevelAppointmentRequestedCsfFteQuantity;
    }
    public void setTotalLevelAppointmentRequestedCsfFteQuantity(BigDecimal totalLevelAppointmentRequestedCsfFteQuantity) {
        this.totalLevelAppointmentRequestedCsfFteQuantity = totalLevelAppointmentRequestedCsfFteQuantity;
    }
    public BigDecimal getTotalLevelAppointmentRequestedFteQuantity() {
        return totalLevelAppointmentRequestedFteQuantity;
    }
    public void setTotalLevelAppointmentRequestedFteQuantity(BigDecimal totalLevelAppointmentRequestedFteQuantity) {
        this.totalLevelAppointmentRequestedFteQuantity = totalLevelAppointmentRequestedFteQuantity;
    }
    public Integer getTotalLevelFinancialBeginningBalanceLineAmount() {
        return totalLevelFinancialBeginningBalanceLineAmount;
    }
    public void setTotalLevelFinancialBeginningBalanceLineAmount(Integer totalLevelFinancialBeginningBalanceLineAmount) {
        this.totalLevelFinancialBeginningBalanceLineAmount = totalLevelFinancialBeginningBalanceLineAmount;
    }
    public BigDecimal getTotalLevelPositionFullTimeEquivalencyQuantity() {
        return totalLevelPositionFullTimeEquivalencyQuantity;
    }
    public void setTotalLevelPositionFullTimeEquivalencyQuantity(BigDecimal totalLevelPositionFullTimeEquivalencyQuantity) {
        this.totalLevelPositionFullTimeEquivalencyQuantity = totalLevelPositionFullTimeEquivalencyQuantity;
    }
    public BigDecimal getTotalLevelPositionCsfLeaveFteQuantity() {
        return totalLevelPositionCsfLeaveFteQuantity;
    }
    public void setTotalLevelPositionCsfLeaveFteQuantity(BigDecimal totalLevelPositionCsfLeaveFteQuantity) {
        this.totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity;
    }
    public BudgetConstructionAccountBalance getBudgetConstructionAccountBalance() {
        return budgetConstructionAccountBalance;
    }
    public void setBudgetConstructionAccountBalance(BudgetConstructionAccountBalance budgetConstructionAccountBalance) {
        this.budgetConstructionAccountBalance = budgetConstructionAccountBalance;
    }
    public Integer getTotalObjectAccountLineAnnualBalanceAmount() {
        return totalObjectAccountLineAnnualBalanceAmount;
    }
    public void setTotalObjectAccountLineAnnualBalanceAmount(Integer totalObjectAccountLineAnnualBalanceAmount) {
        this.totalObjectAccountLineAnnualBalanceAmount = totalObjectAccountLineAnnualBalanceAmount;
    }
    public Integer getTotalObjectAmountChange() {
        return totalObjectAmountChange;
    }
    public void setTotalObjectAmountChange(Integer totalObjectAmountChange) {
        this.totalObjectAmountChange = totalObjectAmountChange;
    }
    public BigDecimal getTotalObjectAppointmentRequestedCsfFteQuantity() {
        return totalObjectAppointmentRequestedCsfFteQuantity;
    }
    public void setTotalObjectAppointmentRequestedCsfFteQuantity(BigDecimal totalObjectAppointmentRequestedCsfFteQuantity) {
        this.totalObjectAppointmentRequestedCsfFteQuantity = totalObjectAppointmentRequestedCsfFteQuantity;
    }
    public BigDecimal getTotalObjectAppointmentRequestedFteQuantity() {
        return totalObjectAppointmentRequestedFteQuantity;
    }
    public void setTotalObjectAppointmentRequestedFteQuantity(BigDecimal totalObjectAppointmentRequestedFteQuantity) {
        this.totalObjectAppointmentRequestedFteQuantity = totalObjectAppointmentRequestedFteQuantity;
    }
    public Integer getTotalObjectFinancialBeginningBalanceLineAmount() {
        return totalObjectFinancialBeginningBalanceLineAmount;
    }
    public void setTotalObjectFinancialBeginningBalanceLineAmount(Integer totalObjectFinancialBeginningBalanceLineAmount) {
        this.totalObjectFinancialBeginningBalanceLineAmount = totalObjectFinancialBeginningBalanceLineAmount;
    }
    public BigDecimal getTotalObjectPositionFullTimeEquivalencyQuantity() {
        return totalObjectPositionFullTimeEquivalencyQuantity;
    }
    public void setTotalObjectPositionFullTimeEquivalencyQuantity(BigDecimal totalObjectPositionFullTimeEquivalencyQuantity) {
        this.totalObjectPositionFullTimeEquivalencyQuantity = totalObjectPositionFullTimeEquivalencyQuantity;
    }
    public BigDecimal getTotalObjectPositionCsfLeaveFteQuantity() {
        return totalObjectPositionCsfLeaveFteQuantity;
    }
    public void setTotalObjectPositionCsfLeaveFteQuantity(BigDecimal totalObjectPositionCsfLeaveFteQuantity) {
        this.totalObjectPositionCsfLeaveFteQuantity = totalObjectPositionCsfLeaveFteQuantity;
    }
    public Integer getAccountDifferenceAccountLineAnnualBalanceAmount() {
        return accountDifferenceAccountLineAnnualBalanceAmount;
    }
    public void setAccountDifferenceAccountLineAnnualBalanceAmount(Integer accountDifferenceAccountLineAnnualBalanceAmount) {
        this.accountDifferenceAccountLineAnnualBalanceAmount = accountDifferenceAccountLineAnnualBalanceAmount;
    }
    public Integer getAccountDifferenceAmountChange() {
        return accountDifferenceAmountChange;
    }
    public void setAccountDifferenceAmountChange(Integer accountDifferenceAmountChange) {
        this.accountDifferenceAmountChange = accountDifferenceAmountChange;
    }
    public Integer getAccountDifferenceFinancialBeginningBalanceLineAmount() {
        return accountDifferenceFinancialBeginningBalanceLineAmount;
    }
    public void setAccountDifferenceFinancialBeginningBalanceLineAmount(Integer accountDifferenceFinancialBeginningBalanceLineAmount) {
        this.accountDifferenceFinancialBeginningBalanceLineAmount = accountDifferenceFinancialBeginningBalanceLineAmount;
    }
    public Integer getAccountExpenditureAccountLineAnnualBalanceAmount() {
        return accountExpenditureAccountLineAnnualBalanceAmount;
    }
    public void setAccountExpenditureAccountLineAnnualBalanceAmount(Integer accountExpenditureAccountLineAnnualBalanceAmount) {
        this.accountExpenditureAccountLineAnnualBalanceAmount = accountExpenditureAccountLineAnnualBalanceAmount;
    }
    public Integer getAccountExpenditureAmountChange() {
        return accountExpenditureAmountChange;
    }
    public void setAccountExpenditureAmountChange(Integer accountExpenditureAmountChange) {
        this.accountExpenditureAmountChange = accountExpenditureAmountChange;
    }
    public Integer getAccountExpenditureFinancialBeginningBalanceLineAmount() {
        return accountExpenditureFinancialBeginningBalanceLineAmount;
    }
    public void setAccountExpenditureFinancialBeginningBalanceLineAmount(Integer accountExpenditureFinancialBeginningBalanceLineAmount) {
        this.accountExpenditureFinancialBeginningBalanceLineAmount = accountExpenditureFinancialBeginningBalanceLineAmount;
    }
    public Integer getAccountRevenueAccountLineAnnualBalanceAmount() {
        return accountRevenueAccountLineAnnualBalanceAmount;
    }
    public void setAccountRevenueAccountLineAnnualBalanceAmount(Integer accountRevenueAccountLineAnnualBalanceAmount) {
        this.accountRevenueAccountLineAnnualBalanceAmount = accountRevenueAccountLineAnnualBalanceAmount;
    }
    public Integer getAccountRevenueAmountChange() {
        return accountRevenueAmountChange;
    }
    public void setAccountRevenueAmountChange(Integer accountRevenueAmountChange) {
        this.accountRevenueAmountChange = accountRevenueAmountChange;
    }
    public Integer getAccountRevenueFinancialBeginningBalanceLineAmount() {
        return accountRevenueFinancialBeginningBalanceLineAmount;
    }
    public void setAccountRevenueFinancialBeginningBalanceLineAmount(Integer accountRevenueFinancialBeginningBalanceLineAmount) {
        this.accountRevenueFinancialBeginningBalanceLineAmount = accountRevenueFinancialBeginningBalanceLineAmount;
    }
    public Integer getSubFundDifferenceAccountLineAnnualBalanceAmount() {
        return subFundDifferenceAccountLineAnnualBalanceAmount;
    }
    public void setSubFundDifferenceAccountLineAnnualBalanceAmount(Integer subFundDifferenceAccountLineAnnualBalanceAmount) {
        this.subFundDifferenceAccountLineAnnualBalanceAmount = subFundDifferenceAccountLineAnnualBalanceAmount;
    }
    public Integer getSubFundDifferenceAmountChange() {
        return subFundDifferenceAmountChange;
    }
    public void setSubFundDifferenceAmountChange(Integer subFundDifferenceAmountChange) {
        this.subFundDifferenceAmountChange = subFundDifferenceAmountChange;
    }
    public Integer getSubFundDifferenceFinancialBeginningBalanceLineAmount() {
        return subFundDifferenceFinancialBeginningBalanceLineAmount;
    }
    public void setSubFundDifferenceFinancialBeginningBalanceLineAmount(Integer subFundDifferenceFinancialBeginningBalanceLineAmount) {
        this.subFundDifferenceFinancialBeginningBalanceLineAmount = subFundDifferenceFinancialBeginningBalanceLineAmount;
    }
    public Integer getSubFundExpenditureAccountLineAnnualBalanceAmount() {
        return subFundExpenditureAccountLineAnnualBalanceAmount;
    }
    public void setSubFundExpenditureAccountLineAnnualBalanceAmount(Integer subFundExpenditureAccountLineAnnualBalanceAmount) {
        this.subFundExpenditureAccountLineAnnualBalanceAmount = subFundExpenditureAccountLineAnnualBalanceAmount;
    }
    public Integer getSubFundExpenditureAmountChange() {
        return subFundExpenditureAmountChange;
    }
    public void setSubFundExpenditureAmountChange(Integer subFundExpenditureAmountChange) {
        this.subFundExpenditureAmountChange = subFundExpenditureAmountChange;
    }
    public Integer getSubFundExpenditureFinancialBeginningBalanceLineAmount() {
        return subFundExpenditureFinancialBeginningBalanceLineAmount;
    }
    public void setSubFundExpenditureFinancialBeginningBalanceLineAmount(Integer subFundExpenditureFinancialBeginningBalanceLineAmount) {
        this.subFundExpenditureFinancialBeginningBalanceLineAmount = subFundExpenditureFinancialBeginningBalanceLineAmount;
    }
    public Integer getSubFundRevenueAccountLineAnnualBalanceAmount() {
        return subFundRevenueAccountLineAnnualBalanceAmount;
    }
    public void setSubFundRevenueAccountLineAnnualBalanceAmount(Integer subFundRevenueAccountLineAnnualBalanceAmount) {
        this.subFundRevenueAccountLineAnnualBalanceAmount = subFundRevenueAccountLineAnnualBalanceAmount;
    }
    public Integer getSubFundRevenueAmountChange() {
        return subFundRevenueAmountChange;
    }
    public void setSubFundRevenueAmountChange(Integer subFundRevenueAmountChange) {
        this.subFundRevenueAmountChange = subFundRevenueAmountChange;
    }
    public Integer getSubFundRevenueFinancialBeginningBalanceLineAmount() {
        return subFundRevenueFinancialBeginningBalanceLineAmount;
    }
    public void setSubFundRevenueFinancialBeginningBalanceLineAmount(Integer subFundRevenueFinancialBeginningBalanceLineAmount) {
        this.subFundRevenueFinancialBeginningBalanceLineAmount = subFundRevenueFinancialBeginningBalanceLineAmount;
    }
}
