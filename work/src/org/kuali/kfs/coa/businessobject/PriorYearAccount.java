/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.bo;

import java.sql.Timestamp;

/**
 * This class...
 * @author Bin Gao from Michigan State University
 */
public class PriorYearAccount {
    
    private String accountCityName;
    private boolean accountClosedIndicator;
    private Timestamp accountCreateDate;
    private String accountCustomIndCstRcvyExclCd;
    private Timestamp accountEffectiveDate;
    private Timestamp accountExpirationDate;
    private boolean accountFringesBenefitIndicator;
    private String accountIndirectCostRcvyTypeCd;
    private boolean accountInFinancialProcessingIndicator;
    private String accountManagerSystemIdentifier;
    private String accountName;
    private String accountNumber;
    private boolean accountOffCampusIndicator;
    private boolean accountPendingSufficientFundIndicator;
    private String accountPhysicalCampusCode;
    private String accountRestrictedStatusCode;
    private Timestamp accountRestrictedStatusDate;
    private String accountReviewerSystemIdentifier;
    private String accountStateCode;
    private String accountStreetAddress;
    private String accountSufficientFundCode;
    private String accountSupervisorySystemIdentifier;
    private String accountTypeCode;
    private String accountZipCode;
    private String awardPeriodBeginMonth;
    private Integer awardPeriodBeginYear;
    private String awardPeriodEndMonth;
    private Integer awardPeriodEndYear;
    private String budgetRecordingLevelCode;
    private String chartOfAccountsCode;
    private String continuationAccountNumber;
    private String continuationFinChrtOfAcctCd;
    private String contractControlAccountNumber;
    private String contractControlFinCoaCode;
    private String contractGrantCatlfFedDomesticAssistNumber;
    private String endowmentAccountNumber;
    private String endowmentChartOfAccountCode;
    private boolean financialExternalEncumSufficientFundIndicator;
    private String financialHigherEducationFunctionCode;
    private boolean financialObjectivePrsctrlIndicator;
    private boolean financialPreencumSufficientFundIndicator;
    private String financialSeriesIdentifier;
    private String incomeAccountNumber;
    private String incomeFinancialChartOfAccountCode;
    private String indirectCostRecoveryAccountNumber;
    private String indirectCostRecoveryFinancialChartOfAccountCode;
    private String organizationCode;
    private boolean pendingAccountSufficientFundsIndicator;
    private String reportsToAccountNumber;
    private String reportsToChartOfAccountsCode;
    private String subFundGroupCode;
    
