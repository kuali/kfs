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
package org.kuali.module.ar.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.UserSession;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.bo.Lockbox;
import org.kuali.module.ar.bo.SystemInformation;
import org.kuali.module.ar.dao.LockboxDao;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.CashControlDocumentService;
import org.kuali.module.ar.service.LockboxService;
import org.kuali.module.ar.service.SystemInformationService;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.service.OrganizationService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

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


    public List<Lockbox> getLockboxes(Integer fiscalYear, String chartCode, String orgCode) {

        OrganizationService oService = SpringContext.getBean(OrganizationService.class);
        List<Org> orgs = oService.getActiveFinancialOrgs();


        List<Lockbox> lockboxes = new ArrayList<Lockbox>();
        SystemInformationService service = SpringContext.getBean(SystemInformationService.class);
        SystemInformation sysInfo = service.getByPrimaryKey(fiscalYear, chartCode, orgCode);

        String lockboxNum = sysInfo.getLockboxNumber();
        String initiator = sysInfo.getFinancialDocumentInitiatorIdentifier();


        Iterator<Lockbox> it = lockboxDao.getByLockboxNumber(lockboxNum);
        while(it.hasNext()) {
            Lockbox lockbox = (Lockbox)it.next();
            lockbox.setProxyInitiator(initiator);
            lockboxes.add(lockbox);
        }
        // TODO Auto-generated method stub
        return lockboxes;
    }

    public boolean processLockbox() throws WorkflowException {

        Iterator<Lockbox> itr = lockboxDao.getAllLockboxes();
        Lockbox ctrlLockbox = new Lockbox();
        CashControlDocument cashControlDocument = new CashControlDocument();
        while (itr.hasNext()) {
            Lockbox lockbox = (Lockbox)itr.next();

            SystemInformationService service = SpringContext.getBean(SystemInformationService.class);
            SystemInformation sysInfo = service.getByLockboxNumber(lockbox.getLockboxNumber());
            String initiator = sysInfo.getFinancialDocumentInitiatorIdentifier();
            LOG.info(initiator +" initiator");
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
                LOG.info("New Lockbox batch");

                cashControlDocument = (CashControlDocument)KNSServiceLocator.getDocumentService().getNewDocument("CashControlDocument");
                cashControlDocument.setCustomerPaymentMediumCode(lockbox.getCustomerPaymentMediumCode());
                // cashControlDocument.setCashControlTotalAmount(lockbox.getInvoicePaidOrAppliedAmount());
                cashControlDocument.getDocumentHeader().setFinancialDocumentDescription("Created by Lockbox " + lockbox.getLockboxNumber());
                AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
                AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
                accountsReceivableDocumentHeader.setDocumentNumber(cashControlDocument.getDocumentNumber());
                accountsReceivableDocumentHeader.setCustomerNumber(lockbox.getCustomerNumber());
                accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(sysInfo.getProcessingChartOfAccountCode());
                accountsReceivableDocumentHeader.setProcessingOrganizationCode(sysInfo.getProcessingOrganizationCode());

                cashControlDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
            } 

            ctrlLockbox = lockbox;

            CashControlDetail detail = new CashControlDetail();
            detail.setCustomerNumber(lockbox.getCustomerNumber());
            detail.setFinancialDocumentLineAmount(lockbox.getInvoicePaidOrAppliedAmount());
            detail.setCustomerPaymentDate(lockbox.getProcessedInvoiceDate());
            detail.setCustomerPaymentDescription("Lockbox Remittance  " +lockbox.getFinancialDocumentReferenceInvoiceNumber());


            CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
            cashControlDocumentService.addNewCashControlDetail("Created by Lockbox", cashControlDocument, detail);
            
            String invoiceNumber = lockbox.getFinancialDocumentReferenceInvoiceNumber();
           
            if (!docService.documentExists(invoiceNumber)) {
                detail.setCustomerPaymentDescription("Lockbox: Remittance for INVALID invoice number " +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                docService.saveDocument(cashControlDocument);
                continue;
            }

            CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument)docService.getByDocumentHeaderId(lockbox.getFinancialDocumentReferenceInvoiceNumber());
            
            if (!customerInvoiceDocument.isOpenInvoiceIndicator()) {
                detail.setCustomerPaymentDescription("Lockbox: Remittance for CLOSED invoice number " +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                docService.saveDocument(cashControlDocument);
                continue;
            } else {
                detail.setCustomerPaymentDescription("Lockbox: Remittance for invoice number " +lockbox.getFinancialDocumentReferenceInvoiceNumber());

                if (customerInvoiceDocument.getTotalDollarAmount().equals(lockbox.getInvoicePaidOrAppliedAmount())){
                    //TODO approve app doc created for this lockbox
                    //   payAppDoc.getAccountsReceivableDocumentHeader().getDocumentHeader().getWorkflowDocument().approve();
                    //TODO create GLPEs ????
                    customerInvoiceDocument.setOpenInvoiceIndicator(false);
                }
                docService.saveDocument(cashControlDocument);
            }

        }
        return true;

    }

