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
package org.kuali.kfs.module.bc.document.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;

/**
 * This interface defines methods that an Budget Construction Organization Reports Service must provide.
 */
public interface BudgetConstructionOrganizationReportsService {

    /**
     * This method retrieves a Budget Construction Organization Reports instance by its composite primary keys (parameters passed
     * in).
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return a Budget Construction Organization Reports instance.
     */
    public BudgetConstructionOrganizationReports getByPrimaryId(String chartOfAccountsCode, String organizationCode);

    public List getBySearchCriteria(Class cls, Map searchCriteria);

    public List getBySearchCriteriaOrderByList(Class cls, Map searchCriteria, List<String> orderList);

    /**
     * This method returns a list of child BC organization reports objects for the passed in org.
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode);

    /**
     * This method returns if an org is a leaf
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public boolean isLeafOrg(String chartOfAccountsCode, String organizationCode);
}
