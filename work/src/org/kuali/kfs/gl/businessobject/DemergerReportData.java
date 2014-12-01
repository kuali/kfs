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

/**
 * A class to encapsulate statistics generated during a demerger
 */
public class DemergerReportData {
    /**
     * Constructs a DemergerReportData instance
     */
    public DemergerReportData() {
    }

    private int errorTransactionsRead = 0;
    private int validTransactionsRead = 0;
    private int errorTransactionsSaved = 0;
    private int validTransactionsSaved = 0;
    private int offsetTransactionsBypassed = 0;
    private int capitalizationTransactionsBypassed = 0;
    private int liabilityTransactionsBypassed = 0;
    private int transferTransactionsBypassed = 0;
    private int costShareTransactionsBypassed = 0;
    private int costShareEncumbranceTransactionsBypassed = 0;

    /**
     * Adds the values from the parameter report data into this object.
     * 
     * @param anotherReport more demerger report data to add to the current demerger report data
     */
    public void incorporateReportData(DemergerReportData anotherReport) {
        errorTransactionsRead += anotherReport.errorTransactionsRead;
        validTransactionsRead += anotherReport.validTransactionsRead;
        errorTransactionsSaved += anotherReport.errorTransactionsSaved;
        validTransactionsSaved += anotherReport.validTransactionsSaved;
        offsetTransactionsBypassed += anotherReport.offsetTransactionsBypassed;
        capitalizationTransactionsBypassed += anotherReport.capitalizationTransactionsBypassed;
        liabilityTransactionsBypassed += anotherReport.liabilityTransactionsBypassed;
        transferTransactionsBypassed += anotherReport.transferTransactionsBypassed;
        costShareTransactionsBypassed += anotherReport.costShareTransactionsBypassed;
        costShareEncumbranceTransactionsBypassed += anotherReport.costShareEncumbranceTransactionsBypassed;
    }

    /**
     * Increments the count of error transactions read by 1
     */
    public void incrementErrorTransactionsRead() {
        errorTransactionsRead++;
    }

    /**
     * Increments the count of valid transactions read by 1
     */
    public void incrementValidTransactionsRead() {
        validTransactionsRead++;
    }
    
    /**
     * Increments the count of error transactions written by 1
     */
    public void incrementErrorTransactionsSaved() {
        errorTransactionsSaved++;
    }

    /**
     * Increments the count of valid transactions saved by 1
     */
    public void incrementValidTransactionsSaved() {
        validTransactionsSaved++;
    }

    /**
     * Increments the count of offset transactions bypassed by 1
     */
    public void incrementOffsetTransactionsBypassed() {
        offsetTransactionsBypassed++;
    }

    /**
     * Increments the count of capitalization transactions bypassed by 1
     */
    public void incrementCapitalizationTransactionsBypassed() {
        capitalizationTransactionsBypassed++;
    }

    /**
     * Increments the count of liability transactions bypassed by 1
     */
    public void incrementLiabilityTransactionsBypassed() {
        liabilityTransactionsBypassed++;
    }

    /**
     * Increments the count of transfer transactions bypassed by 1
     */
    public void incrementTransferTransactionsBypassed() {
        transferTransactionsBypassed++;
    }

    /**
     * Increments the count of cost share transactions bypassed by 1
     */
    public void incrementCostShareTransactionsBypassed() {
        costShareTransactionsBypassed++;
    }

    /**
     * Increments the count of cost share encumbrance transactions bypassed by 1
     */
    public void incrementCostShareEncumbranceTransactionsBypassed() {
        costShareEncumbranceTransactionsBypassed++;
    }

    /**
     * Returns the count of capitalization transactions bypassed
     * 
     * @return the count of capitalization transactions bypassed
     */
    public int getCapitalizationTransactionsBypassed() {
        return capitalizationTransactionsBypassed;
    }

    /**
     * Returns the count of cost share encumbranace transactions bypassed
     * 
     * @return the count of cost share encumbranace transactions bypassed
     */
    public int getCostShareEncumbranceTransactionsBypassed() {
        return costShareEncumbranceTransactionsBypassed;
    }

    /**
     * Returns the count of cost share transactions bypassed
     * 
     * @return the count of cost share transactions bypassed
     */
    public int getCostShareTransactionsBypassed() {
        return costShareTransactionsBypassed;
    }

    /**
     * Returns the count of error transactions read
     * 
     * @return the count of error transactions read
     */
    public int getErrorTransactionsRead() {
        return errorTransactionsRead;
    }

    /**
     * Returns the count of error transactions saved
     * 
     * @return the count of error transactions saved
     */
    public int getErrorTransactionsSaved() {
        return errorTransactionsSaved;
    }

    /**
     * Returns the count of liability transactions bypassed
     * 
     * @return the count of liability transactions bypassed
     */
    public int getLiabilityTransactionsBypassed() {
        return liabilityTransactionsBypassed;
    }

    /**
     * Returns the count of offset transactions bypassed
     * 
     * @return the count of offset transactions bypassed
     */
    public int getOffsetTransactionsBypassed() {
        return offsetTransactionsBypassed;
    }

    /**
     * Returns the count of transfer transactions bypassed
     * 
     * @return the count of transfer transactions bypassed
     */
    public int getTransferTransactionsBypassed() {
        return transferTransactionsBypassed;
    }

    /**
     * Returns the count of valid transactions saved
     * 
     * @return the count of valid transactions saved
     */
    public int getValidTransactionsSaved() {
        return validTransactionsSaved;
    }

    /**
     * Resets the number of error transactions read to the given amount
     * 
     * @param x the count of error transactions read to reset to
     */
    public void setErrorTransactionsRead(int x) {
        this.errorTransactionsRead = x;
    }

    /**
     * Resets the number of error transactions written to the given amount
     * 
     * @param x the count of error transactions written to reset to
     */
    public void setErrorTransactionWritten(int x) {
        this.errorTransactionsSaved = x;
    }

    /**
     * Sets the validTransactionsSaved attribute value.
     * 
     * @param validTransactionsSaved The validTransactionsSaved to set.
     */
    public void setValidTransactionsSaved(int validTransactionsSaved) {
        this.validTransactionsSaved = validTransactionsSaved;
    }
    
    /**
     * Returns the count of valid transactions read
     * 
     * @return the count of valid transactions read
     */
    public int getValidTransactionsRead() {
        return validTransactionsRead;
    }
    
    /**
     * Resets the number of valid transactions read to the given amount
     * 
     * @param x the count of valid transactions read to reset to
     */
    public void setValidTransactionsRead(int x) {
        this.validTransactionsRead = x;
    }
}
