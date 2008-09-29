/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Accounting line grouped data for a Pending GL Entry
 */
public class PendingGlAccountLineGroup extends AccountLineGroup {
    private GeneralLedgerPendingEntry targetEntry;
    private List<GeneralLedgerPendingEntry> sourceEntries = new ArrayList<GeneralLedgerPendingEntry>();

    /**
     * Constructs a PendingGlAccountLineGroup from a Pending GL Entry
     * 
     * @param entry Pending GL Entry
     */
    public PendingGlAccountLineGroup(GeneralLedgerPendingEntry entry) {
        setUniversityFiscalYear(entry.getUniversityFiscalYear());
        setChartOfAccountsCode(entry.getChartOfAccountsCode());
        setAccountNumber(entry.getAccountNumber());
        setSubAccountNumber(entry.getSubAccountNumber());
        setFinancialObjectCode(entry.getFinancialObjectCode());
        setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        setUniversityFiscalPeriodCode(entry.getUniversityFiscalPeriodCode());
        setDocumentNumber(entry.getDocumentNumber());
        setReferenceFinancialDocumentNumber(entry.getReferenceFinancialDocumentNumber());
        this.sourceEntries.add(entry);
        this.targetEntry = entry;

        KualiDecimal amount = entry.getTransactionLedgerEntryAmount();
        if (KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
            // negate the amount
            setAmount(amount.multiply(NEGATIVE_ONE));
        }
        else {
            setAmount(amount);
        }
    }

    /**
     * Combines a pending GL entry if they match by account line group
     * 
     * @param newEntry Source Pending GL Entry
     */
    public void combineEntry(GeneralLedgerPendingEntry newEntry) {
        this.sourceEntries.add(newEntry);
        KualiDecimal newAmt = newEntry.getTransactionLedgerEntryAmount();
        String newDebitCreditCode = newEntry.getTransactionDebitCreditCode();

        KualiDecimal targetAmount = this.targetEntry.getTransactionLedgerEntryAmount();
        String targetDebitCreditCode = this.targetEntry.getTransactionDebitCreditCode();

        // if debit/credit code is same then just add the amount
        if (targetDebitCreditCode.equals(newDebitCreditCode)) {
            targetAmount = targetAmount.add(newAmt);
        }
        else {
            // if debit/credit code is not the same and new amount is greater, toggle the debit/credit code
            if (newAmt.isGreaterThan(targetAmount)) {
                targetDebitCreditCode = newDebitCreditCode;
                targetAmount = newAmt.subtract(targetAmount);
            }
            else {
                // if debit/credit code is not the same and current amount is greater or equal
                targetAmount = targetAmount.subtract(newAmt);
            }
        }
        this.targetEntry.setTransactionDebitCreditCode(targetDebitCreditCode);
        this.targetEntry.setTransactionLedgerEntryAmount(targetAmount);
        // re-compute the absolute value of amount
        if (KFSConstants.GL_CREDIT_CODE.equals(targetDebitCreditCode)) {
            setAmount(targetAmount.multiply(NEGATIVE_ONE));
        }
    }
}
