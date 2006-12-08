/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/businessobject/OrganizationExtension.java,v $
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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.SpringServiceLocator;

/**
 * 
 */
public class OrganizationExtension extends BusinessObjectBase {

    private String chartOfAccountsCode;
    private String organizationCode;
    private String hrmsShortDescription;
    private String hrmsCompany;
    private String hrmsSetIdLocation;
    private String hrmsLocation;
    private String hrmsTaxLocationCode;
    private String hrmsPersonnelApproverUniversalId;
    private String hrmsManagerPositionNumber;
    private Integer hrmsBudgetYearEndDate;
    private String hrmsBudgetLevel;
    private String hrmsGeneralLedgerExpense;
    private String hrmsEqualEmploymentOpportunity4Function;
    private String hrmsAccidentInsurance;
    private String hrmsSocialInsuranceAccidentNumber;
    private String hrmsHazard;
    private String hrmsEstablishmentId;
    private String hrmsRiskCode;
    private String hrmsFullTimeEmploymentEditIndicator;
    private String hrmsDepartmentTenureFlag;
    private String hrmsTimeAndLaborDistributionInformation;
    private String hrmsUseBudgetsIndicator;
    private String hrmsUseEncumbrancesIndicator;
    private String hrmsUseDistributionIndicator;
    private String hrmsBudgetDepartmentId;
    private String hrmsDistributionProrateOption;
    private String hrmsHealthProgramStatisticsDepartmentCode;
    private String hrmsHealthProgramStatisticsFaculty;
    private String hrmsAccountingOwner;
    private String hrmsCountryGroup;
    private String hrmsIuOrganizationMailDropCode;
    private String hrmsIuOrganizationAddress2;
    private String hrmsIuOrganizationAddress3;
    private String hrmsIuCampusCode;
    private String hrmsIuCampusBuilding;
    private String hrmsIuCampusRoom;
    private boolean hrmsIuPositionAllowedFlag;
    private boolean hrmsIuTenureAllowedFlag;
    private boolean hrmsIuTitleAllowedFlag;
    private boolean hrmsIuOccupationalUnitAllowedFlag;
    private String fiscalApproverUniversalId;
    private Timestamp hrmsLastUpdateDate;

    private Chart chartOfAccounts;
    private Org organization;
    private UniversalUser hrmsPersonnelApproverUniversal;
    private Campus hrmsIuCampus;
    private UniversalUser fiscalApproverUniversal;

