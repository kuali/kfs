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
package org.kuali.kfs.module.bc.document.dataaccess;

/**
 * This class...
 */
public interface OrganizationBCDocumentSearchDao {

    /**
     * This method populates BudgetConstructionAccountSelect with rows associated with a set of selected organizations in the
     * Organization Tree for the user and where Budget Documents exist.
     * 
     * @param principalName
     * @param universityFiscalYear
     */
    public int buildAccountSelectPullList(String principalName, Integer universityFiscalYear);
    
    /**
     * Populates BudgetConstructionAccountSelect with accounts that are above the user's current point of view
     * and returns the number of rows inserted (which equates to number of accounts above).
     * 
     * @param principalName user who we are building the list for
     * @param universityFiscalYear budget year for records to look at
     * @param chartOfAccountsCode chart code of user's current point of view
     * @param organizationCode organization code of user's current point of view
     * @return int number of rows inserted
     */
    public int buildBudgetedAccountsAbovePointsOfView(String principalName, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode);
    
    /**
     * Populates BudgetConstructionAccountSelect with accounts that the given user is a manager or delegate for. Returns the number of rows
     * inserted.
     * 
     * @param principalName manager or delegate universal identifier
     * @param universityFiscalYear budget fiscal year
     */
    public int buildAccountManagerDelegateList(String principalName, Integer universityFiscalYear);
    
    /**
     * This method depopulates BudgetConstructionAccountSelect rows associated with the user.
     * 
     * @param principalName
     */
    public void cleanAccountSelectPullList(String principalName);
}

