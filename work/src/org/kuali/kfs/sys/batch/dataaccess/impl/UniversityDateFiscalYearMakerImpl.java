/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.dataaccess.impl;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Performs custom fiscal year process for University Date records
 */
public class UniversityDateFiscalYearMakerImpl extends FiscalYearMakerImpl {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(UniversityDateFiscalYearMakerImpl.class);

    /**
     * @see org.kuali.kfs.coa.batch.dataaccess.impl.FiscalYearMakerHelperImpl#performCustomProcessing(java.lang.Integer)
     */
    @Override
    public void performCustomProcessing(Integer baseFiscalYear, boolean firstCopyYear) {
        int fiscalYearStartMonth = getFiscalYearStartMonth(baseFiscalYear);

        // determine start date year, if start month is not January the year will be one behind the fiscal year
        int startDateYear = baseFiscalYear;
        if (Calendar.JANUARY == fiscalYearStartMonth) {
            startDateYear += 1;
        }
        getPersistenceBrokerTemplate();
        // start with first day of fiscal year and create records for each year up to end date
        GregorianCalendar univPeriodDate = new GregorianCalendar(startDateYear, fiscalYearStartMonth, 1);

        // setup end date
        GregorianCalendar enddate = new GregorianCalendar(univPeriodDate.get(Calendar.YEAR), univPeriodDate.get(Calendar.MONTH), univPeriodDate.get(Calendar.DAY_OF_MONTH));
        enddate.add(Calendar.MONTH, 12);
        enddate.add(Calendar.DAY_OF_MONTH, -1);

        // the fiscal year is always the year of the ending date of the fiscal year
        Integer nextFiscalYear = enddate.get(Calendar.YEAR);

        // get rid of any records already existing for next fiscal year
        deleteNewYearRows(nextFiscalYear);

        // initialize the period variables
        int period = 1;
        String periodString = String.format("%02d", period);
        int compareMonth = univPeriodDate.get(Calendar.MONTH);
        int currentMonth = compareMonth;

        // loop through the dates until we are past end date
        while (univPeriodDate.compareTo(enddate) <= 0) {
            // if we hit period 13 something went wrong
            if (period == 13) {
                LOG.error("Hit period 13 while creating university date records");
                throw new RuntimeException("Hit period 13 while creating university date records");
            }
            
            // create the university date record
            UniversityDate universityDate = new UniversityDate();
            universityDate.setUniversityFiscalYear(nextFiscalYear);
            universityDate.setUniversityDate(new Date(univPeriodDate.getTimeInMillis()));
            universityDate.setUniversityFiscalAccountingPeriod(periodString);

            businessObjectService.save(universityDate);

            // add one to day for the next record
            univPeriodDate.add(Calendar.DAY_OF_MONTH, 1);

            // does this kick us into a new month and therefore a new accounting period?
            compareMonth = univPeriodDate.get(Calendar.MONTH);
            if (currentMonth != compareMonth) {
                period = period + 1;
                periodString = String.format("%02d", period);
                currentMonth = compareMonth;
            }
        }
    }

    /**
     * Removes all UniversityDate records for the given fiscal year
     * 
     * @param requestYear year to delete records for
     */
    protected void deleteNewYearRows(Integer requestYear) {
        businessObjectService.deleteMatching(UniversityDate.class, Collections.singletonMap(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, requestYear));

        LOG.warn(String.format("\n rows for %d deleted", requestYear));
    }

    /**
     * Retrieves the system options record for the base fiscal year to determine the fiscal year start month
     * 
     * @param baseFiscalYear fiscal year to retrieve options record for
     * @return int fiscal year start month (0 being Jan)
     */
    protected int getFiscalYearStartMonth(Integer baseFiscalYear) {
        SystemOptions systemOptions = new SystemOptions();
        systemOptions.setUniversityFiscalYear(baseFiscalYear);

        SystemOptions foundOptions = (SystemOptions) businessObjectService.retrieve(systemOptions);
        if (foundOptions == null) {
            LOG.error("Unable to retrieve system options record for fiscal year " + baseFiscalYear);
            throw new RuntimeException("Unable to retrieve system options record for fiscal year " + baseFiscalYear);
        }

        Integer fiscalYearStartMonth = Integer.parseInt(foundOptions.getUniversityFiscalYearStartMo());

        return fiscalYearStartMonth - 1;
    }

    /**
     * @see org.kuali.kfs.coa.batch.dataaccess.impl.FiscalYearMakerHelperImpl#doCustomProcessingOnly()
     */
    @Override
    public boolean doCustomProcessingOnly() {
        return true;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
