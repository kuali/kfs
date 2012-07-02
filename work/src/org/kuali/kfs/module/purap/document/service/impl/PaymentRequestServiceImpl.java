/*
 * Copyright 2007 The Kuali Foundation
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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
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
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.kfs.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapParameterConstants.NRATaxParameters;
import org.kuali.kfs.module.purap.businessobject.AutoApproveExclude;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.NegativePaymentRequestApprovalLimit;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.NegativePaymentRequestApprovalLimitService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.module.purap.document.validation.event.PurchasingAccountsPayableItemPreCalculateEvent;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.FinancialSystemWorkflowHelperService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides services of use to a payment request document
 */
@Transactional
public class PaymentRequestServiceImpl implements PaymentRequestService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestServiceImpl.class);

    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected NoteService noteService;
    protected PurapService purapService;
    protected PaymentRequestDao paymentRequestDao;
    protected ParameterService parameterService;
    protected ConfigurationService configurationService;
    protected NegativePaymentRequestApprovalLimitService negativePaymentRequestApprovalLimitService;
    protected PurapAccountingService purapAccountingService;
    protected BusinessObjectService businessObjectService;
    protected PurApWorkflowIntegrationService purapWorkflowIntegrationService;
    protected WorkflowDocumentService workflowDocumentService;
    protected AccountsPayableService accountsPayableService;
    protected VendorService vendorService;
    protected DataDictionaryService dataDictionaryService;
    protected UniversityDateService universityDateService;
    protected BankService bankService;
    protected PurchaseOrderService purchaseOrderService;
    protected FinancialSystemWorkflowHelperService financialSystemWorkflowHelperService;
    protected KualiRuleService kualiRuleService;

    /**
     * NOTE: unused
     *
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#getPaymentRequestsToExtractByCM(java.lang.String, org.kuali.kfs.module.purap.document.VendorCreditMemoDocument)
     */
    @Override
    @Deprecated
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractByCM(String campusCode, VendorCreditMemoDocument cmd) {
        LOG.debug("getPaymentRequestsByCM() started");
        Date currentSqlDateMidnight = dateTimeService.getCurrentSqlDateMidnight();
        List<PaymentRequestDocument> paymentRequestIterator = paymentRequestDao.getPaymentRequestsToExtract(campusCode, null, null, cmd.getVendorHeaderGeneratedIdentifier(), cmd.getVendorDetailAssignedIdentifier(), currentSqlDateMidnight);

        return filterPaymentRequestByAppDocStatus(paymentRequestIterator,
                PurapConstants.PaymentRequestStatuses.APPDOC_AUTO_APPROVED,
                PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
    }


    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#getPaymentRequestsToExtractByVendor(java.lang.String,
     *      org.kuali.kfs.module.purap.util.VendorGroupingHelper, java.sql.Date)
     */
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractByVendor(String campusCode, VendorGroupingHelper vendor, Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsByVendor() started");
        Collection<PaymentRequestDocument> paymentRequestDocuments = paymentRequestDao.getPaymentRequestsToExtractForVendor(campusCode, vendor, onOrBeforePaymentRequestPayDate);

        return filterPaymentRequestByAppDocStatus(paymentRequestDocuments,
                PurapConstants.PaymentRequestStatuses.APPDOC_AUTO_APPROVED,
                PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
    }

    /**
     * @see org.kuali.module.purap.server.PaymentRequestService.getPaymentRequestsToExtract(Date)
     */
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtract(Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsToExtract() started");

        Collection<PaymentRequestDocument> paymentRequestIterator = paymentRequestDao.getPaymentRequestsToExtract(false, null, onOrBeforePaymentRequestPayDate);
        return filterPaymentRequestByAppDocStatus(paymentRequestIterator,
                PaymentRequestStatuses.STATUSES_ALLOWED_FOR_EXTRACTION);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#getPaymentRequestsToExtractSpecialPayments(java.lang.String,
     *      java.sql.Date)
     */
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractSpecialPayments(String chartCode, Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsToExtractSpecialPayments() started");

        Collection<PaymentRequestDocument> paymentRequestIterator =  paymentRequestDao.getPaymentRequestsToExtract(true, chartCode, onOrBeforePaymentRequestPayDate);
        return filterPaymentRequestByAppDocStatus(paymentRequestIterator,
                PaymentRequestStatuses.STATUSES_ALLOWED_FOR_EXTRACTION);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#getImmediatePaymentRequestsToExtract(java.lang.String)
     */
    @Override
    public Collection<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode) {
        LOG.debug("getImmediatePaymentRequestsToExtract() started");

        Collection<PaymentRequestDocument> paymentRequestIterator = paymentRequestDao.getImmediatePaymentRequestsToExtract(chartCode);
        return filterPaymentRequestByAppDocStatus(paymentRequestIterator,
                PaymentRequestStatuses.STATUSES_ALLOWED_FOR_EXTRACTION);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#getPaymentRequestToExtractByChart(java.lang.String,
     *      java.sql.Date)
     */
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestToExtractByChart(String chartCode, Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestToExtractByChart() started");

        Collection<PaymentRequestDocument> paymentRequestIterator =  paymentRequestDao.getPaymentRequestsToExtract(false, chartCode, onOrBeforePaymentRequestPayDate);
        return filterPaymentRequestByAppDocStatus(paymentRequestIterator,
                PaymentRequestStatuses.STATUSES_ALLOWED_FOR_EXTRACTION);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService.autoApprovePaymentRequests()
     */
    @Override
    public boolean autoApprovePaymentRequests() {
        boolean hadErrorAtLeastOneError = true;
        // should objects from existing user session be copied over
        Date todayAtMidnight = dateTimeService.getCurrentSqlDateMidnight();

        List<String> docNumbers = paymentRequestDao.getEligibleForAutoApproval(todayAtMidnight);
        docNumbers = filterPaymentRequestByAppDocStatus(docNumbers, PurapConstants.PaymentRequestStatuses.PREQ_STATUSES_FOR_AUTO_APPROVE);

        List<PaymentRequestDocument> docs = new ArrayList<PaymentRequestDocument>();
        for (String docNumber : docNumbers) {
            PaymentRequestDocument preq = getPaymentRequestByDocumentNumber(docNumber);
            if (ObjectUtils.isNotNull(preq)) {
                docs.add(preq);
            }
        }
        if ( LOG.isInfoEnabled() ) {
            LOG.info(" -- Initial filtering complete, returned " + new Integer((docs == null ? 0 : docs.size())).toString() + " docs.");
        }
        if (docs != null) {
            String samt = parameterService.getParameterValueAsString(PaymentRequestDocument.class, PurapParameterConstants.PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT);
            KualiDecimal defaultMinimumLimit = new KualiDecimal(samt);
            for (PaymentRequestDocument paymentRequestDocument : docs) {
                hadErrorAtLeastOneError |= !autoApprovePaymentRequest(paymentRequestDocument, defaultMinimumLimit);
            }
        }
        return hadErrorAtLeastOneError;
    }

    /**
     * NOTE: in the event of auto-approval failure, this method may throw a RuntimeException, indicating to Spring transactional
     * management that the transaction should be rolled back.
     *
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#autoApprovePaymentRequest(java.lang.String,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public boolean autoApprovePaymentRequest(String docNumber, KualiDecimal defaultMinimumLimit) {
        PaymentRequestDocument paymentRequestDocument = null;
        try {
            paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docNumber);
            if (paymentRequestDocument.isHoldIndicator() || paymentRequestDocument.isPaymentRequestedCancelIndicator() || !Arrays.asList(PurapConstants.PaymentRequestStatuses.PREQ_STATUSES_FOR_AUTO_APPROVE).contains(paymentRequestDocument.getApplicationDocumentStatus())) {
                // this condition is based on the conditions that PaymentRequestDaoOjb.getEligibleDocumentNumbersForAutoApproval()
                // uses to query
                // the database. Rechecking these conditions to ensure that the document is eligible for auto-approval, because
                // we're not running things
                // within the same transaction anymore and changes could have occurred since we called that method that make this
                // document not auto-approvable

                // note that this block does not catch all race conditions
                // however, this error condition is not enough to make us return an error code, so just skip the document
                LOG.warn("Payment Request Document " + paymentRequestDocument.getDocumentNumber() + " could not be auto-approved because it has either been placed on hold, " + " requested cancel, or does not have one of the PREQ statuses for auto-approve.");
                return true;
            }
            if (autoApprovePaymentRequest(paymentRequestDocument, defaultMinimumLimit)) {
                if ( LOG.isInfoEnabled() ) {
                    LOG.info("Auto-approval for payment request successful.  Doc number: " + docNumber);
                }
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
     * NOTE: in the event of auto-approval failure, this method may throw a RuntimeException, indicating to Spring transactional
     * management that the transaction should be rolled back.
     *
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#autoApprovePaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public boolean autoApprovePaymentRequest(PaymentRequestDocument doc, KualiDecimal defaultMinimumLimit) {
        if (isEligibleForAutoApproval(doc, defaultMinimumLimit)) {
            try {
                // Much of the rice frameworks assumes that document instances that are saved via DocumentService.saveDocument are
                // those
                // that were dynamically created by PojoFormBase (i.e., the Document instance wasn't created from OJB). We need to
                // make
                // a deep copy and materialize collections to fulfill that assumption so that collection elements will delete
                // properly

                // TODO: maybe rewriting PurapService.calculateItemTax could be rewritten so that the a deep copy doesn't need to be
                // made
                // by taking advantage of OJB's managed array lists
                try {
                    ObjectUtils.materializeUpdateableCollections(doc);
                    for (PaymentRequestItem item : (List<PaymentRequestItem>) doc.getItems()) {
                        ObjectUtils.materializeUpdateableCollections(item);
                    }
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                doc = (PaymentRequestDocument) ObjectUtils.deepCopy(doc);
                //purapService.updateStatus(doc, PaymentRequestStatuses.AUTO_APPROVED);
                doc.updateAndSaveAppDocStatus(PaymentRequestStatuses.APPDOC_AUTO_APPROVED);
                
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
     * Determines whether or not a payment request document can be automatically approved. FYI - If fiscal reviewers are allowed to
     * save account changes without the full account validation running then this method must call full account validation to make
     * sure auto approver is not blanket approving an invalid document according the the accounts on the items
     *
     * @param document The payment request document to be determined whether it can be automatically approved.
     * @param defaultMinimumLimit The amount to be used as the minimum amount if no limit was found or the default is less than the
     *        limit.
     * @return boolean true if the payment request document is eligible for auto approval.
     */
    protected boolean isEligibleForAutoApproval(PaymentRequestDocument document, KualiDecimal defaultMinimumLimit) {
        // Check if vendor is foreign.
        if (document.getVendorDetail().getVendorHeader().getVendorForeignIndicator().booleanValue()) {
            return false;
        }

        // check to make sure the payment request isn't scheduled to stop in tax review.
        if (purapWorkflowIntegrationService.willDocumentStopAtGivenFutureRouteNode(document, PaymentRequestStatuses.NODE_VENDOR_TAX_REVIEW)) {
            return false;
        }

        // Change to not auto approve if positive approval required indicator set to Yes
        if (document.isPaymentRequestPositiveApprovalIndicator()) {
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
            Map<String, Object> autoApproveMap = new HashMap<String, Object>();
            autoApproveMap.put("chartOfAccountsCode", line.getChartOfAccountsCode());
            autoApproveMap.put("accountNumber", line.getAccountNumber());
            autoApproveMap.put("active", true);
            AutoApproveExclude autoApproveExclude = (AutoApproveExclude) businessObjectService.findByPrimaryKey(AutoApproveExclude.class, autoApproveMap);
            if (autoApproveExclude != null) {
                return false;
            }

            minimumAmount = getMinimumLimitAmount(negativePaymentRequestApprovalLimitService.findByChart(line.getChartOfAccountsCode()), minimumAmount);
            minimumAmount = getMinimumLimitAmount(negativePaymentRequestApprovalLimitService.findByChartAndAccount(line.getChartOfAccountsCode(), line.getAccountNumber()), minimumAmount);
            minimumAmount = getMinimumLimitAmount(negativePaymentRequestApprovalLimitService.findByChartAndOrganization(line.getChartOfAccountsCode(), line.getOrganizationReferenceId()), minimumAmount);
        }

        // If Receiving required is set, it's not needed to check the negative payment request approval limit
        if (document.isReceivingDocumentRequiredIndicator()) {
            return true;
        }

        // If no limit was found or the default is less than the limit, the default limit is used.
        if (ObjectUtils.isNull(minimumAmount) || defaultMinimumLimit.compareTo(minimumAmount) < 0) {
            minimumAmount = defaultMinimumLimit;
        }

        // The document is eligible for auto-approval if the document total is below the limit.
        if (document.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount().isLessThan(minimumAmount)) {
            return true;
        }

        return false;
    }

    /**
     * This method iterates a collection of negative payment request approval limits and returns the minimum of a given minimum
     * amount and the least among the limits in the collection.
     *
     * @param limits The collection of NegativePaymentRequestApprovalLimit to be used in determining the minimum limit amount.
     * @param minimumAmount The amount to be compared with the collection of NegativePaymentRequestApprovalLimit to determine the
     *        minimum limit amount.
     * @return The minimum of the given minimum amount and the least among the limits in the collection.
     */
    protected KualiDecimal getMinimumLimitAmount(Collection<NegativePaymentRequestApprovalLimit> limits, KualiDecimal minimumAmount) {
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
     * @param vendorHeaderGeneratedId The vendor header generated id.
     * @param vendorDetailAssignedId The vendor detail assigned id.
     * @param invoiceNumber The invoice number as entered by AP.
     * @return List of payment request document.
     */
    @Override
    public List getPaymentRequestsByVendorNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId) {
        LOG.debug("getActivePaymentRequestsByVendorNumber() started");
        return paymentRequestDao.getActivePaymentRequestsByVendorNumber(vendorHeaderGeneratedId, vendorDetailAssignedId);
    }

    /**
     * Retrieves a list of payment request documents with the given vendor id and invoice number.
     *
     * @param vendorHeaderGeneratedId The vendor header generated id.
     * @param vendorDetailAssignedId The vendor detail assigned id.
     * @param invoiceNumber The invoice number as entered by AP.
     * @return List of payment request document.
     */
    @Override
    public List getPaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId, String invoiceNumber) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        return paymentRequestDao.getActivePaymentRequestsByVendorNumberInvoiceNumber(vendorHeaderGeneratedId, vendorDetailAssignedId, invoiceNumber);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#paymentRequestDuplicateMessages(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public HashMap<String, String> paymentRequestDuplicateMessages(PaymentRequestDocument document) {
        HashMap<String, String> msgs;
        msgs = new HashMap<String, String>();

        Integer purchaseOrderId = document.getPurchaseOrderIdentifier();

        if (ObjectUtils.isNotNull(document.getInvoiceDate())) {
            if (purapService.isDateAYearBeforeToday(document.getInvoiceDate())) {
                msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_INVOICE_DATE_A_YEAR_OR_MORE_PAST));
            }
        }
        PurchaseOrderDocument po = document.getPurchaseOrderDocument();

        if (po != null) {
            Integer vendorDetailAssignedId = po.getVendorDetailAssignedIdentifier();
            Integer vendorHeaderGeneratedId = po.getVendorHeaderGeneratedIdentifier();

            List<PaymentRequestDocument> preqs = new ArrayList();

            List<PaymentRequestDocument> preqsDuplicates = getPaymentRequestsByVendorNumber(vendorHeaderGeneratedId, vendorDetailAssignedId);
            for (PaymentRequestDocument duplicatePREQ : preqsDuplicates) {
                if (duplicatePREQ.getInvoiceNumber().toUpperCase().equals(document.getInvoiceNumber().toUpperCase())) {
                    // found the duplicate row... so add to the preqs list...
                    preqs.add(duplicatePREQ);
                }
            }

            if (preqs.size() > 0) {
                boolean addedMessage = false;
                boolean foundCanceledPostApprove = false; // cancelled
                boolean foundCanceledPreApprove = false; // voided
                for (PaymentRequestDocument testPREQ : preqs) {
                    if (StringUtils.equals(testPREQ.getApplicationDocumentStatus(), PaymentRequestStatuses.APPDOC_CANCELLED_POST_AP_APPROVE)) {
                        foundCanceledPostApprove |= true;
                    }
                    else if (StringUtils.equals(testPREQ.getApplicationDocumentStatus(), PaymentRequestStatuses.APPDOC_CANCELLED_IN_PROCESS)) {
                        foundCanceledPreApprove |= true;
                    }
                    else {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE));
                        addedMessage = true;
                        break;
                    }
                }
                // Custom error message for duplicates related to cancelled/voided PREQs
                if (!addedMessage) {
                    if (foundCanceledPostApprove && foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_CANCELLEDORVOIDED));
                    }
                    else if (foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_VOIDED));
                    }
                    else if (foundCanceledPostApprove) {
                        // messages.add("errors.duplicate.vendor.invoice.cancelled");
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_CANCELLED));
                    }
                }
            }

            // Check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
            preqs = getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(purchaseOrderId, document.getVendorInvoiceAmount(), document.getInvoiceDate());
            if (preqs.size() > 0) {
                boolean addedMessage = false;
                boolean foundCanceledPostApprove = false; // cancelled
                boolean foundCanceledPreApprove = false; // voided
                msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT));
                for (PaymentRequestDocument testPREQ : preqs) {
                    if (StringUtils.equalsIgnoreCase(testPREQ.getApplicationDocumentStatus(), PaymentRequestStatuses.APPDOC_CANCELLED_POST_AP_APPROVE)) {
                        foundCanceledPostApprove |= true;
                    }
                    else if (StringUtils.equalsIgnoreCase(testPREQ.getApplicationDocumentStatus(), PaymentRequestStatuses.APPDOC_CANCELLED_IN_PROCESS)) {
                        foundCanceledPreApprove |= true;
                    }
                    else {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT));
                        addedMessage = true;
                        break;
                    }
                }

                // Custom error message for duplicates related to cancelled/voided PREQs
                if (!addedMessage) {
                    if (foundCanceledPostApprove && foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLEDORVOIDED));
                    }
                    else if (foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_VOIDED));
                        addedMessage = true;
                    }
                    else if (foundCanceledPostApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, configurationService.getPropertyValueAsString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLED));
                        addedMessage = true;
                    }

                }
            }
        }
        return msgs;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#getPaymentRequestByDocumentNumber(java.lang.String)
     */
    @Override
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
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#getPaymentRequestById(java.lang.Integer)
     */
    @Override
    public PaymentRequestDocument getPaymentRequestById(Integer poDocId) {
        return getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(poDocId));
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#getPaymentRequestsByPurchaseOrderId(java.lang.Integer)
     */
    @Override
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
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(java.lang.Integer,
     *      org.kuali.rice.core.api.util.type.KualiDecimal, java.sql.Date)
     */
    @Override
    public List<PaymentRequestDocument> getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate) {
        LOG.debug("getPaymentRequestsByPOIdInvoiceAmountInvoiceDate() started");
        return paymentRequestDao.getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(poId, invoiceAmount, invoiceDate);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#isInvoiceDateAfterToday(java.sql.Date)
     */
    @Override
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
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#calculatePayDate(java.sql.Date,
     *      org.kuali.kfs.vnd.businessobject.PaymentTermType)
     */
    @Override
    public java.sql.Date calculatePayDate(Date invoiceDate, PaymentTermType terms) {
        LOG.debug("calculatePayDate() started");
        // calculate the invoice + processed calendar
        Calendar invoicedDateCalendar = dateTimeService.getCalendar(invoiceDate);
        Calendar processedDateCalendar = dateTimeService.getCurrentCalendar();

        // add default number of days to processed
        String defaultDays = parameterService.getParameterValueAsString(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PREQ_PAY_DATE_DEFAULT_NUMBER_OF_DAYS);
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
     * @param invoicedDateCalendar One of the dates to be used in determining which date is later.
     * @param processedDateCalendar The other date to be used in determining which date is later.
     * @return The date which is the later of the two given dates in the input parameters.
     */
    protected java.sql.Date returnLaterDate(Calendar invoicedDateCalendar, Calendar processedDateCalendar) {
        if (invoicedDateCalendar.after(processedDateCalendar)) {
            return new java.sql.Date(invoicedDateCalendar.getTimeInMillis());
        }
        else {
            return new java.sql.Date(processedDateCalendar.getTimeInMillis());
        }
    }

    /**
     * Calculates the paymentTermsDate given the dueTypeDescription, invoicedDateCalendar and the dueNumber.
     *
     * @param dueTypeDescription The due type description of the payment term.
     * @param invoicedDateCalendar The Calendar object of the invoice date.
     * @param discountDueNumber Either the vendorDiscountDueNumber or the vendorDiscountDueNumber of the payment term.
     */
    protected void paymentTermsDateCalculation(String dueTypeDescription, Calendar invoicedDateCalendar, Integer dueNumber) {

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
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#calculatePaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument,
     *      boolean)
     */
    @Override
    public void calculatePaymentRequest(PaymentRequestDocument paymentRequest, boolean updateDiscount) {
        LOG.debug("calculatePaymentRequest() started");

        // general calculation, i.e. for the whole preq document
        if (ObjectUtils.isNull(paymentRequest.getPaymentRequestPayDate())) {
            paymentRequest.setPaymentRequestPayDate(calculatePayDate(paymentRequest.getInvoiceDate(), paymentRequest.getVendorPaymentTerms()));
        }

        distributeAccounting(paymentRequest);

        purapService.calculateTax(paymentRequest);

        // do proration for full order and trade in
        purapService.prorateForTradeInAndFullOrderDiscount(paymentRequest);

        // do proration for payment terms discount
        if (updateDiscount) {
            calculateDiscount(paymentRequest);
        }

        distributeAccounting(paymentRequest);
    }


    /**
     * Calculates the discount item for this paymentRequest.
     *
     * @param paymentRequestDocument The payment request document whose discount to be calculated.
     */
    protected void calculateDiscount(PaymentRequestDocument paymentRequestDocument) {
        PaymentRequestItem discountItem = findDiscountItem(paymentRequestDocument);
        // find out if we really need the discount item
        PaymentTermType pt = paymentRequestDocument.getVendorPaymentTerms();
        if ((pt != null) && (pt.getVendorPaymentTermsPercent() != null) && (BigDecimal.ZERO.compareTo(pt.getVendorPaymentTermsPercent()) != 0)) {
            if (discountItem == null) {
                // set discountItem and add to items
                // this is probably not the best way of doing it but should work for now if we start excluding discount from below
                // we will need to manually add
                purapService.addBelowLineItems(paymentRequestDocument);

                // fix up below the line items
                removeIneligibleAdditionalCharges(paymentRequestDocument);

                discountItem = findDiscountItem(paymentRequestDocument);
            }

            // Deleted the discountItem.getExtendedPrice() null and isZero
            PaymentRequestItem fullOrderItem = findFullOrderDiscountItem(paymentRequestDocument);
            KualiDecimal fullOrderAmount = KualiDecimal.ZERO;
            KualiDecimal fullOrderTaxAmount = KualiDecimal.ZERO;

            if (fullOrderItem != null) {
                fullOrderAmount = (ObjectUtils.isNotNull(fullOrderItem.getExtendedPrice())) ? fullOrderItem.getExtendedPrice() : KualiDecimal.ZERO;
                fullOrderTaxAmount = (ObjectUtils.isNotNull(fullOrderItem.getItemTaxAmount())) ? fullOrderItem.getItemTaxAmount() : KualiDecimal.ZERO;
            }
            KualiDecimal totalCost = paymentRequestDocument.getTotalPreTaxDollarAmountAboveLineItems().add(fullOrderAmount);
            PurApItem tradeInItem = paymentRequestDocument.getTradeInItem();
            if (ObjectUtils.isNotNull(tradeInItem)) totalCost = totalCost.subtract(tradeInItem.getTotalAmount());
            BigDecimal discountAmount = pt.getVendorPaymentTermsPercent().multiply(totalCost.bigDecimalValue()).multiply(new BigDecimal(PurapConstants.PREQ_DISCOUNT_MULT));

            // do we really need to set both, not positive, but probably won't hurt
            discountItem.setItemUnitPrice(discountAmount.setScale(2, KualiDecimal.ROUND_BEHAVIOR));
            discountItem.setExtendedPrice(new KualiDecimal(discountAmount));

            // set tax amount
            boolean salesTaxInd = parameterService.getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
            boolean useTaxIndicator = paymentRequestDocument.isUseTaxIndicator();

            if (salesTaxInd == true && useTaxIndicator == false) {
                KualiDecimal totalTax = paymentRequestDocument.getTotalTaxAmountAboveLineItems().add(fullOrderTaxAmount);
                BigDecimal discountTaxAmount = null;
                if (totalCost.isNonZero()) {
                    discountTaxAmount = discountAmount.divide(totalCost.bigDecimalValue()).multiply(totalTax.bigDecimalValue());
                }
                else {
                    discountTaxAmount = BigDecimal.ZERO;
                }

                discountItem.setItemTaxAmount(new KualiDecimal(discountTaxAmount.setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
            }

            // set document
            discountItem.setPurapDocument(paymentRequestDocument);
        }
        else { // no discount
            if (discountItem != null) {
                paymentRequestDocument.getItems().remove(discountItem);
            }
        }

    }


    @Override
    public void clearTax(PaymentRequestDocument document) {
        // remove all existing tax items added by previous calculation
        removeTaxItems(document);
        // reset values
        document.setTaxClassificationCode(null);
        document.setTaxFederalPercent(null);
        document.setTaxStatePercent(null);
        document.setTaxCountryCode(null);
        document.setTaxNQIId(null);

        document.setTaxForeignSourceIndicator(false);
        document.setTaxExemptTreatyIndicator(false);
        document.setTaxOtherExemptIndicator(false);
        document.setTaxGrossUpIndicator(false);
        document.setTaxUSAIDPerDiemIndicator(false);
        document.setTaxSpecialW4Amount(null);

    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#calculateTaxArea(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public void calculateTaxArea(PaymentRequestDocument preq) {
        LOG.debug("calculateTaxArea() started");

        // remove all existing tax items added by previous calculation
        removeTaxItems(preq);

        // don't need to calculate tax items if TaxClassificationCode is N (Non_Reportable)
        if (StringUtils.equalsIgnoreCase(preq.getTaxClassificationCode(), "N"))
            return;

        // reserve the grand total excluding any tax amount, to be used as the base to compute all tax items
        // if we don't reserve this, the pre-tax total could be changed as new tax items are added
        BigDecimal taxableAmount = preq.getGrandPreTaxTotal().bigDecimalValue();

        // generate and add state tax gross up item and its accounting line, update total amount,
        // if gross up indicator is true and tax rate is non-zero
        if (preq.getTaxGrossUpIndicator() && preq.getTaxStatePercent().compareTo(new BigDecimal(0)) != 0) {
            PurApItem stateGrossItem = addTaxItem(preq, ItemTypeCodes.ITEM_TYPE_STATE_GROSS_CODE, taxableAmount);
        }

        // generate and add state tax item and its accounting line, update total amount, if tax rate is non-zero
        if (preq.getTaxStatePercent().compareTo(new BigDecimal(0)) != 0) {
            PurApItem stateTaxItem = addTaxItem(preq, ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE, taxableAmount);
        }

        // generate and add federal tax gross up item and its accounting line, update total amount,
        // if gross up indicator is true and tax rate is non-zero
        if (preq.getTaxGrossUpIndicator() && preq.getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0) {
            PurApItem federalGrossItem = addTaxItem(preq, ItemTypeCodes.ITEM_TYPE_FEDERAL_GROSS_CODE, taxableAmount);
        }

        // generate and add federal tax item and its accounting line, update total amount, if tax rate is non-zero
        if (preq.getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0) {
            PurApItem federalTaxItem = addTaxItem(preq, ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE, taxableAmount);
        }

        // FIXME if user request to add zero tax lines and remove them after tax approval,
        // then remove the conditions above when adding the tax lines, and
        // add a branch in PaymentRequestDocument.processNodeChange to call PurapService.deleteUnenteredItems
    }

    /**
     * Removes all existing NRA tax items from the specified payment request.
     *
     * @param preq The payment request from which all tax items are to be removed.
     */
    protected void removeTaxItems(PaymentRequestDocument preq) {
        List<PurApItem> items = (List<PurApItem>) preq.getItems();
        for (int i = 0; i < items.size(); i++) {
            PurApItem item = items.get(i);
            String code = item.getItemTypeCode();
            if (ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE.equals(code) || ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE.equals(code) || ItemTypeCodes.ITEM_TYPE_FEDERAL_GROSS_CODE.equals(code) || ItemTypeCodes.ITEM_TYPE_STATE_GROSS_CODE.equals(code)) {
                items.remove(i--);
            }
        }
    }

    /**
     * Generates a NRA tax item and adds to the specified payment request, according to the specified item type code.
     *
     * @param preq The payment request the tax item will be added to.
     * @param itemTypeCode The item type code for the tax item.
     * @param taxableAmount The amount to which tax is computed against.
     * @return A fully populated PurApItem instance representing NRA tax amount data for the specified payment request.
     */
    protected PurApItem addTaxItem(PaymentRequestDocument preq, String itemTypeCode, BigDecimal taxableAmount) {
        PurApItem taxItem = null;

        try {
            taxItem = (PurApItem) preq.getItemClass().newInstance();
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("Unable to access itemClass", e);
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("Unable to instantiate itemClass", e);
        }

        // add item to preq before adding the accounting line
        taxItem.setItemTypeCode(itemTypeCode);
        preq.addItem(taxItem);

        // generate and add tax accounting line
        PurApAccountingLine taxLine = addTaxAccountingLine(taxItem, taxableAmount);

        // set extended price amount as now it's calculated when accounting line is generated
        taxItem.setItemUnitPrice(taxLine.getAmount().bigDecimalValue());
        taxItem.setExtendedPrice(taxLine.getAmount());

        // use item type description as the item description
        ItemType itemType = new ItemType();
        itemType.setItemTypeCode(itemTypeCode);
        itemType = (ItemType) businessObjectService.retrieve(itemType);
        taxItem.setItemType(itemType);
        taxItem.setItemDescription(itemType.getItemTypeDescription());

        return taxItem;
    }

    /**
     * Generates a PurAP accounting line and adds to the specified tax item.
     *
     * @param taxItem The specified tax item the accounting line will be associated with.
     * @param taxableAmount The amount to which tax is computed against.
     * @return A fully populated PurApAccountingLine instance for the specified tax item.
     */
    protected PurApAccountingLine addTaxAccountingLine(PurApItem taxItem, BigDecimal taxableAmount) {
        PaymentRequestDocument preq = taxItem.getPurapDocument();
        PurApAccountingLine taxLine = null;

        try {
            taxLine = (PurApAccountingLine) taxItem.getAccountingLineClass().newInstance();
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("Unable to access sourceAccountingLineClass", e);
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("Unable to instantiate sourceAccountingLineClass", e);
        }

        // tax item type indicators
        boolean isFederalTax = ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE.equals(taxItem.getItemTypeCode());
        boolean isFederalGross = ItemTypeCodes.ITEM_TYPE_FEDERAL_GROSS_CODE.equals(taxItem.getItemTypeCode());
        boolean isStateTax = ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE.equals(taxItem.getItemTypeCode());
        boolean isStateGross = ItemTypeCodes.ITEM_TYPE_STATE_GROSS_CODE.equals(taxItem.getItemTypeCode());
        boolean isFederal = isFederalTax || isFederalGross; // true for federal tax/gross; false for state tax/gross
        boolean isGross = isFederalGross || isStateGross; // true for federal/state gross, false for federal/state tax

        // obtain accounting line info according to tax item type code
        String taxChart = null;
        String taxAccount = null;
        String taxObjectCode = null;

        if (isGross) {
            // for gross up tax items, use preq's first item's first accounting line, which shall exist at this point
            AccountingLine line1 = preq.getFirstAccount();
            taxChart = line1.getChartOfAccountsCode();
            taxAccount = line1.getAccountNumber();
            taxObjectCode = line1.getFinancialObjectCode();
        }
        else if (isFederalTax) {
            // for federal tax item, get chart, account, object code info from parameters
            taxChart = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_CHART_SUFFIX);
            taxAccount = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
            taxObjectCode = parameterService.getSubParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, preq.getTaxClassificationCode());
            if (StringUtils.isBlank(taxChart) || StringUtils.isBlank(taxAccount) || StringUtils.isBlank(taxObjectCode)) {
                LOG.error("Unable to retrieve federal tax parameters.");
                throw new RuntimeException("Unable to retrieve federal tax parameters.");
            }
        }
        else if (isStateTax) {
            // for state tax item, get chart, account, object code info from parameters
            taxChart = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_CHART_SUFFIX);
            taxAccount = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
            taxObjectCode = parameterService.getSubParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.STATE_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX, preq.getTaxClassificationCode());
            if (StringUtils.isBlank(taxChart) || StringUtils.isBlank(taxAccount) || StringUtils.isBlank(taxObjectCode)) {
                LOG.error("Unable to retrieve state tax parameters.");
                throw new RuntimeException("Unable to retrieve state tax parameters.");
            }
        }

        // calculate tax amount according to gross up indicator and federal/state tax type
        /*
         * The formula of tax and gross up amount are as follows: if (not gross up) gross not existing taxFederal/State = - amount *
         * rateFederal/State otherwise gross up grossFederal/State = amount * rateFederal/State / (1 - rateFederal - rateState) tax
         * = - gross
         */

        // pick federal/state tax rate
        BigDecimal taxPercentFederal = preq.getTaxFederalPercent();
        BigDecimal taxPercentState = preq.getTaxStatePercent();
        BigDecimal taxPercent = isFederal ? taxPercentFederal : taxPercentState;

        // divider value according to gross up or not
        BigDecimal taxDivider = new BigDecimal(100);
        if (preq.getTaxGrossUpIndicator()) {
            taxDivider = taxDivider.subtract(taxPercentFederal.add(taxPercentState));
        }

        // tax = amount * rate / divider
        BigDecimal taxAmount = taxableAmount.multiply(taxPercent);
        taxAmount = taxAmount.divide(taxDivider, 5, BigDecimal.ROUND_HALF_UP);

        // tax is always negative, since it reduces the total amount; while gross up is always the positive of tax
        if (!isGross) {
            taxAmount = taxAmount.negate();
        }

        // populate necessary accounting line fields
        taxLine.setDocumentNumber(preq.getDocumentNumber());
        taxLine.setSequenceNumber(preq.getNextSourceLineNumber());
        taxLine.setChartOfAccountsCode(taxChart);
        taxLine.setAccountNumber(taxAccount);
        taxLine.setFinancialObjectCode(taxObjectCode);
        taxLine.setAmount(new KualiDecimal(taxAmount));

        // add the accounting line to the item
        taxLine.setItemIdentifier(taxItem.getItemIdentifier());
        taxLine.setPurapItem(taxItem);
        taxItem.getSourceAccountingLines().add(taxLine);

        return taxLine;
    }

    /**
     * Finds the discount item of the payment request document.
     *
     * @param paymentRequestDocument The payment request document to be used to find the discount item.
     * @return The discount item if it exists.
     */
    protected PaymentRequestItem findDiscountItem(PaymentRequestDocument paymentRequestDocument) {
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
     * Finds the full order discount item of the payment request document.
     *
     * @param paymentRequestDocument The payment request document to be used to find the full order discount item.
     * @return The discount item if it exists.
     */
    protected PaymentRequestItem findFullOrderDiscountItem(PaymentRequestDocument paymentRequestDocument) {
        PaymentRequestItem discountItem = null;
        for (PaymentRequestItem preqItem : (List<PaymentRequestItem>) paymentRequestDocument.getItems()) {
            if (StringUtils.equals(preqItem.getItemTypeCode(), PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) {
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
    protected void distributeAccounting(PaymentRequestDocument paymentRequestDocument) {
        // update the account amounts before doing any distribution
        purapAccountingService.updateAccountAmounts(paymentRequestDocument);

        String accountDistributionMethod = paymentRequestDocument.getAccountDistributionMethod();

        for (PaymentRequestItem item : (List<PaymentRequestItem>) paymentRequestDocument.getItems()) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            List<PurApAccountingLine> distributedAccounts = null;
            List<SourceAccountingLine> summaryAccounts = null;
            Set excludedItemTypeCodes = new HashSet();
            excludedItemTypeCodes.add(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE);

            // skip above the line
            if (item.getItemType().isLineItemIndicator()) {
                continue;
            }

            if ((item.getSourceAccountingLines().isEmpty()) && (ObjectUtils.isNotNull(item.getExtendedPrice())) && (KualiDecimal.ZERO.compareTo(item.getExtendedPrice()) != 0)) {
                if ((StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE, item.getItemType().getItemTypeCode())) && (paymentRequestDocument.getGrandTotal() != null) && ((KualiDecimal.ZERO.compareTo(paymentRequestDocument.getGrandTotal()) != 0))) {

                    // No discount is applied to other item types other than item line
                    // See KFSMI-5210 for details

                    // total amount should be the line item total, not the grand total
                    totalAmount = paymentRequestDocument.getLineItemTotal();

                    // prorate item line accounts only
                    Set includedItemTypeCodes = new HashSet();
                    includedItemTypeCodes.add(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
                    summaryAccounts = purapAccountingService.generateSummaryIncludeItemTypesAndNoZeroTotals(paymentRequestDocument.getItems(), includedItemTypeCodes);
                    distributedAccounts = purapAccountingService.generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE, PaymentRequestAccount.class);
                    
                    if (PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(accountDistributionMethod)) {
                        purapAccountingService.updatePreqAccountAmountsWithTotal(distributedAccounts, item.getTotalAmount());

                    } else {

                        boolean rulePassed = true;
                        // check any business rules
                        rulePassed &= kualiRuleService.applyRules(new PurchasingAccountsPayableItemPreCalculateEvent(paymentRequestDocument, item));

                        if (rulePassed) {
                            purapAccountingService.updatePreqProporationalAccountAmountsWithTotal(distributedAccounts, item.getTotalAmount());
                        }
                    }
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
        }

        // update again now that distribute is finished. (Note: we may not need this anymore now that I added updateItem line above
        //leave the call below since we need to this when sequential method is used on the document.
        purapAccountingService.updateAccountAmounts(paymentRequestDocument);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#addHoldOnPaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument,
     *      java.lang.String)
     */
    @Override
    public PaymentRequestDocument addHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        document.addNote(noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to true
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));
        preqDoc.setHoldIndicator(true);
        preqDoc.setLastActionPerformedByPersonId(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        purapService.saveDocumentNoValidation(preqDoc);

        // must also save it on the incoming document
        document.setHoldIndicator(true);
        document.setLastActionPerformedByPersonId(GlobalVariables.getUserSession().getPerson().getPrincipalId());

        return document;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#removeHoldOnPaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public PaymentRequestDocument removeHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        document.addNote(noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to false
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));
        preqDoc.setHoldIndicator(false);
        preqDoc.setLastActionPerformedByPersonId(null);
        purapService.saveDocumentNoValidation(preqDoc);

        // must also save it on the incoming document
        document.setHoldIndicator(false);
        document.setLastActionPerformedByPersonId(null);

        return preqDoc;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#addHoldOnPaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument,
     *      java.lang.String)
     */
    @Override
    public void requestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        document.addNote(noteObj);
        noteService.save(noteObj);

        // retrieve and save with hold indicator set to true
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));
        preqDoc.setPaymentRequestedCancelIndicator(true);
        preqDoc.setLastActionPerformedByPersonId(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        preqDoc.setAccountsPayableRequestCancelIdentifier(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        purapService.saveDocumentNoValidation(preqDoc);

        // must also save it on the incoming document
        document.setPaymentRequestedCancelIndicator(true);
        document.setLastActionPerformedByPersonId(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        document.setAccountsPayableRequestCancelIdentifier(GlobalVariables.getUserSession().getPerson().getPrincipalId());
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#removeHoldOnPaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public void removeRequestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception {
        // save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        document.addNote(noteObj);
        noteService.save(noteObj);

        clearRequestCancelFields(document);

        purapService.saveDocumentNoValidation(document);

    }

    /**
     * Clears the request cancel fields.
     *
     * @param document The payment request document whose request cancel fields to be cleared.
     */
    protected void clearRequestCancelFields(PaymentRequestDocument document) {
        document.setPaymentRequestedCancelIndicator(false);
        document.setLastActionPerformedByPersonId(null);
        document.setAccountsPayableRequestCancelIdentifier(null);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#isExtracted(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public boolean isExtracted(PaymentRequestDocument document) {
        return (ObjectUtils.isNull(document.getExtractedTimestamp()) ? false : true);
    }

    protected boolean isBeingAdHocRouted(PaymentRequestDocument document) {
        return financialSystemWorkflowHelperService.isAdhocApprovalRequestedForPrincipal(document.getDocumentHeader().getWorkflowDocument(), GlobalVariables.getUserSession().getPrincipalId());
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#cancelExtractedPaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument,
     *      java.lang.String)
     */
    @Override
    public void cancelExtractedPaymentRequest(PaymentRequestDocument paymentRequest, String note) {
        LOG.debug("cancelExtractedPaymentRequest() started");
        if (PaymentRequestStatuses.CANCELLED_STATUSES.contains(paymentRequest.getApplicationDocumentStatus())) {
            LOG.debug("cancelExtractedPaymentRequest() ended");
            return;
        }

        try {
            Note cancelNote = documentService.createNoteFromDocument(paymentRequest, note);
            paymentRequest.addNote(cancelNote);
            noteService.save(cancelNote);
        }
        catch (Exception e) {
            throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE, e);
        }

        // cancel extracted should not reopen PO
        paymentRequest.setReopenPurchaseOrderIndicator(false);

        getAccountsPayableService().cancelAccountsPayableDocument(paymentRequest, ""); // Performs save, so
        // no explicit save
        // is necessary
        if (LOG.isDebugEnabled()) {
            LOG.debug("cancelExtractedPaymentRequest() PREQ " + paymentRequest.getPurapDocumentIdentifier() + " Cancelled Without Workflow");
            LOG.debug("cancelExtractedPaymentRequest() ended");
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#resetExtractedPaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument,
     *      java.lang.String)
     */
    @Override
    public void resetExtractedPaymentRequest(PaymentRequestDocument paymentRequest, String note) {
        LOG.debug("resetExtractedPaymentRequest() started");
        if (PaymentRequestStatuses.CANCELLED_STATUSES.contains(paymentRequest.getApplicationDocumentStatus())) {
            LOG.debug("resetExtractedPaymentRequest() ended");
            return;
        }
        paymentRequest.setExtractedTimestamp(null);
        paymentRequest.setPaymentPaidTimestamp(null);
        String noteText = "This Payment Request is being reset for extraction by PDP " + note;
        try {
            Note resetNote = documentService.createNoteFromDocument(paymentRequest, noteText);
            paymentRequest.addNote(resetNote);
            noteService.save(resetNote);
        }
        catch (Exception e) {
            throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE + " " + e);
        }
        purapService.saveDocumentNoValidation(paymentRequest);
        if (LOG.isDebugEnabled()) {
            LOG.debug("resetExtractedPaymentRequest() PREQ " + paymentRequest.getPurapDocumentIdentifier() + " Reset from Extracted status");
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#populatePaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public void populatePaymentRequest(PaymentRequestDocument paymentRequestDocument) {

        PurchaseOrderDocument purchaseOrderDocument = paymentRequestDocument.getPurchaseOrderDocument();

        // make a call to search for expired/closed accounts
        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList = getAccountsPayableService().getExpiredOrClosedAccountList(paymentRequestDocument);

        paymentRequestDocument.populatePaymentRequestFromPurchaseOrder(purchaseOrderDocument, expiredOrClosedAccountList);

        paymentRequestDocument.getDocumentHeader().setDocumentDescription(createPreqDocumentDescription(paymentRequestDocument.getPurchaseOrderIdentifier(), paymentRequestDocument.getVendorName()));

        // write a note for expired/closed accounts if any exist and add a message stating there were expired/closed accounts at the
        // top of the document
        getAccountsPayableService().generateExpiredOrClosedAccountNote(paymentRequestDocument, expiredOrClosedAccountList);

        // set indicator so a message is displayed for accounts that were replaced due to expired/closed status
        if (!expiredOrClosedAccountList.isEmpty()) {
            paymentRequestDocument.setContinuationAccountIndicator(true);
        }

        // add discount item
        calculateDiscount(paymentRequestDocument);
        // distribute accounts (i.e. proration)
        distributeAccounting(paymentRequestDocument);

        // set bank code to default bank code in the system parameter
        Bank defaultBank = bankService.getDefaultBankByDocType(paymentRequestDocument.getClass());
        if (defaultBank != null) {
            paymentRequestDocument.setBankCode(defaultBank.getBankCode());
            paymentRequestDocument.setBank(defaultBank);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#createPreqDocumentDescription(java.lang.Integer,
     *      java.lang.String)
     */
    @Override
    public String createPreqDocumentDescription(Integer purchaseOrderIdentifier, String vendorName) {
        StringBuffer descr = new StringBuffer("");
        descr.append("PO: ");
        descr.append(purchaseOrderIdentifier);
        descr.append(" Vendor: ");
        descr.append(StringUtils.trimToEmpty(vendorName));

        int noteTextMaxLength = dataDictionaryService.getAttributeMaxLength(DocumentHeader.class, KRADPropertyConstants.DOCUMENT_DESCRIPTION).intValue();
        if (noteTextMaxLength >= descr.length()) {
            return descr.toString();
        }
        else {
            return descr.toString().substring(0, noteTextMaxLength);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#populateAndSavePaymentRequest(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public void populateAndSavePaymentRequest(PaymentRequestDocument preq) throws WorkflowException {
        try {
            preq.updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_IN_PROCESS);
            documentService.saveDocument(preq, AttributedContinuePurapEvent.class);
        }
        catch (ValidationException ve) {
            preq.updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_INITIATE);
        }
        catch (WorkflowException we) {
            preq.updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_INITIATE);

            String errorMsg = "Error saving document # " + preq.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * If the full document entry has been completed and the status of the related purchase order document is closed, return true,
     * otherwise return false.
     *
     * @param apDoc The AccountsPayableDocument to be determined whether its purchase order should be reversed.
     * @return boolean true if the purchase order should be reversed.
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#shouldPurchaseOrderBeReversed
     *      (org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public boolean shouldPurchaseOrderBeReversed(AccountsPayableDocument apDoc) {
        PurchaseOrderDocument po = apDoc.getPurchaseOrderDocument();
        if (ObjectUtils.isNull(po)) {
            throw new RuntimeException("po should never be null on PREQ");
        }
        // if past full entry and already closed return true
        if (purapService.isFullDocumentEntryCompleted(apDoc) && StringUtils.equalsIgnoreCase(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED, po.getApplicationDocumentStatus())) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#getPersonForCancel(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public Person getPersonForCancel(AccountsPayableDocument apDoc) {
        PaymentRequestDocument preqDoc = (PaymentRequestDocument) apDoc;
        Person user = null;
        if (preqDoc.isPaymentRequestedCancelIndicator()) {
            user = preqDoc.getLastActionPerformedByUser();
        }
        return user;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#takePurchaseOrderCancelAction(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public void takePurchaseOrderCancelAction(AccountsPayableDocument apDoc) {
        PaymentRequestDocument preqDocument = (PaymentRequestDocument) apDoc;
        if (preqDocument.isReopenPurchaseOrderIndicator()) {
            String docType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT;
            purchaseOrderService.createAndRoutePotentialChangeDocument(preqDocument.getPurchaseOrderDocument().getDocumentNumber(), docType, "reopened by Credit Memo " + apDoc.getPurapDocumentIdentifier() + "cancel", new ArrayList(), PurapConstants.PurchaseOrderStatuses.APPDOC_PENDING_REOPEN);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#updateStatusByNode(java.lang.String,
     *      org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public String updateStatusByNode(String currentNodeName, AccountsPayableDocument apDoc) {
        return updateStatusByNode(currentNodeName, (PaymentRequestDocument) apDoc);
    }

    /**
     * Updates the status of the payment request document.
     *
     * @param currentNodeName The current node name.
     * @param preqDoc The payment request document whose status to be updated.
     * @return The canceled status code.
     */
    protected String updateStatusByNode(String currentNodeName, PaymentRequestDocument preqDoc) {
        // remove request cancel if necessary
        clearRequestCancelFields(preqDoc);

        // update the status on the document

        String cancelledStatus = "";
        if (StringUtils.isEmpty(currentNodeName)) {
            // if empty probably not coming from workflow
            cancelledStatus = PurapConstants.PaymentRequestStatuses.APPDOC_CANCELLED_POST_AP_APPROVE;
        }
        else {
            cancelledStatus = PurapConstants.PaymentRequestStatuses.getPaymentRequestAppDocDisapproveStatuses().get(currentNodeName);
        }

        if (StringUtils.isNotBlank(cancelledStatus)) {
            try {
            preqDoc.updateAndSaveAppDocStatus(cancelledStatus);
            } catch (WorkflowException we) {
                throw new RuntimeException("Unable to save the route status data for document: " + preqDoc.getDocumentNumber(), we);
            }
            purapService.saveDocumentNoValidation(preqDoc);
        }
        else {
            logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + currentNodeName + "'");
        }
        return cancelledStatus;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#markPaid(org.kuali.kfs.module.purap.document.PaymentRequestDocument,
     *      java.sql.Date)
     */
    @Override
    public void markPaid(PaymentRequestDocument pr, Date processDate) {
        LOG.debug("markPaid() started");

        pr.setPaymentPaidTimestamp(new Timestamp(processDate.getTime()));
        purapService.saveDocumentNoValidation(pr);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#hasDiscountItem(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public boolean hasDiscountItem(PaymentRequestDocument preq) {
        return ObjectUtils.isNotNull(findDiscountItem(preq));
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#poItemEligibleForAp(org.kuali.kfs.module.purap.document.AccountsPayableDocument,
     *      org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem)
     */
    @Override
    public boolean poItemEligibleForAp(AccountsPayableDocument apDoc, PurchaseOrderItem poi) {
        if (ObjectUtils.isNull(poi)) {
            throw new RuntimeException("item null in purchaseOrderItemEligibleForPayment ... this should never happen");
        }
        // if the po item is not active... skip it
        if (!poi.isItemActiveIndicator()) {
            return false;
        }

        ItemType poiType = poi.getItemType();
        if (ObjectUtils.isNull(poiType)) {
            return false;
        }

        if (poiType.isQuantityBasedGeneralLedgerIndicator()) {
            if (poi.getItemQuantity().isGreaterThan(poi.getItemInvoicedTotalQuantity())) {
                return true;
            }
            return false;
        }
        else { // not quantity based
            // As long as it contains a number (whether it's 0, negative or positive number), we'll
            // have to return true. This is so that the OutstandingEncumberedAmount and the
            // Original Amount from PO column would appear on the page for Trade In.
            if (poi.getItemOutstandingEncumberedAmount() != null) {
                return true;
            }
            return false;
        }
    }

    @Override
    public void removeIneligibleAdditionalCharges(PaymentRequestDocument document) {

        List<PaymentRequestItem> itemsToRemove = new ArrayList<PaymentRequestItem>();

        for (PaymentRequestItem item : (List<PaymentRequestItem>) document.getItems()) {

            // if no extended price and its an order discount or trade in, remove
            if (ObjectUtils.isNull(item.getPurchaseOrderItemUnitPrice()) && (ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE.equals(item.getItemTypeCode()) || ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE.equals(item.getItemTypeCode()))) {
                itemsToRemove.add(item);
                continue;
            }

            // if a payment terms discount exists but not set on teh doc, remove
            if (StringUtils.equals(item.getItemTypeCode(), PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE)) {
                PaymentTermType pt = document.getVendorPaymentTerms();
                if ((pt != null) && (pt.getVendorPaymentTermsPercent() != null) && (BigDecimal.ZERO.compareTo(pt.getVendorPaymentTermsPercent()) != 0)) {
                    // discount ok
                }
                else {
                    // remove discount
                    itemsToRemove.add(item);
                }
                continue;
            }

        }

        // remove items marked for removal
        for (PaymentRequestItem item : (List<PaymentRequestItem>) itemsToRemove) {
            document.getItems().remove(item);
        }
    }

    @Override
    public void changeVendor(PaymentRequestDocument preq, Integer headerId, Integer detailId) {

        VendorDetail primaryVendor = vendorService.getVendorDetail(preq.getOriginalVendorHeaderGeneratedIdentifier(), preq.getOriginalVendorDetailAssignedIdentifier());

        if (primaryVendor == null) {
            LOG.error("useAlternateVendor() primaryVendorDetail from database for header id " + headerId + " and detail id " + detailId + "is null");
            throw new PurError("AlternateVendor: VendorDetail from database for header id " + headerId + " and detail id " + detailId + "is null");
        }

        // set vendor detail
        VendorDetail vd = vendorService.getVendorDetail(headerId, detailId);
        if (vd == null) {
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

        // Set vendor address
        String deliveryCampus = preq.getPurchaseOrderDocument().getDeliveryCampusCode();
        VendorAddress va = vendorService.getVendorDefaultAddress(headerId, detailId, VendorConstants.AddressTypes.REMIT, deliveryCampus);
        if (va == null) {
            va = vendorService.getVendorDefaultAddress(headerId, detailId, VendorConstants.AddressTypes.PURCHASE_ORDER, deliveryCampus);
        }
        if (va == null) {
            LOG.error("changeVendor() VendorAddress from database for header id " + headerId + " and detail id " + detailId + "is null");
            throw new PurError("changeVendor  VendorAddress from database for header id " + headerId + " and detail id " + detailId + "is null");
        }

        if (preq != null) {
            setVendorAddress(va, preq);
        }
        else {
            LOG.error("changeVendor(): Null link back to the Purchase Order.");
            throw new PurError("Null link back to the Purchase Order.");
        }

        // change document description
        preq.getDocumentHeader().setDocumentDescription(createPreqDocumentDescription(preq.getPurchaseOrderIdentifier(), preq.getVendorName()));
    }

    /**
     * Set the Vendor address of the given ID.
     *
     * @param addressID ID of the address to set
     * @param pr PaymentRequest to set in
     * @return New PaymentRequest to use
     */
    protected void setVendorAddress(VendorAddress va, PaymentRequestDocument preq) {

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
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService#generateGLEntriesCreateAccountsPayableDocument(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public void generateGLEntriesCreateAccountsPayableDocument(AccountsPayableDocument apDocument) {
        PaymentRequestDocument paymentRequest = (PaymentRequestDocument) apDocument;
        // JHK: this is not being injected because it would cause a circular reference in the Spring definitions
        SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesCreatePaymentRequest(paymentRequest);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#hasActivePaymentRequestsForPurchaseOrder(java.lang.Integer)
     */
    @Override
    public boolean hasActivePaymentRequestsForPurchaseOrder(Integer purchaseOrderIdentifier) {

        boolean hasActivePreqs = false;
        List<String> docNumbers = null;
        WorkflowDocument workflowDocument = null;

        docNumbers = paymentRequestDao.getActivePaymentRequestDocumentNumbersForPurchaseOrder(purchaseOrderIdentifier);
        docNumbers = filterPaymentRequestByAppDocStatus(docNumbers, PaymentRequestStatuses.STATUSES_POTENTIALLY_ACTIVE);

        for (String docNumber : docNumbers) {
            try {
                workflowDocument = workflowDocumentService.loadWorkflowDocument(docNumber, GlobalVariables.getUserSession().getPerson());
            }
            catch (WorkflowException we) {
                throw new RuntimeException(we);
            }
            // if the document is not in a non-active status then return true and stop evaluation
            if (!(workflowDocument.isCanceled() || workflowDocument.isException())) {
                hasActivePreqs = true;
                break;
            }
        }
        return hasActivePreqs;
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
     * @return
     */
    protected List<String> filterPaymentRequestByAppDocStatus(List<String> lookupDocNumbers, String... appDocStatus) {
        boolean valid = false;

        final String DOC_NUM_DELIM = "|";
        StrBuilder routerHeaderIdBuilder = new StrBuilder().appendWithSeparators(lookupDocNumbers, DOC_NUM_DELIM);

        List<String> paymentRequestDocNumbers = new ArrayList<String>();

        DocumentSearchCriteria.Builder documentSearchCriteriaDTO = DocumentSearchCriteria.Builder.create();
        documentSearchCriteriaDTO.setDocumentId(routerHeaderIdBuilder.toString());
        documentSearchCriteriaDTO.setDocumentTypeName(PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT);

        DocumentSearchResults reqDocumentsList = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(
                GlobalVariables.getUserSession().getPrincipalId(), documentSearchCriteriaDTO.build());

        for (DocumentSearchResult reqDocument : reqDocumentsList.getSearchResults()) {
            ///use the appDocStatus from the KeyValueDTO result to look up custom status
            if (Arrays.asList(appDocStatus).contains(reqDocument.getDocument().getApplicationDocumentStatus())){
                //found the matching status, retrieve the routeHeaderId and add to the list
                paymentRequestDocNumbers.add(reqDocument.getDocument().getDocumentId());
            }

        }

        return paymentRequestDocNumbers;
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
    protected Collection<PaymentRequestDocument> filterPaymentRequestByAppDocStatus(Collection<PaymentRequestDocument> paymentRequestDocuments, String... appDocStatus) {
        List<String> paymentRequestDocNumbers = new ArrayList<String>();
        for (PaymentRequestDocument paymentRequest : paymentRequestDocuments){
            paymentRequestDocNumbers.add(paymentRequest.getDocumentNumber());
        }

        List<String> filteredPaymentRequestDocNumbers = filterPaymentRequestByAppDocStatus(paymentRequestDocNumbers, appDocStatus);

        Collection<PaymentRequestDocument> filteredPaymentRequestDocuments = new ArrayList<PaymentRequestDocument>();
        //add to filtered collection if it is in the filtered payment request doc number list
        for (PaymentRequestDocument paymentRequest : paymentRequestDocuments){
            if (filteredPaymentRequestDocNumbers.contains(paymentRequest.getDocumentNumber())){
                filteredPaymentRequestDocuments.add(paymentRequest);
            }
        }
        return filteredPaymentRequestDocuments;
    }


    /**
     * Wrapper class to the filterPaymentRequestByAppDocStatus (Collection<PaymentRequestDocument>)
     *
     * This class first construct the Payment Request Collection from the iterator, and then process through
     * filterPaymentRequestByAppDocStatus
     *
     * @param paymentRequestDocuments
     * @param appDocStatus
     * @return
     */
    protected Iterator<PaymentRequestDocument> filterPaymentRequestByAppDocStatus(Iterator<PaymentRequestDocument> paymentRequestIterator, String... appDocStatus) {
        Collection<PaymentRequestDocument> paymentRequestDocuments = new ArrayList<PaymentRequestDocument>();
        for (;paymentRequestIterator.hasNext();){
            paymentRequestDocuments.add(paymentRequestIterator.next());
        }

        return filterPaymentRequestByAppDocStatus(paymentRequestDocuments, appDocStatus).iterator();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#processPaymentRequestInReceivingStatus()
     */
    @Override
    public void processPaymentRequestInReceivingStatus() {
        List<String> docNumbers = paymentRequestDao.getPaymentRequestInReceivingStatus();
        docNumbers = filterPaymentRequestByAppDocStatus(docNumbers, PurapConstants.PaymentRequestStatuses.APPDOC_AWAITING_RECEIVING_REVIEW);

        List<PaymentRequestDocument> preqsAwaitingReceiving = new ArrayList<PaymentRequestDocument>();
        for (String docNumber : docNumbers) {
            PaymentRequestDocument preq = getPaymentRequestByDocumentNumber(docNumber);
            if (ObjectUtils.isNotNull(preq)) {
                preqsAwaitingReceiving.add(preq);
            }
        }
        if (ObjectUtils.isNotNull(preqsAwaitingReceiving)) {
            for (PaymentRequestDocument preqDoc : preqsAwaitingReceiving) {
                if (preqDoc.isReceivingRequirementMet()) {
                    try {
                        documentService.approveDocument(preqDoc, "Approved by Receiving Required PREQ job", null);
                    }
                    catch (WorkflowException e) {
                        LOG.error("processPaymentRequestInReceivingStatus() Error approving payment request document from awaiting receiving", e);
                        throw new RuntimeException("Error approving payment request document from awaiting receiving", e);
                    }
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService#allowBackpost(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    public boolean allowBackpost(PaymentRequestDocument paymentRequestDocument) {
        int allowBackpost = (Integer.parseInt(parameterService.getParameterValueAsString(PaymentRequestDocument.class, PurapRuleConstants.ALLOW_BACKPOST_DAYS)));

        Calendar today = dateTimeService.getCurrentCalendar();
        Integer currentFY = universityDateService.getCurrentUniversityDate().getUniversityFiscalYear();
        java.util.Date priorClosingDateTemp = universityDateService.getLastDateOfFiscalYear(currentFY - 1);
        Calendar priorClosingDate = Calendar.getInstance();
        priorClosingDate.setTime(priorClosingDateTemp);

        // adding 1 to set the date to midnight the day after backpost is allowed so that preqs allow backpost on the last day
        Calendar allowBackpostDate = Calendar.getInstance();
        allowBackpostDate.setTime(priorClosingDate.getTime());
        allowBackpostDate.add(Calendar.DATE, allowBackpost + 1);

        Calendar preqInvoiceDate = Calendar.getInstance();
        preqInvoiceDate.setTime(paymentRequestDocument.getInvoiceDate());

        // if today is after the closing date but before/equal to the allowed backpost date and the invoice date is for the
        // prior year, set the year to prior year
        if ((today.compareTo(priorClosingDate) > 0) && (today.compareTo(allowBackpostDate) <= 0) && (preqInvoiceDate.compareTo(priorClosingDate) <= 0)) {
            LOG.debug("allowBackpost() within range to allow backpost; posting entry to period 12 of previous FY");
            return true;
        }

        LOG.debug("allowBackpost() not within range to allow backpost; posting entry to current FY");
        return false;
    }

    @Override
    public boolean isPurchaseOrderValidForPaymentRequestDocumentCreation(PaymentRequestDocument paymentRequestDocument, PurchaseOrderDocument po) {
        Integer POID = paymentRequestDocument.getPurchaseOrderIdentifier();
        boolean valid = true;

        PurchaseOrderDocument purchaseOrderDocument = paymentRequestDocument.getPurchaseOrderDocument();
        if (ObjectUtils.isNull(purchaseOrderDocument)) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
            valid &= false;
        }
        else if (purchaseOrderDocument.isPendingActionIndicator()) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_PENDING_ACTION);
            valid &= false;
        }
        else if (!StringUtils.equals(purchaseOrderDocument.getApplicationDocumentStatus(), PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN)) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_OPEN);
            valid &= false;
            // if the PO is pending and it is not a Retransmit, we cannot generate a Payment Request for it
        }
        else {
            // Verify that there exists at least 1 item left to be invoiced
            // valid &= encumberedItemExistsForInvoicing(purchaseOrderDocument);
        }

        return valid;
    }

    @Override
    public boolean encumberedItemExistsForInvoicing(PurchaseOrderDocument document) {
        boolean zeroDollar = true;
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) document.getItems()) {
            // Quantity-based items
            if (poi.getItemType().isLineItemIndicator() && poi.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                KualiDecimal encumberedQuantity = poi.getItemOutstandingEncumberedQuantity() == null ? KualiDecimal.ZERO : poi.getItemOutstandingEncumberedQuantity();
                if (encumberedQuantity.compareTo(KualiDecimal.ZERO) == 1) {
                    zeroDollar = false;
                    break;
                }
            }
            // Service Items or Below-the-line Items
            else if (poi.getItemType().isAmountBasedGeneralLedgerIndicator() || poi.getItemType().isAdditionalChargeIndicator()) {
                KualiDecimal encumberedAmount = poi.getItemOutstandingEncumberedAmount() == null ? KualiDecimal.ZERO : poi.getItemOutstandingEncumberedAmount();
                if (encumberedAmount.compareTo(KualiDecimal.ZERO) == 1) {
                    zeroDollar = false;
                    break;
                }
            }
        }

        return !zeroDollar;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
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

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
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

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }


    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setFinancialSystemWorkflowHelperService(FinancialSystemWorkflowHelperService financialSystemWorkflowHelperService) {
        this.financialSystemWorkflowHelperService = financialSystemWorkflowHelperService;
    }


    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }
    
    /**
     * Gets the accountsPayableService attribute.
     * 
     * @return Returns the accountsPayableService
     */
    
    public AccountsPayableService getAccountsPayableService() {
        return SpringContext.getBean(AccountsPayableService.class);
    }
}
