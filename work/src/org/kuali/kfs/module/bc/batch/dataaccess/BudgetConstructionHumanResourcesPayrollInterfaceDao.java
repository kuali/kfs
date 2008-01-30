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

public interface BudgetConstructionHumanResourcesPayrollInterfaceDao {

    /**
     * 
     * use this method to populate the budget construction administrative posts from the human resources system
     * @param requestFiscalYear
     */
    public void buildBudgetConstructionAdministrativePosts (Integer requestFiscalYear);
    /**
     * 
     * use this method to initialize appointment funding reasons (union codes in the present FIS) from the human resources system
     * @param requestFiscalYear
     */
    public void buildBudgetConstructionAppointmentFundingReasons (Integer requestFiscalYear);
    /**
     * 
     * use this method to populate the intended incumbent for positions in the budget from the human resources system
     * @param requestFiscalYear
     */
    public void buildBudgetConstructionIntendedIncumbent (Integer requestFiscalYear);
    /**
     * 
     * use this method to import the most recent version of positions in the current fiscal year which occur in CSF, the current year salary table
     * @param baseFiscalYear
     */
    public void buildBudgetConstructionPositionBaseYear (Integer baseFiscalYear);
    /**
     * 
     * use this method to import positions eligible for budgeting in the coming year
     * from the payroll and human resources system
     * @param requestFiscalYear
     */
    public void buildBudgetConstructionPositonRequestYear (Integer requestFiscalYear);
    
    /**
     * 
     *  use this method to update the "IU_CLASSIF_LEVEL" (an attribute which indicates the principal type of academic title)
     *  this attribute is only for display, so this method can be implemented (as we do here) to do nothing at all.
     *  Alternatively, if you add fields to your intended incumbent table, this method can be called to update those
     *  attributes on the existing rows in intended incumbent.
     * @param requestFiscalYear
     */
    public void updateBudgetConstructionIntendedIncumbentAttributes (Integer requestFiscalYear);
    
}
