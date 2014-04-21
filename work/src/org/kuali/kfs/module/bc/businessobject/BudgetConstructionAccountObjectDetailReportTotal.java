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
public class BudgetConstructionAccountObjectDetailReportTotal {

    // Total parts

    private String totalObjectDescription;

    private BigDecimal totalObjectPositionCsfLeaveFteQuantity;
    private BigDecimal totalObjectCsfFullTimeEmploymentQuantity;
    private KualiInteger totalObjectFinancialBeginningBalanceLineAmount;
    private BigDecimal totalObjectAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedFteQuantity;
    private KualiInteger totalObjectAccountLineAnnualBalanceAmount;

    private String totalLevelDescription;

    private BigDecimal totalLevelPositionCsfLeaveFteQuantity;
    private BigDecimal totalLevelCsfFullTimeEmploymentQuantity;
    private KualiInteger totalLevelFinancialBeginningBalanceLineAmount;
    private BigDecimal totalLevelAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalLevelAppointmentRequestedFteQuantity;
    private KualiInteger totalLevelAccountLineAnnualBalanceAmount;

    private String typeDesc;

    private BigDecimal totalTypePositionCsfLeaveFteQuantity;
    private BigDecimal totalTypeCsfFullTimeEmploymentQuantity;
    private KualiInteger totalTypeFinancialBeginningBalanceLineAmount;
    private BigDecimal totalTypeAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalTypeAppointmentRequestedFteQuantity;
    private KualiInteger totalTypeAccountLineAnnualBalanceAmount;

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

    public KualiInteger getTotalObjectFinancialBeginningBalanceLineAmount() {
        return totalObjectFinancialBeginningBalanceLineAmount;
    }

    public void setTotalObjectFinancialBeginningBalanceLineAmount(KualiInteger totalObjectFinancialBeginningBalanceLineAmount) {
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

    public KualiInteger getTotalObjectAccountLineAnnualBalanceAmount() {
        return totalObjectAccountLineAnnualBalanceAmount;
    }

    public void setTotalObjectAccountLineAnnualBalanceAmount(KualiInteger totalObjectAccountLineAnnualBalanceAmount) {
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

    public KualiInteger getTotalLevelFinancialBeginningBalanceLineAmount() {
        return totalLevelFinancialBeginningBalanceLineAmount;
    }

    public void setTotalLevelFinancialBeginningBalanceLineAmount(KualiInteger totalLevelFinancialBeginningBalanceLineAmount) {
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

    public KualiInteger getTotalLevelAccountLineAnnualBalanceAmount() {
        return totalLevelAccountLineAnnualBalanceAmount;
    }

    public void setTotalLevelAccountLineAnnualBalanceAmount(KualiInteger totalLevelAccountLineAnnualBalanceAmount) {
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

    public KualiInteger getTotalTypeFinancialBeginningBalanceLineAmount() {
        return totalTypeFinancialBeginningBalanceLineAmount;
    }

    public void setTotalTypeFinancialBeginningBalanceLineAmount(KualiInteger totalTypeFinancialBeginningBalanceLineAmount) {
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

    public KualiInteger getTotalTypeAccountLineAnnualBalanceAmount() {
        return totalTypeAccountLineAnnualBalanceAmount;
    }

    public void setTotalTypeAccountLineAnnualBalanceAmount(KualiInteger totalTypeAccountLineAnnualBalanceAmount) {
        this.totalTypeAccountLineAnnualBalanceAmount = totalTypeAccountLineAnnualBalanceAmount;
    }

    public BudgetConstructionBalanceByAccount getBudgetConstructionBalanceByAccount() {
        return budgetConstructionBalanceByAccount;
    }

    public void setBudgetConstructionBalanceByAccount(BudgetConstructionBalanceByAccount budgetConstructionBalanceByAccount) {
        this.budgetConstructionBalanceByAccount = budgetConstructionBalanceByAccount;
    }
}