    /**
     * Gets the accountCityName attribute. 
     * @return Returns the accountCityName.
     */
    public String getAccountCityName() {
        return accountCityName;
    }
    /**
     * Sets the accountCityName attribute value.
     * @param accountCityName The accountCityName to set.
     */
    public void setAccountCityName(String accountCityName) {
        this.accountCityName = accountCityName;
    }
    /**
     * Gets the accountClosedIndicator attribute. 
     * @return Returns the accountClosedIndicator.
     */
    public boolean isAccountClosedIndicator() {
        return accountClosedIndicator;
    }
    /**
     * Sets the accountClosedIndicator attribute value.
     * @param accountClosedIndicator The accountClosedIndicator to set.
     */
    public void setAccountClosedIndicator(boolean accountClosedIndicator) {
        this.accountClosedIndicator = accountClosedIndicator;
    }
    /**
     * Gets the accountCreateDate attribute. 
     * @return Returns the accountCreateDate.
     */
    public Timestamp getAccountCreateDate() {
        return accountCreateDate;
    }
    /**
     * Sets the accountCreateDate attribute value.
     * @param accountCreateDate The accountCreateDate to set.
     */
    public void setAccountCreateDate(Timestamp accountCreateDate) {
        this.accountCreateDate = accountCreateDate;
    }
    /**
     * Gets the accountCustomIndCstRcvyExclCd attribute. 
     * @return Returns the accountCustomIndCstRcvyExclCd.
     */
    public String getAccountCustomIndCstRcvyExclCd() {
        return accountCustomIndCstRcvyExclCd;
    }
    /**
     * Sets the accountCustomIndCstRcvyExclCd attribute value.
     * @param accountCustomIndCstRcvyExclCd The accountCustomIndCstRcvyExclCd to set.
     */
    public void setAccountCustomIndCstRcvyExclCd(String accountCustomIndCstRcvyExclCd) {
        this.accountCustomIndCstRcvyExclCd = accountCustomIndCstRcvyExclCd;
    }
    /**
     * Gets the accountEffectiveDate attribute. 
     * @return Returns the accountEffectiveDate.
     */
    public Timestamp getAccountEffectiveDate() {
        return accountEffectiveDate;
    }
    /**
     * Sets the accountEffectiveDate attribute value.
     * @param accountEffectiveDate The accountEffectiveDate to set.
     */
    public void setAccountEffectiveDate(Timestamp accountEffectiveDate) {
        this.accountEffectiveDate = accountEffectiveDate;
    }
    /**
     * Gets the accountExpirationDate attribute. 
     * @return Returns the accountExpirationDate.
     */
    public Timestamp getAccountExpirationDate() {
        return accountExpirationDate;
    }
    /**
     * Sets the accountExpirationDate attribute value.
     * @param accountExpirationDate The accountExpirationDate to set.
     */
    public void setAccountExpirationDate(Timestamp accountExpirationDate) {
        this.accountExpirationDate = accountExpirationDate;
    }
    /**
     * Gets the accountFringesBenefitIndicator attribute. 
     * @return Returns the accountFringesBenefitIndicator.
     */
    public boolean isAccountFringesBenefitIndicator() {
        return accountFringesBenefitIndicator;
    }
    /**
     * Sets the accountFringesBenefitIndicator attribute value.
     * @param accountFringesBenefitIndicator The accountFringesBenefitIndicator to set.
     */
    public void setAccountFringesBenefitIndicator(boolean accountFringesBenefitIndicator) {
        this.accountFringesBenefitIndicator = accountFringesBenefitIndicator;
    }
    /**
     * Gets the accountIndirectCostRcvyTypeCd attribute. 
     * @return Returns the accountIndirectCostRcvyTypeCd.
     */
    public String getAccountIndirectCostRcvyTypeCd() {
        return accountIndirectCostRcvyTypeCd;
    }
    /**
     * Sets the accountIndirectCostRcvyTypeCd attribute value.
     * @param accountIndirectCostRcvyTypeCd The accountIndirectCostRcvyTypeCd to set.
     */
    public void setAccountIndirectCostRcvyTypeCd(String accountIndirectCostRcvyTypeCd) {
        this.accountIndirectCostRcvyTypeCd = accountIndirectCostRcvyTypeCd;
    }
    /**
     * Gets the accountInFinancialProcessingIndicator attribute. 
     * @return Returns the accountInFinancialProcessingIndicator.
     */
    public boolean isAccountInFinancialProcessingIndicator() {
        return accountInFinancialProcessingIndicator;
    }
    /**
     * Sets the accountInFinancialProcessingIndicator attribute value.
     * @param accountInFinancialProcessingIndicator The accountInFinancialProcessingIndicator to set.
     */
    public void setAccountInFinancialProcessingIndicator(
            boolean accountInFinancialProcessingIndicator) {
        this.accountInFinancialProcessingIndicator = accountInFinancialProcessingIndicator;
    }
    /**
     * Gets the accountManagerSystemIdentifier attribute. 
     * @return Returns the accountManagerSystemIdentifier.
     */
    public String getAccountManagerSystemIdentifier() {
        return accountManagerSystemIdentifier;
    }
    /**
     * Sets the accountManagerSystemIdentifier attribute value.
     * @param accountManagerSystemIdentifier The accountManagerSystemIdentifier to set.
     */
    public void setAccountManagerSystemIdentifier(String accountManagerSystemIdentifier) {
        this.accountManagerSystemIdentifier = accountManagerSystemIdentifier;
    }
    /**
     * Gets the accountName attribute. 
     * @return Returns the accountName.
     */
    public String getAccountName() {
        return accountName;
    }
    /**
     * Sets the accountName attribute value.
     * @param accountName The accountName to set.
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    /**
     * Gets the accountNumber attribute. 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }
    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    /**
     * Gets the accountOffCampusIndicator attribute. 
     * @return Returns the accountOffCampusIndicator.
     */
    public boolean isAccountOffCampusIndicator() {
        return accountOffCampusIndicator;
    }
    /**
     * Sets the accountOffCampusIndicator attribute value.
     * @param accountOffCampusIndicator The accountOffCampusIndicator to set.
     */
    public void setAccountOffCampusIndicator(boolean accountOffCampusIndicator) {
        this.accountOffCampusIndicator = accountOffCampusIndicator;
    }
    /**
     * Gets the accountPendingSufficientFundIndicator attribute. 
     * @return Returns the accountPendingSufficientFundIndicator.
     */
    public boolean isAccountPendingSufficientFundIndicator() {
        return accountPendingSufficientFundIndicator;
    }
    /**
     * Sets the accountPendingSufficientFundIndicator attribute value.
     * @param accountPendingSufficientFundIndicator The accountPendingSufficientFundIndicator to set.
     */
    public void setAccountPendingSufficientFundIndicator(
            boolean accountPendingSufficientFundIndicator) {
        this.accountPendingSufficientFundIndicator = accountPendingSufficientFundIndicator;
    }
    /**
     * Gets the accountPhysicalCampusCode attribute. 
     * @return Returns the accountPhysicalCampusCode.
     */
    public String getAccountPhysicalCampusCode() {
        return accountPhysicalCampusCode;
    }
    /**
     * Sets the accountPhysicalCampusCode attribute value.
     * @param accountPhysicalCampusCode The accountPhysicalCampusCode to set.
     */
    public void setAccountPhysicalCampusCode(String accountPhysicalCampusCode) {
        this.accountPhysicalCampusCode = accountPhysicalCampusCode;
    }
    /**
     * Gets the accountRestrictedStatusCode attribute. 
     * @return Returns the accountRestrictedStatusCode.
     */
    public String getAccountRestrictedStatusCode() {
        return accountRestrictedStatusCode;
    }
    /**
     * Sets the accountRestrictedStatusCode attribute value.
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     */
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
        this.accountRestrictedStatusCode = accountRestrictedStatusCode;
    }
    /**
     * Gets the accountRestrictedStatusDate attribute. 
     * @return Returns the accountRestrictedStatusDate.
     */
    public Timestamp getAccountRestrictedStatusDate() {
        return accountRestrictedStatusDate;
    }
    /**
     * Sets the accountRestrictedStatusDate attribute value.
     * @param accountRestrictedStatusDate The accountRestrictedStatusDate to set.
     */
    public void setAccountRestrictedStatusDate(Timestamp accountRestrictedStatusDate) {
        this.accountRestrictedStatusDate = accountRestrictedStatusDate;
    }
    /**
     * Gets the accountReviewerSystemIdentifier attribute. 
     * @return Returns the accountReviewerSystemIdentifier.
     */
    public String getAccountReviewerSystemIdentifier() {
        return accountReviewerSystemIdentifier;
    }
    /**
     * Sets the accountReviewerSystemIdentifier attribute value.
     * @param accountReviewerSystemIdentifier The accountReviewerSystemIdentifier to set.
     */
    public void setAccountReviewerSystemIdentifier(String accountReviewerSystemIdentifier) {
        this.accountReviewerSystemIdentifier = accountReviewerSystemIdentifier;
    }
    /**
     * Gets the accountStateCode attribute. 
     * @return Returns the accountStateCode.
     */
    public String getAccountStateCode() {
        return accountStateCode;
    }
    /**
     * Sets the accountStateCode attribute value.
     * @param accountStateCode The accountStateCode to set.
     */
    public void setAccountStateCode(String accountStateCode) {
        this.accountStateCode = accountStateCode;
    }
    /**
     * Gets the accountStreetAddress attribute. 
     * @return Returns the accountStreetAddress.
     */
    public String getAccountStreetAddress() {
        return accountStreetAddress;
    }
    /**
     * Sets the accountStreetAddress attribute value.
     * @param accountStreetAddress The accountStreetAddress to set.
     */
    public void setAccountStreetAddress(String accountStreetAddress) {
        this.accountStreetAddress = accountStreetAddress;
    }
    /**
     * Gets the accountSufficientFundCode attribute. 
     * @return Returns the accountSufficientFundCode.
     */
    public String getAccountSufficientFundCode() {
        return accountSufficientFundCode;
    }
    /**
     * Sets the accountSufficientFundCode attribute value.
     * @param accountSufficientFundCode The accountSufficientFundCode to set.
     */
    public void setAccountSufficientFundCode(String accountSufficientFundCode) {
        this.accountSufficientFundCode = accountSufficientFundCode;
    }
    /**
     * Gets the accountSupervisorySystemIdentifier attribute. 
     * @return Returns the accountSupervisorySystemIdentifier.
     */
    public String getAccountSupervisorySystemIdentifier() {
        return accountSupervisorySystemIdentifier;
    }
    /**
     * Sets the accountSupervisorySystemIdentifier attribute value.
     * @param accountSupervisorySystemIdentifier The accountSupervisorySystemIdentifier to set.
     */
    public void setAccountSupervisorySystemIdentifier(
            String accountSupervisorySystemIdentifier) {
        this.accountSupervisorySystemIdentifier = accountSupervisorySystemIdentifier;
    }
    /**
     * Gets the accountTypeCode attribute. 
     * @return Returns the accountTypeCode.
     */
    public String getAccountTypeCode() {
        return accountTypeCode;
    }
    /**
     * Sets the accountTypeCode attribute value.
     * @param accountTypeCode The accountTypeCode to set.
     */
    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }
    /**
     * Gets the accountZipCode attribute. 
     * @return Returns the accountZipCode.
     */
    public String getAccountZipCode() {
        return accountZipCode;
    }
    /**
     * Sets the accountZipCode attribute value.
     * @param accountZipCode The accountZipCode to set.
     */
    public void setAccountZipCode(String accountZipCode) {
        this.accountZipCode = accountZipCode;
    }
    /**
     * Gets the awardPeriodBeginMonth attribute. 
     * @return Returns the awardPeriodBeginMonth.
     */
    public String getAwardPeriodBeginMonth() {
        return awardPeriodBeginMonth;
    }
    /**
     * Sets the awardPeriodBeginMonth attribute value.
     * @param awardPeriodBeginMonth The awardPeriodBeginMonth to set.
     */
    public void setAwardPeriodBeginMonth(String awardPeriodBeginMonth) {
        this.awardPeriodBeginMonth = awardPeriodBeginMonth;
    }
    /**
     * Gets the awardPeriodBeginYear attribute. 
     * @return Returns the awardPeriodBeginYear.
     */
    public Integer getAwardPeriodBeginYear() {
        return awardPeriodBeginYear;
    }
    /**
     * Sets the awardPeriodBeginYear attribute value.
     * @param awardPeriodBeginYear The awardPeriodBeginYear to set.
     */
    public void setAwardPeriodBeginYear(Integer awardPeriodBeginYear) {
        this.awardPeriodBeginYear = awardPeriodBeginYear;
    }
    /**
     * Gets the awardPeriodEndMonth attribute. 
     * @return Returns the awardPeriodEndMonth.
     */
    public String getAwardPeriodEndMonth() {
        return awardPeriodEndMonth;
    }
    /**
     * Sets the awardPeriodEndMonth attribute value.
     * @param awardPeriodEndMonth The awardPeriodEndMonth to set.
     */
    public void setAwardPeriodEndMonth(String awardPeriodEndMonth) {
        this.awardPeriodEndMonth = awardPeriodEndMonth;
    }
    /**
     * Gets the awardPeriodEndYear attribute. 
     * @return Returns the awardPeriodEndYear.
     */
    public Integer getAwardPeriodEndYear() {
        return awardPeriodEndYear;
    }
    /**
     * Sets the awardPeriodEndYear attribute value.
     * @param awardPeriodEndYear The awardPeriodEndYear to set.
     */
    public void setAwardPeriodEndYear(Integer awardPeriodEndYear) {
        this.awardPeriodEndYear = awardPeriodEndYear;
    }
    /**
     * Gets the budgetRecordingLevelCode attribute. 
     * @return Returns the budgetRecordingLevelCode.
     */
    public String getBudgetRecordingLevelCode() {
        return budgetRecordingLevelCode;
    }
    /**
     * Sets the budgetRecordingLevelCode attribute value.
     * @param budgetRecordingLevelCode The budgetRecordingLevelCode to set.
     */
    public void setBudgetRecordingLevelCode(String budgetRecordingLevelCode) {
        this.budgetRecordingLevelCode = budgetRecordingLevelCode;
    }
    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }
    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }
    /**
     * Gets the continuationAccountNumber attribute. 
     * @return Returns the continuationAccountNumber.
     */
    public String getContinuationAccountNumber() {
        return continuationAccountNumber;
    }
    /**
     * Sets the continuationAccountNumber attribute value.
     * @param continuationAccountNumber The continuationAccountNumber to set.
     */
    public void setContinuationAccountNumber(String continuationAccountNumber) {
        this.continuationAccountNumber = continuationAccountNumber;
    }
    /**
     * Gets the continuationFinChrtOfAcctCd attribute. 
     * @return Returns the continuationFinChrtOfAcctCd.
     */
    public String getContinuationFinChrtOfAcctCd() {
        return continuationFinChrtOfAcctCd;
    }
    /**
     * Sets the continuationFinChrtOfAcctCd attribute value.
     * @param continuationFinChrtOfAcctCd The continuationFinChrtOfAcctCd to set.
     */
    public void setContinuationFinChrtOfAcctCd(String continuationFinChrtOfAcctCd) {
        this.continuationFinChrtOfAcctCd = continuationFinChrtOfAcctCd;
    }
    /**
     * Gets the contractControlAccountNumber attribute. 
     * @return Returns the contractControlAccountNumber.
     */
    public String getContractControlAccountNumber() {
        return contractControlAccountNumber;
    }
    /**
     * Sets the contractControlAccountNumber attribute value.
     * @param contractControlAccountNumber The contractControlAccountNumber to set.
     */
    public void setContractControlAccountNumber(String contractControlAccountNumber) {
        this.contractControlAccountNumber = contractControlAccountNumber;
    }
    /**
     * Gets the contractControlFinCoaCode attribute. 
     * @return Returns the contractControlFinCoaCode.
     */
    public String getContractControlFinCoaCode() {
        return contractControlFinCoaCode;
    }
    /**
     * Sets the contractControlFinCoaCode attribute value.
     * @param contractControlFinCoaCode The contractControlFinCoaCode to set.
     */
    public void setContractControlFinCoaCode(String contractControlFinCoaCode) {
        this.contractControlFinCoaCode = contractControlFinCoaCode;
    }
    /**
     * Gets the contractGrantCatlfFedDomesticAssistNumber attribute. 
     * @return Returns the contractGrantCatlfFedDomesticAssistNumber.
     */
    public String getContractGrantCatlfFedDomesticAssistNumber() {
        return contractGrantCatlfFedDomesticAssistNumber;
    }
    /**
     * Sets the contractGrantCatlfFedDomesticAssistNumber attribute value.
     * @param contractGrantCatlfFedDomesticAssistNumber The contractGrantCatlfFedDomesticAssistNumber to set.
     */
    public void setContractGrantCatlfFedDomesticAssistNumber(
            String contractGrantCatlfFedDomesticAssistNumber) {
        this.contractGrantCatlfFedDomesticAssistNumber = contractGrantCatlfFedDomesticAssistNumber;
    }
    /**
     * Gets the endowmentAccountNumber attribute. 
     * @return Returns the endowmentAccountNumber.
     */
    public String getEndowmentAccountNumber() {
        return endowmentAccountNumber;
    }
    /**
     * Sets the endowmentAccountNumber attribute value.
     * @param endowmentAccountNumber The endowmentAccountNumber to set.
     */
    public void setEndowmentAccountNumber(String endowmentAccountNumber) {
        this.endowmentAccountNumber = endowmentAccountNumber;
    }
    /**
     * Gets the endowmentChartOfAccountCode attribute. 
     * @return Returns the endowmentChartOfAccountCode.
     */
    public String getEndowmentChartOfAccountCode() {
        return endowmentChartOfAccountCode;
    }
    /**
     * Sets the endowmentChartOfAccountCode attribute value.
     * @param endowmentChartOfAccountCode The endowmentChartOfAccountCode to set.
     */
    public void setEndowmentChartOfAccountCode(String endowmentChartOfAccountCode) {
        this.endowmentChartOfAccountCode = endowmentChartOfAccountCode;
    }
    /**
     * Gets the financialExternalEncumSufficientFundIndicator attribute. 
     * @return Returns the financialExternalEncumSufficientFundIndicator.
     */
    public boolean isFinancialExternalEncumSufficientFundIndicator() {
        return financialExternalEncumSufficientFundIndicator;
    }
    /**
     * Sets the financialExternalEncumSufficientFundIndicator attribute value.
     * @param financialExternalEncumSufficientFundIndicator The financialExternalEncumSufficientFundIndicator to set.
     */
    public void setFinancialExternalEncumSufficientFundIndicator(
            boolean financialExternalEncumSufficientFundIndicator) {
        this.financialExternalEncumSufficientFundIndicator = financialExternalEncumSufficientFundIndicator;
    }
    /**
     * Gets the financialHigherEducationFunctionCode attribute. 
     * @return Returns the financialHigherEducationFunctionCode.
     */
    public String getFinancialHigherEducationFunctionCode() {
        return financialHigherEducationFunctionCode;
    }
    /**
     * Sets the financialHigherEducationFunctionCode attribute value.
     * @param financialHigherEducationFunctionCode The financialHigherEducationFunctionCode to set.
     */
    public void setFinancialHigherEducationFunctionCode(
            String financialHigherEducationFunctionCode) {
        this.financialHigherEducationFunctionCode = financialHigherEducationFunctionCode;
    }
    /**
     * Gets the financialObjectivePrsctrlIndicator attribute. 
     * @return Returns the financialObjectivePrsctrlIndicator.
     */
    public boolean isFinancialObjectivePrsctrlIndicator() {
        return financialObjectivePrsctrlIndicator;
    }
    /**
     * Sets the financialObjectivePrsctrlIndicator attribute value.
     * @param financialObjectivePrsctrlIndicator The financialObjectivePrsctrlIndicator to set.
     */
    public void setFinancialObjectivePrsctrlIndicator(
            boolean financialObjectivePrsctrlIndicator) {
        this.financialObjectivePrsctrlIndicator = financialObjectivePrsctrlIndicator;
    }
    /**
     * Gets the financialPreencumSufficientFundIndicator attribute. 
     * @return Returns the financialPreencumSufficientFundIndicator.
     */
    public boolean isFinancialPreencumSufficientFundIndicator() {
        return financialPreencumSufficientFundIndicator;
    }
    /**
     * Sets the financialPreencumSufficientFundIndicator attribute value.
     * @param financialPreencumSufficientFundIndicator The financialPreencumSufficientFundIndicator to set.
     */
    public void setFinancialPreencumSufficientFundIndicator(
            boolean financialPreencumSufficientFundIndicator) {
        this.financialPreencumSufficientFundIndicator = financialPreencumSufficientFundIndicator;
    }
    /**
     * Gets the financialSeriesIdentifier attribute. 
     * @return Returns the financialSeriesIdentifier.
     */
    public String getFinancialSeriesIdentifier() {
        return financialSeriesIdentifier;
    }
    /**
     * Sets the financialSeriesIdentifier attribute value.
     * @param financialSeriesIdentifier The financialSeriesIdentifier to set.
     */
    public void setFinancialSeriesIdentifier(String financialSeriesIdentifier) {
        this.financialSeriesIdentifier = financialSeriesIdentifier;
    }
    /**
     * Gets the incomeAccountNumber attribute. 
     * @return Returns the incomeAccountNumber.
     */
    public String getIncomeAccountNumber() {
        return incomeAccountNumber;
    }
    /**
     * Sets the incomeAccountNumber attribute value.
     * @param incomeAccountNumber The incomeAccountNumber to set.
     */
    public void setIncomeAccountNumber(String incomeAccountNumber) {
        this.incomeAccountNumber = incomeAccountNumber;
    }
    /**
     * Gets the incomeFinancialChartOfAccountCode attribute. 
     * @return Returns the incomeFinancialChartOfAccountCode.
     */
    public String getIncomeFinancialChartOfAccountCode() {
        return incomeFinancialChartOfAccountCode;
    }
    /**
     * Sets the incomeFinancialChartOfAccountCode attribute value.
     * @param incomeFinancialChartOfAccountCode The incomeFinancialChartOfAccountCode to set.
     */
    public void setIncomeFinancialChartOfAccountCode(
            String incomeFinancialChartOfAccountCode) {
        this.incomeFinancialChartOfAccountCode = incomeFinancialChartOfAccountCode;
    }
    /**
     * Gets the indirectCostRecoveryAccountNumber attribute. 
     * @return Returns the indirectCostRecoveryAccountNumber.
     */
    public String getIndirectCostRecoveryAccountNumber() {
        return indirectCostRecoveryAccountNumber;
    }
    /**
     * Sets the indirectCostRecoveryAccountNumber attribute value.
     * @param indirectCostRecoveryAccountNumber The indirectCostRecoveryAccountNumber to set.
     */
    public void setIndirectCostRecoveryAccountNumber(
            String indirectCostRecoveryAccountNumber) {
        this.indirectCostRecoveryAccountNumber = indirectCostRecoveryAccountNumber;
    }
    /**
     * Gets the indirectCostRecoveryFinancialChartOfAccountCode attribute. 
     * @return Returns the indirectCostRecoveryFinancialChartOfAccountCode.
     */
    public String getIndirectCostRecoveryFinancialChartOfAccountCode() {
        return indirectCostRecoveryFinancialChartOfAccountCode;
    }
    /**
     * Sets the indirectCostRecoveryFinancialChartOfAccountCode attribute value.
     * @param indirectCostRecoveryFinancialChartOfAccountCode The indirectCostRecoveryFinancialChartOfAccountCode to set.
     */
    public void setIndirectCostRecoveryFinancialChartOfAccountCode(
            String indirectCostRecoveryFinancialChartOfAccountCode) {
        this.indirectCostRecoveryFinancialChartOfAccountCode = indirectCostRecoveryFinancialChartOfAccountCode;
    }
    /**
     * Gets the organizationCode attribute. 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }
    /**
     * Sets the organizationCode attribute value.
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
    /**
     * Gets the pendingAccountSufficientFundsIndicator attribute. 
     * @return Returns the pendingAccountSufficientFundsIndicator.
     */
    public boolean isPendingAccountSufficientFundsIndicator() {
        return pendingAccountSufficientFundsIndicator;
    }
    /**
     * Sets the pendingAccountSufficientFundsIndicator attribute value.
     * @param pendingAccountSufficientFundsIndicator The pendingAccountSufficientFundsIndicator to set.
     */
    public void setPendingAccountSufficientFundsIndicator(
            boolean pendingAccountSufficientFundsIndicator) {
        this.pendingAccountSufficientFundsIndicator = pendingAccountSufficientFundsIndicator;
    }
    /**
     * Gets the reportsToAccountNumber attribute. 
     * @return Returns the reportsToAccountNumber.
     */
    public String getReportsToAccountNumber() {
        return reportsToAccountNumber;
    }
    /**
     * Sets the reportsToAccountNumber attribute value.
     * @param reportsToAccountNumber The reportsToAccountNumber to set.
     */
    public void setReportsToAccountNumber(String reportsToAccountNumber) {
        this.reportsToAccountNumber = reportsToAccountNumber;
    }
    /**
     * Gets the reportsToChartOfAccountsCode attribute. 
     * @return Returns the reportsToChartOfAccountsCode.
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }
    /**
     * Sets the reportsToChartOfAccountsCode attribute value.
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }
    /**
     * Gets the subFundGroupCode attribute. 
     * @return Returns the subFundGroupCode.
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }
    /**
     * Sets the subFundGroupCode attribute value.
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }
}
