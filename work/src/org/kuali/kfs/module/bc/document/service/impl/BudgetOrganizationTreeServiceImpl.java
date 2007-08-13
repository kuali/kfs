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

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.module.budget.dao.BudgetConstructionDao;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetOrganizationTreeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the BudgetOrganizationTreeService interface
 */
@Transactional
public class BudgetOrganizationTreeServiceImpl implements BudgetOrganizationTreeService {
    private BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    private BusinessObjectService businessObjectService;
    private BudgetConstructionDao budgetConstructionDao;

    /**
     * @see org.kuali.module.budget.service.BudgetOrganizationTreeService#buildPullup(java.lang.String, java.lang.String, java.lang.String)
     */
    public void buildPullup(String personUserIdentifier, String chartOfAccountsCode, String organizationCode) {
        cleanPullup(personUserIdentifier);
        buildSubTree(personUserIdentifier, chartOfAccountsCode, organizationCode);
    }
    
    private void buildSubTree(String personUserIdentifier, String chartOfAccountsCode, String organizationCode){
        
        BudgetConstructionOrganizationReports bcOrgRpts = budgetConstructionOrganizationReportsService.getByPrimaryId(chartOfAccountsCode, organizationCode);
        if (bcOrgRpts != null){
            BudgetConstructionPullup bcPullup = new BudgetConstructionPullup();
            bcPullup.setPersonUniversalIdentifier(personUserIdentifier);
            bcPullup.setChartOfAccountsCode(bcOrgRpts.getChartOfAccountsCode());
            bcPullup.setOrganizationCode(bcOrgRpts.getOrganizationCode());
            bcPullup.setReportsToChartOfAccountsCode(bcOrgRpts.getReportsToChartOfAccountsCode());
            bcPullup.setReportsToOrganizationCode(bcOrgRpts.getReportsToOrganizationCode());
            bcPullup.setPullFlag(new Integer(0));
            businessObjectService.save(bcPullup);
        }
    }

    /**
     * @see org.kuali.module.budget.service.BudgetOrganizationTreeService#cleanPullup(java.lang.String)
     */
    public void cleanPullup(String personUserIdentifier) {

        budgetConstructionDao.deleteBudgetConstructionPullupByUserId(personUserIdentifier);

    }

    /**
     * Gets the budgetConstructionOrganizationReportsService attribute. 
     * @return Returns the budgetConstructionOrganizationReportsService.
     */
    public BudgetConstructionOrganizationReportsService getBudgetConstructionOrganizationReportsService() {
        return budgetConstructionOrganizationReportsService;
    }

    /**
     * Sets the budgetConstructionOrganizationReportsService attribute value.
     * @param budgetConstructionOrganizationReportsService The budgetConstructionOrganizationReportsService to set.
     */
    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the budgetConstructionDao attribute. 
     * @return Returns the budgetConstructionDao.
     */
    public BudgetConstructionDao getBudgetConstructionDao() {
        return budgetConstructionDao;
    }

    /**
     * Sets the budgetConstructionDao attribute value.
     * @param budgetConstructionDao The budgetConstructionDao to set.
     */
    public void setBudgetConstructionDao(BudgetConstructionDao budgetConstructionDao) {
        this.budgetConstructionDao = budgetConstructionDao;
    }

}
