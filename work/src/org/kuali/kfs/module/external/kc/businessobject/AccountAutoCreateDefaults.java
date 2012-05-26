/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.module.external.kc.businessobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountType;
import org.kuali.kfs.coa.businessobject.BudgetRecordingLevel;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.businessobject.SufficientFundsCode;
import org.kuali.kfs.integration.cg.ContractsAndGrantsUnit;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 *
 */
public class AccountAutoCreateDefaults extends PersistableBusinessObjectBase implements MutableInactivatable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountAutoCreateDefaults.class);

    protected Integer accountDefaultId;
    protected String kcUnit;
    //protected KCUnit kcUnit;
    protected String kcUnitName;
    protected Chart chartOfAccounts;
    protected String chartOfAccountsCode;
    protected Organization organization;
    protected String organizationCode;
    protected String accountZipCode;
    protected String accountCityName;
    protected String accountStateCode;
    protected String accountStreetAddress;
    protected AccountType accountType;
    protected String accountTypeCode;
    protected String accountPhysicalCampusCode;
    protected SubFundGroup subFundGroup;
    protected String subFundGroupCode;
    protected boolean accountsFringesBnftIndicator;
    protected Chart fringeBenefitsChartOfAccount;
    protected String reportsToChartOfAccountsCode;
    protected String reportsToAccountNumber;
    protected String accountFiscalOfficerSystemIdentifier;
    protected String accountsSupervisorySystemsIdentifier;
    protected String accountManagerSystemIdentifier;
    protected Account reportsToAccount;
    protected Chart continuationChartOfAccount;
    protected String continuationFinChrtOfAcctCd;
    protected Account continuationAccount;
    protected String continuationAccountNumber;
    protected Account incomeStreamAccount;
    protected Chart incomeStreamChartOfAccounts;
    protected String incomeStreamFinancialCoaCode;
    protected String incomeStreamAccountNumber;
    protected String budgetRecordingLevelCode;
    protected BudgetRecordingLevel budgetRecordingLevel;
    protected SufficientFundsCode sufficientFundsCode;
    protected String accountSufficientFundsCode;
    protected boolean pendingAcctSufficientFundsIndicator;
    protected boolean extrnlFinEncumSufficntFndIndicator;
    protected boolean intrnlFinEncumSufficntFndIndicator;
    protected boolean finPreencumSufficientFundIndicator;
    protected boolean financialObjectivePrsctrlIndicator;
    protected Integer contractsAndGrantsAccountResponsibilityId;
    protected String accountDescriptionCampusCode;
    protected String accountDescriptionBuildingCode;
    protected boolean active;

    //
    protected Person accountFiscalOfficerUser;
    protected Person accountSupervisoryUser;
    protected Person accountManagerUser;
    protected ContractsAndGrantsUnit unitDTO;
    protected List<IndirectCostRecoveryAutoDefAccount> indirectCostRecoveryAutoDefAccounts;

    /**
     * Default no-arg constructor.
     */
    public AccountAutoCreateDefaults() {
        active = true; // assume active until otherwise set
        indirectCostRecoveryAutoDefAccounts = new ArrayList<IndirectCostRecoveryAutoDefAccount>();
    }

    /**
     * Gets the kcUnit attribute.
     * @return Returns the kcUnit.
     */
    public String getKcUnit() {
        return kcUnit;
    }

    /**
     * Sets the kcUnit attribute value.
     * @param kcUnit The kcUnit to set.
     */
    public void setKcUnit(String kcUnit) {
        this.kcUnit = kcUnit;
    }

    /**
     * Gets the kcUnitName attribute.
     * @return Returns the kcUnitName.
     */
    public String getKcUnitName() {
        return kcUnitName;
    }

    /**
     * Sets the kcUnitName attribute value.
     * @param kcUnitName The kcUnitName to set.
     */
    public void setKcUnitName(String kcUnitName) {
        this.kcUnitName = kcUnitName;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
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
     * Gets the organization attribute.
     * @return Returns the organization.
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute value.
     * @param organization The organization to set.
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
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
     * Gets the postalZipCode attribute.
     * @return Returns the postalZipCode.
     */
    public PostalCodeEbo getPostalZipCode() {
        PostalCodeEbo postalZipCode = null;
        if ( StringUtils.isBlank(accountZipCode) ) {
            postalZipCode = null;
        } else {
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
            if ( moduleService != null ) {
                Map<String,Object> keys = new HashMap<String, Object>(2);
                keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);
                keys.put(LocationConstants.PrimaryKeyConstants.CODE, accountZipCode);
                postalZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
            } else {
                throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
            }
        }

        return postalZipCode;        
    }

  
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
     * Gets the accountState attribute.
     * @return Returns the accountState.
     */
    public StateEbo getAccountState() {
        StateEbo state = null;
        
        if ( StringUtils.isBlank(accountStateCode)) {
            state = null;
        } else {
            if ( accountStateCode == null  ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, accountStateCode);
                    state = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return state;
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
     * Gets the accountType attribute.
     * @return Returns the accountType.
     */
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * Sets the accountType attribute value.
     * @param accountType The accountType to set.
     */
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
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
     * Gets the accountPhysicalCampus attribute.
     * @return Returns the accountPhysicalCampus.
     */
    public CampusEbo getAccountPhysicalCampus() {
        CampusEbo defaultProcessingCampus = null;
        if ( StringUtils.isBlank(accountPhysicalCampusCode) ) {
            defaultProcessingCampus = null;
        } else {
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
            if ( moduleService != null ) {
                Map<String,Object> keys = new HashMap<String, Object>(1);
                keys.put(LocationConstants.PrimaryKeyConstants.CODE, accountPhysicalCampusCode);
                defaultProcessingCampus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
            } else {
                throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
            }

        }
        return defaultProcessingCampus;

    }

    /**
     * Gets the subFundGroup attribute.
     * @return Returns the subFundGroup.
     */
    public SubFundGroup getSubFundGroup() {
        return subFundGroup;
    }

    /**
     * Sets the subFundGroup attribute value.
     * @param subFundGroup The subFundGroup to set.
     */
    public void setSubFundGroup(SubFundGroup subFundGroup) {
        this.subFundGroup = subFundGroup;
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

    /**
     * Gets the accountsFringesBnftIndicator attribute.
     * @return Returns the accountsFringesBnftIndicator.
     */
    public boolean isAccountsFringesBnftIndicator() {
        return accountsFringesBnftIndicator;
    }

    /**
     * Sets the accountsFringesBnftIndicator attribute value.
     * @param accountsFringesBnftIndicator The accountsFringesBnftIndicator to set.
     */
    public void setAccountsFringesBnftIndicator(boolean accountsFringesBnftIndicator) {
        this.accountsFringesBnftIndicator = accountsFringesBnftIndicator;
    }

    /**
     * Gets the fringeBenefitsChartOfAccount attribute.
     * @return Returns the fringeBenefitsChartOfAccount.
     */
    public Chart getFringeBenefitsChartOfAccount() {
        return fringeBenefitsChartOfAccount;
    }

    /**
     * Sets the fringeBenefitsChartOfAccount attribute value.
     * @param fringeBenefitsChartOfAccount The fringeBenefitsChartOfAccount to set.
     */
    public void setFringeBenefitsChartOfAccount(Chart fringeBenefitsChartOfAccount) {
        this.fringeBenefitsChartOfAccount = fringeBenefitsChartOfAccount;
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
     * @return Returns the reportsToAccountNumber.
     */
    public String getReportsToAccountNumber() {
        return reportsToAccountNumber;
    }

    /**
     * @param reportsToAccountNumber The reportsToAccountNumber to set.
     */
    public void setReportsToAccountNumber(String reportsToAccountNumber) {
        this.reportsToAccountNumber = reportsToAccountNumber;
    }

    /**
     * Gets the reportsToAccount attribute.
     *
     * @return Returns the reportsToAccount
     */
    public Account getReportsToAccount() {
        return reportsToAccount;
    }

    /**
     * Sets the reportsToAccount attribute.
     *
     * @param reportsToAccount The reportsToAccount to set.
     * @deprecated
     */
    public void setReportsToAccount(Account reportsToAccount) {
        this.reportsToAccount = reportsToAccount;
    }

    /**
     * Gets the accountFiscalOfficerSystemIdentifier attribute.
     * @return Returns the accountFiscalOfficerSystemIdentifier.
     */
    public String getAccountFiscalOfficerSystemIdentifier() {
        return accountFiscalOfficerSystemIdentifier;
    }

    /**
     * @return Returns the accountFiscalOfficerSystemIdentifier.
     */
    public String getAccountFiscalOfficerSystemIdentifierForSearching() {
        return getAccountFiscalOfficerSystemIdentifier();
    }

    /**
     * @return Returns the accountsSupervisorySystemsIdentifier.
     */
    public String getAccountsSupervisorySystemsIdentifierForSearching() {
        return accountsSupervisorySystemsIdentifier;
    }

    /**
     * Sets the accountFiscalOfficerSystemIdentifier attribute value.
     * @param accountFiscalOfficerSystemIdentifier The accountFiscalOfficerSystemIdentifier to set.
     */
    public void setAccountFiscalOfficerSystemIdentifier(String accountFiscalOfficerSystemIdentifier) {
        this.accountFiscalOfficerSystemIdentifier = accountFiscalOfficerSystemIdentifier;
    }

    /**
     * Gets the accountsSupervisorySystemsIdentifier attribute.
     * @return Returns the accountsSupervisorySystemsIdentifier.
     */
    public String getAccountsSupervisorySystemsIdentifier() {
        return accountsSupervisorySystemsIdentifier;
    }

    /**
     * Sets the accountsSupervisorySystemsIdentifier attribute value.
     * @param accountsSupervisorySystemsIdentifier The accountsSupervisorySystemsIdentifier to set.
     */
    public void setAccountsSupervisorySystemsIdentifier(String accountsSupervisorySystemsIdentifier) {
        this.accountsSupervisorySystemsIdentifier = accountsSupervisorySystemsIdentifier;
    }

    /**
     * Gets the accountManagerSystemIdentifier attribute.
     * @return Returns the accountManagerSystemIdentifier.
     */
    public String getAccountManagerSystemIdentifier() {
        return accountManagerSystemIdentifier;
    }
    /**
     * @return Returns the accountManagerSystemIdentifier.
     */
    public String getAccountManagerSystemIdentifierForSearching() {
        return getAccountManagerSystemIdentifier();
    }


    /**
     * Sets the accountManagerSystemIdentifier attribute value.
     * @param accountManagerSystemIdentifier The accountManagerSystemIdentifier to set.
     */
    public void setAccountManagerSystemIdentifier(String accountManagerSystemIdentifier) {
        this.accountManagerSystemIdentifier = accountManagerSystemIdentifier;
    }

    /**
     * Gets the continuationChartOfAccount attribute.
     * @return Returns the continuationChartOfAccount.
     */
    public Chart getContinuationChartOfAccount() {
        return continuationChartOfAccount;
    }

    /**
     * Sets the continuationChartOfAccount attribute value.
     * @param continuationChartOfAccount The continuationChartOfAccount to set.
     */
    public void setContinuationChartOfAccount(Chart continuationChartOfAccount) {
        this.continuationChartOfAccount = continuationChartOfAccount;
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
     * Gets the continuationAccount attribute.
     * @return Returns the continuationAccount.
     */
    public Account getContinuationAccount() {
        return continuationAccount;
    }

    /**
     * Sets the continuationAccount attribute value.
     * @param continuationAccount The continuationAccount to set.
     */
    public void setContinuationAccount(Account continuationAccount) {
        this.continuationAccount = continuationAccount;
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
     * Gets the incomeStreamAccount attribute.
     * @return Returns the incomeStreamAccount.
     */
    public Account getIncomeStreamAccount() {
        return incomeStreamAccount;
    }

    /**
     * Sets the incomeStreamAccount attribute value.
     * @param incomeStreamAccount The incomeStreamAccount to set.
     */
    public void setIncomeStreamAccount(Account incomeStreamAccount) {
        this.incomeStreamAccount = incomeStreamAccount;
    }

    /**
     * Gets the incomeStreamChartOfAccounts attribute.
     * @return Returns the incomeStreamChartOfAccounts.
     */
    public Chart getIncomeStreamChartOfAccounts() {
        return incomeStreamChartOfAccounts;
    }

    /**
     * Sets the incomeStreamChartOfAccounts attribute value.
     * @param incomeStreamChartOfAccounts The incomeStreamChartOfAccounts to set.
     */
    public void setIncomeStreamChartOfAccounts(Chart incomeStreamChartOfAccounts) {
        this.incomeStreamChartOfAccounts = incomeStreamChartOfAccounts;
    }

    /**
     * Gets the incomeStreamFinancialCoaCode attribute.
     * @return Returns the incomeStreamFinancialCoaCode.
     */
    public String getIncomeStreamFinancialCoaCode() {
        return incomeStreamFinancialCoaCode;
    }

    /**
     * Sets the incomeStreamFinancialCoaCode attribute value.
     * @param incomeStreamFinancialCoaCode The incomeStreamFinancialCoaCode to set.
     */
    public void setIncomeStreamFinancialCoaCode(String incomeStreamFinancialCoaCode) {
        this.incomeStreamFinancialCoaCode = incomeStreamFinancialCoaCode;
    }

    /**
     * Gets the incomeStreamAccountNumber attribute.
     * @return Returns the incomeStreamAccountNumber.
     */
    public String getIncomeStreamAccountNumber() {
        return incomeStreamAccountNumber;
    }

    /**
     * Sets the incomeStreamAccountNumber attribute value.
     * @param incomeStreamAccountNumber The incomeStreamAccountNumber to set.
     */
    public void setIncomeStreamAccountNumber(String incomeStreamAccountNumber) {
        this.incomeStreamAccountNumber = incomeStreamAccountNumber;
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
     * Gets the budgetRecordingLevel attribute.
     *
     * @return Returns the budgetRecordingLevel.
     */
    public BudgetRecordingLevel getBudgetRecordingLevel() {
        return budgetRecordingLevel;
    }

    /**
     * Sets the budgetRecordingLevel attribute value.
     *
     * @param budgetRecordingLevel The budgetRecordingLevel to set.
     */
    public void setBudgetRecordingLevel(BudgetRecordingLevel budgetRecordingLevel) {
        this.budgetRecordingLevel = budgetRecordingLevel;
    }

    /**
    /**
     * Gets the sufficientFundsCode attribute.
     * @return Returns the sufficientFundsCode.
     */
    public SufficientFundsCode getSufficientFundsCode() {
        return sufficientFundsCode;
    }

    /**
     * Sets the sufficientFundsCode attribute value.
     * @param sufficientFundsCode The sufficientFundsCode to set.
     */
    public void setSufficientFundsCode(SufficientFundsCode sufficientFundsCode) {
        this.sufficientFundsCode = sufficientFundsCode;
    }

    /**
     * Gets the accountSufficientFundsCode attribute.
     * @return Returns the accountSufficientFundsCode.
     */
    public String getAccountSufficientFundsCode() {
        return accountSufficientFundsCode;
    }

    /**
     * Sets the accountSufficientFundsCode attribute value.
     * @param accountSufficientFundsCode The accountSufficientFundsCode to set.
     */
    public void setAccountSufficientFundsCode(String accountSufficientFundsCode) {
        this.accountSufficientFundsCode = accountSufficientFundsCode;
    }

    /**
     * Gets the pendingAcctSufficientFundsIndicator attribute.
     * @return Returns the pendingAcctSufficientFundsIndicator.
     */
    public boolean isPendingAcctSufficientFundsIndicator() {
        return pendingAcctSufficientFundsIndicator;
    }

    /**
     * Sets the pendingAcctSufficientFundsIndicator attribute value.
     * @param pendingAcctSufficientFundsIndicator The pendingAcctSufficientFundsIndicator to set.
     */
    public void setPendingAcctSufficientFundsIndicator(boolean pendingAcctSufficientFundsIndicator) {
        this.pendingAcctSufficientFundsIndicator = pendingAcctSufficientFundsIndicator;
    }

    /**
     * Gets the extrnlFinEncumSufficntFndIndicator attribute.
     * @return Returns the extrnlFinEncumSufficntFndIndicator.
     */
    public boolean isExtrnlFinEncumSufficntFndIndicator() {
        return extrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Sets the extrnlFinEncumSufficntFndIndicator attribute value.
     * @param extrnlFinEncumSufficntFndIndicator The extrnlFinEncumSufficntFndIndicator to set.
     */
    public void setExtrnlFinEncumSufficntFndIndicator(boolean extrnlFinEncumSufficntFndIndicator) {
        this.extrnlFinEncumSufficntFndIndicator = extrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Gets the intrnlFinEncumSufficntFndIndicator attribute.
     * @return Returns the intrnlFinEncumSufficntFndIndicator.
     */
    public boolean isIntrnlFinEncumSufficntFndIndicator() {
        return intrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Sets the intrnlFinEncumSufficntFndIndicator attribute value.
     * @param intrnlFinEncumSufficntFndIndicator The intrnlFinEncumSufficntFndIndicator to set.
     */
    public void setIntrnlFinEncumSufficntFndIndicator(boolean intrnlFinEncumSufficntFndIndicator) {
        this.intrnlFinEncumSufficntFndIndicator = intrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Gets the finPreencumSufficientFundIndicator attribute.
     * @return Returns the finPreencumSufficientFundIndicator.
     */
    public boolean isFinPreencumSufficientFundIndicator() {
        return finPreencumSufficientFundIndicator;
    }

    /**
     * Sets the finPreencumSufficientFundIndicator attribute value.
     * @param finPreencumSufficientFundIndicator The finPreencumSufficientFundIndicator to set.
     */
    public void setFinPreencumSufficientFundIndicator(boolean finPreencumSufficientFundIndicator) {
        this.finPreencumSufficientFundIndicator = finPreencumSufficientFundIndicator;
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
    public void setFinancialObjectivePrsctrlIndicator(boolean financialObjectivePrsctrlIndicator) {
        this.financialObjectivePrsctrlIndicator = financialObjectivePrsctrlIndicator;
    }


    /**
     * Gets the contractsAndGrantsAccountResponsibilityId attribute.
     * @return Returns the contractsAndGrantsAccountResponsibilityId.
     */
    public Integer getContractsAndGrantsAccountResponsibilityId() {
        return contractsAndGrantsAccountResponsibilityId;
    }

    /**
     * Sets the contractsAndGrantsAccountResponsibilityId attribute value.
     * @param contractsAndGrantsAccountResponsibilityId The contractsAndGrantsAccountResponsibilityId to set.
     */
    public void setContractsAndGrantsAccountResponsibilityId(Integer contractsAndGrantsAccountResponsibilityId) {
        this.contractsAndGrantsAccountResponsibilityId = contractsAndGrantsAccountResponsibilityId;
    }

    /**
     * Gets the accountDescriptionCampusCode attribute.
     * @return Returns the accountDescriptionCampusCode.
     */
    public String getAccountDescriptionCampusCode() {
        return accountDescriptionCampusCode;
    }

    /**
     * Sets the accountDescriptionCampusCode attribute value.
     * @param accountDescriptionCampusCode The accountDescriptionCampusCode to set.
     */
    public void setAccountDescriptionCampusCode(String accountDescriptionCampusCode) {
        this.accountDescriptionCampusCode = accountDescriptionCampusCode;
    }

    /**
     * Gets the accountDescriptionBuildingCode attribute.
     * @return Returns the accountDescriptionBuildingCode.
     */
    public String getAccountDescriptionBuildingCode() {
        return accountDescriptionBuildingCode;
    }

    /**
     * Sets the accountDescriptionBuildingCode attribute value.
     * @param accountDescriptionBuildingCode The accountDescriptionBuildingCode to set.
     */
    public void setAccountDescriptionBuildingCode(String accountDescriptionBuildingCode) {
        this.accountDescriptionBuildingCode = accountDescriptionBuildingCode;
    }

    /**
     * Gets the active attribute.
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the accountFiscalOfficerUser attribute.
     * @return Returns the accountFiscalOfficerUser.
     */
    public Person getAccountFiscalOfficerUser() {
        accountFiscalOfficerUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountFiscalOfficerSystemIdentifier, accountFiscalOfficerUser);
        return accountFiscalOfficerUser;
    }

    /**
     * Sets the accountFiscalOfficerUser attribute value.
     * @param accountFiscalOfficerUser The accountFiscalOfficerUser to set.
     */
    public void setAccountFiscalOfficerUser(Person accountFiscalOfficerUser) {
        this.accountFiscalOfficerUser = accountFiscalOfficerUser;
    }

    /**
     * Gets the accountSupervisoryUser attribute.
     * @return Returns the accountSupervisoryUser.
     */
    public Person getAccountSupervisoryUser() {
        accountSupervisoryUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountsSupervisorySystemsIdentifier, accountSupervisoryUser);
        return accountSupervisoryUser;
    }

    /**
     * Sets the accountSupervisoryUser attribute value.
     * @param accountSupervisoryUser The accountSupervisoryUser to set.
     */
    public void setAccountSupervisoryUser(Person accountSupervisoryUser) {
        this.accountSupervisoryUser = accountSupervisoryUser;
    }

    /**
     * Gets the accountManagerUser attribute.
     * @return Returns the accountManagerUser.
     */
    public Person getAccountManagerUser() {
        accountManagerUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountManagerSystemIdentifier, accountManagerUser);
        return accountManagerUser;
    }

    /**
     * Sets the accountManagerUser attribute value.
     * @param accountManagerUser The accountManagerUser to set.
     */
    public void setAccountManagerUser(Person accountManagerUser) {
        this.accountManagerUser = accountManagerUser;
    }

    public ContractsAndGrantsUnit getUnitDTO() {
        return unitDTO = (ContractsAndGrantsUnit) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsUnit.class).retrieveExternalizableBusinessObjectIfNecessary(this, unitDTO, "unitDTO");
    }

    public void setUnitDTO(ContractsAndGrantsUnit unitDTO) {
        this.unitDTO = unitDTO;
    }

    /**
     *
     */
    public Integer getAccountDefaultId() {
        return accountDefaultId;
    }

    /**
     *
     */
    public void setAccountDefaultId(Integer accountDefaultId) {
        this.accountDefaultId = accountDefaultId;
    }

    public List<IndirectCostRecoveryAutoDefAccount> getIndirectCostRecoveryAutoDefAccounts() {
        return this.indirectCostRecoveryAutoDefAccounts;
    }

    public List<IndirectCostRecoveryAutoDefAccount> getActiveIndirectCostRecoveryAccounts() {
        List<IndirectCostRecoveryAutoDefAccount> activeList = new ArrayList<IndirectCostRecoveryAutoDefAccount>();
        for (IndirectCostRecoveryAutoDefAccount icr : getIndirectCostRecoveryAutoDefAccounts()){
            if (icr.isActive()){
                activeList.add(IndirectCostRecoveryAutoDefAccount.copyICRAccount(icr));
            }
        }
        return activeList;
    }

    public void setIndirectCostRecoveryAutoDefAccounts(List<? extends IndirectCostRecoveryAutoDefAccount> indirectCostRecoveryAccounts) {
        List<IndirectCostRecoveryAutoDefAccount> accountIcrList = new ArrayList<IndirectCostRecoveryAutoDefAccount>();
        for (IndirectCostRecoveryAutoDefAccount icr : indirectCostRecoveryAccounts){
            accountIcrList.add(icr);
        }
        this.indirectCostRecoveryAutoDefAccounts = accountIcrList;
    }
    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        List<Collection<PersistableBusinessObject>> managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add((List)getIndirectCostRecoveryAutoDefAccounts());
        return managedLists;
    }

}
