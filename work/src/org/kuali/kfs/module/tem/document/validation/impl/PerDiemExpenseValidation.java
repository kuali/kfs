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

import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class PerDiemExpenseValidation extends GenericValidation {
    protected DictionaryValidationService dictionaryValidationService;
    protected DateTimeService dateTimeService;
    protected Boolean incidentalsWithMealsOnlyInd;

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
            if (success) {
                success &= validatePerDiemIncidentals(perDiem);
            }
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
    protected boolean validatePerDiemValues(PerDiemExpense perDiemExpense) {
        boolean success = true;

        //this is calling the alternative getter functions which will return negative values
        if ((perDiemExpense.getUnfilteredBreakfastValue() != null && perDiemExpense.getUnfilteredBreakfastValue().isNegative())
                || (perDiemExpense.getUnfilteredLunchValue() != null && perDiemExpense.getUnfilteredLunchValue().isNegative())
                || (perDiemExpense.getUnfilteredDinnerValue() != null && perDiemExpense.getUnfilteredDinnerValue().isNegative())
                || (perDiemExpense.getUnfilteredIncidentalsValue() != null && perDiemExpense.getUnfilteredIncidentalsValue().isNegative())
                || (perDiemExpense.getUnfilteredLodging() != null && perDiemExpense.getUnfilteredLodging().isNegative())
                || (perDiemExpense.getUnfilteredMiles() != null && perDiemExpense.getUnfilteredMiles() < 0)) {
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.MILEAGE_DATE, TemKeyConstants.ERROR_PER_DIEM_LESS_THAN_ZERO);
            success = false;
        }
        return success;
    }

    /**
     * Validates that if the KFS-TEM / Document / INCIDENTALS_WITH_MEALS_IND is set on, that the per diem expense does not have any incidentals claimed
     * if it does not have expenses associated with at least one meal.
     * @param perDiemExpense the per diem expense to validate
     * @return true if the validation succeeded, false otherwise
     */
    protected boolean validatePerDiemIncidentals(PerDiemExpense perDiemExpense) {
        boolean success = true;

        if (isIncidentalsWithMealsOnlyInd()) {
            if ((perDiemExpense.getIncidentalsValue() != null && perDiemExpense.getIncidentalsValue().isGreaterThan(KualiDecimal.ZERO)) && (perDiemExpense.getBreakfastValue() == null || perDiemExpense.getBreakfastValue().equals(KualiDecimal.ZERO)) && (perDiemExpense.getLunchValue() == null || perDiemExpense.getLunchValue().equals(KualiDecimal.ZERO)) && (perDiemExpense.getDinnerValue() == null || perDiemExpense.getDinnerValue().equals(KualiDecimal.ZERO))) {
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.UNFILTERED_INCIDENTALS_VALUE, TemKeyConstants.ERROR_PER_DIEM_INCIDENTALS_BUT_NO_MEALS, getDateTimeService().toDateString(perDiemExpense.getMileageDate()));
                success = false;
            }
        }

        return success;
    }

    /**
     * @return the boolean value of the KFS-TEM / Document / TravelParameters.INCIDENTALS_WITH_MEALS_IND parameter
     */
    protected boolean isIncidentalsWithMealsOnlyInd() {
        if (incidentalsWithMealsOnlyInd == null) {
            incidentalsWithMealsOnlyInd = Boolean.valueOf(SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INCIDENTALS_WITH_MEALS_IND, Boolean.FALSE));
        }
        return incidentalsWithMealsOnlyInd.booleanValue();
    }

    public final DictionaryValidationService getDictionaryValidationService() {
        return this.dictionaryValidationService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }
}