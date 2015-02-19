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
package org.kuali.kfs.module.bc.batch.service;

public interface BudgetConstructionHumanResourcesPayrollInterfaceService {

    /**
     * 
     * read from the HR/Payroll systems and create/update position rows in Budget Construction for both the base and request years
     * @param BaseYear
     * @param PositionSynchOK control flag which indicates whether position data in budget construction is updateable or frozen
     * @param CSFUpdateOK control flag which indicates whether current payroll information can flow into budget construction
     */
    public void refreshBudgetConstructionPosition (Integer BaseYear, boolean PositionSynchOK, boolean CSFUpdateOK);
    
    /**
     * 
     * read from the HR/Payroll systems and create person-related information (ranks, titles) for display in budget construction
     * @param BaseYear
     * @param PositionSynchOK control flag which indicates whether position data in budget construction is updateable or frozen
     * @param CSFUpdateOK control flag which indicates whether current payroll information can flow into budget construction
     */
    public void refreshBudgetConstructionIntendedIncumbent (Integer BaseYear, boolean PositionSynchOK, boolean CSFUpdateOK, boolean BCUpdatesAllowed);
}
