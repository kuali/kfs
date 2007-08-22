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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.UserSession;
import org.kuali.core.bo.Note;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.RouteDocumentEvent;
import org.kuali.core.rule.event.SaveDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.DocumentSystemSaveEvent;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.POTransmissionMethods;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.PurapConstants.RequisitionSources;
import org.kuali.module.purap.PurapConstants.VendorChoice;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderQuoteStatus;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.dao.PurchaseOrderDao;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocumentBase;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.PrintService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.RequisitionService;
import org.kuali.module.purap.util.PurApObjectUtils;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private NoteService noteService;
    private PurapService purapService;
    private PrintService printService;
    private PurchaseOrderDao purchaseOrderDao;
    private WorkflowDocumentService workflowDocumentService;
    private KualiConfigurationService kualiConfigurationService;
    private KualiRuleService kualiRuleService;
    private VendorService vendorService;
    private RequisitionService requisitionService;

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

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setRequisitionService(RequisitionService requisitionService) {
        this.requisitionService = requisitionService;
    }

    public void saveDocumentWithoutValidation(PurchaseOrderDocument document) {
        try {
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);
//            documentService.saveDocumentWithoutRunningValidation(document);
        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage(); 
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * Creates an automatic PurchaseOrderDocument from given RequisitionDocument. Both documents need to be saved after this method
     * is called.
     * 
     * @param reqDocument - RequisitionDocument that the PO is being created from
     * @return PurchaseOrderDocument
     */
    public PurchaseOrderDocument createAutomaticPurchaseOrderDocument(RequisitionDocument reqDocument) {
        String newSessionUserId = RiceConstants.SYSTEM_USER;
        try {
            UserSession actualUserSession = null;
            if (!StringUtils.equals(RiceConstants.SYSTEM_USER, GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier())) {
                actualUserSession = GlobalVariables.getUserSession();
                GlobalVariables.setUserSession(new UserSession(RiceConstants.SYSTEM_USER));
            }
    
            // update REQ data
            reqDocument.setPurchaseOrderAutomaticIndicator(Boolean.TRUE);
            reqDocument.setContractManagerCode(PurapConstants.APO_CONTRACT_MANAGER);
            // create PO and populate with default data
            PurchaseOrderDocument poDocument = generatePurchaseOrderFromRequisition(reqDocument);
            poDocument.setDefaultValuesForAPO();
            documentService.routeDocument(poDocument, null, null);
            
            if (ObjectUtils.isNotNull(actualUserSession)) {
                GlobalVariables.setUserSession(actualUserSession);
            }

            return poDocument;
        }
        catch (WorkflowException e) {
            String errorMsg = "Workflow Exception caught: " + e.getLocalizedMessage();
            LOG.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
        catch (UserNotFoundException e) {
            String errorMsg = "User not found for PersonUserIdentifier '" + newSessionUserId + "'";
            LOG.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Creates a PurchaseOrderDocument from given RequisitionDocument. Both documents need to be saved after this method is called.
     * 
     * @param reqDocument - RequisitionDocument that the PO is being created from
     * @return PurchaseOrderDocument
     */
    public PurchaseOrderDocument createPurchaseOrderDocument(RequisitionDocument reqDocument) {
        try {
            PurchaseOrderDocument poDocument = generatePurchaseOrderFromRequisition(reqDocument);
            // added line below for JIRA KULOWF-294 - Purchase Order search results are not displaying the total amount on some "in process" purchase orders
            // TODO delyea - RICE CHANGE - below needs to call document service save method passing in no Validate Save Event
//            poDocument.prepareForSave();
//            documentService.updateDocument(poDocument);
//            documentService.prepareWorkflowDocument(poDocument);
//            workflowDocumentService.save(poDocument.getDocumentHeader().getWorkflowDocument(), "", null);
            saveDocumentWithoutValidation(poDocument);
            return poDocument;
        }
        catch (WorkflowException e) {
            LOG.error("Error creating PO document: " + e.getMessage(),e);
            throw new RuntimeException("Error creating PO document: " + e.getMessage(),e);
        }
    }
    
    private PurchaseOrderDocument generatePurchaseOrderFromRequisition(RequisitionDocument reqDocument) throws WorkflowException {
        // create PO and populate with default data
        PurchaseOrderDocument poDocument = null;
//        try {
            poDocument = (PurchaseOrderDocument) documentService.getNewDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT);
            poDocument.populatePurchaseOrderFromRequisition(reqDocument);
            poDocument.setStatusCode(PurchaseOrderStatuses.IN_PROCESS);
            poDocument.setPurchaseOrderCurrentIndicator(true);
            poDocument.setPendingActionIndicator(false);

            // TODO: need this?
            // poDocument.setInternalPurchasingLimit(getInternalPurchasingDollarLimit(po, u.getOrganization().getChart().getCode(),
            // u.getOrganization().getCode()));

            if (RequisitionSources.B2B.equals(poDocument.getRequisitionSourceCode())) {
                poDocument.setPurchaseOrderVendorChoiceCode(VendorChoice.CONTRACTED_PRICE);
            }

            VendorDetail vendor = vendorService.getVendorDetail(poDocument.getVendorHeaderGeneratedIdentifier(), poDocument.getVendorDetailAssignedIdentifier());

            if (ObjectUtils.isNotNull(poDocument.getVendorContract())) {
                poDocument.setVendorPaymentTermsCode(poDocument.getVendorContract().getVendorPaymentTermsCode());
                poDocument.setVendorShippingPaymentTermsCode(poDocument.getVendorContract().getVendorShippingPaymentTermsCode());
                poDocument.setVendorShippingTitleCode(poDocument.getVendorContract().getVendorShippingTitleCode());
            }
            else if (ObjectUtils.isNotNull(vendor)) {
                poDocument.setVendorPaymentTermsCode(vendor.getVendorPaymentTermsCode());
                poDocument.setVendorShippingPaymentTermsCode(vendor.getVendorShippingPaymentTermsCode());
                poDocument.setVendorShippingTitleCode(vendor.getVendorShippingTitleCode());
            }

            purapService.addBelowLineItems(poDocument);

//            if (isAPO) {
//                poDocument.setDefaultValuesForAPO();
//                documentService.routeDocument(poDocument, null, null);
//            }
//            else {
//                documentService.updateDocument(poDocument);
//                documentService.prepareWorkflowDocument(poDocument);
//                workflowDocumentService.save(poDocument.getDocumentHeader().getWorkflowDocument(), "", null);
//            }
//        }
//        catch (WorkflowException e) {
//            LOG.error("Error creating PO document: " + e.getMessage(),e);
//            throw new RuntimeException("Error creating PO document: " + e.getMessage(),e);
//        }
//        catch (Exception e) {
//            LOG.error("Error persisting document # " + poDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(),e);
//            throw new RuntimeException("Error persisting document # " + poDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(),e);
//        }
        return poDocument;
    }

    public KualiDecimal getInternalPurchasingDollarLimit(PurchasingDocumentBase document, String chartCode, String orgCode) {
        if ((document.getVendorContract() != null) && (document.getContractManager() != null)) {
            KualiDecimal contractDollarLimit = vendorService.getApoLimitFromContract(document.getVendorContract().getVendorContractGeneratedIdentifier(), chartCode, orgCode);
            KualiDecimal contractManagerLimit = document.getContractManager().getContractManagerDelegationDollarLimit();
            if ((contractDollarLimit != null) && (contractManagerLimit != null)) {
                if (contractDollarLimit.compareTo(contractManagerLimit) > 0) {
                    return contractDollarLimit;
                }
                else {
                    return contractManagerLimit;
                }
            } else if (contractDollarLimit != null) {
                return contractDollarLimit;
            } else {
                return contractManagerLimit;
            }
        }
        else if ((document.getVendorContract() == null) && (document.getContractManager() != null)) {
            return document.getContractManager().getContractManagerDelegationDollarLimit();
        }
        else if ((document.getVendorContract() != null) && (document.getContractManager() == null)) {
            return purapService.getApoLimit(document.getVendorContract().getVendorContractGeneratedIdentifier(), chartCode, orgCode);
        }
        else {
            String errorMsg = "No internal purchase order dollar limit found for purchase order '" + document.getPurapDocumentIdentifier() + "'.";
            LOG.warn(errorMsg);
            return null;
        }
    }
    
    public boolean printPurchaseOrderQuoteRequestsListPDF(PurchaseOrderDocument po, ByteArrayOutputStream baosPDF) {
        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderQuoteRequestsListPdf(po, baosPDF);

        if (generatePDFErrors.size() > 0) {
            for (String error : generatePDFErrors) {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, error);
            }
            return false;
        }
        else {
            // TODO QUOTE - update PurchaseOrderVendorQuote here
            saveDocumentWithoutValidation(po);
            return true;
        }
    }

    public boolean printPurchaseOrderQuotePDF(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, ByteArrayOutputStream baosPDF) {

        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderQuotePdf(po, povq, baosPDF, environment);

        if (generatePDFErrors.size() > 0) {
            for (String error : generatePDFErrors) {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, error);
            }
            return false;
        }
        else {
            // TODO QUOTE - update PurchaseOrderVendorQuote here
            saveDocumentWithoutValidation(po);
            return true;
        }
    }

    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#printPurchaseOrderPDF(org.kuali.module.purap.document.PurchaseOrderDocument,
     *      java.lang.String, java.lang.String, java.util.List, java.io.ByteArrayOutputStream, java.lang.String)
     */
    public boolean printPurchaseOrderPDF(PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients, ByteArrayOutputStream baosPDF) {

        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        boolean isRetransmit = false;
        boolean result = true;
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdf(po, baosPDF, isRetransmit, environment);

        if (generatePDFErrors.size() > 0) {
            for (String error : generatePDFErrors) {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, error);
            }
            result = false;
        }
        else {
            // perform workflow action if needed
        }
        return result;
    }

    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#printPurchaseOrderPDF(org.kuali.module.purap.document.PurchaseOrderDocument,
     *      java.lang.String, java.lang.String, java.util.List, java.io.ByteArrayOutputStream, java.lang.String)
     */
    public boolean retransmitPurchaseOrderPDF(PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients, ByteArrayOutputStream baosPDF) {

        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        boolean isRetransmit = true;
        boolean result = true;
        List<PurchaseOrderItem> items = po.getItems();
        List<PurchaseOrderItem> retransmitItems = new ArrayList();
        for (PurchaseOrderItem item : items) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                item.refreshNonUpdateableReferences();
                retransmitItems.add(item);
            }
        }
        po.setItems(retransmitItems);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdf(po, baosPDF, isRetransmit, environment);

        if (generatePDFErrors.size() > 0) {
            for (String error : generatePDFErrors) {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, error);
            }
            result = false;
        }
        // below logic moved to post processor PurchaseOrderPostProcessorRetransmitService.handleRouteStatusChange()
