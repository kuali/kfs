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
public class BudgetConstructionAccountObjectDetailReportTotal {

    // Total parts

    private String totalObjectDescription;

    private BigDecimal totalObjectPositionCsfLeaveFteQuantity;
    private BigDecimal totalObjectCsfFullTimeEmploymentQuantity;
    private Integer totalObjectFinancialBeginningBalanceLineAmount;
    private BigDecimal totalObjectAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedFteQuantity;
    private Integer totalObjectAccountLineAnnualBalanceAmount;
    
    private String totalLevelDescription;

    private BigDecimal totalLevelPositionCsfLeaveFteQuantity;
    private BigDecimal totalLevelCsfFullTimeEmploymentQuantity;
    private Integer totalLevelFinancialBeginningBalanceLineAmount;
    private BigDecimal totalLevelAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalLevelAppointmentRequestedFteQuantity;
    private Integer totalLevelAccountLineAnnualBalanceAmount;
    
    private String typeDesc;
    
    private BigDecimal totalTypePositionCsfLeaveFteQuantity;
    private BigDecimal totalTypeCsfFullTimeEmploymentQuantity;
    private Integer totalTypeFinancialBeginningBalanceLineAmount;
    private BigDecimal totalTypeAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalTypeAppointmentRequestedFteQuantity;
    private Integer totalTypeAccountLineAnnualBalanceAmount;
    
    private BudgetConstructionBalanceByAccount budgetConstructionBalanceByAccount;

    public String getTotalObjectDescription() {
        return totalObjectDescription;
    }

    public void setTotalObjectDescription(String totalObjectDescription) {
        this.totalObjectDescription = totalObjectDescription;
    }

    public BigDecimal getTotalObjectPositionCsfLeaveFteQuantity() {
        return totalObjectPositionCsfLeaveFteQuantity;
    }

    public void setTotalObjectPositionCsfLeaveFteQuantity(BigDecimal totalObjectPositionCsfLeaveFteQuantity) {
        this.totalObjectPositionCsfLeaveFteQuantity = totalObjectPositionCsfLeaveFteQuantity;
    }

    public BigDecimal getTotalObjectCsfFullTimeEmploymentQuantity() {
        return totalObjectCsfFullTimeEmploymentQuantity;
    }

    public void setTotalObjectCsfFullTimeEmploymentQuantity(BigDecimal totalObjectCsfFullTimeEmploymentQuantity) {
        this.totalObjectCsfFullTimeEmploymentQuantity = totalObjectCsfFullTimeEmploymentQuantity;
    }

    public Integer getTotalObjectFinancialBeginningBalanceLineAmount() {
        return totalObjectFinancialBeginningBalanceLineAmount;
    }

