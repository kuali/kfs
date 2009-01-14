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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.ChartHierarchyService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationHierarchyService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionOrganizationReportsDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.sys.context.SpringContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the getByPrimaryId method defined by BudgetConstructionOrganizationReportsService.
 * 
 * @param chartOfAccountsCode The FIN_COA_CD that is being searched for
 * @param organizationCode the ORG_CD that is being searched for
 * @return BudgetConstructionOrganizationReports Business Object
 * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService#getByPrimaryId(java.lang.String,
 *      java.lang.String)
 */
@Transactional
public class BudgetConstructionOrganizationReportsServiceImpl implements BudgetConstructionOrganizationReportsService, OrganizationHierarchyService {

    private BudgetConstructionOrganizationReportsDao budgetConstructionOrganizationReportsDao;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService#getByPrimaryId(java.lang.String,
     *      java.lang.String)
     */
    public BudgetConstructionOrganizationReports getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
        return budgetConstructionOrganizationReportsDao.getByPrimaryId(chartOfAccountsCode, organizationCode);
    }
    
    public Collection getBySearchCriteria(Class cls, Map searchCriteria) {
        return budgetConstructionOrganizationReportsDao.getBySearchCriteria(cls, searchCriteria);
    }
  
    public Collection getBySearchCriteriaOrderByList(Class cls, Map searchCriteria, List<String> orderList) {
        return budgetConstructionOrganizationReportsDao.getBySearchCriteriaWithOrderByList(cls, searchCriteria, orderList);
    }
 
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService#getActiveChildOrgs(java.lang.String,
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
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService#isLeafOrg(java.lang.String,
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

    /**
     * @see org.kuali.kfs.coa.service.OrganizationHierarchyService#isParentOrganization(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean isParentOrganization(String potentialChildChartCode, String potentialChildOrganizationCode, String potentialParentChartCode, String potentialParentOrganizationCode) {
        List<BudgetConstructionOrganizationReports> childOrgs = budgetConstructionOrganizationReportsDao.getActiveChildOrgs(potentialParentChartCode, potentialParentOrganizationCode);
        for (BudgetConstructionOrganizationReports orgReports : childOrgs) {
            if (potentialChildChartCode.equals(orgReports.getChartOfAccountsCode()) && potentialChildOrganizationCode.equals(orgReports.getOrganizationCode())) {
                return true;
            }
        }

        return false;
    }
}
