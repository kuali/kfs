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
package org.kuali.module.budget.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.module.budget.dao.BudgetConstructionOrganizationReportsDao;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the getByPrimaryId method defined by BudgetConstructionOrganizationReportsService.
 * 
 * @param chartOfAccountsCode The FIN_COA_CD that is being searched for
 * @param organizationCode the ORG_CD that is being searched for
 * @return BudgetConstructionOrganizationReports Business Object
 * @see org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService#getByPrimaryId(java.lang.String,
 *      java.lang.String)
 */
@Transactional
public class BudgetConstructionOrganizationReportsServiceImpl implements BudgetConstructionOrganizationReportsService {

    private BudgetConstructionOrganizationReportsDao budgetConstructionOrganizationReportsDao;

    /**
     * @see org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService#getByPrimaryId(java.lang.String,
     *      java.lang.String)
     */
    public BudgetConstructionOrganizationReports getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
        return budgetConstructionOrganizationReportsDao.getByPrimaryId(chartOfAccountsCode, organizationCode);
    }
    
    public Collection getBySearchCriteria(Class cls, Map searchCriteria) {
        return budgetConstructionOrganizationReportsDao.getBySearchCriteria(cls, searchCriteria);
    }
  
    public Collection getBySearchCriteriaOrderByList(Class cls, Map searchCriteria, List<String> orderList) {
        return budgetConstructionOrganizationReportsDao.getBySearchCriteriaByList(cls, searchCriteria, orderList);
    }
 
    /**
     * @see org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService#getActiveChildOrgs(java.lang.String,
     *      java.lang.String)
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode) {

        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("String parameter chartOfAccountsCode was null or blank.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("String parameter organizationCode was null or blank.");
        }

        return budgetConstructionOrganizationReportsDao.getActiveChildOrgs(chartOfAccountsCode, organizationCode);
    }

    /**
     * Gets the budgetConstructionOrganizationReportsDao attribute.
     * 
     * @return Returns the budgetConstructionOrganizationReportsDao.
     */
    public BudgetConstructionOrganizationReportsDao getBudgetConstructionOrganizationReportsDao() {
        return budgetConstructionOrganizationReportsDao;
    }

    /**
     * Sets the budgetConstructionOrganizationReportsDao attribute value.
     * 
     * @param budgetConstructionOrganizationReportsDao The budgetConstructionOrganizationReportsDao to set.
     */
    public void setBudgetConstructionOrganizationReportsDao(BudgetConstructionOrganizationReportsDao budgetConstructionOrganizationReportsDao) {
        this.budgetConstructionOrganizationReportsDao = budgetConstructionOrganizationReportsDao;
    }

    /**
     * @see org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService#isLeafOrg(java.lang.String,
     *      java.lang.String)
     */
    public boolean isLeafOrg(String chartOfAccountsCode, String organizationCode) {

        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("String parameter chartOfAccountsCode was null or blank.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("String parameter organizationCode was null or blank.");
        }

        return budgetConstructionOrganizationReportsDao.isLeafOrg(chartOfAccountsCode, organizationCode);
    }

}
