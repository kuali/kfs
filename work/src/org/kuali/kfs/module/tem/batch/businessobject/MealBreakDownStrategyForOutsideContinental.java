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

import org.kuali.kfs.module.tem.TemConstants.PerDiemParameter;

/**
 * implement the meal break down strategy based on the pre-configured percentage
 */
public class MealBreakDownStrategyForOutsideContinental extends DefaultMealBreakDownStrategy {

    /**
     * @see org.kuali.kfs.module.tem.batch.businessobject.DefaultMealBreakDownStrategy#getParameterName()
     */
    @Override
    protected String getParameterName() {
        return PerDiemParameter.OCONUS_MEAL_BREAKDOWN_PARAM_NAME;
    }
}
