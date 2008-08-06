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

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.util.CustomerInvoiceBalanceHelper;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

public class PaymentApplicationDocumentForm extends FinancialSystemTransactionalDocumentFormBase {

    private String selectedInvoiceDocumentNumber;
    private String enteredInvoiceDocumentNumber;
    private KualiDecimal unappliedCustomerAmount;
    private KualiDecimal selectedInvoiceTotalAmount;
    private KualiDecimal selectedInvoiceBalance;
    private KualiDecimal amountAppliedDirectlyToInvoice;
    private CustomerInvoiceDocument selectedInvoiceDocument;

    private ArrayList<CustomerInvoiceDetail> customerInvoiceDetails;
    private ArrayList<CustomerInvoiceDocument> invoices;
    private Map<String, Collection> appliedPaymentsPerCustomerInvoiceDetail;

    /**
     * Constructs a PaymentApplicationDocumentForm.java.
     */
    public PaymentApplicationDocumentForm() {
        super();
        setDocument(new PaymentApplicationDocument());

        customerInvoiceDetails = new ArrayList<CustomerInvoiceDetail>();
        invoices = new ArrayList<CustomerInvoiceDocument>();
        selectedInvoiceDocument = new CustomerInvoiceDocument();

        appliedPaymentsPerCustomerInvoiceDetail = new HashMap<String, Collection>();
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
    public Collection<CustomerInvoiceBalanceHelper> getUpdatedBalanceInvoices() {
        Collection<CustomerInvoiceBalanceHelper> invoices = new ArrayList<CustomerInvoiceBalanceHelper>();
        for (CustomerInvoiceDocument invoice : getInvoices()) {
            // /!! get invoice paidapplieds for invoice
            invoices.add(new CustomerInvoiceBalanceHelper(invoice, new ArrayList<InvoicePaidApplied>()));
        }
        return invoices;
    }

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

    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetails() {
        return customerInvoiceDetails;
    }

    public Collection<CustomerInvoiceDocument> getInvoices() {
        return invoices;
    }

    public CustomerInvoiceDocument getSelectedInvoiceDocument() {
        return selectedInvoiceDocument;
    }

    public void setSelectedInvoiceDocument(CustomerInvoiceDocument selectedInvoiceDocument) {
        this.selectedInvoiceDocument = selectedInvoiceDocument;
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

    public void setCustomerInvoiceDetails(ArrayList<CustomerInvoiceDetail> customerInvoiceDetails) {
        this.customerInvoiceDetails = customerInvoiceDetails;
    }


    /**
     * This method retrieves a specific customer invoice detail from the list, by array index
     * 
     * @param index the index of the customer invoice detail to retrieve
     * @return a CustomerInvoiceDetail
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetail(int index) {
        if (index >= customerInvoiceDetails.size()) {
            for (int i = customerInvoiceDetails.size(); i <= index; i++) {
                customerInvoiceDetails.add(new CustomerInvoiceDetail());
            }
        }
        return (CustomerInvoiceDetail) customerInvoiceDetails.get(index);
    }

    /**
     * This method retrieves a specific customer invoice from the list, by array index
     * 
     * @param index the index of the customer invoice to retrieve
     * @return a CustomerInvoiceDocument
     */
    public CustomerInvoiceDocument getCustomerInvoiceDocument(int index) {
        if (index >= invoices.size()) {
            for (int i = invoices.size(); i <= index; i++) {
                invoices.add(new CustomerInvoiceDocument());
            }
        }
        return (CustomerInvoiceDocument) invoices.get(index);
    }

    public void setCustomerInvoiceDetail(int key, CustomerInvoiceDetail value) {
        customerInvoiceDetails.set(key, value);
    }

    public KualiDecimal getSelectedInvoiceBalance() {
        return selectedInvoiceBalance;
    }

    public void setSelectedInvoiceBalance(KualiDecimal selectedInvoiceBalance) {
        this.selectedInvoiceBalance = selectedInvoiceBalance;
    }

    public KualiDecimal getSelectedInvoiceTotalAmount() {
        return selectedInvoiceTotalAmount;
    }

    public void setSelectedInvoiceTotalAmount(KualiDecimal selectedInvoiceTotalAmount) {
        this.selectedInvoiceTotalAmount = selectedInvoiceTotalAmount;
    }

    public KualiDecimal getAmountAppliedDirectlyToInvoice() {
        return amountAppliedDirectlyToInvoice;
    }

    public void setAmountAppliedDirectlyToInvoice(KualiDecimal amountAppliedDirectlyToInvoice) {
        this.amountAppliedDirectlyToInvoice = amountAppliedDirectlyToInvoice;
    }

    public Map<String, Collection> getAppliedPaymentsPerCustomerInvoiceDetail() {
        return appliedPaymentsPerCustomerInvoiceDetail;
    }

    public void setAppliedPaymentsPerCustomerInvoiceDetail(Map<String, Collection> appliedPaymentsPerCustomerInvoiceDetail) {
        this.appliedPaymentsPerCustomerInvoiceDetail = appliedPaymentsPerCustomerInvoiceDetail;
    }
    
    /**
     * This method gets the previous invoice document number
     * 
     * @return the previous invoice document number
     */
    public String getPreviousInvoiceDocumentNumber() {
        CustomerInvoiceDocument _previousInvoiceDocument = null;

        CustomerInvoiceDocument selectedInvoiceDocument = getSelectedInvoiceDocument();
        if (null == selectedInvoiceDocument || 2 > getInvoices().size()) {
            _previousInvoiceDocument = null;
        }
        else {
            Iterator<CustomerInvoiceDocument> iterator = getInvoices().iterator();
            CustomerInvoiceDocument previousInvoiceDocument = iterator.next();
            String selectedInvoiceDocumentNumber = selectedInvoiceDocument.getDocumentNumber();
            if (null != selectedInvoiceDocumentNumber && selectedInvoiceDocumentNumber.equals(previousInvoiceDocument.getDocumentNumber())) {
                _previousInvoiceDocument = null;
            }
            else {
                while (iterator.hasNext()) {
                    CustomerInvoiceDocument currentInvoiceDocument = iterator.next();
                    String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                    if (null != currentInvoiceDocumentNumber && currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
                        _previousInvoiceDocument = previousInvoiceDocument;
                    }
                    else {
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

        CustomerInvoiceDocument selectedInvoiceDocument = getSelectedInvoiceDocument();
        if (null == selectedInvoiceDocument || 2 > getInvoices().size()) {
            _nextInvoiceDocument = null;
        }
        else {
            Iterator<CustomerInvoiceDocument> iterator = getInvoices().iterator();
            while (iterator.hasNext()) {
                CustomerInvoiceDocument currentInvoiceDocument = iterator.next();
                String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                if (currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
                    if (iterator.hasNext()) {
                        _nextInvoiceDocument = iterator.next();
                    }
                    else {
                        _nextInvoiceDocument = null;
                    }
                }
            }
        }

        return null == _nextInvoiceDocument ? "" : _nextInvoiceDocument.getDocumentNumber();
    }

}
