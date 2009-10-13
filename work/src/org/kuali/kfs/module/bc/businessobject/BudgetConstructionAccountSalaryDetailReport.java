/*
 * Copyright 2006-2008 The Kuali Foundation
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
 * 
 */
public class BudgetConstructionAccountSalaryDetailReport{

    // Header parts
    private String fiscalYear;
    private String chartOfAccountsCode;
    private String chartOfAccountDescription;
    private String organizationCode;
    private String organizationName;
    private String fundGroupCode;
    private String fundGroupName;
    private String accountNumber;
    private String subAccountNumber;
    private String accountName;
    private String subAccountName;

    
    // Body parts

    private String financialObjectCode;
    private String financialObjectCodeName;
    
    private String name;
    // from PendingBudgetConstructionAppointmentFunding
    private String deleteBox;
    private String financialSubObjectCode;
    private String iuClassificationLevel;
    // from BudgetConstructionAdministrativePost
    private String administrativePost;
    
    // from BudgetConstructionPosition
    private String positionNumber;
    private String positionSalaryPlanDefault;
    private String positionGradeDefault;
    private String normalWorkMonthsAndiuPayMonths;
    
    // from BudgetConstructionCalculatedSalaryFoundationTracker
    private Integer positionCsfAmount;
    private BigDecimal csfTimePercent;
    private BigDecimal positionCsfFullTimeEmploymentQuantity;
    private String positionCsfFundingStatusCode;

    // from PendingBudgetConstructionAppointmentFunding
    private Integer appointmentFundingMonth;
    private BigDecimal appointmentRequestedPayRate;
    private Integer appointmentRequestedAmount;
    private BigDecimal appointmentRequestedTimePercent;
    private BigDecimal appointmentRequestedFteQuantity;
    private Integer appointmentRequestedCsfAmount;
    private BigDecimal appointmentRequestedCsfTimePercent;
    private BigDecimal appointmentRequestedCsfFteQuantity;
    private String appointmentFundingDurationCode;
    private Integer appointmentTotalIntendedAmount;
    private BigDecimal appointmentTotalIntendedFteQuantity;
    
    private Integer amountChange;
    private BigDecimal percentChange;

    // total
    private String totalDescription;
    private Integer totalBaseAmount;
    private BigDecimal totalBaseFte;
    private Integer totalRequestAmount;
    private BigDecimal totalRequestFte;
    private Integer totalAmountChange;
    private BigDecimal totalPercentChange;
    
    
    
    
    /**
     * Default constructor.
     */
    public BudgetConstructionAccountSalaryDetailReport() {

    }




