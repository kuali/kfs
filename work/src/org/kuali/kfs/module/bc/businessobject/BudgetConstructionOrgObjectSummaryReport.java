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
 * Budget Construction Organization Account Summary Report Business Object.
 */
public class BudgetConstructionOrgObjectSummaryReport {

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
    
    private String header1;
    private String header2;
    private String header2a;
    private String header3;
    private String header31;
    private String header4;
    private String header40;
    private String header5;
    private String header6;

    // Groups
    private String financialObjectLevelCode;
    private String incomeExpenseCode;
    private String financialConsolidationSortCode; 
    private String financialLevelSortCode;
    
    // Body parts
    private String financialObjectCode;
    private String financialObjectName;
    
    //when the values are zero, below fields should be blank, so make them as string.
    private BigDecimal positionCsfLeaveFteQuantity;
    private BigDecimal csfFullTimeEmploymentQuantity;
    private Integer financialBeginningBalanceLineAmount = new Integer(0);
    private BigDecimal appointmentRequestedCsfFteQuantity;
    private BigDecimal appointmentRequestedFteQuantity;
    private Integer accountLineAnnualBalanceAmount = new Integer(0);
    private Integer amountChange = new Integer(0);
    private BigDecimal percentChange = BigDecimal.ZERO;

    // Total parts
    
    private String totalLevelDescription;
    
    private BigDecimal totalLevelPositionCsfLeaveFteQuantity;
    private BigDecimal totalLevelPositionCsfFullTimeEmploymentQuantity;
    private Integer totalLevelFinancialBeginningBalanceLineAmount;
    private BigDecimal totalLevelAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalLevelAppointmentRequestedFteQuantity;
    private Integer totalLevelAccountLineAnnualBalanceAmount;
    private Integer totalLevelAmountChange;
    private BigDecimal totalLevelPercentChange;
    
    private String totalConsolidationDescription;
    
    private BigDecimal totalConsolidationPositionCsfLeaveFteQuantity;
    private BigDecimal totalConsolidationPositionCsfFullTimeEmploymentQuantity;
    private Integer totalConsolidationFinancialBeginningBalanceLineAmount;
    private BigDecimal totalConsolidationAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalConsolidationAppointmentRequestedFteQuantity;
    private Integer totalConsolidationAccountLineAnnualBalanceAmount;
    private Integer totalConsolidationAmountChange;
    private BigDecimal totalConsolidationPercentChange;

    private String grossDescription;
    private Integer grossFinancialBeginningBalanceLineAmount;
    private Integer grossAccountLineAnnualBalanceAmount;
    private Integer grossAmountChange;
    private BigDecimal grossPercentChange;
    
    private String typeDesc;
    private BigDecimal typePositionCsfLeaveFteQuantity;
    private BigDecimal typePositionCsfFullTimeEmploymentQuantity;
    private Integer typeFinancialBeginningBalanceLineAmount;
    private BigDecimal typeAppointmentRequestedCsfFteQuantity;
    private BigDecimal typeAppointmentRequestedFteQuantity;
    private Integer typeAccountLineAnnualBalanceAmount;
    private Integer typeAmountChange;
    private BigDecimal typePercentChange;
    
    private String totalSubFundGroupDesc;
    
    private Integer revenueFinancialBeginningBalanceLineAmount;
    private Integer revenueAccountLineAnnualBalanceAmount;
    private Integer revenueAmountChange;
    private BigDecimal revenuePercentChange;
    
    private Integer expenditureFinancialBeginningBalanceLineAmount;
    private Integer expenditureAccountLineAnnualBalanceAmount;
    private Integer expenditureAmountChange;
    private BigDecimal expenditurePercentChange;
    
    private Integer differenceFinancialBeginningBalanceLineAmount;
    private Integer differenceAccountLineAnnualBalanceAmount;
    private Integer differenceAmountChange;
    private BigDecimal differencePercentChange;
    
    
    /**
     * Gets the amountChange
     * 
     * @return Returns the amountChange.
     */
    public Integer getAmountChange() {
        return amountChange;
    }

    /**
     * Sets the amountChange
     * 
     * @param amountChange The amountChange to set.
     */
    public void setAmountChange(Integer amountChange) {
        this.amountChange = amountChange;
    }

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
     * Gets the header1
     * 
     * @return Returns the header1.
     */
    public String getHeader1() {
        return header1;
    }

    /**
     * Sets the header1
     * 
     * @param header1 The header1 to set.
     */
    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    /**
     * Gets the header2
     * 
     * @return Returns the header2.
     */
    public String getHeader2() {
        return header2;
    }

