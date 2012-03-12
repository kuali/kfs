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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceCustomerNumberValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;

    public boolean validate(AttributedDocumentEvent event) {
        
        customerInvoiceDocument.getAccountsReceivableDocumentHeader().refreshReferenceObject(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER);
        Customer customer = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomer();
        if (ObjectUtils.isNull(customer) || !customer.isActive()) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CUSTOMER_NUMBER);
            return false;
        }

        return true;
    }
    
    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }
}
