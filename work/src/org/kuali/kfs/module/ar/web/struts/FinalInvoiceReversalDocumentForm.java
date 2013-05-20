/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.web.struts;

import org.kuali.kfs.module.ar.businessobject.FinalInvoiceReversalEntry;
import org.kuali.kfs.module.ar.document.FinalInvoiceReversalDocument;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

/**
 * Form class for Final Invoice Reversal Document.
 */
public class FinalInvoiceReversalDocumentForm extends KualiTransactionalDocumentFormBase {
    private FinalInvoiceReversalEntry invoiceEntry;

    // this in sync with the default
    // value set in the document business object
    public FinalInvoiceReversalDocumentForm() {
        super();
    }

    /**
     * @return FinalInvoiceReversalDocument
     */
    public FinalInvoiceReversalDocument getFinalInvoiceReversalDocument() {
        return (FinalInvoiceReversalDocument) getDocument();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return "FIR";
    }

    /**
     * Gets the invoiceEntry attribute.
     * 
     * @return Returns the invoiceEntry.
     */
    public FinalInvoiceReversalEntry getInvoiceEntry() {
        return invoiceEntry;
    }

    /**
     * Sets the invoiceEntry attribute value.
     * 
     * @param invoiceEntry The invoiceEntry to set.
     */
    public void setInvoiceEntry(FinalInvoiceReversalEntry invoiceEntry) {
        this.invoiceEntry = invoiceEntry;
    }

}