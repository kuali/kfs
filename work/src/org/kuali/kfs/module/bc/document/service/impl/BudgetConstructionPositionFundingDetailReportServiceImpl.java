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
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionObjectPick;
import org.kuali.module.budget.bo.BudgetConstructionOrgPositionFundingDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionPositionFunding;
import org.kuali.module.budget.bo.BudgetConstructionReportThresholdSettings;
import org.kuali.module.budget.dao.BudgetConstructionPositionFundingDetailReportDao;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetConstructionPositionFundingDetailReportService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionPositionFundingDetailReportServiceImpl implements BudgetConstructionPositionFundingDetailReportService {

    BudgetConstructionPositionFundingDetailReportDao budgetConstructionPositionFundingDetailReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;
     
    
    public void updatePositionFundingDetailReport(String personUserIdentifier, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings){
    
    boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
    boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
    KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();
    
    budgetConstructionPositionFundingDetailReportDao.updateReportsPositionFundingDetailTable(personUserIdentifier, applyAThreshold, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
    }
    
    
    
    
    
    
    public Collection<BudgetConstructionOrgPositionFundingDetailReport> buildReports(Integer universityFiscalYear, String personUserIdentifier) {
        Collection<BudgetConstructionOrgPositionFundingDetailReport> reportSet = new ArrayList();
        
        
        //List<BudgetConstructionOrgPositionFundingDetailReportTotal> orgPositionFundingDetailReportTotalList;
        BudgetConstructionOrgPositionFundingDetailReport orgPositionFundingDetailReportEntry;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionPositionFunding> positionFundingDetailList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionPositionFunding.class, searchCriteria, orderList);
        
        Map appointmentFundingEntireMap = new HashMap();
    /*    for (BudgetConstructionObjectDump positionFundingDetailEntry : positionFundingDetailList) {
            appointmentFundingEntireMap.put(positionFundingDetailEntry, getPendingBudgetConstructionAppointmentFundingList(universityFiscalYear, positionFundingDetailEntry));
        }
    */    
        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        Collection<BudgetConstructionObjectPick> objectPickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        String objectCodes = "";
        for (BudgetConstructionObjectPick objectPick: objectPickList){
            objectCodes += objectPick.getFinancialObjectCode() + " ";
        }
         
   //     List<BudgetConstructionObjectDump> listForCalculateTotalObject = deleteDuplicated((List) positionFundingDetailList, 1);
   //     List<BudgetConstructionObjectDump> listForCalculateTotalAccount = deleteDuplicated((List) positionFundingDetailList, 2);

        // Calculate Total Section
        //Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalObject = calculateObjectTotal(appointmentFundingEntireMap, listForCalculateTotalObject);
        //Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalAccount = calculateAccountTotal(fundingDetailTotalObject, listForCalculateTotalAccount);

    /*    for (BudgetConstructionObjectDump positionFundingDetailEntry : positionFundingDetailList) {
            Collection<PendingBudgetConstructionAppointmentFunding> appointmentFundingCollection = (Collection<PendingBudgetConstructionAppointmentFunding>) appointmentFundingEntireMap.get(positionFundingDetailEntry);
            for (PendingBudgetConstructionAppointmentFunding appointmentFundingEntry : appointmentFundingCollection) {

                orgPositionFundingDetailReportEntry = new BudgetConstructionOrgPositionFundingDetailReport();
                buildReportsHeader(universityFiscalYear, objectCodes, orgPositionFundingDetailReportEntry, positionFundingDetailEntry);
                buildReportsBody(universityFiscalYear, orgPositionFundingDetailReportEntry, appointmentFundingEntry);
                buildReportsTotal(orgPositionFundingDetailReportEntry, positionFundingDetailEntry, fundingDetailTotalObject, fundingDetailTotalAccount);
                reportSet.add(orgPositionFundingDetailReportEntry);


            }

        }*/

        return reportSet;
    }
    
    

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.SELECTED_ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.SELECTED_ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.PERSON_NAME);
        returnList.add(KFSPropertyConstants.EMPLID);
        returnList.add(KFSPropertyConstants.POSITION_NUMBER);
        returnList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);        
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return returnList;
    }






    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }






    public void setBudgetConstructionPositionFundingDetailReportDao(BudgetConstructionPositionFundingDetailReportDao budgetConstructionPositionFundingDetailReportDao) {
        this.budgetConstructionPositionFundingDetailReportDao = budgetConstructionPositionFundingDetailReportDao;
    }






    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }






    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }



    
    
}
