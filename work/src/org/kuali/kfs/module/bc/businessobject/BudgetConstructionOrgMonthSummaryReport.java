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


/**
 * Budget Construction Organization Account Summary Report Business Object.
 */
public class BudgetConstructionOrgMonthSummaryReport {

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
   

    // Groups
    //private String financialObjectLevelCode;
    private String incomeExpenseCode;
    private String financialConsolidationSortCode;
    private String financialLevelSortCode;
    private String financialObjectCode;
    private String financialSubObjectCode;
    //private String financialConsolidationSortCode; 
    
    // Body parts
    //private String financialObjectLevelName;
    private String financialObjectCodeName;
    private Integer accountLineAnnualBalanceAmount;
    private Integer financialDocumentMonth1LineAmount;
    private Integer financialDocumentMonth2LineAmount;
    private Integer financialDocumentMonth3LineAmount;
    private Integer financialDocumentMonth4LineAmount;
    private Integer financialDocumentMonth5LineAmount;
    private Integer financialDocumentMonth6LineAmount;
    private Integer financialDocumentMonth7LineAmount;
    private Integer financialDocumentMonth8LineAmount;
    private Integer financialDocumentMonth9LineAmount;
    private Integer financialDocumentMonth10LineAmount;
    private Integer financialDocumentMonth11LineAmount;
    private Integer financialDocumentMonth12LineAmount;
   

    // Total parts
    private String levelTotalDescription;
    private Integer levelAccountLineAnnualBalanceAmount;
    private Integer levelMonth1LineAmount;
    private Integer levelMonth2LineAmount;
    private Integer levelMonth3LineAmount;
    private Integer levelMonth4LineAmount;
    private Integer levelMonth5LineAmount;
    private Integer levelMonth6LineAmount;
    private Integer levelMonth7LineAmount;
    private Integer levelMonth8LineAmount;
    private Integer levelMonth9LineAmount;
    private Integer levelMonth10LineAmount;
    private Integer levelMonth11LineAmount;
    private Integer levelMonth12LineAmount;
    
    private String consTotalDescription;
    private Integer consAccountLineAnnualBalanceAmount;
    private Integer consMonth1LineAmount;
    private Integer consMonth2LineAmount;
    private Integer consMonth3LineAmount;
    private Integer consMonth4LineAmount;
    private Integer consMonth5LineAmount;
    private Integer consMonth6LineAmount;
    private Integer consMonth7LineAmount;
    private Integer consMonth8LineAmount;
    private Integer consMonth9LineAmount;
    private Integer consMonth10LineAmount;
    private Integer consMonth11LineAmount;
    private Integer consMonth12LineAmount;
    
    private String typeTotalDescription;
    private Integer typeAccountLineAnnualBalanceAmount;
    private Integer typeMonth1LineAmount;
    private Integer typeMonth2LineAmount;
    private Integer typeMonth3LineAmount;
    private Integer typeMonth4LineAmount;
    private Integer typeMonth5LineAmount;
    private Integer typeMonth6LineAmount;
    private Integer typeMonth7LineAmount;
    private Integer typeMonth8LineAmount;
    private Integer typeMonth9LineAmount;
    private Integer typeMonth10LineAmount;
    private Integer typeMonth11LineAmount;
    private Integer typeMonth12LineAmount;
    
    private Integer revAccountLineAnnualBalanceAmount;
    private Integer revMonth1LineAmount;
    private Integer revMonth2LineAmount;
    private Integer revMonth3LineAmount;
    private Integer revMonth4LineAmount;
    private Integer revMonth5LineAmount;
    private Integer revMonth6LineAmount;
    private Integer revMonth7LineAmount;
    private Integer revMonth8LineAmount;
    private Integer revMonth9LineAmount;
    private Integer revMonth10LineAmount;
    private Integer revMonth11LineAmount;
    private Integer revMonth12LineAmount;
    
