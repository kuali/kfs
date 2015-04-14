/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.bc.businessobject;

import java.math.BigDecimal;

/**
 * Budget Construction Organization Account Funding Detail Report Business Object.
 */
public class BudgetConstructionOrgReasonStatisticsReport {

    // Header parts
    private String fiscalYear;
    private String chartOfAccountsCode;
    private String chartOfAccountDescription;
    private String organizationCode;
    private String organizationName;
    private String consHdr;
    private String objectCodes;
    private String thresholdOrReason;
    
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
    
    
    
    public BigDecimal getAppointmentRequestedFteQuantity() {
        return appointmentRequestedFteQuantity;
    }
    public void setAppointmentRequestedFteQuantity(BigDecimal appointmentRequestedFteQuantity) {
        this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
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
    public BigDecimal getInitialRequestedFteQuantity() {
        return initialRequestedFteQuantity;
    }
    public void setInitialRequestedFteQuantity(BigDecimal initialRequestedFteQuantity) {
        this.initialRequestedFteQuantity = initialRequestedFteQuantity;
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
    public Integer getTotalAverageAmount() {
        return totalAverageAmount;
    }
    public void setTotalAverageAmount(Integer totalAverageAmount) {
        this.totalAverageAmount = totalAverageAmount;
    }
    public Integer getTotalCsfAmount() {
        return totalCsfAmount;
    }
    public void setTotalCsfAmount(Integer totalCsfAmount) {
        this.totalCsfAmount = totalCsfAmount;
    }
    public Integer getTotalInitialRequestedAmount() {
        return totalInitialRequestedAmount;
    }
    public void setTotalInitialRequestedAmount(Integer totalInitialRequestedAmount) {
        this.totalInitialRequestedAmount = totalInitialRequestedAmount;
    }
    public String getThresholdOrReason() {
        return thresholdOrReason;
    }
    public void setThresholdOrReason(String thresholdOrReason) {
        this.thresholdOrReason = thresholdOrReason;
    }
}
