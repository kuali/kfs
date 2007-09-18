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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.bo.Note;
import org.kuali.core.document.DocumentBase;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
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
import org.kuali.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderQuoteStatus;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.dao.PurchaseOrderDao;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocumentBase;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.LogicContainer;
import org.kuali.module.purap.service.PrintService;
import org.kuali.module.purap.service.PurApWorkflowIntegrationService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.RequisitionService;
import org.kuali.module.purap.util.PurApObjectUtils;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
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
    
    public void saveDocumentNoValidation(PurchaseOrderDocument document) {
        try {
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage(); 
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    // TODO delyea - investigate need for this method... in most places if error map has an entry then validation exception is thrown correctly?
    private void saveDocumentNoValidationUsingClearErrorMap(PurchaseOrderDocument document) {
        ErrorMap errorHolder = GlobalVariables.getErrorMap();
        GlobalVariables.setErrorMap(new ErrorMap());
        try {
            saveDocumentNoValidation(document);
        }
        finally {
            GlobalVariables.setErrorMap(errorHolder);
        }
    }

    private void saveDocumentStandardSave(PurchaseOrderDocument document) {
        try {
            documentService.saveDocument(document);
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage(); 
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
//    public PurchaseOrderDocument createAutomaticPurchaseOrderDocument(RequisitionDocument reqDocument) {
//        String newSessionUserId = RiceConstants.SYSTEM_USER;
//        try {
//            UserSession actualUserSession = null;
//            if (!StringUtils.equals(RiceConstants.SYSTEM_USER, GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier())) {
//                actualUserSession = GlobalVariables.getUserSession();
//                GlobalVariables.setUserSession(new UserSession(RiceConstants.SYSTEM_USER));
//            }
//    
//            // update REQ data
//            reqDocument.setPurchaseOrderAutomaticIndicator(Boolean.TRUE);
//            reqDocument.setContractManagerCode(PurapConstants.APO_CONTRACT_MANAGER);
//            // create PO and populate with default data
//            PurchaseOrderDocument poDocument = generatePurchaseOrderFromRequisition(reqDocument);
//            poDocument.setDefaultValuesForAPO();
//            documentService.routeDocument(poDocument, null, null);
//            
//            if (ObjectUtils.isNotNull(actualUserSession)) {
//                GlobalVariables.setUserSession(actualUserSession);
//            }
//
//            return poDocument;
//        }
//        catch (WorkflowException e) {
//            String errorMsg = "Workflow Exception caught: " + e.getLocalizedMessage();
//            LOG.error(errorMsg, e);
//            throw new RuntimeException(errorMsg, e);
//        }
//        catch (UserNotFoundException e) {
//            String errorMsg = "User not found for PersonUserIdentifier '" + newSessionUserId + "'";
//            LOG.error(errorMsg, e);
//            throw new RuntimeException(errorMsg, e);
//        }
//    }
    public void createAutomaticPurchaseOrderDocument(RequisitionDocument reqDocument) {
        String newSessionUserId = RiceConstants.SYSTEM_USER;
        try {
            LogicContainer logicToRun = new LogicContainer() {
                public Object runLogic(Object[] objects) throws Exception {
                    RequisitionDocument doc = (RequisitionDocument)objects[0];
                    // update REQ data
                    doc.setPurchaseOrderAutomaticIndicator(Boolean.TRUE);
                    doc.setContractManagerCode(PurapConstants.APO_CONTRACT_MANAGER);
                    // create PO and populate with default data
                    PurchaseOrderDocument po = generatePurchaseOrderFromRequisition(doc);
                    po.setDefaultValuesForAPO();
                    documentService.routeDocument(po, null, null);
                    return null;
                }
            };
            purapService.performLogicWithFakedUserSession(newSessionUserId, logicToRun, new Object[]{reqDocument});
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
        catch (Exception e) {
            throw new RuntimeException(e);
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
            saveDocumentNoValidation(poDocument);
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
    
    private void addStringErrorMessagesToErrorMap(String errorKey, Collection<String> errors) {
        if (ObjectUtils.isNotNull(errors)) {
            for (String error : errors) {
                LOG.error("Adding error message using error key '" + errorKey + "' with text '" + error + "'");
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, errorKey, error);
            }
        }
    }
    
    /* TODO delyea/PURAP - Below method may be incorrect
     * 
     * This method appears to try to print the quote request list which may or may not return errors.  If no errors are returned the PO
     * is saved.  The PO doesn't appear to be edited in the PrintService and should not be.  If any editing of the PO is occurring that
     * should be happening here in the PO Service leaving the PrintService to simply generate PDFs and potential errors if needed.  If
     * in fact PO is not edited when this method is called the save should be removed completely
     * 
     */
    public boolean printPurchaseOrderQuoteRequestsListPDF(PurchaseOrderDocument po, ByteArrayOutputStream baosPDF) {
        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderQuoteRequestsListPdf(po, baosPDF);

        if (generatePDFErrors.size() > 0) {
            addStringErrorMessagesToErrorMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            return false;
        }
        else {
            // TODO QUOTE - update PurchaseOrderVendorQuote here (delyea - does printing the quote request list update anything on the doc... if not no save needed here)
            saveDocumentStandardSave(po);
            return true;
        }
    }
    
    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#printPurchaseOrderQuotePDF(org.kuali.module.purap.document.PurchaseOrderDocument, org.kuali.module.purap.bo.PurchaseOrderVendorQuote, java.io.ByteArrayOutputStream)
     */
    public boolean printPurchaseOrderQuotePDF(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, ByteArrayOutputStream baosPDF) {

        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderQuotePdf(po, povq, baosPDF, environment);

        if (generatePDFErrors.size() > 0) {
            addStringErrorMessagesToErrorMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            return false;
        }
        else {
            // TODO QUOTE - update PurchaseOrderVendorQuote here
            // TODO QUOTE - PURAP/delyea - if standard save causes errors here examine potential for saving individual updated PurchaseOrderVendorQuote
            saveDocumentStandardSave(po);
            return true;
        }
    }

    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#performPurchaseOrderFirstTransmitViaPrinting(java.lang.String, java.io.ByteArrayOutputStream)
     */
    public void performPurchaseOrderFirstTransmitViaPrinting(String documentNumber, ByteArrayOutputStream baosPDF) {
        PurchaseOrderDocument po = getPurchaseOrderByDocumentNumber(documentNumber);
        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdf(po, baosPDF, environment, null);
        if (!generatePDFErrors.isEmpty()) {
            addStringErrorMessagesToErrorMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            throw new ValidationException("printing purchase order for first transmission failed");
        }
        if (ObjectUtils.isNotNull(po.getPurchaseOrderFirstTransmissionDate())) {
            // should not call this method for first transmission if document has already been transmitted
            String errorMsg = "Method to perform first transmit was called on document (doc id " + documentNumber + ") with already filled in 'first transmit date'";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        Date currentDate = dateTimeService.getCurrentSqlDate();
        po.setPurchaseOrderFirstTransmissionDate(currentDate);
        po.setPurchaseOrderLastTransmitDate(currentDate);
        po.setOverrideWorkflowButtons(Boolean.FALSE);
        boolean performedAction = SpringContext.getBean(PurApWorkflowIntegrationService.class).takeAllActionsForGivenCriteria(po, "Action taken automatically as part of document initial print transmission", NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), GlobalVariables.getUserSession().getUniversalUser(), null);
        if (!performedAction) {
            SpringContext.getBean(PurApWorkflowIntegrationService.class).takeAllActionsForGivenCriteria(po, "Action taken automatically as part of document initial print transmission by user " + GlobalVariables.getUserSession().getUniversalUser().getPersonName(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), null, RiceConstants.SYSTEM_USER);
        }
//        takeWorkflowActionsForDocumentTransmission(po, null);
        po.setOverrideWorkflowButtons(Boolean.TRUE);
        attemptSetupOfInitialOpenOfDocument(po);
    }
    
    public void performPrintPurchaseOrderPDFOnly(String documentNumber, ByteArrayOutputStream baosPDF) {
        PurchaseOrderDocument po = getPurchaseOrderByDocumentNumber(documentNumber);
        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdf(po, baosPDF, environment, null);
        if (!generatePDFErrors.isEmpty()) {
            addStringErrorMessagesToErrorMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            throw new ValidationException("printing purchase order for first transmission failed");
        }
    }
    
    private void takeWorkflowActionsForDocumentTransmission(PurchaseOrderDocument po, String annotation) {
        try {
            List<ActionRequestVO> docTransRequests = new ArrayList<ActionRequestVO>();
            ActionRequestVO[] actionRequests = SpringContext.getBean(KualiWorkflowInfo.class).getActionRequests(Long.valueOf(po.getDocumentNumber()));
            for (ActionRequestVO actionRequestVO : actionRequests) {
                if (actionRequestVO.isActivated()) {
                    if (StringUtils.equals(actionRequestVO.getNodeName(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName())) {
                        docTransRequests.add(actionRequestVO);
                    }
                }
            }
            if (!docTransRequests.isEmpty()) {
                for (ActionRequestVO actionRequest : docTransRequests) {
                    // TODO delyea - UNCOMMENT BELOW ONCE KEW IS UPDATED
//                    po.getDocumentHeader().getWorkflowDocument().superUserActionRequestApproveAction(actionRequest.getActionRequestId(), annotation);
                }
            }
            if (po.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
                SpringContext.getBean(DocumentService.class).approveDocument(po, null, new ArrayList());
            }
            else if (po.getDocumentHeader().getWorkflowDocument().isAcknowledgeRequested()) {
                SpringContext.getBean(DocumentService.class).acknowledgeDocument(po, null, new ArrayList());
            }
            else if (po.getDocumentHeader().getWorkflowDocument().isFYIRequested()) {
                SpringContext.getBean(DocumentService.class).clearDocumentFyi(po, new ArrayList());
            }
        }
        catch (NumberFormatException nfe) {
            String errorMsg = "Exception trying to convert '" + po.getDocumentNumber() + "' into a number (Long)";
            LOG.error(errorMsg, nfe);
            throw new RuntimeException(errorMsg, nfe);
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Exception caught trying to take actions for document transmission node: " + we.getLocalizedMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }
    
    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#retransmitPurchaseOrderPDF(org.kuali.module.purap.document.PurchaseOrderDocument, java.io.ByteArrayOutputStream)
     */
    public void retransmitPurchaseOrderPDF(PurchaseOrderDocument po, ByteArrayOutputStream baosPDF) {

        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        List<PurchaseOrderItem> items = po.getItems();
        List<PurchaseOrderItem> retransmitItems = new ArrayList<PurchaseOrderItem>();
        for (PurchaseOrderItem item : items) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                retransmitItems.add(item);
            }
        }
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdfForRetransmission(po, baosPDF, environment, retransmitItems);

        if (generatePDFErrors.size() > 0) {
            addStringErrorMessagesToErrorMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            throw new ValidationException("found errors while trying to print po with doc id " + po.getDocumentNumber());
        }
        po.setPurchaseOrderLastTransmitDate(dateTimeService.getCurrentSqlDate());
        saveDocumentNoValidation(po);
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
            String errorMsg = "Attempting to create new PO of type '" + docType + "' from source PO doc that is null";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        PurchaseOrderDocument newPurchaseOrderChangeDocument = (PurchaseOrderDocument)documentService.getNewDocument(docType);
        
//        PurApObjectUtils.populateFromBaseWithSuper(sourceDocument, newPurchaseOrderChangeDocument);
        Set classesToExclude = new HashSet();
        Class sourceObjectClass = DocumentBase.class;
        classesToExclude.add(sourceObjectClass);
        while (sourceObjectClass.getSuperclass() != null) {
            sourceObjectClass = sourceObjectClass.getSuperclass();
            classesToExclude.add(sourceObjectClass);
        }
        PurApObjectUtils.populateFromBaseWithSuper(sourceDocument, newPurchaseOrderChangeDocument, PurapConstants.UNCOPYABLE_FIELDS_FOR_PO, classesToExclude);
        newPurchaseOrderChangeDocument.getDocumentHeader().setFinancialDocumentDescription(sourceDocument.getDocumentHeader().getFinancialDocumentDescription());
        newPurchaseOrderChangeDocument.getDocumentHeader().setOrganizationDocumentNumber(sourceDocument.getDocumentHeader().getOrganizationDocumentNumber());

        newPurchaseOrderChangeDocument.setPurchaseOrderCurrentIndicator(false);
        newPurchaseOrderChangeDocument.setPendingActionIndicator(false);
        
        //TODO f2f: what is this doing?
        //Need to find a way to make the ManageableArrayList to expand and populating the items and
        //accounts, otherwise it will complain about the account on item 1 is missing. 
        for (PurApItem item : (List<PurApItem>)newPurchaseOrderChangeDocument.getItems()) {
            item.getSourceAccountingLines().iterator();
            //we only need to do this once to apply to all items, so we can break out of the loop now
            break;
        }
        
        newPurchaseOrderChangeDocument.refreshNonUpdateableReferences();
        return newPurchaseOrderChangeDocument;
    }
    
    public PurchaseOrderDocument createAndSavePotentialChangeDocument(String documentNumber, String docType, String newDocumentStatusCode) {
        PurchaseOrderDocument currentDocument = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderByDocumentNumber(documentNumber);
        try {
            PurchaseOrderDocument newDocument = createPurchaseOrderDocumentFromSourceDocument(currentDocument, docType);
            if (ObjectUtils.isNotNull(newDocument)) {
                // set status if needed
                if (StringUtils.isNotBlank(newDocumentStatusCode)) {
                    newDocument.setStatusCode(newDocumentStatusCode);
                }
                try {
                    documentService.saveDocument(newDocument);
                }
                // if we catch a ValidationException it means the new PO doc found errors
                catch (ValidationException ve) {
                    // TODO PURAP/delyea - old system used to just return the current document
//                    return currentDocument;
                    throw ve;
                }
                // if no validation exception was thrown then rules have passed and we are ok to edit the current PO
                currentDocument.setPendingActionIndicator(true);
                saveDocumentNoValidationUsingClearErrorMap(currentDocument);
                return newDocument;
            }
            else {
                String errorMsg = "Attempting to create new PO of type '" + docType + "' from source PO doc id " + documentNumber + " returned null for new document";
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
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
            if (ObjectUtils.isNotNull(newDocument)) {
                try {
                    documentService.routeDocument(newDocument, annotation, adhocRoutingRecipients);
                }
                // if we catch a ValidationException it means the new PO doc found errors
                catch (ValidationException ve) {
                    // TODO PURAP/delyea - old system used to just return the current document
//                    return currentDocument;
                    throw ve;
                }
                // if no validation exception was thrown then rules have passed and we are ok to edit the current PO
                currentDocument.setPendingActionIndicator(true);
                saveDocumentNoValidationUsingClearErrorMap(currentDocument);
                return newDocument;
            }
            else {
                String errorMsg = "Attempting to create new PO of type '" + docType + "' from source PO doc id " + documentNumber + " returned null for new document";
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
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
        setCurrentAndPendingIndicatorsForApprovedPODocuments(po);
//        // if the status of the PO is already OPEN or is one of the pending transmission statuses... do not change the status to OPEN
//        if ( (!StringUtils.equals(PurchaseOrderStatuses.OPEN,po.getStatusCode())) && (!PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.values().contains(po.getStatusCode())) ) {
//            LOG.info("completePurchaseOrder() Setting po document id " + po.getDocumentNumber() + " status from '" + po.getStatusCode() + "' to '" + PurchaseOrderStatuses.OPEN + "'" );
//            purapService.updateStatusAndStatusHistory(po, PurchaseOrderStatuses.OPEN);
//            po.setPurchaseOrderInitialOpenDate(dateTimeService.getCurrentSqlDate());
//        }
//        this.saveDocumentNoValidation(po);
        // if the document is set in a Pending Transmission status then don't OPEN the PO just leave it as is
        if (!PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.values().contains(po.getStatusCode())) {
            LOG.info("completePurchaseOrder() Setting po document id " + po.getDocumentNumber() + " status from '" + po.getStatusCode() + "' to '" + PurchaseOrderStatuses.OPEN + "'" );
            attemptSetupOfInitialOpenOfDocument(po);
        }
    }
    
    public void setupDocumentForPendingFirstTransmission(PurchaseOrderDocument po, boolean hasActionRequestForDocumentTransmission) {
        if (POTransmissionMethods.PRINT.equals(po.getPurchaseOrderTransmissionMethodCode())) {
            String newStatusCode = PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.get(po.getPurchaseOrderTransmissionMethodCode());
            LOG.debug("setupDocumentForPendingFirstTransmission() Purchase Order Transmission Type is '" + po.getPurchaseOrderTransmissionMethodCode() + "' setting status to '" + newStatusCode + "'");
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
            attemptSetupOfInitialOpenOfDocument(po);
        }
    }
    
    private boolean attemptSetupOfInitialOpenOfDocument(PurchaseOrderDocument po) {
        LOG.debug("attemptSetupOfInitialOpenOfDocument() started using document with doc id " + po.getDocumentNumber());
        boolean documentWasSaved = false;
        if (!PurchaseOrderStatuses.OPEN.equals(po.getStatusCode())) {
            if (ObjectUtils.isNull(po.getPurchaseOrderInitialOpenDate())) {
                LOG.debug("attemptSetupOfInitialOpenOfDocument() setting initial open date on document");
                po.setPurchaseOrderInitialOpenDate(dateTimeService.getCurrentSqlDate());
            }
            else {
                throw new RuntimeException("Document does not have status code '" + PurchaseOrderStatuses.OPEN + "' on it but value of initial open date is " + po.getPurchaseOrderInitialOpenDate());
            }
            LOG.debug("attemptSetupOfInitialOpenOfDocument() Set up document to use status code '" + PurchaseOrderStatuses.OPEN + "'");
            purapService.updateStatusAndStatusHistory(po, PurchaseOrderStatuses.OPEN);
            saveDocumentNoValidation(po);
            documentWasSaved = true;
        }
        else {
            LOG.debug("attemptSetupOfInitialOpenOfDocument() Found document already in '" + PurchaseOrderStatuses.OPEN + "' status... will not change or update");
        }
        return documentWasSaved;
    }

    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id) {
        return getPurchaseOrderByDocumentNumber(purchaseOrderDao.getDocumentNumberForCurrentPurchaseOrder(id));
    }

    public PurchaseOrderDocument getPurchaseOrderByDocumentNumber(String documentNumber) {
        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                PurchaseOrderDocument doc = (PurchaseOrderDocument)documentService.getByDocumentHeaderId(documentNumber);
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
        saveDocumentNoValidationUsingClearErrorMap(oldPO);
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
        saveDocumentNoValidationUsingClearErrorMap(oldPO);
    }

    public ArrayList<PurchaseOrderQuoteStatus> getPurchaseOrderQuoteStatusCodes() {
        ArrayList poQuoteStatuses = new TypedArrayList(PurchaseOrderQuoteStatus.class);
        poQuoteStatuses = (ArrayList) businessObjectService.findAll(PurchaseOrderQuoteStatus.class);
        return poQuoteStatuses;
    }

}
