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
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.NoteService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.bo.NegativePaymentRequestApprovalLimit;
import org.kuali.module.purap.bo.PaymentRequestAccount;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.exceptions.PurError;
import org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.util.PurApItemUtils;
import org.kuali.module.vendor.bo.PaymentTermType;
import org.kuali.module.vendor.service.VendorService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;



/**
 * This class provides services of use to a payment request document
 */
@Transactional
public class PaymentRequestServiceImpl implements PaymentRequestService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private NoteService noteService;
    private PurapGeneralLedgerService purapGeneralLedgerService;
    private PurapService purapService;
    private PaymentRequestDao paymentRequestDao;
    private WorkflowDocumentService workflowDocumentService;
    private VendorService vendorService;
    private PurchaseOrderService purchaseOrderService;
    private UniversalUserService universalUserService;
    private KualiConfigurationService kualiConfigurationService;
    private NegativePaymentRequestApprovalLimitService negativePaymentRequestApprovalLimitService;
    private PurapAccountingService purapAccountingService;

    /*
     private static BigDecimal zero = new BigDecimal(0);

     private PurchaseOrderService purchaseOrderService;
     private ChartOfAccountsService chartOfAccountsService;
     private RoutingService routingService;
     private ReferenceService referenceService;
     private DocumentHeaderDao documentHeaderDao;
     private MailService mailService;
     private EnvironmentService environmentService;
     private OnbaseService onbaseService;
     
     private ApplicationSettingService applicationSettingService;
     private UserService userService;
     private NegativePaymentRequestApprovalLimitService negativePaymentRequestApprovalLimitService;
     private VendorService vendorService;
     private FiscalAccountingService fiscalAccountingService;
     
     private AutoApproveExclusionService autoApproveExclusionService;
     private EmailService emailService;
     */
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the universalUserService attribute value.
     * @param universalUserService The universalUserService to set.
     */
    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setPurapGeneralLedgerService(PurapGeneralLedgerService purapGeneralLedgerService) {
        this.purapGeneralLedgerService = purapGeneralLedgerService;
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

    /* Start Paste */

    public void setUserService(UniversalUserService us) {
        universalUserService = us;
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

    /*
     public void setAutoApproveExclusionService(AutoApproveExclusionService aaeService) {
     autoApproveExclusionService = aaeService;
     }
     
     
     public void setNegativePaymentRequestApprovalLimitService(NegativePaymentRequestApprovalLimitService ns) {
     this.negativePaymentRequestApprovalLimitService = ns;
     }
     
     public void setApplicationSettingService(ApplicationSettingService ass) {
     this.applicationSettingService = ass;
     }
     public void setOnbaseService(OnbaseService onbaseService) {
     this.onbaseService = onbaseService;
     }
     
     public void setChartOfAccountsService(ChartOfAccountsService coaService) {
     this.chartOfAccountsService = coaService;
     }
     public void setRoutingService(RoutingService routingService) {
     this.routingService = routingService;
     }
     public void setReferenceService(ReferenceService referenceService) {
     this.referenceService = referenceService;
     }
     
     public void setDocumentHeaderDao(DocumentHeaderDao documentHeaderDao) {
     this.documentHeaderDao = documentHeaderDao;
     }
     public void setEnvironmentService(EnvironmentService environmentService) {
     this.environmentService = environmentService;
     }
     public void setMailService(MailService mailService) {
     this.mailService = mailService;
     }
     
     public void setFiscalAccountingService(FiscalAccountingService fiscalAccountingService) {
     this.fiscalAccountingService = fiscalAccountingService;
     }
     public void setEmailService(EmailService emailService) {
     this.emailService = emailService;
     }
     
     */

    /* End Paste */


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
        Iterator<PaymentRequestDocument> docs = paymentRequestDao.getEligibleForAutoApproval();
        if (docs.hasNext()) {
            String samt = kualiConfigurationService.getApplicationParameterValue(
                    PurapParameterConstants.PURAP_ADMIN_GROUP, 
                    PurapParameterConstants.PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT);
            KualiDecimal defaultMinimumLimit = new KualiDecimal(samt);
            
            while (docs.hasNext()) {
                PaymentRequestDocument doc = docs.next();
                hadErrorAtLeastOneError |= !autoApprovePaymentRequest(doc, defaultMinimumLimit);
            }
        }
        return hadErrorAtLeastOneError;
    }

    public boolean autoApprovePaymentRequest(PaymentRequestDocument doc, KualiDecimal defaultMinimumLimit) {
        if (isEligibleForAutoApproval(doc, defaultMinimumLimit)) {
            try {
                purapService.updateStatusAndStatusHistory(doc, PaymentRequestStatuses.AUTO_APPROVED);
                documentService.blanketApproveDocument(doc, "auto-approving: Total is below threshold.", new ArrayList());
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
     */
    private boolean isEligibleForAutoApproval(PaymentRequestDocument document, KualiDecimal defaultMinimumLimit) {
        // TODO PURAP/delyea/ckirschenman - do 'approve' validation here in case fiscal office has edited?

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
                if (ObjectUtils.isNotNull(doc)) {
                    doc.refreshNonUpdateableReferences();
                }
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
            documentService.saveDocumentWithoutRunningValidation(document);
            document.refreshNonUpdateableReferences();
        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage(); 
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

//    public void save(PaymentRequestDocument paymentRequestDocument) {
//
//        // Integer poId = paymentRequestDocument.getPurchaseOrderIdentifier();
//        // PurchaseOrderDocument purchaseOrderDocument = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderIdentifier());
//        // paymentRequestDocument.populatePaymentRequestFormPurchaseOrder(purchaseOrderDocument);
//
//        paymentRequestDao.save(paymentRequestDocument);
//    }
//
//    /**
//     * @see org.kuali.module.purap.service.PaymentRequestService#saveWithWorkflowDocumentUpdate(org.kuali.module.purap.document.PaymentRequestDocument)
//     */
//    public void saveWithWorkflowDocumentUpdate(PaymentRequestDocument paymentRequestDocument) throws WorkflowException {
//        paymentRequestDocument.getDocumentHeader().getWorkflowDocument().saveRoutingData();
//        this.save(paymentRequestDocument);
//    }
    
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
    
    public HashMap<String, String> expiredOrClosedAccountsList(PaymentRequestDocument document) {

        HashMap<String, String> list = new HashMap<String, String>();
        Integer POID = document.getPurchaseOrderIdentifier();
        java.util.Date currentDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        
        //TODO: Why not rely on ojb here?  You should have a po already from the mapping Actually it looks like the mapping isn't correct.  I'll fix this later
        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(document.getPurchaseOrderIdentifier());
        
        //TODO: check for po not be null
        //TODO: also, this method should not call po.getSourceAccountingLines; that method is not reliable for PURAP docs. (hjs)
        List accountNumberList = new ArrayList();
        for (Iterator i = po.getSourceAccountingLines().iterator(); i.hasNext();) {
            SourceAccountingLine poAccountingLine = (SourceAccountingLine) i.next();
            Account account = (Account) poAccountingLine.getAccount();

            if (account.isAccountClosedIndicator()) {
                
                // 1.  if the account is closed, get the continuation account and it to the list 
                Account continuationAccount = account.getContinuationAccount();
                if (continuationAccount == null) {
                    //TODO: what to do here? - This should be an error presented to the user
                }
                else {
                    list.put(account.getAccountNumber(), continuationAccount.getAccountNumber());
                }
                // 2.  if the account is expired and the current date is <= 30 days from the expiration date, do nothing 
                // 3.  if the account is expired and the current date is > 30 days from the expiration date, get the continuation account and add it to the list
                //TODO: check to see if there is a constant defiend for this number of days (30) in the system and use it instead of 30. If not we need to define a new one 
            } else if (account.isExpired() & SpringContext.getBean(DateTimeService.class).dateDiff(account.getAccountExpirationDate(), SpringContext.getBean(DateTimeService.class).getCurrentDate(), true) > 30) {
                Account continuationAccount = account.getContinuationAccount();
                //TODO: Do we need to check for not being null and what to do??  Yes see above
                list.put(account.getAccountNumber(), continuationAccount.getAccountNumber());
            }
           //TODO: I need a stub in here somewhere here to actually do the update on the preq account list
           //maybe pass in the account and the continuation
        }
        return list;
    }

    /*
     * Calculate based on the terms and calculate a date 10 days from today.  Pick the
     * one that is the farthest out.  We always calculate the discount date, if there is one.
     */
    public Date calculatePayDate(Date invoiceDate,PaymentTermType terms) {
      LOG.debug("calculatePayDate() started");
      //TODO: this method is mainly a direct copy from epic.  It could be made a lot better by using the DateUtils and checking if those constants should be app params
      Calendar invoiceDateCalendar = Calendar.getInstance();
      invoiceDateCalendar.setTime(invoiceDate);
      invoiceDateCalendar.set(Calendar.HOUR, 12);
      invoiceDateCalendar.set(Calendar.MINUTE, 0);
      invoiceDateCalendar.set(Calendar.SECOND, 0);
      invoiceDateCalendar.set(Calendar.MILLISECOND, 0);
      invoiceDateCalendar.set(Calendar.AM_PM, Calendar.AM);

      // 10 days from now
      Calendar processDateCalendar = Calendar.getInstance();
      processDateCalendar.setTime(new java.util.Date());
      processDateCalendar.add(Calendar.DAY_OF_MONTH,10);
      processDateCalendar.set(Calendar.HOUR, 12);
      processDateCalendar.set(Calendar.MINUTE, 0);
      processDateCalendar.set(Calendar.SECOND, 0);
      processDateCalendar.set(Calendar.MILLISECOND, 0);
      processDateCalendar.set(Calendar.AM_PM, Calendar.AM);

      // Handle this weird one.  Due on the 10th or the 25th
      if (ObjectUtils.isNull(terms) || StringUtils.isEmpty(terms.getVendorPaymentTermsCode())) {
        // Payment terms are empty
        invoiceDateCalendar.add(Calendar.DATE,PurapConstants.PREQ_PAY_DATE_CALCULATION_DAYS);
        
      } else {
        // Payment terms are not empty
        if ( PurapConstants.PMT_TERMS_TYP_NO_DISCOUNT_CD.equals(terms.getVendorPaymentTermsCode()) ) {
          int dayOfMonth = invoiceDateCalendar.get(Calendar.DAY_OF_MONTH);
          if ( dayOfMonth < 10 ) {
            invoiceDateCalendar.set(Calendar.DAY_OF_MONTH,10);
          } else {
            invoiceDateCalendar.set(Calendar.DAY_OF_MONTH,25);
          }
        } else {
          if ( (terms.getVendorDiscountDueNumber() != null) && (terms.getVendorDiscountDueNumber().intValue() > 0) ) {
            if ( "date".equals(terms.getVendorDiscountDueTypeDescription()) ) {
              invoiceDateCalendar.set(Calendar.DAY_OF_MONTH,terms.getVendorDiscountDueNumber().intValue());
            } else {
              invoiceDateCalendar.add(Calendar.DAY_OF_MONTH,terms.getVendorDiscountDueNumber().intValue());
            }
          } else {
            if ( "date".endsWith(terms.getVendorNetDueTypeDescription()) ) {
              invoiceDateCalendar.set(Calendar.DAY_OF_MONTH,terms.getVendorNetDueNumber().intValue());
            } else {
              invoiceDateCalendar.add(Calendar.DAY_OF_MONTH,terms.getVendorNetDueNumber().intValue());
            }
          }
        }
      }
      if ( processDateCalendar.after(invoiceDateCalendar) ) {
        return new Date(processDateCalendar.getTime().getTime());
      } else {
        return new Date(invoiceDateCalendar.getTime().getTime());
      }
    }


    
    public void calculatePaymentRequest(PaymentRequestDocument paymentRequest,boolean updateDiscount) {
        LOG.debug("calculatePaymentRequest() started");
        //refresh the payment and shipping terms
        paymentRequest.refreshNonUpdateableReferences();
        
        if(ObjectUtils.isNull(paymentRequest.getPaymentRequestPayDate())) {
            //TODO: do some in depth tests on this
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
        
        //update the amounts on the accounts
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(paymentRequest);
        //refresh account summary
        paymentRequest.refreshAccountSummary();
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
                    
                    SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(paymentRequestDocument);
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
    }
    
    
    
    public void addContinuationAccountsNote(PaymentRequestDocument document, HashMap<String, String> accounts) {
        Note note = new Note();
        String noteText;
        StringBuffer sb = new StringBuffer("");

        // List the entries using entrySet()
        Set entries = accounts.entrySet();
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            
            sb.append(" Account " + entry.getKey() + " has replaces with account " + entry.getValue() + " ; ");
            
        }
        note.setNoteText(sb.toString());
        document.addNote(note);
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
        
        String accountsPayableGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        
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
        
        String accountsPayableSupervisorGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        
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
        String accountsPayableGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        
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
        String accountsPayableSupervisorGroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        
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

    //FIXME: delete from PaymentRequestService(Impl), it is now in CreditMemoCreateService
    public Collection convertMoneyToPercent(PaymentRequestDocument pr) {
        LOG.debug("convertMoneyToPercent() started");
        Collection errors = new ArrayList();
        int itemNbr = 0;

        for (Iterator iter = pr.getItems().iterator(); iter.hasNext();) {
            PaymentRequestItem item = (PaymentRequestItem) iter.next();

            itemNbr++;
            String identifier = item.getItemIdentifierString();

            if (item.getExtendedPrice().isNonZero()) {
                // Collections.sort((List)item.getAccounts());

                KualiDecimal accountTotal = KualiDecimal.ZERO;
                // haven't decided if I'm going to round
                /* PaymentRequestAccount lastAccount = null; */
                int accountIdentifier = 0;
                for (Iterator iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                    accountIdentifier++;
                    PaymentRequestAccount account = (PaymentRequestAccount) iterator.next();
                    KualiDecimal accountAmount = account.getAmount();
                    BigDecimal tmpPercent = BigDecimal.ZERO;
                    KualiDecimal extendedPrice = item.getExtendedPrice();
                    tmpPercent = accountAmount.bigDecimalValue().divide(extendedPrice.bigDecimalValue(),PurapConstants.PRORATION_SCALE.intValue(), KualiDecimal.ROUND_BEHAVIOR);
                    // test that the above amount is correct, if so just check that the total of all these matches the item total

                    KualiDecimal calcAmount = new KualiDecimal(tmpPercent.multiply(extendedPrice.bigDecimalValue()));
                    if (calcAmount.compareTo(accountAmount) != 0) {
                        // rounding error
                        LOG.debug("convertMoneyToPercent() Rounding error on " + account);
                        String param1 = identifier + "." + accountIdentifier;
                        String param2 = calcAmount.bigDecimalValue().subtract(accountAmount.bigDecimalValue()).toString();
                        GlobalVariables.getErrorMap().putError(item.getItemIdentifierString(), PurapKeyConstants.ERROR_ITEM_ACCOUNTING_ROUNDING, param1, param2);
                        PurError se = new PurError("Rounding Error");
                        errors.add(se);
                        // fix
                        account.setAmount(calcAmount);
                    }

                    // update percent
                    LOG.debug("convertMoneyToPercent() updating percent to " + tmpPercent);
                    account.setAccountLinePercent(tmpPercent.multiply(new BigDecimal(100)));

                    // check total based on adjusted amount
                    accountTotal = accountTotal.add(calcAmount);

                }
                if (!accountTotal.equals(item.getExtendedPrice())) {
                    GlobalVariables.getErrorMap().putError(item.getItemIdentifierString(), PurapKeyConstants.ERROR_ITEM_ACCOUNTING_DOLLAR_TOTAL, identifier, accountTotal.toString(), item.getExtendedPrice().toString());
                    PurError se = new PurError("Invalid Totals");
                    errors.add(se);
                }

            }
        }
        return errors;
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

        //FIXME hjs - add call to new method once added
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

        paymentRequestDocument.populatePaymentRequestFromPurchaseOrder(purchaseOrderDocument);

        paymentRequestDocument.getDocumentHeader().setFinancialDocumentDescription( createPreqDocumentDescription(paymentRequestDocument.getPurchaseOrderIdentifier(), paymentRequestDocument.getVendorName() ) );
        
        //If the list of closed/expired accounts is not empty add a warning and add a note for the close / epired accounts which get replaced
        
        //HashMap<String, String> expiredOrClosedAccounts = paymentRequestService.expiredOrClosedAccountsList(paymentRequestDocument);
        //TODO: Chris finish above method for now just set to empty
        HashMap<String, String> expiredOrClosedAccounts = new HashMap<String,String>();
        
        if (!expiredOrClosedAccounts.isEmpty()){
            GlobalVariables.getMessageList().add(PurapKeyConstants.MESSAGE_CLOSED_OR_EXPIRED_ACCOUNTS_REPLACED);
            addContinuationAccountsNote(paymentRequestDocument, expiredOrClosedAccounts);
        }
        //add discount item
        calculateDiscount(paymentRequestDocument);
        //distribute accounts (i.e. proration)
        distributeAccounting(paymentRequestDocument);
        
    }

    private String createPreqDocumentDescription(Integer purchaseOrderIdentifier, String vendorName){
        //KULPURAP-683 - set description to a specific value
        //TODO: can we abstract this any better so the values aren't hardcoded
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
        preq.setStatusCode(PurapConstants.PaymentRequestStatuses.IN_PROCESS);
        SpringContext.getBean(PaymentRequestService.class).populatePaymentRequest(preq);
        documentService.saveDocument(preq);
    }
    
    /*
     public PaymentRequestInitializationValidationErrors verifyPreqInitialization(
     Integer purchaseOrderId, String invoiceNumber, BigDecimal invoiceAmount, Timestamp invoiceDate,
     List expiredAccounts, List closedAccounts, User u) {
     SERVICELOG.debug("verifyPreqInitialization() started");
     LOG.debug("verifyPreqInitialization started");
     List messages = new ArrayList();
     List expirAcctList = new ArrayList();
     List closeAcctList = new ArrayList();
     
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

  /**
   * Retrieve a list of PREQs that aren't approved, check to see if they match specific
   * requirements, then auto-approve them if possible.
   *
   */
    /*
  public void autoApprovePaymentRequests() {
    LOG.debug("autoApprovePaymentRequests() started");
    
    // Get all the payment requests that have the status for Auto Approve
    String[] statuses = EpicConstants.PREQ_STATUSES_FOR_AUTO_APPROVE;
    
    // get ap supervisor user
    User apSupervisor = this.getAccountsPayableSupervisorUser();

    // Get the user that will approve them
    Collection requests = paymentRequestDao.getByStatuses(statuses);

    // default auto approve limit is $5,000
    String defaultLimit = applicationSettingService.getString("AP_AUTO_APPROVE_MAX_AMOUNT");

    for (Iterator ri = requests.iterator(); ri.hasNext();) {
      PaymentRequest preq = (PaymentRequest)ri.next();
      boolean autoApproveExcluded = false;
      LOG.info("autoApprovePaymentRequests() Attempting Auto Approve for PREQ " + preq.getId());
      
      // Only process preq's that aren't held
      if ( preq.getPaymentHoldIndicator().booleanValue() ) {
        LOG.info("autoApprovePaymentRequests() PREQ " + preq.getId() + " is held... will not auto approve");
        continue;
      }
      
      // Only process preq's that aren't requested for cancel
      if ( preq.getPaymentRequestCancelIndicator().booleanValue() ) {
        LOG.info("autoApprovePaymentRequests() PREQ " + preq.getId() + " is requested for cancel... will not auto approve");
        continue;
      }

      // Only process preq's that have a pay date of now or before
      Date now = new Date();
      if ( preq.getPaymentRequestPayDate().getTime() > now.getTime() ) {
        // Don't process it.  It isn't ready to be paid yet
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(preq.getPaymentRequestPayDate().getTime()));
        LOG.info("autoApprovePaymentRequests() PREQ " + preq.getId() + " has pay date " + 
            (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + "-" + c.get(Calendar.YEAR) + "... will not auto approve");
        // we add one above because January's value is 0, February is 1, and so on
        continue;
      }

      // Only process preq's whose vendor is not an exclusion from auto approve in ownership type table
      VendorHeader vh = vendorService.getVendorHeader(preq.getVendorHeaderGeneratedId());
      if ( (vh != null) && (vh.getOwnershipType() != null) &&
           (vh.getOwnershipType().getExcludeFromAutoApprove() != null) &&
           (vh.getOwnershipType().getExcludeFromAutoApprove().booleanValue()) ) {
        LOG.info("autoApprovePaymentRequests() PREQ " + preq.getId() + " has vendor auto approve exclusion set to true in ownership type table... will not auto approve");
        continue;
      }
      
      if (routingService.willDocumentRouteToTaxAsEmployee(preq.getDocumentHeader().getId(),apSupervisor)) {
        LOG.info("autoApprovePaymentRequests() PREQ " + preq.getId() + " is scheduled to route as Employee to Tax Area... will not auto approve");
        continue;
      }
      
      // Orders below the autoApproveAmount will be auto approved
      BigDecimal autoApproveAmount = null;
      
      // Find the lowest auto approve amount in all the accounts
      String chart = null;
      String accountNumber = null;
      for (Iterator iter = preq.getDisplayAccounts().iterator(); iter.hasNext();) {
        DisplayAccount acct = (DisplayAccount)iter.next();

        // Only proces the preq whose chart and account number
        // are not in the maintenance table for Auto Approve Exclusion
        chart = acct.getFinancialChartOfAccountsCode();
        accountNumber = acct.getAccountNumber();

        if ( (acct.getAmount() != null) && (BigDecimal.ZERO.compareTo(acct.getAmount()) < 0) ) {
          AutoApproveExclusion aae = autoApproveExclusionService.getByChartAndAcct(chart, accountNumber);
          // as long as there's at least one account whose chart/acct is in the maint.
          // table, we should break from this inner for-loop and continue with the 
          // next preq.
          if (aae != null && aae.getActive().booleanValue()) {
            autoApproveExcluded = true;
            break;
          }
        }
            
        BigDecimal limit = null;

        // Check chart
        limit = negativePaymentRequestApprovalLimitService.chartApprovalLimit(acct.getFinancialChartOfAccountsCode());
        if ( (limit != null) && (limit.compareTo(autoApproveAmount) < 0) ) {
          autoApproveAmount = limit;
        }

        // Check account
        limit = negativePaymentRequestApprovalLimitService.accountApprovalLimit(acct.getFinancialChartOfAccountsCode(),acct.getAccountNumber());
        if ( (limit != null) && (limit.compareTo(autoApproveAmount) < 0) ) {
          autoApproveAmount = limit;
        }

        // Check account's org
        Account account = chartOfAccountsService.getAccount(acct.getFinancialChartOfAccountsCode(),acct.getAccountNumber());
        if ( account == null ) {
          // if account is non-existant then check to see if any dollars are assigned
          if ( (acct.getAmount() == null) || (BigDecimal.ZERO.compareTo(acct.getAmount()) == 0) ) {
            LOG.info("autoApprovePaymentRequests() PREQ " + preq.getId() + " has invalid account " + acct.getFinancialChartOfAccountsCode() + "/" 
                + acct.getAccountNumber() + " but will skip because account has no money assigned to it");
            continue;
          } else {
            LOG.error("autoApprovePaymentRequests() Invalid account on preq " + acct.getFinancialChartOfAccountsCode() + "/" + acct.getAccountNumber());
            throw new IllegalArgumentException("Invalid account on preq " + acct.getFinancialChartOfAccountsCode() + "/" + acct.getAccountNumber());
          }
        }
        limit = negativePaymentRequestApprovalLimitService.organizationApprovalLimit(acct.getFinancialChartOfAccountsCode(),account.getOrganizationCode());
        if ( (limit != null) && (limit.compareTo(autoApproveAmount) < 0) ) {
          autoApproveAmount = limit;
        }
      }
      
      // if the chart and acct nbr of this preq is in the maintenance table to be
      // excluded from auto approve, then don't do the autoApprovePaymentRequest
      if (autoApproveExcluded) {
        LOG.info("autoApprovePaymentRequests() PREQ " + preq.getId() + " is excluded based on Auto Approve Exclusion settings for Chart-Account combo" +
                chart + "-" + accountNumber + "... will not auto approve");
        continue;
      }
      
      if ( (FormValidation.isStringEmpty(defaultLimit)) && (autoApproveAmount == null) ) {
        LOG.error("autoApprovePaymentRequests() Unable to get AP Auto Approve Max Amount and none set for PREQ " + preq.getId());
        throw new InternalError("Unable to get AP Auto Approve Max Amount");
      }
      BigDecimal testAutoApproveAmount = new BigDecimal(defaultLimit);
      if ( autoApproveAmount != null ) {
        testAutoApproveAmount = autoApproveAmount;
      }
      if ( preq.getTotalCost().compareTo(testAutoApproveAmount) < 0 ) {
        String env = environmentService.getEnvironment();
        if (EnvironmentService.PRODUCTION_ENVIRONMENT.equals(env)) {
          LOG.debug("autoApprovePaymentRequests() auto approving PREQ with ID " + preq.getId() + " and status " + preq.getStatus().getCode());
          routingService.autoApprovePaymentRequest(preq,apSupervisor);
        } else {
          LOG.debug("autoApprovePaymentRequests() if not final approved... auto approving PREQ with ID " + preq.getId() + " and status " + preq.getStatus().getCode());
          routingService.autoApprovePaymentRequestSkipPrevious(preq,apSupervisor);
        }
      } else {
        LOG.info("autoApprovePaymentRequests() PREQ " + preq.getId() + " has total cost " + preq.getTotalCost().doubleValue() + 
            " and is not less than the auto approve max limit of " + testAutoApproveAmount.doubleValue() + "... will not auto approve");
      }
    }
  }

     */
    
    
}
