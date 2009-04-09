/*
 * Copyright (c) 2004, 2005 The National Association of College and University
 * Business Officers, Cornell University, Trustees of Indiana University,
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you
 * have read, understand, and will comply with the terms and conditions of the
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.kuali.kfs.module.purap.document.service.impl;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.FaxBatchDocumentsService;
import org.kuali.kfs.module.purap.document.service.FaxService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;

public class FaxBatchDocumentsServiceImpl implements FaxBatchDocumentsService {
   private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FaxBatchDocumentsServiceImpl.class);

   private PurchaseOrderService purchaseOrderService;
   private FaxService faxService;
   private PurapService purapService;
   

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
         
         GlobalVariables.getErrorMap().clear();
         faxService.faxPurchaseOrderPdf(po,false);
       
         if (GlobalVariables.getErrorMap().isEmpty()){
             po.setStatusCode(PurapConstants.PurchaseOrderStatuses.OPEN);
             po.setPurchaseOrderInitialOpenTimestamp(new Timestamp(new Date().getTime()));
             po.setPurchaseOrderLastTransmitTimestamp(new Timestamp(new Date().getTime()));
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
    
}
