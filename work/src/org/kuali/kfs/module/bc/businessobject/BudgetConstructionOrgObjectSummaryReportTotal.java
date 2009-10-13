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
public class BudgetConstructionOrgObjectSummaryReportTotal {

    private BudgetConstructionObjectSummary bcos;

    // Total parts
    
    private BigDecimal totalLevelPositionCsfLeaveFteQuantity;
    private BigDecimal totalLevelPositionCsfFullTimeEmploymentQuantity;
    private Integer totalLevelFinancialBeginningBalanceLineAmount;
    private BigDecimal totalLevelAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalLevelAppointmentRequestedFteQuantity;
    private Integer totalLevelAccountLineAnnualBalanceAmount;

    private BigDecimal totalConsolidationPositionCsfLeaveFteQuantity;
    private BigDecimal totalConsolidationPositionCsfFullTimeEmploymentQuantity;
    private Integer totalConsolidationFinancialBeginningBalanceLineAmount;
    private BigDecimal totalConsolidationAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalConsolidationAppointmentRequestedFteQuantity;
    private Integer totalConsolidationAccountLineAnnualBalanceAmount;

    private Integer grossFinancialBeginningBalanceLineAmount;
    private Integer grossAccountLineAnnualBalanceAmount;

    private BigDecimal typePositionCsfLeaveFteQuantity;
    private BigDecimal typePositionCsfFullTimeEmploymentQuantity;
    private Integer typeFinancialBeginningBalanceLineAmount;
    private BigDecimal typeAppointmentRequestedCsfFteQuantity;
    private BigDecimal typeAppointmentRequestedFteQuantity;
    private Integer typeAccountLineAnnualBalanceAmount;

    private Integer revenueFinancialBeginningBalanceLineAmount;
    private Integer revenueAccountLineAnnualBalanceAmount;

    private Integer expenditureFinancialBeginningBalanceLineAmount;
    private Integer expenditureAccountLineAnnualBalanceAmount;

    private Integer differenceFinancialBeginningBalanceLineAmount;
    private Integer differenceAccountLineAnnualBalanceAmount;