    private Integer expAccountLineAnnualBalanceAmount;
    private Integer expMonth1LineAmount;
    private Integer expMonth2LineAmount;
    private Integer expMonth3LineAmount;
    private Integer expMonth4LineAmount;
    private Integer expMonth5LineAmount;
    private Integer expMonth6LineAmount;
    private Integer expMonth7LineAmount;
    private Integer expMonth8LineAmount;
    private Integer expMonth9LineAmount;
    private Integer expMonth10LineAmount;
    private Integer expMonth11LineAmount;
    private Integer expMonth12LineAmount;
    
    private Integer differenceAccountLineAnnualBalanceAmount;
    private Integer differenceMonth1LineAmount;
    private Integer differenceMonth2LineAmount;
    private Integer differenceMonth3LineAmount;
    private Integer differenceMonth4LineAmount;
    private Integer differenceMonth5LineAmount;
    private Integer differenceMonth6LineAmount;
    private Integer differenceMonth7LineAmount;
    private Integer differenceMonth8LineAmount;
    private Integer differenceMonth9LineAmount;
    private Integer differenceMonth10LineAmount;
    private Integer differenceMonth11LineAmount;
    private Integer differenceMonth12LineAmount;
    

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

    public String getFinancialConsolidationSortCode() {
        return financialConsolidationSortCode;
    }

    public void setFinancialConsolidationSortCode(String financialConsolidationSortCode) {
        this.financialConsolidationSortCode = financialConsolidationSortCode;
    }

    public String getIncomeExpenseCode() {
        return incomeExpenseCode;
    }

    public void setIncomeExpenseCode(String incomeExpenseCode) {
        this.incomeExpenseCode = incomeExpenseCode;
    }

    public Integer getAccountLineAnnualBalanceAmount() {
        return accountLineAnnualBalanceAmount;
    }

    public void setAccountLineAnnualBalanceAmount(Integer accountLineAnnualBalanceAmount) {
        this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
    }

    public Integer getConsMonth10LineAmount() {
        return consMonth10LineAmount;
    }

    public void setConsMonth10LineAmount(Integer consMonth10LineAmount) {
        this.consMonth10LineAmount = consMonth10LineAmount;
    }

    public Integer getConsMonth11LineAmount() {
        return consMonth11LineAmount;
    }

    public void setConsMonth11LineAmount(Integer consMonth11LineAmount) {
        this.consMonth11LineAmount = consMonth11LineAmount;
    }

    public Integer getConsMonth12LineAmount() {
        return consMonth12LineAmount;
    }

    public void setConsMonth12LineAmount(Integer consMonth12LineAmount) {
        this.consMonth12LineAmount = consMonth12LineAmount;
    }

    public Integer getConsMonth1LineAmount() {
        return consMonth1LineAmount;
    }

    public void setConsMonth1LineAmount(Integer consMonth1LineAmount) {
        this.consMonth1LineAmount = consMonth1LineAmount;
    }

    public Integer getConsMonth2LineAmount() {
        return consMonth2LineAmount;
    }

    public void setConsMonth2LineAmount(Integer consMonth2LineAmount) {
        this.consMonth2LineAmount = consMonth2LineAmount;
    }

    public Integer getConsMonth3LineAmount() {
        return consMonth3LineAmount;
    }

    public void setConsMonth3LineAmount(Integer consMonth3LineAmount) {
        this.consMonth3LineAmount = consMonth3LineAmount;
    }

    public Integer getConsMonth4LineAmount() {
        return consMonth4LineAmount;
    }

    public void setConsMonth4LineAmount(Integer consMonth4LineAmount) {
        this.consMonth4LineAmount = consMonth4LineAmount;
    }

    public Integer getConsMonth5LineAmount() {
        return consMonth5LineAmount;
    }

    public void setConsMonth5LineAmount(Integer consMonth5LineAmount) {
        this.consMonth5LineAmount = consMonth5LineAmount;
    }

