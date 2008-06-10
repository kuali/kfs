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
public class BudgetConstructionAccountObjectDetailReport {

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
    
    private String typeDesc;
    private String typePositionCsfLeaveFteQuantity;
    private String typePositionFullTimeEquivalencyQuantity;
    private Integer typeFinancialBeginningBalanceLineAmount;
    private String typeAppointmentRequestedCsfFteQuantity;
    private String typeAppointmentRequestedFteQuantity;
    private Integer typeAccountLineAnnualBalanceAmount;
    private Integer typeAmountChange;
    private BigDecimal typePercentChange;
    
    
    public BudgetConstructionAccountObjectDetailReport() {
    }
    
    
    public String getFiscalYear() {
        return fiscalYear;
    }
    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }
    public String getOrgChartOfAccountsCode() {
        return orgChartOfAccountsCode;
    }
    public void setOrgChartOfAccountsCode(String orgChartOfAccountsCode) {
        this.orgChartOfAccountsCode = orgChartOfAccountsCode;
    }
    public String getOrgChartOfAccountDescription() {
        return orgChartOfAccountDescription;
    }
    public void setOrgChartOfAccountDescription(String orgChartOfAccountDescription) {
        this.orgChartOfAccountDescription = orgChartOfAccountDescription;
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
    public String getConsHdr() {
        return consHdr;
    }
    public void setConsHdr(String consHdr) {
        this.consHdr = consHdr;
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
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }
    public String getSubFundGroupDescription() {
        return subFundGroupDescription;
    }
    public void setSubFundGroupDescription(String subFundGroupDescription) {
        this.subFundGroupDescription = subFundGroupDescription;
    }
    public String getBaseFy() {
        return baseFy;
    }
    public void setBaseFy(String baseFy) {
        this.baseFy = baseFy;
    }
    public String getReqFy() {
        return reqFy;
    }
    public void setReqFy(String reqFy) {
        this.reqFy = reqFy;
    }
    public String getHeader1() {
        return header1;
    }
    public void setHeader1(String header1) {
        this.header1 = header1;
    }
    public String getHeader2() {
        return header2;
    }
    public void setHeader2(String header2) {
        this.header2 = header2;
    }
    public String getHeader2a() {
        return header2a;
    }
    public void setHeader2a(String header2a) {
        this.header2a = header2a;
    }
    public String getHeader3() {
        return header3;
    }
    public void setHeader3(String header3) {
        this.header3 = header3;
    }
    public String getHeader31() {
        return header31;
    }
    public void setHeader31(String header31) {
        this.header31 = header31;
    }
    public String getHeader4() {
        return header4;
    }
    public void setHeader4(String header4) {
        this.header4 = header4;
    }
    public String getHeader40() {
        return header40;
    }
    public void setHeader40(String header40) {
        this.header40 = header40;
    }
    public String getHeader5() {
        return header5;
    }
    public void setHeader5(String header5) {
        this.header5 = header5;
    }
    public String getHeader6() {
        return header6;
    }
    public void setHeader6(String header6) {
        this.header6 = header6;
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
    public String getFinancialObjectName() {
        return financialObjectName;
    }
    public void setFinancialObjectName(String financialObjectName) {
        this.financialObjectName = financialObjectName;
    }
    public String getPositionCsfLeaveFteQuantity() {
        return positionCsfLeaveFteQuantity;
    }
    public void setPositionCsfLeaveFteQuantity(String positionCsfLeaveFteQuantity) {
        this.positionCsfLeaveFteQuantity = positionCsfLeaveFteQuantity;
    }
    public String getPositionFullTimeEquivalencyQuantity() {
        return positionFullTimeEquivalencyQuantity;
    }
    public void setPositionFullTimeEquivalencyQuantity(String positionFullTimeEquivalencyQuantity) {
        this.positionFullTimeEquivalencyQuantity = positionFullTimeEquivalencyQuantity;
    }
    public Integer getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }
    public void setFinancialBeginningBalanceLineAmount(Integer financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
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
    public Integer getAccountLineAnnualBalanceAmount() {
        return accountLineAnnualBalanceAmount;
    }
    public void setAccountLineAnnualBalanceAmount(Integer accountLineAnnualBalanceAmount) {
        this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
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
    public String getTotalObjectDescription() {
        return totalObjectDescription;
    }
    public void setTotalObjectDescription(String totalObjectDescription) {
        this.totalObjectDescription = totalObjectDescription;
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
    public Integer getTotalObjectFinancialBeginningBalanceLineAmount() {
        return totalObjectFinancialBeginningBalanceLineAmount;
    }
    public void setTotalObjectFinancialBeginningBalanceLineAmount(Integer totalObjectFinancialBeginningBalanceLineAmount) {
        this.totalObjectFinancialBeginningBalanceLineAmount = totalObjectFinancialBeginningBalanceLineAmount;
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
    public BigDecimal getTotalObjectPercentChange() {
        return totalObjectPercentChange;
    }
    public void setTotalObjectPercentChange(BigDecimal totalObjectPercentChange) {
        this.totalObjectPercentChange = totalObjectPercentChange;
    }
    public String getTotalLevelDescription() {
        return totalLevelDescription;
    }
    public void setTotalLevelDescription(String totalLevelDescription) {
        this.totalLevelDescription = totalLevelDescription;
    }
    public String getTotalLevelPositionCsfLeaveFteQuantity() {
        return totalLevelPositionCsfLeaveFteQuantity;
    }
    public void setTotalLevelPositionCsfLeaveFteQuantity(String totalLevelPositionCsfLeaveFteQuantity) {
        this.totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity;
    }
    public String getTotalLevelPositionFullTimeEquivalencyQuantity() {
        return totalLevelPositionFullTimeEquivalencyQuantity;
    }
    public void setTotalLevelPositionFullTimeEquivalencyQuantity(String totalLevelPositionFullTimeEquivalencyQuantity) {
        this.totalLevelPositionFullTimeEquivalencyQuantity = totalLevelPositionFullTimeEquivalencyQuantity;
    }
    public Integer getTotalLevelFinancialBeginningBalanceLineAmount() {
        return totalLevelFinancialBeginningBalanceLineAmount;
    }
    public void setTotalLevelFinancialBeginningBalanceLineAmount(Integer totalLevelFinancialBeginningBalanceLineAmount) {
        this.totalLevelFinancialBeginningBalanceLineAmount = totalLevelFinancialBeginningBalanceLineAmount;
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
    public BigDecimal getTotalLevelPercentChange() {
        return totalLevelPercentChange;
    }
    public void setTotalLevelPercentChange(BigDecimal totalLevelPercentChange) {
        this.totalLevelPercentChange = totalLevelPercentChange;
    }
    public String getTypeDesc() {
        return typeDesc;
    }
    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }
    public String getTypePositionCsfLeaveFteQuantity() {
        return typePositionCsfLeaveFteQuantity;
    }
    public void setTypePositionCsfLeaveFteQuantity(String typePositionCsfLeaveFteQuantity) {
        this.typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity;
    }
    public String getTypePositionFullTimeEquivalencyQuantity() {
        return typePositionFullTimeEquivalencyQuantity;
    }
    public void setTypePositionFullTimeEquivalencyQuantity(String typePositionFullTimeEquivalencyQuantity) {
        this.typePositionFullTimeEquivalencyQuantity = typePositionFullTimeEquivalencyQuantity;
    }
    public Integer getTypeFinancialBeginningBalanceLineAmount() {
        return typeFinancialBeginningBalanceLineAmount;
    }
    public void setTypeFinancialBeginningBalanceLineAmount(Integer typeFinancialBeginningBalanceLineAmount) {
        this.typeFinancialBeginningBalanceLineAmount = typeFinancialBeginningBalanceLineAmount;
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
    public BigDecimal getTypePercentChange() {
        return typePercentChange;
    }
    public void setTypePercentChange(BigDecimal typePercentChange) {
        this.typePercentChange = typePercentChange;
    }
    
    
}
