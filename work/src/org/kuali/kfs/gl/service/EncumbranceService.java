/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group. Licensed under the Educational
 * Community License Version 1.0 (the "License"); By obtaining, using and/or copying this Original
 * Work, you agree that you have read, understand, and will comply with the terms and conditions of
 * the Educational Community License. You may obtain a copy of the License at:
 * http://kualiproject.org/license.html THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.kuali.module.gl.service;

import java.util.Iterator;
import java.util.Map;

import org.kuali.module.gl.bo.Encumbrance;

public interface EncumbranceService {
    /**
     * Save an Encumbrance entry
     * 
     * @param enc
     */
    public void save(Encumbrance enc);

    /**
     * Purge an entire fiscal year for a single chart.
     * 
     * @param chartOfAccountscode
     * @param year
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year);

    /**
     * fetch all encumbrance records from GL open encumbrance table
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
     * @return a collection of open encumbrances
     */
    public Iterator findOpenEncumbrance(Map fieldValues);
}
