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
}
