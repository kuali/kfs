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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionMonthSummary;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetConstructionReportsServiceHelper;
import org.kuali.module.chart.bo.ObjectCode;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class BudgetConstructionReportsServiceHelperImpl implements BudgetConstructionReportsServiceHelper {

    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    BusinessObjectService businessObjectService;

    
    
    
    public Collection getDataForBuildingReports(Class clazz, String personUserIdentifier, List<String> orderList){
        
        //build searchCriteria 
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        return budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(clazz, searchCriteria, orderList);
    }
    
    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode){
        
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, financialObjectCode);
        return (ObjectCode) businessObjectService.findByPrimaryKey(ObjectCode.class, searchCriteria);
    }
    




    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }




    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
   }
