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
package org.kuali.kfs.fp.service;

import java.util.List;

/**
 * 
 * This interface defines methods that a FiscalYearFunctionControl Service must provide.
 * 
 */
public interface FiscalYearFunctionControlService {

    /**
     * Retrieves list of fiscal years that the BudgetAdjustment is allowed in.
     * 
     * @return A list of fiscal years that the current user is authorized to perform a budget adjustment against.
     */
    public List getBudgetAdjustmentAllowedYears();

    /**
     * Checks the fiscal year against the control tables to see if BudgetAdjustment to base amount is allowed.
     * 
     * @param Posting year that the base amount is being changed for.
     * @return True if the current user is authorized to edit the base amount, false otherwise.
     */
    public boolean isBaseAmountChangeAllowed(Integer postingYear);
    
    /**
     * 
     * returns a list of years for which budget construction is visible in the application
     * @return list of type integer
     */
    public List<Integer> getActiveBudgetYear();

    /**
     * 
     * checks the fiscal year against the control table to see if a user can force a refresh of human resources data for
     * selected organizations in the budget construction security tree from the application
     * @param universityFiscalYear
     * @return true if selected human resources updates are allowed.
     */
    public boolean isApplicationUpdateFromHumanResourcesAllowed(Integer universityFiscalYear);
    
    /**
     * 
     * checks the fiscal year against the control table to see if the batch process will do a global update (ALL changes found)
     * of the human-resource derived data in budget construction
     * @param universityFiscalYear
     * @return true if the batch processes will feed human resource data changes into budget construction
     */
    public boolean isBatchUpdateFromHumanResourcesAllowed(Integer universityFiscalYear);

    /**
     * 
     * checks the fiscal year against the control table to see if the batch process will update base salaries in the budget
     * using the CalculatedSalaryFoundation data derived from payroll
     * @param universityFiscalYear
     * @return true if base salaries will be changed 
     */
    public boolean isBatchUpdateFromPayrollAllowed (Integer universityFiscalYear);
    
    /**
     * 
     * checks the fiscal year against the control table to see if users can view budget construction data in the application
     * @param universityFiscalYear
     * @return true if the budget construction data is viewable in general (in other words, security may still freeze some viewers
     * out even though everyone is not excluded)
     */
    public boolean isBudgetConstructionActive(Integer universityFiscalYear);
    
    /**
     * 
     * checks the fiscal year against the control table to see if the general ledger in budget construction should reflect
     * current base budget amounts in the accounting general ledger for the fiscal year 
     * @param universityFiscalYear
     * @return true if updates are allowed
     */
    public boolean isBudgetGeneralLedgerUpdateAllowed(Integer universityFiscalYear);

    /**
     * 
     * checks the fiscal year against the control table to see if users can make edits to budget construction for the given year
     * (some users may still be frozen out because of the security mechanism)
     * @param universityFiscalYear
     * @return true if authorized users can update budget construction using the application
     */
    public boolean isBudgetUpdateAllowed(Integer universityFiscalYear);
}
