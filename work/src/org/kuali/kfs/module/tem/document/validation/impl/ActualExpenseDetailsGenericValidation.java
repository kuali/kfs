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

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation which will attempt to generically validate all of the details in an actual expense
 */
public class ActualExpenseDetailsGenericValidation extends GenericValidation {
    protected ActualExpense actualExpenseForValidation;
    protected List<? extends ActualExpenseDetailValidation> detailValidations;

    /**
     * For each detail on the actual expense, runs through each of the given detailValidations
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;

        int count = 0;
        for (TemExpense genericDetail : getActualExpenseForValidation().getExpenseDetails()) {
            final ActualExpense detail = (ActualExpense)genericDetail;
            final String expenseDetailIdentifier = "expenseDetails["+count+"]";

            GlobalVariables.getMessageMap().addToErrorPath(expenseDetailIdentifier);

            for (ActualExpenseDetailValidation subValidation : detailValidations) {
                subValidation.setActualExpenseForValidation(getActualExpenseForValidation());
                subValidation.setActualExpenseDetailForValidation(detail);
                success = subValidation.validate(event);
            }

            GlobalVariables.getMessageMap().removeFromErrorPath(expenseDetailIdentifier);

            count += 1;
        }

        return success;
    }

    public ActualExpense getActualExpenseForValidation() {
        return actualExpenseForValidation;
    }

    public void setActualExpenseForValidation(ActualExpense actualExpenseForValidation) {
        this.actualExpenseForValidation = actualExpenseForValidation;
    }

    public List<? extends ActualExpenseDetailValidation> getDetailValidations() {
        return detailValidations;
    }

    public void setDetailValidations(List<? extends ActualExpenseDetailValidation> detailValidations) {
        this.detailValidations = detailValidations;
    }


}
