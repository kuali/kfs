/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.EmployeeStatus;
import org.kuali.rice.kns.bo.EmployeeType;
import org.kuali.rice.kns.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.kns.bo.user.KualiGroup;
import org.kuali.rice.kns.bo.user.KualiModuleUser;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;

public class FinancialSystemUser extends UniversalUser {

    protected transient static FinancialSystemUserService financialSystemUserService;
    protected transient static ChartService chartService;
    protected transient static AccountService accountService;
    
    protected static FinancialSystemUserService getFinancialSystemUserService() {
        if ( financialSystemUserService == null ) {
            financialSystemUserService = SpringContext.getBean(FinancialSystemUserService.class);
        }
        return financialSystemUserService;
    }
    
    protected UniversalUser embeddedUniversalUser = null;

    protected boolean activeFinancialSystemUser = false;
    protected String chartOfAccountsCode;
    protected String organizationCode;
    protected Chart chartOfAccounts;
    protected Org organization;
    protected List<FinancialSystemUserPrimaryOrganization> primaryOrganizations = new TypedArrayList(FinancialSystemUserPrimaryOrganization.class);
    protected List<FinancialSystemUserOrganizationSecurity> organizationSecurity = new TypedArrayList(FinancialSystemUserOrganizationSecurity.class);
    protected transient Map accountResponsibilities = null;
    // account map keyed by chartCode + "::" + accountNumber
    protected transient Map chartResponsibilities = null;

    public void refreshEmbeddedUniversalUser() {
        if ( getPersonUniversalIdentifier() != null && 
                (embeddedUniversalUser == null || !StringUtils.equals( getPersonUniversalIdentifier(), embeddedUniversalUser.getPersonUniversalIdentifier() )) ) {
            try {
                embeddedUniversalUser = getFinancialSystemUserService().getUniversalUser(getPersonUniversalIdentifier());
            } catch ( UserNotFoundException ex ) {
                embeddedUniversalUser = new UniversalUser();
                embeddedUniversalUser.setPersonUniversalIdentifier(getPersonUniversalIdentifier());
            }
        } else if ( getPersonUniversalIdentifier() == null ) {
            setEmbeddedUniversalUser( new UniversalUser() );
        }
    }
    
    public UniversalUser getEmbeddedUniversalUser() {
        refreshEmbeddedUniversalUser();
        return embeddedUniversalUser;
    }


    public void setEmbeddedUniversalUser(UniversalUser embeddedUniversalUser) {
        if ( this.embeddedUniversalUser == null ) {
            this.embeddedUniversalUser = embeddedUniversalUser;
        }
    }


