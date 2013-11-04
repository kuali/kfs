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

import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TmExpense;
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
        for (TmExpense genericDetail : getActualExpenseForValidation().getExpenseDetails()) {
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
