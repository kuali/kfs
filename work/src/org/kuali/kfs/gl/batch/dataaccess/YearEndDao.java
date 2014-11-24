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
package org.kuali.kfs.gl.batch.dataaccess;

import java.util.List;
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
     * Returns the keys (Chart Code and Account Number) of PriorYearAccounts that are missing for the balances associated with the
     * given fiscal year and specified charts
     *
     * @param balanceFiscalYear a fiscal year to find balances for
     * @param balanceCharts list of charts to find balances for
     * @return a set of the missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingPriorYearAccountsForBalances(Integer balanceFiscalYear, List<String> balanceCharts);

    /**
     * Returns a set of the keys (chartOfAccountsCode and accountNumber) of PriorYearAccounts that are missing for the open
     * encumbrances of a given fiscal year
     *
     * @param balanceFiscalYear a fiscal year to find open encumbrances for
     * @return a set of the missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingPriorYearAccountsForOpenEncumbrances(Integer encumbranceFiscalYear);

    /**
     * Returns a set of the keys (chartOfAccountsCode and accountNumber) of PriorYearAccounts that are missing for the open
     * encumbrances of a given fiscal year and specified charts
     *
     * @param encumbranceFiscalYear a fiscal year to find open encumbrances for
     * @param charts list of charts to find balances for
     *
     * @return a set of the missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingPriorYearAccountsForOpenEncumbrances(Integer encumbranceFiscalYear, List<String> charts);

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
     * a fiscal year and specified charts to find balances for
     *
     * @param balanceFiscalYear the fiscal year to find balances for
     * @param chartsList the charts to find balances for
     * @return a set of missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingSubFundGroupsForBalances(Integer balanceFiscalYear, List<String> chartsList);

    /**
     * Returns a set of the keys (subFundGroupCode) of sub fund groups that are missing for the prior year accounts associated with
     * a fiscal year to find open encumbrances for
     *
     * @param encumbranceFiscalYear the fiscal year to find encumbrnaces for
     * @return a set of missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingSubFundGroupsForOpenEncumbrances(Integer encumbranceFiscalYear);

    /**
     * Returns a set of the keys (subFundGroupCode) of sub fund groups that are missing for the prior year accounts associated with
     * a fiscal year and specified charts to find open encumbrances for
     *
     * @param encumbranceFiscalYear the fiscal year to find encumbrnaces for
     * @param encumbranceCharts charts to find encumbrnaces for
     * @return a set of missing primary keys
     */
    public Set<Map<String, String>> findKeysOfMissingSubFundGroupsForOpenEncumbrances(Integer encumbranceFiscalYear, List<String> encumbranceCharts);
}
