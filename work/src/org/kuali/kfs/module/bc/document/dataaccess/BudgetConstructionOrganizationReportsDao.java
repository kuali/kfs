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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;

public interface BudgetConstructionOrganizationReportsDao {

    public BudgetConstructionOrganizationReports getByPrimaryId(String chartOfAccountsCode, String organizationCode);

    public Collection getByPrimaryId(Class cls, Map searchCriteria);
    
    /**
     * This method returns active organizations that report to the passed in organization.
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode);

    /**
     * This method returns whether or not an organization is a leaf in the reports to tree
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public boolean isLeafOrg(String chartOfAccountsCode, String organizationCode);

}
