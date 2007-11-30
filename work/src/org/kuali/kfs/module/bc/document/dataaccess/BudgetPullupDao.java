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
 * This class defines methods an BudgetPullupDao must provide
 */
public interface BudgetPullupDao {
    
    /**
     * This method initializes the root of the subtree in the temp table for the user
     * 
     * @param personUserIdentifier
     * @param chartOfAccountsCode
     * @param organizationCode
     * @param currentLevel
     */
    public void initPointOfView(String personUserIdentifier, String chartOfAccountsCode, String organizationCode, int currentLevel);

    /**
     * This method fills out the subtree for the root node already found in the temp table for the user.
     * It is assumed the root node is already in the table initialized as the level 0 organization.
     * The report flag field is used to keep track of the levels of the organizations during the build process.
     * The report flag field is reset to zero at the end
     * 
     * @param personUserIdentifier
     * @param previousLevel
     */
    public void insertChildOrgs(String personUserIdentifier, int previousLevel);

}
