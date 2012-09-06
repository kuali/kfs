/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.businessobject.PurapEnterableItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.document.validation.AddReceivingItemRule;
import org.kuali.kfs.module.purap.document.validation.ContinuePurapRule;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class LineItemReceivingDocumentRule extends org.kuali.rice.kns.rules.TransactionalDocumentRuleBase implements ContinuePurapRule, AddReceivingItemRule{

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {        
        boolean valid = true;
        LineItemReceivingDocument lineItemReceivingDocument = (LineItemReceivingDocument)document;
        
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid &= super.processCustomRouteDocumentBusinessRules(document);
        valid &= canCreateLineItemReceivingDocument(lineItemReceivingDocument);
        valid &= isAtLeastOneItemEntered(lineItemReceivingDocument);
        valid &= validateItemUnitOfMeasure(lineItemReceivingDocument);
        
        //  makes sure all of the lines adhere to the rule that quantityDamaged and 
        // quantityReturned cannot (each) equal more than the quantityReceived
        valid &= validateAllReceivingLinesHaveSaneQuantities(lineItemReceivingDocument);
        
        return valid;
    }
    /**
     * TODO: move this up
     * This method...
     * @param receivingDocument
     * @return
     */
    protected boolean isAtLeastOneItemEntered(ReceivingDocument receivingDocument){
        for (ReceivingItem item : (List<ReceivingItem>) receivingDocument.getItems()) {
            if (((PurapEnterableItem)item).isConsideredEntered()) {
                //if any item is entered return true
                return true;
            }
        }
        //if no items are entered return false
        GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_RECEIVING_LINEITEM_REQUIRED);
        return false;
        
    }    
    
    public boolean processContinuePurapBusinessRules(TransactionalDocument document) {
        
        boolean valid = true;
        LineItemReceivingDocument lineItemReceivingDocument = (LineItemReceivingDocument)document;
        
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid &= hasRequiredFieldsForContinue(lineItemReceivingDocument);
        //only do this if valid
        if(valid){
            valid &= canCreateLineItemReceivingDocument(lineItemReceivingDocument);
        }
        
        return valid;
    }
    
    /**
     * Make sure the required fields on the init screen are filled in.
     * 
     * @param lineItemReceivingDocument
     * @return
     */
    protected boolean hasRequiredFieldsForContinue(LineItemReceivingDocument lineItemReceivingDocument){
        
        boolean valid = true;
        
        if (ObjectUtils.isNull(lineItemReceivingDocument.getPurchaseOrderIdentifier())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.PURCHASE_ORDER_ID);
            valid &= false;
        }

        if (ObjectUtils.isNull(lineItemReceivingDocument.getShipmentReceivedDate())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.SHIPMENT_RECEIVED_DATE, KFSKeyConstants.ERROR_REQUIRED, PurapConstants.LineItemReceivingDocumentStrings.VENDOR_DATE);
            valid &= false;
        }

        return valid;
    }
    
    /**
     * Determines if it is valid to create a receiving line document.  Only one
     * receiving line document can be active at any time per purchase order document.
     * 
     * @param lineItemReceivingDocument
     * @return
     */
    protected boolean canCreateLineItemReceivingDocument(LineItemReceivingDocument lineItemReceivingDocument){
        
        boolean valid = true;
        
        if( SpringContext.getBean(ReceivingService.class).canCreateLineItemReceivingDocument(lineItemReceivingDocument.getPurchaseOrderIdentifier(), lineItemReceivingDocument.getDocumentNumber()) == false){
            valid &= false;
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_RECEIVING_LINE_DOCUMENT_ACTIVE_FOR_PO, lineItemReceivingDocument.getDocumentNumber(), lineItemReceivingDocument.getPurchaseOrderIdentifier().toString());
        }
         
        return valid;
    }

    /**
     * Validates that if the item type is quantity based, the unit of measure is required.
     */
    protected boolean validateItemUnitOfMeasure(ReceivingDocument receivingDocument) {
        boolean valid = true;
        for (ReceivingItem item : (List<ReceivingItem>) receivingDocument.getItems()) {
            // Validations for quantity based item type
            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                String uomCode = item.getItemUnitOfMeasureCode();
                if (StringUtils.isEmpty(uomCode)) {
                    valid = false;
                    String attributeLabel = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(item.getClass().getName()).getAttributeDefinition(KFSPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE).getLabel();
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + item.getItemUnitOfMeasureCode());
                }
                else {
                    // Find out whether the unit of measure code has existed in the database
                    Map<String, String> fieldValues = new HashMap<String, String>();
                    fieldValues.put(KFSPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, item.getItemUnitOfMeasureCode());
                    if (SpringContext.getBean(BusinessObjectService.class).countMatching(UnitOfMeasure.class, fieldValues) != 1) {
                        // This is the case where the unit of measure code on the item does not exist in the database.
                        valid = false;
                        GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, PurapKeyConstants.PUR_ITEM_UNIT_OF_MEASURE_CODE_INVALID, item.getItemUnitOfMeasureCode());
                    }
                }
            }
        }
        return valid;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.AddReceivingItemRule#processAddReceivingItemRules(org.kuali.kfs.module.purap.document.ReceivingDocument, org.kuali.kfs.module.purap.businessobject.ReceivingItem)
     */
    public boolean processAddReceivingItemRules(ReceivingDocument document, LineItemReceivingItem item,String errorPathPrefix) {
        boolean valid = true;
        
        valid &= SpringContext.getBean(DictionaryValidationService.class).isBusinessObjectValid(item,errorPathPrefix);
        
        //  test that the amount entered in the QuantityReturned and/or QuantityDamaged fields dont 
        // either equal more than the QuantityReceived.  In other words, you can only return or mark as 
        // damaged those that are received.  It doesnt make sense to receive 2 but return 3.  
        valid &= validateQuantityReturnedNotMoreThanReceived(document, item, errorPathPrefix, new Integer(0));
        valid &= validateQuantityDamagedNotMoreThanReceived(document, item, errorPathPrefix, new Integer(0));
        
        return valid;
    }

    protected boolean validateQuantityReturnedNotMoreThanReceived(ReceivingDocument document, LineItemReceivingItem item, String errorPathPrefix, Integer lineNumber) {
        if (item.getItemReturnedTotalQuantity() != null && item.getItemReceivedTotalQuantity() != null) {
            if (item.getItemReturnedTotalQuantity().isGreaterThan(item.getItemReceivedTotalQuantity())) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_RECEIVING_LINE_QTYRETURNED_GT_QTYRECEIVED, (lineNumber.intValue() == 0 ? "Add Line" : lineNumber.toString()));
                return false;
            }
        }
        return true;
    }

    protected boolean validateQuantityDamagedNotMoreThanReceived(ReceivingDocument document, LineItemReceivingItem item, String errorPathPrefix, Integer lineNumber) {
        if (item.getItemDamagedTotalQuantity() != null && item.getItemReceivedTotalQuantity() != null) {
            if (item.getItemDamagedTotalQuantity().isGreaterThan(item.getItemReceivedTotalQuantity())) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_RECEIVING_LINE_QTYDAMAGED_GT_QTYRECEIVED, (lineNumber.intValue() == 0 ? "Add Line" : lineNumber.toString()));
                return false;
            }
        }
        return true;
    }
    
    protected boolean validateAllReceivingLinesHaveSaneQuantities(ReceivingDocument document) {
        GlobalVariables.getMessageMap().clearErrorPath();
        boolean valid = true;
        for (int i = 0; i < document.getItems().size(); i++) {
            LineItemReceivingItem item = (LineItemReceivingItem) document.getItems().get(i);
            
            valid &= validateQuantityReturnedNotMoreThanReceived(document, item, "", new Integer(i + 1));
            valid &= validateQuantityDamagedNotMoreThanReceived(document, item, "", new Integer(i + 1));
        }
        return valid;
    }
    
}
