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
package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.businessobject.InternalBillingItem;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;


/**
 * This is the business object that represents the InternalBillingDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: Expense
 * and Income.
 */
public class InternalBillingDocument extends CapitalAccountingLinesDocumentBase implements Copyable, Correctable, AmountTotaling {

    protected List items;
    protected Integer nextItemLineNumber;

    protected transient CapitalAssetManagementModuleService capitalAssetManagementModuleService;

    /**
     * Initializes the array lists and some basic info.
     */
    public InternalBillingDocument() {
        super();
        setItems(new ArrayList());
        this.nextItemLineNumber = new Integer(1);
    }

    /**
     * This method determines if an accounting line is a debit accounting line by calling IsDebitUtils.isDebitConsideringSection().
     * 
     * @param transactionalDocument The document containing the accounting line being analyzed.
     * @param accountingLine The accounting line being reviewed to determine if it is a debit line or not.
     * @return True if the accounting line is a debit accounting line, false otherwise.
     * @see IsDebitUtils#isDebitConsideringSection(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        return isDebitUtils.isDebitConsideringSection(this, (AccountingLine) postable);
    }
    
    /**
     * Adds a new item to the item list.
     * 
     * @param item
     */
    public void addItem(InternalBillingItem item) {
        item.setItemSequenceId(this.nextItemLineNumber);
        this.items.add(item);
        this.nextItemLineNumber = new Integer(this.nextItemLineNumber.intValue() + 1);
    }

    /**
     * Retrieve a particular item at a given index in the list of items. For Struts, the requested item and any intervening ones are
     * initialized if necessary.
     * 
     * @param index
     * @return the item
     */
    public InternalBillingItem getItem(int index) {
        while (getItems().size() <= index) {
            getItems().add(new InternalBillingItem());
        }
        return (InternalBillingItem) getItems().get(index);
    }

    /**
     * @return Returns the items.
     */
    public List getItems() {
        return items;
    }

    /**
     * Iterates through the list of items and sums up their totals.
     * 
     * @return the total
     */
    public KualiDecimal getItemTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            total = total.add(((InternalBillingItem) iterator.next()).getTotal());
        }
        return total;
    }

    /**
     * Retrieves the next item line number.
     * 
     * @return The next available item line number
     */
    public Integer getNextItemLineNumber() {
        return this.nextItemLineNumber;
    }

    /**
     * @param items
     */
    public void setItems(List items) {
        this.items = items;
    }

    /**
     * Setter for OJB to get from database and JSP to maintain state in hidden fields. This property is also incremented by the
     * <code>addItem</code> method.
     * 
     * @param nextItemLineNumber
     */
    public void setNextItemLineNumber(Integer nextItemLineNumber) {
        this.nextItemLineNumber = nextItemLineNumber;
    }

    /**
     * @return "Income"
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.INCOME;
    }

    /**
     * @return "Expense"
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.EXPENSE;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        this.getCapitalAssetManagementModuleService().deleteDocumentAssetLocks(this);
    }


    /**
     * @see org.kuali.rice.krad.document.DocumentBase#postProcessSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
            this.getCapitalAssetManagementModuleService().generateCapitalAssetLock(this, documentTypeName);
        }
    }


    /**
     * @return CapitalAssetManagementModuleService
     */
    CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        if (capitalAssetManagementModuleService == null) {
            capitalAssetManagementModuleService = SpringContext.getBean(CapitalAssetManagementModuleService.class);
        }
        return capitalAssetManagementModuleService;
    }
}
