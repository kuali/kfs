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
package org.kuali.kfs.module.ec.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * To provide a set of utilities to consolidate/group the specified ledger balances and build a returning ledger balance Map.
 */
public class LedgerBalanceConsolidationHelper {

    /**
     * consolidate the amount of the given ledger balance into the balance with the same values of specified key fields
     * 
     * @param ledgerBalanceMap the hash map that contains the consolidated balance records. Its key can be the combined string value
     *        of the given consolidation keys.
     * @param ledgerBalance the given ledger balance to be consolidated
     * @param consolidationKeys the given key field names used to build the keys of ledgerBalanceMap
     */
    public static void consolidateLedgerBalances(Map<String, LaborLedgerBalance> ledgerBalanceMap, LaborLedgerBalance ledgerBalance, List<String> consolidationKeys) {
        String consolidationKeyFieldsAsString = ObjectUtil.concatPropertyAsString(ledgerBalance, consolidationKeys);
        
        consolidateLedgerBalances(ledgerBalanceMap, ledgerBalance, consolidationKeyFieldsAsString);
    }
    
    /**
     * consolidate the amount of the given ledger balance into the balance with the same values of specified key fields
     * 
     * @param ledgerBalanceMap the hash map that contains the consolidated balance records. Its key can be the combined string value
     *        of the given consolidation keys.
     * @param ledgerBalance the given ledger balance to be consolidated
     * @param consolidationKeys the given key field names used to build the keys of ledgerBalanceMap
     */
    public static void consolidateLedgerBalances(Map<String, LaborLedgerBalance> ledgerBalanceMap, LaborLedgerBalance ledgerBalance, String consolidationKeyFieldsAsString) {
        if (ledgerBalanceMap.containsKey(consolidationKeyFieldsAsString)) {
            LaborLedgerBalance existingBalance = ledgerBalanceMap.get(consolidationKeyFieldsAsString);
            addLedgerBalanceAmounts(existingBalance, ledgerBalance);
        }
        else {
            ledgerBalanceMap.put(consolidationKeyFieldsAsString, ledgerBalance);
        }
    }

    /**
     * consolidate the amounts of the given ledger balances into the balances with the same values of specified key fields
     * 
     * @param ledgerBalanceMap the hash map that contains the consolidated balance records. Its key can be the combined string value
     *        of the given consolidation keys.
     * @param ledgerBalances the given ledger balances to be consolidated
     * @param consolidationKeys the given key field names used to build the keys of ledgerBalanceMap
     */
    public static void consolidateLedgerBalances(Map<String, LaborLedgerBalance> ledgerBalanceMap, Collection<LaborLedgerBalance> ledgerBalances, List<String> consolidationKeys) {
        for (LaborLedgerBalance balance : ledgerBalances) {
            consolidateLedgerBalances(ledgerBalanceMap, balance, consolidationKeys);
        }
    }

    /**
     * group the given ledger balance into the list of balances with the same values of specified key fields
     * 
     * @param ledgerBalanceMap the hash map that contains a set of ledger balance lists. Its key can be the combined string value of
     *        the given consolidation keys.
     * @param ledgerBalance the given ledger balance to be grouped
     * @param consolidationKeys the given key field names used to build the keys of ledgerBalanceMap
     */
    public static void groupLedgerBalancesByKeys(Map<String, List<LaborLedgerBalance>> ledgerBalanceMap, LaborLedgerBalance ledgerBalance, List<String> consolidationKeys) {
        String consolidationKeyFieldsAsString = ObjectUtil.concatPropertyAsString(ledgerBalance, consolidationKeys);
        groupLedgerBalancesByKeys(ledgerBalanceMap, ledgerBalance, consolidationKeyFieldsAsString);
    }
    
    /**
     * group the given ledger balance into the list of balances with the same values of specified key fields
     * 
     * @param ledgerBalanceMap the hash map that contains a set of ledger balance lists. Its key can be the combined string value of
     *        the given consolidation keys.
     * @param ledgerBalance the given ledger balance to be grouped
     * @param consolidationKeys the given key field names used to build the keys of ledgerBalanceMap
     */
    public static void groupLedgerBalancesByKeys(Map<String, List<LaborLedgerBalance>> ledgerBalanceMap, LaborLedgerBalance ledgerBalance, String consolidationKeyFieldsAsString) {
        if (ledgerBalanceMap.containsKey(consolidationKeyFieldsAsString)) {
            List<LaborLedgerBalance> balanceList = ledgerBalanceMap.get(consolidationKeyFieldsAsString);
            balanceList.add(ledgerBalance);
        }
        else {
            List<LaborLedgerBalance> balanceList = new ArrayList<LaborLedgerBalance>();
            balanceList.add(ledgerBalance);
            ledgerBalanceMap.put(consolidationKeyFieldsAsString, balanceList);
        }
    }

