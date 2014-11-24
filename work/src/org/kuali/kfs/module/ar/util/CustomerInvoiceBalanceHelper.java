/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
