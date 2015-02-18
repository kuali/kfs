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
package org.kuali.kfs.module.ar.document.web.struts;

import org.kuali.kfs.module.ar.ArConstants;
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
        return ArConstants.FINAL_BILLED_INDICATOR_DOCUMENT_TYPE_CODE;
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
