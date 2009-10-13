/*
 * Copyright 2007-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

