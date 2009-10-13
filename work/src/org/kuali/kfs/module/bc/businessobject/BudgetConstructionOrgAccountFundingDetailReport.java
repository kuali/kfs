/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject;

import java.math.BigDecimal;

/**
 * Budget Construction Organization Account Funding Detail Report Business Object.
 */
public class BudgetConstructionOrgAccountFundingDetailReport {

    // Header parts
    private String fiscalYear;
    private String orgChartOfAccountsCode;
    private String orgChartOfAccountDescription;
    private String chartOfAccountsCode;
    private String chartOfAccountDescription;
    private String organizationCode;
    private String organizationName;
    private String consHdr;
    private String fundGroupCode;
    private String fundGroupName;
    private String subFundGroupCode;
    private String subFundGroupDescription;
    private String baseFy;
    private String reqFy;
    private String objectCodes;
    private String accountName;
    private String subAccountName;
    private String accountNumberAndName; 
    private String subAccountNumberAndName;
    private String divider;
    
    // Groups
    private String accountNumber;
    private String subAccountNumber;
    private String emplid;
    
    // Body parts
    private String financialObjectCode;
    private String financialObjectCodeName;
    
    private String deleteBox;
    private String name;
    //from BudgetConstructionIntendedIncumbent
    private String iuClassificationLevel;
    //from PendingBudgetConstructionAppointmentFunding
    private String financialSubObjectCode;
    //from BudgetConstructionAdministrativePost
    private String administrativePost;
    //from BudgetConstructionPosition
    private String positionNumber;
    private String normalWorkMonthsAndiuPayMonths;
    private String positionSalaryPlanDefault;
    private String positionGradeDefault;
    private BigDecimal positionStandardHoursDefault;
    //from BudgetConstructionCalculatedSalaryFoundationTracker
    private BigDecimal csfTimePercent;
    private Integer csfAmount;
    private BigDecimal csfFullTimeEmploymentQuantity;
    //from PendingBudgetConstructionAppointmentFunding
    private Integer appointmentFundingMonth;
    private Integer appointmentRequestedAmount;
    private BigDecimal appointmentRequestedTimePercent;
    private BigDecimal appointmentRequestedFteQuantity;
    private String appointmentFundingDurationCode;
    private Integer appointmentRequestedCsfAmount;
    private BigDecimal appointmentRequestedCsfTimePercent;
    private BigDecimal appointmentRequestedCsfFteQuantity;
    private Integer appointmentTotalIntendedAmount;
    private BigDecimal appointmentTotalIntendedFteQuantity;
    private Integer amountChange;
    private BigDecimal percentChange;
    
    //not sure where it is from
    private BigDecimal positionFte;

    // Total parts
    
    private String totalObjectname;
    
    private Integer totalObjectPositionCsfAmount;
    private Integer totalObjectAppointmentRequestedAmount;
    private BigDecimal totalObjectPositionCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedFteQuantity;
    private Integer totalObjectAmountChange;
    private BigDecimal totalObjectPercentChange;
    
    private Integer totalAccountPositionCsfAmount;
    private Integer totalAccountAppointmentRequestedAmount;
    private BigDecimal totalAccountPositionCsfFteQuantity;
    private BigDecimal totalAccountAppointmentRequestedFteQuantity;
    private Integer totalAccountAmountChange;
    private BigDecimal totalAccountPercentChange;
        
    
    /**
     * Gets the baseFy
     * 
     * @return Returns the baseFy.
     */
    public String getBaseFy() {
        return baseFy;
    }

    /**
     * Sets the baseFy
     * 
     * @param baseFy The baseFy to set.
     */
    public void setBaseFy(String baseFy) {
        this.baseFy = baseFy;
    }

    /**
     * Gets the consHdr
     * 
     * @return Returns the consHdr.
     */
    public String getConsHdr() {
        return consHdr;
    }

    /**
     * Sets the consHdr
     * 
     * @param consHdr The consHdr to set.
     */
    public void setConsHdr(String consHdr) {
        this.consHdr = consHdr;
    }

    /**
     * Gets the fiscalYear
     * 
     * @return Returns the fiscalYear.
     */
    public String getFiscalYear() {
        return fiscalYear;
    }

    /**
     * Sets the fiscalYear
     * 
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * Gets the fundGroupCode
     * 
     * @return Returns the fundGroupCode.
     */
    public String getFundGroupCode() {
        return fundGroupCode;
    }

    /**
     * Sets the fundGroupCode
     * 
     * @param fundGroupCode The fundGroupCode to set.
     */
    public void setFundGroupCode(String fundGroupCode) {
        this.fundGroupCode = fundGroupCode;
    }

