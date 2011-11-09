/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.batch;

import java.sql.Date;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Value Object (not OJB aware) class to bring information required by Depreciation Job this avoid unnecessary calls to DB for
 * reference information required by the processing and eases the use of memory and DB calls by OJB
 */
public class AssetPaymentInfo {
    private Long capitalAssetNumber;
    private Integer paymentSequenceNumber;
    private Date depreciationDate;
    private Integer depreciableLifeLimit;
    private String organizationPlantAccountNumber;
    private String organizationPlantChartCode;
    private String campusPlantChartCode;
    private String campusPlantAccountNumber;
    private String financialObjectTypeCode;
    private String financialObjectSubTypeCode;
    private String primaryDepreciationMethodCode;
    private KualiDecimal salvageAmount;

    private KualiDecimal primaryDepreciationBaseAmount;
    private String financialObjectCode;
    private KualiDecimal accumulatedPrimaryDepreciationAmount;
    private String subAccountNumber;
    private String financialSubObjectCode;
    private String projectCode;
    private String chartOfAccountsCode;

    private KualiDecimal transactionAmount;


    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber.
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute value.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * Gets the paymentSequenceNumber attribute.
     * 
     * @return Returns the paymentSequenceNumber.
     */
    public Integer getPaymentSequenceNumber() {
        return paymentSequenceNumber;
    }

    /**
     * Sets the paymentSequenceNumber attribute value.
     * 
     * @param paymentSequenceNumber The paymentSequenceNumber to set.
     */
    public void setPaymentSequenceNumber(Integer paymentSequenceNumber) {
        this.paymentSequenceNumber = paymentSequenceNumber;
    }

    /**
     * Gets the depreciationDate attribute.
     * 
     * @return Returns the depreciationDate.
     */
    public Date getDepreciationDate() {
        return depreciationDate;
    }

    /**
     * Sets the depreciationDate attribute value.
     * 
     * @param depreciationDate The depreciationDate to set.
     */
    public void setDepreciationDate(Date depreciationDate) {
        this.depreciationDate = depreciationDate;
    }

    /**
     * Gets the depreciableLifeLimit attribute.
     * 
     * @return Returns the depreciableLifeLimit.
     */
    public Integer getDepreciableLifeLimit() {
        return depreciableLifeLimit;
    }

    /**
     * Sets the depreciableLifeLimit attribute value.
     * 
     * @param depreciableLifeLimit The depreciableLifeLimit to set.
     */
    public void setDepreciableLifeLimit(Integer depreciableLifeLimit) {
        this.depreciableLifeLimit = depreciableLifeLimit;
    }

    /**
     * Gets the organizationPlantAccountNumber attribute.
     * 
     * @return Returns the organizationPlantAccountNumber.
     */
    public String getOrganizationPlantAccountNumber() {
        return organizationPlantAccountNumber;
    }

    /**
     * Sets the organizationPlantAccountNumber attribute value.
     * 
     * @param organizationPlantAccountNumber The organizationPlantAccountNumber to set.
     */
    public void setOrganizationPlantAccountNumber(String organizationPlantAccountNumber) {
        this.organizationPlantAccountNumber = organizationPlantAccountNumber;
    }

    /**
     * Gets the organizationPlantChartCode attribute.
     * 
     * @return Returns the organizationPlantChartCode.
     */
    public String getOrganizationPlantChartCode() {
        return organizationPlantChartCode;
    }

    /**
     * Sets the organizationPlantChartCode attribute value.
     * 
     * @param organizationPlantChartCode The organizationPlantChartCode to set.
     */
    public void setOrganizationPlantChartCode(String organizationPlantChartCode) {
        this.organizationPlantChartCode = organizationPlantChartCode;
    }

    /**
     * Gets the primaryDepreciationMethodCode attribute.
     * 
     * @return Returns the primaryDepreciationMethodCode.
     */
    public String getPrimaryDepreciationMethodCode() {
        return primaryDepreciationMethodCode;
    }

