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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentApplicationDocumentForm extends FinancialSystemTransactionalDocumentFormBase {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentForm.class);;

    private String selectedInvoiceDocumentNumber;
    private String enteredInvoiceDocumentNumber;
    private KualiDecimal unappliedCustomerAmount;
    //private KualiDecimal selectedInvoiceTotalAmount;
    //private KualiDecimal selectedInvoiceBalance;
    //private KualiDecimal amountAppliedDirectlyToInvoice;
    private PaymentApplicationInvoiceApply selectedInvoiceApplication;
    private NonInvoiced nonInvoicedAddLine;

    private List<PaymentApplicationInvoiceApply> invoiceApplications;
    
    private List<CustomerInvoiceDocument> invoices;
    
    //TODO Andrew
//    @SuppressWarnings("unchecked")
//    private Map<String, Collection> appliedPaymentsPerCustomerInvoiceDetail;
//    private Map<String, Boolean> previouslyQuickAppliedInvoices;
//    private Map<String, Boolean> quickApplyToInvoiceDetails;
    private Integer nextNonInvoicedLineNumber;

    private KualiDecimal nonAppliedHoldingAmount;
    private String nonAppliedHoldingCustomerNumber;

    private KualiDecimal oldNonAppliedHoldingAmount;

    /**
     * Constructs a PaymentApplicationDocumentForm.java.
     */
    @SuppressWarnings("unchecked")
    public PaymentApplicationDocumentForm() {
        super();
        setDocument(new PaymentApplicationDocument());
        nonInvoicedAddLine = new NonInvoiced();
        invoices = new ArrayList<CustomerInvoiceDocument>();
        selectedInvoiceApplication = null;
        invoiceApplications = new ArrayList<PaymentApplicationInvoiceApply>();
    }

    @Override
    public void reset(ActionMapping mapping, ServletRequest request) {
        super.reset(mapping, request);
        for (PaymentApplicationInvoiceApply application : invoiceApplications) {
            application.setQuickApply(false);
        }
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        
        // Set the next non-invoiced line number
        PaymentApplicationDocument paymentApplicationDocument = getPaymentApplicationDocument();
        if (ObjectUtils.isNotNull(paymentApplicationDocument.getNonInvoicedDistributions())) {
            for (NonInvoicedDistribution u : paymentApplicationDocument.getNonInvoicedDistributions()) {
                if (null == getNextNonInvoicedLineNumber()) {
                    setNextNonInvoicedLineNumber(u.getFinancialDocumentLineNumber());
                } else if (u.getFinancialDocumentLineNumber() > getNextNonInvoicedLineNumber()) {
                    setNextNonInvoicedLineNumber(u.getFinancialDocumentLineNumber());
                }
            }
        }
        
        if (null == getNextNonInvoicedLineNumber()) {
            setNextNonInvoicedLineNumber(1);
        }
        
        // This step doesn't affect anything persisted to the database. It allows proper calculation
        // of amounts for the display.
        String customerNumber = null;
        String docId = getDocument().getDocumentNumber();
        if(ObjectUtils.isNotNull(request.getParameter("docId")) && ObjectUtils.isNull(getDocument().getDocumentNumber())) {
            // The document hasn't yet been set on the form. Let's look it up manually so that we can get the customer number.
            docId = request.getParameter("docId").trim();
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            Document d;
            try {
                d = documentService.getByDocumentHeaderId(docId);
            }
            catch (WorkflowException e) {
                throw new RuntimeException("WorkflowException thrown when trying to load docId [" + docId + "]", e);
            }
            PaymentApplicationDocument pDocument = (PaymentApplicationDocument) d;
            AccountsReceivableDocumentHeader arHeader = pDocument.getAccountsReceivableDocumentHeader();
            if(ObjectUtils.isNotNull(arHeader)) {
                customerNumber = arHeader.getCustomerNumber();
            }
        }
        
        if(ObjectUtils.isNull(getSelectedInvoiceApplication())) {
            if(ObjectUtils.isNull(invoices) || invoices.isEmpty()) {
                if(ObjectUtils.isNotNull(customerNumber)) {
                    // get open invoices for the current customer
                    CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
                    Collection<CustomerInvoiceDocument> openInvoicesForCustomer = customerInvoiceDocumentService.getOpenInvoiceDocumentsByCustomerNumber(customerNumber);
                    setInvoices(new ArrayList<CustomerInvoiceDocument>(openInvoicesForCustomer));
                    if (invoices != null && !invoices.isEmpty()) {
                        setSelectedInvoiceDocumentNumber(invoices.get(0).getDocumentNumber());
                    }
                    setupInvoiceWrappers(docId);
                }
            }
        }
        
        //TODO Andrew - change this so quickapply's arent remembered, they're 
        //     turned into detail applies
//        for (int i = 0; i < invoices.size(); i++) {
//            CustomerInvoiceDocument invoice = invoices.get(i);
//            for (PaymentApplicationInvoiceApply application : invoiceApplications) {
//                if (application.getDocumentNumber().equalsIgnoreCase(invoice.getDocumentNumber())) {
//                    application.setQuickApply(request.getParameterMap().containsKey("invoices["+i+"].quickApply"));
//                }
//            }
//            //TODO Andrew
//            //invoice.setQuickApply(request.getParameterMap().containsKey("invoices["+i+"].quickApply"));
//        }
        
        //TODO Andrew
//        if(ObjectUtils.isNotNull(getSelectedInvoiceDocument())) {
//            for(CustomerInvoiceDetail customerInvoiceDetail : getSelectedInvoiceDocument().getCustomerInvoiceDetailsWithoutDiscounts()) {
//                customerInvoiceDetail.setCurrentPaymentApplicationDocument(paymentApplicationDocument);
//            }
//        }
        
    }

    public List<PaymentApplicationInvoiceApply> getQuickAppliableInvoiceApplications() {
        List<PaymentApplicationInvoiceApply> quickAppliable = new ArrayList<PaymentApplicationInvoiceApply>();
        for (PaymentApplicationInvoiceApply invoiceApplication : invoiceApplications) {
            if (!invoiceApplication.hasAnyAppliedAmounts()) {
                quickAppliable.add(invoiceApplication);
            }
        }
        return quickAppliable;
    }
    
    protected void setupInvoiceWrappers(String payAppDocNumber) {
        if (StringUtils.isBlank(payAppDocNumber)) {
            throw new IllegalArgumentException("The payAppDocNumber parameter passed in was null or blank.");
        }
        if (invoices == null || invoices.isEmpty()) {
            return;
        }
        
        //  clear any existing
        invoiceApplications.clear();
        
        for (CustomerInvoiceDocument invoice : invoices) {
            PaymentApplicationInvoiceApply invoiceApplication = new PaymentApplicationInvoiceApply(payAppDocNumber, invoice);
            addInvoiceApplication(invoiceApplication);
        }
    }

    //TODO Andrew
