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

import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.kfs.module.purap.businessobject.PurapEnterableItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.document.validation.ContinuePurapRule;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class LineItemReceivingDocumentRule extends DocumentRuleBase implements ContinuePurapRule {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {        
        boolean valid = true;
        LineItemReceivingDocument lineItemReceivingDocument = (LineItemReceivingDocument)document;
        
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid &= super.processCustomRouteDocumentBusinessRules(document);
        valid &= canCreateLineItemReceivingDocument(lineItemReceivingDocument);
        valid &= isAtLeastOneItemEntered(lineItemReceivingDocument);
        
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

}
