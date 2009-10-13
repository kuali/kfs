/*
 * Copyright 2008 The Kuali Foundation
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