    public void setTotalObjectFinancialBeginningBalanceLineAmount(Integer totalObjectFinancialBeginningBalanceLineAmount) {
        this.totalObjectFinancialBeginningBalanceLineAmount = totalObjectFinancialBeginningBalanceLineAmount;
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

    public Integer getTotalObjectAccountLineAnnualBalanceAmount() {
        return totalObjectAccountLineAnnualBalanceAmount;
    }

    public void setTotalObjectAccountLineAnnualBalanceAmount(Integer totalObjectAccountLineAnnualBalanceAmount) {
        this.totalObjectAccountLineAnnualBalanceAmount = totalObjectAccountLineAnnualBalanceAmount;
    }
    public String getTotalLevelDescription() {
        return totalLevelDescription;
    }

    public void setTotalLevelDescription(String totalLevelDescription) {
        this.totalLevelDescription = totalLevelDescription;
    }

    public BigDecimal getTotalLevelPositionCsfLeaveFteQuantity() {
        return totalLevelPositionCsfLeaveFteQuantity;
    }

    public void setTotalLevelPositionCsfLeaveFteQuantity(BigDecimal totalLevelPositionCsfLeaveFteQuantity) {
        this.totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity;
    }

    public BigDecimal getTotalLevelCsfFullTimeEmploymentQuantity() {
        return totalLevelCsfFullTimeEmploymentQuantity;
    }

    public void setTotalLevelCsfFullTimeEmploymentQuantity(BigDecimal totalLevelCsfFullTimeEmploymentQuantity) {
        this.totalLevelCsfFullTimeEmploymentQuantity = totalLevelCsfFullTimeEmploymentQuantity;
    }

    public Integer getTotalLevelFinancialBeginningBalanceLineAmount() {
        return totalLevelFinancialBeginningBalanceLineAmount;
    }

    public void setTotalLevelFinancialBeginningBalanceLineAmount(Integer totalLevelFinancialBeginningBalanceLineAmount) {
        this.totalLevelFinancialBeginningBalanceLineAmount = totalLevelFinancialBeginningBalanceLineAmount;
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

    public Integer getTotalLevelAccountLineAnnualBalanceAmount() {
        return totalLevelAccountLineAnnualBalanceAmount;
    }

    public void setTotalLevelAccountLineAnnualBalanceAmount(Integer totalLevelAccountLineAnnualBalanceAmount) {
        this.totalLevelAccountLineAnnualBalanceAmount = totalLevelAccountLineAnnualBalanceAmount;
    }
    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public BigDecimal getTotalTypePositionCsfLeaveFteQuantity() {
        return totalTypePositionCsfLeaveFteQuantity;
    }

    public void setTotalTypePositionCsfLeaveFteQuantity(BigDecimal totalTypePositionCsfLeaveFteQuantity) {
        this.totalTypePositionCsfLeaveFteQuantity = totalTypePositionCsfLeaveFteQuantity;
    }

    public BigDecimal getTotalTypeCsfFullTimeEmploymentQuantity() {
        return totalTypeCsfFullTimeEmploymentQuantity;
    }

    public void setTotalTypeCsfFullTimeEmploymentQuantity(BigDecimal totalTypeCsfFullTimeEmploymentQuantity) {
        this.totalTypeCsfFullTimeEmploymentQuantity = totalTypeCsfFullTimeEmploymentQuantity;
    }

    public Integer getTotalTypeFinancialBeginningBalanceLineAmount() {
        return totalTypeFinancialBeginningBalanceLineAmount;
    }

    public void setTotalTypeFinancialBeginningBalanceLineAmount(Integer totalTypeFinancialBeginningBalanceLineAmount) {
        this.totalTypeFinancialBeginningBalanceLineAmount = totalTypeFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getTotalTypeAppointmentRequestedCsfFteQuantity() {
        return totalTypeAppointmentRequestedCsfFteQuantity;
    }

    public void setTotalTypeAppointmentRequestedCsfFteQuantity(BigDecimal totalTypeAppointmentRequestedCsfFteQuantity) {
        this.totalTypeAppointmentRequestedCsfFteQuantity = totalTypeAppointmentRequestedCsfFteQuantity;
    }

    public BigDecimal getTotalTypeAppointmentRequestedFteQuantity() {
        return totalTypeAppointmentRequestedFteQuantity;
    }

    public void setTotalTypeAppointmentRequestedFteQuantity(BigDecimal totalTypeAppointmentRequestedFteQuantity) {
        this.totalTypeAppointmentRequestedFteQuantity = totalTypeAppointmentRequestedFteQuantity;
    }

    public Integer getTotalTypeAccountLineAnnualBalanceAmount() {
        return totalTypeAccountLineAnnualBalanceAmount;
    }

    public void setTotalTypeAccountLineAnnualBalanceAmount(Integer totalTypeAccountLineAnnualBalanceAmount) {
        this.totalTypeAccountLineAnnualBalanceAmount = totalTypeAccountLineAnnualBalanceAmount;
    }

    public BudgetConstructionBalanceByAccount getBudgetConstructionBalanceByAccount() {
        return budgetConstructionBalanceByAccount;
    }

    public void setBudgetConstructionBalanceByAccount(BudgetConstructionBalanceByAccount budgetConstructionBalanceByAccount) {
        this.budgetConstructionBalanceByAccount = budgetConstructionBalanceByAccount;
    }
}
