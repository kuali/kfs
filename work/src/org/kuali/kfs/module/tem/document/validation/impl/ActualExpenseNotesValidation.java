/*
 * Copyright 2013 The Kuali Foundation.
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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * On submit, we want to go through the actual expenes on the document and verify that there are notes if notes are required
 */
public class ActualExpenseNotesValidation extends GenericValidation {
    protected ActualExpense actualExpenseForValidation;

    /**
     * Checks a single actual expense for notes - if the expense has an expense type object code which requires notes, either the actual expense
     * itself or all of its details, need notes
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        final ExpenseTypeObjectCode expenseTypeCode = getActualExpenseForValidation().getExpenseTypeObjectCode();
        //validate note required - but if we're just adding this line, don't require it yet
        if (ObjectUtils.isNotNull(expenseTypeCode) && expenseTypeCode.isNoteRequired() && !expenseOrDetailHasNotes(getActualExpenseForValidation())) {
            success = false;
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, KFSKeyConstants.ERROR_REQUIRED, "Notes for expense type " + getActualExpenseForValidation().getTravelExpenseTypeCodeCode());
        }
        return success;
    }

    /**
     * Determines if the given expense has notes, or if its detail has notes
     * @param expense the expense to check
     * @return true if notes are found, false otherwise
     */
    protected boolean expenseOrDetailHasNotes(ActualExpense expense) {
        if (expense.getExpenseParentId() != null) {
            return true; // skip, because we only look at details in context of the summary
        }
        if (!StringUtils.isBlank(expense.getDescription())) {
            return true; // we're good because the expense itself has a description
        }
        // no note?  then let's look in the details
        if (!ObjectUtils.isNull(expense.getExpenseDetails()) && !expense.getExpenseDetails().isEmpty()) {
            return allDetailsHaveNotes(expense.getExpenseDetails());
        }
        return false; // no notes 'round here
    }

    /**
     * Determines if all the given detail lines have notes
     * @param expenseDetails the detail lines to check
     * @return true if all details have notes, false if any of them are blank
     */
    protected boolean allDetailsHaveNotes(List<? extends TEMExpense> expenseDetails) {
        for (TEMExpense expenseDetail : expenseDetails) {
            if (StringUtils.isBlank(expenseDetail.getDescription())) {
                return false;
            }
        }
        return true; // still here?  then we're good
    }

    public ActualExpense getActualExpenseForValidation() {
        return actualExpenseForValidation;
    }

    public void setActualExpenseForValidation(ActualExpense actualExpenseForValidation) {
        this.actualExpenseForValidation = actualExpenseForValidation;
    }

}
