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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;

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
        // Check that the user is in purchasing workgroup.
        valid &= SpringContext.getBean(PurchaseOrderService.class).isPurchasingUser((PurchaseOrderDocument)purapDocument, "amend");
        valid &= validateContainsAtLeastOneActiveItem(purapDocument);
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
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(purapDocument.getDocumentHeader().getWorkflowDocument().getDocumentType()).getLabel();

        GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_REQUIRED, documentType);
        return false;
    }
    
    /**
     * Overrides to provide validation for PurchaseOrderAmendmentDocument. 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#validateCommodityCodes(org.kuali.kfs.module.purap.businessobject.PurApItem, boolean)
     */
    @Override
    protected boolean validateCommodityCodes(PurApItem item, boolean commodityCodeRequired) {
        boolean valid = true;
        String identifierString = item.getItemIdentifierString();
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        
        //If the item is inactive then don't need any of the following validations.
        if (!((PurchaseOrderItem)purItem).isItemActiveIndicator()) {
            return true;
        }
        
        //This validation is only needed if the commodityCodeRequired system parameter is true
        if (commodityCodeRequired && StringUtils.isBlank(purItem.getPurchasingCommodityCode()) ) {
            //This is the case where the commodity code is required but the item does not currently contain the commodity code.
            valid = false;
            String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                                    getDataDictionary().getBusinessObjectEntry(CommodityCode.class.getName()).
                                    getAttributeDefinition(PurapPropertyConstants.ITEM_COMMODITY_CODE).getLabel();
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + identifierString);
        }
        else if (StringUtils.isNotBlank(purItem.getPurchasingCommodityCode())) {
            //Find out whether the commodity code has existed in the database
            Map fieldValues = new HashMap<String, String>();
            fieldValues.put(PurapPropertyConstants.ITEM_COMMODITY_CODE, purItem.getPurchasingCommodityCode());
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(CommodityCode.class, fieldValues) != 1) {
                //This is the case where the commodity code on the item does not exist in the database.
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, PurapKeyConstants.PUR_COMMODITY_CODE_INVALID,  " in " + identifierString);
            }
            //Only validate this if the item has not been saved to the database
            else if (purItem.getVersionNumber() == null && !purItem.getCommodityCode().isActive()) {
                //This is the case where the commodity code on the item is not active.
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE, " in " + identifierString);
            }
        }
        
        return valid;
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
        /* Well, the following doesn't always work, not for below-line items. This caused Number Format Exception. 
        //This is because the itemLineNumber in the input parameter is "Item x", so we only need the x for the int
        int itemLineNumberInt = Integer.parseInt(itemLineNumber.substring(5));
        PurchaseOrderItem itemLine = (PurchaseOrderItem) document.getItemByLineNumber(itemLineNumberInt);
        */
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
