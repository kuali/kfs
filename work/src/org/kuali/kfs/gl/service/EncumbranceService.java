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
package org.kuali.kfs.gl.service;

import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Encumbrance;

/**
 * An interface declaring services dealing with encumbrances
 */
public interface EncumbranceService {
    /**
     * Save an Encumbrance entry
     *
     * @param enc an encumbrance entry
     */
    public void save(Encumbrance enc);

    /**
     * Purge an entire fiscal year for a single chart.
     *
     * @param chartOfAccountsCode the chart of encumbrances to purge
     * @param year the year of encumbrances to purage
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year);

    /**
     * Fetch all encumbrance records from GL open encumbrance table.  Based on test data, there's only
     * about a third as many encumbrances as there are, say, balances, so unless your institution is huge,
     * it's probably safe to call this method.
     * @return an Iterator of encumbrances
     */
    public Iterator getAllEncumbrances();

    /**
     * group all encumbrances with/without the given document type code by fiscal year, chart, account, sub-account, object code,
     * sub object code, and balance type code, and summarize the encumbrance amount and the encumbrance close amount.
     *
     * @param documentTypeCode the given document type code
     * @param included indicate if all encumbrances with the given document type are included in the results or not
     */
    public Iterator getSummarizedEncumbrances(String documentTypeCode, boolean included);

    /**
     * This method finds the open encumbrances according to input fields and values
     *
     * @param fieldValues the input fields and values
     * @param includeZeroEncumbrances should the query include encumbrances which have zeroed out?
     * @return a collection of open encumbrances
     */
    public Iterator findOpenEncumbrance(Map fieldValues, boolean includeZeroEncumbrances);

    /**
     * This method gets the number of the open encumbrances according to input fields and values
     *
     * @param fieldValues the input fields and values
     * @param includeZeroEncumbrances should the query include encumbrances which have zeroed out?
     * @return the number of the open encumbrances
     */
    public Integer getOpenEncumbranceRecordCount(Map fieldValues, boolean includeZeroEncumbrances);

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
    public boolean hasSummarizedOpenEncumbranceRecords(Map fieldValues, boolean includeZeroEncumbrances);

}
