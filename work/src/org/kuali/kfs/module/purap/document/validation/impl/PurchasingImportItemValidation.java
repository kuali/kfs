/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.ItemFields;
import org.kuali.kfs.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingImportItemValidation extends PurchasingAccountsPayableImportItemValidation {

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;  
        
        PurApItem refreshedItem = getItemForValidation();
        refreshedItem.refreshReferenceObject("itemType");
        super.setItemForValidation(refreshedItem);
        
        valid &= super.validate(event);
        GlobalVariables.getMessageMap().addToErrorPath(PurapConstants.ITEM_TAB_ERROR_PROPERTY);
        
        if (getItemForValidation().getItemType().isLineItemIndicator()) {
            valid &= validateItemDescription(getItemForValidation());
        }
        valid &= validateItemUnitPrice(getItemForValidation());
        valid &= validateUnitOfMeasureCodeExists(getItemForValidation());        
        valid &= validateCommodityCodes(getItemForValidation(), commodityCodeIsRequired());
        
        GlobalVariables.getMessageMap().removeFromErrorPath(PurapConstants.ITEM_TAB_ERROR_PROPERTY);
        return valid;
    }

    /**
     * Validates whether the commodity code existed on the item, and if existed, whether the
     * commodity code on the item existed in the database, and if so, whether the commodity 
     * code is active. Display error if any of these 3 conditions are not met.
     * 
     * @param item  The PurApItem containing the commodity code to be validated.
     * @return boolean false if the validation fails and true otherwise.
     */
    protected boolean validateCommodityCodes(PurApItem item, boolean commodityCodeRequired) {
        boolean valid = true;
        String identifierString = item.getItemIdentifierString();
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        
        //This validation is only needed if the commodityCodeRequired system parameter is true
        if (commodityCodeRequired && StringUtils.isBlank(purItem.getPurchasingCommodityCode()) ) {
            //This is the case where the commodity code is required but the item does not currently contain the commodity code.
            valid = false;
            String attributeLabel = dataDictionaryService.
                                    getDataDictionary().getBusinessObjectEntry(CommodityCode.class.getName()).
                                    getAttributeDefinition(PurapPropertyConstants.ITEM_COMMODITY_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + identifierString);
        }
        else if (StringUtils.isNotBlank(purItem.getPurchasingCommodityCode())) {
            //Find out whether the commodity code has existed in the database
            Map<String,String> fieldValues = new HashMap<String, String>();
            fieldValues.put(PurapPropertyConstants.ITEM_COMMODITY_CODE, purItem.getPurchasingCommodityCode());
            if (businessObjectService.countMatching(CommodityCode.class, fieldValues) != 1) {
                //This is the case where the commodity code on the item does not exist in the database.
                valid = false;
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, PurapKeyConstants.PUR_COMMODITY_CODE_INVALID,  " in " + identifierString);
            }
            else {
                valid &= validateThatCommodityCodeIsActive(item);
            }
        }
        
        return valid;
    }

    /**
     * Checks that a description was entered for the item.
     * 
     * @param item
     * @return
     */
    public boolean validateItemDescription(PurApItem item) {
        boolean valid = true;      
        if (StringUtils.isEmpty(item.getItemDescription())) {
            valid = false;
            String attributeLabel = dataDictionaryService.
                                    getDataDictionary().getBusinessObjectEntry(item.getClass().getName()).
                                    getAttributeDefinition(PurapPropertyConstants.ITEM_DESCRIPTION).getLabel();
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_DESCRIPTION, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + item.getItemIdentifierString());
        }
        return valid;
    }

    /**
     * Validates the unit price for all applicable item types. It validates that the unit price field was
     * entered on the item, and that the price is in the right range for the item type.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if there is any validation that fails.
     */
    public boolean validateItemUnitPrice(PurApItem item) {
        boolean valid = true;
        if (item.getItemType().isLineItemIndicator()) {
            if (ObjectUtils.isNull(item.getItemUnitPrice())) {
                valid = false;
                String attributeLabel = dataDictionaryService.
                                        getDataDictionary().getBusinessObjectEntry(item.getClass().getName()).
                                        getAttributeDefinition(PurapPropertyConstants.ITEM_UNIT_PRICE).getLabel();
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + item.getItemIdentifierString());
            }
        }    

        if (ObjectUtils.isNotNull(item.getItemUnitPrice())) {
            if ((BigDecimal.ZERO.compareTo(item.getItemUnitPrice()) > 0) && ((!item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) && (!item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                // If the item type is not full order discount or trade in items, don't allow negative unit price.
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.UNIT_COST, item.getItemIdentifierString());
                valid = false;
            }
            else if ((BigDecimal.ZERO.compareTo(item.getItemUnitPrice()) < 0) && ((item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) || (item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                // If the item type is full order discount or trade in items, its unit price must be negative.
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_AMOUNT_NOT_BELOW_ZERO, ItemFields.UNIT_COST, item.getItemIdentifierString());
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates that if the item type is quantity based, that the unit of measure code is valid.
     * Looks for the UOM Code in the table. If it is not there, the code is invalid. 
     * This checking is needed only for imported items, since items added from new line could only 
     * choose an existing UOM from the drop-down list.
     * 
     * @param item the item to be validated
     * @return boolean false if the item type is quantity based and the unit of measure code is invalid.
     */
    protected boolean validateUnitOfMeasureCodeExists(PurApItem item) {
        boolean valid = true;
        
        if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {            
            String uomCode = item.getItemUnitOfMeasureCode();
            Map<String,String> fieldValues = new HashMap<String,String>();
            fieldValues.put(KFSPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, uomCode);
            if (businessObjectService.countMatching(UnitOfMeasure.class, fieldValues) != 1) {
                String[] errorParams = { uomCode, "" + item.getItemLineNumber() };
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERRORS, PurapKeyConstants.ERROR_ITEMPARSER_INVALID_UOM_CODE, errorParams);
                valid = false;
            }  
        }

        return valid;
    }

    /**
     * Predicate to do a parameter lookup and tell us whether a commodity code is required.
     * Override in child classes. 
     * 
     * @return      True if a commodity code is required.
     */
    protected boolean commodityCodeIsRequired() {
        return false;
    }

    protected boolean validateThatCommodityCodeIsActive(PurApItem item) {
        if (!((PurchasingItemBase)item).getCommodityCode().isActive()) {
            //This is the case where the commodity code on the item is not active.
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE, " in " + item.getItemIdentifierString());
            return false;
        }
        return true;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
