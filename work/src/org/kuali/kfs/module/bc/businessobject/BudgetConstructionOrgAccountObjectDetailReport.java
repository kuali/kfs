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
    
    private String accountNumber;
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
    
    private String subAccountNumberAndName;  
    private String accountNumberAndName;
    private String divider; 
    
    // Body parts
    private String financialObjectName;
    
    //when the values are zero, below fields should be blank, so make them as string.
    private BigDecimal positionCsfLeaveFteQuantity;
    private BigDecimal positionFullTimeEquivalencyQuantity;
    private Integer financialBeginningBalanceLineAmount = new Integer(0);
    private BigDecimal appointmentRequestedCsfFteQuantity;
    private BigDecimal appointmentRequestedFteQuantity;
    private Integer accountLineAnnualBalanceAmount = new Integer(0);
    private Integer amountChange = new Integer(0);
    private BigDecimal percentChange = BigDecimal.ZERO;

    // Total parts
    
    private String totalObjectDescription;
    
    private BigDecimal totalObjectPositionCsfLeaveFteQuantity;
    private BigDecimal totalObjectPositionFullTimeEquivalencyQuantity;
    private Integer totalObjectFinancialBeginningBalanceLineAmount;
    private BigDecimal totalObjectAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedFteQuantity;
    private Integer totalObjectAccountLineAnnualBalanceAmount;
    private Integer totalObjectAmountChange;
    private BigDecimal totalObjectPercentChange;
    
    private String totalLevelDescription;
    
    private BigDecimal totalLevelPositionCsfLeaveFteQuantity;
    private BigDecimal totalLevelPositionFullTimeEquivalencyQuantity;
    private Integer totalLevelFinancialBeginningBalanceLineAmount;
    private BigDecimal totalLevelAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalLevelAppointmentRequestedFteQuantity;
    private Integer totalLevelAccountLineAnnualBalanceAmount;
    private Integer totalLevelAmountChange;
    private BigDecimal totalLevelPercentChange;

    private String grossDescription;
    private Integer grossFinancialBeginningBalanceLineAmount;
    private Integer grossAccountLineAnnualBalanceAmount;
    private Integer grossAmountChange;
    private BigDecimal grossPercentChange;
    
    private String typeDesc;
    private BigDecimal typePositionCsfLeaveFteQuantity;
    private BigDecimal typePositionFullTimeEquivalencyQuantity;
    private Integer typeFinancialBeginningBalanceLineAmount;
    private BigDecimal typeAppointmentRequestedCsfFteQuantity;
    private BigDecimal typeAppointmentRequestedFteQuantity;
    private Integer typeAccountLineAnnualBalanceAmount;
    private Integer typeAmountChange;
    private BigDecimal typePercentChange;
    
    // account total
    private String accountNameForAccountTotal;
    private BigDecimal accountPositionCsfLeaveFteQuantity;
    private BigDecimal accountPositionFullTimeEquivalencyQuantity;
    private BigDecimal accountAppointmentRequestedCsfFteQuantity;
    private BigDecimal accountAppointmentRequestedFteQuantity;
    
    private Integer accountRevenueFinancialBeginningBalanceLineAmount;
    private Integer accountRevenueAccountLineAnnualBalanceAmount;
    private Integer accountRevenueAmountChange;
    private BigDecimal accountRevenuePercentChange;
    
    private Integer accountGrossFinancialBeginningBalanceLineAmount;
    private Integer accountGrossAccountLineAnnualBalanceAmount;
    private Integer accountGrossAmountChange;
    private BigDecimal accountGrossPercentChange;
    
    private Integer accountTrnfrInFinancialBeginningBalanceLineAmount;
    private Integer accountTrnfrInAccountLineAnnualBalanceAmount;
    private Integer accountTrnfrInAmountChange;
    private BigDecimal accountTrnfrInPercentChange;
    
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
    private BigDecimal subFundPositionCsfLeaveFteQuantity;
    private BigDecimal subFundPositionFullTimeEquivalencyQuantity;
    private BigDecimal subFundAppointmentRequestedCsfFteQuantity;
    private BigDecimal subFundAppointmentRequestedFteQuantity;
    
    private Integer subFundRevenueFinancialBeginningBalanceLineAmount;
    private Integer subFundRevenueAccountLineAnnualBalanceAmount;
    private Integer subFundRevenueAmountChange;
    private BigDecimal subFundRevenuePercentChange;
    
    private Integer subFundGrossFinancialBeginningBalanceLineAmount;
    private Integer subFundGrossAccountLineAnnualBalanceAmount;
    private Integer subFundGrossAmountChange;
    private BigDecimal subFundGrossPercentChange;
    
    private Integer subFundTrnfrInFinancialBeginningBalanceLineAmount;
    private Integer subFundTrnfrInAccountLineAnnualBalanceAmount;
    private Integer subFundTrnfrInAmountChange;
    private BigDecimal subFundTrnfrInPercentChange;
    
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

    public BigDecimal getAccountAppointmentRequestedCsfFteQuantity() {
        return accountAppointmentRequestedCsfFteQuantity;
    }

    public void setAccountAppointmentRequestedCsfFteQuantity(BigDecimal accountAppointmentRequestedCsfFteQuantity) {
        this.accountAppointmentRequestedCsfFteQuantity = accountAppointmentRequestedCsfFteQuantity;
    }

    public BigDecimal getAccountAppointmentRequestedFteQuantity() {
        return accountAppointmentRequestedFteQuantity;
    }

    public void setAccountAppointmentRequestedFteQuantity(BigDecimal accountAppointmentRequestedFteQuantity) {
        this.accountAppointmentRequestedFteQuantity = accountAppointmentRequestedFteQuantity;
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

    public Integer getAccountGrossAccountLineAnnualBalanceAmount() {
        return accountGrossAccountLineAnnualBalanceAmount;
    }

    public void setAccountGrossAccountLineAnnualBalanceAmount(Integer accountGrossAccountLineAnnualBalanceAmount) {
        this.accountGrossAccountLineAnnualBalanceAmount = accountGrossAccountLineAnnualBalanceAmount;
    }

    public Integer getAccountGrossAmountChange() {
        return accountGrossAmountChange;
    }

    public void setAccountGrossAmountChange(Integer accountGrossAmountChange) {
        this.accountGrossAmountChange = accountGrossAmountChange;
    }

    public Integer getAccountGrossFinancialBeginningBalanceLineAmount() {
        return accountGrossFinancialBeginningBalanceLineAmount;
    }

    public void setAccountGrossFinancialBeginningBalanceLineAmount(Integer accountGrossFinancialBeginningBalanceLineAmount) {
        this.accountGrossFinancialBeginningBalanceLineAmount = accountGrossFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getAccountGrossPercentChange() {
        return accountGrossPercentChange;
    }

    public void setAccountGrossPercentChange(BigDecimal accountGrossPercentChange) {
        this.accountGrossPercentChange = accountGrossPercentChange;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNameForAccountTotal() {
        return accountNameForAccountTotal;
    }

    public void setAccountNameForAccountTotal(String accountNameForAccountTotal) {
        this.accountNameForAccountTotal = accountNameForAccountTotal;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAccountPositionCsfLeaveFteQuantity() {
        return accountPositionCsfLeaveFteQuantity;
    }

    public void setAccountPositionCsfLeaveFteQuantity(BigDecimal accountPositionCsfLeaveFteQuantity) {
        this.accountPositionCsfLeaveFteQuantity = accountPositionCsfLeaveFteQuantity;
    }

    public BigDecimal getAccountPositionFullTimeEquivalencyQuantity() {
        return accountPositionFullTimeEquivalencyQuantity;
    }

    public void setAccountPositionFullTimeEquivalencyQuantity(BigDecimal accountPositionFullTimeEquivalencyQuantity) {
        this.accountPositionFullTimeEquivalencyQuantity = accountPositionFullTimeEquivalencyQuantity;
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

    public Integer getAccountTrnfrInAccountLineAnnualBalanceAmount() {
        return accountTrnfrInAccountLineAnnualBalanceAmount;
    }

    public void setAccountTrnfrInAccountLineAnnualBalanceAmount(Integer accountTrnfrInAccountLineAnnualBalanceAmount) {
        this.accountTrnfrInAccountLineAnnualBalanceAmount = accountTrnfrInAccountLineAnnualBalanceAmount;
    }

    public Integer getAccountTrnfrInAmountChange() {
        return accountTrnfrInAmountChange;
    }

    public void setAccountTrnfrInAmountChange(Integer accountTrnfrInAmountChange) {
        this.accountTrnfrInAmountChange = accountTrnfrInAmountChange;
    }

    public Integer getAccountTrnfrInFinancialBeginningBalanceLineAmount() {
        return accountTrnfrInFinancialBeginningBalanceLineAmount;
    }

    public void setAccountTrnfrInFinancialBeginningBalanceLineAmount(Integer accountTrnfrInFinancialBeginningBalanceLineAmount) {
        this.accountTrnfrInFinancialBeginningBalanceLineAmount = accountTrnfrInFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getAccountTrnfrInPercentChange() {
        return accountTrnfrInPercentChange;
    }

    public void setAccountTrnfrInPercentChange(BigDecimal accountTrnfrInPercentChange) {
        this.accountTrnfrInPercentChange = accountTrnfrInPercentChange;
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

    public Integer getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    public void setFinancialBeginningBalanceLineAmount(Integer financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
    }

    public String getFinancialLevelSortCode() {
        return financialLevelSortCode;
    }

    public void setFinancialLevelSortCode(String financialLevelSortCode) {
        this.financialLevelSortCode = financialLevelSortCode;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialObjectName() {
        return financialObjectName;
    }

    public void setFinancialObjectName(String financialObjectName) {
        this.financialObjectName = financialObjectName;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
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

    public String getIncomeExpenseCode() {
        return incomeExpenseCode;
    }

    public void setIncomeExpenseCode(String incomeExpenseCode) {
        this.incomeExpenseCode = incomeExpenseCode;
    }

    public String getPageBreak() {
        return pageBreak;
    }

    public void setPageBreak(String pageBreak) {
        this.pageBreak = pageBreak;
    }

    public BigDecimal getPositionCsfLeaveFteQuantity() {
        return positionCsfLeaveFteQuantity;
    }

    public void setPositionCsfLeaveFteQuantity(BigDecimal positionCsfLeaveFteQuantity) {
        this.positionCsfLeaveFteQuantity = positionCsfLeaveFteQuantity;
    }

    public BigDecimal getPositionFullTimeEquivalencyQuantity() {
        return positionFullTimeEquivalencyQuantity;
    }

    public void setPositionFullTimeEquivalencyQuantity(BigDecimal positionFullTimeEquivalencyQuantity) {
        this.positionFullTimeEquivalencyQuantity = positionFullTimeEquivalencyQuantity;
    }

    public String getSubAccountName() {
        return subAccountName;
    }

    public void setSubAccountName(String subAccountName) {
        this.subAccountName = subAccountName;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public BigDecimal getSubFundAppointmentRequestedCsfFteQuantity() {
        return subFundAppointmentRequestedCsfFteQuantity;
    }

    public void setSubFundAppointmentRequestedCsfFteQuantity(BigDecimal subFundAppointmentRequestedCsfFteQuantity) {
        this.subFundAppointmentRequestedCsfFteQuantity = subFundAppointmentRequestedCsfFteQuantity;
    }

    public BigDecimal getSubFundAppointmentRequestedFteQuantity() {
        return subFundAppointmentRequestedFteQuantity;
    }

    public void setSubFundAppointmentRequestedFteQuantity(BigDecimal subFundAppointmentRequestedFteQuantity) {
        this.subFundAppointmentRequestedFteQuantity = subFundAppointmentRequestedFteQuantity;
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

    public Integer getSubFundGrossAccountLineAnnualBalanceAmount() {
        return subFundGrossAccountLineAnnualBalanceAmount;
    }

    public void setSubFundGrossAccountLineAnnualBalanceAmount(Integer subFundGrossAccountLineAnnualBalanceAmount) {
        this.subFundGrossAccountLineAnnualBalanceAmount = subFundGrossAccountLineAnnualBalanceAmount;
    }

    public Integer getSubFundGrossAmountChange() {
        return subFundGrossAmountChange;
    }

    public void setSubFundGrossAmountChange(Integer subFundGrossAmountChange) {
        this.subFundGrossAmountChange = subFundGrossAmountChange;
    }

    public Integer getSubFundGrossFinancialBeginningBalanceLineAmount() {
        return subFundGrossFinancialBeginningBalanceLineAmount;
    }

    public void setSubFundGrossFinancialBeginningBalanceLineAmount(Integer subFundGrossFinancialBeginningBalanceLineAmount) {
        this.subFundGrossFinancialBeginningBalanceLineAmount = subFundGrossFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getSubFundGrossPercentChange() {
        return subFundGrossPercentChange;
    }

    public void setSubFundGrossPercentChange(BigDecimal subFundGrossPercentChange) {
        this.subFundGrossPercentChange = subFundGrossPercentChange;
    }

    public String getSubFundGroupDesc() {
        return subFundGroupDesc;
    }

    public void setSubFundGroupDesc(String subFundGroupDesc) {
        this.subFundGroupDesc = subFundGroupDesc;
    }

    public BigDecimal getSubFundPositionCsfLeaveFteQuantity() {
        return subFundPositionCsfLeaveFteQuantity;
    }

    public void setSubFundPositionCsfLeaveFteQuantity(BigDecimal subFundPositionCsfLeaveFteQuantity) {
        this.subFundPositionCsfLeaveFteQuantity = subFundPositionCsfLeaveFteQuantity;
    }

    public BigDecimal getSubFundPositionFullTimeEquivalencyQuantity() {
        return subFundPositionFullTimeEquivalencyQuantity;
    }

    public void setSubFundPositionFullTimeEquivalencyQuantity(BigDecimal subFundPositionFullTimeEquivalencyQuantity) {
        this.subFundPositionFullTimeEquivalencyQuantity = subFundPositionFullTimeEquivalencyQuantity;
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

    public Integer getSubFundTrnfrInAccountLineAnnualBalanceAmount() {
        return subFundTrnfrInAccountLineAnnualBalanceAmount;
    }

    public void setSubFundTrnfrInAccountLineAnnualBalanceAmount(Integer subFundTrnfrInAccountLineAnnualBalanceAmount) {
        this.subFundTrnfrInAccountLineAnnualBalanceAmount = subFundTrnfrInAccountLineAnnualBalanceAmount;
    }

    public Integer getSubFundTrnfrInAmountChange() {
        return subFundTrnfrInAmountChange;
    }

    public void setSubFundTrnfrInAmountChange(Integer subFundTrnfrInAmountChange) {
        this.subFundTrnfrInAmountChange = subFundTrnfrInAmountChange;
    }

    public Integer getSubFundTrnfrInFinancialBeginningBalanceLineAmount() {
        return subFundTrnfrInFinancialBeginningBalanceLineAmount;
    }

    public void setSubFundTrnfrInFinancialBeginningBalanceLineAmount(Integer subFundTrnfrInFinancialBeginningBalanceLineAmount) {
        this.subFundTrnfrInFinancialBeginningBalanceLineAmount = subFundTrnfrInFinancialBeginningBalanceLineAmount;
    }

    public BigDecimal getSubFundTrnfrInPercentChange() {
        return subFundTrnfrInPercentChange;
    }

    public void setSubFundTrnfrInPercentChange(BigDecimal subFundTrnfrInPercentChange) {
        this.subFundTrnfrInPercentChange = subFundTrnfrInPercentChange;
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

    public BigDecimal getTotalLevelPositionCsfLeaveFteQuantity() {
        return totalLevelPositionCsfLeaveFteQuantity;
    }

    public void setTotalLevelPositionCsfLeaveFteQuantity(BigDecimal totalLevelPositionCsfLeaveFteQuantity) {
        this.totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity;
    }

    public BigDecimal getTotalLevelPositionFullTimeEquivalencyQuantity() {
        return totalLevelPositionFullTimeEquivalencyQuantity;
    }

    public void setTotalLevelPositionFullTimeEquivalencyQuantity(BigDecimal totalLevelPositionFullTimeEquivalencyQuantity) {
        this.totalLevelPositionFullTimeEquivalencyQuantity = totalLevelPositionFullTimeEquivalencyQuantity;
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

    public BigDecimal getTotalObjectAppointmentRequestedCsfFteQuantity() {
        return totalObjectAppointmentRequestedCsfFteQuantity;
    }

    public void setTotalObjectAppointmentRequestedCsfFteQuantity(BigDecimal totalObjectAppointmentRequestedCsfFteQuantity) {
        this.totalObjectAppointmentRequestedCsfFteQuantity = totalObjectAppointmentRequestedCsfFteQuantity;
    }

    public BigDecimal getTotalObjectAppointmentRequestedFteQuantity() {
        return totalObjectAppointmentRequestedFteQuantity;
    }

    public void setTotalObjectAppointmentRequestedFteQuantity(BigDecimal totalObjectAppointmentRequestedFteQuantity) {
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

    public BigDecimal getTotalObjectPositionCsfLeaveFteQuantity() {
        return totalObjectPositionCsfLeaveFteQuantity;
    }

    public void setTotalObjectPositionCsfLeaveFteQuantity(BigDecimal totalObjectPositionCsfLeaveFteQuantity) {
        this.totalObjectPositionCsfLeaveFteQuantity = totalObjectPositionCsfLeaveFteQuantity;
    }

    public BigDecimal getTotalObjectPositionFullTimeEquivalencyQuantity() {
        return totalObjectPositionFullTimeEquivalencyQuantity;
    }

    public void setTotalObjectPositionFullTimeEquivalencyQuantity(BigDecimal totalObjectPositionFullTimeEquivalencyQuantity) {
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

    public BigDecimal getTypePositionCsfLeaveFteQuantity() {
        return typePositionCsfLeaveFteQuantity;
    }

    public void setTypePositionCsfLeaveFteQuantity(BigDecimal typePositionCsfLeaveFteQuantity) {
        this.typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity;
    }

    public BigDecimal getTypePositionFullTimeEquivalencyQuantity() {
        return typePositionFullTimeEquivalencyQuantity;
    }

    public void setTypePositionFullTimeEquivalencyQuantity(BigDecimal typePositionFullTimeEquivalencyQuantity) {
        this.typePositionFullTimeEquivalencyQuantity = typePositionFullTimeEquivalencyQuantity;
    }

    /**
     * Gets the subAccountNumberAndName attribute. 
     * @return Returns the subAccountNumberAndName.
     */
    public String getSubAccountNumberAndName() {
        return subAccountNumberAndName;
    }

    /**
     * Sets the subAccountNumberAndName attribute value.
     * @param subAccountNumberAndName The subAccountNumberAndName to set.
     */
    public void setSubAccountNumberAndName(String subAccountNumberAndName) {
        this.subAccountNumberAndName = subAccountNumberAndName;
    }

    /**
     * Gets the accountNumberAndName attribute. 
     * @return Returns the accountNumberAndName.
     */
    public String getAccountNumberAndName() {
        return accountNumberAndName;
    }

    /**
     * Sets the accountNumberAndName attribute value.
     * @param accountNumberAndName The accountNumberAndName to set.
     */
    public void setAccountNumberAndName(String accountNumberAndName) {
        this.accountNumberAndName = accountNumberAndName;
    }

    /**
     * Gets the divider attribute. 
     * @return Returns the divider.
     */
    public String getDivider() {
        return divider;
    }

    /**
     * Sets the divider attribute value.
     * @param divider The divider to set.
     */
    public void setDivider(String divider) {
        this.divider = divider;
    }

}
