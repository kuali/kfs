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
package org.kuali.module.purap.service.impl;

import java.util.HashMap;
import java.util.List;

import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.ReceivingDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.rule.event.ContinuePurapEvent;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.ReceivingService;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.exception.WorkflowException;

public class ReceivingServiceImpl implements ReceivingService {

    private PurchaseOrderService purchaseOrderService;
    private ReceivingDao receivingDao;
    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setReceivingDao(ReceivingDao receivingDao) {
        this.receivingDao = receivingDao;
    }

    public void setDocumentService(DocumentService documentService){
        this.documentService = documentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService){
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * 
     * @see org.kuali.module.purap.service.ReceivingService#populateReceivingLineFromPurchaseOrder(org.kuali.module.purap.document.ReceivingLineDocument)
     */
    public void populateReceivingLineFromPurchaseOrder(ReceivingLineDocument rlDoc) {
        
        if(rlDoc == null){
            rlDoc = new ReceivingLineDocument();
        }
                             
        //retrieve po by doc id
        PurchaseOrderDocument poDoc = null;
        poDoc = purchaseOrderService.getCurrentPurchaseOrder(rlDoc.getPurchaseOrderIdentifier());

        if(poDoc != null){
            rlDoc.populateReceivingLineFromPurchaseOrder(poDoc);
        }                
        
    }

    /**
     * 
     * @see org.kuali.module.purap.service.ReceivingService#populateAndSaveReceivingLineDocument(org.kuali.module.purap.document.ReceivingLineDocument)
     */
    public void populateAndSaveReceivingLineDocument(ReceivingLineDocument rlDoc) throws WorkflowException {
        try {            
            documentService.saveDocument(rlDoc, ContinuePurapEvent.class);
        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + rlDoc.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            //LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    private boolean isReceivingLineDocumentInProcessForPurchaseOrder(Integer poId) throws RuntimeException{
        
        boolean isInProcess = false;
        
        List<String> docNumbers = receivingDao.getDocumentNumbersByPurchaseOrderId(poId);
        KualiWorkflowDocument workflowDocument = null;
        
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getUniversalUser());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            if(!(workflowDocument.stateIsCanceled() ||
                 workflowDocument.stateIsException() ||
                 workflowDocument.stateIsFinal()) ){
                     
                isInProcess = false;
                break;
            }
        }

        return isInProcess;
    }

    public boolean canCreateReceivingLineDocument(Integer poId) throws RuntimeException {
        
        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(poId);
        
        return canCreateReceivingLineDocument(po);            
    }

    public boolean canCreateReceivingLineDocument(PurchaseOrderDocument po) throws RuntimeException {
        
        boolean canCreate = false;
        
        if( (po.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) || 
             po.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CLOSED) || 
             po.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD)) &&
             !isReceivingLineDocumentInProcessForPurchaseOrder(po.getPurapDocumentIdentifier()) &&
             po.isPurchaseOrderCurrentIndicator() ){
            
            canCreate = true;
        }
        
        return canCreate;
    }

    public HashMap<String, String> receivingLineDuplicateMessages(ReceivingLineDocument rlDoc) {
        // TODO Auto-generated method stub
        return null;
    }

}
