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
