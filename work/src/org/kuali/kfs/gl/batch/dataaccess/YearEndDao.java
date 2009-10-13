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
package org.kuali.kfs.gl.batch.dataaccess;

import java.util.Map;
import java.util.Set;

/**
 * An interface that declares methods needed for error reporting by the year end jobs
 */
public interface YearEndDao {
    
    /**
     * Returns the keys (Chart Code and Account Number) of PriorYearAccounts that are missing for the balances associated with the
     * given fiscal year
     * 
     * @param balanceFiscalYear a fiscal year to find balances for
     * @return a set of the missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingPriorYearAccountsForBalances(Integer balanceFiscalYear);

    /**
     * Returns a set of the keys (chartOfAccountsCode and accountNumber) of PriorYearAccounts that are missing for the open
     * encumbrances of a given fiscal year
     * 
     * @param balanceFiscalYear a fiscal year to find open encumbrances for
     * @return a set of the missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingPriorYearAccountsForOpenEncumbrances(Integer encumbranceFiscalYear);

    /**
     * Returns a set of the keys (subFundGroupCode) of sub fund groups that are missing for the prior year accounts associated with
     * a fiscal year to find balances for
     * 
     * @param balanceFiscalYear the fiscal year to find balances for
     * @return a set of missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingSubFundGroupsForBalances(Integer balanceFiscalYear);

    /**
     * Returns a set of the keys (subFundGroupCode) of sub fund groups that are missing for the prior year accounts associated with
     * a fiscal year to find open encumbrances for
     * 
     * @param encumbranceFiscalYear the fiscal year to find encumbrnaces for
     * @return a set of missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingSubFundGroupsForOpenEncumbrances(Integer encumbranceFiscalYear);
}
