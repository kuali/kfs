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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.AccountBalance;

/**
 * This class...
 */
public interface AccountBalanceService {
    public final int PENDING_NONE = 1;
    public final int PENDING_APPROVED = 2;
    public final int PENDING_ALL = 3;

    /**
     * This method finds the available account balances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @return the summary records of account balance entries
     */
    public Iterator findConsolidatedAvailableAccountBalance(Map fieldValues);

    /**
     * This method gets the number of the available account balances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated determine whether the search results are consolidated
     * @return the number of the available account balances
     */
    public Integer getAvailableAccountBalanceCount(Map fieldValues, boolean isConsolidated);

    /**
     * This method finds the available account balances according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated determine whether the search results are consolidated
     * @return a collection of account balance entries
     */
    public Iterator findAvailableAccountBalance(Map fieldValues);

    /**
     * This method finds the available account balances according to input fields and values
     */
    public List findAccountBalanceByConsolidation(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode);

    /**
     * This method finds the available account balances according to input fields and values
     */
    public List findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode);

    /**
     * This method finds the available account balances according to input fields and values
     */
    public List findAccountBalanceByObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, String financialObjectLevelCode, String financialReportingSortCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode);

    /**
     * Save an account balance
     * 
     * @param ab
     */
    public void save(AccountBalance ab);

    /**
     * Purge an entire fiscal year for a single chart.
     * 
     * @param chartOfAccountscode
     * @param year
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year);
}
