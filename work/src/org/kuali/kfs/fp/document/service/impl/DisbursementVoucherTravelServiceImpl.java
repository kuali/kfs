/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;

import org.kuali.kfs.fp.businessobject.TravelMileageRate;
import org.kuali.kfs.fp.document.dataaccess.TravelMileageRateDao;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTravelService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This is the default implementation of the DisbursementVoucherTravelService interface.
 * Performs calculations of travel per diem and mileage amounts.
 */

@NonTransactional
public class DisbursementVoucherTravelServiceImpl implements DisbursementVoucherTravelService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherTravelServiceImpl.class);

    protected TravelMileageRateDao travelMileageRateDao;
    protected DateTimeService dateTimeService;

    /**
     * This method calculates the per diem amount for a given period of time at the rate provided.  The per diem amount is
     * calculated as described below.
     *
     * For same day trips:
     * - Per diem is equal to 1/2 of the per diem rate provided if the difference in time between the start and end time is
     * greater than 12 hours.  An additional 1/4 of a day is added back to the amount if the trip lasted past 7:00pm.
     * - If the same day trip is less than 12 hours, the per diem amount will be zero.
     *
     * For multiple day trips:
     * - Per diem amount is equal to the full rate times the number of full days of travel.  A full day is equal to any day
     * during the trip that is not the first day or last day of the trip.
     * - For the first day of the trip,
     *   if the travel starts before noon, you receive a full day per diem,
     *   if the travel starts between noon and 5:59pm, you get a half day per diem,
     *   if the travel starts after 6:00pm, you only receive a quarter day per diem
     * - For the last day of the trip,
     *   if the travel ends before 6:00am, you only receive a quarter day per diem,
     *   if the travel ends between 6:00am and noon, you receive a half day per diem,
     *   if the travel ends after noon, you receive a full day per diem
     *
     * @param stateDateTime The starting date and time of the period the per diem amount is calculated for.
     * @param endDateTime The ending date and time of the period the per diema mount is calculated for.
     * @param rate The per diem rate used to calculate the per diem amount.
     * @return The per diem amount for the period specified, at the rate given.
     *
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherTravelService#calculatePerDiemAmount(org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel)
     */
    @Override
    public KualiDecimal calculatePerDiemAmount(Timestamp startDateTime, Timestamp endDateTime, KualiDecimal rate) {
        KualiDecimal perDiemAmount = KualiDecimal.ZERO;
        KualiDecimal perDiemRate = new KualiDecimal(rate.doubleValue());

        // make sure we have the fields needed
        if (perDiemAmount == null || startDateTime == null || endDateTime == null) {
            LOG.error("Per diem amount, Start date/time, and End date/time must all be given.");
            throw new RuntimeException("Per diem amount, Start date/time, and End date/time must all be given.");
        }

        // check end time is after start time
        if (endDateTime.compareTo(startDateTime) <= 0) {
            LOG.error("End date/time must be after start date/time.");
            throw new RuntimeException("End date/time must be after start date/time.");
        }

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDateTime);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDateTime);

        double diffDays = KfsDateUtils.getDifferenceInDays(startDateTime, endDateTime);
        double diffHours = KfsDateUtils.getDifferenceInHours(startDateTime, endDateTime);

        // same day travel
        if (diffDays == 0) {
            // no per diem for only 12 hours or less
            if (diffHours > 12) {
                // half day of per diem
                perDiemAmount = perDiemRate.divide(new KualiDecimal(2));

                // add in another 1/4 of a day if end time past 7:00
                if (timeInPerDiemPeriod(endCalendar, 19, 0, 23, 59)) {
                    perDiemAmount = perDiemAmount.add(perDiemRate.divide(new KualiDecimal(4)));
                }
            }
        }

        // multiple days of travel
        else {
            // must at least have 7 1/2 hours to get any per diem
            if (diffHours >= 7.5) {
                // per diem for whole days
                perDiemAmount = perDiemRate.multiply(new KualiDecimal(diffDays - 1));

                // per diem for first day
                if (timeInPerDiemPeriod(startCalendar, 0, 0, 11, 59)) { // Midnight to noon
                    perDiemAmount = perDiemAmount.add(perDiemRate);
                }
                else if (timeInPerDiemPeriod(startCalendar, 12, 0, 17, 59)) { // Noon to 5:59pm
                    perDiemAmount = perDiemAmount.add(perDiemRate.divide(new KualiDecimal(2)));
                }
                else if (timeInPerDiemPeriod(startCalendar, 18, 0, 23, 59)) { // 6:00pm to Midnight
                    perDiemAmount = perDiemAmount.add(perDiemRate.divide(new KualiDecimal(4)));
                }

                // per diem for end day
                if (timeInPerDiemPeriod(endCalendar, 0, 1, 6, 0)) { // Midnight to 6:00am
                    perDiemAmount = perDiemAmount.add(perDiemRate.divide(new KualiDecimal(4)));
                }
                else if (timeInPerDiemPeriod(endCalendar, 6, 1, 12, 0)) { // 6:00am to noon
                    perDiemAmount = perDiemAmount.add(perDiemRate.divide(new KualiDecimal(2)));
                }
                else if (timeInPerDiemPeriod(endCalendar, 12, 01, 23, 59)) { // Noon to midnight
                    perDiemAmount = perDiemAmount.add(perDiemRate);
                }
            }
        }

        return perDiemAmount;
    }

    /**
     * Checks whether the date is in a per diem period given by the start hour and end hour and minutes.
     *
     * @param cal The date being checked to see if it occurred within the defined travel per diem period.
     * @param periodStartHour The starting hour of the per diem period.
     * @param periodStartMinute The starting minute of the per diem period.
     * @param periodEndHour The ending hour of the per diem period.
     * @param periodEndMinute The ending minute of the per diem period.
     * @return True if the date passed in occurred within the period defined by the given parameters, false otherwise.
     */
    protected boolean timeInPerDiemPeriod(Calendar cal, int periodStartHour, int periodStartMinute, int periodEndHour, int periodEndMinute) {
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        return (((hour > periodStartHour) || (hour == periodStartHour && minute >= periodStartMinute)) && ((hour < periodEndHour) || (hour == periodEndHour && minute <= periodEndMinute)));
    }

    /**
     * This method calculates the mileage amount based on the total mileage traveled and the using the reimbursement rate
     * applicable to when the trip started.
     *
     * For this method, a collection of mileage rates is retrieved, where each mileage rate is defined by a mileage limit.
     * This collection is iterated over to determine which mileage rate will be used for calculating the total mileage
     * amount due.
     *
     * @param totalMileage The total mileage traveled that will be reimbursed for.
     * @param travelStartDate The start date of the travel, which will be used to retrieve the mileage reimbursement rate.
     * @return The total reimbursement due to the traveler for the mileage traveled.
     *
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherTravelService#calculateMileageAmount(org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel)
     */
    @Override
    public KualiDecimal calculateMileageAmount(Integer totalMileage, Timestamp travelStartDate) {
        KualiDecimal mileageAmount = KualiDecimal.ZERO;

        if (totalMileage == null || travelStartDate == null) {
            LOG.error("Total Mileage and Travel Start Date must be given.");
            throw new RuntimeException("Total Mileage and Travel Start Date must be given.");
        }

        // convert timestamp to sql date
        Date effectiveDate = null;
        try {
            effectiveDate = dateTimeService.convertToSqlDate(travelStartDate);
        } catch (ParseException e) {
            LOG.error("Unable to parse travel start date into sql date " + travelStartDate, e);
            throw new RuntimeException("Unable to parse travel start date into sql date ", e);
        }

        // retrieve mileage rates
        Collection<TravelMileageRate> mileageRates = travelMileageRateDao.retrieveMostEffectiveMileageRates(effectiveDate);

        if (mileageRates == null || mileageRates.isEmpty()) {
            LOG.error("Unable to retreive mileage rates.");
            throw new RuntimeException("Unable to retreive mileage rates.");
        }

        int mileage = totalMileage.intValue();
        int mileageRemaining = mileage;

        /**
         * Iterate over mileage rates sorted in descending order by the mileage limit amount. For all miles over the mileage limit
         * amount, the rate times those number of miles over is added to the mileage amount.
         */
        for ( TravelMileageRate rate : mileageRates ) {
            int mileageLimitAmount = rate.getMileageLimitAmount().intValue();
            if (mileageRemaining > mileageLimitAmount) {
                BigDecimal numMiles = new BigDecimal(mileageRemaining - mileageLimitAmount);
                BigDecimal rateForMiles = numMiles.multiply(rate.getMileageRate()).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR);
                mileageAmount = mileageAmount.add(new KualiDecimal(rateForMiles));
                mileageRemaining = mileageLimitAmount;
            }

        }

        return mileageAmount;
    }

    /**
     * Gets the travelMileageRateDao attribute.
     * @return Returns the travelMileageRateDao.
     */
    public TravelMileageRateDao getTravelMileageRateDao() {
        return travelMileageRateDao;
    }

    /**
     * Sets the travelMileageRateDao attribute.
     * @param travelMileageRateDao The travelMileageRateDao to set.
     */
    public void setTravelMileageRateDao(TravelMileageRateDao travelMileageRateDao) {
        this.travelMileageRateDao = travelMileageRateDao;
    }

    /**
     * Gets the dateTimeService attribute.
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
