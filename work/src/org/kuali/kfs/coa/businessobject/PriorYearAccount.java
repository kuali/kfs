/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.chart.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PriorYearAccount extends BusinessObjectBase {

	private String chartOfAccountsCode;
	private String accountNumber;
	private String accountName;
	private String accountFiscalOfficerUniversalIdentifier;
	private String accountSupervisorUniversalId;
	private String accountManagerUniversalId;
	private String organizationCode;
	private String accountTypeCode;
	private String accountPhysicalCampusCode;
	private String subFundGroupCode;
	private String accountFringeBenefitCode;
	private String financialHigherEdFunctionCd;
	private String accountRestrictedStatusCode;
	private Date accountRestrictedStatusDate;
	private String accountCityName;
	private String accountStateCode;
	private String accountStreetAddress;
	private String accountZipCode;
	private String reportsToChartOfAccountsCode;
	private String reportsToAccountNumber;
	private Date accountCreateDate;
	private Date accountEffectiveDate;
	private Date accountExpirationDate;
	private String continuationFinChrtOfAcctCd;
	private String continuationAccountNumber;
	private Integer awardPeriodEndYear;
	private String awardPeriodEndMonth;
	private Integer awardPeriodBeginYear;
	private String awardPeriodBeginMonth;
	private String endowmentIncomeAcctFinCoaCd;
	private String endowmentIncomeAccountNumber;
	private String contractControlFinCoaCode;
	private String contractControlAccountNumber;
	private String incomeStreamFinancialCoaCode;
	private String incomeStreamAccountNumber;
	private String acctIndirectCostRcvyTypeCd;
	private String acctCustomIndCstRcvyExclCd;
	private String financialIcrSeriesIdentifier;
	private String indirectCostRcvyFinCoaCode;
	private String indirectCostRecoveryAcctNbr;
	private boolean accountInFinProcessingIndicator;
	private String budgetRecordingLevelCode;
	private String accountSufficientFundsCode;
	private boolean pendingAcctSufficientFundsIndicator;
	private boolean extrnlFinEncumSufficntFndIndicator;
	private boolean intrnlFinEncumSufficntFndIndicator;
	private boolean finPreencumSufficientFundIndicator;
	private boolean finObjectPresenceControlIndicator;
	private String cgCatlfFedDomestcAssistNbr;
	private boolean accountOffCampusIndicator;
	private boolean accountClosedIndicator;
    private String programCode;
    
    private Chart chart;
	private Campus campus;
	private Chart reportsToChartOfAccounts;
	private Account account;
	private Chart continuationFinChrtOfAcct;
	private Chart endowmentIncomeAcctFinCoa;
	private Account endowmentIncomeAccount;
	private Account contractControlAccount;
	private Chart contractControlFinCoa;
	private Chart incomeStreamFinancialCoa;
	private Account incomeStreamAccount;
	private Account indirectCostRecoveryAcct;
	private Chart indirectCostRcvyFinCoa;
    private Program program;
    
	/**
	 * Default constructor.
	 */
	public PriorYearAccount() {

	}

	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return - Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param - chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return - Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param - accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the accountName attribute.
	 * 
	 * @return - Returns the accountName
	 * 
	 */
	public String getAccountName() { 
		return accountName;
	}

	/**
	 * Sets the accountName attribute.
	 * 
	 * @param - accountName The accountName to set.
	 * 
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	/**
	 * Gets the accountFiscalOfficerUniversalIdentifier attribute.
	 * 
	 * @return - Returns the accountFiscalOfficerUniversalIdentifier
	 * 
	 */
	public String getAccountFiscalOfficerUniversalIdentifier() { 
		return accountFiscalOfficerUniversalIdentifier;
	}

	/**
	 * Sets the accountFiscalOfficerUniversalIdentifier attribute.
	 * 
	 * @param - accountFiscalOfficerUniversalIdentifier The accountFiscalOfficerUniversalIdentifier to set.
	 * 
	 */
	public void setAccountFiscalOfficerUniversalIdentifier(String accountFiscalOfficerUniversalIdentifier) {
		this.accountFiscalOfficerUniversalIdentifier = accountFiscalOfficerUniversalIdentifier;
	}


	/**
	 * Gets the accountSupervisorUniversalId attribute.
	 * 
	 * @return - Returns the accountSupervisorUniversalId
	 * 
	 */
	public String getAccountSupervisorUniversalId() { 
		return accountSupervisorUniversalId;
	}

	/**
	 * Sets the accountSupervisorUniversalId attribute.
	 * 
	 * @param - accountSupervisorUniversalId The accountSupervisorUniversalId to set.
	 * 
	 */
	public void setAccountSupervisorUniversalId(String accountSupervisorUniversalId) {
		this.accountSupervisorUniversalId = accountSupervisorUniversalId;
	}


	/**
	 * Gets the accountManagerUniversalId attribute.
	 * 
	 * @return - Returns the accountManagerUniversalId
	 * 
	 */
	public String getAccountManagerUniversalId() { 
		return accountManagerUniversalId;
	}

	/**
	 * Sets the accountManagerUniversalId attribute.
	 * 
	 * @param - accountManagerUniversalId The accountManagerUniversalId to set.
	 * 
	 */
	public void setAccountManagerUniversalId(String accountManagerUniversalId) {
		this.accountManagerUniversalId = accountManagerUniversalId;
	}


	/**
	 * Gets the organizationCode attribute.
	 * 
	 * @return - Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param - organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}


	/**
	 * Gets the accountTypeCode attribute.
	 * 
	 * @return - Returns the accountTypeCode
	 * 
	 */
	public String getAccountTypeCode() { 
		return accountTypeCode;
	}

	/**
	 * Sets the accountTypeCode attribute.
	 * 
	 * @param - accountTypeCode The accountTypeCode to set.
	 * 
	 */
	public void setAccountTypeCode(String accountTypeCode) {
		this.accountTypeCode = accountTypeCode;
	}


	/**
	 * Gets the accountPhysicalCampusCode attribute.
	 * 
	 * @return - Returns the accountPhysicalCampusCode
	 * 
	 */
	public String getAccountPhysicalCampusCode() { 
		return accountPhysicalCampusCode;
	}

	/**
	 * Sets the accountPhysicalCampusCode attribute.
	 * 
	 * @param - accountPhysicalCampusCode The accountPhysicalCampusCode to set.
	 * 
	 */
	public void setAccountPhysicalCampusCode(String accountPhysicalCampusCode) {
		this.accountPhysicalCampusCode = accountPhysicalCampusCode;
	}


	/**
	 * Gets the subFundGroupCode attribute.
	 * 
	 * @return - Returns the subFundGroupCode
	 * 
	 */
	public String getSubFundGroupCode() { 
		return subFundGroupCode;
	}

	/**
	 * Sets the subFundGroupCode attribute.
	 * 
	 * @param - subFundGroupCode The subFundGroupCode to set.
	 * 
	 */
	public void setSubFundGroupCode(String subFundGroupCode) {
		this.subFundGroupCode = subFundGroupCode;
	}


	/**
	 * Gets the accountFringeBenefitCode attribute.
	 * 
	 * @return - Returns the accountFringeBenefitCode
	 * 
	 */
	public String getAccountFringeBenefitCode() { 
		return accountFringeBenefitCode;
	}

	/**
	 * Sets the accountFringeBenefitCode attribute.
	 * 
	 * @param - accountFringeBenefitCode The accountFringeBenefitCode to set.
	 * 
	 */
	public void setAccountFringeBenefitCode(String accountFringeBenefitCode) {
		this.accountFringeBenefitCode = accountFringeBenefitCode;
	}


	/**
	 * Gets the financialHigherEdFunctionCd attribute.
	 * 
	 * @return - Returns the financialHigherEdFunctionCd
	 * 
	 */
	public String getFinancialHigherEdFunctionCd() { 
		return financialHigherEdFunctionCd;
	}

	/**
	 * Sets the financialHigherEdFunctionCd attribute.
	 * 
	 * @param - financialHigherEdFunctionCd The financialHigherEdFunctionCd to set.
	 * 
	 */
	public void setFinancialHigherEdFunctionCd(String financialHigherEdFunctionCd) {
		this.financialHigherEdFunctionCd = financialHigherEdFunctionCd;
	}


	/**
	 * Gets the accountRestrictedStatusCode attribute.
	 * 
	 * @return - Returns the accountRestrictedStatusCode
	 * 
	 */
	public String getAccountRestrictedStatusCode() { 
		return accountRestrictedStatusCode;
	}

	/**
	 * Sets the accountRestrictedStatusCode attribute.
	 * 
	 * @param - accountRestrictedStatusCode The accountRestrictedStatusCode to set.
	 * 
	 */
	public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
		this.accountRestrictedStatusCode = accountRestrictedStatusCode;
	}


	/**
	 * Gets the accountRestrictedStatusDate attribute.
	 * 
	 * @return - Returns the accountRestrictedStatusDate
	 * 
	 */
	public Date getAccountRestrictedStatusDate() { 
		return accountRestrictedStatusDate;
	}

	/**
	 * Sets the accountRestrictedStatusDate attribute.
	 * 
	 * @param - accountRestrictedStatusDate The accountRestrictedStatusDate to set.
	 * 
	 */
	public void setAccountRestrictedStatusDate(Date accountRestrictedStatusDate) {
		this.accountRestrictedStatusDate = accountRestrictedStatusDate;
	}


	/**
	 * Gets the accountCityName attribute.
	 * 
	 * @return - Returns the accountCityName
	 * 
	 */
	public String getAccountCityName() { 
		return accountCityName;
	}

	/**
	 * Sets the accountCityName attribute.
	 * 
	 * @param - accountCityName The accountCityName to set.
	 * 
	 */
	public void setAccountCityName(String accountCityName) {
		this.accountCityName = accountCityName;
	}


	/**
	 * Gets the accountStateCode attribute.
	 * 
	 * @return - Returns the accountStateCode
	 * 
	 */
	public String getAccountStateCode() { 
		return accountStateCode;
	}

	/**
	 * Sets the accountStateCode attribute.
	 * 
	 * @param - accountStateCode The accountStateCode to set.
	 * 
	 */
	public void setAccountStateCode(String accountStateCode) {
		this.accountStateCode = accountStateCode;
	}


	/**
	 * Gets the accountStreetAddress attribute.
	 * 
	 * @return - Returns the accountStreetAddress
	 * 
	 */
	public String getAccountStreetAddress() { 
		return accountStreetAddress;
	}

	/**
	 * Sets the accountStreetAddress attribute.
	 * 
	 * @param - accountStreetAddress The accountStreetAddress to set.
	 * 
	 */
	public void setAccountStreetAddress(String accountStreetAddress) {
		this.accountStreetAddress = accountStreetAddress;
	}


	/**
	 * Gets the accountZipCode attribute.
	 * 
	 * @return - Returns the accountZipCode
	 * 
	 */
	public String getAccountZipCode() { 
		return accountZipCode;
	}

	/**
	 * Sets the accountZipCode attribute.
	 * 
	 * @param - accountZipCode The accountZipCode to set.
	 * 
	 */
	public void setAccountZipCode(String accountZipCode) {
		this.accountZipCode = accountZipCode;
	}


	/**
	 * Gets the reportsToChartOfAccountsCode attribute.
	 * 
	 * @return - Returns the reportsToChartOfAccountsCode
	 * 
	 */
	public String getReportsToChartOfAccountsCode() { 
		return reportsToChartOfAccountsCode;
	}

	/**
	 * Sets the reportsToChartOfAccountsCode attribute.
	 * 
	 * @param - reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
	 * 
	 */
	public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
		this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
	}


	/**
	 * Gets the reportsToAccountNumber attribute.
	 * 
	 * @return - Returns the reportsToAccountNumber
	 * 
	 */
	public String getReportsToAccountNumber() { 
		return reportsToAccountNumber;
	}

	/**
	 * Sets the reportsToAccountNumber attribute.
	 * 
	 * @param - reportsToAccountNumber The reportsToAccountNumber to set.
	 * 
	 */
	public void setReportsToAccountNumber(String reportsToAccountNumber) {
		this.reportsToAccountNumber = reportsToAccountNumber;
	}


	/**
	 * Gets the accountCreateDate attribute.
	 * 
	 * @return - Returns the accountCreateDate
	 * 
	 */
	public Date getAccountCreateDate() { 
		return accountCreateDate;
	}

	/**
	 * Sets the accountCreateDate attribute.
	 * 
	 * @param - accountCreateDate The accountCreateDate to set.
	 * 
	 */
	public void setAccountCreateDate(Date accountCreateDate) {
		this.accountCreateDate = accountCreateDate;
	}


	/**
	 * Gets the accountEffectiveDate attribute.
	 * 
	 * @return - Returns the accountEffectiveDate
	 * 
	 */
	public Date getAccountEffectiveDate() { 
		return accountEffectiveDate;
	}

	/**
	 * Sets the accountEffectiveDate attribute.
	 * 
	 * @param - accountEffectiveDate The accountEffectiveDate to set.
	 * 
	 */
	public void setAccountEffectiveDate(Date accountEffectiveDate) {
		this.accountEffectiveDate = accountEffectiveDate;
	}


	/**
	 * Gets the accountExpirationDate attribute.
	 * 
	 * @return - Returns the accountExpirationDate
	 * 
	 */
	public Date getAccountExpirationDate() { 
		return accountExpirationDate;
	}

	/**
	 * Sets the accountExpirationDate attribute.
	 * 
	 * @param - accountExpirationDate The accountExpirationDate to set.
	 * 
	 */
	public void setAccountExpirationDate(Date accountExpirationDate) {
		this.accountExpirationDate = accountExpirationDate;
	}


	/**
	 * Gets the continuationFinChrtOfAcctCd attribute.
	 * 
	 * @return - Returns the continuationFinChrtOfAcctCd
	 * 
	 */
	public String getContinuationFinChrtOfAcctCd() { 
		return continuationFinChrtOfAcctCd;
	}

	/**
	 * Sets the continuationFinChrtOfAcctCd attribute.
	 * 
	 * @param - continuationFinChrtOfAcctCd The continuationFinChrtOfAcctCd to set.
	 * 
	 */
	public void setContinuationFinChrtOfAcctCd(String continuationFinChrtOfAcctCd) {
		this.continuationFinChrtOfAcctCd = continuationFinChrtOfAcctCd;
	}


	/**
	 * Gets the continuationAccountNumber attribute.
	 * 
	 * @return - Returns the continuationAccountNumber
	 * 
	 */
	public String getContinuationAccountNumber() { 
		return continuationAccountNumber;
	}

	/**
	 * Sets the continuationAccountNumber attribute.
	 * 
	 * @param - continuationAccountNumber The continuationAccountNumber to set.
	 * 
	 */
	public void setContinuationAccountNumber(String continuationAccountNumber) {
		this.continuationAccountNumber = continuationAccountNumber;
	}


	/**
	 * Gets the awardPeriodEndYear attribute.
	 * 
	 * @return - Returns the awardPeriodEndYear
	 * 
	 */
	public Integer getAwardPeriodEndYear() { 
		return awardPeriodEndYear;
	}

	/**
	 * Sets the awardPeriodEndYear attribute.
	 * 
	 * @param - awardPeriodEndYear The awardPeriodEndYear to set.
	 * 
	 */
	public void setAwardPeriodEndYear(Integer awardPeriodEndYear) {
		this.awardPeriodEndYear = awardPeriodEndYear;
	}


	/**
	 * Gets the awardPeriodEndMonth attribute.
	 * 
	 * @return - Returns the awardPeriodEndMonth
	 * 
	 */
	public String getAwardPeriodEndMonth() { 
		return awardPeriodEndMonth;
	}

	/**
	 * Sets the awardPeriodEndMonth attribute.
	 * 
	 * @param - awardPeriodEndMonth The awardPeriodEndMonth to set.
	 * 
	 */
	public void setAwardPeriodEndMonth(String awardPeriodEndMonth) {
		this.awardPeriodEndMonth = awardPeriodEndMonth;
	}


	/**
	 * Gets the awardPeriodBeginYear attribute.
	 * 
	 * @return - Returns the awardPeriodBeginYear
	 * 
	 */
	public Integer getAwardPeriodBeginYear() { 
		return awardPeriodBeginYear;
	}

	/**
	 * Sets the awardPeriodBeginYear attribute.
	 * 
	 * @param - awardPeriodBeginYear The awardPeriodBeginYear to set.
	 * 
	 */
	public void setAwardPeriodBeginYear(Integer awardPeriodBeginYear) {
		this.awardPeriodBeginYear = awardPeriodBeginYear;
	}


	/**
	 * Gets the awardPeriodBeginMonth attribute.
	 * 
	 * @return - Returns the awardPeriodBeginMonth
	 * 
	 */
	public String getAwardPeriodBeginMonth() { 
		return awardPeriodBeginMonth;
	}

	/**
	 * Sets the awardPeriodBeginMonth attribute.
	 * 
	 * @param - awardPeriodBeginMonth The awardPeriodBeginMonth to set.
	 * 
	 */
	public void setAwardPeriodBeginMonth(String awardPeriodBeginMonth) {
		this.awardPeriodBeginMonth = awardPeriodBeginMonth;
	}


	/**
	 * Gets the endowmentIncomeAcctFinCoaCd attribute.
	 * 
	 * @return - Returns the endowmentIncomeAcctFinCoaCd
	 * 
	 */
	public String getEndowmentIncomeAcctFinCoaCd() { 
		return endowmentIncomeAcctFinCoaCd;
	}

	/**
	 * Sets the endowmentIncomeAcctFinCoaCd attribute.
	 * 
	 * @param - endowmentIncomeAcctFinCoaCd The endowmentIncomeAcctFinCoaCd to set.
	 * 
	 */
	public void setEndowmentIncomeAcctFinCoaCd(String endowmentIncomeAcctFinCoaCd) {
		this.endowmentIncomeAcctFinCoaCd = endowmentIncomeAcctFinCoaCd;
	}


	/**
	 * Gets the endowmentIncomeAccountNumber attribute.
	 * 
	 * @return - Returns the endowmentIncomeAccountNumber
	 * 
	 */
	public String getEndowmentIncomeAccountNumber() { 
		return endowmentIncomeAccountNumber;
	}

	/**
	 * Sets the endowmentIncomeAccountNumber attribute.
	 * 
	 * @param - endowmentIncomeAccountNumber The endowmentIncomeAccountNumber to set.
	 * 
	 */
	public void setEndowmentIncomeAccountNumber(String endowmentIncomeAccountNumber) {
		this.endowmentIncomeAccountNumber = endowmentIncomeAccountNumber;
	}


	/**
	 * Gets the contractControlFinCoaCode attribute.
	 * 
	 * @return - Returns the contractControlFinCoaCode
	 * 
	 */
	public String getContractControlFinCoaCode() { 
		return contractControlFinCoaCode;
	}

	/**
	 * Sets the contractControlFinCoaCode attribute.
	 * 
	 * @param - contractControlFinCoaCode The contractControlFinCoaCode to set.
	 * 
	 */
	public void setContractControlFinCoaCode(String contractControlFinCoaCode) {
		this.contractControlFinCoaCode = contractControlFinCoaCode;
	}


	/**
	 * Gets the contractControlAccountNumber attribute.
	 * 
	 * @return - Returns the contractControlAccountNumber
	 * 
	 */
	public String getContractControlAccountNumber() { 
		return contractControlAccountNumber;
	}

	/**
	 * Sets the contractControlAccountNumber attribute.
	 * 
	 * @param - contractControlAccountNumber The contractControlAccountNumber to set.
	 * 
	 */
	public void setContractControlAccountNumber(String contractControlAccountNumber) {
		this.contractControlAccountNumber = contractControlAccountNumber;
	}


	/**
	 * Gets the incomeStreamFinancialCoaCode attribute.
	 * 
	 * @return - Returns the incomeStreamFinancialCoaCode
	 * 
	 */
	public String getIncomeStreamFinancialCoaCode() { 
		return incomeStreamFinancialCoaCode;
	}

	/**
	 * Sets the incomeStreamFinancialCoaCode attribute.
	 * 
	 * @param - incomeStreamFinancialCoaCode The incomeStreamFinancialCoaCode to set.
	 * 
	 */
	public void setIncomeStreamFinancialCoaCode(String incomeStreamFinancialCoaCode) {
		this.incomeStreamFinancialCoaCode = incomeStreamFinancialCoaCode;
	}


	/**
	 * Gets the incomeStreamAccountNumber attribute.
	 * 
	 * @return - Returns the incomeStreamAccountNumber
	 * 
	 */
	public String getIncomeStreamAccountNumber() { 
		return incomeStreamAccountNumber;
	}

	/**
	 * Sets the incomeStreamAccountNumber attribute.
	 * 
	 * @param - incomeStreamAccountNumber The incomeStreamAccountNumber to set.
	 * 
	 */
	public void setIncomeStreamAccountNumber(String incomeStreamAccountNumber) {
		this.incomeStreamAccountNumber = incomeStreamAccountNumber;
	}


	/**
	 * Gets the acctIndirectCostRcvyTypeCd attribute.
	 * 
	 * @return - Returns the acctIndirectCostRcvyTypeCd
	 * 
	 */
	public String getAcctIndirectCostRcvyTypeCd() { 
		return acctIndirectCostRcvyTypeCd;
	}

	/**
	 * Sets the acctIndirectCostRcvyTypeCd attribute.
	 * 
	 * @param - acctIndirectCostRcvyTypeCd The acctIndirectCostRcvyTypeCd to set.
	 * 
	 */
	public void setAcctIndirectCostRcvyTypeCd(String acctIndirectCostRcvyTypeCd) {
		this.acctIndirectCostRcvyTypeCd = acctIndirectCostRcvyTypeCd;
	}


	/**
	 * Gets the acctCustomIndCstRcvyExclCd attribute.
	 * 
	 * @return - Returns the acctCustomIndCstRcvyExclCd
	 * 
	 */
	public String getAcctCustomIndCstRcvyExclCd() { 
		return acctCustomIndCstRcvyExclCd;
	}

	/**
	 * Sets the acctCustomIndCstRcvyExclCd attribute.
	 * 
	 * @param - acctCustomIndCstRcvyExclCd The acctCustomIndCstRcvyExclCd to set.
	 * 
	 */
	public void setAcctCustomIndCstRcvyExclCd(String acctCustomIndCstRcvyExclCd) {
		this.acctCustomIndCstRcvyExclCd = acctCustomIndCstRcvyExclCd;
	}


	/**
	 * Gets the financialIcrSeriesIdentifier attribute.
	 * 
	 * @return - Returns the financialIcrSeriesIdentifier
	 * 
	 */
	public String getFinancialIcrSeriesIdentifier() { 
		return financialIcrSeriesIdentifier;
	}

	/**
	 * Sets the financialIcrSeriesIdentifier attribute.
	 * 
	 * @param - financialIcrSeriesIdentifier The financialIcrSeriesIdentifier to set.
	 * 
	 */
	public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
		this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
	}


	/**
	 * Gets the indirectCostRcvyFinCoaCode attribute.
	 * 
	 * @return - Returns the indirectCostRcvyFinCoaCode
	 * 
	 */
	public String getIndirectCostRcvyFinCoaCode() { 
		return indirectCostRcvyFinCoaCode;
	}

	/**
	 * Sets the indirectCostRcvyFinCoaCode attribute.
	 * 
	 * @param - indirectCostRcvyFinCoaCode The indirectCostRcvyFinCoaCode to set.
	 * 
	 */
	public void setIndirectCostRcvyFinCoaCode(String indirectCostRcvyFinCoaCode) {
		this.indirectCostRcvyFinCoaCode = indirectCostRcvyFinCoaCode;
	}


	/**
	 * Gets the indirectCostRecoveryAcctNbr attribute.
	 * 
	 * @return - Returns the indirectCostRecoveryAcctNbr
	 * 
	 */
	public String getIndirectCostRecoveryAcctNbr() { 
		return indirectCostRecoveryAcctNbr;
	}

	/**
	 * Sets the indirectCostRecoveryAcctNbr attribute.
	 * 
	 * @param - indirectCostRecoveryAcctNbr The indirectCostRecoveryAcctNbr to set.
	 * 
	 */
	public void setIndirectCostRecoveryAcctNbr(String indirectCostRecoveryAcctNbr) {
		this.indirectCostRecoveryAcctNbr = indirectCostRecoveryAcctNbr;
	}


	/**
	 * Gets the accountInFinProcessingIndicator attribute.
	 * 
	 * @return - Returns the accountInFinProcessingIndicator
	 * 
	 */
	public boolean isAccountInFinProcessingIndicator() { 
		return accountInFinProcessingIndicator;
	}
	

	/**
	 * Sets the accountInFinProcessingIndicator attribute.
	 * 
	 * @param - accountInFinProcessingIndicator The accountInFinProcessingIndicator to set.
	 * 
	 */
	public void setAccountInFinProcessingIndicator(boolean accountInFinProcessingIndicator) {
		this.accountInFinProcessingIndicator = accountInFinProcessingIndicator;
	}


	/**
	 * Gets the budgetRecordingLevelCode attribute.
	 * 
	 * @return - Returns the budgetRecordingLevelCode
	 * 
	 */
	public String getBudgetRecordingLevelCode() { 
		return budgetRecordingLevelCode;
	}

	/**
	 * Sets the budgetRecordingLevelCode attribute.
	 * 
	 * @param - budgetRecordingLevelCode The budgetRecordingLevelCode to set.
	 * 
	 */
	public void setBudgetRecordingLevelCode(String budgetRecordingLevelCode) {
		this.budgetRecordingLevelCode = budgetRecordingLevelCode;
	}


	/**
	 * Gets the accountSufficientFundsCode attribute.
	 * 
	 * @return - Returns the accountSufficientFundsCode
	 * 
	 */
	public String getAccountSufficientFundsCode() { 
		return accountSufficientFundsCode;
	}

	/**
	 * Sets the accountSufficientFundsCode attribute.
	 * 
	 * @param - accountSufficientFundsCode The accountSufficientFundsCode to set.
	 * 
	 */
	public void setAccountSufficientFundsCode(String accountSufficientFundsCode) {
		this.accountSufficientFundsCode = accountSufficientFundsCode;
	}


	/**
	 * Gets the pendingAcctSufficientFundsIndicator attribute.
	 * 
	 * @return - Returns the pendingAcctSufficientFundsIndicator
	 * 
	 */
	public boolean isPendingAcctSufficientFundsIndicator() { 
		return pendingAcctSufficientFundsIndicator;
	}
	

	/**
	 * Sets the pendingAcctSufficientFundsIndicator attribute.
	 * 
	 * @param - pendingAcctSufficientFundsIndicator The pendingAcctSufficientFundsIndicator to set.
	 * 
	 */
	public void setPendingAcctSufficientFundsIndicator(boolean pendingAcctSufficientFundsIndicator) {
		this.pendingAcctSufficientFundsIndicator = pendingAcctSufficientFundsIndicator;
	}


	/**
	 * Gets the extrnlFinEncumSufficntFndIndicator attribute.
	 * 
	 * @return - Returns the extrnlFinEncumSufficntFndIndicator
	 * 
	 */
	public boolean isExtrnlFinEncumSufficntFndIndicator() { 
		return extrnlFinEncumSufficntFndIndicator;
	}
	

	/**
	 * Sets the extrnlFinEncumSufficntFndIndicator attribute.
	 * 
	 * @param - extrnlFinEncumSufficntFndIndicator The extrnlFinEncumSufficntFndIndicator to set.
	 * 
	 */
	public void setExtrnlFinEncumSufficntFndIndicator(boolean extrnlFinEncumSufficntFndIndicator) {
		this.extrnlFinEncumSufficntFndIndicator = extrnlFinEncumSufficntFndIndicator;
	}


	/**
	 * Gets the intrnlFinEncumSufficntFndIndicator attribute.
	 * 
	 * @return - Returns the intrnlFinEncumSufficntFndIndicator
	 * 
	 */
	public boolean isIntrnlFinEncumSufficntFndIndicator() { 
		return intrnlFinEncumSufficntFndIndicator;
	}
	

	/**
	 * Sets the intrnlFinEncumSufficntFndIndicator attribute.
	 * 
	 * @param - intrnlFinEncumSufficntFndIndicator The intrnlFinEncumSufficntFndIndicator to set.
	 * 
	 */
	public void setIntrnlFinEncumSufficntFndIndicator(boolean intrnlFinEncumSufficntFndIndicator) {
		this.intrnlFinEncumSufficntFndIndicator = intrnlFinEncumSufficntFndIndicator;
	}


	/**
	 * Gets the finPreencumSufficientFundIndicator attribute.
	 * 
	 * @return - Returns the finPreencumSufficientFundIndicator
	 * 
	 */
	public boolean isFinPreencumSufficientFundIndicator() { 
		return finPreencumSufficientFundIndicator;
	}
	

	/**
	 * Sets the finPreencumSufficientFundIndicator attribute.
	 * 
	 * @param - finPreencumSufficientFundIndicator The finPreencumSufficientFundIndicator to set.
	 * 
	 */
	public void setFinPreencumSufficientFundIndicator(boolean finPreencumSufficientFundIndicator) {
		this.finPreencumSufficientFundIndicator = finPreencumSufficientFundIndicator;
	}


	/**
	 * Gets the finObjectPresenceControlIndicator attribute.
	 * 
	 * @return - Returns the finObjectPresenceControlIndicator
	 * 
	 */
	public boolean isFinObjectPresenceControlIndicator() { 
		return finObjectPresenceControlIndicator;
	}
	

	/**
	 * Sets the finObjectPresenceControlIndicator attribute.
	 * 
	 * @param - finObjectPresenceControlIndicator The finObjectPresenceControlIndicator to set.
	 * 
	 */
	public void setFinObjectPresenceControlIndicator(boolean finObjectPresenceControlIndicator) {
		this.finObjectPresenceControlIndicator = finObjectPresenceControlIndicator;
	}


	/**
	 * Gets the cgCatlfFedDomestcAssistNbr attribute.
	 * 
	 * @return - Returns the cgCatlfFedDomestcAssistNbr
	 * 
	 */
	public String getCgCatlfFedDomestcAssistNbr() { 
		return cgCatlfFedDomestcAssistNbr;
	}

	/**
	 * Sets the cgCatlfFedDomestcAssistNbr attribute.
	 * 
	 * @param - cgCatlfFedDomestcAssistNbr The cgCatlfFedDomestcAssistNbr to set.
	 * 
	 */
	public void setCgCatlfFedDomestcAssistNbr(String cgCatlfFedDomestcAssistNbr) {
		this.cgCatlfFedDomestcAssistNbr = cgCatlfFedDomestcAssistNbr;
	}


	/**
	 * Gets the accountOffCampusIndicator attribute.
	 * 
	 * @return - Returns the accountOffCampusIndicator
	 * 
	 */
	public boolean getAccountOffCampusIndicator() { 
		return accountOffCampusIndicator;
	}

	/**
	 * Sets the accountOffCampusIndicator attribute.
	 * 
	 * @param - accountOffCampusIndicator The accountOffCampusIndicator to set.
	 * 
	 */
	public void setAccountOffCampusIndicator(boolean accountOffCampusIndicator) {
		this.accountOffCampusIndicator = accountOffCampusIndicator;
	}


	/**
	 * Gets the accountClosedIndicator attribute.
	 * 
	 * @return - Returns the accountClosedIndicator
	 * 
	 */
	public boolean isAccountClosedIndicator() { 
		return accountClosedIndicator;
	}
	

	/**
	 * Sets the accountClosedIndicator attribute.
	 * 
	 * @param - accountClosedIndicator The accountClosedIndicator to set.
	 * 
	 */
	public void setAccountClosedIndicator(boolean accountClosedIndicator) {
		this.accountClosedIndicator = accountClosedIndicator;
	}


	/**
	 * Gets the chart attribute.
	 * 
	 * @return - Returns the chart
	 * 
	 */
	public Chart getChart() { 
		return chart;
	}

	/**
	 * Sets the chart attribute.
	 * 
	 * @param - chart The chart to set.
	 * @deprecated
	 */
	public void setChart(Chart chart) {
		this.chart = chart;
	}

	/**
	 * Gets the campus attribute.
	 * 
	 * @return - Returns the campus
	 * 
	 */
	public Campus getCampus() { 
		return campus;
	}

	/**
	 * Sets the campus attribute.
	 * 
	 * @param - campus The campus to set.
	 * @deprecated
	 */
	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	/**
	 * Gets the reportsToChartOfAccounts attribute.
	 * 
	 * @return - Returns the reportsToChartOfAccounts
	 * 
	 */
	public Chart getReportsToChartOfAccounts() { 
		return reportsToChartOfAccounts;
	}

	/**
	 * Sets the reportsToChartOfAccounts attribute.
	 * 
	 * @param - reportsToChartOfAccounts The reportsToChartOfAccounts to set.
	 * @deprecated
	 */
	public void setReportsToChartOfAccounts(Chart reportsToChartOfAccounts) {
		this.reportsToChartOfAccounts = reportsToChartOfAccounts;
	}

	/**
	 * Gets the account attribute.
	 * 
	 * @return - Returns the account
	 * 
	 */
	public Account getAccount() { 
		return account;
	}

	/**
	 * Sets the account attribute.
	 * 
	 * @param - account The account to set.
	 * @deprecated
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Gets the continuationFinChrtOfAcct attribute.
	 * 
	 * @return - Returns the continuationFinChrtOfAcct
	 * 
	 */
	public Chart getContinuationFinChrtOfAcct() { 
		return continuationFinChrtOfAcct;
	}

	/**
	 * Sets the continuationFinChrtOfAcct attribute.
	 * 
	 * @param - continuationFinChrtOfAcct The continuationFinChrtOfAcct to set.
	 * @deprecated
	 */
	public void setContinuationFinChrtOfAcct(Chart continuationFinChrtOfAcct) {
		this.continuationFinChrtOfAcct = continuationFinChrtOfAcct;
	}

	/**
	 * Gets the endowmentIncomeAcctFinCoa attribute.
	 * 
	 * @return - Returns the endowmentIncomeAcctFinCoa
	 * 
	 */
	public Chart getEndowmentIncomeAcctFinCoa() { 
		return endowmentIncomeAcctFinCoa;
	}

	/**
	 * Sets the endowmentIncomeAcctFinCoa attribute.
	 * 
	 * @param - endowmentIncomeAcctFinCoa The endowmentIncomeAcctFinCoa to set.
	 * @deprecated
	 */
	public void setEndowmentIncomeAcctFinCoa(Chart endowmentIncomeAcctFinCoa) {
		this.endowmentIncomeAcctFinCoa = endowmentIncomeAcctFinCoa;
	}

	/**
	 * Gets the endowmentIncomeAccount attribute.
	 * 
	 * @return - Returns the endowmentIncomeAccount
	 * 
	 */
	public Account getEndowmentIncomeAccount() { 
		return endowmentIncomeAccount;
	}

	/**
	 * Sets the endowmentIncomeAccount attribute.
	 * 
	 * @param - endowmentIncomeAccount The endowmentIncomeAccount to set.
	 * @deprecated
	 */
	public void setEndowmentIncomeAccount(Account endowmentIncomeAccount) {
		this.endowmentIncomeAccount = endowmentIncomeAccount;
	}

	/**
	 * Gets the contractControlAccount attribute.
	 * 
	 * @return - Returns the contractControlAccount
	 * 
	 */
	public Account getContractControlAccount() { 
		return contractControlAccount;
	}

	/**
	 * Sets the contractControlAccount attribute.
	 * 
	 * @param - contractControlAccount The contractControlAccount to set.
	 * @deprecated
	 */
	public void setContractControlAccount(Account contractControlAccount) {
		this.contractControlAccount = contractControlAccount;
	}

	/**
	 * Gets the contractControlFinCoa attribute.
	 * 
	 * @return - Returns the contractControlFinCoa
	 * 
	 */
	public Chart getContractControlFinCoa() { 
		return contractControlFinCoa;
	}

	/**
	 * Sets the contractControlFinCoa attribute.
	 * 
	 * @param - contractControlFinCoa The contractControlFinCoa to set.
	 * @deprecated
	 */
	public void setContractControlFinCoa(Chart contractControlFinCoa) {
		this.contractControlFinCoa = contractControlFinCoa;
	}

	/**
	 * Gets the incomeStreamFinancialCoa attribute.
	 * 
	 * @return - Returns the incomeStreamFinancialCoa
	 * 
	 */
	public Chart getIncomeStreamFinancialCoa() { 
		return incomeStreamFinancialCoa;
	}

	/**
	 * Sets the incomeStreamFinancialCoa attribute.
	 * 
	 * @param - incomeStreamFinancialCoa The incomeStreamFinancialCoa to set.
	 * @deprecated
	 */
	public void setIncomeStreamFinancialCoa(Chart incomeStreamFinancialCoa) {
		this.incomeStreamFinancialCoa = incomeStreamFinancialCoa;
	}

	/**
	 * Gets the incomeStreamAccount attribute.
	 * 
	 * @return - Returns the incomeStreamAccount
	 * 
	 */
	public Account getIncomeStreamAccount() { 
		return incomeStreamAccount;
	}

	/**
	 * Sets the incomeStreamAccount attribute.
	 * 
	 * @param - incomeStreamAccount The incomeStreamAccount to set.
	 * @deprecated
	 */
	public void setIncomeStreamAccount(Account incomeStreamAccount) {
		this.incomeStreamAccount = incomeStreamAccount;
	}

	/**
	 * Gets the indirectCostRecoveryAcct attribute.
	 * 
	 * @return - Returns the indirectCostRecoveryAcct
	 * 
	 */
	public Account getIndirectCostRecoveryAcct() { 
		return indirectCostRecoveryAcct;
	}

	/**
	 * Sets the indirectCostRecoveryAcct attribute.
	 * 
	 * @param - indirectCostRecoveryAcct The indirectCostRecoveryAcct to set.
	 * @deprecated
	 */
	public void setIndirectCostRecoveryAcct(Account indirectCostRecoveryAcct) {
		this.indirectCostRecoveryAcct = indirectCostRecoveryAcct;
	}

	/**
	 * Gets the indirectCostRcvyFinCoa attribute.
	 * 
	 * @return - Returns the indirectCostRcvyFinCoa
	 * 
	 */
	public Chart getIndirectCostRcvyFinCoa() { 
		return indirectCostRcvyFinCoa;
	}

	/**
	 * Sets the indirectCostRcvyFinCoa attribute.
	 * 
	 * @param - indirectCostRcvyFinCoa The indirectCostRcvyFinCoa to set.
	 * @deprecated
	 */
	public void setIndirectCostRcvyFinCoa(Chart indirectCostRcvyFinCoa) {
		this.indirectCostRcvyFinCoa = indirectCostRcvyFinCoa;
	}

    /**
     * Gets the programCode attribute. 
     * @return Returns the programCode.
     */
    public String getProgramCode() {
        return programCode;
    }

    /**
     * Sets the programCode attribute value.
     * @param programCode The programCode to set.
     */
    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    /**
     * Gets the program attribute. 
     * @return Returns the program.
     */
    public Program getProgram() {
        return program;
    }

    /**
     * Sets the program attribute value.
     * @param program The program to set.
     * @deprecated
     */
    public void setProgram(Program program) {
        this.program = program;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);     
        return m;
    }
    

}
