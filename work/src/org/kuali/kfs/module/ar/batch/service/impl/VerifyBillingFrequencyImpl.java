/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch.service.impl;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequency;
import org.kuali.kfs.sys.service.UniversityDateService;

/**
 * Implementation of the billing frequency validation.
 */
public class VerifyBillingFrequencyImpl implements VerifyBillingFrequency {

    private AccountingPeriodService accountingPeriodService;
    private UniversityDateService universityDateService;

    protected static final Set<String> _invalidPeriodCodes = new TreeSet<String>();
    static {
        _invalidPeriodCodes.add("13");
        _invalidPeriodCodes.add("AB");
        _invalidPeriodCodes.add("BB");
        _invalidPeriodCodes.add("CB");
    }


    public boolean validatBillingFrequency(ContractsAndGrantsCGBAward award) {

        // first we need to get the period itself to check these things
        boolean valid = true;
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        Date today = new Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);


        Date[] pair = getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
        Date previousAccountingPeriodEndDate = pair[1];
        Date previousAccountingPeriodStartDate = pair[0];
        // To check if the previousAccountingPeriodStartDate is after previousAccountingPeriodEndDate - situations where award
        // beginning date is set later.
        if (previousAccountingPeriodStartDate.after(previousAccountingPeriodEndDate)) {
            valid = false;
        }
        else {
            valid = calculateIfWithinGracePeriod(today, previousAccountingPeriodEndDate, previousAccountingPeriodStartDate, award.getLastBilledDate(), Integer.parseInt(award.getBillingFrequency().getGracePeriodDays()));
        }

