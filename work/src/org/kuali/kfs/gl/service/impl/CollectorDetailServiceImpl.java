/*
 * Copyright 2007 The Kuali Foundation
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
