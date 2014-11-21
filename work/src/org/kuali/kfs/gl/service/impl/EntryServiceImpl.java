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
