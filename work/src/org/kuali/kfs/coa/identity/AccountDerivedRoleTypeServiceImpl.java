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
import java.util.List;

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
import org.kuali.rice.kim.bo.role.dto.KimDelegationMemberInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimDelegationTypeService;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;

public class AccountDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase implements KimDelegationTypeService {

    private AccountService accountService;
    protected List<String> requiredAttributes = new ArrayList<String>();
    {
        requiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        requiredAttributes.add(KfsKimAttributes.ACCOUNT_NUMBER);
        requiredAttributes.add(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
    }
    
    /**
     * Attributes:
     *  Chart Code
     *  Account Number
     *   
     *  Requirements:
     *  - KFS-COA Account Supervisor: CA_ACCOUNT_T.ACCT_SPVSR_UNVL_ID
     *  - KFS-SYS Fiscal Officer: CA_ACCOUNT_T.ACCT_FSC_OFC_UID
     *  - KFS-SYS Fiscal Officer Primary Delegate: CA_ACCT_DELEGATE_T.ACCT_DLGT_UNVL_ID where ACCT_DLGT_PRMRT_CD = Y
     *  - KFS-SYS Fiscal Officer Secondary Delegate: CA_ACCT_DELEGATE_T.ACCT_DLGT_UNVL_ID where ACCT_DLGT_PRMRT_CD = N
     *  - KFS-SYS Award Project Director: use the getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber) method on the contracts and grants module service
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        //validate received attributes
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        String chartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String accountNumber = qualification.get(KfsKimAttributes.ACCOUNT_NUMBER);
        String generalLedgerInputTypeCode = qualification.get(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
        String totalDollarAmount = qualification.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT);
        //Default to 0 total amount
        if(StringUtils.isEmpty(totalDollarAmount))
                totalDollarAmount = getDefaultTotalAmount();
        List<String> principalIds = new ArrayList<String>();
        
        if(KFSConstants.SysKimConstants.ACCOUNT_SUPERVISOR_KIM_ROLE_NAME.equals(roleName)){
            Account account = getAccount(chartOfAccountsCode, accountNumber);
            if(account!=null) principalIds.add(account.getAccountsSupervisorySystemsIdentifier());
        } else if(KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME.equals(roleName)){
            Account account = getAccount(chartOfAccountsCode, accountNumber);
            if(account!=null) principalIds.add(account.getAccountFiscalOfficerSystemIdentifier());
        } else if(KFSConstants.SysKimConstants.FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)){
            AccountDelegate primaryDelegate = getPrimaryDelegate(chartOfAccountsCode, accountNumber, generalLedgerInputTypeCode, totalDollarAmount);
            if(primaryDelegate!=null) principalIds.add(primaryDelegate.getAccountDelegateSystemId());
        } else if(KFSConstants.SysKimConstants.FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME.equals(roleName)){
            List<AccountDelegate> secondaryDelegates = 
                getSecondaryDelegates(chartOfAccountsCode, accountNumber, generalLedgerInputTypeCode, totalDollarAmount);
            for(AccountDelegate secondaryDelegate: secondaryDelegates)
                principalIds.add(secondaryDelegate.getAccountDelegateSystemId());
        } else if(KFSConstants.SysKimConstants.AWARD_SECONDARY_DIRECTOR_KIM_ROLE_NAME.equals(roleName)){
            Person person = getProjectDirectorForAccount(chartOfAccountsCode, accountNumber);
            if(person!=null) principalIds.add(person.getPrincipalId());
        }
        
        return principalIds;
    }

    protected Account getAccount(String chartOfAccountsCode, String accountNumber){
        return accountService.getByPrimaryId(chartOfAccountsCode, accountNumber);
    }

    protected AccountDelegate getPrimaryDelegate(
            String chartOfAccountsCode, String accountNumber, String fisDocumentTypeCode, String totalDollarAmount){
        return SpringContext.getBean(AccountService.class).getPrimaryDelegationByExample(
            getDelegateExample(chartOfAccountsCode, accountNumber, fisDocumentTypeCode), totalDollarAmount);
    }

    protected List<AccountDelegate> getSecondaryDelegates(
            String chartOfAccountsCode, String accountNumber, String fisDocumentTypeCode, String totalDollarAmount){
        return SpringContext.getBean(AccountService.class).getSecondaryDelegationsByExample(
            getDelegateExample(chartOfAccountsCode, accountNumber, fisDocumentTypeCode), totalDollarAmount);
    }
    
    protected Person getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber){
        return SpringContext.getBean(
                ContractsAndGrantsModuleService.class).getProjectDirectorForAccount(chartOfAccountsCode, accountNumber);
    }

    //Default to 0 total amount
    protected String getDefaultTotalAmount(){
        return "0";
    }
    
    protected AccountDelegate getDelegateExample(String chartOfAccountsCode, String accountNumber, String fisDocumentTypeCode){
        AccountDelegate delegateExample = new AccountDelegate();
        delegateExample.setChartOfAccountsCode(chartOfAccountsCode);
        delegateExample.setAccountNumber(accountNumber);
        delegateExample.setFinancialDocumentTypeCode(fisDocumentTypeCode);
        return delegateExample;
    }
    
    /**
     * Gets the accountService attribute. 
     * @return Returns the accountService.
     */
    public AccountService getAccountService() {
        return accountService;
    }

    /**
     * Sets the accountService attribute value.
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    public boolean doesDelegationQualifierMatchQualification(AttributeSet qualification, AttributeSet delegationQualifier){
        return performMatch(translateInputAttributeSet(qualification), delegationQualifier);
    }
    
    public List<KimDelegationMemberInfo> doDelegationQualifiersMatchQualification(
            AttributeSet qualification, List<KimDelegationMemberInfo> delegationMemberList){
        AttributeSet translatedQualification = translateInputAttributeSet(qualification);
        List<KimDelegationMemberInfo> matchingMemberships = new ArrayList<KimDelegationMemberInfo>();
        for ( KimDelegationMemberInfo dmi : delegationMemberList ) {
            if ( performMatch( translatedQualification, dmi.getQualifier() ) ) {
                matchingMemberships.add( dmi );
            }
        }
        return matchingMemberships;
    }

    public AttributeSet convertQualificationAttributesToRequired(AttributeSet qualificationAttributes){
        return qualificationAttributes;
    }

}