//    public Map<String, Boolean> getPreviouslyQuickAppliedInvoices() {
//        return previouslyQuickAppliedInvoices;
//    }
//
//    public void setPreviouslyQuickAppliedInvoices(Map<String, Boolean> previouslyQuickAppliedInvoices) {
//        this.previouslyQuickAppliedInvoices = previouslyQuickAppliedInvoices;
//    }

    //TODO Andrew
//    public void setInvoicesByDocumentNumber(Map<String,CustomerInvoiceDocument> m1) {
//        Map<String,CustomerInvoiceDocument> m2 = getInvoicesByDocumentNumber();
//        for(String k : m1.keySet()) {
//            m2.put(k, m1.get(k));
//        }
//    }
    
    public Map<String,PaymentApplicationInvoiceApply> getInvoiceApplicationsByDocumentNumber() {
        Map<String,PaymentApplicationInvoiceApply> m = new HashMap<String,PaymentApplicationInvoiceApply>();
        for (PaymentApplicationInvoiceApply i : invoiceApplications) {
            m.put(i.getDocumentNumber(),i);
        }
        return m;
    }
    
    public Integer getNextNonInvoicedLineNumber() {
        return nextNonInvoicedLineNumber;
    }

    public void setNextNonInvoicedLineNumber(Integer nextNonInvoicedLineNumber) {
        this.nextNonInvoicedLineNumber = nextNonInvoicedLineNumber;
    }

    /**
     * @return
     */
    public KualiDecimal getNonArTotal() {
        return null == getPaymentApplicationDocument() ? KualiDecimal.ZERO : getPaymentApplicationDocument().getNonArTotal();
    }

    /**
     * This method gets the payment application document
     * 
     * @return the payment application document
     */
    public PaymentApplicationDocument getPaymentApplicationDocument() {
        return (PaymentApplicationDocument) getDocument();
    }

    /**
     * This method returns the customer invoices with their computed balance
     * 
     * @return the customer invoices and their balance
     */
    //TODO Andrew - this will likely go away as invoices calc their own balances based on the db
