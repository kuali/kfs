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
package org.kuali.kfs.module.ar.batch.service.impl;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class VerifyBillingFrequencyServiceImpl implements VerifyBillingFrequencyService {
    protected BusinessObjectService businessObjectService;
    protected AccountingPeriodService accountingPeriodService;
    protected UniversityDateService universityDateService;
    protected DateTimeService dateTimeService;

    protected static final Set<String> invalidPeriodCodes = new TreeSet<>();

    static {
        invalidPeriodCodes.add(KFSConstants.MONTH13);
        invalidPeriodCodes.add(KFSConstants.PERIOD_CODE_ANNUAL_BALANCE);
        invalidPeriodCodes.add(KFSConstants.PERIOD_CODE_BEGINNING_BALANCE);
        invalidPeriodCodes.add(KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE);
    }


    @Override
    public boolean validateBillingFrequency(ContractsAndGrantsBillingAward award) {
        return validateBillingFrequency(award, award.getLastBilledDate());
    }

    @Override
    public boolean validateBillingFrequency(ContractsAndGrantsBillingAward award, ContractsAndGrantsBillingAwardAccount awardAccount) {
        return validateBillingFrequency(award, awardAccount.getCurrentLastBilledDate());
    }

    protected boolean validateBillingFrequency(ContractsAndGrantsBillingAward award, Date lastBilledDate) {
        final Date today = getDateTimeService().getCurrentSqlDate();
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);

        Date[] pair = getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
        Date previousAccountingPeriodStartDate = pair[0];
        Date previousAccountingPeriodEndDate = pair[1];
        if (previousAccountingPeriodStartDate.after(previousAccountingPeriodEndDate)) {
            return false;
        }
        return calculateIfWithinGracePeriod(today, previousAccountingPeriodEndDate, previousAccountingPeriodStartDate, lastBilledDate, award.getBillingFrequency().getGracePeriodDays());
    }

    /**
     * This method checks if a given moment of time is within an accounting period, or its billing frequency grace period.
     *
     * @param today         a date to check if it is within the period
     * @param previousAccountingPeriodEndDate the end of the accounting period
     * @param previousAccountingPeriodStartDate the start of the accounting period
     * @return true if a given moment in time is within an accounting period or an billing frequency grace period
     */
    @Override
    public boolean calculateIfWithinGracePeriod(Date today, Date previousAccountingPeriodEndDate, Date previousAccountingPeriodStartDate, Date lastBilledDate, int gracePeriodDays) {

        if (previousAccountingPeriodEndDate == null || previousAccountingPeriodStartDate == null) {
            throw new IllegalArgumentException("invalid (null) previousAccountingPeriodEndDate or previousAccountingPeriodStartDate");
        }

        final int todayAsComparableDate = comparableDateForm(today);
        final int previousPeriodClose = comparableDateForm(previousAccountingPeriodEndDate);
        final int previousPeriodBegin = comparableDateForm(previousAccountingPeriodStartDate);
        int lastBilled = -1;
        if (lastBilledDate != null) {
            lastBilled = comparableDateForm(lastBilledDate);
        }
        final int gracePeriodClose = previousPeriodClose + gracePeriodDays;
        return (todayAsComparableDate >= previousPeriodBegin && gracePeriodClose <= todayAsComparableDate && (lastBilledDate == null || previousPeriodBegin > lastBilled));

    }

    @Override
    public Date[] getStartDateAndEndDateOfPreviousBillingPeriod(ContractsAndGrantsBillingAward award, AccountingPeriod currPeriod) {
        Date[] startDt_EndDt = new Date[2];
        Date previousAccountingPeriodEndDay = null;
        Date previousAccountingPeriodStartDay = null;
        Date lastBilledDate = award.getLastBilledDate();
        String billingFrequency = award.getBillingFrequencyCode();
        ArrayList<Date> periodEndDateListOfCurrFiscalYear = getSortedListOfPeriodEndDatesOfCurrentFiscalYear(currPeriod);

        // this is used later on when obtaining the last date of the previous fiscal year as the previousAccountPeriodEndDay
        // it subtracts one from the fiscal year for the currPeriod passed in rather than assuming we want the
        // previous fiscal year to the current active fiscal year, just in case a past period is sent in as currPeriod
        // this is mostly just to facilitate unit tests but does make the code more robust (theoretically at least)
        int previousYear = currPeriod.getUniversityFiscalYear() - 1;

        int periodEndDateListOfCurrFiscalYearSize = 0;
        if (periodEndDateListOfCurrFiscalYear != null) {
            periodEndDateListOfCurrFiscalYearSize = periodEndDateListOfCurrFiscalYear.size();
        }

        // 2.billed monthly. ( Milestone and Predetermined Scheduled billing frequencies will also be invoiced as monthly.)
        if (billingFrequency.equalsIgnoreCase(ArConstants.MONTHLY_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            // 2.1 find end date
            if (periodEndDateListOfCurrFiscalYearSize > 0 && currPeriod.getUniversityFiscalPeriodEndDate().equals(periodEndDateListOfCurrFiscalYear.get(0))) {
                previousAccountingPeriodEndDay = new Date(universityDateService.getLastDateOfFiscalYear(previousYear).getTime());
            } else {
                previousAccountingPeriodEndDay = findPreviousAccountingPeriodEndDate(currPeriod, periodEndDateListOfCurrFiscalYear);
            }

            // 2.2 find start date
            // PreviousAccountingPeriodStartDate = previous accounting period endDate of previous accounting period + 1 day
            // for example current date is 2012.8.16, then PreviousAccountingPeriodStartDate = 2012.6.30 + 1day, which is 2012.7.1

            AccountingPeriod period = accountingPeriodService.getByDate(previousAccountingPeriodEndDay);
            if (period.getUniversityFiscalYear().intValue() < currPeriod.getUniversityFiscalYear().intValue()) {
                if (lastBilledDate == null) {
                    previousAccountingPeriodStartDay = award.getAwardBeginningDate();
                } else {
                    ArrayList<Date> acctPeriodEndDateListOfPreviousFiscalYear = getSortedListOfPeriodEndDatesOfCurrentFiscalYear(period);
                    previousAccountingPeriodStartDay = findPreviousAccountingPeriodEndDateReverse(previousAccountingPeriodEndDay, previousAccountingPeriodStartDay, acctPeriodEndDateListOfPreviousFiscalYear);
                }
            }
            else if (period.getUniversityFiscalYear().intValue() == currPeriod.getUniversityFiscalYear().intValue()) {
                if (lastBilledDate == null) {
                    previousAccountingPeriodStartDay = award.getAwardBeginningDate();
                } else {
                    if (periodEndDateListOfCurrFiscalYearSize > 0 && previousAccountingPeriodEndDay.equals(periodEndDateListOfCurrFiscalYear.get(0))) {
                        previousAccountingPeriodStartDay = new Date(universityDateService.getFirstDateOfFiscalYear(currPeriod.getUniversityFiscalYear()).getTime());
                    } else {
                        previousAccountingPeriodStartDay = findPreviousAccountingPeriodStartDate(previousAccountingPeriodEndDay, periodEndDateListOfCurrFiscalYear);
                    }
                }
            }
        }

        // 3.billed quarterly
        if (billingFrequency.equalsIgnoreCase(ArConstants.QUATERLY_BILLING_SCHEDULE_CODE)) {
            // 3.1 find end date
            if (lastBilledDate != null) {
                if (periodEndDateListOfCurrFiscalYearSize > 2 && !currPeriod.getUniversityFiscalPeriodEndDate().after(periodEndDateListOfCurrFiscalYear.get(2))) {
                    previousAccountingPeriodEndDay = new Date(universityDateService.getLastDateOfFiscalYear(previousYear).getTime());
                } else {
                    previousAccountingPeriodEndDay = findPreviousQuarterlyAccountingPeriodEndDate(currPeriod, periodEndDateListOfCurrFiscalYear);
                }
            } else {
                if (periodEndDateListOfCurrFiscalYearSize > 0 && currPeriod.getUniversityFiscalPeriodEndDate().equals(periodEndDateListOfCurrFiscalYear.get(0))) {
                    previousAccountingPeriodEndDay = new Date(universityDateService.getLastDateOfFiscalYear(previousYear).getTime());
                } else {
                    Date dt = accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalPeriodEndDate();
                    previousAccountingPeriodEndDay = findPreviousQuarterlyAccountingPeriodEndDate2(periodEndDateListOfCurrFiscalYear, dt);
                }
            }
            // 3.2 find start date
            // PreviousAccountingPeriodStartDate falls into previous fiscal year
            AccountingPeriod period = accountingPeriodService.getByDate(previousAccountingPeriodEndDay);
            if (lastBilledDate == null) {
                previousAccountingPeriodStartDay = award.getAwardBeginningDate();
            } else {
                if (period.getUniversityFiscalYear().intValue() < currPeriod.getUniversityFiscalYear().intValue()) {
                    ArrayList<Date> acctPeriodEndDateListOfPreviousFiscalYear = getSortedListOfPeriodEndDatesOfCurrentFiscalYear(period);
                    previousAccountingPeriodStartDay = findPreviousQuarterlyAccountingPeriodEndDateReverse(previousAccountingPeriodEndDay, previousAccountingPeriodStartDay, acctPeriodEndDateListOfPreviousFiscalYear);
                }
                else if (period.getUniversityFiscalYear().intValue() == currPeriod.getUniversityFiscalYear().intValue()) {
                    if (periodEndDateListOfCurrFiscalYearSize > 2 && !previousAccountingPeriodEndDay.after(periodEndDateListOfCurrFiscalYear.get(2))) {
                        final Date firstDayOfCurrentFiscalYear = new Date(universityDateService.getFirstDateOfFiscalYear(currPeriod.getUniversityFiscalYear()).getTime());
                        previousAccountingPeriodStartDay = firstDayOfCurrentFiscalYear;
                    } else {
                        previousAccountingPeriodStartDay = findPreviousQuarterlyAccountingPeriodStartDate(previousAccountingPeriodEndDay, periodEndDateListOfCurrFiscalYear);
                    }
                }
            }
        }

        // 4.billed semi-annually
        if (billingFrequency.equalsIgnoreCase(ArConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE)) {
            // 4.1 find end date
            if (lastBilledDate != null) {
                // if the current month is in the first fiscal semi-year of the current year,
                // then get the last day of the previous fiscal year as PreviousAccountingPeriodEndDate
                if (!currPeriod.getUniversityFiscalPeriodEndDate().after(periodEndDateListOfCurrFiscalYear.get(5))) {
                    previousAccountingPeriodEndDay = new Date(universityDateService.getLastDateOfFiscalYear(previousYear).getTime());
                } else {
                    previousAccountingPeriodEndDay = findPreviousSemiAnnualAccountingPeriodEndDate(currPeriod, periodEndDateListOfCurrFiscalYear);
                }
            } else {
                Date dt = accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalPeriodEndDate();

                if (accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalYear().compareTo(currPeriod.getUniversityFiscalYear()) < 0) {
                    previousAccountingPeriodEndDay = new Date(universityDateService.getLastDateOfFiscalYear(previousYear).getTime());
                } else {
                    previousAccountingPeriodEndDay = findPreviousSemiAnnualAccountingPeriodEndDate2(previousAccountingPeriodEndDay, periodEndDateListOfCurrFiscalYear, dt);
                }
            }


            // 4.2 find start date
            // PreviousAccountingPeriodStartDate falls into previous fiscal year
            AccountingPeriod period = accountingPeriodService.getByDate(previousAccountingPeriodEndDay);
            if (lastBilledDate == null) {
                previousAccountingPeriodStartDay = award.getAwardBeginningDate();
            } else {
                if (period.getUniversityFiscalYear() < currPeriod.getUniversityFiscalYear()) {
                    ArrayList<Date> periodEndDateListOfPreviousFiscalYear = getSortedListOfPeriodEndDatesOfCurrentFiscalYear(period);
                    previousAccountingPeriodStartDay = findPreviousSemiAnnualAccountingPeriodEndDateReverse(previousAccountingPeriodEndDay, previousAccountingPeriodStartDay, periodEndDateListOfPreviousFiscalYear);
                }
                // PreviousAccountingPeriodStartDate falls into current fiscal year
                else if (period.getUniversityFiscalYear().intValue() == currPeriod.getUniversityFiscalYear().intValue()) {

                    // previousAccoutingPeriodEndDay falls in the first fiscal period
                    if (!previousAccountingPeriodEndDay.after(periodEndDateListOfCurrFiscalYear.get(5))) {
                        final Date firstDayOfCurrentFiscalYear = new Date(universityDateService.getFirstDateOfFiscalYear(currPeriod.getUniversityFiscalYear()).getTime());
                        previousAccountingPeriodStartDay = firstDayOfCurrentFiscalYear;
                    }
                    // previousAccoutingPeriodEndDay does not falls in the first fiscal period
                    else {
                        previousAccountingPeriodStartDay = findPreviousSemiAnnualAccountingPeriodStartDate(previousAccountingPeriodEndDay, periodEndDateListOfCurrFiscalYear);
                    }
                }
            }
        }

        // 5.billed annually
        if (billingFrequency.equalsIgnoreCase(ArConstants.ANNUALLY_BILLING_SCHEDULE_CODE)) {
            // 5.1 find end date
            if (lastBilledDate != null) {
                previousAccountingPeriodEndDay = new Date(universityDateService.getLastDateOfFiscalYear(previousYear).getTime()); // assume the calendar date, discussion needed
            } else {
                if (accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalYear().compareTo(currPeriod.getUniversityFiscalYear()) < 0) {
                    previousAccountingPeriodEndDay = new Date(universityDateService.getLastDateOfFiscalYear(previousYear).getTime());
                } else {
                    previousAccountingPeriodEndDay = periodEndDateListOfCurrFiscalYear.get(11);
                }
            }

            // 5.2 find start date
            if (lastBilledDate == null) {
                previousAccountingPeriodStartDay = award.getAwardBeginningDate();
            } else {
                previousAccountingPeriodStartDay = new Date(universityDateService.getFirstDateOfFiscalYear(previousYear).getTime());
            }
        }

        // 6.billed for LOC Review - A Random billing period
        if (billingFrequency.equalsIgnoreCase(ArConstants.LOC_BILLING_SCHEDULE_CODE)) {

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            previousAccountingPeriodEndDay = new Date(cal.getTime().getTime());

            // 5.2 find start date
            if (lastBilledDate == null) {
                previousAccountingPeriodStartDay = award.getAwardBeginningDate();
            } else {
                previousAccountingPeriodStartDay = calculateNextDay(lastBilledDate);
            }
        }

        startDt_EndDt[0] = previousAccountingPeriodStartDay;
        startDt_EndDt[1] = previousAccountingPeriodEndDay;
        return startDt_EndDt;
    }

    protected Date findPreviousSemiAnnualAccountingPeriodStartDate(Date previousAccountingPeriodEndDay, ArrayList<Date> periodEndDateListOfCurrFiscalYear) {
        for (int i = 5; i < periodEndDateListOfCurrFiscalYear.size(); i += 6) {
            Date tmpEndDate = periodEndDateListOfCurrFiscalYear.get(i);

            if (!tmpEndDate.before(previousAccountingPeriodEndDay)) {
                return calculateNextDay(periodEndDateListOfCurrFiscalYear.get(i - 6));
            }
        }
        throw new RuntimeException("Could not find start date for current period " + previousAccountingPeriodEndDay.toString());
    }

    protected Date findPreviousSemiAnnualAccountingPeriodEndDateReverse(Date previousAccountingPeriodEndDay, Date previousAccountingPeriodStartDay, ArrayList<Date> periodEndDateListOfPreviousFiscalYear) {
        for (int i = periodEndDateListOfPreviousFiscalYear.size() - 1; i >= 0; i -= 6) {
            Date tmpEndDate = periodEndDateListOfPreviousFiscalYear.get(i);

            if (tmpEndDate.before(previousAccountingPeriodEndDay)) {
                return calculateNextDay(tmpEndDate);
            }
        }
        return previousAccountingPeriodStartDay;
    }

    // find the closest period end date by the award beginning date,
    // for exmple award is created on 7/15/2012 and billed annually, then the next billing date for this award
    // is 12/31/2012
    protected Date findPreviousSemiAnnualAccountingPeriodEndDate2(Date previousAccountingPeriodEndDay, ArrayList<Date> periodEndDateListOfCurrFiscalYear, Date dt) {
        for (int i = 5; i < periodEndDateListOfCurrFiscalYear.size(); i += 6) {
            if (dt.before(periodEndDateListOfCurrFiscalYear.get(i)) || dt.equals(periodEndDateListOfCurrFiscalYear.get(i))) {
                return periodEndDateListOfCurrFiscalYear.get(i);
            }
        }
        return previousAccountingPeriodEndDay;
    }

    protected Date findPreviousSemiAnnualAccountingPeriodEndDate(AccountingPeriod currPeriod, ArrayList<Date> periodEndDateListOfCurrFiscalYear) {
        for (int i = 5; i < periodEndDateListOfCurrFiscalYear.size(); i += 6) {
            if (!currPeriod.getUniversityFiscalPeriodEndDate().after(periodEndDateListOfCurrFiscalYear.get(i))) {
                return periodEndDateListOfCurrFiscalYear.get(i - 6);
            }
        }
        throw new RuntimeException("Could not find end date for current period " + currPeriod.toString());
    }

    protected Date findPreviousQuarterlyAccountingPeriodStartDate(Date previousAccountingPeriodEndDay, ArrayList<Date> periodEndDateListOfCurrFiscalYear) {
        for (int i = 2; i < periodEndDateListOfCurrFiscalYear.size(); i += 3) {
            Date tmpEndDate = periodEndDateListOfCurrFiscalYear.get(i);

            if (!tmpEndDate.before(previousAccountingPeriodEndDay)) {
                return calculateNextDay(periodEndDateListOfCurrFiscalYear.get(i - 3));
            }
        }
        throw new RuntimeException("Could not find start date for current period " + previousAccountingPeriodEndDay.toString());
    }

    protected Date findPreviousQuarterlyAccountingPeriodEndDateReverse(Date previousAccountingPeriodEndDay, Date previousAccountingPeriodStartDay, ArrayList<Date> acctPeriodEndDateListOfPreviousFiscalYear) {
        for (int j = acctPeriodEndDateListOfPreviousFiscalYear.size() - 1; j >= 0; j -= 3) {
            Date tmpEndDate = acctPeriodEndDateListOfPreviousFiscalYear.get(j);

            if (tmpEndDate.before(previousAccountingPeriodEndDay)) {
                return calculateNextDay(tmpEndDate);
            }
        }
        return previousAccountingPeriodStartDay;
    }

    // find the closest period end date by the award beginning date,
    // for exmple award is created on 7/15/2012 and billed quarterly, then the next billing date for this award is
    // 9/30/2012
    protected Date findPreviousQuarterlyAccountingPeriodEndDate2(ArrayList<Date> periodEndDateListOfCurrFiscalYear, Date dt) {
        for (int i = 2; i < periodEndDateListOfCurrFiscalYear.size(); i += 3) {
            if (!dt.after(periodEndDateListOfCurrFiscalYear.get(i))) {
                return periodEndDateListOfCurrFiscalYear.get(i);
            }
        }
        throw new RuntimeException("Could not find end date for current period " + dt.toString());
    }

    // find the PreviousAccountingPeriodEndDate by current fiscal period end date and last billed date.
    // for exmple, if current date is 2011.10.8, then the code will get out from for loop when looping to i =5
    // (2011.12.31), so previous end date is 2011.9.30(i=5-3=2)
    protected Date findPreviousQuarterlyAccountingPeriodEndDate(AccountingPeriod currPeriod, ArrayList<Date> periodEndDateListOfCurrFiscalYear) {
        for (int i = 2; i < periodEndDateListOfCurrFiscalYear.size(); i += 3) {
            if (!currPeriod.getUniversityFiscalPeriodEndDate().after(periodEndDateListOfCurrFiscalYear.get(i))) {
                return periodEndDateListOfCurrFiscalYear.get(i - 3);
            }
        }
        throw new RuntimeException("Could not find end date for current period " + currPeriod.toString());
    }

    protected Date findPreviousAccountingPeriodStartDate(Date previousAccountingPeriodEndDay, ArrayList<Date> periodEndDateListOfCurrFiscalYear) {
        for (int i = 0; i < periodEndDateListOfCurrFiscalYear.size(); i++) {
            Date tmpEndDate = periodEndDateListOfCurrFiscalYear.get(i);

            if (!tmpEndDate.before(previousAccountingPeriodEndDay)) {
                return calculateNextDay(periodEndDateListOfCurrFiscalYear.get(i - 1));
            }
        }
        throw new RuntimeException("Could not find start date for current period " + previousAccountingPeriodEndDay.toString());
    }

    protected Date findPreviousAccountingPeriodEndDateReverse(Date previousAccountingPeriodEndDay, Date previousAccountingPeriodStartDay, ArrayList<Date> acctPeriodEndDateListOfPreviousFiscalYear) {
        for (int i = acctPeriodEndDateListOfPreviousFiscalYear.size() - 1; i >= 0; i -= 1) {
            Date tmpEndDate = acctPeriodEndDateListOfPreviousFiscalYear.get(i);

            if (tmpEndDate.before(previousAccountingPeriodEndDay)) {
                return calculateNextDay(tmpEndDate);
            }
        }
        return previousAccountingPeriodStartDay;
    }

    protected Date findPreviousAccountingPeriodEndDate(AccountingPeriod currPeriod, ArrayList<Date> periodEndDateListOfCurrFiscalYear) {
        for (int i = 0; i < periodEndDateListOfCurrFiscalYear.size(); i++) {
            if (currPeriod.getUniversityFiscalPeriodEndDate().equals(periodEndDateListOfCurrFiscalYear.get(i))) {
                return periodEndDateListOfCurrFiscalYear.get(i - 1);
            }
        }
        throw new RuntimeException("Could not find end date for current period " + currPeriod.toString());
    }


    @Override
    public ArrayList<Date> getSortedListOfPeriodEndDatesOfCurrentFiscalYear(AccountingPeriod currPeriod) {
        ArrayList<Date> acctPeriodEndDateList = new ArrayList<>();
        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put(KFSConstants.ACCOUNTING_PERIOD_ACTIVE_INDICATOR_FIELD, Boolean.TRUE);
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currPeriod.getUniversityFiscalYear());

        Collection<AccountingPeriod> acctPeriodList = businessObjectService.findMatching(AccountingPeriod.class, fieldValues);
        if (acctPeriodList != null) {
            for (AccountingPeriod acctPeriod : acctPeriodList) {
                if (!isInvalidPeriodCode(acctPeriod)) {
                    acctPeriodEndDateList.add(acctPeriod.getUniversityFiscalPeriodEndDate());
                }

            }
            if (acctPeriodEndDateList == null || acctPeriodEndDateList.size() != 12) {
                String fiscalYear = "; fiscalYear: " + currPeriod.getUniversityFiscalYear();
                String size = "; size = " + ((acctPeriodEndDateList == null) ? "null" : acctPeriodEndDateList.size());
                throw new IllegalArgumentException("invalid (null) Accounting-Period-End-Date List" + fiscalYear + size);
            }
            java.util.Collections.sort(acctPeriodEndDateList);
            return acctPeriodEndDateList;

        } else {
            throw new IllegalArgumentException("invalid (null) Accounting-Period-End-Date List");
        }
    }

    /**
     * This method returns a date as an approximate count of days since the BCE epoch.
     *
     * @param d the date to convert
     * @return an integer count of days, very approximate
     */
    public int comparableDateForm(Date d) {
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(d);
        return cal.get(java.util.Calendar.YEAR) * 365 + cal.get(java.util.Calendar.DAY_OF_YEAR);
    }

    /**
     * Given a day, this method calculates the next day of that day was.
     *
     * @param d date to find the next day for
     * @return date of the next day of the given day
     */
    public Date calculateNextDay(Date d) {
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(d);
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
        return new Date(cal.getTimeInMillis());
    }

    /**
     * This checks to see if the period code is empty or invalid ("13", "AB", "BB", "CB")
     *
     * @param period
     * @return
     */
    protected boolean isInvalidPeriodCode(AccountingPeriod period) {
        String periodCode = period.getUniversityFiscalPeriodCode();
        if (StringUtils.isBlank(periodCode)) {
            throw new IllegalArgumentException("invalid (null) universityFiscalPeriodCode (" + periodCode + ")for" + period);
        }
        return invalidPeriodCodes.contains(periodCode);
    }


    /**
     * Sets the accountingPeriodService attribute value.
     *
     * @param accountingPeriodService The accountingPeriodService to set.
     */
    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    /**
     * Sets the universityDateService attribute value.
     *
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
