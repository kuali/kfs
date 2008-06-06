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
import org.kuali.module.budget.bo.BudgetConstructionObjectPick;
import org.kuali.module.budget.bo.BudgetConstructionOrgReasonStatisticsReport;
import org.kuali.module.budget.bo.BudgetConstructionReasonCodePick;
import org.kuali.module.budget.bo.BudgetConstructionReportThresholdSettings;
import org.kuali.module.budget.bo.BudgetConstructionSalaryTotal;
import org.kuali.module.budget.dao.BudgetConstructionReasonStatisticsReportDao;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetConstructionReasonStatisticsReportService;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.kuali.module.chart.bo.Chart;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionReasonStatisticsReportServiceImpl implements BudgetConstructionReasonStatisticsReportService {

    BudgetConstructionReasonStatisticsReportDao budgetConstructionReasonStatisticsReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;


    public void updateReasonStatisticsReport(String personUserIdentifier, Integer universityFiscalYear, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();
        if (applyAThreshold) {
            budgetConstructionReasonStatisticsReportDao.updateReasonStatisticsReportsWithAThreshold(personUserIdentifier, universityFiscalYear, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
        }
        else {
            budgetConstructionReasonStatisticsReportDao.updateReasonStatisticsReportsWithoutAThreshold(personUserIdentifier, universityFiscalYear);
        }

    }

    public Collection<BudgetConstructionOrgReasonStatisticsReport> buildReports(Integer universityFiscalYear, String personUserIdentifier, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        Collection<BudgetConstructionOrgReasonStatisticsReport> reportSet = new ArrayList();


        BudgetConstructionOrgReasonStatisticsReport orgReasonStatisticsReportEntry;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionSalaryTotal> reasonStatisticsList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryTotal.class, searchCriteria, orderList);

        // get object codes  --> helper class?
        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        Collection<BudgetConstructionObjectPick> objectCodePickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        String objectCodes = "";
        for (BudgetConstructionObjectPick objectCode : objectCodePickList) {
            objectCodes += objectCode.getFinancialObjectCode() + " ";
        }
        
        // get reason codes  --> helper class?
        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        Collection<BudgetConstructionObjectPick> reasonCodePickList = businessObjectService.findMatching(BudgetConstructionReasonCodePick.class, searchCriteria);
        String reasonCodes = "";
        for (BudgetConstructionObjectPick reasonCode : reasonCodePickList) {
            reasonCodes += reasonCode.getFinancialObjectCode() + " ";
        }
        
        
        // build reports
        for (BudgetConstructionSalaryTotal reasonStatisticsEntry : reasonStatisticsList) {
            orgReasonStatisticsReportEntry = new BudgetConstructionOrgReasonStatisticsReport();
            buildReportsHeader(universityFiscalYear, objectCodes, reasonCodes, orgReasonStatisticsReportEntry, reasonStatisticsEntry, budgetConstructionReportThresholdSettings);
            buildReportsBody(orgReasonStatisticsReportEntry, reasonStatisticsEntry);
            reportSet.add(orgReasonStatisticsReportEntry);
        }
        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, String reasonCodes, BudgetConstructionOrgReasonStatisticsReport orgReasonStatisticsReportEntry, BudgetConstructionSalaryTotal salaryTotalEntry, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {

        // set fiscal year
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgReasonStatisticsReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        // get Chart with orgChartCode
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, salaryTotalEntry.getOrganizationChartOfAccountsCode());
        Chart chart = (Chart) businessObjectService.findByPrimaryKey(Chart.class, searchCriteria);

        // set OrgCode and Desc
        String orgName = salaryTotalEntry.getOrganization().getOrganizationName();
        orgReasonStatisticsReportEntry.setOrganizationCode(salaryTotalEntry.getOrganizationCode());
        if (orgName == null) {
            orgReasonStatisticsReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgReasonStatisticsReportEntry.setOrganizationName(orgName);
        }
        // set ChartCode and Desc
        if (chart == null) {
            orgReasonStatisticsReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
            orgReasonStatisticsReportEntry.setChartOfAccountsCode(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgReasonStatisticsReportEntry.setChartOfAccountsCode(chart.getChartOfAccountsCode());
            orgReasonStatisticsReportEntry.setChartOfAccountDescription(chart.getFinChartOfAccountDescription());
        }
        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgReasonStatisticsReportEntry.setObjectCodes(objectCodes);
        
        if(budgetConstructionReportThresholdSettings.isUseThreshold()){
            if(budgetConstructionReportThresholdSettings.isUseGreaterThanOperator()){
                orgReasonStatisticsReportEntry.setThresholdOrReason(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_GREATER + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            } else {
                orgReasonStatisticsReportEntry.setThresholdOrReason(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_LESS + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            }
        } else {
            orgReasonStatisticsReportEntry.setThresholdOrReason(BCConstants.Report.SELECTED_REASONS + reasonCodes);
        }
        
    }


    public void buildReportsBody(BudgetConstructionOrgReasonStatisticsReport orgReasonStatisticsReportEntry, BudgetConstructionSalaryTotal salaryTotalEntry) {
        orgReasonStatisticsReportEntry.setInitialRequestedFteQuantity(salaryTotalEntry.getInitialRequestedFteQuantity());
        BigDecimal requestedAmount = BudgetConstructionReportHelper.calculateDivide(salaryTotalEntry.getInitialRequestedAmount().bigDecimalValue(), salaryTotalEntry.getInitialRequestedFteQuantity());
        orgReasonStatisticsReportEntry.setTotalInitialRequestedAmount(new Integer(BudgetConstructionReportHelper.setDecimalDigit(requestedAmount, 0).intValue()));
        
        orgReasonStatisticsReportEntry.setAppointmentRequestedFteQuantity(salaryTotalEntry.getAppointmentRequestedFteQuantity());
        orgReasonStatisticsReportEntry.setTotalCsfAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getCsfAmount()));
        orgReasonStatisticsReportEntry.setTotalAppointmentRequestedAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getAppointmentRequestedAmount()));

        BigDecimal decimaCsfAmount = new BigDecimal(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getCsfAmount()));
        BigDecimal decimalAppointmentRequestedAmount = new BigDecimal(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getAppointmentRequestedAmount()));
        orgReasonStatisticsReportEntry.setAverageCsfAmount(BudgetConstructionReportHelper.calculateDivide(decimaCsfAmount, salaryTotalEntry.getAppointmentRequestedFteQuantity()));
        orgReasonStatisticsReportEntry.setAverageAppointmentRequestedAmount(BudgetConstructionReportHelper.calculateDivide(decimalAppointmentRequestedAmount, salaryTotalEntry.getAppointmentRequestedFteQuantity()));
        orgReasonStatisticsReportEntry.setAverageChange(orgReasonStatisticsReportEntry.getAverageAppointmentRequestedAmount().subtract(orgReasonStatisticsReportEntry.getAverageCsfAmount()));
      
        orgReasonStatisticsReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(orgReasonStatisticsReportEntry.getAverageChange(), orgReasonStatisticsReportEntry.getAverageCsfAmount()));


    }

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        return returnList;
    }

    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReasonStatisticsReportDao(BudgetConstructionReasonStatisticsReportDao budgetConstructionReasonStatisticsReportDao) {
        this.budgetConstructionReasonStatisticsReportDao = budgetConstructionReasonStatisticsReportDao;
    }

}
