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


/**
 * Total Part of Budget Construction Organization Account Summary Report Business Object.
 */
public class BudgetConstructionOrgAccountSummaryReportTotal {
    BudgetConstructionAccountSummary bcas;
    private Integer totalRevenueBaseAmount;
    private Integer totalGrossBaseAmount;
    private Integer totalTransferInBaseAmount;
    private Integer totalNetTransferBaseAmount;

    private Integer totalRevenueReqAmount;
    private Integer totalGrossReqAmount;
    private Integer totalTransferInReqAmount;
    private Integer totalNetTransferReqAmount;

    /**
     * Default constructor.
     */
    public BudgetConstructionOrgAccountSummaryReportTotal() {
    }

    /**
     * Gets the totalGrossBaseAmount
     * 
     * @return Returns the totalGrossBaseAmount.
     */
    public Integer getTotalGrossBaseAmount() {
        return totalGrossBaseAmount;
    }

    /**
     * Sets the totalGrossBaseAmount
     * 
     * @param totalGrossBaseAmount The totalGrossBaseAmount to set.
     */
    public void setTotalGrossBaseAmount(Integer totalGrossBaseAmount) {
        this.totalGrossBaseAmount = totalGrossBaseAmount;
    }

    /**
     * Gets the totalGrossReqAmount
     * 
     * @return Returns the totalGrossReqAmount.
     */
    public Integer getTotalGrossReqAmount() {
        return totalGrossReqAmount;
    }

    /**
     * Sets the totalGrossReqAmount
     * 
     * @param totalGrossReqAmount The totalGrossReqAmount to set.
     */
    public void setTotalGrossReqAmount(Integer totalGrossReqAmount) {
        this.totalGrossReqAmount = totalGrossReqAmount;
    }

    /**
     * Gets the totalNetTransferBaseAmount
     * 
     * @return Returns the totalNetTransferBaseAmount.
     */
    public Integer getTotalNetTransferBaseAmount() {
        return totalNetTransferBaseAmount;
    }

    /**
     * Sets the totalNetTransferBaseAmount
     * 
     * @param totalNetTransferBaseAmount The totalNetTransferBaseAmount to set.
     */
    public void setTotalNetTransferBaseAmount(Integer totalNetTransferBaseAmount) {
        this.totalNetTransferBaseAmount = totalNetTransferBaseAmount;
    }

    /**
     * Gets the totalNetTransferReqAmount
     * 
     * @return Returns the totalNetTransferReqAmount.
     */
    public Integer getTotalNetTransferReqAmount() {
        return totalNetTransferReqAmount;
    }

    /**
     * Sets the totalNetTransferReqAmount
     * 
     * @param totalNetTransferReqAmount The totalNetTransferReqAmount to set.
     */
    public void setTotalNetTransferReqAmount(Integer totalNetTransferReqAmount) {
        this.totalNetTransferReqAmount = totalNetTransferReqAmount;
    }

    /**
     * Gets the totalRevenueBaseAmount
     * 
     * @return Returns the totalRevenueBaseAmount.
     */
    public Integer getTotalRevenueBaseAmount() {
        return totalRevenueBaseAmount;
    }

    /**
     * Sets the totalRevenueBaseAmount
     * 
     * @param totalRevenueBaseAmount The totalRevenueBaseAmount to set.
     */
    public void setTotalRevenueBaseAmount(Integer totalRevenueBaseAmount) {
        this.totalRevenueBaseAmount = totalRevenueBaseAmount;
    }

    /**
     * Gets the totalRevenueReqAmount
     * 
     * @return Returns the totalRevenueReqAmount.
     */
    public Integer getTotalRevenueReqAmount() {
        return totalRevenueReqAmount;
    }

    /**
     * Sets the totalRevenueReqAmount
     * 
     * @param totalRevenueReqAmount The totalRevenueReqAmount to set.
     */
    public void setTotalRevenueReqAmount(Integer totalRevenueReqAmount) {
        this.totalRevenueReqAmount = totalRevenueReqAmount;
    }

    /**
     * Gets the totalTransferInBaseAmount
     * 
     * @return Returns the totalTransferInBaseAmount.
     */
    public Integer getTotalTransferInBaseAmount() {
        return totalTransferInBaseAmount;
    }

    /**
     * Sets the totalTransferInBaseAmount
     * 
     * @param totalTransferInBaseAmount The totalTransferInBaseAmount to set.
     */
    public void setTotalTransferInBaseAmount(Integer totalTransferInBaseAmount) {
        this.totalTransferInBaseAmount = totalTransferInBaseAmount;
    }

    /**
     * Gets the totalTransferInReqAmount
     * 
     * @return Returns the totalTransferInReqAmount.
     */
    public Integer getTotalTransferInReqAmount() {
        return totalTransferInReqAmount;
    }

    /**
     * Sets the totalTransferInReqAmount
     * 
     * @param totalTransferInReqAmount The totalTransferInReqAmount to set.
     */
    public void setTotalTransferInReqAmount(Integer totalTransferInReqAmount) {
        this.totalTransferInReqAmount = totalTransferInReqAmount;
    }

    /**
     * Gets the budgetConstructionAccountSummary
     * 
     * @return Returns the bcas.
     */
    public BudgetConstructionAccountSummary getBcas() {
        return bcas;
    }

    /**
     * Sets the budgetConstructionAccountSummary
     * 
     * @param bcas The budgetConstructionAccountSummary to set.
     */
    public void setBcas(BudgetConstructionAccountSummary bcas) {
        this.bcas = bcas;
    }
}
