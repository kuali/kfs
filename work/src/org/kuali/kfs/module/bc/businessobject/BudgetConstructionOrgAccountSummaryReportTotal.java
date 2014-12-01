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

import org.kuali.rice.core.api.util.type.KualiInteger;


/**
 * Total Part of Budget Construction Organization Account Summary Report Business Object.
 */
public class BudgetConstructionOrgAccountSummaryReportTotal {
    BudgetConstructionAccountSummary bcas;
    private KualiInteger totalRevenueBaseAmount;
    private KualiInteger totalGrossBaseAmount;
    private KualiInteger totalTransferInBaseAmount;
    private KualiInteger totalNetTransferBaseAmount;

    private KualiInteger totalRevenueReqAmount;
    private KualiInteger totalGrossReqAmount;
    private KualiInteger totalTransferInReqAmount;
    private KualiInteger totalNetTransferReqAmount;

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
    public KualiInteger getTotalGrossBaseAmount() {
        return totalGrossBaseAmount;
    }

    /**
     * Sets the totalGrossBaseAmount
     *
     * @param totalGrossBaseAmount The totalGrossBaseAmount to set.
     */
    public void setTotalGrossBaseAmount(KualiInteger totalGrossBaseAmount) {
        this.totalGrossBaseAmount = totalGrossBaseAmount;
    }

    /**
     * Gets the totalGrossReqAmount
     *
     * @return Returns the totalGrossReqAmount.
     */
    public KualiInteger getTotalGrossReqAmount() {
        return totalGrossReqAmount;
    }

    /**
     * Sets the totalGrossReqAmount
     *
     * @param totalGrossReqAmount The totalGrossReqAmount to set.
     */
    public void setTotalGrossReqAmount(KualiInteger totalGrossReqAmount) {
        this.totalGrossReqAmount = totalGrossReqAmount;
    }

    /**
     * Gets the totalNetTransferBaseAmount
     *
     * @return Returns the totalNetTransferBaseAmount.
     */
    public KualiInteger getTotalNetTransferBaseAmount() {
        return totalNetTransferBaseAmount;
    }

    /**
     * Sets the totalNetTransferBaseAmount
     *
     * @param totalNetTransferBaseAmount The totalNetTransferBaseAmount to set.
     */
    public void setTotalNetTransferBaseAmount(KualiInteger totalNetTransferBaseAmount) {
        this.totalNetTransferBaseAmount = totalNetTransferBaseAmount;
    }

    /**
     * Gets the totalNetTransferReqAmount
     *
     * @return Returns the totalNetTransferReqAmount.
     */
    public KualiInteger getTotalNetTransferReqAmount() {
        return totalNetTransferReqAmount;
    }

    /**
     * Sets the totalNetTransferReqAmount
     *
     * @param totalNetTransferReqAmount The totalNetTransferReqAmount to set.
     */
    public void setTotalNetTransferReqAmount(KualiInteger totalNetTransferReqAmount) {
        this.totalNetTransferReqAmount = totalNetTransferReqAmount;
    }

    /**
     * Gets the totalRevenueBaseAmount
     *
     * @return Returns the totalRevenueBaseAmount.
     */
    public KualiInteger getTotalRevenueBaseAmount() {
        return totalRevenueBaseAmount;
    }

    /**
     * Sets the totalRevenueBaseAmount
     *
     * @param totalRevenueBaseAmount The totalRevenueBaseAmount to set.
     */
    public void setTotalRevenueBaseAmount(KualiInteger totalRevenueBaseAmount) {
        this.totalRevenueBaseAmount = totalRevenueBaseAmount;
    }

    /**
     * Gets the totalRevenueReqAmount
     *
     * @return Returns the totalRevenueReqAmount.
     */
    public KualiInteger getTotalRevenueReqAmount() {
        return totalRevenueReqAmount;
    }

    /**
     * Sets the totalRevenueReqAmount
     *
     * @param totalRevenueReqAmount The totalRevenueReqAmount to set.
     */
    public void setTotalRevenueReqAmount(KualiInteger totalRevenueReqAmount) {
        this.totalRevenueReqAmount = totalRevenueReqAmount;
    }

    /**
     * Gets the totalTransferInBaseAmount
     *
     * @return Returns the totalTransferInBaseAmount.
     */
    public KualiInteger getTotalTransferInBaseAmount() {
        return totalTransferInBaseAmount;
    }

    /**
     * Sets the totalTransferInBaseAmount
     *
     * @param totalTransferInBaseAmount The totalTransferInBaseAmount to set.
     */
    public void setTotalTransferInBaseAmount(KualiInteger totalTransferInBaseAmount) {
        this.totalTransferInBaseAmount = totalTransferInBaseAmount;
    }

    /**
     * Gets the totalTransferInReqAmount
     *
     * @return Returns the totalTransferInReqAmount.
     */
    public KualiInteger getTotalTransferInReqAmount() {
        return totalTransferInReqAmount;
    }

    /**
     * Sets the totalTransferInReqAmount
     *
     * @param totalTransferInReqAmount The totalTransferInReqAmount to set.
     */
    public void setTotalTransferInReqAmount(KualiInteger totalTransferInReqAmount) {
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
