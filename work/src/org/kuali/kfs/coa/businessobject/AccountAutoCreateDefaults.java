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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.service.StateService;

/**
 * 
 */
public class AccountAutoCreateDefaults extends PersistableBusinessObjectBase implements Inactivateable {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountAutoCreateDefaults.class);

    private String kcUnit;
    //private KCUnit kcUnit;
    private String kcUnitName;
    private Chart chartOfAccounts;
    private String chartOfAccountsCode;
    private Organization organization;
    private String organizationCode;
    private String accountZipCode;
    private PostalCode postalZipCode;
    private String accountCityName;
    private String accountStateCode;
    private State accountState;
    private String accountStreetAddress;
    private boolean accountOffCampusIndicator;
    private AccountType accountType;
    private String accountTypeCode;
    private String accountPhysicalCampusCode;
    private Campus accountPhysicalCampus;
    private SubFundGroup subFundGroup;
    private String subFundGroupCode;
    private boolean accountsFringesBnftIndicator;
    protected Chart fringeBenefitsChartOfAccount;
    private String reportsToChartOfAccountsCode;
    private String fringeBenefitAccountNumber;
    private HigherEducationFunction financialHigherEdFunction;
    private String financialHigherEdFunctionCd;
    private String accountFiscalOfficerSystemIdentifier;
    private String accountsSupervisorySystemsIdentifier;
    private String accountManagerSystemIdentifier;
    protected Chart continuationChartOfAccount;
    private String continuationFinChrtOfAcctCd;
    private Account continuationAccount;
    private String continuationAccountNumber;
    private Account incomeStreamAccount;
    protected Chart incomeStreamChartOfAccounts;
    private String incomeStreamFinancialCoaCode;
    private String incomeStreamAccountNumber;
    private String budgetRecordingLevelCode;
    private SufficientFundsCode sufficientFundsCode;
    private String accountSufficientFundsCode;
    private boolean pendingAcctSufficientFundsIndicator;
    private boolean extrnlFinEncumSufficntFndIndicator;
    private boolean intrnlFinEncumSufficntFndIndicator;
    private boolean finPreencumSufficientFundIndicator;
    private boolean financialObjectivePrsctrlIndicator;
    private IndirectCostRecoveryType acctIndirectCostRcvyType;
    private String acctIndirectCostRcvyTypeCd;
    protected Chart indirectCostRcvyChartOfAccounts;
    private String indirectCostRcvyFinCoaCode;
    private Account indirectCostRecoveryAcct;
    private String indirectCostRecoveryAcctNbr;
    private Integer contractsAndGrantsAccountResponsibilityId;
    private String accountExpenseGuidelineText;
    private String accountIncomeGuidelineText;
    private String accountPurposeText;
    private String accountDescriptionCampusCode;
    private String accountDescriptionBuildingCode;
    private boolean active;

    //
    private Person accountFiscalOfficerUser;
    private Person accountSupervisoryUser;
    private Person accountManagerUser;
    

    /**
     * Default no-arg constructor.
     */
    public AccountAutoCreateDefaults() {
        active = true; // assume active until otherwise set
    }
   
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        return m;
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
    public PostalCode getPostalZipCode() {
        postalZipCode = SpringContext.getBean(PostalCodeService.class).getByPostalCodeInDefaultCountryIfNecessary(accountZipCode, postalZipCode);
        return postalZipCode;
    }

    /**
     * Sets the postalZipCode attribute value.
     * @param postalZipCode The postalZipCode to set.
     */
    public void setPostalZipCode(PostalCode postalZipCode) {
        this.postalZipCode = postalZipCode;
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
    public State getAccountState() {
        return accountState;
    }

    /**
     * Sets the accountState attribute value.
     * @param accountState The accountState to set.
     */
    public void setAccountState(State accountState) {
        this.accountState = accountState;
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
    public Campus getAccountPhysicalCampus() {
        return accountPhysicalCampus;
    }

    /**
     * Sets the accountPhysicalCampus attribute value.
     * @param accountPhysicalCampus The accountPhysicalCampus to set.
     */
    public void setAccountPhysicalCampus(Campus accountPhysicalCampus) {
        this.accountPhysicalCampus = accountPhysicalCampus;
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
     * Gets the fringeBenefitAccountNumber attribute. 
     * @return Returns the fringeBenefitAccountNumber.
     */
    public String getFringeBenefitAccountNumber() {
        return fringeBenefitAccountNumber;
    }

    /**
     * Sets the fringeBenefitAccountNumber attribute value.
     * @param fringeBenefitAccountNumber The fringeBenefitAccountNumber to set.
     */
    public void setFringeBenefitAccountNumber(String fringeBenefitAccountNumber) {
        this.fringeBenefitAccountNumber = fringeBenefitAccountNumber;
    }

    /**
     * Gets the financialHigherEdFunction attribute. 
     * @return Returns the financialHigherEdFunction.
     */
    public HigherEducationFunction getFinancialHigherEdFunction() {
        return financialHigherEdFunction;
    }

    /**
     * Sets the financialHigherEdFunction attribute value.
     * @param financialHigherEdFunction The financialHigherEdFunction to set.
     */
    public void setFinancialHigherEdFunction(HigherEducationFunction financialHigherEdFunction) {
        this.financialHigherEdFunction = financialHigherEdFunction;
    }

    /**
     * Gets the financialHigherEdFunctionCd attribute. 
     * @return Returns the financialHigherEdFunctionCd.
     */
    public String getFinancialHigherEdFunctionCd() {
        return financialHigherEdFunctionCd;
    }

    /**
     * Sets the financialHigherEdFunctionCd attribute value.
     * @param financialHigherEdFunctionCd The financialHigherEdFunctionCd to set.
     */
    public void setFinancialHigherEdFunctionCd(String financialHigherEdFunctionCd) {
        this.financialHigherEdFunctionCd = financialHigherEdFunctionCd;
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
     * Gets the acctIndirectCostRcvyType attribute. 
     * @return Returns the acctIndirectCostRcvyType.
     */
    public IndirectCostRecoveryType getAcctIndirectCostRcvyType() {
        return acctIndirectCostRcvyType;
    }

    /**
     * Sets the acctIndirectCostRcvyType attribute value.
     * @param acctIndirectCostRcvyType The acctIndirectCostRcvyType to set.
     */
    public void setAcctIndirectCostRcvyType(IndirectCostRecoveryType acctIndirectCostRcvyType) {
        this.acctIndirectCostRcvyType = acctIndirectCostRcvyType;
    }

    /**
     * Gets the acctIndirectCostRcvyTypeCd attribute. 
     * @return Returns the acctIndirectCostRcvyTypeCd.
     */
    public String getAcctIndirectCostRcvyTypeCd() {
        return acctIndirectCostRcvyTypeCd;
    }

    /**
     * Sets the acctIndirectCostRcvyTypeCd attribute value.
     * @param acctIndirectCostRcvyTypeCd The acctIndirectCostRcvyTypeCd to set.
     */
    public void setAcctIndirectCostRcvyTypeCd(String acctIndirectCostRcvyTypeCd) {
        this.acctIndirectCostRcvyTypeCd = acctIndirectCostRcvyTypeCd;
    }

    /**
     * Gets the indirectCostRcvyChartOfAccounts attribute. 
     * @return Returns the indirectCostRcvyChartOfAccounts.
     */
    public Chart getIndirectCostRcvyChartOfAccounts() {
        return indirectCostRcvyChartOfAccounts;
    }

    /**
     * Sets the indirectCostRcvyChartOfAccounts attribute value.
     * @param indirectCostRcvyChartOfAccounts The indirectCostRcvyChartOfAccounts to set.
     */
    public void setIndirectCostRcvyChartOfAccounts(Chart indirectCostRcvyChartOfAccounts) {
        this.indirectCostRcvyChartOfAccounts = indirectCostRcvyChartOfAccounts;
    }

    /**
     * Gets the indirectCostRcvyFinCoaCode attribute. 
     * @return Returns the indirectCostRcvyFinCoaCode.
     */
    public String getIndirectCostRcvyFinCoaCode() {
        return indirectCostRcvyFinCoaCode;
    }

    /**
     * Sets the indirectCostRcvyFinCoaCode attribute value.
     * @param indirectCostRcvyFinCoaCode The indirectCostRcvyFinCoaCode to set.
     */
    public void setIndirectCostRcvyFinCoaCode(String indirectCostRcvyFinCoaCode) {
        this.indirectCostRcvyFinCoaCode = indirectCostRcvyFinCoaCode;
    }

    /**
     * Gets the indirectCostRecoveryAcct attribute. 
     * @return Returns the indirectCostRecoveryAcct.
     */
    public Account getIndirectCostRecoveryAcct() {
        return indirectCostRecoveryAcct;
    }

    /**
     * Sets the indirectCostRecoveryAcct attribute value.
     * @param indirectCostRecoveryAcct The indirectCostRecoveryAcct to set.
     */
    public void setIndirectCostRecoveryAcct(Account indirectCostRecoveryAcct) {
        this.indirectCostRecoveryAcct = indirectCostRecoveryAcct;
    }

    /**
     * Gets the indirectCostRecoveryAcctNbr attribute. 
     * @return Returns the indirectCostRecoveryAcctNbr.
     */
    public String getIndirectCostRecoveryAcctNbr() {
        return indirectCostRecoveryAcctNbr;
    }

    /**
     * Sets the indirectCostRecoveryAcctNbr attribute value.
     * @param indirectCostRecoveryAcctNbr The indirectCostRecoveryAcctNbr to set.
     */
    public void setIndirectCostRecoveryAcctNbr(String indirectCostRecoveryAcctNbr) {
        this.indirectCostRecoveryAcctNbr = indirectCostRecoveryAcctNbr;
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
     * Gets the accountExpenseGuidelineText attribute. 
     * @return Returns the accountExpenseGuidelineText.
     */
    public String getAccountExpenseGuidelineText() {
        return accountExpenseGuidelineText;
    }

    /**
     * Sets the accountExpenseGuidelineText attribute value.
     * @param accountExpenseGuidelineText The accountExpenseGuidelineText to set.
     */
    public void setAccountExpenseGuidelineText(String accountExpenseGuidelineText) {
        this.accountExpenseGuidelineText = accountExpenseGuidelineText;
    }

    /**
     * Gets the accountIncomeGuidelineText attribute. 
     * @return Returns the accountIncomeGuidelineText.
     */
    public String getAccountIncomeGuidelineText() {
        return accountIncomeGuidelineText;
    }

    /**
     * Sets the accountIncomeGuidelineText attribute value.
     * @param accountIncomeGuidelineText The accountIncomeGuidelineText to set.
     */
    public void setAccountIncomeGuidelineText(String accountIncomeGuidelineText) {
        this.accountIncomeGuidelineText = accountIncomeGuidelineText;
    }

    /**
     * Gets the accountPurposeText attribute. 
     * @return Returns the accountPurposeText.
     */
    public String getAccountPurposeText() {
        return accountPurposeText;
    }

    /**
     * Sets the accountPurposeText attribute value.
     * @param accountPurposeText The accountPurposeText to set.
     */
    public void setAccountPurposeText(String accountPurposeText) {
        this.accountPurposeText = accountPurposeText;
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
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the accountFiscalOfficerUser attribute. 
     * @return Returns the accountFiscalOfficerUser.
     */
    public Person getAccountFiscalOfficerUser() {
        accountFiscalOfficerUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(accountFiscalOfficerSystemIdentifier, accountFiscalOfficerUser);        
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
        accountSupervisoryUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(accountsSupervisorySystemsIdentifier, accountSupervisoryUser);
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
        accountManagerUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(accountManagerSystemIdentifier, accountManagerUser);
        return accountManagerUser;
    }

    /**
     * Sets the accountManagerUser attribute value.
     * @param accountManagerUser The accountManagerUser to set.
     */
    public void setAccountManagerUser(Person accountManagerUser) {
        this.accountManagerUser = accountManagerUser;
    }
    
}
