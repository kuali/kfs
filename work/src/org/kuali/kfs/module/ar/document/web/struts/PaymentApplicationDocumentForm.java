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
package org.kuali.module.ar.web.struts.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.ar.bo.Customer;
import org.kuali.module.ar.bo.NonAppliedHolding;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.ar.service.CustomerInvoiceDocumentService;
import org.kuali.module.ar.service.CustomerService;

public class PaymentApplicationDocumentForm extends KualiAccountingDocumentFormBase {
    
    private String customerNumber;
    private String selectedInvoiceDocumentNumber;
    private Collection<NonAppliedHolding> nonAppliedHoldings;
    
    private CustomerService customerService;
    private CustomerInvoiceDocumentService invoiceService;
    private BusinessObjectService businessObjectService;

    public PaymentApplicationDocumentForm() {
        super();
        setDocument(new PaymentApplicationDocument());
        customerService = SpringContext.getBean(CustomerService.class);
        invoiceService  = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        nonAppliedHoldings = new ArrayList<NonAppliedHolding>();
    }

    /**
     * @return
     */
    public PaymentApplicationDocument getPaymentApplicationDocument() {
        return (PaymentApplicationDocument) getDocument();
    }

    /**
     * @return
     */
    public Customer getCustomer() {
        Customer customer = null;

        if(null != customerNumber) {
            customer = customerService.getByPrimaryKey(this.customerNumber);
        } else if(null != selectedInvoiceDocumentNumber) {
            customer = invoiceService.getCustomerByInvoiceDocumentNumber(this.selectedInvoiceDocumentNumber);
        }
        
        return customer;
    }

    /**
     * @return
     */
    public CustomerInvoiceDocument getSelectedInvoiceDocument() {
        CustomerInvoiceDocument invoice = null;
        
        if(null != this.selectedInvoiceDocumentNumber) {
            invoice = invoiceService.getInvoiceByInvoiceDocumentNumber(selectedInvoiceDocumentNumber);
        } else {
            Collection<CustomerInvoiceDocument> invoices = getInvoices();
            if(null != invoices) {
                invoice = invoices.iterator().next();
            }
        }
        
        return invoice;
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
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(boolean refresh) {
        
        if(refresh || nonAppliedHoldings.isEmpty()) {
            Map args = new HashMap();
            args.put("customerNumber", getCustomer().getCustomerNumber());
            nonAppliedHoldings = businessObjectService.findMatching(NonAppliedHolding.class, args);
        }
        
        return nonAppliedHoldings;
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
        this.customerNumber = customerNumber.toUpperCase();
    }

    public String getSelectedInvoiceDocumentNumber() {
        return selectedInvoiceDocumentNumber;
    }

    public void setSelectedInvoiceDocumentNumber(String selectedInvoiceDocumentNumber) {
        this.selectedInvoiceDocumentNumber = selectedInvoiceDocumentNumber;
    }

}
