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
package org.kuali.kfs.module.cab.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountRevision;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Accounting line grouped data for GL Line
 */
public class PurApAccountLineGroup extends AccountLineGroup {

    private Logger LOG = Logger.getLogger(PurApAccountLineGroup.class);
    private List<PurApAccountingLineBase> sourceEntries = new ArrayList<PurApAccountingLineBase>();

    /**
     * Constructs a PurApAccountLineGroup from a PurApAccountingLineBase Line Entry
     * 
     * @param entry PurApAccountingLineBase Line
     */
    protected PurApAccountLineGroup() {

    }

    public PurApAccountLineGroup(PurApAccountingLineBase entry) {
        entry.refreshReferenceObject(CabPropertyConstants.PURAP_ITEM);
        AccountsPayableItem purapItem = (AccountsPayableItem) entry.getPurapItem();
        if (ObjectUtils.isNotNull(purapItem)) {
            purapItem.refreshReferenceObject(CabPropertyConstants.PURAP_DOCUMENT);
            AccountsPayableDocumentBase purapDocument = ((AccountsPayableItem) entry.getPurapItem()).getPurapDocument();
            if (ObjectUtils.isNotNull(purapDocument)) {
                setReferenceFinancialDocumentNumber(purapDocument.getPurchaseOrderIdentifier() != null ? purapDocument.getPurchaseOrderIdentifier().toString() : "");
                setDocumentNumber(purapDocument.getDocumentNumber());
            }
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
        setOrganizationReferenceId(entry.getOrganizationReferenceId());
        setProjectCode(entry.getProjectCode());
        this.sourceEntries.add(entry);
        if (CreditMemoAccountRevision.class.isAssignableFrom(entry.getClass())) {
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
        if (CreditMemoAccountRevision.class.isAssignableFrom(newEntry.getClass())) {
            this.amount = this.amount.add(newEntry.getAmount().negated());
        }
        else {
            this.amount = this.amount.add(newEntry.getAmount());
        }
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
