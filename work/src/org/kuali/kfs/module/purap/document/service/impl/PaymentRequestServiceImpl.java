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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.DocumentSystemSaveEvent;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
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
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.module.purap.util.PurApItemUtils;
import org.kuali.module.vendor.bo.PaymentTermType;
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
    private PurchaseOrderService purchaseOrderService;
    private KualiConfigurationService kualiConfigurationService;
    private NegativePaymentRequestApprovalLimitService negativePaymentRequestApprovalLimitService;
    private PurapAccountingService purapAccountingService;

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
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

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }
    
    public void setNegativePaymentRequestApprovalLimitService(NegativePaymentRequestApprovalLimitService negativePaymentRequestApprovalLimitService) {
        this.negativePaymentRequestApprovalLimitService = negativePaymentRequestApprovalLimitService;
    }
    
    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }


    /**
     * @see org.kuali.module.purap.server.PaymentRequestService.getPaymentRequestsToExtractByCM()
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtractByCM(String campusCode,CreditMemoDocument cmd) {
        LOG.debug("getPaymentRequestsByCM() started");

        return paymentRequestDao.getPaymentRequestsToExtract(campusCode,cmd.getPaymentRequestIdentifier(),cmd.getPurchaseOrderIdentifier(),cmd.getVendorHeaderGeneratedIdentifier(),cmd.getVendorDetailAssignedIdentifier());
    }

    /**
     * @see org.kuali.module.purap.server.PaymentRequestService.getPaymentRequestsToExtract()
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract() {
        LOG.debug("getPaymentRequestsToExtract() started");

        return paymentRequestDao.getPaymentRequestsToExtract(false,null);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getPaymentRequestsToExtractSpecialPayments(java.lang.String)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtractSpecialPayments(String chartCode) {
        LOG.debug("getPaymentRequestsToExtractSpecialPayments() started");

        return paymentRequestDao.getPaymentRequestsToExtract(true,chartCode);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getImmediatePaymentRequestsToExtract(java.lang.String)
     */
    public Iterator<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode) {
        LOG.debug("getImmediatePaymentRequestsToExtract() started");

        return paymentRequestDao.getImmediatePaymentRequestsToExtract(chartCode);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#getPaymentRequestToExtractByChart(java.lang.String)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestToExtractByChart(String chartCode) {
        LOG.debug("getPaymentRequestToExtractByChart() started");

        return paymentRequestDao.getPaymentRequestsToExtract(false,chartCode);
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService.autoApprovePaymentRequests()
     */
    public boolean autoApprovePaymentRequests() {
        boolean hadErrorAtLeastOneError = true;
        // should objects from existing user session be copied over
        List<PaymentRequestDocument> docs = paymentRequestDao.getEligibleForAutoApproval();
        if (docs != null) {
            String samt = kualiConfigurationService.getApplicationParameterValue(
                    PurapParameterConstants.PURAP_ADMIN_GROUP, 
                    PurapParameterConstants.PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT);
            KualiDecimal defaultMinimumLimit = new KualiDecimal(samt);
            for (PaymentRequestDocument paymentRequestDocument : docs) {
                hadErrorAtLeastOneError |= !autoApprovePaymentRequest(paymentRequestDocument, defaultMinimumLimit);
            }
        }
        return hadErrorAtLeastOneError;
    }

    public boolean autoApprovePaymentRequest(PaymentRequestDocument doc, KualiDecimal defaultMinimumLimit) {
        if (isEligibleForAutoApproval(doc, defaultMinimumLimit)) {
            try {
                purapService.updateStatusAndStatusHistory(doc, PaymentRequestStatuses.AUTO_APPROVED);
                documentService.blanketApproveDocument(doc, "auto-approving: Total is below threshold.", null);
            }
            catch (WorkflowException we) {
                LOG.error("Exception encountered when approving document number " + doc.getDocumentNumber() + ".", we);
                return false;
            }
        }
        return true;
    }
    
    /**
     * This method determines whether or not a payment request document can be
     * automatically approved.
     * 
     *  FYI - If fiscal reviewers are allowed to save account changes without the full account validation running then this
     *        method must call full account validation to make sure auto approver is not blanket approving an invalid document
     *        according the the accounts on the items
     */
    private boolean isEligibleForAutoApproval(PaymentRequestDocument document, KualiDecimal defaultMinimumLimit) {
        // This minimum will be set to the minimum limit derived from all
        // accounting lines on the document. If no limit is determined, the 
        // default will be used.
        KualiDecimal minimumAmount = null;
        
        // Iterate all source accounting lines on the document, deriving a 
        // minimum limit from each according to chart, chart and account, and 
        // chart and organization. 
        for (SourceAccountingLine line : purapAccountingService.generateSummary(document.getItems())) {
            minimumAmount = getMinimumLimitAmount(
                    negativePaymentRequestApprovalLimitService.findByChart(
                            line.getChartOfAccountsCode()), minimumAmount);
            minimumAmount = getMinimumLimitAmount(
                    negativePaymentRequestApprovalLimitService.findByChartAndAccount(
                            line.getChartOfAccountsCode(), line.getAccountNumber()), minimumAmount);
            minimumAmount = getMinimumLimitAmount(
                    negativePaymentRequestApprovalLimitService.findByChartAndOrganization(
                            line.getChartOfAccountsCode(), line.getOrganizationReferenceId()), minimumAmount);
        }
        
        // If no limit was found, the default limit is used.
        if(null == minimumAmount) {
            minimumAmount = defaultMinimumLimit;
        }
        
        // The document is eligible for auto-approval if the document total is below the limit. 
        if(document.getDocumentHeader().getFinancialDocumentTotalAmount().isLessThan(minimumAmount)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * This method iterates a collection of negative payment request approval 
     * limits and returns the minimum of a given minimum amount and the least
     * among the limits in the collection.
     * 
     * @param limits
     * @param minimumAmount
     * @return
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
     * Retreives a list of Pay Reqs with the given vendor id and invoice number.
     * 
     * @param vendorHeaderGeneratedId  header id of the vendor id
     * @param vendorDetailAssignedId   detail id of the vendor id
     * @param invoiceNumber            invoice number as entered by AP
     * @return List of Pay Reqs.
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
        
        if (ObjectUtils.isNotNull(document.getInvoiceDate())){
            if (purapService.isDateAYearBeforeToday(document.getInvoiceDate())) {
                msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_INVOICE_DATE_A_YEAR_OR_MORE_PAST));
            }
        }
        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderId);

        if (po != null) {
            Integer vendorDetailAssignedId = po.getVendorDetailAssignedIdentifier();
            Integer vendorHeaderGeneratedId = po.getVendorHeaderGeneratedIdentifier();


            List<PaymentRequestDocument> preqs = getPaymentRequestsByVendorNumberInvoiceNumber(vendorHeaderGeneratedId, vendorDetailAssignedId, document.getInvoiceNumber());

            if (preqs.size() > 0) {
                boolean addedMessage = false;
                boolean foundCanceledPostApprove = false;  // cancelled
                boolean foundCanceledPreApprove = false;  // voided
                for (PaymentRequestDocument testPREQ : preqs) {
                    if (StringUtils.equals(testPREQ.getStatusCode(), PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE)) {
                        foundCanceledPostApprove |= true;
                    }
                    else if (StringUtils.equals(testPREQ.getStatusCode(), PaymentRequestStatuses.CANCELLED_IN_PROCESS)) {
                        foundCanceledPreApprove |= true;
                    }
                    else {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE));
                        addedMessage = true;
                        break;
                    }
                }
                // Custom error message for duplicates related to cancelled/voided PREQs
                if (!addedMessage) {
                    if (foundCanceledPostApprove && foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_CANCELLEDORVOIDED));
                    }
                    else if (foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_VOIDED));
                    }
                    else if (foundCanceledPostApprove) {
                        // messages.add("errors.duplicate.vendor.invoice.cancelled");
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_CANCELLED));
                    }
                }
            }

            // Check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
            preqs = getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(purchaseOrderId, document.getVendorInvoiceAmount(), document.getInvoiceDate());
            if (preqs.size() > 0) {
                boolean addedMessage = false;
                boolean foundCanceledPostApprove = false;  // cancelled
                boolean foundCanceledPreApprove = false;  // voided
                msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT));
                for (PaymentRequestDocument testPREQ : preqs) {
                    if (StringUtils.equalsIgnoreCase(testPREQ.getStatusCode(), PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE)) {
                        foundCanceledPostApprove |= true;
                    }
                    else if (StringUtils.equalsIgnoreCase(testPREQ.getStatusCode(), PaymentRequestStatuses.CANCELLED_IN_PROCESS)) {
                        foundCanceledPreApprove |= true;
                    }
                    else {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT));
                        addedMessage = true;
                        break;
                    }
                }

                // Custom error message for duplicates related to cancelled/voided PREQs
                if (!addedMessage) {
                    if (foundCanceledPostApprove && foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLEDORVOIDED));
                    }
                    else if (foundCanceledPreApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_VOIDED));
                        addedMessage = true;
                    }
                    else if (foundCanceledPostApprove) {
                        msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLED));
                        addedMessage = true;
                    }

                }
            }
        }
        return msgs;
    }
    
    private PaymentRequestDocument getPaymentRequestByDocumentNumber(String documentNumber) {
        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                PaymentRequestDocument doc = (PaymentRequestDocument)documentService.getByDocumentHeaderId(documentNumber);
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting payment request document from document service";
                LOG.error("getPaymentRequestByDocumentNumber() " + errorMessage,e);
                throw new RuntimeException(errorMessage,e);
            }
        }
        return null;
    }
    
    public PaymentRequestDocument getPaymentRequestById(Integer poDocId){
        return getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(poDocId));
    }

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
     * @see org.kuali.module.purap.service.CreditMemoService#saveDocumentWithoutValidation(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void saveDocumentWithoutValidation(PaymentRequestDocument document) {
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
     * Retreives a list of Pay Reqs with the given PO Id, invoice amount, and invoice date.
     * 
     * @param poId           purchase order ID
     * @param invoiceAmount  amount of the invoice as entered by AP
     * @param invoiceDate    date of the invoice as entered by AP
     * @return List of Pay Reqs.
     */
    public List getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate) {
        LOG.debug("getPaymentRequestsByPOIdInvoiceAmountInvoiceDate() started");
        return paymentRequestDao.getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(poId, invoiceAmount, invoiceDate);
    }


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
    
    /*
     * Calculate based on the terms and calculate a date 10 days from today.  Pick the
     * one that is the farthest out.  We always calculate the discount date, if there is one.
     */
    public Date calculatePayDate(Date invoiceDate,PaymentTermType terms) {
        LOG.debug("calculatePayDate() started");
        //calculate the invoice + processed calendar
        Calendar invoicedDateCalendar = dateTimeService.getCalendar(invoiceDate);
        Calendar processedDateCalendar = dateTimeService.getCurrentCalendar();
        //add default number of days to processed
        processedDateCalendar.add(Calendar.DAY_OF_MONTH,PurapConstants.PREQ_PAY_DATE_DEFAULT_NUMBER_OF_DAYS);

        if(ObjectUtils.isNull(terms) || StringUtils.isEmpty(terms.getVendorPaymentTermsCode())) {
            invoicedDateCalendar.add(Calendar.DAY_OF_MONTH, PurapConstants.PREQ_PAY_DATE_EMPTY_TERMS_DEFAULT_DAYS);
            return returnLaterDate(invoicedDateCalendar, processedDateCalendar);
        }
        
        Integer discountDueNumber = terms.getVendorDiscountDueNumber();
        Integer netDueNumber = terms.getVendorNetDueNumber();
        if(ObjectUtils.isNotNull(discountDueNumber)) {
            String discountDueTypeDescription = terms.getVendorDiscountDueTypeDescription();
            paymentTermsDateCalculation(discountDueTypeDescription, invoicedDateCalendar, discountDueNumber);
        }
        else if(ObjectUtils.isNotNull(netDueNumber)) {
            String netDueTypeDescription = terms.getVendorNetDueTypeDescription();
            paymentTermsDateCalculation(netDueTypeDescription, invoicedDateCalendar, netDueNumber);
        }
        else {
            throw new RuntimeException("Neither discount or net number were specified for this payment terms type");
        }

        //return the later date
        return returnLaterDate(invoicedDateCalendar, processedDateCalendar);
    }

    /**
     * This method...
     * @param invoicedDateCalendar
     * @param processedDateCalendar
     * @return
     */
    private Date returnLaterDate(Calendar invoicedDateCalendar, Calendar processedDateCalendar) {
        if(invoicedDateCalendar.after(processedDateCalendar)) {
            return new Date(invoicedDateCalendar.getTimeInMillis());
        } else {
            return new Date(processedDateCalendar.getTimeInMillis());
        }
    }

    /**
     * This method...
     * @param terms
     * @param invoicedDateCalendar
     * @param discountDueNumber
     */
    private void paymentTermsDateCalculation(String dueTypeDescription, Calendar invoicedDateCalendar, Integer dueNumber) {
        
          if(StringUtils.equals(dueTypeDescription,PurapConstants.PREQ_PAY_DATE_DATE)) {
            //date specified set to date in next month
              invoicedDateCalendar.add(Calendar.MONTH, 1);
              invoicedDateCalendar.set(Calendar.DAY_OF_MONTH,dueNumber.intValue());
          } else if(StringUtils.equals(PurapConstants.PREQ_PAY_DATE_DAYS, dueTypeDescription)) {
            //days specified go forward that number
              invoicedDateCalendar.add(Calendar.DAY_OF_MONTH,dueNumber.intValue());   
          } else {
              //improper string
              throw new RuntimeException("missing payment terms description or not properly enterred on payment term maintenance doc");
          }
    }


    
    public void calculatePaymentRequest(PaymentRequestDocument paymentRequest,boolean updateDiscount) {
        LOG.debug("calculatePaymentRequest() started");
        
        if(ObjectUtils.isNull(paymentRequest.getPaymentRequestPayDate())) {
            paymentRequest.setPaymentRequestPayDate(calculatePayDate(paymentRequest.getInvoiceDate(),paymentRequest.getVendorPaymentTerms()));
        }
        
        for (PaymentRequestItem item : (List<PaymentRequestItem>)paymentRequest.getItems()) {
            if(item.getItemType().isItemTypeAboveTheLineIndicator() &&
                !item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                PurchaseOrderItem poi = (PurchaseOrderItem)paymentRequest.getPurchaseOrderDocument().getItem(item.getItemLineNumber().intValue());
                if(ObjectUtils.isNotNull(item.getExtendedPrice()) &&
                   !(KualiDecimal.ZERO.compareTo(item.getExtendedPrice())==0)) {
                    item.setItemUnitPrice(new BigDecimal(item.getExtendedPrice().toString()));
                }
            }
        }
        
        if(updateDiscount) {
            calculateDiscount(paymentRequest);
        }
        
        distributeAccounting(paymentRequest);
    }
    
    
    /**
     * This method calculates the discount item for this paymentRequest
     */
    private void calculateDiscount(PaymentRequestDocument paymentRequestDocument) {
        PaymentRequestItem discountItem = findDiscountItem(paymentRequestDocument);
        //find out if we really need the discount item
        PaymentTermType pt = paymentRequestDocument.getVendorPaymentTerms(); 
        if((pt!=null)&&(pt.getVendorPaymentTermsPercent()!=null) && (BigDecimal.ZERO.compareTo(pt.getVendorPaymentTermsPercent())!=0)) {
            if(discountItem==null) {
                //set discountItem and add to items
                //this is probably not the best way of doing it but should work for now if we start excluding discount from below we will need to manually add
                SpringContext.getBean(PurapService.class).addBelowLineItems(paymentRequestDocument);
                discountItem = findDiscountItem(paymentRequestDocument);
            }
            KualiDecimal totalCost = paymentRequestDocument.getTotalDollarAmountAboveLineItems();
            BigDecimal discountAmount = pt.getVendorPaymentTermsPercent().multiply(totalCost.bigDecimalValue()).multiply(new BigDecimal(PurapConstants.PREQ_DISCOUNT_MULT));
            //do we really need to set both, not positive, but probably won't hurt
            discountItem.setItemUnitPrice(discountAmount.setScale(2,KualiDecimal.ROUND_BEHAVIOR));
            discountItem.setExtendedPrice(new KualiDecimal(discountAmount));
            
        } else { //no discount
            if(discountItem!=null) {
                paymentRequestDocument.getItems().remove(discountItem);
            }
        }
        
    }

    /**
     * This method finds the discount item
     * @param paymentRequestDocument the payment request document
     * @return the discount item if it exists
     */
    private PaymentRequestItem findDiscountItem(PaymentRequestDocument paymentRequestDocument) {
        PaymentRequestItem discountItem = null;
        for (PaymentRequestItem preqItem : (List<PaymentRequestItem>)paymentRequestDocument.getItems()) {
            if(StringUtils.equals(preqItem.getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE)) {
                discountItem=preqItem;
                break;
            }
        }
        return discountItem;
    }
    
    /**
     * 
     * This method distributes accounts for a payment request document
     * @param paymentRequestDocument
     */
    private void distributeAccounting(PaymentRequestDocument paymentRequestDocument) {
        //update the account amounts before doing any distribution
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(paymentRequestDocument);
        
        for (PaymentRequestItem item : (List<PaymentRequestItem>)paymentRequestDocument.getItems()) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            List<PurApAccountingLine> distributedAccounts = null;
            List<SourceAccountingLine> summaryAccounts = null;
            
            //skip above the line
            if(item.getItemType().isItemTypeAboveTheLineIndicator()) {
                continue;
            }
            
            if((item.getSourceAccountingLines().isEmpty()) && (ObjectUtils.isNotNull(item.getExtendedPrice())) &&
               (KualiDecimal.ZERO.compareTo(item.getExtendedPrice())!=0)) {
                //TODO: add tax stuff here in 2B
                if ((StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE,item.getItemType().getItemTypeCode())) &&
                        (paymentRequestDocument.getGrandTotal() != null) && 
                        ((KualiDecimal.ZERO.compareTo(paymentRequestDocument.getGrandTotal()) != 0))) {

                    totalAmount = paymentRequestDocument.getGrandTotal();
                    
                    summaryAccounts = SpringContext.getBean(PurapAccountingService.class).generateSummary(paymentRequestDocument.getItems());
                    
                    distributedAccounts = SpringContext.getBean(PurapAccountingService.class).generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class);
                    
                }
                else {


                    PurchaseOrderItem poi = item.getPurchaseOrderItem();
                    if ((poi != null) && (poi.getSourceAccountingLines() != null) && 
                            (!(poi.getSourceAccountingLines().isEmpty())) && 
                            (poi.getExtendedPrice() != null) && 
                            ((KualiDecimal.ZERO.compareTo(poi.getExtendedPrice())) != 0)) {
                        // use accounts from purchase order item matching this item
                        // account list of current item is already empty
                        item.generateAccountListFromPoItemAccounts(poi.getSourceAccountingLines());
                    }else { 
                         totalAmount = paymentRequestDocument.getPurchaseOrderDocument().getTotalDollarAmountAboveLineItems();
                         SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(paymentRequestDocument.getPurchaseOrderDocument());
                         summaryAccounts = SpringContext.getBean(PurapAccountingService.class).generateSummary(PurApItemUtils.getAboveTheLineOnly(paymentRequestDocument.getPurchaseOrderDocument().getItems()));
                         distributedAccounts = SpringContext.getBean(PurapAccountingService.class).generateAccountDistributionForProration(summaryAccounts, totalAmount, new Integer("6"),PaymentRequestAccount.class); 
                    }
                         
                }
                if(CollectionUtils.isNotEmpty(distributedAccounts)&&
                   CollectionUtils.isEmpty(item.getSourceAccountingLines())) {
                    item.setSourceAccountingLines(distributedAccounts);
                }
            }
        }
        //update again now that distribute is finished. 
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(paymentRequestDocument);
    }
              
    /**
     * This method marks a payment request on hold.
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#addHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, java.lang.String)
     */
    public void addHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception{        
        //save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        documentService.addNoteToDocument(document, noteObj);
        noteService.save(noteObj);
        
        // retrieve and save with hold indicator set to true
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));        
        preqDoc.setHoldIndicator(true);
        preqDoc.setLastActionPerformedByUniversalUserId( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
        saveDocumentWithoutValidation(preqDoc);
        
        // must also save it on the incoming document
        document.setHoldIndicator(true);
        document.setLastActionPerformedByUniversalUserId( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
    }
    
    /**
     * This method removes a hold on a payment request.
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#removeHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public void removeHoldOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception{
        //save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        documentService.addNoteToDocument(document, noteObj);
        noteService.save(noteObj);

        //retrieve and save with hold indicator set to false
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));        
        preqDoc.setHoldIndicator(false);
        preqDoc.setLastActionPerformedByUniversalUserId(null);
        saveDocumentWithoutValidation(preqDoc);

        //must also save it on the incoming document
        document.setHoldIndicator(false);
        document.setLastActionPerformedByUniversalUserId(null);
    }   


    /**
     * This method determines if this document may be placed on hold
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#isPaymentRequestHoldable(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public boolean isPaymentRequestHoldable(PaymentRequestDocument document){
        boolean holdable = false;
        String statusCode = document.getStatusCode();
        
        /* The document is not already on hold,
         * The document has not been extracted,
         * The document is out for approval,
         * The document is one of a set of specific PREQ status codes
         */
        if( document.isHoldIndicator() == false &&
            document.getExtractedDate() == null &&
            document.getDocumentHeader().hasWorkflowDocument() &&
            ( document.getDocumentHeader().getWorkflowDocument().stateIsEnroute() && 
              document.getDocumentHeader().getWorkflowDocument().isApprovalRequested() ) &&
            ( !PurapConstants.PaymentRequestStatuses.STATUSES_DISALLOWING_HOLD.contains(statusCode) ) ){
            
            holdable = true;
        }
        
        return holdable;
    }
    
    /**
     * This method determines if a user has permission to put a PREQ on hold
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#canHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canHoldPaymentRequest(PaymentRequestDocument document, UniversalUser user){
        boolean canHold = false;
        
        String accountsPayableGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        
        /* The user is an approver of the document,
         * The user is a member of the Accounts Payable group
         */
        if( document.isHoldIndicator() == false &&
             (document.getDocumentHeader().hasWorkflowDocument() &&
             ( document.getDocumentHeader().getWorkflowDocument().stateIsEnroute() && 
               document.getDocumentHeader().getWorkflowDocument().isApprovalRequested() ) ) ||
            ( user.isMember(accountsPayableGroup)) ){
            
            canHold = true;
        }
        
        return canHold;
    }
    
    /**
     * This method determines if a user has permission to remove a hold on a PREQ
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#canRemoveHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canRemoveHoldPaymentRequest(PaymentRequestDocument document, UniversalUser user){
        boolean canRemoveHold = false;
        
        String accountsPayableSupervisorGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        
        /* The user is the person who put the preq on hold
         * The user is a member of the AP Supervisor group
         */
        if( document.isHoldIndicator() &&
            ( user.getPersonUniversalIdentifier().equals( document.getLastActionPerformedByUniversalUserId() ) ||
             (user.isMember(accountsPayableSupervisorGroup)) ) ){
            
            canRemoveHold = true;
        }
        return canRemoveHold;
    }
    
    /**
     * This method marks a payment request as requested to be cancelled.
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#addHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, java.lang.String)
     */
    public void requestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception{    
        //save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        documentService.addNoteToDocument(document, noteObj);
        noteService.save(noteObj);
        
        //retrieve and save with hold indicator set to true
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));        
        preqDoc.setPaymentRequestedCancelIndicator(true);
        preqDoc.setLastActionPerformedByUniversalUserId( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
        preqDoc.setAccountsPayableRequestCancelIdentifier( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
        saveDocumentWithoutValidation(preqDoc);
        
        //must also save it on the incoming document
        document.setPaymentRequestedCancelIndicator(true);
        document.setLastActionPerformedByUniversalUserId( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
        document.setAccountsPayableRequestCancelIdentifier( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
    }
    
    /**
     * This method removes a cancel on a payment request.
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#removeHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public void removeRequestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception{
        //save the note
        Note noteObj = documentService.createNoteFromDocument(document, note);
        documentService.addNoteToDocument(document, noteObj);
        noteService.save(noteObj);

        //retrieve and save with hold indicator set to false
        PaymentRequestDocument preqDoc = getPaymentRequestByDocumentNumber(paymentRequestDao.getDocumentNumberByPaymentRequestId(document.getPurapDocumentIdentifier()));        
        preqDoc.setPaymentRequestedCancelIndicator(false);
        preqDoc.setLastActionPerformedByUniversalUserId(null);
        preqDoc.setAccountsPayableRequestCancelIdentifier(null);
        saveDocumentWithoutValidation(preqDoc);

        //must also save it on the incoming document
        document.setPaymentRequestedCancelIndicator(false);
        document.setLastActionPerformedByUniversalUserId(null);
        document.setAccountsPayableRequestCancelIdentifier(null);
    }   

    /**
     * This method determines if this document may have a cancel requested
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#isPaymentRequestHoldable(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public boolean canRequestCancelOnPaymentRequest(PaymentRequestDocument document){
        String statusCode = document.getStatusCode();
        
        /* The document is not already requested to be canceled,
         * The document has not been extracted,
         * The document is out for approval,
         * The document is one of a set of specific PREQ status codes
         */
        if( document.getPaymentRequestedCancelIndicator() == false &&
            document.getExtractedDate() == null &&
            document.getDocumentHeader().hasWorkflowDocument() &&
            ( document.getDocumentHeader().getWorkflowDocument().stateIsEnroute() && 
              document.getDocumentHeader().getWorkflowDocument().isApprovalRequested() ) &&
            ( PurapConstants.PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.AWAITING_ORG_REVIEW.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals(statusCode)) ){
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#isExtracted(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public boolean isExtracted(PaymentRequestDocument document) {
        return (ObjectUtils.isNull(document.getExtractedDate())?false:true);
    }
    
    /**
     * This method determines if a user has permission to request cancel on a PREQ
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#canHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canUserRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user){
        String accountsPayableGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        
        /* The user is an approver of the document,
         * The user is a member of the Accounts Payable group
         */
        if( (!document.getPaymentRequestedCancelIndicator()) &&
             (document.getDocumentHeader().hasWorkflowDocument() &&
             ( document.getDocumentHeader().getWorkflowDocument().stateIsEnroute() && 
               document.getDocumentHeader().getWorkflowDocument().isApprovalRequested() ) ) ||
            ( user.isMember(accountsPayableGroup)) ){
            return true;
        }
        return false;
    }

    /**
     * This method determines if a user has permission to remove a request for cancel on a PREQ
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#canRemoveHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canUserRemoveRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user){
        String accountsPayableSupervisorGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        
        /* The user is the person who requested a cancel on the preq
         * The user is a member of the AP Supervisor group
         */
        if( document.getPaymentRequestedCancelIndicator() &&
            ( user.getPersonUniversalIdentifier().equals( document.getLastActionPerformedByUniversalUserId() ) ||
             (user.isMember(accountsPayableSupervisorGroup))) ){
            return true;
        }
        return false;
    }

    /**
     * Cancel a PREQ that has already been extracted if allowed.
     * 
     * @param paymentRequest PaymentRequest to cancel
     * @param note String for cancel note
     * @param u User cancelling the PREQ
     */
    public void cancelExtractedPaymentRequest(PaymentRequestDocument paymentRequest, String note) {
        LOG.debug("cancelExtractedPaymentRequest() started");
        if (PaymentRequestStatuses.CANCELLED_STATUSES.contains(paymentRequest.getStatusCode())) {
            LOG.debug("cancelExtractedPaymentRequest() ended");
            return;
        }

        //FIXME (KULPURAP-1580: hjs) add call to new method once added
        //        generalLedgerService.generateEntriesCancelPreq(paymentRequest);
        try {
            Note cancelNote = documentService.createNoteFromDocument(paymentRequest,note);
            documentService.addNoteToDocument(paymentRequest,cancelNote);
        }
        catch (Exception e) {
            throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE+" "+e);
        }
        purapService.updateStatusAndStatusHistory(paymentRequest, PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE);
        this.saveDocumentWithoutValidation(paymentRequest);
        LOG.debug("cancelExtractedPaymentRequest() PREQ " + paymentRequest.getPurapDocumentIdentifier() + " Cancelled Without Workflow");
        LOG.debug("cancelExtractedPaymentRequest() ended");
    }
    
    /**
     * Reset a Payment Request that had an associated Payment Request or Credit Memo cancelled externally.
     * 
     * @param paymentRequest PaymentRequest to reset
     * @param note String for the status change note
     * @param u User resetting the PREQ
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
            Note resetNote = documentService.createNoteFromDocument(paymentRequest,noteText);
            documentService.addNoteToDocument(paymentRequest,resetNote);
        }
        catch (Exception e) {
            throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE+" "+e);
        }
        this.saveDocumentWithoutValidation(paymentRequest);
        LOG.debug("resetExtractedPaymentRequest() PREQ " + paymentRequest.getPurapDocumentIdentifier() + " Reset from Extracted status");
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#populatePaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public void populatePaymentRequest(PaymentRequestDocument paymentRequestDocument) {

        PurchaseOrderDocument purchaseOrderDocument = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderIdentifier());

        //make a call to search for expired/closed accounts
        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList = SpringContext.getBean(AccountsPayableService.class).getExpiredOrClosedAccountList( paymentRequestDocument );

        paymentRequestDocument.populatePaymentRequestFromPurchaseOrder(purchaseOrderDocument, expiredOrClosedAccountList);

        paymentRequestDocument.getDocumentHeader().setFinancialDocumentDescription( createPreqDocumentDescription(paymentRequestDocument.getPurchaseOrderIdentifier(), paymentRequestDocument.getVendorName() ) );
        
        //write a note for expired/closed accounts if any exist and add a message stating there were expired/closed accounts at the top of the document
        SpringContext.getBean(AccountsPayableService.class).generateExpiredOrClosedAccountNote(paymentRequestDocument, expiredOrClosedAccountList);
        
        //set indicator so a message is displayed for accounts that were replaced due to expired/closed status
        if(!expiredOrClosedAccountList.isEmpty()){
            paymentRequestDocument.setContinuationAccountIndicator(true);
        }
        
        //add discount item
        calculateDiscount(paymentRequestDocument);
        //distribute accounts (i.e. proration)
        distributeAccounting(paymentRequestDocument);
        
    }

    private String createPreqDocumentDescription(Integer purchaseOrderIdentifier, String vendorName){
        StringBuffer descr = new StringBuffer("");
        descr.append("PO: ");
        descr.append(purchaseOrderIdentifier);
        descr.append(" Vendor: ");
        descr.append( StringUtils.trimToEmpty(vendorName) );

        int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(DocumentHeader.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_DESCRIPTION).intValue();
        if(noteTextMaxLength >= descr.length()) {
            return descr.toString();
        }
        else {
            return descr.toString().substring(0, noteTextMaxLength);
        }
    }

    /**
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#populateAndSavePaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public void populateAndSavePaymentRequest(PaymentRequestDocument preq) throws WorkflowException {
        try {
            preq.setStatusCode(PurapConstants.PaymentRequestStatuses.IN_PROCESS);            
            documentService.saveDocument(preq, ContinueAccountsPayableEvent.class);
        }
        catch(ValidationException ve){
            preq.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
        }
        catch (WorkflowException we) {
            preq.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
            String errorMsg = "Error saving document # " + preq.getDocumentHeader().getDocumentNumber() + " " + we.getMessage(); 
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }        
    }
    
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier) {
        paymentRequestDao.deleteSummaryAccounts(purapDocumentIdentifier);
    }

    public boolean shouldPurchaseOrderBeReversed(AccountsPayableDocument apDoc) {
       PurchaseOrderDocument po = apDoc.getPurchaseOrderDocument();
       if(ObjectUtils.isNull(po)) {
           throw new RuntimeException("po should never be null on PREQ");
       }
       //if past full entry and already closed return true
       if(purapService.isFullDocumentEntryCompleted(apDoc) &&
               StringUtils.equalsIgnoreCase(PurapConstants.PurchaseOrderStatuses.CLOSED,po.getStatusCode())) {
           return true;
       }
       return false;
    }
    
    public UniversalUser getUniversalUserForCancel(AccountsPayableDocument apDoc) { 
        PaymentRequestDocument preqDoc = (PaymentRequestDocument)apDoc;
        UniversalUser user = null;
        if (preqDoc.isPaymentRequestedCancelIndicator()) {            
            user = preqDoc.getLastActionPerformedByUser();
        }
        return user;
    }

    public void takePurchaseOrderCancelAction(AccountsPayableDocument apDoc) {
        PaymentRequestDocument preqDocument = (PaymentRequestDocument)apDoc;
        if(preqDocument.isReopenPurchaseOrderIndicator()) {
            String docType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT;
            SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(preqDocument.getPurchaseOrderDocument().getDocumentNumber(), docType, "reopened by Credit Memo "+apDoc.getPurapDocumentIdentifier()+ "cancel", new ArrayList());
        }
    }
    /**
     * delegate method
     * @see org.kuali.module.purap.service.AccountsPayableDocumentSpecificService#updateStatusByNode(java.lang.String, org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public String updateStatusByNode(String currentNodeName, AccountsPayableDocument apDoc) {
        return updateStatusByNode(currentNodeName, (PaymentRequestDocument)apDoc);
    }
    /**
     * This method updates the status of a cm document
     * @param cmDocument
     * @param currentNodeName
     * @param cmDoc
     */
    private String updateStatusByNode(String currentNodeName, PaymentRequestDocument preqDoc) {
        // update the status on the document
        
        String cancelledStatusCode = "";
        if(StringUtils.isEmpty(currentNodeName)) {
            //if empty probably not coming from workflow TODO - ckirschenman check status to be sure
            cancelledStatusCode = PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE;
        } else {
            NodeDetails currentNode = NodeDetailEnum.getNodeDetailEnumByName(currentNodeName);
            if (ObjectUtils.isNotNull(currentNode)) {
                cancelledStatusCode = currentNode.getDisapprovedStatusCode();
            }
        }
        
        if (StringUtils.isNotBlank(cancelledStatusCode)) {
            purapService.updateStatusAndStatusHistory(preqDoc, cancelledStatusCode);
            saveDocumentWithoutValidation(preqDoc);
            return cancelledStatusCode;
        } else {
            // TODO (KULPURAP-1579: ckirshenman/hjs) delyea - what to do in a cancel where no status to set exists?
            LOG.warn("No status found to set for document being disapproved in node '" + currentNodeName + "'");
        }
        return cancelledStatusCode;
    }

    /**
     * @see org.kuali.module.purap.service.PaymentRequestService#markPaid(org.kuali.module.purap.document.PaymentRequestDocument, java.sql.Date)
     */
    public void markPaid(PaymentRequestDocument pr,Date processDate) {
        LOG.debug("markPaid() started");

        pr.setPaymentPaidDate(processDate);
        saveDocumentWithoutValidation(pr);
    }

    public boolean hasDiscountItem(PaymentRequestDocument preq) {
        return ObjectUtils.isNotNull(findDiscountItem(preq));
    }
}