    public Integer getConsMonth6LineAmount() {
        return consMonth6LineAmount;
    }

    public void setConsMonth6LineAmount(Integer consMonth6LineAmount) {
        this.consMonth6LineAmount = consMonth6LineAmount;
    }

    public Integer getConsMonth7LineAmount() {
        return consMonth7LineAmount;
    }

    public void setConsMonth7LineAmount(Integer consMonth7LineAmount) {
        this.consMonth7LineAmount = consMonth7LineAmount;
    }

    public Integer getConsMonth8LineAmount() {
        return consMonth8LineAmount;
    }

    public void setConsMonth8LineAmount(Integer consMonth8LineAmount) {
        this.consMonth8LineAmount = consMonth8LineAmount;
    }

    public Integer getConsMonth9LineAmount() {
        return consMonth9LineAmount;
    }

    public void setConsMonth9LineAmount(Integer consMonth9LineAmount) {
        this.consMonth9LineAmount = consMonth9LineAmount;
    }

    public String getConsTotalDescription() {
        return consTotalDescription;
    }

    public void setConsTotalDescription(String consTotalDescription) {
        this.consTotalDescription = consTotalDescription;
    }

    public Integer getDifferenceMonth10LineAmount() {
        return differenceMonth10LineAmount;
    }

    public void setDifferenceMonth10LineAmount(Integer differenceMonth10LineAmount) {
        this.differenceMonth10LineAmount = differenceMonth10LineAmount;
    }

    public Integer getDifferenceMonth11LineAmount() {
        return differenceMonth11LineAmount;
    }

    public void setDifferenceMonth11LineAmount(Integer differenceMonth11LineAmount) {
        this.differenceMonth11LineAmount = differenceMonth11LineAmount;
    }

    public Integer getDifferenceMonth12LineAmount() {
        return differenceMonth12LineAmount;
    }

    public void setDifferenceMonth12LineAmount(Integer differenceMonth12LineAmount) {
        this.differenceMonth12LineAmount = differenceMonth12LineAmount;
    }

    public Integer getDifferenceMonth1LineAmount() {
        return differenceMonth1LineAmount;
    }

    public void setDifferenceMonth1LineAmount(Integer differenceMonth1LineAmount) {
        this.differenceMonth1LineAmount = differenceMonth1LineAmount;
    }

    public Integer getDifferenceMonth2LineAmount() {
        return differenceMonth2LineAmount;
    }

    public void setDifferenceMonth2LineAmount(Integer differenceMonth2LineAmount) {
        this.differenceMonth2LineAmount = differenceMonth2LineAmount;
    }

    public Integer getDifferenceMonth3LineAmount() {
        return differenceMonth3LineAmount;
    }

    public void setDifferenceMonth3LineAmount(Integer differenceMonth3LineAmount) {
        this.differenceMonth3LineAmount = differenceMonth3LineAmount;
    }

    public Integer getDifferenceMonth4LineAmount() {
        return differenceMonth4LineAmount;
    }

    public void setDifferenceMonth4LineAmount(Integer differenceMonth4LineAmount) {
        this.differenceMonth4LineAmount = differenceMonth4LineAmount;
    }

    public Integer getDifferenceMonth5LineAmount() {
        return differenceMonth5LineAmount;
    }

    public void setDifferenceMonth5LineAmount(Integer differenceMonth5LineAmount) {
        this.differenceMonth5LineAmount = differenceMonth5LineAmount;
    }

    public Integer getDifferenceMonth6LineAmount() {
        return differenceMonth6LineAmount;
    }

    public void setDifferenceMonth6LineAmount(Integer differenceMonth6LineAmount) {
        this.differenceMonth6LineAmount = differenceMonth6LineAmount;
    }

    public Integer getDifferenceMonth7LineAmount() {
        return differenceMonth7LineAmount;
    }

    public void setDifferenceMonth7LineAmount(Integer differenceMonth7LineAmount) {
        this.differenceMonth7LineAmount = differenceMonth7LineAmount;
    }

