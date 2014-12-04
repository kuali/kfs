/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
