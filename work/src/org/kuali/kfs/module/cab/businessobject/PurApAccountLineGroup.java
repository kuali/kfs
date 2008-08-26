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

import org.kuali.kfs.gl.businessobject.UniversityDate;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountHistory;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Accounting line grouped data for GL Line
 */
public class PurApAccountLineGroup extends AccountLineGroup {
    private PurApAccountingLineBase targetEntry;
    private List<PurApAccountingLineBase> sourceEntries = new ArrayList<PurApAccountingLineBase>();

    /**
     * Constructs a PurApAccountLineGroup from a PurApAccountingLineBase Line Entry
     * 
     * @param entry PurApAccountingLineBase Line
     */
    public PurApAccountLineGroup(PurApAccountingLineBase entry) {
        // TODO validate this
        if (ObjectUtils.isNotNull((PurApItem)entry.getPurapItem()) && ObjectUtils.isNotNull(((PurApItem)entry.getPurapItem()).getPurapDocument())) {
            UniversityDate currentUniversityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
            AccountsPayableDocumentBase document = ((PurApItem)entry.getPurapItem()).getPurapDocument();
            setUniversityFiscalYear(currentUniversityDate.getUniversityFiscalYear());
            // TODO - Remove hard coding when local testing is over
            // setUniversityFiscalPeriodCode(currentUniversityDate.getUniversityFiscalAccountingPeriod());
            setUniversityFiscalPeriodCode("01");
            setReferenceFinancialDocumentNumber(document.getPurchaseOrderIdentifier() != null ? document.getPurchaseOrderIdentifier().toString() : "");
            setDocumentNumber(document.getDocumentNumber());
        }
        else {
            throw new RuntimeException("Document is unknown");
        }
        setChartOfAccountsCode(entry.getChartOfAccountsCode());
        setAccountNumber(entry.getAccountNumber());
        setSubAccountNumber(entry.getSubAccountNumber());
        setFinancialObjectCode(entry.getFinancialObjectCode());
        setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        this.sourceEntries.add(entry);
        this.targetEntry = entry;
        if (CreditMemoAccountHistory.class.isAssignableFrom(entry.getClass())) {
            setAmount(entry.getAmount().multiply(NEGATIVE_ONE));
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
     * This method will combine multiple GL entries for the same account line group, so that m:n association is prevented in the
     * database. This could be a rare case that we need to address. First GL is used as the final target and rest of the GL entries
     * are adjusted.
     * 
     * @param entry PurApAccountingLineBase
     */
    public void combineEntry(PurApAccountingLineBase srcEntry) {
        this.sourceEntries.add(srcEntry);
        if (CreditMemoAccountHistory.class.isAssignableFrom(srcEntry.getClass())) {
            this.amount = targetEntry.getAmount().add(srcEntry.getAmount().multiply(NEGATIVE_ONE));
        }
        else {
            this.amount = targetEntry.getAmount().add(srcEntry.getAmount());
        }
        this.targetEntry.setAmount(this.amount);
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
