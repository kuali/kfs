/*
 * Copyright 2007 The Kuali Foundation
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

