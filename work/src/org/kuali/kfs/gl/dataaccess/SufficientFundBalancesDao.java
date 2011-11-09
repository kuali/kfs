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
