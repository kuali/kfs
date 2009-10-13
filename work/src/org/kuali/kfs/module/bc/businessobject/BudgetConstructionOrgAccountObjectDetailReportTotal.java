/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject;

import java.math.BigDecimal;

/**
 * Budget Construction Organization Account Summary Report Business Object.
 */
public class BudgetConstructionOrgAccountObjectDetailReportTotal {

    private BudgetConstructionAccountBalance budgetConstructionAccountBalance;

    // Total parts
    
    // object total
    private BigDecimal totalObjectPositionCsfLeaveFteQuantity;
    private BigDecimal totalObjectPositionFullTimeEquivalencyQuantity;
    private Integer totalObjectFinancialBeginningBalanceLineAmount;
    private BigDecimal totalObjectAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedFteQuantity;
    private Integer totalObjectAccountLineAnnualBalanceAmount;
    
    //level total
    private BigDecimal totalLevelPositionCsfLeaveFteQuantity;
    private BigDecimal totalLevelPositionFullTimeEquivalencyQuantity;
    private Integer totalLevelFinancialBeginningBalanceLineAmount;
    private BigDecimal totalLevelAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalLevelAppointmentRequestedFteQuantity;
    private Integer totalLevelAccountLineAnnualBalanceAmount;

    // Gexp And Type Total
    private Integer grossFinancialBeginningBalanceLineAmount;
    private Integer grossAccountLineAnnualBalanceAmount;

    private BigDecimal typePositionCsfLeaveFteQuantity;
    private BigDecimal typePositionFullTimeEquivalencyQuantity;
    private Integer typeFinancialBeginningBalanceLineAmount;
    private BigDecimal typeAppointmentRequestedCsfFteQuantity;
    private BigDecimal typeAppointmentRequestedFteQuantity;
    private Integer typeAccountLineAnnualBalanceAmount;

    //account total
    private BigDecimal accountPositionCsfLeaveFteQuantity;
    private BigDecimal accountPositionFullTimeEquivalencyQuantity;
    private BigDecimal accountAppointmentRequestedCsfFteQuantity;
    private BigDecimal accountAppointmentRequestedFteQuantity;
    
    private Integer accountRevenueFinancialBeginningBalanceLineAmount;
    private Integer accountRevenueAccountLineAnnualBalanceAmount;
    private Integer accountTrnfrInFinancialBeginningBalanceLineAmount;
    private Integer accountTrnfrInAccountLineAnnualBalanceAmount;
    private Integer accountExpenditureFinancialBeginningBalanceLineAmount;
    private Integer accountExpenditureAccountLineAnnualBalanceAmount;
    
    //subFund total
    private BigDecimal subFundPositionCsfLeaveFteQuantity;
    private BigDecimal subFundPositionFullTimeEquivalencyQuantity;
    private BigDecimal subFundAppointmentRequestedCsfFteQuantity;
    private BigDecimal subFundAppointmentRequestedFteQuantity;
    
    private Integer subFundRevenueFinancialBeginningBalanceLineAmount;
    private Integer subFundRevenueAccountLineAnnualBalanceAmount;
    private Integer subFundTrnfrInFinancialBeginningBalanceLineAmount;
    private Integer subFundTrnfrInAccountLineAnnualBalanceAmount;
    private Integer subFundExpenditureFinancialBeginningBalanceLineAmount;
    private Integer subFundExpenditureAccountLineAnnualBalanceAmount;
    
