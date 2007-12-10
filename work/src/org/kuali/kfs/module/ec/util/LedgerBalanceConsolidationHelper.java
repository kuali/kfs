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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.ObjectUtil;

public class LedgerBalanceConsolidationHelper {
    private Map<String, LedgerBalance> ledgerBalanceConsolidationMap;
    private List<String> consolidationKeys;

    /**
     * Constructs a LedgerBalanceConsolidationHelper.java.
     */
    public LedgerBalanceConsolidationHelper() {
        super();
        ledgerBalanceConsolidationMap = new HashMap<String, LedgerBalance>();
    }

    public void consolidateLedgerBalances(LedgerBalance ledgerBalance) {
        String consolidationKeyFieldsAsString = ObjectUtil.concatPropertyAsString(ledgerBalance, consolidationKeys);

        if (ledgerBalanceConsolidationMap.containsKey(consolidationKeyFieldsAsString)) {
            LedgerBalance existingBalance = ledgerBalanceConsolidationMap.get(consolidationKeyFieldsAsString);
            addLedgerBalanceAmounts(existingBalance, ledgerBalance);
        }
        else {
            ledgerBalanceConsolidationMap.put(consolidationKeyFieldsAsString, ledgerBalance);
        }
    }

    public void consolidateLedgerBalances(Collection<LedgerBalance> ledgerBalances) {
        for (LedgerBalance balance : ledgerBalances) {
            consolidateLedgerBalances(balance);
        }
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
     * 
     * @return Returns the ledgerBalanceConsolidationMap.
     */
    public Map<String, LedgerBalance> getLedgerBalanceConsolidationMap() {
        return ledgerBalanceConsolidationMap;
    }
    
    private  List<String> getDefualtConsolidationKeys() {
        List<String> consolidationKeys = new ArrayList<String>();
        consolidationKeys.add(KFSPropertyConstants.EMPLID);
        consolidationKeys.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        consolidationKeys.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        consolidationKeys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        consolidationKeys.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        consolidationKeys.add(KFSPropertyConstants.POSITION_NUMBER);

        return consolidationKeys;
    }

    /**
     * Sets the ledgerBalanceConsolidationMap attribute value.
     * 
     * @param ledgerBalanceConsolidationMap The ledgerBalanceConsolidationMap to set.
     */
    public void setLedgerBalanceConsolidationMap(Map<String, LedgerBalance> ledgerBalanceConsolidationMap) {
        this.ledgerBalanceConsolidationMap = ledgerBalanceConsolidationMap;
    }

    /**
     * Gets the consolidationKeys attribute. 
     * @return Returns the consolidationKeys.
     */
    public List<String> getConsolidationKeys() {
        return consolidationKeys == null ? getDefualtConsolidationKeys() : consolidationKeys;
    }

    /**
     * Sets the consolidationKeys attribute value.
     * @param consolidationKeys The consolidationKeys to set.
     */
    public void setConsolidationKeys(List<String> consolidationKeys) {
        this.consolidationKeys = consolidationKeys;
    }
}
