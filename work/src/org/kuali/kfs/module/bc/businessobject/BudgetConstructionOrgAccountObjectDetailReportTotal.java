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

import org.kuali.rice.core.api.util.type.KualiInteger;

/**
 * Budget Construction Organization Account Summary Report Business Object.
 */
public class BudgetConstructionOrgAccountObjectDetailReportTotal {

    private BudgetConstructionAccountBalance budgetConstructionAccountBalance;

    // Total parts

    // object total
    private BigDecimal totalObjectPositionCsfLeaveFteQuantity;
    private BigDecimal totalObjectPositionFullTimeEquivalencyQuantity;
    private KualiInteger totalObjectFinancialBeginningBalanceLineAmount;
    private BigDecimal totalObjectAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedFteQuantity;
    private KualiInteger totalObjectAccountLineAnnualBalanceAmount;

    //level total
    private BigDecimal totalLevelPositionCsfLeaveFteQuantity;
    private BigDecimal totalLevelPositionFullTimeEquivalencyQuantity;
    private KualiInteger totalLevelFinancialBeginningBalanceLineAmount;
    private BigDecimal totalLevelAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalLevelAppointmentRequestedFteQuantity;
    private KualiInteger totalLevelAccountLineAnnualBalanceAmount;

    // Gexp And Type Total
    private KualiInteger grossFinancialBeginningBalanceLineAmount;
    private KualiInteger grossAccountLineAnnualBalanceAmount;

    private BigDecimal typePositionCsfLeaveFteQuantity;
    private BigDecimal typePositionFullTimeEquivalencyQuantity;
    private KualiInteger typeFinancialBeginningBalanceLineAmount;
    private BigDecimal typeAppointmentRequestedCsfFteQuantity;
    private BigDecimal typeAppointmentRequestedFteQuantity;
    private KualiInteger typeAccountLineAnnualBalanceAmount;

    //account total
    private BigDecimal accountPositionCsfLeaveFteQuantity;
    private BigDecimal accountPositionFullTimeEquivalencyQuantity;
    private BigDecimal accountAppointmentRequestedCsfFteQuantity;
    private BigDecimal accountAppointmentRequestedFteQuantity;

    private KualiInteger accountRevenueFinancialBeginningBalanceLineAmount;
    private KualiInteger accountRevenueAccountLineAnnualBalanceAmount;
    private KualiInteger accountTrnfrInFinancialBeginningBalanceLineAmount;
    private KualiInteger accountTrnfrInAccountLineAnnualBalanceAmount;
    private KualiInteger accountExpenditureFinancialBeginningBalanceLineAmount;
    private KualiInteger accountExpenditureAccountLineAnnualBalanceAmount;

    //subFund total
    private BigDecimal subFundPositionCsfLeaveFteQuantity;
    private BigDecimal subFundPositionFullTimeEquivalencyQuantity;
    private BigDecimal subFundAppointmentRequestedCsfFteQuantity;
    private BigDecimal subFundAppointmentRequestedFteQuantity;

