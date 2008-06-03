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
package org.kuali.module.purap.rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchasingItemBase;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.vendor.bo.CommodityCode;

/**
 * Rules for Purchase Order Amendment documents creation.
 */
public class PurchaseOrderAmendmentDocumentRule extends PurchaseOrderDocumentRule {

    /**
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        // Check that the user is in purchasing workgroup.
//        String initiatorNetworkId = purapDocument.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
//        UniversalUserService uus = SpringContext.getBean(UniversalUserService.class);
//        UniversalUser user = null;
//        try {
//            user = uus.getUniversalUserByAuthenticationUserId(initiatorNetworkId);
//            String purchasingGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_PURCHASING);
//            if (!uus.isMember(user, purchasingGroup)) {
//                valid = false;
//                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, KFSKeyConstants.AUTHORIZATION_ERROR_DOCUMENT, initiatorNetworkId, "amend", PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT);
//            }
//        }
//        catch (UserNotFoundException ue) {
//            valid = false;
//        }
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
            if (((PurchaseOrderItem) item).isItemActiveIndicator() && (!((PurchaseOrderItem) item).isEmpty() && item.getItemType().isItemTypeAboveTheLineIndicator())) {
                return true;
            }
        }
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(purapDocument.getDocumentHeader().getWorkflowDocument().getDocumentType()).getLabel();

        GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_REQUIRED, documentType);
        return false;
    }
    
    /**
     * Overrides to provide validation for PurchaseOrderAmendmentDocument. 
     * @see org.kuali.module.purap.rules.PurchasingDocumentRuleBase#validateCommodityCodes(org.kuali.module.purap.bo.PurApItem, boolean)
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
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#requiresAccountValidationOnAllEnteredItems(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {
                
        boolean requiresAccountValidation = false;
        
        //if a new unordered item has been added to the purchase order, this is due to Receiving Line document,
        // and items should not have account validation performed.
        if( SpringContext.getBean(PurchaseOrderService.class).hasNewUnorderedItem((PurchaseOrderDocument)document)){
            requiresAccountValidation = false;
        }else{
            requiresAccountValidation = super.requiresAccountValidationOnAllEnteredItems(document);
        }
        
        return requiresAccountValidation;
    }
}
