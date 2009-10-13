/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * A collection of many LedgerEntry records, which appropriately groups the records
 */
public class LedgerEntryHolder {
    private Map ledgerEntries;
    private Map subtotals;
    private LedgerEntryForReporting grandTotal;

    private static final String GRAND_TOTAL = "Grand Total";
    private static final String SUB_TOTAL = "Subtotal";

    /**
     * Constructs a LedgerEntryHolder.java.
     */
    public LedgerEntryHolder() {
        ledgerEntries = new HashMap();
        subtotals = new HashMap();
        grandTotal = new LedgerEntryForReporting(null, null, null, GRAND_TOTAL);
    }

    /**
     * add a given ledger entry into the holder. If there exists a ledger entry with the same key, then update the amount and count
     * fields of the ledger entry; otherwise, insert it into the holder.
     * 
     * @param newLedgerEntry the given ledger entry
     * @param calculateTotals indicate if the subtotals and grand total need to be calculated
     */
    public void insertLedgerEntry(LedgerEntryForReporting newLedgerEntry, boolean calculateTotal) {
        Integer fiscalYear = newLedgerEntry.getFiscalYear();
        String periodCode = newLedgerEntry.getPeriod();

        String balanceType = newLedgerEntry.getBalanceType();
        String originCode = newLedgerEntry.getOriginCode();

        String keyOfLedgerEntry = balanceType + "-" + originCode + "-" + fiscalYear + "-" + periodCode;

        if (!ledgerEntries.containsKey(keyOfLedgerEntry)) {
            ledgerEntries.put(keyOfLedgerEntry, newLedgerEntry);
        }
        else {
            LedgerEntryForReporting ledgerEntry = (LedgerEntryForReporting) ledgerEntries.get(keyOfLedgerEntry);
            ledgerEntry.add(newLedgerEntry);
        }

        // calculate the subtotals and grand total
        if (calculateTotal) {
            updateSubtotal(newLedgerEntry);
            updateGrandTotal(newLedgerEntry);
        }
    }

    /**
     * update the subtotal using the given ledger entry
     * 
     * @param newLedgerEntry a new ledger entry to add to the holder
     */
    private void updateSubtotal(LedgerEntryForReporting newLedgerEntry) {
        String groupingKey = newLedgerEntry.getBalanceType();

        if (StringUtils.isBlank(groupingKey)) {
            return;
        }

        LedgerEntryForReporting ledgerEntry = null;
        if (!subtotals.containsKey(groupingKey)) {
            ledgerEntry = new LedgerEntryForReporting(null, "", newLedgerEntry.getBalanceType(), SUB_TOTAL);
            subtotals.put(groupingKey, ledgerEntry);
        }
        else {
            ledgerEntry = (LedgerEntryForReporting) subtotals.get(groupingKey);
        }
        ledgerEntry.add(newLedgerEntry);
    }

    /**
     * update the grand total with the given ledger entry
     * 
     * @param newLedgerEntry entry to help update the grand total
     */
    private void updateGrandTotal(LedgerEntryForReporting newLedgerEntry) {
        this.grandTotal.add(newLedgerEntry);
    }

    /**
     * Gets the grandTotal attribute.
     * 
     * @return Returns the grandTotal.
     */
    public LedgerEntryForReporting getGrandTotal() {
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
