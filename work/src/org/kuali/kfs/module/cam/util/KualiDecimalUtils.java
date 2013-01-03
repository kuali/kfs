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

import java.util.Currency;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Utility class that divides currency into equal targets with remainder to cents in some buckets.<br>
 */
public class KualiDecimalUtils {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDecimalUtils.class);
    private static final int[] cents = new int[] { 1, 10, 100, 1000 };

    private long totalAmount;
    private Currency currencyCode;

    /**
     * Default constructor.
     */
    public KualiDecimalUtils() {
    }

    /**
     * Constructs a KualiDecimalService.
     *
     * @param amount
     * @param currency
     */
    public KualiDecimalUtils(KualiDecimal totalAmount, Currency currencyCode) {
        this.currencyCode = currencyCode;
        this.totalAmount = Math.round(totalAmount.multiply(new KualiDecimal(centFactor())).floatValue());
    }

    /**
     * Gets the default number of fraction digits used with the given currency.
     *
     * @return int
     */
    private int centFactor() {
        return cents[currencyCode.getDefaultFractionDigits()];
    }

    /**
     * Sets a new amount.
     *
     * @param amount
     * @return KualiDecimalService
     */
    private KualiDecimalUtils setNewAmount(long amount) {
        KualiDecimalUtils kds = new KualiDecimalUtils();
        kds.totalAmount = amount;
        return kds;
    }

    /**
     * Allocate a sum of money amongst many targets by quantity.
     *
     * @param divisor
     * @return KualiDecimal[]
     */
    public KualiDecimal[] allocateByQuantity(int divisor) {
        // calculate lowest and highest amount
        KualiDecimalUtils lowAmount = setNewAmount(totalAmount / divisor);
        KualiDecimalUtils highAmount = setNewAmount(lowAmount.totalAmount + 1);

        int remainder = (int) Math.abs(totalAmount % divisor);

        // allocate amounts into array
        KualiDecimal[] amountsArray = new KualiDecimal[divisor];

        // set the lowest amount into array
        KualiDecimal low = new KualiDecimal(lowAmount.totalAmount);
        for (int i = remainder; i < divisor; i++) {
            amountsArray[i] = low.divide(new KualiDecimal(centFactor()));
        }

        // set the highest amount into array
        KualiDecimal high = new KualiDecimal(highAmount.totalAmount);
        for (int i = 0; i < remainder; i++) {
            amountsArray[i] = high.divide(new KualiDecimal(centFactor()));
        }

        ArrayUtils.reverse(amountsArray);

        return amountsArray;
    }

    /**
     * Allocate a sum of money amongst many targets by ratio.
     *
     * @param divisor
     * @return KualiDecimal[]
     */
    public static KualiDecimal[] allocateByRatio(KualiDecimal amount, double[] ratios) {

        if (ratios == null || ratios.length == 0 || amount == null) {
            return amount == null ? null : new KualiDecimal[] { amount };
        }
        double value = amount.doubleValue();

        // if just one ratio, apply and return
        if (ratios.length == 1) {
            return new KualiDecimal[] { new KualiDecimal(value * ratios[0]) };
        }
        KualiDecimal allocated = KualiDecimal.ZERO;
        // allocate amounts into array
        KualiDecimal[] amountsArray = new KualiDecimal[ratios.length];
        for (int i = 0; i < ratios.length - 1; i++) {
            KualiDecimal currAmount = new KualiDecimal(value * ratios[i]);
            amountsArray[i] = currAmount;
            allocated = allocated.add(currAmount);
        }
        // adjust the last one with remaining value
        amountsArray[ratios.length - 1] = new KualiDecimal(value).subtract(allocated);
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
    public KualiDecimal safeMultiply(KualiDecimal value, double multiplier) {
        if (value == null) {
            return value;
        }
        else {
            return new KualiDecimal(value.doubleValue() * multiplier);
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
    public KualiDecimal safeSubtract(KualiDecimal value, KualiDecimal subtrahend) {
        if (subtrahend == null || value == null) {
            return value;
        }

        return value.subtract(subtrahend);
    }
}
