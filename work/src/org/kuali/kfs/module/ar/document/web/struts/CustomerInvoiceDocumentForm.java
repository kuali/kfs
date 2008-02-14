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
package org.kuali.module.ar.web.struts.form;

import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;

public class CustomerInvoiceDocumentForm extends KualiAccountingDocumentFormBase {
    
    private CustomerInvoiceDetail newCustomerInvoiceDetail; 
    
    /**
     * Constructs a CustomerInvoiceDocumentForm.java.  Also sets new customer invoice document detail to a newly constructed customer invoice detail. 
     */
    public CustomerInvoiceDocumentForm() {
        super();
        setDocument(new CustomerInvoiceDocument());
    }

    public CustomerInvoiceDetail getNewCustomerInvoiceDetail() {
        return newCustomerInvoiceDetail;
    }

    public void setNewCustomerInvoiceDetail(CustomerInvoiceDetail newCustomerInvoiceDetail) {
        this.newCustomerInvoiceDetail = newCustomerInvoiceDetail;
    }
    
    public CustomerInvoiceDocument getCustomerInvoiceDocument( ) {
        return (CustomerInvoiceDocument)getDocument();
    }
    
    /**
     * By overriding this method, we can add the invoice total amount to the document header
     * 
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo1()
     */
    @Override
    public KeyLabelPair getAdditionalDocInfo2() {
        return new KeyLabelPair("DataDictionary.CustomerInvoiceDocument.attributes.invoiceTotalAmount", (String)new CurrencyFormatter().format(getCustomerInvoiceDocument().getInvoiceTotalAmount()));
    }
}