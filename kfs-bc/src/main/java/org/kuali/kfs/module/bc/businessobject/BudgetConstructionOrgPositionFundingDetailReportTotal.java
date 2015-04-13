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

/**
 * Total Part of Budget Construction Organization Account Funding Detail Total Business Object.
 */
public class BudgetConstructionOrgPositionFundingDetailReportTotal {
    private BudgetConstructionPositionFunding budgetConstructionPositionFunding;
    
    private Integer totalPersonPositionCsfAmount;
    private Integer totalPersonAppointmentRequestedAmount;
    private BigDecimal totalPersonPositionCsfFteQuantity;
    private BigDecimal totalPersonAppointmentRequestedFteQuantity;
    
    private Integer totalOrgPositionCsfAmount;
    private Integer totalOrgAppointmentRequestedAmount;
    private BigDecimal totalOrgPositionCsfFteQuantity;
    private BigDecimal totalOrgAppointmentRequestedFteQuantity;
    
    private Integer personSortCode;
    
    
    
    /**
     * Default constructor.
     */
    public BudgetConstructionOrgPositionFundingDetailReportTotal() {
    }

    public Integer getPersonSortCode() {
        return personSortCode;
    }


    public void setPersonSortCode(Integer personSortCode) {
        this.personSortCode = personSortCode;
    }
    
    
    public BudgetConstructionPositionFunding getBudgetConstructionPositionFunding() {
        return budgetConstructionPositionFunding;
    }

    public void setBudgetConstructionPositionFunding(BudgetConstructionPositionFunding budgetConstructionPositionFunding) {
        this.budgetConstructionPositionFunding = budgetConstructionPositionFunding;
    }

    public Integer getTotalOrgAppointmentRequestedAmount() {
        return totalOrgAppointmentRequestedAmount;
    }

    public void setTotalOrgAppointmentRequestedAmount(Integer totalOrgAppointmentRequestedAmount) {
        this.totalOrgAppointmentRequestedAmount = totalOrgAppointmentRequestedAmount;
    }

    public BigDecimal getTotalOrgAppointmentRequestedFteQuantity() {
        return totalOrgAppointmentRequestedFteQuantity;
    }

    public void setTotalOrgAppointmentRequestedFteQuantity(BigDecimal totalOrgAppointmentRequestedFteQuantity) {
        this.totalOrgAppointmentRequestedFteQuantity = totalOrgAppointmentRequestedFteQuantity;
    }

    public Integer getTotalOrgPositionCsfAmount() {
        return totalOrgPositionCsfAmount;
    }

    public void setTotalOrgPositionCsfAmount(Integer totalOrgPositionCsfAmount) {
        this.totalOrgPositionCsfAmount = totalOrgPositionCsfAmount;
    }

    public BigDecimal getTotalOrgPositionCsfFteQuantity() {
        return totalOrgPositionCsfFteQuantity;
    }

    public void setTotalOrgPositionCsfFteQuantity(BigDecimal totalOrgPositionCsfFteQuantity) {
        this.totalOrgPositionCsfFteQuantity = totalOrgPositionCsfFteQuantity;
    }

    public Integer getTotalPersonAppointmentRequestedAmount() {
        return totalPersonAppointmentRequestedAmount;
    }

    public void setTotalPersonAppointmentRequestedAmount(Integer totalPersonAppointmentRequestedAmount) {
        this.totalPersonAppointmentRequestedAmount = totalPersonAppointmentRequestedAmount;
    }

    public BigDecimal getTotalPersonAppointmentRequestedFteQuantity() {
        return totalPersonAppointmentRequestedFteQuantity;
    }

    public void setTotalPersonAppointmentRequestedFteQuantity(BigDecimal totalPersonAppointmentRequestedFteQuantity) {
        this.totalPersonAppointmentRequestedFteQuantity = totalPersonAppointmentRequestedFteQuantity;
    }

    public Integer getTotalPersonPositionCsfAmount() {
        return totalPersonPositionCsfAmount;
    }

    public void setTotalPersonPositionCsfAmount(Integer totalPersonPositionCsfAmount) {
        this.totalPersonPositionCsfAmount = totalPersonPositionCsfAmount;
    }

    public BigDecimal getTotalPersonPositionCsfFteQuantity() {
        return totalPersonPositionCsfFteQuantity;
    }

    public void setTotalPersonPositionCsfFteQuantity(BigDecimal totalPersonPositionCsfFteQuantity) {
        this.totalPersonPositionCsfFteQuantity = totalPersonPositionCsfFteQuantity;
    }
    
    
    
}
