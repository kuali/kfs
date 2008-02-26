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
 * Total Part of Budget Construction Organization SubFund Summary Report Business Object.
 */
public class BudgetConstructionOrgSubFundSummaryReportTotal {
    BudgetConstructionAccountSummary bcas;
    
    private BigDecimal subFundTotalRevenueBaseAmount;
    private BigDecimal subFundTotalRevenueReqAmount;
   
    private BigDecimal totalRevenueBaseAmount;
    private BigDecimal totalGrossBaseAmount;
    private BigDecimal totalTransferInBaseAmount;
    private BigDecimal totalNetTransferBaseAmount;

    private BigDecimal totalRevenueReqAmount;
    private BigDecimal totalGrossReqAmount;
    private BigDecimal totalTransferInReqAmount;
    private BigDecimal totalNetTransferReqAmount;

    /**
     * Default constructor.
     */
    public BudgetConstructionOrgSubFundSummaryReportTotal() {
    }

    /**
     * Gets the totalGrossBaseAmount
     * 
     * @return Returns the totalGrossBaseAmount.
     */
    public BigDecimal getTotalGrossBaseAmount() {
        return totalGrossBaseAmount;
    }

    /**
     * Sets the totalGrossBaseAmount
     * 
     * @param totalGrossBaseAmount The totalGrossBaseAmount to set.
     */
    public void setTotalGrossBaseAmount(BigDecimal totalGrossBaseAmount) {
        this.totalGrossBaseAmount = totalGrossBaseAmount;
    }

    /**
     * Gets the totalGrossReqAmount
     * 
     * @return Returns the totalGrossReqAmount.
     */
    public BigDecimal getTotalGrossReqAmount() {
        return totalGrossReqAmount;
    }

    /**
     * Sets the totalGrossReqAmount
     * 
     * @param totalGrossReqAmount The totalGrossReqAmount to set.
     */
    public void setTotalGrossReqAmount(BigDecimal totalGrossReqAmount) {
        this.totalGrossReqAmount = totalGrossReqAmount;
    }

    /**
     * Gets the totalNetTransferBaseAmount
     * 
     * @return Returns the totalNetTransferBaseAmount.
     */
    public BigDecimal getTotalNetTransferBaseAmount() {
        return totalNetTransferBaseAmount;
    }

    /**
     * Sets the totalNetTransferBaseAmount
     * 
     * @param totalNetTransferBaseAmount The totalNetTransferBaseAmount to set.
     */
    public void setTotalNetTransferBaseAmount(BigDecimal totalNetTransferBaseAmount) {
        this.totalNetTransferBaseAmount = totalNetTransferBaseAmount;
    }

    /**
     * Gets the totalNetTransferReqAmount
     * 
     * @return Returns the totalNetTransferReqAmount.
     */
    public BigDecimal getTotalNetTransferReqAmount() {
        return totalNetTransferReqAmount;
    }

    /**
     * Sets the totalNetTransferReqAmount
     * 
     * @param totalNetTransferReqAmount The totalNetTransferReqAmount to set.
     */
    public void setTotalNetTransferReqAmount(BigDecimal totalNetTransferReqAmount) {
        this.totalNetTransferReqAmount = totalNetTransferReqAmount;
    }

    /**
     * Gets the totalRevenueBaseAmount
     * 
     * @return Returns the totalRevenueBaseAmount.
     */
    public BigDecimal getTotalRevenueBaseAmount() {
        return totalRevenueBaseAmount;
    }

    /**
     * Sets the totalRevenueBaseAmount
     * 
     * @param totalRevenueBaseAmount The totalRevenueBaseAmount to set.
     */
    public void setTotalRevenueBaseAmount(BigDecimal totalRevenueBaseAmount) {
        this.totalRevenueBaseAmount = totalRevenueBaseAmount;
    }

    /**
     * Gets the totalRevenueReqAmount
     * 
     * @return Returns the totalRevenueReqAmount.
     */
    public BigDecimal getTotalRevenueReqAmount() {
        return totalRevenueReqAmount;
    }

    /**
     * Sets the totalRevenueReqAmount
     * 
     * @param totalRevenueReqAmount The totalRevenueReqAmount to set.
     */
    public void setTotalRevenueReqAmount(BigDecimal totalRevenueReqAmount) {
        this.totalRevenueReqAmount = totalRevenueReqAmount;
    }

    /**
     * Gets the totalTransferInBaseAmount
     * 
     * @return Returns the totalTransferInBaseAmount.
     */
    public BigDecimal getTotalTransferInBaseAmount() {
        return totalTransferInBaseAmount;
    }

    /**
     * Sets the totalTransferInBaseAmount
     * 
     * @param totalTransferInBaseAmount The totalTransferInBaseAmount to set.
     */
    public void setTotalTransferInBaseAmount(BigDecimal totalTransferInBaseAmount) {
        this.totalTransferInBaseAmount = totalTransferInBaseAmount;
    }

    /**
     * Gets the totalTransferInReqAmount
     * 
     * @return Returns the totalTransferInReqAmount.
     */
    public BigDecimal getTotalTransferInReqAmount() {
        return totalTransferInReqAmount;
    }

    /**
     * Sets the totalTransferInReqAmount
     * 
     * @param totalTransferInReqAmount The totalTransferInReqAmount to set.
     */
    public void setTotalTransferInReqAmount(BigDecimal totalTransferInReqAmount) {
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

    /**
     * Gets the subFundTotalRevenueBaseAmount
     * 
     * @return Returns the bcas.
     */
    public BigDecimal getSubFundTotalRevenueBaseAmount() {
        return subFundTotalRevenueBaseAmount;
    }

    /**
     * Sets the subFundTotalRevenueBaseAmount
     * 
     * @param bcas The subFundTotalRevenueBaseAmount to set.
     */
    public void setSubFundTotalRevenueBaseAmount(BigDecimal subFundTotalRevenueBaseAmount) {
        this.subFundTotalRevenueBaseAmount = subFundTotalRevenueBaseAmount;
    }

    /**
     * Gets the subFundTotalRevenueReqAmount
     * 
     * @return Returns the bcas.
     */
    public BigDecimal getSubFundTotalRevenueReqAmount() {
        return subFundTotalRevenueReqAmount;
    }

    /**
     * Sets the subFundTotalRevenueReqAmount
     * 
     * @param bcas The subFundTotalRevenueReqAmount to set.
     */
    public void setSubFundTotalRevenueReqAmount(BigDecimal subFundTotalRevenueReqAmount) {
        this.subFundTotalRevenueReqAmount = subFundTotalRevenueReqAmount;
    }
}
