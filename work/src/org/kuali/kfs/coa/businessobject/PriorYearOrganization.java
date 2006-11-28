/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/businessobject/PriorYearOrganization.java,v $
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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.Country;

/**
 * 
 */
public class PriorYearOrganization extends BusinessObjectBase {

    private String chartOfAccountsCode;
    private String organizationCode;
    private String organizationManagerUniversalId;
    private String organizationName;
    private String responsibilityCenterCode;
    private String organizationPhysicalCampusCode;
    private String organizationTypeCode;
    private String organizationDefaultAccountNumber;
    private String organizationCityName;
    private String organizationStateCode;
    private String organizationZipCode;
    private Date organizationBeginDate;
    private Date organizationEndDate;
    private String reportsToChartOfAccountsCode;
    private String reportsToOrganizationCode;
    private boolean organizationActiveIndicator;
    private boolean organizationInFinancialProcessingIndicator;
    private String organizationPlantAccountNumber;
    private String campusPlantAccountNumber;
    private String organizationPlantChartCode;
    private String campusPlantChartCode;
    private String organizationCountryCode;
    private String organizationLine1Address;
    private String organizationLine2Address;

    private Chart chartOfAccounts;
    private Account organizationDefaultAccount;
    private Org organization;
    private Campus organizationPhysicalCampus;
    private Org reportsToOrganization;
    private Chart reportsToChartOfAccounts;
    private Account organizationPlantAccount;
    private Account campusPlantAccount;
    private Chart organizationPlantChart;
    private Chart campusPlantChart;
    private Country organizationCountry;

