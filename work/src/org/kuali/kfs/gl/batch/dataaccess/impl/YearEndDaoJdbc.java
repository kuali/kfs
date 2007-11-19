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
package org.kuali.module.gl.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.kuali.core.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.module.gl.dao.YearEndDao;
import org.springframework.jdbc.core.RowMapper;

/**
 * A JDBC implementation of the YearEndDao, built mainly because OJB is darn slow at some queries
 */
@RawSQL
public class YearEndDaoJdbc extends PlatformAwareDaoBaseJdbc implements YearEndDao {

    // All of the Comparators and RowMappers are stateless, so I can simply create them as variables and avoid unnecessary object
    // creation
    private final Comparator<Map<String, String>> subFundGroupPrimaryKeyComparator = new Comparator<Map<String, String>>() {
        public int compare(Map<String, String> firstSubFundGroupPK, Map<String, String> secondSubFundGroupPK) {
            return firstSubFundGroupPK.get("subFundGroupCode").compareTo(secondSubFundGroupPK.get("subFundGroupCode"));
        }
    };

    private final Comparator<Map<String, String>> priorYearAccountPrimaryKeyComparator = new Comparator<Map<String, String>>() {
        public int compare(Map<String, String> firstPriorYearPK, Map<String, String> secondPriorYearPK) {
            if (firstPriorYearPK.get("chartOfAccountsCode").equals(secondPriorYearPK.get("chartOfAccountsCode"))) {
                return firstPriorYearPK.get("accountNumber").compareTo(secondPriorYearPK.get("accountNumber"));
            }
            else {
                return firstPriorYearPK.get("chartOfAccountsCode").compareTo(secondPriorYearPK.get("chartOfAccountsCode"));
            }
        }
    };

