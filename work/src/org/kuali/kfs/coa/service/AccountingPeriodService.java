/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.service;

import java.sql.Date;
import java.util.Collection;

import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * This service interface defines methods necessary for retreiving fully populated AccountingPeriod business objects from the
 * database that are necessary for transaction processing in the application.
 * 
 * @author Kuali Financial Transactions Red Team ()
 */
public interface AccountingPeriodService {
    /**
     * This method retrieves all valid accounting periods in the system.
     * 
     * @return A list of accounting periods in Kuali.
     */
    public Collection getAllAccountingPeriods();

    /**
     * This method retrieves a list of all open accounting periods in the system.
     * 
     * @return
     */
    public Collection getOpenAccountingPeriods();

    /**
     * 
     * This method retrieves an individual AccountingPeriod based on the period and fiscal year
     * 
     * @param periodCode
     * @param fiscalYear
     * @return an accounting period
     */
    public AccountingPeriod getByPeriod(String periodCode, Integer fiscalYear);

    /**
     * This method takes a date and returns the corresponding period
     * 
     * @param date
     * @return period that matches the date
     */
    public AccountingPeriod getByDate(Date date);
}