    /**
     * Default constructor.
     */
    public PriorYearOrganization() {

    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     * 
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     * 
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the organizationManagerUniversalId attribute.
     * 
     * @return Returns the organizationManagerUniversalId
     * 
     */
    public String getOrganizationManagerUniversalId() {
        return organizationManagerUniversalId;
    }

    /**
     * Sets the organizationManagerUniversalId attribute.
     * 
     * @param organizationManagerUniversalId The organizationManagerUniversalId to set.
     * 
     */
    public void setOrganizationManagerUniversalId(String organizationManagerUniversalId) {
        this.organizationManagerUniversalId = organizationManagerUniversalId;
    }


    /**
     * Gets the organizationName attribute.
     * 
     * @return Returns the organizationName
     * 
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the organizationName attribute.
     * 
     * @param organizationName The organizationName to set.
     * 
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }


    /**
     * Gets the responsibilityCenterCode attribute.
     * 
     * @return Returns the responsibilityCenterCode
     * 
     */
    public String getResponsibilityCenterCode() {
        return responsibilityCenterCode;
    }

    /**
     * Sets the responsibilityCenterCode attribute.
     * 
     * @param responsibilityCenterCode The responsibilityCenterCode to set.
     * 
     */
    public void setResponsibilityCenterCode(String responsibilityCenterCode) {
        this.responsibilityCenterCode = responsibilityCenterCode;
    }


    /**
     * Gets the organizationPhysicalCampusCode attribute.
     * 
     * @return Returns the organizationPhysicalCampusCode
     * 
     */
    public String getOrganizationPhysicalCampusCode() {
        return organizationPhysicalCampusCode;
    }

    /**
     * Sets the organizationPhysicalCampusCode attribute.
     * 
     * @param organizationPhysicalCampusCode The organizationPhysicalCampusCode to set.
     * 
     */
    public void setOrganizationPhysicalCampusCode(String organizationPhysicalCampusCode) {
        this.organizationPhysicalCampusCode = organizationPhysicalCampusCode;
    }


    /**
     * Gets the organizationTypeCode attribute.
     * 
     * @return Returns the organizationTypeCode
     * 
     */
    public String getOrganizationTypeCode() {
        return organizationTypeCode;
    }

    /**
     * Sets the organizationTypeCode attribute.
     * 
     * @param organizationTypeCode The organizationTypeCode to set.
     * 
     */
    public void setOrganizationTypeCode(String organizationTypeCode) {
        this.organizationTypeCode = organizationTypeCode;
    }


    /**
     * Gets the organizationDefaultAccountNumber attribute.
     * 
     * @return Returns the organizationDefaultAccountNumber
     * 
     */
    public String getOrganizationDefaultAccountNumber() {
        return organizationDefaultAccountNumber;
    }

    /**
     * Sets the organizationDefaultAccountNumber attribute.
     * 
     * @param organizationDefaultAccountNumber The organizationDefaultAccountNumber to set.
     * 
     */
    public void setOrganizationDefaultAccountNumber(String organizationDefaultAccountNumber) {
        this.organizationDefaultAccountNumber = organizationDefaultAccountNumber;
    }

    /**
     * Gets the organizationCityName attribute.
     * 
     * @return Returns the organizationCityName
     * 
     */
    public String getOrganizationCityName() {
        return organizationCityName;
    }

    /**
     * Sets the organizationCityName attribute.
     * 
     * @param organizationCityName The organizationCityName to set.
     * 
     */
    public void setOrganizationCityName(String organizationCityName) {
        this.organizationCityName = organizationCityName;
    }


    /**
     * Gets the organizationStateCode attribute.
     * 
     * @return Returns the organizationStateCode
     * 
     */
    public String getOrganizationStateCode() {
        return organizationStateCode;
    }

    /**
     * Sets the organizationStateCode attribute.
     * 
     * @param organizationStateCode The organizationStateCode to set.
     * 
     */
    public void setOrganizationStateCode(String organizationStateCode) {
        this.organizationStateCode = organizationStateCode;
    }


    /**
     * Gets the organizationZipCode attribute.
     * 
     * @return Returns the organizationZipCode
     * 
     */
    public String getOrganizationZipCode() {
        return organizationZipCode;
    }

    /**
     * Sets the organizationZipCode attribute.
     * 
     * @param organizationZipCode The organizationZipCode to set.
     * 
     */
    public void setOrganizationZipCode(String organizationZipCode) {
        this.organizationZipCode = organizationZipCode;
    }


    /**
     * Gets the organizationBeginDate attribute.
     * 
     * @return Returns the organizationBeginDate
     * 
     */
    public Date getOrganizationBeginDate() {
        return organizationBeginDate;
    }

    /**
     * Sets the organizationBeginDate attribute.
     * 
     * @param organizationBeginDate The organizationBeginDate to set.
     * 
     */
    public void setOrganizationBeginDate(Date organizationBeginDate) {
        this.organizationBeginDate = organizationBeginDate;
    }


    /**
     * Gets the organizationEndDate attribute.
     * 
     * @return Returns the organizationEndDate
     * 
     */
    public Date getOrganizationEndDate() {
        return organizationEndDate;
    }

    /**
     * Sets the organizationEndDate attribute.
     * 
     * @param organizationEndDate The organizationEndDate to set.
     * 
     */
    public void setOrganizationEndDate(Date organizationEndDate) {
        this.organizationEndDate = organizationEndDate;
    }


    /**
     * Gets the reportsToChartOfAccountsCode attribute.
     * 
     * @return Returns the reportsToChartOfAccountsCode
     * 
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * Sets the reportsToChartOfAccountsCode attribute.
     * 
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     * 
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }


    /**
     * Gets the reportsToOrganizationCode attribute.
     * 
     * @return Returns the reportsToOrganizationCode
     * 
     */
    public String getReportsToOrganizationCode() {
        return reportsToOrganizationCode;
    }

    /**
     * Sets the reportsToOrganizationCode attribute.
     * 
     * @param reportsToOrganizationCode The reportsToOrganizationCode to set.
     * 
     */
    public void setReportsToOrganizationCode(String reportsToOrganizationCode) {
        this.reportsToOrganizationCode = reportsToOrganizationCode;
    }


    /**
     * Gets the organizationActiveIndicator attribute.
     * 
     * @return Returns the organizationActiveIndicator
     * 
     */
    public boolean isOrganizationActiveIndicator() {
        return organizationActiveIndicator;
    }


    /**
     * Sets the organizationActiveIndicator attribute.
     * 
     * @param organizationActiveIndicator The organizationActiveIndicator to set.
     * 
     */
    public void setOrganizationActiveIndicator(boolean organizationActiveIndicator) {
        this.organizationActiveIndicator = organizationActiveIndicator;
    }


    /**
     * Gets the organizationInFinancialProcessingIndicator attribute.
     * 
     * @return Returns the organizationInFinancialProcessingIndicator
     * 
     */
    public boolean isOrganizationInFinancialProcessingIndicator() {
        return organizationInFinancialProcessingIndicator;
    }


    /**
     * Sets the organizationInFinancialProcessingIndicator attribute.
     * 
     * @param organizationInFinancialProcessingIndicator The organizationInFinancialProcessingIndicator to set.
     * 
     */
    public void setOrganizationInFinancialProcessingIndicator(boolean organizationInFinancialProcessingIndicator) {
        this.organizationInFinancialProcessingIndicator = organizationInFinancialProcessingIndicator;
    }


    /**
     * Gets the organizationPlantAccountNumber attribute.
     * 
     * @return Returns the organizationPlantAccountNumber
     * 
     */
    public String getOrganizationPlantAccountNumber() {
        return organizationPlantAccountNumber;
    }

    /**
     * Sets the organizationPlantAccountNumber attribute.
     * 
     * @param organizationPlantAccountNumber The organizationPlantAccountNumber to set.
     * 
     */
    public void setOrganizationPlantAccountNumber(String organizationPlantAccountNumber) {
        this.organizationPlantAccountNumber = organizationPlantAccountNumber;
    }


    /**
     * Gets the campusPlantAccountNumber attribute.
     * 
     * @return Returns the campusPlantAccountNumber
     * 
     */
    public String getCampusPlantAccountNumber() {
        return campusPlantAccountNumber;
    }

    /**
     * Sets the campusPlantAccountNumber attribute.
     * 
     * @param campusPlantAccountNumber The campusPlantAccountNumber to set.
     * 
     */
    public void setCampusPlantAccountNumber(String campusPlantAccountNumber) {
        this.campusPlantAccountNumber = campusPlantAccountNumber;
    }


    /**
     * Gets the organizationPlantChartCode attribute.
     * 
     * @return Returns the organizationPlantChartCode
     * 
     */
    public String getOrganizationPlantChartCode() {
        return organizationPlantChartCode;
    }

    /**
     * Sets the organizationPlantChartCode attribute.
     * 
     * @param organizationPlantChartCode The organizationPlantChartCode to set.
     * 
     */
    public void setOrganizationPlantChartCode(String organizationPlantChartCode) {
        this.organizationPlantChartCode = organizationPlantChartCode;
    }


    /**
     * Gets the campusPlantChartCode attribute.
     * 
     * @return Returns the campusPlantChartCode
     * 
     */
    public String getCampusPlantChartCode() {
        return campusPlantChartCode;
    }

    /**
     * Sets the campusPlantChartCode attribute.
     * 
     * @param campusPlantChartCode The campusPlantChartCode to set.
     * 
     */
    public void setCampusPlantChartCode(String campusPlantChartCode) {
        this.campusPlantChartCode = campusPlantChartCode;
    }

    /**
     * Gets the organizationCountryCode attribute.
     * 
     * @return Returns the organizationCountryCode.
     */
    public String getOrganizationCountryCode() {
        return organizationCountryCode;
    }

    /**
     * Sets the organizationCountryCode attribute value.
     * 
     * @param organizationCountryCode The organizationCountryCode to set.
     */
    public void setOrganizationCountryCode(String organizationCountryCode) {
        this.organizationCountryCode = organizationCountryCode;
    }

    /**
     * Gets the organizationLine1Address attribute.
     * 
     * @return Returns the organizationLine1Address.
     */
    public String getOrganizationLine1Address() {
        return organizationLine1Address;
    }

    /**
     * Sets the organizationLine1Address attribute value.
     * 
     * @param organizationLine1Address The organizationLine1Address to set.
     */
    public void setOrganizationLine1Address(String organizationLine1Address) {
        this.organizationLine1Address = organizationLine1Address;
    }

    /**
     * Gets the organizationLine2Address attribute.
     * 
     * @return Returns the organizationLine2Address.
     */
    public String getOrganizationLine2Address() {
        return organizationLine2Address;
    }

    /**
     * Sets the organizationLine2Address attribute value.
     * 
     * @param organizationLine2Address The organizationLine2Address to set.
     */
    public void setOrganizationLine2Address(String organizationLine2Address) {
        this.organizationLine2Address = organizationLine2Address;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organizationDefaultAccount attribute.
     * 
     * @return Returns the organizationDefaultAccount
     * 
     */
    public Account getOrganizationDefaultAccount() {
        return organizationDefaultAccount;
    }

    /**
     * Sets the organizationDefaultAccount attribute.
     * 
     * @param organizationDefaultAccount The organizationDefaultAccount to set.
     * @deprecated
     */
    public void setOrganizationDefaultAccount(Account organizationDefaultAccount) {
        this.organizationDefaultAccount = organizationDefaultAccount;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     * 
     */
    public Org getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * Gets the organizationPhysicalCampus attribute.
     * 
     * @return Returns the organizationPhysicalCampus
     * 
     */
    public Campus getOrganizationPhysicalCampus() {
        return organizationPhysicalCampus;
    }

    /**
     * Sets the organizationPhysicalCampus attribute.
     * 
     * @param organizationPhysicalCampus The organizationPhysicalCampus to set.
     * @deprecated
     */
    public void setOrganizationPhysicalCampus(Campus organizationPhysicalCampus) {
        this.organizationPhysicalCampus = organizationPhysicalCampus;
    }

    /**
     * Gets the reportsToOrganization attribute.
     * 
     * @return Returns the reportsToOrganization
     * 
     */
    public Org getReportsToOrganization() {
        return reportsToOrganization;
    }

    /**
     * Sets the reportsToOrganization attribute.
     * 
     * @param reportsToOrganization The reportsToOrganization to set.
     * @deprecated
     */
    public void setReportsToOrganization(Org reportsToOrganization) {
        this.reportsToOrganization = reportsToOrganization;
    }

    /**
     * Gets the reportsToChartOfAccounts attribute.
     * 
     * @return Returns the reportsToChartOfAccounts
     * 
     */
    public Chart getReportsToChartOfAccounts() {
        return reportsToChartOfAccounts;
    }

    /**
     * Sets the reportsToChartOfAccounts attribute.
     * 
     * @param reportsToChartOfAccounts The reportsToChartOfAccounts to set.
     * @deprecated
     */
    public void setReportsToChartOfAccounts(Chart reportsToChartOfAccounts) {
        this.reportsToChartOfAccounts = reportsToChartOfAccounts;
    }

    /**
     * Gets the organizationPlantAccount attribute.
     * 
     * @return Returns the organizationPlantAccount
     * 
     */
    public Account getOrganizationPlantAccount() {
        return organizationPlantAccount;
    }

    /**
     * Sets the organizationPlantAccount attribute.
     * 
     * @param organizationPlantAccount The organizationPlantAccount to set.
     * @deprecated
     */
    public void setOrganizationPlantAccount(Account organizationPlantAccount) {
        this.organizationPlantAccount = organizationPlantAccount;
    }

    /**
     * Gets the campusPlantAccount attribute.
     * 
     * @return Returns the campusPlantAccount
     * 
     */
    public Account getCampusPlantAccount() {
        return campusPlantAccount;
    }

    /**
     * Sets the campusPlantAccount attribute.
     * 
     * @param campusPlantAccount The campusPlantAccount to set.
     * @deprecated
     */
    public void setCampusPlantAccount(Account campusPlantAccount) {
        this.campusPlantAccount = campusPlantAccount;
    }

    /**
     * Gets the organizationPlantChart attribute.
     * 
     * @return Returns the organizationPlantChart
     * 
     */
    public Chart getOrganizationPlantChart() {
        return organizationPlantChart;
    }

    /**
     * Sets the organizationPlantChart attribute.
     * 
     * @param organizationPlantChart The organizationPlantChart to set.
     * @deprecated
     */
    public void setOrganizationPlantChart(Chart organizationPlantChart) {
        this.organizationPlantChart = organizationPlantChart;
    }

    /**
     * Gets the campusPlantChart attribute.
     * 
     * @return Returns the campusPlantChart
     * 
     */
    public Chart getCampusPlantChart() {
        return campusPlantChart;
    }

    /**
     * Sets the campusPlantChart attribute.
     * 
     * @param campusPlantChart The campusPlantChart to set.
     * @deprecated
     */
    public void setCampusPlantChart(Chart campusPlantChart) {
        this.campusPlantChart = campusPlantChart;
    }

    /**
     * Gets the organizationCountry attribute.
     * 
     * @return Returns the organizationCountry.
     */
    public Country getOrganizationCountry() {
        return organizationCountry;
    }

    /**
     * Sets the organizationCountry attribute value.
     * 
     * @param organizationCountry The organizationCountry to set.
     * @deprecated
     */
    public void setOrganizationCountry(Country organizationCountry) {
        this.organizationCountry = organizationCountry;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        return m;
    }
}