    public Integer getDifferenceMonth8LineAmount() {
        return differenceMonth8LineAmount;
    }

    public void setDifferenceMonth8LineAmount(Integer differenceMonth8LineAmount) {
        this.differenceMonth8LineAmount = differenceMonth8LineAmount;
    }

    public Integer getDifferenceMonth9LineAmount() {
        return differenceMonth9LineAmount;
    }

    public void setDifferenceMonth9LineAmount(Integer differenceMonth9LineAmount) {
        this.differenceMonth9LineAmount = differenceMonth9LineAmount;
    }

    public Integer getExpMonth10LineAmount() {
        return expMonth10LineAmount;
    }

    public void setExpMonth10LineAmount(Integer expMonth10LineAmount) {
        this.expMonth10LineAmount = expMonth10LineAmount;
    }

    public Integer getExpMonth11LineAmount() {
        return expMonth11LineAmount;
    }

    public void setExpMonth11LineAmount(Integer expMonth11LineAmount) {
        this.expMonth11LineAmount = expMonth11LineAmount;
    }

    public Integer getExpMonth12LineAmount() {
        return expMonth12LineAmount;
    }

    public void setExpMonth12LineAmount(Integer expMonth12LineAmount) {
        this.expMonth12LineAmount = expMonth12LineAmount;
    }

    public Integer getExpMonth1LineAmount() {
        return expMonth1LineAmount;
    }

    public void setExpMonth1LineAmount(Integer expMonth1LineAmount) {
        this.expMonth1LineAmount = expMonth1LineAmount;
    }

    public Integer getExpMonth2LineAmount() {
        return expMonth2LineAmount;
    }

    public void setExpMonth2LineAmount(Integer expMonth2LineAmount) {
        this.expMonth2LineAmount = expMonth2LineAmount;
    }

    public Integer getExpMonth3LineAmount() {
        return expMonth3LineAmount;
    }

    public void setExpMonth3LineAmount(Integer expMonth3LineAmount) {
        this.expMonth3LineAmount = expMonth3LineAmount;
    }

    public Integer getExpMonth4LineAmount() {
        return expMonth4LineAmount;
    }

    public void setExpMonth4LineAmount(Integer expMonth4LineAmount) {
        this.expMonth4LineAmount = expMonth4LineAmount;
    }

    public Integer getExpMonth5LineAmount() {
        return expMonth5LineAmount;
    }

    public void setExpMonth5LineAmount(Integer expMonth5LineAmount) {
        this.expMonth5LineAmount = expMonth5LineAmount;
    }

    public Integer getExpMonth6LineAmount() {
        return expMonth6LineAmount;
    }

    public void setExpMonth6LineAmount(Integer expMonth6LineAmount) {
        this.expMonth6LineAmount = expMonth6LineAmount;
    }

    public Integer getExpMonth7LineAmount() {
        return expMonth7LineAmount;
    }

    public void setExpMonth7LineAmount(Integer expMonth7LineAmount) {
        this.expMonth7LineAmount = expMonth7LineAmount;
    }

    public Integer getExpMonth8LineAmount() {
        return expMonth8LineAmount;
    }

    public void setExpMonth8LineAmount(Integer expMonth8LineAmount) {
        this.expMonth8LineAmount = expMonth8LineAmount;
    }

    public Integer getExpMonth9LineAmount() {
        return expMonth9LineAmount;
    }

    public void setExpMonth9LineAmount(Integer expMonth9LineAmount) {
        this.expMonth9LineAmount = expMonth9LineAmount;
    }


    public Integer getFinancialDocumentMonth10LineAmount() {
        return financialDocumentMonth10LineAmount;
    }

    public void setFinancialDocumentMonth10LineAmount(Integer financialDocumentMonth10LineAmount) {
        this.financialDocumentMonth10LineAmount = financialDocumentMonth10LineAmount;
    }

