/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.bo;

import java.math.BigDecimal;

/**
 * Budget Construction Organization Account Summary Report Business Object.
 */
public class BudgetConstructionOrgAccountObjectDetailReport {

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
    private String accountName;
    private String subAccountName;
    
    // Groups
    private String subAccountNumber;
    private String incomeExpenseCode;
    private String financialLevelSortCode;
    private String financialObjectCode;
    private String financialSubObjectCode;
    // page break org_fin_coa_cd, org_cd, sub_fund_grp_cd)%\
    private String pageBreak;
   
    
    // Body parts
    private String financialObjectName;
    
    //when the values are zero, below fields should be blank, so make them as string.
    private String positionCsfLeaveFteQuantity;
    private String positionFullTimeEquivalencyQuantity;
    private Integer financialBeginningBalanceLineAmount = new Integer(0);
    private String appointmentRequestedCsfFteQuantity;
    private String appointmentRequestedFteQuantity;
    private Integer accountLineAnnualBalanceAmount = new Integer(0);
    private Integer amountChange = new Integer(0);
    private BigDecimal percentChange = BigDecimal.ZERO;

    // Total parts
    
    private String totalObjectDescription;
    
    private String totalObjectPositionCsfLeaveFteQuantity;
    private String totalObjectPositionFullTimeEquivalencyQuantity;
    private Integer totalObjectFinancialBeginningBalanceLineAmount;
    private String totalObjectAppointmentRequestedCsfFteQuantity;
    private String totalObjectAppointmentRequestedFteQuantity;
    private Integer totalObjectAccountLineAnnualBalanceAmount;
    private Integer totalObjectAmountChange;
    private BigDecimal totalObjectPercentChange;
    
    private String totalLevelDescription;
    
    private String totalLevelPositionCsfLeaveFteQuantity;
    private String totalLevelPositionFullTimeEquivalencyQuantity;
    private Integer totalLevelFinancialBeginningBalanceLineAmount;
    private String totalLevelAppointmentRequestedCsfFteQuantity;
    private String totalLevelAppointmentRequestedFteQuantity;
    private Integer totalLevelAccountLineAnnualBalanceAmount;
    private Integer totalLevelAmountChange;
    private BigDecimal totalLevelPercentChange;

    private String grossDescription;
    private Integer grossFinancialBeginningBalanceLineAmount;
    private Integer grossAccountLineAnnualBalanceAmount;
    private Integer grossAmountChange;
    private BigDecimal grossPercentChange;
    
    private String typeDesc;
    private String typePositionCsfLeaveFteQuantity;
    private String typePositionFullTimeEquivalencyQuantity;
    private Integer typeFinancialBeginningBalanceLineAmount;
    private String typeAppointmentRequestedCsfFteQuantity;
    private String typeAppointmentRequestedFteQuantity;
    private Integer typeAccountLineAnnualBalanceAmount;
    private Integer typeAmountChange;
    private BigDecimal typePercentChange;
    
    // account total
    private Integer accountRevenueFinancialBeginningBalanceLineAmount;
    private Integer accountRevenueAccountLineAnnualBalanceAmount;
    private Integer accountRevenueAmountChange;
    private BigDecimal accountRevenuePercentChange;
    
    private Integer accountExpenditureFinancialBeginningBalanceLineAmount;
    private Integer accountExpenditureAccountLineAnnualBalanceAmount;
    private Integer accountExpenditureAmountChange;
    private BigDecimal accountExpenditurePercentChange;
    
    private Integer accountDifferenceFinancialBeginningBalanceLineAmount;
    private Integer accountDifferenceAccountLineAnnualBalanceAmount;
    private Integer accountDifferenceAmountChange;
    private BigDecimal accountDifferencePercentChange;

    //subFund total
    private String subFundGroupDesc;
    
    private Integer subFundRevenueFinancialBeginningBalanceLineAmount;
    private Integer subFundRevenueAccountLineAnnualBalanceAmount;
    private Integer subFundRevenueAmountChange;
    private BigDecimal subFundRevenuePercentChange;
    
    private Integer subFundExpenditureFinancialBeginningBalanceLineAmount;
    private Integer subFundExpenditureAccountLineAnnualBalanceAmount;
    private Integer subFundExpenditureAmountChange;
    private BigDecimal subFundExpenditurePercentChange;
    
