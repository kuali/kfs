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
package org.kuali.module.budget.service;

import java.util.List;

/**
 * This interface defines methods that manipulate objects used by the Organization Selection screens.
 * 
 * Manipulated objects include BudgetConstructionPullup with methods that populate and depopulate the associated table
 * for a specific user.
 * 
 */
public interface BudgetOrganizationTreeService {

    /**
     * This method populates BudgetConstructionPullup with rows that represent the subtree of the passed in
     * point of view organization for a user. All organizations reporting to the point of view are inserted.
     * 
     * @param personUserIdentifier
     * @param chartOfAccountsCode
     * @param organizationCode
     */
    public void buildPullup(String personUserIdentifier, String chartOfAccountsCode, String organizationCode);

    /**
     * This method depopulates BudgetConstructionPullup of any rows associated with the user
     *  
     * @param personUserIdentifier
     */
    public void cleanPullup(String personUserIdentifier);

    /**
     * This method returns a list of child organizations for the passed in organization and user
     * 
     * @param personUniversalIdentifier
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List getPullupChildOrgs(String personUniversalIdentifier, String chartOfAccountsCode, String organizationCode);
    
    /**
     * This method resets the pullflag for the BudgetConstructionPullup set of records owned by the user
     * @param personUniversalIdentifier
     */
    public void resetPullFlag(String personUniversalIdentifier);
}