    public String getFiscalYear() {
        return fiscalYear;
    }




    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }




    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }




    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }




    public String getChartOfAccountDescription() {
        return chartOfAccountDescription;
    }




    public void setChartOfAccountDescription(String chartOfAccountDescription) {
        this.chartOfAccountDescription = chartOfAccountDescription;
    }




    public String getOrganizationCode() {
        return organizationCode;
    }




    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }




    public String getOrganizationName() {
        return organizationName;
    }




    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }




    public String getFundGroupCode() {
        return fundGroupCode;
    }




    public void setFundGroupCode(String fundGroupCode) {
        this.fundGroupCode = fundGroupCode;
    }




    public String getFundGroupName() {
        return fundGroupName;
    }




    public void setFundGroupName(String fundGroupName) {
        this.fundGroupName = fundGroupName;
    }




    public String getAccountNumber() {
        return accountNumber;
    }




    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }




    public String getSubAccountNumber() {
        return subAccountNumber;
    }




    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
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




    public String getFinancialObjectCode() {
        return financialObjectCode;
    }




    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }




    public String getFinancialObjectCodeName() {
        return financialObjectCodeName;
    }




    public void setFinancialObjectCodeName(String financialObjectCodeName) {
        this.financialObjectCodeName = financialObjectCodeName;
    }




    public String getName() {
        return name;
    }




    public void setName(String name) {
        this.name = name;
    }




    public String getDeleteBox() {
        return deleteBox;
    }




    public void setDeleteBox(String deleteBox) {
        this.deleteBox = deleteBox;
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




    public String getAdministrativePost() {
        return administrativePost;
    }




    public void setAdministrativePost(String administrativePost) {
        this.administrativePost = administrativePost;
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




    public String getPositionGradeDefault() {
        return positionGradeDefault;
    }




    public void setPositionGradeDefault(String positionGradeDefault) {
        this.positionGradeDefault = positionGradeDefault;
    }




    public String getNormalWorkMonthsAndiuPayMonths() {
        return normalWorkMonthsAndiuPayMonths;
    }




    public void setNormalWorkMonthsAndiuPayMonths(String normalWorkMonthsAndiuPayMonths) {
        this.normalWorkMonthsAndiuPayMonths = normalWorkMonthsAndiuPayMonths;
    }




    public Integer getPositionCsfAmount() {
        return positionCsfAmount;
    }




    public void setPositionCsfAmount(Integer positionCsfAmount) {
        this.positionCsfAmount = positionCsfAmount;
    }




    public BigDecimal getPositionCsfFullTimeEmploymentQuantity() {
        return positionCsfFullTimeEmploymentQuantity;
    }




    public void setPositionCsfFullTimeEmploymentQuantity(BigDecimal positionCsfFullTimeEmploymentQuantity) {
        this.positionCsfFullTimeEmploymentQuantity = positionCsfFullTimeEmploymentQuantity;
    }




    public String getPositionCsfFundingStatusCode() {
        return positionCsfFundingStatusCode;
    }




    public void setPositionCsfFundingStatusCode(String positionCsfFundingStatusCode) {
        this.positionCsfFundingStatusCode = positionCsfFundingStatusCode;
    }




    public Integer getAppointmentFundingMonth() {
        return appointmentFundingMonth;
    }




    public void setAppointmentFundingMonth(Integer appointmentFundingMonth) {
        this.appointmentFundingMonth = appointmentFundingMonth;
    }




    public BigDecimal getAppointmentRequestedPayRate() {
        return appointmentRequestedPayRate;
    }




    public void setAppointmentRequestedPayRate(BigDecimal appointmentRequestedPayRate) {
        this.appointmentRequestedPayRate = appointmentRequestedPayRate;
    }




    public Integer getAppointmentRequestedAmount() {
        return appointmentRequestedAmount;
    }




    public void setAppointmentRequestedAmount(Integer appointmentRequestedAmount) {
        this.appointmentRequestedAmount = appointmentRequestedAmount;
    }




    public BigDecimal getAppointmentRequestedFteQuantity() {
        return appointmentRequestedFteQuantity;
    }




    public void setAppointmentRequestedFteQuantity(BigDecimal appointmentRequestedFteQuantity) {
        this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
    }




    public Integer getAppointmentRequestedCsfAmount() {
        return appointmentRequestedCsfAmount;
    }




    public void setAppointmentRequestedCsfAmount(Integer appointmentRequestedCsfAmount) {
        this.appointmentRequestedCsfAmount = appointmentRequestedCsfAmount;
    }




    public String getAppointmentFundingDurationCode() {
        return appointmentFundingDurationCode;
    }




    public void setAppointmentFundingDurationCode(String appointmentFundingDurationCode) {
        this.appointmentFundingDurationCode = appointmentFundingDurationCode;
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




    public String getTotalDescription() {
        return totalDescription;
    }




    public void setTotalDescription(String totalDescription) {
        this.totalDescription = totalDescription;
    }




    public Integer getTotalBaseAmount() {
        return totalBaseAmount;
    }




    public void setTotalBaseAmount(Integer totalBaseAmount) {
        this.totalBaseAmount = totalBaseAmount;
    }




    public BigDecimal getTotalBaseFte() {
        return totalBaseFte;
    }




    public void setTotalBaseFte(BigDecimal totalBaseFte) {
        this.totalBaseFte = totalBaseFte;
    }




    public Integer getTotalRequestAmount() {
        return totalRequestAmount;
    }




    public void setTotalRequestAmount(Integer totalRequestAmount) {
        this.totalRequestAmount = totalRequestAmount;
    }




    public BigDecimal getTotalRequestFte() {
        return totalRequestFte;
    }




    public void setTotalRequestFte(BigDecimal totalRequestFte) {
        this.totalRequestFte = totalRequestFte;
    }




    public Integer getTotalAmountChange() {
        return totalAmountChange;
    }




    public void setTotalAmountChange(Integer totalAmountChange) {
        this.totalAmountChange = totalAmountChange;
    }




    public BigDecimal getTotalPercentChange() {
        return totalPercentChange;
    }




    public void setTotalPercentChange(BigDecimal totalPercentChange) {
        this.totalPercentChange = totalPercentChange;
    }




    /**
     * Gets the csfTimePercent attribute. 
     * @return Returns the csfTimePercent.
     */
    public BigDecimal getCsfTimePercent() {
        return csfTimePercent;
    }




    /**
     * Sets the csfTimePercent attribute value.
     * @param csfTimePercent The csfTimePercent to set.
     */
    public void setCsfTimePercent(BigDecimal csfTimePercent) {
        this.csfTimePercent = csfTimePercent;
    }




    /**
     * Gets the appointmentRequestedTimePercent attribute. 
     * @return Returns the appointmentRequestedTimePercent.
     */
    public BigDecimal getAppointmentRequestedTimePercent() {
        return appointmentRequestedTimePercent;
    }




    /**
     * Sets the appointmentRequestedTimePercent attribute value.
     * @param appointmentRequestedTimePercent The appointmentRequestedTimePercent to set.
     */
    public void setAppointmentRequestedTimePercent(BigDecimal appointmentRequestedTimePercent) {
        this.appointmentRequestedTimePercent = appointmentRequestedTimePercent;
    }




    /**
     * Gets the appointmentRequestedCsfTimePercent attribute. 
     * @return Returns the appointmentRequestedCsfTimePercent.
     */
    public BigDecimal getAppointmentRequestedCsfTimePercent() {
        return appointmentRequestedCsfTimePercent;
    }




    /**
     * Sets the appointmentRequestedCsfTimePercent attribute value.
     * @param appointmentRequestedCsfTimePercent The appointmentRequestedCsfTimePercent to set.
     */
    public void setAppointmentRequestedCsfTimePercent(BigDecimal appointmentRequestedCsfTimePercent) {
        this.appointmentRequestedCsfTimePercent = appointmentRequestedCsfTimePercent;
    }




    /**
     * Gets the appointmentRequestedCsfFteQuantity attribute. 
     * @return Returns the appointmentRequestedCsfFteQuantity.
     */
    public BigDecimal getAppointmentRequestedCsfFteQuantity() {
        return appointmentRequestedCsfFteQuantity;
    }




    /**
     * Sets the appointmentRequestedCsfFteQuantity attribute value.
     * @param appointmentRequestedCsfFteQuantity The appointmentRequestedCsfFteQuantity to set.
     */
    public void setAppointmentRequestedCsfFteQuantity(BigDecimal appointmentRequestedCsfFteQuantity) {
        this.appointmentRequestedCsfFteQuantity = appointmentRequestedCsfFteQuantity;
    }
}

