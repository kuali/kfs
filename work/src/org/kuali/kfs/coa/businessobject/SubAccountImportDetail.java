/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;

/**
 * This class is the detail business object for Sub Account Import Global Maintenance Document
 */
public class SubAccountImportDetail extends MassImportLineBase {

    private static final Logger LOG = Logger.getLogger(SubAccountImportDetail.class);
    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String subAccountName;
    private String financialReportChartCode;
    private String finReportOrganizationCode;
    private String financialReportingCode;
    private boolean active;

    private Account account;
    private Chart chart;
    private Organization finacialReportOrg;
    private Chart financialReportChart;
    private ReportingCode reportingCode;

    // transient
    private String subAccountTypeCode;

    /**
     * Default constructor.
     */
    public SubAccountImportDetail() {

    }


    /**
     * Sub account type code always default to EX
     *
     * @return
     */
    public String getDefaultSubAccountTypeCode() {
        return KFSConstants.SubAccountType.EXPENSE;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     *
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     *
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the subAccountName attribute.
     *
     * @return Returns the subAccountName.
     */
    public String getSubAccountName() {
        return subAccountName;
    }

    /**
     * Sets the subAccountName attribute value.
     *
     * @param subAccountName The subAccountName to set.
     */
    public void setSubAccountName(String subAccountName) {
        this.subAccountName = subAccountName;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the financialReportChartCode attribute.
     *
     * @return Returns the financialReportChartCode.
     */
    public String getFinancialReportChartCode() {
        return financialReportChartCode;
    }

    /**
     * Sets the financialReportChartCode attribute value.
     *
     * @param financialReportChartCode The financialReportChartCode to set.
     */
    public void setFinancialReportChartCode(String financialReportChartCode) {
        this.financialReportChartCode = financialReportChartCode;
    }

    /**
     * Gets the finReportOrganizationCode attribute.
     *
     * @return Returns the finReportOrganizationCode.
     */
    public String getFinReportOrganizationCode() {
        return finReportOrganizationCode;
    }

    /**
     * Sets the finReportOrganizationCode attribute value.
     *
     * @param finReportOrganizationCode The finReportOrganizationCode to set.
     */
    public void setFinReportOrganizationCode(String finReportOrganizationCode) {
        this.finReportOrganizationCode = finReportOrganizationCode;
    }

    /**
     * Gets the financialReportingCode attribute.
     *
     * @return Returns the financialReportingCode.
     */
    public String getFinancialReportingCode() {
        return financialReportingCode;
    }

    /**
     * Sets the financialReportingCode attribute value.
     *
     * @param financialReportingCode The financialReportingCode to set.
     */
    public void setFinancialReportingCode(String financialReportingCode) {
        this.financialReportingCode = financialReportingCode;
    }

    /**
     * Gets the account attribute.
     *
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     *
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the reportingCode attribute.
     *
     * @return Returns the reportingCode.
     */
    public ReportingCode getReportingCode() {
        return reportingCode;
    }

    /**
     * Sets the reportingCode attribute value.
     *
     * @param reportingCode The reportingCode to set.
     */
    public void setReportingCode(ReportingCode reportingCode) {
        this.reportingCode = reportingCode;
    }


    /**
     * Gets the finacialReportOrg attribute.
     *
     * @return Returns the finacialReportOrg.
     */
    public Organization getFinacialReportOrg() {
        return finacialReportOrg;
    }

    /**
     * Sets the finacialReportOrg attribute value.
     *
     * @param finacialReportOrg The finacialReportOrg to set.
     */
    public void setFinacialReportOrg(Organization finacialReportOrg) {
        this.finacialReportOrg = finacialReportOrg;
    }

    /**
     * Gets the subAccountTypeCode attribute.
     *
     * @return Returns the subAccountTypeCode.
     */
    public String getSubAccountTypeCode() {
        if (StringUtils.isBlank(subAccountTypeCode)) {
            subAccountTypeCode = getDefaultSubAccountTypeCode();
        }
        return subAccountTypeCode;
    }

    /**
     * Sets the subAccountTypeCode attribute value.
     *
     * @param subAccountTypeCode The subAccountTypeCode to set.
     */
    public void setSubAccountTypeCode(String subAccountTypeCode) {
        this.subAccountTypeCode = subAccountTypeCode;
    }

    /**
     * Gets the chart attribute.
     *
     * @return Returns the chart.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute value.
     *
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }


    /**
     * Gets the financialReportChart attribute.
     *
     * @return Returns the financialReportChart.
     */
    public Chart getFinancialReportChart() {
        return financialReportChart;
    }

    /**
     * Sets the financialReportChart attribute value.
     *
     * @param financialReportChart The financialReportChart to set.
     */
    public void setFinancialReportChart(Chart financialReportChart) {
        this.financialReportChart = financialReportChart;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        return m;
    }
}
