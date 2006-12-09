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

import org.kuali.module.gl.bo.SufficientFundBalances;

/**
 * 
 * 
 */
public interface SufficientFundBalancesDao {
    /**
     * 
     * This method...
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param financialObjectCode
     * @return
     */
    public Collection getByObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);

    /**
     * 
     * This method...
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     */
    public void deleteByAccountNumber(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    /**
     * 
     * This method...
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param financialObjectCode
     * @return
     */
    public SufficientFundBalances getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode);

    /**
     * Save a sufficient funds balance
     * 
     * @param sfb
     */
    public void save(SufficientFundBalances sfb);

    /**
     * This method should only be used in unit tests. It loads all the gl_sf_balances_t rows in memory into a collection. This won't
     * sace for production.
     * 
     * @return
     */
    public Collection testingGetAllEntries();
}