    /**
     * Sets the primaryDepreciationMethodCode attribute value.
     * 
     * @param primaryDepreciationMethodCode The primaryDepreciationMethodCode to set.
     */
    public void setPrimaryDepreciationMethodCode(String primaryDepreciationMethodCode) {
        this.primaryDepreciationMethodCode = primaryDepreciationMethodCode;
    }

    /**
     * Gets the salvageAmount attribute.
     * 
     * @return Returns the salvageAmount.
     */
    public KualiDecimal getSalvageAmount() {
        return salvageAmount;
    }

    /**
     * Sets the salvageAmount attribute value.
     * 
     * @param salvageAmount The salvageAmount to set.
     */
    public void setSalvageAmount(KualiDecimal salvageAmount) {
        this.salvageAmount = salvageAmount;
    }

    /**
     * Gets the campusPlantChartCode attribute.
     * 
     * @return Returns the campusPlantChartCode.
     */
    public String getCampusPlantChartCode() {
        return campusPlantChartCode;
    }

    /**
     * Sets the campusPlantChartCode attribute value.
     * 
     * @param campusPlantChartCode The campusPlantChartCode to set.
     */
    public void setCampusPlantChartCode(String campusPlantChartCode) {
        this.campusPlantChartCode = campusPlantChartCode;
    }

    /**
     * Gets the campusPlantAccountNumber attribute.
     * 
     * @return Returns the campusPlantAccountNumber.
     */
    public String getCampusPlantAccountNumber() {
        return campusPlantAccountNumber;
    }

    /**
     * Sets the campusPlantAccountNumber attribute value.
     * 
     * @param campusPlantAccountNumber The campusPlantAccountNumber to set.
     */
    public void setCampusPlantAccountNumber(String campusPlantAccountNumber) {
        this.campusPlantAccountNumber = campusPlantAccountNumber;
    }

    /**
     * Gets the financialObjectSubTypeCode attribute.
     * 
     * @return Returns the financialObjectSubTypeCode.
     */
    public String getFinancialObjectSubTypeCode() {
        return financialObjectSubTypeCode;
    }

    /**
     * Sets the financialObjectSubTypeCode attribute value.
     * 
     * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
     */
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
        this.financialObjectSubTypeCode = financialObjectSubTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the primaryDepreciationBaseAmount attribute.
     * 
     * @return Returns the primaryDepreciationBaseAmount.
     */
    public KualiDecimal getPrimaryDepreciationBaseAmount() {
        return primaryDepreciationBaseAmount;
    }

    /**
     * Sets the primaryDepreciationBaseAmount attribute value.
     * 
     * @param primaryDepreciationBaseAmount The primaryDepreciationBaseAmount to set.
     */
    public void setPrimaryDepreciationBaseAmount(KualiDecimal primaryDepreciationBaseAmount) {
        this.primaryDepreciationBaseAmount = primaryDepreciationBaseAmount;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the accumulatedPrimaryDepreciationAmount attribute.
     * 
     * @return Returns the accumulatedPrimaryDepreciationAmount.
     */
    public KualiDecimal getAccumulatedPrimaryDepreciationAmount() {
        return accumulatedPrimaryDepreciationAmount;
    }

    /**
     * Sets the accumulatedPrimaryDepreciationAmount attribute value.
     * 
     * @param accumulatedPrimaryDepreciationAmount The accumulatedPrimaryDepreciationAmount to set.
     */
    public void setAccumulatedPrimaryDepreciationAmount(KualiDecimal accumulatedPrimaryDepreciationAmount) {
        this.accumulatedPrimaryDepreciationAmount = accumulatedPrimaryDepreciationAmount;
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
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the projectCode attribute.
     * 
     * @return Returns the projectCode.
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode attribute value.
     * 
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Gets the transactionAmount attribute.
     * 
     * @return Returns the transactionAmount.
     */
    public KualiDecimal getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * Sets the transactionAmount attribute value.
     * 
     * @param transactionAmount The transactionAmount to set.
     */
    public void setTransactionAmount(KualiDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
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


}
