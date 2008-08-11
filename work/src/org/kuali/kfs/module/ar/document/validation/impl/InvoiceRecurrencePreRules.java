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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.context.SpringContext;

import edu.iu.uis.eden.exception.WorkflowException;

public class InvoiceRecurrencePreRules extends PreRulesContinuationBase {

    /**
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean doRules(Document document) {
        boolean preRulesOK = true;
        preRulesOK &= setCustomerNumberIfInvoiceIsEntered(document);
        preRulesOK &= setEndDateIfTotalRecurrenceNumberIsEntered(document);
        preRulesOK &= setTotalRecurrenceNumberIfEndDateIsEntered(document);
        return preRulesOK;
    }

    /**
     * This method checks if there is another customer with the same name and generates yes/no question
     * 
     * @param document the maintenance document
     * @return
     */
    private boolean setCustomerNumberIfInvoiceIsEntered(Document document) {


        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        InvoiceRecurrence newInvoiceRecurrence = (InvoiceRecurrence) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        if (ObjectUtils.isNull(newInvoiceRecurrence.getInvoiceNumber())) {
            return true;
        }
        
        CustomerInvoiceDocument customerInvoiceDocument = null;
        try {
            customerInvoiceDocument = (CustomerInvoiceDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(newInvoiceRecurrence.getInvoiceNumber());
        } catch (WorkflowException e){
        }
        
        newInvoiceRecurrence.setCustomerNumber(customerInvoiceDocument.getCustomer().getCustomerNumber());
        return true;
    }

    /**
     * This method checks if there is another customer with the same name and generates yes/no question
     * 
     * @param document the maintenance document
     * @return
     */
    private boolean setEndDateIfTotalRecurrenceNumberIsEntered(Document document) {


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
        newInvoiceRecurrence.setDocumentRecurrenceEndDate(DateUtils.convertToSqlDate(new Date(endCalendar.getTime().getTime())));
        
        return true;
    }

    /**
     * This method calculates the total number of recurrences when a begin date and end date is entered.
     * 
     * @param document the maintenance document
     * @return
     */
    private boolean setTotalRecurrenceNumberIfEndDateIsEntered(Document document) {

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
            beginDate = new Date(beginCalendar.getTime().getTime());
            totalRecurrences++;

            nextDate = beginDate;
            nextCalendar.setTime(nextDate);
            nextCalendar.add(Calendar.MONTH, addCounter);
            nextDate = new Date(nextCalendar.getTime().getTime());
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
