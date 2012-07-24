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
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgReasonStatisticsReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalaryTotal;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionReasonStatisticsReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReasonStatisticsReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionReasonStatisticsReportServiceImpl implements BudgetConstructionReasonStatisticsReportService {

    protected BudgetConstructionReasonStatisticsReportDao budgetConstructionReasonStatisticsReportDao;
    protected BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected PersistenceService persistenceServiceOjb;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReasonStatisticsReportService#updateReasonStatisticsReport(java.lang.String,
     *      java.lang.Integer, org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings)
     */
    public void updateReasonStatisticsReport(String principalName, Integer universityFiscalYear, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();
        if (applyAThreshold) {
            budgetConstructionReasonStatisticsReportDao.updateReasonStatisticsReportsWithAThreshold(principalName, universityFiscalYear - 1, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
        }
        else {
            budgetConstructionReasonStatisticsReportDao.updateReasonStatisticsReportsWithoutAThreshold(principalName, universityFiscalYear - 1);
        }

    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReasonStatisticsReportService#buildReports(java.lang.Integer,
     *      java.lang.String, org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings)
     */
    public Collection<BudgetConstructionOrgReasonStatisticsReport> buildReports(Integer universityFiscalYear, String principalId, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        Collection<BudgetConstructionOrgReasonStatisticsReport> reportSet = new ArrayList<BudgetConstructionOrgReasonStatisticsReport>();

        BudgetConstructionOrgReasonStatisticsReport orgReasonStatisticsReportEntry;

        // force OJB to go to DB since it is populated using JDBC
        // normally done in BudgetConstructionReportsServiceHelperImpl.getDataForBuildingReports
        persistenceServiceOjb.clearCache();

        // build searchCriteria
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, principalId);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionSalaryTotal> reasonStatisticsList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryTotal.class, searchCriteria, orderList);

        // get object codes
        String objectCodes = budgetConstructionReportsServiceHelper.getSelectedObjectCodes(principalId);

        // get reason codes
        String reasonCodes = budgetConstructionReportsServiceHelper.getSelectedReasonCodes(principalId);

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
        orgReasonStatisticsReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));

        // get Chart with orgChartCode
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, salaryTotalEntry.getOrganizationChartOfAccountsCode());
        Chart chart = (Chart) businessObjectService.findByPrimaryKey(Chart.class, searchCriteria);

        // set OrgCode and Desc
        String orgName = salaryTotalEntry.getOrganization().getOrganizationName();
        orgReasonStatisticsReportEntry.setOrganizationCode(salaryTotalEntry.getOrganizationCode());
        if (orgName == null) {
            orgReasonStatisticsReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgReasonStatisticsReportEntry.setOrganizationName(orgName);
        }
        // set ChartCode and Desc
        if (chart == null) {
            orgReasonStatisticsReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
            orgReasonStatisticsReportEntry.setChartOfAccountsCode(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgReasonStatisticsReportEntry.setChartOfAccountsCode(chart.getChartOfAccountsCode());
            orgReasonStatisticsReportEntry.setChartOfAccountDescription(chart.getFinChartOfAccountDescription());
        }
        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgReasonStatisticsReportEntry.setObjectCodes(objectCodes);

        if (budgetConstructionReportThresholdSettings.isUseThreshold()) {
            if (budgetConstructionReportThresholdSettings.isUseGreaterThanOperator()) {
                orgReasonStatisticsReportEntry.setThresholdOrReason(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_GREATER + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            }
            else {
                orgReasonStatisticsReportEntry.setThresholdOrReason(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_LESS + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            }
        }
        else {
            orgReasonStatisticsReportEntry.setThresholdOrReason(BCConstants.Report.SELECTED_REASONS + reasonCodes);
        }

    }

    public void buildReportsBody(BudgetConstructionOrgReasonStatisticsReport orgReasonStatisticsReportEntry, BudgetConstructionSalaryTotal salaryTotalEntry) {
        orgReasonStatisticsReportEntry.setInitialRequestedFteQuantity(salaryTotalEntry.getInitialRequestedFteQuantity());
        orgReasonStatisticsReportEntry.setTotalInitialRequestedAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getInitialRequestedAmount()));

        BigDecimal averageAmount = BudgetConstructionReportHelper.calculateDivide(salaryTotalEntry.getInitialRequestedAmount().bigDecimalValue(), salaryTotalEntry.getInitialRequestedFteQuantity());
        orgReasonStatisticsReportEntry.setTotalAverageAmount(BudgetConstructionReportHelper.setDecimalDigit(averageAmount, 0, false).intValue());

        BigDecimal requestedFteQuantity = salaryTotalEntry.getAppointmentRequestedFteQuantity().setScale(5, BigDecimal.ROUND_HALF_UP);
        orgReasonStatisticsReportEntry.setAppointmentRequestedFteQuantity(requestedFteQuantity);
        
        orgReasonStatisticsReportEntry.setTotalCsfAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getCsfAmount()));
        orgReasonStatisticsReportEntry.setTotalAppointmentRequestedAmount(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getAppointmentRequestedAmount()));

        BigDecimal csfAmount = new BigDecimal(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getCsfAmount()));
        BigDecimal averageCsfAmount = BudgetConstructionReportHelper.calculateDivide(csfAmount, salaryTotalEntry.getAppointmentRequestedFteQuantity());
        orgReasonStatisticsReportEntry.setAverageCsfAmount(BudgetConstructionReportHelper.setDecimalDigit(averageCsfAmount, 0, false));

        BigDecimal appointmentRequestedAmount = new BigDecimal(BudgetConstructionReportHelper.convertKualiInteger(salaryTotalEntry.getAppointmentRequestedAmount()));
        BigDecimal averageRequestedAmount = BudgetConstructionReportHelper.calculateDivide(appointmentRequestedAmount, requestedFteQuantity);
        orgReasonStatisticsReportEntry.setAverageAppointmentRequestedAmount(BudgetConstructionReportHelper.setDecimalDigit(averageRequestedAmount, 0, false));

        orgReasonStatisticsReportEntry.setAverageChange(orgReasonStatisticsReportEntry.getAverageAppointmentRequestedAmount().subtract(orgReasonStatisticsReportEntry.getAverageCsfAmount()));

        orgReasonStatisticsReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(orgReasonStatisticsReportEntry.getAverageChange(), orgReasonStatisticsReportEntry.getAverageCsfAmount()));
    }

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList<String>();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);

        return returnList;
    }

    /**
     * Sets the budgetConstructionReasonStatisticsReportDao attribute value.
     * 
     * @param budgetConstructionReasonStatisticsReportDao The budgetConstructionReasonStatisticsReportDao to set.
     */
    public void setBudgetConstructionReasonStatisticsReportDao(BudgetConstructionReasonStatisticsReportDao budgetConstructionReasonStatisticsReportDao) {
        this.budgetConstructionReasonStatisticsReportDao = budgetConstructionReasonStatisticsReportDao;
    }

    /**
     * Sets the budgetConstructionOrganizationReportsService attribute value.
     * 
     * @param budgetConstructionOrganizationReportsService The budgetConstructionOrganizationReportsService to set.
     */
    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the budgetConstructionReportsServiceHelper attribute value.
     * 
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
