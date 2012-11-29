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

import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemMealIncidentalBreakDown;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class MealBreakDownStrategyForContinental extends DefaultMealBreakDownStrategy {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.tem.batch.businessobject.DefaultMealBreakDownStrategy#breakDown(org.kuali.kfs.module.tem.businessobject.PerDiem, org.kuali.rice.kns.util.KualiDecimal)
     */
    @Override
    public void breakDown(PerDiem perDiem, KualiDecimal mealsAndIncidentals) {
        if (ObjectUtils.isNull(mealsAndIncidentals) || mealsAndIncidentals.isNegative()) {
            throw new RuntimeException("The given mealsAndIncidentals cannot be null or negative.");
        }

        PerDiemMealIncidentalBreakDown breakDown = this.getBusinessObjectService().findBySinglePrimaryKey(PerDiemMealIncidentalBreakDown.class, Float.valueOf(mealsAndIncidentals.floatValue()));

        if(ObjectUtils.isNull(breakDown)){
            super.breakDown(perDiem, mealsAndIncidentals);
        }
        else{
            this.breakDown(perDiem, breakDown);
        }
    }

    /**
     * break down meal and incidental by the given break down object
     */
    protected void breakDown(PerDiem perDiem, PerDiemMealIncidentalBreakDown breakDown) {
        Integer breakfast =  breakDown.getBreakfast();
        perDiem.setBreakfast(breakfast);

        Integer lunch = breakDown.getLunch();
        perDiem.setLunch(lunch);

        Integer dinner = breakDown.getDinner();
        perDiem.setDinner(dinner);

        KualiDecimal incidentals = breakDown.getIncidentals();
        perDiem.setIncidentals(incidentals);
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
