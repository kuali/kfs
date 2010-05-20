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

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class AccountAutoCreateDefaults extends PersistableBusinessObjectBase implements Inactivateable {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountAutoCreateDefaults.class);

    private String KCUnit;
    private String KCUnitName;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String accoutPostalCode;
    private String accountCityName;
    private String accountStateCode;
    private String accountStreetAddress;
    private boolean accountOffCampusIndicator;
    private String accountTypeCode;
    private String subFundGroupCode;
    private boolean accountsFringesBnftIndicator;
    private boolean accountFringeBenfit;
    protected Chart fringeBenefitsChartOfAccount;
    private String fringeBenefitAccountNumber;
    private String financialHigherEdFunctionCd;
    private String fiscalOfficerPrincipalName;
    private String accountSupervisorPrincipalName;
    private String accountManagerPrincipalName;
    private String continuationFinChrtOfAcctCd;
    private String continuationAccountNumber;
    private String incomeStreamFinancialCoaCode;
    private String incomeStreamAccountNumber;
    private String budgetRecordingLevelCode;
    private String accountSufficientFundsCode;
    private boolean pendingAcctSufficientFundsIndicator;
    private boolean extrnlFinEncumSufficntFndIndicator;
    private boolean intrnlFinEncumSufficntFndIndicator;
    private boolean finPreencumSufficientFundIndicator;
    private boolean financialObjectivePrsctrlIndicator;
    private String indirectCostRcvyFinCoaCode;
    private String indirectCostRecoveryAcctNbr;
    private Integer contractsAndGrantsAccountResponsibilityId;
    private String accountExpenseGuidelineText;
    private String accountIncomeGuidelineText;
    private String accountPurposeText;

    private boolean active;

/*    private String accountFiscalOfficerSystemIdentifier;
    private String accountsSupervisorySystemsIdentifier;
    private String accountManagerSystemIdentifier;
    private String accountPhysicalCampusCode;
    private String accountRestrictedStatusCode;
    private String reportsToChartOfAccountsCode;
    private String reportsToAccountNumber;
    private String endowmentIncomeAcctFinCoaCd;
    private String endowmentIncomeAccountNumber;
    private String contractControlFinCoaCode;
    private String contractControlAccountNumber;

    private Chart chartOfAccounts;
    private Chart endowmentIncomeChartOfAccounts;
    private Organization organization;
    private AccountType accountType;
    private Campus accountPhysicalCampus;
    private State accountState;
    private SubFundGroup subFundGroup;
    private HigherEducationFunction financialHigherEdFunction;
    private RestrictedStatus accountRestrictedStatus;
    private Account reportsToAccount;
    private Account continuationAccount;
    private Account endowmentIncomeAccount;
    private Account contractControlAccount;
    private Account incomeStreamAccount;
    private Account indirectCostRecoveryAcct;
    private IndirectCostRecoveryType acctIndirectCostRcvyType;
    private BudgetRecordingLevel budgetRecordingLevel;
    private SufficientFundsCode sufficientFundsCode;
    private ContractsAndGrantsCfda cfda;

    protected Chart continuationChartOfAccount;
    protected Chart incomeStreamChartOfAccounts;
    protected Chart contractControlChartOfAccounts;
    protected Chart indirectCostRcvyChartOfAccounts;
    
    // Several kinds of Dummy Attributes for dividing sections on Inquiry page
    private String accountResponsibilitySectionBlank;
    private String accountResponsibilitySection;
    private String contractsAndGrantsSectionBlank;
    private String contractsAndGrantsSection;
    private String guidelinesAndPurposeSectionBlank;
    private String guidelinesAndPurposeSection;
    private String accountDescriptionSectionBlank;
    private String accountDescriptionSection;

    private Boolean forContractsAndGrants;

    private AccountGuideline accountGuideline;
    private AccountDescription accountDescription;

    private List subAccounts;
    private List<ContractsAndGrantsAccountAwardInformation> awards;
*/ 
    /**
     * Default no-arg constructor.
     */
    public AccountAutoCreateDefaults() {
        active = true; // assume active until otherwise set
    }

    /**
     * Gets the kCUnit attribute. 
     * @return Returns the kCUnit.
     */
    public String getKCUnit() {
        return KCUnit;
    }

    /**
     * Sets the kCUnit attribute value.
     * @param kCUnit The kCUnit to set.
     */
    public void setKCUnit(String kCUnit) {
        KCUnit = kCUnit;
    }

    /**
     * Gets the kCUnitName attribute. 
     * @return Returns the kCUnitName.
     */
    public String getKCUnitName() {
        return KCUnitName;
    }

    /**
     * Sets the kCUnitName attribute value.
     * @param kCUnitName The kCUnitName to set.
     */
    public void setKCUnitName(String kCUnitName) {
        KCUnitName = kCUnitName;
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
     * Gets the accoutPostalCode attribute. 
     * @return Returns the accoutPostalCode.
     */
    public String getAccoutPostalCode() {
        return accoutPostalCode;
    }

    /**
     * Sets the accoutPostalCode attribute value.
     * @param accoutPostalCode The accoutPostalCode to set.
     */
    public void setAccoutPostalCode(String accoutPostalCode) {
        this.accoutPostalCode = accoutPostalCode;
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
     * Gets the accountFringeBenfit attribute. 
     * @return Returns the accountFringeBenfit.
     */
    public boolean isAccountFringeBenfit() {
        return accountFringeBenfit;
    }

    /**
     * Sets the accountFringeBenfit attribute value.
     * @param accountFringeBenfit The accountFringeBenfit to set.
     */
    public void setAccountFringeBenfit(boolean accountFringeBenfit) {
        this.accountFringeBenfit = accountFringeBenfit;
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
     * Gets the fiscalOfficerPrincipalName attribute. 
     * @return Returns the fiscalOfficerPrincipalName.
     */
    public String getFiscalOfficerPrincipalName() {
        return fiscalOfficerPrincipalName;
    }

    /**
     * Sets the fiscalOfficerPrincipalName attribute value.
     * @param fiscalOfficerPrincipalName The fiscalOfficerPrincipalName to set.
     */
    public void setFiscalOfficerPrincipalName(String fiscalOfficerPrincipalName) {
        this.fiscalOfficerPrincipalName = fiscalOfficerPrincipalName;
    }

    /**
     * Gets the accountSupervisorPrincipalName attribute. 
     * @return Returns the accountSupervisorPrincipalName.
     */
    public String getAccountSupervisorPrincipalName() {
        return accountSupervisorPrincipalName;
    }

    /**
     * Sets the accountSupervisorPrincipalName attribute value.
     * @param accountSupervisorPrincipalName The accountSupervisorPrincipalName to set.
     */
    public void setAccountSupervisorPrincipalName(String accountSupervisorPrincipalName) {
        this.accountSupervisorPrincipalName = accountSupervisorPrincipalName;
    }

    /**
     * Gets the accountManagerPrincipalName attribute. 
     * @return Returns the accountManagerPrincipalName.
     */
    public String getAccountManagerPrincipalName() {
        return accountManagerPrincipalName;
    }

    /**
     * Sets the accountManagerPrincipalName attribute value.
     * @param accountManagerPrincipalName The accountManagerPrincipalName to set.
     */
    public void setAccountManagerPrincipalName(String accountManagerPrincipalName) {
        this.accountManagerPrincipalName = accountManagerPrincipalName;
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        return m;
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

    
}
