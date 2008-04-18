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
package org.kuali.module.effort.bo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.core.bo.TransientBusinessObjectBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.effort.EffortPropertyConstants;

public class OutstandingCertificationsByReport extends TransientBusinessObjectBase {

    private Integer universityFiscalYear;
    private String effortCertificationReportNumber;
    private String chartOfAccountsCode;
    private String organizationCode;
    private Integer outstandingCertificationCount;

    private Options options;
    private EffortCertificationReportDefinition effortCertificationReportDefinition;
    private Chart chart;
    private Org organization;

    /**
     * Return the chartOfAccountsCode
     * 
     * @return
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets chartOfAccountsCode
     * 
     * @param chartOfAccountsCode
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets effortCertificationReportNumber
     * 
     * @return
     */
    public String getEffortCertificationReportNumber() {
        return effortCertificationReportNumber;
    }

    /**
     * Sets effortCertificationReportNumber
     * 
     * @param effortCertificationReportNumber
     */
    public void setEffortCertificationReportNumber(String effortCertificationReportNumber) {
        this.effortCertificationReportNumber = effortCertificationReportNumber;
    }

    /**
     * Gets organizationCode
     * 
     * @return
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets organizationCode
     * 
     * @param organizationCode
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets outstandingCertificationCount
     * 
     * @return
     */
    public Integer getOutstandingCertificationCount() {
        return outstandingCertificationCount;
    }

    /**
     * Sets outstandingCertificationCount
     * 
     * @param outstandingCertificationCount
     */
    public void setOutstandingCertificationCount(Integer outstandingCertificationCount) {
        this.outstandingCertificationCount = outstandingCertificationCount;
    }

    /**
     * Gets universityFiscalYear
     * 
     * @return
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets universityFiscalYear
     * 
     * @param universityFiscalYear
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the options attribute.
     * 
     * @return Returns the options.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     */
    public void setOptions(Options options) {
        this.options = options;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.universityFiscalYear);
        m.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, this.effortCertificationReportNumber);
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode);
        m.put(KFSPropertyConstants.ORGANIZATION_CODE, this.organizationCode);

        return m;
    }

    /**
     * Gets the effort certification report definition
     * 
     * @return
     */
    public EffortCertificationReportDefinition getEffortCertificationReportDefinition() {
        return effortCertificationReportDefinition;
    }

    /**
     * Sets effort certification report definition
     * 
     * @param effortCertificationReportDefinition
     */
    public void setEffortCertificationReportDefinition(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        this.effortCertificationReportDefinition = effortCertificationReportDefinition;
    }

    /**
     * Gets the organization
     * 
     * @return
     */
    public Org getOrganization() {
        return organization;
    }

    /**
     * Sets organization
     * 
     * @param organization
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * Gets the chart
     * 
     * @return
     */
    public Chart getChart() {
        Map objectKeys = new HashMap();
        objectKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());

        return (Chart) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Chart.class, objectKeys);
    }

    /**
     * Sets the chart
     * 
     * @param chart
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

}
