/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.gl.dao;

import java.util.Iterator;
import java.util.Map;

import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.Transaction;

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
     * Saves an encumbrance to the database
     * 
     * @param e an encumbrance to save
     */
    public void save(Encumbrance e);

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
     * @return a collection of open encumbrances
     */
    public Iterator findOpenEncumbrance(Map fieldValues);

    /**
     * Counts the number of the open encumbrances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @return the number of the open encumbrances
     */
    public Integer getOpenEncumbranceRecordCount(Map fieldValues);
}
