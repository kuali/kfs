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

import org.apache.commons.lang.StringUtils;
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
    private PaymentApplicationDocumentService payAppDocService;
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

            //  retrieve the processingOrg (system information) for this lockbox number
            SystemInformation sysInfo = systemInformationService.getByLockboxNumberForCurrentFiscalYear(lockbox.getLockboxNumber());
            String initiator = sysInfo.getFinancialDocumentInitiatorIdentifier();
            LOG.info("   using SystemInformation: '" + sysInfo.toString() + "'");
            LOG.info("   using Financial Document Initiator ID: '" + initiator + "'");
            
            //  puke if the initiator stored in the systemInformation table is no good
            Person person = personService.getPerson(initiator);
            if (person == null) {
                LOG.warn("   could not find [" + initiator + "] when searching by PrincipalID, so trying to find as a PrincipalName.");
                person = personService.getPersonByPrincipalName(initiator);
                if (person == null) {
                    LOG.error("Financial Document Initiator ID [" + initiator + "] specified in SystemInformation [" + sysInfo.toString() + "] for Lockbox Number " + lockbox.getLockboxNumber() + " is not present in the system as either a PrincipalID or a PrincipalName.");
                    throw new RuntimeException("Financial Document Initiator ID [" + initiator + "] specified in SystemInformation [" + sysInfo.toString() + "] for Lockbox Number " + lockbox.getLockboxNumber() + " is not present in the system as either a PrincipalID or a PrincipalName.");
                }
                else {
                    LOG.info("   found [" + initiator + "] in the system as a PrincipalName.");
                }
            }
            else {
                LOG.info("   found [" + initiator + "] in the system as a PrincipalID.");
            }
            
            //  masquerade as the person indicated in the systemInformation
            GlobalVariables.clear();
            GlobalVariables.setUserSession(new UserSession(person.getPrincipalName()));

            if (lockbox.compareTo(ctrlLockbox) != 0) {
                // If we made it in here, then we have hit a different batchSequenceNumber and processedInvoiceDate.
                // When this is the case, we create a new cashcontroldocument and start tacking subsequent lockboxes on 
                // to the current cashcontroldocument as cashcontroldetails.
                LOG.info("New Lockbox batch");

                //  create a new CashControl document
                LOG.info("Creating new CashControl document for invoice: " + lockbox.getFinancialDocumentReferenceInvoiceNumber() + ".");
                try {
                    cashControlDocument = (CashControlDocument)documentService.getNewDocument(dataDictionaryService.getDocumentTypeNameByClass(CashControlDocument.class));
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to initiate a new CashControl document.", e);
                    throw new RuntimeException("A Exception was thrown while trying to initiate a new CashControl document.", e);
                }
                LOG.info("   CashControl documentNumber == '" + cashControlDocument.getDocumentNumber() + "'");
                cashControlDocument.setCustomerPaymentMediumCode(lockbox.getCustomerPaymentMediumCode());
                if(ObjectUtils.isNotNull(lockbox.getBankCode())) {
                    String bankCode = lockbox.getBankCode();
                    cashControlDocument.setBankCode(bankCode);
                } 
                cashControlDocument.getDocumentHeader().setDocumentDescription(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION + lockbox.getLockboxNumber());

                //  setup the AR header for this CashControl doc
                LOG.info("   creating AR header for customer: [" + lockbox.getCustomerNumber() + "] and ProcessingOrg: " + sysInfo.getProcessingChartOfAccountCode() + "-" + sysInfo.getProcessingOrganizationCode() + ".");
                AccountsReceivableDocumentHeader arDocHeader;
                try {
                    arDocHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeader(
                            sysInfo.getProcessingChartOfAccountCode(), sysInfo.getProcessingOrganizationCode());
                }
                catch (Exception e) {
                    LOG.error("An Exception was thrown while trying to create a new AccountsReceivableDocumentHeader for the current user: '" + person.getPrincipalName() + "'.", e);
                    throw new RuntimeException("An Exception was thrown while trying to create a new AccountsReceivableDocumentHeader for the current user: '" + person.getPrincipalName() + "'.", e);
                }
                arDocHeader.setDocumentNumber(cashControlDocument.getDocumentNumber());
                arDocHeader.setCustomerNumber(lockbox.getCustomerNumber());
                cashControlDocument.setAccountsReceivableDocumentHeader(arDocHeader);
            } 
            // set our control lockbox as the current lockbox and create details.
            ctrlLockbox = lockbox;

            //  skip zero-dollar-amount lockboxes
            if (lockbox.getInvoicePaidOrAppliedAmount().isZero()) {
                LOG.warn("   lockbox has a zero dollar amount, so we're skipping it.");
                continue;
            }
            
            //  create a new cashcontrol detail
            CashControlDetail detail = new CashControlDetail();
            detail.setCustomerNumber(lockbox.getCustomerNumber());
            detail.setFinancialDocumentLineAmount(lockbox.getInvoicePaidOrAppliedAmount());
            detail.setCustomerPaymentDate(lockbox.getProcessedInvoiceDate());
            detail.setCustomerPaymentDescription("Lockbox Remittance  " +lockbox.getFinancialDocumentReferenceInvoiceNumber());

            //  add it to the document
            LOG.info("   creating detail " + lockbox.getInvoicePaidOrAppliedAmount() + " with invoiceDate: " + lockbox.getProcessedInvoiceDate());
            try {
                getCashControlDocumentService().addNewCashControlDetail(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION, cashControlDocument, detail);
            }
            catch (Exception e) {
                LOG.error("A Exception was thrown while trying to create a new CashControl detail.", e);
                throw new RuntimeException("A Exception was thrown while trying to create a new CashControl detail.", e);
            }

            String invoiceNumber = lockbox.getFinancialDocumentReferenceInvoiceNumber();
            LOG.info("   lockbox references invoice number [" + invoiceNumber + "].");
            
            //  before release 3, during dev, sometimes invoice numbers we got from the functional 
            // testing dataset were old FIS style, and not compatible with KFS
            boolean invoiceNumberNotParsable = false;
            if (StringUtils.isBlank(invoiceNumber)) {
                invoiceNumberNotParsable = true;
            }
            else {
                try {
                    Integer.parseInt(invoiceNumber);
                } catch (Exception e) {
                    invoiceNumberNotParsable = true;
                }
            }
            
            //  if thats the case, dont even bother looking for an invoice, just save the CashControl
            if (invoiceNumberNotParsable) {
                LOG.info("   invoice number [" + invoiceNumber + "] isnt in expected KFS format, so cannot load the original invoice.");
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                try {
                    documentService.saveDocument(cashControlDocument);
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
                    throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
                }
                continue;
            }
            
            //  check to see if the invoice indicated exists, and if not, then save the CashControl and move on
            if (!documentService.documentExists(invoiceNumber)) {
                LOG.info("   invoice number [" + invoiceNumber + "] does not exist in system, so cannot load the original invoice.");
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                try {
                    documentService.saveDocument(cashControlDocument);
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
                    throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
                }
                continue;
            }

            //  load up the specified invoice from the lockbox
            LOG.info("   loading invoice number [" + invoiceNumber + "].");
            CustomerInvoiceDocument customerInvoiceDocument;
            try {
                customerInvoiceDocument = (CustomerInvoiceDocument)documentService.getByDocumentHeaderId(invoiceNumber);
            }
            catch (Exception e) {
                LOG.error("A Exception was thrown while trying to load invoice #" + invoiceNumber + ".", e);
                throw new RuntimeException("A Exception was thrown while trying to load invoice #" + invoiceNumber + ".", e);
            }
            
            //  if the invoice is already closed, then just save the CashControl and move on
            if (!customerInvoiceDocument.isOpenInvoiceIndicator()) {
                LOG.info("   invoice is already closed, so saving CashControl doc and moving on.");
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_CLOSED_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                try {
                    documentService.saveDocument(cashControlDocument);
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
                    throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
                }
                continue;
            }
            
            //  if the lockbox amount matches the invoice amount, then create, save and approve a PayApp, and then 
            // mark the invoice
            detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
            if (customerInvoiceDocument.getTotalDollarAmount().equals(lockbox.getInvoicePaidOrAppliedAmount())){
                LOG.info("   lockbox amount matches invoice total document amount.");
                // KULAR-290
                LOG.info("   creating, saving, and approving Payment Application Document.");
                PaymentApplicationDocument payAppDoc;
                try {
                    payAppDoc = payAppDocService.createSaveAndApprovePaymentApplicationToMatchInvoice(customerInvoiceDocument, 
                            "Auto-approving. Created via Lockbox process.", null);
                    // note that the payapp will automatically close the invoice if it brought 
                    // the invoice to zero open amount
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to create, save and approve a PayAppDoc.", e);
                    throw new RuntimeException("A Exception was thrown while trying to create, save and approve a PayAppDoc.", e);
                }
                LOG.info("   paymentApplicationDocument [" + payAppDoc.getDocumentNumber() + "] created successfully.");
            }
            LOG.info("   saving cash control document.");
            try {
                documentService.saveDocument(cashControlDocument);
            }
            catch (Exception e) {
                LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
                throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
            }
        }
        return true;

    }

    public Long getMaxLockboxSequenceNumber() {
        return lockboxDao.getMaxLockboxSequenceNumber();
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

    public void setPaymentApplicationDocumentService(PaymentApplicationDocumentService paymentApplicationDocumentService) {
        this.payAppDocService = paymentApplicationDocumentService;
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
