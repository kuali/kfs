/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.coa.identity;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.service.AccountDelegateService;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.util.ObjectUtils;

public class AccountDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    protected AccountService accountService;
    protected ContractsAndGrantsModuleService contractsAndGrantsModuleService;
    protected AccountDelegateService accountDelegateService;
    protected ConfigurationService configurationService;
    protected IdentityManagementService identityManagementService;
    protected MailService mailService;
    protected ParameterService parameterService;
    
    protected final static String DERIVED_ROLE_MEMBER_INACTIVATION_NOTIFICATION_EMAIL_ADDRESSES_PARAMETER_NAME = "DERIVED_ROLE_MEMBER_INACTIVATION_NOTIFICATION_EMAIL_ADDRESSES";

    /**
     * Attributes: Chart Code Account Number Requirements: - KFS-COA Account Supervisor: CA_ACCOUNT_T.ACCT_SPVSR_UNVL_ID - KFS-SYS
     * Fiscal Officer: CA_ACCOUNT_T.ACCT_FSC_OFC_UID - KFS-SYS Fiscal Officer Primary Delegate: CA_ACCT_DELEGATE_T.ACCT_DLGT_UNVL_ID
     * where ACCT_DLGT_PRMRT_CD = Y - KFS-SYS Fiscal Officer Secondary Delegate: CA_ACCT_DELEGATE_T.ACCT_DLGT_UNVL_ID where
     * ACCT_DLGT_PRMRT_CD = N - KFS-SYS Award Project Director: use the getProjectDirectorForAccount(String chartOfAccountsCode,
     * String accountNumber) method on the contracts and grants module service
     * 
     * @see org.kuali.rice.kns.kim.role.RoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String,
     *      java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        // validate received attributes
        validateRequiredAttributesAgainstReceived(qualification);
        List<RoleMembership> members = new ArrayList<RoleMembership>();
        if(qualification!=null && !qualification.isEmpty()){
            String chartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            String accountNumber = qualification.get(KfsKimAttributes.ACCOUNT_NUMBER);
    
            String financialSystemDocumentTypeCodeCode = qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
            String totalDollarAmount = qualification.get(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT);
    
            String fiscalOfficerPrincipalID = qualification.get(KFSPropertyConstants.ACCOUNT_FISCAL_OFFICER_SYSTEM_IDENTIFIER);
            String accountSupervisorPrincipalID = qualification.get(KFSPropertyConstants.ACCOUNTS_SUPERVISORY_SYSTEMS_IDENTIFIER);
            String principalId = qualification.get(KimConstants.AttributeConstants.PRINCIPAL_ID);
    
            // Default to 0 total amount
            if (StringUtils.isEmpty(totalDollarAmount)) {
                totalDollarAmount = getDefaultTotalAmount();
            }
    
            Map<String,String> roleQualifier = new HashMap<String,String>();
            roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            roleQualifier.put(KfsKimAttributes.ACCOUNT_NUMBER, accountNumber);
    
            if (chartOfAccountsCode == null && accountNumber == null && StringUtils.isNotBlank(principalId)) {
                RoleMembership roleMembershipInfo = getRoleMembershipWhenAccountInfoUnavailable(roleName, principalId, roleQualifier);
                if(ObjectUtils.isNotNull(roleMembershipInfo)) {
                    members.add(roleMembershipInfo);
                }
            }                        
    
            if (KFSConstants.SysKimApiConstants.ACCOUNT_SUPERVISOR_KIM_ROLE_NAME.equals(roleName)) {
                Account account = getAccount(chartOfAccountsCode, accountNumber);
                if (account != null) {
                    members.add(RoleMembership.Builder.create(null, null, account.getAccountsSupervisorySystemsIdentifier(), MemberType.PRINCIPAL, roleQualifier).build());
                }
                // only add the additional approver if they are different
                if (StringUtils.isNotBlank(accountSupervisorPrincipalID) && (account == null || !StringUtils.equals(accountSupervisorPrincipalID, account.getAccountsSupervisorySystemsIdentifier()))) {                    
                    members.add(RoleMembership.Builder.create(null, null, accountSupervisorPrincipalID, MemberType.PRINCIPAL, roleQualifier).build());
                }
            }
            else if (KFSConstants.SysKimApiConstants.FISCAL_OFFICER_KIM_ROLE_NAME.equals(roleName)) {
                Account account = getAccount(chartOfAccountsCode, accountNumber);
                if (account != null) {                    
                    members.add(RoleMembership.Builder.create(null, null, account.getAccountFiscalOfficerSystemIdentifier(), MemberType.PRINCIPAL, roleQualifier).build());
                }
                // only add the additional approver if they are different
                if (StringUtils.isNotBlank(fiscalOfficerPrincipalID) && (account == null || !StringUtils.equals(fiscalOfficerPrincipalID, account.getAccountFiscalOfficerSystemIdentifier()))) {
                    members.add(RoleMembership.Builder.create(null, null, fiscalOfficerPrincipalID, MemberType.PRINCIPAL, roleQualifier).build());                    
                }
            }
            else if (KFSConstants.SysKimApiConstants.FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
                AccountDelegate delegate = getPrimaryDelegate(chartOfAccountsCode, accountNumber, financialSystemDocumentTypeCodeCode, totalDollarAmount);
                if (delegate != null) {
                    roleQualifier.put(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE, delegate.getFinancialDocumentTypeCode());
                    roleQualifier.put(KfsKimAttributes.FROM_AMOUNT, (delegate.getFinDocApprovalFromThisAmt() == null) ? "0" : delegate.getFinDocApprovalFromThisAmt().toString());
                    roleQualifier.put(KfsKimAttributes.TO_AMOUNT, (delegate.getFinDocApprovalToThisAmount() == null) ? "NOLIMIT" : delegate.getFinDocApprovalToThisAmount().toString());
                    members.add(RoleMembership.Builder.create(null, null, delegate.getAccountDelegateSystemId(), MemberType.PRINCIPAL, roleQualifier).build());                       
                }
            }
            else if (KFSConstants.SysKimApiConstants.FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
                List<AccountDelegate> delegates = getSecondaryDelegates(chartOfAccountsCode, accountNumber, financialSystemDocumentTypeCodeCode, totalDollarAmount);
                for (AccountDelegate delegate : delegates) {
                    roleQualifier.put(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE, delegate.getFinancialDocumentTypeCode());
                    roleQualifier.put(KfsKimAttributes.FROM_AMOUNT, (delegate.getFinDocApprovalFromThisAmt() == null) ? "0" : delegate.getFinDocApprovalFromThisAmt().toString());
                    roleQualifier.put(KfsKimAttributes.TO_AMOUNT, (delegate.getFinDocApprovalToThisAmount() == null) ? "NOLIMIT" : delegate.getFinDocApprovalToThisAmount().toString());
                    members.add(RoleMembership.Builder.create(null, null, delegate.getAccountDelegateSystemId(), MemberType.PRINCIPAL, roleQualifier).build());                    
                }
            }
            else if (KFSConstants.SysKimApiConstants.AWARD_SECONDARY_DIRECTOR_KIM_ROLE_NAME.equals(roleName)) {
                Person person = getProjectDirectorForAccount(chartOfAccountsCode, accountNumber);
                if (person != null) {
                    members.add(RoleMembership.Builder.create(null, null, person.getPrincipalId(), MemberType.PRINCIPAL, roleQualifier).build());                    
                }
            }
        }
        return members;
    }

    // build memebership information based on the given role name for the given principal when account information is unavailable
    protected RoleMembership getRoleMembershipWhenAccountInfoUnavailable(String roleName, String principalId, Map<String,String> roleQualifier) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        
        RoleMembership roleMembership = null;

        if ((KFSConstants.SysKimApiConstants.FISCAL_OFFICER_KIM_ROLE_NAME.equals(roleName))) {
            fieldValues.put(KFSPropertyConstants.ACCOUNT_FISCAL_OFFICER_SYSTEM_IDENTIFIER, principalId);

            if (businessObjectService.countMatching(Account.class, fieldValues) > 0) {
                roleMembership = RoleMembership.Builder.create(null, null, principalId, MemberType.PRINCIPAL, roleQualifier).build();
            }
        }
        else if (KFSConstants.SysKimApiConstants.FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
            fieldValues.put(KFSPropertyConstants.ACCOUNT_DELEGATE_SYSTEM_ID, principalId);
            fieldValues.put(KFSPropertyConstants.ACCOUNTS_DELEGATE_PRMRT_INDICATOR, Boolean.TRUE);

            if (businessObjectService.countMatching(AccountDelegate.class, fieldValues) > 0) {
                roleMembership = RoleMembership.Builder.create(null, null, principalId, MemberType.PRINCIPAL, roleQualifier).build();
            }
        }
        else if (KFSConstants.SysKimApiConstants.FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
            fieldValues.put(KFSPropertyConstants.ACCOUNT_DELEGATE_SYSTEM_ID, principalId);
            fieldValues.put(KFSPropertyConstants.ACCOUNTS_DELEGATE_PRMRT_INDICATOR, Boolean.FALSE);

            if (businessObjectService.countMatching(AccountDelegate.class, fieldValues) > 0) {
                roleMembership = RoleMembership.Builder.create(null, null, principalId, MemberType.PRINCIPAL, roleQualifier).build();
            }
        }
        
        return roleMembership;
    }

    protected Account getAccount(String chartOfAccountsCode, String accountNumber) {
        return getAccountService().getByPrimaryId(chartOfAccountsCode, accountNumber);
    }

    protected AccountDelegate getPrimaryDelegate(String chartOfAccountsCode, String accountNumber, String fisDocumentTypeCode, String totalDollarAmount) {
        return getAccountService().getPrimaryDelegationByExample(getDelegateExample(chartOfAccountsCode, accountNumber, fisDocumentTypeCode), totalDollarAmount);
    }

    protected List<AccountDelegate> getSecondaryDelegates(String chartOfAccountsCode, String accountNumber, String fisDocumentTypeCode, String totalDollarAmount) {
        return getAccountService().getSecondaryDelegationsByExample(getDelegateExample(chartOfAccountsCode, accountNumber, fisDocumentTypeCode), totalDollarAmount);
    }

    protected Person getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber) {
        return getContractsAndGrantsModuleService().getProjectDirectorForAccount(chartOfAccountsCode, accountNumber);
    }

    // Default to 0 total amount
    protected String getDefaultTotalAmount() {
        return "0";
    }

    protected AccountDelegate getDelegateExample(String chartOfAccountsCode, String accountNumber, String fisDocumentTypeCode) {
        AccountDelegate delegateExample = new AccountDelegate();
        delegateExample.setChartOfAccountsCode(chartOfAccountsCode);
        delegateExample.setAccountNumber(accountNumber);
        delegateExample.setFinancialDocumentTypeCode(fisDocumentTypeCode);
        return delegateExample;
    }

    /**
     * Gets the accountService attribute.
     * 
     * @return Returns the accountService.
     */
    protected AccountService getAccountService() {
        if (accountService == null) {
            accountService = SpringContext.getBean(AccountService.class);
        }
        return accountService;
    }

    /**
     * Sets the accountService attribute value.
     * 
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    /**
     * @return an implementation of the AccountDelegateService
     */
    protected AccountDelegateService getAccountDelegateService() {
        if (accountDelegateService == null) {
            accountDelegateService = SpringContext.getBean(AccountDelegateService.class);
        }
        return accountDelegateService;
    }

    /**
     * Sets the accountDelegateService attribute value.
     * @param accountDelegateService The accountDelegateService to set.
     */
    public void setAccountDelegateService(AccountDelegateService accountDelegateService) {
        this.accountDelegateService = accountDelegateService;
    }
    
    /**
     * @return an implementation of the ConfigurationService
     */
    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }
    
    /**
     * @return an implementation of the IdentityManagementService
     */
    protected IdentityManagementService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }
    
    /**
     * @return an implementation of the MailService
     */
    protected MailService getMailService() {
        if (mailService == null) {
            mailService = SpringContext.getBean(MailService.class);
        }
        return mailService;
    }
    
    /**
     * @return an implementation of the ParameterService
     */
    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    protected ContractsAndGrantsModuleService getContractsAndGrantsModuleService() {
        if (contractsAndGrantsModuleService == null) {
            contractsAndGrantsModuleService = SpringContext.getBean(ContractsAndGrantsModuleService.class);
        }
        return contractsAndGrantsModuleService;
    }

    /**
     * @see org.kuali.rice.kns.kim.role.RoleTypeServiceBase#principalInactivated(java.lang.String, java.lang.String, java.lang.String)
     */
 //   @Override
    public void principalInactivated(String principalId, String namespaceCode, String roleName) {
    //    super.principalInactivated(principalId, namespaceCode, roleName);
        
        if (KFSConstants.SysKimApiConstants.ACCOUNT_SUPERVISOR_KIM_ROLE_NAME.equals(roleName)) {
            if (getAccountService().isPrincipalInAnyWayShapeOrFormAccountSupervisor(principalId)) {
                handleAccountSupervisorInactivation(principalId, namespaceCode, roleName);
            }
        }
        else if (KFSConstants.SysKimApiConstants.FISCAL_OFFICER_KIM_ROLE_NAME.equals(roleName)) {
            if (getAccountService().isPrincipalInAnyWayShapeOrFormFiscalOfficer(principalId)) {
                handleFiscalOfficerInactivation(principalId, namespaceCode, roleName);
            }
        }
        else if (KFSConstants.SysKimApiConstants.FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
            if (getAccountDelegateService().isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(principalId)) {
                handlePrimaryAccountDelegateInactivation(principalId, namespaceCode, roleName);
            }
        }
        else if (KFSConstants.SysKimApiConstants.FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
            if (getAccountDelegateService().isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(principalId)) {
                handleSecondaryAccountDelegateInactivation(principalId, namespaceCode, roleName);
            }
        }
    }
    
    /**
     * Notifies the specified list that the given principal is being inactivated, listing any active or expired accounts the principal is fiscal officer of
     * @param principalId the principal id of the inactivating person
     * @param roleNamespace the namespace of the role the inactivated principal is leaving
     * @param roleName the name of the role the inactivated principal is leaving
     */
    protected void handleFiscalOfficerInactivation(String principalId, String roleNamespace, String roleName) {
        // build the message
        final Principal principal = getIdentityManagementService().getPrincipal(principalId);
        
        Iterator<Account> activeAccounts = getAccountService().getActiveAccountsForFiscalOfficer(principalId);
        final String joinedActiveAccounts = joinAccounts(activeAccounts);
        Iterator<Account> inactiveAccounts = getAccountService().getExpiredAccountsForFiscalOfficer(principalId);
        final String joinedExpiredAccounts = joinAccounts(inactiveAccounts);
        
        final String message = getMessage(KFSKeyConstants.MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_FISCAL_OFFICER_NOTIFICATION, roleNamespace, roleName, principal.getPrincipalName(), joinedActiveAccounts, joinedExpiredAccounts);
        
        // get listserv to mail to
        String toAddress = getDeactivationInterestAddress();
        try {
            
            // get mail message
            MailMessage mailMessage = buildMailMessage(toAddress, message, roleNamespace, roleName, principalId);
            // send it
            getMailService().sendMessage(mailMessage);
        }
        catch (Exception iae) {
            throw new RuntimeException("Could not mail principal inactivation notification to "+toAddress);
        }
        
    }
    
    /**
     * Notifies the specified list that the given principal is being inactivated, listing any active or expired accounts the principal is account supervisor of
     * @param principalId the principal id of the inactivating person
     * @param roleNamespace the namespace of the role the inactivated principal is leaving
     * @param roleName the name of the role the inactivated principal is leaving
     */
    protected void handleAccountSupervisorInactivation(String principalId, String roleNamespace, String roleName) {
        // build the message
        final Principal principal = getIdentityManagementService().getPrincipal(principalId);
        
        Iterator<Account> activeAccounts = getAccountService().getActiveAccountsForAccountSupervisor(principalId);
        final String joinedActiveAccounts = joinAccounts(activeAccounts);
        Iterator<Account> inactiveAccounts = getAccountService().getExpiredAccountsForAccountSupervisor(principalId);
        final String joinedExpiredAccounts = joinAccounts(inactiveAccounts);
        
        final String message = getMessage(KFSKeyConstants.MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_ACCOUNT_SUPERVISOR_NOTIFICATION, roleNamespace, roleName, principal.getPrincipalName(), joinedActiveAccounts, joinedExpiredAccounts);
        
        // get listserv to mail to
        String toAddress = getDeactivationInterestAddress();
        try {
            
            // get mail message
            MailMessage mailMessage = buildMailMessage(toAddress, message, roleNamespace, roleName, principalId);
            // send it
            getMailService().sendMessage(mailMessage);
        }
        catch (Exception iae) {
            throw new RuntimeException("Could not mail principal inactivation notification to "+toAddress);
        }
    }
    
    /**
     * Does the logic for the inactivation of a secondary delegate
     * @param principalId the principal id of the inactivating person
     * @param roleNamespace the namespace of the role the inactivated principal is leaving
     * @param roleName the name of the role the inactivated principal is leaving
     */
    protected void handlePrimaryAccountDelegateInactivation(String principalId, String roleNamespace, String roleName) {
        handleAccountDelegateInactivation(principalId, roleNamespace, roleName, true);
    }
    
    /**
     * Does the logic for the inactivation of a secondary delegate
     * @param principalId the principal id of the inactivating person
     * @param roleNamespace the namespace of the role the inactivated principal is leaving
     * @param roleName the name of the role the inactivated principal is leaving
     */
    protected void handleSecondaryAccountDelegateInactivation(String principalId, String roleNamespace, String roleName) {
        handleAccountDelegateInactivation(principalId, roleNamespace, roleName, false);
    }
    
    /**
     * Handles the inactivation of account delegates when their principal is inactivated
     * @param principalId the principal id of the inactivating person
     * @param roleNamespace the namespace of the role the inactivated principal is leaving
     * @param roleName the name of the role the inactivated principal is leaving
     * @param primary whether primary delegates should be inactivated, rather than secondary
     */
    protected void handleAccountDelegateInactivation(String principalId, String roleNamespace, String roleName, boolean primary) {
        List<String> accountDisdelegations = new ArrayList<String>();
        List<String> blockedAccountDisdelegations = new ArrayList<String>();
        
        Iterator<AccountDelegate> accountDelegationsToInactivate = getAccountDelegateService().retrieveAllActiveDelegationsForPerson(principalId, primary);
        while (accountDelegationsToInactivate.hasNext()) {
            final AccountDelegate accountDelegate = accountDelegationsToInactivate.next();
            final String blockingDocumentId = lookupDocumentBlockingInactivation(accountDelegate);
            if (StringUtils.isBlank(blockingDocumentId)) {
                accountDisdelegations.add(getAccountDisdelegationInformationForAccountDelegate(accountDelegate));
                inactivateDelegation(accountDelegate);
            } else {
                blockedAccountDisdelegations.add(getBlockedAccountDisdelegationInformationForAccountDelegate(accountDelegate, blockingDocumentId));
            }            
        }
        notifyListAboutAccountDelegateInactivations(principalId, roleNamespace, roleName, accountDisdelegations, blockedAccountDisdelegations, primary);
    }
    
    /**
     * Returns the document number of a maintenance document which would block the inactivation of the given account delegate
     * @param delegate the account delegate to check
     * @return the document id of the maintenance document blocking the inactivation of the given account delegate, null otherwise
     */
    protected String lookupDocumentBlockingInactivation(AccountDelegate delegate) {
        FinancialSystemMaintainable maintainable = getAccountDelegateService().buildMaintainableForAccountDelegate(delegate);
        return maintainable.getLockingDocumentId();
    }
    
    /**
     * Inactivates an account delegate
     * @param delegate the account delegate to inactivate
     */
    protected void inactivateDelegation(AccountDelegate delegate) {
        delegate.setActive(false); // this is deprecated?  lame!
        getBusinessObjectService().save(delegate);
    }
    
    /**
     * Converts information from the given AccountDelegate record into an informative String
     * @param accountDelegate the account delegate to report on
     * @return the report on the given account delegate
     */
    protected String getAccountDisdelegationInformationForAccountDelegate(AccountDelegate accountDelegate) {
        final CurrencyFormatter formatter = new CurrencyFormatter();
        final String message = getMessage(KFSKeyConstants.MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_ACCOUNT_DELEGATE_INACTIVATED_INFORMATION, accountDelegate.getChartOfAccountsCode(), accountDelegate.getAccountNumber(), accountDelegate.getFinancialDocumentTypeCode(), (String)(accountDelegate.getFinDocApprovalFromThisAmt() != null ? formatter.format(accountDelegate.getFinDocApprovalFromThisAmt()) : ""), (String)(accountDelegate.getFinDocApprovalFromThisAmt() != null ? formatter.format(accountDelegate.getFinDocApprovalFromThisAmt()) : ""));
        return message;
    }
    
    /**
     * Converts information from the given AccountDelegate record into an informative String
     * @param accountDelegate the account delegate to report on
     * @return the report on the given account delegate
     */
    protected String getBlockedAccountDisdelegationInformationForAccountDelegate(AccountDelegate accountDelegate, String blockingDocumentId) {
        final CurrencyFormatter formatter = new CurrencyFormatter();
        final String message = getMessage(KFSKeyConstants.MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_ACCOUNT_DELEGATE_BLOCKED_INACTIVATION_INFORMATION, accountDelegate.getChartOfAccountsCode(), accountDelegate.getAccountNumber(), accountDelegate.getFinancialDocumentTypeCode(), (String)(accountDelegate.getFinDocApprovalFromThisAmt() != null ? formatter.format(accountDelegate.getFinDocApprovalFromThisAmt()) : ""), (String)(accountDelegate.getFinDocApprovalFromThisAmt() != null ? formatter.format(accountDelegate.getFinDocApprovalFromThisAmt()) : ""), blockingDocumentId);
        return message;    
    }
    
    /**
     * Fires an e-mail off the specified e-mail listserv about the account delegates inactivated by a
     * @param principalId the principal being inactivated 
     * @param roleNamespace the namespace of the role the inactivated principal is leaving
     * @param roleName the name of the role the inactivated principal is leaving
     * @param accountsDisdelegated the account delegate records which were inactivated
     * @param blockedDisdelegations the account delegate records which could not be inactivated because of locking maintenance documents
     * @param primary whether the account delegations being reported on were primary delegations, as opposed to secondary delegations
     */
    protected void notifyListAboutAccountDelegateInactivations(String principalId, String roleNamespace, String roleName, List<String> accountsDisdelegated, List<String> blockedDisdelegations, boolean primary) {
        // build message
        final Principal principal = getIdentityManagementService().getPrincipal(principalId);
        
        final String message = getMessage((primary ? KFSKeyConstants.MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_FISCAL_OFFICER_PRIMARY_DELEGATE_NOTIFICATION : KFSKeyConstants.MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_FISCAL_OFFICER_SECONARY_DELEGATE_NOTIFICATION), roleNamespace, roleName, principal.getPrincipalName(), StringUtils.join(accountsDisdelegated,'\n'), StringUtils.join(blockedDisdelegations, '\n'));
        
        String toAddress = getDeactivationInterestAddress();
        try {
            // get mail message
            MailMessage mailMessage = buildMailMessage(toAddress, message, roleNamespace, roleName, principalId);
            // send it
            getMailService().sendMessage(mailMessage);
        }
        catch (Exception iae) {
            throw new RuntimeException("Could not mail principal inactivation notification to "+toAddress);
        }
    }
    
    /**
     * Retrieves message from configuration service and formats it directly
     * @param messageConstant the property to find the message under
     * @param parameters the parameters to interpolate into the message
     * @return a formatted String
     */
    protected String getMessage(String messageConstant, String...parameters) {
        final String messagePattern = getConfigurationService().getPropertyValueAsString(messageConstant);
        return MessageFormat.format(messagePattern, (Object[])parameters);
    }
    
    /**
     * Joins an Iterator of accounts into a meaningful String
     * @param accounts the accounts to join
     * @return a String of account information
     */
    protected String joinAccounts(Iterator<Account> accounts) {
        StringBuilder s = new StringBuilder();
        int count = 0;
        
        while (accounts.hasNext()) {
            final Account account = accounts.next();
            if (count > 0) {
                s.append('\n');
            }
            s.append(account.getChartOfAccountsCode()+"-"+account.getAccountNumber());
            count += 1;
        }
        return s.toString();
    }
    
    /**
     * Builds a MailMessage to send out a notification e-mail
     * @param toAddress the address to send the notification to
     * @param message the body of the notification
     * @param principalId the principal being inactivated 
     * @param roleNamespace the namespace of the role the inactivated principal is leaving
     * @param roleName the name of the role the inactivated principal is leaving
     * @return an appropriately constructed MailMessage
     */
    protected MailMessage buildMailMessage(String toAddress, String message, String roleNamespace, String roleName, String principalId) {
        MailMessage mailMessage = new MailMessage();
        mailMessage.setFromAddress(getFromAddress());
        mailMessage.setSubject(getMailMessageSubject(roleNamespace, roleName, principalId));
        
        Set<String> toAddresses = new HashSet<String>();
        toAddresses.add(toAddress);
        mailMessage.setToAddresses(toAddresses);
        mailMessage.setMessage(message);
        return mailMessage;
    }
    
    /**
     * Builds a subject line for an e-mail
     * @param principalId the principal being inactivated 
     * @param roleNamespace the namespace of the role the inactivated principal is leaving
     * @param roleName the name of the role the inactivated principal is leaving
     * @return a suitable subject line for the notification e-mail
     */
    protected String getMailMessageSubject(String roleNamespace, String roleName, String principalId) {
        final Principal principal = getIdentityManagementService().getPrincipal(principalId);
        return getMessage(KFSKeyConstants.MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_NOTIFICATION_SUBJECT, roleNamespace, roleName, principal.getPrincipalName());
    }
    
    /**
     * @return the from address to send mail from; in the default version, the given toAddress (but since this is a method, implementers can override)
     */
    protected String getFromAddress() {
        return getDeactivationInterestAddress();
    }
    
    /**
     * @return the e-mail address of those interested in account role principal inactivations
     */
    protected String getDeactivationInterestAddress() {
        return getParameterService().getParameterValueAsString(Account.class, AccountDerivedRoleTypeServiceImpl.DERIVED_ROLE_MEMBER_INACTIVATION_NOTIFICATION_EMAIL_ADDRESSES_PARAMETER_NAME);
    }
    
    
}