//    public Collection<CustomerInvoiceBalanceHelper> getUpdatedBalanceInvoices() {
//        Collection<CustomerInvoiceBalanceHelper> invoices = new ArrayList<CustomerInvoiceBalanceHelper>();
//        for (CustomerInvoiceDocument invoice : getInvoices()) {
//            // Get InvoicePaidApplieds for invoice
//            invoices.add(new CustomerInvoiceBalanceHelper(invoice, new ArrayList<InvoicePaidApplied>()));
//        }
//        return invoices;
//    }

    public String getSelectedInvoiceDocumentNumber() {
        return selectedInvoiceDocumentNumber;
    }

    public void setSelectedInvoiceDocumentNumber(String selectedInvoiceDocumentNumber) {
        this.selectedInvoiceDocumentNumber = selectedInvoiceDocumentNumber;
    }

    public KualiDecimal getUnappliedCustomerAmount() {
        return unappliedCustomerAmount;
    }

    public void setUnappliedCustomerAmount(KualiDecimal unappliedCustomerAmount) {
        this.unappliedCustomerAmount = unappliedCustomerAmount;
    }

    public List<PaymentApplicationInvoiceDetailApply> getSelectedInvoiceDetailApplications() {
        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication();
        List<PaymentApplicationInvoiceDetailApply> detailApplications = null;
        if (ObjectUtils.isNotNull(invoiceApplication)) {
            detailApplications = invoiceApplication.getDetailApplications();
            if (null == detailApplications) {
                detailApplications = new ArrayList<PaymentApplicationInvoiceDetailApply>();
                //getSelectedInvoiceDocument().setCustomerInvoiceDetailsWithoutDiscounts(new ArrayList<CustomerInvoiceDetail>());
                //details = getSelectedInvoiceDocument().getCustomerInvoiceDetailsWithoutDiscounts();
            }
        }
        return detailApplications; 
    }

    public List<PaymentApplicationInvoiceApply> getNonSelectedInvoiceApplications() {
        String selectedInvoiceNumber = getSelectedInvoiceApplication().getDocumentNumber();
        
        List<PaymentApplicationInvoiceApply> nonSelectedInvoiceApplications = new ArrayList<PaymentApplicationInvoiceApply>();
        for (PaymentApplicationInvoiceApply invoiceApplication : invoiceApplications) {
            if (!invoiceApplication.getDocumentNumber().equalsIgnoreCase(selectedInvoiceNumber)) {
                nonSelectedInvoiceApplications.add(invoiceApplication);
            }
        }
        return nonSelectedInvoiceApplications; 
    }

    //TODO Andrew - this has got to go
//    public void setCustomerInvoiceDetails(Collection<CustomerInvoiceDetail> customerInvoiceDetails) {
//        if(null != getSelectedInvoiceDocument()) {
//            // This would be dangerous under any other circumstances. But in this case the invoice is never saved. So it should be fine.
//            getSelectedInvoiceDocument().setCustomerInvoiceDetailsWithoutDiscounts((List<CustomerInvoiceDetail>)customerInvoiceDetails);
//        }
//    }

    public List<PaymentApplicationInvoiceApply> getInvoiceApplications() {
        return invoiceApplications;
    }

    //TODO Andrew
