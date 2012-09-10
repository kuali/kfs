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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.ContinuePurapRule;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class BulkReceivingDocumentRule extends DocumentRuleBase implements ContinuePurapRule {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {       
        
        boolean valid = true;
        
        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument)document;
        
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid &= super.processCustomRouteDocumentBusinessRules(document);
        valid &= canCreateBulkReceivingDocument(bulkReceivingDocument);
        
        return valid;
    }
    
    public boolean processContinuePurapBusinessRules(TransactionalDocument document) {
        
        boolean valid = true;
        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument)document;
        
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid = hasRequiredFieldsForContinue(bulkReceivingDocument) &&
                canCreateBulkReceivingDocument(bulkReceivingDocument);
        
        return valid;
    }
    
    /**
     * Make sure the required fields on the init screen are filled in.
     * 
     * @param bulkReceivingDocument
     * @return
     */
    protected boolean hasRequiredFieldsForContinue(BulkReceivingDocument bulkReceivingDocument){
        
        boolean valid = true;
        
        if (ObjectUtils.isNull(bulkReceivingDocument.getShipmentReceivedDate())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.SHIPMENT_RECEIVED_DATE, KFSKeyConstants.ERROR_REQUIRED, PurapConstants.BulkReceivingDocumentStrings.VENDOR_DATE);
            valid = false;
        }

        return valid;
    }
    
    /**
     * Determines if it is valid to create a bulk receiving document.  
     * 
     * @param bulkReceivingDocument
     * @return
     */
    protected boolean canCreateBulkReceivingDocument(BulkReceivingDocument bulkReceivingDocument){
        
        boolean valid = true;
        
        if (bulkReceivingDocument.getPurchaseOrderIdentifier() != null){
            PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(bulkReceivingDocument.getPurchaseOrderIdentifier());
            
            if (ObjectUtils.isNull(po)){
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_BULK_RECEIVING_DOCUMENT_INVALID_PO, bulkReceivingDocument.getDocumentNumber(), bulkReceivingDocument.getPurchaseOrderIdentifier().toString());
                valid = false;
            }else{
                if (!(po.getApplicationDocumentStatus().equals(PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN) || 
                    po.getApplicationDocumentStatus().equals(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED))){
                    valid &= false;
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_BULK_RECEIVING_PO_NOT_OPEN, bulkReceivingDocument.getDocumentNumber(), bulkReceivingDocument.getPurchaseOrderIdentifier().toString());
                }else{
                    String docNumberInProcess = SpringContext.getBean(BulkReceivingService.class).getBulkReceivingDocumentNumberInProcessForPurchaseOrder(po.getPurapDocumentIdentifier(), bulkReceivingDocument.getDocumentNumber());
                    if (StringUtils.isNotEmpty(docNumberInProcess)){
                        valid &= false;
                        GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_BULK_RECEIVING_DOCUMENT_ACTIVE_FOR_PO, docNumberInProcess, bulkReceivingDocument.getPurchaseOrderIdentifier().toString());
                    }
                }
            }
        }
         
        return valid;
    }

}
