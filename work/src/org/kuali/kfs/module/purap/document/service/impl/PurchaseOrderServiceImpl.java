/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentNote;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.DocumentAuthorizationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentNoteService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.dao.PurchaseOrderDao;
import org.kuali.module.purap.document.PurchaseOrderCloseDocumentAuthorizer;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.GeneralLedgerService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderPostProcessorService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private DocumentNoteService documentNoteService;
    private DocumentService documentService;
    private GeneralLedgerService generalLedgerService;
    private PurapService purapService;
    private PurchaseOrderDao purchaseOrderDao;
    private WorkflowDocumentService workflowDocumentService;
    
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;    
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;    
    }

    public void setDocumentNoteService(DocumentNoteService documentNoteService) {
        this.documentNoteService = documentNoteService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setGeneralLedgerService(GeneralLedgerService generalLedgerService) {
        this.generalLedgerService = generalLedgerService;
    }
    
    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) {
        this.purchaseOrderDao = purchaseOrderDao;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }
    
    public void save(PurchaseOrderDocument purchaseOrderDocument) {
        purchaseOrderDao.save(purchaseOrderDocument);
    }

    /**
     * Creates a PurchaseOrderDocument from given RequisitionDocument
     * 
     * @param reqDocument - RequisitionDocument that the PO is being created from
     * @return PurchaseOrderDocument
     */
    public PurchaseOrderDocument createPurchaseOrderDocument(RequisitionDocument reqDocument) {
        PurchaseOrderDocument poDocument = null;

        // get new document from doc service
        try {
            poDocument = (PurchaseOrderDocument) documentService.getNewDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT);
            poDocument.populatePurchaseOrderFromRequisition(reqDocument);
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

    public void close( PurchaseOrderDocument document, String annotation ){
        checkForNulls(document);
        PurchaseOrderCloseDocumentAuthorizer authorizer = new PurchaseOrderCloseDocumentAuthorizer();
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();
        if (!authorizer.getDocumentActionFlags(document, kualiUser).getCanClose()) {
            throw buildAuthorizationException("close", document);
        }
        DocumentNote note = new DocumentNote();
        
        note.setDocumentNumber(document.getDocumentNumber());
        note.setFinDocumentAuthorUniversalId(kualiUser.getPersonUniversalIdentifier());
        //note.setFinDocumentAuthorUniversal(kualiUser);
        note.setFinancialDocumentNoteText(annotation);
        document.getDocumentHeader().addNote(note); // add to doc so it shows up on next post
        try {
            documentNoteService.save(note);
        } catch (Exception e) {
            //TODO: Handle failure to save note.
        }
        try {
            documentService.prepareWorkflowDocument(document);
        } catch (WorkflowException we) {
            //TODO: Handle problem in preparing document for Workflow.
        }
        
        //TODO: This needs to side-effect the PO table entries.
        //workflowDocumentService.route(document.getDocumentHeader().getWorkflowDocument(), annotation,);
        //workflowDocumentService.disapprove(document.getDocumentHeader().getWorkflowDocument(), annotation);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
    }
    
    public void updateFlagsAndRoute(PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients) {
        String oldDocNum = po.getDocumentNumber();
        //PO row that is set to curr_ind to Y, set the pend_action to Y
        po.setPurchaseOrderCurrentIndicator(true);
        po.setPendingActionIndicator(true);
        save(po);
        try {
            //call toCopy to give us a new documentHeader
            po.toCopy(docType);
            po.refreshNonUpdateableReferences();
            documentService.routeDocument(po, annotation, adhocRoutingRecipients);
            //update old po flags
            PurchaseOrderDocument oldPo = (PurchaseOrderDocument)documentService.getByDocumentHeaderId(oldDocNum);
            oldPo.setPurchaseOrderCurrentIndicator(false);
            oldPo.setPendingActionIndicator(false);
            oldPo.setStatusCode(PurchaseOrderStatuses.CLOSED);
            save(oldPo);
        }
        catch (WorkflowException e) {
            LOG.error("Error creating PO document: " + e.getMessage());
            throw new RuntimeException("Error setting oldPendingCurrentIndicator on PO document: " + e.getMessage());            
        }
    }
    
    protected void checkForNulls(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        else if (document.getDocumentNumber() == null) {
            throw new IllegalStateException("invalid (null) documentHeaderId");
        }
    }
    
    private DocumentAuthorizationException buildAuthorizationException(String action, Document document) {
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();

        return new DocumentAuthorizationException(currentUser.getPersonUserIdentifier(), action, document.getDocumentNumber());
    }

    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#convertDocTypeToService()
     */
    public PurchaseOrderPostProcessorService convertDocTypeToService(String docTypeId) {
        PurchaseOrderPostProcessorService popp=null;
        String docType;
        docType=(String)PurapConstants.PURCHASE_ORDER_DOC_TYPE_MAP.get(docTypeId);
        if(StringUtils.isNotEmpty(docType)) {
            popp=(PurchaseOrderPostProcessorService)SpringServiceLocator.getBeanFactory().getBean(docType);
        }
        
        return popp;
    }

    public void completePurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("completePurchaseOrder() started");

        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();

        // create general ledger entries for this PO
        generalLedgerService.generateEntriesApprovePo(po);

        if (PurapConstants.POTransmissionMethods.PRINT.equals(po.getPurchaseOrderTransmissionMethodCode())) {
            LOG.debug("completePurchaseOrder() Purchase Order Transmission Type is Print");
            purapService.updateStatusAndStatusHistory(po, PurapConstants.PurchaseOrderStatuses.PENDING_PRINT);
            this.save(po);
            // TODO create PO print doc
            // routingService.routePrintablePurchaseOrderFYI(po, u);
        }
        else {
            LOG.info("completePurchaseOrder() Unhandled Transmission Status: " + po.getPurchaseOrderTransmissionMethodCode() + " -- Defaulting Status to OPEN");
            purapService.updateStatusAndStatusHistory(po, PurapConstants.PurchaseOrderStatuses.OPEN);
            po.setPurchaseOrderInitialOpenDate(dateTimeService.getCurrentSqlDate());
            this.save(po);
        }

    }


}