//        if (result) {
//            Date currentSqlDate = dateTimeService.getCurrentSqlDate();
//            po.setPurchaseOrderLastTransmitDate(currentSqlDate);
//            po.setPurchaseOrderCurrentIndicator(true);
//            saveDocumentWithoutValidation(po);
//        }
        return result;
    }

    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#updateFlagsAndRoute(org.kuali.module.purap.document.PurchaseOrderDocument,
     *      java.lang.String, java.lang.String, java.util.List)
     */
//    public PurchaseOrderDocument updateFlagsAndRoute(String documentNumber, String docType, String annotation, List adhocRoutingRecipients) {
//        try {
//            PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderByDocumentNumber(documentNumber);
//            //TODO: Chris - RESEARCH: does this have any effect?  I think it may be lost before the po is brought up again.
//            po.setSummaryAccountsWithItems(new HashMap());
//            po.setSummaryAccountsWithItemsKey(new ArrayList());
//            po.setSummaryAccountsWithItemsValue(new ArrayList());
//            po.setPendingActionIndicator(true);
//
//            businessObjectService.save(po);
//
//            PurchaseOrderDocument newPO = (PurchaseOrderDocument)documentService.getNewDocument(docType);
//            //TODO: Chris - RESEARCH: does this have any effect?  I think it may be lost before the po is brought up again.
//            newPO.refreshAccountSummary();
//            PurApObjectUtils.populateFromBaseWithSuper(po, newPO);
//            newPO.refreshNonUpdateableReferences();
//            newPO.setPurchaseOrderCurrentIndicator(false);
//            newPO.setPendingActionIndicator(false);
//
//            // check the rules before taking action
//            if (docType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
//                boolean rulePassed = kualiRuleService.applyRules(new SaveDocumentEvent(newPO));
//                if (!rulePassed) {
//                    po.setPendingActionIndicator(false);
//                    saveDocumentWithoutValidation(po);
//                    return po;
//                }
//                else {
//                    newPO.setStatusCode(PurapConstants.PurchaseOrderStatuses.AMENDMENT);
//                    saveDocumentWithoutValidation(po);
//                    documentService.saveDocument(newPO);
//                }
//            }
//            else {
//                boolean rulePassed = kualiRuleService.applyRules(new RouteDocumentEvent(newPO));
//                if (!rulePassed) {
//                    po.setPendingActionIndicator(false);
//                    saveDocumentWithoutValidation(po);
//                    return po;
//                }
//                else {
//                    saveDocumentWithoutValidation(po);
//                    documentService.routeDocument(newPO, annotation, adhocRoutingRecipients);
//                }
//            }
//            return newPO;
//        }
//        catch (WorkflowException we) {
//            LOG.error("Error during updateFlagsAndRoute on PO document: " + we);
//            throw new RuntimeException("Error during updateFlagsAndRoute on PO document: " + we.getMessage(),we);
//        }
//        catch (OjbOperationException ooe) {
//            LOG.error("Error during updateFlagsAndRoute on PO document: " + ooe);
//            throw new RuntimeException("Error during updateFlagsAndRoute on PO document: " + ooe.getMessage(),ooe);
//        }
//        catch (Exception e) {
//            LOG.error("Error during updateFlagsAndRoute on PO document: " + e);
//            throw new RuntimeException("Error during updateFlagsAndRoute on PO document: " + e.getMessage(),e);
//        }
//    }
    
    /**
     * This method creates a new Purchase Order Document using the given document type based off the given source document.  This method will
     * return null if the source document given is null.<br>
     * <br>
     *  ** THIS METHOD DOES NOT SAVE EITHER THE GIVEN SOURCE DOCUMENT OR THE NEW DOCUMENT CREATED
     * 
     * @param sourceDocument - document the new Purchase Order Document should be based off of in terms of data
     * @param docType - document type of the potential new Purchase Order Document
     * @return the new Purchase Order Document of the given document type or null if the given source document is null
     * @throws WorkflowException if a new document cannot be created using the given type
     */
    private PurchaseOrderDocument createPurchaseOrderDocumentFromSourceDocument(PurchaseOrderDocument sourceDocument, String docType) throws WorkflowException {
        if (ObjectUtils.isNull(sourceDocument)) {
            return null;
        }
        //TODO: Chris - RESEARCH: does this have any effect?  I think it may be lost before the po is brought up again.
        sourceDocument.setSummaryAccountsWithItems(new HashMap());
        sourceDocument.setSummaryAccountsWithItemsKey(new ArrayList());
        sourceDocument.setSummaryAccountsWithItemsValue(new ArrayList());

        PurchaseOrderDocument newPurchaseOrderChangeDocument = (PurchaseOrderDocument)documentService.getNewDocument(docType);
        //TODO: Chris - RESEARCH: does this have any effect?  I think it may be lost before the po is brought up again.
        newPurchaseOrderChangeDocument.refreshAccountSummary();
        PurApObjectUtils.populateFromBaseWithSuper(sourceDocument, newPurchaseOrderChangeDocument);
        newPurchaseOrderChangeDocument.refreshNonUpdateableReferences();
        newPurchaseOrderChangeDocument.setPurchaseOrderCurrentIndicator(false);
        newPurchaseOrderChangeDocument.setPendingActionIndicator(false);
        return newPurchaseOrderChangeDocument;
    }
    
    /**
     * This method validate the new document created against the document even passed in.  If the new document is valid the following will occur:<br>
     * <ol>
     * <li>The new status code given is set on the new document (if the status code is given)
     * <li>The current document has the pending action indicator set to true to indicate that the new PO is proceeding as planned
     * </ol>
     * 
     * @param event - the Document event that will be processed for business rules
     * @param newDocumentStatusCode - 
     * @param currentDocument
     * @param newDocument
     * @return
     */
    private boolean validateAndSetUpCurrentAndNewDocuments(KualiDocumentEvent event, String newDocumentStatusCode, PurchaseOrderDocument currentDocument, PurchaseOrderDocument newDocument) {
        if (ObjectUtils.isNotNull(newDocument)) {
            if (kualiRuleService.applyRules(event)) {
                // new document is valid
                if (StringUtils.isNotBlank(newDocumentStatusCode)) {
                    // set status if possible
                    newDocument.setStatusCode(newDocumentStatusCode);
                }
                currentDocument.setPendingActionIndicator(true);
                return true;
            }
        }
        return false;
    }

    public PurchaseOrderDocument createAndSavePotentialChangeDocument(String documentNumber, String docType, String newDocumentStatusCode) {
        PurchaseOrderDocument currentDocument = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderByDocumentNumber(documentNumber);
        try {
            PurchaseOrderDocument newDocument = createPurchaseOrderDocumentFromSourceDocument(currentDocument, docType);
            boolean valid = validateAndSetUpCurrentAndNewDocuments(new SaveDocumentEvent(newDocument), newDocumentStatusCode, currentDocument, newDocument);
            if (valid) {
                /*
                 *  TODO PURAP/delyea -  BELOW SECTION HAVE TRY/CATCH BLOCK REMOVED (LEAVING CODE INSIDE THE TRY) AFTER RICE IS UPDATED
                 * 
                 */
                try {
                    saveDocumentWithoutValidation(currentDocument);
                }
                catch (ValidationException ve) {
                    LOG.error("Caught Validation Exception trying to save old PO... calling save in BO Service", ve);
                    businessObjectService.save(currentDocument);
                }
                /*
                 *  TODO PURAP/delyea -  ABOVE SECTION HAVE TRY/CATCH BLOCK REMOVED (LEAVING CODE INSIDE THE TRY) AFTER RICE IS UPDATED
                 * 
                 */
                documentService.saveDocument(newDocument);
                return newDocument;
            }
            return currentDocument;
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Exception caught trying to create and save PO document of type '" + docType + "' using source document with doc id '" + documentNumber + "'";
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    public PurchaseOrderDocument createAndRoutePotentialChangeDocument(String documentNumber, String docType, String annotation, List adhocRoutingRecipients) {
        PurchaseOrderDocument currentDocument = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderByDocumentNumber(documentNumber);
        try {
            PurchaseOrderDocument newDocument = createPurchaseOrderDocumentFromSourceDocument(currentDocument, docType);
            boolean valid = validateAndSetUpCurrentAndNewDocuments(new RouteDocumentEvent(newDocument), null, currentDocument, newDocument);
            if (valid) {
                /*
                 *  TODO PURAP/delyea -  BELOW SECTION HAVE TRY/CATCH BLOCK REMOVED (LEAVING CODE INSIDE THE TRY) AFTER RICE IS UPDATED
                 * 
                 */
                try {
                    saveDocumentWithoutValidation(currentDocument);
                }
                catch (ValidationException ve) {
                    LOG.error("Caught Validation Exception trying to save old PO... calling save in BO Service", ve);
                    documentService.updateDocument(currentDocument);
                }
                /*
                 *  TODO PURAP/delyea -  ABOVE SECTION HAVE TRY/CATCH BLOCK REMOVED (LEAVING CODE INSIDE THE TRY) AFTER RICE IS UPDATED
                 * 
                 */
                documentService.routeDocument(newDocument, annotation, adhocRoutingRecipients);
                return newDocument;
            }
            return currentDocument;
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Exception caught trying to create and route PO document of type '" + docType + "' using source document with doc id '" + documentNumber + "'";
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    public void cancelAmendment(PurchaseOrderDocument document) {
        updateCurrentDocumentForNoPendingAction(document);
    }
    
    private String getCurrentRouteNodeName(KualiWorkflowDocument wd) throws WorkflowException {
        String[] nodeNames = wd.getNodeNames();
        if ((nodeNames == null) || (nodeNames.length == 0)) {
            return null;
        }
        else {
            return nodeNames[0];
        }
    }

    public void completePurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("completePurchaseOrder() started");
        String pendingTransmissionStatusCode = PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.get(po.getPurchaseOrderTransmissionMethodCode());
        // if the status of the PO is already OPEN or is one of the pending transmission statuses... do not change the status to OPEN
        if ( (!StringUtils.equals(PurchaseOrderStatuses.OPEN,po.getStatusCode())) && (!PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.values().contains(po.getStatusCode())) ) {
            LOG.info("completePurchaseOrder() Setting po document id " + po.getDocumentNumber() + " status from '" + po.getStatusCode() + "' to '" + PurchaseOrderStatuses.OPEN + "'" );
            purapService.updateStatusAndStatusHistory(po, PurchaseOrderStatuses.OPEN);
            po.setPurchaseOrderInitialOpenDate(dateTimeService.getCurrentSqlDate());
        }
        this.saveDocumentWithoutValidation(po);
    }
    
    public void setupDocumentForPendingFirstTransmission(PurchaseOrderDocument po, boolean hasActionRequestForDocumentTransmission) {
        if (POTransmissionMethods.PRINT.equals(po.getPurchaseOrderTransmissionMethodCode())) {
            String newStatusCode = PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.get(po.getPurchaseOrderTransmissionMethodCode());
            LOG.debug("setupDocumentForPendingFirstTransmission() Purchase Order Transmission Type is '" + po.getPurchaseOrderTransmissionMethodCode() + "' setting status to '" + newStatusCode + "'");
            po.setPurchaseOrderCurrentIndicator(true);
            po.setPendingActionIndicator(true);
            purapService.updateStatusAndStatusHistory(po, newStatusCode);
        }
        else {
            if (hasActionRequestForDocumentTransmission) {
                /*
                 * here we error out because the document generated a request for the doc transmission route level but the default
                 * status to set is open... this prevents Open purchase orders that could be awaiting transmission by a valid method
                 * (via the generated request)
                 */
                String errorMessage = "An action request was generated for document id " + po.getDocumentNumber() + " with an unhandled transmission type '" + po.getPurchaseOrderTransmissionMethodCode() + "'";
                LOG.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
            LOG.info("setupDocumentForPendingFirstTransmission() Unhandled Transmission Status: " + po.getPurchaseOrderTransmissionMethodCode() + " -- Defaulting Status to '" + PurchaseOrderStatuses.OPEN + "'");
            if (!PurchaseOrderStatuses.OPEN.equals(po.getStatusCode())) {
                if (ObjectUtils.isNull(po.getPurchaseOrderInitialOpenDate())) {
                    po.setPurchaseOrderInitialOpenDate(dateTimeService.getCurrentSqlDate());
                }
                purapService.updateStatusAndStatusHistory(po, PurchaseOrderStatuses.OPEN);
            }
        }
    }

    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id) {
        return getPurchaseOrderByDocumentNumber(purchaseOrderDao.getDocumentNumberForCurrentPurchaseOrder(id));
    }

    public PurchaseOrderDocument getPurchaseOrderByDocumentNumber(String documentNumber) {
        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                PurchaseOrderDocument doc = (PurchaseOrderDocument)documentService.getByDocumentHeaderId(documentNumber);
                if (ObjectUtils.isNotNull(doc)) {
                    doc.refreshNonUpdateableReferences();
                }
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting purchase order document from document service";
                LOG.error("getPurchaseOrderByDocumentNumber() " + errorMessage,e);
                throw new RuntimeException(errorMessage,e);
            }
        }
        return null;
    }
    
    public PurchaseOrderDocument getOldestPurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("entering getOldestPO(PurchaseOrderDocument)");
        if (ObjectUtils.isNotNull(po)) {
            String oldestDocumentNumber = purchaseOrderDao.getOldestPurchaseOrderDocumentNumber(po.getPurapDocumentIdentifier());
            if (StringUtils.equals(oldestDocumentNumber, po.getDocumentNumber())) {
                //manually set bo notes - this is mainly done for performance reasons (preferably we could call
                //retrieve doc notes in PersistableBusinessObjectBase but that is private)
                po.setBoNotes(SpringContext.getBean(NoteService.class).getByRemoteObjectId(po.getObjectId()));
                LOG.debug("exiting getOldestPO(PurchaseOrderDocument)");
                return po;
            }
            LOG.debug("exiting getOldestPO(PurchaseOrderDocument)");
            return getPurchaseOrderByDocumentNumber(oldestDocumentNumber);
        }
        return null;
    }
    
    public ArrayList<Note> getPurchaseOrderNotes(Integer id) {
        ArrayList notes = new TypedArrayList(Note.class);
        PurchaseOrderDocument po = getPurchaseOrderByDocumentNumber(purchaseOrderDao.getOldestPurchaseOrderDocumentNumber(id));
        if (ObjectUtils.isNotNull(po)) {
            notes = noteService.getByRemoteObjectId(po.getObjectId());
        }
        return notes;
    }

    public void setCurrentAndPendingIndicatorsForApprovedPODocuments(PurchaseOrderDocument newPO) {
        // Get the "current PO" that's in the database, i.e. the PO row that contains current indicator = Y
        PurchaseOrderDocument oldPO = getCurrentPurchaseOrder(newPO.getPurapDocumentIdentifier());
        // First, we set the indicators for the oldPO to : Current = N and Pending = N
        oldPO.setPurchaseOrderCurrentIndicator(false);
        oldPO.setPendingActionIndicator(false);
        saveDocumentWithoutValidation(oldPO);
        // Now, we set the "new PO" indicators so that Current = Y and Pending = N
        newPO.setPurchaseOrderCurrentIndicator(true);
        newPO.setPendingActionIndicator(false);
    }

    public void setCurrentAndPendingIndicatorsForDisapprovedPODocuments(PurchaseOrderDocument newPO) {
        updateCurrentDocumentForNoPendingAction(newPO);
    }
    
    private void updateCurrentDocumentForNoPendingAction(PurchaseOrderDocument newPO) {
        // Get the "current PO" that's in the database, i.e. the PO row that contains current indicator = Y
        PurchaseOrderDocument oldPO = getCurrentPurchaseOrder(newPO.getPurapDocumentIdentifier());
        // Set the Pending indicator for the oldPO to N
        oldPO.setPendingActionIndicator(false);
        saveDocumentWithoutValidation(oldPO);
    }

    public ArrayList<PurchaseOrderQuoteStatus> getPurchaseOrderQuoteStatusCodes() {
        ArrayList poQuoteStatuses = new TypedArrayList(PurchaseOrderQuoteStatus.class);
        poQuoteStatuses = (ArrayList) businessObjectService.findAll(PurchaseOrderQuoteStatus.class);
        return poQuoteStatuses;
    }
}
