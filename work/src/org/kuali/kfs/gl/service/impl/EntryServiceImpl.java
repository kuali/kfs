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
package org.kuali.kfs.gl.service.impl;

import java.util.Map;

import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.dataaccess.EntryDao;
import org.kuali.kfs.gl.service.EntryService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of EntryService
 */
@Transactional
public class EntryServiceImpl implements EntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntryServiceImpl.class);

    private EntryDao entryDao;

    /**
     * Purge the entry table by year/chart
     * 
     * @param chart chart of entries to purge
     * @param year fiscal year of entries to purge
     * @see org.kuali.kfs.gl.service.EntryService#purgeYearByChart(java.lang.String, int)
     */
    public void purgeYearByChart(String chart, int year) {
        LOG.debug("purgeYearByChart() started");

        entryDao.purgeYearByChart(chart, year);
    }

    public void setEntryDao(EntryDao entryDao) {
        this.entryDao = entryDao;
    }

    /**
     * This method gets the number of GL entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @return the number of the open encumbrances
     * @see org.kuali.kfs.gl.service.EntryService#getEntryRecordCount(java.util.Map)
     */
    public Integer getEntryRecordCount(Map fieldValues) {
        return OJBUtility.getResultSizeFromMap(fieldValues, new Entry()).intValue();
    }
}
