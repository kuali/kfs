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
package org.kuali.kfs.coa.service;

import java.sql.Date;
import java.util.Collection;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;

/**
 * This service interface defines methods necessary for retrieving fully populated AccountingPeriod business objects from the
 * database that are necessary for transaction processing in the application.
 */
public interface AccountingPeriodService {
    /**
     * This method retrieves all valid accounting periods in the system.
     *
     * @return A list of accounting periods in Kuali.
     */
    public Collection<AccountingPeriod> getAllAccountingPeriods();

    /**
     * This method retrieves a list of all open accounting periods in the system.
     *
     * @return
     */
    public Collection<AccountingPeriod> getOpenAccountingPeriods();

    /**
     * This method retrieves an individual AccountingPeriod based on the period and fiscal year
     *
     * @param periodCode
     * @param fiscalYear
     * @return an accounting period
     */
    public AccountingPeriod getByPeriod(String periodCode, Integer fiscalYear);


    /**
     * This method allows for AccountingPeriod retrieval via String date.
     *
     * @param String
     */
    public AccountingPeriod getByStringDate(String dateString);

    /**
     * This method takes a date and returns the corresponding period
     *
     * @param date
     * @return period that matches the date
     */
    public AccountingPeriod getByDate(Date date);

    /**
     * This method compares two accounting periods, hopefully by comparing their closing dates. If a is earlier than b, it should
     * return a negative number; if a is later, it should return a positive number; and if the closing dates are equal, it should
     * return a 0.
     *
     * @param a the first accounting period to compare
     * @param b the second accounting period to compare
     * @return an integer representing which is earlier or later, or if they occur simultaneously
     */
    public int compareAccountingPeriodsByDate(AccountingPeriod a, AccountingPeriod b);

    public void clearCache();
}
