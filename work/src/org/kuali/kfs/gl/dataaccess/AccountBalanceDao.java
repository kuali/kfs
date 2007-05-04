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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.Transaction;

/**
 * 
 * 
 */
public interface AccountBalanceDao {
    public AccountBalance getByTransaction(Transaction t);

    public void save(AccountBalance ab);

    /**
     * This method finds the available account balances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @return the summary records of account balance entries
     */
    public Iterator findConsolidatedAvailableAccountBalance(Map fieldValues);

    /**
     * This method finds the available account balances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @return account balance entries
     */
    public Iterator findAvailableAccountBalance(Map fieldValues);

    /**
     * Get available balances by consolidation for specific object types
     * 
     * @param objectTypes
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param isExcludeCostShare
     * @param isConsolidated
     * @param pendingEntriesCode
     * @return
     */
    public List findAccountBalanceByConsolidationByObjectTypes(String[] objectTypes, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isExcludeCostShare, boolean isConsolidated, int pendingEntriesCode);

    /**
     * Get available balances by level
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param financialConsolidationObjectCode
     * @param isCostShareExcluded
     * @param isConsolidated
     * @return a List of Maps
     */
    public List findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode);

    /**
     * Get available balances by object
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param financialObjectLevelCode
     * @param financialReportingSortCode
     * @param isCostShareExcluded
     * @param isConsolidated
     * @return a List of Maps
     */
    public List findAccountBalanceByObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectLevelCode, String financialReportingSortCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode);

    /**
     * Purge an entire fiscal year for a single chart.
     * 
     * @param chartOfAccountscode
     * @param year
     */
    public void purgeYearByChart(String chartOfAccountscode, int year);
}