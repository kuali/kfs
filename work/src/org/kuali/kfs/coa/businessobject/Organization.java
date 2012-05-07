/*
 * Copyright 2005 The Kuali Foundation
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

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;

/**
 *
 */
public class Organization extends PersistableBusinessObjectBase implements MutableInactivatable {
    private static final Logger LOG = Logger.getLogger(Organization.class);

    private static final long serialVersionUID = 121873645110037203L;

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "Organization";

    protected String organizationCode;
    protected String organizationName;
    protected String organizationCityName;
    protected String organizationStateCode;
    protected String organizationZipCode;
    protected Date organizationBeginDate;
    protected Date organizationEndDate;
    protected boolean organizationInFinancialProcessingIndicator = false;
    protected String organizationManagerUniversalId;
    protected String responsibilityCenterCode;
    protected String organizationPhysicalCampusCode;
    protected String organizationTypeCode;
    protected String reportsToChartOfAccountsCode;
    protected String reportsToOrganizationCode;
    protected String organizationPlantAccountNumber;
    protected String campusPlantAccountNumber;
    protected String organizationPlantChartCode;
    protected String campusPlantChartCode;
    protected String organizationCountryCode;
    protected String organizationLine1Address;
    protected String organizationLine2Address;

    protected Chart chartOfAccounts;
    protected Organization hrisOrganization;
    protected Account organizationDefaultAccount;
    protected Person organizationManagerUniversal;
    protected ResponsibilityCenter responsibilityCenter;
    protected CampusEbo organizationPhysicalCampus;
    protected OrganizationType organizationType;
    protected Organization reportsToOrganization;
    protected Chart reportsToChartOfAccounts;
    protected Account organizationPlantAccount;
    protected Account campusPlantAccount;
    protected Chart organizationPlantChart;
    protected Chart campusPlantChart;
    protected PostalCodeEbo postalZip;
    protected CountryEbo organizationCountry;

    // HRMS Org fields
    protected OrganizationExtension organizationExtension;
    protected String editHrmsUnitSectionBlank;
    protected String editHrmsUnitSection;

    // fields for mixed anonymous keys
    protected String organizationDefaultAccountNumber;
    protected String chartOfAccountsCode;

    // Several kinds of Dummy Attributes for dividing sections on Inquiry page
    protected String editPlantAccountsSectionBlank;
    protected String editPlantAccountsSection;

    protected boolean active = true;

    /**
     * Gets the organizationCode attribute.
     *
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     *
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the organizationName attribute.
     *
     * @return Returns the organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the organizationName attribute.
     *
     * @param organizationName The organizationName to set.
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * Gets the organizationCityName attribute.
     *
     * @return Returns the organizationCityName
     */
    public String getOrganizationCityName() {
        return organizationCityName;
    }

    /**
     * Sets the organizationCityName attribute.
     *
     * @param organizationCityName The organizationCityName to set.
     */
    public void setOrganizationCityName(String organizationCityName) {
        this.organizationCityName = organizationCityName;
    }

    /**
     * Gets the organizationStateCode attribute.
     *
     * @return Returns the organizationStateCode
     */
    public String getOrganizationStateCode() {
        return organizationStateCode;
    }

    /**
     * Sets the organizationStateCode attribute.
     *
     * @param organizationStateCode The organizationStateCode to set.
     */
    public void setOrganizationStateCode(String organizationStateCode) {
        this.organizationStateCode = organizationStateCode;
    }

    /**
     * Gets the organizationZipCode attribute.
     *
     * @return Returns the organizationZipCode
     */
    public String getOrganizationZipCode() {
        return organizationZipCode;
    }

    /**
     * Sets the organizationZipCode attribute.
     *
     * @param organizationZipCode The organizationZipCode to set.
     */
    public void setOrganizationZipCode(String organizationZipCode) {
        this.organizationZipCode = organizationZipCode;
    }

    /**
     * Gets the organizationBeginDate attribute.
     *
     * @return Returns the organizationBeginDate
     */
    public Date getOrganizationBeginDate() {
        return organizationBeginDate;
    }

