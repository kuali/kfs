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

import java.sql.Timestamp;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.ParameterService;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

public class CustomerInvoiceDueDateValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;
    private DateTimeService dateTimeService;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        Timestamp dueDateTimestamp = new Timestamp(customerInvoiceDocument.getInvoiceDueDate().getTime());
        Timestamp billingDateTimestamp = new Timestamp(dateTimeService.getCurrentSqlDate().getTime());
        
        if (dueDateTimestamp.before(billingDateTimestamp) || dueDateTimestamp.equals(billingDateTimestamp)) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + ArConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_BEFORE_OR_EQUAL_TO_BILLING_DATE);
            return false;
        }
        else {
            double diffInDays = DateUtils.getDifferenceInDays(billingDateTimestamp, dueDateTimestamp);
            int maxNumOfDaysAfterCurrentDateForInvoiceDueDate = Integer.parseInt(SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE));
            if (diffInDays >= maxNumOfDaysAfterCurrentDateForInvoiceDueDate) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + ArConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_MORE_THAN_X_DAYS, maxNumOfDaysAfterCurrentDateForInvoiceDueDate + "");
                return false;
            }

        }
        
        return true;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
