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
package org.kuali.module.gl.service;

import java.util.Collection;

import org.kuali.module.gl.bo.SufficientFundRebuild;

/**
 * An interface which declares methods needed to rebuild sufficient funds balances
 */
public interface SufficientFundRebuildService {
    /**
     * Returns all sufficient funds records in the persistence store
     * 
     * @return a Collection of all sufficient fund rebuild records
     */
    public Collection getAll();

    /**
     * Returns all sufficient fund rebuild records using account numbers
     * 
     * @return a Collection of sufficient fund rebuild records
     */
    public Collection getAllAccountEntries();

    /**
     * Returns all sufficient fund rebuild records using object codes
     * 
     * @return a Collection of sufficient fund rebuild records
     */
    public Collection getAllObjectEntries();

    /**
     * Returns a sufficient fund rebuild record given the parameters as keys
     * 
     * @param chartOfAccountsCode the chart of the record to return
     * @param accountNumberFinancialObjectCode either an account number or an object code of the sufficient fund rebuild record to return
     * @return the qualifying sufficient fund rebuild record, or null if not found
     */
    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode);

    /**
     * Returns a sufficient fund rebuild record, based on the given keys
     * 
     * @param chartOfAccountsCode the chart of the sufficient fund rebuild record to return
     * @param accountFinancialObjectTypeCode if the record has an object code, the object code of the sufficient fund rebuild record to return
     * @param accountNumberFinancialObjectCode if the record has an account number, the account number of the sufficient fund rebuild record to return
     * @return the qualifying sufficient fund rebuild record, or null if not found
     */
    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode);

    /**
     * Saves a sufficient fund rebuild record to the persistence store
     * 
     * @param sfrb the sufficient fund rebuild record to save
     */
    public void save(SufficientFundRebuild sfrb);

    /**
     * Deletes a SufficientFundRebuild record from the persistence store 
     *
     * @param sfrb the sufficient fund rebuild record to delete
     */
    public void delete(SufficientFundRebuild sfrb);
}
