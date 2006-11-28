/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.chart.bo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.kuali.core.bo.PostalZipCode;
import org.kuali.core.bo.State;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.chart.bo.codes.BudgetRecordingLevelCode;
import org.kuali.module.chart.bo.codes.SufficientFundsCode;


public interface AccountIntf {

    /**
     * This tells if this account is a C&G account.
     * 
     * @return true if C&G account
     */
    public boolean isInCg();

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     * 
     */
    public String getAccountNumber();

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     * 
     */
    public void setAccountNumber(String accountNumber);

    /**
     * Gets the accountName attribute.
     * 
     * @return Returns the accountName
     * 
     */
    public String getAccountName();

    /**
     * Sets the accountName attribute.
     * 
     * @param accountName The accountName to set.
     * 
     */
    public void setAccountName(String accountName);

    /**
     * Gets the _AccountsFringesBnftIndicator_ attribute.
     * 
     * @return Returns the _AccountsFringesBnftIndicator_
     * 
     */
    public boolean isAccountsFringesBnftIndicator();

    /**
     * Sets the _AccountsFringesBnftIndicator_ attribute.
     * 
     * @param _AccountsFringesBnftIndicator_ The _AccountsFringesBnftIndicator_ to set.
     * 
     */
    public void setAccountsFringesBnftIndicator(boolean _AccountsFringesBnftIndicator_);

    /**
     * Gets the accountRestrictedStatusDate attribute.
     * 
     * @return Returns the accountRestrictedStatusDate
     * 
     */
    public Timestamp getAccountRestrictedStatusDate();

    /**
     * Sets the accountRestrictedStatusDate attribute.
     * 
     * @param accountRestrictedStatusDate The accountRestrictedStatusDate to set.
     * 
     */
    public void setAccountRestrictedStatusDate(Timestamp accountRestrictedStatusDate);

    /**
     * Gets the accountCityName attribute.
     * 
     * @return Returns the accountCityName
     * 
     */
    public String getAccountCityName();

    /**
     * Sets the accountCityName attribute.
     * 
     * @param accountCityName The accountCityName to set.
     * 
     */
    public void setAccountCityName(String accountCityName);

    /**
     * Gets the accountStateCode attribute.
     * 
     * @return Returns the accountStateCode
     * 
     */
    public String getAccountStateCode();

    /**
     * Sets the accountStateCode attribute.
     * 
     * @param accountStateCode The accountStateCode to set.
     * 
     */
    public void setAccountStateCode(String accountStateCode);

    /**
     * Gets the accountStreetAddress attribute.
     * 
     * @return Returns the accountStreetAddress
     * 
     */
    public String getAccountStreetAddress();

    /**
     * Sets the accountStreetAddress attribute.
     * 
     * @param accountStreetAddress The accountStreetAddress to set.
     * 
     */
    public void setAccountStreetAddress(String accountStreetAddress);

    /**
     * Gets the accountZipCode attribute.
     * 
     * @return Returns the accountZipCode
     * 
     */
    public String getAccountZipCode();

    /**
     * Sets the accountZipCode attribute.
     * 
     * @param accountZipCode The accountZipCode to set.
     * 
     */
    public void setAccountZipCode(String accountZipCode);

    /**
     * Gets the accountCreateDate attribute.
     * 
     * @return Returns the accountCreateDate
     * 
     */
    public Timestamp getAccountCreateDate();

    /**
     * Sets the accountCreateDate attribute.
     * 
     * @param accountCreateDate The accountCreateDate to set.
     * 
     */
    public void setAccountCreateDate(Timestamp accountCreateDate);

    /**
     * Gets the accountEffectiveDate attribute.
     * 
     * @return Returns the accountEffectiveDate
     * 
     */
    public Timestamp getAccountEffectiveDate();

    /**
     * Sets the accountEffectiveDate attribute.
     * 
     * @param accountEffectiveDate The accountEffectiveDate to set.
     * 
     */
    public void setAccountEffectiveDate(Timestamp accountEffectiveDate);

