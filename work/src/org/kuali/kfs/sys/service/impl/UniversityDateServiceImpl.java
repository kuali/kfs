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
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.Timer;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UniversityDateServiceImpl implements UniversityDateService {

    private static final Logger LOG = Logger.getLogger(UniversityDateServiceImpl.class);

    private UniversityDateDao universityDateDao;
    
    public UniversityDate getCurrentUniversityDate() {
        LOG.debug("getCurrentUniversityDate() started");
        java.util.Date now = SpringServiceLocator.getDateTimeService().getCurrentDate();

        return universityDateDao.getByPrimaryKey(DateUtils.clearTimeFields(now));
    }

    /**
     * @see org.kuali.core.service.DateTimeService#getCurrentFiscalYear()
     */
    public Integer getCurrentFiscalYear() {
        Timer t0 = new Timer("getCurrentFiscalYear");
        java.util.Date now = SpringServiceLocator.getDateTimeService().getCurrentDate();

        Integer result = getFiscalYear(DateUtils.clearTimeFields(now));
        t0.log();
        return result;
    }
    
    /**
     * @see org.kuali.core.service.DateTimeService#getFiscalYear(java.util.Date)
     */
    public Integer getFiscalYear(java.util.Date date) {
        if (date == null) {
            throw new IllegalArgumentException("invalid (null) date");
        }

        UniversityDate uDate = universityDateDao.getByPrimaryKey(date);
        return (uDate == null) ? null : uDate.getUniversityFiscalYear();
    }

    public java.util.Date getFirstDateOfFiscalYear(Integer fiscalYear) {
        UniversityDate uDate = universityDateDao.getFirstFiscalYearDate(fiscalYear);
        return (uDate == null) ? null : uDate.getUniversityDate();
    }

    public java.util.Date getLastDateOfFiscalYear(Integer fiscalYear) {
        UniversityDate uDate = universityDateDao.getLastFiscalYearDate(fiscalYear);
        return (uDate == null) ? null : uDate.getUniversityDate();
    }
    
  public void setUniversityDateDao(UniversityDateDao universityDateDao) {
  this.universityDateDao = universityDateDao;
}
    
}
