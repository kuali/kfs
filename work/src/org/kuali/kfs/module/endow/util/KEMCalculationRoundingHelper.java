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
package org.kuali.kfs.module.endow.util;

import java.math.BigDecimal;

/**
 * This class will ensure Calculation Rounding Rules in KEM: - No calculated value will ever be truncated to the decimals set for
 * the field. - Every calculation made by the system must always round to the proper decimal for the field. - Each field has a
 * decimal value and the calculation results carry past the expected decimals and if the next decimal value is greater than or equal
 * to 5, the last decimal value will be incremented by 1. - If the calculation results carry past the expected decimals and if the
 * next decimal value is less than 5, the last decimal value will not be incremented.
 */
public class KEMCalculationRoundingHelper {

    /**
     * Divides two decimals and applies the given scale and a ROUND_HALF_UP. This method should be used only for the final result
     * calculation. For example if we have something like this: (axb)/c the rules should be applied to the result of the division
     * only and not all the computations that give us the final result.
     * 
     * @param dividend the dividend
     * @param divisor the divisor
     * @param scale the scale for the result
     * @return the result of the division after the scale and rounding are applied
     */
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {

        BigDecimal result = BigDecimal.ZERO;

        if (dividend != null && divisor != null && !divisor.equals(BigDecimal.ZERO)) {
            result = dividend.divide(divisor, scale, BigDecimal.ROUND_HALF_UP);
        }

        return result;
    }

    /**
     * Multiplies two decimals and applies the given scale and a ROUND_HALF_UP. This method should be used only for the final result
     * calculation.For example if we have something like this: (axb)/c the rules should be applied to the result of the division
     * only and not all the computations that give us the final result.
     * 
     * @param multiplier1 first multiplier
     * @param multiplier2 second multiplier
     * @param scale the scale fo the result
     * @return the result of the multiplication after scale and rounding are applied
     */
    public static BigDecimal multiply(BigDecimal multiplier1, BigDecimal multiplier2, int scale) {

        BigDecimal result = BigDecimal.ZERO;
        
        if (multiplier1 != null && multiplier2 != null) {
            result = multiplier1.multiply(multiplier2);
        }
        
        result = result.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return result;
    }

}
