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
package org.kuali.kfs.gl.dataaccess;

import java.util.Collection;

/**
 * A DAO interface declaring methods needed to help SufficientFundBalance records interact with the database
 */
public interface SufficientFundBalancesDao {
    /**
     * Fetches sufficient fund balances based on given keys of fiscal year, chart code, and object code
     * 
     * @param universityFiscalYear the university fiscal year of sufficient fund balances to find
     * @param chartOfAccountsCode the chart of accounts code of sufficient fund balances to find
     * @param financialObjectCode the object code of sufficient fund balances to find
     * @return a Collection of sufficient fund balances, qualified by the parameter values
     */
    public Collection getByObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);

    /**
     * Deletes sufficient fund balances associated with a given year, chart, and account number
     * 
     * @param universityFiscalYear the university fiscal year of sufficient fund balances to delete
     * @param chartOfAccountsCode the chart code of sufficient fund balances to delete
     * @param accountNumber the account number of sufficient fund balances to delete
     * @return the number of records deleted
     */
    public int deleteByAccountNumber(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    /**
     * This method should only be used in unit tests. It loads all the gl_sf_balances_t rows in memory into a collection. This won't
     * sace for production.
     * 
     * @return a Collection with all sufficient funds balances in the database
     */
    public Collection testingGetAllEntries();
}
