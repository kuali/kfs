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

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelAuthTripDetailMealsAndIncidentalsValidation extends GenericValidation {

    protected TravelDocumentService travelDocumentService;
    protected ParameterService parameterService;
    protected PerDiemService perDiemService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        TravelDocumentBase document = (TravelDocumentBase) event.getDocument();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        //determine if per diem rule should be daily or per meal
        final boolean checkDailyPerDiem = parameterService.getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.VALIDATE_DAILY_PER_DIEM_AND_INCIDENTALS_IND);

        int count = 0;
        for (PerDiemExpense estimate : document.getPerDiemExpenses()) {
            final boolean prorated = !KfsDateUtils.isSameDay(document.getTripBegin(), document.getTripEnd()) && (KfsDateUtils.isSameDay(estimate.getMileageDate(), document.getTripBegin()) || KfsDateUtils.isSameDay(estimate.getMileageDate(), document.getTripEnd()));
            if (prorated && !estimate.isProrated()) {
                estimate.setProrated(true); // we lost the prorated and we need it back
            }

            Integer perDiemPercent = travelDocumentService.calculateProratePercentage(estimate, document.getTripType().getPerDiemCalcMethod(), document.getTripEnd());
            String expenseDate = sdf.format(estimate.getMileageDate());

            if (estimate != null) {
                final PerDiem estimatePerDiem = getPerDiemService().getPerDiem(estimate.getPrimaryDestinationId(), estimate.getMileageDate(), document.getEffectiveDateForPerDiem(estimate));
                if (estimatePerDiem != null && perDiemPercent != null) {

                    //check daily per diem instead of validation in each meal
                    if (!checkDailyPerDiem){

                        // check for invalid breakfast amounts
                        KualiDecimal defaultBreakfast = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimatePerDiem.getBreakfast(), perDiemPercent);
                        if (defaultBreakfast.isGreaterThan(KualiDecimal.ZERO) && defaultBreakfast.isLessThan(estimate.getBreakfastValue())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES+"["+count+"]."+TemPropertyConstants.UNFILTERED_BREAKFAST_VALUE, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Breakfast - " + expenseDate, estimate.getBreakfastValue().toString(), defaultBreakfast.toString()});
                            rulePassed = false;
                        }

                        // check for invalid lunch amounts
                        KualiDecimal defaultLunch = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimatePerDiem.getLunch(), perDiemPercent);
                        if (defaultLunch.isGreaterThan(KualiDecimal.ZERO) && defaultLunch.isLessThan(estimate.getLunchValue())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES+"["+count+"]."+TemPropertyConstants.UNFILTERED_LUNCH_VALUE, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Lunch - " + expenseDate, estimate.getLunchValue().toString(), defaultLunch.toString()});
                            rulePassed = false;
                        }

                        // check for invalid dinner amounts
                        KualiDecimal defaultDinner = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimatePerDiem.getDinner(), perDiemPercent);
                        if (defaultDinner.isGreaterThan(KualiDecimal.ZERO) && defaultDinner.isLessThan(estimate.getDinnerValue())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES+"["+count+"]."+TemPropertyConstants.UNFILTERED_DINNER_VALUE, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Dinner - " + expenseDate, estimate.getDinnerValue().toString(), defaultDinner.toString()});
                            rulePassed = false;
                        }

                        // check for invalid incidentals amounts
                        KualiDecimal defaultIncidentals = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimatePerDiem.getIncidentals(), perDiemPercent);
                        if (defaultIncidentals.isGreaterThan(KualiDecimal.ZERO) && defaultIncidentals.isLessThan(estimate.getIncidentalsValue())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES+"["+count+"]."+TemPropertyConstants.UNFILTERED_INCIDENTALS_VALUE, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Incidentals - " + expenseDate, estimate.getIncidentalsValue().toString(), defaultIncidentals.toString()});
                            rulePassed = false;
                        }
                    }
                    else{

                        KualiDecimal dailyPerDiem = PerDiemExpense.calculateMealsAndIncidentalsProrated(estimatePerDiem.getMealsAndIncidentals(), perDiemPercent);
                        if (dailyPerDiem.isGreaterThan(KualiDecimal.ZERO) && dailyPerDiem.isLessThan(estimate.getMealsAndIncidentals())) {
                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES, TemKeyConstants.ERROR_TA_MEAL_AND_INC_NOT_VALID, new String[]{"Daily PerDiem - " + expenseDate, estimate.getMealsAndIncidentals().toString(), dailyPerDiem.toString()});
                            rulePassed = false;
                            break;
                        }

                    }
                }
            }
            count += 1;
        }

        if (StringUtils.isBlank(document.getMealWithoutLodgingReason()) && document.isMealsWithoutLodging()) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.MEAL_WITHOUT_LODGING_REASON, TemKeyConstants.ERROR_TRVL_MEALS_NO_LODGING_REQUIRES_JUSTIFICATION);
            rulePassed = false;
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
            if (expense.isLodging() || expense.isLodgingAllowance()) {
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

    public PerDiemService getPerDiemService() {
        return perDiemService;
    }

    public void setPerDiemService(PerDiemService perDiemService) {
        this.perDiemService = perDiemService;
    }

}
