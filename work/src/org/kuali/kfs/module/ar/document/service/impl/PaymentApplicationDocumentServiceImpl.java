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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.AppliedPayment;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentApplicationDocumentServiceImpl implements PaymentApplicationDocumentService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentServiceImpl.class);;

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private NonAppliedHoldingService nonAppliedHoldingService;
    private InvoicePaidAppliedService<AppliedPayment> invoicePaidAppliedService;
    private UniversityDateService universityDateService;

    /**
     * 
     * @param customerInvoiceDocument
     * @return
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createPaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {
        PaymentApplicationDocument applicationDocument = 
            (PaymentApplicationDocument) documentService.getNewDocument(PaymentApplicationDocument.class);

        // KULAR-290
        // This code is basically copied from PaymentApplicationDocumentAction.createDocument
        AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(applicationDocument.getDocumentNumber());
        applicationDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        
        // This code is basically copied from PaymentApplicationDocumentAction.quickApply
        int paidAppliedItemNumber = 1;
        for(CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts()) {
            InvoicePaidApplied invoicePaidApplied = 
                createInvoicePaidAppliedForInvoiceDetail(
                    customerInvoiceDetail, applicationDocument, customerInvoiceDetail.getAmountOpenFromDatabaseDiscounted(),paidAppliedItemNumber);
            // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will not be null
            if (invoicePaidApplied != null) {
                // add it to the payment application document list of applied payments
                applicationDocument.getInvoicePaidApplieds().add(invoicePaidApplied);
                paidAppliedItemNumber++;
            }
        }
        
        return applicationDocument;
    }

    /**
     *
     * @param customerInvoiceDocument
     * @return
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createAndSavePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {
        PaymentApplicationDocument applicationDocument = createPaymentApplicationToMatchInvoice(customerInvoiceDocument);
        documentService.saveDocument(applicationDocument);
        return applicationDocument;
    }

    /**
     *
     * @param customerInvoiceDocument
     * @param approvalAnnotation
     * @param workflowNotificationRecipients
     * @return
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createSaveAndApprovePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument, String approvalAnnotation, List workflowNotificationRecipients) throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        PaymentApplicationDocument applicationDocument = createAndSavePaymentApplicationToMatchInvoice(customerInvoiceDocument);
        documentService.approveDocument(applicationDocument,approvalAnnotation,workflowNotificationRecipients);
        return applicationDocument;
    }

    /**
     *
     * @param document
     * @return
     */
    public KualiDecimal getTotalAppliedAmountForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;
        Collection<InvoicePaidApplied> invoicePaidApplieds = document.getInvoicePaidApplieds();

        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            total = total.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }

        // Include non-ar funds as well
        total = total.add(document.getSumOfNonInvoiceds());

        return total;
    }

    /**
     *
     * @param document
     * @return
     */
    public KualiDecimal getTotalUnappliedFundsForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;

        String customerNumber = document.getAccountsReceivableDocumentHeader().getCustomerNumber();
        Collection<NonAppliedHolding> nonAppliedHoldings = nonAppliedHoldingService.getNonAppliedHoldingsForCustomer(customerNumber);

        for (NonAppliedHolding nonAppliedHolding : nonAppliedHoldings) {
            total = total.add(nonAppliedHolding.getFinancialDocumentLineAmount());
        }

        // Add the amount for this document, if it's set
        NonAppliedHolding nonAppliedHolding = document.getNonAppliedHolding();
        if(null != nonAppliedHolding) {
            KualiDecimal amount = nonAppliedHolding.getFinancialDocumentLineAmount();
            if(null != amount) {
                total = total.add(amount);
            }
        }

        return total;
    }

    /**
     *
     * @param document
     * @return
     */
    public KualiDecimal getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal totalUnapplied = getTotalUnappliedFundsForPaymentApplicationDocument(document);
        KualiDecimal totalApplied = getTotalAppliedAmountForPaymentApplicationDocument(document);
        return totalUnapplied.subtract(totalApplied);
    }

    /**
     *
     * @param document
     * @return
     * @throws WorkflowException
     */
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument paymentApplicationDocument) throws WorkflowException {
        DocumentHeader documentHeader = paymentApplicationDocument.getDocumentHeader();
        String cashControlDocumentNumber = documentHeader.getOrganizationDocumentNumber();
        if(null == cashControlDocumentNumber) {
            return null;
        }
        return (CashControlDocument) documentService.getByDocumentHeaderId(cashControlDocumentNumber);
    }
    
    @SuppressWarnings("unchecked")
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocumentNumber(String paymentApplicationDocumentNumber) throws WorkflowException {
        return getCashControlDocumentForPaymentApplicationDocument((PaymentApplicationDocument)documentService.getByDocumentHeaderId(paymentApplicationDocumentNumber));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDetailForPaymentApplicationDocument(org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
    public CashControlDetail getCashControlDetailForPaymentApplicationDocument(PaymentApplicationDocument document) throws WorkflowException {
        String payAppDocumentNumber = document.getDocumentNumber();
        String cashControlDocumentNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
        
        //  if no such docNumber exists, then nothing to find
        if(null == cashControlDocumentNumber) {
            return null;
        }
        
        //  load up the cash control doc, and walk through the cashcontrol detail lines
        CashControlDocument cashControlDocument = (CashControlDocument) documentService.getByDocumentHeaderId(cashControlDocumentNumber);
        List<CashControlDetail> cashControlDetails = cashControlDocument.getCashControlDetails();
        for (CashControlDetail cashControlDetail : cashControlDetails) {
            String detailDocumentNumber = cashControlDetail.getDocumentNumber();
            String refDocNumber = cashControlDetail.getReferenceFinancialDocumentNumber();
            
            //  the cashControlDetail line we care about is the one with refDocNumber = payAppDoc Number
            if (payAppDocumentNumber.equalsIgnoreCase(refDocNumber)) {
                return cashControlDetail;
            }
        }
        return null;
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createInvoicePaidAppliedForInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail, org.kuali.rice.kns.util.KualiDecimal)
     */
    public InvoicePaidApplied createInvoicePaidAppliedForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, PaymentApplicationDocument paymentApplicationDocument, KualiDecimal amount, Integer paidAppliedItemNumber) {

        Integer universityFiscalYear = universityDateService.getCurrentFiscalYear();
        String universityFiscalPeriodCode = universityDateService.getCurrentUniversityDate().getAccountingPeriod().getUniversityFiscalPeriodCode();
        
        InvoicePaidApplied invoicePaidApplied = new InvoicePaidApplied();
        // set the document number for the invoice paid applied to the payment application document number.
        invoicePaidApplied.setDocumentNumber(paymentApplicationDocument.getDocumentNumber());
        
        // Set the invoice paid applied ref doc number to the document number for the customer invoice document
        invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDetail.getDocumentNumber());
        
        invoicePaidApplied.setInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
        invoicePaidApplied.setInvoiceItemAppliedAmount(amount);
        invoicePaidApplied.setUniversityFiscalYear(universityFiscalYear);
        invoicePaidApplied.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
        //Integer invoicePaidAppliedItemNumber = 1 + paymentApplicationDocument.getInvoicePaidApplieds().size();
        invoicePaidApplied.setPaidAppliedItemNumber(paidAppliedItemNumber);

        return invoicePaidApplied;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#customerInvoiceDetailPairsWithInvoicePaidApplied(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail, org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied)
     */
    public boolean customerInvoiceDetailPairsWithInvoicePaidApplied(CustomerInvoiceDetail customerInvoiceDetail, InvoicePaidApplied invoicePaidApplied) {
        boolean pairs = true;
        pairs &= customerInvoiceDetail.getSequenceNumber().equals(invoicePaidApplied.getInvoiceItemNumber());
        pairs &= customerInvoiceDetail.getDocumentNumber().equals(invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber());
        return pairs;
    }
    
    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }
    
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public NonAppliedHoldingService getNonAppliedHoldingService() {
        return nonAppliedHoldingService;
    }

    public void setNonAppliedHoldingService(NonAppliedHoldingService nonAppliedHoldingService) {
        this.nonAppliedHoldingService = nonAppliedHoldingService;
    }
    
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
    
}
