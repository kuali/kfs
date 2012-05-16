/*
 * Copyright 2005-2006 The Kuali Foundation
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

package org.kuali.kfs.coa.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 *
 */
public class PriorYearAccount extends PersistableBusinessObjectBase implements AccountIntf, MutableInactivatable {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PriorYearAccount.class);

    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String accountName;
    protected boolean accountsFringesBnftIndicator;
    protected Date accountRestrictedStatusDate;
    protected String accountCityName;
    protected String accountStateCode;
    protected String accountStreetAddress;
    protected String accountZipCode;
    protected Date accountCreateDate;
    protected Date accountEffectiveDate;
    protected Date accountExpirationDate;
    protected String acctIndirectCostRcvyTypeCd;
    protected String acctCustomIndCstRcvyExclCd;
    protected String financialIcrSeriesIdentifier;
    protected boolean accountInFinancialProcessingIndicator;
    protected String budgetRecordingLevelCode;
    protected String accountSufficientFundsCode;
    protected boolean pendingAcctSufficientFundsIndicator;
    protected boolean extrnlFinEncumSufficntFndIndicator;
    protected boolean intrnlFinEncumSufficntFndIndicator;
    protected boolean finPreencumSufficientFundIndicator;
    protected boolean financialObjectivePrsctrlIndicator;
    protected String accountCfdaNumber;
    protected boolean accountOffCampusIndicator;
    protected boolean active;

    protected String accountFiscalOfficerSystemIdentifier;
    protected String accountsSupervisorySystemsIdentifier;
    protected String accountManagerSystemIdentifier;
    protected String organizationCode;
    protected String accountTypeCode;
    protected String accountPhysicalCampusCode;
    protected String subFundGroupCode;
    protected String financialHigherEdFunctionCd;
    protected String accountRestrictedStatusCode;
    protected String reportsToChartOfAccountsCode;
    protected String reportsToAccountNumber;
    protected String continuationFinChrtOfAcctCd;
    protected String continuationAccountNumber;
    protected String endowmentIncomeAcctFinCoaCd;
    protected String endowmentIncomeAccountNumber;
    protected String contractControlFinCoaCode;
    protected String contractControlAccountNumber;
    protected String incomeStreamFinancialCoaCode;
    protected String incomeStreamAccountNumber;

    protected Chart chartOfAccounts;
    protected Organization organization;
    protected AccountType accountType;
    protected CampusEbo accountPhysicalCampus;
    protected StateEbo accountState;
    protected SubFundGroup subFundGroup;
    protected HigherEducationFunction financialHigherEdFunction;
    protected RestrictedStatus accountRestrictedStatus;
    protected Account reportsToAccount;
    protected Account continuationAccount;
    protected Account endowmentIncomeAccount;
    protected Account contractControlAccount;
    protected Account incomeStreamAccount;
    protected Person accountFiscalOfficerUser;
    protected Person accountSupervisoryUser;
    protected Person accountManagerUser;
    protected PostalCodeEbo postalZipCode;
    protected BudgetRecordingLevel budgetRecordingLevel;
    protected SufficientFundsCode sufficientFundsCode;

    // Several kinds of Dummy Attributes for dividing sections on Inquiry page
    protected String accountResponsibilitySectionBlank;
    protected String accountResponsibilitySection;
    protected String contractsAndGrantsSectionBlank;
    protected String contractsAndGrantsSection;
    protected String guidelinesAndPurposeSectionBlank;
    protected String guidelinesAndPurposeSection;
    protected String accountDescriptionSectionBlank;
    protected String accountDescriptionSection;

    protected AccountGuideline accountGuideline;
    protected AccountDescription accountDescription;

    protected List subAccounts;
    protected Boolean forContractsAndGrants;

    protected List<PriorYearIndirectCostRecoveryAccount> indirectCostRecoveryAccounts;

    /**
     * Default no-arg constructor.
     */
    public PriorYearAccount() {
        indirectCostRecoveryAccounts = new ArrayList<PriorYearIndirectCostRecoveryAccount>();
    }

    /**
     * Constructs a PriorYearAccount by populating fields from an Account object.
     */
    public PriorYearAccount(Account account) {
        chartOfAccountsCode = account.getChartOfAccountsCode();
        accountNumber = account.getAccountNumber();
        accountName = account.getAccountName();
        accountsFringesBnftIndicator = account.isAccountsFringesBnftIndicator();
        accountRestrictedStatusDate = account.getAccountRestrictedStatusDate();
        accountCityName = account.getAccountCityName();
        accountStateCode = account.getAccountStateCode();
        accountStreetAddress = account.getAccountStreetAddress();
        accountZipCode = account.getAccountZipCode();
        accountCreateDate = account.getAccountCreateDate();
        accountEffectiveDate = account.getAccountEffectiveDate();
        accountExpirationDate = account.getAccountExpirationDate();
        acctIndirectCostRcvyTypeCd = account.getAcctIndirectCostRcvyTypeCd();
        acctCustomIndCstRcvyExclCd = account.getAcctCustomIndCstRcvyExclCd();
        financialIcrSeriesIdentifier = account.getFinancialIcrSeriesIdentifier();
        accountInFinancialProcessingIndicator = account.getAccountInFinancialProcessingIndicator();
        budgetRecordingLevelCode = account.getBudgetRecordingLevelCode();
        accountSufficientFundsCode = account.getAccountSufficientFundsCode();
        pendingAcctSufficientFundsIndicator = account.isPendingAcctSufficientFundsIndicator();
        extrnlFinEncumSufficntFndIndicator = account.isExtrnlFinEncumSufficntFndIndicator();
        intrnlFinEncumSufficntFndIndicator = account.isIntrnlFinEncumSufficntFndIndicator();
        finPreencumSufficientFundIndicator = account.isFinPreencumSufficientFundIndicator();
        financialObjectivePrsctrlIndicator = account.isFinancialObjectivePrsctrlIndicator();
        accountCfdaNumber = account.getAccountCfdaNumber();
        accountOffCampusIndicator = account.isAccountOffCampusIndicator();
        active = account.isActive();

        accountFiscalOfficerSystemIdentifier = account.getAccountFiscalOfficerSystemIdentifier();
        accountsSupervisorySystemsIdentifier = account.getAccountsSupervisorySystemsIdentifier();
        accountManagerSystemIdentifier = account.getAccountManagerSystemIdentifier();
        organizationCode = account.getOrganizationCode();
        accountTypeCode = account.getAccountTypeCode();
        accountPhysicalCampusCode = account.getAccountPhysicalCampusCode();
        subFundGroupCode = account.getSubFundGroupCode();
        financialHigherEdFunctionCd = account.getFinancialHigherEdFunctionCd();
        accountRestrictedStatusCode = account.getAccountRestrictedStatusCode();
        reportsToChartOfAccountsCode = account.getReportsToChartOfAccountsCode();
        reportsToAccountNumber = account.getReportsToAccountNumber();
        continuationFinChrtOfAcctCd = account.getContinuationFinChrtOfAcctCd();
        continuationAccountNumber = account.getContinuationAccountNumber();
        endowmentIncomeAcctFinCoaCd = account.getEndowmentIncomeAcctFinCoaCd();
        endowmentIncomeAccountNumber = account.getEndowmentIncomeAccountNumber();
        contractControlFinCoaCode = account.getContractControlFinCoaCode();
        contractControlAccountNumber = account.getContractControlAccountNumber();
        incomeStreamFinancialCoaCode = account.getIncomeStreamFinancialCoaCode();
        incomeStreamAccountNumber = account.getIncomeStreamAccountNumber();
        
        refresh();
    }
    
    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber
     */
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    @Override
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the accountName attribute.
     *
     * @return Returns the accountName
     */
    @Override
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the accountName attribute.
     *
     * @param accountName The accountName to set.
     */
    @Override
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * Gets the _AccountsFringesBnftIndicator_ attribute.
     *
     * @return Returns the _AccountsFringesBnftIndicator_
     */
    @Override
    public boolean isAccountsFringesBnftIndicator() {
        return accountsFringesBnftIndicator;
    }

    /**
     * Sets the _AccountsFringesBnftIndicator_ attribute.
     *
     * @param _AccountsFringesBnftIndicator_ The _AccountsFringesBnftIndicator_ to set.
     */
    @Override
    public void setAccountsFringesBnftIndicator(boolean _AccountsFringesBnftIndicator_) {
        this.accountsFringesBnftIndicator = _AccountsFringesBnftIndicator_;
    }

    /**
     * Gets the accountRestrictedStatusDate attribute.
     *
     * @return Returns the accountRestrictedStatusDate
     */
    @Override
    public Date getAccountRestrictedStatusDate() {
        return accountRestrictedStatusDate;
    }

    /**
     * Sets the accountRestrictedStatusDate attribute.
     *
     * @param accountRestrictedStatusDate The accountRestrictedStatusDate to set.
     */
    @Override
    public void setAccountRestrictedStatusDate(Date accountRestrictedStatusDate) {
        this.accountRestrictedStatusDate = accountRestrictedStatusDate;
    }

    /**
     * Gets the accountCityName attribute.
     *
     * @return Returns the accountCityName
     */
    @Override
    public String getAccountCityName() {
        return accountCityName;
    }

    /**
     * Sets the accountCityName attribute.
     *
     * @param accountCityName The accountCityName to set.
     */
    @Override
    public void setAccountCityName(String accountCityName) {
        this.accountCityName = accountCityName;
    }

    /**
     * Gets the accountStateCode attribute.
     *
     * @return Returns the accountStateCode
     */
    @Override
    public String getAccountStateCode() {
        return accountStateCode;
    }

    /**
     * Sets the accountStateCode attribute.
     *
     * @param accountStateCode The accountStateCode to set.
     */
    @Override
    public void setAccountStateCode(String accountStateCode) {
        this.accountStateCode = accountStateCode;
    }

    /**
     * Gets the accountStreetAddress attribute.
     *
     * @return Returns the accountStreetAddress
     */
    @Override
    public String getAccountStreetAddress() {
        return accountStreetAddress;
    }

    /**
     * Sets the accountStreetAddress attribute.
     *
     * @param accountStreetAddress The accountStreetAddress to set.
     */
    @Override
    public void setAccountStreetAddress(String accountStreetAddress) {
        this.accountStreetAddress = accountStreetAddress;
    }

    /**
     * Gets the accountZipCode attribute.
     *
     * @return Returns the accountZipCode
     */
    @Override
    public String getAccountZipCode() {
        return accountZipCode;
    }

    /**
     * Sets the accountZipCode attribute.
     *
     * @param accountZipCode The accountZipCode to set.
     */
    @Override
    public void setAccountZipCode(String accountZipCode) {
        this.accountZipCode = accountZipCode;
    }

    /**
     * Gets the accountCreateDate attribute.
     *
     * @return Returns the accountCreateDate
     */
    @Override
    public Date getAccountCreateDate() {
        return accountCreateDate;
    }

    /**
     * Sets the accountCreateDate attribute.
     *
     * @param accountCreateDate The accountCreateDate to set.
     */
    @Override
    public void setAccountCreateDate(Date accountCreateDate) {
        this.accountCreateDate = accountCreateDate;
    }

    /**
     * Gets the accountEffectiveDate attribute.
     *
     * @return Returns the accountEffectiveDate
     */
    @Override
    public Date getAccountEffectiveDate() {
        return accountEffectiveDate;
    }

    /**
     * Sets the accountEffectiveDate attribute.
     *
     * @param accountEffectiveDate The accountEffectiveDate to set.
     */
    @Override
    public void setAccountEffectiveDate(Date accountEffectiveDate) {
        this.accountEffectiveDate = accountEffectiveDate;
    }

    /**
     * Gets the accountExpirationDate attribute.
     *
     * @return Returns the accountExpirationDate
     */
    @Override
    public Date getAccountExpirationDate() {
        return accountExpirationDate;
    }

    /**
     * Sets the accountExpirationDate attribute.
     *
     * @param accountExpirationDate The accountExpirationDate to set.
     */
    @Override
    public void setAccountExpirationDate(Date accountExpirationDate) {
        this.accountExpirationDate = accountExpirationDate;
    }

    /**
     * This method determines whether the account is expired or not. Note that if Expiration Date is the same as today, then this
     * will return false. It will only return true if the account expiration date is one day earlier than today or earlier. Note
     * that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on date
     * values, not time-values.
     *
     * @return true or false based on the logic outlined above
     */
    @Override
    public boolean isExpired() {
        LOG.debug("entering isExpired()");
        // dont even bother trying to test if the accountExpirationDate is null
        if (this.accountExpirationDate == null) {
            return false;
        }

        return this.isExpired(SpringContext.getBean(DateTimeService.class).getCurrentCalendar());
    }

    /**
     * This method determines whether the account is expired or not. Note that if Expiration Date is the same date as testDate, then
     * this will return false. It will only return true if the account expiration date is one day earlier than testDate or earlier.
     * Note that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on
     * date values, not time-values.
     *
     * @param testDate - Calendar instance with the date to test the Account's Expiration Date against. This is most commonly set to
     *        today's date.
     * @return true or false based on the logic outlined above
     */
    @Override
    public boolean isExpired(Calendar testDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("entering isExpired(" + testDate + ")");
        }

        // dont even bother trying to test if the accountExpirationDate is null
        if (this.accountExpirationDate == null) {
            return false;
        }

        // remove any time-components from the testDate
        testDate = DateUtils.truncate(testDate, Calendar.DAY_OF_MONTH);

        // get a calendar reference to the Account Expiration
        // date, and remove any time components
        Calendar acctDate = Calendar.getInstance();
        acctDate.setTime(this.accountExpirationDate);
        acctDate = DateUtils.truncate(acctDate, Calendar.DAY_OF_MONTH);

        // if the Account Expiration Date is before the testDate
        if (acctDate.before(testDate)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * This method determines whether the account is expired or not. Note that if Expiration Date is the same date as testDate, then
     * this will return false. It will only return true if the account expiration date is one day earlier than testDate or earlier.
     * Note that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on
     * date values, not time-values.
     *
     * @param testDate - java.util.Date instance with the date to test the Account's Expiration Date against. This is most commonly
     *        set to today's date.
     * @return true or false based on the logic outlined above
     */
    @Override
    public boolean isExpired(Date testDate) {

        // dont even bother trying to test if the accountExpirationDate is null
        if (this.accountExpirationDate == null) {
            return false;
        }

        Calendar acctDate = Calendar.getInstance();
        acctDate.setTime(testDate);
        return isExpired(acctDate);
    }

    /**
     * Gets the acctIndirectCostRcvyTypeCd attribute.
     *
     * @return Returns the acctIndirectCostRcvyTypeCd
     */
    @Override
    public String getAcctIndirectCostRcvyTypeCd() {
        return acctIndirectCostRcvyTypeCd;
    }

    /**
     * Sets the acctIndirectCostRcvyTypeCd attribute.
     *
     * @param acctIndirectCostRcvyTypeCd The acctIndirectCostRcvyTypeCd to set.
     */
    @Override
    public void setAcctIndirectCostRcvyTypeCd(String acctIndirectCostRcvyTypeCd) {
        this.acctIndirectCostRcvyTypeCd = acctIndirectCostRcvyTypeCd;
    }

    /**
     * Gets the acctCustomIndCstRcvyExclCd attribute.
     *
     * @return Returns the acctCustomIndCstRcvyExclCd
     */
    @Override
    public String getAcctCustomIndCstRcvyExclCd() {
        return acctCustomIndCstRcvyExclCd;
    }

    /**
     * Sets the acctCustomIndCstRcvyExclCd attribute.
     *
     * @param acctCustomIndCstRcvyExclCd The acctCustomIndCstRcvyExclCd to set.
     */
    @Override
    public void setAcctCustomIndCstRcvyExclCd(String acctCustomIndCstRcvyExclCd) {
        this.acctCustomIndCstRcvyExclCd = acctCustomIndCstRcvyExclCd;
    }

    /**
     * Gets the financialIcrSeriesIdentifier attribute.
     *
     * @return Returns the financialIcrSeriesIdentifier
     */
    @Override
    public String getFinancialIcrSeriesIdentifier() {
        return financialIcrSeriesIdentifier;
    }

    /**
     * Sets the financialIcrSeriesIdentifier attribute.
     *
     * @param financialIcrSeriesIdentifier The financialIcrSeriesIdentifier to set.
     */
    @Override
    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }

    /**
     * Gets the accountInFinancialProcessingIndicator attribute.
     *
     * @return Returns the accountInFinancialProcessingIndicator
     */
    @Override
    public boolean getAccountInFinancialProcessingIndicator() {
        return accountInFinancialProcessingIndicator;
    }

    /**
     * Sets the accountInFinancialProcessingIndicator attribute.
     *
     * @param accountInFinancialProcessingIndicator The accountInFinancialProcessingIndicator to set.
     */
    @Override
    public void setAccountInFinancialProcessingIndicator(boolean accountInFinancialProcessingIndicator) {
        this.accountInFinancialProcessingIndicator = accountInFinancialProcessingIndicator;
    }

    /**
     * Gets the budgetRecordingLevelCode attribute.
     *
     * @return Returns the budgetRecordingLevelCode
     */
    @Override
    public String getBudgetRecordingLevelCode() {
        return budgetRecordingLevelCode;
    }

    /**
     * Sets the budgetRecordingLevelCode attribute.
     *
     * @param budgetRecordingLevelCode The budgetRecordingLevelCode to set.
     */
    @Override
    public void setBudgetRecordingLevelCode(String budgetRecordingLevelCode) {
        this.budgetRecordingLevelCode = budgetRecordingLevelCode;
    }

    /**
     * Gets the accountSufficientFundsCode attribute.
     *
     * @return Returns the accountSufficientFundsCode
     */
    @Override
    public String getAccountSufficientFundsCode() {
        return accountSufficientFundsCode;
    }

    /**
     * Sets the accountSufficientFundsCode attribute.
     *
     * @param accountSufficientFundsCode The accountSufficientFundsCode to set.
     */
    @Override
    public void setAccountSufficientFundsCode(String accountSufficientFundsCode) {
        this.accountSufficientFundsCode = accountSufficientFundsCode;
    }

    /**
     * Gets the pendingAcctSufficientFundsIndicator attribute.
     *
     * @return Returns the pendingAcctSufficientFundsIndicator
     */
    @Override
    public boolean isPendingAcctSufficientFundsIndicator() {
        return pendingAcctSufficientFundsIndicator;
    }

    /**
     * Sets the pendingAcctSufficientFundsIndicator attribute.
     *
     * @param pendingAcctSufficientFundsIndicator The pendingAcctSufficientFundsIndicator to set.
     */
    @Override
    public void setPendingAcctSufficientFundsIndicator(boolean pendingAcctSufficientFundsIndicator) {
        this.pendingAcctSufficientFundsIndicator = pendingAcctSufficientFundsIndicator;
    }

    /**
     * Gets the extrnlFinEncumSufficntFndIndicator attribute.
     *
     * @return Returns the extrnlFinEncumSufficntFndIndicator
     */
    @Override
    public boolean isExtrnlFinEncumSufficntFndIndicator() {
        return extrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Sets the extrnlFinEncumSufficntFndIndicator attribute.
     *
     * @param extrnlFinEncumSufficntFndIndicator The extrnlFinEncumSufficntFndIndicator to set.
     */
    @Override
    public void setExtrnlFinEncumSufficntFndIndicator(boolean extrnlFinEncumSufficntFndIndicator) {
        this.extrnlFinEncumSufficntFndIndicator = extrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Gets the intrnlFinEncumSufficntFndIndicator attribute.
     *
     * @return Returns the intrnlFinEncumSufficntFndIndicator
     */
    @Override
    public boolean isIntrnlFinEncumSufficntFndIndicator() {
        return intrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Sets the intrnlFinEncumSufficntFndIndicator attribute.
     *
     * @param intrnlFinEncumSufficntFndIndicator The intrnlFinEncumSufficntFndIndicator to set.
     */
    @Override
    public void setIntrnlFinEncumSufficntFndIndicator(boolean intrnlFinEncumSufficntFndIndicator) {
        this.intrnlFinEncumSufficntFndIndicator = intrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Gets the finPreencumSufficientFundIndicator attribute.
     *
     * @return Returns the finPreencumSufficientFundIndicator
     */
    @Override
    public boolean isFinPreencumSufficientFundIndicator() {
        return finPreencumSufficientFundIndicator;
    }

    /**
     * Sets the finPreencumSufficientFundIndicator attribute.
     *
     * @param finPreencumSufficientFundIndicator The finPreencumSufficientFundIndicator to set.
     */
    @Override
    public void setFinPreencumSufficientFundIndicator(boolean finPreencumSufficientFundIndicator) {
        this.finPreencumSufficientFundIndicator = finPreencumSufficientFundIndicator;
    }

    /**
     * Gets the _FinancialObjectivePrsctrlIndicator_ attribute.
     *
     * @return Returns the _FinancialObjectivePrsctrlIndicator_
     */
    @Override
    public boolean isFinancialObjectivePrsctrlIndicator() {
        return financialObjectivePrsctrlIndicator;
    }

    /**
     * Sets the _FinancialObjectivePrsctrlIndicator_ attribute.
     *
     * @param _FinancialObjectivePrsctrlIndicator_ The _FinancialObjectivePrsctrlIndicator_ to set.
     */
    @Override
    public void setFinancialObjectivePrsctrlIndicator(boolean _FinancialObjectivePrsctrlIndicator_) {
        this.financialObjectivePrsctrlIndicator = _FinancialObjectivePrsctrlIndicator_;
    }

    /**
     * Gets the accountCfdaNumber attribute.
     *
     * @return Returns the accountCfdaNumber
     */
    @Override
    public String getAccountCfdaNumber() {
        return accountCfdaNumber;
    }

    /**
     * Sets the accountCfdaNumber attribute.
     *
     * @param accountCfdaNumber The accountCfdaNumber to set.
     */
    @Override
    public void setAccountCfdaNumber(String accountCfdaNumber) {
        this.accountCfdaNumber = accountCfdaNumber;
    }

    /**
     * Gets the accountOffCampusIndicator attribute.
     *
     * @return Returns the accountOffCampusIndicator
     */
    @Override
    public boolean isAccountOffCampusIndicator() {
        return accountOffCampusIndicator;
    }

    /**
     * Sets the accountOffCampusIndicator attribute.
     *
     * @param accountOffCampusIndicator The accountOffCampusIndicator to set.
     */
    @Override
    public void setAccountOffCampusIndicator(boolean accountOffCampusIndicator) {
        this.accountOffCampusIndicator = accountOffCampusIndicator;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
     */
    @Override
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    @Override
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organization attribute.
     *
     * @return Returns the organization
     */
    @Override
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     *
     * @param organization The organization to set.
     * @deprecated
     */
    @Override
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the accountType attribute.
     *
     * @return Returns the accountType
     */
    @Override
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * Sets the accountType attribute.
     *
     * @param accountType The accountType to set.
     * @deprecated
     */
    @Override
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * Gets the accountPhysicalCampus attribute.
     *
     * @return Returns the accountPhysicalCampus
     */
    @Override
    public CampusEbo getAccountPhysicalCampus() {
        if ( StringUtils.isBlank(accountPhysicalCampusCode) ) {
            accountPhysicalCampus = null;
        } else {
            if ( accountPhysicalCampus == null || !StringUtils.equals( accountPhysicalCampus.getCode(),accountPhysicalCampusCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, accountPhysicalCampusCode);
                    accountPhysicalCampus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return accountPhysicalCampus;
    }

    /**
     * Sets the accountPhysicalCampus attribute.
     *
     * @param accountPhysicalCampus The accountPhysicalCampus to set.
     * @deprecated
     */
    @Override
    public void setAccountPhysicalCampus(CampusEbo accountPhysicalCampus) {
        this.accountPhysicalCampus = accountPhysicalCampus;
    }

    /**
     * Gets the accountState attribute
     *
     * @return Returns the accountState
     */
    @Override
    public StateEbo getAccountState() {
        if ( StringUtils.isBlank(accountStateCode) || StringUtils.isBlank(KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
            accountState = null;
        } else {
            if ( accountState == null || !StringUtils.equals( accountState.getCode(),accountStateCode) || !StringUtils.equals(accountState.getCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, accountStateCode);
                    accountState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return accountState;
    }

    /**
     * Sets the accountState attribute
     *
     * @param state
     * @deprecated
     */
    @Override
    public void setAccountState(StateEbo state) {
        this.accountState = state;
    }

    /**
     * Gets the subFundGroup attribute.
     *
     * @return Returns the subFundGroup
     */
    @Override
    public SubFundGroup getSubFundGroup() {
        return subFundGroup;
    }

    /**
     * Sets the subFundGroup attribute.
     *
     * @param subFundGroup The subFundGroup to set.
     * @deprecated
     */
    @Override
    public void setSubFundGroup(SubFundGroup subFundGroup) {
        this.subFundGroup = subFundGroup;
    }

    /**
     * Gets the financialHigherEdFunction attribute.
     *
     * @return Returns the financialHigherEdFunction
     */
    @Override
    public HigherEducationFunction getFinancialHigherEdFunction() {
        return financialHigherEdFunction;
    }

    /**
     * Sets the financialHigherEdFunction attribute.
     *
     * @param financialHigherEdFunction The financialHigherEdFunction to set.
     * @deprecated
     */
    @Override
    public void setFinancialHigherEdFunction(HigherEducationFunction financialHigherEdFunction) {
        this.financialHigherEdFunction = financialHigherEdFunction;
    }

    /**
     * Gets the accountRestrictedStatus attribute.
     *
     * @return Returns the accountRestrictedStatus
     */
    @Override
    public RestrictedStatus getAccountRestrictedStatus() {
        return accountRestrictedStatus;
    }

    /**
     * Sets the accountRestrictedStatus attribute.
     *
     * @param accountRestrictedStatus The accountRestrictedStatus to set.
     * @deprecated
     */
    @Override
    public void setAccountRestrictedStatus(RestrictedStatus accountRestrictedStatus) {
        this.accountRestrictedStatus = accountRestrictedStatus;
    }

    /**
     * Gets the reportsToAccount attribute.
     *
     * @return Returns the reportsToAccount
     */
    @Override
    public Account getReportsToAccount() {
        return reportsToAccount;
    }

    /**
     * Sets the reportsToAccount attribute.
     *
     * @param reportsToAccount The reportsToAccount to set.
     * @deprecated
     */
    @Override
    public void setReportsToAccount(Account reportsToAccount) {
        this.reportsToAccount = reportsToAccount;
    }

    /**
     * Gets the endowmentIncomeAccount attribute.
     *
     * @return Returns the endowmentIncomeAccount
     */
    @Override
    public Account getEndowmentIncomeAccount() {
        return endowmentIncomeAccount;
    }

    /**
     * Sets the endowmentIncomeAccount attribute.
     *
     * @param endowmentIncomeAccount The endowmentIncomeAccount to set.
     * @deprecated
     */
    @Override
    public void setEndowmentIncomeAccount(Account endowmentIncomeAccount) {
        this.endowmentIncomeAccount = endowmentIncomeAccount;
    }

    /**
     * Gets the contractControlAccount attribute.
     *
     * @return Returns the contractControlAccount
     */
    @Override
    public Account getContractControlAccount() {
        return contractControlAccount;
    }

    /**
     * Sets the contractControlAccount attribute.
     *
     * @param contractControlAccount The contractControlAccount to set.
     * @deprecated
     */
    @Override
    public void setContractControlAccount(Account contractControlAccount) {
        this.contractControlAccount = contractControlAccount;
    }


    /**
     * Gets the incomeStreamAccount attribute.
     *
     * @return Returns the incomeStreamAccount
     */
    @Override
    public Account getIncomeStreamAccount() {
        return incomeStreamAccount;
    }

    /**
     * Sets the incomeStreamAccount attribute.
     *
     * @param incomeStreamAccount The incomeStreamAccount to set.
     * @deprecated
     */
    @Override
    public void setIncomeStreamAccount(Account incomeStreamAccount) {
        this.incomeStreamAccount = incomeStreamAccount;
    }

    @Override
    public Person getAccountFiscalOfficerUser() {
        accountFiscalOfficerUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountFiscalOfficerSystemIdentifier, accountFiscalOfficerUser);
        return accountFiscalOfficerUser;
    }


    /**
     * @param accountFiscalOfficerUser The accountFiscalOfficerUser to set.
     * @deprecated
     */
    @Override
    public void setAccountFiscalOfficerUser(Person accountFiscalOfficerUser) {
        this.accountFiscalOfficerUser = accountFiscalOfficerUser;
    }

    @Override
    public Person getAccountManagerUser() {
        accountManagerUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountManagerSystemIdentifier, accountManagerUser);
        return accountManagerUser;
    }

    /**
     * @param accountManagerUser The accountManagerUser to set.
     * @deprecated
     */
    @Override
    public void setAccountManagerUser(Person accountManagerUser) {
        this.accountManagerUser = accountManagerUser;
    }


    @Override
    public Person getAccountSupervisoryUser() {
        accountSupervisoryUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountsSupervisorySystemsIdentifier, accountSupervisoryUser);
        return accountSupervisoryUser;
    }


    /**
     * @param accountSupervisoryUser The accountSupervisoryUser to set.
     * @deprecated
     */
    @Override
    public void setAccountSupervisoryUser(Person accountSupervisoryUser) {
        this.accountSupervisoryUser = accountSupervisoryUser;
    }


    /**
     * @return Returns the continuationAccount.
     */
    @Override
    public Account getContinuationAccount() {
        return continuationAccount;
    }


    /**
     * @param continuationAccount The continuationAccount to set.
     * @deprecated
     */
    @Override
    public void setContinuationAccount(Account continuationAccount) {
        this.continuationAccount = continuationAccount;
    }


    /**
     * @return Returns the accountGuideline.
     */
    @Override
    public AccountGuideline getAccountGuideline() {
        return accountGuideline;
    }

    /**
     * @param accountGuideline The accountGuideline to set.
     * @deprecated
     */
    @Override
    public void setAccountGuideline(AccountGuideline accountGuideline) {
        this.accountGuideline = accountGuideline;
    }


    /**
     * Gets the accountDescription attribute.
     *
     * @return Returns the accountDescription.
     */
    @Override
    public AccountDescription getAccountDescription() {
        return accountDescription;
    }

    /**
     * Sets the accountDescription attribute value.
     *
     * @param accountDescription The accountDescription to set.
     */
    @Override
    public void setAccountDescription(AccountDescription accountDescription) {
        this.accountDescription = accountDescription;
    }

    /**
     * @return Returns the subAccounts.
     */
    @Override
    public List getSubAccounts() {
        return subAccounts;
    }


    /**
     * @param subAccounts The subAccounts to set.
     */
    @Override
    public void setSubAccounts(List subAccounts) {
        this.subAccounts = subAccounts;
    }


    /**
     * @return Returns the chartOfAccountsCode.
     */
    @Override
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    @Override
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * @return Returns the accountFiscalOfficerSystemIdentifier.
     */
    @Override
    public String getAccountFiscalOfficerSystemIdentifier() {
        return accountFiscalOfficerSystemIdentifier;
    }

    /**
     * @param accountFiscalOfficerSystemIdentifier The accountFiscalOfficerSystemIdentifier to set.
     */
    @Override
    public void setAccountFiscalOfficerSystemIdentifier(String accountFiscalOfficerSystemIdentifier) {
        this.accountFiscalOfficerSystemIdentifier = accountFiscalOfficerSystemIdentifier;
    }

    /**
     * @return Returns the accountManagerSystemIdentifier.
     */
    @Override
    public String getAccountManagerSystemIdentifier() {
        return accountManagerSystemIdentifier;
    }

    /**
     * @param accountManagerSystemIdentifier The accountManagerSystemIdentifier to set.
     */
    @Override
    public void setAccountManagerSystemIdentifier(String accountManagerSystemIdentifier) {
        this.accountManagerSystemIdentifier = accountManagerSystemIdentifier;
    }

    /**
     * @return Returns the accountPhysicalCampusCode.
     */
    @Override
    public String getAccountPhysicalCampusCode() {
        return accountPhysicalCampusCode;
    }

    /**
     * @param accountPhysicalCampusCode The accountPhysicalCampusCode to set.
     */
    @Override
    public void setAccountPhysicalCampusCode(String accountPhysicalCampusCode) {
        this.accountPhysicalCampusCode = accountPhysicalCampusCode;
    }

    /**
     * @return Returns the accountRestrictedStatusCode.
     */
    @Override
    public String getAccountRestrictedStatusCode() {
        return accountRestrictedStatusCode;
    }

    /**
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     */
    @Override
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
        this.accountRestrictedStatusCode = accountRestrictedStatusCode;
    }

    /**
     * @return Returns the accountsSupervisorySystemsIdentifier.
     */
    @Override
    public String getAccountsSupervisorySystemsIdentifier() {
        return accountsSupervisorySystemsIdentifier;
    }

    /**
     * @param accountsSupervisorySystemsIdentifier The accountsSupervisorySystemsIdentifier to set.
     */
    @Override
    public void setAccountsSupervisorySystemsIdentifier(String accountsSupervisorySystemsIdentifier) {
        this.accountsSupervisorySystemsIdentifier = accountsSupervisorySystemsIdentifier;
    }

    /**
     * @return Returns the accountTypeCode.
     */
    @Override
    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    /**
     * @param accountTypeCode The accountTypeCode to set.
     */
    @Override
    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }

    /**
     * @return Returns the continuationAccountNumber.
     */
    @Override
    public String getContinuationAccountNumber() {
        return continuationAccountNumber;
    }

    /**
     * @param continuationAccountNumber The continuationAccountNumber to set.
     */
    @Override
    public void setContinuationAccountNumber(String continuationAccountNumber) {
        this.continuationAccountNumber = continuationAccountNumber;
    }

    /**
     * @return Returns the continuationFinChrtOfAcctCd.
     */
    @Override
    public String getContinuationFinChrtOfAcctCd() {
        return continuationFinChrtOfAcctCd;
    }

    /**
     * @param continuationFinChrtOfAcctCd The continuationFinChrtOfAcctCd to set.
     */
    @Override
    public void setContinuationFinChrtOfAcctCd(String continuationFinChrtOfAcctCd) {
        this.continuationFinChrtOfAcctCd = continuationFinChrtOfAcctCd;
    }

    /**
     * @return Returns the contractControlAccountNumber.
     */
    @Override
    public String getContractControlAccountNumber() {
        return contractControlAccountNumber;
    }

    /**
     * @param contractControlAccountNumber The contractControlAccountNumber to set.
     */
    @Override
    public void setContractControlAccountNumber(String contractControlAccountNumber) {
        this.contractControlAccountNumber = contractControlAccountNumber;
    }

    /**
     * @return Returns the contractControlFinCoaCode.
     */
    @Override
    public String getContractControlFinCoaCode() {
        return contractControlFinCoaCode;
    }

    /**
     * @param contractControlFinCoaCode The contractControlFinCoaCode to set.
     */
    @Override
    public void setContractControlFinCoaCode(String contractControlFinCoaCode) {
        this.contractControlFinCoaCode = contractControlFinCoaCode;
    }

    /**
     * @return Returns the endowmentIncomeAccountNumber.
     */
    @Override
    public String getEndowmentIncomeAccountNumber() {
        return endowmentIncomeAccountNumber;
    }

    /**
     * @param endowmentIncomeAccountNumber The endowmentIncomeAccountNumber to set.
     */
    @Override
    public void setEndowmentIncomeAccountNumber(String endowmentIncomeAccountNumber) {
        this.endowmentIncomeAccountNumber = endowmentIncomeAccountNumber;
    }

    /**
     * @return Returns the endowmentIncomeAcctFinCoaCd.
     */
    @Override
    public String getEndowmentIncomeAcctFinCoaCd() {
        return endowmentIncomeAcctFinCoaCd;
    }

    /**
     * @param endowmentIncomeAcctFinCoaCd The endowmentIncomeAcctFinCoaCd to set.
     */
    @Override
    public void setEndowmentIncomeAcctFinCoaCd(String endowmentIncomeAcctFinCoaCd) {
        this.endowmentIncomeAcctFinCoaCd = endowmentIncomeAcctFinCoaCd;
    }

    /**
     * @return Returns the financialHigherEdFunctionCd.
     */
    @Override
    public String getFinancialHigherEdFunctionCd() {
        return financialHigherEdFunctionCd;
    }

    /**
     * @param financialHigherEdFunctionCd The financialHigherEdFunctionCd to set.
     */
    @Override
    public void setFinancialHigherEdFunctionCd(String financialHigherEdFunctionCd) {
        this.financialHigherEdFunctionCd = financialHigherEdFunctionCd;
    }

    /**
     * @return Returns the incomeStreamAccountNumber.
     */
    @Override
    public String getIncomeStreamAccountNumber() {
        return incomeStreamAccountNumber;
    }

    /**
     * @param incomeStreamAccountNumber The incomeStreamAccountNumber to set.
     */
    @Override
    public void setIncomeStreamAccountNumber(String incomeStreamAccountNumber) {
        this.incomeStreamAccountNumber = incomeStreamAccountNumber;
    }

    /**
     * @return Returns the incomeStreamFinancialCoaCode.
     */
    @Override
    public String getIncomeStreamFinancialCoaCode() {
        return incomeStreamFinancialCoaCode;
    }

    /**
     * @param incomeStreamFinancialCoaCode The incomeStreamFinancialCoaCode to set.
     */
    @Override
    public void setIncomeStreamFinancialCoaCode(String incomeStreamFinancialCoaCode) {
        this.incomeStreamFinancialCoaCode = incomeStreamFinancialCoaCode;
    }

    /**
     * @return Returns the organizationCode.
     */
    @Override
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * @param organizationCode The organizationCode to set.
     */
    @Override
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * @return Returns the reportsToAccountNumber.
     */
    @Override
    public String getReportsToAccountNumber() {
        return reportsToAccountNumber;
    }

    /**
     * @param reportsToAccountNumber The reportsToAccountNumber to set.
     */
    @Override
    public void setReportsToAccountNumber(String reportsToAccountNumber) {
        this.reportsToAccountNumber = reportsToAccountNumber;
    }

    /**
     * @return Returns the reportsToChartOfAccountsCode.
     */
    @Override
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    @Override
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }

    /**
     * @return Returns the subFundGroupCode.
     */
    @Override
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    @Override
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
        forContractsAndGrants = null;
    }

    /**
     * Gets the postalZipCode attribute.
     *
     * @return Returns the postalZipCode.
     */
    @Override
    public PostalCodeEbo getPostalZipCode() {
        if ( StringUtils.isBlank(accountZipCode) || StringUtils.isBlank(KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
            postalZipCode = null;
        } else {
            if ( postalZipCode == null || !StringUtils.equals( postalZipCode.getCode(),accountZipCode) || !StringUtils.equals(postalZipCode.getCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, accountZipCode);
                    postalZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return postalZipCode;
    }

    /**
     * Sets the postalZipCode attribute value.
     *
     * @param postalZipCode The postalZipCode to set.
     */
    @Override
    public void setPostalZipCode(PostalCodeEbo postalZipCode) {
        this.postalZipCode = postalZipCode;
    }

    /**
     * Gets the budgetRecordingLevel attribute.
     *
     * @return Returns the budgetRecordingLevel.
     */
    @Override
    public BudgetRecordingLevel getBudgetRecordingLevel() {
        return budgetRecordingLevel;
    }

    /**
     * Sets the budgetRecordingLevel attribute value.
     *
     * @param budgetRecordingLevel The budgetRecordingLevel to set.
     */
    @Override
    public void setBudgetRecordingLevel(BudgetRecordingLevel budgetRecordingLevel) {
        this.budgetRecordingLevel = budgetRecordingLevel;
    }

    /**
     * Gets the sufficientFundsCode attribute.
     *
     * @return Returns the sufficientFundsCode.
     */
    @Override
    public SufficientFundsCode getSufficientFundsCode() {
        return sufficientFundsCode;
    }

    /**
     * Sets the sufficientFundsCode attribute value.
     *
     * @param sufficientFundsCode The sufficientFundsCode to set.
     */
    @Override
    public void setSufficientFundsCode(SufficientFundsCode sufficientFundsCode) {
        this.sufficientFundsCode = sufficientFundsCode;
    }

    /**
     * Implementing equals since I need contains to behave reasonably in a hashed datastructure.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                Account other = (Account) obj;

                if (StringUtils.equals(this.getChartOfAccountsCode(), other.getChartOfAccountsCode())) {
                    if (StringUtils.equals(this.getAccountNumber(), other.getAccountNumber())) {
                        equal = true;
                    }
                }
            }
        }

        return equal;
    }

    /**
     * Calcluates hashCode based on current values of chartOfAccountsCode and accountNumber fields. Somewhat dangerous, since both
     * of those fields are mutable, but I don't expect people to be editing those values directly for Accounts stored in hashed
     * datastructures.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        String hashString = getChartOfAccountsCode() + "|" + getAccountNumber();

        return hashString.hashCode();
    }


    /**
     * Convenience method to make the primitive account fields from this Account easier to compare to the account fields from
     * another Account or an AccountingLine
     *
     * @return String representing the account associated with this Accounting
     */
    @Override
    public String getAccountKey() {
        String key = getChartOfAccountsCode() + ":" + getAccountNumber();
        return key;
    }

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
    @Override
    public String getAccountResponsibilitySection() {
        return accountResponsibilitySection;
    }

    /**
     * Sets the accountResponsibilitySection attribute value.
     *
     * @param accountResponsibilitySection The accountResponsibilitySection to set.
     */
    @Override
    public void setAccountResponsibilitySection(String accountResponsibilitySection) {
        this.accountResponsibilitySection = accountResponsibilitySection;
    }

    /**
     * Gets the contractsAndGrantsSection attribute.
     *
     * @return Returns the contractsAndGrantsSection.
     */
    @Override
    public String getContractsAndGrantsSection() {
        return contractsAndGrantsSection;
    }

    /**
     * Sets the contractsAndGrantsSection attribute value.
     *
     * @param contractsAndGrantsSection The contractsAndGrantsSection to set.
     */
    @Override
    public void setContractsAndGrantsSection(String contractsAndGrantsSection) {
        this.contractsAndGrantsSection = contractsAndGrantsSection;
    }

    /**
     * Gets the accountDescriptionSection attribute.
     *
     * @return Returns the accountDescriptionSection.
     */
    @Override
    public String getAccountDescriptionSection() {
        return accountDescriptionSection;
    }

    /**
     * Sets the accountDescriptionSection attribute value.
     *
     * @param accountDescriptionSection The accountDescriptionSection to set.
     */
    @Override
    public void setAccountDescriptionSection(String accountDescriptionSection) {
        this.accountDescriptionSection = accountDescriptionSection;
    }

    /**
     * Gets the guidelinesAndPurposeSection attribute.
     *
     * @return Returns the guidelinesAndPurposeSection.
     */
    @Override
    public String getGuidelinesAndPurposeSection() {
        return guidelinesAndPurposeSection;
    }

    /**
     * Sets the guidelinesAndPurposeSection attribute value.
     *
     * @param guidelinesAndPurposeSection The guidelinesAndPurposeSection to set.
     */
    @Override
    public void setGuidelinesAndPurposeSection(String guidelinesAndPurposeSection) {
        this.guidelinesAndPurposeSection = guidelinesAndPurposeSection;
    }

    /**
     * Gets the accountResponsibilitySectionBlank attribute.
     *
     * @return Returns the accountResponsibilitySectionBlank.
     */
    @Override
    public String getAccountResponsibilitySectionBlank() {
        return accountResponsibilitySectionBlank;
    }

    /**
     * Gets the contractsAndGrantsSectionBlank attribute.
     *
     * @return Returns the contractsAndGrantsSectionBlank.
     */
    @Override
    public String getContractsAndGrantsSectionBlank() {
        return contractsAndGrantsSectionBlank;
    }

    /**
     * Gets the accountDescriptionSectionBlank attribute.
     *
     * @return Returns the accountDescriptionSectionBlank.
     */
    @Override
    public String getAccountDescriptionSectionBlank() {
        return accountDescriptionSectionBlank;
    }

    /**
     * Gets the guidelinesAndPurposeSectionBlank attribute.
     *
     * @return Returns the guidelinesAndPurposeSectionBlank.
     */
    @Override
    public String getGuidelinesAndPurposeSectionBlank() {
        return guidelinesAndPurposeSectionBlank;
    }

    /**
     * Gets the forContractsAndGrants attribute.
     *
     * @return Returns the forContractsAndGrants.
     */
    @Override
    public boolean isForContractsAndGrants() {
        if ( forContractsAndGrants == null ) {
            forContractsAndGrants = SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(getSubFundGroup());
        }
        return forContractsAndGrants;
    }

    /**
     * @see org.kuali.kfs.coa.businessobject.AccountIntf#isClosed()
     */
    @Override
    public boolean isClosed() {
        return !active;
    }

    @Override
    public List<PriorYearIndirectCostRecoveryAccount> getIndirectCostRecoveryAccounts() {
        return indirectCostRecoveryAccounts;
    }

    @Override
    public void setIndirectCostRecoveryAccounts(List<? extends IndirectCostRecoveryAccount> indirectCostRecoveryAccounts) {
        List<PriorYearIndirectCostRecoveryAccount> priorYearAccountIcrList = new ArrayList<PriorYearIndirectCostRecoveryAccount>();
        for (IndirectCostRecoveryAccount icr : indirectCostRecoveryAccounts){
            priorYearAccountIcrList.add(new PriorYearIndirectCostRecoveryAccount(icr));
        }
        this.indirectCostRecoveryAccounts = priorYearAccountIcrList;
    }

}
