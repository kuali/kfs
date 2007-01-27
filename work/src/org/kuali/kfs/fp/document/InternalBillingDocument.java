/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.bo.AccountingLineParserBase;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.InternalBillingItem;


/**
 * This is the business object that represents the InternalBillingDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: Expense
 * and Income.
 * 
 * 
 */
public class InternalBillingDocument extends TransactionalDocumentBase {

    private List items;
    private Integer nextItemLineNumber;

    /**
     * Initializes the array lists and some basic info.
     */
    public InternalBillingDocument() {
        super();
        setItems(new ArrayList());
        this.nextItemLineNumber = new Integer(1);
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
     * 
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
     * Allows items (in addition to accounting lines) to be deleted from the database after being saved there.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getItems());
        return managedLists;
    }

    /**
     * Iterates through the list of items and sums up their totals.
     * 
     * @return the total
     */
    public KualiDecimal getItemTotal() {
        KualiDecimal total = new KualiDecimal(0);
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
        return Constants.INCOME;
    }

    /**
     * @return "Expense"
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.EXPENSE;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new AccountingLineParserBase();
    }
}