    @Override
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        if ( !StringUtils.equals(personUniversalIdentifier, getPersonUniversalIdentifier() ) ) {
            super.setPersonUniversalIdentifier(personUniversalIdentifier);
            if ( embeddedUniversalUser == null || embeddedUniversalUser.getObjectId() != null ) {
                refreshEmbeddedUniversalUser();
            }
        }
    }
    
    public void clearPersonUniversalIdentifierForCopy() {
        super.setPersonUniversalIdentifier(null);
        embeddedUniversalUser.setPersonUniversalIdentifier(null);
        embeddedUniversalUser.setObjectId(null);
        embeddedUniversalUser.setVersionNumber(null);
    }
    
    public boolean isActiveFinancialSystemUser() {
        return activeFinancialSystemUser;
    }


    public void setActiveFinancialSystemUser(boolean activeFinancialSystemUser) {
        this.activeFinancialSystemUser = activeFinancialSystemUser;
    }

    public List<FinancialSystemUserPrimaryOrganization> getPrimaryOrganizations() {
        return primaryOrganizations;
    }

    public void setPrimaryOrganizations(List<FinancialSystemUserPrimaryOrganization> primaryOrganizations) {
        this.primaryOrganizations = primaryOrganizations;
    }
    
    public FinancialSystemUserPrimaryOrganization getPrimaryOrganizationByModuleId( String moduleId ) {
        if ( moduleId == null ) {
            return null;
        }
        for ( FinancialSystemUserPrimaryOrganization org : getPrimaryOrganizations() ) {
            if ( org.getModuleId().equals(moduleId)) {
                return org;
            }
        }
        return null;
    }
    

    public List<FinancialSystemUserOrganizationSecurity> getOrganizationSecurity() {
        return organizationSecurity;
    }

    public void setOrganizationSecurity(List<FinancialSystemUserOrganizationSecurity> organizationSecurity) {
        this.organizationSecurity = organizationSecurity;
    }

    public List<FinancialSystemUserOrganizationSecurity> getOrganizationSecurityByModuleId( String moduleId ) {
        List<FinancialSystemUserOrganizationSecurity> orgs = new ArrayList<FinancialSystemUserOrganizationSecurity>();
        if ( moduleId == null ) {
            return orgs;
        }
        for ( FinancialSystemUserOrganizationSecurity org : getOrganizationSecurity() ) {
            if ( org.getModuleId().equals(moduleId)) {
                orgs.add(org);
            }
        }
        return orgs;
    }

    
    public String getActiveModuleCodeString() {
        return getEmbeddedUniversalUser().getActiveModuleCodeString();
    }

    public Campus getCampus() {
        return getEmbeddedUniversalUser().getCampus();
    }


    public String getCampusCode() {
        return getEmbeddedUniversalUser().getCampusCode();
    }


    public Set<String> getChangedModuleCodes() {
        return getEmbeddedUniversalUser().getChangedModuleCodes();
    }


    public EmployeeStatus getEmployeeStatus() {
        return getEmbeddedUniversalUser().getEmployeeStatus();
    }


    public String getEmployeeStatusCode() {
        return getEmbeddedUniversalUser().getEmployeeStatusCode();
    }


    public EmployeeType getEmployeeType() {
        return getEmbeddedUniversalUser().getEmployeeType();
    }


    public String getEmployeeTypeCode() {
        return getEmbeddedUniversalUser().getEmployeeTypeCode();
    }


    public PersistableBusinessObjectExtension getExtension() {
        return getEmbeddedUniversalUser().getExtension();
    }


    public String getFinancialSystemsEncryptedPasswordText() {
        return getEmbeddedUniversalUser().getFinancialSystemsEncryptedPasswordText();
    }


    public List<KualiGroup> getGroups() {
        return getEmbeddedUniversalUser().getGroups();
    }


    public Map<String, Map<String, String>> getModuleProperties() {
        return getEmbeddedUniversalUser().getModuleProperties();
    }


    public Map<String, String> getModuleProperties(String moduleId) {
        return getEmbeddedUniversalUser().getModuleProperties(moduleId);
    }


    public KualiModuleUser getModuleUser(String moduleId) {
        return getEmbeddedUniversalUser().getModuleUser(moduleId);
    }


    public Map<String, KualiModuleUser> getModuleUsers() {
        return getEmbeddedUniversalUser().getModuleUsers();
    }

    public KualiDecimal getPersonBaseSalaryAmount() {
        return getEmbeddedUniversalUser().getPersonBaseSalaryAmount();
    }


    public String getPersonCampusAddress() {
        return getEmbeddedUniversalUser().getPersonCampusAddress();
    }


    public String getPersonEmailAddress() {
        return getEmbeddedUniversalUser().getPersonEmailAddress();
    }


    public String getPersonFirstName() {
        return getEmbeddedUniversalUser().getPersonFirstName();
    }


    public String getPersonLastName() {
        return getEmbeddedUniversalUser().getPersonLastName();
    }


    public String getPersonLocalPhoneNumber() {
        return getEmbeddedUniversalUser().getPersonLocalPhoneNumber();
    }


    public String getPersonMiddleName() {
        return getEmbeddedUniversalUser().getPersonMiddleName();
    }


    public String getPersonName() {
        return getEmbeddedUniversalUser().getPersonName();
    }


    public String getPersonPayrollIdentifier() {
        return getEmbeddedUniversalUser().getPersonPayrollIdentifier();
    }


    public String getPersonTaxIdentifier() {
        return getEmbeddedUniversalUser().getPersonTaxIdentifier();
    }


    public String getPersonTaxIdentifierTypeCode() {
        return getEmbeddedUniversalUser().getPersonTaxIdentifierTypeCode();
    }

    public String getPersonUserIdentifier() {
        return getEmbeddedUniversalUser().getPersonUserIdentifier();
    }


    public String getPrimaryDepartmentCode() {
        return getEmbeddedUniversalUser().getPrimaryDepartmentCode();
    }

    public boolean isActiveForAnyModule() {
        // initial check - KFS as a whole
        boolean active = isActiveFinancialSystemUser();
        
        // if not an explicitly active user for KFS, check the module authorizers
        if ( !active ) {
            active = SpringContext.getBean(FinancialSystemUserService.class).isActiveForAnyModule( this );            
        }
        
        return active;
    }


    public boolean isActiveForModule(String moduleId) {
        return SpringContext.getBean(FinancialSystemUserService.class).isActiveForModule( this, moduleId );
    }


    public boolean isAffiliate() {
        return getEmbeddedUniversalUser().isAffiliate();
    }

    public boolean isFaculty() {
        return getEmbeddedUniversalUser().isFaculty();
    }


    public boolean isMember(KualiGroup kualiGroup) {
        return getEmbeddedUniversalUser().isMember(kualiGroup);
    }


    public boolean isMember(String groupName) {
        return getEmbeddedUniversalUser().isMember(groupName);
    }


    public boolean isStaff() {
        return getEmbeddedUniversalUser().isStaff();
    }


    public boolean isStudent() {
        return getEmbeddedUniversalUser().isStudent();
    }


    public boolean isSupervisorUser() {
        return getEmbeddedUniversalUser().isSupervisorUser();
    }


    public boolean isWorkflowExceptionUser() {
        return getEmbeddedUniversalUser().isWorkflowExceptionUser();
    }

    public void refreshUserGroups() {
        getEmbeddedUniversalUser().refreshUserGroups();
    }


    public void setAffiliate(boolean affiliate) {
        getEmbeddedUniversalUser().setAffiliate(affiliate);
    }

    public void setCampus(Campus campus) {
        getEmbeddedUniversalUser().setCampus(campus);
    }


    public void setCampusCode(String campusCode) {
        getEmbeddedUniversalUser().setCampusCode(campusCode);
    }


    public void setChangedModuleCodes(Set<String> changedModuleCodes) {
        getEmbeddedUniversalUser().setChangedModuleCodes(changedModuleCodes);
    }


    public void setEmployeeStatus(EmployeeStatus employeeStatus) {
        getEmbeddedUniversalUser().setEmployeeStatus(employeeStatus);
    }


    public void setEmployeeStatusCode(String employeeStatusCode) {
        getEmbeddedUniversalUser().setEmployeeStatusCode(employeeStatusCode);
    }


    public void setEmployeeType(EmployeeType employeeType) {
        getEmbeddedUniversalUser().setEmployeeType(employeeType);
    }


    public void setEmployeeTypeCode(String personTypeCode) {
        getEmbeddedUniversalUser().setEmployeeTypeCode(personTypeCode);
    }

    public void setFaculty(boolean faculty) {
        getEmbeddedUniversalUser().setFaculty(faculty);
    }


    public void setFinancialSystemsEncryptedPasswordText(String financialSystemsEncryptedPasswordText) {
        getEmbeddedUniversalUser().setFinancialSystemsEncryptedPasswordText(financialSystemsEncryptedPasswordText);
    }


    public void setGroups(List<KualiGroup> groups) {
        getEmbeddedUniversalUser().setGroups(groups);
    }


    public void setPersonBaseSalaryAmount(KualiDecimal personBaseSalaryAmount) {
        getEmbeddedUniversalUser().setPersonBaseSalaryAmount(personBaseSalaryAmount);
    }


    public void setPersonCampusAddress(String personCampusAddress) {
        getEmbeddedUniversalUser().setPersonCampusAddress(personCampusAddress);
    }


    public void setPersonEmailAddress(String personEmailAddress) {
        getEmbeddedUniversalUser().setPersonEmailAddress(personEmailAddress);
    }


    public void setPersonFirstName(String personFirstName) {
        getEmbeddedUniversalUser().setPersonFirstName(personFirstName);
    }


    public void setPersonLastName(String personLastName) {
        getEmbeddedUniversalUser().setPersonLastName(personLastName);
    }


    public void setPersonLocalPhoneNumber(String personLocalPhoneNumber) {
        getEmbeddedUniversalUser().setPersonLocalPhoneNumber(personLocalPhoneNumber);
    }


    public void setPersonMiddleName(String personMiddleName) {
        getEmbeddedUniversalUser().setPersonMiddleName(personMiddleName);
    }


    public void setPersonName(String personName) {
        getEmbeddedUniversalUser().setPersonName(personName);
    }


    public void setPersonPayrollIdentifier(String emplid) {
        getEmbeddedUniversalUser().setPersonPayrollIdentifier(emplid);
    }


    public void setPersonTaxIdentifier(String personSocialSecurityNbrId) {
        getEmbeddedUniversalUser().setPersonTaxIdentifier(personSocialSecurityNbrId);
    }


    public void setPersonTaxIdentifierTypeCode(String personTaxIdentifierTypeCode) {
        getEmbeddedUniversalUser().setPersonTaxIdentifierTypeCode(personTaxIdentifierTypeCode);
    }


    public void setPersonUserIdentifier(String personUserIdentifier) {
        getEmbeddedUniversalUser().setPersonUserIdentifier(personUserIdentifier);
    }


    public void setPrimaryDepartmentCode(String deptid) {
        getEmbeddedUniversalUser().setPrimaryDepartmentCode(deptid);
    }


    public void setStaff(boolean staff) {
        getEmbeddedUniversalUser().setStaff(staff);
    }


    public void setStudent(boolean student) {
        getEmbeddedUniversalUser().setStudent(student);
    }

    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public Org getOrganization() {
        return organization;
    }

    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }
    
    public Map getAccountResponsibilities() {
        if (accountResponsibilities == null && getEmbeddedUniversalUser() != null && getEmbeddedUniversalUser().getPersonUniversalIdentifier() != null) {
            setAccountResponsibilities(getAccountService().getAccountsThatUserIsResponsibleFor(getEmbeddedUniversalUser()));
        }
        return accountResponsibilities;
    }

    public void setAccountResponsibilities(Map accountResponsibilities) {
        this.accountResponsibilities = accountResponsibilities;
    }
    
    public Map getChartResponsibilities() {
        if (chartResponsibilities == null && getEmbeddedUniversalUser() != null && getEmbeddedUniversalUser().getPersonUniversalIdentifier() != null) {
            setChartResponsibilities(getChartService().getChartsThatUserIsResponsibleFor(getEmbeddedUniversalUser()));
        }
        return chartResponsibilities;
    }

    public void setChartResponsibilities(Map chartResponsibilities) {
        this.chartResponsibilities = chartResponsibilities;
    }

    /**
     * check if the user is an administrator
     * 
     * @return
     */
    public boolean isAdministratorUser() {
        return getFinancialSystemUserService().isAdministratorUser(this);
    }

    /**
     * boolean to indicate if the user is responsible for a given account
     * 
     * @param account
     * @return true if the user is responsible for the account passed in
     */
    public boolean isResponsibleForAccount(String chartCode, String accountNumber) {
        boolean isResponsible = false;

        if (!StringUtils.isEmpty(chartCode) && !StringUtils.isEmpty(accountNumber)) {
            String accountKey = buildAccountKey(chartCode, accountNumber);

            isResponsible = getAccountResponsibilities().containsKey(accountKey);
        }

        return isResponsible;
    }


    /**
     * boolean to indicate if the user is responsible for a given chart
     * 
     * @param chartCode
     * @return true if the user is responsible for the chart passed in
     */
    public boolean isManagerForChart(String chartCode) {
        boolean isResponsible = false;

        if (!StringUtils.isEmpty(chartCode)) {

            isResponsible = getChartResponsibilities().containsKey(chartCode);
        }

        return isResponsible;
    }

    /**
     * Convenience setter for converting AccountResponsibility list into AccountResponsibility map
     * 
     * @param accountList The accountResponsibilities to set.
     */
    public void setChartResponsibilities(List accountList) {
        // build the map
        Map chartMap = new HashMap();
        for (Iterator i = accountList.iterator(); i.hasNext();) {
            Chart chart = (Chart) i.next();
            String chartCode = chart.getChartOfAccountsCode();

            chartMap.put(chartCode, chart);
        }

        setChartResponsibilities(chartMap);
    }

    /**
     * Convenience setter for converting AccountResponsibility list into AccountResponsibility map
     * 
     * @param accountList The accountResponsibilities to set.
     */
    public void setAccountResponsibilities(List accountList) {
        // build the map
        Map accountMap = new HashMap();
        for (Iterator i = accountList.iterator(); i.hasNext();) {
            AccountResponsibility accountResponsibility = (AccountResponsibility) i.next();
            Account account = accountResponsibility.getAccount();

            accountMap.put(buildAccountKey(account.getChartOfAccountsCode(), account.getAccountNumber()), accountResponsibility);
        }

        setAccountResponsibilities(accountMap);
    }

    /**
     * @param chartCode
     * @param accountNumber
     * @return key used to store the given Account in the Account Map
     */
    private String buildAccountKey(String chartCode, String accountNumber) {
        if (StringUtils.isEmpty(chartCode)) {
            throw new IllegalArgumentException("invalid (blank) chartCode");
        }
        if (StringUtils.isEmpty(accountNumber)) {
            throw new IllegalArgumentException("invalid (blank) accountNumber");
        }

        String accountKey = chartCode + "::" + accountNumber;
        return accountKey;
    }
    
    public static ChartService getChartService() {
        if ( chartService == null ) {
            chartService = SpringContext.getBean(ChartService.class);
        }
        return chartService;
    }
    
    public static AccountService getAccountService() {
        if ( accountService == null ) {
            accountService = SpringContext.getBean(AccountService.class);
        }
        return accountService;
    }
    
}
