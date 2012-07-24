/*
 * Copyright 2008 The Kuali Foundation
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
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionSalaryStatisticsReportService;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionSalaryStatisticsReportServiceImpl implements BudgetConstructionSalaryStatisticsReportService {

    protected BudgetConstructionSalaryStatisticsReportDao budgetConstructionSalaryStatisticsReportDao;
    protected BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected PersistenceService persistenceServiceOjb;

    public void updateSalaryStatisticsReport(String principalName, Integer universityFiscalYear) {
        budgetConstructionSalaryStatisticsReportDao.updateReportsSalaryStatisticsTable(principalName, universityFiscalYear-1);
    }

    public Collection<BudgetConstructionOrgSalaryStatisticsReport> buildReports(Integer universityFiscalYear, String principalName) {
        Collection<BudgetConstructionOrgSalaryStatisticsReport> reportSet = new ArrayList();

        // force OJB to go to DB since it is populated using JDBC
        // normally done in BudgetConstructionReportsServiceHelperImpl.getDataForBuildingReports
        persistenceServiceOjb.clearCache();

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
        
        String objectCodes = budgetConstructionReportsServiceHelper.getSelectedObjectCodes(principalName);
        
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
        orgSalaryStatisticsReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        // get Chart with orgChartCode
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, salaryTotalEntry.getOrganizationChartOfAccountsCode());
        Chart chart = (Chart) businessObjectService.findByPrimaryKey(Chart.class, searchCriteria);

        // set OrgCode and Desc
        String orgName = salaryTotalEntry.getOrganization().getOrganizationName();
        orgSalaryStatisticsReportEntry.setOrganizationCode(salaryTotalEntry.getOrganizationCode());
        if (orgName == null) {
            orgSalaryStatisticsReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgSalaryStatisticsReportEntry.setOrganizationName(orgName);
        }
        // set ChartCode and Desc
        if (chart == null) {
            orgSalaryStatisticsReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
            orgSalaryStatisticsReportEntry.setChartOfAccountsCode(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgSalaryStatisticsReportEntry.setChartOfAccountsCode(chart.getChartOfAccountsCode());
            orgSalaryStatisticsReportEntry.setChartOfAccountDescription(chart.getFinChartOfAccountDescription());
        }
        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgSalaryStatisticsReportEntry.setObjectCodes(objectCodes);

        // place holder for possible threshold use
        orgSalaryStatisticsReportEntry.setThreshold("");
    }


    public void buildReportsBody(BudgetConstructionOrgSalaryStatisticsReport orgSalaryStatisticsReportEntry, BudgetConstructionSalaryTotal salaryTotalEntry) {
        orgSalaryStatisticsReportEntry.setInitialRequestedFteQuantity(salaryTotalEntry.getInitialRequestedFteQuantity());
        
        orgSalaryStatisticsReportEntry.setTotalInitialRequestedAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getInitialRequestedAmount()));

        BigDecimal averageAmount = BudgetConstructionReportHelper.calculateDivide(salaryTotalEntry.getInitialRequestedAmount().bigDecimalValue(), salaryTotalEntry.getInitialRequestedFteQuantity());
        orgSalaryStatisticsReportEntry.setTotalAverageAmount(BudgetConstructionReportHelper.setDecimalDigit(averageAmount, 0, false).intValue());
        
        BigDecimal requestedFteQuantity = salaryTotalEntry.getAppointmentRequestedFteQuantity().setScale(5, BigDecimal.ROUND_HALF_UP);
        orgSalaryStatisticsReportEntry.setAppointmentRequestedFteQuantity(requestedFteQuantity);
        
        orgSalaryStatisticsReportEntry.setTotalCsfAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getCsfAmount()));
        orgSalaryStatisticsReportEntry.setTotalAppointmentRequestedAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getAppointmentRequestedAmount()));

        BigDecimal csfAmount = new BigDecimal(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getCsfAmount()));
        BigDecimal averageCfsAmount = BudgetConstructionReportHelper.calculateDivide(csfAmount, salaryTotalEntry.getAppointmentRequestedFteQuantity());
        orgSalaryStatisticsReportEntry.setAverageCsfAmount(BudgetConstructionReportHelper.setDecimalDigit(averageCfsAmount, 0, false));
        
        BigDecimal appointmentRequestedAmount = new BigDecimal(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getAppointmentRequestedAmount()));
        BigDecimal averageRequestedAmount = BudgetConstructionReportHelper.calculateDivide(appointmentRequestedAmount, requestedFteQuantity);
        orgSalaryStatisticsReportEntry.setAverageAppointmentRequestedAmount(BudgetConstructionReportHelper.setDecimalDigit(averageRequestedAmount, 0, false));
        
        BigDecimal averageChange = orgSalaryStatisticsReportEntry.getAverageAppointmentRequestedAmount().subtract(orgSalaryStatisticsReportEntry.getAverageCsfAmount());
        orgSalaryStatisticsReportEntry.setAverageChange(averageChange);
      
        BigDecimal percentChange = BudgetConstructionReportHelper.calculatePercent(orgSalaryStatisticsReportEntry.getAverageChange(), orgSalaryStatisticsReportEntry.getAverageCsfAmount());
        orgSalaryStatisticsReportEntry.setPercentChange(percentChange);
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

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the budgetConstructionReportsServiceHelper attribute value.
     * @param budgetConstructionReportsServiceHelper The budgetConstructionReportsServiceHelper to set.
     */
    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }

    /**
     * Gets the persistenceServiceOjb attribute.
     * 
     * @return Returns the persistenceServiceOjb
     */
    
    public PersistenceService getPersistenceServiceOjb() {
        return persistenceServiceOjb;
    }

    /**	
     * Sets the persistenceServiceOjb attribute.
     * 
     * @param persistenceServiceOjb The persistenceServiceOjb to set.
     */
    public void setPersistenceServiceOjb(PersistenceService persistenceServiceOjb) {
        this.persistenceServiceOjb = persistenceServiceOjb;
    }

}

