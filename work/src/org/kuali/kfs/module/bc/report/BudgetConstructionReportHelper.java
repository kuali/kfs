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
package org.kuali.module.budget.util;

import java.math.BigDecimal;

import org.kuali.core.util.KualiInteger;

/**
 * This class contains methods 
 * 
 */
public class BudgetConstructionReportHelper {

    public static BigDecimal setFiveDecimalDigit(BigDecimal number){
        BigDecimal returnNum = BigDecimal.ZERO;
        if (number != null){
            number.setScale(5, BigDecimal.ROUND_HALF_EVEN);
        }
        
        return returnNum;
    }
    
    public static BigDecimal setTwoDecimalDigit(BigDecimal number){
        BigDecimal returnNum = BigDecimal.ZERO;
        if (number != null){
            number.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        }
        
        return returnNum;
    }
    
    public static BigDecimal calculateChange (BigDecimal numerator, BigDecimal denominator){
        BigDecimal result = BigDecimal.ZERO;
        if (!denominator.equals(BigDecimal.ZERO)){
            result = numerator.divide(denominator).multiply(new BigDecimal(100)).setScale(1, 1);
        }
        return result;
    }
    
    public static Integer convertKualiInteger(KualiInteger num){
        Integer returnNum = null;
        if (num != null) {
            returnNum = new Integer (num.intValue());
        } else {
            returnNum = new Integer (0);
        }
        
        
        
        return returnNum;
    }
    
    
}
