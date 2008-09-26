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
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
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
    DocumentService docService = KNSServiceLocator.getDocumentService();

    public boolean processLockbox() throws WorkflowException {

        Iterator<Lockbox> itr = lockboxDao.getAllLockboxes();
        Lockbox ctrlLockbox = new Lockbox();
        CashControlDocument cashControlDocument = new CashControlDocument();
        while (itr.hasNext()) {
            Lockbox lockbox = (Lockbox)itr.next();

            SystemInformationService service = SpringContext.getBean(SystemInformationService.class);
            SystemInformation sysInfo = service.getByLockboxNumber(lockbox.getLockboxNumber());
            String initiator = sysInfo.getFinancialDocumentInitiatorIdentifier();
            GlobalVariables.clear();
            try {
                GlobalVariables.setUserSession(new UserSession(initiator));
            }
            catch (WorkflowException wfex) {
                LOG.warn(String.format("\nworkflow exception on fetching session %s", wfex.getMessage()));
            }
            catch (UserNotFoundException nfex) {
                LOG.warn(String.format("\nuser not found on fetching session %s", nfex.getMessage()));
            }

            if (lockbox.compareTo(ctrlLockbox) != 0) {
                // If we made it in here, then we have hit a different batchSequenceNumber and processedInvoiceDate.
                // When this is the case, we create a new cashcontroldocument and start tacking subsequent lockboxes on 
                // to the current cashcontroldocument as cashcontroldetails.
                LOG.info("New Lockbox batch");

                cashControlDocument = (CashControlDocument)KNSServiceLocator.getDocumentService().getNewDocument("CashControlDocument");
                cashControlDocument.setCustomerPaymentMediumCode(lockbox.getCustomerPaymentMediumCode());
                cashControlDocument.getDocumentHeader().setDocumentDescription(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION + lockbox.getLockboxNumber());

                AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
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


            CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
            cashControlDocumentService.addNewCashControlDetail(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION, cashControlDocument, detail);
            
            String invoiceNumber = lockbox.getFinancialDocumentReferenceInvoiceNumber();
           
            
            //This is just a temporary work around due to the invoice numbers being in FIS format still.
            try {
                Integer.parseInt(invoiceNumber);
            } catch (Exception e) {
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                docService.saveDocument(cashControlDocument);
                continue;
            }
            
            if (!docService.documentExists(invoiceNumber)) {
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                docService.saveDocument(cashControlDocument);
                continue;
            }

            CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument)docService.getByDocumentHeaderId(lockbox.getFinancialDocumentReferenceInvoiceNumber());
            
            if (!customerInvoiceDocument.isOpenInvoiceIndicator()) {
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_CLOSED_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                docService.saveDocument(cashControlDocument);
                continue;
            } else {
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());

                if (customerInvoiceDocument.getTotalDollarAmount().equals(lockbox.getInvoicePaidOrAppliedAmount())){
                    //TODO approve app doc created for this lockbox
                   
                    customerInvoiceDocument.setOpenInvoiceIndicator(false);
                }
                docService.saveDocument(cashControlDocument);
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

}
