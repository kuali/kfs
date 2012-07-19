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
public class BudgetConstructionOrgSalaryStatisticsReport {

    // Header parts

    private String fiscalYear;

    private String chartOfAccountsCode;
    private String chartOfAccountDescription;
    private String organizationCode;
    private String organizationName;
    private String consHdr;
    private String objectCodes;
    private String threshold;

    // Body parts
    private BigDecimal initialRequestedFteQuantity;
    private Integer totalInitialRequestedAmount;
    private Integer totalAverageAmount = new Integer(0);
    private BigDecimal appointmentRequestedFteQuantity;
    private Integer totalCsfAmount;
    private Integer totalAppointmentRequestedAmount;
    private BigDecimal averageCsfAmount;
    private BigDecimal averageAppointmentRequestedAmount;
    private BigDecimal averageChange;
    private BigDecimal percentChange;

    public String getChartOfAccountDescription() {
        return chartOfAccountDescription;
    }

    public void setChartOfAccountDescription(String chartOfAccountDescription) {
        this.chartOfAccountDescription = chartOfAccountDescription;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getConsHdr() {
        return consHdr;
    }

    public void setConsHdr(String consHdr) {
        this.consHdr = consHdr;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getObjectCodes() {
        return objectCodes;
    }

    public void setObjectCodes(String objectCodes) {
        this.objectCodes = objectCodes;
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

    public BigDecimal getAverageAppointmentRequestedAmount() {
        return averageAppointmentRequestedAmount;
    }

    public void setAverageAppointmentRequestedAmount(BigDecimal averageAppointmentRequestedAmount) {
        this.averageAppointmentRequestedAmount = averageAppointmentRequestedAmount;
    }

    public BigDecimal getAverageChange() {
        return averageChange;
    }

    public void setAverageChange(BigDecimal averageChange) {
        this.averageChange = averageChange;
    }

    public BigDecimal getAverageCsfAmount() {
        return averageCsfAmount;
    }

    public void setAverageCsfAmount(BigDecimal averageCsfAmount) {
        this.averageCsfAmount = averageCsfAmount;
    }

    public BigDecimal getInitialRequestedFteQuantity() {
        return initialRequestedFteQuantity;
    }

    public void setInitialRequestedFteQuantity(BigDecimal initialRequestedFteQuantity) {
        this.initialRequestedFteQuantity = initialRequestedFteQuantity;
    }

    public BigDecimal getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(BigDecimal percentChange) {
        this.percentChange = percentChange;
    }

    public Integer getTotalAppointmentRequestedAmount() {
        return totalAppointmentRequestedAmount;
    }

    public void setTotalAppointmentRequestedAmount(Integer totalAppointmentRequestedAmount) {
        this.totalAppointmentRequestedAmount = totalAppointmentRequestedAmount;
    }

    public Integer getTotalCsfAmount() {
        return totalCsfAmount;
    }

    public void setTotalCsfAmount(Integer totalCsfAmount) {
        this.totalCsfAmount = totalCsfAmount;
    }

    public BigDecimal getAppointmentRequestedFteQuantity() {
        return appointmentRequestedFteQuantity;
    }

    public void setAppointmentRequestedFteQuantity(BigDecimal appointmentRequestedFteQuantity) {
        this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
    }

    public Integer getTotalAverageAmount() {
        return totalAverageAmount;
    }

    public void setTotalAverageAmount(Integer totalAverageAmount) {
        this.totalAverageAmount = totalAverageAmount;
    }

    public Integer getTotalInitialRequestedAmount() {
        return totalInitialRequestedAmount;
    }

    public void setTotalInitialRequestedAmount(Integer totalInitialRequestedAmount) {
        this.totalInitialRequestedAmount = totalInitialRequestedAmount;
    }

    /**
     * Gets the threshold attribute.
     * 
     * @return Returns the threshold
     */
    
    public String getThreshold() {
        return threshold;
    }

    /**	
     * Sets the threshold attribute.
     * 
     * @param threshold The threshold to set.
     */
    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }
}
