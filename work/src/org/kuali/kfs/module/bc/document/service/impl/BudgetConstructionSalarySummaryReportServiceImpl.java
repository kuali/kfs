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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionAdministrativePost;
import org.kuali.module.budget.bo.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.BudgetConstructionObjectPick;
import org.kuali.module.budget.bo.BudgetConstructionOrgPositionFundingDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgSalarySummaryReport;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.BudgetConstructionPositionFunding;
import org.kuali.module.budget.bo.BudgetConstructionReportThresholdSettings;
import org.kuali.module.budget.bo.BudgetConstructionSalaryFunding;
import org.kuali.module.budget.bo.BudgetConstructionSalarySocialSecurityNumber;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.dao.BudgetConstructionSalarySummaryReportDao;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetConstructionSalarySummaryReportService;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionSalarySummaryReportServiceImpl implements BudgetConstructionSalarySummaryReportService {

    BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;


    public void updateSalarySummaryReport(String personUserIdentifier, Integer universityFiscalYear, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {

        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();
        if (applyAThreshold) {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithThreshold(personUserIdentifier, universityFiscalYear, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
        }
        else {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithoutThreshold(personUserIdentifier, false);
        }

    }

    public Collection<BudgetConstructionOrgSalarySummaryReport> buildReports(Integer universityFiscalYear, String personUserIdentifier) {
        Collection<BudgetConstructionOrgSalarySummaryReport> reportSet = new ArrayList();

        BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionSalaryFunding> salarySummaryList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryFunding.class, searchCriteria, orderList);
        
        // get BudgetConstructionSalarySocialSecurityNumber and put into map
        searchCriteria.clear();
        Map salarySummaryMap = new HashMap();
        for (BudgetConstructionSalaryFunding salaryFundingEntry : salarySummaryList) {
            searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
            searchCriteria.put(KFSPropertyConstants.EMPLID, salaryFundingEntry.getEmplid());
            BudgetConstructionSalarySocialSecurityNumber bcSSN = (BudgetConstructionSalarySocialSecurityNumber) businessObjectService.findByPrimaryKey(BudgetConstructionSalarySocialSecurityNumber.class, searchCriteria);
            salarySummaryMap.put(salaryFundingEntry, bcSSN); 
        }
        
        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        Collection<BudgetConstructionObjectPick> objectPickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        String objectCodes = "";
        for (BudgetConstructionObjectPick objectPick : objectPickList) {
            objectCodes += objectPick.getFinancialObjectCode() + " ";
        }
        
        for (BudgetConstructionSalaryFunding salaryFundingEntry : salarySummaryList) {
            BudgetConstructionSalarySocialSecurityNumber bcSSN = (BudgetConstructionSalarySocialSecurityNumber) salarySummaryMap.get(salaryFundingEntry);
            orgSalarySummaryReportEntry = new BudgetConstructionOrgSalarySummaryReport();
            buildReportsHeader(universityFiscalYear, objectCodes, orgSalarySummaryReportEntry, salaryFundingEntry, bcSSN);
            buildReportsBody(universityFiscalYear, orgSalarySummaryReportEntry, salaryFundingEntry);
            //buildReportsTotal(orgSalarySummaryReportEntry, salaryFundingEntry, fundingDetailTotalPerson, fundingDetailTotalOrg);
            reportSet.add(orgSalarySummaryReportEntry);

        }
        return reportSet;
    }


    
    
    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, BudgetConstructionSalarySocialSecurityNumber bcSSN) {
        String chartDesc = salaryFundingEntry.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = bcSSN.getOrganization().getOrganizationName();


        Integer prevFiscalyear = universityFiscalYear - 1;
        orgSalarySummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));

        orgSalarySummaryReportEntry.setOrganizationCode(bcSSN.getOrganizationCode());
        if (orgName == null) {
            orgSalarySummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgSalarySummaryReportEntry.setOrganizationName(orgName);
        }

        orgSalarySummaryReportEntry.setChartOfAccountsCode(salaryFundingEntry.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgSalarySummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgSalarySummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }


        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgSalarySummaryReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgSalarySummaryReportEntry.setFinancialObjectCode(salaryFundingEntry.getFinancialObjectCode());
       
        orgSalarySummaryReportEntry.setObjectCodes(objectCodes);
        
        
        
    }
    
    
    
    
    /**
     * builds report body
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry) {
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost;
        BudgetConstructionPosition budgetConstructionPosition;
        BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker;
        PendingBudgetConstructionAppointmentFunding appointmentFundingEntry = salaryFundingEntry.getPendingAppointmentFunding();

        // get budgetConstructionIntendedIncumbent, budgetConstructionAdministrativePost, budgetConstructionPosition objects
        budgetConstructionAdministrativePost = getBudgetConstructionAdministrativePost(appointmentFundingEntry);
        budgetConstructionPosition = getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);

        // set report body
        orgSalarySummaryReportEntry.setAccountNumber(salaryFundingEntry.getAccountNumber());
        orgSalarySummaryReportEntry.setSubAccountNumber(salaryFundingEntry.getSubAccountNumber());
        orgSalarySummaryReportEntry.setFinancialSubObjectCode(salaryFundingEntry.getFinancialSubObjectCode());
      
        


        if (budgetConstructionAdministrativePost != null) {
            orgSalarySummaryReportEntry.setAdministrativePost(budgetConstructionAdministrativePost.getAdministrativePost());
        }

        if (budgetConstructionPosition != null) {
            orgSalarySummaryReportEntry.setPositionNumber(budgetConstructionPosition.getPositionNumber());
            orgSalarySummaryReportEntry.setNormalWorkMonthsAndiuPayMonths(budgetConstructionPosition.getIuNormalWorkMonths() + "/" + budgetConstructionPosition.getIuPayMonths());
            orgSalarySummaryReportEntry.setPositionFte(budgetConstructionPosition.getPositionFullTimeEquivalency().setScale(5, 5).toString());
            orgSalarySummaryReportEntry.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            orgSalarySummaryReportEntry.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
        }
        if (appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
            budgetConstructionCalculatedSalaryFoundationTracker = appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
            orgSalarySummaryReportEntry.setCsfTimePercent(budgetConstructionCalculatedSalaryFoundationTracker.getCsfTimePercent().setScale(2, 2));
            orgSalarySummaryReportEntry.setCsfAmount(new Integer(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue()));

            // calculate amountChange and percentChange
            if (appointmentFundingEntry.getAppointmentRequestedFteQuantity().equals(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity())) {
                Integer amountChange = appointmentFundingEntry.getAppointmentRequestedAmount().subtract(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount()).intValue();
                orgSalarySummaryReportEntry.setAmountChange(amountChange);
                orgSalarySummaryReportEntry.setPercentChange(BudgetConstructionReportHelper.calculateChange(new BigDecimal(amountChange.intValue()), budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().bigDecimalValue()));
            }
        }

        if (appointmentFundingEntry != null) {
            if (appointmentFundingEntry.getFinancialSubObjectCode().equals(BCConstants.Report.BLANK_SUB_OBJECT_CODE)) {
                orgSalarySummaryReportEntry.setFinancialSubObjectCode(BCConstants.Report.BLANK);
            }
            else {
                orgSalarySummaryReportEntry.setFinancialSubObjectCode(appointmentFundingEntry.getFinancialSubObjectCode());
            }

            orgSalarySummaryReportEntry.setAppointmentFundingDurationCode(appointmentFundingEntry.getAppointmentFundingDurationCode());
            orgSalarySummaryReportEntry.setAppointmentTotalIntendedAmount(new Integer(appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue()));
            orgSalarySummaryReportEntry.setAppointmentTotalIntendedFteQuantity(appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().setScale(5, 5).toString());
            if (appointmentFundingEntry.getAppointmentFundingDurationCode().equals(BCConstants.Report.NONE)){
              
                orgSalarySummaryReportEntry.setSalaryAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedAmount().intValue()));
                orgSalarySummaryReportEntry.setPercentAmount(appointmentFundingEntry.getAppointmentRequestedTimePercent());
                orgSalarySummaryReportEntry.setSalaryMonths(appointmentFundingEntry.getAppointmentFundingMonth());
                
            } else {
                orgSalarySummaryReportEntry.setSalaryAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedCsfAmount().intValue()));
                orgSalarySummaryReportEntry.setPercentAmount(appointmentFundingEntry.getAppointmentRequestedCsfTimePercent());
                
                if (budgetConstructionPosition != null){
                    orgSalarySummaryReportEntry.setSalaryMonths(budgetConstructionPosition.getIuNormalWorkMonths());
                }
            }

            // group
            orgSalarySummaryReportEntry.setEmplid(appointmentFundingEntry.getEmplid());
        }


        if (appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
            orgSalarySummaryReportEntry.setDeleteBox(BCConstants.Report.DELETE_MARK);
        }
        else {
            orgSalarySummaryReportEntry.setDeleteBox(BCConstants.Report.BLANK);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        /*returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);*/
        returnList.add(KFSPropertyConstants.EMPLID);
        return returnList;
    }
    
    
    private BudgetConstructionAdministrativePost getBudgetConstructionAdministrativePost(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.EMPLID, appointmentFundingEntry.getEmplid());
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFundingEntry.getPositionNumber());
        return (BudgetConstructionAdministrativePost) businessObjectService.findByPrimaryKey(BudgetConstructionAdministrativePost.class, searchCriteria);
    }

    private BudgetConstructionPosition getBudgetConstructionPosition(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFundingEntry.getPositionNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        return (BudgetConstructionPosition) businessObjectService.findByPrimaryKey(BudgetConstructionPosition.class, searchCriteria);
    }
    
    
    
    
    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }


    public void setBudgetConstructionSalarySummaryReportDao(BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao) {
        this.budgetConstructionSalarySummaryReportDao = budgetConstructionSalarySummaryReportDao;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
