/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.LockboxService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.dataaccess.LockboxDao;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 *  
 * Lockbox Iterators are sorted by processedInvoiceDate and batchSequenceNumber. 
 * Potentially there could be many batches on the same date. 
 * For each set of records with the same processedInvoiceDate and batchSequenceNumber, 
 * there will be one Cash-Control document. Each record within this set will create one Application document.
 * 
 */

@Transactional
public class LockboxServiceImpl implements LockboxService {

    public LockboxDao lockboxDao;
    private static Logger LOG = org.apache.log4j.Logger.getLogger(LockboxServiceImpl.class);;
    private DocumentService documentService;
    private SystemInformationService systemInformationService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private CashControlDocumentService cashControlDocumentService;
    private PaymentApplicationDocumentService paymentApplicationDocumentService;
    private PersonService<PersonImpl> personService;
    private DataDictionaryService dataDictionaryService;
    
    public CashControlDocumentService getCashControlDocumentService() {
        return cashControlDocumentService;
    }

    public void setCashControlDocumentService(CashControlDocumentService cashControlDocumentService) {
        this.cashControlDocumentService = cashControlDocumentService;
    }

    public boolean processLockbox() throws WorkflowException {

        Iterator<Lockbox> itr = lockboxDao.getAllLockboxes();
        Lockbox ctrlLockbox = new Lockbox();
        CashControlDocument cashControlDocument = new CashControlDocument();
        while (itr.hasNext()) {
            Lockbox lockbox = (Lockbox)itr.next();
            LOG.info("LOCKBOX: '" + lockbox.getLockboxNumber() + "'");

            SystemInformation sysInfo = systemInformationService.getByLockboxNumberForCurrentFiscalYear(lockbox.getLockboxNumber());
            String initiator = sysInfo.getFinancialDocumentInitiatorIdentifier();
            LOG.info("Using SystemInformation: '" + sysInfo.toString() + "'");
            LOG.info("Using Financial Document Initiator PrincipalID: '" + initiator + "'");
            
            //  this will throw obviously if the user isnt setup in the system
            Person person = personService.getPerson(initiator);
            if (person == null) {
                LOG.error("Financial Document Initiator ID [" + initiator + "] specified in SystemInformation [" + sysInfo.toString() + "] for Lockbox Number " + lockbox.getLockboxNumber() + " is not present in the system.");
                throw new RuntimeException("Financial Document Initiator ID [" + initiator + "] specified in SystemInformation [" + sysInfo.toString() + "] for Lockbox Number " + lockbox.getLockboxNumber() + " is not present in the system.");
            }
            
            GlobalVariables.clear();
            GlobalVariables.setUserSession(new UserSession(person.getPrincipalName()));

            if (lockbox.compareTo(ctrlLockbox) != 0) {
                // If we made it in here, then we have hit a different batchSequenceNumber and processedInvoiceDate.
                // When this is the case, we create a new cashcontroldocument and start tacking subsequent lockboxes on 
                // to the current cashcontroldocument as cashcontroldetails.
                LOG.info("New Lockbox batch");

                cashControlDocument = (CashControlDocument)documentService.getNewDocument(dataDictionaryService.getDocumentTypeNameByClass(CashControlDocument.class));
                cashControlDocument.setCustomerPaymentMediumCode(lockbox.getCustomerPaymentMediumCode());
                
                if(ObjectUtils.isNotNull(lockbox.getBankCode())) {
                    String bankCode = lockbox.getBankCode();
                    cashControlDocument.setBankCode(bankCode);
                } 
                
                cashControlDocument.getDocumentHeader().setDocumentDescription(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION + lockbox.getLockboxNumber());

                AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
                accountsReceivableDocumentHeader.setDocumentNumber(cashControlDocument.getDocumentNumber());
                accountsReceivableDocumentHeader.setCustomerNumber(lockbox.getCustomerNumber());
                accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(sysInfo.getProcessingChartOfAccountCode());
                accountsReceivableDocumentHeader.setProcessingOrganizationCode(sysInfo.getProcessingOrganizationCode());

                cashControlDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
            } 
            // set our control lockbox as the current lockbox and create details.
            ctrlLockbox = lockbox;

            CashControlDetail detail = new CashControlDetail();
            detail.setCustomerNumber(lockbox.getCustomerNumber());
            detail.setFinancialDocumentLineAmount(lockbox.getInvoicePaidOrAppliedAmount());
            detail.setCustomerPaymentDate(lockbox.getProcessedInvoiceDate());
            detail.setCustomerPaymentDescription("Lockbox Remittance  " +lockbox.getFinancialDocumentReferenceInvoiceNumber());

            getCashControlDocumentService().addNewCashControlDetail(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION, cashControlDocument, detail);

            String invoiceNumber = lockbox.getFinancialDocumentReferenceInvoiceNumber();
           
            // This is just a temporary work around due to the invoice numbers being in FIS format still.
            try {
                Integer.parseInt(invoiceNumber);
            } catch (Exception e) {
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                documentService.saveDocument(cashControlDocument);
                continue;
            }
            
            if (!documentService.documentExists(invoiceNumber)) {
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                documentService.saveDocument(cashControlDocument);
                continue;
            }

            CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument)documentService.getByDocumentHeaderId(lockbox.getFinancialDocumentReferenceInvoiceNumber());
            
            if (!customerInvoiceDocument.isOpenInvoiceIndicator()) {
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_CLOSED_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                documentService.saveDocument(cashControlDocument);
                continue;
            } else {
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());

                if (customerInvoiceDocument.getTotalDollarAmount().equals(lockbox.getInvoicePaidOrAppliedAmount())){
                    // KULAR-290
                    PaymentApplicationDocument paymentApplicationDocument = 
                        getPaymentApplicationDocumentService().createSaveAndApprovePaymentApplicationToMatchInvoice(customerInvoiceDocument, "Auto-approving. Created via Lockbox process.", null);
                    customerInvoiceDocument.setOpenInvoiceIndicator(false);
                }
                documentService.saveDocument(cashControlDocument);
            }

        }
        return true;

    }

    public LockboxDao getLockboxDao() {
        return lockboxDao;
    }

    public void setLockboxDao(LockboxDao lockboxDao) {
        this.lockboxDao = lockboxDao;
    }

    public SystemInformationService getSystemInformationService() {
        return systemInformationService;
    }

    public void setSystemInformationService(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    public AccountsReceivableDocumentHeaderService getAccountsReceivableDocumentHeaderService() {
        return accountsReceivableDocumentHeaderService;
    }

    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }

    public PaymentApplicationDocumentService getPaymentApplicationDocumentService() {
        return paymentApplicationDocumentService;
    }

    public void setPaymentApplicationDocumentService(PaymentApplicationDocumentService paymentApplicationDocumentService) {
        this.paymentApplicationDocumentService = paymentApplicationDocumentService;
    }

    public void setPersonService(PersonService<PersonImpl> personService) {
        this.personService = personService;
    }

    /**
     * Gets the documentService attribute. 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
