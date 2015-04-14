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
 * 
 */
public class BudgetConstructionAccountSalaryDetailReportTotal{

    // total
    private String totalDescription;
    private Integer totalBaseAmount;
    private BigDecimal totalBaseFte;
    private Integer totalRequestAmount;
    private BigDecimal totalRequestFte;
    private Integer totalAmountChange;
    private BigDecimal totalPercentChange;
    
    private PendingBudgetConstructionAppointmentFunding pendingBudgetConstructionAppointmentFunding;
    
    
    
    /**
     * Default constructor.
     */
    public BudgetConstructionAccountSalaryDetailReportTotal() {

    }

    public PendingBudgetConstructionAppointmentFunding getPendingBudgetConstructionAppointmentFunding() {
        return pendingBudgetConstructionAppointmentFunding;
    }

    public void setPendingBudgetConstructionAppointmentFunding(PendingBudgetConstructionAppointmentFunding pendingBudgetConstructionAppointmentFunding) {
        this.pendingBudgetConstructionAppointmentFunding = pendingBudgetConstructionAppointmentFunding;
    }

    public String getTotalDescription() {
        return totalDescription;
    }




    public void setTotalDescription(String totalDescription) {
        this.totalDescription = totalDescription;
    }




    public Integer getTotalBaseAmount() {
        return totalBaseAmount;
    }




    public void setTotalBaseAmount(Integer totalBaseAmount) {
        this.totalBaseAmount = totalBaseAmount;
    }




    public BigDecimal getTotalBaseFte() {
        return totalBaseFte;
    }




    public void setTotalBaseFte(BigDecimal totalBaseFte) {
        this.totalBaseFte = totalBaseFte;
    }




    public Integer getTotalRequestAmount() {
        return totalRequestAmount;
    }




    public void setTotalRequestAmount(Integer totalRequestAmount) {
        this.totalRequestAmount = totalRequestAmount;
    }




    public BigDecimal getTotalRequestFte() {
        return totalRequestFte;
    }




    public void setTotalRequestFte(BigDecimal totalRequestFte) {
        this.totalRequestFte = totalRequestFte;
    }




    public Integer getTotalAmountChange() {
        return totalAmountChange;
    }




    public void setTotalAmountChange(Integer totalAmountChange) {
        this.totalAmountChange = totalAmountChange;
    }




    public BigDecimal getTotalPercentChange() {
        return totalPercentChange;
    }




    public void setTotalPercentChange(BigDecimal totalPercentChange) {
        this.totalPercentChange = totalPercentChange;
    }
}