    public BudgetConstructionObjectSummary getBcos() {
        return bcos;
    }
    public void setBcos(BudgetConstructionObjectSummary bcos) {
        this.bcos = bcos;
    }
    public Integer getDifferenceAccountLineAnnualBalanceAmount() {
        return differenceAccountLineAnnualBalanceAmount;
    }
    public void setDifferenceAccountLineAnnualBalanceAmount(Integer differenceAccountLineAnnualBalanceAmount) {
        this.differenceAccountLineAnnualBalanceAmount = differenceAccountLineAnnualBalanceAmount;
    }
    public Integer getDifferenceFinancialBeginningBalanceLineAmount() {
        return differenceFinancialBeginningBalanceLineAmount;
    }
    public void setDifferenceFinancialBeginningBalanceLineAmount(Integer differenceFinancialBeginningBalanceLineAmount) {
        this.differenceFinancialBeginningBalanceLineAmount = differenceFinancialBeginningBalanceLineAmount;
    }
    public Integer getExpenditureAccountLineAnnualBalanceAmount() {
        return expenditureAccountLineAnnualBalanceAmount;
    }
    public void setExpenditureAccountLineAnnualBalanceAmount(Integer expenditureAccountLineAnnualBalanceAmount) {
        this.expenditureAccountLineAnnualBalanceAmount = expenditureAccountLineAnnualBalanceAmount;
    }
    public Integer getExpenditureFinancialBeginningBalanceLineAmount() {
        return expenditureFinancialBeginningBalanceLineAmount;
    }
    public void setExpenditureFinancialBeginningBalanceLineAmount(Integer expenditureFinancialBeginningBalanceLineAmount) {
        this.expenditureFinancialBeginningBalanceLineAmount = expenditureFinancialBeginningBalanceLineAmount;
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
    public Integer getRevenueAccountLineAnnualBalanceAmount() {
        return revenueAccountLineAnnualBalanceAmount;
    }
    public void setRevenueAccountLineAnnualBalanceAmount(Integer revenueAccountLineAnnualBalanceAmount) {
        this.revenueAccountLineAnnualBalanceAmount = revenueAccountLineAnnualBalanceAmount;
    }
    public Integer getRevenueFinancialBeginningBalanceLineAmount() {
        return revenueFinancialBeginningBalanceLineAmount;
    }
    public void setRevenueFinancialBeginningBalanceLineAmount(Integer revenueFinancialBeginningBalanceLineAmount) {
        this.revenueFinancialBeginningBalanceLineAmount = revenueFinancialBeginningBalanceLineAmount;
    }
    public Integer getTotalConsolidationAccountLineAnnualBalanceAmount() {
        return totalConsolidationAccountLineAnnualBalanceAmount;
    }
    public void setTotalConsolidationAccountLineAnnualBalanceAmount(Integer totalConsolidationAccountLineAnnualBalanceAmount) {
        this.totalConsolidationAccountLineAnnualBalanceAmount = totalConsolidationAccountLineAnnualBalanceAmount;
    }
    public BigDecimal getTotalConsolidationAppointmentRequestedCsfFteQuantity() {
        return totalConsolidationAppointmentRequestedCsfFteQuantity;
    }
    public void setTotalConsolidationAppointmentRequestedCsfFteQuantity(BigDecimal totalConsolidationAppointmentRequestedCsfFteQuantity) {
        this.totalConsolidationAppointmentRequestedCsfFteQuantity = totalConsolidationAppointmentRequestedCsfFteQuantity;
    }
    public BigDecimal getTotalConsolidationAppointmentRequestedFteQuantity() {
        return totalConsolidationAppointmentRequestedFteQuantity;
    }
    public void setTotalConsolidationAppointmentRequestedFteQuantity(BigDecimal totalConsolidationAppointmentRequestedFteQuantity) {
        this.totalConsolidationAppointmentRequestedFteQuantity = totalConsolidationAppointmentRequestedFteQuantity;
    }
    public Integer getTotalConsolidationFinancialBeginningBalanceLineAmount() {
        return totalConsolidationFinancialBeginningBalanceLineAmount;
    }
    public void setTotalConsolidationFinancialBeginningBalanceLineAmount(Integer totalConsolidationFinancialBeginningBalanceLineAmount) {
        this.totalConsolidationFinancialBeginningBalanceLineAmount = totalConsolidationFinancialBeginningBalanceLineAmount;
    }
    public BigDecimal getTotalConsolidationPositionCsfFullTimeEmploymentQuantity() {
        return totalConsolidationPositionCsfFullTimeEmploymentQuantity;
    }
    public void setTotalConsolidationPositionCsfFullTimeEmploymentQuantity(BigDecimal totalConsolidationPositionCsfFullTimeEmploymentQuantity) {
        this.totalConsolidationPositionCsfFullTimeEmploymentQuantity = totalConsolidationPositionCsfFullTimeEmploymentQuantity;
    }
    public BigDecimal getTotalConsolidationPositionCsfLeaveFteQuantity() {
        return totalConsolidationPositionCsfLeaveFteQuantity;
    }
    public void setTotalConsolidationPositionCsfLeaveFteQuantity(BigDecimal totalConsolidationPositionCsfLeaveFteQuantity) {
        this.totalConsolidationPositionCsfLeaveFteQuantity = totalConsolidationPositionCsfLeaveFteQuantity;
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
    public BigDecimal getTotalLevelPositionCsfFullTimeEmploymentQuantity() {
        return totalLevelPositionCsfFullTimeEmploymentQuantity;
    }
    public void setTotalLevelPositionCsfFullTimeEmploymentQuantity(BigDecimal totalLevelPositionCsfFullTimeEmploymentQuantity) {
        this.totalLevelPositionCsfFullTimeEmploymentQuantity = totalLevelPositionCsfFullTimeEmploymentQuantity;
    }
    public BigDecimal getTotalLevelPositionCsfLeaveFteQuantity() {
        return totalLevelPositionCsfLeaveFteQuantity;
    }
    public void setTotalLevelPositionCsfLeaveFteQuantity(BigDecimal totalLevelPositionCsfLeaveFteQuantity) {
        this.totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity;
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
    public BigDecimal getTypePositionCsfFullTimeEmploymentQuantity() {
        return typePositionCsfFullTimeEmploymentQuantity;
    }
    public void setTypePositionCsfFullTimeEmploymentQuantity(BigDecimal typePositionCsfFullTimeEmploymentQuantity) {
        this.typePositionCsfFullTimeEmploymentQuantity = typePositionCsfFullTimeEmploymentQuantity;
    }
    public BigDecimal getTypePositionCsfLeaveFteQuantity() {
        return typePositionCsfLeaveFteQuantity;
    }
    public void setTypePositionCsfLeaveFteQuantity(BigDecimal typePositionCsfLeaveFteQuantity) {
        this.typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity;
    }
   }
