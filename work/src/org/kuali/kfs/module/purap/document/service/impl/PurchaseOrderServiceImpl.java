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

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.rule.event.RouteDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.dao.PurchaseOrderDao;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.GeneralLedgerService;
import org.kuali.module.purap.service.PrintService;
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
    private DocumentService documentService;
    private NoteService noteService;
    private GeneralLedgerService generalLedgerService;
    private PurapService purapService;
    private PrintService printService;
    private PurchaseOrderDao purchaseOrderDao;
    private WorkflowDocumentService workflowDocumentService;
    private KualiConfigurationService kualiConfigurationService;
    private KualiRuleService kualiRuleService;
    
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

    public void setPrintService(PrintService printService) {
        this.printService = printService;
    }

    public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) {
        this.purchaseOrderDao = purchaseOrderDao;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }
    
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
    
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
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
            poDocument.setPurchaseOrderCurrentIndicator(true);
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
    
    /**
     * Creates a PurchaseOrderPrintDocument from given PurchaseOrderDocument
     * 
     * @param reqDocument - RequisitionDocument that the PO is being created from
     * @return PurchaseOrderDocument with document type PurchaseOrderPrintDocument
     */
    public PurchaseOrderDocument createPurchaseOrderPrintDocument(PurchaseOrderDocument poDocument) {

        // get new document from doc service
        try {
            poDocument.toCopy(PurchaseOrderDocTypes.PURCHASE_ORDER_PRINT_DOCUMENT);
            poDocument.setPurchaseOrderCurrentIndicator(true);
            // TODO set other default info
            // TODO set initiator of document as contract manager (is that right?)

            documentService.updateDocument(poDocument);
            documentService.prepareWorkflowDocument(poDocument);
            workflowDocumentService.save(poDocument.getDocumentHeader().getWorkflowDocument(), "", null);

        }
        catch (WorkflowException e) {
            LOG.error("Error creating PO Print document: " + e.getMessage());
            throw new RuntimeException("Error creating PO Print document: " + e.getMessage());
        }
        catch (Exception e) {
            LOG.error("Error persisting document # " + poDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage());
            throw new RuntimeException("Error persisting document # " + poDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage());
        }
        return poDocument;
    }
    
    public boolean firstPurchaseOrderTransmitViaPrint (PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients,
        ByteArrayOutputStream baosPDF,  String environment) {
        
        boolean isRetransmit = false;
        boolean result = true;
        po.setPurchaseOrderFirstTransmissionDate(dateTimeService.getCurrentSqlDate());
        result = updateFlagsAndRoute (po, docType, annotation, adhocRoutingRecipients);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdf(po, baosPDF, isRetransmit, environment);
        
        if (generatePDFErrors.size() > 0) {
            for (String error: generatePDFErrors) {
                GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, error);
            }
            result = false;
        }
        return result;
    }
    
    /**
     * 
     * @see org.kuali.module.purap.service.PurchaseOrderService#printPurchaseOrderPDF(org.kuali.module.purap.document.PurchaseOrderDocument, java.lang.String, java.lang.String, java.util.List, java.io.ByteArrayOutputStream, java.lang.String)
     */
    public boolean printPurchaseOrderPDF (PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients,
        ByteArrayOutputStream baosPDF) {
            
        String environment = kualiConfigurationService.getPropertyString( Constants.ENVIRONMENT_KEY );
        boolean isRetransmit = false;
        boolean result = true;
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdf(po, baosPDF, isRetransmit, environment);
            
        if (generatePDFErrors.size() > 0) {
            for (String error: generatePDFErrors) {
                GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, error);
            }
            result = false;
        }
        if (result) {
            Date currentSqlDate = dateTimeService.getCurrentSqlDate();
            po.setPurchaseOrderFirstTransmissionDate(currentSqlDate);
            po.setPurchaseOrderInitialOpenDate(currentSqlDate);
            po.setPurchaseOrderLastTransmitDate(currentSqlDate);
            save(po);
            
            //Get the PurchaseOrderDocument of this print document whose status is Pending Print and update that PO's
            //status to Open and set its firstTransmissionDate, initialOpenDate and lastTransmitDate to current date
            PurchaseOrderDocument previousPo = getPurchaseOrderInPendingPrintStatus(po.getPurapDocumentIdentifier());
            previousPo.setPurchaseOrderFirstTransmissionDate(currentSqlDate);
            previousPo.setPurchaseOrderInitialOpenDate(currentSqlDate);
            previousPo.setPurchaseOrderLastTransmitDate(currentSqlDate);
            purapService.updateStatusAndStatusHistory( previousPo, PurapConstants.PurchaseOrderStatuses.OPEN );
            save(previousPo);
        }
        return result;
    }
    
    public PurchaseOrderDocument getPurchaseOrderInPendingPrintStatus(Integer id) {
        return purchaseOrderDao.getPurchaseOrderInPendingPrintStatus(id);    
    }
    
    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#updateFlagsAndRoute(org.kuali.module.purap.document.PurchaseOrderDocument, java.lang.String, java.lang.String, java.util.List)
     */
    public boolean updateFlagsAndRoute(PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients) {
        try {
            //call toCopy to give us a new documentHeader
            po.toCopy(docType);
            po.refreshNonUpdateableReferences();
            po.setPurchaseOrderCurrentIndicator(false);
            po.setPendingActionIndicator(false);
            //Before Routing, I think we ought to check the rules first
            boolean rulePassed = kualiRuleService.applyRules(new RouteDocumentEvent(po)); 
            if (!rulePassed) {
                return false;
            } 
            else { 
                documentService.routeDocument(po, annotation, adhocRoutingRecipients);
            }
        }
        catch (WorkflowException we) {
            LOG.error("Error during updateFlagsAndRoute on PO document: " + we.getMessage());
            throw new RuntimeException("Error during updateFlagsAndRoute on PO document: " + we.getMessage());            
        }
        catch (Exception e) {
            LOG.error("Error during updateFlagsAndRoute on PO document: " + e.getMessage());
            throw new RuntimeException("Error during updateFlagsAndRoute on PO document: " + e.getMessage());      
        }
        return true;
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
            GlobalVariables.setErrorMap(new ErrorMap());
            createPurchaseOrderPrintDocument(po);
            try {
                po = (PurchaseOrderDocument)documentService.routeDocument(po, null, null);
            }
            catch (WorkflowException e) {
                LOG.error("Error routing PO document: " + e.getMessage());
                throw new RuntimeException("Error routing PO document: " + e.getMessage());
            }
        }
        else {
            LOG.info("completePurchaseOrder() Unhandled Transmission Status: " + po.getPurchaseOrderTransmissionMethodCode() + " -- Defaulting Status to OPEN");
            purapService.updateStatusAndStatusHistory(po, PurapConstants.PurchaseOrderStatuses.OPEN);
            po.setPurchaseOrderInitialOpenDate(dateTimeService.getCurrentSqlDate());
            this.save(po);
        }
    }
    
    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id) {
        return purchaseOrderDao.getCurrentPurchaseOrder(id);        
    }
    
    public PurchaseOrderDocument getOldestPurchaseOrder(Integer id) {
        return purchaseOrderDao.getOldestPurchaseOrder(id);
    }

    public void setCurrentAndPendingIndicatorsInPostProcessor(PurchaseOrderDocument newPO, String workflowState) {
        if (workflowState.equals(Constants.DocumentStatusCodes.APPROVED)) {
            setCurrentAndPendingIndicatorsForApprovedPODocuments(newPO);
        } 
        else if (workflowState.equals(Constants.DocumentStatusCodes.DISAPPROVED)) {
            setCurrentAndPendingIndicatorsForDisapprovedPODocuments(newPO);
        }
    }
    
    private void setCurrentAndPendingIndicatorsForApprovedPODocuments(PurchaseOrderDocument newPO) {
        //Get the "current PO" that's in the database, i.e. the PO row that contains current indicator = Y
        PurchaseOrderDocument oldPO = getCurrentPurchaseOrder(newPO.getPurapDocumentIdentifier());
        //First, we set the indicators for the oldPO to : Current = N and Pending = N
        oldPO.setPurchaseOrderCurrentIndicator(false);
        oldPO.setPendingActionIndicator(false);
        save(oldPO);
        //Now, we set the "new PO" indicators so that Current = Y and Pending = N
        newPO.setPurchaseOrderCurrentIndicator(true);
        newPO.setPendingActionIndicator(false);        
    }
    
    private void setCurrentAndPendingIndicatorsForDisapprovedPODocuments(PurchaseOrderDocument newPO) {
        //Get the "current PO" that's in the database, i.e. the PO row that contains current indicator = Y
        PurchaseOrderDocument oldPO = getCurrentPurchaseOrder(newPO.getPurapDocumentIdentifier());
        //Set the Pending indicator for the oldPO to N
        oldPO.setPendingActionIndicator(false);
        save(oldPO);
    }
}
