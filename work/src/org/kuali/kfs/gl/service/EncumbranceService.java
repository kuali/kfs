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
}
