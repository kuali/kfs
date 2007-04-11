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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.NoteService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
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
    
    private UniversalUserService universalUserService;
    
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
   
    /* Start Paste */
    
    public void setUserService(UniversalUserService us) {
        universalUserService = us;
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
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
      this.purchaseOrderService = purchaseOrderService;
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
    
   
    public HashMap<String, String> paymentRequestDuplicateMessages(PaymentRequestDocument document) {
        HashMap<String, String> msgs;
        msgs = new HashMap<String, String>();
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
        Integer POID = document.getPurchaseOrderIdentifier();
        // List<PaymentRequestDocument> preqs =
        // SpringServiceLocator.getPaymentRequestService().getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(POID,
        // document.getVendorInvoiceAmount(), document.getInvoiceDate());
        List<PaymentRequestDocument> preqs = getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(POID, document.getVendorInvoiceAmount(), document.getInvoiceDate());
        POID = document.getPurchaseOrderIdentifier();

        // This line is for test only to simulate duplicate messages, need to remove after Code review
        msgs.put(PurapConstants.PREQDocumentsStrings.DUPLICATE_DATE_AMONT_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT));

        if (preqs.size() > 0) {
            boolean addedMessage = false;
            List cancelled = new ArrayList();
            List voided = new ArrayList();
            msgs.put(PurapConstants.PREQDocumentsStrings.DUPLICATE_DATE_AMONT_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT));
            for (Iterator iter = preqs.iterator(); iter.hasNext();) {
                PaymentRequestDocument testPREQ = (PaymentRequestDocument) iter.next();
                if ((!(PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getStatusCode()))) && (!(PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getStatusCode())))) {
                    msgs.put(PurapConstants.PREQDocumentsStrings.DUPLICATE_DATE_AMONT_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT));
                    addedMessage = true;

                    break;
                }
                else if (PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getStatusCode())) {
                    voided.add(testPREQ);
                }
                else if (PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getStatusCode())) {
                    cancelled.add(testPREQ);
                }
            }
            // custom error message for duplicates related to cancelled/voided PREQs
            if (!addedMessage) {
                // if (valid) {
                if ((!(voided.isEmpty())) && (!(cancelled.isEmpty()))) {
                    // messages.add("errors.duplicate.invoice.date.amount.cancelledOrVoided");
                    msgs.put(PurapConstants.PREQDocumentsStrings.DUPLICATE_DATE_AMONT_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT_CANCELLEDORVOIDED));

                }
                else if ((!(voided.isEmpty())) && (cancelled.isEmpty())) {
                    // messages.add("errors.duplicate.invoice.date.amount.voided");
                    msgs.put(PurapConstants.PREQDocumentsStrings.DUPLICATE_DATE_AMONT_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT_VOIDED));
                    addedMessage = true;
                    // valid &= false;
                }
                else if ((voided.isEmpty()) && (!(cancelled.isEmpty()))) {
                    // messages.add("errors.duplicate.invoice.date.amount.cancelled");
                    msgs.put(PurapConstants.PREQDocumentsStrings.DUPLICATE_DATE_AMONT_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_PREQ_DATE_AMOUNT_CANCELLED));
                    addedMessage = true;
                }
            }
        }


        return msgs;
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
        return ( (invoiceDateTime.compareTo(nowTime)) > 0 );
      }
 
    
      /**
   * @param purchaseOrderId
   * @param invoiceNumber
   * @param invoiceAmount
   * @param invoiceDate
   * @param expiredAccounts
   * @param closedAccounts
   * @return
   */
  
    /*
    public PaymentRequestInitializationValidationErrors verifyPreqInitialization(
      Integer purchaseOrderId, String invoiceNumber, BigDecimal invoiceAmount, Timestamp invoiceDate,
      List expiredAccounts, List closedAccounts, User u) {
    SERVICELOG.debug("verifyPreqInitialization() started");
    LOG.debug("verifyPreqInitialization started");
    List messages = new ArrayList();
    List expirAcctList = new ArrayList();
    List closeAcctList = new ArrayList();
    
    PaymentRequestInitializationValidationErrors initValidationErrors = new PaymentRequestInitializationValidationErrors();

    PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(purchaseOrderId,u);
    
    if (po == null) {
      // no PO was found in the system - notify the user
      messages.add("errors.po.not.exist");
      initValidationErrors.errorMessages = messages;
      SERVICELOG.debug("verifyPreqInitialization() ended");    
      return initValidationErrors;
    }
    
    // Verify that there exists at least 1 item left to be invoiced
    initValidationErrors.setPurchaseOrderNumberToUse(purchaseOrderId);
    boolean zeroDollar = true;
    for (Iterator itemIter = po.getItems().iterator(); itemIter.hasNext();) {
      PurchaseOrderItem poi = (PurchaseOrderItem) itemIter.next();
      BigDecimal encumberedQuantity = poi.getOutstandingEncumberedQuantity() == null ? zero : poi.getOutstandingEncumberedQuantity();
      if (encumberedQuantity.compareTo(zero) == 1) {
        zeroDollar = false;
        break;
      }
    }
    
    // if messages exist now there is no need to check anything else
    if (!messages.isEmpty()) {
      initValidationErrors.errorMessages = messages;
      SERVICELOG.debug("verifyPreqInitialization() ended");
      return initValidationErrors;
    }
    
    // Check invoice date to make sure it is today or before
    if (this.isInvoiceDateAfterToday(invoiceDate)) {
      messages.add("errors.invalid.invoice.date");
    }
//    Timestamp now = new Timestamp( (new Date()).getTime() );
//    if ( invoiceDate.getTime() > now.getTime() ) {
//      messages.add("errors.invalid.invoice.date");
//    }
    
    if (EpicConstants.PO_STAT_OPEN.equals(po.getPurchaseOrderStatus().getCode())) {
      //then check that there are no other non-cancelled PREQs for this vendor number and invoice number
      Integer vendorDetailAssignedId = po.getVendorDetailAssignedId();
      Integer vendorHeaderGeneratedId = po.getVendorHeaderGeneratedId();

      List preqs = getPaymentRequestsByVendorNumberInvoiceNumber(vendorHeaderGeneratedId,vendorDetailAssignedId,invoiceNumber);
      if (preqs.size() > 0) {
        boolean addedError = false;
        List cancelled = new ArrayList();
        List voided = new ArrayList();
        for (Iterator iter = preqs.iterator(); iter.hasNext();) {
          PaymentRequest testPREQ = (PaymentRequest) iter.next();
          if ( (!(EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode()))) && 
               (!(EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode()))) ) {
            messages.add("errors.duplicate.vendor.invoice");
            addedError = true;
            break;
          } else if (EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode())) {
            voided.add(testPREQ);
          } else if (EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode())) {
            cancelled.add(testPREQ);
          }
        }
        // custom error message for duplicates related to cancelled/voided PREQs
        if (!addedError) {
          if ( (!(voided.isEmpty())) && (!(cancelled.isEmpty())) ) {
            messages.add("errors.duplicate.vendor.invoice.cancelledOrVoided");
          } else if ( (!(voided.isEmpty())) && (cancelled.isEmpty()) ) {
            messages.add("errors.duplicate.vendor.invoice.voided");
          } else if ( (voided.isEmpty()) && (!(cancelled.isEmpty())) ) {
            messages.add("errors.duplicate.vendor.invoice.cancelled");
          }
        }
      }
      
      //check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
      preqs = getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(po.getId(), invoiceAmount, invoiceDate);
      if (preqs.size() > 0) {
        boolean addedError = false;
        List cancelled = new ArrayList();
        List voided = new ArrayList();
        for (Iterator iter = preqs.iterator(); iter.hasNext();) {
          PaymentRequest testPREQ = (PaymentRequest) iter.next();
          if ( (!(EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode()))) && 
               (!(EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode()))) ) {
            messages.add("errors.duplicate.invoice.date.amount");
            addedError = true;
            break;
          } else if (EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode())) {
            voided.add(testPREQ);
          } else if (EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode())) {
            cancelled.add(testPREQ);
          }
        }
        // custom error message for duplicates related to cancelled/voided PREQs
        if (!addedError) {
          if ( (!(voided.isEmpty())) && (!(cancelled.isEmpty())) ) {
            messages.add("errors.duplicate.invoice.date.amount.cancelledOrVoided");
          } else if ( (!(voided.isEmpty())) && (cancelled.isEmpty()) ) {
            messages.add("errors.duplicate.invoice.date.amount.voided");
          } else if ( (voided.isEmpty()) && (!(cancelled.isEmpty())) ) {
            messages.add("errors.duplicate.invoice.date.amount.cancelled");
          }
        }
      }
      
      this.checkForExpiredOrClosedAccounts(po, initValidationErrors, closedAccounts, expiredAccounts);
      
    } else if (EpicConstants.PO_STAT_PAYMENT_HOLD.equals(po.getPurchaseOrderStatus().getCode())) {
      //PO is not open - notify the user
      messages.add("errors.po.status.hold");
    } else {
      //PO is not open - notify the user
      messages.add("errors.po.not.open");
    }

    if ( 1 == 2 ) {
      // TODO 2006: delyea PREQ CLOSE PO: Add code to return encumberances check for auto close of PO
      initValidationErrors.setCanAutoClosePO(false);
      //initValidationErrors.setCanAutoClosePO(generalLedgerService.isPOAutoCloseEligible(prd.getPreq()) && (prd.getPreq().getPurchaseOrder().getRecurringPaymentType() == null));
    }

    initValidationErrors.errorMessages = messages;
    SERVICELOG.debug("verifyPreqInitialization() ended");    
    return initValidationErrors;
  }
  */
  
  
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
