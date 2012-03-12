/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cab.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Accounting line grouped data for GL Line
 */
public class GlAccountLineGroup extends AccountLineGroup {
    private Entry targetEntry;
    private List<Entry> sourceEntries = new ArrayList<Entry>();
    private List<PurApAccountingLineBase> matchedPurApAcctLines = new ArrayList<PurApAccountingLineBase>();

    /**
     * Constructs a GlAccountLineGroup from a GL Line Entry
     * 
     * @param entry GL Line
     */
    public GlAccountLineGroup(Entry entry) {
        setUniversityFiscalYear(entry.getUniversityFiscalYear());
        setChartOfAccountsCode(entry.getChartOfAccountsCode());
        setAccountNumber(entry.getAccountNumber());
        setSubAccountNumber(entry.getSubAccountNumber());
        setFinancialObjectCode(entry.getFinancialObjectCode());
        setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        setUniversityFiscalPeriodCode(entry.getUniversityFiscalPeriodCode());
        setDocumentNumber(entry.getDocumentNumber());
        setReferenceFinancialDocumentNumber(entry.getReferenceFinancialDocumentNumber());
        setOrganizationReferenceId(entry.getOrganizationReferenceId());
        setProjectCode(entry.getProjectCode());
        this.sourceEntries.add(entry);
        this.targetEntry = (Entry) ObjectUtils.deepCopy(entry);

        KualiDecimal amount = entry.getTransactionLedgerEntryAmount();
        if (KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
            // negate the amount
            setAmount(amount.negated());
        }
        else {
            setAmount(amount);
        }
    }

    /**
     * Returns true if input GL entry belongs to this account group
     * 
     * @param entry Entry
     * @return true if Entry belongs to same account line group
     */
    public boolean isAccounted(Entry entry) {
        GlAccountLineGroup test = new GlAccountLineGroup(entry);
        return this.equals(test);
    }

    /**
     * This method will combine multiple GL entries for the same account line group, so that m:n association is prevented in the
     * database. This could be a rare case that we need to address. First GL is used as the final target and rest of the GL entries
     * are adjusted.
     * 
     * @param entry
     */
    public void combineEntry(Entry newEntry) {
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
            setAmount(targetAmount.negated());
        }
        else {
            setAmount(targetAmount);
        }
    }

    /**
     * Gets the targetEntry attribute.
     * 
     * @return Returns the targetEntry
     */
    public Entry getTargetEntry() {
        return targetEntry;
    }

    /**
     * Sets the targetEntry attribute.
     * 
     * @param targetEntry The targetEntry to set.
     */
    public void setTargetEntry(Entry targetGlEntry) {
        this.targetEntry = targetGlEntry;
    }

    /**
     * Gets the sourceEntries attribute.
     * 
     * @return Returns the sourceEntries
     */
    public List<Entry> getSourceEntries() {
        return sourceEntries;
    }

    /**
     * Sets the sourceEntries attribute.
     * 
     * @param sourceEntries The sourceEntries to set.
     */
    public void setSourceEntries(List<Entry> sourceGlEntries) {
        this.sourceEntries = sourceGlEntries;
    }

    /**
     * Gets the matchedPurApAcctLines attribute.
     * 
     * @return Returns the matchedPurApAcctLines.
     */
    public List<PurApAccountingLineBase> getMatchedPurApAcctLines() {
        return matchedPurApAcctLines;
    }

    /**
     * Sets the matchedPurApAcctLines attribute value.
     * 
     * @param matchedPurApAcctLines The matchedPurApAcctLines to set.
     */
    public void setMatchedPurApAcctLines(List<PurApAccountingLineBase> matchedPurApAcctLines) {
        this.matchedPurApAcctLines = matchedPurApAcctLines;
    }
}
