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
 * Budget Construction Organization SubFund Summary Report Business Object.
 */
public class BudgetConstructionOrgSubFundSummaryReport {

    // Header parts
    private String fiscalYear;
    private String orgChartOfAccountsCode;
    private String orgChartOfAccountDescription;
    private String organizationCode;
    private String organizationName;
    private String chartOfAccountsCode;
    private String chartOfAccountDescription;
    
    private String consHdr;
    private String fundGroupCode;
    private String fundGroupName;
    private String subFundGroupCode;
    private String subFundGroupDescription;
    private String baseFy;
    private String reqFy;
    private String header1;
    private String header2;
    private String header3;
    private String header4;
    private String header5;
    private String header6;

    // Body parts
    private String incExpDesc;
    private Integer baseAmount;
    private Integer reqAmount;
    private Integer amountChange;
    private BigDecimal percentChange = BigDecimal.ZERO;

    // Total parts
    private Integer subFundTotalRevenueBaseAmount = 0;
    private Integer subFundTotalRevenueReqAmount = 0;
    private Integer subFundTotalRevenueAmountChange = 0;
    private BigDecimal subFundTotalRevenuePercentChange = BigDecimal.ZERO;
    
    private Integer totalRevenueBaseAmount = 0;
    private Integer totalGrossBaseAmount = 0;
    private Integer totalTransferInBaseAmount = 0;
    private Integer totalNetTransferBaseAmount = 0;

    private Integer totalRevenueReqAmount = 0;
    private Integer totalGrossReqAmount = 0;
    private Integer totalTransferInReqAmount = 0;
    private Integer totalNetTransferReqAmount = 0;

    private Integer totalRevenueAmountChange = 0;
    private Integer totalGrossAmountChange = 0;
    private Integer totalTransferAmountChange = 0;
    private Integer totalNetTransferAmountChange = 0;

    private BigDecimal totalRevenuePercentChange = BigDecimal.ZERO;
    private BigDecimal totalGrossPercentChange = BigDecimal.ZERO;
    private BigDecimal totalTransferInPercentChange = BigDecimal.ZERO;
    private BigDecimal totalNetTransferPercentChange = BigDecimal.ZERO;

    private Integer revExpDifferenceBaseAmount = 0;
    private Integer revExpDifferenceReqAmount = 0;
    private Integer revExpDifferenceAmountChange = 0;
    private BigDecimal revExpDifferencePercentChange = BigDecimal.ZERO;

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
     * Gets the baseAmount
     * 
     * @return Returns the baseAmount.
     */
    public Integer getBaseAmount() {
        return baseAmount;
    }