    /**
     * Sets the organizationBeginDate attribute.
     *
     * @param organizationBeginDate The organizationBeginDate to set.
     */
    public void setOrganizationBeginDate(Date organizationBeginDate) {
        this.organizationBeginDate = organizationBeginDate;
    }

    /**
     * Gets the organizationEndDate attribute.
     *
     * @return Returns the organizationEndDate
     */
    public Date getOrganizationEndDate() {
        return organizationEndDate;
    }

    /**
     * Sets the organizationEndDate attribute.
     *
     * @param organizationEndDate The organizationEndDate to set.
     */
    public void setOrganizationEndDate(Date organizationEndDate) {
        this.organizationEndDate = organizationEndDate;
    }

    /**
     * Gets the organizationInFinancialProcessingIndicator attribute.
     *
     * @return Returns the organizationInFinancialProcessingIndicator
     */
    public boolean isOrganizationInFinancialProcessingIndicator() {
        return organizationInFinancialProcessingIndicator;
    }

    /**
     * Sets the organizationInFinancialProcessingIndicator attribute.
     *
     * @param organizationInFinancialProcessingIndicator The organizationInFinancialProcessingIndicator to set.
     */
    public void setOrganizationInFinancialProcessingIndicator(boolean organizationInFinancialProcessingIndicator) {
        this.organizationInFinancialProcessingIndicator = organizationInFinancialProcessingIndicator;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
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
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organizationDefaultAccount attribute.
     *
     * @return Returns the organizationDefaultAccount
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
    @Deprecated
    public void setOrganizationDefaultAccount(Account organizationDefaultAccount) {
        this.organizationDefaultAccount = organizationDefaultAccount;
    }

    public Person getOrganizationManagerUniversal() {
        organizationManagerUniversal = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(organizationManagerUniversalId, organizationManagerUniversal);
        return organizationManagerUniversal;
    }

    /**
     * Sets the organizationManagerUniversal attribute.
     *
     * @param organizationManagerUniversal The organizationManagerUniversal to set.
     * @deprecated
     */
    @Deprecated
    public void setOrganizationManagerUniversal(Person organizationManagerUniversal) {
        this.organizationManagerUniversal = organizationManagerUniversal;
    }

    /**
     * Gets the responsibilityCenter attribute.
     *
     * @return Returns the responsibilityCenter
     */
    public ResponsibilityCenter getResponsibilityCenter() {
        return responsibilityCenter;
    }

    /**
     * Sets the responsibilityCenter attribute.
     *
     * @param responsibilityCenter The responsibilityCenter to set.
     * @deprecated
     */
    @Deprecated
    public void setResponsibilityCenter(ResponsibilityCenter responsibilityCenter) {
        this.responsibilityCenter = responsibilityCenter;
    }

    /**
     * Gets the organizationPhysicalCampus attribute.
     *
     * @return Returns the organizationPhysicalCampus
     */
    public CampusEbo getOrganizationPhysicalCampus() {
        if ( StringUtils.isBlank(organizationPhysicalCampusCode) ) {
            organizationPhysicalCampus = null;
        } else {
            if ( organizationPhysicalCampus == null || !StringUtils.equals( organizationPhysicalCampus.getCode(),organizationPhysicalCampusCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, organizationPhysicalCampusCode);
                    organizationPhysicalCampus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return organizationPhysicalCampus;
    }

    /**
     * Sets the organizationPhysicalCampus attribute.
     *
     * @param organizationPhysicalCampus The organizationPhysicalCampus to set.
     * @deprecated
     */
    @Deprecated
    public void setOrganizationPhysicalCampus(CampusEbo organizationPhysicalCampus) {
        this.organizationPhysicalCampus = organizationPhysicalCampus;
    }

    /**
     * Gets the organizationType attribute.
     *
     * @return Returns the organizationType
     */
    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    /**
     * Sets the organizationType attribute.
     *
     * @param organizationType The organizationType to set.
     * @deprecated
     */
    @Deprecated
    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    /**
     * Gets the reportsToOrganization attribute.
     *
     * @return Returns the reportsToOrganization
     */
    public Organization getReportsToOrganization() {
        return reportsToOrganization;
    }

    /**
     * Sets the reportsToOrganization attribute.
     *
     * @param reportsToOrganization The reportsToOrganization to set.
     * @deprecated
     */
    @Deprecated
    public void setReportsToOrganization(Organization reportsToOrganization) {
        this.reportsToOrganization = reportsToOrganization;
    }

    /**
     * Gets the reportsToChartOfAccounts attribute.
     *
     * @return Returns the reportsToChartOfAccounts
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
    @Deprecated
    public void setReportsToChartOfAccounts(Chart reportsToChartOfAccounts) {
        this.reportsToChartOfAccounts = reportsToChartOfAccounts;
    }

    /**
     * Gets the organizationPlantAccount attribute.
     *
     * @return Returns the organizationPlantAccount
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
    @Deprecated
    public void setOrganizationPlantAccount(Account organizationPlantAccount) {
        this.organizationPlantAccount = organizationPlantAccount;
    }

    /**
     * Gets the campusPlantAccount attribute.
     *
     * @return Returns the campusPlantAccount
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
    @Deprecated
    public void setCampusPlantAccount(Account campusPlantAccount) {
        this.campusPlantAccount = campusPlantAccount;
    }

    /**
     * Gets the organizationPlantChart attribute.
     *
     * @return Returns the organizationPlantChart
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
    @Deprecated
    public void setOrganizationPlantChart(Chart organizationPlantChart) {
        this.organizationPlantChart = organizationPlantChart;
    }

    /**
     * Gets the campusPlantChart attribute.
     *
     * @return Returns the campusPlantChart
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
    @Deprecated
    public void setCampusPlantChart(Chart campusPlantChart) {
        this.campusPlantChart = campusPlantChart;
    }

    /**
     * Gets the organizationCountry attribute.
     *
     * @return Returns the organizationCountry.
     */
    public CountryEbo getOrganizationCountry() {
        if ( StringUtils.isBlank(organizationCountryCode) ) {
            organizationCountry = null;
        } else {
            if ( organizationCountry == null || !StringUtils.equals( organizationCountry.getCode(),organizationCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, organizationCountryCode);
                    organizationCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return organizationCountry;
    }

    /**
     * Sets the organizationCountry attribute value.
     *
     * @param organizationCountry The organizationCountry to set.
     * @deprecated
     */
    @Deprecated
    public void setOrganizationCountry(CountryEbo organizationCountry) {
        this.organizationCountry = organizationCountry;
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
     * Gets the organizationDefaultAccountNumber attribute.
     *
     * @return Returns the organizationDefaultAccountNumber.
     */
    public String getOrganizationDefaultAccountNumber() {
        return organizationDefaultAccountNumber;
    }

    /**
     * Sets the organizationDefaultAccountNumber attribute value.
     *
     * @param organizationDefaultAccountNumber The organizationDefaultAccountNumber to set.
     */
    public void setOrganizationDefaultAccountNumber(String organizationDefaultAccountNumber) {
        this.organizationDefaultAccountNumber = organizationDefaultAccountNumber;
    }

    /**
     * @return Returns the campusPlantAccountNumber.
     */
    public String getCampusPlantAccountNumber() {
        return campusPlantAccountNumber;
    }

    /**
     * @param campusPlantAccountNumber The campusPlantAccountNumber to set.
     */
    public void setCampusPlantAccountNumber(String campusPlantAccountNumber) {
        this.campusPlantAccountNumber = campusPlantAccountNumber;
    }

    /**
     * @return Returns the campusPlantChartCode.
     */
    public String getCampusPlantChartCode() {
        return campusPlantChartCode;
    }

    /**
     * @param campusPlantChartCode The campusPlantChartCode to set.
     */
    public void setCampusPlantChartCode(String campusPlantChartCode) {
        this.campusPlantChartCode = campusPlantChartCode;
    }

    /**
     * @return Returns the organizationManagerUniversalId.
     */
    public String getOrganizationManagerUniversalId() {
        return organizationManagerUniversalId;
    }

    /**
     * @param organizationManagerUniversalId The organizationManagerUniversalId to set.
     */
    public void setOrganizationManagerUniversalId(String organizationManagerUniversalId) {
        this.organizationManagerUniversalId = organizationManagerUniversalId;
    }

    /**
     * @return Returns the organizationPhysicalCampusCode.
     */
    public String getOrganizationPhysicalCampusCode() {
        return organizationPhysicalCampusCode;
    }

    /**
     * @param organizationPhysicalCampusCode The organizationPhysicalCampusCode to set.
     */
    public void setOrganizationPhysicalCampusCode(String organizationPhysicalCampusCode) {
        this.organizationPhysicalCampusCode = organizationPhysicalCampusCode;
    }

    /**
     * @return Returns the organizationPlantAccountNumber.
     */
    public String getOrganizationPlantAccountNumber() {
        return organizationPlantAccountNumber;
    }

    /**
     * @param organizationPlantAccountNumber The organizationPlantAccountNumber to set.
     */
    public void setOrganizationPlantAccountNumber(String organizationPlantAccountNumber) {
        this.organizationPlantAccountNumber = organizationPlantAccountNumber;
    }

    /**
     * @return Returns the organizationPlantChartCode.
     */
    public String getOrganizationPlantChartCode() {
        return organizationPlantChartCode;
    }

    /**
     * @param organizationPlantChartCode The organizationPlantChartCode to set.
     */
    public void setOrganizationPlantChartCode(String organizationPlantChartCode) {
        this.organizationPlantChartCode = organizationPlantChartCode;
    }

    /**
     * @return Returns the organizationTypeCode.
     */
    public String getOrganizationTypeCode() {
        return organizationTypeCode;
    }

    /**
     * @param organizationTypeCode The organizationTypeCode to set.
     */
    public void setOrganizationTypeCode(String organizationTypeCode) {
        this.organizationTypeCode = organizationTypeCode;
    }

    /**
     * @return Returns the reportsToChartOfAccountsCode.
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }

    /**
     * @return Returns the reportsToOrganizationCode.
     */
    public String getReportsToOrganizationCode() {
        return reportsToOrganizationCode;
    }

    /**
     * @param reportsToOrganizationCode The reportsToOrganizationCode to set.
     */
    public void setReportsToOrganizationCode(String reportsToOrganizationCode) {
        this.reportsToOrganizationCode = reportsToOrganizationCode;
    }

    /**
     * @return Returns the responsibilityCenterCode.
     */
    public String getResponsibilityCenterCode() {
        return responsibilityCenterCode;
    }

    /**
     * @param responsibilityCenterCode The responsibilityCenterCode to set.
     */
    public void setResponsibilityCenterCode(String responsibilityCenterCode) {
        this.responsibilityCenterCode = responsibilityCenterCode;
    }

    /**
     * Gets the postalZip attribute.
     *
     * @return Returns the postalZip.
     */
    public PostalCodeEbo getPostalZip() {
        if ( StringUtils.isBlank(organizationZipCode) || StringUtils.isBlank(organizationCountryCode) ) {
            postalZip = null;
        } else {
            if ( postalZip == null || !StringUtils.equals( postalZip.getCode(),organizationZipCode) || !StringUtils.equals(postalZip.getCountryCode(), organizationCountryCode ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, organizationCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, organizationZipCode);
                    postalZip = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return postalZip;
    }

    /**
     * Sets the postalZip attribute value.
     *
     * @param postalZip The postalZip to set.
     */
    public void setPostalZip(PostalCodeEbo postalZip) {
        this.postalZip = postalZip;
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
     * Gets the editPlantAccountsSection attribute.
     *
     * @return Returns the editPlantAccountsSection.
     */
    public String getEditPlantAccountsSection() {
        return editPlantAccountsSection;
    }

    /**
     * Gets the editPlantAccountsSectionBlank attribute.
     *
     * @return Returns the editPlantAccountsSectionBlank.
     */
    public String getEditPlantAccountsSectionBlank() {
        return editPlantAccountsSectionBlank;
    }

    /**
     * Gets the editHrmsUnitSection attribute.
     *
     * @return Returns the editHrmsUnitSection.
     */
    public final String getEditHrmsUnitSection() {
        return editHrmsUnitSection;
    }

    /**
     * Sets the editHrmsUnitSection attribute value.
     *
     * @param editHrmsUnitSection The editHrmsUnitSection to set.
     */
    public final void setEditHrmsUnitSection(String editHrmsUnitSection) {
        this.editHrmsUnitSection = editHrmsUnitSection;
    }

    /**
     * Gets the editHrmsUnitSectionBlank attribute.
     *
     * @return Returns the editHrmsUnitSectionBlank.
     */
    public final String getEditHrmsUnitSectionBlank() {
        return editHrmsUnitSectionBlank;
    }

    /**
     * Sets the editHrmsUnitSectionBlank attribute value.
     *
     * @param editHrmsUnitSectionBlank The editHrmsUnitSectionBlank to set.
     */
    public final void setEditHrmsUnitSectionBlank(String editHrmsUnitSectionBlank) {
        this.editHrmsUnitSectionBlank = editHrmsUnitSectionBlank;
    }

    /**
     * Gets the organizationExtension attribute.
     *
     * @return Returns the organizationExtension.
     */
    public final OrganizationExtension getOrganizationExtension() {
        return organizationExtension;
    }

    /**
     * Sets the organizationExtension attribute value.
     *
     * @param organizationExtension The organizationExtension to set.
     */
    public final void setOrganizationExtension(OrganizationExtension organizationExtension) {
        this.organizationExtension = organizationExtension;
    }

    public String getOrganizationHierarchy() {
        OrganizationService organizationService = SpringContext.getBean(OrganizationService.class);
        StringBuffer result = new StringBuffer();
        Set<Organization> seen = new HashSet<Organization>();

        Organization org = this;

        while (org != null && org.getReportsToOrganizationCode() != null && !seen.contains(org)) {
            String rChart = org.getReportsToChartOfAccountsCode();
            String rOrg = org.getReportsToOrganizationCode();

            seen.add(org);
            org = organizationService.getByPrimaryIdWithCaching(rChart, rOrg);

            result.append(rChart).append("/").append(rOrg).append(" ");
            result.append(((org == null) ? "" : org.getOrganizationName()));
            if (org != null && org.getReportsToOrganizationCode() != null && !seen.contains(org)) {
                result.append(" ==> ");
            }
            result.append("\n");
        }

        return result.toString();
    }

    public String getOrganizationReviewHierarchy() {

        Properties params = new Properties();
        params.put("methodToCall", "start");
        params.put("docFormKey", "");
        params.put("lookupableImplServiceName", "RuleBaseValuesLookupableImplService");
        params.put("fin_coa_cd", this.chartOfAccountsCode);
        params.put("org_cd", this.organizationCode);
        params.put("conversionFields", "");
        params.put("returnLocation", "");
        params.put("active_ind", "true");
        params.put("ruleTemplateName", "KualiOrgReviewTemplate");

        return UrlFactory.parameterizeUrl(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/Lookup.do", params);
    }

    /**
     * Gets the campus code for Endowment Report
     *
     * @return
     */
    public String getOrganizationPhysicalCampusCodeForReport() {
        return organizationPhysicalCampusCode;
    }

    /**
     * Implementing equals so Org will behave reasonably in a hashed datastructure.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {

            if (this == obj) {
                return true;
            }

            if (this.getClass().isAssignableFrom(obj.getClass())) {

                Organization other = (Organization) obj;

                if (StringUtils.equals(this.getChartOfAccountsCode(), other.getChartOfAccountsCode())) {
                    if (StringUtils.equals(this.getOrganizationCode(), other.getOrganizationCode())) {
                        equal = true;
                    }
                }
            }
        }

        return equal;
    }

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        String theString = getOrganizationCode() + "-" + getOrganizationName();
        return theString;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        String hashString = getChartOfAccountsCode() + "|" + getOrganizationCode();
        return hashString.hashCode();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the organizationName attribute for report
     *
     * @return Returns the organizationName
     */
    public String getOrganizationCodeForReport() {
        return organizationCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#refreshReferenceObject(java.lang.String)
     */
    @Override
    public void refreshReferenceObject(String referenceObjectName) {
        if (referenceObjectName.equals("organizationManagerUniversal")) {
            getOrganizationManagerUniversal();
        }
        else {
            super.refreshReferenceObject(referenceObjectName);
        }
    }
}
