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
package org.kuali.kfs.module.ec.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;

/**
 * To enumerate the accounting periods as months and provides a set of utilities to manage the period code.
 */
public enum AccountingPeriodMonth {
    MONTH1(KFSConstants.MONTH1), MONTH2(KFSConstants.MONTH2), MONTH3(KFSConstants.MONTH3), MONTH4(KFSConstants.MONTH4), MONTH5(KFSConstants.MONTH5), MONTH6(KFSConstants.MONTH6), MONTH7(KFSConstants.MONTH7), MONTH8(KFSConstants.MONTH8), MONTH9(KFSConstants.MONTH9), MONTH10(KFSConstants.MONTH10), MONTH11(KFSConstants.MONTH11), MONTH12(KFSConstants.MONTH12);

    public final String periodCode;

    /**
     * Constructs a AccountingPeriodMonth.java.
     * @param periodCode a period code
     */
    private AccountingPeriodMonth(String periodCode) {
        this.periodCode = periodCode;
    }

    /**
     * find an accounting period with the given period code
     * 
     * @param periodCode the given period code
     * @return an accounting period with the given period code
     */
    public static AccountingPeriodMonth findAccountingPeriod(String periodCode) {
        for (AccountingPeriodMonth accountingPeriod : AccountingPeriodMonth.values()) {
            if (accountingPeriod.periodCode.equals(periodCode)) {
                return accountingPeriod;
            }
        }
        return null;
    }

    /**
     * find all accounting periods between the given begin period and end period. Here, a period can be represented by the
     * combination of year and period code. The begin perid should be no later than the end period.
     * 
     * @param beginYear the begin year
     * @param beginPeriodCode the begin period code
     * @param endYear the end year
     * @param endPeriodCode the end period code
     * @return all accounting periods between the given begin period and end period. The returning results are stored in a map,
     *         whose keys is year and whose values are period code set.
     */
    public static Map<Integer, Set<String>> findAccountingPeriodsBetween(Integer beginYear, String beginPeriodCode, Integer endYear, String endPeriodCode) {
        Map<Integer, Set<String>> accountingPeriods = new HashMap<Integer, Set<String>>();

        AccountingPeriodMonth beginPeriod = findAccountingPeriod(beginPeriodCode);
        AccountingPeriodMonth endPeriod = findAccountingPeriod(endPeriodCode);

        int difference = endYear - beginYear;
        if (difference > 0) {
            accountingPeriods.put(beginYear, buildPeriodCodeSetWithinRange(beginPeriod, AccountingPeriodMonth.MONTH12));

            for (int middleYear = beginYear + 1; middleYear < endYear; middleYear++) {
                accountingPeriods.put(middleYear, buildPeriodCodeSetWithinRange(AccountingPeriodMonth.MONTH1, AccountingPeriodMonth.MONTH12));
            }

            accountingPeriods.put(endYear, buildPeriodCodeSetWithinRange(AccountingPeriodMonth.MONTH1, endPeriod));
        }
        else if (difference == 0) {
            accountingPeriods.put(beginYear, buildPeriodCodeSetWithinRange(beginPeriod, endPeriod));
        }
        else {
            throw new IllegalArgumentException("The begin year " + beginYear + "should be no later than the end year " + endYear);
        }
        return accountingPeriods;
    }

    /**
     * get the period codes between the begin period and the end period. The begin period should not later than the end period;
     * otherwise, IllegalArgumentException occurs.
     * 
     * @param beginPeriod the begin period
     * @param endPeriod the end period
     * @return the period codes between the begin period and the end period. The returning codes include The codes of begin and end
     *         periods.
     */
    public static Set<String> buildPeriodCodeSetWithinRange(AccountingPeriodMonth beginPeriod, AccountingPeriodMonth endPeriod) {
        if (beginPeriod.compareTo(endPeriod) > 0) {
            throw new IllegalArgumentException("The begin period " + beginPeriod + "should be no later than the end period " + endPeriod);
        }

        Set<String> periodCodesWithinRange = new HashSet<String>();
        for (AccountingPeriodMonth period : EnumSet.range(beginPeriod, endPeriod)) {
            periodCodesWithinRange.add(period.periodCode);
        }
        return periodCodesWithinRange;
    }
}
