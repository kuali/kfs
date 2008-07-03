/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.service;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;

/**
 * define the service methods that are related to budget construction Intended Incumbent class
 * 
 * @see org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent
 */
public interface BudgetConstructionIntendedIncumbentService {
    /**
     * retrieve a Budget Construction Intended Incumbent object by its primary key - the employee id.
     * 
     * @param emplid the given employee id
     * @return a Budget Construction Intended Incumbent object retrived by its primary key
     */
    public BudgetConstructionIntendedIncumbent getByPrimaryId(String emplid);
}
