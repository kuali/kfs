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
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

public class PaymentApplicationDocumentForm extends KualiAccountingDocumentFormBase {
    
    private String customerNumber;
    private String selectedInvoiceDocumentNumber;
    private Collection<NonAppliedHolding> nonAppliedHoldings;
    
    private CustomerService customerService;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private BusinessObjectService businessObjectService;
    private PaymentApplicationDocumentService paymentApplicationDocumentService;

    public PaymentApplicationDocumentForm() {
        super();
        setDocument(new PaymentApplicationDocument());
        customerService = SpringContext.getBean(CustomerService.class);
        customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        nonAppliedHoldings = new ArrayList<NonAppliedHolding>();
    }
    
    /**
     * @return
     */
    public PaymentApplicationDocument getPaymentApplicationDocument() {
        return (PaymentApplicationDocument) getDocument();
    }
    
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument() {
        return paymentApplicationDocumentService.getCashControlDocumentForPaymentApplicationDocument((PaymentApplicationDocument)getDocument());
    }
    
    public KualiDecimal getCashControlTotalForPaymentApplicationDocument() {
        CashControlDocument cashControlDocument = getCashControlDocumentForPaymentApplicationDocument();
        if(null == cashControlDocument) {
            return null;
        }
        
        return cashControlDocument.getCashControlTotalAmount();
    }

    /**
     * @return
     */
    public Customer getCustomer() {
        Customer customer = null;

        if(null != customerNumber) {
            customer = customerService.getByPrimaryKey(this.customerNumber);
        } else if(null != selectedInvoiceDocumentNumber) {
            customer = customerInvoiceDocumentService.getCustomerByInvoiceDocumentNumber(this.selectedInvoiceDocumentNumber);
        }
        
        return customer;
    }

    /**
     * @return
     */
    public KualiDecimal getAmountAppliedDirectlyToInvoice() {
        return customerInvoiceDocumentService.getTotalAmountForCustomerInvoiceDocument(getSelectedInvoiceDocumentNumber());
    }
    
    /**
     * @return
     */
    public CustomerInvoiceDocument getSelectedInvoiceDocument() {
        CustomerInvoiceDocument invoice = null;
        
        if(null != this.selectedInvoiceDocumentNumber) {
            invoice = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(selectedInvoiceDocumentNumber);
        } else {
            Collection<CustomerInvoiceDocument> invoices = getInvoices();
            if(null != invoices && 0 != invoices.size()) {
                invoice = invoices.iterator().next();
            }
        }
        
        return invoice;
    }
    
    /**
     * @return
     */
    public KualiDecimal getSelectedInvoiceTotalAmount() {
        return getInvoiceTotalAmount(getSelectedInvoiceDocumentNumber());
    }

    /**
     * @param invoiceDocumentNumber
     * @return
     */
    private KualiDecimal getInvoiceTotalAmount(String invoiceDocumentNumber) {
        // TODO Fill in logic.
        
        
        
        return null;
    }
    
    /**
     * @return
     */
    public String getPreviousInvoiceDocumentNumber() {
        CustomerInvoiceDocument _previousInvoiceDocument = null;
        
        CustomerInvoiceDocument selectedInvoiceDocument = getSelectedInvoiceDocument();
        if(null == selectedInvoiceDocument || 2 > getInvoices().size()) {
            _previousInvoiceDocument = null;
        } else {
            Iterator<CustomerInvoiceDocument> iterator = getInvoices().iterator();
            CustomerInvoiceDocument previousInvoiceDocument = iterator.next();
            String selectedInvoiceDocumentNumber = selectedInvoiceDocument.getDocumentNumber();
            if(null != selectedInvoiceDocumentNumber && selectedInvoiceDocumentNumber.equals(previousInvoiceDocument.getDocumentNumber())) {
                _previousInvoiceDocument = null;
            } else {
                while(iterator.hasNext()) {
                    CustomerInvoiceDocument currentInvoiceDocument = iterator.next();
                    String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                    if(null != currentInvoiceDocumentNumber && currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
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
     * @return
     */
    public String getNextInvoiceDocumentNumber() {
        CustomerInvoiceDocument _nextInvoiceDocument = null;
        
        CustomerInvoiceDocument selectedInvoiceDocument = getSelectedInvoiceDocument();
        if(null == selectedInvoiceDocument || 2 > getInvoices().size()) {
            _nextInvoiceDocument = null;
        } else {
            Iterator<CustomerInvoiceDocument> iterator = getInvoices().iterator();
            while(iterator.hasNext()) {
                CustomerInvoiceDocument currentInvoiceDocument = iterator.next();
                String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                if(currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
                    if(iterator.hasNext()) {
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
     * @return
     */
    public Collection<CustomerInvoiceDocument> getInvoices() {
        if(null == getCustomer()) {
            return null;
        } else {
            return customerService.getInvoicesForCustomer(getCustomer());
        }
    }

    /**
     * @param refresh
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(boolean refresh) {
        
        if((refresh || nonAppliedHoldings.isEmpty()) && null != getCustomer()) {
            Map args = new HashMap();
            args.put("customerNumber", getCustomer().getCustomerNumber());
            nonAppliedHoldings = businessObjectService.findMatching(NonAppliedHolding.class, args);
        }
        
        return nonAppliedHoldings;
    }

    /**
     * @return
     */
    public KualiDecimal getTotalAmountForSelectedInvoiceDocument() {
        return customerInvoiceDocumentService.getTotalAmountForCustomerInvoiceDocument(getSelectedInvoiceDocumentNumber());
    }
    
    /**
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForSelectedCustomerInvoiceDocument() {
        return customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(getSelectedInvoiceDocumentNumber());
    }
    
    /**
     * @return
     */
    public KualiDecimal getBalanceForSelectedInvoiceDocument() {
        return customerInvoiceDocumentService.getBalanceForCustomerInvoiceDocument(getSelectedInvoiceDocumentNumber());
    }
    
    /**
     * @return
     */
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer() {
        return getNonAppliedHoldingsForCustomer(false);
    }
    
    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = null == customerNumber ? null : customerNumber.toUpperCase();
    }

    public String getSelectedInvoiceDocumentNumber() {
        
        if(null == selectedInvoiceDocumentNumber) {
            Collection<CustomerInvoiceDocument> invoices = getInvoices();
            if(null != invoices && 0 < invoices.size()) {
                selectedInvoiceDocumentNumber = invoices.iterator().next().getDocumentNumber();
            }
        }
        
        return selectedInvoiceDocumentNumber;
    }

    public void setSelectedInvoiceDocumentNumber(String selectedInvoiceDocumentNumber) {
        this.selectedInvoiceDocumentNumber = selectedInvoiceDocumentNumber;
    }

    public void setPaymentApplicationDocumentService(PaymentApplicationDocumentService service) {
        this.paymentApplicationDocumentService = service;
    }
}
