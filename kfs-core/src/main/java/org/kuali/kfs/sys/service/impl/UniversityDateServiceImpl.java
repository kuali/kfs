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
package org.kuali.kfs.sys.service.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

/**
 *
 * This is the default implementation of the UniversityDateService interface.
 */

@NonTransactional
public class UniversityDateServiceImpl implements UniversityDateService {

    private static final Logger LOG = Logger.getLogger(UniversityDateServiceImpl.class);

    protected UniversityDateDao universityDateDao;
    protected DateTimeService dateTimeService;

    /**
     * This method retrieves a UniversityDate object using today's date to create the instance.
     *
     * @return A UniversityDate instance representing today's date.
     *
     * @see org.kuali.kfs.sys.service.UniversityDateService#getCurrentUniversityDate()
     */
    @Override
    public UniversityDate getCurrentUniversityDate() {
        java.util.Date now = dateTimeService.getCurrentDate();
        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date( KfsDateUtils.clearTimeFields(now).getTime() ));
    }

    /**
     * This method retrieves the current fiscal year using today's date.
     *
     * @return The current fiscal year as an Integer.
     *
     * @see org.kuali.rice.core.api.datetime.DateTimeService#getCurrentFiscalYear()
     */
    @Override
    public Integer getCurrentFiscalYear() {
        java.util.Date now = dateTimeService.getCurrentDate();

        return getFiscalYear(KfsDateUtils.clearTimeFields(now));
    }

    /**
     * This method retrieves the fiscal year associated with the date provided.
     *
     * @param date The date to be used for retrieving the associated fiscal year.
     * @return The fiscal year that the date provided falls within.
     *
     * @see org.kuali.rice.core.api.datetime.DateTimeService#getFiscalYear(java.util.Date)
     */
    @Override
    @Cacheable(value=UniversityDate.CACHE_NAME, key="'{FiscalYear}'+#p0")
    public Integer getFiscalYear(java.util.Date date) {
        if (date == null) {
            throw new IllegalArgumentException("invalid (null) date");
        }
        UniversityDate uDate = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date( KfsDateUtils.clearTimeFields(date).getTime() ) );
        return (uDate == null) ? null : uDate.getUniversityFiscalYear();
    }

    /**
     * This method retrieves the first date of the fiscal year provided.
     *
     * @param fiscalYear The fiscal year to retrieve the first date for.
     * @return A Date object representing the first date of the fiscal year given.
     *
     * @see org.kuali.kfs.sys.service.UniversityDateService#getFirstDateOfFiscalYear(java.lang.Integer)
     */
    @Override
    @Cacheable(value=UniversityDate.CACHE_NAME, key="'{FirstDateOfFiscalYear}'+#p0")
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
     * @see org.kuali.kfs.sys.service.UniversityDateService#getLastDateOfFiscalYear(java.lang.Integer)
     */
    @Override
    @Cacheable(value=UniversityDate.CACHE_NAME, key="'{LastDateOfFiscalYear}'+#p0")
    public java.util.Date getLastDateOfFiscalYear(Integer fiscalYear) {
        UniversityDate uDate = universityDateDao.getLastFiscalYearDate(fiscalYear);
        return (uDate == null) ? null : uDate.getUniversityDate();
    }

    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
