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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.UnknownDocumentIdException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

public class InvoiceRecurrencePreRules extends PromptBeforeValidationBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceRecurrencePreRules.class);
    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = true;
        preRulesOK &= setCustomerNumberIfInvoiceIsEntered(document);
        preRulesOK &= setEndDateIfTotalRecurrenceNumberIsEntered(document);
        preRulesOK &= setTotalRecurrenceNumberIfEndDateIsEntered(document);
        return preRulesOK;
    }

    /**
     * @param document the maintenance document
     * @return
     */
    protected boolean setCustomerNumberIfInvoiceIsEntered(Document document) {


        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        InvoiceRecurrence newInvoiceRecurrence = (InvoiceRecurrence) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        if (ObjectUtils.isNull(newInvoiceRecurrence.getInvoiceNumber()) ||
            ObjectUtils.isNotNull(newInvoiceRecurrence.getCustomerNumber())) {
            return true;
        }
        
        try {
            if ( SpringContext.getBean(DocumentService.class).documentExists(newInvoiceRecurrence.getInvoiceNumber()) ) {
                CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(newInvoiceRecurrence.getInvoiceNumber());
                newInvoiceRecurrence.setCustomerNumber(customerInvoiceDocument.getCustomer().getCustomerNumber());
            }
        } catch (WorkflowException ex ) {
            LOG.error( "Unable to retrieve document " + newInvoiceRecurrence.getInvoiceNumber() + " from workflow.", ex );
        } catch ( UnknownDocumentIdException ex ) {
            LOG.error( "Document " + newInvoiceRecurrence.getInvoiceNumber() + " does not exist." );
        }
        
        return true;
    }

    /**
     * This method checks if there is another customer with the same name and generates yes/no question
     * 
     * @param document the maintenance document
     * @return
     */
    protected boolean setEndDateIfTotalRecurrenceNumberIsEntered(Document document) {


        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        InvoiceRecurrence newInvoiceRecurrence = (InvoiceRecurrence) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        if (ObjectUtils.isNull(newInvoiceRecurrence.getDocumentTotalRecurrenceNumber())) {
            return true;
        }
        if (ObjectUtils.isNotNull(newInvoiceRecurrence.getDocumentRecurrenceEndDate()) && ObjectUtils.isNotNull(newInvoiceRecurrence.getDocumentTotalRecurrenceNumber())) {
            return true;
        }
        
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(new Timestamp(newInvoiceRecurrence.getDocumentRecurrenceBeginDate().getTime()));
        Calendar endCalendar = Calendar.getInstance();
        endCalendar = beginCalendar;
        
        int addCounter = 0;
        Integer documentTotalRecurrenceNumber = newInvoiceRecurrence.getDocumentTotalRecurrenceNumber();
        String intervalCode = newInvoiceRecurrence.getDocumentRecurrenceIntervalCode();
        if (intervalCode.equals("M")) {
            addCounter = -1;
            addCounter += documentTotalRecurrenceNumber * 1;
        }
        if (intervalCode.equals("Q")) {
            addCounter = -3;
            addCounter += documentTotalRecurrenceNumber * 3;
        }
        endCalendar.add(Calendar.MONTH, addCounter);
        newInvoiceRecurrence.setDocumentRecurrenceEndDate(KfsDateUtils.convertToSqlDate(endCalendar.getTime()));
        
        return true;
    }

    /**
     * This method calculates the total number of recurrences when a begin date and end date is entered.
     * 
     * @param document the maintenance document
     * @return
     */
    protected boolean setTotalRecurrenceNumberIfEndDateIsEntered(Document document) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        InvoiceRecurrence newInvoiceRecurrence = (InvoiceRecurrence) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        
        if (ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceEndDate())) {
            return true;            
        }
        if (ObjectUtils.isNotNull(newInvoiceRecurrence.getDocumentRecurrenceEndDate()) && ObjectUtils.isNotNull(newInvoiceRecurrence.getDocumentTotalRecurrenceNumber())) {
            return true;
        }
        
        Calendar beginCalendar = Calendar.getInstance();
/*
 *      beginCalendar.setTime(new Timestamp(newInvoiceRecurrence.getDocumentRecurrenceBeginDate().getTime()));
 */
        beginCalendar.setTime(newInvoiceRecurrence.getDocumentRecurrenceBeginDate());
        Date beginDate = newInvoiceRecurrence.getDocumentRecurrenceBeginDate();
        Calendar endCalendar = Calendar.getInstance();
/*
 *      endCalendar.setTime(new Timestamp(newInvoiceRecurrence.getDocumentRecurrenceEndDate().getTime()));
 */
        endCalendar.setTime(newInvoiceRecurrence.getDocumentRecurrenceEndDate());
        Date endDate = newInvoiceRecurrence.getDocumentRecurrenceEndDate();
        Calendar nextCalendar = Calendar.getInstance();
        Date nextDate = beginDate;
        
        int totalRecurrences = 0;
        int addCounter = 0;
        String intervalCode = newInvoiceRecurrence.getDocumentRecurrenceIntervalCode();
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
        if (totalRecurrences > 0) {
            newInvoiceRecurrence.setDocumentTotalRecurrenceNumber(totalRecurrences);
        }
        return true;
    }

}