    /**
     * Default constructor.
     */
    public OrganizationExtension() {

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
     * Gets the hrmsShortDescription attribute.
     * 
     * @return Returns the hrmsShortDescription
     * 
     */
    public String getHrmsShortDescription() {
        return hrmsShortDescription;
    }

    /**
     * Sets the hrmsShortDescription attribute.
     * 
     * @param hrmsShortDescription The hrmsShortDescription to set.
     * 
     */
    public void setHrmsShortDescription(String hrmsShortDescription) {
        this.hrmsShortDescription = hrmsShortDescription;
    }


    /**
     * Gets the hrmsCompany attribute.
     * 
     * @return Returns the hrmsCompany
     * 
     */
    public String getHrmsCompany() {
        return hrmsCompany;
    }

    /**
     * Sets the hrmsCompany attribute.
     * 
     * @param hrmsCompany The hrmsCompany to set.
     * 
     */
    public void setHrmsCompany(String hrmsCompany) {
        this.hrmsCompany = hrmsCompany;
    }


    /**
     * Gets the hrmsSetIdLocation attribute.
     * 
     * @return Returns the hrmsSetIdLocation
     * 
     */
    public String getHrmsSetIdLocation() {
        return hrmsSetIdLocation;
    }

    /**
     * Sets the hrmsSetIdLocation attribute.
     * 
     * @param hrmsSetIdLocation The hrmsSetIdLocation to set.
     * 
     */
    public void setHrmsSetIdLocation(String hrmsSetIdLocation) {
        this.hrmsSetIdLocation = hrmsSetIdLocation;
    }


    /**
     * Gets the hrmsLocation attribute.
     * 
     * @return Returns the hrmsLocation
     * 
     */
    public String getHrmsLocation() {
        return hrmsLocation;
    }

    /**
     * Sets the hrmsLocation attribute.
     * 
     * @param hrmsLocation The hrmsLocation to set.
     * 
     */
    public void setHrmsLocation(String hrmsLocation) {
        this.hrmsLocation = hrmsLocation;
    }


    /**
     * Gets the hrmsTaxLocationCode attribute.
     * 
     * @return Returns the hrmsTaxLocationCode
     * 
     */
    public String getHrmsTaxLocationCode() {
        return hrmsTaxLocationCode;
    }

    /**
     * Sets the hrmsTaxLocationCode attribute.
     * 
     * @param hrmsTaxLocationCode The hrmsTaxLocationCode to set.
     * 
     */
    public void setHrmsTaxLocationCode(String hrmsTaxLocationCode) {
        this.hrmsTaxLocationCode = hrmsTaxLocationCode;
    }


    /**
     * Gets the hrmsPersonnelApproverUniversalId attribute.
     * 
     * @return Returns the hrmsPersonnelApproverUniversalId
     * 
     */
    public String getHrmsPersonnelApproverUniversalId() {
        return hrmsPersonnelApproverUniversalId;
    }

    /**
     * Sets the hrmsPersonnelApproverUniversalId attribute.
     * 
     * @param hrmsPersonnelApproverUniversalId The hrmsPersonnelApproverUniversalId to set.
     * 
     */
    public void setHrmsPersonnelApproverUniversalId(String hrmsPersonnelApproverUniversalId) {
        this.hrmsPersonnelApproverUniversalId = hrmsPersonnelApproverUniversalId;
    }


    /**
     * Gets the hrmsManagerPositionNumber attribute.
     * 
     * @return Returns the hrmsManagerPositionNumber
     * 
     */
    public String getHrmsManagerPositionNumber() {
        return hrmsManagerPositionNumber;
    }

    /**
     * Sets the hrmsManagerPositionNumber attribute.
     * 
     * @param hrmsManagerPositionNumber The hrmsManagerPositionNumber to set.
     * 
     */
    public void setHrmsManagerPositionNumber(String hrmsManagerPositionNumber) {
        this.hrmsManagerPositionNumber = hrmsManagerPositionNumber;
    }


    /**
     * Gets the hrmsBudgetYearEndDate attribute.
     * 
     * @return Returns the hrmsBudgetYearEndDate
     * 
     */
    public Integer getHrmsBudgetYearEndDate() {
        return hrmsBudgetYearEndDate;
    }

    /**
     * Sets the hrmsBudgetYearEndDate attribute.
     * 
     * @param hrmsBudgetYearEndDate The hrmsBudgetYearEndDate to set.
     * 
     */
    public void setHrmsBudgetYearEndDate(Integer hrmsBudgetYearEndDate) {
        this.hrmsBudgetYearEndDate = hrmsBudgetYearEndDate;
    }


    /**
     * Gets the hrmsBudgetLevel attribute.
     * 
     * @return Returns the hrmsBudgetLevel
     * 
     */
    public String getHrmsBudgetLevel() {
        return hrmsBudgetLevel;
    }

    /**
     * Sets the hrmsBudgetLevel attribute.
     * 
     * @param hrmsBudgetLevel The hrmsBudgetLevel to set.
     * 
     */
    public void setHrmsBudgetLevel(String hrmsBudgetLevel) {
        this.hrmsBudgetLevel = hrmsBudgetLevel;
    }


    /**
     * Gets the hrmsGeneralLedgerExpense attribute.
     * 
     * @return Returns the hrmsGeneralLedgerExpense
     * 
     */
    public String getHrmsGeneralLedgerExpense() {
        return hrmsGeneralLedgerExpense;
    }

    /**
     * Sets the hrmsGeneralLedgerExpense attribute.
     * 
     * @param hrmsGeneralLedgerExpense The hrmsGeneralLedgerExpense to set.
     * 
     */
    public void setHrmsGeneralLedgerExpense(String hrmsGeneralLedgerExpense) {
        this.hrmsGeneralLedgerExpense = hrmsGeneralLedgerExpense;
    }


    /**
     * Gets the hrmsEqualEmploymentOpportunity4Function attribute.
     * 
     * @return Returns the hrmsEqualEmploymentOpportunity4Function
     * 
     */
    public String getHrmsEqualEmploymentOpportunity4Function() {
        return hrmsEqualEmploymentOpportunity4Function;
    }

    /**
     * Sets the hrmsEqualEmploymentOpportunity4Function attribute.
     * 
     * @param hrmsEqualEmploymentOpportunity4Function The hrmsEqualEmploymentOpportunity4Function to set.
     * 
     */
    public void setHrmsEqualEmploymentOpportunity4Function(String hrmsEqualEmploymentOpportunity4Function) {
        this.hrmsEqualEmploymentOpportunity4Function = hrmsEqualEmploymentOpportunity4Function;
    }


    /**
     * Gets the hrmsAccidentInsurance attribute.
     * 
     * @return Returns the hrmsAccidentInsurance
     * 
     */
    public String getHrmsAccidentInsurance() {
        return hrmsAccidentInsurance;
    }

    /**
     * Sets the hrmsAccidentInsurance attribute.
     * 
     * @param hrmsAccidentInsurance The hrmsAccidentInsurance to set.
     * 
     */
    public void setHrmsAccidentInsurance(String hrmsAccidentInsurance) {
        this.hrmsAccidentInsurance = hrmsAccidentInsurance;
    }


    /**
     * Gets the hrmsSocialInsuranceAccidentNumber attribute.
     * 
     * @return Returns the hrmsSocialInsuranceAccidentNumber
     * 
     */
    public String getHrmsSocialInsuranceAccidentNumber() {
        return hrmsSocialInsuranceAccidentNumber;
    }

    /**
     * Sets the hrmsSocialInsuranceAccidentNumber attribute.
     * 
     * @param hrmsSocialInsuranceAccidentNumber The hrmsSocialInsuranceAccidentNumber to set.
     * 
     */
    public void setHrmsSocialInsuranceAccidentNumber(String hrmsSocialInsuranceAccidentNumber) {
        this.hrmsSocialInsuranceAccidentNumber = hrmsSocialInsuranceAccidentNumber;
    }


    /**
     * Gets the hrmsHazard attribute.
     * 
     * @return Returns the hrmsHazard
     * 
     */
    public String getHrmsHazard() {
        return hrmsHazard;
    }

    /**
     * Sets the hrmsHazard attribute.
     * 
     * @param hrmsHazard The hrmsHazard to set.
     * 
     */
    public void setHrmsHazard(String hrmsHazard) {
        this.hrmsHazard = hrmsHazard;
    }


    /**
     * Gets the hrmsEstablishmentId attribute.
     * 
     * @return Returns the hrmsEstablishmentId
     * 
     */
    public String getHrmsEstablishmentId() {
        return hrmsEstablishmentId;
    }

    /**
     * Sets the hrmsEstablishmentId attribute.
     * 
     * @param hrmsEstablishmentId The hrmsEstablishmentId to set.
     * 
     */
    public void setHrmsEstablishmentId(String hrmsEstablishmentId) {
        this.hrmsEstablishmentId = hrmsEstablishmentId;
    }


    /**
     * Gets the hrmsRiskCode attribute.
     * 
     * @return Returns the hrmsRiskCode
     * 
     */
    public String getHrmsRiskCode() {
        return hrmsRiskCode;
    }

    /**
     * Sets the hrmsRiskCode attribute.
     * 
     * @param hrmsRiskCode The hrmsRiskCode to set.
     * 
     */
    public void setHrmsRiskCode(String hrmsRiskCode) {
        this.hrmsRiskCode = hrmsRiskCode;
    }


    /**
     * Gets the hrmsFullTimeEmploymentEditIndicator attribute.
     * 
     * @return Returns the hrmsFullTimeEmploymentEditIndicator
     * 
     */
    public String getHrmsFullTimeEmploymentEditIndicator() {
        return hrmsFullTimeEmploymentEditIndicator;
    }

    /**
     * Sets the hrmsFullTimeEmploymentEditIndicator attribute.
     * 
     * @param hrmsFullTimeEmploymentEditIndicator The hrmsFullTimeEmploymentEditIndicator to set.
     * 
     */
    public void setHrmsFullTimeEmploymentEditIndicator(String hrmsFullTimeEmploymentEditIndicator) {
        this.hrmsFullTimeEmploymentEditIndicator = hrmsFullTimeEmploymentEditIndicator;
    }


    /**
     * Gets the hrmsDepartmentTenureFlag attribute.
     * 
     * @return Returns the hrmsDepartmentTenureFlag
     * 
     */
    public String getHrmsDepartmentTenureFlag() {
        return hrmsDepartmentTenureFlag;
    }

    /**
     * Sets the hrmsDepartmentTenureFlag attribute.
     * 
     * @param hrmsDepartmentTenureFlag The hrmsDepartmentTenureFlag to set.
     * 
     */
    public void setHrmsDepartmentTenureFlag(String hrmsDepartmentTenureFlag) {
        this.hrmsDepartmentTenureFlag = hrmsDepartmentTenureFlag;
    }


    /**
     * Gets the hrmsTimeAndLaborDistributionInformation attribute.
     * 
     * @return Returns the hrmsTimeAndLaborDistributionInformation
     * 
     */
    public String getHrmsTimeAndLaborDistributionInformation() {
        return hrmsTimeAndLaborDistributionInformation;
    }

    /**
     * Sets the hrmsTimeAndLaborDistributionInformation attribute.
     * 
     * @param hrmsTimeAndLaborDistributionInformation The hrmsTimeAndLaborDistributionInformation to set.
     * 
     */
    public void setHrmsTimeAndLaborDistributionInformation(String hrmsTimeAndLaborDistributionInformation) {
        this.hrmsTimeAndLaborDistributionInformation = hrmsTimeAndLaborDistributionInformation;
    }


    /**
     * Gets the hrmsUseBudgetsIndicator attribute.
     * 
     * @return Returns the hrmsUseBudgetsIndicator
     * 
     */
    public String getHrmsUseBudgetsIndicator() {
        return hrmsUseBudgetsIndicator;
    }

    /**
     * Sets the hrmsUseBudgetsIndicator attribute.
     * 
     * @param hrmsUseBudgetsIndicator The hrmsUseBudgetsIndicator to set.
     * 
     */
    public void setHrmsUseBudgetsIndicator(String hrmsUseBudgetsIndicator) {
        this.hrmsUseBudgetsIndicator = hrmsUseBudgetsIndicator;
    }


    /**
     * Gets the hrmsUseEncumbrancesIndicator attribute.
     * 
     * @return Returns the hrmsUseEncumbrancesIndicator
     * 
     */
    public String getHrmsUseEncumbrancesIndicator() {
        return hrmsUseEncumbrancesIndicator;
    }

    /**
     * Sets the hrmsUseEncumbrancesIndicator attribute.
     * 
     * @param hrmsUseEncumbrancesIndicator The hrmsUseEncumbrancesIndicator to set.
     * 
     */
    public void setHrmsUseEncumbrancesIndicator(String hrmsUseEncumbrancesIndicator) {
        this.hrmsUseEncumbrancesIndicator = hrmsUseEncumbrancesIndicator;
    }


    /**
     * Gets the hrmsUseDistributionIndicator attribute.
     * 
     * @return Returns the hrmsUseDistributionIndicator
     * 
     */
    public String getHrmsUseDistributionIndicator() {
        return hrmsUseDistributionIndicator;
    }

    /**
     * Sets the hrmsUseDistributionIndicator attribute.
     * 
     * @param hrmsUseDistributionIndicator The hrmsUseDistributionIndicator to set.
     * 
     */
    public void setHrmsUseDistributionIndicator(String hrmsUseDistributionIndicator) {
        this.hrmsUseDistributionIndicator = hrmsUseDistributionIndicator;
    }


    /**
     * Gets the hrmsBudgetDepartmentId attribute.
     * 
     * @return Returns the hrmsBudgetDepartmentId
     * 
     */
    public String getHrmsBudgetDepartmentId() {
        return hrmsBudgetDepartmentId;
    }

    /**
     * Sets the hrmsBudgetDepartmentId attribute.
     * 
     * @param hrmsBudgetDepartmentId The hrmsBudgetDepartmentId to set.
     * 
     */
    public void setHrmsBudgetDepartmentId(String hrmsBudgetDepartmentId) {
        this.hrmsBudgetDepartmentId = hrmsBudgetDepartmentId;
    }


    /**
     * Gets the hrmsDistributionProrateOption attribute.
     * 
     * @return Returns the hrmsDistributionProrateOption
     * 
     */
    public String getHrmsDistributionProrateOption() {
        return hrmsDistributionProrateOption;
    }

    /**
     * Sets the hrmsDistributionProrateOption attribute.
     * 
     * @param hrmsDistributionProrateOption The hrmsDistributionProrateOption to set.
     * 
     */
    public void setHrmsDistributionProrateOption(String hrmsDistributionProrateOption) {
        this.hrmsDistributionProrateOption = hrmsDistributionProrateOption;
    }


    /**
     * Gets the hrmsHealthProgramStatisticsDepartmentCode attribute.
     * 
     * @return Returns the hrmsHealthProgramStatisticsDepartmentCode
     * 
     */
    public String getHrmsHealthProgramStatisticsDepartmentCode() {
        return hrmsHealthProgramStatisticsDepartmentCode;
    }

    /**
     * Sets the hrmsHealthProgramStatisticsDepartmentCode attribute.
     * 
     * @param hrmsHealthProgramStatisticsDepartmentCode The hrmsHealthProgramStatisticsDepartmentCode to set.
     * 
     */
    public void setHrmsHealthProgramStatisticsDepartmentCode(String hrmsHealthProgramStatisticsDepartmentCode) {
        this.hrmsHealthProgramStatisticsDepartmentCode = hrmsHealthProgramStatisticsDepartmentCode;
    }


    /**
     * Gets the hrmsHealthProgramStatisticsFaculty attribute.
     * 
     * @return Returns the hrmsHealthProgramStatisticsFaculty
     * 
     */
    public String getHrmsHealthProgramStatisticsFaculty() {
        return hrmsHealthProgramStatisticsFaculty;
    }

    /**
     * Sets the hrmsHealthProgramStatisticsFaculty attribute.
     * 
     * @param hrmsHealthProgramStatisticsFaculty The hrmsHealthProgramStatisticsFaculty to set.
     * 
     */
    public void setHrmsHealthProgramStatisticsFaculty(String hrmsHealthProgramStatisticsFaculty) {
        this.hrmsHealthProgramStatisticsFaculty = hrmsHealthProgramStatisticsFaculty;
    }


    /**
     * Gets the hrmsAccountingOwner attribute.
     * 
     * @return Returns the hrmsAccountingOwner
     * 
     */
    public String getHrmsAccountingOwner() {
        return hrmsAccountingOwner;
    }

    /**
     * Sets the hrmsAccountingOwner attribute.
     * 
     * @param hrmsAccountingOwner The hrmsAccountingOwner to set.
     * 
     */
    public void setHrmsAccountingOwner(String hrmsAccountingOwner) {
        this.hrmsAccountingOwner = hrmsAccountingOwner;
    }


    /**
     * Gets the hrmsCountryGroup attribute.
     * 
     * @return Returns the hrmsCountryGroup
     * 
     */
    public String getHrmsCountryGroup() {
        return hrmsCountryGroup;
    }

    /**
     * Sets the hrmsCountryGroup attribute.
     * 
     * @param hrmsCountryGroup The hrmsCountryGroup to set.
     * 
     */
    public void setHrmsCountryGroup(String hrmsCountryGroup) {
        this.hrmsCountryGroup = hrmsCountryGroup;
    }


    /**
     * Gets the hrmsIuOrganizationMailDropCode attribute.
     * 
     * @return Returns the hrmsIuOrganizationMailDropCode
     * 
     */
    public String getHrmsIuOrganizationMailDropCode() {
        return hrmsIuOrganizationMailDropCode;
    }

    /**
     * Sets the hrmsIuOrganizationMailDropCode attribute.
     * 
     * @param hrmsIuOrganizationMailDropCode The hrmsIuOrganizationMailDropCode to set.
     * 
     */
    public void setHrmsIuOrganizationMailDropCode(String hrmsIuOrganizationMailDropCode) {
        this.hrmsIuOrganizationMailDropCode = hrmsIuOrganizationMailDropCode;
    }


    /**
     * Gets the hrmsIuOrganizationAddress2 attribute.
     * 
     * @return Returns the hrmsIuOrganizationAddress2
     * 
     */
    public String getHrmsIuOrganizationAddress2() {
        return hrmsIuOrganizationAddress2;
    }

    /**
     * Sets the hrmsIuOrganizationAddress2 attribute.
     * 
     * @param hrmsIuOrganizationAddress2 The hrmsIuOrganizationAddress2 to set.
     * 
     */
    public void setHrmsIuOrganizationAddress2(String hrmsIuOrganizationAddress2) {
        this.hrmsIuOrganizationAddress2 = hrmsIuOrganizationAddress2;
    }


    /**
     * Gets the hrmsIuOrganizationAddress3 attribute.
     * 
     * @return Returns the hrmsIuOrganizationAddress3
     * 
     */
    public String getHrmsIuOrganizationAddress3() {
        return hrmsIuOrganizationAddress3;
    }

    /**
     * Sets the hrmsIuOrganizationAddress3 attribute.
     * 
     * @param hrmsIuOrganizationAddress3 The hrmsIuOrganizationAddress3 to set.
     * 
     */
    public void setHrmsIuOrganizationAddress3(String hrmsIuOrganizationAddress3) {
        this.hrmsIuOrganizationAddress3 = hrmsIuOrganizationAddress3;
    }


    /**
     * Gets the hrmsIuCampusCode attribute.
     * 
     * @return Returns the hrmsIuCampusCode
     * 
     */
    public String getHrmsIuCampusCode() {
        return hrmsIuCampusCode;
    }

    /**
     * Sets the hrmsIuCampusCode attribute.
     * 
     * @param hrmsIuCampusCode The hrmsIuCampusCode to set.
     * 
     */
    public void setHrmsIuCampusCode(String hrmsIuCampusCode) {
        this.hrmsIuCampusCode = hrmsIuCampusCode;
    }


    /**
     * Gets the hrmsIuCampusBuilding attribute.
     * 
     * @return Returns the hrmsIuCampusBuilding
     * 
     */
    public String getHrmsIuCampusBuilding() {
        return hrmsIuCampusBuilding;
    }

    /**
     * Sets the hrmsIuCampusBuilding attribute.
     * 
     * @param hrmsIuCampusBuilding The hrmsIuCampusBuilding to set.
     * 
     */
    public void setHrmsIuCampusBuilding(String hrmsIuCampusBuilding) {
        this.hrmsIuCampusBuilding = hrmsIuCampusBuilding;
    }


    /**
     * Gets the hrmsIuCampusRoom attribute.
     * 
     * @return Returns the hrmsIuCampusRoom
     * 
     */
    public String getHrmsIuCampusRoom() {
        return hrmsIuCampusRoom;
    }

    /**
     * Sets the hrmsIuCampusRoom attribute.
     * 
     * @param hrmsIuCampusRoom The hrmsIuCampusRoom to set.
     * 
     */
    public void setHrmsIuCampusRoom(String hrmsIuCampusRoom) {
        this.hrmsIuCampusRoom = hrmsIuCampusRoom;
    }

    /**
     * Gets the hrmsIuOccupationalUnitAllowedFlag attribute.
     * 
     * @return Returns the hrmsIuOccupationalUnitAllowedFlag.
     */
    public final boolean isHrmsIuOccupationalUnitAllowedFlag() {
        return hrmsIuOccupationalUnitAllowedFlag;
    }

    /**
     * Sets the hrmsIuOccupationalUnitAllowedFlag attribute value.
     * 
     * @param hrmsIuOccupationalUnitAllowedFlag The hrmsIuOccupationalUnitAllowedFlag to set.
     */
    public final void setHrmsIuOccupationalUnitAllowedFlag(boolean hrmsIuOccupationalUnitAllowedFlag) {
        this.hrmsIuOccupationalUnitAllowedFlag = hrmsIuOccupationalUnitAllowedFlag;
    }

    /**
     * Gets the hrmsIuPositionAllowedFlag attribute.
     * 
     * @return Returns the hrmsIuPositionAllowedFlag.
     */
    public final boolean isHrmsIuPositionAllowedFlag() {
        return hrmsIuPositionAllowedFlag;
    }

    /**
     * Sets the hrmsIuPositionAllowedFlag attribute value.
     * 
     * @param hrmsIuPositionAllowedFlag The hrmsIuPositionAllowedFlag to set.
     */
    public final void setHrmsIuPositionAllowedFlag(boolean hrmsIuPositionAllowedFlag) {
        this.hrmsIuPositionAllowedFlag = hrmsIuPositionAllowedFlag;
    }

    /**
     * Gets the hrmsIuTenureAllowedFlag attribute.
     * 
     * @return Returns the hrmsIuTenureAllowedFlag.
     */
    public final boolean isHrmsIuTenureAllowedFlag() {
        return hrmsIuTenureAllowedFlag;
    }

    /**
     * Sets the hrmsIuTenureAllowedFlag attribute value.
     * 
     * @param hrmsIuTenureAllowedFlag The hrmsIuTenureAllowedFlag to set.
     */
    public final void setHrmsIuTenureAllowedFlag(boolean hrmsIuTenureAllowedFlag) {
        this.hrmsIuTenureAllowedFlag = hrmsIuTenureAllowedFlag;
    }

    /**
     * Gets the hrmsIuTitleAllowedFlag attribute.
     * 
     * @return Returns the hrmsIuTitleAllowedFlag.
     */
    public final boolean isHrmsIuTitleAllowedFlag() {
        return hrmsIuTitleAllowedFlag;
    }

    /**
     * Sets the hrmsIuTitleAllowedFlag attribute value.
     * 
     * @param hrmsIuTitleAllowedFlag The hrmsIuTitleAllowedFlag to set.
     */
    public final void setHrmsIuTitleAllowedFlag(boolean hrmsIuTitleAllowedFlag) {
        this.hrmsIuTitleAllowedFlag = hrmsIuTitleAllowedFlag;
    }

    /**
     * Gets the fiscalApproverUniversalId attribute.
     * 
     * @return Returns the fiscalApproverUniversalId
     * 
     */
    public String getFiscalApproverUniversalId() {
        return fiscalApproverUniversalId;
    }

    /**
     * Sets the fiscalApproverUniversalId attribute.
     * 
     * @param fiscalApproverUniversalId The fiscalApproverUniversalId to set.
     * 
     */
    public void setFiscalApproverUniversalId(String fiscalApproverUniversalId) {
        this.fiscalApproverUniversalId = fiscalApproverUniversalId;
    }


    /**
     * Gets the hrmsLastUpdateDate attribute.
     * 
     * @return Returns the hrmsLastUpdateDate
     * 
     */
    public Timestamp getHrmsLastUpdateDate() {
        return hrmsLastUpdateDate;
    }

    /**
     * Sets the hrmsLastUpdateDate attribute.
     * 
     * @param hrmsLastUpdateDate The hrmsLastUpdateDate to set.
     * 
     */
    public void setHrmsLastUpdateDate(Timestamp hrmsLastUpdateDate) {
        this.hrmsLastUpdateDate = hrmsLastUpdateDate;
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

    public UniversalUser getHrmsPersonnelApproverUniversal() {
        hrmsPersonnelApproverUniversal = SpringServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(hrmsPersonnelApproverUniversalId, hrmsPersonnelApproverUniversal);
        return hrmsPersonnelApproverUniversal;
    }

    /**
     * Sets the hrmsPersonnelApproverUniversal attribute.
     * 
     * @param hrmsPersonnelApproverUniversal The hrmsPersonnelApproverUniversal to set.
     * @deprecated
     */
    public void setHrmsPersonnelApproverUniversal(UniversalUser hrmsPersonnelApproverUniversal) {
        this.hrmsPersonnelApproverUniversal = hrmsPersonnelApproverUniversal;
    }

    /**
     * Gets the hrmsIuCampus attribute.
     * 
     * @return Returns the hrmsIuCampus
     * 
     */
    public Campus getHrmsIuCampus() {
        return hrmsIuCampus;
    }

    /**
     * Sets the hrmsIuCampus attribute.
     * 
     * @param hrmsIuCampus The hrmsIuCampus to set.
     * @deprecated
     */
    public void setHrmsIuCampus(Campus hrmsIuCampus) {
        this.hrmsIuCampus = hrmsIuCampus;
    }

    public UniversalUser getFiscalApproverUniversal() {
        fiscalApproverUniversal = SpringServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(fiscalApproverUniversalId, fiscalApproverUniversal);
        return fiscalApproverUniversal;
    }

    /**
     * Sets the fiscalApproverUniversal attribute.
     * 
     * @param fiscalApproverUniversal The fiscalApproverUniversal to set.
     * @deprecated
     */
    public void setFiscalApproverUniversal(UniversalUser fiscalApproverUniversal) {
        this.fiscalApproverUniversal = fiscalApproverUniversal;
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
