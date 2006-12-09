/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * This class...
 * 
 * 
 */
public class LedgerEntryHolder {
    private Map ledgerEntries;
    private Map subtotals;
    private LedgerEntry grandTotal;

    private static final String GRAND_TOTAL = "Grand Total";
    private static final String SUB_TOTAL = "Subtotal";

    /**
     * Constructs a LedgerEntryHolder.java.
     */
    public LedgerEntryHolder() {
        ledgerEntries = new HashMap();
        subtotals = new HashMap();
        grandTotal = new LedgerEntry(null, null, null, GRAND_TOTAL);
    }

    /**
     * add a given ledger entry into the holder. If there exists a ledger entry with the same key, then update the amount and count
     * fields of the ledger entry; otherwise, insert it into the holder.
     * 
     * @param newLedgerEntry the given ledger entry
     * @param calculateTotals indicate if the subtotals and grand total need to be calculated
     */
    public void insertLedgerEntry(LedgerEntry newLedgerEntry, boolean calculateTotal) {
        Integer fiscalYear = newLedgerEntry.getFiscalYear();
        String periodCode = newLedgerEntry.getPeriod();

        String balanceType = newLedgerEntry.getBalanceType();
        String originCode = newLedgerEntry.getOriginCode();

        String keyOfLedgerEntry = balanceType + "-" + originCode + "-" + fiscalYear + "-" + periodCode;

        if (!ledgerEntries.containsKey(keyOfLedgerEntry)) {
            ledgerEntries.put(keyOfLedgerEntry, newLedgerEntry);
        }
        else {
            LedgerEntry ledgerEntry = (LedgerEntry) ledgerEntries.get(keyOfLedgerEntry);
            ledgerEntry.add(newLedgerEntry);
        }

        // calculate the subtotals and grand total
        if (calculateTotal) {
            updateSubtotal(newLedgerEntry);
            updateGrandTotal(newLedgerEntry);
        }
    }

    // update the subtotal using the given ledger entry
    private void updateSubtotal(LedgerEntry newLedgerEntry) {
        String groupingKey = newLedgerEntry.getBalanceType();

        if (StringUtils.isBlank(groupingKey)) {
            return;
        }

        LedgerEntry ledgerEntry = null;
        if (!subtotals.containsKey(groupingKey)) {
            ledgerEntry = new LedgerEntry(null, "", newLedgerEntry.getBalanceType(), SUB_TOTAL);
            subtotals.put(groupingKey, ledgerEntry);
        }
        else {
            ledgerEntry = (LedgerEntry) subtotals.get(groupingKey);
        }
        ledgerEntry.add(newLedgerEntry);
    }

    // update the grand total with the given ledger entry
    private void updateGrandTotal(LedgerEntry newLedgerEntry) {
        this.grandTotal.add(newLedgerEntry);
    }

    /**
     * Gets the grandTotal attribute.
     * 
     * @return Returns the grandTotal.
     */
    public LedgerEntry getGrandTotal() {
        return grandTotal;
    }

    /**
     * Gets the ledgerEntries attribute.
     * 
     * @return Returns the ledgerEntries.
     */
    public Map getLedgerEntries() {
        return ledgerEntries;
    }

    /**
     * Gets the subtotals attribute.
     * 
     * @return Returns the subtotals.
     */
    public Map getSubtotals() {
        return subtotals;
    }
}