//    /**
//     * Returns the list of Open Invoices available for this document.
//     * 
//     * @return
//     */
//    public List<CustomerInvoiceDocument> getOpenInvoices() {
//        List<CustomerInvoiceDocument> openInvoices = new ArrayList<CustomerInvoiceDocument>();
//        for (CustomerInvoiceDocument invoice : invoices) {
//            if (invoice.isOpenInvoiceIndicator()) {
//                openInvoices.add(invoice);
//            }
//        }
//        return openInvoices;
//    }

    public PaymentApplicationInvoiceApply getSelectedInvoiceApplication() {
        String docNumber = getSelectedInvoiceDocumentNumber();
        if(ObjectUtils.isNotNull(docNumber)) {
            return getInvoiceApplicationsByDocumentNumber().get(docNumber);
        } else {
            List<PaymentApplicationInvoiceApply> i = invoiceApplications;
            if (i.isEmpty()) {
                return null;
            } else {
                return invoiceApplications.get(0);
            }
        }
    }

    public List<CustomerInvoiceDocument> getInvoices() {
        return invoices;
    }
    
    public void setInvoices(ArrayList<CustomerInvoiceDocument> invoices) {
        this.invoices = invoices;
    }

    public String getEnteredInvoiceDocumentNumber() {
        return enteredInvoiceDocumentNumber;
    }

    public void setEnteredInvoiceDocumentNumber(String enteredInvoiceDocumentNumber) {
        this.enteredInvoiceDocumentNumber = enteredInvoiceDocumentNumber;
    }

    /**
     * This method retrieves a specific customer invoice detail from the list, by array index
     * 
     * @param index the index of the customer invoice detail to retrieve
     * @return a CustomerInvoiceDetail
     */
    public PaymentApplicationInvoiceDetailApply getInvoiceDetailApplication(int index) {
        List<PaymentApplicationInvoiceDetailApply> details = getSelectedInvoiceDetailApplications();
        //TODO Andrew
//        if (index >= details.size()) {
//            details.add(new PaymentApplicationInvoiceDetailApply());
//        }
        return details.get(index);
    }

    /**
     * This method retrieves a specific customer invoice from the list, by array index
     * 
     * @param index the index of the customer invoice to retrieve
     * @return a CustomerInvoiceDocument
     */
    public PaymentApplicationInvoiceApply getInvoiceApplication(int index) {
        //TODO Andrew
//        if (index >= invoices.size()) {
//            for (int i = invoices.size(); i <= index; i++) {
//                invoices.add(new CustomerInvoiceDocument());
//            }
//        }
        return (PaymentApplicationInvoiceApply) invoiceApplications.get(index);
    }

    @SuppressWarnings("unchecked")
    public void setInvoiceDetailApplication(int key, PaymentApplicationInvoiceDetailApply value) {
        getSelectedInvoiceDetailApplications().set(key, value);
    }

    //TODO Andrew - this should just delegate to the invoice balance method
    public KualiDecimal getSelectedInvoiceBalance() {
        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication();
        return invoiceApplication.getOpenAmount();
        //TODO Andrew
//        KualiDecimal amt = new KualiDecimal(0);
//        for(CustomerInvoiceDetail invoiceDetail : invoiceApplication.getCustomerInvoiceDetailsWithoutDiscounts()) {
//            amt = amt.add(invoiceDetail.getAmountOpen());
//            CustomerInvoiceDetail discount = invoiceDetail.getDiscountCustomerInvoiceDetail();
//            if(ObjectUtils.isNotNull(discount)) {
//                amt = amt.add(discount.getAmount());
//            }
//        }
//        return amt;
    }

    public KualiDecimal getSelectedInvoiceTotalAmount() {
        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication();
        return invoiceApplication.getInvoice().getSourceTotal();
    }

    public KualiDecimal getAmountAppliedDirectlyToInvoice() {
        PaymentApplicationInvoiceApply invoiceApplicationToFind = getSelectedInvoiceApplication();
        KualiDecimal amount = new KualiDecimal(0);
        for (PaymentApplicationInvoiceApply invoiceApplication : invoiceApplications) {
            if (invoiceApplicationToFind.getDocumentNumber().equalsIgnoreCase(invoiceApplication.getDocumentNumber())) {
                amount = amount.add(invoiceApplication.getAmountToApply());
            }
        }
        //TODO Andrew
//        for(CustomerInvoiceDetail invoiceDetail : invoice.getCustomerInvoiceDetailsWithoutDiscounts()) {
//            amount = amount.add(invoiceDetail.getAmountAppliedBy(getPaymentApplicationDocument()));
//        }
        return amount;
    }

    //TODO Andrew
