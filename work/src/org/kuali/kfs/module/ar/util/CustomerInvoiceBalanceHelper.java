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
package org.kuali.kfs.module.ar.util;

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

public class CustomerInvoiceBalanceHelper {

    private CustomerInvoiceDocument invoice;
    private Collection<InvoicePaidApplied> invoicePaidApplieds;

    public CustomerInvoiceBalanceHelper() {

    }

    public CustomerInvoiceBalanceHelper(CustomerInvoiceDocument invoice, Collection<InvoicePaidApplied> invoicePaidApplieds) {
        this.invoice = invoice;
        this.invoicePaidApplieds = new ArrayList<InvoicePaidApplied>(invoicePaidApplieds);
    }

    public KualiDecimal getCalculatedBalance() {
        return invoice.getBalance().subtract(getTotalAppliedAmount());
    }

    public KualiDecimal getTotalAppliedAmount() {
        KualiDecimal appliedAmount = new KualiDecimal(0);
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        return appliedAmount;
    }

    public CustomerInvoiceDocument getInvoice() {
        return invoice;
    }

    public void setInvoice(CustomerInvoiceDocument invoice) {
        this.invoice = invoice;
    }

    public Collection<InvoicePaidApplied> getInvoicePaidApplieds() {
        return invoicePaidApplieds;
    }

    public void setInvoicePaidApplieds(Collection<InvoicePaidApplied> invoicePaidApplieds) {
        this.invoicePaidApplieds = invoicePaidApplieds;
    }

}
