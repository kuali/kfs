/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.service;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionDuration;

/**
 * define the service methods that are related to budget construction duration class
 * 
 * @see org.kuali.kfs.module.bc.businessobject.BudgetConstructionDuration
 */
public interface BudgetConstructionDurationService {

    /**
     * retrieve a duration object by its primary key - the duration code.
     * 
     * @param durationCode the given duration code
     * @return a duration object retrived by its primary key
     */
    public BudgetConstructionDuration getByPrimaryId(String durationCode);
}