//  public boolean processLockboxes() throws Exception {
//  LOG.info("Started processLockboxes()");
//  OrganizationService oService = SpringContext.getBean(OrganizationService.class);
//  List<Org> orgs = oService.getActiveFinancialOrgs();
//  Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
//  SystemInformationService service = SpringContext.getBean(SystemInformationService.class);
//  LOG.info(orgs.size()+" orgs size");
//  for (Iterator<Org> itr = orgs.iterator(); itr.hasNext();) {
//  Org org = (Org)itr.next(); 
//  String chartCode = org.getChartOfAccountsCode();
//  LOG.info(chartCode+" chartcode");
//  String orgCode = org.getOrganizationCode();
//  LOG.info(orgCode +" orgCode");
//  SystemInformation sysInfo = service.getByPrimaryKey(fiscalYear, chartCode, orgCode);
//  if (sysInfo == null)
//  continue;
//  String lockboxNum = sysInfo.getLockboxNumber();
//  LOG.info(lockboxNum +" lockbox number");
//  String initiator = sysInfo.getFinancialDocumentInitiatorIdentifier();
//  LOG.info(initiator +" initiator");
//  GlobalVariables.clear();
//  try {
//  GlobalVariables.setUserSession(new UserSession(initiator));
//  }
//  catch (WorkflowException wfex) {
//  LOG.warn(String.format("\nworkflow exception on fetching session %s", wfex.getMessage()));
//  }
//  catch (UserNotFoundException nfex) {
//  LOG.warn(String.format("\nuser not found on fetching session %s", nfex.getMessage()));
//  }

//  Iterator<Lockbox> it = lockboxDao.getByLockboxNumber(lockboxNum);

//  Lockbox ctrlLockbox = new Lockbox();
//  CashControlDocument cashControlDocument = new CashControlDocument();
//  while (it.hasNext()) {
//  Lockbox lockbox = (Lockbox)it.next();

//  if (lockbox.compareTo(ctrlLockbox) != 0) {

//  cashControlDocument = (CashControlDocument)KNSServiceLocator.getDocumentService().getNewDocument("CashControlDocument");
//  cashControlDocument.setCustomerPaymentMedium(lockbox.getCustomerPaymentMedium());
//  cashControlDocument.setCashControlTotalAmount(lockbox.getInvoicePaidOrAppliedAmount());

//  AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
//  AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
//  accountsReceivableDocumentHeader.setDocumentNumber(cashControlDocument.getDocumentNumber());
//  accountsReceivableDocumentHeader.setCustomerNumber(lockbox.getCustomerNumber());
//  accountsReceivableDocumentHeader.setFinancialDocumentExplanationText("Created by Lockbox " + lockbox.getInvoiceSequenceNumber());
//  cashControlDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

//  } 

//  ctrlLockbox = lockbox;

//  CashControlDetail detail = new CashControlDetail();
//  detail.setCustomerNumber(lockbox.getCustomerNumber());
//  detail.setFinancialDocumentLineAmount(lockbox.getInvoicePaidOrAppliedAmount());
//  detail.setCustomerPaymentDate(lockbox.getProcessedInvoiceDate());

//  CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
//  PaymentApplicationDocument payAppDoc = cashControlDocumentService.createAndSavePaymentApplicationDocument("Created by Lockbox", cashControlDocument, detail);

//  DocumentService docService = KNSServiceLocator.getDocumentService();
//  CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument)docService.getByDocumentHeaderId(lockbox.getFinancialDocumentReferenceInvoiceNumber());

//  if (customerInvoiceDocument == null) {
//  detail.setCustomerPaymentDescription("Lockbox: Remittance for INVALID invoice number" +lockbox.getFinancialDocumentReferenceInvoiceNumber());
//  // break?
//  } else {
//  if (!customerInvoiceDocument.isOpenInvoiceIndicator()) {
//  detail.setCustomerPaymentDescription("Lockbox: Remittance for CLOSED invoice number" +lockbox.getFinancialDocumentReferenceInvoiceNumber());
//  } else {
//  detail.setCustomerPaymentDescription("Lockbox: Remittance for invoice number" +lockbox.getFinancialDocumentReferenceInvoiceNumber());
//  }


//  if (customerInvoiceDocument.getTotalDollarAmount().equals(lockbox.getInvoicePaidOrAppliedAmount())){
//  //TODO approve app doc created for this lockbox
//  //   payAppDoc.getAccountsReceivableDocumentHeader().getDocumentHeader().getWorkflowDocument().approve();
//  //TODO create GLPEs ????
//  customerInvoiceDocument.setOpenInvoiceIndicator(false);
//  }


//  }
//  }
//  }
//  return true;
//  }


    public LockboxDao getLockboxDao() {
        return lockboxDao;
    }

    public void setLockboxDao(LockboxDao lockboxDao) {
        this.lockboxDao = lockboxDao;
    }

}
