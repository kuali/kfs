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
package org.kuali.kfs.gl.service.impl;

import java.sql.Date;

import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.dataaccess.CollectorDetailDao;
import org.kuali.kfs.gl.service.CollectorDetailService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of CollectorDetailService
 */
@Transactional
public class CollectorDetailServiceImpl implements CollectorDetailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorDetailServiceImpl.class);

    private CollectorDetailDao collectorDetailDao;

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart chart of CollectorDetails to purge
     * @param year year of CollectorDetails to purage
     * @see org.kuali.kfs.gl.service.CollectorDetailService#purgeYearByChart(java.lang.String, int)
     */
    public void purgeYearByChart(String chartOfAccountsCode, int universityFiscalYear) {
        LOG.debug("purgeYearByChart() started");

        collectorDetailDao.purgeYearByChart(chartOfAccountsCode, universityFiscalYear);
    }
    
    public Integer getNextCreateSequence(Date date) {
        return collectorDetailDao.getMaxCreateSequence(date);
    }

    /**
     * Saves a CollectorDetail
     * 
     * @param detail the detail to save
     * @see org.kuali.kfs.gl.service.CollectorDetailService#save(org.kuali.kfs.gl.businessobject.CollectorDetail)
     */
    public void save(CollectorDetail detail) {
        LOG.debug("save() started");
        SpringContext.getBean(BusinessObjectService.class).save(detail);
    }

    public void setCollectorDetailDao(CollectorDetailDao idbd) {
        collectorDetailDao = idbd;
    }
}
