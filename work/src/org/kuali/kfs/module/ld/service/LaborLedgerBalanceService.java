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
package org.kuali.module.labor.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.labor.bo.AccountStatusCurrentFunds;
import org.kuali.module.labor.bo.LedgerBalance;

public interface LaborLedgerBalanceService {
    /**
     * Because there is &lt;extent-class .../&gt; is broken in OJB, we need to create this class
     * that will handle the inheritence. This is intended for use with AccountStatusCurrentFunds and
     * AccountStatusBaseFunds. This is a simple implementation and only handls POJO stuff.
     *
     * @param source Original LedgerBalance
     * @param dest some class to extend LedgerBalance to copy to
     * @returns the copy
     */
    public <U> U copyLedgerBalance(LedgerBalance source, Class<? extends LedgerBalance> dest);

    /**
     * Save
     * 
     * @param b
     */
    public void save(LedgerBalance b);

    public boolean hasAssetLiabilityFundBalanceBalances(Account account);

    public boolean fundBalanceWillNetToZero(Account account);

    public boolean hasEncumbrancesOrBaseBudgets(Account account);

    public boolean beginningBalanceLoaded(Account account);

    public boolean hasAssetLiabilityOrFundBalance(Account account);

    /**
     * 
     * @param fiscalYear
     * @return an Iterator over all balances for a given year
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear);

    /**
     * This method finds the summary records of balance entries according to input fields an values
     * 
     * @param fieldValues the input fields an values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     */
    public Iterator findCashBalance(Map fieldValues, boolean isConsolidated);

    /**
     * This method gets the size of cash balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size of cash balance entries
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
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart, int year);

    /**
     * Get the GL Balance summary for the GL Summary report
     * 
     * @param universityFiscalYear
     * @param balanceTypeCodes
     * @return
     */
    public List getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes);
    
    public Iterator<AccountStatusCurrentFunds> getAccountStatusCurrentFunds(Map fieldValues);
    
    /**
     * find a ledger balance from the given ledger balance collection with the given transaction information
     * @param ledgerBalanceCollection the given ledger balance collection 
     * @param transaction the given transaction information
     * @return a matching ledger balance from the given ledger balance
     */
    public LedgerBalance findLedgerBalance(Collection<LedgerBalance> ledgerBalanceCollection, Transaction transaction);
    
    /**
     * convert the given transaction information into a ledger balance and add it into the given ledger balance collection with 
     * @param ledgerBalanceCollection the given ledger balance collection 
     * @param transaction the given transaction information
     * @return true if the ledger balance has been added; otherwise, false;
     */
    public boolean addLedgerBalance(Collection<LedgerBalance> ledgerBalanceCollection, Transaction transaction);

    /**
     * update the given ledger balance with the given transaction information 
     * @param ledgerBalance the given ledger balance
     * @param transaction the given transaction information
     */
    public void updateLedgerBalance(LedgerBalance ledgerBalance, Transaction transaction);
    
    
}
