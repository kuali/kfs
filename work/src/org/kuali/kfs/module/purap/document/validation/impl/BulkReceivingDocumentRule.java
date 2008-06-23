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
package org.kuali.module.purap.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rules.DocumentRuleBase;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.bo.PurapEnterableItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.ReceivingItem;
import org.kuali.module.purap.bo.ReceivingLineItem;
import org.kuali.module.purap.document.BulkReceivingDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.ReceivingDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.rule.ContinuePurapRule;
import org.kuali.module.purap.service.BulkReceivingService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.ReceivingService;

public class BulkReceivingDocumentRule extends DocumentRuleBase implements ContinuePurapRule {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {        
        boolean valid = true;
//        ReceivingLineDocument receivingLineDocument = (ReceivingLineDocument)document;
//        
//        GlobalVariables.getErrorMap().clearErrorPath();
//        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
//        
//        valid &= super.processCustomRouteDocumentBusinessRules(document);
//        valid &= canCreateReceivingLineDocument(receivingLineDocument);
//        valid &= isAtLeastOneItemEntered(receivingLineDocument);
        
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
        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument)document;
        
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid = hasRequiredFieldsForContinue(bulkReceivingDocument) &&
                canCreateBulkReceivingDocument(bulkReceivingDocument);
        
        return valid;
    }
    
    /**
     * Make sure the required fields on the init screen are filled in.
     * 
     * @param receivingLineDocument
     * @return
     */
    private boolean hasRequiredFieldsForContinue(BulkReceivingDocument bulkReceivingDocument){
        
        boolean valid = true;
        
        if (ObjectUtils.isNull(bulkReceivingDocument.getShipmentReceivedDate())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.SHIPMENT_RECEIVED_DATE, KFSKeyConstants.ERROR_REQUIRED, PurapConstants.BulkReceivingDocumentStrings.VENDOR_DATE);
            valid = false;
        }

        return valid;
    }
    
    /**
     * FIXME: Have to pass the po id only, not the bulk doc since this method wont do much with the doc but we 
     * need the po available check, maybe have to move this check to the calling method - vpc 
     */
//    private boolean isValidPOIdentifier(BulkReceivingDocument bulkReceivingDocument){
//        
//        if (!bulkReceivingDocument.isPOAvailable()){
//            return true;
//        }
//        
//        boolean valid = true;
//        
//        GlobalVariables.getErrorMap().clearErrorPath();
//        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
//
//        Integer POID = bulkReceivingDocument.getPurchaseOrderIdentifier();
//
//        PurchaseOrderDocument purchaseOrderDocument = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(POID);
//        
//        if (ObjectUtils.isNull(purchaseOrderDocument)) {
//            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
//            valid &= false;
//        }
//        else if (purchaseOrderDocument.isPendingActionIndicator()) {
//            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_PENDING_ACTION);
//            valid &= false;
//        }
//        else if (!StringUtils.equals(purchaseOrderDocument.getStatusCode(), PurapConstants.PurchaseOrderStatuses.OPEN)) {
//            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_OPEN);
//            valid &= false;
//            // if the PO is pending and it is not a Retransmit, we cannot generate a Payment Request for it
//        }
//        else {
//            // Verify that there exists at least 1 item left to be invoiced
//            valid &= encumberedItemExistsForInvoicing(purchaseOrderDocument);
//        }
//        GlobalVariables.getErrorMap().clearErrorPath();
//        return valid;
//    }
//
//    /**
//     * Determines if there are items with encumbrances to be invoiced on passed in
//     * purchase order document.
//     * 
//     * @param document - purchase order document
//     * @return
//     */
//    public boolean encumberedItemExistsForInvoicing(PurchaseOrderDocument document) {
//        boolean zeroDollar = true;
//        GlobalVariables.getErrorMap().clearErrorPath();
//        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
//        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) document.getItems()) {
//            // Quantity-based items
//            if (poi.getItemType().isItemTypeAboveTheLineIndicator() && poi.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
//                KualiDecimal encumberedQuantity = poi.getItemOutstandingEncumberedQuantity() == null ? KualiDecimal.ZERO : poi.getItemOutstandingEncumberedQuantity();
//                if (encumberedQuantity.compareTo(KualiDecimal.ZERO) == 1) {
//                    zeroDollar = false;
//                    break;
//                }
//            }
//            // Service Items or Below-the-line Items
//            else if (poi.getItemType().isAmountBasedGeneralLedgerIndicator() || !poi.getItemType().isItemTypeAboveTheLineIndicator()) {
//                KualiDecimal encumberedAmount = poi.getItemOutstandingEncumberedAmount() == null ? KualiDecimal.ZERO : poi.getItemOutstandingEncumberedAmount();
//                if (encumberedAmount.compareTo(KualiDecimal.ZERO) == 1) {
//                    zeroDollar = false;
//                    break;
//                }
//            }
//        }
//        if (zeroDollar) {
//            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_NO_ITEMS_TO_INVOICE);
//        }
//        GlobalVariables.getErrorMap().clearErrorPath();
//        return !zeroDollar;
//    }
    
    /**
     * Determines if it is valid to create a receiving line document.  Only one
     * receiving line document can be active at any time per purchase order document.
     * 
     * @param receivingLineDocument
     * @return
     */
    private boolean canCreateBulkReceivingDocument(BulkReceivingDocument bulkReceivingDocument){
        
        boolean valid = true;
        
        if (bulkReceivingDocument.getPurchaseOrderIdentifier() != null){
            PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(bulkReceivingDocument.getPurchaseOrderIdentifier());
            
            if (ObjectUtils.isNull(po)){
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_BULK_RECEIVING_DOCUMENT_INVALID_PO, bulkReceivingDocument.getDocumentNumber(), bulkReceivingDocument.getPurchaseOrderIdentifier().toString());
                valid = false;
            }
            
            if (valid){
                if(!SpringContext.getBean(BulkReceivingService.class).canCreateBulkReceivingDocument(po, bulkReceivingDocument.getDocumentNumber())){
                    valid &= false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_BULK_RECEIVING_DOCUMENT_ACTIVE_FOR_PO, bulkReceivingDocument.getDocumentNumber(), bulkReceivingDocument.getPurchaseOrderIdentifier().toString());
                }
            }
        }
         
        return valid;
    }

}