    public BigDecimal getAccountAppointmentRequestedCsfFteQuantity() {
        return accountAppointmentRequestedCsfFteQuantity;
    }
    public void setAccountAppointmentRequestedCsfFteQuantity(BigDecimal accountAppointmentRequestedCsfFteQuantity) {
        this.accountAppointmentRequestedCsfFteQuantity = accountAppointmentRequestedCsfFteQuantity;
    }
    public BigDecimal getAccountAppointmentRequestedFteQuantity() {
        return accountAppointmentRequestedFteQuantity;
    }
    public void setAccountAppointmentRequestedFteQuantity(BigDecimal accountAppointmentRequestedFteQuantity) {
        this.accountAppointmentRequestedFteQuantity = accountAppointmentRequestedFteQuantity;
    }
    public Integer getAccountExpenditureAccountLineAnnualBalanceAmount() {
        return accountExpenditureAccountLineAnnualBalanceAmount;
    }
    public void setAccountExpenditureAccountLineAnnualBalanceAmount(Integer accountExpenditureAccountLineAnnualBalanceAmount) {
        this.accountExpenditureAccountLineAnnualBalanceAmount = accountExpenditureAccountLineAnnualBalanceAmount;
    }
    public Integer getAccountExpenditureFinancialBeginningBalanceLineAmount() {
        return accountExpenditureFinancialBeginningBalanceLineAmount;
    }
    public void setAccountExpenditureFinancialBeginningBalanceLineAmount(Integer accountExpenditureFinancialBeginningBalanceLineAmount) {
        this.accountExpenditureFinancialBeginningBalanceLineAmount = accountExpenditureFinancialBeginningBalanceLineAmount;
    }
    public BigDecimal getAccountPositionCsfLeaveFteQuantity() {
        return accountPositionCsfLeaveFteQuantity;
    }
    public void setAccountPositionCsfLeaveFteQuantity(BigDecimal accountPositionCsfLeaveFteQuantity) {
        this.accountPositionCsfLeaveFteQuantity = accountPositionCsfLeaveFteQuantity;
    }
    public BigDecimal getAccountPositionFullTimeEquivalencyQuantity() {
        return accountPositionFullTimeEquivalencyQuantity;
    }
    public void setAccountPositionFullTimeEquivalencyQuantity(BigDecimal accountPositionFullTimeEquivalencyQuantity) {
        this.accountPositionFullTimeEquivalencyQuantity = accountPositionFullTimeEquivalencyQuantity;
    }
    public Integer getAccountRevenueAccountLineAnnualBalanceAmount() {
        return accountRevenueAccountLineAnnualBalanceAmount;
    }
    public void setAccountRevenueAccountLineAnnualBalanceAmount(Integer accountRevenueAccountLineAnnualBalanceAmount) {
        this.accountRevenueAccountLineAnnualBalanceAmount = accountRevenueAccountLineAnnualBalanceAmount;
    }
    public Integer getAccountRevenueFinancialBeginningBalanceLineAmount() {
        return accountRevenueFinancialBeginningBalanceLineAmount;
    }
    public void setAccountRevenueFinancialBeginningBalanceLineAmount(Integer accountRevenueFinancialBeginningBalanceLineAmount) {
        this.accountRevenueFinancialBeginningBalanceLineAmount = accountRevenueFinancialBeginningBalanceLineAmount;
    }
    public BudgetConstructionAccountBalance getBudgetConstructionAccountBalance() {
        return budgetConstructionAccountBalance;
    }
    public void setBudgetConstructionAccountBalance(BudgetConstructionAccountBalance budgetConstructionAccountBalance) {
        this.budgetConstructionAccountBalance = budgetConstructionAccountBalance;
    }
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
    public BigDecimal getSubFundAppointmentRequestedCsfFteQuantity() {
        return subFundAppointmentRequestedCsfFteQuantity;
    }
    public void setSubFundAppointmentRequestedCsfFteQuantity(BigDecimal subFundAppointmentRequestedCsfFteQuantity) {
        this.subFundAppointmentRequestedCsfFteQuantity = subFundAppointmentRequestedCsfFteQuantity;
    }
    public BigDecimal getSubFundAppointmentRequestedFteQuantity() {
        return subFundAppointmentRequestedFteQuantity;
    }
    public void setSubFundAppointmentRequestedFteQuantity(BigDecimal subFundAppointmentRequestedFteQuantity) {
        this.subFundAppointmentRequestedFteQuantity = subFundAppointmentRequestedFteQuantity;
    }
    public Integer getSubFundExpenditureAccountLineAnnualBalanceAmount() {
        return subFundExpenditureAccountLineAnnualBalanceAmount;
    }
    public void setSubFundExpenditureAccountLineAnnualBalanceAmount(Integer subFundExpenditureAccountLineAnnualBalanceAmount) {
        this.subFundExpenditureAccountLineAnnualBalanceAmount = subFundExpenditureAccountLineAnnualBalanceAmount;
    }
    public Integer getSubFundExpenditureFinancialBeginningBalanceLineAmount() {
        return subFundExpenditureFinancialBeginningBalanceLineAmount;
    }
    public void setSubFundExpenditureFinancialBeginningBalanceLineAmount(Integer subFundExpenditureFinancialBeginningBalanceLineAmount) {
        this.subFundExpenditureFinancialBeginningBalanceLineAmount = subFundExpenditureFinancialBeginningBalanceLineAmount;
    }
    public BigDecimal getSubFundPositionCsfLeaveFteQuantity() {
        return subFundPositionCsfLeaveFteQuantity;
    }
    public void setSubFundPositionCsfLeaveFteQuantity(BigDecimal subFundPositionCsfLeaveFteQuantity) {
        this.subFundPositionCsfLeaveFteQuantity = subFundPositionCsfLeaveFteQuantity;
    }
    public BigDecimal getSubFundPositionFullTimeEquivalencyQuantity() {
        return subFundPositionFullTimeEquivalencyQuantity;
    }
    public void setSubFundPositionFullTimeEquivalencyQuantity(BigDecimal subFundPositionFullTimeEquivalencyQuantity) {
        this.subFundPositionFullTimeEquivalencyQuantity = subFundPositionFullTimeEquivalencyQuantity;
    }
    public Integer getSubFundRevenueAccountLineAnnualBalanceAmount() {
        return subFundRevenueAccountLineAnnualBalanceAmount;
    }
    public void setSubFundRevenueAccountLineAnnualBalanceAmount(Integer subFundRevenueAccountLineAnnualBalanceAmount) {
        this.subFundRevenueAccountLineAnnualBalanceAmount = subFundRevenueAccountLineAnnualBalanceAmount;
    }
    public Integer getSubFundRevenueFinancialBeginningBalanceLineAmount() {
        return subFundRevenueFinancialBeginningBalanceLineAmount;
    }
    public void setSubFundRevenueFinancialBeginningBalanceLineAmount(Integer subFundRevenueFinancialBeginningBalanceLineAmount) {
        this.subFundRevenueFinancialBeginningBalanceLineAmount = subFundRevenueFinancialBeginningBalanceLineAmount;
    }
    public Integer getTotalLevelAccountLineAnnualBalanceAmount() {
        return totalLevelAccountLineAnnualBalanceAmount;
    }
    public void setTotalLevelAccountLineAnnualBalanceAmount(Integer totalLevelAccountLineAnnualBalanceAmount) {
        this.totalLevelAccountLineAnnualBalanceAmount = totalLevelAccountLineAnnualBalanceAmount;
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
    public BigDecimal getTotalLevelPositionCsfLeaveFteQuantity() {
        return totalLevelPositionCsfLeaveFteQuantity;
    }
    public void setTotalLevelPositionCsfLeaveFteQuantity(BigDecimal totalLevelPositionCsfLeaveFteQuantity) {
        this.totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity;
    }
    public BigDecimal getTotalLevelPositionFullTimeEquivalencyQuantity() {
        return totalLevelPositionFullTimeEquivalencyQuantity;
    }
    public void setTotalLevelPositionFullTimeEquivalencyQuantity(BigDecimal totalLevelPositionFullTimeEquivalencyQuantity) {
        this.totalLevelPositionFullTimeEquivalencyQuantity = totalLevelPositionFullTimeEquivalencyQuantity;
    }
    public Integer getTotalObjectAccountLineAnnualBalanceAmount() {
        return totalObjectAccountLineAnnualBalanceAmount;
    }
    public void setTotalObjectAccountLineAnnualBalanceAmount(Integer totalObjectAccountLineAnnualBalanceAmount) {
        this.totalObjectAccountLineAnnualBalanceAmount = totalObjectAccountLineAnnualBalanceAmount;
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
    public BigDecimal getTotalObjectPositionCsfLeaveFteQuantity() {
        return totalObjectPositionCsfLeaveFteQuantity;
    }
    public void setTotalObjectPositionCsfLeaveFteQuantity(BigDecimal totalObjectPositionCsfLeaveFteQuantity) {
        this.totalObjectPositionCsfLeaveFteQuantity = totalObjectPositionCsfLeaveFteQuantity;
    }
    public BigDecimal getTotalObjectPositionFullTimeEquivalencyQuantity() {
        return totalObjectPositionFullTimeEquivalencyQuantity;
    }
    public void setTotalObjectPositionFullTimeEquivalencyQuantity(BigDecimal totalObjectPositionFullTimeEquivalencyQuantity) {
        this.totalObjectPositionFullTimeEquivalencyQuantity = totalObjectPositionFullTimeEquivalencyQuantity;
    }
    public Integer getTypeAccountLineAnnualBalanceAmount() {
        return typeAccountLineAnnualBalanceAmount;
    }
    public void setTypeAccountLineAnnualBalanceAmount(Integer typeAccountLineAnnualBalanceAmount) {
        this.typeAccountLineAnnualBalanceAmount = typeAccountLineAnnualBalanceAmount;
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
    public BigDecimal getTypePositionCsfLeaveFteQuantity() {
        return typePositionCsfLeaveFteQuantity;
    }
    public void setTypePositionCsfLeaveFteQuantity(BigDecimal typePositionCsfLeaveFteQuantity) {
        this.typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity;
    }
    public BigDecimal getTypePositionFullTimeEquivalencyQuantity() {
        return typePositionFullTimeEquivalencyQuantity;
    }
    public void setTypePositionFullTimeEquivalencyQuantity(BigDecimal typePositionFullTimeEquivalencyQuantity) {
        this.typePositionFullTimeEquivalencyQuantity = typePositionFullTimeEquivalencyQuantity;
    }
    public Integer getAccountTrnfrInAccountLineAnnualBalanceAmount() {
        return accountTrnfrInAccountLineAnnualBalanceAmount;
    }
    public void setAccountTrnfrInAccountLineAnnualBalanceAmount(Integer accountTrnfrInAccountLineAnnualBalanceAmount) {
        this.accountTrnfrInAccountLineAnnualBalanceAmount = accountTrnfrInAccountLineAnnualBalanceAmount;
    }
    public Integer getAccountTrnfrInFinancialBeginningBalanceLineAmount() {
        return accountTrnfrInFinancialBeginningBalanceLineAmount;
    }
    public void setAccountTrnfrInFinancialBeginningBalanceLineAmount(Integer accountTrnfrInFinancialBeginningBalanceLineAmount) {
        this.accountTrnfrInFinancialBeginningBalanceLineAmount = accountTrnfrInFinancialBeginningBalanceLineAmount;
    }
    public Integer getSubFundTrnfrInAccountLineAnnualBalanceAmount() {
        return subFundTrnfrInAccountLineAnnualBalanceAmount;
    }
    public void setSubFundTrnfrInAccountLineAnnualBalanceAmount(Integer subFundTrnfrInAccountLineAnnualBalanceAmount) {
        this.subFundTrnfrInAccountLineAnnualBalanceAmount = subFundTrnfrInAccountLineAnnualBalanceAmount;
    }
    public Integer getSubFundTrnfrInFinancialBeginningBalanceLineAmount() {
        return subFundTrnfrInFinancialBeginningBalanceLineAmount;
    }
    public void setSubFundTrnfrInFinancialBeginningBalanceLineAmount(Integer subFundTrnfrInFinancialBeginningBalanceLineAmount) {
        this.subFundTrnfrInFinancialBeginningBalanceLineAmount = subFundTrnfrInFinancialBeginningBalanceLineAmount;
    }
    
   
    }
