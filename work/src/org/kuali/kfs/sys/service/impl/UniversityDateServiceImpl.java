/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.financial.service.impl;

import org.apache.log4j.Logger;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.spring.CacheNoCopy;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;

/**
 * 
 * This is the default implementation of the UniversityDateService interface.
 */

@NonTransactional
public class UniversityDateServiceImpl implements UniversityDateService {

    private static final Logger LOG = Logger.getLogger(UniversityDateServiceImpl.class);

    private UniversityDateDao universityDateDao;
    private DateTimeService dateTimeService;
    
    /**
     * This method retrieves a UniversityDate object using today's date to create the instance.
     * 
     * @return A UniversityDate instance representing today's date.
     * 
     * @see org.kuali.module.financial.service.UniversityDateService#getCurrentUniversityDate()
     */
    public UniversityDate getCurrentUniversityDate() {
        LOG.debug("getCurrentUniversityDate() started");
        java.util.Date now = dateTimeService.getCurrentDate();

        return universityDateDao.getByPrimaryKey(DateUtils.clearTimeFields(now));
    }

    /**
     * This method retrieves the current fiscal year using today's date.
     * 
     * @return The current fiscal year as an Integer.
     * 
     * @see org.kuali.core.service.DateTimeService#getCurrentFiscalYear()
     */
    public Integer getCurrentFiscalYear() {
        //Timer t0 = new Timer("getCurrentFiscalYear");
        java.util.Date now = dateTimeService.getCurrentDate();

        Integer result = getFiscalYear(DateUtils.clearTimeFields(now));
        //t0.log();
        return result;
    }
    
    /**
     * This method retrieves the fiscal year associated with the date provided.
     * 
     * @param date The date to be used for retrieving the associated fiscal year.
     * @return The fiscal year that the date provided falls within.
     * 
     * @see org.kuali.core.service.DateTimeService#getFiscalYear(java.util.Date)
     */
    @CacheNoCopy
    public Integer getFiscalYear(java.util.Date date) {
        if (date == null) {
            throw new IllegalArgumentException("invalid (null) date");
        }

        UniversityDate uDate = universityDateDao.getByPrimaryKey(date);
        return (uDate == null) ? null : uDate.getUniversityFiscalYear();
    }

    /**
     * This method retrieves the first date of the fiscal year provided.
     * 
     * @param fiscalYear The fiscal year to retrieve the first date for.
     * @return A Date object representing the first date of the fiscal year given.
     * 
     * @see org.kuali.module.financial.service.UniversityDateService#getFirstDateOfFiscalYear(java.lang.Integer)
     */
    @CacheNoCopy
    public java.util.Date getFirstDateOfFiscalYear(Integer fiscalYear) {
        UniversityDate uDate = universityDateDao.getFirstFiscalYearDate(fiscalYear);
        return (uDate == null) ? null : uDate.getUniversityDate();
    }

    /**
     * This method retrieves the last date of the fiscal year provided.
     * 
     * @param fiscalYear The fiscal year to retrieve the last date for.
     * @return A Date object representing the last date of the fiscal year given.
     * 
     * @see org.kuali.module.financial.service.UniversityDateService#getLastDateOfFiscalYear(java.lang.Integer)
     */
    @CacheNoCopy
    public java.util.Date getLastDateOfFiscalYear(Integer fiscalYear) {
        UniversityDate uDate = universityDateDao.getLastFiscalYearDate(fiscalYear);
        return (uDate == null) ? null : uDate.getUniversityDate();
    }
    
    /**
     * Sets the universityDateDao attribute value.
     * @param universityDateDao The universityDateDao to set.
     */
    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
}
