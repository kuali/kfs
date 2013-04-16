/*
 * Copyright 2006 The Kuali Foundation
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

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.kfs.module.purap.PurapConstants.POTransmissionMethods;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.batch.AutoCloseRecurringOrdersStep;
import org.kuali.kfs.module.purap.businessobject.AutoClosePurchaseOrderView;
import org.kuali.kfs.module.purap.businessobject.ContractManagerAssignmentDetail;
import org.kuali.kfs.module.purap.businessobject.CreditMemoView;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteStatus;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingThreshold;
import org.kuali.kfs.module.purap.document.ContractManagerAssignmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderSplitDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.dataaccess.PurchaseOrderDao;
import org.kuali.kfs.module.purap.document.service.B2BPurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.LogicContainer;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PrintService;
import org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.util.ThresholdHelper;
import org.kuali.kfs.module.purap.util.ThresholdHelper.ThresholdSummary;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.util.ObjectPopulationUtils;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorConstants.AddressTypes;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorPhoneNumber;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentBase;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.service.MaintenanceDocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected NoteService noteService;
    protected PurapService purapService;
    protected PrintService printService;
    protected PurchaseOrderDao purchaseOrderDao;
    protected WorkflowDocumentService workflowDocumentService;
    protected ConfigurationService kualiConfigurationService;
    protected KualiRuleService kualiRuleService;
    protected VendorService vendorService;
    protected RequisitionService requisitionService;
    protected PurApWorkflowIntegrationService purapWorkflowIntegrationService;
    protected MaintenanceDocumentService maintenanceDocumentService;
    protected ParameterService parameterService;
    protected PersonService personService;
    protected MailService mailService;
    protected B2BPurchaseOrderService b2bPurchaseOrderService;
    protected DataDictionaryService dataDictionaryService;



    @Override
    public boolean isPurchaseOrderOpenForProcessing(Integer poId) {
        return isPurchaseOrderOpenForProcessing(getCurrentPurchaseOrder(poId));
    }

    @Override
    public boolean isPurchaseOrderOpenForProcessing(PurchaseOrderDocument purchaseOrderDocument) {
        boolean can = PurchaseOrderStatuses.APPDOC_OPEN.equals(purchaseOrderDocument.getApplicationDocumentStatus());
        can = can && purchaseOrderDocument.isPurchaseOrderCurrentIndicator() && !purchaseOrderDocument.isPendingActionIndicator();
        // can't be any PREQ or CM that have not completed fullDocumentEntry
        if (can) {
            List<PaymentRequestView> preqViews = purchaseOrderDocument.getRelatedViews().getRelatedPaymentRequestViews();
            if ( preqViews != null ) {
                for (PaymentRequestView preqView : preqViews) {
                if (!purapService.isPaymentRequestFullDocumentEntryCompleted(preqView.getApplicationDocumentStatus())) {
                        return false;
                    }
                }
            }
            List<CreditMemoView> cmViews = purchaseOrderDocument.getRelatedViews().getRelatedCreditMemoViews();
            if ( cmViews != null ) {
                for (CreditMemoView cmView : cmViews) {
                if (!purapService.isVendorCreditMemoFullDocumentEntryCompleted(cmView.getApplicationDocumentStatus())) {
                        return false;
                    }
                }
            }
        }

        // passed all conditions; return true
        return can;
    }

    @Override
    public boolean isCommodityCodeRequiredOnPurchaseOrder() {
        boolean enableCommodityCode = parameterService.getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_COMMODITY_CODE_IND);
        if (!enableCommodityCode) {
            return false;
        }
        else {
            return parameterService.getParameterValueAsBoolean(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND);
        }
    }

    /**
     * Sets the error map to a new, empty error map before calling saveDocumentNoValidation to save the document.
     *
     * @param document The purchase order document to be saved.
     */
    protected void saveDocumentNoValidationUsingClearMessageMap(PurchaseOrderDocument document) {
        MessageMap errorHolder = GlobalVariables.getMessageMap();
        GlobalVariables.setMessageMap(new MessageMap());
        try {
            purapService.saveDocumentNoValidation(document);
        }
        finally {
            GlobalVariables.setMessageMap(errorHolder);
        }
    }

    /**
     * Calls the saveDocument method of documentService to save the document.
     *
     * @param document The document to be saved.
     */
    protected void saveDocumentStandardSave(PurchaseOrderDocument document) {
        try {
            documentService.saveDocument(document);
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    @Override
    public PurchasingCapitalAssetItem createCamsItem(PurchasingDocument purDoc, PurApItem purapItem) {
        PurchasingCapitalAssetItem camsItem = new PurchaseOrderCapitalAssetItem();
        camsItem.setItemIdentifier(purapItem.getItemIdentifier());
        // If the system type is INDIVIDUAL then for each of the capital asset items, we need a system attached to it.
        if (purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS)) {
            CapitalAssetSystem resultSystem = new PurchaseOrderCapitalAssetSystem();
            camsItem.setPurchasingCapitalAssetSystem(resultSystem);
        }
        camsItem.setPurchasingDocument(purDoc);

        return camsItem;
    }

    @Override
    public CapitalAssetSystem createCapitalAssetSystem() {
        CapitalAssetSystem resultSystem = new PurchaseOrderCapitalAssetSystem();
        return resultSystem;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#createAutomaticPurchaseOrderDocument(org.kuali.kfs.module.purap.document.RequisitionDocument)
     */
    @Override
    public void createAutomaticPurchaseOrderDocument(RequisitionDocument reqDocument) {
        String newSessionUserId = KFSConstants.SYSTEM_USER;
        try {
            LogicContainer logicToRun = new LogicContainer() {
                @Override
                public Object runLogic(Object[] objects) throws Exception {
                    RequisitionDocument doc = (RequisitionDocument) objects[0];
                    // update REQ data
                    doc.setPurchaseOrderAutomaticIndicator(Boolean.TRUE);
                    // create PO and populate with default data
                    PurchaseOrderDocument po = generatePurchaseOrderFromRequisition(doc);
                    po.setDefaultValuesForAPO();
                    //check for print transmission method.. if print is selected
                    //the doc status needs to be "Pending To Print"..
                    checkForPrintTransmission(po);
                    po.setContractManagerCode(PurapConstants.APO_CONTRACT_MANAGER);

                    documentService.routeDocument(po, null, null);

                    final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
                    documentAttributeIndexingQueue.indexDocument(po.getDocumentNumber());

                    return null;
                }
            };
            purapService.performLogicWithFakedUserSession(newSessionUserId, logicToRun, new Object[] { reqDocument });
        }
        catch (WorkflowException e) {
            String errorMsg = "Workflow Exception caught: " + e.getLocalizedMessage();
            LOG.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * checks for print option and if chosen then sets the app doc status to
     * Pending To Print.
     *
     * @param po
     */
    protected void checkForPrintTransmission(PurchaseOrderDocument po) throws WorkflowException {
        if (PurapConstants.POTransmissionMethods.PRINT.equals( po.getPurchaseOrderRetransmissionMethodCode())) {
            po.updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_PENDING_PRINT);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#createPurchaseOrderDocument(org.kuali.kfs.module.purap.document.RequisitionDocument,
     *      java.lang.String, java.lang.Integer)
     */
    @Override
    public PurchaseOrderDocument createPurchaseOrderDocument(RequisitionDocument reqDocument, String newSessionUserId, Integer contractManagerCode) {
        try {
            LogicContainer logicToRun = new LogicContainer() {
                @Override
                public Object runLogic(Object[] objects) throws Exception {
                    RequisitionDocument doc = (RequisitionDocument) objects[0];
                    PurchaseOrderDocument po = generatePurchaseOrderFromRequisition(doc);
                    Integer cmCode = (Integer) objects[1];
                    po.setContractManagerCode(cmCode);
                    purapService.saveDocumentNoValidation(po);
                    return po;
                }
            };
            return (PurchaseOrderDocument) purapService.performLogicWithFakedUserSession(newSessionUserId, logicToRun, new Object[] { reqDocument, contractManagerCode });
        }
        catch (WorkflowException e) {
            String errorMsg = "Workflow Exception caught: " + e.getLocalizedMessage();
            LOG.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create Purchase Order and populate with data from Requisition and other default data
     *
     * @param reqDocument The requisition document from which we create the purchase order document.
     * @return The purchase order document created by this method.
     * @throws WorkflowException
     */
    protected PurchaseOrderDocument generatePurchaseOrderFromRequisition(RequisitionDocument reqDocument) throws WorkflowException {
        PurchaseOrderDocument poDocument = null;
        poDocument = (PurchaseOrderDocument) documentService.getNewDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT);
        poDocument.populatePurchaseOrderFromRequisition(reqDocument);

        poDocument.updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS);

        poDocument.setPurchaseOrderCurrentIndicator(true);
        poDocument.setPendingActionIndicator(false);

        if (RequisitionSources.B2B.equals(poDocument.getRequisitionSourceCode())) {
            String paramName = PurapParameterConstants.DEFAULT_B2B_VENDOR_CHOICE;
            String paramValue = parameterService.getParameterValueAsString(PurchaseOrderDocument.class, paramName);
            poDocument.setPurchaseOrderVendorChoiceCode(paramValue);
        }

        if (ObjectUtils.isNotNull(poDocument.getVendorContract())) {
            poDocument.setVendorPaymentTermsCode(poDocument.getVendorContract().getVendorPaymentTermsCode());
            poDocument.setVendorShippingPaymentTermsCode(poDocument.getVendorContract().getVendorShippingPaymentTermsCode());
            poDocument.setVendorShippingTitleCode(poDocument.getVendorContract().getVendorShippingTitleCode());
        }
        else {
            VendorDetail vendor = vendorService.getVendorDetail(poDocument.getVendorHeaderGeneratedIdentifier(), poDocument.getVendorDetailAssignedIdentifier());
            if (ObjectUtils.isNotNull(vendor)) {
                poDocument.setVendorPaymentTermsCode(vendor.getVendorPaymentTermsCode());
                poDocument.setVendorShippingPaymentTermsCode(vendor.getVendorShippingPaymentTermsCode());
                poDocument.setVendorShippingTitleCode(vendor.getVendorShippingTitleCode());
            }
        }

        if (!PurapConstants.RequisitionSources.B2B.equals(poDocument.getRequisitionSourceCode())) {
            purapService.addBelowLineItems(poDocument);
        }
        poDocument.fixItemReferences();

        return poDocument;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#getInternalPurchasingDollarLimit(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public KualiDecimal getInternalPurchasingDollarLimit(PurchaseOrderDocument document) {
        if ((document.getVendorContract() != null) && (document.getContractManager() != null)) {
            KualiDecimal contractDollarLimit = vendorService.getApoLimitFromContract(document.getVendorContract().getVendorContractGeneratedIdentifier(), document.getChartOfAccountsCode(), document.getOrganizationCode());
            //FIXME somehow data fields such as contractManagerDelegationDollarLimit in reference object contractManager didn't get retrieved
            // (are null) as supposed to be (this happens whether or not proxy is set to true), even though contractManager is not null;
            // so here we have to manually refresh the contractManager to retrieve the fields
            if (document.getContractManager().getContractManagerDelegationDollarLimit() == null) {
                document.refreshReferenceObject(PurapPropertyConstants.CONTRACT_MANAGER);
            }
            KualiDecimal contractManagerLimit = document.getContractManager().getContractManagerDelegationDollarLimit();
            if ((contractDollarLimit != null) && (contractManagerLimit != null)) {
                if (contractDollarLimit.compareTo(contractManagerLimit) > 0) {
                    return contractDollarLimit;
                }
                else {
                    return contractManagerLimit;
                }
            }
            else if (contractDollarLimit != null) {
                return contractDollarLimit;
            }
            else {
                return contractManagerLimit;
            }
        }
        else if ((document.getVendorContract() == null) && (document.getContractManager() != null)) {
            //FIXME As above, here we have to manually refresh the contractManager to retrieve its field
            if (document.getContractManager().getContractManagerDelegationDollarLimit() == null) {
                document.refreshReferenceObject(PurapPropertyConstants.CONTRACT_MANAGER);
            }
            return document.getContractManager().getContractManagerDelegationDollarLimit();
        }
        else if ((document.getVendorContract() != null) && (document.getContractManager() == null)) {
            return purapService.getApoLimit(document.getVendorContract().getVendorContractGeneratedIdentifier(), document.getChartOfAccountsCode(), document.getOrganizationCode());
        }
        else {
            String errorMsg = "No internal purchase order dollar limit found for purchase order '" + document.getPurapDocumentIdentifier() + "'.";
            LOG.warn(errorMsg);
            return null;
        }
    }

    /**
     * Loops through the collection of error messages and adding each of them to the error map.
     *
     * @param errorKey The resource key used to retrieve the error text from the error message resource bundle.
     * @param errors The collection of error messages.
     */
    protected void addStringErrorMessagesToMessageMap(String errorKey, Collection<String> errors) {
        if (ObjectUtils.isNotNull(errors)) {
            for (String error : errors) {
                LOG.error("Adding error message using error key '" + errorKey + "' with text '" + error + "'");
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, errorKey, error);
            }
        }
    }


    /**
     * TODO RELEASE 3 - QUOTE
     *
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#printPurchaseOrderQuoteRequestsListPDF(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    public boolean printPurchaseOrderQuoteRequestsListPDF(String documentNumber, ByteArrayOutputStream baosPDF) {
        PurchaseOrderDocument po = getPurchaseOrderByDocumentNumber(documentNumber);
        String environment = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderQuoteRequestsListPdf(po, baosPDF);

        if (generatePDFErrors.size() > 0) {
            addStringErrorMessagesToMessageMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            return false;
        } else {
            return true;
        }
    }

    /**
     * TODO RELEASE 3 - QUOTE
     *
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#printPurchaseOrderQuotePDF(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote, java.io.ByteArrayOutputStream)
     */
    @Override
    public boolean printPurchaseOrderQuotePDF(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, ByteArrayOutputStream baosPDF) {
        String environment = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderQuotePdf(po, povq, baosPDF, environment);

        if (generatePDFErrors.size() > 0) {
            addStringErrorMessagesToMessageMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#performPurchaseOrderFirstTransmitViaPrinting(java.lang.String,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    public void performPurchaseOrderFirstTransmitViaPrinting(String documentNumber, ByteArrayOutputStream baosPDF) {
        PurchaseOrderDocument po = getPurchaseOrderByDocumentNumber(documentNumber);
        String environment = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdf(po, baosPDF, environment, null);
        if (!generatePDFErrors.isEmpty()) {
            addStringErrorMessagesToMessageMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            throw new ValidationException("printing purchase order for first transmission failed");
        }
        if (ObjectUtils.isNotNull(po.getPurchaseOrderFirstTransmissionTimestamp())) {
            // should not call this method for first transmission if document has already been transmitted
            String errorMsg = "Method to perform first transmit was called on document (doc id " + documentNumber + ") with already filled in 'first transmit date'";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        Timestamp currentDate = dateTimeService.getCurrentTimestamp();
        po.setPurchaseOrderFirstTransmissionTimestamp(currentDate);
        po.setPurchaseOrderLastTransmitTimestamp(currentDate);
        po.setOverrideWorkflowButtons(Boolean.FALSE);
        boolean performedAction = purapWorkflowIntegrationService.takeAllActionsForGivenCriteria(po, "Action taken automatically as part of document initial print transmission", PurapConstants.PurchaseOrderStatuses.NODE_DOCUMENT_TRANSMISSION, GlobalVariables.getUserSession().getPerson(), null);
        if (!performedAction) {
            Person systemUserPerson = getPersonService().getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
            purapWorkflowIntegrationService.takeAllActionsForGivenCriteria(po, "Action taken automatically as part of document initial print transmission by user " + GlobalVariables.getUserSession().getPerson().getName(), PurapConstants.PurchaseOrderStatuses.NODE_DOCUMENT_TRANSMISSION, systemUserPerson, KFSConstants.SYSTEM_USER);
        }
        po.setOverrideWorkflowButtons(Boolean.TRUE);
        if (!po.getApplicationDocumentStatus().equals(PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN)) {
            attemptSetupOfInitialOpenOfDocument(po);
        }
        purapService.saveDocumentNoValidation(po);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#performPurchaseOrderPreviewPrinting(java.lang.String,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    public void performPurchaseOrderPreviewPrinting(String documentNumber, ByteArrayOutputStream baosPDF) {
        performPrintPurchaseOrderPDFOnly(documentNumber, baosPDF);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#performPrintPurchaseOrderPDFOnly(java.lang.String,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    public void performPrintPurchaseOrderPDFOnly(String documentNumber, ByteArrayOutputStream baosPDF) {
        PurchaseOrderDocument po = getPurchaseOrderByDocumentNumber(documentNumber);
        String environment = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdf(po, baosPDF, environment, null);
        if (!generatePDFErrors.isEmpty()) {
            addStringErrorMessagesToMessageMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            throw new ValidationException("printing purchase order for first transmission failed");
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#retransmitPurchaseOrderPDF(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    public void retransmitPurchaseOrderPDF(PurchaseOrderDocument po, ByteArrayOutputStream baosPDF) {

        String environment = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        List<PurchaseOrderItem> items = po.getItems();
        List<PurchaseOrderItem> retransmitItems = new ArrayList<PurchaseOrderItem>();
        for (PurchaseOrderItem item : items) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                retransmitItems.add(item);
            }
        }
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdfForRetransmission(po, baosPDF, environment, retransmitItems);

        if (generatePDFErrors.size() > 0) {
            addStringErrorMessagesToMessageMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            throw new ValidationException("found errors while trying to print po with doc id " + po.getDocumentNumber());
        }
        po.setPurchaseOrderLastTransmitTimestamp(dateTimeService.getCurrentTimestamp());
        purapService.saveDocumentNoValidation(po);
    }

    /**
     * This method creates a new Purchase Order Document using the given document type based off the given source document. This
     * method will return null if the source document given is null.<br>
     * <br> ** THIS METHOD DOES NOT SAVE EITHER THE GIVEN SOURCE DOCUMENT OR THE NEW DOCUMENT CREATED
     *
     * @param sourceDocument - document the new Purchase Order Document should be based off of in terms of data
     * @param docType - document type of the potential new Purchase Order Document
     * @return the new Purchase Order Document of the given document type or null if the given source document is null
     * @throws WorkflowException if a new document cannot be created using the given type
     */
    protected PurchaseOrderDocument createPurchaseOrderDocumentFromSourceDocument(PurchaseOrderDocument sourceDocument, String docType) throws WorkflowException {
        if (ObjectUtils.isNull(sourceDocument)) {
            String errorMsg = "Attempting to create new PO of type '" + docType + "' from source PO doc that is null";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        PurchaseOrderDocument newPurchaseOrderChangeDocument = (PurchaseOrderDocument) documentService.getNewDocument(docType);
        newPurchaseOrderChangeDocument.setAccountDistributionMethod(sourceDocument.getAccountDistributionMethod());

        Set classesToExclude = new HashSet();
        Class sourceObjectClass = FinancialSystemTransactionalDocumentBase.class;
        classesToExclude.add(sourceObjectClass);
        while (sourceObjectClass.getSuperclass() != null) {
            sourceObjectClass = sourceObjectClass.getSuperclass();
            classesToExclude.add(sourceObjectClass);
        }
        ObjectPopulationUtils.populateFromBaseWithSuper(sourceDocument, newPurchaseOrderChangeDocument, PurapConstants.uncopyableFieldsForPurchaseOrder(), classesToExclude);
        newPurchaseOrderChangeDocument.getDocumentHeader().setDocumentDescription(sourceDocument.getDocumentHeader().getDocumentDescription());
        newPurchaseOrderChangeDocument.getDocumentHeader().setOrganizationDocumentNumber(sourceDocument.getDocumentHeader().getOrganizationDocumentNumber());
        newPurchaseOrderChangeDocument.getDocumentHeader().setExplanation(sourceDocument.getDocumentHeader().getExplanation());
        newPurchaseOrderChangeDocument.setPurchaseOrderCurrentIndicator(false);
        newPurchaseOrderChangeDocument.setPendingActionIndicator(false);

        // TODO f2f: what is this doing?
        // Need to find a way to make the ManageableArrayList to expand and populating the items and
        // accounts, otherwise it will complain about the account on item 1 is missing.
        for (PurApItem item : (List<PurApItem>) newPurchaseOrderChangeDocument.getItems()) {
            item.getSourceAccountingLines().iterator();
            // we only need to do this once to apply to all items, so we can break out of the loop now
            SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);
            Integer itemIdentifier = sas.getNextAvailableSequenceNumber("PO_ITM_ID", PurApItem.class).intValue();
            item.setItemIdentifier(itemIdentifier);
        }

        updateCapitalAssetRelatedCollections(newPurchaseOrderChangeDocument);
        newPurchaseOrderChangeDocument.refreshNonUpdateableReferences();

        return newPurchaseOrderChangeDocument;
    }

    protected void updateCapitalAssetRelatedCollections(PurchaseOrderDocument newDocument) {

        for (PurchasingCapitalAssetItem capitalAssetItem : newDocument.getPurchasingCapitalAssetItems()) {
            Integer lineNumber = capitalAssetItem.getPurchasingItem().getItemLineNumber();
            PurApItem newItem = newDocument.getItemByLineNumber(lineNumber.intValue());
            capitalAssetItem.setItemIdentifier(newItem.getItemIdentifier());
            capitalAssetItem.setPurchasingDocument(newDocument);
            capitalAssetItem.setCapitalAssetSystemIdentifier(null);
            CapitalAssetSystem oldSystem = capitalAssetItem.getPurchasingCapitalAssetSystem();
            capitalAssetItem.setPurchasingCapitalAssetSystem(new PurchaseOrderCapitalAssetSystem(oldSystem));

        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#createAndSavePotentialChangeDocument(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public PurchaseOrderDocument createAndSavePotentialChangeDocument(String documentNumber, String docType, String currentDocumentStatusCode) {
        PurchaseOrderDocument currentDocument = getPurchaseOrderByDocumentNumber(documentNumber);

        try {
            PurchaseOrderDocument newDocument = createPurchaseOrderDocumentFromSourceDocument(currentDocument, docType);

            if (ObjectUtils.isNotNull(newDocument)) {
                newDocument.updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS);

                // set status if needed
                if (StringUtils.isNotBlank(currentDocumentStatusCode)) {
                    currentDocument.updateAndSaveAppDocStatus(currentDocumentStatusCode);
                }
                try {
                    documentService.saveDocument(newDocument, DocumentSystemSaveEvent.class);
                }
                // if we catch a ValidationException it means the new PO doc found errors
                catch (ValidationException ve) {
                    throw ve;
                }
                // if no validation exception was thrown then rules have passed and we are ok to edit the current PO
                currentDocument.setPendingActionIndicator(true);
                savePurchaseOrderData(currentDocument);

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

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#createAndRoutePotentialChangeDocument(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.List, java.lang.String)
     */
    @Override
    public PurchaseOrderDocument createAndRoutePotentialChangeDocument(String documentNumber, String docType, String annotation, List adhocRoutingRecipients, String currentDocumentStatusCode) {
        PurchaseOrderDocument currentDocument = getPurchaseOrderByDocumentNumber(documentNumber);

        try {
            currentDocument.updateAndSaveAppDocStatus(currentDocumentStatusCode);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Error saving routing data while saving document with id " + currentDocument.getDocumentNumber(), e);
        }

        try {
            PurchaseOrderDocument newDocument = createPurchaseOrderDocumentFromSourceDocument(currentDocument, docType);
            newDocument.updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS);

            if (ObjectUtils.isNotNull(newDocument)) {
                try {
                    // set the pending indictor before routing, so that when routing is done in synch mode, the pending indicator won't be set again after route finishes and cause inconsistency
                    currentDocument.setPendingActionIndicator(true);
                    documentService.routeDocument(newDocument, annotation, adhocRoutingRecipients);
                }
                // if we catch a ValidationException it means the new PO doc found errors
                catch (ValidationException ve) {
                    //clear the pending indictor if an exception occurs, to leave the existing PO intact
                    currentDocument.setPendingActionIndicator(false);
                    savePurchaseOrderData(currentDocument);

                    throw ve;
                }
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

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#createAndSavePurchaseOrderSplitDocument(java.util.List, java.lang.String, boolean)
     */
    @Override
    public PurchaseOrderSplitDocument createAndSavePurchaseOrderSplitDocument(List<PurchaseOrderItem> newPOItems, PurchaseOrderDocument currentDocument, boolean copyNotes, String splitNoteText) {

    	if (ObjectUtils.isNull(currentDocument)) {
            String errorMsg = "Attempting to create new PO of type PurchaseOrderSplitDocument from source PO doc that is null";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        String documentNumber = currentDocument.getDocumentNumber();

        try {
            // Create the new Split PO document (throws WorkflowException)
            // Assign PO's initiator to Split PO.
            Person person = SpringContext.getBean(PersonService.class).getPerson(currentDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
            PurchaseOrderSplitDocument newDocument = (PurchaseOrderSplitDocument) documentService.getNewDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT,person.getPrincipalName());

            if (ObjectUtils.isNotNull(newDocument)) {

                // Prepare for copying fields over from the current document.
                Set<Class> classesToExclude = getClassesToExcludeFromCopy();
                Map<String, Class> uncopyableFields = PurapConstants.UNCOPYABLE_FIELDS_FOR_PO;
                uncopyableFields.putAll(PurapConstants.uncopyableFieldsForSplitPurchaseOrder());

                // Copy all fields over from the current document except the items and the above-specified fields.
                ObjectPopulationUtils.populateFromBaseWithSuper(currentDocument, newDocument, uncopyableFields, classesToExclude);
                newDocument.getDocumentHeader().setDocumentDescription(currentDocument.getDocumentHeader().getDocumentDescription());
                newDocument.getDocumentHeader().setOrganizationDocumentNumber(currentDocument.getDocumentHeader().getOrganizationDocumentNumber());
                newDocument.setPurchaseOrderCurrentIndicator(true);
                newDocument.setPendingActionIndicator(false);
                newDocument.setAccountDistributionMethod(currentDocument.getAccountDistributionMethod());
                // Add in and renumber the items that the new document should have.
                newDocument.setItems(newPOItems);
                purapService.addBelowLineItems(newDocument);
                newDocument.renumberItems(0);

                newDocument.setPostingYear(currentDocument.getPostingYear());

                if (copyNotes) {
                    // Copy the old notes, except for the one that contains the split note text.
                    List<Note> notes = currentDocument.getNotes();
                    int noteLength = notes.size();
                    if (noteLength > 0) {
                        notes.subList(noteLength - 1, noteLength).clear();
                        for (Note note : notes) {
                            try {
                                Note copyingNote = documentService.createNoteFromDocument(newDocument, note.getNoteText());
                                newDocument.addNote(copyingNote);
                                noteService.saveNoteList(notes);
                            }
                            catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                newDocument.updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS);

                //fix references before saving
                fixItemReferences(newDocument);
                newDocument.clearCapitalAssetFields();
                //need to save the document first before creating the note
                purapService.saveDocumentNoValidation(newDocument);

                // Modify the split note text and add the note.
                splitNoteText = splitNoteText.substring(splitNoteText.indexOf(":") + 1);
                splitNoteText = PurapConstants.PODocumentsStrings.SPLIT_NOTE_PREFIX_NEW_DOC + currentDocument.getPurapDocumentIdentifier() + " : " + splitNoteText;
                try {
                    Note splitNote = documentService.createNoteFromDocument(newDocument, splitNoteText);
                    newDocument.addNote(splitNote);
                    noteService.save(splitNote);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return newDocument;
            }
            else {
                String errorMsg = "Attempting to create new PO of type 'PurchaseOrderSplitDocument' from source PO doc id " + documentNumber + " returned null for new document";
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Exception caught trying to create and save PO document of type PurchaseOrderSplitDocument using source document with doc id '" + documentNumber + "'";
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * Gets a set of classes to exclude from those whose fields will be copied during a copy operation from one Document to
     * another.
     *
     * @return A Set<Class>
     */
    protected Set<Class> getClassesToExcludeFromCopy() {
        Set<Class> classesToExclude = new HashSet<Class>();
        Class sourceObjectClass = DocumentBase.class;
        classesToExclude.add(sourceObjectClass);
        while (sourceObjectClass.getSuperclass() != null) {
            sourceObjectClass = sourceObjectClass.getSuperclass();
            classesToExclude.add(sourceObjectClass);
        }
        return classesToExclude;
    }

    /**
     * Returns the current route node name.
     *
     * @param wd The KualiWorkflowDocument object whose current route node we're trying to get.
     * @return The current route node name.
     * @throws WorkflowException
     */
    protected String getCurrentRouteNodeName(WorkflowDocument wd) throws WorkflowException {
        String[] nodeNames = (String[]) wd.getNodeNames().toArray();
        if ((nodeNames == null) || (nodeNames.length == 0)) {
            return null;
        }
        else {
            return nodeNames[0];
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#completePurchaseOrder(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void completePurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("completePurchaseOrder() started");
        setCurrentAndPendingIndicatorsForApprovedPODocuments(po);
        setupDocumentForPendingFirstTransmission(po);

        // check thresholds to see if receiving is required for purchase order
        if (!po.isReceivingDocumentRequiredIndicator()) {
            setReceivingRequiredIndicatorForPurchaseOrder(po);
        }

        // update the vendor record if the commodity code used on the PO is not already associated with the vendor.
        updateVendorCommodityCode(po);

        // PERFORM ANY LOGIC THAT COULD POTENTIALLY CAUSE THE DOCUMENT TO FAIL BEFORE THIS LINE
        // FOLLOWING LINES COULD INVOLVE TRANSMITTING THE PO TO THE VENDOR WHICH WILL NOT BE REVERSED IN A TRANSACTION ROLLBACK

        // if the document is set in a Pending Transmission status then don't OPEN the PO just leave it as is
        if (!PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.values().contains(po.getApplicationDocumentStatus())) {
            attemptSetupOfInitialOpenOfDocument(po);
        }
        else if (PurchaseOrderStatuses.APPDOC_PENDING_CXML.equals(po.getApplicationDocumentStatus())) {
            completeB2BPurchaseOrder(po);
        }
        else if (PurchaseOrderStatuses.APPDOC_PENDING_PRINT.equals(po.getApplicationDocumentStatus())) {
            //default to using user that routed PO
            String userToRouteFyi = po.getDocumentHeader().getWorkflowDocument().getRoutedByPrincipalId();
            if (po.getPurchaseOrderAutomaticIndicator()) {
                //if APO, use the user that initiated the requisition
                RequisitionDocument req = requisitionService.getRequisitionById(po.getRequisitionIdentifier());
                userToRouteFyi = req.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
            }

            Set<String> currentNodes = po.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
            if (CollectionUtils.isNotEmpty(currentNodes)) {
                po.getDocumentHeader().getWorkflowDocument().adHocToPrincipal(ActionRequestType.FYI, currentNodes.iterator().next(), "This PO is ready for printing and distribution.", userToRouteFyi, "", true, "PRINT");

            }
        }

    }

    protected boolean completeB2BPurchaseOrder(PurchaseOrderDocument po) {
        String errors = b2bPurchaseOrderService.sendPurchaseOrder(po);
        if (StringUtils.isEmpty(errors)) {
            //PO sent successfully; change status to OPEN
            attemptSetupOfInitialOpenOfDocument(po);
            po.setPurchaseOrderLastTransmitTimestamp(dateTimeService.getCurrentTimestamp());
            return true;
        }
        else {
            //PO transmission failed; record errors and change status to "cxml failed"
            try {
                String noteText = "Unable to transmit the PO for the following reasons:\n" + errors;
                int noteMaxSize = dataDictionaryService.getAttributeMaxLength("Note", "noteText");

                // Break up the note into multiple pieces if the note is too large to fit in the database field.
                while (noteText.length() > noteMaxSize) {
                    int fromIndex = 0;
                    String noteText1 = noteText.substring(0, noteMaxSize);
                    Note note1 = documentService.createNoteFromDocument(po, noteText1);
                    po.addNote(note1);
                    noteText = noteText.substring(noteMaxSize);
                }

                Note note = documentService.createNoteFromDocument(po, noteText);
                po.addNote(note);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                po.updateAndSaveAppDocStatus(PurchaseOrderStatuses.APPDOC_CXML_ERROR);
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Error saving routing data while saving document with id " + po.getDocumentNumber(), e);
            }

            return false;
        }
    }

    @Override
    public void retransmitB2BPurchaseOrder(PurchaseOrderDocument po) {
        if (completeB2BPurchaseOrder(po)) {
            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.B2B_PO_RETRANSMIT_SUCCESS);
        }
        else {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PurapKeyConstants.B2B_PO_RETRANSMIT_FAILED);
        }
        purapService.saveDocumentNoValidation(po);
    }

    @Override
    public void completePurchaseOrderAmendment(PurchaseOrderDocument poa) {
        LOG.debug("completePurchaseOrderAmendment() started");

        setCurrentAndPendingIndicatorsForApprovedPODocuments(poa);

        // check thresholds to see if receiving is required for purchase order amendment
        if (!poa.isReceivingDocumentRequiredIndicator() &&
                !SpringContext.getBean(PaymentRequestService.class).hasActivePaymentRequestsForPurchaseOrder(poa.getPurapDocumentIdentifier())) {
            setReceivingRequiredIndicatorForPurchaseOrder(poa);
        }

        // if unordered items have been added to the PO then send an FYI to all fiscal officers
        if (hasNewUnorderedItem(poa)) {
            sendFyiForNewUnorderedItems(poa);
        }

    }

    /**
     * If there are commodity codes on the items on the PurchaseOrderDocument that
     * haven't existed yet on the vendor that the PurchaseOrderDocument is using,
     * then we will spawn a new VendorDetailMaintenanceDocument automatically to
     * update the vendor with the commodity codes that aren't already existing on
     * the vendor.
     *
     * @param po The PurchaseOrderDocument containing the vendor that we want to update.
     */
    @Override
    public void updateVendorCommodityCode(PurchaseOrderDocument po) {
        String noteText = "";
        VendorDetail oldVendorDetail = po.getVendorDetail();
        VendorDetail newVendorDetail = updateVendorWithMissingCommodityCodesIfNecessary(po);
        if (newVendorDetail != null) {
            try {
                // spawn a new vendor maintenance document to add the note
                MaintenanceDocument vendorMaintDoc = null;
                try {
                    vendorMaintDoc = (MaintenanceDocument) documentService.getNewDocument("PVEN");
                    vendorMaintDoc.getDocumentHeader().setDocumentDescription("Automatically spawned from PO");
                    vendorMaintDoc.getOldMaintainableObject().setBusinessObject(oldVendorDetail);
                    vendorMaintDoc.getNewMaintainableObject().setBusinessObject(newVendorDetail);
                    vendorMaintDoc.getNewMaintainableObject().setMaintenanceAction(KFSConstants.MAINTENANCE_EDIT_ACTION);
                    vendorMaintDoc.getNewMaintainableObject().setDocumentNumber(vendorMaintDoc.getDocumentNumber());
                    boolean isVendorLocked = checkForLockingDocument(vendorMaintDoc);
                    if (!isVendorLocked) {
                        //validating vendor doc to capture exception before trying to route which if exception happens in docService, then PO will fail too
                        vendorMaintDoc.validateBusinessRules(new RouteDocumentEvent(vendorMaintDoc));
                        addNoteForCommodityCodeToVendor(vendorMaintDoc.getNewMaintainableObject(), vendorMaintDoc.getDocumentNumber(), po.getPurapDocumentIdentifier());
                        documentService.routeDocument(vendorMaintDoc, null, null);
                    }
                    else {
                        // Add a note to the PO to tell the users that we can't automatically update the vendor because it's locked.
                        noteText = "Unable to automatically update vendor because it is locked";
                    }
                }
                catch (Exception e) {
                    if (ObjectUtils.isNull(vendorMaintDoc)) {
                        noteText = "Unable to create a new VendorDetailMaintenanceDocument to update the vendor with new commodity codes";
                    }
                    else {
                        noteText = "Unable to route a new VendorDetailMaintenanceDocument to update the vendor with new commodity codes";
                    }
                }
                finally {
                    if (StringUtils.isNotBlank(noteText)) {
                        // update on purchase order notes
                        Note note = documentService.createNoteFromDocument(po, noteText);
                        po.addNote(note);
                        noteService.save(note);
                    }
                }
            }
            catch (Exception e) {
                LOG.error("updateVendorCommodityCode() unable to add a note(" + noteText + ") to PO document " + po.getDocumentNumber());
            }
        }
    }

    /**
     * Creates a note to be added to the Vendor Maintenance Document which is spawned
     * from the PurchaseOrderDocument.
     *
     * @param maintainable
     * @param documentNumber
     * @param poID
     */
    protected void addNoteForCommodityCodeToVendor(Maintainable maintainable, String documentNumber, Integer poID) {
        Note newBONote = new Note();
        newBONote.setNoteText("Change vendor document ID <" + documentNumber + ">. Document was automatically created from PO <" + poID + "> to add commodity codes used on this PO that were not yet assigned to this vendor.");
        try {

            newBONote = noteService.createNote(newBONote, maintainable.getBusinessObject(), GlobalVariables.getUserSession().getPrincipalId());
            newBONote.setNotePostedTimestampToCurrent();
        }
        catch (Exception e) {
            throw new RuntimeException("Caught Exception While Trying To Add Note to Vendor", e);
        }
        List<Note> noteList = noteService.getByRemoteObjectId(maintainable.getBusinessObject().getObjectId());
        noteList.add(newBONote);
        noteService.saveNoteList(noteList);
    }

    /**
     * Checks whether the vendor is currently locked.
     *
     * @param document The MaintenanceDocument containing the vendor.
     * @return boolean true if the vendor is currently locked and false otherwise.
     */
    protected boolean checkForLockingDocument(MaintenanceDocument document) {
        String blockingDocId = maintenanceDocumentService.getLockingDocumentId(document);
        if (StringUtils.isBlank(blockingDocId)) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#updateVendorWithMissingCommodityCodesIfNecessary(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public VendorDetail updateVendorWithMissingCommodityCodesIfNecessary(PurchaseOrderDocument po) {
        List<CommodityCode> result = new ArrayList<CommodityCode>();
        boolean foundDefault = false;
        VendorDetail vendor = (VendorDetail)ObjectUtils.deepCopy(po.getVendorDetail());
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>)po.getItems()) {
            //Only check on commodity codes if the item is active and is above the line item type.
            if (item.getItemType().isLineItemIndicator() && item.isItemActiveIndicator()) {
                CommodityCode cc = item.getCommodityCode();
                if (cc != null && !result.contains(cc)) {
                    List<VendorCommodityCode> vendorCommodityCodes = po.getVendorDetail().getVendorCommodities();
                    boolean foundMatching = false;
                    for (VendorCommodityCode vcc : vendorCommodityCodes) {
                        if (vcc.getCommodityCode().getPurchasingCommodityCode().equals(cc.getPurchasingCommodityCode())) {
                            foundMatching = true;
                        }
                        if (!foundDefault && vcc.isCommodityDefaultIndicator()) {
                            foundDefault = true;
                        }
                    }
                    if (!foundMatching) {
                        result.add(cc);
                        VendorCommodityCode vcc = new VendorCommodityCode(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), cc, true);
                        vcc.setActive(true);
                        if (!foundDefault) {
                            vcc.setCommodityDefaultIndicator(true);
                            foundDefault = true;
                        }
                        vendor.getVendorCommodities().add(vcc);
                    }
                }
            }
        }
        if (result.size() > 0) {
            //We also have to add to the old vendor detail's vendorCommodities if we're adding to the new
            //vendor detail's vendorCommodities.
            for (int i=0; i<result.size(); i++) {
                po.getVendorDetail().getVendorCommodities().add(new VendorCommodityCode());
            }
            return vendor;
        }
        else {
            return null;
        }
    }

    /**
     * Update the purchase order document with the appropriate status for pending first transmission based on the transmission type.
     *
     * @param po The purchase order document whose status to be updated.
     */
    protected void setupDocumentForPendingFirstTransmission(PurchaseOrderDocument po) {
        if (POTransmissionMethods.PRINT.equals(po.getPurchaseOrderTransmissionMethodCode()) || POTransmissionMethods.FAX.equals(po.getPurchaseOrderTransmissionMethodCode()) || POTransmissionMethods.ELECTRONIC.equals(po.getPurchaseOrderTransmissionMethodCode())) {
            String newStatusCode = PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.get(po.getPurchaseOrderTransmissionMethodCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug("setupDocumentForPendingFirstTransmission() Purchase Order Transmission Type is '" + po.getPurchaseOrderTransmissionMethodCode() + "' setting status to '" + newStatusCode + "'");
            }
            try {
                po.updateAndSaveAppDocStatus(newStatusCode);
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Error saving routing data while saving document with id " + po.getDocumentNumber(), e);
            }
        }
    }

    /**
     * If the status of the purchase order is not OPEN and the initial open date is null, sets the initial open date to current date
     * and update the status to OPEN, then save the purchase order.
     *
     * @param po The purchase order document whose initial open date and status we want to update.
     */
    protected void attemptSetupOfInitialOpenOfDocument(PurchaseOrderDocument po) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("attemptSetupOfInitialOpenOfDocument() started using document with doc id " + po.getDocumentNumber());
        }

        if (!PurchaseOrderStatuses.APPDOC_OPEN.equals(po.getApplicationDocumentStatus())) {
            if (ObjectUtils.isNull(po.getPurchaseOrderInitialOpenTimestamp())) {
                LOG.debug("attemptSetupOfInitialOpenOfDocument() setting initial open date on document");
                po.setPurchaseOrderInitialOpenTimestamp(dateTimeService.getCurrentTimestamp());
            }
            else {
                throw new RuntimeException("Document does not have status code '" + PurchaseOrderStatuses.APPDOC_OPEN + "' on it but value of initial open date is " + po.getPurchaseOrderInitialOpenTimestamp());
            }
            LOG.info("attemptSetupOfInitialOpenOfDocument() Setting po document id " + po.getDocumentNumber() + " status from '" + po.getApplicationDocumentStatus() + "' to '" + PurchaseOrderStatuses.APPDOC_OPEN + "'");
            try {
                po.updateAndSaveAppDocStatus(PurchaseOrderStatuses.APPDOC_OPEN);
            }
            catch (WorkflowException we) {
                throw new RuntimeException("Unable to load a WorkflowDocument object for " + po.getDocumentNumber(), we);
            }
        }
        else {
            LOG.error("attemptSetupOfInitialOpenOfDocument() Found document already in '" + PurchaseOrderStatuses.APPDOC_OPEN + "' status for PO#" + po.getPurapDocumentIdentifier() + "; will not change or update");
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#getCurrentPurchaseOrder(java.lang.Integer)
     */
    @Override
    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id) {
        return getPurchaseOrderByDocumentNumber(purchaseOrderDao.getDocumentNumberForCurrentPurchaseOrder(id));
        //TODO hjs: code review (why is this DB call so complicated?  wouldn't this method be cleaner and less db calls?)
//        return purchaseOrderDao.getCurrentPurchaseOrder(id);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#getPurchaseOrderByDocumentNumber(java.lang.String)
     */
    @Override
    public PurchaseOrderDocument getPurchaseOrderByDocumentNumber(String documentNumber) {
        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                PurchaseOrderDocument doc = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(documentNumber);
                if (ObjectUtils.isNotNull(doc)) {
                    WorkflowDocument workflowDocument = doc.getDocumentHeader().getWorkflowDocument();
                    doc.refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
                    doc.getDocumentHeader().setWorkflowDocument(workflowDocument);
                }
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting purchase order document from document service";
                LOG.error("getPurchaseOrderByDocumentNumber() " + errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#getOldestPurchaseOrder(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public PurchaseOrderDocument getOldestPurchaseOrder(PurchaseOrderDocument po, PurchaseOrderDocument documentBusinessObject) {
        LOG.debug("entering getOldestPO(PurchaseOrderDocument)");
        if (ObjectUtils.isNotNull(po)) {
            String oldestDocumentNumber = purchaseOrderDao.getOldestPurchaseOrderDocumentNumber(po.getPurapDocumentIdentifier());
            //KFSMI-9746 -- See Harsha's comments...
            if (StringUtils.isBlank(oldestDocumentNumber)) {
                return null;
            }
            if (StringUtils.equals(oldestDocumentNumber, po.getDocumentNumber())) {
                // manually set bo notes - this is mainly done for performance reasons (preferably we could call
                // retrieve doc notes in PersistableBusinessObjectBase but that is protected)
                updateNotes(po, documentBusinessObject);
                LOG.debug("exiting getOldestPO(PurchaseOrderDocument)");
                return po;
            }
            else {
                PurchaseOrderDocument oldestPurchaseOrder = getPurchaseOrderByDocumentNumber(oldestDocumentNumber);
                updateNotes(oldestPurchaseOrder, documentBusinessObject);
                LOG.debug("exiting getOldestPO(PurchaseOrderDocument)");
                return oldestPurchaseOrder;
            }
        }
        return null;
    }

    /**
     * If the purchase order's object id is not null (I think this means if it's an existing purchase order that had already been
     * saved to the db previously), get the notes of the purchase order from the database, fix the notes' fields by calling the
     * fixDbNoteFields, then set the notes to the purchase order. Otherwise (I think this means if it's a new purchase order), set
     * the notes of this purchase order to be the notes of the documentBusinessObject.
     *
     * @param po The current purchase order.
     * @param documentBusinessObject The oldest purchase order whose purapDocumentIdentifier is the same as the po's
     *        purapDocumentIdentifier.
     */
    protected void updateNotes(PurchaseOrderDocument po, PurchaseOrderDocument documentBusinessObject) {
        if (ObjectUtils.isNotNull(documentBusinessObject)) {
            if (ObjectUtils.isNotNull(po.getObjectId())) {
                List<Note> dbNotes = noteService.getByRemoteObjectId(po.getObjectId());
                // need to set fields that are not ojb managed (i.e. the notes on the documentBusinessObject may have been modified
                // independently of the ones in the db)
                fixDbNoteFields(documentBusinessObject, dbNotes);
                po.setNotes(dbNotes);
            }
            else{
                po.setNotes(documentBusinessObject.getNotes());
            }
        }
    }

    /**
     * This method fixes non ojb managed missing fields from the db
     *
     * @param documentBusinessObject The oldest purchase order whose purapDocumentIdentifier is the same as the po's
     *        purapDocumentIdentifier.
     * @param dbNotes The notes of the purchase order obtained from the database.
     */
    protected void fixDbNoteFields(PurchaseOrderDocument documentBusinessObject, List<Note> dbNotes) {
        for (int i = 0; i < dbNotes.size(); i++) {
            Note dbNote = dbNotes.get(i);
            List<Note> currentNotes = documentBusinessObject.getNotes();
            if (i < currentNotes.size()) {
                Note currentNote = (currentNotes).get(i);
                // set the fyi from the current note if not empty
                AdHocRouteRecipient fyiNoteRecipient = currentNote.getAdHocRouteRecipient();
                if (ObjectUtils.isNotNull(fyiNoteRecipient)) {
                    dbNote.setAdHocRouteRecipient(fyiNoteRecipient);
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#getPurchaseOrderNotes(java.lang.Integer)
     */

    @Override
    public List<Note> getPurchaseOrderNotes(Integer id) {
        List<Note> notes = new ArrayList<Note>();
        PurchaseOrderDocument po = getPurchaseOrderByDocumentNumber(purchaseOrderDao.getOldestPurchaseOrderDocumentNumber(id));

        if (ObjectUtils.isNotNull(po)) {

            notes = noteService.getByRemoteObjectId(po.getDocumentHeader().getObjectId());
        }
        return notes;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#setCurrentAndPendingIndicatorsForApprovedPODocuments(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void setCurrentAndPendingIndicatorsForApprovedPODocuments(PurchaseOrderDocument newPO) {
        // Get the "current PO" that's in the database, i.e. the PO row that contains current indicator = Y
        PurchaseOrderDocument oldPO = getCurrentPurchaseOrder(newPO.getPurapDocumentIdentifier());

        // If the document numbers between the oldPO and the newPO are different, then this is a PO change document.
        if (!oldPO.getDocumentNumber().equals(newPO.getDocumentNumber())) {
            // First, we set the indicators for the oldPO to : Current = N and Pending = N
            oldPO.setPurchaseOrderCurrentIndicator(false);
            oldPO.setPendingActionIndicator(false);

            // set the status and status history of the oldPO to retired version
            try {
                oldPO.updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_RETIRED_VERSION);
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Error saving routing data while saving document with id " + oldPO.getDocumentNumber(), e);
            }

            savePurchaseOrderData(oldPO);
        }

        // Now, we set the "new PO" indicators so that Current = Y and Pending = N
        newPO.setPurchaseOrderCurrentIndicator(true);
        newPO.setPendingActionIndicator(false);

        // KFSMI-9879 - Don't save the newPO here - if it's a PO Close Doc, that could delete previously
        // saved GLPEs.
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#setCurrentAndPendingIndicatorsForDisapprovedChangePODocuments(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void setCurrentAndPendingIndicatorsForDisapprovedChangePODocuments(PurchaseOrderDocument newPO) {
        updateCurrentDocumentForNoPendingAction(newPO, PurapConstants.PurchaseOrderStatuses.APPDOC_DISAPPROVED_CHANGE, PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#setCurrentAndPendingIndicatorsForCancelledChangePODocuments(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void setCurrentAndPendingIndicatorsForCancelledChangePODocuments(PurchaseOrderDocument newPO) {
        updateCurrentDocumentForNoPendingAction(newPO, PurapConstants.PurchaseOrderStatuses.APPDOC_CANCELLED_CHANGE, PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#setCurrentAndPendingIndicatorsForCancelledReopenPODocuments(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void setCurrentAndPendingIndicatorsForCancelledReopenPODocuments(PurchaseOrderDocument newPO) {
        updateCurrentDocumentForNoPendingAction(newPO, PurapConstants.PurchaseOrderStatuses.APPDOC_CANCELLED_CHANGE, PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#setCurrentAndPendingIndicatorsForDisapprovedReopenPODocuments(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void setCurrentAndPendingIndicatorsForDisapprovedReopenPODocuments(PurchaseOrderDocument newPO) {
        updateCurrentDocumentForNoPendingAction(newPO, PurapConstants.PurchaseOrderStatuses.APPDOC_DISAPPROVED_CHANGE, PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#setCurrentAndPendingIndicatorsForCancelledRemoveHoldPODocuments(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void setCurrentAndPendingIndicatorsForCancelledRemoveHoldPODocuments(PurchaseOrderDocument newPO) {
        updateCurrentDocumentForNoPendingAction(newPO, PurapConstants.PurchaseOrderStatuses.APPDOC_CANCELLED_CHANGE, PurapConstants.PurchaseOrderStatuses.APPDOC_PAYMENT_HOLD);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#setCurrentAndPendingIndicatorsForDisapprovedRemoveHoldPODocuments(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public void setCurrentAndPendingIndicatorsForDisapprovedRemoveHoldPODocuments(PurchaseOrderDocument newPO) {
        updateCurrentDocumentForNoPendingAction(newPO, PurapConstants.PurchaseOrderStatuses.APPDOC_DISAPPROVED_CHANGE, PurapConstants.PurchaseOrderStatuses.APPDOC_PAYMENT_HOLD);
    }

    /**
     * Update the statuses of both the old purchase order and the new purchase orders, then save the old and the new purchase
     * orders.
     *
     * @param newPO The new change purchase order document (e.g. the PurchaseOrderAmendmentDocument that was resulted from the user
     *        clicking on the amend button).
     * @param newPOStatus The status to be set on the new change purchase order document.
     * @param oldPOStatus The status to be set on the existing (old) purchase order document.
     */
    protected void updateCurrentDocumentForNoPendingAction(PurchaseOrderDocument newPO, String newPOStatus, String oldPOStatus) {
        // Get the "current PO" that's in the database, i.e. the PO row that contains current indicator = Y
        PurchaseOrderDocument oldPO = getCurrentPurchaseOrder(newPO.getPurapDocumentIdentifier());
        // Set the Pending indicator for the oldPO to N
        oldPO.setPendingActionIndicator(false);
        try {
            oldPO.updateAndSaveAppDocStatus(oldPOStatus);
            newPO.updateAndSaveAppDocStatus(newPOStatus);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Error saving routing data while saving document", e);
        }

        savePurchaseOrderData(oldPO);
        saveDocumentNoValidationUsingClearMessageMap(newPO);
    }

    @Override
    public List<PurchaseOrderQuoteStatus> getPurchaseOrderQuoteStatusCodes() {
        List<PurchaseOrderQuoteStatus> poQuoteStatuses = new ArrayList<PurchaseOrderQuoteStatus>();
        poQuoteStatuses = (List<PurchaseOrderQuoteStatus>) businessObjectService.findAll(PurchaseOrderQuoteStatus.class);
        return poQuoteStatuses;
    }

    @Override
    public void setReceivingRequiredIndicatorForPurchaseOrder(PurchaseOrderDocument po) {
        ThresholdHelper thresholdHelper = new ThresholdHelper(po);
        boolean result = thresholdHelper.isReceivingDocumentRequired();
        if (result) {
            ThresholdSummary thresholdSummary = thresholdHelper.getThresholdSummary();
            ReceivingThreshold receivingThreshold = thresholdHelper.getReceivingThreshold();
            po.setReceivingDocumentRequiredIndicator(true);

            String notetxt = "Receiving is set to be required because the threshold summary with a total amount of " + thresholdSummary.getTotalAmount();
            notetxt += " exceeds the receiving threshold of " + receivingThreshold.getThresholdAmount();
            notetxt += " with respect to the threshold criteria ";

            if (thresholdSummary.getThresholdCriteria() == ThresholdHelper.CHART){
                notetxt += " Chart " + receivingThreshold.getChartOfAccountsCode();
            } else if (thresholdSummary.getThresholdCriteria() == ThresholdHelper.CHART_AND_ACCOUNTTYPE){
                notetxt += " Chart " + receivingThreshold.getChartOfAccountsCode();
                notetxt += " - Account Type " + receivingThreshold.getAccountTypeCode();
            } else if (thresholdSummary.getThresholdCriteria() == ThresholdHelper.CHART_AND_SUBFUND){
                notetxt += " Chart " + receivingThreshold.getChartOfAccountsCode();
                notetxt += " - Sub-Fund " + receivingThreshold.getSubFundGroupCode();
            } else if (thresholdSummary.getThresholdCriteria() == ThresholdHelper.CHART_AND_COMMODITYCODE){
                notetxt += " Chart " + receivingThreshold.getChartOfAccountsCode();
                notetxt += " - Commodity Code " + receivingThreshold.getPurchasingCommodityCode();
            } else if (thresholdSummary.getThresholdCriteria() == ThresholdHelper.CHART_AND_OBJECTCODE){
                notetxt += " Chart " + receivingThreshold.getChartOfAccountsCode();
                notetxt += " - Object code " + receivingThreshold.getFinancialObjectCode();
            } else if (thresholdSummary.getThresholdCriteria() == ThresholdHelper.CHART_AND_ORGANIZATIONCODE){
                notetxt += " Chart " + receivingThreshold.getChartOfAccountsCode();
                notetxt += " - Organization " + receivingThreshold.getOrganizationCode();
            } else if (thresholdSummary.getThresholdCriteria() == ThresholdHelper.CHART_AND_VENDOR){
                notetxt += " Chart " + receivingThreshold.getChartOfAccountsCode();
                notetxt += " - Vendor " + receivingThreshold.getVendorNumber();
            }

            try {
                Note note = documentService.createNoteFromDocument(po, notetxt);
//                documentService.addNoteToDocument(po, note);
                noteService.save(note);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#hasNewUnorderedItem(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    @Override
    public boolean hasNewUnorderedItem(PurchaseOrderDocument po){

        boolean itemAdded = false;

        for(PurchaseOrderItem poItem: (List<PurchaseOrderItem>)po.getItems()){
            //only check, active, above the line, unordered items
            if (poItem.isItemActiveIndicator() && poItem.getItemType().isLineItemIndicator() && PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE.equals(poItem.getItemTypeCode()) ) {

                //if the item identifier is null its new, or if the item doesn't exist on the current purchase order it's new
                if( poItem.getItemIdentifier() == null || !purchaseOrderDao.itemExistsOnPurchaseOrder(poItem.getItemLineNumber(), purchaseOrderDao.getDocumentNumberForCurrentPurchaseOrder(po.getPurapDocumentIdentifier()) )){
                    itemAdded = true;
                    break;
                }
            }
        }

        return itemAdded;
    }

    @Override
    public boolean isNewUnorderedItem(PurchaseOrderItem poItem){

        boolean itemAdded = false;

        //only check, active, above the line, unordered items
        if (poItem.isItemActiveIndicator() && poItem.getItemType().isLineItemIndicator() && PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE.equals(poItem.getItemTypeCode()) ) {

            //if the item identifier is null its new, or if the item doesn't exist on the current purchase order it's new
            if( poItem.getItemIdentifier() == null || !purchaseOrderDao.itemExistsOnPurchaseOrder(poItem.getItemLineNumber(), purchaseOrderDao.getDocumentNumberForCurrentPurchaseOrder(poItem.getPurchaseOrder().getPurapDocumentIdentifier()) )){
                itemAdded = true;
            }
        }

        return itemAdded;
    }

    @Override
    public boolean isNewItemForAmendment(PurchaseOrderItem poItem) {

        boolean itemAdded = false;

        // only check, active, above the line, unordered items
        if (poItem.isItemActiveIndicator() && poItem.getItemType().isLineItemIndicator()) {

            // if the item identifier is null its new, or if the item doesn't exist on the current purchase order it's new
            Integer docId = poItem.getPurapDocumentIdentifier();
            if (docId == null) {
                docId = poItem.getPurchaseOrder().getPurapDocumentIdentifier();
            }
            if (poItem.getItemIdentifier() == null || !purchaseOrderDao.itemExistsOnPurchaseOrder(poItem.getItemLineNumber(), purchaseOrderDao.getDocumentNumberForCurrentPurchaseOrder(docId))) {
                itemAdded = true;
            }
        }

        return itemAdded;
    }

    /**
     * Sends an FYI to fiscal officers for new unordered items.
     *
     * @param po
     */
    protected void sendFyiForNewUnorderedItems(PurchaseOrderDocument po){

        List<AdHocRoutePerson> fyiList = createFyiFiscalOfficerListForNewUnorderedItems(po);
        String annotation = "Notification of New Unordered Items for Purchase Order" + po.getPurapDocumentIdentifier() + "(document id " + po.getDocumentNumber() + ")";
        String responsibilityNote = "Purchase Order Amendment Routed By User";

        for(AdHocRoutePerson adHocPerson: fyiList){
            try{
                po.appSpecificRouteDocumentToUser(
                        po.getDocumentHeader().getWorkflowDocument(),
                        adHocPerson.getPerson().getPrincipalId(),
                        annotation,
                        responsibilityNote);
            } catch (WorkflowException e) {
                throw new RuntimeException("Error routing fyi for document with id " + po.getDocumentNumber(), e);
            }

        }
    }

    /**
     * Creates a list of fiscal officers for new unordered items added to a purchase order.
     *
     * @param po
     * @return
     */
    protected List<AdHocRoutePerson> createFyiFiscalOfficerListForNewUnorderedItems(PurchaseOrderDocument po){

        List<AdHocRoutePerson> adHocRoutePersons = new ArrayList<AdHocRoutePerson>();
        Map fiscalOfficers = new HashMap();
        AdHocRoutePerson adHocRoutePerson = null;

        for(PurchaseOrderItem poItem: (List<PurchaseOrderItem>)po.getItems()){
            //only check, active, above the line, unordered items
            if (poItem.isItemActiveIndicator() && poItem.getItemType().isLineItemIndicator() && PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE.equals(poItem.getItemTypeCode()) ) {

                //if the item identifier is null its new, or if the item doesn't exist on the current purchase order it's new
                if( poItem.getItemIdentifier() == null || !purchaseOrderDao.itemExistsOnPurchaseOrder(poItem.getItemLineNumber(), purchaseOrderDao.getDocumentNumberForCurrentPurchaseOrder(po.getPurapDocumentIdentifier()) )){

                    // loop through accounts and pull off fiscal officer
                    for(PurApAccountingLine account : poItem.getSourceAccountingLines()){

                        //check for dupes of fiscal officer
                        if( fiscalOfficers.containsKey(account.getAccount().getAccountFiscalOfficerUser().getPrincipalName()) == false ){

                            //add fiscal officer to list
                            fiscalOfficers.put(account.getAccount().getAccountFiscalOfficerUser().getPrincipalName(), account.getAccount().getAccountFiscalOfficerUser().getPrincipalName());

                            //create AdHocRoutePerson object and add to list
                            adHocRoutePerson = new AdHocRoutePerson();
                            adHocRoutePerson.setId(account.getAccount().getAccountFiscalOfficerUser().getPrincipalName());
                            adHocRoutePerson.setActionRequested(KFSConstants.WORKFLOW_FYI_REQUEST);
                            adHocRoutePersons.add(adHocRoutePerson);
                        }
                    }
                }
            }
        }

        return adHocRoutePersons;
    }


    /**
     * Sends an FYI to fiscal officers for general ledger entries created for amend purchase order
     *
     * @param po
     */
    @Override
    public void sendFyiForGLEntries(PurchaseOrderDocument po){

        List<AdHocRoutePerson> fyiList = createFyiFiscalOfficerListForAmendGlEntries(po);
        String annotation = "Amendment to Purchase Order " + po.getPurapDocumentIdentifier() + "( Document id " + po.getDocumentNumber() + ")" +
                              " resulted in the generation of Pending General Ledger Entries.";
        String responsibilityNote = "Purchase Order Amendment Routed By User";

        for(AdHocRoutePerson adHocPerson: fyiList){
            try{
                po.appSpecificRouteDocumentToUser(
                        po.getDocumentHeader().getWorkflowDocument(),
                        adHocPerson.getPerson().getPrincipalId(),
                        annotation,
                        responsibilityNote);
            }catch (WorkflowException e) {
                throw new RuntimeException("Error routing fyi for document with id " + po.getDocumentNumber(), e);
            }

        }
    }

    /**
     * Creates a list of fiscal officers for amend genera
     *
     * @param po
     * @return
     */
    protected List<AdHocRoutePerson> createFyiFiscalOfficerListForAmendGlEntries(PurchaseOrderDocument po){

        List<AdHocRoutePerson> adHocRoutePersons = new ArrayList<AdHocRoutePerson>();
        Map fiscalOfficers = new HashMap();
        AdHocRoutePerson adHocRoutePerson = null;

        for(SourceAccountingLine account: po.getGlOnlySourceAccountingLines()){
                     // loop through accounts and pull off fiscal officer
                //    for(PurApAccountingLine account : poItem.getSourceAccountingLines()){
                        //check for dupes of fiscal officer
                    Account acct = SpringContext.getBean(AccountService.class).getByPrimaryId(account.getChartOfAccountsCode(), account.getAccountNumber());
                    String principalName  = acct.getAccountFiscalOfficerUser().getPrincipalName();
                    //String principalName = account.getAccount().getAccountFiscalOfficerUser().getPrincipalName();
                        if( fiscalOfficers.containsKey(principalName) == false ){
                            //add fiscal officer to list
                            fiscalOfficers.put(principalName, principalName);
                            //create AdHocRoutePerson object and add to list
                            adHocRoutePerson = new AdHocRoutePerson();
                            adHocRoutePerson.setId(principalName);
                            adHocRoutePerson.setActionRequested(KewApiConstants.ACTION_REQUEST_FYI_REQ);
                            adHocRoutePersons.add(adHocRoutePerson);
                        }
                  //  }
        }

        return adHocRoutePersons;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#categorizeItemsForSplit(java.util.List)
     */
    @Override
    public HashMap<String, List<PurchaseOrderItem>> categorizeItemsForSplit(List<PurchaseOrderItem> items) {
        HashMap<String, List<PurchaseOrderItem>> movingOrNot =  new HashMap<String, List<PurchaseOrderItem>>(3);
        List<PurchaseOrderItem> movingPOItems = new ArrayList<PurchaseOrderItem>();
        List<PurchaseOrderItem> remainingPOItems = new ArrayList<PurchaseOrderItem>();
        List<PurchaseOrderItem> remainingPOLineItems = new ArrayList<PurchaseOrderItem>();
        for (PurchaseOrderItem item : items) {
            if(item.isMovingToSplit()) {
                movingPOItems.add(item);
            }
            else {
                remainingPOItems.add(item);
                if (item.getItemType().isLineItemIndicator()) {
                    remainingPOLineItems.add(item);
                }
            }
        }
        movingOrNot.put(PODocumentsStrings.ITEMS_MOVING_TO_SPLIT, movingPOItems);
        movingOrNot.put(PODocumentsStrings.ITEMS_REMAINING, remainingPOItems);
        movingOrNot.put(PODocumentsStrings.LINE_ITEMS_REMAINING, remainingPOLineItems);
        return movingOrNot;
    }

    /**
     * @see org.kuali.module.purap.service.PurchaseOrderService#populateQuoteWithVendor(java.lang.Integer, java.lang.Integer, java.lang.String)
     */
    @Override
    public PurchaseOrderVendorQuote populateQuoteWithVendor(Integer headerId, Integer detailId, String documentNumber) {
        VendorDetail vendor = vendorService.getVendorDetail(headerId, detailId);
        updateDefaultVendorAddress(vendor);
        PurchaseOrderVendorQuote newPOVendorQuote = populateAddressForPOVendorQuote(vendor, documentNumber);

        //Set the vendorPhoneNumber on the quote to be the first "phone number" type phone
        //found on the list. If there's no "phone number" type found, the quote's
        //vendorPhoneNumber will be blank regardless of any other types of phone found on the list.
        for (VendorPhoneNumber phone : vendor.getVendorPhoneNumbers()) {
            if (VendorConstants.PhoneTypes.PHONE.equals(phone.getVendorPhoneTypeCode())) {
                newPOVendorQuote.setVendorPhoneNumber(phone.getVendorPhoneNumber());
                break;
            }
        }

        return newPOVendorQuote;
    }

    /**
     * Creates the new PurchaseOrderVendorQuote and populate the address fields for it.
     *
     * @param newVendor       The VendorDetail object from which we obtain the values for the address fields.
     * @param documentNumber  The documentNumber of the PurchaseOrderDocument containing the PurchaseOrderVendorQuote.
     * @return
     */
    protected PurchaseOrderVendorQuote populateAddressForPOVendorQuote(VendorDetail newVendor, String documentNumber) {
        PurchaseOrderVendorQuote newPOVendorQuote = new PurchaseOrderVendorQuote();
        newPOVendorQuote.setVendorName(newVendor.getVendorName());
        newPOVendorQuote.setVendorHeaderGeneratedIdentifier(newVendor.getVendorHeaderGeneratedIdentifier());
        newPOVendorQuote.setVendorDetailAssignedIdentifier(newVendor.getVendorDetailAssignedIdentifier());
        newPOVendorQuote.setDocumentNumber(documentNumber);
        boolean foundAddress = false;
        for (VendorAddress address : newVendor.getVendorAddresses()) {
            if (AddressTypes.QUOTE.equals(address.getVendorAddressTypeCode())) {
                newPOVendorQuote.setVendorCityName(address.getVendorCityName());
                newPOVendorQuote.setVendorCountryCode(address.getVendorCountryCode());
                newPOVendorQuote.setVendorLine1Address(address.getVendorLine1Address());
                newPOVendorQuote.setVendorLine2Address(address.getVendorLine2Address());
                newPOVendorQuote.setVendorPostalCode(address.getVendorZipCode());
                newPOVendorQuote.setVendorStateCode(address.getVendorStateCode());
                newPOVendorQuote.setVendorFaxNumber(address.getVendorFaxNumber());
                foundAddress = true;
                break;
            }
        }
        if (!foundAddress) {
            newPOVendorQuote.setVendorCityName(newVendor.getDefaultAddressCity());
            newPOVendorQuote.setVendorCountryCode(newVendor.getDefaultAddressCountryCode());
            newPOVendorQuote.setVendorLine1Address(newVendor.getDefaultAddressLine1());
            newPOVendorQuote.setVendorLine2Address(newVendor.getDefaultAddressLine2());
            newPOVendorQuote.setVendorPostalCode(newVendor.getDefaultAddressPostalCode());
            newPOVendorQuote.setVendorStateCode(newVendor.getDefaultAddressStateCode());
            newPOVendorQuote.setVendorFaxNumber(newVendor.getDefaultFaxNumber());
        }
        return newPOVendorQuote;
    }

    /**
     * Obtains the defaultAddress of the vendor and setting the default address fields on
     * the vendor.
     *
     * @param vendor The VendorDetail object whose default address we'll obtain and set the fields.
     */
    protected void updateDefaultVendorAddress(VendorDetail vendor) {
        VendorAddress defaultAddress = vendorService.getVendorDefaultAddress(vendor.getVendorAddresses(), vendor.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
        if (defaultAddress != null ) {
            if (defaultAddress.getVendorState() != null) {
                vendor.setVendorStateForLookup(defaultAddress.getVendorState().getName());
            }
            vendor.setDefaultAddressLine1(defaultAddress.getVendorLine1Address());
            vendor.setDefaultAddressLine2(defaultAddress.getVendorLine2Address());
            vendor.setDefaultAddressCity(defaultAddress.getVendorCityName());
            vendor.setDefaultAddressPostalCode(defaultAddress.getVendorZipCode());
            vendor.setDefaultAddressStateCode(defaultAddress.getVendorStateCode());
            vendor.setDefaultAddressInternationalProvince(defaultAddress.getVendorAddressInternationalProvinceName());
            vendor.setDefaultAddressCountryCode(defaultAddress.getVendorCountryCode());
            vendor.setDefaultFaxNumber(defaultAddress.getVendorFaxNumber());
        }
    }

    /**
     *
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#processACMReq(org.kuali.kfs.module.purap.document.ContractManagerAssignmentDocument)
     */
    @Override
    public void processACMReq(ContractManagerAssignmentDocument acmDoc) {
        List<ContractManagerAssignmentDetail> acmDetails = acmDoc.getContractManagerAssignmentDetails();
        for (Iterator iter = acmDetails.iterator(); iter.hasNext();) {
            ContractManagerAssignmentDetail detail = (ContractManagerAssignmentDetail) iter.next();

            if (ObjectUtils.isNotNull(detail.getContractManagerCode())) {
                // Get the requisition for this ContractManagerAssignmentDetail.
                RequisitionDocument req = requisitionService.getRequisitionById(detail.getRequisitionIdentifier());

                if ( PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN.equals(req.getApplicationDocumentStatus())) {
                    // only update REQ if code is empty and status is correct
                    try {
                        req.updateAndSaveAppDocStatus(PurapConstants.RequisitionStatuses.APPDOC_CLOSED);
                    }
                    catch (WorkflowException e) {
                        throw new RuntimeException("Error saving routing data while saving document with id " + req.getDocumentNumber(), e);
                    }

                    purapService.saveDocumentNoValidation(req);
                    createPurchaseOrderDocument(req, KFSConstants.SYSTEM_USER, detail.getContractManagerCode());
                }
            }

        }// endfor
    }

    /**
     *
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#autoCloseFullyDisencumberedOrders()
     */
    @Override
    public boolean autoCloseFullyDisencumberedOrders() {
        LOG.debug("autoCloseFullyDisencumberedOrders() started");

        List<AutoClosePurchaseOrderView> autoCloseList = purchaseOrderDao.getAllOpenPurchaseOrders(getExcludedVendorChoiceCodes());

        //we need to eliminate the AutoClosePurchaseOrderView whose workflowdocument status is not OPEN..
        //KFSMI-7533
        List<AutoClosePurchaseOrderView> purchaseOrderAutoCloseList = filterDocumentsForAppDocStatusOpen(autoCloseList);

        for (AutoClosePurchaseOrderView poAutoClose : purchaseOrderAutoCloseList) {
            if ((poAutoClose.getTotalAmount() != null) && ((KualiDecimal.ZERO.compareTo(poAutoClose.getTotalAmount())) != 0)) {
                LOG.info("autoCloseFullyDisencumberedOrders() PO ID " + poAutoClose.getPurapDocumentIdentifier() + " with total " + poAutoClose.getTotalAmount().doubleValue() + " will be closed");
                String newStatus = PurapConstants.PurchaseOrderStatuses.APPDOC_PENDING_CLOSE;
                String annotation = "This PO was automatically closed in batch.";
                String documentType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
                PurchaseOrderDocument document = getPurchaseOrderByDocumentNumber(poAutoClose.getDocumentNumber());
                createNoteForAutoCloseOrders(document, annotation);
                createAndRoutePotentialChangeDocument(poAutoClose.getDocumentNumber(), documentType, annotation, null, newStatus);

            }
        }

        LOG.debug("autoCloseFullyDisencumberedOrders() ended");

        return true;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#autoCloseRecurringOrders()
     */
    @Override
    public boolean autoCloseRecurringOrders() {
        LOG.debug("autoCloseRecurringOrders() started");
        boolean shouldSendEmail = true;
        MailMessage message = new MailMessage();
        String parameterEmail = parameterService.getParameterValueAsString(AutoCloseRecurringOrdersStep.class, PurapParameterConstants.AUTO_CLOSE_RECURRING_PO_TO_EMAIL_ADDRESSES);

        if (StringUtils.isEmpty(parameterEmail)) {
            // Don't stop the show if the email address is wrong, log it and continue.
            LOG.error("autoCloseRecurringOrders(): parameterEmail is missing, we'll not send out any emails for this job.");
            shouldSendEmail = false;
        }
        if (shouldSendEmail) {
            message = setMessageAddressesAndSubject(message, parameterEmail);
        }
        StringBuffer emailBody = new StringBuffer();
        // There should always be a "AUTO_CLOSE_RECURRING_ORDER_DT"
        // row in the table, this method sets it to "mm/dd/yyyy" after processing.
        String recurringOrderDateString = parameterService.getParameterValueAsString(AutoCloseRecurringOrdersStep.class, PurapParameterConstants.AUTO_CLOSE_RECURRING_PO_DATE);
        boolean validDate = true;
        java.util.Date recurringOrderDate = null;
        try {
            recurringOrderDate = dateTimeService.convertToDate(recurringOrderDateString);
        }
        catch (ParseException pe) {
            validDate = false;
        }
        if (StringUtils.isEmpty(recurringOrderDateString) || recurringOrderDateString.equalsIgnoreCase("mm/dd/yyyy") || (!validDate)) {
            if (recurringOrderDateString.equalsIgnoreCase("mm/dd/yyyy")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("autoCloseRecurringOrders(): mm/dd/yyyy " + "was found in the Application Settings table. No orders will be closed, method will end.");
                }
                if (shouldSendEmail) {
                    emailBody.append("The AUTO_CLOSE_RECURRING_ORDER_DT found in the Application Settings table " + "was mm/dd/yyyy. No recurring PO's were closed.");
                }
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("autoCloseRecurringOrders(): An invalid autoCloseRecurringOrdersDate " + "was found in the Application Settings table: " + recurringOrderDateString + ". Method will end.");
                }
                if (shouldSendEmail) {
                    emailBody.append("An invalid AUTO_CLOSE_RECURRING_ORDER_DT was found in the Application Settings table: " + recurringOrderDateString + ". No recurring PO's were closed.");
                }
            }
            if (shouldSendEmail) {
                sendMessage(message, emailBody.toString());
            }
            LOG.info("autoCloseRecurringOrders() ended");

            return false;
        }
        LOG.info("autoCloseRecurringOrders() The autoCloseRecurringOrdersDate found in the Application Settings table was " + recurringOrderDateString);
        if (shouldSendEmail) {
            emailBody.append("The autoCloseRecurringOrdersDate found in the Application Settings table was " + recurringOrderDateString + ".");
        }
        Calendar appSettingsDate = dateTimeService.getCalendar(recurringOrderDate);
        Timestamp appSettingsDay = new Timestamp(appSettingsDate.getTime().getTime());

        Calendar todayMinusThreeMonths = getTodayMinusThreeMonths();
        Timestamp threeMonthsAgo = new Timestamp(todayMinusThreeMonths.getTime().getTime());

        if (appSettingsDate.after(todayMinusThreeMonths)) {
            LOG.info("autoCloseRecurringOrders() The appSettingsDate: " + appSettingsDay + " is after todayMinusThreeMonths: " + threeMonthsAgo + ". The program will end.");
            if (shouldSendEmail) {
                emailBody.append("\n\nThe autoCloseRecurringOrdersDate: " + appSettingsDay + " is after todayMinusThreeMonths: " + threeMonthsAgo + ". The program will end.");
                sendMessage(message, emailBody.toString());
            }
            LOG.info("autoCloseRecurringOrders() ended");

            return false;
        }

        List<AutoClosePurchaseOrderView> closeList = purchaseOrderDao.getAutoCloseRecurringPurchaseOrders(getExcludedVendorChoiceCodes());

        //we need to eliminate the AutoClosePurchaseOrderView whose workflowdocument status is not OPEN..
        //KFSMI-7533
        List<AutoClosePurchaseOrderView> purchaseOrderAutoCloseList = filterDocumentsForAppDocStatusOpen(closeList);

        LOG.info("autoCloseRecurringOrders(): " + purchaseOrderAutoCloseList.size() + " PO's were returned for processing.");
        int counter = 0;
        for (AutoClosePurchaseOrderView poAutoClose : purchaseOrderAutoCloseList) {
            LOG.info("autoCloseRecurringOrders(): Testing PO ID " + poAutoClose.getPurapDocumentIdentifier() + ". recurringPaymentEndDate: " + poAutoClose.getRecurringPaymentEndDate());
            if (poAutoClose.getRecurringPaymentEndDate().before(threeMonthsAgo)) {
                String newStatus = PurapConstants.PurchaseOrderStatuses.APPDOC_PENDING_CLOSE;
                String annotation = "This recurring PO was automatically closed in batch.";
                String documentType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
                PurchaseOrderDocument document = getPurchaseOrderByDocumentNumber(poAutoClose.getDocumentNumber());
                boolean rulePassed = kualiRuleService.applyRules(new AttributedRouteDocumentEvent("", document));

                boolean success = true;
                if (success) {
                    ++counter;
                    if (counter == 1) {
                        emailBody.append("\n\nThe following recurring Purchase Orders will be closed by auto close recurring batch job \n");
                    }
                    LOG.info("autoCloseRecurringOrders() PO ID " + poAutoClose.getPurapDocumentIdentifier() + " will be closed.");
                    createNoteForAutoCloseOrders(document, annotation);
                    createAndRoutePotentialChangeDocument(poAutoClose.getDocumentNumber(), documentType, annotation, null, newStatus);
                    if (shouldSendEmail) {
                        emailBody.append("\n\n" + counter + " PO ID: " + poAutoClose.getPurapDocumentIdentifier() + ", End Date: " + poAutoClose.getRecurringPaymentEndDate() + ", Status: " + poAutoClose.getApplicationDocumentStatus() + ", VendorChoice: " + poAutoClose.getVendorChoiceCode() + ", RecurringPaymentType: " + poAutoClose.getRecurringPaymentTypeCode());
                    }
                }
                else {
                    //If it was unsuccessful, we have to clear the error map in the GlobalVariables so that the previous
                    //error would not still be lingering around and the next PO in the list can be validated.
                    GlobalVariables.getMessageMap().clearErrorMessages();
                }
            }
        }
        if (counter == 0) {
            LOG.info("\n\nNo recurring PO's fit the conditions for closing.");
            if (shouldSendEmail) {
                emailBody.append("\n\nNo recurring PO's fit the conditions for closing.");
            }
        }
        if (shouldSendEmail) {
            sendMessage(message, emailBody.toString());
        }
        resetAutoCloseRecurringOrderDateParameter();
        LOG.debug("autoCloseRecurringOrders() ended");

        return true;
    }

    /**
     * Filter out the auto close purchase order view documents for the appDocStatus with status open
     * For each document in the list, check if there is workflowdocument whose appdocstatus is open
     * add add to the return list.
     * @param List<AutoClosePurchaseOrderView>
     * @param appDocStatus
     * @return filteredAutoClosePOView filtered auto close po view documents where appdocstatus is open
     */
    protected List<AutoClosePurchaseOrderView> filterDocumentsForAppDocStatusOpen(List<AutoClosePurchaseOrderView> autoClosePurchaseOrderViews) {
        List<AutoClosePurchaseOrderView> filteredAutoClosePOView = new ArrayList<AutoClosePurchaseOrderView>();

        for (AutoClosePurchaseOrderView autoClosePurchaseOrderView : autoClosePurchaseOrderViews) {
            Document document = findDocument(autoClosePurchaseOrderView.getDocumentNumber());

            if (document != null) {
                if (PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN.equalsIgnoreCase(
                        document.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus())){
                    //found the matched Awaiting Contract Manager Assignment status, retrieve the routeHeaderId and add to the list
                    filteredAutoClosePOView.add(autoClosePurchaseOrderView);
                }
            }
        }

        return filteredAutoClosePOView;
    }

    /**
     * This method finds the document for the given document header id
     * @param documentHeaderId
     * @return document The document in the workflow that matches the document header id.
     */
    protected Document findDocument(String documentHeaderId) {
        Document document = null;

        try {
            document = documentService.getByDocumentHeaderId(documentHeaderId);
        }
        catch (WorkflowException ex) {
            LOG.error("Exception encountered on finding the document: " + documentHeaderId, ex );
        } catch ( UnknownDocumentTypeException ex ) {
            // don't blow up just because a document type is not installed (but don't return it either)
            LOG.error("Exception encountered on finding the document: " + documentHeaderId, ex );
        }

        return document;
    }


    /**
     * Creates and returns a Calendar object of today minus three months.
     *
     * @return Calendar object of today minus three months.
     */
    protected Calendar getTodayMinusThreeMonths() {
        Calendar todayMinusThreeMonths = Calendar.getInstance(); // Set to today.
        todayMinusThreeMonths.add(Calendar.MONTH, -3); // Back up 3 months.
        todayMinusThreeMonths.set(Calendar.HOUR, 12);
        todayMinusThreeMonths.set(Calendar.MINUTE, 0);
        todayMinusThreeMonths.set(Calendar.SECOND, 0);
        todayMinusThreeMonths.set(Calendar.MILLISECOND, 0);
        todayMinusThreeMonths.set(Calendar.AM_PM, Calendar.AM);
        return todayMinusThreeMonths;
    }

    /**
     * Sets the to addresses, from address and the subject of the email.
     *
     * @param message         The MailMessage object of the email to be sent.
     * @param parameterEmail  The String of email addresses with delimiters of ";"
     *                        obtained from the system parameter.
     * @return                The MailMessage object after the to addresses, from
     *                        address and the subject have been set.
     */
    protected MailMessage setMessageAddressesAndSubject(MailMessage message, String parameterEmail) {
        String toAddressList[] = parameterEmail.split(";");

        if (toAddressList.length > 0) {
            for (int i = 0; i < toAddressList.length; i++) {
                if (toAddressList[i] != null) {
                    message.addToAddress(toAddressList[i].trim());
                }
            }
        }

        message.setFromAddress(toAddressList[0]);
            message.setSubject("Auto Close Recurring Purchase Orders");
        return message;
    }

    /**
     * Sends the email by calling the sendMessage method in mailService and log error if exception occurs
     * during the attempt to send the message.
     *
     * @param message    The MailMessage object containing information to be sent.
     * @param emailBody  The String containing the body of the email to be sent.
     */
    protected void sendMessage(MailMessage message, String emailBody) {
        message.setMessage(emailBody);
        try {
            mailService.sendMessage(message);
        }
        catch (Exception e) {
            // Don't stop the show if the email has problem, log it and continue.
            LOG.error("autoCloseRecurringOrders(): email problem. Message not sent.", e);
        }
    }

    /**
     * Resets the AUTO_CLOSE_RECURRING_ORDER_DT system parameter to "mm/dd/yyyy".
     *
     */
    protected void resetAutoCloseRecurringOrderDateParameter() {
        Parameter autoCloseRecurringPODate = parameterService.getParameter(AutoCloseRecurringOrdersStep.class, PurapParameterConstants.AUTO_CLOSE_RECURRING_PO_DATE);
        if ( autoCloseRecurringPODate != null ) {
            Parameter.Builder updatedParameter = Parameter.Builder.create(autoCloseRecurringPODate);
            updatedParameter.setValue("mm/dd/yyyy");
            parameterService.updateParameter(updatedParameter.build());
        }
    }

    /**
     * Gets a List of excluded vendor choice codes from PurapConstants.
     *
     * @return a List of excluded vendor choice codes
     */
    protected List<String> getExcludedVendorChoiceCodes() {
        List<String> excludedVendorChoiceCodes = new ArrayList<String>();
        for (int i = 0; i < PurapConstants.AUTO_CLOSE_EXCLUSION_VNDR_CHOICE_CODES.length; i++) {
            String excludedCode = PurapConstants.AUTO_CLOSE_EXCLUSION_VNDR_CHOICE_CODES[i];
            excludedVendorChoiceCodes.add(excludedCode);
        }
        return excludedVendorChoiceCodes;
    }

    /**
     * Creates and add a note to the purchase order document using the annotation String
     * in the input parameter. This method is used by the autoCloseRecurringOrders() and
     * autoCloseFullyDisencumberedOrders to add a note to the purchase order to
     * indicate that the purchase order was closed by the batch job.
     *
     * @param purchaseOrderDocument The purchase order document that is being closed by the batch job.
     * @param annotation            The string to appear on the note to be attached to the purchase order.
     */
    protected void createNoteForAutoCloseOrders(PurchaseOrderDocument purchaseOrderDocument, String annotation) {
        try {
            Note noteObj = documentService.createNoteFromDocument(purchaseOrderDocument, annotation);
//            documentService.addNoteToDocument(purchaseOrderDocument, noteObj);
            noteService.save(noteObj);
        }
        catch(Exception e){
            String errorMessage = "Error creating and saving close note for purchase order with document service";
            LOG.error("createNoteForAutoCloseRecurringOrders " + errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#retrieveCapitalAssetItemsForIndividual(java.lang.Integer)
     */
    @Override
    public List<PurchasingCapitalAssetItem> retrieveCapitalAssetItemsForIndividual(Integer poId) {
        PurchaseOrderDocument po = getCurrentPurchaseOrder(poId);
        if (ObjectUtils.isNotNull(po)) {
            return po.getPurchasingCapitalAssetItems();
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#retrieveCapitalAssetSystemForOneSystem(java.lang.Integer)
     */
    @Override
    public CapitalAssetSystem retrieveCapitalAssetSystemForOneSystem(Integer poId) {
        PurchaseOrderDocument po = getCurrentPurchaseOrder(poId);
        if (ObjectUtils.isNotNull(po)) {
            List<CapitalAssetSystem> systems = po.getPurchasingCapitalAssetSystems();
            if (ObjectUtils.isNotNull(systems)) {
                //for one system, there should only ever be one system
                return systems.get(0);
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#retrieveCapitalAssetSystemsForMultipleSystem(java.lang.Integer)
     */
    @Override
    public List<CapitalAssetSystem> retrieveCapitalAssetSystemsForMultipleSystem(Integer poId) {
        PurchaseOrderDocument po = getCurrentPurchaseOrder(poId);
        if (ObjectUtils.isNotNull(po)) {
            return po.getPurchasingCapitalAssetSystems();
        }
        return null;
    }

    /**
     * This method fixes the item references in this document
     */
    protected void fixItemReferences(PurchaseOrderDocument po) {
        //fix item and account references in case this is a new doc (since they will be lost)
        for (PurApItem item : (List<PurApItem>)po.getItems()) {
            item.setPurapDocument(po);
            item.fixAccountReferences();
        }
    }

    @Override
    public List getPendingPurchaseOrderFaxes() {
        List<PurchaseOrderDocument> purchaseOrderList = purchaseOrderDao.getPendingPurchaseOrdersForFaxing();
        return filterPurchaseOrderDocumentByAppDocStatus(purchaseOrderList,
                PurapConstants.PurchaseOrderStatuses.APPDOC_PENDING_FAX);
    }

    /**
     * Wrapper class to the filterPaymentRequestByAppDocStatus
     *
     * This class first extract the payment request document numbers from the Payment Request Collections,
     * then perform the filterPaymentRequestByAppDocStatus function.  Base on the filtered payment request
     * doc number, reconstruct the filtered Payment Request Collection
     *
     * @param paymentRequestDocuments
     * @param appDocStatus
     * @return
     */
    protected List<PurchaseOrderDocument> filterPurchaseOrderDocumentByAppDocStatus(Collection<PurchaseOrderDocument> purchaseOrderDocuments, String... appDocStatus) {
        List<String> purchaseOrderDocNumbers = new ArrayList<String>();
        for (PurchaseOrderDocument purchaseOrder : purchaseOrderDocuments){
            purchaseOrderDocNumbers.add(purchaseOrder.getDocumentNumber());
        }

        List<String> filteredPurchaseOrderDocNumbers = filterPurchaseOrderDocumentNumbersByAppDocStatus(purchaseOrderDocNumbers, appDocStatus);

        List<PurchaseOrderDocument> filteredPaymentRequestDocuments = new ArrayList<PurchaseOrderDocument>();
        //add to filtered collection if it is in the filtered payment request doc number list
        for (PurchaseOrderDocument po : purchaseOrderDocuments){
            if (filteredPurchaseOrderDocNumbers.contains(po.getDocumentNumber())){
                filteredPaymentRequestDocuments.add(po);
            }
        }
        return filteredPaymentRequestDocuments;
    }

    /**
     *  Since PaymentRequest does not have the app doc status, perform an additional lookup
     *  through doc search by using list of PaymentRequest Doc numbers.  Query appDocStatus
     *  from workflow document and filter against the provided status
     *
     *  DocumentSearch allows for multiple docNumber lookup by docId|docId|docId conversion
     *
     * @param lookupDocNumbers
     * @param appDocStatus
     * @return List<String> purchaseOrderDocumentNumbers
     */
    protected List<String> filterPurchaseOrderDocumentNumbersByAppDocStatus(List<String> lookupDocNumbers, String... appDocStatus) {
        boolean valid = false;

        final String DOC_NUM_DELIM = "|";
        StrBuilder routerHeaderIdBuilder = new StrBuilder().appendWithSeparators(lookupDocNumbers, DOC_NUM_DELIM);

        List<String> purchaseOrderDocNumbers = new ArrayList<String>();

        DocumentSearchCriteria.Builder documentSearchCriteriaDTO = DocumentSearchCriteria.Builder.create();
        documentSearchCriteriaDTO.setDocumentId(routerHeaderIdBuilder.toString());
        documentSearchCriteriaDTO.setDocumentTypeName(PurapConstants.PurapDocTypeCodes.PO_DOCUMENT);

        DocumentSearchResults poDocumentsList = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(
             GlobalVariables.getUserSession().getPrincipalId(), documentSearchCriteriaDTO.build());

        for (DocumentSearchResult poDocument : poDocumentsList.getSearchResults()) {
            ///use the appDocStatus from the KeyValueDTO result to look up custom status
            if (Arrays.asList(appDocStatus).contains(poDocument.getDocument().getApplicationDocumentStatus())){
                //found the matching status, retrieve the routeHeaderId and add to the list
                purchaseOrderDocNumbers.add(poDocument.getDocument().getDocumentId());
            }
        }

        return purchaseOrderDocNumbers;
    }

    @Override
    public String getPurchaseOrderAppDocStatus(Integer poId){
        //TODO: This could be kind of expensive for one field
        PurchaseOrderDocument po = getCurrentPurchaseOrder(poId);
        if (ObjectUtils.isNotNull(po)) {
            return po.getApplicationDocumentStatus();
        }

        return null;
    }

    /**
     * helper method to take the po and save it using businessObjectService so that
     * only the po related data is saved since most often we are only updating the flags on
     * the document. It will then reindex the document.
     *
     * @param po
     */
    protected void savePurchaseOrderData(PurchaseOrderDocument po) {
        //saving old PO using the business object service because the documentService saveDocument
        //will try to save the notes again and will cause ojb lock exception.
        //since only values that is changed on PO is pendingActionIndicator, save on businessObjectService is used
        //KFSMI-9741

        businessObjectService.save(po);

        //reindex the document so that the app doc status gets updated in the results for the PO lookups.
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
        documentAttributeIndexingQueue.indexDocument(po.getDocumentNumber());

    }
    /**
     * @return Returns the personService.
     */
    protected PersonService getPersonService() {
        if(personService==null) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setB2bPurchaseOrderService(B2BPurchaseOrderService purchaseOrderService) {
        this.b2bPurchaseOrderService = purchaseOrderService;
    }

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

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
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

    public void setPurapWorkflowIntegrationService(PurApWorkflowIntegrationService purapWorkflowIntegrationService) {
        this.purapWorkflowIntegrationService = purapWorkflowIntegrationService;
    }

    public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
}
