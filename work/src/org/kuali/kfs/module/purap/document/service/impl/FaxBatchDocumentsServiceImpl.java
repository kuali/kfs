/*
 * Copyright 2007-2008 The Kuali Foundation
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

package org.kuali.kfs.module.purap.document.service.impl;

import java.util.Collection;
import java.util.Iterator;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.FaxBatchDocumentsService;
import org.kuali.kfs.module.purap.document.service.FaxService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

public class FaxBatchDocumentsServiceImpl implements FaxBatchDocumentsService {
   private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FaxBatchDocumentsServiceImpl.class);

   private PurchaseOrderService purchaseOrderService;
   private FaxService faxService;
   private PurapService purapService;
   private DateTimeService dateTimeService;

   /**
    * Faxes pending documents.  Currently only PO documents set to Pending Fax
    * Status inside workflow.
    * 
    * @return Collection of ServiceError objects
    */
   public boolean faxPendingPurchaseOrders() {
       
     Collection<PurchaseOrderDocument> pendingPOs = purchaseOrderService.getPendingPurchaseOrderFaxes();
     boolean result = true;
     
     for (Iterator<PurchaseOrderDocument> iter = pendingPOs.iterator(); iter.hasNext();) {

         PurchaseOrderDocument po =  iter.next();
       
         if (!po.getDocumentHeader().hasWorkflowDocument()){
             try {
                 po = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(po.getDocumentNumber());
             }
             catch (WorkflowException e) {
                 throw new RuntimeException(e);
             }
         }
         
         GlobalVariables.getMessageMap().clearErrorMessages();
         faxService.faxPurchaseOrderPdf(po,false);
       
         if (GlobalVariables.getMessageMap().hasErrors()){
             try {
                 po.updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN);
             }
             catch (WorkflowException we) {
                 String errorMsg = "Workflow Exception caught trying to create and save PO document of type PurchaseOrderSplitDocument using source document with doc id '" + po.getDocumentNumber() + "'";
                 LOG.error(errorMsg, we);
                 throw new RuntimeException(errorMsg, we);
             }
             
             po.setPurchaseOrderInitialOpenTimestamp(dateTimeService.getCurrentTimestamp());
             po.setPurchaseOrderLastTransmitTimestamp(dateTimeService.getCurrentTimestamp());
             purapService.saveDocumentNoValidation(po);
         }else{
             result = false;
         }
     }
     
     return result;
   }
   
   public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
       this.purchaseOrderService = purchaseOrderService;
   }

   public void setFaxService(FaxService faxService) {
       this.faxService = faxService;
   }

   public void setPurapService(PurapService purapService) {
       this.purapService = purapService;
   }

   public void setDateTimeService(DateTimeService dateTimeService) {
       this.dateTimeService = dateTimeService;
   }
    
}
