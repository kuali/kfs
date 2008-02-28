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

import java.util.Map;

import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;

public class CustomerInvoiceDocumentForm extends KualiAccountingDocumentFormBase {
    
    private CustomerInvoiceDetail newCustomerInvoiceDetail; 
    
    /**
     * Constructs a CustomerInvoiceDocumentForm.java.  Also sets new customer invoice document detail to a newly constructed customer invoice detail. 
     */
    public CustomerInvoiceDocumentForm() {
        super();
        setDocument(new CustomerInvoiceDocument());
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
    
    
    /**
     * Reused to create new source accounting line (i.e customer invoice detail line) with defaulted values.
     * 
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#createNewSourceAccountingLine(org.kuali.kfs.document.AccountingDocument)
     */
    @Override
    protected SourceAccountingLine createNewSourceAccountingLine(AccountingDocument financialDocument) {
        if (financialDocument == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        try {

            CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
            return (SourceAccountingLine) customerInvoiceDetailService.getAddLineCustomerInvoiceDetailForCurrentUserAndYear();
        }
        catch (Exception e) {
            throw new InfrastructureException("unable to create a new source accounting line", e);
        }
    }    
    
    
    /**
     * Configure lookup for Invoice Item Code source accounting line
     * 
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#getForcedLookupOptionalFields()
     */
    @Override
    public Map getForcedLookupOptionalFields() {
        Map forcedLookupOptionalFields = super.getForcedLookupOptionalFields();

        String lookupField = ArConstants.CUSTOMER_INVOICE_DOCUMENT_INVOICE_ITEM_CODE_PROPERTY;
        forcedLookupOptionalFields.put(lookupField, lookupField + ";" + CustomerInvoiceItemCode.class.getName());

        return forcedLookupOptionalFields;
    }    
}