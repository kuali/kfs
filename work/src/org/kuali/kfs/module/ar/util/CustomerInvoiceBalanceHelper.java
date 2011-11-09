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
package org.kuali.kfs.module.ar.util;

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class CustomerInvoiceBalanceHelper {

    private CustomerInvoiceDocument invoice;
    private Collection<InvoicePaidApplied> invoicePaidApplieds;
    private Collection<CustomerInvoiceDetail> customerInvoiceDetails;

    public CustomerInvoiceBalanceHelper() {

    }

    public CustomerInvoiceBalanceHelper(CustomerInvoiceDocument invoice, Collection<InvoicePaidApplied> invoicePaidApplieds) {
        this.invoice = invoice;
        this.invoicePaidApplieds = new ArrayList<InvoicePaidApplied>(invoicePaidApplieds);
    }

    /**
     * This method calculates the invoice document balance as the difference between the open amount and the total applied amount
     * @return the balance of the customer invoice document
     */
    public KualiDecimal getCalculatedBalance() {
        return invoice.getTotalDollarAmount().subtract(getTotalAppliedAmountForAppDoc());
    }
    
    /**
     * This method gets the open amount of the ustomer invoice document
     * @return the open amount of the invoice
     */
    public KualiDecimal getOpenAmount() {
        CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        return customerInvoiceDocumentService.getOpenAmountForCustomerInvoiceDocument(this.invoice.getDocumentNumber());
    }
    
    /**
     * This method gets the total applied amount 
     * @return the total applied amount
     */
    public KualiDecimal getTotalAppliedAmountForAppDoc() {
        KualiDecimal appliedAmount = new KualiDecimal(0);
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        return appliedAmount;
    }
   

    /**
     * This method gets the invoice
     * @return
     */
    public CustomerInvoiceDocument getInvoice() {
        return invoice;
    }

    /**
     * This method sets the invoice
     * @param invoice
     */
    public void setInvoice(CustomerInvoiceDocument invoice) {
        this.invoice = invoice;
    }

    /**
     * This method gets the invoice paid applieds
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidApplieds() {
        return invoicePaidApplieds;
    }

    /**
     * This method sets the invoice paid applieds
     * @param invoicePaidApplieds
     */
    public void setInvoicePaidApplieds(Collection<InvoicePaidApplied> invoicePaidApplieds) {
        this.invoicePaidApplieds = invoicePaidApplieds;
    }

}
