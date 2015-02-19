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

import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.dataaccess.EncumbranceDao;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of EncumbranaceService
 */
@Transactional
public class EncumbranceServiceImpl implements EncumbranceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceServiceImpl.class);

    private EncumbranceDao encumbranceDao;

    /**
     * Saves an encumbrance
     *
     * @param enc an encumbrance to save
     * @see org.kuali.kfs.gl.service.EncumbranceService#save(org.kuali.kfs.gl.businessobject.Encumbrance)
     */
    public void save(Encumbrance enc) {
        LOG.debug("save() started");
        SpringContext.getBean(BusinessObjectService.class).save(enc);
    }

    /**
     * Removes all encumbrances from the database having a certain chart and fiscal year
     * @param chartOfAccountsCode the chart of encumbrances to purge
     * @param year the year of encumbrances to purge
     * @see org.kuali.kfs.gl.service.EncumbranceService#purgeYearByChart(java.lang.String, int)
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        encumbranceDao.purgeYearByChart(chartOfAccountsCode, year);
    }

    /**
     * Returns an iterator with all encumbrances from the database.
     * @return an Iterator of all encumbrances
     * @see org.kuali.kfs.gl.service.EncumbranceService#getAllEncumbrances()
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
     * group all encumbrances with/without the given document type code by fiscal year, chart, account, sub-account, object code,
     * sub object code, and balance type code, and summarize the encumbrance amount and the encumbrance close amount.
     *
     * @param documentTypeCode the given document type code
     * @param included indicate if all encumbrances with the given document type are included in the results or not
     * @see org.kuali.kfs.gl.service.EncumbranceService#getSummarizedEncumbrances(java.lang.String, boolean)
     */
    public Iterator getSummarizedEncumbrances(String documentTypeCode, boolean included) {
        return encumbranceDao.getSummarizedEncumbrances(documentTypeCode, included);
    }

    /**
     * Given the fieldValues, forms a query and finds the open encumbrances that match it
     * @param fieldValues the values to form an encumbrance query out of
     * @param includeZeroEncumbrances
     * @return an Iterator full of qualifying encumbrances
     * @see org.kuali.kfs.gl.service.EncumbranceService#findOpenEncumbrance(java.util.Map)
     */
    public Iterator findOpenEncumbrance(Map fieldValues, boolean includeZeroEncumbrances) {
        return encumbranceDao.findOpenEncumbrance(fieldValues, includeZeroEncumbrances);
    }

    /**
     * Returns the count of all open encumbrances in the database, matching the given field values
     * @param fieldValues the field values to build an encumbrance query out of
     * @param includeZeroEncumbrances
     * @return the number of qualifying open encumbrances
     * @see org.kuali.kfs.gl.service.EncumbranceService#getOpenEncumbranceCount(java.util.Map)
     */
    public Integer getOpenEncumbranceRecordCount(Map fieldValues, boolean includeZeroEncumbrances) {
        return encumbranceDao.getOpenEncumbranceRecordCount(fieldValues, includeZeroEncumbrances);
    }

    /**
     * Finds open encumbrances that have the keys given in the map summarized by balance type codes
     * where the sum(ACCOUNT_LINE_ENCUMBRANCE_AMOUNT  - sum(ACCOUNT_LINE_ENCUMBRANCE_CLOSED_AMOUNT ) != 0
     * and returns true if there are any results.
     *
     * @param fieldValues the input fields and values
     * @param includeZeroEncumbrances
     * @return true if there any open encumbrances when summarized by balance type
     * @see org.kuali.kfs.gl.dataaccess.EncumbranceDao#hasSummarizedOpenEncumbranceRecords(java.util.Map)
     */
    public boolean hasSummarizedOpenEncumbranceRecords(Map fieldValues, boolean includeZeroEncumbrances) {
        return encumbranceDao.hasSummarizedOpenEncumbranceRecords(fieldValues, includeZeroEncumbrances);
    }
}
