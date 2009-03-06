/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.identity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.purap.identity.PurapKimAttributes;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.util.KIMPropertyConstants;

/**
 * Test of various KFS application roles.
 */
@ConfigureContext
public class KFSApplicationRoleTest extends KualiTestBase {
    
    public static final String STRAIGHT_COMMODITY_USER = "season";
    public static final String WILDCARD_COMMODITY_USER = "cdbookma";
    public static final String BAD_WILDCARD_COMMODITY_USER = "fwillhit";
    public static final String COMMODITY_CAMPUS = "BL";
    public static final String COMMODITY_CODE = "1113";
    public static final String PURAP_NAMESPACE = "KFS-PURAP";
    public static final String COMMODITY_REVIEWER_ROLE_NAME = "Commodity Reviewer";
    
    public void testCommodityReviewRoleTypeService() {
        AttributeSet roleQualifiers = new AttributeSet();
        roleQualifiers.put(KfsKimAttributes.CAMPUS_CODE, COMMODITY_CAMPUS);
        roleQualifiers.put(KfsKimAttributes.PURCHASING_COMMODITY_CODE, COMMODITY_CODE);
        
        assertUserIsRoleMember(getPrincipalIdByName(STRAIGHT_COMMODITY_USER), PURAP_NAMESPACE, COMMODITY_REVIEWER_ROLE_NAME, roleQualifiers);
        assertUserIsRoleMember(getPrincipalIdByName(WILDCARD_COMMODITY_USER), PURAP_NAMESPACE, COMMODITY_REVIEWER_ROLE_NAME, roleQualifiers);
        assertUserIsNotRoleMember(getPrincipalIdByName(BAD_WILDCARD_COMMODITY_USER), PURAP_NAMESPACE, COMMODITY_REVIEWER_ROLE_NAME, roleQualifiers);
    }
    
    public static final String KFS_PRINCIPAL_NAME = "khuntley";
    public static final String NON_KFS_PRINCIPAL_NAME = "bcoffee";
    public static final String FINANCIAL_SYSTEM_USER_ROLE_NAME = "User";
    
    public void testFinancialSystemUserRoleTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(KFS_PRINCIPAL_NAME), KFSConstants.ParameterNamespaces.KFS, FINANCIAL_SYSTEM_USER_ROLE_NAME, new AttributeSet());
        assertUserIsNotRoleMember(getPrincipalIdByName(NON_KFS_PRINCIPAL_NAME), KFSConstants.ParameterNamespaces.KFS, FINANCIAL_SYSTEM_USER_ROLE_NAME, new AttributeSet());
    }
    
    public static final String ACCOUNT_DERIVED_CHART = "BL";
    public static final String ACCOUNT_DERIVED_ACCOUNT = "1031400";
    
    public static final String ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE = "AV";
    public static final String ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART = "BA";
    public static final String ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER = "9012025";
    public static final String ACCOUNT_DERIVED_DELEGATE_SECONDARY_DOC_TYPE = "DI";
    public static final String ACCOUNT_DERIVED_DELEGATE_SECONDARY_CHART = "BA";
    public static final String ACCOUNT_DERIVED_DELEGATE_SECONDARY_ACCOUNT_NUMBER = "9019995";
    public static final String ACCOUNT_DERIVED_DELEGATE_PRINCIPAL = "rmunroe";
    
    public static final String ACCOUNT_DERIVED_AWARD_CHART = "BL";
    public static final String ACCOUNT_DERIVED_AWARD_ACCOUNT = "4831498";
    public static final String ACCOUNT_DERIVED_AWARD_PROJECT_DIRECTOR = "ahlers";
    
    
    public void testAccountDerivedRoleTypeService() {
        AccountService accountService = SpringContext.getBean(AccountService.class);
        
        Account account = accountService.getByPrimaryId(ACCOUNT_DERIVED_CHART , ACCOUNT_DERIVED_ACCOUNT);
        AttributeSet roleQualifications = new AttributeSet();
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, account.getAccountNumber());
        roleQualifications.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, "10.0");
        
        // 1. test fiscal officer
        assertUserIsSingleMemberInRole(account.getAccountFiscalOfficerSystemIdentifier(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME, roleQualifications);
        
        // 2. test account supervisor
        assertUserIsSingleMemberInRole(account.getAccountsSupervisorySystemsIdentifier(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.ACCOUNT_SUPERVISOR_KIM_ROLE_NAME, roleQualifications);
        
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER);
        final String delegatePrincipalId = getPrincipalIdByName(ACCOUNT_DERIVED_DELEGATE_PRINCIPAL);
        // 3. test primary delegate
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER);
        roleQualifications.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE);
        assertUserIsRoleMember(delegatePrincipalId, KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME, roleQualifications);
        
        // 4. test secondary delegate
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_SECONDARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_SECONDARY_ACCOUNT_NUMBER);
        roleQualifications.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_SECONDARY_DOC_TYPE);
        assertUserIsRoleMember(delegatePrincipalId, KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME, roleQualifications);
        
        // does the FO pick up the delegates?
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER);
        roleQualifications.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE);
        Collection<RoleMembershipInfo> roleMembers = getRoleMembers(KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME, roleQualifications);
        RoleMembershipInfo fo = roleMembers.iterator().next();
        roleMembers.iterator().hasNext();
        List<String> delegateIds = new ArrayList<String>();
        for (DelegateInfo delegate : fo.getDelegates()) {
            delegateIds.add(delegate.getMemberId());
        }
        assertTrue("Fiscal Officer delegates does not contain primary delegate", delegateIds.contains(delegatePrincipalId));
        
        // 5. test award secondary director
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_AWARD_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_AWARD_ACCOUNT);
        assertUserIsRoleMember(getPrincipalIdByName(ACCOUNT_DERIVED_AWARD_PROJECT_DIRECTOR), KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.AWARD_SECONDARY_DIRECTOR_KIM_ROLE_NAME, roleQualifications);
    }
    
    private final static String BILLER_PRINCIPAL_NAME = "keasterl"; // in UA-VPIT
    private final static String PROCESSOR_PRINCIPAL_NAME = "keasterl"; // in UA-VPIT
    private final static String BILLER_ROLE_NAME = "Biller";
    private final static String PROCESSOR_ROLE_NAME = "Processor";
    private final static String ACCOUNTS_RECEIVABLE_NAMESPACE = "KFS-AR";
    
    public void testAccountsReceivableOrganizationRoleTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(BILLER_PRINCIPAL_NAME), ACCOUNTS_RECEIVABLE_NAMESPACE, BILLER_ROLE_NAME, new AttributeSet());
        assertUserIsRoleMember(getPrincipalIdByName(PROCESSOR_PRINCIPAL_NAME), ACCOUNTS_RECEIVABLE_NAMESPACE, PROCESSOR_ROLE_NAME, new AttributeSet());
    }
    
    public static final String SENSITIVE_DATA_1 = "ANIM";
    public static final String SENSITIVE_DATA_2 = "RADI";
    public static final String SENSITIVE_DATA_3 = "ANIM;RADI";
    public static final String SENSITIVE_DATA_REVIEWER = "bhhallow";
    public static final String SENSITIVE_DATA_ROLE_NAME = "Sensitive Data Viewer";
    
    public AttributeSet buildRoleQualificationForSensitiveData(String sensitiveDataCode) {
        AttributeSet roleQualification = new AttributeSet();
        roleQualification.put(PurapKimAttributes.SENSITIVE_DATA_CODE, sensitiveDataCode);
        return roleQualification;
    }
    
    public void testSensitiveDataRoleTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(SENSITIVE_DATA_REVIEWER), PURAP_NAMESPACE, SENSITIVE_DATA_ROLE_NAME, buildRoleQualificationForSensitiveData(SENSITIVE_DATA_1));
        assertUserIsNotRoleMember(getPrincipalIdByName(SENSITIVE_DATA_REVIEWER), PURAP_NAMESPACE, SENSITIVE_DATA_ROLE_NAME, buildRoleQualificationForSensitiveData(SENSITIVE_DATA_2));
        assertUserIsRoleMember(getPrincipalIdByName(SENSITIVE_DATA_REVIEWER), PURAP_NAMESPACE, SENSITIVE_DATA_ROLE_NAME, buildRoleQualificationForSensitiveData(SENSITIVE_DATA_3));
    }
    
    public static final String ORG_HIERARCHY_ORG_1_CHART = "BL";
    public static final String ORG_HIERARCHY_ORG_1_ORG = "BL";
    public static final String ORG_HIERARCHY_ORG_2_CHART = "BL";
    public static final String ORG_HIERARCHY_ORG_2_ORG = "ANTH";
    public static final String ORG_HIERARCHY_ORG_3_CHART = "KO";
    public static final String ORG_HIERARCHY_ORG_3_ORG = "CADM";
    public static final String ORG_HIERARCHY_REVIEWER = "cknotts";
    public static final String ORG_HIERARCHY_ROLE_NAME = "Organization Reviewer";
    public static final String GOOD_DOCUMENT_TYPE_NAME = "ACCT";
    
    public AttributeSet buildOrganizationHierarchyRoleQualifiers(String chartCode, String organizationCode, String docTypeName) {
        AttributeSet roleQualifiers = new AttributeSet();
        roleQualifiers.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartCode);
        roleQualifiers.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);
        roleQualifiers.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, docTypeName);
        return roleQualifiers;
    }
    
    public void testOrganizationHierarchyTypeServce() {
        assertUserIsRoleMember(getPrincipalIdByName(ORG_HIERARCHY_REVIEWER), KFSConstants.ParameterNamespaces.KFS, ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(ORG_HIERARCHY_ORG_1_CHART, ORG_HIERARCHY_ORG_1_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsRoleMember(getPrincipalIdByName(ORG_HIERARCHY_REVIEWER), KFSConstants.ParameterNamespaces.KFS, ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(ORG_HIERARCHY_ORG_2_CHART, ORG_HIERARCHY_ORG_2_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsNotRoleMember(getPrincipalIdByName(ORG_HIERARCHY_REVIEWER), KFSConstants.ParameterNamespaces.KFS, ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(ORG_HIERARCHY_ORG_3_CHART, ORG_HIERARCHY_ORG_3_ORG, GOOD_DOCUMENT_TYPE_NAME));
    }
    
    public static final String OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE = "KFS-CAM";
    public static final String OPTIONAL_ORG_HIERARCHY_ROLE_NAME = "Processor";
    public static final String OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_CHART = "BL";
    public static final String OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_ORG = "ARSC";
    public static final String OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_CHART = "BL";
    public static final String OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_ORG = "CHEM";
    public static final String OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_MEMBER = "agetz";
    public static final String OPTIONAL_ORG_HIERARCHY_DESCENDING_HIGH_LEVEL_MEMBER = "avogel";
    public static final String OPTIONAL_ORG_HIERARCHY_NON_DESCENDING_HIGH_LEVEL_MEMBER = "ayoung";
    
    public void testOrganizationHierarchyOptionalTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_DESCENDING_HIGH_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsNotRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_NON_DESCENDING_HIGH_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        
        assertUserIsNotRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_DESCENDING_HIGH_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_NON_DESCENDING_HIGH_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
    }
    
    public static final String ACCOUNTING_ORG_HIERARCHY_ROLE_NAME = "Accounting Reviewer";
    public static final String ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_CHART = "BL";
    public static final String ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_ORG = "PSY";
    public static final String ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_CHART = "BL";
    public static final String ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_ORG = "HMNF";
    public static final String ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_CHART = "BL";
    public static final String ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_ORG = "ARSC";
    public static final String ACCOUNTING_ORG_HIERARCHY_MEMBER = "jrichard";
    public static final String ACCOUNTING_ORG_HIERARCHY_DOC_TYPE = "REQS";
    public static final String ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT = "25000.03";
    public static final String ACCOUNTING_ORG_HIERARCHY_NOT_QUITE_ENOUGH_AMOUNT = "3";
    
    public AttributeSet buildAccountingOrganizationHierarchyReviewRoleQualifiers(String chartCode, String organizationCode, String documentTypeName, String amount) {
        AttributeSet roleQualifiers = buildOrganizationHierarchyRoleQualifiers(chartCode, organizationCode, documentTypeName);
        roleQualifiers.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, amount);
        roleQualifiers.put(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, "");
        return roleQualifiers;
    }
    
    public void testAccountOrganizationHierarchyTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(ACCOUNTING_ORG_HIERARCHY_MEMBER), KFSConstants.ParameterNamespaces.KFS, ACCOUNTING_ORG_HIERARCHY_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_CHART, ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT));
        assertUserIsRoleMember(getPrincipalIdByName(ACCOUNTING_ORG_HIERARCHY_MEMBER), KFSConstants.ParameterNamespaces.KFS, ACCOUNTING_ORG_HIERARCHY_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_CHART, ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT));
        assertUserIsNotRoleMember(getPrincipalIdByName(ACCOUNTING_ORG_HIERARCHY_MEMBER), KFSConstants.ParameterNamespaces.KFS, ACCOUNTING_ORG_HIERARCHY_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_CHART, ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT));
        assertUserIsNotRoleMember(getPrincipalIdByName(ACCOUNTING_ORG_HIERARCHY_MEMBER), KFSConstants.ParameterNamespaces.KFS, ACCOUNTING_ORG_HIERARCHY_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_CHART, ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_NOT_QUITE_ENOUGH_AMOUNT));
    }
    
    private void assertUserIsRoleMember(String principalId, String roleNamespace, String roleName, AttributeSet roleQualifications) {
        final Collection<RoleMembershipInfo> roleMembers = getRoleMembers(roleNamespace, roleName, roleQualifications);
        
        int memberCount = 0;
        for (RoleMembershipInfo roleMember : roleMembers) {
            if (roleMember.getMemberTypeCode().equals("P") && roleMember.getMemberId().equals(principalId)) {
                memberCount += 1;
            }
        }
        assertTrue("Principal "+SpringContext.getBean(PersonService.class).getPerson(principalId).getName()+" not found in role: "+roleNamespace+" "+roleName, memberCount > 0);
    }
    
    private void assertUserIsNotRoleMember(String principalId, String roleNamespace, String roleName, AttributeSet roleQualifications) {
        final Collection<RoleMembershipInfo> roleMembers = getRoleMembers(roleNamespace, roleName, roleQualifications);
        
        int memberCount = 0;
        for (RoleMembershipInfo roleMember : roleMembers) {
            if (roleMember.getMemberTypeCode().equals("P") && roleMember.getMemberId().equals(principalId)) {
                memberCount += 1;
            }
        }
        assertTrue("Principal "+SpringContext.getBean(PersonService.class).getPerson(principalId).getName()+" found in role: "+roleNamespace+" "+roleName, memberCount == 0);
    }
    
    private void assertUserIsSingleMemberInRole(String principalId, String roleNamespace, String roleName, AttributeSet roleQualifications) {
        final Collection<RoleMembershipInfo> roleMembers = getRoleMembers(roleNamespace, roleName, roleQualifications);
        
        assertTrue("Only one role member returned", roleMembers.size() == 1);
        
        final RoleMembershipInfo roleMember = roleMembers.iterator().next();
        roleMembers.iterator().hasNext(); // wind the iterator out, just in case
        assertTrue("Role member "+roleMember.getMemberId()+" does not match expected principal id: "+principalId, (roleMember.getMemberTypeCode().equals("P") && roleMember.getMemberId().equals(principalId)));
    }
    
    private Collection<RoleMembershipInfo> getRoleMembers(String roleNamespace, String roleName, AttributeSet roleQualifications) {
        final RoleManagementService roleManagementService = SpringContext.getBean(RoleManagementService.class);
        final KimRoleInfo roleInfo = roleManagementService.getRoleByName(roleNamespace, roleName);
        return roleManagementService.getRoleMembers(Arrays.asList(new String[] { roleInfo.getRoleId() }), roleQualifications);
    }
    
    private String getPrincipalIdByName(String principalName) {
        return SpringContext.getBean(PersonService.class).getPersonByPrincipalName(principalName).getPrincipalId();
    }
    
}
