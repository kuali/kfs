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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceMaximumNumberOfRecurrencesValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;

    public boolean validate(AttributedDocumentEvent event) {
        
        // short circuit if no recurrence object at all
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails())) {
            return true;
        }
        
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceIntervalCode()) ||
            ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentTotalRecurrenceNumber())) {
            return true;
        }

        boolean success = true;
        Integer maximumRecurrencesByInterval;
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceIntervalCode())) {
            List<String> maximumRecurrences = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getSubParameterValuesAsString(InvoiceRecurrence.class, ArConstants.MAXIMUM_RECURRENCES_BY_INTERVAL, customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentRecurrenceIntervalCode()) );
            if (maximumRecurrences.size() > 0 && StringUtils.isNotBlank(maximumRecurrences.get(0))) {
                maximumRecurrencesByInterval = Integer.valueOf(maximumRecurrences.get(0));
                if (customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentTotalRecurrenceNumber() > maximumRecurrencesByInterval) {
                    GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_RECURRENCE_TOTAL_RECURRENCE_NUMBER, ArKeyConstants.ERROR_TOTAL_NUMBER_OF_RECURRENCES_GREATER_THAN_ALLOWED, maximumRecurrences.get(0));
                    return false;
                }
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
}