    /**
     * Gets the organizationCode
     * 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the organizationName
     * 
     * @return Returns the organizationName.
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the organizationName
     * 
     * @param organizationName The organizationName to set.
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * Gets the reqFy
     * 
     * @return Returns the reqFy.
     */
    public String getReqFy() {
        return reqFy;
    }

    /**
     * Sets the reqFy
     * 
     * @param reqFy The reqFy to set.
     */
    public void setReqFy(String reqFy) {
        this.reqFy = reqFy;
    }

    /**
     * Gets the subFundGroupCode
     * 
     * @return Returns the subFundGroupCode.
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * Sets the subFundGroupCode
     * 
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    /**
     * Gets the subFundGroupDescription
     * 
     * @return Returns the subFundGroupDescription.
     */
    public String getSubFundGroupDescription() {
        return subFundGroupDescription;
    }

    /**
     * Sets the subFundGroupDescription
     * 
     * @param subFundGroupDescription The subFundGroupDescription to set.
     */
    public void setSubFundGroupDescription(String subFundGroupDescription) {
        this.subFundGroupDescription = subFundGroupDescription;
    }

    /**
     * Gets the fundGroupName
     * 
     * @return Returns the fundGroupName.
     */
    public String getFundGroupName() {
        return fundGroupName;
    }

    /**
     * Sets the fundGroupName
     * 
     * @param fundGroupName The fundGroupName to set.
     */
    public void setFundGroupName(String fundGroupName) {
        this.fundGroupName = fundGroupName;
    }

    /**
     * Gets the chartOfAccountDescription
     * 
     * @return Returns the chartOfAccountDescription.
     */
    public String getChartOfAccountDescription() {
        return chartOfAccountDescription;
    }

    /**
     * Sets the chartOfAccountDescription
     * 
     * @param chartOfAccountDescription The chartOfAccountDescription to set.
     */
    public void setChartOfAccountDescription(String chartOfAccountDescription) {
        this.chartOfAccountDescription = chartOfAccountDescription;
    }

    /**
     * Gets the chartOfAccountsCode
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the orgChartOfAccountDescription
     * 
     * @return Returns the orgChartOfAccountDescription.
     */
    public String getOrgChartOfAccountDescription() {
        return orgChartOfAccountDescription;
    }

    /**
     * Sets the orgChartOfAccountDescription
     * 
     * @param orgChartOfAccountDescription The orgChartOfAccountDescription to set.
     */
    public void setOrgChartOfAccountDescription(String orgChartOfAccountDescription) {
        this.orgChartOfAccountDescription = orgChartOfAccountDescription;
    }

    /**
     * Gets the orgChartOfAccountsCode
     * 
     * @return Returns the orgChartOfAccountsCode.
     */
    public String getOrgChartOfAccountsCode() {
        return orgChartOfAccountsCode;
    }

