/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.util.Map;

import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.dao.EntryDao;
import org.kuali.module.gl.service.EntryService;
import org.kuali.module.gl.util.OJBUtility;

public class EntryServiceImpl implements EntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntryServiceImpl.class);

    private EntryDao entryDao;

    public void purgeYearByChart(String chart, int year) {
        LOG.debug("purgeYearByChart() started");

        entryDao.purgeYearByChart(chart, year);
    }

    public void setEntryDao(EntryDao entryDao) {
        this.entryDao = entryDao;
    }

    /**
     * @see org.kuali.module.gl.service.EntryService#getEntryRecordCount(java.util.Map)
     */
    public Integer getEntryRecordCount(Map fieldValues) {
        return OJBUtility.getResultSizeFromMap(fieldValues, new Entry()).intValue();
    }
}
