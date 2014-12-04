/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.role.RoleMembership;

/**
 * Test of various KFS application roles.
 */
@ConfigureContext
public class KFSApplicationRoleTest extends RoleTestBase {

    public static final UserNameFixture KFS_USER = UserNameFixture.khuntley;
    public static final UserNameFixture NON_KFS_USER = UserNameFixture.bcoffee;

    public void testFinancialSystemUserRoleTypeService() {
        assertUserIsRoleMember(KFS_USER.getPerson().getPrincipalId(), KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, new HashMap<String,String>());
        assertUserIsNotRoleMember(NON_KFS_USER.getPerson().getPrincipalId(), KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, new HashMap<String,String>());
    }

    public static final String ACCOUNT_DERIVED_CHART = "BL";
    public static final String ACCOUNT_DERIVED_ACCOUNT = "1031400";

    public static final String ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE = "AV";
    public static final String ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART = "BA";
    public static final String ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER = "9012025";
    public static final String ACCOUNT_DERIVED_DELEGATE_SECONDARY_DOC_TYPE = "DI";
    public static final String ACCOUNT_DERIVED_DELEGATE_SECONDARY_CHART = "BA";
    public static final String ACCOUNT_DERIVED_DELEGATE_SECONDARY_ACCOUNT_NUMBER = "9019995";
    public static final UserNameFixture ACCOUNT_DERIVED_DELEGATE_PRINCIPAL = UserNameFixture.rmunroe;



    public void testAccountDerivedRoleTypeService() {
        AccountService accountService = SpringContext.getBean(AccountService.class);

        Account account = accountService.getByPrimaryId(ACCOUNT_DERIVED_CHART , ACCOUNT_DERIVED_ACCOUNT);
        Map<String,String> roleQualifications = new HashMap<String,String>();
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, account.getAccountNumber());
        roleQualifications.put(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, "10.00");
        roleQualifications.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE);

        // 1. test fiscal officer
        assertUserIsSingleMemberInRole(account.getAccountFiscalOfficerSystemIdentifier(), KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.FISCAL_OFFICER_KIM_ROLE_NAME, roleQualifications);

        // 2. test account supervisor
        assertUserIsSingleMemberInRole(account.getAccountsSupervisorySystemsIdentifier(), KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.ACCOUNT_SUPERVISOR_KIM_ROLE_NAME, roleQualifications);

        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER);
        String delegatePrincipalId = ACCOUNT_DERIVED_DELEGATE_PRINCIPAL.getPerson().getPrincipalId();
        // 3. test primary delegate
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER);
        roleQualifications.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE);
        assertUserIsRoleMember(delegatePrincipalId, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME, roleQualifications);

        // 4. test secondary delegate
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_SECONDARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_SECONDARY_ACCOUNT_NUMBER);
        roleQualifications.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_SECONDARY_DOC_TYPE);
        assertUserIsRoleMember(delegatePrincipalId, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME, roleQualifications);

        // does the FO pick up the delegates?
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER);
        roleQualifications.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE);
        Collection<RoleMembership> roleMembers = getRoleMembers(KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.FISCAL_OFFICER_KIM_ROLE_NAME, roleQualifications);
        RoleMembership fo = roleMembers.iterator().next();
        roleMembers.iterator().hasNext();
        List<String> delegateIds = new ArrayList<String>();
        for (DelegateType delegateType : fo.getDelegates()) {
            for (DelegateMember delegateMember : delegateType.getMembers()) {
                delegateIds.add(delegateMember.getMemberId());
            }
        }
        assertTrue("Fiscal Officer delegates does not contain primary delegate", delegateIds.contains(delegatePrincipalId));
    }

    public static final String ORG_HIERARCHY_ORG_1_CHART = "BL";
    public static final String ORG_HIERARCHY_ORG_1_ORG = "BL";
    public static final String ORG_HIERARCHY_ORG_2_CHART = "BL";
    public static final String ORG_HIERARCHY_ORG_2_ORG = "ANTH";
    public static final String ORG_HIERARCHY_ORG_3_CHART = "KO";
    public static final String ORG_HIERARCHY_ORG_3_ORG = "CADM";
    public static final UserNameFixture ORG_HIERARCHY_REVIEWER = UserNameFixture.cknotts;
    public static final String ORG_HIERARCHY_ROLE_NAME = KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME;
    public static final String GOOD_DOCUMENT_TYPE_NAME = "ACCT";

    public Map<String,String> buildOrganizationHierarchyRoleQualifiers(String chartCode, String organizationCode, String docTypeName) {
        Map<String,String> roleQualifiers = new HashMap<String,String>();
        roleQualifiers.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartCode);
        roleQualifiers.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);
        roleQualifiers.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, docTypeName);
        return roleQualifiers;
    }

    public void testOrganizationHierarchyTypeServce() {
        assertUserIsRoleMember(ORG_HIERARCHY_REVIEWER.getPerson().getPrincipalId(), KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(ORG_HIERARCHY_ORG_1_CHART, ORG_HIERARCHY_ORG_1_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsRoleMember(ORG_HIERARCHY_REVIEWER.getPerson().getPrincipalId(), KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(ORG_HIERARCHY_ORG_2_CHART, ORG_HIERARCHY_ORG_2_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsNotRoleMember(ORG_HIERARCHY_REVIEWER.getPerson().getPrincipalId(), KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(ORG_HIERARCHY_ORG_3_CHART, ORG_HIERARCHY_ORG_3_ORG, GOOD_DOCUMENT_TYPE_NAME));
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

    public static final String ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_CHART = "BL";
    public static final String ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_ORG = "PSY";
    public static final String ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_CHART = "BL";
    public static final String ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_ORG = "HMNF";
    public static final String ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_CHART = "BL";
    public static final String ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_ORG = "ARSC";
    public static final UserNameFixture ACCOUNTING_ORG_HIERARCHY_MEMBER = UserNameFixture.jrichard;
    public static final String ACCOUNTING_ORG_HIERARCHY_DOC_TYPE = "REQS";
    public static final String ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT = "25000.03";
    public static final String ACCOUNTING_ORG_HIERARCHY_NOT_QUITE_ENOUGH_AMOUNT = "3.00";

    public Map<String,String> buildAccountingOrganizationHierarchyReviewRoleQualifiers(String chartCode, String organizationCode, String documentTypeName, String amount) {
        Map<String,String> roleQualifiers = buildOrganizationHierarchyRoleQualifiers(chartCode, organizationCode, documentTypeName);
        roleQualifiers.put(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, amount);
        roleQualifiers.put(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, "");
        return roleQualifiers;
    }

    public void testAccountOrganizationHierarchyTypeService() {
        assertUserIsRoleMember(ACCOUNTING_ORG_HIERARCHY_MEMBER.getPerson().getPrincipalId(), KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_CHART, ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT));
        assertUserIsRoleMember(ACCOUNTING_ORG_HIERARCHY_MEMBER.getPerson().getPrincipalId(), KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_CHART, ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT));
        assertUserIsNotRoleMember(ACCOUNTING_ORG_HIERARCHY_MEMBER.getPerson().getPrincipalId(), KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_CHART, ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT));
        assertUserIsNotRoleMember(ACCOUNTING_ORG_HIERARCHY_MEMBER.getPerson().getPrincipalId(), KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_CHART, ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_NOT_QUITE_ENOUGH_AMOUNT));
    }

}
