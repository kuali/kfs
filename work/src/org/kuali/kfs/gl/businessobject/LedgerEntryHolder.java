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
