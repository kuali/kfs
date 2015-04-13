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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceWriteoffExplanationValidation extends GenericValidation {

    private CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument;
    
    public boolean validate(AttributedDocumentEvent event) {
    
        String explanation = customerInvoiceWriteoffDocument.getDocumentHeader().getExplanation();
        if (ObjectUtils.isNull(explanation) || StringUtils.isEmpty(explanation.trim())) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_WRITEOFF_EXPLANATION, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_EMPTY_EXPLANATION);
            return false;
        } else if (explanation.trim().length() < 10) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_WRITEOFF_EXPLANATION, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_INVALID_EXPLANATION);
        }
        return true;
    }

    public CustomerInvoiceWriteoffDocument getCustomerInvoiceWriteoffDocument() {
        return customerInvoiceWriteoffDocument;
    }

    public void setCustomerInvoiceWriteoffDocument(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument) {
        this.customerInvoiceWriteoffDocument = customerInvoiceWriteoffDocument;
    }
    
}
