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
package org.kuali.kfs.module.cam.util;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Utility class that divides currency into equal targets.
 */
public class KualiDecimalService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDecimalService.class);
    private static final int[] cents = new int[] {1, 10, 100, 1000};

    private long totalAmount;
    private Currency currencyCode;

    /**
     * Default constructor.
     */
    public KualiDecimalService() {}

    /**
     * Constructs a KualiDecimalService.
     * 
     * @param amount
     * @param currency
     */
    public KualiDecimalService(KualiDecimal totalAmount, Currency currencyCode) {
        this.currencyCode = currencyCode;
        //LOG.info("this.currency: '" + this.currencyCode + "'");
        this.totalAmount = Math.round(totalAmount.multiply(new KualiDecimal(centFactor())).floatValue());
        //LOG.info("this.amount: '" + this.totalAmount + "'");
    }

    /**
     * Gets the default number of fraction digits used with the given currency.
     * 
     * @return int
     */
    private int centFactor() {
        //LOG.info("cents: '" + cents[currencyCode.getDefaultFractionDigits()] + "'");
        return cents[currencyCode.getDefaultFractionDigits()];
    }

    /**
     * Sets a new amount.
     * 
     * @param amount
     * @return KualiDecimalService
     */
    private KualiDecimalService setNewAmount(long amount) {
        KualiDecimalService kds = new KualiDecimalService();
        kds.totalAmount = amount;
        return kds;
    }

    /**
     * Allocate a sum of money amongst many targets.
     * 
     * @param divisor
     * @return KualiDecimal[]
     */
    public KualiDecimal[] allocate(int divisor) {
        // calculate lowest and highest amount
        KualiDecimalService lowAmount = setNewAmount(totalAmount / divisor);
        KualiDecimalService highAmount = setNewAmount(lowAmount.totalAmount + 1);
        //LOG.info("lowAmount: '" + lowAmount.totalAmount + "'");
        //LOG.info("highAmount: '" + highAmount.totalAmount + "'");

        int remainder = (int) totalAmount % divisor;
        //LOG.info("remainder: '" + remainder + "'");

        // allocate amounts into array
        KualiDecimal[] amountsArray = new KualiDecimal[divisor];;


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

        return amountsArray;
    }

}
