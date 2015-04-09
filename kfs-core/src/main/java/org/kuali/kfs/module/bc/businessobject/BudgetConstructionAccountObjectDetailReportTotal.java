/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
