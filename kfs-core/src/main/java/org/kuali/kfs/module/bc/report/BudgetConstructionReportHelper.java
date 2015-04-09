/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    public static BigDecimal calculatePercent(KualiInteger numerator, KualiInteger denominator) {
        BigDecimal result = BigDecimal.ZERO;
        if (numerator != null && denominator != null){
            return calculatePercent(new BigDecimal(numerator.bigIntegerValue()), new BigDecimal(denominator.bigIntegerValue()));
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
