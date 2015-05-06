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
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.BillingPeriod;
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

        BillingPeriod billingPeriod = getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
        if (!billingPeriod.isBillable()) {
            return false;
        }
        if (billingPeriod.getStartDate().after(billingPeriod.getEndDate())) {
            return false;
        }
        return calculateIfWithinGracePeriod(today, billingPeriod.getEndDate(), billingPeriod.getStartDate(), lastBilledDate, award.getBillingFrequency().getGracePeriodDays());
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
        final int gracePeriodAfterLastBilled = lastBilled + gracePeriodDays;
        return (todayAsComparableDate >= previousPeriodBegin && gracePeriodClose <= todayAsComparableDate && (lastBilledDate == null || todayAsComparableDate > gracePeriodAfterLastBilled));

    }

    @Override
    public BillingPeriod getStartDateAndEndDateOfPreviousBillingPeriod(ContractsAndGrantsBillingAward award, AccountingPeriod currPeriod) {
        return BillingPeriod.determineBillingPeriodPriorTo(award.getAwardBeginningDate(), this.dateTimeService.getCurrentSqlDate(), award.getLastBilledDate(), award.getBillingFrequencyCode(), this.accountingPeriodService);
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
