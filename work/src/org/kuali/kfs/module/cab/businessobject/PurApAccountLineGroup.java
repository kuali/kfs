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

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountHistory;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Accounting line grouped data for GL Line
 */
public class PurApAccountLineGroup extends AccountLineGroup {
    private Logger LOG = Logger.getLogger(PurApAccountLineGroup.class);
    private PurApAccountingLineBase targetEntry;
    private List<PurApAccountingLineBase> sourceEntries = new ArrayList<PurApAccountingLineBase>();

    /**
     * Constructs a PurApAccountLineGroup from a PurApAccountingLineBase Line Entry
     * 
     * @param entry PurApAccountingLineBase Line
     */
    public PurApAccountLineGroup(PurApAccountingLineBase entry) {
        if (ObjectUtils.isNotNull((AccountsPayableItem) entry.getPurapItem()) && ObjectUtils.isNotNull(((AccountsPayableItem) entry.getPurapItem()).getPurapDocument())) {
            AccountsPayableDocumentBase document = ((AccountsPayableItem) entry.getPurapItem()).getPurapDocument();
            setReferenceFinancialDocumentNumber(document.getPurchaseOrderIdentifier() != null ? document.getPurchaseOrderIdentifier().toString() : "");
            setDocumentNumber(document.getDocumentNumber());
        }
        else {
            LOG.error("Could not load PurAP document details for " + entry.toString());
        }
        setUniversityFiscalYear(entry.getPostingYear());
        setUniversityFiscalPeriodCode(entry.getPostingPeriodCode());
        setChartOfAccountsCode(entry.getChartOfAccountsCode());
        setAccountNumber(entry.getAccountNumber());
        setSubAccountNumber(entry.getSubAccountNumber());
        setFinancialObjectCode(entry.getFinancialObjectCode());
        setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        this.sourceEntries.add(entry);
        this.targetEntry = entry;
        if (CreditMemoAccountHistory.class.isAssignableFrom(entry.getClass())) {
            setAmount(entry.getAmount().negated());
        }
        else {
            setAmount(entry.getAmount());
        }
    }

    /**
     * Returns true if input PurApAccountingLineBase entry belongs to this account group
     * 
     * @param entry PurApAccountingLineBase
     * @return true if PurApAccountingLineBase belongs to same account line group
     */
    public boolean isAccounted(PurApAccountingLineBase entry) {
        PurApAccountLineGroup test = new PurApAccountLineGroup(entry);
        return this.equals(test);
    }

    /**
     * This method will combine multiple Purap account entries for the same account line group.
     * 
     * @param entry PurApAccountingLineBase
     */
    public void combineEntry(PurApAccountingLineBase newEntry) {
        this.sourceEntries.add(newEntry);
        if (CreditMemoAccountHistory.class.isAssignableFrom(newEntry.getClass())) {
            this.amount = this.amount.add(newEntry.getAmount().negated());
        }
        else {
            this.amount = this.amount.add(newEntry.getAmount());
        }
        this.targetEntry.setAmount(this.targetEntry.getAmount().add(newEntry.getAmount()));
    }

    /**
     * Gets the targetEntry attribute.
     * 
     * @return Returns the targetEntry
     */
    public PurApAccountingLineBase getTargetEntry() {
        return targetEntry;
    }

    /**
     * Sets the targetEntry attribute.
     * 
     * @param targetEntry The targetEntry to set.
     */
    public void setTargetEntry(PurApAccountingLineBase targetGlEntry) {
        this.targetEntry = targetGlEntry;
    }

    /**
     * Gets the sourceEntries attribute.
     * 
     * @return Returns the sourceEntries
     */
    public List<PurApAccountingLineBase> getSourceEntries() {
        return sourceEntries;
    }

    /**
     * Sets the sourceEntries attribute.
     * 
     * @param sourceEntries The sourceEntries to set.
     */
    public void setSourceEntries(List<PurApAccountingLineBase> sourceGlEntries) {
        this.sourceEntries = sourceGlEntries;
    }
}