    /**
     * Sets the orgChartOfAccountsCode
     * 
     * @param orgChartOfAccountsCode The orgChartOfAccountsCode to set.
     */
    public void setOrgChartOfAccountsCode(String orgChartOfAccountsCode) {
        this.orgChartOfAccountsCode = orgChartOfAccountsCode;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getAdministrativePost() {
        return administrativePost;
    }

    public void setAdministrativePost(String administrativePost) {
        this.administrativePost = administrativePost;
    }

    public String getAppointmentFundingDurationCode() {
        return appointmentFundingDurationCode;
    }

    public void setAppointmentFundingDurationCode(String appointmentFundingDurationCode) {
        this.appointmentFundingDurationCode = appointmentFundingDurationCode;
    }

    public Integer getAppointmentFundingMonth() {
        return appointmentFundingMonth;
    }

    public void setAppointmentFundingMonth(Integer appointmentFundingMonth) {
        this.appointmentFundingMonth = appointmentFundingMonth;
    }

    public Integer getAppointmentRequestedAmount() {
        return appointmentRequestedAmount;
    }

    public void setAppointmentRequestedAmount(Integer appointmentRequestedAmount) {
        this.appointmentRequestedAmount = appointmentRequestedAmount;
    }

    public Integer getAppointmentRequestedCsfAmount() {
        return appointmentRequestedCsfAmount;
    }

    public void setAppointmentRequestedCsfAmount(Integer appointmentRequestedCsfAmount) {
        this.appointmentRequestedCsfAmount = appointmentRequestedCsfAmount;
    }

    public BigDecimal getAppointmentRequestedCsfTimePercent() {
        return appointmentRequestedCsfTimePercent;
    }

    public void setAppointmentRequestedCsfTimePercent(BigDecimal appointmentRequestedCsfTimePercent) {
        this.appointmentRequestedCsfTimePercent = appointmentRequestedCsfTimePercent;
    }

    public BigDecimal getAppointmentRequestedTimePercent() {
        return appointmentRequestedTimePercent;
    }

    public void setAppointmentRequestedTimePercent(BigDecimal appointmentRequestedTimePercent) {
        this.appointmentRequestedTimePercent = appointmentRequestedTimePercent;
    }

    public Integer getAppointmentTotalIntendedAmount() {
        return appointmentTotalIntendedAmount;
    }

    public void setAppointmentTotalIntendedAmount(Integer appointmentTotalIntendedAmount) {
        this.appointmentTotalIntendedAmount = appointmentTotalIntendedAmount;
    }

    public Integer getCsfAmount() {
        return csfAmount;
    }

    public void setCsfAmount(Integer csfAmount) {
        this.csfAmount = csfAmount;
    }

    public BigDecimal getAppointmentRequestedCsfFteQuantity() {
        return appointmentRequestedCsfFteQuantity;
    }

    public void setAppointmentRequestedCsfFteQuantity(BigDecimal appointmentRequestedCsfFteQuantity) {
        this.appointmentRequestedCsfFteQuantity = appointmentRequestedCsfFteQuantity;
    }

    public BigDecimal getAppointmentRequestedFteQuantity() {
        return appointmentRequestedFteQuantity;
    }

    public void setAppointmentRequestedFteQuantity(BigDecimal appointmentRequestedFteQuantity) {
        this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
    }

    public BigDecimal getAppointmentTotalIntendedFteQuantity() {
        return appointmentTotalIntendedFteQuantity;
    }

    public void setAppointmentTotalIntendedFteQuantity(BigDecimal appointmentTotalIntendedFteQuantity) {
        this.appointmentTotalIntendedFteQuantity = appointmentTotalIntendedFteQuantity;
    }

    public BigDecimal getCsfFullTimeEmploymentQuantity() {
        return csfFullTimeEmploymentQuantity;
    }

    public void setCsfFullTimeEmploymentQuantity(BigDecimal csfFullTimeEmploymentQuantity) {
        this.csfFullTimeEmploymentQuantity = csfFullTimeEmploymentQuantity;
    }

    public BigDecimal getCsfTimePercent() {
        return csfTimePercent;
    }

    public void setCsfTimePercent(BigDecimal csfTimePercent) {
        this.csfTimePercent = csfTimePercent;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getIuClassificationLevel() {
        return iuClassificationLevel;
    }

    public void setIuClassificationLevel(String iuClassificationLevel) {
        this.iuClassificationLevel = iuClassificationLevel;
    }

    public String getNormalWorkMonthsAndiuPayMonths() {
        return normalWorkMonthsAndiuPayMonths;
    }

    public void setNormalWorkMonthsAndiuPayMonths(String normalWorkMonthsAndiuPayMonths) {
        this.normalWorkMonthsAndiuPayMonths = normalWorkMonthsAndiuPayMonths;
    }

    public String getPositionGradeDefault() {
        return positionGradeDefault;
    }

    public void setPositionGradeDefault(String positionGradeDefault) {
        this.positionGradeDefault = positionGradeDefault;
    }

    public String getPositionNumber() {
        return positionNumber;
    }

    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    public String getPositionSalaryPlanDefault() {
        return positionSalaryPlanDefault;
    }

    public void setPositionSalaryPlanDefault(String positionSalaryPlanDefault) {
        this.positionSalaryPlanDefault = positionSalaryPlanDefault;
    }

    public BigDecimal getPositionStandardHoursDefault() {
        return positionStandardHoursDefault;
    }

    public void setPositionStandardHoursDefault(BigDecimal positionStandardHoursDefault) {
        this.positionStandardHoursDefault = positionStandardHoursDefault;
    }

    public Integer getTotalAccountAmountChange() {
        return totalAccountAmountChange;
    }

    public void setTotalAccountAmountChange(Integer totalAccountAmountChange) {
        this.totalAccountAmountChange = totalAccountAmountChange;
    }

    public Integer getTotalAccountAppointmentRequestedAmount() {
        return totalAccountAppointmentRequestedAmount;
    }

    public void setTotalAccountAppointmentRequestedAmount(Integer totalAccountAppointmentRequestedAmount) {
        this.totalAccountAppointmentRequestedAmount = totalAccountAppointmentRequestedAmount;
    }

    public BigDecimal getTotalAccountAppointmentRequestedFteQuantity() {
        return totalAccountAppointmentRequestedFteQuantity;
    }

    public void setTotalAccountAppointmentRequestedFteQuantity(BigDecimal totalAccountAppointmentRequestedFteQuantity) {
        this.totalAccountAppointmentRequestedFteQuantity = totalAccountAppointmentRequestedFteQuantity;
    }

    public BigDecimal getTotalAccountPercentChange() {
        return totalAccountPercentChange;
    }

    public void setTotalAccountPercentChange(BigDecimal totalAccountPercentChange) {
        this.totalAccountPercentChange = totalAccountPercentChange;
    }

    public Integer getTotalAccountPositionCsfAmount() {
        return totalAccountPositionCsfAmount;
    }

    public void setTotalAccountPositionCsfAmount(Integer totalAccountPositionCsfAmount) {
        this.totalAccountPositionCsfAmount = totalAccountPositionCsfAmount;
    }

    public BigDecimal getTotalAccountPositionCsfFteQuantity() {
        return totalAccountPositionCsfFteQuantity;
    }

    public void setTotalAccountPositionCsfFteQuantity(BigDecimal totalAccountPositionCsfFteQuantity) {
        this.totalAccountPositionCsfFteQuantity = totalAccountPositionCsfFteQuantity;
    }

    public Integer getTotalObjectAmountChange() {
        return totalObjectAmountChange;
    }

    public void setTotalObjectAmountChange(Integer totalObjectAmountChange) {
        this.totalObjectAmountChange = totalObjectAmountChange;
    }

    public Integer getTotalObjectAppointmentRequestedAmount() {
        return totalObjectAppointmentRequestedAmount;
    }

    public void setTotalObjectAppointmentRequestedAmount(Integer totalObjectAppointmentRequestedAmount) {
        this.totalObjectAppointmentRequestedAmount = totalObjectAppointmentRequestedAmount;
    }

    public BigDecimal getTotalObjectAppointmentRequestedFteQuantity() {
        return totalObjectAppointmentRequestedFteQuantity;
    }

    public void setTotalObjectAppointmentRequestedFteQuantity(BigDecimal totalObjectAppointmentRequestedFteQuantity) {
        this.totalObjectAppointmentRequestedFteQuantity = totalObjectAppointmentRequestedFteQuantity;
    }

    public String getTotalObjectname() {
        return totalObjectname;
    }

    public void setTotalObjectname(String totalObjectname) {
        this.totalObjectname = totalObjectname;
    }

    public BigDecimal getTotalObjectPercentChange() {
        return totalObjectPercentChange;
    }

    public void setTotalObjectPercentChange(BigDecimal totalObjectPercentChange) {
        this.totalObjectPercentChange = totalObjectPercentChange;
    }

    public Integer getTotalObjectPositionCsfAmount() {
        return totalObjectPositionCsfAmount;
    }

    public void setTotalObjectPositionCsfAmount(Integer totalObjectPositionCsfAmount) {
        this.totalObjectPositionCsfAmount = totalObjectPositionCsfAmount;
    }

    public BigDecimal getTotalObjectPositionCsfFteQuantity() {
        return totalObjectPositionCsfFteQuantity;
    }

    public void setTotalObjectPositionCsfFteQuantity(BigDecimal totalObjectPositionCsfFteQuantity) {
        this.totalObjectPositionCsfFteQuantity = totalObjectPositionCsfFteQuantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmountChange() {
        return amountChange;
    }

    public void setAmountChange(Integer amountChange) {
        this.amountChange = amountChange;
    }

    public BigDecimal getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(BigDecimal percentChange) {
        this.percentChange = percentChange;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getEmplid() {
        return emplid;
    }

    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    public BigDecimal getPositionFte() {
        return positionFte;
    }

    public void setPositionFte(BigDecimal positionFte) {
        this.positionFte = positionFte;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getDeleteBox() {
        return deleteBox;
    }

    public void setDeleteBox(String deleteBox) {
        this.deleteBox = deleteBox;
    }

    public String getFinancialObjectCodeName() {
        return financialObjectCodeName;
    }

    public void setFinancialObjectCodeName(String financialObjectCodeName) {
        this.financialObjectCodeName = financialObjectCodeName;
    }

    public String getObjectCodes() {
        return objectCodes;
    }

    public void setObjectCodes(String objectCodes) {
        this.objectCodes = objectCodes;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getSubAccountName() {
        return subAccountName;
    }

    public void setSubAccountName(String subAccountName) {
        this.subAccountName = subAccountName;
    }

    public String getAccountNumberAndName() {
        return accountNumberAndName;
    }

    public void setAccountNumberAndName(String accountNumberAndName) {
        this.accountNumberAndName = accountNumberAndName;
    }

    public String getSubAccountNumberAndName() {
        return subAccountNumberAndName;
    }

    public void setSubAccountNumberAndName(String subAccountNumberAndName) {
        this.subAccountNumberAndName = subAccountNumberAndName;
    }

    public String getDivider() {
        return divider;
    }

    public void setDivider(String divider) {
        this.divider = divider;
    }
}

