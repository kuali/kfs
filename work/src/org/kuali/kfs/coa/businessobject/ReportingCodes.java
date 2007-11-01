/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.context.SpringContext;

/**
 * Reporting Codes Business Object
 */
public class ReportingCodes extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = -1585612121519839488L;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String financialReportingCode;
    private String financialReportingCodeDescription;
    private String financialReportingCodeMgrId;
    private String financialReportsToReportingCode;

    private Chart chart;
    private Org org;
    private UniversalUser universalUser;
    private ReportingCodes reportingCodes;

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return Returns the financialReportingCode.
     */
    public String getFinancialReportingCode() {
        return financialReportingCode;
    }

    /**
     * @param financialReportingCode The financialReportingCode to set.
     */
    public void setFinancialReportingCode(String financialReportingCode) {
        this.financialReportingCode = financialReportingCode;
    }

    /**
     * @return Returns the financialReportingCodeDescription.
     */
    public String getFinancialReportingCodeDescription() {
        return financialReportingCodeDescription;
    }

    /**
     * @param financialReportingCodeDescription The financialReportingCodeDescription to set.
     */
    public void setFinancialReportingCodeDescription(String financialReportingCodeDescription) {
        this.financialReportingCodeDescription = financialReportingCodeDescription;
    }

    /**
     * @return Returns the financialReportingCodeMgrId.
     */
    public String getFinancialReportingCodeMgrId() {
        return financialReportingCodeMgrId;
    }

    /**
     * @param financialReportingCodeMgrId The financialReportingCodeMgrId to set.
     */
    public void setFinancialReportingCodeMgrId(String financialReportingCodeMgrId) {
        this.financialReportingCodeMgrId = financialReportingCodeMgrId;
    }

    /**
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * @return Returns the financialReportsToReportingCode.
     */
    public String getFinancialReportsToReportingCode() {
        return financialReportsToReportingCode;
    }

    /**
     * @param financialReportsToReportingCode The financialReportsToReportingCode to set.
     */
    public void setFinancialReportsToReportingCode(String financialReportsToReportingCode) {
        this.financialReportsToReportingCode = financialReportsToReportingCode;
    }

    /**
     * @return Returns the chart.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * @param chart The chart to set.
     * @deprecated
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @return Returns the org.
     */
    public Org getOrg() {
        return org;
    }

    /**
     * @param org The org to set.
     * @deprecated
     */
    public void setOrg(Org org) {
        this.org = org;
    }

    public UniversalUser getUniversalUser() {
        universalUser = SpringContext.getBean(UniversalUserService.class).updateUniversalUserIfNecessary(financialReportingCodeMgrId, universalUser);
        return universalUser;
    }

    /**
     * @param universalUser The universalUser to set.
     * @deprecated
     */
    public void setUniversalUser(UniversalUser universalUser) {
        this.universalUser = universalUser;
    }

    /**
     * @return Returns the reportingCodes.
     */
    public ReportingCodes getReportingCodes() {
        return reportingCodes;
    }

    /**
     * @param reportingCodes The reportingCodes to set.
     * @deprecated
     */
    public void setReportingCodes(ReportingCodes reportingCodes) {
        this.reportingCodes = reportingCodes;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put("financialReportingCode", this.financialReportingCode);
        m.put("financialReportingCodeDescription", this.financialReportingCodeDescription);
        m.put("financialReportingCodeMgrId", this.financialReportingCodeMgrId);
        m.put("financialReportsToReportingCode", this.financialReportsToReportingCode);
        return m;
    }

}