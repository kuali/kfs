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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class holds information about the sums of a list of origin entries. This information includes
 * total credit amount, debit amount, other amount, number of credit entries, number of debit entries,
 * and number of "other" entries
 */
public class OriginEntryTotals {

    protected KualiDecimal creditAmount;
    protected KualiDecimal debitAmount;
    protected KualiDecimal otherAmount;
    protected int numCreditEntries;
    protected int numDebitEntries;
    protected int numOtherEntries;

    public OriginEntryTotals() {
        creditAmount = KualiDecimal.ZERO;
        debitAmount = KualiDecimal.ZERO;
        otherAmount = KualiDecimal.ZERO;
        numCreditEntries = 0;
        numDebitEntries = 0;
        numOtherEntries = 0;
    }

    /**
     * Gets the creditAmount attribute.
     * 
     * @return Returns the creditAmount.
     */
    public KualiDecimal getCreditAmount() {
        return creditAmount;
    }

    /**
     * Sets the creditAmount attribute value.
     * 
     * @param creditAmount The creditAmount to set.
     */
    public void setCreditAmount(KualiDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    /**
     * Gets the debitAmount attribute.
     * 
     * @return Returns the debitAmount.
     */
    public KualiDecimal getDebitAmount() {
        return debitAmount;
    }

    /**
     * Sets the debitAmount attribute value.
     * 
     * @param debitAmount The debitAmount to set.
     */
    public void setDebitAmount(KualiDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    /**
     * Gets the numCreditEntries attribute.
     * 
     * @return Returns the numCreditEntries.
     */
    public int getNumCreditEntries() {
        return numCreditEntries;
    }

    /**
     * Sets the numCreditEntries attribute value.
     * 
     * @param numCreditEntries The numCreditEntries to set.
     */
    public void setNumCreditEntries(int numCreditEntries) {
        this.numCreditEntries = numCreditEntries;
    }

    /**
     * Gets the numDebitEntries attribute.
     * 
     * @return Returns the numDebitEntries.
     */
    public int getNumDebitEntries() {
        return numDebitEntries;
    }

    /**
     * Sets the numDebitEntries attribute value.
     * 
     * @param numDebitEntries The numDebitEntries to set.
     */
    public void setNumDebitEntries(int numDebitEntries) {
        this.numDebitEntries = numDebitEntries;
    }

    /**
     * Gets the numOtherEntries attribute.
     * 
     * @return Returns the numOtherEntries.
     */
    public int getNumOtherEntries() {
        return numOtherEntries;
    }

    /**
     * Sets the numOtherEntries attribute value.
     * 
     * @param numOtherEntries The numOtherEntries to set.
     */
    public void setNumOtherEntries(int numOtherEntries) {
        this.numOtherEntries = numOtherEntries;
    }

    /**
     * Gets the otherAmount attribute.
     * 
     * @return Returns the otherAmount.
     */
    public KualiDecimal getOtherAmount() {
        return otherAmount;
    }

    /**
     * Sets the otherAmount attribute value.
     * 
     * @param otherAmount The otherAmount to set.
     */
    public void setOtherAmount(KualiDecimal otherAmount) {
        this.otherAmount = otherAmount;
    }

    /**
     * This method adds amount from origin entries and increments number totals for the appropriate type
     * (i.e. credit, debit, or other).
     * 
     * @param entries
     */
    public void addToTotals(Iterator<OriginEntryFull> entries) {
        while (entries.hasNext()) {
            OriginEntryFull originEntry = entries.next();
            if (KFSConstants.GL_CREDIT_CODE.equals(originEntry.getTransactionDebitCreditCode())) {
                creditAmount = creditAmount.add(originEntry.getTransactionLedgerEntryAmount());
                numCreditEntries++;
            }
            else if (KFSConstants.GL_DEBIT_CODE.equals(originEntry.getTransactionDebitCreditCode())) {
                debitAmount = debitAmount.add(originEntry.getTransactionLedgerEntryAmount());
                numDebitEntries++;
            }
            else {
                otherAmount = otherAmount.add(originEntry.getTransactionLedgerEntryAmount());
                numOtherEntries++;
                ;
            }
        }
    }
    
    /**
     * This method adds amount from origin entry and increments number totals for the appropriate type (i.e. credit, debit, or
     * other).
     * 
     * @param entry
     */
    public void addToTotals(OriginEntryFull originEntry) {
        if (KFSConstants.GL_CREDIT_CODE.equals(originEntry.getTransactionDebitCreditCode())) {
            creditAmount = creditAmount.add(originEntry.getTransactionLedgerEntryAmount());
            numCreditEntries++;
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(originEntry.getTransactionDebitCreditCode())) {
            debitAmount = debitAmount.add(originEntry.getTransactionLedgerEntryAmount());
            numDebitEntries++;
        }
        else {
            otherAmount = otherAmount.add(originEntry.getTransactionLedgerEntryAmount());
            numOtherEntries++;
        }
    }

    /**
     * Adds up the values in the parameter totals object to the corresponding fields in this object
     * 
     * @param anotherTotals another OriginEntryTotals to add to this OriginEntryTotals totals
     */
    public void incorporateTotals(OriginEntryTotals anotherTotals) {
        creditAmount = creditAmount.add(anotherTotals.creditAmount);
        debitAmount = debitAmount.add(anotherTotals.debitAmount);
        otherAmount = otherAmount.add(anotherTotals.otherAmount);
        numCreditEntries += anotherTotals.numCreditEntries;
        numDebitEntries += anotherTotals.numDebitEntries;
        numOtherEntries += anotherTotals.numOtherEntries;
    }
}
