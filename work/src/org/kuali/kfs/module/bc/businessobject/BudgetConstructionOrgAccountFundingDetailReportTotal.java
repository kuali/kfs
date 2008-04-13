/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.budget.bo;

import java.math.BigDecimal;

/**
 * Total Part of Budget Construction Organization Account Funding Detail Total Business Object.
 */
public class BudgetConstructionOrgAccountFundingDetailReportTotal {
    
    BudgetConstructionObjectDump budgetConstructionObjectDump;
    
    private Integer totalObjectPositionCsfAmount;
    private Integer totalObjectAppointmentRequestedAmount;
    private BigDecimal totalObjectPositionCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedQuantity;
    private Integer totalObjectAmountChange;
    private BigDecimal totalObjectPercentChange;
    
    private Integer totalAccountPositionCsfAmount;
    private Integer totalAccountAppointmentRequestedAmount;
    private BigDecimal totalAccountPositionCsfFteQuantity;
    private BigDecimal totalAccountAppointmentRequestedQuantity;
    private Integer totalAccountAmountChange;
    private BigDecimal totalAccountPercentChange;

    /**
     * Default constructor.
     */
    public BudgetConstructionOrgAccountFundingDetailReportTotal() {
    }

    public BudgetConstructionObjectDump getBudgetConstructionObjectDump() {
        return budgetConstructionObjectDump;
    }

    public void setBudgetConstructionObjectDump(BudgetConstructionObjectDump budgetConstructionObjectDump) {
        this.budgetConstructionObjectDump = budgetConstructionObjectDump;
    }

    public Integer getTotalAccountAmountChange() {
        return totalAccountAmountChange;
    }

    public void setTotalAccountAmountChange(Integer totalAccountAmountChange) {
        this.totalAccountAmountChange = totalAccountAmountChange;
    }

    public Integer getTotalAccountAppointmentRequestedAmount() {
        return totalAccountAppointmentRequestedAmount;
    }

    public void setTotalAccountAppointmentRequestedAmount(Integer totalAccountAppointmentRequestedAmount) {
        this.totalAccountAppointmentRequestedAmount = totalAccountAppointmentRequestedAmount;
    }

    public BigDecimal getTotalAccountAppointmentRequestedQuantity() {
        return totalAccountAppointmentRequestedQuantity;
    }

    public void setTotalAccountAppointmentRequestedQuantity(BigDecimal totalAccountAppointmentRequestedQuantity) {
        this.totalAccountAppointmentRequestedQuantity = totalAccountAppointmentRequestedQuantity;
    }

    public BigDecimal getTotalAccountPercentChange() {
        return totalAccountPercentChange;
    }

    public void setTotalAccountPercentChange(BigDecimal totalAccountPercentChange) {
        this.totalAccountPercentChange = totalAccountPercentChange;
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

    public Integer getTotalObjectAmountChange() {
        return totalObjectAmountChange;
    }

    public void setTotalObjectAmountChange(Integer totalObjectAmountChange) {
        this.totalObjectAmountChange = totalObjectAmountChange;
    }

    public Integer getTotalObjectAppointmentRequestedAmount() {
        return totalObjectAppointmentRequestedAmount;
    }

    public void setTotalObjectAppointmentRequestedAmount(Integer totalObjectAppointmentRequestedAmount) {
        this.totalObjectAppointmentRequestedAmount = totalObjectAppointmentRequestedAmount;
    }

    public BigDecimal getTotalObjectAppointmentRequestedQuantity() {
        return totalObjectAppointmentRequestedQuantity;
    }

    public void setTotalObjectAppointmentRequestedQuantity(BigDecimal totalObjectAppointmentRequestedQuantity) {
        this.totalObjectAppointmentRequestedQuantity = totalObjectAppointmentRequestedQuantity;
    }

    public BigDecimal getTotalObjectPercentChange() {
        return totalObjectPercentChange;
    }

    public void setTotalObjectPercentChange(BigDecimal totalObjectPercentChange) {
        this.totalObjectPercentChange = totalObjectPercentChange;
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


}
