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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimDelegationTypeService;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

public class AccountDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase implements KimDelegationTypeService {

    private AccountService accountService;
    private ContractsAndGrantsModuleService contractsAndGrantsModuleService;
    protected List<String> requiredAttributes = new ArrayList<String>();
    {
        requiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        requiredAttributes.add(KfsKimAttributes.ACCOUNT_NUMBER);
        requiredAttributes.add(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
    }

    /**
     * Attributes: Chart Code Account Number Requirements: - KFS-COA Account Supervisor: CA_ACCOUNT_T.ACCT_SPVSR_UNVL_ID - KFS-SYS
     * Fiscal Officer: CA_ACCOUNT_T.ACCT_FSC_OFC_UID - KFS-SYS Fiscal Officer Primary Delegate: CA_ACCT_DELEGATE_T.ACCT_DLGT_UNVL_ID
     * where ACCT_DLGT_PRMRT_CD = Y - KFS-SYS Fiscal Officer Secondary Delegate: CA_ACCT_DELEGATE_T.ACCT_DLGT_UNVL_ID where
     * ACCT_DLGT_PRMRT_CD = N - KFS-SYS Award Project Director: use the getProjectDirectorForAccount(String chartOfAccountsCode,
     * String accountNumber) method on the contracts and grants module service
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String,
     *      java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        // validate received attributes
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);

        String chartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String accountNumber = qualification.get(KfsKimAttributes.ACCOUNT_NUMBER);

        String financialSystemDocumentTypeCodeCode = qualification.get(KfsKimAttributes.DOCUMENT_TYPE_NAME);
        String totalDollarAmount = qualification.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT);

        String fiscalOfficerPrincipalID = qualification.get(KFSPropertyConstants.ACCOUNT_FISCAL_OFFICER_SYSTEM_IDENTIFIER);
        String accountSupervisorPrincipalID = qualification.get(KFSPropertyConstants.ACCOUNTS_SUPERVISORY_SYSTEMS_IDENTIFIER);
        String principalId = qualification.get(KimAttributes.PRINCIPAL_ID);

        // Default to 0 total amount
        if (StringUtils.isEmpty(totalDollarAmount)) {
            totalDollarAmount = getDefaultTotalAmount();
        }

        List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>();
        AttributeSet roleQualifier = new AttributeSet();
        roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        roleQualifier.put(KfsKimAttributes.ACCOUNT_NUMBER, accountNumber);

        if (chartOfAccountsCode == null && accountNumber == null && StringUtils.isNotBlank(principalId)) {
            RoleMembershipInfo roleMembershipInfo = getRoleMembershipInfoWhenAccountInfoUnavailable(roleName, principalId, roleQualifier);
            if(ObjectUtils.isNotNull(roleMembershipInfo)) {
                members.add(roleMembershipInfo);
            }
        }

        if (KFSConstants.SysKimConstants.ACCOUNT_SUPERVISOR_KIM_ROLE_NAME.equals(roleName)) {
            Account account = getAccount(chartOfAccountsCode, accountNumber);
            if (account != null) {
                members.add(new RoleMembershipInfo(null, null, account.getAccountsSupervisorySystemsIdentifier(), Role.PRINCIPAL_MEMBER_TYPE, roleQualifier));
            }
            // only add the additional approver if they are different
            if (StringUtils.isNotBlank(accountSupervisorPrincipalID) && (account == null || !StringUtils.equals(accountSupervisorPrincipalID, account.getAccountsSupervisorySystemsIdentifier()))) {
                members.add(new RoleMembershipInfo(null, null, accountSupervisorPrincipalID, Role.PRINCIPAL_MEMBER_TYPE, roleQualifier));
            }
        }
        else if (KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME.equals(roleName)) {
            Account account = getAccount(chartOfAccountsCode, accountNumber);
            if (account != null) {
                members.add(new RoleMembershipInfo(null, null, account.getAccountFiscalOfficerSystemIdentifier(), Role.PRINCIPAL_MEMBER_TYPE, roleQualifier));
            }
            // only add the additional approver if they are different
            if (StringUtils.isNotBlank(fiscalOfficerPrincipalID) && (account == null || !StringUtils.equals(fiscalOfficerPrincipalID, account.getAccountFiscalOfficerSystemIdentifier()))) {
                members.add(new RoleMembershipInfo(null, null, fiscalOfficerPrincipalID, Role.PRINCIPAL_MEMBER_TYPE, roleQualifier));
            }
        }
        else if (KFSConstants.SysKimConstants.FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
            AccountDelegate delegate = getPrimaryDelegate(chartOfAccountsCode, accountNumber, financialSystemDocumentTypeCodeCode, totalDollarAmount);
            if (delegate != null) {
                roleQualifier.put(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE, delegate.getFinancialDocumentTypeCode());
                roleQualifier.put(KfsKimAttributes.FROM_AMOUNT, (delegate.getFinDocApprovalFromThisAmt() == null) ? "0" : delegate.getFinDocApprovalFromThisAmt().toString());
                roleQualifier.put(KfsKimAttributes.TO_AMOUNT, (delegate.getFinDocApprovalToThisAmount() == null) ? "NOLIMIT" : delegate.getFinDocApprovalToThisAmount().toString());
                members.add(new RoleMembershipInfo(null, null, delegate.getAccountDelegateSystemId(), Role.PRINCIPAL_MEMBER_TYPE, roleQualifier));
            }
        }
        else if (KFSConstants.SysKimConstants.FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
            List<AccountDelegate> delegates = getSecondaryDelegates(chartOfAccountsCode, accountNumber, financialSystemDocumentTypeCodeCode, totalDollarAmount);
            for (AccountDelegate delegate : delegates) {
                roleQualifier.put(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE, delegate.getFinancialDocumentTypeCode());
                roleQualifier.put(KfsKimAttributes.FROM_AMOUNT, (delegate.getFinDocApprovalFromThisAmt() == null) ? "0" : delegate.getFinDocApprovalFromThisAmt().toString());
                roleQualifier.put(KfsKimAttributes.TO_AMOUNT, (delegate.getFinDocApprovalToThisAmount() == null) ? "NOLIMIT" : delegate.getFinDocApprovalToThisAmount().toString());
                members.add(new RoleMembershipInfo(null, null, delegate.getAccountDelegateSystemId(), Role.PRINCIPAL_MEMBER_TYPE, roleQualifier));
            }
        }
        else if (KFSConstants.SysKimConstants.AWARD_SECONDARY_DIRECTOR_KIM_ROLE_NAME.equals(roleName)) {
            Person person = getProjectDirectorForAccount(chartOfAccountsCode, accountNumber);
            if (person != null) {
                members.add(new RoleMembershipInfo(null, null, person.getPrincipalId(), Role.PRINCIPAL_MEMBER_TYPE, roleQualifier));
            }
        }

        return members;
    }

    // build memebership information based on the given role name for the given principal when account information is unavailable
    private RoleMembershipInfo getRoleMembershipInfoWhenAccountInfoUnavailable(String roleName, String principalId, AttributeSet roleQualifier) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        RoleMembershipInfo roleMembershipInfo = null;

        if ((KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME.equals(roleName))) {
            fieldValues.put(KFSPropertyConstants.ACCOUNT_FISCAL_OFFICER_SYSTEM_IDENTIFIER, principalId);

            if (businessObjectService.countMatching(Account.class, fieldValues) > 0) {
                roleMembershipInfo = new RoleMembershipInfo(null, null, principalId, Role.PRINCIPAL_MEMBER_TYPE, roleQualifier);
            }
        }
        else if (KFSConstants.SysKimConstants.FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
            fieldValues.put(KFSPropertyConstants.ACCOUNT_DELEGATE_SYSTEM_ID, principalId);
            fieldValues.put(KFSPropertyConstants.ACCOUNTS_DELEGATE_PRMRT_INDICATOR, Boolean.TRUE);

            if (businessObjectService.countMatching(AccountDelegate.class, fieldValues) > 0) {
                roleMembershipInfo = new RoleMembershipInfo(null, null, principalId, Role.PRINCIPAL_MEMBER_TYPE, roleQualifier);
            }
        }
        else if (KFSConstants.SysKimConstants.FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)) {
            fieldValues.put(KFSPropertyConstants.ACCOUNT_DELEGATE_SYSTEM_ID, principalId);
            fieldValues.put(KFSPropertyConstants.ACCOUNTS_DELEGATE_PRMRT_INDICATOR, Boolean.FALSE);

            if (businessObjectService.countMatching(AccountDelegate.class, fieldValues) > 0) {
                roleMembershipInfo = new RoleMembershipInfo(null, null, principalId, Role.PRINCIPAL_MEMBER_TYPE, roleQualifier);
            }
        }
        
        return roleMembershipInfo;
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
    public AccountService getAccountService() {
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

    public boolean doesDelegationQualifierMatchQualification(AttributeSet qualification, AttributeSet delegationQualifier) {
        return performMatch(translateInputAttributeSet(qualification), delegationQualifier);
    }

    public List<DelegateInfo> doDelegationQualifiersMatchQualification(AttributeSet qualification, List<DelegateInfo> delegationMemberList) {
        AttributeSet translatedQualification = translateInputAttributeSet(qualification);
        List<DelegateInfo> matchingMemberships = new ArrayList<DelegateInfo>();
        for (DelegateInfo dmi : delegationMemberList) {
            if (performMatch(translatedQualification, dmi.getQualifier())) {
                matchingMemberships.add(dmi);
            }
        }
        return matchingMemberships;
    }

    public AttributeSet convertQualificationAttributesToRequired(AttributeSet qualificationAttributes) {
        return qualificationAttributes;
    }

    public ContractsAndGrantsModuleService getContractsAndGrantsModuleService() {
        if (contractsAndGrantsModuleService == null) {
            contractsAndGrantsModuleService = SpringContext.getBean(ContractsAndGrantsModuleService.class);
        }
        return contractsAndGrantsModuleService;
    }

}