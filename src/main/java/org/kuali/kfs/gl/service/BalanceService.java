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
package org.kuali.kfs.gl.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.GlSummary;

/**
 * An interface which declares methods needed for using Balance
 */
public interface BalanceService {

    /**
     * This method...
     *
     * @param account
     * @return
     */
    public boolean hasAssetLiabilityFundBalanceBalances(Account account);

    /**
     * This method...
     *
     * @param account
     * @return
     */
    public boolean fundBalanceWillNetToZero(Account account);

    /**
     * This method...
     *
     * @param account
     * @return
     */
    public boolean hasEncumbrancesOrBaseBudgets(Account account);

    /**
     * This method...
     *
     * @param account
     * @return
     */
    public boolean beginningBalanceLoaded(Account account);

    /**
     * This method...
     *
     * @param account
     * @return
     */
    public boolean hasAssetLiabilityOrFundBalance(Account account);

    /**
     * Returns all of the balances for a given fiscal year.
     *
     * @param fiscalYear the fiscal year to find balances for
     * @return an Iterator over all balances for a given year
     */
    public Iterator<Balance> findBalancesForFiscalYear(Integer fiscalYear);

    /**
     * This method finds the summary records of balance entries according to input fields an values. The results will be limited to
     * the system lookup results limit.
     *
     * @param fieldValues the input fields an values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     */
    public Iterator lookupCashBalance(Map fieldValues, boolean isConsolidated);

    /**
     * This method gets the size of cash balance entries according to input fields and values
     *
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the count of cash balance entries
     */
    public Integer getCashBalanceRecordCount(Map fieldValues, boolean isConsolidated);

    /**
     * This method gets the size of balance entries according to input fields and values
     *
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size of balance entries
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated);

    /**
     * This method finds the summary records of balance entries according to input fields and values
     *
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     */
    public Integer getBalanceRecordCount(Map fieldValues, boolean isConsolidated);

    /**
     * Purge the sufficient funds balance table by year/chart
     *
     * @param chart the chart purged balances should have
     * @param year the fiscal year purged balances should have
     */
    public void purgeYearByChart(String chart, int year);

    /**
     * Get the GL Balance summary for the GL Summary report
     *
     * @param universityFiscalYear
     * @param balanceTypeCodes
     * @return a list of summarized GL balances
     */
    public List<GlSummary> getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes);

    /**
     * This method returns the total count of balances for a fiscal year
     *
     * @param year fiscal year to check
     * @return the count of balances
     */
    public int countBalancesForFiscalYear(Integer year);

    /**
     * This method returns the total count of balances for a fiscal year and specified charts
     *
     * @param year fiscal year to check
     * @param list of specified charts
     * @return the count of balances
     */
    public int countBalancesForFiscalYear(Integer year, List<String> charts);

    /**
     * This method returns all of the balances specifically for the nominal activity closing job
     *
     * @param year year to find balances for
     * @return an Iterator of nominal activity balances
     */
    public Iterator<Balance> findNominalActivityBalancesForFiscalYear(Integer year);

    /**
     * This method returns all of the balances specifically for the nominal activity closing job when annual closing charts are specified
     *
     * @param year year to find balances for
     * @param charts list of charts to find balances for
     * @return an Iterator of nominal activity balances
     */
    public Iterator<Balance> findNominalActivityBalancesForFiscalYear(Integer year, List<String> charts);

    /**
     * Returns all the balances specifically to be processed by the balance forwards job for the "general" rule
     *
     * @param year the fiscal year to find balances for
     * @return an Iterator of balances to process for the general balance forward process
     */
    public Iterator<Balance> findGeneralBalancesToForwardForFiscalYear(Integer year);

    /**
     * Returns all the balances specifically to be processed by the balance forwards job for the "general" rule
     * for the specified fiscal year and charts
     *
     * @param year the fiscal year to find balances for
     * @param charts charts to find balances for
     * @return an Iterator of balances to process for the general balance forward process
     */
    public Iterator<Balance> findGeneralBalancesToForwardForFiscalYear(Integer year, List<String> charts);

    /**
     * Returns all the balances to be forwarded for the "cumulative" rule
     *
     * @param year the fiscal year to find balances for
     * @return an Iterator of balances to process for the cumulative/active balance forward process
     */
    public Iterator<Balance> findCumulativeBalancesToForwardForFiscalYear(Integer year);

    /**
     * Returns all the balances to be forwarded for the "cumulative" rule
     * @param year the fiscal year to find balances for
     * @param charts charts to find balances for
     * @return an Iterator of balances to process for the cumulative/active balance forward process
     */
    public Iterator<Balance> findCumulativeBalancesToForwardForFiscalYear(Integer year, List<String> charts);

    /**
     * Returns all of the balances to be forwarded for the organization reversion process
     *
     * @param year the year of balances to find
     * @param endOfYear whether the organization reversion process is running end of year (before the fiscal year change over) or
     *        beginning of year (after the fiscal year change over)
     * @return an iterator of balances to put through the strenuous organization reversion process
     */
    public Iterator<Balance> findOrganizationReversionBalancesForFiscalYear(Integer year, boolean endOfYear);
}
