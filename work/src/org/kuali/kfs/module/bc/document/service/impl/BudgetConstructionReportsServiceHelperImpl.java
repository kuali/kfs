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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectDump;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectPick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalaryFunding;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalarySocialSecurityNumber;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class BudgetConstructionReportsServiceHelperImpl implements BudgetConstructionReportsServiceHelper {

    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    BusinessObjectService businessObjectService;

    
    
    
    public Collection getDataForBuildingReports(Class clazz, String principalName, List<String> orderList){
        
        //build searchCriteria 
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, principalName);

        // build order list
        return budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(clazz, searchCriteria, orderList);
    }
    
    public Collection getDataForBuildingReports(Class clazz, Map searchCriteria, List<String> orderList){
        
        return budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(clazz, searchCriteria, orderList);
    }
    
    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode){
        
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, financialObjectCode);
        return (ObjectCode) businessObjectService.findByPrimaryKey(ObjectCode.class, searchCriteria);
    }

    public String getSelectedObjectCodes (String principalName){
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalName);
        searchCriteria.put(KFSPropertyConstants.SELECT_FLAG, 1);
        Collection<BudgetConstructionObjectPick> objectPickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        String objectCodes = "";
        int count = 0; 
        for (BudgetConstructionObjectPick objectPick : objectPickList) {
            count += 1;
            objectCodes += objectPick.getFinancialObjectCode();
            if (count < objectPickList.size()){
                objectCodes += ", ";
            }
        }
        return objectCodes;
    }
    
    
    public BudgetConstructionAdministrativePost getBudgetConstructionAdministrativePost(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.EMPLID, appointmentFundingEntry.getEmplid());
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFundingEntry.getPositionNumber());
        return (BudgetConstructionAdministrativePost) businessObjectService.findByPrimaryKey(BudgetConstructionAdministrativePost.class, searchCriteria);
    }

    public BudgetConstructionPosition getBudgetConstructionPosition(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFundingEntry.getPositionNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        return (BudgetConstructionPosition) businessObjectService.findByPrimaryKey(BudgetConstructionPosition.class, searchCriteria);
    }

    public BudgetConstructionIntendedIncumbent getBudgetConstructionIntendedIncumbent(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.EMPLID, appointmentFundingEntry.getEmplid());
        return (BudgetConstructionIntendedIncumbent) businessObjectService.findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, searchCriteria);
    }
    
    public BudgetConstructionSalarySocialSecurityNumber getBudgetConstructionSalarySocialSecurityNumber(String principalName, BudgetConstructionSalaryFunding salaryFunding){
        
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalName);
        searchCriteria.put(KFSPropertyConstants.EMPLID, salaryFunding.getEmplid());
        
        List<String> orderList = new ArrayList();
        orderList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        orderList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        orderList.add(KFSPropertyConstants.PERSON_NAME);
        orderList.add(KFSPropertyConstants.EMPLID);

        return (BudgetConstructionSalarySocialSecurityNumber) businessObjectService.findByPrimaryKey(BudgetConstructionSalarySocialSecurityNumber.class, searchCriteria);
    }
    
    public Collection<BudgetConstructionSalaryFunding> getSalaryFunding(String principalName, String emplid){
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalName);
        searchCriteria.put(KFSPropertyConstants.EMPLID, emplid);
        
        List<String> orderList = new ArrayList();
        orderList.add(KFSPropertyConstants.POSITION_NUMBER);
        orderList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        orderList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        orderList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        orderList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        orderList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        orderList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        
        return budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryFunding.class, searchCriteria, orderList);
    }
    
    
    
    
    public Collection<PendingBudgetConstructionAppointmentFunding> getPendingBudgetConstructionAppointmentFundingList(Integer universityFiscalYear, BudgetConstructionObjectDump budgetConstructionObjectDump) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, budgetConstructionObjectDump.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, budgetConstructionObjectDump.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, budgetConstructionObjectDump.getSubAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, budgetConstructionObjectDump.getFinancialObjectCode());
        
        List<String> orderList = new ArrayList();
        orderList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        orderList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        orderList.add(KFSPropertyConstants.POSITION_NUMBER);
        orderList.add(KFSPropertyConstants.EMPLID);
        return budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(PendingBudgetConstructionAppointmentFunding.class, searchCriteria, orderList);
    }



    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }




    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
   }

