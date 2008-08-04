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

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.sys.KFSConstants;

/**
 * Accounting line grouped data for GL Line
 */
public class PurchasingAccountsPayableAccountLineGroup extends AccountLineGroup {
    private PurApAccountingLine targetEntry;
    private List<PurApAccountingLine> sourceEntries = new ArrayList<PurApAccountingLine>();

    /**
     * Constructs a PurchasingAccountsPayableAccountLineGroup from a PurApAccountingLine Line Entry
     * 
     * @param entry PurApAccountingLine Line
     */
    public PurchasingAccountsPayableAccountLineGroup(PurApAccountingLine entry) {
        // TODO
        // setUniversityFiscalYear(entry.getUniversityFiscalYear());
        setChartOfAccountsCode(entry.getChartOfAccountsCode());
        setAccountNumber(entry.getAccountNumber());
        setSubAccountNumber(entry.getSubAccountNumber());
        setFinancialObjectCode(entry.getFinancialObjectCode());
        setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        // setUniversityFiscalPeriodCode(entry.getUniversityFiscalPeriodCode());
        setDocumentNumber(entry.getDocumentNumber());
        // setReferenceFinancialDocumentNumber(entry.getReferenceFinancialDocumentNumber());
        this.sourceEntries.add(entry);
        this.targetEntry = entry;
        setAmount(entry.getAmount());
    }

    /**
     * Returns true if input PurApAccountingLine entry belongs to this account group
     * 
     * @param entry PurApAccountingLine
     * @return true if PurApAccountingLine belongs to same account line group
     */
    public boolean isAccounted(PurApAccountingLine entry) {
        PurchasingAccountsPayableAccountLineGroup test = new PurchasingAccountsPayableAccountLineGroup(entry);
        return this.equals(test);
    }

    /**
     * This method will combine multiple GL entries for the same account line group, so that m:n association is prevented in the
     * database. This could be a rare case that we need to address. First GL is used as the final target and rest of the GL entries
     * are adjusted.
     * 
     * @param entry PurApAccountingLine
     */
    public void combineEntry(PurApAccountingLine srcEntry) {
        this.sourceEntries.add(srcEntry);
        this.targetEntry.setAmount(targetEntry.getAmount().add(srcEntry.getAmount()));
        this.amount = targetEntry.getAmount().add(srcEntry.getAmount());
    }

    /**
     * Gets the targetEntry attribute.
     * 
     * @return Returns the targetEntry
     */
    public PurApAccountingLine getTargetEntry() {
        return targetEntry;
    }

    /**
     * Sets the targetEntry attribute.
     * 
     * @param targetEntry The targetEntry to set.
     */
    public void setTargetEntry(PurApAccountingLine targetGlEntry) {
        this.targetEntry = targetGlEntry;
    }

    /**
     * Gets the sourceEntries attribute.
     * 
     * @return Returns the sourceEntries
     */
    public List<PurApAccountingLine> getSourceEntries() {
        return sourceEntries;
    }

    /**
     * Sets the sourceEntries attribute.
     * 
     * @param sourceEntries The sourceEntries to set.
     */
    public void setSourceEntries(List<PurApAccountingLine> sourceGlEntries) {
        this.sourceEntries = sourceGlEntries;
    }
}
