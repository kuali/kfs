/*
 * Copyright 2008 The Kuali Foundation.
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
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class LineItemReceivingDocumentRule extends DocumentRuleBase implements ContinuePurapRule, AddReceivingItemRule{

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {        
        boolean valid = true;
        LineItemReceivingDocument lineItemReceivingDocument = (LineItemReceivingDocument)document;
        
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid &= super.processCustomRouteDocumentBusinessRules(document);
        valid &= canCreateLineItemReceivingDocument(lineItemReceivingDocument);
        valid &= isAtLeastOneItemEntered(lineItemReceivingDocument);
        valid &= validateItemUnitOfMeasure(lineItemReceivingDocument);
        
        return valid;
    }
    /**
     * TODO: move this up
     * This method...
     * @param receivingDocument
     * @return
     */
    private boolean isAtLeastOneItemEntered(ReceivingDocument receivingDocument){
        for (ReceivingItem item : (List<ReceivingItem>) receivingDocument.getItems()) {
            if (((PurapEnterableItem)item).isConsideredEntered()) {
                //if any item is entered return true
                return true;
            }
        }
        //if no items are entered return false
        GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_RECEIVING_LINEITEM_REQUIRED);
        return false;
        
    }    
    
    public boolean processContinuePurapBusinessRules(TransactionalDocument document) {
        
        boolean valid = true;
        LineItemReceivingDocument lineItemReceivingDocument = (LineItemReceivingDocument)document;
        
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
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
    private boolean hasRequiredFieldsForContinue(LineItemReceivingDocument lineItemReceivingDocument){
        
        boolean valid = true;
        
        if (ObjectUtils.isNull(lineItemReceivingDocument.getPurchaseOrderIdentifier())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.PURCHASE_ORDER_ID);
            valid &= false;
        }

        if (ObjectUtils.isNull(lineItemReceivingDocument.getShipmentReceivedDate())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.SHIPMENT_RECEIVED_DATE, KFSKeyConstants.ERROR_REQUIRED, PurapConstants.LineItemReceivingDocumentStrings.VENDOR_DATE);
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
    private boolean canCreateLineItemReceivingDocument(LineItemReceivingDocument lineItemReceivingDocument){
        
        boolean valid = true;
        
        if( SpringContext.getBean(ReceivingService.class).canCreateLineItemReceivingDocument(lineItemReceivingDocument.getPurchaseOrderIdentifier(), lineItemReceivingDocument.getDocumentNumber()) == false){
            valid &= false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_RECEIVING_LINE_DOCUMENT_ACTIVE_FOR_PO, lineItemReceivingDocument.getDocumentNumber(), lineItemReceivingDocument.getPurchaseOrderIdentifier().toString());
        }
         
        return valid;
    }

    /**
     * Validates that if the item type is quantity based, the unit of measure is required.
     */
    private boolean validateItemUnitOfMeasure(ReceivingDocument receivingDocument) {
        boolean valid = true;
        for (ReceivingItem item : (List<ReceivingItem>) receivingDocument.getItems()) {
            // Validations for quantity based item type
            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                String uomCode = item.getItemUnitOfMeasureCode();
                if (StringUtils.isEmpty(uomCode)) {
                    valid = false;
                    String attributeLabel = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(item.getClass().getName()).getAttributeDefinition(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE).getLabel();
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + item.getItemUnitOfMeasureCode());
                }
                else {
                    // Find out whether the unit of measure code has existed in the database
                    Map<String, String> fieldValues = new HashMap<String, String>();
                    fieldValues.put(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, item.getItemUnitOfMeasureCode());
                    if (SpringContext.getBean(BusinessObjectService.class).countMatching(UnitOfMeasure.class, fieldValues) != 1) {
                        // This is the case where the unit of measure code on the item does not exist in the database.
                        valid = false;
                        GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, PurapKeyConstants.PUR_ITEM_UNIT_OF_MEASURE_CODE_INVALID, item.getItemUnitOfMeasureCode());
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
        boolean valid = SpringContext.getBean(DictionaryValidationService.class).isBusinessObjectValid(item,errorPathPrefix);
        return valid;
    }

}