    /**
     * Gets the accountExpirationDate attribute.
     * 
     * @return Returns the accountExpirationDate
     * 
     */
    public Timestamp getAccountExpirationDate();

    /**
     * Sets the accountExpirationDate attribute.
     * 
     * @param accountExpirationDate The accountExpirationDate to set.
     * 
     */
    public void setAccountExpirationDate(Timestamp accountExpirationDate);

    /**
     * 
     * This method determines whether the account is expired or not.
     * 
     * Note that if Expiration Date is the same as today, then this will return false. It will only return true if the account
     * expiration date is one day earlier than today or earlier.
     * 
     * Note that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on
     * date values, not time-values.
     * 
     * @return true or false based on the logic outlined above
     * 
     */
    public boolean isExpired();

    /**
     * 
     * This method determines whether the account is expired or not.
     * 
     * Note that if Expiration Date is the same date as testDate, then this will return false. It will only return true if the
     * account expiration date is one day earlier than testDate or earlier.
     * 
     * Note that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on
     * date values, not time-values.
     * 
     * @param testDate - Calendar instance with the date to test the Account's Expiration Date against. This is most commonly set to
     *        today's date.
     * @return true or false based on the logic outlined above
     * 
     */
    public boolean isExpired(Calendar testDate);

    /**
     * 
     * This method determines whether the account is expired or not.
     * 
     * Note that if Expiration Date is the same date as testDate, then this will return false. It will only return true if the
     * account expiration date is one day earlier than testDate or earlier.
     * 
     * Note that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on
     * date values, not time-values.
     * 
     * @param testDate - java.util.Date instance with the date to test the Account's Expiration Date against. This is most commonly
     *        set to today's date.
     * @return true or false based on the logic outlined above
     * 
     */
    public boolean isExpired(Date testDate);

    /**
     * Gets the awardPeriodEndYear attribute.
     * 
     * @return Returns the awardPeriodEndYear
     * 
     */
    public Integer getAwardPeriodEndYear();

    /**
     * Sets the awardPeriodEndYear attribute.
     * 
     * @param awardPeriodEndYear The awardPeriodEndYear to set.
     * 
     */
    public void setAwardPeriodEndYear(Integer awardPeriodEndYear);

    /**
     * Gets the awardPeriodEndMonth attribute.
     * 
     * @return Returns the awardPeriodEndMonth
     * 
     */
    public String getAwardPeriodEndMonth();

    /**
     * Sets the awardPeriodEndMonth attribute.
     * 
     * @param awardPeriodEndMonth The awardPeriodEndMonth to set.
     * 
     */
    public void setAwardPeriodEndMonth(String awardPeriodEndMonth);

    /**
     * Gets the awardPeriodBeginYear attribute.
     * 
     * @return Returns the awardPeriodBeginYear
     * 
     */
    public Integer getAwardPeriodBeginYear();

    /**
     * Sets the awardPeriodBeginYear attribute.
     * 
     * @param awardPeriodBeginYear The awardPeriodBeginYear to set.
     * 
     */
    public void setAwardPeriodBeginYear(Integer awardPeriodBeginYear);

    /**
     * Gets the awardPeriodBeginMonth attribute.
     * 
     * @return Returns the awardPeriodBeginMonth
     * 
     */
    public String getAwardPeriodBeginMonth();

    /**
     * Sets the awardPeriodBeginMonth attribute.
     * 
     * @param awardPeriodBeginMonth The awardPeriodBeginMonth to set.
     * 
     */
    public void setAwardPeriodBeginMonth(String awardPeriodBeginMonth);

    /**
     * Gets the acctIndirectCostRcvyTypeCd attribute.
     * 
     * @return Returns the acctIndirectCostRcvyTypeCd
     * 
     */
    public String getAcctIndirectCostRcvyTypeCd();

