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
public class BudgetConstructionOrgPositionFundingDetailReport {

    // Header parts
    private String fiscalYear;
    
    private String orgChartOfAccountsCode;
    private String orgChartOfAccountDescription;
    private String organizationCode;
    private String organizationName;
    private String consHdr;
    private String reqFy;
    private String objectCodes;
    private String numberAndNameForAccountSubAccount;
    
    
    // Groups
    private String emplid;
    private Integer personSortCode;
    
    // Body parts
    
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private BigDecimal appointmentRequestedPayRate;
    
    private String deleteBox;
    private String name;
    private String cls;
    
    //from PendingBudgetConstructionAppointmentFunding
    
    //from BudgetConstructionAdministrativePost
    private String administrativePost;
    //from BudgetConstructionPosition
    private String positionNumber;
    private String normalWorkMonthsAndiuPayMonths;
    private String positionSalaryPlanDefault;
    private String positionGradeDefault;
    private BigDecimal positionStandardHoursDefault;
    //from BudgetConstructionCalculatedSalaryFoundationTracker
    private String csfFundingStatusCode;
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
   
    private Integer totalPersonPositionCsfAmount;
    private Integer totalPersonAppointmentRequestedAmount;
    private BigDecimal totalPersonPositionCsfFteQuantity;
    private BigDecimal totalPersonAppointmentRequestedFteQuantity;
    private Integer totalPersonAmountChange;
    private BigDecimal totalPersonPercentChange;
    
   
    private Integer totalOrgPositionCsfAmount;
    private Integer totalOrgAppointmentRequestedAmount;
    private BigDecimal totalOrgPositionCsfFteQuantity;
    private BigDecimal totalOrgAppointmentRequestedFteQuantity;
    private Integer totalOrgAmountChange;
    private BigDecimal totalOrgPercentChange;
        
 
    
    
    

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

    public BigDecimal getAppointmentTotalIntendedFteQuantity() {
        return appointmentTotalIntendedFteQuantity;
    }

    public void setAppointmentTotalIntendedFteQuantity(BigDecimal appointmentTotalIntendedFteQuantity) {
        this.appointmentTotalIntendedFteQuantity = appointmentTotalIntendedFteQuantity;
    }

    public Integer getCsfAmount() {
        return csfAmount;
    }

    public void setCsfAmount(Integer csfAmount) {
        this.csfAmount = csfAmount;
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

    
    public BigDecimal getAppointmentRequestedPayRate() {
        return appointmentRequestedPayRate;
    }

    public void setAppointmentRequestedPayRate(BigDecimal appointmentRequestedPayRate) {
        this.appointmentRequestedPayRate = appointmentRequestedPayRate;
    }

    public Integer getTotalOrgAmountChange() {
        return totalOrgAmountChange;
    }

    public void setTotalOrgAmountChange(Integer totalOrgAmountChange) {
        this.totalOrgAmountChange = totalOrgAmountChange;
    }

    public Integer getTotalOrgAppointmentRequestedAmount() {
        return totalOrgAppointmentRequestedAmount;
    }

    public void setTotalOrgAppointmentRequestedAmount(Integer totalOrgAppointmentRequestedAmount) {
        this.totalOrgAppointmentRequestedAmount = totalOrgAppointmentRequestedAmount;
    }

    public BigDecimal getTotalOrgAppointmentRequestedFteQuantity() {
        return totalOrgAppointmentRequestedFteQuantity;
    }

    public void setTotalOrgAppointmentRequestedFteQuantity(BigDecimal totalOrgAppointmentRequestedFteQuantity) {
        this.totalOrgAppointmentRequestedFteQuantity = totalOrgAppointmentRequestedFteQuantity;
    }

    public BigDecimal getTotalOrgPercentChange() {
        return totalOrgPercentChange;
    }

    public void setTotalOrgPercentChange(BigDecimal totalOrgPercentChange) {
        this.totalOrgPercentChange = totalOrgPercentChange;
    }

    public Integer getTotalOrgPositionCsfAmount() {
        return totalOrgPositionCsfAmount;
    }

    public void setTotalOrgPositionCsfAmount(Integer totalOrgPositionCsfAmount) {
        this.totalOrgPositionCsfAmount = totalOrgPositionCsfAmount;
    }

    public BigDecimal getTotalOrgPositionCsfFteQuantity() {
        return totalOrgPositionCsfFteQuantity;
    }

    public void setTotalOrgPositionCsfFteQuantity(BigDecimal totalOrgPositionCsfFteQuantity) {
        this.totalOrgPositionCsfFteQuantity = totalOrgPositionCsfFteQuantity;
    }

    public Integer getTotalPersonAmountChange() {
        return totalPersonAmountChange;
    }

    public void setTotalPersonAmountChange(Integer totalPersonAmountChange) {
        this.totalPersonAmountChange = totalPersonAmountChange;
    }

    public Integer getTotalPersonAppointmentRequestedAmount() {
        return totalPersonAppointmentRequestedAmount;
    }

    public void setTotalPersonAppointmentRequestedAmount(Integer totalPersonAppointmentRequestedAmount) {
        this.totalPersonAppointmentRequestedAmount = totalPersonAppointmentRequestedAmount;
    }

    public BigDecimal getTotalPersonAppointmentRequestedFteQuantity() {
        return totalPersonAppointmentRequestedFteQuantity;
    }

    public void setTotalPersonAppointmentRequestedFteQuantity(BigDecimal totalPersonAppointmentRequestedFteQuantity) {
        this.totalPersonAppointmentRequestedFteQuantity = totalPersonAppointmentRequestedFteQuantity;
    }

    public String getCsfFundingStatusCode() {
        return csfFundingStatusCode;
    }

    public void setCsfFundingStatusCode(String csfFundingStatusCode) {
        this.csfFundingStatusCode = csfFundingStatusCode;
    }

    public BigDecimal getTotalPersonPercentChange() {
        return totalPersonPercentChange;
    }

    public void setTotalPersonPercentChange(BigDecimal totalPersonPercentChange) {
        this.totalPersonPercentChange = totalPersonPercentChange;
    }

    public Integer getTotalPersonPositionCsfAmount() {
        return totalPersonPositionCsfAmount;
    }

    public void setTotalPersonPositionCsfAmount(Integer totalPersonPositionCsfAmount) {
        this.totalPersonPositionCsfAmount = totalPersonPositionCsfAmount;
    }

    public BigDecimal getTotalPersonPositionCsfFteQuantity() {
        return totalPersonPositionCsfFteQuantity;
    }

    public void setTotalPersonPositionCsfFteQuantity(BigDecimal totalPersonPositionCsfFteQuantity) {
        this.totalPersonPositionCsfFteQuantity = totalPersonPositionCsfFteQuantity;
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

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
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

    public String getObjectCodes() {
        return objectCodes;
    }

    public void setObjectCodes(String objectCodes) {
        this.objectCodes = objectCodes;
    }

    public String getNumberAndNameForAccountSubAccount() {
        return numberAndNameForAccountSubAccount;
    }

    public void setNumberAndNameForAccountSubAccount(String numberAndNameForAccountSubAccount) {
        this.numberAndNameForAccountSubAccount = numberAndNameForAccountSubAccount;
    }

    public Integer getPersonSortCode() {
        return personSortCode;
    }

    public void setPersonSortCode(Integer personSortCode) {
        this.personSortCode = personSortCode;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }


}

