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
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * define the meal break down strategy for per diem
 */
public interface MealBreakDownStrategy {

    /**
     * break down the meals and incidentals for the given per diem
     *
     * @param perDiem the given per diem
     */
    void breakDown(PerDiem perDiem);

    /**
     * break down the meals and incidentals and update the given per diem
     *
     * @param mealsAndIncidentals the meals and incidentals to be broken down
     */
    void breakDown(PerDiem perDiem, KualiDecimal mealsAndIncidentals);
}
