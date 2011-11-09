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
package org.kuali.kfs.module.bc.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class contains methods
 */
public class BudgetConstructionReportHelper {

    public static BigDecimal setDecimalDigit(BigDecimal number, int digit, boolean setNullIndicator) {
        BigDecimal returnNum = BigDecimal.ZERO;
        if (number != null) {
            if ((number.compareTo(BigDecimal.ZERO) == 0) && setNullIndicator){
                return null;
            }
            returnNum = number.setScale(digit, BigDecimal.ROUND_HALF_UP);
        }
        return returnNum;
    }

    public static BigDecimal calculatePercent(BigDecimal numerator, BigDecimal denominator) {
        BigDecimal result = BigDecimal.ZERO;
        if (numerator != null && denominator != null && (denominator.compareTo(BigDecimal.ZERO) != 0) ) {
            result = numerator.divide(denominator, 3, BigDecimal.ROUND_HALF_UP ).movePointRight(2);
        }
        return result;
    }
    
    public static BigDecimal calculatePercent(Integer numerator, Integer denominator) {
        BigDecimal result = BigDecimal.ZERO;
        if (numerator != null && denominator != null){
            return calculatePercent(new BigDecimal(numerator.intValue()), new BigDecimal(denominator.intValue()));
        } 
        return result;
    }
    
    public static BigDecimal calculateDivide(BigDecimal numerator, BigDecimal denominator) {
        BigDecimal result = BigDecimal.ZERO;
        if (denominator.compareTo(BigDecimal.ZERO) != 0) {
            result = numerator.divide(denominator, 3, BigDecimal.ROUND_HALF_UP);
        }
        return result;
    }

    public static Integer convertKualiInteger(KualiInteger num) {
        Integer returnNum = null;
        if (num != null) {
            returnNum = new Integer(num.intValue());
        }
        else {
            returnNum = new Integer(0);
        }
        return returnNum;
    }

    public static List deleteDuplicated(List<BusinessObject> list, List<String> fields) {
        List returnList = new ArrayList();
        List<String> foundObjects = new ArrayList<String>();

        for (BusinessObject businessObject : list) {
            String valueString = "";
            for (String fieldName : fields) {
                Object fieldValue = ObjectUtils.getPropertyValue(businessObject, fieldName);
                valueString += fieldValue.toString();
            }
            if (!foundObjects.contains(valueString)) {
                returnList.add(businessObject);
                foundObjects.add(valueString);
            }
        }
        return returnList;
    }

    public static boolean isSameEntry(BusinessObject firstObject, BusinessObject secondObject, List<String> fields) {
        String firstValueString = "";
        String secondValueString = "";
        for (String fieldName : fields) {
            Object firstFieldValue = ObjectUtils.getPropertyValue(firstObject, fieldName);
            Object secondFieldValue = ObjectUtils.getPropertyValue(secondObject, fieldName);
            firstValueString += firstFieldValue.toString();
            secondValueString += secondFieldValue.toString();
        }
        if (firstValueString.equals(secondValueString)) {
            return true;
        }
        else {
            return false;
        }

    }
}
