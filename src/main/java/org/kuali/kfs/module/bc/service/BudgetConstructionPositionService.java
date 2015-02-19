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

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.exception.BudgetPositionAlreadyExistsException;

/**
 * Provides methods to retrieve and populate budget construction position records.
 */
public interface BudgetConstructionPositionService {

    /**
     * Retrieves a new position record from an external system using <code>HumanResourcesPayrollService</code> then populates the
     * record in the budget position table.
     * 
     * @param universityFiscalYear budget fiscal year for the position
     * @param positionNumber position number for the record
     * @exception BudgetPositionAlreadyExistsException thrown when position is already in the budget table
     * @exception PositionLockNotObtainedException thrown when the position and associated funding locks could not be obtained
     */
    public void pullNewPositionFromExternal(Integer universityFiscalYear, String positionNumber) throws BudgetPositionAlreadyExistsException;

    /**
     * Refreshes a position record from an external system using <code>HumanResourcesPayrollService</code> then updates the record
     * in the budget position table.
     * 
     * @param universityFiscalYear budget fiscal year for the position
     * @param positionNumber position number for the record
     */
    public void refreshPositionFromExternal(Integer universityFiscalYear, String positionNumber);

    /**
     * retrieve a Budget Construction Position object by its primary key.
     * 
     * @param fiscalYear the given fiscal year
     * @param positionNumber the given position number
     * @return a Budget Construction Position object retrived by its primary key
     */
    public BudgetConstructionPosition getByPrimaryId(String fiscalYear, String positionNumber);

    /**
     * determine whether the given position is budgetable (valid, active and budgeted)
     * 
     * @param budgetConstructionPosition the given position
     * @return true if the given position is budgetable; otherwise, false
     */
    public boolean isBudgetablePosition(BudgetConstructionPosition budgetConstructionPosition);
}
