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
package org.kuali.module.effort.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.labor.bo.LedgerBalance;

public class LedgerBalanceConsolidationHelper {
    private Map<String, LedgerBalance> ledgerBalanceConsolidationMap;

    /**
     * Constructs a LedgerBalanceConsolidationHelper.java.
     */
    public LedgerBalanceConsolidationHelper() {
        super();
        ledgerBalanceConsolidationMap = new HashMap<String, LedgerBalance>();
    }

    public void consolidateLedgerBalances(LedgerBalance ledgerBalance) {
        String consolidationKeyFieldsAsString = getConsolidationKeyFieldsAsString(ledgerBalance);

        if (ledgerBalanceConsolidationMap.containsKey(consolidationKeyFieldsAsString)) {
            LedgerBalance existingBalance = ledgerBalanceConsolidationMap.get(consolidationKeyFieldsAsString);
            addLedgerBalanceAmounts(existingBalance, ledgerBalance);
        }
        else {
            ledgerBalanceConsolidationMap.put(consolidationKeyFieldsAsString, ledgerBalance);
        }
    }
    
    public void consolidateLedgerBalances(Collection<LedgerBalance> ledgerBalances) {
        for(LedgerBalance balance : ledgerBalances) {
            consolidateLedgerBalances(balance);
        }
    }

    public static String getConsolidationKeyFieldsAsString(LedgerBalance ledgerBalance) {
        StringBuilder consolidationFields = new StringBuilder();

        consolidationFields.append(ledgerBalance.getEmplid());
        consolidationFields.append(ledgerBalance.getChartOfAccountsCode());
        consolidationFields.append(ledgerBalance.getAccountNumber());
        consolidationFields.append(ledgerBalance.getSubAccountNumber());
        consolidationFields.append(ledgerBalance.getFinancialObjectCode());
        consolidationFields.append(ledgerBalance.getPositionNumber());

        return consolidationFields.toString();
    }

    public static void addLedgerBalanceAmounts(LedgerBalance ledgerBalance, LedgerBalance anotherLedgerBalance) {
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
     * summurize the balance amounts of a given ledger balance within the specified report periods
     * 
     * @param ledgerBalance the given labor ledger balance
     * @param reportPeriods the given report periods
     * @return the total amounts of the given balance within the specified report periods
     */
    public static KualiDecimal calculateTotalAmountWithinReportPeriod(LedgerBalance ledgerBalance, Map<Integer, Set<String>> reportPeriods) {
        Integer fiscalYear = ledgerBalance.getUniversityFiscalYear();
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        Set<String> periodCodes = reportPeriods.get(fiscalYear);
        for (String period : periodCodes) {
            totalAmount.add(ledgerBalance.getAmountByPeriod(period));
        }
        return totalAmount;
    }

    /**
     * summurize the balance amounts of the given ledger balances within the specified report periods
     * 
     * @param ledgerBalance the given labor ledger balances
     * @param reportPeriods the given report periods
     * @return the total amounts of the given balances within the specified report periods
     */
    public static KualiDecimal calculateTotalAmountWithinReportPeriod(Collection<LedgerBalance> ledgerBalances, Map<Integer, Set<String>> reportPeriods) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        for (LedgerBalance ledgerBalance : ledgerBalances) {
            KualiDecimal totalAmountForOneBalance = calculateTotalAmountWithinReportPeriod(ledgerBalance, reportPeriods);
            totalAmount = totalAmount.add(totalAmountForOneBalance);
        }
        return totalAmount;
    }

    /**
     * Gets the ledgerBalanceConsolidationMap attribute. 
     * @return Returns the ledgerBalanceConsolidationMap.
     */
    public Map<String, LedgerBalance> getLedgerBalanceConsolidationMap() {
        return ledgerBalanceConsolidationMap;
    }

    /**
     * Sets the ledgerBalanceConsolidationMap attribute value.
     * @param ledgerBalanceConsolidationMap The ledgerBalanceConsolidationMap to set.
     */
    public void setLedgerBalanceConsolidationMap(Map<String, LedgerBalance> ledgerBalanceConsolidationMap) {
        this.ledgerBalanceConsolidationMap = ledgerBalanceConsolidationMap;
    }
}