//    @SuppressWarnings("unchecked")
//    public Map<String, Collection> getAppliedPaymentsPerCustomerInvoiceDetail() {
//        return appliedPaymentsPerCustomerInvoiceDetail;
//    }
//
//    @SuppressWarnings("unchecked")
//    public void setAppliedPaymentsPerCustomerInvoiceDetail(Map<String, Collection> appliedPaymentsPerCustomerInvoiceDetail) {
//        this.appliedPaymentsPerCustomerInvoiceDetail = appliedPaymentsPerCustomerInvoiceDetail;
//    }

    /**
     * This method gets the previous invoice document number
     * 
     * @return the previous invoice document number
     */
    public String getPreviousInvoiceDocumentNumber() {
        CustomerInvoiceDocument _previousInvoiceDocument = null;

        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication(); 
        CustomerInvoiceDocument selectedInvoiceDocument = invoiceApplication == null ? null : invoiceApplication.getInvoice();
        if (null == selectedInvoiceDocument || 2 > invoices.size()) {
            _previousInvoiceDocument = null;
        } else {
            Iterator<CustomerInvoiceDocument> iterator = invoices.iterator();
            CustomerInvoiceDocument previousInvoiceDocument = iterator.next();
            String selectedInvoiceDocumentNumber = selectedInvoiceDocument.getDocumentNumber();
            if (null != selectedInvoiceDocumentNumber && selectedInvoiceDocumentNumber.equals(previousInvoiceDocument.getDocumentNumber())) {
                _previousInvoiceDocument = null;
            } else {
                while (iterator.hasNext()) {
                    CustomerInvoiceDocument currentInvoiceDocument = iterator.next();
                    String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                    if (null != currentInvoiceDocumentNumber && currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
                        _previousInvoiceDocument = previousInvoiceDocument;
                    } else {
                        previousInvoiceDocument = currentInvoiceDocument;
                    }
                }
            }
        }

        return null == _previousInvoiceDocument ? "" : _previousInvoiceDocument.getDocumentNumber();
    }

    /**
     * This method gets the next invoice document number
     * 
     * @return the next invoice document number
     */
    public String getNextInvoiceDocumentNumber() {
        CustomerInvoiceDocument _nextInvoiceDocument = null;

        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication(); 
        CustomerInvoiceDocument selectedInvoiceDocument = invoiceApplication == null ? null : invoiceApplication.getInvoice();
        if (null == selectedInvoiceDocument || 2 > invoices.size()) {
            _nextInvoiceDocument = null;
        } else {
            Iterator<CustomerInvoiceDocument> iterator = invoices.iterator();
            while (iterator.hasNext()) {
                CustomerInvoiceDocument currentInvoiceDocument = iterator.next();
                String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                if (currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
                    if (iterator.hasNext()) {
                        _nextInvoiceDocument = iterator.next();
                    } else {
                        _nextInvoiceDocument = null;
                    }
                }
            }
        }

        return null == _nextInvoiceDocument ? "" : _nextInvoiceDocument.getDocumentNumber();
    }

    /**
     * This method gets the Cash Control document for the payment application document
     * 
     * @return the cash control document
     */
    public CashControlDocument getCashControlDocument() {
        return getPaymentApplicationDocument().getCashControlDocument();
        //TODO Andrew
//        PaymentApplicationDocumentService paymentApplicat ionDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
//        CashControlDocument cashControlDocument = paymentApplicationDocumentService.getCashControlDocumentForPaymentApplicationDocument((PaymentApplicationDocument) getDocument());
//        return cashControlDocument;
    }

    public NonInvoiced getNonInvoicedAddLine() {
        return nonInvoicedAddLine;
    }

    public void setNonInvoicedAddLine(NonInvoiced nonInvoicedAddLine) {
        this.nonInvoicedAddLine = nonInvoicedAddLine;
    }

    public Integer getNonInvoicedAddLineItemNumber() {
        Integer number = new Integer(0);
        if (null != getPaymentApplicationDocument()) {
            Collection<NonInvoiced> items = getPaymentApplicationDocument().getNonInvoiceds();
            for (NonInvoiced item : items) {
                Integer i = item.getFinancialDocumentLineNumber();
                if (i > number) {
                    number = i;
                }
            }
        }
        return number + 1;
    }

    public KualiDecimal getOldNonAppliedHoldingAmount() {
        return oldNonAppliedHoldingAmount;
    }

    public void setOldNonAppliedHoldingAmount(KualiDecimal oldNonAppliedHoldingAmount) {
        this.oldNonAppliedHoldingAmount = oldNonAppliedHoldingAmount;
    }

    public PaymentApplicationInvoiceApply getInvoiceApplication(String documentNumber) {
        if (StringUtils.isBlank(documentNumber)) {
            throw new RuntimeException("The parameter passed in [documentNumber] was null or blank.");
        }
        for (PaymentApplicationInvoiceApply invoiceApplication : invoiceApplications) {
            if (documentNumber.equalsIgnoreCase(invoiceApplication.getDocumentNumber())) {
                return invoiceApplication;
            }
        }
        return null;
    }
    
    public void addInvoiceApplication(PaymentApplicationInvoiceApply invoiceApplicationToAdd) {
        if (invoiceApplicationToAdd == null) {
            throw new RuntimeException("The parameter passed in [invoiceApplicationToAdd] was null.");
        }
        for (int i = 0; i < invoiceApplications.size(); i++) {
            PaymentApplicationInvoiceApply invoiceApplication = invoiceApplications.get(i);
            if (invoiceApplicationToAdd.getDocumentNumber().equalsIgnoreCase(invoiceApplication.getDocumentNumber())) {
                invoiceApplications.set(i, invoiceApplicationToAdd);
            }
        }
        invoiceApplications.add(invoiceApplicationToAdd);
    }
    
}