    /**
     * group the given ledger balances into the lists of balances with the same values of specified key fields
     * 
     * @param ledgerBalanceMap the hash map that contains a set of ledger balance lists. Its key can be the combined string value of
     *        the given consolidation keys.
     * @param ledgerBalance the given ledger balances to be grouped
     * @param consolidationKeys the given key field names used to build the keys of ledgerBalanceMap
     */
    public static void groupLedgerBalancesByKeys(Map<String, List<LaborLedgerBalance>> ledgerBalanceMap, Collection<LaborLedgerBalance> ledgerBalances, List<String> consolidationKeys) {
        for (LaborLedgerBalance balance : ledgerBalances) {
            groupLedgerBalancesByKeys(ledgerBalanceMap, balance, consolidationKeys);
        }
    }

    /**
     * add the monthly amounts of the second ledger balance with those of the first one
     * 
     * @param ledgerBalance the given ledger balance, which holds the summerized monthly amounts
     * @param anotherLedgerBalance the given ledger balance, which contributes monthly amounts
     */
    public static void addLedgerBalanceAmounts(LaborLedgerBalance ledgerBalance, LaborLedgerBalance anotherLedgerBalance) {
        if (anotherLedgerBalance == null) {
            return;
        }

        if (ledgerBalance == null) {
            ledgerBalance = anotherLedgerBalance;
            return;
        }

        for (AccountingPeriodMonth period : AccountingPeriodMonth.values()) {
            KualiDecimal amount = anotherLedgerBalance.getAmountByPeriod(period.periodCode);
            ledgerBalance.addAmount(period.periodCode, amount);
        }
    }

    /**
     * summarize the balance amounts of a given ledger balance within the specified report periods
     * 
     * @param ledgerBalance the given labor ledger balance
     * @param reportPeriods the given report periods
     * @param allFiscalYears allows for total amount calculation across all fiscal years. This is useful if the ledger balances have already been consolidated which is the case once we build the document
     * @return the total amounts of the given balance within the specified report periods
     */
    public static KualiDecimal calculateTotalAmountWithinReportPeriod(LaborLedgerBalance ledgerBalance, Map<Integer, Set<String>> reportPeriods, boolean allFiscalYears) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        Set<String> periodCodes = new HashSet<String>();
        
        // KFSCNTRB-696: allFiscalYears was added because once balances are consolidated we need to look at all periods. Otherwise we may miss data if reportPeriods crossed fiscal years
        if (allFiscalYears) {
            for (Set<String> sourcePeriods : reportPeriods.values()) {
                for (String sourcePeriod : sourcePeriods) {
                    if (!periodCodes.contains(sourcePeriod)) {
                        periodCodes.add(sourcePeriod);
                    }
                }
            }
        } else {
            Integer fiscalYear = ledgerBalance.getUniversityFiscalYear();
            periodCodes = reportPeriods.get(fiscalYear);
        }

        for (String periodCode : periodCodes) {
            totalAmount = totalAmount.add(ledgerBalance.getAmountByPeriod(periodCode));
        }
        
        return totalAmount;
    }

    /**
     * summurize the balance amounts of the given ledger balances within the specified report periods
     * 
     * @param ledgerBalance the given labor ledger balances
     * @param reportPeriods the given report periods
     * @param allFiscalYears allows for total amount calculation across all fiscal years. This is useful if the ledger balances have already been consolidated
     * @return the total amounts of the given balances within the specified report periods
     */
    public static KualiDecimal calculateTotalAmountWithinReportPeriod(Collection<LaborLedgerBalance> ledgerBalances, Map<Integer, Set<String>> reportPeriods, boolean allFiscalYears) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        for (LaborLedgerBalance ledgerBalance : ledgerBalances) {
            KualiDecimal totalAmountForOneBalance = calculateTotalAmountWithinReportPeriod(ledgerBalance, reportPeriods, allFiscalYears);
            totalAmount = totalAmount.add(totalAmountForOneBalance);
        }
        return totalAmount;
    }
}
