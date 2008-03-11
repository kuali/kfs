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

import java.util.List;

import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.DocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.ReceivingDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.ReceivingService;

import edu.iu.uis.eden.exception.WorkflowException;

public class ReceivingServiceImpl implements ReceivingService {

    private PurchaseOrderService purchaseOrderService;
    private ReceivingDao receivingDao;
    private DocumentService documentService;
    
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setReceivingDao(ReceivingDao receivingDao) {
        this.receivingDao = receivingDao;
    }

    public void setDocumentService(DocumentService documentService){
        this.documentService = documentService;
    }
    
    /**
     * 
     * @see org.kuali.module.purap.service.ReceivingService#populateReceivingLineFromPurchaseOrder(org.kuali.module.purap.document.ReceivingLineDocument, java.lang.String)
     */
    public void populateReceivingLineFromPurchaseOrder(ReceivingLineDocument rlDoc, String poDocId) {
        
        if(rlDoc == null){
            rlDoc = new ReceivingLineDocument();
        }
                             
        //retrieve po by doc id
        PurchaseOrderDocument poDoc = null;
        poDoc = purchaseOrderService.getPurchaseOrderByDocumentNumber(poDocId);

        if(poDoc != null){
            rlDoc.populateReceivingLineFromPurchaseOrder(poDoc);
        }                
        
    }

    public void saveReceivingLineDocument(ReceivingLineDocument rlDoc) throws WorkflowException {
        try {
            //rlDoc.getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.);
            documentService.saveDocument(rlDoc, ContinueAccountsPayableEvent.class);
        }
        catch (ValidationException ve) {
            rlDoc.getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.INITIATED);
        }
        catch (WorkflowException we) {
            rlDoc.getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.INITIATED);
            String errorMsg = "Error saving document # " + rlDoc.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            //LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    public boolean isReceivingLineDocumentInProcessForPurchaseOrder(Integer poId){
        
        boolean isInProcess = false;
        
        isInProcess = receivingDao.isReceivingLineDocumentInProcessForPurchaseOrder(poId);
        
        return isInProcess;
    }

}
