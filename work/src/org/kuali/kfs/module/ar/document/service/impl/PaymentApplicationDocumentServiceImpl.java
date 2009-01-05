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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
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
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentApplicationDocumentServiceImpl implements PaymentApplicationDocumentService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentServiceImpl.class);;

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private NonAppliedHoldingService nonAppliedHoldingService;

    /**
     * 
     * @param customerInvoiceDocument
     * @return
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createPaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {
        PaymentApplicationDocument applicationDocument = 
            (PaymentApplicationDocument) KNSServiceLocator.getDocumentService().getNewDocument(PaymentApplicationDocument.class);

        // KULAR-290
        // This code is basically copied from PaymentApplicationDocumentAction.createDocument
        AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(applicationDocument.getDocumentNumber());
        applicationDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        
        // This code is needed for the code below but isn't copied from anywhere.
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        Integer universityFiscalYear = universityDateService.getCurrentFiscalYear();
        String universityFiscalPeriodCode = universityDateService.getCurrentUniversityDate().getAccountingPeriod().getUniversityFiscalPeriodCode();
        
        // This code is basically copied from PaymentApplicationDocumentAction.quickApply
        for(CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts()) {
            updateCustomerInvoiceDetailInfo(applicationDocument, customerInvoiceDetail);
            Integer invoicePaidAppliedItemNbr = applicationDocument.getInvoicePaidApplieds().size() + 1;
            InvoicePaidApplied invoicePaidApplied = 
                createInvoicePaidAppliedForInvoiceDetail(
                    customerInvoiceDetail, applicationDocument.getDocumentNumber(), universityFiscalYear, 
                    universityFiscalPeriodCode, customerInvoiceDetail.getOpenAmount(), invoicePaidAppliedItemNbr);
            // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will not be null
            if (invoicePaidApplied != null) {
                // add it to the payment application document list of applied payments
                applicationDocument.getInvoicePaidApplieds().add(invoicePaidApplied);
                customerInvoiceDetail.setAmountToBeApplied(customerInvoiceDetail.getAmount());
            }
            updateCustomerInvoiceDetailInfo(applicationDocument, customerInvoiceDetail);
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
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
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
        total = total.add(document.getNonInvoicedTotalAmount());

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
        return (CashControlDocument) documentService.getByDocumentHeaderId(cashControlDocumentNumber);
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
    
    @SuppressWarnings("unchecked")
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocumentNumber(String paymentApplicationDocumentNumber) throws WorkflowException {
        return getCashControlDocumentForPaymentApplicationDocument((PaymentApplicationDocument)documentService.getByDocumentHeaderId(paymentApplicationDocumentNumber));
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createInvoicePaidAppliedForInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail,
     *      org.kuali.rice.kns.util.KualiDecimal)
     */
    public InvoicePaidApplied createInvoicePaidAppliedForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, String applicationDocNbr, Integer universityFiscalYear, String universityFiscalPeriodCode, KualiDecimal amount, Integer invoicePaidAppliedItemNbr) {

        Collection<InvoicePaidApplied> invoicePaidApplieds = customerInvoiceDetail.getInvoicePaidApplieds();
        InvoicePaidApplied invoicePaidApplied = null;
        boolean found = false;

        if (invoicePaidApplieds != null && 0 < invoicePaidApplieds.size()) {
            for (InvoicePaidApplied _invoicePaidApplied : invoicePaidApplieds) {
                boolean invoicePaidAppliedDocumentNumberEqualsApplicationDocumentNumber = 
                    _invoicePaidApplied.getDocumentNumber().equals(applicationDocNbr);
                boolean invoicePaidAppliedFinancialDocumentReferenceInvoiceNumberEqualsCustomerInvoiceDetailDocumentNumber =
                    _invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber().equals(customerInvoiceDetail.getDocumentNumber());
                boolean invoicePaidAppliedInvoiceItemNumberEqualsCustomerInvoiceDetailSequenceNumber = 
                    _invoicePaidApplied.getInvoiceItemNumber().equals(customerInvoiceDetail.getSequenceNumber());
                
                if (invoicePaidAppliedDocumentNumberEqualsApplicationDocumentNumber 
                        && invoicePaidAppliedFinancialDocumentReferenceInvoiceNumberEqualsCustomerInvoiceDetailDocumentNumber
                        && invoicePaidAppliedInvoiceItemNumberEqualsCustomerInvoiceDetailSequenceNumber) {
                    _invoicePaidApplied.setInvoiceItemAppliedAmount(amount);
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            invoicePaidApplied = new InvoicePaidApplied();
            
            // set the document number for the invoice paid applied to the payment application document number.
            invoicePaidApplied.setDocumentNumber(applicationDocNbr);
            
            // Set the invoice paid applied ref doc number to the document number for the customer invoice document
            invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDetail.getDocumentNumber());
            
            invoicePaidApplied.setInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
            invoicePaidApplied.setInvoiceItemAppliedAmount(amount);
            invoicePaidApplied.setUniversityFiscalYear(universityFiscalYear);
            invoicePaidApplied.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
            invoicePaidApplied.setPaidAppliedItemNumber(invoicePaidAppliedItemNbr);
        }

        return invoicePaidApplied;
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
    
    /**
     * This method update customer invoice detail information.
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    public void updateCustomerInvoiceDetailInfo(PaymentApplicationDocument applicationDocument, CustomerInvoiceDetail customerInvoiceDetail) {
        // update information for customer invoice detail: update the list of invoice paid applieds, compute applied amount and balance(should be done in this order as
        // balance calculation depends on applied amount )
        updateCustomerInvoiceDetailAppliedPayments(applicationDocument, customerInvoiceDetail);
        updateCustomerInvoiceDetailAppliedAmount(customerInvoiceDetail);
        updateCustomerInvoiceDetailBalance(customerInvoiceDetail);
        updateAmountAppliedOnDetail(applicationDocument, customerInvoiceDetail);
    }

    /**
     * This method updates the applied amount for the given customer invoice detail.
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    private void updateCustomerInvoiceDetailAppliedAmount(CustomerInvoiceDetail customerInvoiceDetail) {
        ArrayList<InvoicePaidApplied> invoicePaidApplieds = new ArrayList(customerInvoiceDetail.getInvoicePaidApplieds());
        KualiDecimal appliedAmount = customerInvoiceDetail.getAppliedAmount();

        // TODO we might want to compute this based on the applied payments on this doc...
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        customerInvoiceDetail.setAppliedAmount(appliedAmount);
    }

    /**
     * This method updates the balance for the given customer invoice detail.
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    public void updateCustomerInvoiceDetailBalance(CustomerInvoiceDetail customerInvoiceDetail) {
        KualiDecimal totalAmount = customerInvoiceDetail.getAmount();
        KualiDecimal appliedAmount = customerInvoiceDetail.getAppliedAmount();
        KualiDecimal balance = totalAmount.subtract(appliedAmount);
        customerInvoiceDetail.setBalance(balance);
    }

    /**
     * This method will update the list of the applied payments for this customer invoice detail taking into account the applied
     * payments on the form that are not yet saved in the db
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    public void updateCustomerInvoiceDetailAppliedPayments(PaymentApplicationDocument applicationDocument, CustomerInvoiceDetail customerInvoiceDetail) {
        String applicationDocNumber = applicationDocument.getDocumentNumber();

        // get the invoice paid applieds for this detail that where saved in the db for this app doc
        InvoicePaidAppliedService invoicePaidAppliedService = SpringContext.getBean(InvoicePaidAppliedService.class);
        Collection<InvoicePaidApplied> detailInvPaidApplieds = invoicePaidAppliedService.getInvoicePaidAppliedsForCustomerInvoiceDetail(customerInvoiceDetail, applicationDocNumber);

        Collection<InvoicePaidApplied> invPaidAppliedsFormForThisDetail = getInvoicePaidAppliedsForDetail(applicationDocument.getInvoicePaidApplieds(), customerInvoiceDetail);

        Collection<InvoicePaidApplied> invPaidAppliedsToBeAdded = new ArrayList<InvoicePaidApplied>();

        // go over the invoice paid applieds from the form for this detail and check if they are in the detail inv paid applieds list; if not add the in the invPaidAppliedsToBeAdded collection
        for (InvoicePaidApplied invoicePaidApplied2 : invPaidAppliedsFormForThisDetail) {
            boolean found = false;
            for (InvoicePaidApplied invoicePaidApplied1 : detailInvPaidApplieds) {

                String invoiceNumber1 = invoicePaidApplied1.getFinancialDocumentReferenceInvoiceNumber();
                String invoiceNumber2 = invoicePaidApplied2.getFinancialDocumentReferenceInvoiceNumber();
                Integer detailNumber1 = invoicePaidApplied1.getInvoiceItemNumber();
                Integer detailNumber2 = invoicePaidApplied2.getInvoiceItemNumber();
                Integer paidAppliedNumber1 = invoicePaidApplied1.getPaidAppliedItemNumber();
                Integer paidAppliedNumber2 = invoicePaidApplied2.getPaidAppliedItemNumber();

                if (invoiceNumber1.equals(invoiceNumber2) && detailNumber1.equals(detailNumber2) && paidAppliedNumber1.equals(paidAppliedNumber2)) {
                    found = true;
                    break;
                }

            }
            if (!found) {
                invPaidAppliedsToBeAdded.add(invoicePaidApplied2);
            }
        }

        detailInvPaidApplieds.addAll(invPaidAppliedsToBeAdded);

        customerInvoiceDetail.setInvoicePaidApplieds(detailInvPaidApplieds);
    }
    
    /**
     * This method gets the invoice paid applieds from the form for the given invoice detail
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForDetail(Collection<InvoicePaidApplied> invPaidAppliedsForm, CustomerInvoiceDetail customerInvoiceDetail) {
        // get the invoice paid applieds from the form
        Collection<InvoicePaidApplied> invPaidAppliedsFormForThisDetail = new ArrayList<InvoicePaidApplied>();

        // get the invoice paid applieds from the form for this detail
        for (InvoicePaidApplied invoicePaidApplied : invPaidAppliedsForm) {
            if (invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber().equals(customerInvoiceDetail.getDocumentNumber()) && invoicePaidApplied.getInvoiceItemNumber().equals(customerInvoiceDetail.getSequenceNumber())) {
                invPaidAppliedsFormForThisDetail.add(invoicePaidApplied);
            }
        }
        return invPaidAppliedsFormForThisDetail;
    }

    /**
     * This method updates amount to be applied on invoice detail
     * @param applicationDocumentForm
     */
    public void updateAmountAppliedOnDetail(PaymentApplicationDocument applicationDocument, CustomerInvoiceDetail customerInvoiceDetail) {
        Collection<InvoicePaidApplied> paidAppliedsFromDocument = applicationDocument.getInvoicePaidApplieds();
        Collection<InvoicePaidApplied> invoicePaidApplieds = getInvoicePaidAppliedsForDetail(paidAppliedsFromDocument, customerInvoiceDetail);
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            customerInvoiceDetail.setAmountToBeApplied(invoicePaidApplied.getInvoiceItemAppliedAmount());
            //there should be actualy only one paid applied per detail
            break;
        }
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    
}
