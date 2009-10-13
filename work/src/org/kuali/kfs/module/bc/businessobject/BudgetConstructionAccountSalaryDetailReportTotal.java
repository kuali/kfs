/*
 * Copyright 2006-2008 The Kuali Foundation
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
