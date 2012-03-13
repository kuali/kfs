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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceDueDateValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;
    private DateTimeService dateTimeService;
    private ParameterService parameterService;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        Timestamp dueDateTimestamp = new Timestamp(customerInvoiceDocument.getInvoiceDueDate().getTime());
        Timestamp billingDateTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());
        // test only for initial state and not for correction
        if (ObjectUtils.isNull((customerInvoiceDocument.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber())) &&
                (dueDateTimestamp.before(billingDateTimestamp) || dueDateTimestamp.equals(billingDateTimestamp))) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_BEFORE_OR_EQUAL_TO_BILLING_DATE);
            return false;
        }
        else {
            long diffInDays = getDifferenceInDays(billingDateTimestamp, dueDateTimestamp); //KfsDateUtils.getDifferenceInDays(billingDateTimestamp, dueDateTimestamp);
            int maxNumOfDaysAfterCurrentDateForInvoiceDueDate = Integer.parseInt(parameterService.getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE));
            if (diffInDays >= maxNumOfDaysAfterCurrentDateForInvoiceDueDate) {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_MORE_THAN_X_DAYS, maxNumOfDaysAfterCurrentDateForInvoiceDueDate + "");
                return false;
            }

        }
        
        return true;
    }

    /**
     * 
     * This method calculates the difference in days between the two timestamps provided.
     * 
     * This method is used instead of KfsDateUtils.getDifferenceInDays() because there is a rounding issue within the timestamp that exists which must be dealt with to 
     * prevent improper calculations.  This issue is similar to the problems that exist with adding and subtracting doubles and the inherently bad way that Java handles
     * numbers.  
     * 
     * The approach used within KfsDateUtils does not offer enough accuracy to calculate the difference consistently and accurately.
     * 
     * @param t1
     * @param t2
     * @return The difference in days between the two given timestamps.  
     */
    public static long getDifferenceInDays (Timestamp t1, Timestamp t2) {
        // Make sure the result is always > 0
        if (t1.compareTo (t2) < 0) {
            Timestamp tmp = t1;
            t1 = t2;
            t2 = tmp;
        }

        // Timestamps mix milli and nanoseconds in the API, so we have to separate the two
        long diffSeconds = (t1.getTime () / 1000) - (t2.getTime () / 1000);
        // For normals dates, we have millisecond precision
        int nano1 = ((int) t1.getTime () % 1000) * 1000000;
        nano1 = t1.getNanos ();
        int nano2 = ((int) t2.getTime () % 1000) * 1000000;
        nano2 = t2.getNanos ();

        int diffNanos = nano1 - nano2;
        if (diffNanos < 0) {
            // Borrow one second
            diffSeconds --;
            diffNanos += 1000000000;
        }

        // mix nanos and millis again
        Timestamp result = new Timestamp ((diffSeconds * 1000) + (diffNanos / 1000000));
        // setNanos() with a value of in the millisecond range doesn't affect the value of the time field
        // while milliseconds in the time field will modify nanos! Damn, this API is a *mess*
        result.setNanos (diffNanos);
        return result.getDate();
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
    
    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
