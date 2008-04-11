/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountBalance;
import org.kuali.module.budget.bo.BudgetConstructionObjectSummary;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountObjectDetailReport;
import org.kuali.module.budget.dao.BudgetConstructionAccountObjectDetailReportDao;
import org.kuali.module.budget.service.BudgetConstructionAccountObjectDetailReportService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionAccountObjectDetailReportServiceImpl implements BudgetConstructionAccountObjectDetailReportService {

    BudgetConstructionAccountObjectDetailReportDao budgetConstructionAccountObjectDetailReportDao;
    KualiConfigurationService kualiConfigurationService;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateSubFundSummaryReport(java.lang.String)
     */
    public void updateAccountObjectDetailReport(String personUserIdentifier, boolean consolidated) {
        if (consolidated){
            budgetConstructionAccountObjectDetailReportDao.updateReportsAccountObjectConsolidatedTable(personUserIdentifier);
        } else {
            budgetConstructionAccountObjectDetailReportDao.updateReportsAccountObjectDetailTable(personUserIdentifier);
        }
    }
   
    public Collection<BudgetConstructionOrgAccountObjectDetailReport> buildReports(Integer universityFiscalYear,  String personUserIdentifier){
        Collection<BudgetConstructionOrgAccountObjectDetailReport> reportSet = new ArrayList();
        Collection<BudgetConstructionAccountBalance> accountObjectDetailList;
        
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        accountObjectDetailList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionAccountBalance.class, searchCriteria, orderList);

        
        return reportSet;
    }
    
    
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        
        
        return returnList;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setBudgetConstructionAccountObjectDetailReportDao(BudgetConstructionAccountObjectDetailReportDao budgetConstructionAccountObjectDetailReportDao) {
        this.budgetConstructionAccountObjectDetailReportDao = budgetConstructionAccountObjectDetailReportDao;
    }

    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

}
