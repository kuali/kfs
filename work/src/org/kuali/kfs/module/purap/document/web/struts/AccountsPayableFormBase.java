/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.web.struts.form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.util.PurApItemUtils;

/**
 * Form class for Purchasing documents.
 */
public class AccountsPayableFormBase extends PurchasingAccountsPayableFormBase {

    private PurApItem newPurchasingItemLine;
    private Boolean notOtherDeliveryBuilding;
    private boolean calculated;
    private int countOfAboveTheLine = 0;
    private int countOfBelowTheLine = 0;

    /**
     * Constructs an AccountsPayableForm instance and sets up the appropriately casted document.
     */
    public AccountsPayableFormBase() {
        super();
        calculated = false;
        notOtherDeliveryBuilding = true;
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

    public Boolean getNotOtherDeliveryBuilding() {
        return notOtherDeliveryBuilding;
    }

    public void setNotOtherDeliveryBuilding(Boolean notOtherDeliveryBuilding) {
        this.notOtherDeliveryBuilding = notOtherDeliveryBuilding;
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
     * Determines if the current user is an AP user and returns true, false otherwise.
     * 
     * @return - true if current user is AP user, false otherwise.
     */
    public boolean isApUser() {

        boolean apUser = false;
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();

        String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);

        if (user.isMember(apGroup)) {
            apUser = true;
        }

        return apUser;
    }

    /**
     * Adds a new button to the extra buttons collection.
     * 
     * @param property - property for button
     * @param source - location of image
     * @param altText - alternate text for button if images don't appear
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }

    /**
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        
        super.populate(request);
        
        AccountsPayableDocument apDoc = (AccountsPayableDocument)this.getDocument();
        //update po doc
        apDoc.setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(apDoc.getPurchaseOrderIdentifier()));
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