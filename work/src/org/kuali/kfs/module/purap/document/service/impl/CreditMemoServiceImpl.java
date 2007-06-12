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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.dao.CreditMemoDao;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.service.GeneralLedgerService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.module.vendor.util.VendorUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class CreditMemoServiceImpl implements CreditMemoService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private NoteService noteService;
    private GeneralLedgerService generalLedgerService;
    private PurapService purapService;
    private CreditMemoDao creditMemoDao;
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

    public void setCreditMemoDao(CreditMemoDao creditMemoDao) {
        this.creditMemoDao = creditMemoDao;
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
/*
    private List getPaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId, String invoiceNumber) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        return creditMemoDao.getActivePaymentRequestsByVendorNumberInvoiceNumber(vendorHeaderGeneratedId, vendorDetailAssignedId, invoiceNumber);
    }
*/
    public HashMap<String, String> creditMemoDuplicateMessages(CreditMemoDocument cm) {
        HashMap<String, String> msgs;
        msgs = new HashMap<String, String>();
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();
        UniversalUser currentUser = (UniversalUser) GlobalVariables.getUserSession().getUniversalUser();


        Integer purchaseOrderId = cm.getPurchaseOrderIdentifier();

        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderId);

        // This line is for test only to simulate duplicate messages, need to remove after Code review
        //msgs.put(PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE));

        Integer vendorDetailAssignedId = cm.getVendorDetailAssignedIdentifier();
        Integer vendorHeaderGeneratedId = cm.getVendorHeaderGeneratedIdentifier();

        // Check vendorNumber/creditMemoNumber in the database.
        // If already exists, give a duplication warning
        String vn = cm.getVendorNumber();
        if (StringUtils.isNotEmpty(cm.getVendorNumber())) {
            if (duplicateExists(cm.getVendorNumber(), cm.getCreditMemoNumber())) {
                msgs.put(PurapConstants.CMDocumentsStrings.DUPLICATE_CREDIT_MEMO_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER));
            }

            // Check credit memo date/credit memo amount for a vendor in the database.
            // If already exists, give a duplication error
            if (duplicateExists(cm.getVendorNumber(), cm.getCreditMemoDate(), cm.getCreditMemoAmount())) {
                msgs.put(PurapConstants.CMDocumentsStrings.DUPLICATE_CREDIT_MEMO_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER_DATE_AMOUNT));
            }
        }

        return msgs;
    }


    /*
    public List<CreditMemoDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId) {
        return creditMemoDao.getPaymentRequestsByPurchaseOrderId(poDocId);
    }

    public void save(CreditMemoDocument creditMemoDocument) {

        // Integer poId = creditMemoDocument.getPurchaseOrderIdentifier();
        // PurchaseOrderDocument purchaseOrderDocument = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(creditMemoDocument.getPurchaseOrderIdentifier());
        // creditMemoDocument.populatePaymentRequestFormPurchaseOrder(purchaseOrderDocument);

        creditMemoDao.save(creditMemoDocument);
    }
*/
    /**
     * Retreives a list of Pay Reqs with the given PO Id, invoice amount, and invoice date.
     * 
     * @param poId           purchase order ID
     * @param invoiceAmount  amount of the invoice as entered by AP
     * @param invoiceDate    date of the invoice as entered by AP
     * @return List of Pay Reqs.
     */
  /*
    public List getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate) {
        LOG.debug("getPaymentRequestsByPOIdInvoiceAmountInvoiceDate() started");
        return creditMemoDao.getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(poId, invoiceAmount, invoiceDate);
    }

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
     * This method is a shallow wrapper around the DAO implementation by the same
     * name. The only substantial difference at this point is that the service
     * takes a composite vendorNumber, extracts the parts of the vendor number,
     * and passes it to the DAO impl.
     * 
     * @param vendorNumber - composite two-part vendor number (headerId-detailId)
     * @param creditMemoNumber - vendor supplied credit memo number
     * @return boolean - true if a matching credit memo exists in the db, false if not
     */
    public boolean duplicateExists(String vendorNumber, String creditMemoNumber) {
        LOG.debug("duplicateExists() started");
      Integer vendorNumberHeaderId;
      Integer vendorNumberDetailId;

      vendorNumberHeaderId = VendorUtils.getVendorHeaderId(vendorNumber);
      vendorNumberDetailId = VendorUtils.getVendorDetailId(vendorNumber);
      LOG.debug("duplicateExists() ended");
      return duplicateExists(vendorNumberHeaderId, vendorNumberDetailId,
          creditMemoNumber);
    }

    /**
     * 
     * This method is a shallow wrapper around the DAO implementation by the same
     * name.
     * 
     * @param vendorNumberHeaderId
     * @param vendorNumberDetailId
     * @param creditMemoNumber
     * @return
     */
    public boolean duplicateExists(Integer vendorNumberHeaderId,
        Integer vendorNumberDetailId, String creditMemoNumber) {
      LOG.debug("duplicateExists() started");
      LOG.debug("duplicateExists() ended");
        return creditMemoDao.duplicateExists(vendorNumberHeaderId,
          vendorNumberDetailId, creditMemoNumber);
    }

    /**
     * 
     * This method is a shallow wrapper around the service method, except with a
     * composite vendorNumber
     * 
     * @param vendorNumber
     * @param date
     * @param amount
     * @return
     */
    public boolean duplicateExists(String vendorNumber, Date date, KualiDecimal amount) {

      LOG.debug("duplicateExists() started");
        Integer vendorNumberHeaderId;
      Integer vendorNumberDetailId;

      vendorNumberHeaderId = VendorUtils.getVendorHeaderId(vendorNumber);
      vendorNumberDetailId = VendorUtils.getVendorDetailId(vendorNumber);
      LOG.debug("duplicateExists() ended");
      return creditMemoDao.duplicateExists(vendorNumberHeaderId,
          vendorNumberDetailId, date, amount);
    }

    /**
     * 
     * This method is a shallow wrapper around the DAO implementation by the same
     * name and signature.
     * 
     * @param vendorNumberHeaderId
     * @param vendorNumberDetailId
     * @param date
     * @param amount
     * @return
     */
    public boolean duplicateExists(Integer vendorNumberHeaderId,
        Integer vendorNumberDetailId, Date date, KualiDecimal amount) {
      LOG.debug("duplicateExists() started");
      LOG.debug("duplicateExists() ended");
        return creditMemoDao.duplicateExists(vendorNumberHeaderId,
          vendorNumberDetailId, date, amount);
    }

    /**
     * Get all credit Memo Documents associated with this Req ID
     * 
     * @param requistionId
     *          ID of the Req associated with the Credit Memo Documents
     * @return
     */
 /*
    public List getCreditMemoDocumentsByReqID(Integer requisitionId, User user) {
      LOG.debug("getCreditMemoDocumentsByReqID() started");
      LOG.debug("getCreditMemoDocumentsByReqID() started");
      List l = creditMemoDao.getCreditMemosByRequisitionId(requisitionId);
      List creditMemoDocuments = new ArrayList();

      for (Iterator iter = l.iterator(); iter.hasNext();) {
        CreditMemo cm = (CreditMemo) iter.next();
        if (cm != null) {
          CreditMemoDocument cmd = new CreditMemoDocument();
          cmd.setCreditMemo(cm);

          cmd.setOnbaseImageExists(onbaseService.isCMImageAvailable(cmd.getCreditMemo().getId()));
          cmd.setCreditMemoImageViewer(fiscalAccountingService.isCreditMemoDelegate(cmd.getCreditMemo(), user));
          creditMemoDocuments.add(cmd);
        }
      }
      LOG.debug("getCreditMemoDocumentsByReqID() ended");
      return creditMemoDocuments;
    }
*/
    /**
     * Get all credit Memo Documents associated with this PO ID
     * 
     * @param poID  ID of the PO associated with the Credit Memo Documents
     * @return
     */
 /*
    public List getCreditMemoDocumentsByPOID(Integer poID, User user) {
      LOG.debug("getCreditMemoDocumentsByPOID() started");
      LOG.debug("getCreditMemoDocumentsByPOID() started");
      List l = creditMemoDao.getCreditMemosByPOId(poID);
      List creditMemoDocuments = new ArrayList();

      for (Iterator iter = l.iterator(); iter.hasNext();) {
        CreditMemo cm = (CreditMemo) iter.next();
        if (cm != null) {
          CreditMemoDocument cmd = new CreditMemoDocument();
          cmd.setCreditMemo(cm);

          cmd.setOnbaseImageExists(onbaseService.isCMImageAvailable(cmd.getCreditMemo().getId()));
          cmd.setCreditMemoImageViewer(fiscalAccountingService.isCreditMemoDelegate(cmd.getCreditMemo(), user));
          creditMemoDocuments.add(cmd);
        }
      }
      LOG.debug("getCreditMemoDocumentsByPOID() ended");
      return creditMemoDocuments;
    }
  */  
    /**
     * Get all credit Memo associated with this PO ID
     * 
     * @param poID  ID of the PO associated with the Credit Memo 
     * @return
     */
    public List getCreditMemosByPOID(Integer poID) {
      LOG.debug("getCreditMemosByPOID() started");
      LOG.debug("getCreditMemosByPOID() started");
      LOG.debug("getCreditMemosByPOID() ended");
      return this.getCreditMemosByPOID(poID, null);
    }
    
    /**
     * Get all credit Memo associated with this PO ID
     * 
     * @param poID  ID of the PO associated with the Credit Memo 
     * @return
     */
    public List getCreditMemosByPOID(Integer poID, Integer returnListMax) {
      LOG.debug("getCreditMemosByPOID(Integer) started");
      LOG.debug("getCreditMemosByPOID(Integer) started");
      List cms = new ArrayList();
      if (returnListMax == null) {
        cms = creditMemoDao.getCreditMemosByPOId(poID);
      } else {
        cms = creditMemoDao.getCreditMemosByPOId(poID,returnListMax);
      }
      LOG.debug("getCreditMemosByPOID(Integer) ended");
      return cms;
    }
    
    public List getAllCMsByPOIdAndStatus(Integer purchaseOrderID,Collection statusCodes) {
      LOG.debug("getAllCMsByPOIdAndStatus() started");
      LOG.debug("getAllCMsByPOIdAndStatus() started");
      LOG.debug("getAllCMsByPOIdAndStatus() ended");
      return creditMemoDao.getAllCMsByPOIdAndStatus(purchaseOrderID,statusCodes);
    }

    /**
     * get Credit Memo Document
     * @param id
     * @param user
     * @return
     */
   /*
    public CreditMemoDocument getCreditMemoDocumentByID(Integer id, User user) {
          LOG.debug("getCreditMemoDocumentsByReqID() started");
          LOG.debug("getCreditMemoDocumentByID() started");
          CreditMemo cm = creditMemoDao.getCreditMemoById(id);
          
          if(cm == null) {
              LOG.debug("getCreditMemoDocumentByID() Unable to find CreditMemo for id = "+id);
              LOG.debug("getCreditMemoDocumentByID() ended");
              return null;
          }
          CreditMemoDocument cmd = new CreditMemoDocument();
          cmd.setCreditMemo(cm);
          cmd.setOnbaseImageExists(onbaseService.isCMImageAvailable(cmd
                  .getCreditMemo().getId()));
          
          cmd.setCreditMemoImageViewer(fiscalAccountingService
                  .isCreditMemoDelegate(cmd.getCreditMemo(), user));
          
          cmd.setWorkflowDocument(routingService.getWorkflowDocument(cm.getDocumentHeader().getId(), user));
          LOG.debug("getCreditMemoDocumentByID() ended");
          return cmd;
      }
*/
}
