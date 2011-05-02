/*
 * Copyright 2007 The Kuali Foundation
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
