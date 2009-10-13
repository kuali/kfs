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
