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
