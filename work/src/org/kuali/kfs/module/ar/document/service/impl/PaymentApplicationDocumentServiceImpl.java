/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDetailDao;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentApplicationDocumentServiceImpl implements PaymentApplicationDocumentService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentServiceImpl.class);;

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private NonAppliedHoldingService nonAppliedHoldingService;
    private InvoicePaidAppliedService<AppliedPayment> invoicePaidAppliedService;
    private UniversityDateService universityDateService;
    private CashControlDetailDao cashControlDetailDao;
    
    /**
     * 
     * @param customerInvoiceDocument
     * @return
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createPaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {
        
        PaymentApplicationDocument applicationDocument = 
            (PaymentApplicationDocument) documentService.getNewDocument(PaymentApplicationDocument.class);

        //  get the processing chart & org off the invoice, we'll create the payapp with the same processing org
        String processingChartCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        String processingOrgCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        
        AccountsReceivableDocumentHeaderService arDocHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader arDocHeader = arDocHeaderService.getNewAccountsReceivableDocumentHeader(processingChartCode, processingOrgCode);
        arDocHeader.setDocumentNumber(applicationDocument.getDocumentNumber());
        applicationDocument.setAccountsReceivableDocumentHeader(arDocHeader);
        
        // This code is basically copied from PaymentApplicationDocumentAction.quickApply
        int paidAppliedItemNumber = 1;
        for(CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts()) {
            InvoicePaidApplied invoicePaidApplied = 
                createInvoicePaidAppliedForInvoiceDetail(
                    customerInvoiceDetail, applicationDocument, paidAppliedItemNumber);
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
        documentService.approveDocument(applicationDocument, approvalAnnotation, workflowNotificationRecipients);
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
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDocumentForPaymentApplicationDocument(org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument paymentApplicationDocument) {
        if (paymentApplicationDocument == null) {
            throw new IllegalArgumentException("A null paymentApplicationDocument parameter was passed in.");
        }
        String payAppDocNumber = paymentApplicationDocument.getDocumentHeader().getDocumentNumber();
        return getCashControlDocumentForPayAppDocNumber(payAppDocNumber);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDocumentForPayAppDocNumber(java.lang.String)
     */
    public CashControlDocument getCashControlDocumentForPayAppDocNumber(String paymentApplicationDocumentNumber) {
        if (StringUtils.isBlank(paymentApplicationDocumentNumber)) {
            throw new IllegalArgumentException("A null or blank paymentApplicationDocumentNumber paraemter was passed in.");
        }
        CashControlDetail cashControlDetail = getCashControlDetailForPayAppDocNumber(paymentApplicationDocumentNumber);
        if (cashControlDetail == null) {
            return null;
        }
        CashControlDocument cashControlDocument = null;
        try {
            cashControlDocument = (CashControlDocument) documentService.getByDocumentHeaderId(cashControlDetail.getDocumentNumber());
        }
        catch (WorkflowException e) {
            //TODO we may need to swallow this ...
            throw new RuntimeException("A workflow exception was thrown when trying to retrieve document [" + cashControlDetail.getDocumentNumber() + "].", e);
        }
        return cashControlDocument;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDetailForPaymentApplicationDocument(org.kuali.kfs.module.ar.document.PaymentApplicationDocument)
     */
    public CashControlDetail getCashControlDetailForPaymentApplicationDocument(PaymentApplicationDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("A null paymentApplicationDocument parameter was passed in.");
        }
        String payAppDocumentNumber = document.getDocumentNumber();
        CashControlDetail cashControlDetail = getCashControlDetailForPayAppDocNumber(payAppDocumentNumber);
        return cashControlDetail;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#getCashControlDetailForPayAppDocNumber(java.lang.String)
     */
    public CashControlDetail getCashControlDetailForPayAppDocNumber(String payAppDocNumber) {
        if (StringUtils.isBlank(payAppDocNumber)) {
            throw new IllegalArgumentException("A null or blank payAppDocNumber parameter was passed in.");
        }
        CashControlDetail cashControlDetail = cashControlDetailDao.getCashControlDetailByRefDocNumber(payAppDocNumber);
        return cashControlDetail;
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public PaymentApplicationDocument createInvoicePaidAppliedsForEntireInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument, PaymentApplicationDocument paymentApplicationDocument) {
        
        //  clear any existing paidapplieds
        paymentApplicationDocument.getInvoicePaidApplieds().clear();

        int paidAppliedItemNumber = 1;
        for(CustomerInvoiceDetail detail : customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts()) {

            //  create the new paidapplied
            InvoicePaidApplied invoicePaidApplied = createInvoicePaidAppliedForInvoiceDetail(
                    detail, paymentApplicationDocument, paidAppliedItemNumber);
            
            // add it to the payment application document list of applied payments
            paymentApplicationDocument.getInvoicePaidApplieds().add(invoicePaidApplied);
            paidAppliedItemNumber++;
        }
        
        return paymentApplicationDocument;
    }
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createInvoicePaidAppliedForInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public InvoicePaidApplied createInvoicePaidAppliedForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, PaymentApplicationDocument paymentApplicationDocument, Integer paidAppliedItemNumber) {

        Integer universityFiscalYear = universityDateService.getCurrentFiscalYear();
        String universityFiscalPeriodCode = universityDateService.getCurrentUniversityDate().getAccountingPeriod().getUniversityFiscalPeriodCode();
        
        InvoicePaidApplied invoicePaidApplied = new InvoicePaidApplied();
        
        // set the document number for the invoice paid applied to the payment application document number.
        invoicePaidApplied.setDocumentNumber(paymentApplicationDocument.getDocumentNumber());
        
        // Set the invoice paid applied ref doc number to the document number for the customer invoice document
        invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDetail.getDocumentNumber());
        
        invoicePaidApplied.setInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
        invoicePaidApplied.setInvoiceItemAppliedAmount(customerInvoiceDetail.getAmountOpen());
        invoicePaidApplied.setUniversityFiscalYear(universityFiscalYear);
        invoicePaidApplied.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
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

    public Collection<PaymentApplicationDocument> getPaymentApplicationDocumentByInvoiceDocument(String invoiceNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("financialDocumentReferenceInvoiceNumber", invoiceNumber);
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        
        Collection<PaymentApplicationDocument> payments = service.findMatching(PaymentApplicationDocument.class, fieldValues);
        
        return payments;
    }
    
    /*
    public Collection<PaymentApplicationDocument> getPaymentApplicationDocumentsByCustomerNumber(String customerNumber) {

        Collection<PaymentApplicationDocument> payments = new ArrayList<PaymentApplicationDocument>();

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("customerNumber", customerNumber);

        Collection<AccountsReceivableDocumentHeader> documentHeaders = businessObjectService.findMatching(AccountsReceivableDocumentHeader.class, fieldValues);

        List<String> documentHeaderIds = new ArrayList<String>();
        for (AccountsReceivableDocumentHeader header : documentHeaders) {
            String documentNumber = null;
            try {
                Long.parseLong(header.getDocumentHeader().getDocumentNumber());
                documentNumber = header.getDocumentHeader().getDocumentNumber();
                documentHeaderIds.add(documentNumber);
            }
            catch (NumberFormatException nfe) {
            }
        }

        if (0 < documentHeaderIds.size()) {
            try {
                payments = documentService.getDocumentsByListOfDocumentHeaderIds(PaymentApplicationDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                //LOG.error(e.getMessage(), e);
            }
        }
        return payments;
    }

    public Collection<PaymentApplicationDocument> getPaymentApplicationDocumentsByAccountNumber(String accountNumber) {

        Collection<CustomerInvoiceDocument> invoiceList = SpringContext.getBean(CustomerInvoiceDocumentService.class).getCustomerInvoiceDocumentsByAccountNumber(accountNumber);
        
        Set<String> customerNumberSet = new HashSet<String>();
        for (CustomerInvoiceDocument invoice : invoiceList) {
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put("documentNumber", invoice.getDocumentNumber());

            AccountsReceivableDocumentHeader arDocHeader = (AccountsReceivableDocumentHeader)businessObjectService.findByPrimaryKey(AccountsReceivableDocumentHeader.class, fieldValues);
            customerNumberSet.add(arDocHeader.getCustomerNumber());
        }

        Collection<PaymentApplicationDocument> paymentApplicationDocumentList = new ArrayList<PaymentApplicationDocument>();        
        for (String customerNumber : customerNumberSet) {
            paymentApplicationDocumentList.addAll(getPaymentApplicationDocumentsByCustomerNumber(customerNumber));
        }
        
        return paymentApplicationDocumentList;
    }
    */
    
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

    public void setCashControlDetailDao(CashControlDetailDao cashControlDetailDao) {
        this.cashControlDetailDao = cashControlDetailDao;
    }
    
}
