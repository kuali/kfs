/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TmExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class TmExpenseTotalsValidation extends GenericValidation {

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
            /*
             * Determine if the detail is an amount that doesn't go over the threshold
             */
            KualiDecimal total = KualiDecimal.ZERO;
            for (TmExpense detail : importedExpense.getExpenseDetails()) {
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

        return rulePassed;
    }

}
