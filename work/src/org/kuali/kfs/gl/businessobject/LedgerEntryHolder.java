/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * This class...
 * 
 * @author Bin Gao
 */
public class LedgerEntryHolder {
    private Map ledgerEntries;
    private Map subtotals;
    private LedgerEntry grandTotal;

    /**
     * Constructs a LedgerEntryHolder.java.
     */
    public LedgerEntryHolder() {
        ledgerEntries = new HashMap();
        subtotals = new HashMap();
        grandTotal = new LedgerEntry(null, null, null, "Grand Total");
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
            ledgerEntry = new LedgerEntry(null, "", newLedgerEntry.getBalanceType(), "Subtotal");
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