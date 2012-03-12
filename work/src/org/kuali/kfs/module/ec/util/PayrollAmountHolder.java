/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.util;

import static org.kuali.kfs.sys.KFSConstants.CurrencyTypeAmounts.HUNDRED_DOLLAR_AMOUNT;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * To hold the payroll amount and percent
 */
public class PayrollAmountHolder {

    private KualiDecimal payrollAmount;
    private Integer payrollPercent;

    private KualiDecimal totalAmount;

    private KualiDecimal accumulatedAmount;
    private Integer accumulatedPercent;

    /**
     * Constructs a PayrollAmountHolder.java.
     * 
     * @param totalAmount the total payroll amount
     * @param accumulatedAmount the accumulated payroll amount
     * @param accumulatedPercent the accumulated payroll percent
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

    /**
     * calculate the payroll percentage based on the given information in payroll amount holder
     * 
     * @param payrollAmountHolder the given payroll amount holder containing relating information
     */
    public static void calculatePayrollPercent(PayrollAmountHolder payrollAmountHolder) {
        KualiDecimal totalAmount = payrollAmountHolder.getTotalAmount();
        if (totalAmount.isZero()) {
            return;
        }

        KualiDecimal payrollAmount = payrollAmountHolder.getPayrollAmount();
        KualiDecimal accumulatedAmount = payrollAmountHolder.getAccumulatedAmount();
        accumulatedAmount = accumulatedAmount.add(payrollAmount);

        int accumulatedPercent = payrollAmountHolder.getAccumulatedPercent();
        int quotientOne = Math.round(payrollAmount.multiply(HUNDRED_DOLLAR_AMOUNT).divide(totalAmount).floatValue());
        accumulatedPercent = accumulatedPercent + quotientOne;

        int quotientTwo = Math.round(accumulatedAmount.multiply(HUNDRED_DOLLAR_AMOUNT).divide(totalAmount).floatValue());
        quotientTwo = quotientTwo - accumulatedPercent;

        payrollAmountHolder.setAccumulatedAmount(accumulatedAmount);
        payrollAmountHolder.setAccumulatedPercent(accumulatedPercent + quotientTwo);
        payrollAmountHolder.setPayrollPercent(quotientOne + quotientTwo);
    }

    /**
     * recalculate the payroll amount based on the given total amount and effort percent
     * 
     * @param totalPayrollAmount the given total amount
     * @param effortPercent the given effort percent
     * @return the payroll amount calculated from the given total amount and effort percent
     */
    public static KualiDecimal recalculatePayrollAmount(KualiDecimal totalPayrollAmount, Integer effortPercent) {
        double amount = totalPayrollAmount.doubleValue() * effortPercent / HUNDRED_DOLLAR_AMOUNT.doubleValue();

        return new KualiDecimal(amount);
    }

    /**
     * recalculate the effort percent based on the given total amount and payroll amount
     * 
     * @param totalPayrollAmount the given total amount
     * @param payrollAmount the given payroll amount
     * @return the effort percent calculated from the given total amount and payroll amount
     */
    public static Double recalculateEffortPercent(KualiDecimal totalPayrollAmount, KualiDecimal payrollAmount) {
        double percent = payrollAmount.doubleValue() * HUNDRED_DOLLAR_AMOUNT.doubleValue() / totalPayrollAmount.doubleValue();

        return percent;
    }

    /**
     * recalculate the effort percent based on the given total amount and payroll amount and return it as of type String
     * 
     * @param totalPayrollAmount the given total amount
     * @param payrollAmount the given payroll amount
     * @return the effort percent as String calculated from the given total amount and payroll amount
     */
    public static String recalculateEffortPercentAsString(KualiDecimal totalPayrollAmount, KualiDecimal payrollAmount) {
        double actualPercentAsDouble = 0;
        if (totalPayrollAmount.isNonZero()) {
            actualPercentAsDouble = recalculateEffortPercent(totalPayrollAmount, payrollAmount);
        }

        return String.format("%.4f%s", actualPercentAsDouble, KFSConstants.PERCENTAGE_SIGN);
    }
}
