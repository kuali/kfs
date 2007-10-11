/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.dao;

/**
 * This class defines methods an OrganizationSalarySettingSearchDao must provide
 */
public interface OrganizationSalarySettingSearchDao {
    
    /**
     * This method populates BudgetConstructionIntendedIncumbentSelect with rows associated with a set of
     * selected organizations in the Organization Tree for the user and that have active funding
     * (PendingBudgetConstructionAppointmentFunding).
     * 
     * @param personUserIdentifier
     * @param universityFiscalYear
     */
    public void buildIntendedIncumbentSelect(String personUserIdentifier, Integer universityFiscalYear);
    
    /**
     * This method depopulates BudgetConstructionIntendedIncumbentSelect rows associated with a user
     * 
     * @param personUserIdentifier
     */
    public void cleanIntendedIncumbentSelect(String personUserIdentifier);

    /**
     * This method populates BudgetConstructionPositionSelect with rows associated with a set of
     * selected organizations in the Organization Tree for the user and that are associated with
     * BudgetConstructionPosition (ld_bcn_pos_t) using the positionDepartmentIdentifier (pos_dept_id))
     * 
     * @param personUserIdentifier
     * @param universityFiscalYear
     */
    public void buildPositionSelect(String personUserIdentifier, Integer universityFiscalYear);

    /**
     * This method depopulates BudgetConstructionPositionSelect rows associated with a user
     * 
     * @param personUserIdentifier
     */
    public void cleanPositionSelect(String personUserIdentifier);
}
