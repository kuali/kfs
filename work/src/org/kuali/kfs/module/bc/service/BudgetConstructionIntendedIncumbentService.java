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

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.exception.BudgetIncumbentAlreadyExistsException;

/**
 * define the service methods that are related to budget construction Intended Incumbent class
 * 
 * @see org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent
 */
public interface BudgetConstructionIntendedIncumbentService {
    
    /**
     * Retrieves a new incumbent record from an external system using <code>HumanResourcesPayrollService</code> then populates the
     * record in the budget intended incumbent table.
     * 
     * @param emplid university id for the incumbent to pull
     * @exception BudgetPositionAlreadyExistsException thrown when position is already in the budget table
     */
    public void pullNewIncumbentFromExternal(String emplid) throws BudgetIncumbentAlreadyExistsException;

    /**
     * Refreshes an incumbent record from an external system using <code>HumanResourcesPayrollService</code> then updates the
     * record in the budget intended incumbent table.
     * 
     * @param emplid university id for the incumbent to pull
     */
    public void refreshIncumbentFromExternal(String emplid);
    
    /**
     * retrieve a Budget Construction Intended Incumbent object by its primary key - the employee id.
     * 
     * @param emplid the given employee id
     * @return a Budget Construction Intended Incumbent object retrived by its primary key
     */
    public BudgetConstructionIntendedIncumbent getByPrimaryId(String emplid);
}
