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

import java.util.Iterator;
import java.util.Map;

import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.dao.EncumbranceDao;
import org.kuali.module.gl.service.EncumbranceService;

/**
 */
public class EncumbranceServiceImpl implements EncumbranceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceServiceImpl.class);

    private EncumbranceDao encumbranceDao;

    /**
     * @see org.kuali.module.gl.service.EncumbranceService#save(org.kuali.module.gl.bo.Encumbrance)
     */
    public void save(Encumbrance enc) {
        LOG.debug("save() started");

        encumbranceDao.save(enc);
    }

    /**
     * @see org.kuali.module.gl.service.EncumbranceService#purgeYearByChart(java.lang.String, int)
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        encumbranceDao.purgeYearByChart(chartOfAccountsCode, year);
    }

    /**
     * @see org.kuali.module.gl.service.EncumbranceService#getAllEncumbrances()
     */
    public Iterator getAllEncumbrances() {
        return encumbranceDao.getAllEncumbrances();
    }

    /**
     * Field accessor for EncumbranceDao
     * 
     * @param ed
     */
    public void setEncumbranceDao(EncumbranceDao ed) {
        encumbranceDao = ed;
    }

    /**
     * @see org.kuali.module.gl.service.EncumbranceService#getSummarizedEncumbrances(java.lang.String, boolean)
     */
    public Iterator getSummarizedEncumbrances(String documentTypeCode, boolean included) {
        return encumbranceDao.getSummarizedEncumbrances(documentTypeCode, included);
    }

    /**
     * @see org.kuali.module.gl.service.EncumbranceService#findOpenEncumbrance(java.util.Map)
     */
    public Iterator findOpenEncumbrance(Map fieldValues) {
        return encumbranceDao.findOpenEncumbrance(fieldValues);
    }

    /**
     * @see org.kuali.module.gl.service.EncumbranceService#getOpenEncumbranceCount(java.util.Map)
     */
    public Integer getOpenEncumbranceRecordCount(Map fieldValues) {
        return encumbranceDao.getOpenEncumbranceRecordCount(fieldValues);
    }
}