    /**
     * Sets the baseAmount
     * 
     * @param baseAmount The baseAmount to set.
     */
    public void setBaseAmount(Integer baseAmount) {
        this.baseAmount = baseAmount;
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
     * Gets the incExpDesc
     * 
     * @return Returns the incExpDesc.
     */
    public String getIncExpDesc() {
        return incExpDesc;
    }

    /**
     * Sets the incExpDesc
     * 
     * @param incExpDesc The incExpDesc to set.
     */
    public void setIncExpDesc(String incExpDesc) {
        this.incExpDesc = incExpDesc;
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
     * Gets the reqAmount
     * 
     * @return Returns the reqAmount.
     */
    public Integer getReqAmount() {
        return reqAmount;
    }

    /**
     * Sets the reqAmount
     * 
     * @param reqAmount The reqAmount to set.
     */
    public void setReqAmount(Integer reqAmount) {
        this.reqAmount = reqAmount;
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
     * Gets the revExpDifferenceAmountChange
     * 
     * @return Returns the revExpDifferenceAmountChange.
     */
    public Integer getRevExpDifferenceAmountChange() {
        return revExpDifferenceAmountChange;
    }

    /**
     * Sets the revExpDifferenceAmountChange
     * 
     * @param revExpDifferenceAmountChange The revExpDifferenceAmountChange to set.
     */
    public void setRevExpDifferenceAmountChange(Integer revExpDifferenceAmountChange) {
        this.revExpDifferenceAmountChange = revExpDifferenceAmountChange;
    }

    /**
     * Gets the revExpDifferenceBaseAmount
     * 
     * @return Returns the revExpDifferenceBaseAmount.
     */
    public Integer getRevExpDifferenceBaseAmount() {
        return revExpDifferenceBaseAmount;
    }

    /**
     * Sets the revExpDifferenceBaseAmount
     * 
     * @param revExpDifferenceBaseAmount The revExpDifferenceBaseAmount to set.
     */
    public void setRevExpDifferenceBaseAmount(Integer revExpDifferenceBaseAmount) {
        this.revExpDifferenceBaseAmount = revExpDifferenceBaseAmount;
    }

    /**
     * Gets the revExpDifferencePercentChange
     * 
     * @return Returns the revExpDifferencePercentChange.
     */
    public BigDecimal getRevExpDifferencePercentChange() {
        return revExpDifferencePercentChange;
    }

    /**
     * Sets the revExpDifferencePercentChange
     * 
     * @param revExpDifferencePercentChange The revExpDifferencePercentChange to set.
     */
    public void setRevExpDifferencePercentChange(BigDecimal revExpDifferencePercentChange) {
        this.revExpDifferencePercentChange = revExpDifferencePercentChange;
    }

    /**
     * Gets the revExpDifferenceReqAmount
     * 
     * @return Returns the revExpDifferenceReqAmount.
     */
    public Integer getRevExpDifferenceReqAmount() {
        return revExpDifferenceReqAmount;
    }

    /**
     * Sets the revExpDifferenceReqAmount
     * 
     * @param revExpDifferenceReqAmount The revExpDifferenceReqAmount to set.
     */
    public void setRevExpDifferenceReqAmount(Integer revExpDifferenceReqAmount) {
        this.revExpDifferenceReqAmount = revExpDifferenceReqAmount;
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
     * Gets the totalGrossAmountChange
     * 
     * @return Returns the totalGrossAmountChange.
     */
    public Integer getTotalGrossAmountChange() {
        return totalGrossAmountChange;
    }

    /**
     * Sets the totalGrossAmountChange
     * 
     * @param totalGrossAmountChange The totalGrossAmountChange to set.
     */
    public void setTotalGrossAmountChange(Integer totalGrossAmountChange) {
        this.totalGrossAmountChange = totalGrossAmountChange;
    }

    /**
     * Gets the totalGrossBaseAmount
     * 
     * @return Returns the totalGrossBaseAmount.
     */
    public Integer getTotalGrossBaseAmount() {
        return totalGrossBaseAmount;
    }

    /**
     * Sets the totalGrossBaseAmount
     * 
     * @param totalGrossBaseAmount The totalGrossBaseAmount to set.
     */
    public void setTotalGrossBaseAmount(Integer totalGrossBaseAmount) {
        this.totalGrossBaseAmount = totalGrossBaseAmount;
    }

    /**
     * Gets the totalGrossPercentChange
     * 
     * @return Returns the totalGrossPercentChange.
     */
    public BigDecimal getTotalGrossPercentChange() {
        return totalGrossPercentChange;
    }

    /**
     * Sets the totalGrossPercentChange
     * 
     * @param totalGrossPercentChange The totalGrossPercentChange to set.
     */
    public void setTotalGrossPercentChange(BigDecimal totalGrossPercentChange) {
        this.totalGrossPercentChange = totalGrossPercentChange;
    }

    /**
     * Gets the totalGrossReqAmount
     * 
     * @return Returns the totalGrossReqAmount.
     */
    public Integer getTotalGrossReqAmount() {
        return totalGrossReqAmount;
    }

    /**
     * Sets the totalGrossReqAmount
     * 
     * @param totalGrossReqAmount The totalGrossReqAmount to set.
     */
    public void setTotalGrossReqAmount(Integer totalGrossReqAmount) {
        this.totalGrossReqAmount = totalGrossReqAmount;
    }

    /**
     * Gets the totalNetTransferAmountChange
     * 
     * @return Returns the totalNetTransferAmountChange.
     */
    public Integer getTotalNetTransferAmountChange() {
        return totalNetTransferAmountChange;
    }

    /**
     * Sets the totalNetTransferAmountChange
     * 
     * @param totalNetTransferAmountChange The totalNetTransferAmountChange to set.
     */
    public void setTotalNetTransferAmountChange(Integer totalNetTransferAmountChange) {
        this.totalNetTransferAmountChange = totalNetTransferAmountChange;
    }

    /**
     * Gets the totalNetTransferBaseAmount
     * 
     * @return Returns the totalNetTransferBaseAmount.
     */
    public Integer getTotalNetTransferBaseAmount() {
        return totalNetTransferBaseAmount;
    }

    /**
     * Sets the totalNetTransferBaseAmount
     * 
     * @param totalNetTransferBaseAmount The totalNetTransferBaseAmount to set.
     */
    public void setTotalNetTransferBaseAmount(Integer totalNetTransferBaseAmount) {
        this.totalNetTransferBaseAmount = totalNetTransferBaseAmount;
    }

    /**
     * Gets the totalNetTransferPercentChange
     * 
     * @return Returns the totalNetTransferPercentChange.
     */
    public BigDecimal getTotalNetTransferPercentChange() {
        return totalNetTransferPercentChange;
    }

    /**
     * Sets the totalNetTransferPercentChange
     * 
     * @param totalNetTransferPercentChange The totalNetTransferPercentChange to set.
     */
    public void setTotalNetTransferPercentChange(BigDecimal totalNetTransferPercentChange) {
        this.totalNetTransferPercentChange = totalNetTransferPercentChange;
    }

    /**
     * Gets the totalNetTransferReqAmount
     * 
     * @return Returns the totalNetTransferReqAmount.
     */
    public Integer getTotalNetTransferReqAmount() {
        return totalNetTransferReqAmount;
    }

    /**
     * Sets the totalNetTransferReqAmount
     * 
     * @param totalNetTransferReqAmount The totalNetTransferReqAmount to set.
     */
    public void setTotalNetTransferReqAmount(Integer totalNetTransferReqAmount) {
        this.totalNetTransferReqAmount = totalNetTransferReqAmount;
    }

    /**
     * Gets the totalRevenueAmountChange
     * 
     * @return Returns the totalRevenueAmountChange.
     */
    public Integer getTotalRevenueAmountChange() {
        return totalRevenueAmountChange;
    }

    /**
     * Sets the totalRevenueAmountChange
     * 
     * @param totalRevenueAmountChange The totalRevenueAmountChange to set.
     */
    public void setTotalRevenueAmountChange(Integer totalRevenueAmountChange) {
        this.totalRevenueAmountChange = totalRevenueAmountChange;
    }

    /**
     * Gets the totalRevenueBaseAmount
     * 
     * @return Returns the totalRevenueBaseAmount.
     */
    public Integer getTotalRevenueBaseAmount() {
        return totalRevenueBaseAmount;
    }

    /**
     * Sets the totalRevenueBaseAmount
     * 
     * @param totalRevenueBaseAmount The totalRevenueBaseAmount to set.
     */
    public void setTotalRevenueBaseAmount(Integer totalRevenueBaseAmount) {
        this.totalRevenueBaseAmount = totalRevenueBaseAmount;
    }

    /**
     * Gets the totalRevenuePercentChange
     * 
     * @return Returns the totalRevenuePercentChange.
     */
    public BigDecimal getTotalRevenuePercentChange() {
        return totalRevenuePercentChange;
    }

    /**
     * Sets the totalRevenuePercentChange
     * 
     * @param totalRevenuePercentChange The totalRevenuePercentChange to set.
     */
    public void setTotalRevenuePercentChange(BigDecimal totalRevenuePercentChange) {
        this.totalRevenuePercentChange = totalRevenuePercentChange;
    }

    /**
     * Gets the payrollEndDateFiscalPeriod
     * 
     * @return Returns the payrollEndDateFiscalPeriod.
     */
    public Integer getTotalRevenueReqAmount() {
        return totalRevenueReqAmount;
    }

    /**
     * Sets the payrollEndDateFiscalPeriod
     * 
     * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
     */
    public void setTotalRevenueReqAmount(Integer totalRevenueReqAmount) {
        this.totalRevenueReqAmount = totalRevenueReqAmount;
    }

    /**
     * Gets the totalTransferAmountChange
     * 
     * @return Returns the totalTransferAmountChange.
     */
    public Integer getTotalTransferAmountChange() {
        return totalTransferAmountChange;
    }

    /**
     * Sets the totalTransferAmountChange
     * 
     * @param totalTransferAmountChange The totalTransferAmountChange to set.
     */
    public void setTotalTransferAmountChange(Integer totalTransferAmountChange) {
        this.totalTransferAmountChange = totalTransferAmountChange;
    }

    /**
     * Gets the totalTransferInBaseAmount
     * 
     * @return Returns the totalTransferInBaseAmount.
     */
    public Integer getTotalTransferInBaseAmount() {
        return totalTransferInBaseAmount;
    }

    /**
     * Sets the totalTransferInBaseAmount
     * 
     * @param totalTransferInBaseAmount The totalTransferInBaseAmount to set.
     */
    public void setTotalTransferInBaseAmount(Integer totalTransferInBaseAmount) {
        this.totalTransferInBaseAmount = totalTransferInBaseAmount;
    }

    /**
     * Gets the totalTransferInPercentChange
     * 
     * @return Returns the totalTransferInPercentChange.
     */
    public BigDecimal getTotalTransferInPercentChange() {
        return totalTransferInPercentChange;
    }

    /**
     * Sets the totalTransferInPercentChange
     * 
     * @param totalTransferInPercentChange The totalTransferInPercentChange to set.
     */
    public void setTotalTransferInPercentChange(BigDecimal totalTransferInPercentChange) {
        this.totalTransferInPercentChange = totalTransferInPercentChange;
    }

    /**
     * Gets the totalTransferInReqAmount
     * 
     * @return Returns the totalTransferInReqAmount.
     */
    public Integer getTotalTransferInReqAmount() {
        return totalTransferInReqAmount;
    }

    /**
     * Sets the totalTransferInReqAmount
     * 
     * @param totalTransferInReqAmount The totalTransferInReqAmount to set.
     */
    public void setTotalTransferInReqAmount(Integer totalTransferInReqAmount) {
        this.totalTransferInReqAmount = totalTransferInReqAmount;
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

    /**
     * Gets the subFundTotalRevenueAmountChange
     * 
     * @return Returns the subFundTotalRevenueAmountChange.
     */
    public Integer getSubFundTotalRevenueAmountChange() {
        return subFundTotalRevenueAmountChange;
    }

    /**
     * Sets the subFundTotalRevenueAmountChange
     * 
     * @param subFundTotalRevenueAmountChange The subFundTotalRevenueAmountChange to set.
     */
    public void setSubFundTotalRevenueAmountChange(Integer subFundTotalRevenueAmountChange) {
        this.subFundTotalRevenueAmountChange = subFundTotalRevenueAmountChange;
    }

    /**
     * Gets the subFundTotalRevenueBaseAmount
     * 
     * @return Returns the subFundTotalRevenueBaseAmount.
     */
    public Integer getSubFundTotalRevenueBaseAmount() {
        return subFundTotalRevenueBaseAmount;
    }

    /**
     * Sets the subFundTotalRevenueBaseAmount
     * 
     * @param subFundTotalRevenueBaseAmount The subFundTotalRevenueBaseAmount to set.
     */
    public void setSubFundTotalRevenueBaseAmount(Integer subFundTotalRevenueBaseAmount) {
        this.subFundTotalRevenueBaseAmount = subFundTotalRevenueBaseAmount;
    }

    /**
     * Gets the subFundTotalRevenuePercentChange
     * 
     * @return Returns the subFundTotalRevenuePercentChange.
     */
    public BigDecimal getSubFundTotalRevenuePercentChange() {
        return subFundTotalRevenuePercentChange;
    }

    /**
     * Sets the subFundTotalRevenuePercentChange
     * 
     * @param subFundTotalRevenuePercentChange The subFundTotalRevenuePercentChange to set.
     */
    public void setSubFundTotalRevenuePercentChange(BigDecimal subFundTotalRevenuePercentChange) {
        this.subFundTotalRevenuePercentChange = subFundTotalRevenuePercentChange;
    }

    /**
     * Gets the subFundTotalRevenueReqAmount
     * 
     * @return Returns the subFundTotalRevenueReqAmount.
     */
    public Integer getSubFundTotalRevenueReqAmount() {
        return subFundTotalRevenueReqAmount;
    }

    /**
     * Sets the subFundTotalRevenueReqAmount
     * 
     * @param subFundTotalRevenueReqAmount The subFundTotalRevenueReqAmount to set.
     */
    public void setSubFundTotalRevenueReqAmount(Integer subFundTotalRevenueReqAmount) {
        this.subFundTotalRevenueReqAmount = subFundTotalRevenueReqAmount;
    }

}
