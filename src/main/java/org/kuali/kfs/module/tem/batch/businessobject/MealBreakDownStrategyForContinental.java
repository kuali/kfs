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
        KualiDecimal breakfast =  breakDown.getBreakfast();
        perDiem.setBreakfast(breakfast);

        KualiDecimal lunch = breakDown.getLunch();
        perDiem.setLunch(lunch);

        KualiDecimal dinner = breakDown.getDinner();
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