    /**
     * Sets the acctIndirectCostRcvyTypeCd attribute.
     * 
     * @param acctIndirectCostRcvyTypeCd The acctIndirectCostRcvyTypeCd to set.
     * 
     */
    public void setAcctIndirectCostRcvyTypeCd(String acctIndirectCostRcvyTypeCd);

    /**
     * Gets the acctCustomIndCstRcvyExclCd attribute.
     * 
     * @return Returns the acctCustomIndCstRcvyExclCd
     * 
     */
    public String getAcctCustomIndCstRcvyExclCd();

    /**
     * Sets the acctCustomIndCstRcvyExclCd attribute.
     * 
     * @param acctCustomIndCstRcvyExclCd The acctCustomIndCstRcvyExclCd to set.
     * 
     */
    public void setAcctCustomIndCstRcvyExclCd(String acctCustomIndCstRcvyExclCd);

    /**
     * Gets the financialIcrSeriesIdentifier attribute.
     * 
     * @return Returns the financialIcrSeriesIdentifier
     * 
     */
    public String getFinancialIcrSeriesIdentifier();

    /**
     * Sets the financialIcrSeriesIdentifier attribute.
     * 
     * @param financialIcrSeriesIdentifier The financialIcrSeriesIdentifier to set.
     * 
     */
    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier);

    /**
     * Gets the accountInFinancialProcessingIndicator attribute.
     * 
     * @return Returns the accountInFinancialProcessingIndicator
     * 
     */
    public boolean getAccountInFinancialProcessingIndicator();

    /**
     * Sets the accountInFinancialProcessingIndicator attribute.
     * 
     * @param accountInFinancialProcessingIndicator The accountInFinancialProcessingIndicator to set.
     * 
     */
    public void setAccountInFinancialProcessingIndicator(boolean accountInFinancialProcessingIndicator);

    /**
     * Gets the budgetRecordingLevelCode attribute.
     * 
     * @return Returns the budgetRecordingLevelCode
     * 
     */
    public String getBudgetRecordingLevelCode();

    /**
     * Sets the budgetRecordingLevelCode attribute.
     * 
     * @param budgetRecordingLevelCode The budgetRecordingLevelCode to set.
     * 
     */
    public void setBudgetRecordingLevelCode(String budgetRecordingLevelCode);

    /**
     * Gets the accountSufficientFundsCode attribute.
     * 
     * @return Returns the accountSufficientFundsCode
     * 
     */
    public String getAccountSufficientFundsCode();

    /**
     * Sets the accountSufficientFundsCode attribute.
     * 
     * @param accountSufficientFundsCode The accountSufficientFundsCode to set.
     * 
     */
    public void setAccountSufficientFundsCode(String accountSufficientFundsCode);

    /**
     * Gets the pendingAcctSufficientFundsIndicator attribute.
     * 
     * @return Returns the pendingAcctSufficientFundsIndicator
     * 
     */
    public boolean isPendingAcctSufficientFundsIndicator();

    /**
     * Sets the pendingAcctSufficientFundsIndicator attribute.
     * 
     * @param pendingAcctSufficientFundsIndicator The pendingAcctSufficientFundsIndicator to set.
     * 
     */
    public void setPendingAcctSufficientFundsIndicator(boolean pendingAcctSufficientFundsIndicator);

    /**
     * Gets the extrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @return Returns the extrnlFinEncumSufficntFndIndicator
     * 
     */
    public boolean isExtrnlFinEncumSufficntFndIndicator();

    /**
     * Sets the extrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @param extrnlFinEncumSufficntFndIndicator The extrnlFinEncumSufficntFndIndicator to set.
     * 
     */
    public void setExtrnlFinEncumSufficntFndIndicator(boolean extrnlFinEncumSufficntFndIndicator);

    /**
     * Gets the intrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @return Returns the intrnlFinEncumSufficntFndIndicator
     * 
     */
    public boolean isIntrnlFinEncumSufficntFndIndicator();

    /**
     * Sets the intrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @param intrnlFinEncumSufficntFndIndicator The intrnlFinEncumSufficntFndIndicator to set.
     * 
     */
    public void setIntrnlFinEncumSufficntFndIndicator(boolean intrnlFinEncumSufficntFndIndicator);

    /**
     * Gets the finPreencumSufficientFundIndicator attribute.
     * 
     * @return Returns the finPreencumSufficientFundIndicator
     * 
     */
    public boolean isFinPreencumSufficientFundIndicator();

    /**
     * Sets the finPreencumSufficientFundIndicator attribute.
     * 
     * @param finPreencumSufficientFundIndicator The finPreencumSufficientFundIndicator to set.
     * 
     */
    public void setFinPreencumSufficientFundIndicator(boolean finPreencumSufficientFundIndicator);

    /**
     * Gets the _FinancialObjectivePrsctrlIndicator_ attribute.
     * 
     * @return Returns the _FinancialObjectivePrsctrlIndicator_
     * 
     */
    public boolean isFinancialObjectivePrsctrlIndicator();

    /**
     * Sets the _FinancialObjectivePrsctrlIndicator_ attribute.
     * 
     * @param _FinancialObjectivePrsctrlIndicator_ The _FinancialObjectivePrsctrlIndicator_ to set.
     * 
     */
    public void setFinancialObjectivePrsctrlIndicator(boolean _FinancialObjectivePrsctrlIndicator_);

    /**
     * Gets the cgCatlfFedDomestcAssistNbr attribute.
     * 
     * @return Returns the cgCatlfFedDomestcAssistNbr
     * 
     */
    public String getCgCatlfFedDomestcAssistNbr();

    /**
     * Sets the cgCatlfFedDomestcAssistNbr attribute.
     * 
     * @param cgCatlfFedDomestcAssistNbr The cgCatlfFedDomestcAssistNbr to set.
     * 
     */
    public void setCgCatlfFedDomestcAssistNbr(String cgCatlfFedDomestcAssistNbr);

    /**
     * Gets the accountOffCampusIndicator attribute.
     * 
     * @return Returns the accountOffCampusIndicator
     * 
     */
    public boolean isAccountOffCampusIndicator();

    /**
     * Sets the accountOffCampusIndicator attribute.
     * 
     * @param accountOffCampusIndicator The accountOffCampusIndicator to set.
     * 
     */
    public void setAccountOffCampusIndicator(boolean accountOffCampusIndicator);

    /**
     * Gets the accountClosedIndicator attribute.
     * 
     * @return Returns the accountClosedIndicator
     * 
     */
    public boolean isAccountClosedIndicator();

    /**
     * Sets the accountClosedIndicator attribute.
     * 
     * @param accountClosedIndicator The accountClosedIndicator to set.
     * 
     */
    public void setAccountClosedIndicator(boolean accountClosedIndicator);

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts();

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts);

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     * 
     */
    public Org getOrganization();

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Org organization);

    /**
     * Gets the accountType attribute.
     * 
     * @return Returns the accountType
     * 
     */
    public AcctType getAccountType();

    /**
     * Sets the accountType attribute.
     * 
     * @param accountType The accountType to set.
     * @deprecated
     */
    public void setAccountType(AcctType accountType);

    /**
     * Gets the accountPhysicalCampus attribute.
     * 
     * @return Returns the accountPhysicalCampus
     * 
     */
    public Campus getAccountPhysicalCampus();

    /**
     * Sets the accountPhysicalCampus attribute.
     * 
     * @param accountPhysicalCampus The accountPhysicalCampus to set.
     * @deprecated
     */
    public void setAccountPhysicalCampus(Campus accountPhysicalCampus);

    /**
     * Gets the accountState attribute
     * 
     * @return Returns the accountState
     */
    public State getAccountState();

    /**
     * Sets the accountState attribute
     * 
     * @param state
     * @deprecated
     */
    public void setAccountState(State state);

    /**
     * Gets the subFundGroup attribute.
     * 
     * @return Returns the subFundGroup
     * 
     */
    public SubFundGroup getSubFundGroup();

    /**
     * Sets the subFundGroup attribute.
     * 
     * @param subFundGroup The subFundGroup to set.
     * @deprecated
     */
    public void setSubFundGroup(SubFundGroup subFundGroup);

    /**
     * Gets the financialHigherEdFunction attribute.
     * 
     * @return Returns the financialHigherEdFunction
     * 
     */
    public HigherEdFunction getFinancialHigherEdFunction();

    /**
     * Sets the financialHigherEdFunction attribute.
     * 
     * @param financialHigherEdFunction The financialHigherEdFunction to set.
     * @deprecated
     */
    public void setFinancialHigherEdFunction(HigherEdFunction financialHigherEdFunction);

    /**
     * Gets the accountRestrictedStatus attribute.
     * 
     * @return Returns the accountRestrictedStatus
     * 
     */
    public RestrictedStatus getAccountRestrictedStatus();

    /**
     * Sets the accountRestrictedStatus attribute.
     * 
     * @param accountRestrictedStatus The accountRestrictedStatus to set.
     * @deprecated
     */
    public void setAccountRestrictedStatus(RestrictedStatus accountRestrictedStatus);

    /**
     * Gets the reportsToAccount attribute.
     * 
     * @return Returns the reportsToAccount
     * 
     */
    public Account getReportsToAccount();

    /**
     * Sets the reportsToAccount attribute.
     * 
     * @param reportsToAccount The reportsToAccount to set.
     * @deprecated
     */
    public void setReportsToAccount(Account reportsToAccount);

    /**
     * Gets the endowmentIncomeAccount attribute.
     * 
     * @return Returns the endowmentIncomeAccount
     * 
     */
    public Account getEndowmentIncomeAccount();

    /**
     * Sets the endowmentIncomeAccount attribute.
     * 
     * @param endowmentIncomeAccount The endowmentIncomeAccount to set.
     * @deprecated
     */
    public void setEndowmentIncomeAccount(Account endowmentIncomeAccount);

    /**
     * Gets the contractControlAccount attribute.
     * 
     * @return Returns the contractControlAccount
     * 
     */
    public Account getContractControlAccount();

    /**
     * Sets the contractControlAccount attribute.
     * 
     * @param contractControlAccount The contractControlAccount to set.
     * @deprecated
     */
    public void setContractControlAccount(Account contractControlAccount);

    /**
     * Gets the incomeStreamAccount attribute.
     * 
     * @return Returns the incomeStreamAccount
     * 
     */
    public Account getIncomeStreamAccount();

    /**
     * Sets the incomeStreamAccount attribute.
     * 
     * @param incomeStreamAccount The incomeStreamAccount to set.
     * @deprecated
     */
    public void setIncomeStreamAccount(Account incomeStreamAccount);

    /**
     * Gets the indirectCostRecoveryAcct attribute.
     * 
     * @return Returns the indirectCostRecoveryAcct
     * 
     */
    public Account getIndirectCostRecoveryAcct();

    /**
     * Sets the indirectCostRecoveryAcct attribute.
     * 
     * @param indirectCostRecoveryAcct The indirectCostRecoveryAcct to set.
     * @deprecated
     */
    public void setIndirectCostRecoveryAcct(Account indirectCostRecoveryAcct);

    /**
     * @return Returns the accountFiscalOfficerUser.
     */
    public UniversalUser getAccountFiscalOfficerUser();

    /**
     * @param accountFiscalOfficerUser The accountFiscalOfficerUser to set.
     * @deprecated
     */
    public void setAccountFiscalOfficerUser(UniversalUser accountFiscalOfficerUser);

    /**
     * @return Returns the accountManagerUser.
     */
    public UniversalUser getAccountManagerUser();

    /**
     * @param accountManagerUser The accountManagerUser to set.
     * @deprecated
     */
    public void setAccountManagerUser(UniversalUser accountManagerUser);

    /**
     * @return Returns the accountSupervisoryUser.
     */
    public UniversalUser getAccountSupervisoryUser();

    /**
     * @param accountSupervisoryUser The accountSupervisoryUser to set.
     * @deprecated
     */
    public void setAccountSupervisoryUser(UniversalUser accountSupervisoryUser);

    /**
     * @return Returns the continuationAccount.
     */
    public Account getContinuationAccount();

    /**
     * @param continuationAccount The continuationAccount to set.
     * @deprecated
     */
    public void setContinuationAccount(Account continuationAccount);

    /**
     * @return Returns the program.
     */
    public Program getProgram();

    /**
     * @param program The program to set.
     * @deprecated
     */
    public void setProgram(Program program);

    /**
     * @return Returns the accountGuideline.
     */
    public AccountGuideline getAccountGuideline();

    /**
     * @param accountGuideline The accountGuideline to set.
     * @deprecated
     */
    public void setAccountGuideline(AccountGuideline accountGuideline);

    /**
     * Gets the accountDescription attribute.
     * 
     * @return Returns the accountDescription.
     */
    public AccountDescription getAccountDescription();

    /**
     * Sets the accountDescription attribute value.
     * 
     * @param accountDescription The accountDescription to set.
     */
    public void setAccountDescription(AccountDescription accountDescription);

    /**
     * @return Returns the subAccounts.
     */
    public List getSubAccounts();

    /**
     * @param subAccounts The subAccounts to set.
     */
    public void setSubAccounts(List subAccounts);

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode();

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode);

    /**
     * @return Returns the accountFiscalOfficerSystemIdentifier.
     */
    public String getAccountFiscalOfficerSystemIdentifier();

    /**
     * @param accountFiscalOfficerSystemIdentifier The accountFiscalOfficerSystemIdentifier to set.
     */
    public void setAccountFiscalOfficerSystemIdentifier(String accountFiscalOfficerSystemIdentifier);

    /**
     * @return Returns the accountManagerSystemIdentifier.
     */
    public String getAccountManagerSystemIdentifier();

    /**
     * @param accountManagerSystemIdentifier The accountManagerSystemIdentifier to set.
     */
    public void setAccountManagerSystemIdentifier(String accountManagerSystemIdentifier);

    /**
     * @return Returns the accountPhysicalCampusCode.
     */
    public String getAccountPhysicalCampusCode();

    /**
     * @param accountPhysicalCampusCode The accountPhysicalCampusCode to set.
     */
    public void setAccountPhysicalCampusCode(String accountPhysicalCampusCode);

    /**
     * @return Returns the accountRestrictedStatusCode.
     */
    public String getAccountRestrictedStatusCode();

    /**
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     */
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode);

    /**
     * @return Returns the accountsSupervisorySystemsIdentifier.
     */
    public String getAccountsSupervisorySystemsIdentifier();

    /**
     * @param accountsSupervisorySystemsIdentifier The accountsSupervisorySystemsIdentifier to set.
     */
    public void setAccountsSupervisorySystemsIdentifier(String accountsSupervisorySystemsIdentifier);

    /**
     * @return Returns the accountTypeCode.
     */
    public String getAccountTypeCode();

    /**
     * @param accountTypeCode The accountTypeCode to set.
     */
    public void setAccountTypeCode(String accountTypeCode);

    /**
     * @return Returns the continuationAccountNumber.
     */
    public String getContinuationAccountNumber();

    /**
     * @param continuationAccountNumber The continuationAccountNumber to set.
     */
    public void setContinuationAccountNumber(String continuationAccountNumber);

    /**
     * @return Returns the continuationFinChrtOfAcctCd.
     */
    public String getContinuationFinChrtOfAcctCd();

    /**
     * @param continuationFinChrtOfAcctCd The continuationFinChrtOfAcctCd to set.
     */
    public void setContinuationFinChrtOfAcctCd(String continuationFinChrtOfAcctCd);

    /**
     * @return Returns the contractControlAccountNumber.
     */
    public String getContractControlAccountNumber();

    /**
     * @param contractControlAccountNumber The contractControlAccountNumber to set.
     */
    public void setContractControlAccountNumber(String contractControlAccountNumber);

    /**
     * @return Returns the contractControlFinCoaCode.
     */
    public String getContractControlFinCoaCode();

    /**
     * @param contractControlFinCoaCode The contractControlFinCoaCode to set.
     */
    public void setContractControlFinCoaCode(String contractControlFinCoaCode);

    /**
     * @return Returns the endowmentIncomeAccountNumber.
     */
    public String getEndowmentIncomeAccountNumber();

    /**
     * @param endowmentIncomeAccountNumber The endowmentIncomeAccountNumber to set.
     */
    public void setEndowmentIncomeAccountNumber(String endowmentIncomeAccountNumber);

    /**
     * @return Returns the endowmentIncomeAcctFinCoaCd.
     */
    public String getEndowmentIncomeAcctFinCoaCd();

    /**
     * @param endowmentIncomeAcctFinCoaCd The endowmentIncomeAcctFinCoaCd to set.
     */
    public void setEndowmentIncomeAcctFinCoaCd(String endowmentIncomeAcctFinCoaCd);

    /**
     * @return Returns the financialHigherEdFunctionCd.
     */
    public String getFinancialHigherEdFunctionCd();

    /**
     * @param financialHigherEdFunctionCd The financialHigherEdFunctionCd to set.
     */
    public void setFinancialHigherEdFunctionCd(String financialHigherEdFunctionCd);

    /**
     * @return Returns the incomeStreamAccountNumber.
     */
    public String getIncomeStreamAccountNumber();

    /**
     * @param incomeStreamAccountNumber The incomeStreamAccountNumber to set.
     */
    public void setIncomeStreamAccountNumber(String incomeStreamAccountNumber);

    /**
     * @return Returns the incomeStreamFinancialCoaCode.
     */
    public String getIncomeStreamFinancialCoaCode();

    /**
     * @param incomeStreamFinancialCoaCode The incomeStreamFinancialCoaCode to set.
     */
    public void setIncomeStreamFinancialCoaCode(String incomeStreamFinancialCoaCode);

    /**
     * @return Returns the indirectCostRcvyFinCoaCode.
     */
    public String getIndirectCostRcvyFinCoaCode();

    /**
     * @param indirectCostRcvyFinCoaCode The indirectCostRcvyFinCoaCode to set.
     */
    public void setIndirectCostRcvyFinCoaCode(String indirectCostRcvyFinCoaCode);

    /**
     * @return Returns the indirectCostRecoveryAcctNbr.
     */
    public String getIndirectCostRecoveryAcctNbr();

    /**
     * @param indirectCostRecoveryAcctNbr The indirectCostRecoveryAcctNbr to set.
     */
    public void setIndirectCostRecoveryAcctNbr(String indirectCostRecoveryAcctNbr);

    /**
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode();

    /**
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode);

    /**
     * @return Returns the reportsToAccountNumber.
     */
    public String getReportsToAccountNumber();

    /**
     * @param reportsToAccountNumber The reportsToAccountNumber to set.
     */
    public void setReportsToAccountNumber(String reportsToAccountNumber);

    /**
     * @return Returns the reportsToChartOfAccountsCode.
     */
    public String getReportsToChartOfAccountsCode();

    /**
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode);

    /**
     * @return Returns the subFundGroupCode.
     */
    public String getSubFundGroupCode();

    /**
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode);

    /**
     * Gets the postalZipCode attribute.
     * 
     * @return Returns the postalZipCode.
     */
    public PostalZipCode getPostalZipCode();

    /**
     * Sets the postalZipCode attribute value.
     * 
     * @param postalZipCode The postalZipCode to set.
     */
    public void setPostalZipCode(PostalZipCode postalZipCode);

    /**
     * Gets the budgetRecordingLevel attribute.
     * 
     * @return Returns the budgetRecordingLevel.
     */
    public BudgetRecordingLevelCode getBudgetRecordingLevel();

    /**
     * Sets the budgetRecordingLevel attribute value.
     * 
     * @param budgetRecordingLevel The budgetRecordingLevel to set.
     */
    public void setBudgetRecordingLevel(BudgetRecordingLevelCode budgetRecordingLevel);

    /**
     * Gets the sufficientFundsCode attribute.
     * 
     * @return Returns the sufficientFundsCode.
     */
    public SufficientFundsCode getSufficientFundsCode();

    /**
     * Sets the sufficientFundsCode attribute value.
     * 
     * @param sufficientFundsCode The sufficientFundsCode to set.
     */
    public void setSufficientFundsCode(SufficientFundsCode sufficientFundsCode);

    /**
     * @return Returns the programCode.
     */
    public String getProgramCode();

    /**
     * @param programCode The programCode to set.
     */
    public void setProgramCode(String programCode);

    /**
     * Implementing equals since I need contains to behave reasonably in a hashed datastructure.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj);

    /**
     * Calcluates hashCode based on current values of chartOfAccountsCode and accountNumber fields. Somewhat dangerous, since both
     * of those fields are mutable, but I don't expect people to be editing those values directly for Accounts stored in hashed
     * datastructures.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode();

    /**
     * Convenience method to make the primitive account fields from this Account easier to compare to the account fields from
     * another Account or an AccountingLine
     * 
     * @return String representing the account associated with this Accounting
     */
    public String getAccountKey();

    /**
     * Gets the dummy attribute.
     * 
     * @return Returns the dummy.
     */

    /**
     * Gets the accountResponsibilitySection attribute.
     * 
     * @return Returns the accountResponsibilitySection.
     */
    public String getAccountResponsibilitySection();

    /**
     * Sets the accountResponsibilitySection attribute value.
     * 
     * @param accountResponsibilitySection The accountResponsibilitySection to set.
     */
    public void setAccountResponsibilitySection(String accountResponsibilitySection);

    /**
     * Gets the contractsAndGrantsSection attribute.
     * 
     * @return Returns the contractsAndGrantsSection.
     */
    public String getContractsAndGrantsSection();

    /**
     * Sets the contractsAndGrantsSection attribute value.
     * 
     * @param contractsAndGrantsSection The contractsAndGrantsSection to set.
     */
    public void setContractsAndGrantsSection(String contractsAndGrantsSection);

    /**
     * Gets the accountDescriptionSection attribute.
     * 
     * @return Returns the accountDescriptionSection.
     */
    public String getAccountDescriptionSection();

    /**
     * Sets the accountDescriptionSection attribute value.
     * 
     * @param accountDescriptionSection The accountDescriptionSection to set.
     */
    public void setAccountDescriptionSection(String accountDescriptionSection);

    /**
     * Gets the guidelinesAndPurposeSection attribute.
     * 
     * @return Returns the guidelinesAndPurposeSection.
     */
    public String getGuidelinesAndPurposeSection();

    /**
     * Sets the guidelinesAndPurposeSection attribute value.
     * 
     * @param guidelinesAndPurposeSection The guidelinesAndPurposeSection to set.
     */
    public void setGuidelinesAndPurposeSection(String guidelinesAndPurposeSection);

    /**
     * Gets the accountResponsibilitySectionBlank attribute.
     * 
     * @return Returns the accountResponsibilitySectionBlank.
     */
    public String getAccountResponsibilitySectionBlank();

    /**
     * Gets the contractsAndGrantsSectionBlank attribute.
     * 
     * @return Returns the contractsAndGrantsSectionBlank.
     */
    public String getContractsAndGrantsSectionBlank();

    /**
     * Gets the accountDescriptionSectionBlank attribute.
     * 
     * @return Returns the accountDescriptionSectionBlank.
     */
    public String getAccountDescriptionSectionBlank();

    /**
     * Gets the guidelinesAndPurposeSectionBlank attribute.
     * 
     * @return Returns the guidelinesAndPurposeSectionBlank.
     */
    public String getGuidelinesAndPurposeSectionBlank();

}