    /**
     * Sets the header2
     * 
     * @param header2 The header2 to set.
     */
    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    /**
     * Gets the header3
     * 
     * @return Returns the header3.
     */
    public String getHeader3() {
        return header3;
    }

    /**
     * Sets the header3
     * 
     * @param header3 The header3 to set.
     */
    public void setHeader3(String header3) {
        this.header3 = header3;
    }

    /**
     * Gets the header4
     * 
     * @return Returns the header4.
     */
    public String getHeader4() {
        return header4;
    }

    /**
     * Sets the header4
     * 
     * @param header4 The header4 to set.
     */
    public void setHeader4(String header4) {
        this.header4 = header4;
    }

    /**
     * Gets the header5
     * 
     * @return Returns the header5.
     */
    public String getHeader5() {
        return header5;
    }

    /**
     * Sets the header5
     * 
     * @param header5 The header5 to set.
     */
    public void setHeader5(String header5) {
        this.header5 = header5;
    }

    /**
     * Gets the header6
     * 
     * @return Returns the header6.
     */
    public String getHeader6() {
        return header6;
    }

    /**
     * Sets the header6
     * 
     * @param header6 The header6 to set.
     */
    public void setHeader6(String header6) {
        this.header6 = header6;
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
     * Gets the percentChange
     * 
     * @return Returns the percentChange.
     */
    public BigDecimal getPercentChange() {
        return percentChange;
    }

    /**
     * Sets the percentChange
     * 
     * @param percentChange The percentChange to set.
     */
    public void setPercentChange(BigDecimal percentChange) {
        this.percentChange = percentChange;
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

    public Integer getAccountLineAnnualBalanceAmount() {
        return accountLineAnnualBalanceAmount;
    }

    public void setAccountLineAnnualBalanceAmount(Integer accountLineAnnualBalanceAmount) {
        this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
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

    public BigDecimal getCsfFullTimeEmploymentQuantity() {
        return csfFullTimeEmploymentQuantity;
    }

    public void setCsfFullTimeEmploymentQuantity(BigDecimal csfFullTimeEmploymentQuantity) {
        this.csfFullTimeEmploymentQuantity = csfFullTimeEmploymentQuantity;
    }

    public Integer getDifferenceAccountLineAnnualBalanceAmount() {
        return differenceAccountLineAnnualBalanceAmount;
    }

    public void setDifferenceAccountLineAnnualBalanceAmount(Integer differenceAccountLineAnnualBalanceAmount) {
        this.differenceAccountLineAnnualBalanceAmount = differenceAccountLineAnnualBalanceAmount;
    }

    public Integer getDifferenceAmountChange() {
        return differenceAmountChange;
    }

    public void setDifferenceAmountChange(Integer differenceAmountChange) {
        this.differenceAmountChange = differenceAmountChange;
    }

    public Integer getDifferenceFinancialBeginningBalanceLineAmount() {
        return differenceFinancialBeginningBalanceLineAmount;
    }

    public void setDifferenceFinancialBeginningBalanceLineAmount(Integer differenceFinancialBeginningBalanceLineAmount) {
        this.differenceFinancialBeginningBalanceLineAmount = differenceFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getDifferencePercentChange() {
        return differencePercentChange;
    }

    public void setDifferencePercentChange(BigDecimal differencePercentChange) {
        this.differencePercentChange = differencePercentChange;
    }

    public Integer getExpenditureAccountLineAnnualBalanceAmount() {
        return expenditureAccountLineAnnualBalanceAmount;
    }

    public void setExpenditureAccountLineAnnualBalanceAmount(Integer expenditureAccountLineAnnualBalanceAmount) {
        this.expenditureAccountLineAnnualBalanceAmount = expenditureAccountLineAnnualBalanceAmount;
    }

    public Integer getExpenditureAmountChange() {
        return expenditureAmountChange;
    }

    public void setExpenditureAmountChange(Integer expenditureAmountChange) {
        this.expenditureAmountChange = expenditureAmountChange;
    }

    public Integer getExpenditureFinancialBeginningBalanceLineAmount() {
        return expenditureFinancialBeginningBalanceLineAmount;
    }

    public void setExpenditureFinancialBeginningBalanceLineAmount(Integer expenditureFinancialBeginningBalanceLineAmount) {
        this.expenditureFinancialBeginningBalanceLineAmount = expenditureFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getExpenditurePercentChange() {
        return expenditurePercentChange;
    }

    public void setExpenditurePercentChange(BigDecimal expenditurePercentChange) {
        this.expenditurePercentChange = expenditurePercentChange;
    }

    public Integer getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    public void setFinancialBeginningBalanceLineAmount(Integer financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
    }

    public String getFinancialConsolidationSortCode() {
        return financialConsolidationSortCode;
    }

    public void setFinancialConsolidationSortCode(String financialConsolidationSortCode) {
        this.financialConsolidationSortCode = financialConsolidationSortCode;
    }

    public String getFinancialLevelSortCode() {
        return financialLevelSortCode;
    }

    public void setFinancialLevelSortCode(String financialLevelSortCode) {
        this.financialLevelSortCode = financialLevelSortCode;
    }

    public String getFinancialObjectLevelCode() {
        return financialObjectLevelCode;
    }

    public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
        this.financialObjectLevelCode = financialObjectLevelCode;
    }

    public String getFinancialObjectName() {
        return financialObjectName;
    }

    public void setFinancialObjectName(String financialObjectName) {
        this.financialObjectName = financialObjectName;
    }

    public Integer getGrossAccountLineAnnualBalanceAmount() {
        return grossAccountLineAnnualBalanceAmount;
    }

    public void setGrossAccountLineAnnualBalanceAmount(Integer grossAccountLineAnnualBalanceAmount) {
        this.grossAccountLineAnnualBalanceAmount = grossAccountLineAnnualBalanceAmount;
    }

    public Integer getGrossAmountChange() {
        return grossAmountChange;
    }

    public void setGrossAmountChange(Integer grossAmountChange) {
        this.grossAmountChange = grossAmountChange;
    }

    public String getGrossDescription() {
        return grossDescription;
    }

    public void setGrossDescription(String grossDescription) {
        this.grossDescription = grossDescription;
    }

    public Integer getGrossFinancialBeginningBalanceLineAmount() {
        return grossFinancialBeginningBalanceLineAmount;
    }

    public void setGrossFinancialBeginningBalanceLineAmount(Integer grossFinancialBeginningBalanceLineAmount) {
        this.grossFinancialBeginningBalanceLineAmount = grossFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getGrossPercentChange() {
        return grossPercentChange;
    }

    public void setGrossPercentChange(BigDecimal grossPercentChange) {
        this.grossPercentChange = grossPercentChange;
    }

    public String getHeader2a() {
        return header2a;
    }

    public void setHeader2a(String header2a) {
        this.header2a = header2a;
    }

    public String getHeader31() {
        return header31;
    }

    public void setHeader31(String header31) {
        this.header31 = header31;
    }

    public String getHeader40() {
        return header40;
    }

    public void setHeader40(String header40) {
        this.header40 = header40;
    }

    public String getIncomeExpenseCode() {
        return incomeExpenseCode;
    }

    public void setIncomeExpenseCode(String incomeExpenseCode) {
        this.incomeExpenseCode = incomeExpenseCode;
    }

    public BigDecimal getPositionCsfLeaveFteQuantity() {
        return positionCsfLeaveFteQuantity;
    }

    public void setPositionCsfLeaveFteQuantity(BigDecimal positionCsfLeaveFteQuantity) {
        this.positionCsfLeaveFteQuantity = positionCsfLeaveFteQuantity;
    }

    public Integer getRevenueAccountLineAnnualBalanceAmount() {
        return revenueAccountLineAnnualBalanceAmount;
    }

    public void setRevenueAccountLineAnnualBalanceAmount(Integer revenueAccountLineAnnualBalanceAmount) {
        this.revenueAccountLineAnnualBalanceAmount = revenueAccountLineAnnualBalanceAmount;
    }

    public Integer getRevenueAmountChange() {
        return revenueAmountChange;
    }

    public void setRevenueAmountChange(Integer revenueAmountChange) {
        this.revenueAmountChange = revenueAmountChange;
    }

    public Integer getRevenueFinancialBeginningBalanceLineAmount() {
        return revenueFinancialBeginningBalanceLineAmount;
    }

    public void setRevenueFinancialBeginningBalanceLineAmount(Integer revenueFinancialBeginningBalanceLineAmount) {
        this.revenueFinancialBeginningBalanceLineAmount = revenueFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getRevenuePercentChange() {
        return revenuePercentChange;
    }

    public void setRevenuePercentChange(BigDecimal revenuePercentChange) {
        this.revenuePercentChange = revenuePercentChange;
    }

    public Integer getTotalConsolidationAccountLineAnnualBalanceAmount() {
        return totalConsolidationAccountLineAnnualBalanceAmount;
    }

    public void setTotalConsolidationAccountLineAnnualBalanceAmount(Integer totalConsolidationAccountLineAnnualBalanceAmount) {
        this.totalConsolidationAccountLineAnnualBalanceAmount = totalConsolidationAccountLineAnnualBalanceAmount;
    }

    public Integer getTotalConsolidationAmountChange() {
        return totalConsolidationAmountChange;
    }

    public void setTotalConsolidationAmountChange(Integer totalConsolidationAmountChange) {
        this.totalConsolidationAmountChange = totalConsolidationAmountChange;
    }

    public BigDecimal getTotalConsolidationAppointmentRequestedCsfFteQuantity() {
        return totalConsolidationAppointmentRequestedCsfFteQuantity;
    }

    public void setTotalConsolidationAppointmentRequestedCsfFteQuantity(BigDecimal totalConsolidationAppointmentRequestedCsfFteQuantity) {
        this.totalConsolidationAppointmentRequestedCsfFteQuantity = totalConsolidationAppointmentRequestedCsfFteQuantity;
    }

    public BigDecimal getTotalConsolidationAppointmentRequestedFteQuantity() {
        return totalConsolidationAppointmentRequestedFteQuantity;
    }

    public void setTotalConsolidationAppointmentRequestedFteQuantity(BigDecimal totalConsolidationAppointmentRequestedFteQuantity) {
        this.totalConsolidationAppointmentRequestedFteQuantity = totalConsolidationAppointmentRequestedFteQuantity;
    }

    public String getTotalConsolidationDescription() {
        return totalConsolidationDescription;
    }

    public void setTotalConsolidationDescription(String totalConsolidationDescription) {
        this.totalConsolidationDescription = totalConsolidationDescription;
    }

    public Integer getTotalConsolidationFinancialBeginningBalanceLineAmount() {
        return totalConsolidationFinancialBeginningBalanceLineAmount;
    }

    public void setTotalConsolidationFinancialBeginningBalanceLineAmount(Integer totalConsolidationFinancialBeginningBalanceLineAmount) {
        this.totalConsolidationFinancialBeginningBalanceLineAmount = totalConsolidationFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getTotalConsolidationPercentChange() {
        return totalConsolidationPercentChange;
    }

    public void setTotalConsolidationPercentChange(BigDecimal totalConsolidationPercentChange) {
        this.totalConsolidationPercentChange = totalConsolidationPercentChange;
    }

    public BigDecimal getTotalConsolidationPositionCsfFullTimeEmploymentQuantity() {
        return totalConsolidationPositionCsfFullTimeEmploymentQuantity;
    }

    public void setTotalConsolidationPositionCsfFullTimeEmploymentQuantity(BigDecimal totalConsolidationPositionCsfFullTimeEmploymentQuantity) {
        this.totalConsolidationPositionCsfFullTimeEmploymentQuantity = totalConsolidationPositionCsfFullTimeEmploymentQuantity;
    }

    public BigDecimal getTotalConsolidationPositionCsfLeaveFteQuantity() {
        return totalConsolidationPositionCsfLeaveFteQuantity;
    }

    public void setTotalConsolidationPositionCsfLeaveFteQuantity(BigDecimal totalConsolidationPositionCsfLeaveFteQuantity) {
        this.totalConsolidationPositionCsfLeaveFteQuantity = totalConsolidationPositionCsfLeaveFteQuantity;
    }

    public Integer getTotalLevelAccountLineAnnualBalanceAmount() {
        return totalLevelAccountLineAnnualBalanceAmount;
    }

    public void setTotalLevelAccountLineAnnualBalanceAmount(Integer totalLevelAccountLineAnnualBalanceAmount) {
        this.totalLevelAccountLineAnnualBalanceAmount = totalLevelAccountLineAnnualBalanceAmount;
    }

    public Integer getTotalLevelAmountChange() {
        return totalLevelAmountChange;
    }

    public void setTotalLevelAmountChange(Integer totalLevelAmountChange) {
        this.totalLevelAmountChange = totalLevelAmountChange;
    }

    public BigDecimal getTotalLevelAppointmentRequestedCsfFteQuantity() {
        return totalLevelAppointmentRequestedCsfFteQuantity;
    }

    public void setTotalLevelAppointmentRequestedCsfFteQuantity(BigDecimal totalLevelAppointmentRequestedCsfFteQuantity) {
        this.totalLevelAppointmentRequestedCsfFteQuantity = totalLevelAppointmentRequestedCsfFteQuantity;
    }

    public BigDecimal getTotalLevelAppointmentRequestedFteQuantity() {
        return totalLevelAppointmentRequestedFteQuantity;
    }

    public void setTotalLevelAppointmentRequestedFteQuantity(BigDecimal totalLevelAppointmentRequestedFteQuantity) {
        this.totalLevelAppointmentRequestedFteQuantity = totalLevelAppointmentRequestedFteQuantity;
    }

    public String getTotalLevelDescription() {
        return totalLevelDescription;
    }

    public void setTotalLevelDescription(String totalLevelDescription) {
        this.totalLevelDescription = totalLevelDescription;
    }

    public Integer getTotalLevelFinancialBeginningBalanceLineAmount() {
        return totalLevelFinancialBeginningBalanceLineAmount;
    }

    public void setTotalLevelFinancialBeginningBalanceLineAmount(Integer totalLevelFinancialBeginningBalanceLineAmount) {
        this.totalLevelFinancialBeginningBalanceLineAmount = totalLevelFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getTotalLevelPercentChange() {
        return totalLevelPercentChange;
    }

    public void setTotalLevelPercentChange(BigDecimal totalLevelPercentChange) {
        this.totalLevelPercentChange = totalLevelPercentChange;
    }

    public BigDecimal getTotalLevelPositionCsfFullTimeEmploymentQuantity() {
        return totalLevelPositionCsfFullTimeEmploymentQuantity;
    }

    public void setTotalLevelPositionCsfFullTimeEmploymentQuantity(BigDecimal totalLevelPositionCsfFullTimeEmploymentQuantity) {
        this.totalLevelPositionCsfFullTimeEmploymentQuantity = totalLevelPositionCsfFullTimeEmploymentQuantity;
    }

    public BigDecimal getTotalLevelPositionCsfLeaveFteQuantity() {
        return totalLevelPositionCsfLeaveFteQuantity;
    }

    public void setTotalLevelPositionCsfLeaveFteQuantity(BigDecimal totalLevelPositionCsfLeaveFteQuantity) {
        this.totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity;
    }

    public String getTotalSubFundGroupDesc() {
        return totalSubFundGroupDesc;
    }

    public void setTotalSubFundGroupDesc(String totalSubFundGroupDesc) {
        this.totalSubFundGroupDesc = totalSubFundGroupDesc;
    }

    public Integer getTypeAccountLineAnnualBalanceAmount() {
        return typeAccountLineAnnualBalanceAmount;
    }

    public void setTypeAccountLineAnnualBalanceAmount(Integer typeAccountLineAnnualBalanceAmount) {
        this.typeAccountLineAnnualBalanceAmount = typeAccountLineAnnualBalanceAmount;
    }

    public Integer getTypeAmountChange() {
        return typeAmountChange;
    }

    public void setTypeAmountChange(Integer typeAmountChange) {
        this.typeAmountChange = typeAmountChange;
    }

    public BigDecimal getTypeAppointmentRequestedCsfFteQuantity() {
        return typeAppointmentRequestedCsfFteQuantity;
    }

    public void setTypeAppointmentRequestedCsfFteQuantity(BigDecimal typeAppointmentRequestedCsfFteQuantity) {
        this.typeAppointmentRequestedCsfFteQuantity = typeAppointmentRequestedCsfFteQuantity;
    }

    public BigDecimal getTypeAppointmentRequestedFteQuantity() {
        return typeAppointmentRequestedFteQuantity;
    }

    public void setTypeAppointmentRequestedFteQuantity(BigDecimal typeAppointmentRequestedFteQuantity) {
        this.typeAppointmentRequestedFteQuantity = typeAppointmentRequestedFteQuantity;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public Integer getTypeFinancialBeginningBalanceLineAmount() {
        return typeFinancialBeginningBalanceLineAmount;
    }

    public void setTypeFinancialBeginningBalanceLineAmount(Integer typeFinancialBeginningBalanceLineAmount) {
        this.typeFinancialBeginningBalanceLineAmount = typeFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getTypePercentChange() {
        return typePercentChange;
    }

    public void setTypePercentChange(BigDecimal typePercentChange) {
        this.typePercentChange = typePercentChange;
    }

    public BigDecimal getTypePositionCsfFullTimeEmploymentQuantity() {
        return typePositionCsfFullTimeEmploymentQuantity;
    }

    public void setTypePositionCsfFullTimeEmploymentQuantity(BigDecimal typePositionCsfFullTimeEmploymentQuantity) {
        this.typePositionCsfFullTimeEmploymentQuantity = typePositionCsfFullTimeEmploymentQuantity;
    }

    public BigDecimal getTypePositionCsfLeaveFteQuantity() {
        return typePositionCsfLeaveFteQuantity;
    }

    public void setTypePositionCsfLeaveFteQuantity(BigDecimal typePositionCsfLeaveFteQuantity) {
        this.typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

   }
