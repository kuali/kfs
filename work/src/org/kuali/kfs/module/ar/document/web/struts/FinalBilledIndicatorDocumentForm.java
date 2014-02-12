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
package org.kuali.kfs.module.ar.document.web.struts;

import org.kuali.kfs.module.ar.businessobject.FinalBilledIndicatorEntry;
import org.kuali.kfs.module.ar.document.FinalBilledIndicatorDocument;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

/**
 * Form class for Final Billed Indicator Document.
 */
public class FinalBilledIndicatorDocumentForm extends KualiTransactionalDocumentFormBase {
    private FinalBilledIndicatorEntry invoiceEntry;

    // this in sync with the default
    // value set in the document business object
    public FinalBilledIndicatorDocumentForm() {
        super();
    }

    /**
     * @return FinalBilledIndicatorDocument
     */
    public FinalBilledIndicatorDocument getFinalBilledIndicatorDocument() {
        return (FinalBilledIndicatorDocument) getDocument();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return "FBI";
    }

    /**
     * Gets the invoiceEntry attribute.
     *
     * @return Returns the invoiceEntry.
     */
    public FinalBilledIndicatorEntry getInvoiceEntry() {
        return invoiceEntry;
    }

    /**
     * Sets the invoiceEntry attribute value.
     *
     * @param invoiceEntry The invoiceEntry to set.
     */
    public void setInvoiceEntry(FinalBilledIndicatorEntry invoiceEntry) {
        this.invoiceEntry = invoiceEntry;
    }

}