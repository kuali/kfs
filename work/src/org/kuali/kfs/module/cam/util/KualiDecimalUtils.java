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
package org.kuali.kfs.module.cam.util;

import java.math.BigDecimal;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Utility class that provides methods to allocate money into a number of targets according to certain policies.
 * Note: It's assumed that the currency we use here is US dollar.
 */
public class KualiDecimalUtils {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDecimalUtils.class);

    /**
     * Allocates evenly a sum of money amongst a number of targets.
     * Note:
     * 1. This method assumes that the divisor is a positive integer. Validation of the the passed in parameters shall be the caller's responsibility.
     * 2. If the sum can't be evenly divided due to limited accuracy, the last few elements will be adjusted (each by 1c) to reflect the round-up difference.
     *
     * @param amount the total amount to allocate
     * @param divisor the number of targets to allocate to
     * @return an array of allocated amounts
     */
    public static KualiDecimal[] allocateByQuantity(KualiDecimal amount, int divisor) {

        if (amount == null || divisor == 0) {
            return amount == null ? null : new KualiDecimal[] { amount };
        }

        // calculate evenly divided amount
        KualiDecimal dividedAmount = amount.divide(new KualiDecimal(divisor));

        // set the allocated amount into array
        KualiDecimal[] amountsArray = new KualiDecimal[divisor];
        for (int i = 0; i < divisor; i++) {
            amountsArray[i] = dividedAmount;
        }

        // compute the difference between the original total amount and the allocated total amount, due to round up error in division
        KualiDecimal allocatedAmount = dividedAmount.multiply(new KualiDecimal(divisor));
        KualiDecimal reminderAmount = amount.subtract(allocatedAmount);

        /* Note:
         * We choose to distribute 1c to each of the last N elements, instead of putting the total difference into the last one element, because:
         * 1. This way the allocation is more even; otherwise the last element may end up taking too much or too little (even negative).
         * For ex, suppose amount = $99, and divisor = 10000. So the first 9999 elements would each get 1c, while the last one would get -99c,
         * if we dump all of the roundup error into the last element.
         * 2. The round up error for each element is less than 1c (in fact, it's less than 0.5c, as KualiDecimal uses ROUND_HALF_UP),
         * thus the total error is less than 1c * divisor, which means we can safely distribute 1c to the last N elements, where N < divisor.
         */

        // since KualiDecimal has 2 digits after decimal point, multiply it by 100 guarantees that we get an integer number of cents
        int n = reminderAmount.abs().multiply(new KualiDecimal(100)).intValue();
        KualiDecimal cent = reminderAmount.isPositive() ? new KualiDecimal(0.01) : new KualiDecimal(-0.01);

        // compensate the difference by offsetting the last N elements, each by 1c; here N = reminderAmount * 100
        for (int i = divisor - 1; i >= divisor - n ; i--) {
            amountsArray[i] = dividedAmount.add(cent);
        }

        return amountsArray;
    }

    /**
     * Allocates by ratio a sum of money amongst a number of targets.
     * Note:
     * 1. This method assumes that 0 < ratio < 1 and all ratios sum up to 1. Validation of the the passed in parameters shall be the caller's responsibility.
     * 2. If the allocated amount doesn't sum up to exactly the total amount due to limited accuracy, the last few elements will be adjusted (each by 1c) to reflect the round-up difference.
     *
     * @param amount the total amount to allocate
     * @param ratios an array of ratios according to which the total amount is to be allocated
     * @return an array of allocated amounts
     */
    public static KualiDecimal[] allocateByRatio(KualiDecimal amount, double[] ratios) {

        if (ratios == null || ratios.length == 0 || amount == null) {
            return amount == null ? null : new KualiDecimal[] { amount };
        }

        // allocate amounts into array according to the ratios
        KualiDecimal[] amountsArray = new KualiDecimal[ratios.length];
        KualiDecimal allocatedAmount = KualiDecimal.ZERO;
        for (int i = 0; i < ratios.length; i++) {
            KualiDecimal currAmount = new KualiDecimal(amount.bigDecimalValue().multiply(new BigDecimal(ratios[i])));
            amountsArray[i] = currAmount;
            allocatedAmount = allocatedAmount.add(currAmount);
        }

        // compute the difference between the original total amount and the allocated total amount, due to round up error in division
        KualiDecimal reminderAmount = amount.subtract(allocatedAmount);

        /* Note:
         * We choose to distribute 1c to each of the last N elements, instead of putting the total difference into the last one element, because:
         * 1. This way the allocation is more even; otherwise the last element may end up taking too much or too little (even negative).
         * For ex, suppose amount = $99, and ratio[0-9999] = 0.0001. So the first 9999 elements would each get 1c, while the last one would get -99c,
         * if we dump all of the roundup error into the last element.
         * 2. The round up error for each element is less than 1c (in fact, it's less than 0.5c, as KualiDecimal uses ROUND_HALF_UP),
         * thus the total error is less than 1c * ratio.length, which means we can safely distribute 1c to the last N elements, where N < ratio.length.
         */

        // since KualiDecimal has 2 digits after decimal point, multiply it by 100 guarantees that we get an integer number of cents
        int n = reminderAmount.abs().multiply(new KualiDecimal(100)).intValue();
        KualiDecimal cent = reminderAmount.isPositive() ? new KualiDecimal(0.01) : new KualiDecimal(-0.01);

        // If the ratios sum up to 1, then we should have N < ratios.length;
        // However, if that's not the case, it's possible that N > ratio.length; in this case,
        // the best we can do is to chop N to length to avoid ArrayIndexOutOfBoundary exception
        n = n > ratios.length ? ratios.length : n;

        // compensate the difference by offsetting the last N elements, each by 1c; here N = reminderAmount * 100
        for (int i = ratios.length - 1; i >= ratios.length - n ; i--) {
            amountsArray[i] = amountsArray[i].add(cent);
        }

        return amountsArray;
    }

    /**
     * Makes sure no null pointer exception occurs on fields that can accurately be null when multiplying. If either field are null
     * the value is returned.
     *
     * @param value
     * @param multiplier
     * @return
     */
    public static KualiDecimal safeMultiply(KualiDecimal value, double multiplier) {
        if (value == null) {
            return value;
        }
        else {
            return new KualiDecimal(value.bigDecimalValue().multiply(new BigDecimal(multiplier)));
        }
    }

    /**
     * Makes sure no null pointer exception occurs on fields that can accurately be null when subtracting. If either field are null
     * the value is returned.
     *
     * @param value
     * @param subtrahend
     * @return
     */
    public static KualiDecimal safeSubtract(KualiDecimal value, KualiDecimal subtrahend) {
        if (subtrahend == null || value == null) {
            return value;
        }

        return value.subtract(subtrahend);
    }
}
