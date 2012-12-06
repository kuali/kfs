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

import java.util.List;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class PerDiemExpenseValidation extends GenericValidation {

    public PerDiemExpenseValidation() {
        super();
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        TravelDocument travelDocument = (TravelDocument) event.getDocument();

        List<String> errorPath = GlobalVariables.getMessageMap().getErrorPath();

        //reset to start off from document
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KRADPropertyConstants.DOCUMENT);

        int counter = 0;
        for (PerDiemExpense perDiem : travelDocument.getPerDiemExpenses()) {
            String path = TemPropertyConstants.PER_DIEM_EXP + "[" + counter + "]";
            GlobalVariables.getMessageMap().addToErrorPath(path);
            success = validatePerDiemValues(perDiem);
            GlobalVariables.getMessageMap().removeFromErrorPath(path);
            counter++;
        }

        //reset the error path
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().getErrorPath().addAll(errorPath);

        return success;
    }


    /**
     * This method validates following rules
     *
     * 1.Validate the values are non-negative
     *
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validatePerDiemValues(PerDiemExpense perDiemExpense) {
        boolean success = true;

        //this is calling the alternative getter functions which will return negative values
        if (perDiemExpense.getBreakfastValue(false).isNegative() || perDiemExpense.getLunchValue(false).isNegative() ||
                perDiemExpense.getDinnerValue(false).isNegative() || perDiemExpense.getIncidentalsValue(false).isNegative() ||
                perDiemExpense.getLodging(false).isNegative() || perDiemExpense.getMiles(false) < 0) {
            GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.MILEAGE_DATE, TemKeyConstants.ERROR_PER_DIEM_LESS_THAN_ZERO);
        }
        return success;
    }

    /**
     *
     * @return
     */
    public final DictionaryValidationService getDictionaryValidationService() {
        return SpringContext.getBean(DictionaryValidationService.class);
    }

}