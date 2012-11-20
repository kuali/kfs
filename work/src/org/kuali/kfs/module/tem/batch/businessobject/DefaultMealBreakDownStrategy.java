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
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

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

        Integer breakfastPercent = this.getMealPercentByMealCode(MEAL_CODE.BREAKFAST.mealCode);
        Integer breakfast = mealsAndIncidentals.intValue() * breakfastPercent / 100;
        perDiem.setBreakfast(breakfast);

        Integer lunchPercent = this.getMealPercentByMealCode(MEAL_CODE.LUNCH.mealCode);
        Integer lunch = mealsAndIncidentals.intValue() * lunchPercent / 100;
        perDiem.setLunch(lunch);

        Integer dinnerPercent = this.getMealPercentByMealCode(MEAL_CODE.DINNER.mealCode);
        Integer dinner = mealsAndIncidentals.intValue() * dinnerPercent / 100;
        perDiem.setDinner(dinner);

        KualiDecimal meals = new KualiDecimal(breakfast + lunch + dinner);
        KualiDecimal incidentals = mealsAndIncidentals.subtract(meals);
        perDiem.setIncidentals(incidentals);
    }

    /**
     * get meal percentage by meal code defined as an application parameter
     * 
     * @return meal percentage by meal code defined as an application parameter
     */
    protected Integer getMealPercentByMealCode(String mealCode) {
        Integer mealPercent = null;

        try {
            String mealPercentString = getMealPercent(mealCode);

            mealPercent = Integer.parseInt(mealPercentString);
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
        String mealPercentString = this.getParameterService().getParameterValue(PerDiemLoadStep.class, this.getParameterName(), mealCode);

        return mealPercentString;
    }
    
    /**
     * get the parameter name that is used for break down
     */
    protected String getParameterName(){
        return PerDiemParameter.DEFAULT_CONUS_MIE_BREAKDOWN_PARAM_NAME;
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
