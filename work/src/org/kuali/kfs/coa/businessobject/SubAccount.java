/*
 * Copyright 2005-2006 The Kuali Foundation
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

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class SubAccount extends PersistableBusinessObjectBase implements MutableInactivatable {

    private static final long serialVersionUID = 6853259976912014273L;

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "SubAccount";
    
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String subAccountName;
    private boolean active;
    private String financialReportChartCode;
    private String finReportOrganizationCode;
    private String financialReportingCode;

    private A21SubAccount a21SubAccount;
    private Account account;
    private ReportingCode reportingCode;
    private Chart chart;
    private Organization org;
    private Chart financialReportChart;


    // Several kinds of Dummy Attributes for dividing sections on Inquiry page
    private String financialReportingCodeSectionBlank;
    private String financialReportingCodeSection;
    private String cgCostSharingSectionBlank;
    private String cgCostSharingSection;
    private String cgICRSectionBlank;
    private String cgICRSection;

    /**
     * Default no-arg constructor.
     */
    public SubAccount() {
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
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
     * Sets the financialReporting attribute value.
     * 
     * @param reportingCode The financialReporting to set.
     * @deprecated
     */
    public void setReportingCode(ReportingCode reportingCode) {
        this.reportingCode = reportingCode;
    }

    /**
     * Gets the subAccountName attribute.
     * 
     * @return Returns the subAccountName
     */
    public String getSubAccountName() {
        return subAccountName;
    }

    /**
     * Sets the subAccountName attribute.
     * 
     * @param subAccountName The subAccountName to set.
     */
    public void setSubAccountName(String subAccountName) {
        this.subAccountName = subAccountName;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccount attribute.
     * 
     * @param subAccount The subAccount to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the financialReporting attribute.
     * 
     * @return Returns the financialReporting
     */
    public ReportingCode getReportingCode() {
        return reportingCode;
    }


    /**
     * @return Returns the financialReportChartCode.
     */
    public String getFinancialReportChartCode() {
        return financialReportChartCode;
    }

    /**
     * @param financialReportChartCode The financialReportChartCode to set.
     */
    public void setFinancialReportChartCode(String financialReportChartCode) {
        this.financialReportChartCode = financialReportChartCode;
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
     * @return Returns the finReportOrganizationCode.
     */
    public String getFinReportOrganizationCode() {
        return finReportOrganizationCode;
    }

    /**
     * @param finReportOrganizationCode The finReportOrganizationCode to set.
     */
    public void setFinReportOrganizationCode(String finReportOrganizationCode) {
        this.finReportOrganizationCode = finReportOrganizationCode;
    }

    /**
     * Gets the a21SubAccount attribute.
     * 
     * @return Returns the a21SubAccount.
     */
    public A21SubAccount getA21SubAccount() {
        return a21SubAccount;
    }

    /**
     * Sets the a21SubAccount attribute value.
     * 
     * @param subAccount The a21SubAccount to set.
     */
    public void setA21SubAccount(A21SubAccount subAccount) {
        a21SubAccount = subAccount;
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
     * @return Returns the financialReportChart.
     */
    public Chart getFinancialReportChart() {
        return financialReportChart;
    }

    /**
     * @param financialReportChart The financialReportChart to set.
     * @deprecated
     */
    public void setFinancialReportChart(Chart financialReportChart) {
        this.financialReportChart = financialReportChart;
    }

    /**
     * @return Returns the org.
     */
    public Organization getOrg() {
        return org;
    }

    /**
     * @param org The org to set.
     * @deprecated
     */
    public void setOrg(Organization org) {
        this.org = org;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartCode", this.chartOfAccountsCode);
        m.put("account", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        return m;
    }

    /**
     * Gets the cGCostSharingSectionBlank attribute.
     * 
     * @return Returns the cGCostSharingSectionBlank.
     */
    public String getCgCostSharingSectionBlank() {
        return cgCostSharingSectionBlank;
    }

    /**
     * Gets the cGICRSectionBlank attribute.
     * 
     * @return Returns the cGICRSectionBlank.
     */
    public String getCgICRSectionBlank() {
        return cgICRSectionBlank;
    }

    /**
     * Gets the financialReportingCodeSectionBlank attribute.
     * 
     * @return Returns the financialReportingCodeSectionBlank.
     */
    public String getFinancialReportingCodeSectionBlank() {
        return financialReportingCodeSectionBlank;
    }

    /**
     * Gets the cGCostSharingSection attribute.
     * 
     * @return Returns the cGCostSharingSection.
     */
    public String getCgCostSharingSection() {
        return cgCostSharingSection;
    }

    /**
     * Gets the cGICRSection attribute.
     * 
     * @return Returns the cGICRSection.
     */
    public String getCgICRSection() {
        return cgICRSection;
    }

    /**
     * Gets the financialReportingCodeSection attribute.
     * 
     * @return Returns the financialReportingCodeSection.
     */
    public String getFinancialReportingCodeSection() {
        return financialReportingCodeSection;
    }
}
