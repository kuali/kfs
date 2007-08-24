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
package org.kuali.module.gl.service.impl.scrubber;

public class DemergerReportData {
    public DemergerReportData() {
    }

    private int errorTransactionsRead = 0;
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
     * @param anotherReport
     */
    public void incorporateReportData(DemergerReportData anotherReport) {
        errorTransactionsRead += anotherReport.errorTransactionsRead;
        errorTransactionsSaved += anotherReport.errorTransactionsSaved;
        validTransactionsSaved += anotherReport.validTransactionsSaved;
        offsetTransactionsBypassed += anotherReport.offsetTransactionsBypassed;
        capitalizationTransactionsBypassed += anotherReport.capitalizationTransactionsBypassed;
        liabilityTransactionsBypassed += anotherReport.liabilityTransactionsBypassed;
        transferTransactionsBypassed += anotherReport.transferTransactionsBypassed;
        costShareTransactionsBypassed += anotherReport.costShareTransactionsBypassed;
        costShareEncumbranceTransactionsBypassed += anotherReport.costShareEncumbranceTransactionsBypassed;
    }
    
    public void incrementErrorTransactionsRead() {
        errorTransactionsRead++;
    }

    public void incrementErrorTransactionsSaved() {
        errorTransactionsSaved++;
    }

    public void incrementValidTransactionsSaved() {
        validTransactionsSaved++;
    }

    public void incrementOffsetTransactionsBypassed() {
        offsetTransactionsBypassed++;
    }

    public void incrementCapitalizationTransactionsBypassed() {
        capitalizationTransactionsBypassed++;
    }

    public void incrementLiabilityTransactionsBypassed() {
        liabilityTransactionsBypassed++;
    }

    public void incrementTransferTransactionsBypassed() {
        transferTransactionsBypassed++;
    }

    public void incrementCostShareTransactionsBypassed() {
        costShareTransactionsBypassed++;
    }

    public void incrementCostShareEncumbranceTransactionsBypassed() {
        costShareEncumbranceTransactionsBypassed++;
    }

    public int getCapitalizationTransactionsBypassed() {
        return capitalizationTransactionsBypassed;
    }

    public int getCostShareEncumbranceTransactionsBypassed() {
        return costShareEncumbranceTransactionsBypassed;
    }

    public int getCostShareTransactionsBypassed() {
        return costShareTransactionsBypassed;
    }

    public int getErrorTransactionsRead() {
        return errorTransactionsRead;
    }

    public int getErrorTransactionsSaved() {
        return errorTransactionsSaved;
    }

    public int getLiabilityTransactionsBypassed() {
        return liabilityTransactionsBypassed;
    }

    public int getOffsetTransactionsBypassed() {
        return offsetTransactionsBypassed;
    }

    public int getTransferTransactionsBypassed() {
        return transferTransactionsBypassed;
    }

    public int getValidTransactionsSaved() {
        return validTransactionsSaved;
    }

    public void setErrorTransactionsRead(int x) {
        this.errorTransactionsRead = x;
    }

    public void setErrorTransactionWritten(int x) {
        this.errorTransactionsSaved = x;
    }

    /**
     * Sets the validTransactionsSaved attribute value.
     * @param validTransactionsSaved The validTransactionsSaved to set.
     */
    public void setValidTransactionsSaved(int validTransactionsSaved) {
        this.validTransactionsSaved = validTransactionsSaved;
    }
}
