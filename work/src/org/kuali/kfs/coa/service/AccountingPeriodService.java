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
