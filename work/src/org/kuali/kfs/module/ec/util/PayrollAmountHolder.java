/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.effort.util;

import org.kuali.core.util.KualiDecimal;

public class PayrollAmountHolder {
    public static final KualiDecimal oneHundred = new KualiDecimal(100);

    private KualiDecimal payrollAmount;
    private Integer payrollPercent;

    private KualiDecimal totalAmount;

    private KualiDecimal accumulatedAmount;
    private Integer accumulatedPercent;
    
    
    /**
     * Constructs a PayrollAmountHolder.java.
     * @param totalAmount
     * @param accumulatedAmount
     * @param accumulatedPercent
     */
    public PayrollAmountHolder(KualiDecimal totalAmount, KualiDecimal accumulatedAmount, Integer accumulatedPercent) {
        super();
        this.totalAmount = totalAmount;
        this.accumulatedAmount = accumulatedAmount;
        this.accumulatedPercent = accumulatedPercent;
    }

    /**
     * Gets the payrollAmount attribute.
     * 
     * @return Returns the payrollAmount.
     */
    public KualiDecimal getPayrollAmount() {
        return payrollAmount;
    }

    /**
     * Sets the payrollAmount attribute value.
     * 
     * @param payrollAmount The payrollAmount to set.
     */
    public void setPayrollAmount(KualiDecimal payrollAmount) {
        this.payrollAmount = payrollAmount;
    }

    /**
     * Gets the payrollPercent attribute.
     * 
     * @return Returns the payrollPercent.
     */
    public Integer getPayrollPercent() {
        return payrollPercent;
    }

    /**
     * Sets the payrollPercent attribute value.
     * 
     * @param payrollPercent The payrollPercent to set.
     */
    public void setPayrollPercent(Integer payrollPercent) {
        this.payrollPercent = payrollPercent;
    }

    /**
     * Gets the totalAmount attribute.
     * 
     * @return Returns the totalAmount.
     */
    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the totalAmount attribute value.
     * 
     * @param totalAmount The totalAmount to set.
     */
    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets the accumulatedAmount attribute.
     * 
     * @return Returns the accumulatedAmount.
     */
    public KualiDecimal getAccumulatedAmount() {
        return accumulatedAmount;
    }

    /**
     * Sets the accumulatedAmount attribute value.
     * 
     * @param accumulatedAmount The accumulatedAmount to set.
     */
    public void setAccumulatedAmount(KualiDecimal accumulatedAmount) {
        this.accumulatedAmount = accumulatedAmount;
    }

    /**
     * Gets the accumulatedPercent attribute.
     * 
     * @return Returns the accumulatedPercent.
     */
    public Integer getAccumulatedPercent() {
        return accumulatedPercent;
    }

    /**
     * Sets the accumulatedPercent attribute value.
     * 
     * @param accumulatedPercent The accumulatedPercent to set.
     */
    public void setAccumulatedPercent(Integer accumulatedPercent) {
        this.accumulatedPercent = accumulatedPercent;
    }


}
