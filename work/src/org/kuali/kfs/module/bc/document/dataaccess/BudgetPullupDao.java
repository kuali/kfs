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
 * This class defines methods an BudgetPullupDao must provide
 */
public interface BudgetPullupDao {
    
    public void buildSubTree(String principalName, String chartOfAccountsCode, String organizationCode, int currentLevel);
    
//    /**
//     * This method initializes the root of the subtree in the temp table for the user
//     * 
//     * @param principalName
//     * @param chartOfAccountsCode
//     * @param organizationCode
//     * @param currentLevel
//     */
//    public void initPointOfView(String principalName, String chartOfAccountsCode, String organizationCode, int currentLevel);
//
//    /**
//     * This method fills out the subtree for the root node already found in the temp table for the user.
//     * It is assumed the root node is already in the table initialized as the level 0 organization.
//     * The report flag field is used to keep track of the levels of the organizations during the build process.
//     * The report flag field is reset to zero at the end
//     * 
//     * @param principalName
//     * @param previousLevel
//     */
//    public void insertChildOrgs(String principalName, int previousLevel);
    
    /**
     * Deletes rows in the Pullup temp table for the user
     * 
     * @param principalName
     */
    public void cleanGeneralLedgerObjectSummaryTable(String principalName);

}

