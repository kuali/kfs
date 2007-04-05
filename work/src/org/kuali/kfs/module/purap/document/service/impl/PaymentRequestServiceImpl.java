/*
 * Copyright 2007 The Kuali Foundation.
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

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.NoteService;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.GeneralLedgerService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.vendor.service.VendorService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class PaymentRequestServiceImpl implements PaymentRequestService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private NoteService noteService;
    private GeneralLedgerService generalLedgerService;
    private PurapService purapService;
    private PaymentRequestDao paymentRequestDao;
    private WorkflowDocumentService workflowDocumentService;
    private VendorService vendorService;
    
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;    
}
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;    
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setGeneralLedgerService(GeneralLedgerService generalLedgerService) {
        this.generalLedgerService = generalLedgerService;
    }
    
    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setPaymentRequestDao(PaymentRequestDao paymentRequestDao) {
        this.paymentRequestDao = paymentRequestDao;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }
    
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;    
    }
    
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId) {
        return paymentRequestDao.getPaymentRequestsByPurchaseOrderId( poDocId );
    }

    public void save(PaymentRequestDocument paymentRequestDocument) {
       
       // Integer poId = paymentRequestDocument.getPurchaseOrderIdentifier();
       // PurchaseOrderDocument purchaseOrderDocument = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderIdentifier());
       // paymentRequestDocument.populatePaymentRequestFormPurchaseOrder(purchaseOrderDocument);

        paymentRequestDao.save(paymentRequestDocument);
    }

    /**
     * Creates a PaymentRequestDocument from given RequisitionDocument
     * 
     * @param reqDocument - RequisitionDocument that the PO is being created from
     * @return PaymentRequestDocument
     */
  /*
    public PaymentRequestDocument createPaymentRequestDocument(RequisitionDocument reqDocument) {
        PaymentRequestDocument poDocument = null;

        // get new document from doc service
        try {
            poDocument = (PaymentRequestDocument) documentService.getNewDocument(PaymentRequestDocTypes.PURCHASE_ORDER_DOCUMENT);
            poDocument.populatePaymentRequestFromRequisition(reqDocument);
            poDocument.setPaymentRequestCurrentIndicator(true);
            // TODO set other default info
            // TODO set initiator of document as contract manager (is that right?)

            documentService.updateDocument(poDocument);
            documentService.prepareWorkflowDocument(poDocument);
            workflowDocumentService.save(poDocument.getDocumentHeader().getWorkflowDocument(), "", null);

        }
        catch (WorkflowException e) {
            LOG.error("Error creating PO document: " + e.getMessage());
            throw new RuntimeException("Error creating PO document: " + e.getMessage());
        }
        catch (Exception e) {
            LOG.error("Error persisting document # " + poDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage());
            throw new RuntimeException("Error persisting document # " + poDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage());
        }
        return poDocument;
    }
  */
   
}
