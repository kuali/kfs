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
import org.kuali.kfs.module.tem.businessobject.PerDiemMealIncidentalBreakDown;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for the Per Diem Meals and Incidentals Breakdown maintenance document.
 */
public class PerDiemMealIncidentalBreakDownRule extends MaintenanceDocumentRuleBase {

    private ParameterService parameterService;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomSaveDocumentBusinessRules(document);

        final PerDiemMealIncidentalBreakDown perDiemMealIncidentalBreakDown = (PerDiemMealIncidentalBreakDown)document.getNewMaintainableObject().getBusinessObject();
        result &= validateTotals(perDiemMealIncidentalBreakDown);

        return result;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        final PerDiemMealIncidentalBreakDown perDiemMealIncidentalBreakDown = (PerDiemMealIncidentalBreakDown)document.getNewMaintainableObject().getBusinessObject();
        result &= validateTotals(perDiemMealIncidentalBreakDown);

        return result;
    }

    /**
     * Validates values in PerDiemMealIncidentalBreakDown
     *
     * @param perDiemMealIncidentalBreakdown
     * @return
     */
    protected boolean validateTotals(PerDiemMealIncidentalBreakDown perDiemMealIncidentalBreakdown) {
        boolean valid = true;

        //make sure all amounts are non-null and positive
        valid &= validateAmounts(perDiemMealIncidentalBreakdown);

        if (validateAmounts(perDiemMealIncidentalBreakdown)) {

            valid &= validateMealsAndIncidentalsTotals(perDiemMealIncidentalBreakdown);
        }

        return valid;
    }

    /**
     * Validates all amounts on the record
     *
     * @param perDiemMealIncidentalBreakdown
     * @return
     */
    protected boolean validateAmounts(PerDiemMealIncidentalBreakDown perDiemMealIncidentalBreakdown) {

        boolean result = validateAmount(perDiemMealIncidentalBreakdown.getMealsAndIncidentals(), TemPropertyConstants.MEALS_AND_INCIDENTALS);
        result &= validateAmount(perDiemMealIncidentalBreakdown.getBreakfast(), TemPropertyConstants.BREAKFAST);
        result &= validateAmount(perDiemMealIncidentalBreakdown.getLunch(), TemPropertyConstants.LUNCH);
        result &= validateAmount(perDiemMealIncidentalBreakdown.getDinner(), TemPropertyConstants.DINNER);
        result &= validateAmount(perDiemMealIncidentalBreakdown.getIncidentals(), TemPropertyConstants.INCIDENTALS);

        return result;
    }

    /**
     * Validates that the amount is non-null and non-negative (zero values are allowed)
     *
     * @param amount
     * @return
     */
    protected boolean validateAmount(KualiDecimal amount, String property) {
        if (ObjectUtils.isNull(amount)) {
           return false;
        }
        else if (amount.isNegative()) {
            putFieldError(property, TemKeyConstants.ERROR_PER_DIEM_MIB_INVALID_AMOUNTS_MUST_BE_POSITIVE);
            return false;
        }

        return true;
    }

    /**
     * Validates that the total for Meals and Incidentals equals the breakdown
     *
     * @param perDiemMealIncidentalBreakdown
     * @return
     */
    protected boolean validateMealsAndIncidentalsTotals(PerDiemMealIncidentalBreakDown perDiemMealIncidentalBreakdown) {

        //Meals and Incidentals should equals Breakfast, Lunch, Dinner, and Incidentals
        KualiDecimal total = perDiemMealIncidentalBreakdown.getMealsAndIncidentals();

        KualiDecimal breakfast = perDiemMealIncidentalBreakdown.getBreakfast();
        KualiDecimal lunch = perDiemMealIncidentalBreakdown.getLunch();
        KualiDecimal dinner = perDiemMealIncidentalBreakdown.getDinner();
        KualiDecimal incidentals = perDiemMealIncidentalBreakdown.getIncidentals();

        KualiDecimal breakdown = breakfast.add(lunch).add(dinner).add(incidentals);

        if (!total.equals(breakdown)) {
            putFieldError(TemPropertyConstants.MEALS_AND_INCIDENTALS, TemKeyConstants.ERROR_PER_DIEM_MIB_INVALID_TOTAL_MEALS_AND_INCIDENTALS);
            return false;
        }

        return true;
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
