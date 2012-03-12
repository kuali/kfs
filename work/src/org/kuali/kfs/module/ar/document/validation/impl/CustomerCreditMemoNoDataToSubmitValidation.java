/*
 * Copyright 2008-2009 The Kuali Foundation
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

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class CustomerCreditMemoNoDataToSubmitValidation extends GenericValidation {

    private CustomerCreditMemoDocument customerCreditMemoDocument;
    private CustomerCreditMemoDocumentService customerCreditMemoDocumentService;
    
    public boolean validate(AttributedDocumentEvent event) {
    
        if (customerCreditMemoDocumentService.isThereNoDataToSubmit(customerCreditMemoDocument)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_NO_DATA_TO_SUBMIT);
            return false;
        }
        return true;
    }

    public CustomerCreditMemoDocument getCustomerCreditMemoDocument() {
        return customerCreditMemoDocument;
    }

    public void setCustomerCreditMemoDocument(CustomerCreditMemoDocument customerCreditMemoDocument) {
        this.customerCreditMemoDocument = customerCreditMemoDocument;
    }

    public CustomerCreditMemoDocumentService getCustomerCreditMemoDocumentService() {
        return customerCreditMemoDocumentService;
    }

    public void setCustomerCreditMemoDocumentService(CustomerCreditMemoDocumentService customerCreditMemoDocumentService) {
        this.customerCreditMemoDocumentService = customerCreditMemoDocumentService;
    }    
    
}
