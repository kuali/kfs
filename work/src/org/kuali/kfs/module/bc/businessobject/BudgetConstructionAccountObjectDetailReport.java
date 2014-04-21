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

import org.kuali.rice.core.api.util.type.KualiInteger;

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

    private String accountNumber;
    private String accountName;
    private String subAccountName;

    // Groups
    private String subAccountNumber;
    private String typeFinancialReportSortCode;
    private String levelFinancialReportSortCode;
    private String financialObjectCode;
    private String financialSubObjectCode;
    // page break org_fin_coa_cd, org_cd, sub_fund_grp_cd)%\
    private String pageBreak;


    // Body parts
    private String financialObjectName;

    // when the values are zero, below fields should be blank, so make them as string.
    private BigDecimal positionCsfLeaveFteQuantity;
    private BigDecimal csfFullTimeEmploymentQuantity;
    private KualiInteger financialBeginningBalanceLineAmount = KualiInteger.ZERO;
    private BigDecimal appointmentRequestedCsfFteQuantity;
    private BigDecimal appointmentRequestedFteQuantity;
    private KualiInteger accountLineAnnualBalanceAmount = KualiInteger.ZERO;
    private KualiInteger amountChange = KualiInteger.ZERO;
    private BigDecimal percentChange = BigDecimal.ZERO;

    // Total parts

    private String totalObjectDescription;

    private BigDecimal totalObjectPositionCsfLeaveFteQuantity;
    private BigDecimal totalObjectCsfFullTimeEmploymentQuantity;
    private KualiInteger totalObjectFinancialBeginningBalanceLineAmount;
    private BigDecimal totalObjectAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalObjectAppointmentRequestedFteQuantity;
    private KualiInteger totalObjectAccountLineAnnualBalanceAmount;
    private KualiInteger totalObjectAmountChange;
    private BigDecimal totalObjectPercentChange;

    private String totalLevelDescription;

    private BigDecimal totalLevelPositionCsfLeaveFteQuantity;
    private BigDecimal totalLevelCsfFullTimeEmploymentQuantity;
    private KualiInteger totalLevelFinancialBeginningBalanceLineAmount;
    private BigDecimal totalLevelAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalLevelAppointmentRequestedFteQuantity;
    private KualiInteger totalLevelAccountLineAnnualBalanceAmount;
    private KualiInteger totalLevelAmountChange;
    private BigDecimal totalLevelPercentChange;

    private String totalTypeDescription;
    private BigDecimal totalTypePositionCsfLeaveFteQuantity;
    private BigDecimal totalTypeCsfFullTimeEmploymentQuantity;
    private KualiInteger totalTypeFinancialBeginningBalanceLineAmount;
    private BigDecimal totalTypeAppointmentRequestedCsfFteQuantity;
    private BigDecimal totalTypeAppointmentRequestedFteQuantity;
    private KualiInteger totalTypeAccountLineAnnualBalanceAmount;
    private KualiInteger totalTypeAmountChange;
    private BigDecimal totalTypePercentChange;


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

    public String getAccountNumber() {
        return accountNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
    public String getTypeFinancialReportSortCode() {
        return typeFinancialReportSortCode;
    }


    public void setTypeFinancialReportSortCode(String typeFinancialReportSortCode) {
        this.typeFinancialReportSortCode = typeFinancialReportSortCode;
    }


    public String getLevelFinancialReportSortCode() {
        return levelFinancialReportSortCode;
    }


    public void setLevelFinancialReportSortCode(String levelFinancialReportSortCode) {
        this.levelFinancialReportSortCode = levelFinancialReportSortCode;
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


    public BigDecimal getPositionCsfLeaveFteQuantity() {
        return positionCsfLeaveFteQuantity;
    }


    public void setPositionCsfLeaveFteQuantity(BigDecimal positionCsfLeaveFteQuantity) {
        this.positionCsfLeaveFteQuantity = positionCsfLeaveFteQuantity;
    }


    public BigDecimal getCsfFullTimeEmploymentQuantity() {
        return csfFullTimeEmploymentQuantity;
    }


    public void setCsfFullTimeEmploymentQuantity(BigDecimal csfFullTimeEmploymentQuantity) {
        this.csfFullTimeEmploymentQuantity = csfFullTimeEmploymentQuantity;
    }


    public KualiInteger getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }


    public void setFinancialBeginningBalanceLineAmount(KualiInteger financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
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


    public KualiInteger getAccountLineAnnualBalanceAmount() {
        return accountLineAnnualBalanceAmount;
    }


    public void setAccountLineAnnualBalanceAmount(KualiInteger accountLineAnnualBalanceAmount) {
        this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
    }


    public KualiInteger getAmountChange() {
        return amountChange;
    }


    public void setAmountChange(KualiInteger amountChange) {
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


    public BigDecimal getTotalObjectPositionCsfLeaveFteQuantity() {
        return totalObjectPositionCsfLeaveFteQuantity;
    }


    public void setTotalObjectPositionCsfLeaveFteQuantity(BigDecimal totalObjectPositionCsfLeaveFteQuantity) {
        this.totalObjectPositionCsfLeaveFteQuantity = totalObjectPositionCsfLeaveFteQuantity;
    }


    public KualiInteger getTotalObjectFinancialBeginningBalanceLineAmount() {
        return totalObjectFinancialBeginningBalanceLineAmount;
    }


    public void setTotalObjectFinancialBeginningBalanceLineAmount(KualiInteger totalObjectFinancialBeginningBalanceLineAmount) {
        this.totalObjectFinancialBeginningBalanceLineAmount = totalObjectFinancialBeginningBalanceLineAmount;
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


    public KualiInteger getTotalObjectAccountLineAnnualBalanceAmount() {
        return totalObjectAccountLineAnnualBalanceAmount;
    }


    public void setTotalObjectAccountLineAnnualBalanceAmount(KualiInteger totalObjectAccountLineAnnualBalanceAmount) {
        this.totalObjectAccountLineAnnualBalanceAmount = totalObjectAccountLineAnnualBalanceAmount;
    }


    public KualiInteger getTotalObjectAmountChange() {
        return totalObjectAmountChange;
    }


    public void setTotalObjectAmountChange(KualiInteger totalObjectAmountChange) {
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


    public BigDecimal getTotalLevelPositionCsfLeaveFteQuantity() {
        return totalLevelPositionCsfLeaveFteQuantity;
    }


    public void setTotalLevelPositionCsfLeaveFteQuantity(BigDecimal totalLevelPositionCsfLeaveFteQuantity) {
        this.totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity;
    }


    public KualiInteger getTotalLevelFinancialBeginningBalanceLineAmount() {
        return totalLevelFinancialBeginningBalanceLineAmount;
    }


    public void setTotalLevelFinancialBeginningBalanceLineAmount(KualiInteger totalLevelFinancialBeginningBalanceLineAmount) {
        this.totalLevelFinancialBeginningBalanceLineAmount = totalLevelFinancialBeginningBalanceLineAmount;
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


    public KualiInteger getTotalLevelAccountLineAnnualBalanceAmount() {
        return totalLevelAccountLineAnnualBalanceAmount;
    }


    public void setTotalLevelAccountLineAnnualBalanceAmount(KualiInteger totalLevelAccountLineAnnualBalanceAmount) {
        this.totalLevelAccountLineAnnualBalanceAmount = totalLevelAccountLineAnnualBalanceAmount;
    }


    public KualiInteger getTotalLevelAmountChange() {
        return totalLevelAmountChange;
    }


    public void setTotalLevelAmountChange(KualiInteger totalLevelAmountChange) {
        this.totalLevelAmountChange = totalLevelAmountChange;
    }


    public BigDecimal getTotalLevelPercentChange() {
        return totalLevelPercentChange;
    }


    public void setTotalLevelPercentChange(BigDecimal totalLevelPercentChange) {
        this.totalLevelPercentChange = totalLevelPercentChange;
    }


    public String getTotalTypeDescription() {
        return totalTypeDescription;
    }


    public void setTotalTypeDescription(String totalTypeDescription) {
        this.totalTypeDescription = totalTypeDescription;
    }


    public BigDecimal getTotalObjectCsfFullTimeEmploymentQuantity() {
        return totalObjectCsfFullTimeEmploymentQuantity;
    }


    public void setTotalObjectCsfFullTimeEmploymentQuantity(BigDecimal totalObjectCsfFullTimeEmploymentQuantity) {
        this.totalObjectCsfFullTimeEmploymentQuantity = totalObjectCsfFullTimeEmploymentQuantity;
    }


    public BigDecimal getTotalLevelCsfFullTimeEmploymentQuantity() {
        return totalLevelCsfFullTimeEmploymentQuantity;
    }


    public void setTotalLevelCsfFullTimeEmploymentQuantity(BigDecimal totalLevelCsfFullTimeEmploymentQuantity) {
        this.totalLevelCsfFullTimeEmploymentQuantity = totalLevelCsfFullTimeEmploymentQuantity;
    }


    public BigDecimal getTotalTypePositionCsfLeaveFteQuantity() {
        return totalTypePositionCsfLeaveFteQuantity;
    }


    public void setTotalTypePositionCsfLeaveFteQuantity(BigDecimal totalTypePositionCsfLeaveFteQuantity) {
        this.totalTypePositionCsfLeaveFteQuantity = totalTypePositionCsfLeaveFteQuantity;
    }


    public BigDecimal getTotalTypeCsfFullTimeEmploymentQuantity() {
        return totalTypeCsfFullTimeEmploymentQuantity;
    }


    public void setTotalTypeCsfFullTimeEmploymentQuantity(BigDecimal totalTypeCsfFullTimeEmploymentQuantity) {
        this.totalTypeCsfFullTimeEmploymentQuantity = totalTypeCsfFullTimeEmploymentQuantity;
    }


    public KualiInteger getTotalTypeFinancialBeginningBalanceLineAmount() {
        return totalTypeFinancialBeginningBalanceLineAmount;
    }


    public void setTotalTypeFinancialBeginningBalanceLineAmount(KualiInteger totalTypeFinancialBeginningBalanceLineAmount) {
        this.totalTypeFinancialBeginningBalanceLineAmount = totalTypeFinancialBeginningBalanceLineAmount;
    }


    public BigDecimal getTotalTypeAppointmentRequestedCsfFteQuantity() {
        return totalTypeAppointmentRequestedCsfFteQuantity;
    }


    public void setTotalTypeAppointmentRequestedCsfFteQuantity(BigDecimal totalTypeAppointmentRequestedCsfFteQuantity) {
        this.totalTypeAppointmentRequestedCsfFteQuantity = totalTypeAppointmentRequestedCsfFteQuantity;
    }


    public BigDecimal getTotalTypeAppointmentRequestedFteQuantity() {
        return totalTypeAppointmentRequestedFteQuantity;
    }


    public void setTotalTypeAppointmentRequestedFteQuantity(BigDecimal totalTypeAppointmentRequestedFteQuantity) {
        this.totalTypeAppointmentRequestedFteQuantity = totalTypeAppointmentRequestedFteQuantity;
    }


    public KualiInteger getTotalTypeAccountLineAnnualBalanceAmount() {
        return totalTypeAccountLineAnnualBalanceAmount;
    }


    public void setTotalTypeAccountLineAnnualBalanceAmount(KualiInteger totalTypeAccountLineAnnualBalanceAmount) {
        this.totalTypeAccountLineAnnualBalanceAmount = totalTypeAccountLineAnnualBalanceAmount;
    }


    public KualiInteger getTotalTypeAmountChange() {
        return totalTypeAmountChange;
    }


    public void setTotalTypeAmountChange(KualiInteger totalTypeAmountChange) {
        this.totalTypeAmountChange = totalTypeAmountChange;
    }


    public BigDecimal getTotalTypePercentChange() {
        return totalTypePercentChange;
    }


    public void setTotalTypePercentChange(BigDecimal totalTypePercentChange) {
        this.totalTypePercentChange = totalTypePercentChange;
    }


}
