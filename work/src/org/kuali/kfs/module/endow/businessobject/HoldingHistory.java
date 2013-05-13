/*
 * Copyright 2009 The Kuali Foundation.
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
