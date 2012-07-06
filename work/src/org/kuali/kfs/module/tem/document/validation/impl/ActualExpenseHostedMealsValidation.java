/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.validation.impl;

import static java.lang.String.format;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_TR_MEAL_ALREADY_CLAIMED;
import static org.kuali.kfs.module.tem.TemPropertyConstants.ACTUAL_EXPENSES;
import static org.kuali.kfs.sys.KFSConstants.DOCUMENT_PROPERTY_NAME;
import static org.kuali.rice.kns.util.GlobalVariables.getMessageMap;

import java.text.SimpleDateFormat;

import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseLineEvent;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * Checks if a meal was already selected in perdiem if it is, then a hosted meal cannot be added.
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class ActualExpenseHostedMealsValidation extends GenericValidation {
    private static final String OTHER_EXPENSE_ERROR_KEY_FMT = "%s.%s";


    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        
        final ActualExpense actualExpense = (ActualExpense) ((AddActualExpenseLineEvent)event).getExpenseLine();
        final TravelReimbursementDocument reimbursement = (TravelReimbursementDocument) event.getDocument();
        if(reimbursement.getPerDiemExpenses() != null){
            for (final PerDiemExpense perDiemExpense : reimbursement.getPerDiemExpenses()) {
                final String mileageDate = new SimpleDateFormat("MM/dd/yyyy").format(perDiemExpense.getMileageDate());
                final String expenseDate = new SimpleDateFormat("MM/dd/yyyy").format(actualExpense.getExpenseDate());
                String meal = "";
                
                if (mileageDate.equals(expenseDate)) {
                    if (perDiemExpense.getBreakfast() && actualExpense.isHostedBreakfast()) {
                        valid = false;
                        meal = "breakfast";
                    } 
                    else if (perDiemExpense.getLunch() && actualExpense.isHostedLunch()) {
                        valid = false;
                        meal = "lunch";
                    }
                    else if (perDiemExpense.getDinner() && actualExpense.isHostedDinner()) {
                        valid = false;
                        meal = "dinner";
                    }
                    
                    if (!valid) {
                        final String key = format(OTHER_EXPENSE_ERROR_KEY_FMT, DOCUMENT_PROPERTY_NAME, ACTUAL_EXPENSES);
                        getMessageMap().putError(key, MESSAGE_TR_MEAL_ALREADY_CLAIMED, expenseDate, meal);    
                    }
                }            
            }
        }
        return valid;
    }
}
