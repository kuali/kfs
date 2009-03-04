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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectPick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgSalaryStatisticsReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalaryTotal;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionSalaryStatisticsReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionSalaryStatisticsReportService;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionSalaryStatisticsReportServiceImpl implements BudgetConstructionSalaryStatisticsReportService {

    BudgetConstructionSalaryStatisticsReportDao budgetConstructionSalaryStatisticsReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;

    public void updateSalaryStatisticsReport(String principalName, Integer universityFiscalYear) {
        budgetConstructionSalaryStatisticsReportDao.updateReportsSalaryStatisticsTable(principalName, universityFiscalYear);
    }

    public Collection<BudgetConstructionOrgSalaryStatisticsReport> buildReports(Integer universityFiscalYear, String principalName) {
        Collection<BudgetConstructionOrgSalaryStatisticsReport> reportSet = new ArrayList();

        // build searchCriteria
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, principalName);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionSalaryTotal> salaryStatisticsList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryTotal.class, searchCriteria, orderList);
        
        // get object codes
        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalName);
        Collection<BudgetConstructionObjectPick> objectPickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        
        String objectCodes = "";
        for (BudgetConstructionObjectPick objectPick : objectPickList) {
            objectCodes += objectPick.getFinancialObjectCode() + " ";
        }
        
        // build reports
        for (BudgetConstructionSalaryTotal salaryStatisticsEntry : salaryStatisticsList) {
            BudgetConstructionOrgSalaryStatisticsReport orgSalaryStatisticsReportEntry = new BudgetConstructionOrgSalaryStatisticsReport();
            buildReportsHeader(universityFiscalYear, objectCodes, orgSalaryStatisticsReportEntry, salaryStatisticsEntry);
            buildReportsBody(orgSalaryStatisticsReportEntry, salaryStatisticsEntry);
            reportSet.add(orgSalaryStatisticsReportEntry);
        }
        
        return reportSet;
    }


    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, BudgetConstructionOrgSalaryStatisticsReport orgSalaryStatisticsReportEntry, BudgetConstructionSalaryTotal salaryTotalEntry) {

        // set fiscal year
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgSalaryStatisticsReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        // get Chart with orgChartCode
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, salaryTotalEntry.getOrganizationChartOfAccountsCode());
        Chart chart = (Chart) businessObjectService.findByPrimaryKey(Chart.class, searchCriteria);

        // set OrgCode and Desc
        String orgName = salaryTotalEntry.getOrganization().getOrganizationName();
        orgSalaryStatisticsReportEntry.setOrganizationCode(salaryTotalEntry.getOrganizationCode());
        if (orgName == null) {
            orgSalaryStatisticsReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgSalaryStatisticsReportEntry.setOrganizationName(orgName);
        }
        // set ChartCode and Desc
        if (chart == null) {
            orgSalaryStatisticsReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
            orgSalaryStatisticsReportEntry.setChartOfAccountsCode(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgSalaryStatisticsReportEntry.setChartOfAccountsCode(chart.getChartOfAccountsCode());
            orgSalaryStatisticsReportEntry.setChartOfAccountDescription(chart.getFinChartOfAccountDescription());
        }
        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgSalaryStatisticsReportEntry.setObjectCodes(objectCodes);
    }


    public void buildReportsBody(BudgetConstructionOrgSalaryStatisticsReport orgSalaryStatisticsReportEntry, BudgetConstructionSalaryTotal salaryTotalEntry) {
        orgSalaryStatisticsReportEntry.setInitialRequestedFteQuantity(salaryTotalEntry.getInitialRequestedFteQuantity());
        
        BigDecimal requestedAmount = BudgetConstructionReportHelper.calculateDivide(salaryTotalEntry.getInitialRequestedAmount().bigDecimalValue(), salaryTotalEntry.getInitialRequestedFteQuantity());
        orgSalaryStatisticsReportEntry.setTotalInitialRequestedAmount(new Integer(BudgetConstructionReportHelper.setDecimalDigit(requestedAmount, 0, false).intValue()));
        
        orgSalaryStatisticsReportEntry.setAppointmentRequestedFteQuantity(salaryTotalEntry.getAppointmentRequestedFteQuantity().setScale(5));
        orgSalaryStatisticsReportEntry.setTotalCsfAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getCsfAmount()));
        orgSalaryStatisticsReportEntry.setTotalAppointmentRequestedAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getAppointmentRequestedAmount()));

        BigDecimal decimaCsfAmount = new BigDecimal(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getCsfAmount()));
        BigDecimal decimalAppointmentRequestedAmount = new BigDecimal(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getAppointmentRequestedAmount()));
        orgSalaryStatisticsReportEntry.setAverageCsfAmount(BudgetConstructionReportHelper.calculateDivide(decimaCsfAmount, salaryTotalEntry.getAppointmentRequestedFteQuantity()));
        orgSalaryStatisticsReportEntry.setAverageAppointmentRequestedAmount(BudgetConstructionReportHelper.calculateDivide(decimalAppointmentRequestedAmount, salaryTotalEntry.getAppointmentRequestedFteQuantity()));
        orgSalaryStatisticsReportEntry.setAverageChange(orgSalaryStatisticsReportEntry.getAverageAppointmentRequestedAmount().subtract(orgSalaryStatisticsReportEntry.getAverageCsfAmount()));
      
        orgSalaryStatisticsReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(orgSalaryStatisticsReportEntry.getAverageChange(), orgSalaryStatisticsReportEntry.getAverageCsfAmount()));

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


    public void setBudgetConstructionSalaryStatisticsReportDao(BudgetConstructionSalaryStatisticsReportDao budgetConstructionSalaryStatisticsReportDao) {
        this.budgetConstructionSalaryStatisticsReportDao = budgetConstructionSalaryStatisticsReportDao;
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

}

