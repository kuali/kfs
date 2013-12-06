/*
 * Copyright 2011 The Kuali Foundation.
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

import java.text.SimpleDateFormat;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelAuthTripDetailMealsAndIncidentalsValidation extends GenericValidation {

    private TravelDocumentService travelDocumentService;
    private ParameterService parameterService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        TravelDocumentBase document = (TravelDocumentBase) event.getDocument();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        for (PerDiemExpense estimate : document.getPerDiemExpenses()) {
            Integer perDiemPercent = travelDocumentService.calculateProratePercentage(estimate, document.getTripType().getPerDiemCalcMethod(), document.getTripEnd());
            String expenseDate = sdf.format(estimate.getMileageDate());

            if (estimate != null) {
                if (estimate.getPerDiem() != null && perDiemPercent != null) {

                    //determine if per diem rule should be daily or per meal
                    boolean checkDailyPerDiem = parameterService.getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.VALIDATE_DAILY_PER_DIEM_AND_INCIDENTALS_IND);

                    //check daily per diem instead of validation in each meal
                    if (!checkDailyPerDiem){

                        // check for invalid breakfast amounts
                        KualiDecimal defaultBreakfast = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimate.getPerDiem().getBreakfast(), perDiemPercent);
                        if (defaultBreakfast.isGreaterThan(KualiDecimal.ZERO) && defaultBreakfast.isLessThan(estimate.getBreakfastValue())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Breakfast - " + expenseDate, estimate.getBreakfastValue().toString(), defaultBreakfast.toString()});
                            rulePassed = false;
                            break;
                        }

                        // check for invalid lunch amounts
                        KualiDecimal defaultLunch = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimate.getPerDiem().getLunch(), perDiemPercent);
                        if (defaultLunch.isGreaterThan(KualiDecimal.ZERO) && defaultLunch.isLessThan(estimate.getLunchValue())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Lunch - " + expenseDate, estimate.getLunchValue().toString(), defaultLunch.toString()});
                            rulePassed = false;
                            break;
                        }

                        // check for invalid dinner amounts
                        KualiDecimal defaultDinner = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimate.getPerDiem().getDinner(), perDiemPercent);
                        if (defaultDinner.isGreaterThan(KualiDecimal.ZERO) && defaultDinner.isLessThan(estimate.getDinnerValue())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Dinner - " + expenseDate, estimate.getDinnerValue().toString(), defaultDinner.toString()});
                            rulePassed = false;
                            break;
                        }

                        // check for invalid incidentals amounts
                        KualiDecimal defaultIncidentals = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimate.getPerDiem().getIncidentals(), perDiemPercent);
                        if (defaultIncidentals.isGreaterThan(KualiDecimal.ZERO) && defaultIncidentals.isLessThan(estimate.getIncidentalsValue())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Incidentals - " + expenseDate, estimate.getIncidentalsValue().toString(), defaultIncidentals.toString()});
                            rulePassed = false;
                            break;
                        }
                    }
                    else{

                        KualiDecimal dailyPerDiem = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimate.getPerDiem().getMealsAndIncidentals(), perDiemPercent);
                        if (dailyPerDiem.isGreaterThan(KualiDecimal.ZERO) && dailyPerDiem.isLessThan(estimate.getMealsAndIncidentals())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Daily PerDiem - " + expenseDate, estimate.getMealsAndIncidentals().toString(), dailyPerDiem.toString()});
                            rulePassed = false;
                            break;
                        }

                    }
                }

                // check for meal without lodging
                if (document.checkMealWithoutLodging(estimate) && !hasLodgingActualExpense(document) && (document.getMealWithoutLodgingReason() == null || document.getMealWithoutLodgingReason().trim().length() == 0)) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.MEAL_WITHOUT_LODGING_REASON, KFSKeyConstants.ERROR_CUSTOM, "Justification for meals without lodging is required.");
                    rulePassed = false;
                    break;
                }
            }
        }

        for(ActualExpense ote : document.getActualExpenses()) {
            if (document.checkMealWithoutLodging(ote) && (document.getMealWithoutLodgingReason() == null || document.getMealWithoutLodgingReason().trim().length() == 0)){
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.MEAL_WITHOUT_LODGING_REASON, KFSKeyConstants.ERROR_CUSTOM, "Justification for meals without lodging is required.");
                rulePassed = false;
                break;
            }
        }

        return rulePassed;
    }

    /**
     * Determines whether the given document has any actual expenses associated which are lodging expenses
     * @param document the document to look for lodging expenses on
     * @return true if there are lodging expenses on the document, false otherwise
     */
    protected boolean hasLodgingActualExpense(TravelDocument document) {
        for (ActualExpense expense : document.getActualExpenses()) {
            if (TemConstants.ExpenseTypes.LODGING.equals(expense.getExpenseTypeCode())) {
                return true;
            }
        }
        return false;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}