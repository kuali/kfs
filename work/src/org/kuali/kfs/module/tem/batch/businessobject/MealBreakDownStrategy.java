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
import org.kuali.rice.kns.util.KualiDecimal;

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
