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
 * An interface to methods needed to calculate Balance By Consolidation Balance inquiries to query the database
 */
public interface AccountBalanceConsolidationDao {

    /**
     * Returns account balance information that qualifies, based on the inquiry formed out of the parameters
     * 
     * @param objectTypes the object types of account balances to include in the inquiry
     * @param universityFiscalYear the fiscal year of account balances to include in the inquiry
     * @param chartOfAccountsCode the chart of accounts of account balances to include in the inquiry
     * @param accountNumber the account number of account balances to include in the inquiry
     * @param isExcludeCostShare whether to exclude cost share entries from this inquiry or not
     * @param isConsolidated whether the results of the inquiry should be consolidated
     * @param pendingEntriesCode whether the inquiry should also report results based on no pending entries, approved pending
     *        entries, or all pending entries
     * @param options system options
     * @param today current university date
     * @return a List of Maps with the report information from this inquiry
     */
    public List<Map<String, Object>> findAccountBalanceByConsolidationObjectTypes(String[] objectTypes, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isExcludeCostShare, boolean isConsolidated, int pendingEntriesCode, SystemOptions options, UniversityDate today);
}
