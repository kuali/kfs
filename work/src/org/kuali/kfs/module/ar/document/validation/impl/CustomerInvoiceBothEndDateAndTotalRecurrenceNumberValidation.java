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

import java.sql.Date;
import java.util.Calendar;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceBothEndDateAndTotalRecurrenceNumberValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;

    public boolean validate(AttributedDocumentEvent event) {
        
        // short circuit if no recurrence object at all
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails())) {
            return true;
        }
        
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceBeginDate()) || 
            ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceIntervalCode()) ||
            ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceEndDate()) ||
            ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentTotalRecurrenceNumber())) {
            return true;
        }

        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceBeginDate());
        Date beginDate = customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceBeginDate();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceEndDate());
        Date endDate = customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceEndDate();
        Calendar nextCalendar = Calendar.getInstance();
        Date nextDate = beginDate;
                 
        int totalRecurrences = 0;
        int addCounter = 0;
        String intervalCode = customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceIntervalCode();
        if (intervalCode.equals("M")) {
            addCounter = 1;
        }
        if (intervalCode.equals("Q")) {
            addCounter = 3;
        }
        /* perform this loop while begin_date is less than or equal to end_date */
        while (!(beginDate.after(endDate))){
            beginCalendar.setTime(beginDate);
            beginCalendar.add(Calendar.MONTH, addCounter);
            beginDate = KfsDateUtils.convertToSqlDate(beginCalendar.getTime());
            totalRecurrences++;
            nextDate = beginDate;
            nextCalendar.setTime(nextDate);
            nextCalendar.add(Calendar.MONTH, addCounter);
            nextDate = KfsDateUtils.convertToSqlDate(nextCalendar.getTime());
            if (endDate.after(beginDate) && endDate.before(nextDate)) {
                totalRecurrences++;
                break;
            }
        }
        if (totalRecurrences != customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentTotalRecurrenceNumber().intValue()) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_RECURRENCE_END_DATE, ArKeyConstants.ERROR_END_DATE_AND_TOTAL_NUMBER_OF_RECURRENCES_NOT_VALID);
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
