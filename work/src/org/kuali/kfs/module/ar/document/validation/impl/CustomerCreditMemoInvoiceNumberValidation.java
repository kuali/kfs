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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerCreditMemoInvoiceNumberValidation extends GenericValidation {

    private CustomerCreditMemoDocument customerCreditMemoDocument;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    
    public boolean validate(AttributedDocumentEvent event) {
    
        String invDocumentNumber = customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber();
        
        if (ObjectUtils.isNull(invDocumentNumber) || StringUtils.isBlank(invDocumentNumber)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT__INVOICE_DOCUMENT_NUMBER_IS_REQUIRED);
            return false;
        } else {    
            CustomerInvoiceDocumentService service = SpringContext.getBean(CustomerInvoiceDocumentService.class);
            CustomerInvoiceDocument customerInvoiceDocument = service.getInvoiceByInvoiceDocumentNumber(invDocumentNumber);
        
            if (ObjectUtils.isNull(customerInvoiceDocument)) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVALID_INVOICE_DOCUMENT_NUMBER);
                return false;
            }
        }
        return true;
    
    }

    public CustomerCreditMemoDocument getCustomerCreditMemoDocument() {
        return customerCreditMemoDocument;
    }

    public void setCustomerCreditMemoDocument(CustomerCreditMemoDocument customerCreditMemoDocument) {
        this.customerCreditMemoDocument = customerCreditMemoDocument;
    }

    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }    
    
}
