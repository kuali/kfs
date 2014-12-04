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
package org.kuali.kfs.module.tem.batch.businessobject;

import org.kuali.kfs.module.tem.TemConstants.MEAL_CODE;
import org.kuali.kfs.module.tem.TemConstants.PerDiemParameter;
import org.kuali.kfs.module.tem.batch.PerDiemLoadStep;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

public class DefaultMealBreakDownStrategy implements MealBreakDownStrategy {

    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.module.tem.batch.businessobject.MealBreakDownStrategy#breakDown(org.kuali.kfs.module.tem.businessobject.PerDiem)
     */
    @Override
    public void breakDown(PerDiem perDiem) {
        KualiDecimal mealsAndIncidentals = perDiem.getMealsAndIncidentals();

        this.breakDown(perDiem, mealsAndIncidentals);
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.businessobject.MealBreakDownStrategy#breakDown(org.kuali.kfs.module.tem.businessobject.PerDiem,
     *      org.kuali.rice.kns.util.KualiDecimal)
     */
    @Override
    public void breakDown(PerDiem perDiem, KualiDecimal mealsAndIncidentals) {
        if (ObjectUtils.isNull(mealsAndIncidentals) || mealsAndIncidentals.isNegative()) {
            throw new RuntimeException("The given mealsAndIncidentals cannot be null or negative.");
        }

        KualiDecimal breakfastPercent = this.getMealPercentByMealCode(MEAL_CODE.BREAKFAST.mealCode);
        KualiDecimal breakfast = mealsAndIncidentals.multiply(breakfastPercent).divide(new KualiDecimal(100));
        perDiem.setBreakfast(breakfast);

        KualiDecimal lunchPercent = this.getMealPercentByMealCode(MEAL_CODE.LUNCH.mealCode);
        KualiDecimal lunch = mealsAndIncidentals.multiply(lunchPercent).divide(new KualiDecimal(100));
        perDiem.setLunch(lunch);

        KualiDecimal dinnerPercent = this.getMealPercentByMealCode(MEAL_CODE.DINNER.mealCode);
        KualiDecimal dinner = mealsAndIncidentals.multiply(dinnerPercent).divide(new KualiDecimal(100));
        perDiem.setDinner(dinner);

        KualiDecimal meals = breakfast.add(lunch).add(dinner);
        KualiDecimal incidentals = mealsAndIncidentals.subtract(meals);
        perDiem.setIncidentals(incidentals);
    }

    /**
     * get meal percentage by meal code defined as an application parameter
     *
     * @return meal percentage by meal code defined as an application parameter
     */
    protected KualiDecimal getMealPercentByMealCode(String mealCode) {
        KualiDecimal mealPercent = null;

        try {
            String mealPercentString = getMealPercent(mealCode);

            mealPercent =  new KualiDecimal(mealPercentString);
        }
        catch (Exception e) {
            String error = this.getParameterName() + "is not setup correctly";
            throw new RuntimeException(error, e);
        }

        return mealPercent;
    }

    /**
     * get meal percentage from an application parameter
     */
    protected String getMealPercent(String mealCode) {
        String mealPercentString = this.getParameterService().getSubParameterValueAsString(PerDiemLoadStep.class, getParameterName(), mealCode);
        return mealPercentString;
    }

    /**
     * get the parameter name that is used for break down
     */
    protected String getParameterName(){
        return PerDiemParameter.CONUS_MEAL_BREAKDOWN;
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