    private final RowMapper subFundGroupRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<String, String> subFundGroupKey = new LinkedHashMap<String, String>();
            subFundGroupKey.put("subFundGroupCode", rs.getString("sub_fund_grp_cd"));
            return subFundGroupKey;
        }
    };

    private final RowMapper priorYearAccountRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<String, String> keyMap = new LinkedHashMap<String, String>();
            keyMap.put("chartOfAccountsCode", rs.getString("fin_coa_cd"));
            keyMap.put("accountNumber", rs.getString("account_nbr"));
            return keyMap;
        }
    };

    /**
     * Queries the databse to find missing prior year accounts
     * 
     * @param balanceFiscalyear the fiscal year of balances to check for missing prior year accounts for
     * @return a Set of Maps holding the primary keys of missing prior year accounts
     * @see org.kuali.module.gl.dao.YearEndDao#findKeysOfMissingPriorYearAccountsForBalances(java.lang.Integer)
     */
    @RawSQL
    public Set<Map<String, String>> findKeysOfMissingPriorYearAccountsForBalances(Integer balanceFiscalYear) {
        // 1. get a sorted list of the prior year account keys that are used by balances for the given fiscal year
        List priorYearKeys = getJdbcTemplate().query("select distinct fin_coa_cd, account_nbr from gl_balance_t where univ_fiscal_yr = ? order by fin_coa_cd, account_nbr", new Object[] { balanceFiscalYear }, priorYearAccountRowMapper);

        // 2. go through that list, finding which prior year accounts don't show up in the database
        return selectMissingPriorYearAccounts(priorYearKeys);
    }

    /**
     * This method puts all of the prior year accounts that aren't in the database, based on the list of keys sent in, into the
     * given set
     * 
     * @param priorYearKeys the prior year keys to search for
     * @return the set of those prior year accounts that are missing
     */
    @RawSQL
    private Set<Map<String, String>> selectMissingPriorYearAccounts(List priorYearKeys) {
        Set<Map<String, String>> missingPriorYears = new TreeSet<Map<String, String>>(priorYearAccountPrimaryKeyComparator);
        for (Object priorYearKeyAsObject : priorYearKeys) {
            Map<String, String> priorYearKey = (Map<String, String>) priorYearKeyAsObject;
            int count = getJdbcTemplate().queryForInt("select count(*) from ca_prior_yr_acct_t where fin_coa_cd = ? and account_nbr = ? order by sub_fund_grp_cd", new Object[] { priorYearKey.get("chartOfAccountsCode"), priorYearKey.get("accountNumber") });
            if (count == 0) {
                missingPriorYears.add(priorYearKey);
            }
        }
        return missingPriorYears;
    }

    /**
     * Queries the database to find missing sub fund groups
     * 
     * @param balanceFiscalYear the fiscal year of the balance to find missing sub fund groups for
     * @return a Set of Maps holding the primary keys of missing sub fund groups
     * @see org.kuali.module.gl.dao.YearEndDao#findKeysOfMissingSubFundGroupsForBalances(java.lang.Integer)
     */
    @RawSQL
    public Set<Map<String, String>> findKeysOfMissingSubFundGroupsForBalances(Integer balanceFiscalYear) {
        // see algorithm for findKeysOfMissingPriorYearAccountsForBalances
        List subFundGroupKeys = getJdbcTemplate().query("select distinct ca_prior_yr_acct_t.sub_fund_grp_cd from ca_prior_yr_acct_t, gl_balance_t where ca_prior_yr_acct_t.fin_coa_cd = gl_balance_t.fin_coa_cd and ca_prior_yr_acct_t.account_nbr = gl_balance_t.account_nbr and gl_balance_t.univ_fiscal_yr = ? and ca_prior_yr_acct_t.sub_fund_grp_cd is not null order by ca_prior_yr_acct_t.sub_fund_grp_cd", new Object[] { balanceFiscalYear }, subFundGroupRowMapper);
        return selectMissingSubFundGroups(subFundGroupKeys);
    }

    /**
     * This method puts all of the sub fund groups that are in the given list of subFundGroupKeys but aren't in the database into
     * the given set
     * 
     * @param subFundGroupKeys the list of sub fund group keys to search through
     * @return a set of those sub fund group keys that are missing
     */
    @RawSQL
    private Set<Map<String, String>> selectMissingSubFundGroups(List subFundGroupKeys) {
        Set<Map<String, String>> missingSubFundGroups = new TreeSet<Map<String, String>>(subFundGroupPrimaryKeyComparator);
        for (Object subFundGroupKeyAsObject : subFundGroupKeys) {
            Map<String, String> subFundGroupKey = (Map<String, String>) subFundGroupKeyAsObject;
            int count = getJdbcTemplate().queryForInt("select count(*) from ca_sub_fund_grp_t where sub_fund_grp_cd = ?", new Object[] { subFundGroupKey.get("subFundGroupCode") });
            if (count == 0) {
                missingSubFundGroups.add(subFundGroupKey);
            }
        }
        return missingSubFundGroups;
    }

    /**
     * Queries the databsae to find missing prior year account records referred to by encumbrance records
     * 
     * @param encumbranceFiscalYear the fiscal year of balances to find missing encumbrance records for
     * @return a Set of Maps holding the primary keys of missing prior year accounts
     * @see org.kuali.module.gl.dao.YearEndDao#findKeysOfMissingPriorYearAccountsForOpenEncumbrances(java.lang.Integer)
     */
    @RawSQL
    public Set<Map<String, String>> findKeysOfMissingPriorYearAccountsForOpenEncumbrances(Integer encumbranceFiscalYear) {
        List priorYearKeys = getJdbcTemplate().query("select distinct fin_coa_cd, account_nbr from gl_encumbrance_t where univ_fiscal_yr = ? and acln_encum_amt <> acln_encum_cls_amt order by fin_coa_cd, account_nbr", new Object[] { encumbranceFiscalYear }, priorYearAccountRowMapper);
        return selectMissingPriorYearAccounts(priorYearKeys);
    }

    /**
     * Queries the database to find missing sub fund group records referred to by encumbrances
     * 
     * @param  encumbranceFiscalYear the fiscal year of encumbrances to find missing sub fund group records for
     * @return a Set of Maps holding the primary keys of missing sub fund group records
     * @see org.kuali.module.gl.dao.YearEndDao#findKeysOfMissingSubFundGroupsForOpenEncumbrances(java.lang.Integer)
     */
    @RawSQL
    public Set<Map<String, String>> findKeysOfMissingSubFundGroupsForOpenEncumbrances(Integer encumbranceFiscalYear) {
        List subFundGroupKeys = getJdbcTemplate().query("select distinct ca_prior_yr_acct_t.sub_fund_grp_cd from ca_prior_yr_acct_t, gl_encumbrance_t where ca_prior_yr_acct_t.fin_coa_cd = gl_encumbrance_t.fin_coa_cd and ca_prior_yr_acct_t.account_nbr = gl_encumbrance_t.account_nbr and gl_encumbrance_t.univ_fiscal_yr = ? and gl_encumbrance_t.acln_encum_amt <> gl_encumbrance_t.acln_encum_cls_amt and ca_prior_yr_acct_t.sub_fund_grp_cd is not null order by ca_prior_yr_acct_t.sub_fund_grp_cd", new Object[] { encumbranceFiscalYear }, subFundGroupRowMapper);
        return selectMissingSubFundGroups(subFundGroupKeys);
    }

}
