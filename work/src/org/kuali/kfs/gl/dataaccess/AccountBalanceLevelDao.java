/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.List;
import java.util.Map;

/**
 * A DAO inteface that declares methods necessary to query the database for the account balance by level inquiry
 */
public interface AccountBalanceLevelDao {

    /**
     * Returns a collection of report data for the account balance by level inquiry
     * 
     * @param universityFiscalYear the university fiscal year of reported on account balances
     * @param chartOfAccountsCode the chart of accounts code of reported on account balances
     * @param accountNumber the account number of reported on account balances
     * @param financialConsolidationObjectCode the consolidation code of reported on account balances
     * @param isCostShareExcluded whether cost share account balances should be excluded from the query or not
     * @param isConsolidated whether the results of the query should be consolidated
     * @param pendingEntriesCode whether this query should account for no pending entries, approved pending entries, or all pending entries
     * @return a List of Maps with appropriate report data
     */
    public List<Map<String, Object>> findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode);
}
