/*
 * Copyright 2006 The Kuali Foundation.
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

import java.util.Collection;

import org.kuali.module.gl.bo.SufficientFundRebuild;

/**
 * 
 * This class...
 */
public interface SufficientFundRebuildDao {
    /**
     * Returns all sufficient fund rebuild balances in the database
     * 
     * @return a Collection with all sufficient fund rebuild balances
     */
    public Collection getAll();

    /**
     * Returns all sufficient fund rebuild balances with a given object type code
     * 
     * @param accountFinancialObjectTypeCode the object type code of sufficient fund balances to return
     * @return a Collection of qualifying sufficient fund balances
     */
    public Collection getByType(String accountFinancialObjectTypeCode);

    /**
     * Returns the sufficient fund rebuild balance by chart and account number/object code
     * 
     * @param chartOfAccountsCode the chart of the rebuild balance to return
     * @param accountNumberFinancialObjectCode the account number or object code of the rebuild balance to returnd
     * @return a qualifying sufficient fund rebuild record if found in the database, or null
     */
    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode);

    /**
     * Returns the sufficient fund rebuild balance with the primary key given by the parameters 
     * 
     * @param chartOfAccountsCode the chart of the rebuild balance to return
     * @param accountFinancialObjectTypeCode the object type code of the rebuild balance to return
     * @param accountNumberFinancialObjectCode the account number or fiscal object of the rebuild balance to return
     * @return the qualifying rebuild balance, or null if not found in the database
     */
    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode);

    /**
     * Saves a sufficient funds rebuild records to the database
     * 
     * @param sfrb the sufficient fund rebuild balance to save
     */
    public void save(SufficientFundRebuild sfrb);

    /**
     * Deletes a sufficient fund rebuild balance
     * @param sfrb the sufficient fund rebuild balance to delete
     */
    public void delete(SufficientFundRebuild sfrb);

    /**
     * This method should only be used in unit tests. It loads all the gl_sf_rebuild_t rows in memory into a collection. This won't
     * sace for production.
     * 
     * @return a Collection of all sufficient fund rebuild balances in the database
     */
    public Collection testingGetAllEntries();
}
