/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ec.businessobject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public class OutstandingCertificationsByReport extends PersistableBusinessObjectBase {

    private Integer universityFiscalYear;
    private String effortCertificationReportNumber;
    private String chartOfAccountsCode;
    private String organizationCode;
    private Integer outstandingCertificationCount;

    private SystemOptions options;
    private EffortCertificationReportDefinition effortCertificationReportDefinition;
    private Chart chart;
    private Organization organization;

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
    public SystemOptions getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     */
    public void setOptions(SystemOptions options) {
        this.options = options;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
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
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets organization
     * 
     * @param organization
     */
    public void setOrganization(Organization organization) {
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
