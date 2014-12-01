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
package org.kuali.kfs.module.bc.batch.dataaccess;

public interface BudgetConstructionHumanResourcesPayrollInterfaceDao {

    /**
     * 
     * use this method to populate the budget construction administrative posts from the human resources system
     * @param requestFiscalYear
     */
    public void buildBudgetConstructionAdministrativePosts ();
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
     *  use this method to fill in the "IU_CLASSIF_LEVEL" (an attribute which indicates the principal type of academic title) when you build your intended incumbent table.
     *  this attribute is only for display, so this method can be implemented to do the same thing as the standard build.
     *  At IU, the attribute is used for reporting, to see whether salary guidelines for faculty have been met, but that happens outside the application itself.
     *  Alternatively, if you add fields to your intended incumbent table, this method can be called to add values for those fields to the default intended incumbent build.
     * @param requestFiscalYear
     */
    public void buildBudgetConstructionIntendedIncumbentWithFacultyAttributes (Integer requestFiscalYear);
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
     * updates the names in the intended incumbent table
     */
    public void updateNamesInBudgetConstructionIntendedIncumbent();
    
}
