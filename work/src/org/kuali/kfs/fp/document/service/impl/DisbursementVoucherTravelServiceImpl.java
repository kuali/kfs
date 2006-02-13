/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.TravelMileageRate;
import org.kuali.module.financial.service.DisbursementVoucherTravelService;

/**
 * Performs calculations of travel per diem and mileage amounts.
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherTravelServiceImpl implements DisbursementVoucherTravelService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherTravelServiceImpl.class);

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTravelService#calculatePerDiemAmount(org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeTravel)
     */
    public KualiDecimal calculatePerDiemAmount(Timestamp startDateTime, Timestamp endDateTime, KualiDecimal perDiemRate) {
        KualiDecimal perDiemAmount = new KualiDecimal(0);

        // make sure we have the fields neede
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

        // First, get difference in whole days
        Calendar startCompare = Calendar.getInstance();
        startCompare.setTime(startDateTime);
        startCompare.set(Calendar.HOUR_OF_DAY, 0);
        startCompare.set(Calendar.MINUTE, 0);

        Calendar endCompare = Calendar.getInstance();
        endCompare.setTime(endDateTime);
        endCompare.set(Calendar.HOUR_OF_DAY, 0);
        endCompare.set(Calendar.MINUTE, 0);

        long diffDays = (endCompare.getTimeInMillis() - startCompare.getTimeInMillis()) / (24 * 60 * 60 * 1000);

        long diffHours = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / (60 * 60 * 1000);

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
                if (timeInPerDiemPeriod(startCalendar, 0, 0, 11, 59)) {
                    perDiemAmount = perDiemAmount.add(perDiemRate);
                }
                else if (timeInPerDiemPeriod(startCalendar, 12, 0, 17, 59)) {
                    perDiemAmount = perDiemAmount.add(perDiemRate.divide(new KualiDecimal(2)));
                }
                else if (timeInPerDiemPeriod(startCalendar, 18, 0, 23, 59)) {
                    perDiemAmount = perDiemAmount.add(perDiemRate.divide(new KualiDecimal(4)));
                }

                // per diem for end day
                if (timeInPerDiemPeriod(endCalendar, 0, 1, 6, 0)) {
                    perDiemAmount = perDiemAmount.add(perDiemRate.divide(new KualiDecimal(4)));
                }
                else if (timeInPerDiemPeriod(endCalendar, 6, 1, 12, 0)) {
                    perDiemAmount = perDiemAmount.add(perDiemRate.divide(new KualiDecimal(2)));
                }
                else if (timeInPerDiemPeriod(endCalendar, 12, 01, 23, 59)) {
                    perDiemAmount = perDiemAmount.add(perDiemRate);
                }
            }
        }

        return perDiemAmount;
    }

    /**
     * Checks whether the date is in a per diem period given by the start hour and end hour and minutes.
     * @param cal
     * @return
     */
    private boolean timeInPerDiemPeriod(Calendar cal, int periodStartHour, int periodStartMinute, int periodEndHour,
            int periodEndMinute) {
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        return (((hour > periodStartHour) || (hour == periodStartHour && minute >= periodStartMinute)) && ((hour < periodEndHour) || (hour == periodEndHour && minute <= periodEndMinute)));
    }

    /**
     * @see org.kuali.module.financial.service.DisbursementVoucherTravelService#calculateMileageAmount(org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeTravel)
     */
    public KualiDecimal calculateMileageAmount(Integer totalMileage) {
        KualiDecimal mileageAmount = new KualiDecimal(0);

        if (totalMileage == null) {
            LOG.error("Total Mileage must be given");
            throw new RuntimeException("Total Mileage must be given");
        }

        // retrieve mileage rates for this fiscal year
        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", SpringServiceLocator.getDateTimeService().getCurrentFiscalYear());
        List mileageRates = (List) SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(TravelMileageRate.class,
                criteria, "mileageLimitAmount", false);

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
        for (Iterator iter = mileageRates.iterator(); iter.hasNext();) {
            TravelMileageRate rate = (TravelMileageRate) iter.next();
            int mileageLimitAmount = rate.getMileageLimitAmount().intValue();
            if (mileageRemaining > mileageLimitAmount) {
                BigDecimal numMiles = new BigDecimal(mileageRemaining - mileageLimitAmount);
                mileageAmount = mileageAmount.add(new KualiDecimal(numMiles.multiply(rate.getMileageRate())));
                mileageRemaining = mileageLimitAmount;
            }

        }

        return mileageAmount;
    }

}