/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * validates that the date of the actual expense occurred before either the end date of the document or before the initiation date of the document
 */
public class ActualExpenseDatesValidation extends GenericValidation {
    protected ActualExpense actualExpenseForValidation;
    protected DateTimeService dateTimeService;
    protected DataDictionaryService dataDictionaryService;

    /**
     * True if the date of the actual expense is valid, false otherwise
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        if (getActualExpenseForValidation().getExpenseDate() != null) {
            final TravelDocument travelDocument = (TravelDocument)event.getDocument();
            Calendar messageDate = null; // the date we'll put in our message to be earlier than
            String messageProperty = null; // the label of the property to be earlier than

            Calendar expenseDate = Calendar.getInstance();
            expenseDate.setTimeInMillis(getActualExpenseForValidation().getExpenseDate().getTime());

            if (travelDocument.getTripEnd() != null) {
                Calendar tripEndDate = Calendar.getInstance();
                tripEndDate.setTimeInMillis(travelDocument.getTripEnd().getTime());
                if (DateUtils.truncatedCompareTo(expenseDate, tripEndDate, Calendar.DATE) <= 0) {
                    return true; // no error here, because expense date is less than trip end date
                }
                messageDate = tripEndDate;
                messageProperty = getDataDictionaryService().getAttributeLabel(travelDocument.getClass(), TemPropertyConstants.TRIP_END_DT);
            }

            // still here?  let's check initiation date
            if (travelDocument.getDocumentHeader().getWorkflowDocument().getDateCreated() != null) { // how it would be null, I have no idea
                Calendar initiationDate = Calendar.getInstance();
                initiationDate.setTimeInMillis(travelDocument.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());

                if (DateUtils.truncatedCompareTo(expenseDate, initiationDate, Calendar.DATE) > 0) {
                    if (messageDate == null) {
                        messageDate = initiationDate;
                        messageProperty = getDataDictionaryService().getAttributeLabel(travelDocument.getDocumentHeader().getWorkflowDocument().getClass(), KFSPropertyConstants.CREATE_DATE);
                    }

                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_DATE, TemKeyConstants.ERROR_ACTUAL_EXPENSE_EXPENSE_DATE_EARLY, getDateTimeService().toDateString(expenseDate.getTime()), messageProperty, getDateTimeService().toDateString(messageDate.getTime()));
                    return false;
                }

            }
        }

        return true;
    }

    public ActualExpense getActualExpenseForValidation() {
        return actualExpenseForValidation;
    }

    public void setActualExpenseForValidation(ActualExpense actualExpenseForValidation) {
        this.actualExpenseForValidation = actualExpenseForValidation;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
