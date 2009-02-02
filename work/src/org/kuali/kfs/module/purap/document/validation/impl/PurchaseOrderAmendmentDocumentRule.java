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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Rules for Purchase Order Amendment documents creation.
 */
public class PurchaseOrderAmendmentDocumentRule extends PurchaseOrderDocumentRule {

    /**
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= validateContainsAtLeastOneActiveItem(purapDocument);
        return valid;
    }

    /**
     * Overrides the method in PurchaseOrderDocumentRuleBase to add additional validations that are specific to Amendment.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchaseOrderDocumentRule#newIndividualItemValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument, java.lang.String, org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    @Override
    public boolean newIndividualItemValidation(PurchasingAccountsPayableDocument purapDocument, String documentType, PurApItem purapItem) {
        boolean valid = super.newIndividualItemValidation(purapDocument, documentType, purapItem);
        PurchaseOrderItem item = (PurchaseOrderItem)purapItem;
        String identifierString = item.getItemIdentifierString();
        if ((item.getItemInvoicedTotalQuantity() != null) && (!(item.getItemInvoicedTotalQuantity()).isZero())) {
            if (item.getItemQuantity() == null) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMND_NULL, "Item Quantity", identifierString);
            }
            else if (item.getItemQuantity().compareTo(item.getItemInvoicedTotalQuantity()) < 0) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMND_INVALID, "Item Quantity", identifierString);
            }
        }

        if (item.getItemInvoicedTotalAmount() != null) {
            KualiDecimal total = item.getTotalAmount();
            if ((total == null) || total.compareTo(item.getItemInvoicedTotalAmount()) < 0) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMND_INVALID_AMT, "Item Extended Price", identifierString);
            }
        }
        
        return valid;
    }

    
    /**
     * Validates that the given document contains at least one active item.
     * 
     * @param purapDocument A PurchasingAccountsPayableDocument. (Should contain PurchaseOrderItems.)
     * @return True if the document contains at least one active item
     */
    private boolean validateContainsAtLeastOneActiveItem(PurchasingAccountsPayableDocument purapDocument) {
        List<PurApItem> items = purapDocument.getItems();
        for (PurApItem item : items) {
            if (((PurchaseOrderItem) item).isItemActiveIndicator() && (!((PurchaseOrderItem) item).isEmpty() && item.getItemType().isLineItemIndicator())) {
                return true;
            }
        }
        String documentType = getDocumentTypeLabel(purapDocument.getDocumentHeader().getWorkflowDocument().getDocumentType());
        GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_REQUIRED, documentType);

        return false;
    }
    
    /**
     * Overrides to provide validation for PurchaseOrderAmendmentDocument. 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#validateCommodityCodes(org.kuali.kfs.module.purap.businessobject.PurApItem, boolean)
     */
    @Override
    protected boolean validateCommodityCodes(PurApItem item, boolean commodityCodeRequired) {
        //If the item is inactive then don't need any of the following validations.
        if (!((PurchaseOrderItem)item).isItemActiveIndicator()) {
            return true;
        }
        else {
            return super.validateCommodityCodes(item, commodityCodeRequired);
        }
    }

    /**
     * Overrides the method in PurchasingDocumentRuleBase so that we'll return true
     * if the item has been previously saved to the database and we'll only check for
     * the commodity code active flag if the item has not been previously saved to
     * the database. 
     * 
     * @param item
     * @param commodityCodeRequired
     * @return
     */
    protected boolean validateThatCommodityCodeIsActive(PurApItem item) {
        if (item.getVersionNumber() != null) {
            return true;
        }
        else {
            if (!((PurchasingItemBase)item).getCommodityCode().isActive()) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE, " in " + item.getItemIdentifierString());
                return false;
            }
            return true;
        }
    }
    
    /**
     * Overrides to disable account validation for PO amendments when there are new unordered items
     * added to the PO.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#requiresAccountValidationOnAllEnteredItems(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {
                
        boolean requiresAccountValidation = false;
        
        //if a new unordered item has been added to the purchase order, this is due to Receiving Line document,
        // and items should not have account validation performed initially, until new unordered items review.
        if( SpringContext.getBean(PurchaseOrderService.class).hasNewUnorderedItem((PurchaseOrderDocument)document) &&
                !SpringContext.getBean(PurapService.class).isDocumentStoppedInRouteNode(document, "New Unordered Items") ){
            requiresAccountValidation = false;
        }else{
            requiresAccountValidation = super.requiresAccountValidationOnAllEnteredItems(document);
        }
        
        return requiresAccountValidation;
    }
    
    @Override
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {

        if( SpringContext.getBean(PurapService.class).isDocumentStoppedInRouteNode((PurchasingAccountsPayableDocument)financialDocument, "New Unordered Items") ){
            //DO NOTHING: do not check that user owns acct lines; at this level, they can edit all accounts on PO amendment
            return true;
        }else{
            return super.checkAccountingLineAccountAccessibility(financialDocument, accountingLine, action);
        }
    }
    
    /**
     * Overrides the method in PurchasingDocumentRuleBase to provide additional
     * validation condition. If the accounts on the item are editable in the amendment document then
     * we should continue doing the processAccountValidation in the superclass, otherwise
     * we should just return true so that the account won't be validated, because if
     * the items contain accounts that aren't editable, it doesn't make sense to give
     * the user account validation errors.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#processAccountValidation(org.kuali.kfs.sys.document.AccountingDocument, java.util.List, java.lang.String)
     */
    @Override
    public boolean processAccountValidation(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        PurchaseOrderDocument document = (PurchaseOrderDocument)accountingDocument;
        PurchaseOrderItem itemLine = (PurchaseOrderItem) document.getItemByStringIdentifier(itemLineNumber);
        if (itemLine.isItemActiveIndicator() && (! (document.getContainsUnpaidPaymentRequestsOrCreditMemos() && itemLine.getItemInvoicedTotalAmount() != null))) {
            //This means the accounts on the item are editable, so we'll call super's processAccountValidation.
            return super.processAccountValidation(accountingDocument, purAccounts, itemLineNumber);
        }
        else {
            //This means the accounts on the item are not editable, so we'll return true so that it won't do any further validations on the accounts.
            return true;
        }
    }
}
