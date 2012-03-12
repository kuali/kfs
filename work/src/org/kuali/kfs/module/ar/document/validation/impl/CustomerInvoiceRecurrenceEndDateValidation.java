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

import java.sql.Timestamp;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceRecurrenceEndDateValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;

    public boolean validate(AttributedDocumentEvent event) {
        
        // short circuit if no recurrence object at all
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails())) {
            return true;
        }
        
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceBeginDate()) || 
            ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceEndDate())) {
            return true;
        }
        Timestamp beginDateTimestamp = new Timestamp(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceBeginDate().getTime());
        Timestamp endDateTimestamp = new Timestamp(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceEndDate().getTime());
        if ((ObjectUtils.isNotNull(endDateTimestamp)) &&
            (endDateTimestamp.before(beginDateTimestamp) || endDateTimestamp.equals(beginDateTimestamp))) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_RECURRENCE_END_DATE, ArKeyConstants.ERROR_END_DATE_EARLIER_THAN_BEGIN_DATE);
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