    private KualiInteger subFundRevenueFinancialBeginningBalanceLineAmount;
    private KualiInteger subFundRevenueAccountLineAnnualBalanceAmount;
    private KualiInteger subFundTrnfrInFinancialBeginningBalanceLineAmount;
    private KualiInteger subFundTrnfrInAccountLineAnnualBalanceAmount;
    private KualiInteger subFundExpenditureFinancialBeginningBalanceLineAmount;
    private KualiInteger subFundExpenditureAccountLineAnnualBalanceAmount;

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
    public KualiInteger getAccountExpenditureAccountLineAnnualBalanceAmount() {
        return accountExpenditureAccountLineAnnualBalanceAmount;
    }
    public void setAccountExpenditureAccountLineAnnualBalanceAmount(KualiInteger accountExpenditureAccountLineAnnualBalanceAmount) {
        this.accountExpenditureAccountLineAnnualBalanceAmount = accountExpenditureAccountLineAnnualBalanceAmount;
    }
    public KualiInteger getAccountExpenditureFinancialBeginningBalanceLineAmount() {
        return accountExpenditureFinancialBeginningBalanceLineAmount;
    }
    public void setAccountExpenditureFinancialBeginningBalanceLineAmount(KualiInteger accountExpenditureFinancialBeginningBalanceLineAmount) {
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
    public KualiInteger getAccountRevenueAccountLineAnnualBalanceAmount() {
        return accountRevenueAccountLineAnnualBalanceAmount;
    }
    public void setAccountRevenueAccountLineAnnualBalanceAmount(KualiInteger accountRevenueAccountLineAnnualBalanceAmount) {
        this.accountRevenueAccountLineAnnualBalanceAmount = accountRevenueAccountLineAnnualBalanceAmount;
    }
    public KualiInteger getAccountRevenueFinancialBeginningBalanceLineAmount() {
        return accountRevenueFinancialBeginningBalanceLineAmount;
    }
    public void setAccountRevenueFinancialBeginningBalanceLineAmount(KualiInteger accountRevenueFinancialBeginningBalanceLineAmount) {
        this.accountRevenueFinancialBeginningBalanceLineAmount = accountRevenueFinancialBeginningBalanceLineAmount;
    }
    public BudgetConstructionAccountBalance getBudgetConstructionAccountBalance() {
        return budgetConstructionAccountBalance;
    }
    public void setBudgetConstructionAccountBalance(BudgetConstructionAccountBalance budgetConstructionAccountBalance) {
        this.budgetConstructionAccountBalance = budgetConstructionAccountBalance;
    }
    public KualiInteger getGrossAccountLineAnnualBalanceAmount() {
        return grossAccountLineAnnualBalanceAmount;
    }
    public void setGrossAccountLineAnnualBalanceAmount(KualiInteger grossAccountLineAnnualBalanceAmount) {
        this.grossAccountLineAnnualBalanceAmount = grossAccountLineAnnualBalanceAmount;
    }
    public KualiInteger getGrossFinancialBeginningBalanceLineAmount() {
        return grossFinancialBeginningBalanceLineAmount;
    }
    public void setGrossFinancialBeginningBalanceLineAmount(KualiInteger grossFinancialBeginningBalanceLineAmount) {
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
    public KualiInteger getSubFundExpenditureAccountLineAnnualBalanceAmount() {
        return subFundExpenditureAccountLineAnnualBalanceAmount;
    }
    public void setSubFundExpenditureAccountLineAnnualBalanceAmount(KualiInteger subFundExpenditureAccountLineAnnualBalanceAmount) {
        this.subFundExpenditureAccountLineAnnualBalanceAmount = subFundExpenditureAccountLineAnnualBalanceAmount;
    }
    public KualiInteger getSubFundExpenditureFinancialBeginningBalanceLineAmount() {
        return subFundExpenditureFinancialBeginningBalanceLineAmount;
    }
    public void setSubFundExpenditureFinancialBeginningBalanceLineAmount(KualiInteger subFundExpenditureFinancialBeginningBalanceLineAmount) {
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
    public KualiInteger getSubFundRevenueAccountLineAnnualBalanceAmount() {
        return subFundRevenueAccountLineAnnualBalanceAmount;
    }
    public void setSubFundRevenueAccountLineAnnualBalanceAmount(KualiInteger subFundRevenueAccountLineAnnualBalanceAmount) {
        this.subFundRevenueAccountLineAnnualBalanceAmount = subFundRevenueAccountLineAnnualBalanceAmount;
    }
    public KualiInteger getSubFundRevenueFinancialBeginningBalanceLineAmount() {
        return subFundRevenueFinancialBeginningBalanceLineAmount;
    }
    public void setSubFundRevenueFinancialBeginningBalanceLineAmount(KualiInteger subFundRevenueFinancialBeginningBalanceLineAmount) {
        this.subFundRevenueFinancialBeginningBalanceLineAmount = subFundRevenueFinancialBeginningBalanceLineAmount;
    }
    public KualiInteger getTotalLevelAccountLineAnnualBalanceAmount() {
        return totalLevelAccountLineAnnualBalanceAmount;
    }
    public void setTotalLevelAccountLineAnnualBalanceAmount(KualiInteger totalLevelAccountLineAnnualBalanceAmount) {
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
    public KualiInteger getTotalLevelFinancialBeginningBalanceLineAmount() {
        return totalLevelFinancialBeginningBalanceLineAmount;
    }
    public void setTotalLevelFinancialBeginningBalanceLineAmount(KualiInteger totalLevelFinancialBeginningBalanceLineAmount) {
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
    public KualiInteger getTotalObjectAccountLineAnnualBalanceAmount() {
        return totalObjectAccountLineAnnualBalanceAmount;
    }
    public void setTotalObjectAccountLineAnnualBalanceAmount(KualiInteger totalObjectAccountLineAnnualBalanceAmount) {
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
    public KualiInteger getTotalObjectFinancialBeginningBalanceLineAmount() {
        return totalObjectFinancialBeginningBalanceLineAmount;
    }
    public void setTotalObjectFinancialBeginningBalanceLineAmount(KualiInteger totalObjectFinancialBeginningBalanceLineAmount) {
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
    public KualiInteger getTypeAccountLineAnnualBalanceAmount() {
        return typeAccountLineAnnualBalanceAmount;
    }
    public void setTypeAccountLineAnnualBalanceAmount(KualiInteger typeAccountLineAnnualBalanceAmount) {
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
    public KualiInteger getTypeFinancialBeginningBalanceLineAmount() {
        return typeFinancialBeginningBalanceLineAmount;
    }
    public void setTypeFinancialBeginningBalanceLineAmount(KualiInteger typeFinancialBeginningBalanceLineAmount) {
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
    public KualiInteger getAccountTrnfrInAccountLineAnnualBalanceAmount() {
        return accountTrnfrInAccountLineAnnualBalanceAmount;
    }
    public void setAccountTrnfrInAccountLineAnnualBalanceAmount(KualiInteger accountTrnfrInAccountLineAnnualBalanceAmount) {
        this.accountTrnfrInAccountLineAnnualBalanceAmount = accountTrnfrInAccountLineAnnualBalanceAmount;
    }
    public KualiInteger getAccountTrnfrInFinancialBeginningBalanceLineAmount() {
        return accountTrnfrInFinancialBeginningBalanceLineAmount;
    }
    public void setAccountTrnfrInFinancialBeginningBalanceLineAmount(KualiInteger accountTrnfrInFinancialBeginningBalanceLineAmount) {
        this.accountTrnfrInFinancialBeginningBalanceLineAmount = accountTrnfrInFinancialBeginningBalanceLineAmount;
    }
    public KualiInteger getSubFundTrnfrInAccountLineAnnualBalanceAmount() {
        return subFundTrnfrInAccountLineAnnualBalanceAmount;
    }
    public void setSubFundTrnfrInAccountLineAnnualBalanceAmount(KualiInteger subFundTrnfrInAccountLineAnnualBalanceAmount) {
        this.subFundTrnfrInAccountLineAnnualBalanceAmount = subFundTrnfrInAccountLineAnnualBalanceAmount;
    }
    public KualiInteger getSubFundTrnfrInFinancialBeginningBalanceLineAmount() {
        return subFundTrnfrInFinancialBeginningBalanceLineAmount;
    }
    public void setSubFundTrnfrInFinancialBeginningBalanceLineAmount(KualiInteger subFundTrnfrInFinancialBeginningBalanceLineAmount) {
        this.subFundTrnfrInFinancialBeginningBalanceLineAmount = subFundTrnfrInFinancialBeginningBalanceLineAmount;
    }


    }
