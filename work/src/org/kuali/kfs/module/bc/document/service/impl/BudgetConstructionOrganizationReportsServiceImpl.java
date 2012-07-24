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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionOrganizationReportsDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
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
public class BudgetConstructionOrganizationReportsServiceImpl implements BudgetConstructionOrganizationReportsService {

    protected BudgetConstructionOrganizationReportsDao budgetConstructionOrganizationReportsDao;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService#getByPrimaryId(java.lang.String,
     *      java.lang.String)
     */
    public BudgetConstructionOrganizationReports getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ORGANIZATION_CODE, organizationCode);
        return (BudgetConstructionOrganizationReports)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionOrganizationReports.class, keys);
    }
    
    public List getBySearchCriteria(Class cls, Map searchCriteria) {
        return (List)budgetConstructionOrganizationReportsDao.getBySearchCriteria(cls, searchCriteria);
    }
  
    public List getBySearchCriteriaOrderByList(Class cls, Map searchCriteria, List<String> orderList) {
        return (List)budgetConstructionOrganizationReportsDao.getBySearchCriteriaWithOrderByList(cls, searchCriteria, orderList);
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

}
