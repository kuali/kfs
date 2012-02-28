/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess;

import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * A DAO interface that declares methods needed for Encumbrances to interact with the database
 */
public interface EncumbranceDao {
    /**
     * Returns an encumbrance that would be affected by the given transaction
     * 
     * @param t the transaction to find the affected encumbrance for
     * @return an Encumbrance that would be affected by the posting of the transaction, or null
     */
    public Encumbrance getEncumbranceByTransaction(Transaction t);

    /**
     * Returns an Iterator of all encumbrances that need to be closed for the fiscal year
     * 
     * @param fiscalYear a fiscal year to find encumbrances for
     * @return an Iterator of encumbrances to close
     */
    public Iterator getEncumbrancesToClose(Integer fiscalYear);

    /**
     * Purges the database of all those encumbrances with the given chart and year 
     * 
     * @param chartOfAccountsCode the chart of accounts code purged encumbrances will have
     * @param year the university fiscal year purged encumbrances will have
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year);

    /**
     * fetch all encumbrance records from GL open encumbrance table
     * 
     * @return an Iterator with all encumbrances currently in the database
     */
    public Iterator getAllEncumbrances();

    /**
     * group all encumbrances with/without the given document type code by fiscal year, chart, account, sub-account, object code,
     * sub object code, and balance type code, and summarize the encumbrance amount and the encumbrance close amount.
     * 
     * @param documentTypeCode the given document type code
     * @param included indicate if all encumbrances with the given document type are included in the results or not
     * @return an Iterator of arrays of java.lang.Objects holding summarization data about qualifying encumbrances 
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
     * Counts the number of the open encumbrances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param includeZeroEncumbrances should the query include encumbrances which have zeroed out?
     * @return the number of the open encumbrances
     */
    public Integer getOpenEncumbranceRecordCount(Map fieldValues, boolean includeZeroEncumbrances);
    
    /**
     * @param year the given university fiscal year
     * @return count of rows for the given fiscal year
     */
    public Integer findCountGreaterOrEqualThan(Integer year);
}
