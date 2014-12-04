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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;

public class HoldingHistory extends HoldingTaxLot {

    private KualiInteger monthEndDateId;
    private BigDecimal estimatedIncome;
    private BigDecimal securityUnitVal;
    private BigDecimal marketValue;
    private BigDecimal averageMarketValue;
    private BigDecimal remainderOfFYEstimatedIncome;
    private BigDecimal nextFYEstimatedIncome;

    private MonthEndDate monthEndDate;

    /**
     * Gets the estimatedIncome.
     * 
     * @return estimatedIncome
     */
    public BigDecimal getEstimatedIncome() {
        return estimatedIncome;
    }


    /**
     * Sets the estimatedIncome.
     * 
     * @param estimatedIncome
     */
    public void setEstimatedIncome(BigDecimal estimatedIncome) {
        this.estimatedIncome = estimatedIncome;
    }


    /**
     * @see org.kuali.kfs.module.endow.businessobject.HoldingTaxLot#getMarketValue()
     */
    @Override
    public BigDecimal getMarketValue() {
        return marketValue;
    }


    /**
     * Sets the marketValue.
     * 
     * @param marketValue
     */
    public void setMarketValue(BigDecimal marketValue) {
        this.marketValue = marketValue;
    }


    /**
     * Gets the monthEndDateId.
     * 
     * @return monthEndDateId
     */
    public KualiInteger getMonthEndDateId() {
        return monthEndDateId;
    }


    /**
     * Sets the monthEndDateId.
     * 
     * @param monthEndDateId
     */
    public void setMonthEndDateId(KualiInteger monthEndDateId) {
        this.monthEndDateId = monthEndDateId;
    }


    /**
     * Gets the averageMarketValue.
     * 
     * @return averageMarketValue
     */
    public BigDecimal getAverageMarketValue() {
        return averageMarketValue;
    }


    /**
     * Sets the averageMarketValue.
     * 
     * @param averageMarketValue
     */
    public void setAverageMarketValue(BigDecimal quarterAverageMarketValue) {
        this.averageMarketValue = quarterAverageMarketValue;
    }


    /**
     * Gets the securityUnitVal.
     * 
     * @return securityUnitVal
     */
    public BigDecimal getSecurityUnitVal() {
        return securityUnitVal;
    }


    /**
     * Sets the securityUnitVal.
     * 
     * @param securityUnitVal
     */
    public void setSecurityUnitVal(BigDecimal securityUnitVal) {
        this.securityUnitVal = securityUnitVal;
    }


    /**
     * Gets the monthEndDate.
     * 
     * @return monthEndDate
     */
    public MonthEndDate getMonthEndDate() {
        return monthEndDate;
    }


    /**
     * Sets the monthEndDate.
     * 
     * @param monthEndDate
     */
    public void setMonthEndDate(MonthEndDate monthEndDate) {
        this.monthEndDate = monthEndDate;
    }


    /**
     * Gets the nextFYEstimatedIncome.
     * 
     * @return nextFYEstimatedIncome
     */
    public BigDecimal getNextFYEstimatedIncome() {
        return nextFYEstimatedIncome;
    }


    /**
     * Sets the nextFYEstimatedIncome.
     * 
     * @param nextFYEstimatedIncome
     */
    public void setNextFYEstimatedIncome(BigDecimal nextFYEstimatedIncome) {
        this.nextFYEstimatedIncome = nextFYEstimatedIncome;
    }


    /**
     * Gets the remainderOfFYEstimatedIncome.
     * 
     * @return remainderOfFYEstimatedIncome
     */
    public BigDecimal getRemainderOfFYEstimatedIncome() {
        return remainderOfFYEstimatedIncome;
    }


    /**
     * Sets the remainderOfFYEstimatedIncome.
     * 
     * @param remainderOfFYEstimatedIncome
     */
    public void setRemainderOfFYEstimatedIncome(BigDecimal remainderOfFYEstimatedIncome) {
        this.remainderOfFYEstimatedIncome = remainderOfFYEstimatedIncome;
    }

}
