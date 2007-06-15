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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.NoteService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.GeneralLedgerService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.vendor.bo.PaymentTermType;
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
    private PurchaseOrderService purchaseOrderService;
    private UniversalUserService universalUserService;
    private KualiConfigurationService kualiConfigurationService;
    private KualiRuleService kualiRuleService;

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
     * Sets the kualiRuleService attribute value.
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
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

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
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
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();


        Integer purchaseOrderId = document.getPurchaseOrderIdentifier();

        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderId);

        Integer vendorDetailAssignedId = po.getVendorDetailAssignedIdentifier();
        Integer vendorHeaderGeneratedId = po.getVendorHeaderGeneratedIdentifier();

        List<PaymentRequestDocument> preqs = getPaymentRequestsByVendorNumberInvoiceNumber(vendorHeaderGeneratedId, vendorDetailAssignedId, document.getInvoiceNumber());
        if (preqs.size() > 0) {
            boolean addedMessage = false;
            List cancelled = new ArrayList();
            List voided = new ArrayList();
            for (PaymentRequestDocument testPREQ : preqs) {
                if (StringUtils.equals(testPREQ.getStatusCode(),PaymentRequestStatuses.CANCELLED_POST_APPROVE)) {
                    cancelled.add(testPREQ);
                }
                else if (StringUtils.equals(testPREQ.getStatusCode(),PaymentRequestStatuses.CANCELLED_IN_PROCESS)) {
                    voided.add(testPREQ);
                }
                else {
                    msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE));
                    addedMessage = true;
                    break;
                }
            }
            // Custom error message for duplicates related to cancelled/voided PREQs
            if (!addedMessage) {
                if ((!(voided.isEmpty())) && (!(cancelled.isEmpty()))) {
                    msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_CANCELLEDORVOIDED));
                }
                else if ((!(voided.isEmpty())) && (cancelled.isEmpty())) {
                    msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_VOIDED));
                }
                else if ((voided.isEmpty()) && (!(cancelled.isEmpty()))) {
                    // messages.add("errors.duplicate.vendor.invoice.cancelled");
                    msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_CANCELLED));
                }
            }
        }

        // Check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
        preqs = getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(purchaseOrderId, document.getVendorInvoiceAmount(), document.getInvoiceDate());
        if (preqs.size() > 0) {
            boolean addedMessage = false;
            List cancelled = new ArrayList();
            List voided = new ArrayList();
            msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT));
            for (PaymentRequestDocument testPREQ : preqs) {
                if (StringUtils.equalsIgnoreCase(testPREQ.getStatusCode(),PaymentRequestStatuses.CANCELLED_POST_APPROVE)) {
                    cancelled.add(testPREQ);
                }
                else if (StringUtils.equalsIgnoreCase(testPREQ.getStatusCode(),PaymentRequestStatuses.CANCELLED_IN_PROCESS)) {
                    voided.add(testPREQ);
                }
                else {
                    msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT));
                    addedMessage = true;
                    break;
                }
            }
            // Custom error message for duplicates related to cancelled/voided PREQs
            if (!addedMessage) {

                if ((!(voided.isEmpty())) && (!(cancelled.isEmpty()))) {
                    msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLEDORVOIDED));
                }
                else if ((!(voided.isEmpty())) && (cancelled.isEmpty())) {
                    msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_VOIDED));
                    addedMessage = true;

                }
                else if ((voided.isEmpty()) && (!(cancelled.isEmpty()))) {
                    msgs.put(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLED));
                    addedMessage = true;
                }
            }
        }


        return msgs;
    }
    
    public PaymentRequestDocument getPaymentRequestById(Integer poDocId){
        return paymentRequestDao.getPaymentRequestById(poDocId); 
    }

    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId) {
        return paymentRequestDao.getPaymentRequestsByPurchaseOrderId(poDocId);
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
        return ((invoiceDateTime.compareTo(nowTime)) > 0);
    }


    public HashMap<String, String> expiredOrClosedAccountsList(PaymentRequestDocument document) {

        HashMap<String, String> list = new HashMap<String, String>();
        Integer POID = document.getPurchaseOrderIdentifier();
        java.util.Date currentDate = SpringServiceLocator.getDateTimeService().getCurrentDate();
        
        //TODO: Why not rely on ojb here?  You should have a po already from the mapping Actually it looks like the mapping isn't correct.  I'll fix this later
        PurchaseOrderDocument po = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(document.getPurchaseOrderIdentifier());
        
        //TODO: check for for po not be null
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
            } else if (account.isExpired() & SpringServiceLocator.getDateTimeService().dateDiff(account.getAccountExpirationDate(), SpringServiceLocator.getDateTimeService().getCurrentDate(), true) > 30) {
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
    private Date calculatePayDate(Date invoiceDate,PaymentTermType terms) {
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
      if ("".equals(terms.getVendorPaymentTermsCode())) {
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
        
        if(paymentRequest.getPaymentRequestPayDate()==null) {
            //TODO: do some in depth tests on this
            paymentRequest.setPaymentRequestPayDate(calculatePayDate(paymentRequest.getInvoiceDate(),paymentRequest.getVendorPaymentTerms()));
        }
        
        for (PaymentRequestItem item : (List<PaymentRequestItem>)paymentRequest.getItems()) {
            if(item.getItemType().isItemTypeAboveTheLineIndicator() &&
                !item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                PurchaseOrderItem poi = (PurchaseOrderItem)paymentRequest.getPurchaseOrderDocument().getItem(item.getItemLineNumber().intValue());
                item.setItemUnitPrice(new BigDecimal(item.getItemExtendedPrice().toString()));
            }
        }
        
        if(updateDiscount) {
            //TODO: still have to merge this with other changes I did for discount
            //calculateDiscount(paymentRequest);
        }
        
        //TODO Chris merge in distribute accounts code and call here
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
        
        //retrieve and save with hold indicator set to true
        PaymentRequestDocument preqDoc = paymentRequestDao.getPaymentRequestById(document.getPurapDocumentIdentifier());        
        preqDoc.setHoldIndicator(true);
        preqDoc.setAccountsPayableHoldIdentifier( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
        paymentRequestDao.save(preqDoc);
        
        //must also save it on the incoming document
        document.setHoldIndicator(true);
        document.setAccountsPayableHoldIdentifier( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
    }
    
    /**
     * This method removes a hold on a payment request.
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#removeHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public void removeHoldOnPaymentRequest(PaymentRequestDocument document){
        //retrieve and save with hold indicator set to false
        PaymentRequestDocument preqDoc = paymentRequestDao.getPaymentRequestById(document.getPurapDocumentIdentifier());                    
        preqDoc.setHoldIndicator(false);
        preqDoc.setAccountsPayableHoldIdentifier(null);
        paymentRequestDao.save(preqDoc);

        //must also save it on the incoming document
        document.setHoldIndicator(false);
        document.setAccountsPayableHoldIdentifier(null);
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
            ( PurapConstants.PaymentRequestStatuses.AP_APPROVED.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_APPROVAL.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.AWAITING_FISCAL_APPROVAL.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.AUTO_APPROVED.equals(statusCode) ) ){
            
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
        
        String accountsPayableGroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        
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
        
        String accountsPayableSupervisorGroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        
        /* The user is the person who put the preq on hold
         * The user is a member of the AP Supervisor group
         */
        if( document.isHoldIndicator() &&
            ( user.getPersonUniversalIdentifier().equals( document.getAccountsPayableHoldIdentifier() ) ||
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
        PaymentRequestDocument preqDoc = paymentRequestDao.getPaymentRequestById(document.getPurapDocumentIdentifier());        
        preqDoc.setPaymentRequestedCancelIndicator(true);
        preqDoc.setAccountsPayableRequestCancelIdentifier( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
        paymentRequestDao.save(preqDoc);
        
        //must also save it on the incoming document
        document.setPaymentRequestedCancelIndicator(true);
        document.setAccountsPayableRequestCancelIdentifier( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
    }
    
    /**
     * This method removes a cancel on a payment request.
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#removeHoldOnPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public void removeRequestCancelOnPaymentRequest(PaymentRequestDocument document, String note) throws Exception{
        //retrieve and save with hold indicator set to false
        PaymentRequestDocument preqDoc = paymentRequestDao.getPaymentRequestById(document.getPurapDocumentIdentifier());                    
        preqDoc.setPaymentRequestedCancelIndicator(false);
        preqDoc.setAccountsPayableRequestCancelIdentifier(null);
        paymentRequestDao.save(preqDoc);

        //must also save it on the incoming document
        document.setPaymentRequestedCancelIndicator(false);
        document.setAccountsPayableRequestCancelIdentifier(null);
    }   

    /**
     * This method determines if this document may have a cancel requested
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#isPaymentRequestHoldable(org.kuali.module.purap.document.PaymentRequestDocument)
     */
    public boolean canRequestCancelOnPaymentRequest(PaymentRequestDocument document){
        boolean canCancel = false;
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
            ( PurapConstants.PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_APPROVAL.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.AWAITING_FISCAL_APPROVAL.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.AWAITING_CHART_REVIEW.equals(statusCode) ||
              PurapConstants.PaymentRequestStatuses.AWAITING_TAX_APPROVAL.equals(statusCode)) ){
            
            canCancel = true;
        }
        
        return canCancel;
    }
    
    /**
     * This method determines if a user has permission to request cancel on a PREQ
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#canHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canUserRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user){
        boolean canHold = false;
        
        String accountsPayableGroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        
        /* The user is an approver of the document,
         * The user is a member of the Accounts Payable group
         */
        if( document.getPaymentRequestedCancelIndicator() == false &&
             (document.getDocumentHeader().hasWorkflowDocument() &&
             ( document.getDocumentHeader().getWorkflowDocument().stateIsEnroute() && 
               document.getDocumentHeader().getWorkflowDocument().isApprovalRequested() ) ) ||
            ( user.isMember(accountsPayableGroup)) ){
            
            canHold = true;
        }
        
        return canHold;
    }

    /**
     * This method determines if a user has permission to remove a request for cancel on a PREQ
     * 
     * @see org.kuali.module.purap.service.PaymentRequestService#canRemoveHoldPaymentRequest(org.kuali.module.purap.document.PaymentRequestDocument, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canUserRemoveRequestCancelOnPaymentRequest(PaymentRequestDocument document, UniversalUser user){
        boolean canRemoveHold = false;
        
        String accountsPayableSupervisorGroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        
        /* The user is the person who requested a cancel on the preq
         * The user is a member of the AP Supervisor group
         */
        if( document.getPaymentRequestedCancelIndicator() &&
            ( user.getPersonUniversalIdentifier().equals( document.getAccountsPayableHoldIdentifier() ) ||
             (user.isMember(accountsPayableSupervisorGroup))) ){
            
            canRemoveHold = true;
        }
        return canRemoveHold;
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
