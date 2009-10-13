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
package org.kuali.kfs.module.ec.util;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class AccountingPeriodMonthTest extends TestCase {

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * test the AccountingPeriodMonth.findAccountingPeriod method
     * 
     * @see AccountingPeriodMonth.findAccountingPeriod
     */
    public void testFindAccountingPeriod() throws Exception {
        AccountingPeriodMonth month1 = AccountingPeriodMonth.findAccountingPeriod(AccountingPeriodMonth.MONTH1.periodCode);
        assertEquals(AccountingPeriodMonth.MONTH1, month1);

        AccountingPeriodMonth month12 = AccountingPeriodMonth.findAccountingPeriod(AccountingPeriodMonth.MONTH12.periodCode);
        assertEquals(AccountingPeriodMonth.MONTH12, month12);

        AccountingPeriodMonth month6 = AccountingPeriodMonth.findAccountingPeriod(AccountingPeriodMonth.MONTH6.periodCode);
        assertEquals(AccountingPeriodMonth.MONTH6, month6);
        assertFalse(AccountingPeriodMonth.MONTH1.compareTo(month6) == 0);
        assertFalse(AccountingPeriodMonth.MONTH12.compareTo(month6) == 0);

        AccountingPeriodMonth unknownMonth = AccountingPeriodMonth.findAccountingPeriod("UNKNOWN");
        assertNull(unknownMonth);
        
        AccountingPeriodMonth emptyMonth = AccountingPeriodMonth.findAccountingPeriod("");
        assertNull(emptyMonth);
    }

    /**
     * test the AccountingPeriodMonth.findAccountingPeriodsBetween method
     * 
     * @see AccountingPeriodMonth.findAccountingPeriodsBetween
     */
    public void testFindAccountingPeriodsBetween_WithinSameYear() throws Exception {
        Integer year = 2008;

        this.assertAccountingPeriodsWithinYear(year, AccountingPeriodMonth.MONTH1, AccountingPeriodMonth.MONTH12);

        this.assertAccountingPeriodsWithinYear(year, AccountingPeriodMonth.MONTH1, AccountingPeriodMonth.MONTH3);
        this.assertAccountingPeriodsWithinYear(year, AccountingPeriodMonth.MONTH4, AccountingPeriodMonth.MONTH8);
        this.assertAccountingPeriodsWithinYear(year, AccountingPeriodMonth.MONTH9, AccountingPeriodMonth.MONTH12);

        try {
            this.assertAccountingPeriodsWithinYear(year, AccountingPeriodMonth.MONTH9, AccountingPeriodMonth.MONTH5);
            fail();
        }
        catch (IllegalArgumentException e) {
        }
    }

    /**
     * test the AccountingPeriodMonth.findAccountingPeriodsBetween method
     * 
     * @see AccountingPeriodMonth.findAccountingPeriodsBetween
     */
    public void testFindAccountingPeriodsBetween_AcrossMultipleYears() throws Exception {
        Integer beginYear = 2008;
        Integer endYear = 2010;

        this.assertAccountingPeriodsAcrossYears(beginYear, AccountingPeriodMonth.MONTH1, endYear, AccountingPeriodMonth.MONTH12);

        this.assertAccountingPeriodsAcrossYears(beginYear, AccountingPeriodMonth.MONTH1, endYear, AccountingPeriodMonth.MONTH4);
        this.assertAccountingPeriodsAcrossYears(beginYear, AccountingPeriodMonth.MONTH5, endYear, AccountingPeriodMonth.MONTH12);
        this.assertAccountingPeriodsAcrossYears(beginYear, AccountingPeriodMonth.MONTH5, endYear, AccountingPeriodMonth.MONTH4);

        try {
            this.assertAccountingPeriodsAcrossYears(endYear, AccountingPeriodMonth.MONTH5, beginYear, AccountingPeriodMonth.MONTH4);
            fail();
        }
        catch (IllegalArgumentException e) {
        }
    }

    /**
     * test the AccountingPeriodMonth.buildPeriodCodeSetWithinRange method
     * 
     * @see AccountingPeriodMonth.buildPeriodCodeSetWithinRange
     */
    public void testBuildPeriodCodeSetWithinRange() throws Exception {
        this.assertAccountingPeriodsWithinRange(AccountingPeriodMonth.MONTH1, AccountingPeriodMonth.MONTH12);

        this.assertAccountingPeriodsWithinRange(AccountingPeriodMonth.MONTH1, AccountingPeriodMonth.MONTH3);
        this.assertAccountingPeriodsWithinRange(AccountingPeriodMonth.MONTH4, AccountingPeriodMonth.MONTH8);
        this.assertAccountingPeriodsWithinRange(AccountingPeriodMonth.MONTH9, AccountingPeriodMonth.MONTH12);

        try {
            this.assertAccountingPeriodsWithinRange(AccountingPeriodMonth.MONTH9, AccountingPeriodMonth.MONTH5);
            fail();
        }
        catch (IllegalArgumentException e) {
        }
    }

    private void assertAccountingPeriodsWithinYear(Integer year, AccountingPeriodMonth beginPeriod, AccountingPeriodMonth endPeriod) {
        Map<Integer, Set<String>> periods = AccountingPeriodMonth.findAccountingPeriodsBetween(year, beginPeriod.periodCode, year, endPeriod.periodCode);

        Set<String> periodCodes = periods.get(year);
        this.assertAccountingPeriodMonthEqual(periodCodes, beginPeriod, endPeriod);
    }
    
    private void assertAccountingPeriodsWithinRange(AccountingPeriodMonth beginPeriod, AccountingPeriodMonth endPeriod) {       
        Set<String> periodCodes = AccountingPeriodMonth.buildPeriodCodeSetWithinRange(beginPeriod, endPeriod);
        this.assertAccountingPeriodMonthEqual(periodCodes, beginPeriod, endPeriod);
    }
    
    private void assertAccountingPeriodsAcrossYears(Integer beginYear, AccountingPeriodMonth beginPeriod, Integer endYear, AccountingPeriodMonth endPeriod) {
        Map<Integer, Set<String>> periods = AccountingPeriodMonth.findAccountingPeriodsBetween(beginYear, beginPeriod.periodCode, endYear, endPeriod.periodCode);

        Set<String> beginPeriodCodes = periods.get(beginYear);
        this.assertAccountingPeriodMonthEqual(beginPeriodCodes, beginPeriod, AccountingPeriodMonth.MONTH12);

        for (int year = beginYear + 1; year <= endYear - 1; year++) {
            Set<String> periodCodes = periods.get(year);
            this.assertAccountingPeriodMonthEqual(periodCodes, AccountingPeriodMonth.MONTH1, AccountingPeriodMonth.MONTH12);
        }

        Set<String> endPeriodCodes = periods.get(endYear);
        this.assertAccountingPeriodMonthEqual(endPeriodCodes, AccountingPeriodMonth.MONTH1, endPeriod);
    }

    private void assertAccountingPeriodMonthEqual(Set<String> periodCodes, AccountingPeriodMonth beginPeriod, AccountingPeriodMonth endPeriod) {
        Set<AccountingPeriodMonth> accountingPeriodMonth = EnumSet.range(beginPeriod, endPeriod);
        assertTrue(periodCodes.size() == accountingPeriodMonth.size());

        for (String code : periodCodes) {
            AccountingPeriodMonth month = AccountingPeriodMonth.findAccountingPeriod(code);
            assertTrue(accountingPeriodMonth.contains(month));
        }
    }
}
