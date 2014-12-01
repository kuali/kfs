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
public class BudgetConstructionOrgAccountFundingDetailReportTotal {
    private BudgetConstructionObjectDump budgetConstructionObjectDump;
    
    private Integer totalObjectPositionCsfAmount;
    private Integer totalObjectAppointmentRequestedAmount;
    private BigDecimal totalObjectPositionCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedFteQuantity;
    
    private Integer totalAccountPositionCsfAmount;
    private Integer totalAccountAppointmentRequestedAmount;
    private BigDecimal totalAccountPositionCsfFteQuantity;
    private BigDecimal totalAccountAppointmentRequestedFteQuantity;
    
    /**
     * Default constructor.
     */
    public BudgetConstructionOrgAccountFundingDetailReportTotal() {
    }

    
    public Integer getTotalAccountAppointmentRequestedAmount() {
        return totalAccountAppointmentRequestedAmount;
    }

    public void setTotalAccountAppointmentRequestedAmount(Integer totalAccountAppointmentRequestedAmount) {
        this.totalAccountAppointmentRequestedAmount = totalAccountAppointmentRequestedAmount;
    }

    public BigDecimal getTotalAccountAppointmentRequestedFteQuantity() {
        return totalAccountAppointmentRequestedFteQuantity;
    }

    public void setTotalAccountAppointmentRequestedFteQuantity(BigDecimal totalAccountAppointmentRequestedFteQuantity) {
        this.totalAccountAppointmentRequestedFteQuantity = totalAccountAppointmentRequestedFteQuantity;
    }

    public Integer getTotalAccountPositionCsfAmount() {
        return totalAccountPositionCsfAmount;
    }

    public void setTotalAccountPositionCsfAmount(Integer totalAccountPositionCsfAmount) {
        this.totalAccountPositionCsfAmount = totalAccountPositionCsfAmount;
    }

    public BigDecimal getTotalAccountPositionCsfFteQuantity() {
        return totalAccountPositionCsfFteQuantity;
    }

    public void setTotalAccountPositionCsfFteQuantity(BigDecimal totalAccountPositionCsfFteQuantity) {
        this.totalAccountPositionCsfFteQuantity = totalAccountPositionCsfFteQuantity;
    }

    public Integer getTotalObjectAppointmentRequestedAmount() {
        return totalObjectAppointmentRequestedAmount;
    }

    public void setTotalObjectAppointmentRequestedAmount(Integer totalObjectAppointmentRequestedAmount) {
        this.totalObjectAppointmentRequestedAmount = totalObjectAppointmentRequestedAmount;
    }

    public BigDecimal getTotalObjectAppointmentRequestedFteQuantity() {
        return totalObjectAppointmentRequestedFteQuantity;
    }

    public void setTotalObjectAppointmentRequestedFteQuantity(BigDecimal totalObjectAppointmentRequestedFteQuantity) {
        this.totalObjectAppointmentRequestedFteQuantity = totalObjectAppointmentRequestedFteQuantity;
    }

    public Integer getTotalObjectPositionCsfAmount() {
        return totalObjectPositionCsfAmount;
    }

    public void setTotalObjectPositionCsfAmount(Integer totalObjectPositionCsfAmount) {
        this.totalObjectPositionCsfAmount = totalObjectPositionCsfAmount;
    }

    public BigDecimal getTotalObjectPositionCsfFteQuantity() {
        return totalObjectPositionCsfFteQuantity;
    }

    public void setTotalObjectPositionCsfFteQuantity(BigDecimal totalObjectPositionCsfFteQuantity) {
        this.totalObjectPositionCsfFteQuantity = totalObjectPositionCsfFteQuantity;
    }


    public BudgetConstructionObjectDump getBudgetConstructionObjectDump() {
        return budgetConstructionObjectDump;
    }


    public void setBudgetConstructionObjectDump(BudgetConstructionObjectDump budgetConstructionObjectDump) {
        this.budgetConstructionObjectDump = budgetConstructionObjectDump;
    }


}
