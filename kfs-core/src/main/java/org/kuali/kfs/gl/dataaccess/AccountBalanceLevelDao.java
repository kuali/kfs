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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;

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
     * @param pendingEntriesCode whether this query should account for no pending entries, approved pending entries, or all pending
     *        entries
     * @param today the current university date
     * @param options system options
     * @return a List of Maps with appropriate report data
     */
    public List<Map<String, Object>> findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode, UniversityDate today, SystemOptions options);
}