    public Integer getFinancialDocumentMonth11LineAmount() {
        return financialDocumentMonth11LineAmount;
    }

    public void setFinancialDocumentMonth11LineAmount(Integer financialDocumentMonth11LineAmount) {
        this.financialDocumentMonth11LineAmount = financialDocumentMonth11LineAmount;
    }

    public Integer getFinancialDocumentMonth12LineAmount() {
        return financialDocumentMonth12LineAmount;
    }

    public void setFinancialDocumentMonth12LineAmount(Integer financialDocumentMonth12LineAmount) {
        this.financialDocumentMonth12LineAmount = financialDocumentMonth12LineAmount;
    }

    public Integer getFinancialDocumentMonth1LineAmount() {
        return financialDocumentMonth1LineAmount;
    }

    public void setFinancialDocumentMonth1LineAmount(Integer financialDocumentMonth1LineAmount) {
        this.financialDocumentMonth1LineAmount = financialDocumentMonth1LineAmount;
    }

    public Integer getFinancialDocumentMonth2LineAmount() {
        return financialDocumentMonth2LineAmount;
    }

    public void setFinancialDocumentMonth2LineAmount(Integer financialDocumentMonth2LineAmount) {
        this.financialDocumentMonth2LineAmount = financialDocumentMonth2LineAmount;
    }

    public Integer getFinancialDocumentMonth3LineAmount() {
        return financialDocumentMonth3LineAmount;
    }

    public void setFinancialDocumentMonth3LineAmount(Integer financialDocumentMonth3LineAmount) {
        this.financialDocumentMonth3LineAmount = financialDocumentMonth3LineAmount;
    }

    public Integer getFinancialDocumentMonth4LineAmount() {
        return financialDocumentMonth4LineAmount;
    }

    public void setFinancialDocumentMonth4LineAmount(Integer financialDocumentMonth4LineAmount) {
        this.financialDocumentMonth4LineAmount = financialDocumentMonth4LineAmount;
    }

    public Integer getFinancialDocumentMonth5LineAmount() {
        return financialDocumentMonth5LineAmount;
    }

    public void setFinancialDocumentMonth5LineAmount(Integer financialDocumentMonth5LineAmount) {
        this.financialDocumentMonth5LineAmount = financialDocumentMonth5LineAmount;
    }

    public Integer getFinancialDocumentMonth6LineAmount() {
        return financialDocumentMonth6LineAmount;
    }

    public void setFinancialDocumentMonth6LineAmount(Integer financialDocumentMonth6LineAmount) {
        this.financialDocumentMonth6LineAmount = financialDocumentMonth6LineAmount;
    }

    public Integer getFinancialDocumentMonth7LineAmount() {
        return financialDocumentMonth7LineAmount;
    }

    public void setFinancialDocumentMonth7LineAmount(Integer financialDocumentMonth7LineAmount) {
        this.financialDocumentMonth7LineAmount = financialDocumentMonth7LineAmount;
    }

    public Integer getFinancialDocumentMonth8LineAmount() {
        return financialDocumentMonth8LineAmount;
    }

    public void setFinancialDocumentMonth8LineAmount(Integer financialDocumentMonth8LineAmount) {
        this.financialDocumentMonth8LineAmount = financialDocumentMonth8LineAmount;
    }

    public Integer getFinancialDocumentMonth9LineAmount() {
        return financialDocumentMonth9LineAmount;
    }

