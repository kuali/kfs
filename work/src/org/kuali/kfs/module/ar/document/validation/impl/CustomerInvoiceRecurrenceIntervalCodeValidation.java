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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class CustomerInvoiceRecurrenceIntervalCodeValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;

    public boolean validate(AttributedDocumentEvent event) {

        //  if there is no recurrence, then short circuit and dont validate
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails())) {
            return true;
        }
        
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceBeginDate()) &&
            ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceEndDate()) &&
            ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceIntervalCode()) &&
            ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentTotalRecurrenceNumber()) &&
            ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentInitiatorUserIdentifier())) {
            return true;
        }
        
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceIntervalCode())) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_RECURRENCE_INTERVAL_CODE, ArKeyConstants.ERROR_INVOICE_RECURRENCE_INTERVAL_CODE_IS_REQUIRED);
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