    private Integer subFundDifferenceFinancialBeginningBalanceLineAmount;
    private Integer subFundDifferenceAccountLineAnnualBalanceAmount;
    private Integer subFundDifferenceAmountChange;
    private BigDecimal subFundDifferencePercentChange;
    
    
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

    public String getAppointmentRequestedCsfFteQuantity() {
        return appointmentRequestedCsfFteQuantity;
    }

    public void setAppointmentRequestedCsfFteQuantity(String appointmentRequestedCsfFteQuantity) {
        this.appointmentRequestedCsfFteQuantity = appointmentRequestedCsfFteQuantity;
    }

    public String getAppointmentRequestedFteQuantity() {
        return appointmentRequestedFteQuantity;
    }

    public void setAppointmentRequestedFteQuantity(String appointmentRequestedFteQuantity) {
        this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
    }

   

    public Integer getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    public void setFinancialBeginningBalanceLineAmount(Integer financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
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

    public Integer getGrossFinancialBeginningBalanceLineAmount() {
        return grossFinancialBeginningBalanceLineAmount;
    }

    public void setGrossFinancialBeginningBalanceLineAmount(Integer grossFinancialBeginningBalanceLineAmount) {
        this.grossFinancialBeginningBalanceLineAmount = grossFinancialBeginningBalanceLineAmount;
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

    public String getPositionFullTimeEquivalencyQuantity() {
        return positionFullTimeEquivalencyQuantity;
    }

    public void setPositionFullTimeEquivalencyQuantity(String positionFullTimeEquivalencyQuantity) {
        this.positionFullTimeEquivalencyQuantity = positionFullTimeEquivalencyQuantity;
    }

    public String getPositionCsfLeaveFteQuantity() {
        return positionCsfLeaveFteQuantity;
    }

    public void setPositionCsfLeaveFteQuantity(String positionCsfLeaveFteQuantity) {
        this.positionCsfLeaveFteQuantity = positionCsfLeaveFteQuantity;
    }

   

    public Integer getAccountDifferenceAccountLineAnnualBalanceAmount() {
        return accountDifferenceAccountLineAnnualBalanceAmount;
    }

    public void setAccountDifferenceAccountLineAnnualBalanceAmount(Integer accountDifferenceAccountLineAnnualBalanceAmount) {
        this.accountDifferenceAccountLineAnnualBalanceAmount = accountDifferenceAccountLineAnnualBalanceAmount;
    }

    public Integer getAccountDifferenceAmountChange() {
        return accountDifferenceAmountChange;
    }

    public void setAccountDifferenceAmountChange(Integer accountDifferenceAmountChange) {
        this.accountDifferenceAmountChange = accountDifferenceAmountChange;
    }

    public Integer getAccountDifferenceFinancialBeginningBalanceLineAmount() {
        return accountDifferenceFinancialBeginningBalanceLineAmount;
    }

    public void setAccountDifferenceFinancialBeginningBalanceLineAmount(Integer accountDifferenceFinancialBeginningBalanceLineAmount) {
        this.accountDifferenceFinancialBeginningBalanceLineAmount = accountDifferenceFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getAccountDifferencePercentChange() {
        return accountDifferencePercentChange;
    }

    public void setAccountDifferencePercentChange(BigDecimal accountDifferencePercentChange) {
        this.accountDifferencePercentChange = accountDifferencePercentChange;
    }

    public Integer getAccountExpenditureAccountLineAnnualBalanceAmount() {
        return accountExpenditureAccountLineAnnualBalanceAmount;
    }

    public void setAccountExpenditureAccountLineAnnualBalanceAmount(Integer accountExpenditureAccountLineAnnualBalanceAmount) {
        this.accountExpenditureAccountLineAnnualBalanceAmount = accountExpenditureAccountLineAnnualBalanceAmount;
    }

    public Integer getAccountExpenditureAmountChange() {
        return accountExpenditureAmountChange;
    }

    public void setAccountExpenditureAmountChange(Integer accountExpenditureAmountChange) {
        this.accountExpenditureAmountChange = accountExpenditureAmountChange;
    }

    public Integer getAccountExpenditureFinancialBeginningBalanceLineAmount() {
        return accountExpenditureFinancialBeginningBalanceLineAmount;
    }

    public void setAccountExpenditureFinancialBeginningBalanceLineAmount(Integer accountExpenditureFinancialBeginningBalanceLineAmount) {
        this.accountExpenditureFinancialBeginningBalanceLineAmount = accountExpenditureFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getAccountExpenditurePercentChange() {
        return accountExpenditurePercentChange;
    }

    public void setAccountExpenditurePercentChange(BigDecimal accountExpenditurePercentChange) {
        this.accountExpenditurePercentChange = accountExpenditurePercentChange;
    }

    public Integer getAccountRevenueAccountLineAnnualBalanceAmount() {
        return accountRevenueAccountLineAnnualBalanceAmount;
    }

    public void setAccountRevenueAccountLineAnnualBalanceAmount(Integer accountRevenueAccountLineAnnualBalanceAmount) {
        this.accountRevenueAccountLineAnnualBalanceAmount = accountRevenueAccountLineAnnualBalanceAmount;
    }

    public Integer getAccountRevenueAmountChange() {
        return accountRevenueAmountChange;
    }

    public void setAccountRevenueAmountChange(Integer accountRevenueAmountChange) {
        this.accountRevenueAmountChange = accountRevenueAmountChange;
    }

    public Integer getAccountRevenueFinancialBeginningBalanceLineAmount() {
        return accountRevenueFinancialBeginningBalanceLineAmount;
    }

    public void setAccountRevenueFinancialBeginningBalanceLineAmount(Integer accountRevenueFinancialBeginningBalanceLineAmount) {
        this.accountRevenueFinancialBeginningBalanceLineAmount = accountRevenueFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getAccountRevenuePercentChange() {
        return accountRevenuePercentChange;
    }

    public void setAccountRevenuePercentChange(BigDecimal accountRevenuePercentChange) {
        this.accountRevenuePercentChange = accountRevenuePercentChange;
    }


    public Integer getSubFundDifferenceAccountLineAnnualBalanceAmount() {
        return subFundDifferenceAccountLineAnnualBalanceAmount;
    }

    public void setSubFundDifferenceAccountLineAnnualBalanceAmount(Integer subFundDifferenceAccountLineAnnualBalanceAmount) {
        this.subFundDifferenceAccountLineAnnualBalanceAmount = subFundDifferenceAccountLineAnnualBalanceAmount;
    }

    public Integer getSubFundDifferenceAmountChange() {
        return subFundDifferenceAmountChange;
    }

    public void setSubFundDifferenceAmountChange(Integer subFundDifferenceAmountChange) {
        this.subFundDifferenceAmountChange = subFundDifferenceAmountChange;
    }

    public Integer getSubFundDifferenceFinancialBeginningBalanceLineAmount() {
        return subFundDifferenceFinancialBeginningBalanceLineAmount;
    }

    public void setSubFundDifferenceFinancialBeginningBalanceLineAmount(Integer subFundDifferenceFinancialBeginningBalanceLineAmount) {
        this.subFundDifferenceFinancialBeginningBalanceLineAmount = subFundDifferenceFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getSubFundDifferencePercentChange() {
        return subFundDifferencePercentChange;
    }

    public void setSubFundDifferencePercentChange(BigDecimal subFundDifferencePercentChange) {
        this.subFundDifferencePercentChange = subFundDifferencePercentChange;
    }

    public Integer getSubFundExpenditureAccountLineAnnualBalanceAmount() {
        return subFundExpenditureAccountLineAnnualBalanceAmount;
    }

    public void setSubFundExpenditureAccountLineAnnualBalanceAmount(Integer subFundExpenditureAccountLineAnnualBalanceAmount) {
        this.subFundExpenditureAccountLineAnnualBalanceAmount = subFundExpenditureAccountLineAnnualBalanceAmount;
    }

    public Integer getSubFundExpenditureAmountChange() {
        return subFundExpenditureAmountChange;
    }

    public void setSubFundExpenditureAmountChange(Integer subFundExpenditureAmountChange) {
        this.subFundExpenditureAmountChange = subFundExpenditureAmountChange;
    }

    public Integer getSubFundExpenditureFinancialBeginningBalanceLineAmount() {
        return subFundExpenditureFinancialBeginningBalanceLineAmount;
    }

    public void setSubFundExpenditureFinancialBeginningBalanceLineAmount(Integer subFundExpenditureFinancialBeginningBalanceLineAmount) {
        this.subFundExpenditureFinancialBeginningBalanceLineAmount = subFundExpenditureFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getSubFundExpenditurePercentChange() {
        return subFundExpenditurePercentChange;
    }

    public void setSubFundExpenditurePercentChange(BigDecimal subFundExpenditurePercentChange) {
        this.subFundExpenditurePercentChange = subFundExpenditurePercentChange;
    }

    public Integer getSubFundRevenueAccountLineAnnualBalanceAmount() {
        return subFundRevenueAccountLineAnnualBalanceAmount;
    }

    public void setSubFundRevenueAccountLineAnnualBalanceAmount(Integer subFundRevenueAccountLineAnnualBalanceAmount) {
        this.subFundRevenueAccountLineAnnualBalanceAmount = subFundRevenueAccountLineAnnualBalanceAmount;
    }

    public Integer getSubFundRevenueAmountChange() {
        return subFundRevenueAmountChange;
    }

    public void setSubFundRevenueAmountChange(Integer subFundRevenueAmountChange) {
        this.subFundRevenueAmountChange = subFundRevenueAmountChange;
    }

    public Integer getSubFundRevenueFinancialBeginningBalanceLineAmount() {
        return subFundRevenueFinancialBeginningBalanceLineAmount;
    }

    public void setSubFundRevenueFinancialBeginningBalanceLineAmount(Integer subFundRevenueFinancialBeginningBalanceLineAmount) {
        this.subFundRevenueFinancialBeginningBalanceLineAmount = subFundRevenueFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getSubFundRevenuePercentChange() {
        return subFundRevenuePercentChange;
    }

    public void setSubFundRevenuePercentChange(BigDecimal subFundRevenuePercentChange) {
        this.subFundRevenuePercentChange = subFundRevenuePercentChange;
    }

    public Integer getTotalObjectAccountLineAnnualBalanceAmount() {
        return totalObjectAccountLineAnnualBalanceAmount;
    }

    public void setTotalObjectAccountLineAnnualBalanceAmount(Integer totalObjectAccountLineAnnualBalanceAmount) {
        this.totalObjectAccountLineAnnualBalanceAmount = totalObjectAccountLineAnnualBalanceAmount;
    }

    public Integer getTotalObjectAmountChange() {
        return totalObjectAmountChange;
    }

    public void setTotalObjectAmountChange(Integer totalObjectAmountChange) {
        this.totalObjectAmountChange = totalObjectAmountChange;
    }

    public String getTotalObjectAppointmentRequestedCsfFteQuantity() {
        return totalObjectAppointmentRequestedCsfFteQuantity;
    }

    public void setTotalObjectAppointmentRequestedCsfFteQuantity(String totalObjectAppointmentRequestedCsfFteQuantity) {
        this.totalObjectAppointmentRequestedCsfFteQuantity = totalObjectAppointmentRequestedCsfFteQuantity;
    }

    public String getTotalObjectAppointmentRequestedFteQuantity() {
        return totalObjectAppointmentRequestedFteQuantity;
    }

    public void setTotalObjectAppointmentRequestedFteQuantity(String totalObjectAppointmentRequestedFteQuantity) {
        this.totalObjectAppointmentRequestedFteQuantity = totalObjectAppointmentRequestedFteQuantity;
    }

    public String getTotalObjectDescription() {
        return totalObjectDescription;
    }

    public void setTotalObjectDescription(String totalObjectDescription) {
        this.totalObjectDescription = totalObjectDescription;
    }

    public Integer getTotalObjectFinancialBeginningBalanceLineAmount() {
        return totalObjectFinancialBeginningBalanceLineAmount;
    }

    public void setTotalObjectFinancialBeginningBalanceLineAmount(Integer totalObjectFinancialBeginningBalanceLineAmount) {
        this.totalObjectFinancialBeginningBalanceLineAmount = totalObjectFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getTotalObjectPercentChange() {
        return totalObjectPercentChange;
    }

    public void setTotalObjectPercentChange(BigDecimal totalObjectPercentChange) {
        this.totalObjectPercentChange = totalObjectPercentChange;
    }

    public String getTotalObjectPositionCsfLeaveFteQuantity() {
        return totalObjectPositionCsfLeaveFteQuantity;
    }

    public void setTotalObjectPositionCsfLeaveFteQuantity(String totalObjectPositionCsfLeaveFteQuantity) {
        this.totalObjectPositionCsfLeaveFteQuantity = totalObjectPositionCsfLeaveFteQuantity;
    }

    public String getTotalObjectPositionFullTimeEquivalencyQuantity() {
        return totalObjectPositionFullTimeEquivalencyQuantity;
    }

    public void setTotalObjectPositionFullTimeEquivalencyQuantity(String totalObjectPositionFullTimeEquivalencyQuantity) {
        this.totalObjectPositionFullTimeEquivalencyQuantity = totalObjectPositionFullTimeEquivalencyQuantity;
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

    public String getTypeAppointmentRequestedCsfFteQuantity() {
        return typeAppointmentRequestedCsfFteQuantity;
    }

    public void setTypeAppointmentRequestedCsfFteQuantity(String typeAppointmentRequestedCsfFteQuantity) {
        this.typeAppointmentRequestedCsfFteQuantity = typeAppointmentRequestedCsfFteQuantity;
    }

    public String getTypeAppointmentRequestedFteQuantity() {
        return typeAppointmentRequestedFteQuantity;
    }

    public void setTypeAppointmentRequestedFteQuantity(String typeAppointmentRequestedFteQuantity) {
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

    public String getTypePositionFullTimeEquivalencyQuantity() {
        return typePositionFullTimeEquivalencyQuantity;
    }

    public void setTypePositionFullTimeEquivalencyQuantity(String typePositionFullTimeEquivalencyQuantity) {
        this.typePositionFullTimeEquivalencyQuantity = typePositionFullTimeEquivalencyQuantity;
    }

    public String getTypePositionCsfLeaveFteQuantity() {
        return typePositionCsfLeaveFteQuantity;
    }

    public void setTypePositionCsfLeaveFteQuantity(String typePositionCsfLeaveFteQuantity) {
        this.typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity;
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

    public String getSubFundGroupDesc() {
        return subFundGroupDesc;
    }

    public void setSubFundGroupDesc(String subFundGroupDesc) {
        this.subFundGroupDesc = subFundGroupDesc;
    }

    public String getGrossDescription() {
        return grossDescription;
    }

    public void setGrossDescription(String grossDescription) {
        this.grossDescription = grossDescription;
    }

    public Integer getGrossAmountChange() {
        return grossAmountChange;
    }

    public void setGrossAmountChange(Integer grossAmountChange) {
        this.grossAmountChange = grossAmountChange;
    }

    public BigDecimal getGrossPercentChange() {
        return grossPercentChange;
    }

    public void setGrossPercentChange(BigDecimal grossPercentChange) {
        this.grossPercentChange = grossPercentChange;
    }


    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getIncomeExpenseCode() {
        return incomeExpenseCode;
    }

    public void setIncomeExpenseCode(String incomeExpenseCode) {
        this.incomeExpenseCode = incomeExpenseCode;
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

    public String getTotalLevelAppointmentRequestedCsfFteQuantity() {
        return totalLevelAppointmentRequestedCsfFteQuantity;
    }

    public void setTotalLevelAppointmentRequestedCsfFteQuantity(String totalLevelAppointmentRequestedCsfFteQuantity) {
        this.totalLevelAppointmentRequestedCsfFteQuantity = totalLevelAppointmentRequestedCsfFteQuantity;
    }

    public String getTotalLevelAppointmentRequestedFteQuantity() {
        return totalLevelAppointmentRequestedFteQuantity;
    }

    public void setTotalLevelAppointmentRequestedFteQuantity(String totalLevelAppointmentRequestedFteQuantity) {
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

    public String getTotalLevelPositionFullTimeEquivalencyQuantity() {
        return totalLevelPositionFullTimeEquivalencyQuantity;
    }

    public void setTotalLevelPositionFullTimeEquivalencyQuantity(String totalLevelPositionFullTimeEquivalencyQuantity) {
        this.totalLevelPositionFullTimeEquivalencyQuantity = totalLevelPositionFullTimeEquivalencyQuantity;
    }

    public String getTotalLevelPositionCsfLeaveFteQuantity() {
        return totalLevelPositionCsfLeaveFteQuantity;
    }

    public void setTotalLevelPositionCsfLeaveFteQuantity(String totalLevelPositionCsfLeaveFteQuantity) {
        this.totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity;
    }

    public String getFinancialLevelSortCode() {
        return financialLevelSortCode;
    }

    public void setFinancialLevelSortCode(String financialLevelSortCode) {
        this.financialLevelSortCode = financialLevelSortCode;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getPageBreak() {
        return pageBreak;
    }

    public void setPageBreak(String pageBreak) {
        this.pageBreak = pageBreak;
    }

}
