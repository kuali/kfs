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

public class CurrentTaxLotBalance extends HoldingTaxLot {

    private BigDecimal annualEstimatedIncome;
    private BigDecimal remainderOfFYEstimatedIncome;
    private BigDecimal nextFYEstimatedIncome;
    private BigDecimal securityUnitVal;
    private BigDecimal holdingMarketValue;

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
     * Gets the annualEstimatedIncome.
     * 
     * @return annualEstimatedIncome
     */
    public BigDecimal getAnnualEstimatedIncome() {
        return annualEstimatedIncome;
    }

    /**
     * Sets the annualEstimatedIncome.
     * 
     * @param annualEstimatedIncome
     */
    public void setAnnualEstimatedIncome(BigDecimal annualEstimatedIncome) {
        this.annualEstimatedIncome = annualEstimatedIncome;
    }

    /**
     * Gets the holdingMarketValue.
     * 
     * @return holdingMarketValue
     */
    public BigDecimal getHoldingMarketValue() {
        return holdingMarketValue;
    }

    /**
     * Sets the holdingMarketValue.
     * 
     * @param holdingMarketValue
     */
    public void setHoldingMarketValue(BigDecimal holdingMarketValue) {
        this.holdingMarketValue = holdingMarketValue;
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

}
