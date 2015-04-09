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
package org.kuali.kfs.module.bc.document.service;

/**
 * This class defines methods an OrganizationSalarySettingSearchService must provide The OrganizationSalarySettingSearchService
 * provides methods that build and clean the temporary tables used in the Organization Budgeted Position and Incumbent lookups. It
 * is assumed that the Organization Tree exists and that the user has selected at least one organization before deriving the list of
 * budgeted (funded) positions or incumbents.
 */
public interface OrganizationSalarySettingSearchService {

    /**
     * This method populates BudgetConstructionIntendedIncumbentSelect with rows associated with a set of selected organizations in
     * the Organization Tree for the user and that have active funding (PendingBudgetConstructionAppointmentFunding).
     * 
     * @param principalName
     * @param universityFiscalYear
     */
    public void buildIntendedIncumbentSelect(String principalName, Integer universityFiscalYear);

    /**
     * This method depopulates BudgetConstructionIntendedIncumbentSelect rows associated with a user
     * 
     * @param principalName
     */
    public void cleanIntendedIncumbentSelect(String principalName);

    /**
     * This method populates BudgetConstructionPositionSelect with rows associated with a set of selected organizations in the
     * Organization Tree for the user and that are associated with BudgetConstructionPosition (ld_bcn_pos_t) using the
     * positionDepartmentIdentifier (pos_dept_id))
     * 
     * @param principalName
     * @param universityFiscalYear
     */
    public void buildPositionSelect(String principalName, Integer universityFiscalYear);

    /**
     * This method depopulates BudgetConstructionPositionSelect rows associated with a user
     * 
     * @param principalName
     */
    public void cleanPositionSelect(String principalName);
}