        return valid;
    }


    /**
     * This method checks if a given moment of time is within an accounting period, or its billing frequency grace period.
     * 
     * @param today a date to check if it is within the period
     * @param periodToCheck the account period to check against
     * @return true if a given moment in time is within an accounting period or an billing frequency grace period
     */
    public boolean calculateIfWithinGracePeriod(Date today, Date previousAccountingPeriodEndDate, Date previousAccountingPeriodStartDate, Date lastBilledDate, int gracePeriodDays) {
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        boolean result = false;
        int lastBilled = -1;

        if (previousAccountingPeriodEndDate == null || previousAccountingPeriodStartDate == null) {
            throw new IllegalArgumentException("invalid (null) previousAccountingPeriodEndDate or previousAccountingPeriodStartDate");
        }

        final int todayAsComparableDate = comparableDateForm(today);
        final int previousPeriodClose = new Integer(comparableDateForm(previousAccountingPeriodEndDate));
        final int previousPeriodBegin = comparableDateForm(previousAccountingPeriodStartDate);
        if (lastBilledDate != null) {
            lastBilled = comparableDateForm(lastBilledDate);
        }
        final int gracePeriodClose = previousPeriodClose + gracePeriodDays;
        return (todayAsComparableDate >= previousPeriodBegin && gracePeriodClose <= todayAsComparableDate && (lastBilledDate == null || previousPeriodBegin > lastBilled));

    }

    // (assume there are 12 periods in a fiscal year)
    public Date[] getStartDateAndEndDateOfPreviousBillingPeriod(ContractsAndGrantsCGBAward award, AccountingPeriod currPeriod) {
        Date[] startDt_EndDt = new Date[2];
        Date previousAccountingPeriodEndDay = null;
        Date previousAccountingPeriodStartDay = null;
        Date tmpEndDate;
        Date lastBilledDate = award.getLastBilledDate();
        String billingFrequency = award.getPreferredBillingFrequency();
        ArrayList<Date> periodEndDateListOfCurrFiscalYear = getSortedListOfPeriodEndDatesOfCurrentFiscalYear(currPeriod);

        // using dates to find PreviousAccountingPeriodEndDate, instead of AccountingPeriod
        // removing weekly from billing frequencies.

        // 2.billed monthly. ( Milestone and Predetermined Scheduled billing frequencies will also be invoiced as monthly.)
        if (billingFrequency.equalsIgnoreCase(ArPropertyConstants.MONTHLY_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            // 2.1 find end date
            if (lastBilledDate != null) {
                // if the current month is the first fiscal month of the current year, then get the last day of the previous fiscal
                // year
                if (currPeriod.getUniversityFiscalPeriodEndDate().equals(periodEndDateListOfCurrFiscalYear.get(0)))
                    previousAccountingPeriodEndDay = getEndDateOfPreviousFiscalYear();// assume the calendar date, discussion
                                                                                      // needed?
                else {
                    int i = -1;
                    for (i = 0; i < periodEndDateListOfCurrFiscalYear.size(); i++) {
                        // find the PreviousAccountingPeriodEndDate by current fiscal period end date and last billed date
                        // last billed date <= PreviousAccountingPeriodEndDate < current fiscal period end date
                        if (currPeriod.getUniversityFiscalPeriodEndDate().equals(periodEndDateListOfCurrFiscalYear.get(i))) {
                            break;
                        }
                    }

                    previousAccountingPeriodEndDay = periodEndDateListOfCurrFiscalYear.get(i - 1);
                }
            }
            else {
                // if the current month is the first fiscal month of the current year, then get the last day of the previous fiscal
                // year
                if (currPeriod.getUniversityFiscalPeriodEndDate().equals(periodEndDateListOfCurrFiscalYear.get(0)))
                    previousAccountingPeriodEndDay = getEndDateOfPreviousFiscalYear();
                
                else{
                // get end date by award's beginning date
                // previousAccountingPeriodEndDay =
                // accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalPeriodEndDate();
                // if the lastBilledDate = null, means the the award is billed from its start date till the previous period end
                // date, so the calculation would be:
                int i = -1;
                for (i = 0; i < periodEndDateListOfCurrFiscalYear.size(); i++) {
                    // find the PreviousAccountingPeriodEndDate by current fiscal period end date and last billed date
                    // last billed date <= PreviousAccountingPeriodEndDate < current fiscal period end date
                    if (currPeriod.getUniversityFiscalPeriodEndDate().equals(periodEndDateListOfCurrFiscalYear.get(i))) {
                        break;
                    }
                }

                previousAccountingPeriodEndDay = periodEndDateListOfCurrFiscalYear.get(i - 1);

            }
            }

            // 2.2 find start date
            // PreviousAccountingPeriodStartDate = previous accounting period endDate of previous accounting period + 1 day
            // for example current date is 2012.8.16, then PreviousAccountingPeriodStartDate = 2012.6.30 + 1day, which is 2012.7.1

            // PreviousAccountingPeriodEndDate falls into previous fiscal year
            AccountingPeriod period = accountingPeriodService.getByDate(previousAccountingPeriodEndDay);
            if (period.getUniversityFiscalYear().intValue() < currPeriod.getUniversityFiscalYear().intValue()) {
                
                if (lastBilledDate == null) {
                    previousAccountingPeriodStartDay = award.getAwardBeginningDate();
                }
                else{
                ArrayList<Date> acctPeriodEndDateListOfPreviousFiscalYear = getSortedListOfPeriodEndDatesOfCurrentFiscalYear(period);

                int i = -1;
                for (i = acctPeriodEndDateListOfPreviousFiscalYear.size() - 1; i >= 0; i -= 1) {
                    tmpEndDate = acctPeriodEndDateListOfPreviousFiscalYear.get(i);

                    if (tmpEndDate.before(previousAccountingPeriodEndDay)) {
                        previousAccountingPeriodStartDay = calculateNextDay(tmpEndDate);
                        break;
                    }
                }

            }
            }
            // PreviousAccountingPeriodEndDate falls into current fiscal year
            else if (period.getUniversityFiscalYear().intValue() == currPeriod.getUniversityFiscalYear().intValue()) {
                if (lastBilledDate == null) {
                    previousAccountingPeriodStartDay = award.getAwardBeginningDate();
                }
                // when the previousAccountingPeriodEndDay was set to the previous period end date (because the lastBilled date is
                // not null)
                else {
                    // previousAccoutingPeriodEndDay falls in the first fiscal period
                    if (previousAccountingPeriodEndDay.equals(periodEndDateListOfCurrFiscalYear.get(0))) {
                        final Date firstDayOfCurrentFiscalYear = new Date(universityDateService.getFirstDateOfFiscalYear(currPeriod.getUniversityFiscalYear()).getTime());
                        previousAccountingPeriodStartDay = firstDayOfCurrentFiscalYear;
                    }
                    // previousAccoutingPeriodEndDay does not falls in the first fiscal period
                    else {
                        int i = -1;
                        for (i = 0; i < periodEndDateListOfCurrFiscalYear.size(); i++) {
                            tmpEndDate = periodEndDateListOfCurrFiscalYear.get(i);

                            if (!tmpEndDate.before(previousAccountingPeriodEndDay)) // annayue both 7.31 first fiscal period before
                                                                                    // has problem set flag to see//??????????
                                break;
                        }
                        previousAccountingPeriodStartDay = calculateNextDay(periodEndDateListOfCurrFiscalYear.get(i - 1));
                    }
                }
            }
        }

        // 3.billed quarterly
        if (billingFrequency.equalsIgnoreCase(ArPropertyConstants.QUATERLY_BILLING_SCHEDULE_CODE)) {
            // 3.1 find end date
            if (lastBilledDate != null) {
                // if the current month is in the first fiscal quarter of the current year,
                // then get the last day of the previous fiscal year as PreviousAccountingPeriodEndDate
                if (!currPeriod.getUniversityFiscalPeriodEndDate().after(periodEndDateListOfCurrFiscalYear.get(2))) {
                    previousAccountingPeriodEndDay = getEndDateOfPreviousFiscalYear();
                }
                else {
                    int i = 0;
                    for (i = 2; i < periodEndDateListOfCurrFiscalYear.size(); i += 3) {
                        // find the PreviousAccountingPeriodEndDate by current fiscal period end date and last billed date.
                        // last billed date <= PreviousAccountingPeriodEndDate < current fiscal period end date
                        // for exmple, if current date is 2011.10.8, then the code will get out from for loop when looping to i =5
                        // (2011.12.31), so previous end date is 2011.9.30(i=5-3=2)
                        if (!currPeriod.getUniversityFiscalPeriodEndDate().after(periodEndDateListOfCurrFiscalYear.get(i))) {
                            break;
                        }
                    }
                    previousAccountingPeriodEndDay = periodEndDateListOfCurrFiscalYear.get(i - 3);
                }

            }
            else {
                
                if (currPeriod.getUniversityFiscalPeriodEndDate().equals(periodEndDateListOfCurrFiscalYear.get(0)))
                    previousAccountingPeriodEndDay = getEndDateOfPreviousFiscalYear();
                
                else{
                Date dt = accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalPeriodEndDate();
                int fsclYearOfStartDt = accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalYear();

                int i = -1;
                for (i = 2; i < periodEndDateListOfCurrFiscalYear.size(); i += 3) {
                    // find the closest period end date by the award beginning date,
                    // for exmple award is created on 7/15/2012 and billed quarterly, then the next billing date for this award is
                    // 9/30/2012
                    if (!dt.after(periodEndDateListOfCurrFiscalYear.get(i))) {
                        break;
                    }
                }
                previousAccountingPeriodEndDay = periodEndDateListOfCurrFiscalYear.get(i);

            }
            }
            // 3.2 find start date
            // PreviousAccountingPeriodStartDate falls into previous fiscal year
            AccountingPeriod period = accountingPeriodService.getByDate(previousAccountingPeriodEndDay);
            if (lastBilledDate == null) {
                previousAccountingPeriodStartDay = award.getAwardBeginningDate();
            }
            else {
                if (period.getUniversityFiscalYear().intValue() < currPeriod.getUniversityFiscalYear().intValue()) {

                    ArrayList<Date> acctPeriodEndDateListOfPreviousFiscalYear = getSortedListOfPeriodEndDatesOfCurrentFiscalYear(period);

                    int j = -1;
                    for (j = acctPeriodEndDateListOfPreviousFiscalYear.size() - 1; j >= 0; j -= 3) {
                        tmpEndDate = acctPeriodEndDateListOfPreviousFiscalYear.get(j);

                        if (tmpEndDate.before(previousAccountingPeriodEndDay)) {
                            previousAccountingPeriodStartDay = calculateNextDay(tmpEndDate);
                            break;
                        }
                    }

                }
                // PreviousAccountingPeriodStartDate falls into current fiscal year
                else if (period.getUniversityFiscalYear().intValue() == currPeriod.getUniversityFiscalYear().intValue()) {

                    // previousAccoutingPeriodEndDay falls in the first fiscal period(first quarter)
                    if (!previousAccountingPeriodEndDay.after(periodEndDateListOfCurrFiscalYear.get(2))) {
                        final Date firstDayOfCurrentFiscalYear = new Date(universityDateService.getFirstDateOfFiscalYear(currPeriod.getUniversityFiscalYear()).getTime());
                        previousAccountingPeriodStartDay = firstDayOfCurrentFiscalYear;
                    }
                    // previousAccoutingPeriodEndDay does not falls in the first fiscal period
                    else {
                        int i = -1;
                        for (i = 2; i < periodEndDateListOfCurrFiscalYear.size(); i += 3) {
                            tmpEndDate = periodEndDateListOfCurrFiscalYear.get(i);

                            if (!tmpEndDate.before(previousAccountingPeriodEndDay)) {
                                break;
                            }
                        }
                        previousAccountingPeriodStartDay = calculateNextDay(periodEndDateListOfCurrFiscalYear.get(i - 3));
                    }
                }
            }
        }

        // 4.billed semi-annually
        if (billingFrequency.equalsIgnoreCase(ArPropertyConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE)) {
            // 4.1 find end date
            if (lastBilledDate != null) {
                // if the current month is in the first fiscal semi-year of the current year,
                // then get the last day of the previous fiscal year as PreviousAccountingPeriodEndDate
                if (!currPeriod.getUniversityFiscalPeriodEndDate().after(periodEndDateListOfCurrFiscalYear.get(5)))
                    previousAccountingPeriodEndDay = getEndDateOfPreviousFiscalYear();
                else {
                    int i = -1;
                    for (i = 5; i < periodEndDateListOfCurrFiscalYear.size(); i += 6) {
                        // find the PreviousAccountingPeriodEndDate by current fiscal period end date and last billed date.
                        // last billed date <= PreviousAccountingPeriodEndDate < current fiscal period end date
                        tmpEndDate = periodEndDateListOfCurrFiscalYear.get(i);

                        if (!currPeriod.getUniversityFiscalPeriodEndDate().after(periodEndDateListOfCurrFiscalYear.get(i))) {
                            break;
                        }
                    }
                    previousAccountingPeriodEndDay = periodEndDateListOfCurrFiscalYear.get(i - 6);
                }
            }
            else {
                Date dt = accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalPeriodEndDate();
                int fsclYearOfAwdStartDt = accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalYear();
                // if award beginning date is in previous fiscal year.. then the end date is end date of previous fiscal year.

                if (accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalYear().compareTo(currPeriod.getUniversityFiscalYear()) < 0) {
                    previousAccountingPeriodEndDay = getEndDateOfPreviousFiscalYear();
                }
                else {
                    for (int i = 5; i < periodEndDateListOfCurrFiscalYear.size(); i += 6) {
                        // find the closest period end date by the award beginning date,
                        // for exmple award is created on 7/15/2012 and billed annually, then the next billing date for this award
                        // is 12/31/2012
                        if (dt.before(periodEndDateListOfCurrFiscalYear.get(i)) || dt.equals(periodEndDateListOfCurrFiscalYear.get(i))) {
                            previousAccountingPeriodEndDay = periodEndDateListOfCurrFiscalYear.get(i);
                            break;
                        }
                    }
                }
            }


            // 4.2 find start date
            // PreviousAccountingPeriodStartDate falls into previous fiscal year
            AccountingPeriod period = accountingPeriodService.getByDate(previousAccountingPeriodEndDay);
            if (lastBilledDate == null) {
                previousAccountingPeriodStartDay = award.getAwardBeginningDate();
            }
            else {
                if (period.getUniversityFiscalYear() < currPeriod.getUniversityFiscalYear()) {
                    ArrayList<Date> periodEndDateListOfPreviousFiscalYear = getSortedListOfPeriodEndDatesOfCurrentFiscalYear(period);

                    int i = -1;
                    for (i = periodEndDateListOfPreviousFiscalYear.size() - 1; i >= 0; i -= 6) {
                        tmpEndDate = periodEndDateListOfPreviousFiscalYear.get(i);

                        if (tmpEndDate.before(previousAccountingPeriodEndDay)) {
                            previousAccountingPeriodStartDay = calculateNextDay(tmpEndDate);
                            break;
                        }
                    }

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
                        int i = -1;
                        for (i = 5; i < periodEndDateListOfCurrFiscalYear.size(); i += 6) {
                            tmpEndDate = periodEndDateListOfCurrFiscalYear.get(i);

                            if (!tmpEndDate.before(previousAccountingPeriodEndDay)) {
                                break;
                            }
                        }
                        previousAccountingPeriodStartDay = calculateNextDay(periodEndDateListOfCurrFiscalYear.get(i - 6));

                    }


                }

            }
        }

        // 5.billed annually
        if (billingFrequency.equalsIgnoreCase(ArPropertyConstants.ANNUALLY_BILLING_SCHEDULE_CODE)) {
            // 5.1 find end date
            if (lastBilledDate != null) {
                previousAccountingPeriodEndDay = getEndDateOfPreviousFiscalYear(); // assume the calendar date, discussion needed
            }
            else {
                /*
                 * final Date firstDayOfPreviousFiscalYear = new
                 * Date(universityDateService.getFirstDateOfFiscalYear(universityDateService.getCurrentFiscalYear()-1).getTime());
                 * previousAccountingPeriodEndDay = firstDayOfPreviousFiscalYear;
                 */
                // if award beginning date is in previous fiscal year.. then the end date is end date of previous fiscal year.

                if (accountingPeriodService.getByDate(award.getAwardBeginningDate()).getUniversityFiscalYear().compareTo(currPeriod.getUniversityFiscalYear()) < 0) {
                    previousAccountingPeriodEndDay = getEndDateOfPreviousFiscalYear();
                }
                else {
                    previousAccountingPeriodEndDay = periodEndDateListOfCurrFiscalYear.get(11);
                }
            }

            // 5.2 find start date
            if (lastBilledDate == null) {
                previousAccountingPeriodStartDay = award.getAwardBeginningDate();
            }
            else {
                int previousYear = universityDateService.getCurrentFiscalYear() - 1;
                final Date firstDayOfPreviousFiscalYear = new Date(universityDateService.getFirstDateOfFiscalYear(previousYear).getTime());
                previousAccountingPeriodStartDay = firstDayOfPreviousFiscalYear;
            }


        }

        // 6.billed for LOC Review - A Random billing period
        if (billingFrequency.equalsIgnoreCase(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE)) {

            // This would be previous day.
            // Get today as a Calendar
            Calendar cal = Calendar.getInstance();
            // Subtract 1 day
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            // Make an SQL Date out of that
            previousAccountingPeriodEndDay = new Date(cal.getTime().getTime());


            // 5.2 find start date
            if (lastBilledDate == null) {
                previousAccountingPeriodStartDay = award.getAwardBeginningDate();
            }
            else {
                previousAccountingPeriodStartDay = calculateNextDay(lastBilledDate);
            }


        }

        // Other billing frequency check should be added here.

        startDt_EndDt[0] = previousAccountingPeriodStartDay;
        startDt_EndDt[1] = previousAccountingPeriodEndDay;
        return startDt_EndDt;

    }


    public ArrayList<Date> getSortedListOfPeriodEndDatesOfCurrentFiscalYear(AccountingPeriod currPeriod) {
        ArrayList<Date> acctPeriodEndDateList = new ArrayList<Date>();

        Object[] acctPeriodList = accountingPeriodService.getAllAccountingPeriods().toArray();
        if (acctPeriodList != null) {
            // get all the accounting period end dates of current fiscal year
            for (int i = 0; i < acctPeriodList.length - 1; i++) {
                AccountingPeriod acctPeriod = (AccountingPeriod) acctPeriodList[i];
                if (acctPeriod.getUniversityFiscalYear().equals(currPeriod.getUniversityFiscalYear()) && !isInvalidPeriodCode(acctPeriod) && acctPeriod.isActive())
                    acctPeriodEndDateList.add(acctPeriod.getUniversityFiscalPeriodEndDate());

            }
            if (acctPeriodEndDateList == null || acctPeriodEndDateList.size() != 12) {
                throw new IllegalArgumentException("invalid (null) Accounting-Period-End-Date List");
            }
            java.util.Collections.sort(acctPeriodEndDateList);
            return acctPeriodEndDateList;

        }
        else {
            throw new IllegalArgumentException("invalid (null) Aaccounting-Period-End-Date List");
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
     * This method get the last day of the previous fiscal year
     * 
     * @return date of the last day of the previous fiscal year
     */
    public Date getEndDateOfPreviousFiscalYear() {
        final Date firstDayOfCurrFiscalYear = new Date(universityDateService.getFirstDateOfFiscalYear(universityDateService.getCurrentFiscalYear()).getTime());
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(firstDayOfCurrFiscalYear);
        cal.add(java.util.Calendar.DATE, -1);
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
        if (periodCode == null) {
            throw new IllegalArgumentException("invalid (null) universityFiscalPeriodCode (" + periodCode + ")for" + period);
        }
        return _invalidPeriodCodes.contains(periodCode);
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


}
