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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.DocumentSystemSaveEvent;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapWorkflowConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.AutoApproveExclude;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.NegativePaymentRequestApprovalLimit;
import org.kuali.module.purap.bo.PaymentRequestAccount;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.exceptions.PurError;
import org.kuali.module.purap.rule.event.ContinuePurapEvent;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurApWorkflowIntegrationService;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.module.purap.util.PurApItemUtils;
import org.kuali.module.purap.util.VendorGroupingHelper;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.PaymentTermType;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class provides services of use to a payment request document
 */
@Transactional
public class PaymentRequestServiceImpl implements PaymentRequestService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestServiceImpl.class);

    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private NoteService noteService;
    private PurapService purapService;
    private PaymentRequestDao paymentRequestDao;
    private ParameterService parameterService;
    private KualiConfigurationService configurationService;
    private NegativePaymentRequestApprovalLimitService negativePaymentRequestApprovalLimitService;
    private PurapAccountingService purapAccountingService;
    private BusinessObjectService businessObjectService;
    private PurApWorkflowIntegrationService purapWorkflowIntegrationService;
    private WorkflowDocumentService workflowDocumentService;
    private AccountsPayableService accountsPayableService;
    private VendorService vendorService;
    private DataDictionaryService dataDictionaryService;
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
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

    public void setPaymentRequestDao(PaymentRequestDao paymentRequestDao) {
        this.paymentRequestDao = paymentRequestDao;
    }

    public void setNegativePaymentRequestApprovalLimitService(NegativePaymentRequestApprovalLimitService negativePaymentRequestApprovalLimitService) {
        this.negativePaymentRequestApprovalLimitService = negativePaymentRequestApprovalLimitService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setPurapWorkflowIntegrationService(PurApWorkflowIntegrationService purapWorkflowIntegrationService) {
        this.purapWorkflowIntegrationService = purapWorkflowIntegrationService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService){
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setAccountsPayableService(AccountsPayableService accountsPayableService) {
        this.accountsPayableService = accountsPayableService;
    }
    
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * @see org.kuali.module.purap.server.PaymentRequestService.getPaymentRequestsToExtractByCM()
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtractByCM(String campusCode, CreditMemoDocument cmd) {
        LOG.debug("getPaymentRequestsByCM() started");

        return paymentRequestDao.getPaymentRequestsToExtract(campusCode, null, null, cmd.getVendorHeaderGeneratedIdentifier(), cmd.getVendorDetailAssignedIdentifier());
    }

    
    
    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getPaymentRequestsToExtractByVendor(java.lang.String, org.kuali.module.purap.util.VendorGroupingHelper, java.sql.Date)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtractByVendor(String campusCode, VendorGroupingHelper vendor, Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsByVendor() started");

        return paymentRequestDao.getPaymentRequestsToExtractForVendor(campusCode, vendor, onOrBeforePaymentRequestPayDate);
    }

    /**
     * @see org.kuali.module.purap.server.PaymentRequestService.getPaymentRequestsToExtract(Date)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract(Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsToExtract() started");

        return paymentRequestDao.getPaymentRequestsToExtract(false, null, onOrBeforePaymentRequestPayDate);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getPaymentRequestsToExtractSpecialPayments(java.lang.String, java.sql.Date)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtractSpecialPayments(String chartCode, Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsToExtractSpecialPayments() started");

        return paymentRequestDao.getPaymentRequestsToExtract(true, chartCode, onOrBeforePaymentRequestPayDate);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getImmediatePaymentRequestsToExtract(java.lang.String)
     */
    public Iterator<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode) {
        LOG.debug("getImmediatePaymentRequestsToExtract() started");

        return paymentRequestDao.getImmediatePaymentRequestsToExtract(chartCode);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getPaymentRequestToExtractByChart(java.lang.String, java.sql.Date)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestToExtractByChart(String chartCode, Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestToExtractByChart() started");

        return paymentRequestDao.getPaymentRequestsToExtract(false, chartCode, onOrBeforePaymentRequestPayDate);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService.autoApprovePaymentRequests()
     */
    public boolean autoApprovePaymentRequests() {
        boolean hadErrorAtLeastOneError = true;
        // should objects from existing user session be copied over
        List<PaymentRequestDocument> docs = paymentRequestDao.getEligibleForAutoApproval();
        if (docs != null) {
            String samt = parameterService.getParameterValue(PaymentRequestDocument.class, PurapParameterConstants.PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT);
            KualiDecimal defaultMinimumLimit = new KualiDecimal(samt);
            for (PaymentRequestDocument paymentRequestDocument : docs) {
                hadErrorAtLeastOneError |= !autoApprovePaymentRequest(paymentRequestDocument, defaultMinimumLimit);
            }
        }
        return hadErrorAtLeastOneError;
    }

    /**
     * NOTE: in the event of auto-approval failure, this method may throw a RuntimeException, indicating to Spring
     * transactional management that the transaction should be rolled back.
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#autoApprovePaymentRequest(java.lang.String, org.kuali.core.util.KualiDecimal)
     */
    public boolean autoApprovePaymentRequest(String docNumber, KualiDecimal defaultMinimumLimit) {
        PaymentRequestDocument paymentRequestDocument = null;
        try {
            paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docNumber);
            if (paymentRequestDocument.isHoldIndicator() || paymentRequestDocument.isPaymentRequestedCancelIndicator() ||
                    !Arrays.asList(PurapConstants.PaymentRequestStatuses.PREQ_STATUSES_FOR_AUTO_APPROVE).contains(paymentRequestDocument.getStatusCode())) {
                // this condition is based on the conditions that PaymentRequestDaoOjb.getEligibleDocumentNumbersForAutoApproval() uses to query
                // the database.  Rechecking these conditions to ensure that the document is eligible for auto-approval, because we're not running things
                // within the same transaction anymore and changes could have occurred since we called that method that make this document not auto-approvable
                
                // note that this block does not catch all race conditions
                // however, this error condition is not enough to make us return an error code, so just skip the document
                LOG.warn("Payment Request Document " + paymentRequestDocument.getDocumentNumber() + " could not be auto-approved because it has either been placed on hold, " +
                        " requested cancel, or does not have one of the PREQ statuses for auto-approve.");
                return true;
            }
            if (autoApprovePaymentRequest(paymentRequestDocument, defaultMinimumLimit)) {
                LOG.info("Auto-approval for payment request successful.  Doc number: " + docNumber);
                return true;
            }
            else {
                LOG.error("Payment Request Document " + docNumber + " could not be auto-approved.");
                return false;
            }
        }
        catch (WorkflowException we) {
            LOG.error("Exception encountered when retrieving document number " + docNumber + ".", we);
            // throw a runtime exception up so that we can force a rollback
            throw new RuntimeException("Exception encountered when retrieving document number " + docNumber + ".", we);
        }
    }
    
    /**
     * NOTE: in the event of auto-approval failure, this method may throw a RuntimeException, indicating to Spring
     * transactional management that the transaction should be rolled back.
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#autoApprovePaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, org.kuali.core.util.KualiDecimal)
     */
    public boolean autoApprovePaymentRequest(PaymentRequestDocument doc, KualiDecimal defaultMinimumLimit) {
        if (isEligibleForAutoApproval(doc, defaultMinimumLimit)) {
            try {
                purapService.updateStatus(doc, PaymentRequestStatuses.AUTO_APPROVED);
                documentService.blanketApproveDocument(doc, "auto-approving: Total is below threshold.", null);
            }
            catch (WorkflowException we) {
                LOG.error("Exception encountered when approving document number " + doc.getDocumentNumber() + ".", we);
                // throw a runtime exception up so that we can force a rollback
                throw new RuntimeException("Exception encountered when approving document number " + doc.getDocumentNumber() + ".", we);
            }
        }
        return true;
    }

    /**
     * Determines whether or not a payment request document can be automatically approved. FYI - If fiscal reviewers are
     * allowed to save account changes without the full account validation running then this method must call full account
     * validation to make sure auto approver is not blanket approving an invalid document according the the accounts on the items
     * 
     * @param document             The payment request document to be determined whether it can be automatically approved.
     * @param defaultMinimumLimit  The amount to be used as the minimum amount if no limit was found or the default is
     *                             less than the limit.
     * @return                     boolean true if the payment request document is eligible for auto approval.
     */
    private boolean isEligibleForAutoApproval(PaymentRequestDocument document, KualiDecimal defaultMinimumLimit) {
        // check to make sure the payment request isn't scheduled to stop in tax review.
        if (purapWorkflowIntegrationService.willDocumentStopAtGivenFutureRouteNode(document, PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum.VENDOR_TAX_REVIEW)) {
            return false;
        }

        // Change to not auto approve if positive approval required indicator set to Yes
        if (document.isPaymentRequestPositiveApprovalIndicator()){
            return false;
        }
        
        // This minimum will be set to the minimum limit derived from all
        // accounting lines on the document. If no limit is determined, the
        // default will be used.
        KualiDecimal minimumAmount = null;

        // Iterate all source accounting lines on the document, deriving a
        // minimum limit from each according to chart, chart and account, and
        // chart and organization.
        for (SourceAccountingLine line : purapAccountingService.generateSummary(document.getItems())) {
            // check to make sure the account is in the auto approve exclusion list
            Map<String, String> autoApproveMap = new HashMap<String, String>();
            autoApproveMap.put("chartOfAccountsCode", line.getChartOfAccountsCode());
            autoApproveMap.put("accountNumber", line.getAccountNumber());
            AutoApproveExclude autoApproveExclude = (AutoApproveExclude) businessObjectService.findByPrimaryKey(AutoApproveExclude.class, autoApproveMap);
            if (autoApproveExclude != null) {
                return false;
            }

            minimumAmount = getMinimumLimitAmount(negativePaymentRequestApprovalLimitService.findByChart(line.getChartOfAccountsCode()), minimumAmount);
            minimumAmount = getMinimumLimitAmount(negativePaymentRequestApprovalLimitService.findByChartAndAccount(line.getChartOfAccountsCode(), line.getAccountNumber()), minimumAmount);
            minimumAmount = getMinimumLimitAmount(negativePaymentRequestApprovalLimitService.findByChartAndOrganization(line.getChartOfAccountsCode(), line.getOrganizationReferenceId()), minimumAmount);
        }

        // If Receiving required is set, it's not needed to check the negative payment request approval limit
        if (document.isReceivingDocumentRequiredIndicator()){
            return true;
        }
        
        // If no limit was found or the default is less than the limit, the default limit is used.
        if (ObjectUtils.isNull(minimumAmount) || defaultMinimumLimit.compareTo(minimumAmount) < 0) {
            minimumAmount = defaultMinimumLimit;
        }

        // The document is eligible for auto-approval if the document total is below the limit.
        if (document.getDocumentHeader().getFinancialDocumentTotalAmount().isLessThan(minimumAmount)) {
            return true;
        }

        return false;
    }

    /**
     * This method iterates a collection of negative payment request approval limits and returns the minimum of a given minimum
     * amount and the least among the limits in the collection.
     * 
     * @param limits         The collection of NegativePaymentRequestApprovalLimit to be used in determining the minimum limit amount.
     * @param minimumAmount  The amount to be compared with the collection of NegativePaymentRequestApprovalLimit to determine the
     *                       minimum limit amount.
     * @return               The minimum of the given minimum amount and the least among the limits in the collection.
     */
    private KualiDecimal getMinimumLimitAmount(Collection<NegativePaymentRequestApprovalLimit> limits, KualiDecimal minimumAmount) {
        for (NegativePaymentRequestApprovalLimit limit : limits) {
            KualiDecimal amount = limit.getNegativePaymentRequestApprovalLimitAmount();
            if (null == minimumAmount) {
                minimumAmount = amount;
            }
            else if (minimumAmount.isGreaterThan(amount)) {
                minimumAmount = amount;
            }
        }
        return minimumAmount;
    }

    /**
     * Retrieves a list of payment request documents with the given vendor id and invoice number.
     * 
     * @param vendorHeaderGeneratedId  The vendor header generated id.
     * @param vendorDetailAssignedId   The vendor detail assigned id.
     * @param invoiceNumber            The invoice number as entered by AP.
     * @return                         List of payment request document.
     */
    public List getPaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId, String invoiceNumber) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        return paymentRequestDao.getActivePaymentRequestsByVendorNumberInvoiceNumber(vendorHeaderGeneratedId, vendorDetailAssignedId, invoiceNumber);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#paymentRequestDuplicateMessages(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public HashMap<String, String> paymentRequestDuplicateMessages(PaymentRequestDocument document) {
        HashMap<String, String> msgs;
        msgs = new HashMap<String, String>();

        Integer purchaseOrderId = document.getPurchaseOrderIdentifier();

        if (ObjectUtils.isNotNull(document.getInvoiceDate())) {
            if (purapService.isDateAYearBeforeToday(document.getInvoiceDate())) {
                msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_INVOICE_DATE_A_YEAR_OR_MORE_PAST));
            }
        }
        PurchaseOrderDocument po = document.getPurchaseOrderDocument();

        if (po != null) {
            Integer vendorDetailAssignedId = po.getVendorDetailAssignedIdentifier();
            Integer vendorHeaderGeneratedId = po.getVendorHeaderGeneratedIdentifier();


            List<PaymentRequestDocument> preqs = getPaymentRequestsByVendorNumberInvoiceNumber(vendorHeaderGeneratedId, vendorDetailAssignedId, document.getInvoiceNumber());

            if (preqs.size() > 0) {
                boolean addedMessage = false;
                boolean foundCanceledPostApprove = false; // cancelled
                boolean foundCanceledPreApprove = false; // voided
                for (PaymentRequestDocument testPREQ : preqs) {
                    if (StringUtils.equals(testPREQ.getStatusCode(), PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE)) {
                        foundCanceledPostApprove |= true;
                    }
                    else if (StringUtils.equals(testPREQ.getStatusCode(), PaymentRequestStatuses.CANCELLED_IN_PROCESS)) {
                        foundCanceledPreApprove |= true;
                    }
                    else {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE));
                        addedMessage = true;
                        break;
                    }
                }
                // Custom error message for duplicates related to cancelled/voided PREQs
                if (!addedMessage) {
                    if (foundCanceledPostApprove && foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_CANCELLEDORVOIDED));
                    }
                    else if (foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_VOIDED));
                    }
                    else if (foundCanceledPostApprove) {
                        // messages.add("errors.duplicate.vendor.invoice.cancelled");
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_CANCELLED));
                    }
                }
            }

            // Check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
            preqs = getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(purchaseOrderId, document.getVendorInvoiceAmount(), document.getInvoiceDate());
            if (preqs.size() > 0) {
                boolean addedMessage = false;
                boolean foundCanceledPostApprove = false; // cancelled
                boolean foundCanceledPreApprove = false; // voided
                msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT));
                for (PaymentRequestDocument testPREQ : preqs) {
                    if (StringUtils.equalsIgnoreCase(testPREQ.getStatusCode(), PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE)) {
                        foundCanceledPostApprove |= true;
                    }
                    else if (StringUtils.equalsIgnoreCase(testPREQ.getStatusCode(), PaymentRequestStatuses.CANCELLED_IN_PROCESS)) {
                        foundCanceledPreApprove |= true;
                    }
                    else {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT));
                        addedMessage = true;
                        break;
                    }
                }

                // Custom error message for duplicates related to cancelled/voided PREQs
                if (!addedMessage) {
                    if (foundCanceledPostApprove && foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLEDORVOIDED));
                    }
                    else if (foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_VOIDED));
                        addedMessage = true;
                    }
                    else if (foundCanceledPostApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLED));
                        addedMessage = true;
                    }

                }
            }
        }
        return msgs;
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getPaymentRequestByDocumentNumber(java.lang.String)
     */
    public PaymentRequestDocument getPaymentRequestByDocumentNumber(String documentNumber) {
        LOG.debug("getPaymentRequestByDocumentNumber() started");

        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                PaymentRequestDocument doc = (PaymentRequestDocument) documentService.getByDocumentHeaderId(documentNumber);
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting payment request document from document service";
                LOG.error("getPaymentRequestByDocumentNumber() " + errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }
        }
        return null;
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getPaymentRequestById(java.lang.Integer)
     */
    public PaymentRequestDocument getPaymentRequestById(Integer poDocId) {
        return getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(poDocId));
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getPaymentRequestsByPurchaseOrderId(java.lang.Integer)
     */
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId) {
        List<PaymentRequestDocument> preqs = new ArrayList<PaymentRequestDocument>();
        List<String> docNumbers = paymentRequestDao.getDocumentNumbersByPurchaseOrderId(poDocId);
        for (String docNumber : docNumbers) {
            PaymentRequestDocument preq = getPaymentRequestByDocumentNumber(docNumber);
            if (ObjectUtils.isNotNull(preq)) {
                preqs.add(preq);
            }
        }
        return preqs;
    }

    /**
     * @see org.kuali.module.purap.service.AccountsPayableDocumentSpecificService#saveDocumentWithoutValidation(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public void saveDocumentWithoutValidation(AccountsPayableDocument document) {
        try {
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);
        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(java.lang.Integer, org.kuali.core.util.KualiDecimal, java.sql.Date)
     */
    public List getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate) {
        LOG.debug("getPaymentRequestsByPOIdInvoiceAmountInvoiceDate() started");
        return paymentRequestDao.getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(poId, invoiceAmount, invoiceDate);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#isInvoiceDateAfterToday(java.sql.Date)
     */
    public boolean isInvoiceDateAfterToday(Date invoiceDate) {
        // Check invoice date to make sure it is today or before
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, 11);
        now.set(Calendar.MINUTE, 59);
        now.set(Calendar.SECOND, 59);
        now.set(Calendar.MILLISECOND, 59);
        Timestamp nowTime = new Timestamp(now.getTimeInMillis());
        Calendar invoiceDateC = Calendar.getInstance();
        invoiceDateC.setTime(invoiceDate);
        // set time to midnight
        invoiceDateC.set(Calendar.HOUR, 0);
        invoiceDateC.set(Calendar.MINUTE, 0);
        invoiceDateC.set(Calendar.SECOND, 0);
        invoiceDateC.set(Calendar.MILLISECOND, 0);
        Timestamp invoiceDateTime = new Timestamp(invoiceDateC.getTimeInMillis());
        return ((invoiceDateTime.compareTo(nowTime)) > 0);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#calculatePayDate(java.sql.Date, org.kuali.module.vendor.bo.PaymentTermType)
     */
    public Date calculatePayDate(Date invoiceDate, PaymentTermType terms) {
        LOG.debug("calculatePayDate() started");
        // calculate the invoice + processed calendar
        Calendar invoicedDateCalendar = dateTimeService.getCalendar(invoiceDate);
        Calendar processedDateCalendar = dateTimeService.getCurrentCalendar();
        
        // add default number of days to processed
        String defaultDays = parameterService.getParameterValue(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PREQ_PAY_DATE_DEFAULT_NUMBER_OF_DAYS);
        processedDateCalendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(defaultDays));
        
        if (ObjectUtils.isNull(terms) || StringUtils.isEmpty(terms.getVendorPaymentTermsCode())) {
            invoicedDateCalendar.add(Calendar.DAY_OF_MONTH, PurapConstants.PREQ_PAY_DATE_EMPTY_TERMS_DEFAULT_DAYS);
            return returnLaterDate(invoicedDateCalendar, processedDateCalendar);
        }

        Integer discountDueNumber = terms.getVendorDiscountDueNumber();
        Integer netDueNumber = terms.getVendorNetDueNumber();
        if (ObjectUtils.isNotNull(discountDueNumber)) {
            String discountDueTypeDescription = terms.getVendorDiscountDueTypeDescription();
            paymentTermsDateCalculation(discountDueTypeDescription, invoicedDateCalendar, discountDueNumber);
        }
        else if (ObjectUtils.isNotNull(netDueNumber)) {
            String netDueTypeDescription = terms.getVendorNetDueTypeDescription();
            paymentTermsDateCalculation(netDueTypeDescription, invoicedDateCalendar, netDueNumber);
        }
        else {
            throw new RuntimeException("Neither discount or net number were specified for this payment terms type");
        }

        // return the later date
        return returnLaterDate(invoicedDateCalendar, processedDateCalendar);
    }

    /**
     * Returns whichever date is later, the invoicedDateCalendar or the processedDateCalendar.
     * 
     * @param invoicedDateCalendar   One of the dates to be used in determining which date is later.
     * @param processedDateCalendar  The other date to be used in determining which date is later.
     * @return                       The date which is the later of the two given dates in the input parameters.
     */
    private Date returnLaterDate(Calendar invoicedDateCalendar, Calendar processedDateCalendar) {
        if (invoicedDateCalendar.after(processedDateCalendar)) {
            return new Date(invoicedDateCalendar.getTimeInMillis());
        }
        else {
            return new Date(processedDateCalendar.getTimeInMillis());
        }
    }

    /**
     * Calculates the paymentTermsDate given the dueTypeDescription, invoicedDateCalendar and
     * the dueNumber.
     * 
     * @param dueTypeDescription    The due type description of the payment term.
     * @param invoicedDateCalendar  The Calendar object of the invoice date.
     * @param discountDueNumber     Either the vendorDiscountDueNumber or the vendorDiscountDueNumber of the payment term.
     */
    private void paymentTermsDateCalculation(String dueTypeDescription, Calendar invoicedDateCalendar, Integer dueNumber) {

        if (StringUtils.equals(dueTypeDescription, PurapConstants.PREQ_PAY_DATE_DATE)) {
            // date specified set to date in next month
            invoicedDateCalendar.add(Calendar.MONTH, 1);
            invoicedDateCalendar.set(Calendar.DAY_OF_MONTH, dueNumber.intValue());
        }
        else if (StringUtils.equals(PurapConstants.PREQ_PAY_DATE_DAYS, dueTypeDescription)) {
            // days specified go forward that number
            invoicedDateCalendar.add(Calendar.DAY_OF_MONTH, dueNumber.intValue());
        }
        else {
            // improper string
            throw new RuntimeException("missing payment terms description or not properly enterred on payment term maintenance doc");
        }
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#calculatePaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, boolean)
     */
    public void calculatePaymentRequest(PaymentRequestDocument paymentRequest, boolean updateDiscount) {
        LOG.debug("calculatePaymentRequest() started");

        if (ObjectUtils.isNull(paymentRequest.getPaymentRequestPayDate())) {
            paymentRequest.setPaymentRequestPayDate(calculatePayDate(paymentRequest.getInvoiceDate(), paymentRequest.getVendorPaymentTerms()));
        }

        if (updateDiscount) {
            calculateDiscount(paymentRequest);
        }

        distributeAccounting(paymentRequest);
    }


    /**
     * Calculates the discount item for this paymentRequest.
     * 
     * @param paymentRequestDocument  The payment request document whose discount to be calculated.
     */
    private void calculateDiscount(PaymentRequestDocument paymentRequestDocument) {
        PaymentRequestItem discountItem = findDiscountItem(paymentRequestDocument);
        // find out if we really need the discount item
        PaymentTermType pt = paymentRequestDocument.getVendorPaymentTerms();
        if ((pt != null) && (pt.getVendorPaymentTermsPercent() != null) && (BigDecimal.ZERO.compareTo(pt.getVendorPaymentTermsPercent()) != 0)) {
            if (discountItem == null) {
                // set discountItem and add to items
                // this is probably not the best way of doing it but should work for now if we start excluding discount from below
                // we will need to manually add
                purapService.addBelowLineItems(paymentRequestDocument);
                discountItem = findDiscountItem(paymentRequestDocument);
            }
            // discount item should no longer be null, update if necessary
            if (discountItem.getExtendedPrice()==null || 
                discountItem.getExtendedPrice().isZero()) {
                KualiDecimal totalCost = paymentRequestDocument.getTotalDollarAmountAboveLineItems();
                BigDecimal discountAmount = pt.getVendorPaymentTermsPercent().multiply(totalCost.bigDecimalValue()).multiply(new BigDecimal(PurapConstants.PREQ_DISCOUNT_MULT));
                // do we really need to set both, not positive, but probably won't hurt
                discountItem.setItemUnitPrice(discountAmount.setScale(2, KualiDecimal.ROUND_BEHAVIOR));
                discountItem.setExtendedPrice(new KualiDecimal(discountAmount));
            }
        }
        else { // no discount
            if (discountItem != null) {
                paymentRequestDocument.getItems().remove(discountItem);
            }
        }

    }

    /**
     * Finds the discount item of the payment request document.
     * 
     * @param paymentRequestDocument  The payment request document to be used to find the discount item.
     * @return                        The discount item if it exists.
     */
    private PaymentRequestItem findDiscountItem(PaymentRequestDocument paymentRequestDocument) {
        PaymentRequestItem discountItem = null;
        for (PaymentRequestItem preqItem : (List<PaymentRequestItem>) paymentRequestDocument.getItems()) {
            if (StringUtils.equals(preqItem.getItemTypeCode(), PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE)) {
                discountItem = preqItem;
                break;
            }
        }
        return discountItem;
    }

    /**
     * Distributes accounts for a payment request document.
     * 
     * @param paymentRequestDocument
     */
    private void distributeAccounting(PaymentRequestDocument paymentRequestDocument) {
        // update the account amounts before doing any distribution
        purapAccountingService.updateAccountAmounts(paymentRequestDocument);

        for (PaymentRequestItem item : (List<PaymentRequestItem>) paymentRequestDocument.getItems()) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            List<PurApAccountingLine> distributedAccounts = null;
            List<SourceAccountingLine> summaryAccounts = null;

            // skip above the line
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                continue;
            }

            if ((item.getSourceAccountingLines().isEmpty()) && (ObjectUtils.isNotNull(item.getExtendedPrice())) && (KualiDecimal.ZERO.compareTo(item.getExtendedPrice()) != 0)) {
                if ((StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE, item.getItemType().getItemTypeCode())) && (paymentRequestDocument.getGrandTotal() != null) && ((KualiDecimal.ZERO.compareTo(paymentRequestDocument.getGrandTotal()) != 0))) {

                    totalAmount = paymentRequestDocument.getGrandTotal();

                    summaryAccounts = purapAccountingService.generateSummary(paymentRequestDocument.getItems());

                    distributedAccounts = purapAccountingService.generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE, PaymentRequestAccount.class);

                }
                else {


                    PurchaseOrderItem poi = item.getPurchaseOrderItem();
                    if ((poi != null) && (poi.getSourceAccountingLines() != null) && (!(poi.getSourceAccountingLines().isEmpty())) && (poi.getExtendedPrice() != null) && ((KualiDecimal.ZERO.compareTo(poi.getExtendedPrice())) != 0)) {
                        // use accounts from purchase order item matching this item
                        // account list of current item is already empty
                        item.generateAccountListFromPoItemAccounts(poi.getSourceAccountingLines());
                    }
                    else {
                        totalAmount = paymentRequestDocument.getPurchaseOrderDocument().getTotalDollarAmountAboveLineItems();
                        purapAccountingService.updateAccountAmounts(paymentRequestDocument.getPurchaseOrderDocument());
                        summaryAccounts = purapAccountingService.generateSummary(PurApItemUtils.getAboveTheLineOnly(paymentRequestDocument.getPurchaseOrderDocument().getItems()));
                        distributedAccounts = purapAccountingService.generateAccountDistributionForProration(summaryAccounts, totalAmount, new Integer("6"), PaymentRequestAccount.class);
                    }

                }
                if (CollectionUtils.isNotEmpty(distributedAccounts) && CollectionUtils.isEmpty(item.getSourceAccountingLines())) {
                    item.setSourceAccountingLines(distributedAccounts);
                }
            }
            // update the item
            purapAccountingService.updateItemAccountAmounts(item);
        }
        // update again now that distribute is finished. (Note: we may not need this anymore now that I added updateItem line above
        purapAccountingService.updateAccountAmounts(paymentRequestDocument);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#addHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument,
     *      java.lang.String)
     */
    public PaymentRequestDocument addHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        documentService.addNoteToDocument(document, noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to true
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));
        preqDoc.setHoldIndicator(true);
        preqDoc.setLastActionPerformedByUniversalUserId(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        saveDocumentWithoutValidation(preqDoc);

        // must also save it on the incoming document
        document.setHoldIndicator(true);
        document.setLastActionPerformedByUniversalUserId(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        return preqDoc;
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#removeHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public PaymentRequestDocument removeHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        documentService.addNoteToDocument(document, noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to false
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));
        preqDoc.setHoldIndicator(false);
        preqDoc.setLastActionPerformedByUniversalUserId(null);
        saveDocumentWithoutValidation(preqDoc);

        // must also save it on the incoming document
        document.setHoldIndicator(false);
        document.setLastActionPerformedByUniversalUserId(null);
        return preqDoc;
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#canHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canHoldPaymentRequest(PaymentRequestDocument document, UniversalUser user) {
        boolean canHold = false;

        String accountsPayableGroup = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);

        /*
         * The user is an approver of the document, The user is a member of the Accounts Payable group
         */
        if (document.isHoldIndicator() == false && document.getPaymentRequestedCancelIndicator() == false && ((document.getDocumentHeader().hasWorkflowDocument() && (document.getDocumentHeader().getWorkflowDocument().stateIsEnroute() && document.getDocumentHeader().getWorkflowDocument().isApprovalRequested())) || (user.isMember(accountsPayableGroup) && document.getExtractedDate() == null)) && (!PurapConstants.PaymentRequestStatuses.STATUSES_DISALLOWING_HOLD.contains(document.getStatusCode()) || isBeingAdHocRouted(document))) {

            canHold = true;
        }

        return canHold;
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#canRemoveHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canRemoveHoldPaymentRequest(PaymentRequestDocument document, UniversalUser user) {
        boolean canRemoveHold = false;

        String accountsPayableSupervisorGroup = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);

        /*
         * The user is the person who put the preq on hold The user is a member of the AP Supervisor group
         */
        if (document.isHoldIndicator() && (user.getPersonUniversalIdentifier().equals(document.getLastActionPerformedByUniversalUserId()) || user.isMember(accountsPayableSupervisorGroup)) && (!PurapConstants.PaymentRequestStatuses.STATUSES_DISALLOWING_REMOVE_HOLD.contains(document.getStatusCode()))) {

            canRemoveHold = true;
        }
        return canRemoveHold;
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#addHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument,
     *      java.lang.String)
     */
    public void requestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        documentService.addNoteToDocument(document, noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to true
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));
        preqDoc.setPaymentRequestedCancelIndicator(true);
        preqDoc.setLastActionPerformedByUniversalUserId(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        preqDoc.setAccountsPayableRequestCancelIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        saveDocumentWithoutValidation(preqDoc);

        // must also save it on the incoming document
        document.setPaymentRequestedCancelIndicator(true);
        document.setLastActionPerformedByUniversalUserId(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        document.setAccountsPayableRequestCancelIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#removeHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public void removeRequestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        documentService.addNoteToDocument(document, noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to false
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));
        clearRequestCancelFields(preqDoc);
        saveDocumentWithoutValidation(preqDoc);

        // must also save it on the incoming document
        clearRequestCancelFields(document);
    }

    /**
     * Clears the request cancel fields.
     * 
     * @param document  The payment request document whose request cancel fields to be cleared.
     */
    private void clearRequestCancelFields(PaymentRequestDocument document) {
        document.setPaymentRequestedCancelIndicator(false);
        document.setLastActionPerformedByUniversalUserId(null);
        document.setAccountsPayableRequestCancelIdentifier(null);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#isExtracted(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public boolean isExtracted(PaymentRequestDocument document) {
        return (ObjectUtils.isNull(document.getExtractedDate()) ? false : true);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#canHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canUserRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user) {
        /*
         * The user is an approver of the document,
         */
        if (document.getPaymentRequestedCancelIndicator() == false && document.isHoldIndicator() == false && document.getExtractedDate() == null && (document.getDocumentHeader().hasWorkflowDocument() && (document.getDocumentHeader().getWorkflowDocument().stateIsEnroute() && document.getDocumentHeader().getWorkflowDocument().isApprovalRequested())) && (!PurapConstants.PaymentRequestStatuses.STATUSES_DISALLOWING_REQUEST_CANCEL.contains(document.getStatusCode()) || isBeingAdHocRouted(document))) {
            return true;
        }
        return false;
    }

    private boolean isBeingAdHocRouted(PaymentRequestDocument document) {
        return document.getDocumentHeader().getWorkflowDocument().isAdHocRequested();
    }
    
    /**
     * This method determines if a user has permission to remove a request for cancel on a payment request document.
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#canRemoveHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canUserRemoveRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user) {
        String accountsPayableSupervisorGroup = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);

        /*
         * The user is the person who requested a cancel on the preq The user is a member of the AP Supervisor group
         */
        if (document.getPaymentRequestedCancelIndicator() && (user.getPersonUniversalIdentifier().equals(document.getLastActionPerformedByUniversalUserId()) || user.isMember(accountsPayableSupervisorGroup)) && (!PurapConstants.PaymentRequestStatuses.STATUSES_DISALLOWING_REQUEST_CANCEL.contains(document.getStatusCode()))) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#cancelExtractedPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, java.lang.String)
     */
    public void cancelExtractedPaymentRequest(PaymentRequestDocument paymentRequest, String note) {
        LOG.debug("cancelExtractedPaymentRequest() started");
        if (PaymentRequestStatuses.CANCELLED_STATUSES.contains(paymentRequest.getStatusCode())) {
            LOG.debug("cancelExtractedPaymentRequest() ended");
            return;
        }

        try {
            Note cancelNote = documentService.createNoteFromDocument(paymentRequest, note);
            documentService.addNoteToDocument(paymentRequest, cancelNote);
            noteService.save(cancelNote);
        }
        catch (Exception e) {
            throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE + " " + e);
        }

        //cancel extracted should not reopen PO
        paymentRequest.setReopenPurchaseOrderIndicator(false);

        SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(paymentRequest, ""); // Performs save, so no explicit save is necessary
        LOG.debug("cancelExtractedPaymentRequest() PREQ " + paymentRequest.getPurapDocumentIdentifier() + " Cancelled Without Workflow");
        LOG.debug("cancelExtractedPaymentRequest() ended");
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#resetExtractedPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, java.lang.String)
     */
    public void resetExtractedPaymentRequest(PaymentRequestDocument paymentRequest, String note) {
        LOG.debug("resetExtractedPaymentRequest() started");
        if (PaymentRequestStatuses.CANCELLED_STATUSES.contains(paymentRequest.getStatusCode())) {
            LOG.debug("resetExtractedPaymentRequest() ended");
            return;
        }
        paymentRequest.setExtractedDate(null);
        paymentRequest.setPaymentPaidDate(null);
        String noteText = "This Payment Request is being reset for extraction by PDP " + note;
        try {
            Note resetNote = documentService.createNoteFromDocument(paymentRequest, noteText);
            documentService.addNoteToDocument(paymentRequest, resetNote);
            noteService.save(resetNote);
        }
        catch (Exception e) {
            throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE + " " + e);
        }
        this.saveDocumentWithoutValidation(paymentRequest);
        LOG.debug("resetExtractedPaymentRequest() PREQ " + paymentRequest.getPurapDocumentIdentifier() + " Reset from Extracted status");
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#populatePaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public void populatePaymentRequest(PaymentRequestDocument paymentRequestDocument) {

        PurchaseOrderDocument purchaseOrderDocument = paymentRequestDocument.getPurchaseOrderDocument();

        // make a call to search for expired/closed accounts
        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList = SpringContext.getBean(AccountsPayableService.class).getExpiredOrClosedAccountList(paymentRequestDocument);

        paymentRequestDocument.populatePaymentRequestFromPurchaseOrder(purchaseOrderDocument, expiredOrClosedAccountList);

        paymentRequestDocument.getDocumentHeader().setFinancialDocumentDescription(createPreqDocumentDescription(paymentRequestDocument.getPurchaseOrderIdentifier(), paymentRequestDocument.getVendorName()));

        // write a note for expired/closed accounts if any exist and add a message stating there were expired/closed accounts at the
        // top of the document
        SpringContext.getBean(AccountsPayableService.class).generateExpiredOrClosedAccountNote(paymentRequestDocument, expiredOrClosedAccountList);

        // set indicator so a message is displayed for accounts that were replaced due to expired/closed status
        if (!expiredOrClosedAccountList.isEmpty()) {
            paymentRequestDocument.setContinuationAccountIndicator(true);
        }

        // add discount item
        calculateDiscount(paymentRequestDocument);
        // distribute accounts (i.e. proration)
        distributeAccounting(paymentRequestDocument);

    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#createPreqDocumentDescription(java.lang.Integer, java.lang.String)
     */
    public String createPreqDocumentDescription(Integer purchaseOrderIdentifier, String vendorName) {
        StringBuffer descr = new StringBuffer("");
        descr.append("PO: ");
        descr.append(purchaseOrderIdentifier);
        descr.append(" Vendor: ");
        descr.append(StringUtils.trimToEmpty(vendorName));

        int noteTextMaxLength = dataDictionaryService.getAttributeMaxLength(DocumentHeader.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_DESCRIPTION).intValue();
        if (noteTextMaxLength >= descr.length()) {
            return descr.toString();
        }
        else {
            return descr.toString().substring(0, noteTextMaxLength);
        }
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#populateAndSavePaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public void populateAndSavePaymentRequest(PaymentRequestDocument preq) throws WorkflowException {
        try {
            preq.setStatusCode(PurapConstants.PaymentRequestStatuses.IN_PROCESS);
            documentService.saveDocument(preq, ContinuePurapEvent.class);
        }
        catch (ValidationException ve) {
            preq.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
        }
        catch (WorkflowException we) {
            preq.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
            String errorMsg = "Error saving document # " + preq.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * If the full document entry has been completed and the status of the related purchase order document is closed, return true,
     * otherwise return false.
     * 
     * @param apDoc  The AccountsPayableDocument to be determined whether its purchase order should be reversed.
     * @return       boolean true if the purchase order should be reversed.
     * @see          org.kuali.module.purap.service.AccountsPayableDocumentSpecificService#shouldPurchaseOrderBeReversed
     *               (org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public boolean shouldPurchaseOrderBeReversed(AccountsPayableDocument apDoc) {
        PurchaseOrderDocument po = apDoc.getPurchaseOrderDocument();
        if (ObjectUtils.isNull(po)) {
            throw new RuntimeException("po should never be null on PREQ");
        }
        // if past full entry and already closed return true
        if (purapService.isFullDocumentEntryCompleted(apDoc) && StringUtils.equalsIgnoreCase(PurapConstants.PurchaseOrderStatuses.CLOSED, po.getStatusCode())) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.module.purap.service.AccountsPayableDocumentSpecificService#getUniversalUserForCancel(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public UniversalUser getUniversalUserForCancel(AccountsPayableDocument apDoc) {
        PaymentRequestDocument preqDoc = (PaymentRequestDocument) apDoc;
        UniversalUser user = null;
        if (preqDoc.isPaymentRequestedCancelIndicator()) {
            user = preqDoc.getLastActionPerformedByUser();
        }
        return user;
    }

    /**
     * @see org.kuali.module.purap.service.AccountsPayableDocumentSpecificService#takePurchaseOrderCancelAction(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public void takePurchaseOrderCancelAction(AccountsPayableDocument apDoc) {
        PaymentRequestDocument preqDocument = (PaymentRequestDocument) apDoc;
        if (preqDocument.isReopenPurchaseOrderIndicator()) {
            String docType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT;
            SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(preqDocument.getPurchaseOrderDocument().getDocumentNumber(), docType, "reopened by Credit Memo " + apDoc.getPurapDocumentIdentifier() + "cancel", new ArrayList(), PurapConstants.PurchaseOrderStatuses.PENDING_REOPEN);
        }
    }

    /**
     * @see org.kuali.module.purap.service.AccountsPayableDocumentSpecificService#updateStatusByNode(java.lang.String,
     *      org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public String updateStatusByNode(String currentNodeName, AccountsPayableDocument apDoc) {
        return updateStatusByNode(currentNodeName, (PaymentRequestDocument) apDoc);
    }

    /**
     * Updates the status of the payment request document.
     * 
     * @param currentNodeName  The current node name.
     * @param preqDoc          The payment request document whose status to be updated.
     * @return                 The canceled status code.
     */
    private String updateStatusByNode(String currentNodeName, PaymentRequestDocument preqDoc) {
        // remove request cancel if necessary
        clearRequestCancelFields(preqDoc);

        // update the status on the document

        String cancelledStatusCode = "";
        if (StringUtils.isEmpty(currentNodeName)) {
            // if empty probably not coming from workflow
            cancelledStatusCode = PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE;
        }
        else {
            NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(currentNodeName);
            if (ObjectUtils.isNotNull(currentNode)) {
                cancelledStatusCode = currentNode.getDisapprovedStatusCode();
            }
        }

        if (StringUtils.isNotBlank(cancelledStatusCode)) {
            purapService.updateStatus(preqDoc, cancelledStatusCode);
            saveDocumentWithoutValidation(preqDoc);
            return cancelledStatusCode;
        }
        else {
            logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + currentNodeName + "'");
        }
        return cancelledStatusCode;
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#markPaid(org.kuali.module.purap.document.PaymentRequestDocument,
     *      java.sql.Date)
     */
    public void markPaid(PaymentRequestDocument pr, Date processDate) {
        LOG.debug("markPaid() started");

        pr.setPaymentPaidDate(processDate);
        saveDocumentWithoutValidation(pr);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#hasDiscountItem(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public boolean hasDiscountItem(PaymentRequestDocument preq) {
        return ObjectUtils.isNotNull(findDiscountItem(preq));
    }

    /**
     * @see org.kuali.module.purap.service.AccountsPayableDocumentSpecificService#poItemEligibleForAp(org.kuali.module.purap.document.AccountsPayableDocument, org.kuali.module.purap.bo.PurchaseOrderItem)
     */
    public boolean poItemEligibleForAp(AccountsPayableDocument apDoc, PurchaseOrderItem poi) {
        if (ObjectUtils.isNull(poi)) {
            throw new RuntimeException("item null in purchaseOrderItemEligibleForPayment ... this should never happen");
        }
        // if the po item is not active... skip it
        if (!poi.isItemActiveIndicator()) {
            return false;
        }

        ItemType poiType = poi.getItemType();

        if (poiType.isQuantityBasedGeneralLedgerIndicator()) {
            if (poi.getItemQuantity().isGreaterThan(poi.getItemInvoicedTotalQuantity())) {
                return true;
            }
            return false;
        }
        else { // not quantity based
            if (poi.getItemOutstandingEncumberedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
            return false;
        }
    }

    public void changeVendor(PaymentRequestDocument preq, Integer headerId, Integer detailId) {
        
        VendorDetail primaryVendor = vendorService.getVendorDetail(
                preq.getOriginalVendorHeaderGeneratedIdentifier(), preq.getOriginalVendorDetailAssignedIdentifier());
        
        if (primaryVendor == null){
             LOG.error("useAlternateVendor() primaryVendorDetail from database for header id " + headerId + " and detail id " + detailId + "is null");
             throw new PurError("AlternateVendor: VendorDetail from database for header id " + headerId + " and detail id " + detailId + "is null");
        }
        
        //set vendor detail
        VendorDetail vd = vendorService.getVendorDetail(headerId, detailId);
        if (vd == null){
            LOG.error("changeVendor() VendorDetail from database for header id " + headerId + " and detail id " + detailId + "is null");
            throw new PurError("changeVendor: VendorDetail from database for header id " + headerId + " and detail id " + detailId + "is null");
        }        
        preq.setVendorDetail(vd);
        preq.setVendorName(vd.getVendorName());
        preq.setVendorNumber(vd.getVendorNumber());
        preq.setVendorHeaderGeneratedIdentifier(vd.getVendorHeaderGeneratedIdentifier());
        preq.setVendorDetailAssignedIdentifier(vd.getVendorDetailAssignedIdentifier());        
        preq.setVendorPaymentTermsCode(vd.getVendorPaymentTermsCode());
        preq.setVendorShippingPaymentTermsCode(vd.getVendorShippingPaymentTermsCode());        
        preq.setVendorShippingTitleCode(vd.getVendorShippingTitleCode());
        preq.refreshReferenceObject("vendorPaymentTerms");
        preq.refreshReferenceObject("vendorShippingPaymentTerms");
        
        //Set vendor address
        String deliveryCampus = preq.getPurchaseOrderDocument().getDeliveryCampusCode();
        VendorAddress va = vendorService.getVendorDefaultAddress(headerId, detailId, VendorConstants.AddressTypes.REMIT, deliveryCampus);
        if (va == null){
            va = vendorService.getVendorDefaultAddress(headerId, detailId, VendorConstants.AddressTypes.PURCHASE_ORDER, deliveryCampus);
        }
        if (va == null){
          LOG.error("changeVendor() VendorAddress from database for header id " + headerId + " and detail id " + detailId + "is null");
          throw new PurError("changeVendor  VendorAddress from database for header id " + headerId + " and detail id " + detailId + "is null");
        }
              
        if (preq != null) {
            setVendorAddress(va, preq);
        } else {
          LOG.error("changeVendor(): Null link back to the Purchase Order.");
          throw new PurError("Null link back to the Purchase Order.");
        }
        
        //change document description
        preq.getDocumentHeader().setFinancialDocumentDescription( createPreqDocumentDescription(preq.getPurchaseOrderIdentifier(), preq.getVendorName()) );
     }

    /**
     * Set the Vendor address of the given ID.
     * 
     * @param addressID   ID of the address to set
     * @param pr          PaymentRequest to set in
     * @return            New PaymentRequest to use
     */
    private void setVendorAddress(VendorAddress va, PaymentRequestDocument preq) {
                        
      if (va != null) {          
        preq.setVendorAddressGeneratedIdentifier(va.getVendorAddressGeneratedIdentifier());
        preq.setVendorAddressInternationalProvinceName(va.getVendorAddressInternationalProvinceName());        
        preq.setVendorLine1Address(va.getVendorLine1Address());
        preq.setVendorLine2Address(va.getVendorLine2Address());        
        preq.setVendorCityName(va.getVendorCityName());
        preq.setVendorStateCode(va.getVendorStateCode());        
        preq.setVendorPostalCode(va.getVendorZipCode());
        preq.setVendorCountryCode(va.getVendorCountryCode());
      }

    }

    /**
     * Records the specified error message into the Log file and throws a runtime exception.
     * 
     * @param errorMessage the error message to be logged.
     */
    protected void logAndThrowRuntimeException(String errorMessage) {
        this.logAndThrowRuntimeException(errorMessage, null);
    }

    /**
     * Records the specified error message into the Log file and throws the specified runtime exception.
     * 
     * @param errorMessage the specified error message.
     * @param e the specified runtime exception.
     */
    protected void logAndThrowRuntimeException(String errorMessage, Exception e) {
        if (ObjectUtils.isNotNull(e)) {
            LOG.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
        else {
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
    
    /**
     * The given document here actually needs to be a Payment Request.
     * 
     * @see org.kuali.module.purap.service.AccountsPayableDocumentSpecificService#generateGLEntriesCreateAccountsPayableDocument(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public void generateGLEntriesCreateAccountsPayableDocument(AccountsPayableDocument apDocument) {
        PaymentRequestDocument paymentRequest = (PaymentRequestDocument)apDocument;
        SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesCreatePaymentRequest(paymentRequest);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#hasActivePaymentRequestsForPurchaseOrder(java.lang.Integer)
     */
    public boolean hasActivePaymentRequestsForPurchaseOrder(Integer purchaseOrderIdentifier){
        
        boolean hasActivePreqs = false;
        List<String> docNumbers= null;
        KualiWorkflowDocument workflowDocument = null;
        
        docNumbers= paymentRequestDao.getActivePaymentRequestDocumentNumbersForPurchaseOrder(purchaseOrderIdentifier);
        
        for (String docNumber : docNumbers) {
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getUniversalUser());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            //if the document is not in a non-active status then return true and stop evaluation
            if(!(workflowDocument.stateIsCanceled() ||
                    workflowDocument.stateIsException() ||
                    workflowDocument.stateIsFinal()) ){
                hasActivePreqs = true;
                break;
            }

        }
        
        return hasActivePreqs;
    }
            }