    public void setFinancialDocumentMonth9LineAmount(Integer financialDocumentMonth9LineAmount) {
        this.financialDocumentMonth9LineAmount = financialDocumentMonth9LineAmount;
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

    public Integer getLevelMonth10LineAmount() {
        return levelMonth10LineAmount;
    }

    public void setLevelMonth10LineAmount(Integer levelMonth10LineAmount) {
        this.levelMonth10LineAmount = levelMonth10LineAmount;
    }

    public Integer getLevelMonth11LineAmount() {
        return levelMonth11LineAmount;
    }

    public void setLevelMonth11LineAmount(Integer levelMonth11LineAmount) {
        this.levelMonth11LineAmount = levelMonth11LineAmount;
    }

    public Integer getLevelMonth12LineAmount() {
        return levelMonth12LineAmount;
    }

    public void setLevelMonth12LineAmount(Integer levelMonth12LineAmount) {
        this.levelMonth12LineAmount = levelMonth12LineAmount;
    }

    public Integer getLevelMonth1LineAmount() {
        return levelMonth1LineAmount;
    }

    public void setLevelMonth1LineAmount(Integer levelMonth1LineAmount) {
        this.levelMonth1LineAmount = levelMonth1LineAmount;
    }

    public Integer getLevelMonth2LineAmount() {
        return levelMonth2LineAmount;
    }

    public void setLevelMonth2LineAmount(Integer levelMonth2LineAmount) {
        this.levelMonth2LineAmount = levelMonth2LineAmount;
    }

    public Integer getLevelMonth3LineAmount() {
        return levelMonth3LineAmount;
    }

    public void setLevelMonth3LineAmount(Integer levelMonth3LineAmount) {
        this.levelMonth3LineAmount = levelMonth3LineAmount;
    }

    public Integer getLevelMonth4LineAmount() {
        return levelMonth4LineAmount;
    }

    public void setLevelMonth4LineAmount(Integer levelMonth4LineAmount) {
        this.levelMonth4LineAmount = levelMonth4LineAmount;
    }

    public Integer getLevelMonth5LineAmount() {
        return levelMonth5LineAmount;
    }

    public void setLevelMonth5LineAmount(Integer levelMonth5LineAmount) {
        this.levelMonth5LineAmount = levelMonth5LineAmount;
    }

    public Integer getLevelMonth6LineAmount() {
        return levelMonth6LineAmount;
    }

    public void setLevelMonth6LineAmount(Integer levelMonth6LineAmount) {
        this.levelMonth6LineAmount = levelMonth6LineAmount;
    }

    public Integer getLevelMonth7LineAmount() {
        return levelMonth7LineAmount;
    }

    public void setLevelMonth7LineAmount(Integer levelMonth7LineAmount) {
        this.levelMonth7LineAmount = levelMonth7LineAmount;
    }

    public Integer getLevelMonth8LineAmount() {
        return levelMonth8LineAmount;
    }

    public void setLevelMonth8LineAmount(Integer levelMonth8LineAmount) {
        this.levelMonth8LineAmount = levelMonth8LineAmount;
    }

    public Integer getLevelMonth9LineAmount() {
        return levelMonth9LineAmount;
    }

    public void setLevelMonth9LineAmount(Integer levelMonth9LineAmount) {
        this.levelMonth9LineAmount = levelMonth9LineAmount;
    }

    public String getLevelTotalDescription() {
        return levelTotalDescription;
    }

    public void setLevelTotalDescription(String levelTotalDescription) {
        this.levelTotalDescription = levelTotalDescription;
    }

    public Integer getRevMonth10LineAmount() {
        return revMonth10LineAmount;
    }

    public void setRevMonth10LineAmount(Integer revMonth10LineAmount) {
        this.revMonth10LineAmount = revMonth10LineAmount;
    }

    public Integer getRevMonth11LineAmount() {
        return revMonth11LineAmount;
    }

    public void setRevMonth11LineAmount(Integer revMonth11LineAmount) {
        this.revMonth11LineAmount = revMonth11LineAmount;
    }

    public Integer getRevMonth12LineAmount() {
        return revMonth12LineAmount;
    }

    public void setRevMonth12LineAmount(Integer revMonth12LineAmount) {
        this.revMonth12LineAmount = revMonth12LineAmount;
    }

    public Integer getRevMonth1LineAmount() {
        return revMonth1LineAmount;
    }

    public void setRevMonth1LineAmount(Integer revMonth1LineAmount) {
        this.revMonth1LineAmount = revMonth1LineAmount;
    }

    public Integer getRevMonth2LineAmount() {
        return revMonth2LineAmount;
    }

    public void setRevMonth2LineAmount(Integer revMonth2LineAmount) {
        this.revMonth2LineAmount = revMonth2LineAmount;
    }

    public Integer getRevMonth3LineAmount() {
        return revMonth3LineAmount;
    }

    public void setRevMonth3LineAmount(Integer revMonth3LineAmount) {
        this.revMonth3LineAmount = revMonth3LineAmount;
    }

    public Integer getRevMonth4LineAmount() {
        return revMonth4LineAmount;
    }

    public void setRevMonth4LineAmount(Integer revMonth4LineAmount) {
        this.revMonth4LineAmount = revMonth4LineAmount;
    }

    public Integer getRevMonth5LineAmount() {
        return revMonth5LineAmount;
    }

    public void setRevMonth5LineAmount(Integer revMonth5LineAmount) {
        this.revMonth5LineAmount = revMonth5LineAmount;
    }

    public Integer getRevMonth6LineAmount() {
        return revMonth6LineAmount;
    }

    public void setRevMonth6LineAmount(Integer revMonth6LineAmount) {
        this.revMonth6LineAmount = revMonth6LineAmount;
    }

    public Integer getRevMonth7LineAmount() {
        return revMonth7LineAmount;
    }

    public void setRevMonth7LineAmount(Integer revMonth7LineAmount) {
        this.revMonth7LineAmount = revMonth7LineAmount;
    }

    public Integer getRevMonth8LineAmount() {
        return revMonth8LineAmount;
    }

    public void setRevMonth8LineAmount(Integer revMonth8LineAmount) {
        this.revMonth8LineAmount = revMonth8LineAmount;
    }

    public Integer getRevMonth9LineAmount() {
        return revMonth9LineAmount;
    }

    public void setRevMonth9LineAmount(Integer revMonth9LineAmount) {
        this.revMonth9LineAmount = revMonth9LineAmount;
    }

    public Integer getTypeMonth10LineAmount() {
        return typeMonth10LineAmount;
    }

    public void setTypeMonth10LineAmount(Integer typeMonth10LineAmount) {
        this.typeMonth10LineAmount = typeMonth10LineAmount;
    }

    public Integer getTypeMonth11LineAmount() {
        return typeMonth11LineAmount;
    }

    public void setTypeMonth11LineAmount(Integer typeMonth11LineAmount) {
        this.typeMonth11LineAmount = typeMonth11LineAmount;
    }

    public Integer getTypeMonth12LineAmount() {
        return typeMonth12LineAmount;
    }

    public void setTypeMonth12LineAmount(Integer typeMonth12LineAmount) {
        this.typeMonth12LineAmount = typeMonth12LineAmount;
    }

    public Integer getTypeMonth1LineAmount() {
        return typeMonth1LineAmount;
    }

    public void setTypeMonth1LineAmount(Integer typeMonth1LineAmount) {
        this.typeMonth1LineAmount = typeMonth1LineAmount;
    }

    public Integer getTypeMonth2LineAmount() {
        return typeMonth2LineAmount;
    }

    public void setTypeMonth2LineAmount(Integer typeMonth2LineAmount) {
        this.typeMonth2LineAmount = typeMonth2LineAmount;
    }

    public Integer getTypeMonth3LineAmount() {
        return typeMonth3LineAmount;
    }

    public void setTypeMonth3LineAmount(Integer typeMonth3LineAmount) {
        this.typeMonth3LineAmount = typeMonth3LineAmount;
    }

    public Integer getTypeMonth4LineAmount() {
        return typeMonth4LineAmount;
    }

    public void setTypeMonth4LineAmount(Integer typeMonth4LineAmount) {
        this.typeMonth4LineAmount = typeMonth4LineAmount;
    }

    public Integer getTypeMonth5LineAmount() {
        return typeMonth5LineAmount;
    }

    public void setTypeMonth5LineAmount(Integer typeMonth5LineAmount) {
        this.typeMonth5LineAmount = typeMonth5LineAmount;
    }

    public Integer getTypeMonth6LineAmount() {
        return typeMonth6LineAmount;
    }

    public void setTypeMonth6LineAmount(Integer typeMonth6LineAmount) {
        this.typeMonth6LineAmount = typeMonth6LineAmount;
    }

    public Integer getTypeMonth7LineAmount() {
        return typeMonth7LineAmount;
    }

    public void setTypeMonth7LineAmount(Integer typeMonth7LineAmount) {
        this.typeMonth7LineAmount = typeMonth7LineAmount;
    }

    public Integer getTypeMonth8LineAmount() {
        return typeMonth8LineAmount;
    }

    public void setTypeMonth8LineAmount(Integer typeMonth8LineAmount) {
        this.typeMonth8LineAmount = typeMonth8LineAmount;
    }

    public Integer getTypeMonth9LineAmount() {
        return typeMonth9LineAmount;
    }

    public void setTypeMonth9LineAmount(Integer typeMonth9LineAmount) {
        this.typeMonth9LineAmount = typeMonth9LineAmount;
    }

    public String getTypeTotalDescription() {
        return typeTotalDescription;
    }

    public void setTypeTotalDescription(String typeTotalDescription) {
        this.typeTotalDescription = typeTotalDescription;
    }

    public Integer getConsAccountLineAnnualBalanceAmount() {
        return consAccountLineAnnualBalanceAmount;
    }

    public void setConsAccountLineAnnualBalanceAmount(Integer consAccountLineAnnualBalanceAmount) {
        this.consAccountLineAnnualBalanceAmount = consAccountLineAnnualBalanceAmount;
    }

    public Integer getDifferenceAccountLineAnnualBalanceAmount() {
        return differenceAccountLineAnnualBalanceAmount;
    }

    public void setDifferenceAccountLineAnnualBalanceAmount(Integer differenceAccountLineAnnualBalanceAmount) {
        this.differenceAccountLineAnnualBalanceAmount = differenceAccountLineAnnualBalanceAmount;
    }

    public Integer getExpAccountLineAnnualBalanceAmount() {
        return expAccountLineAnnualBalanceAmount;
    }

    public void setExpAccountLineAnnualBalanceAmount(Integer expAccountLineAnnualBalanceAmount) {
        this.expAccountLineAnnualBalanceAmount = expAccountLineAnnualBalanceAmount;
    }

    public Integer getLevelAccountLineAnnualBalanceAmount() {
        return levelAccountLineAnnualBalanceAmount;
    }

    public void setLevelAccountLineAnnualBalanceAmount(Integer levelAccountLineAnnualBalanceAmount) {
        this.levelAccountLineAnnualBalanceAmount = levelAccountLineAnnualBalanceAmount;
    }

    public Integer getRevAccountLineAnnualBalanceAmount() {
        return revAccountLineAnnualBalanceAmount;
    }

    public void setRevAccountLineAnnualBalanceAmount(Integer revAccountLineAnnualBalanceAmount) {
        this.revAccountLineAnnualBalanceAmount = revAccountLineAnnualBalanceAmount;
    }

    public Integer getTypeAccountLineAnnualBalanceAmount() {
        return typeAccountLineAnnualBalanceAmount;
    }

    public void setTypeAccountLineAnnualBalanceAmount(Integer typeAccountLineAnnualBalanceAmount) {
        this.typeAccountLineAnnualBalanceAmount = typeAccountLineAnnualBalanceAmount;
    }

    public String getFinancialObjectCodeName() {
        return financialObjectCodeName;
    }

    public void setFinancialObjectCodeName(String financialObjectCodeName) {
        this.financialObjectCodeName = financialObjectCodeName;
    }

}
