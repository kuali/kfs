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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Struts Action Form for Accounts Payable documents.
 */
public class AccountsPayableFormBase extends PurchasingAccountsPayableFormBase {

    protected PurApItem newPurchasingItemLine;
    protected boolean calculated;
    protected int countOfAboveTheLine = 0;
    protected int countOfBelowTheLine = 0;

    /**
     * Constructs an AccountsPayableForm instance and sets up the appropriately casted document.
     */
    public AccountsPayableFormBase() {
        super();
        calculated = false;
    }

    public PurApItem getNewPurchasingItemLine() {
        return newPurchasingItemLine;
    }

    public void setNewPurchasingItemLine(PurApItem newPurchasingItemLine) {
        this.newPurchasingItemLine = newPurchasingItemLine;
    }

    public PurApItem getAndResetNewPurchasingItemLine() {
        PurApItem aPurchasingItemLine = getNewPurchasingItemLine();
        setNewPurchasingItemLine(setupNewPurchasingItemLine());
        return aPurchasingItemLine;
    }

    /**
     * This method should be overriden (or see accountingLines for an alternate way of doing this with newInstance)
     * 
     * @return - null, enforces overriding
     */
    public PurApItem setupNewPurchasingItemLine() {
        return null;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    public int getCountOfAboveTheLine() {
        return countOfAboveTheLine;
    }

    public void setCountOfAboveTheLine(int countOfAboveTheLine) {
        this.countOfAboveTheLine = countOfAboveTheLine;
    }

    public int getCountOfBelowTheLine() {
        return countOfBelowTheLine;
    }

    public void setCountOfBelowTheLine(int countOfBelowTheLine) {
        this.countOfBelowTheLine = countOfBelowTheLine;
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        AccountsPayableDocument apDoc = (AccountsPayableDocument) this.getDocument();

        // update po doc
        if (apDoc.getPurchaseOrderIdentifier() != null) {
            apDoc.setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(apDoc.getPurchaseOrderIdentifier()));
        }

        // update counts after populate
        updateItemCounts();
    }

    /**
     * Updates item counts for display
     */
    public void updateItemCounts() {
        List<PurApItem> items = ((AccountsPayableDocument) this.getDocument()).getItems();
        countOfBelowTheLine = PurApItemUtils.countBelowTheLineItems(items);
        countOfAboveTheLine = items.size() - countOfBelowTheLine;
    }

}
