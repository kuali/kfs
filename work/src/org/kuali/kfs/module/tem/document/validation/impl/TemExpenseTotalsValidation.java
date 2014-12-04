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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class TemExpenseTotalsValidation extends GenericValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        TravelDocument travelDocument = (TravelDocument) event.getDocument();
        GlobalVariables.getMessageMap().addToErrorPath(KRADPropertyConstants.DOCUMENT);
        //Actual Expenses
        int counter = 0;
        for (ActualExpense actualExpense : travelDocument.getActualExpenses()){
            String property = TemPropertyConstants.ACTUAL_EXPENSES + "[" + counter +"]";
            /*
             * Determine if the detail is an amount that doesn't go over the threshold
             */
            KualiDecimal total = actualExpense.getTotalDetailExpenseAmount();
            if (!total.isZero()) {
                if (total.isGreaterThan(actualExpense.getExpenseAmount())){
                    GlobalVariables.getMessageMap().putError(property + "." + TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_GREATER_THAN_EXPENSE);
                    rulePassed = false;
                }
                else if (total.isLessThan(actualExpense.getExpenseAmount())){
                    GlobalVariables.getMessageMap().putError(property + "." + TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_LESS_THAN_EXPENSE);
                    rulePassed = false;
                }

            }
            counter++;
        }

        //Imported Expenses
        counter = 0;
        for (ImportedExpense importedExpense : travelDocument.getImportedExpenses()){
            String property = TemPropertyConstants.IMPORTED_EXPENSES + "[" + counter +"]";

             //Determine if the detail is an amount that doesn't go over the threshold
            KualiDecimal total = KualiDecimal.ZERO;
            for (TemExpense detail : importedExpense.getExpenseDetails()) {
                total = total.add(detail.getExpenseAmount());
            }
            if (!total.isZero()) {
                if (total.isGreaterThan(importedExpense.getExpenseAmount())){
                    GlobalVariables.getMessageMap().putError(property + "." + TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_GREATER_THAN_EXPENSE);
                    rulePassed = false;
                }
                else if (total.isLessThan(importedExpense.getExpenseAmount())){
                    GlobalVariables.getMessageMap().putError(property + "." + TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_LESS_THAN_EXPENSE);
                    rulePassed = false;
                }

            }
            counter++;
        }

        GlobalVariables.getMessageMap().clearErrorPath();

        return rulePassed;
    }

}
