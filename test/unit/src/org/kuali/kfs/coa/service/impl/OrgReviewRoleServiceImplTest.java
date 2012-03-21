/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.krad.exception.ValidationException;

@ConfigureContext
public class OrgReviewRoleServiceImplTest extends KualiTestBase {

    OrgReviewRoleServiceImpl orgReviewRoleService;
    RoleService roleService;
    ResponsibilityService responsibilityService;
    KimTypeInfoService kimTypeInfoService;
    PersonService personService;
    Role orgHierRole;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        orgReviewRoleService = (OrgReviewRoleServiceImpl) SpringContext.getBean(OrgReviewRoleService.class);
        roleService = KimApiServiceLocator.getRoleService();
        responsibilityService = KimApiServiceLocator.getResponsibilityService();
        kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
        personService = KimApiServiceLocator.getPersonService();
        orgHierRole = roleService.getRoleByNamespaceCodeAndName(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
    }

    protected OrgReviewRole buildOrgHierData() {
        OrgReviewRole orr = new OrgReviewRole();

        orr.setRoleId( orgHierRole.getId() );
        orr.setKimTypeId( orgHierRole.getKimTypeId() );
        orr.setRoleName(orgHierRole.getName());
        orr.setNamespaceCode(orgHierRole.getNamespaceCode());

        orr.setChartOfAccountsCode("BL");
        orr.setOrganizationCode("PSY");
        //orr.setOverrideCode("");
        //orr.setFromAmount(KualiDecimal.ZERO);
        //orr.setToAmount(new KualiDecimal("5000.00"));
        orr.setFinancialSystemDocumentTypeCode("ACCT");

        orr.setActionTypeCode(ActionRequestType.APPROVE.getCode());
        orr.setPriorityNumber("");
        orr.setActionPolicyCode(ActionRequestPolicy.FIRST.getCode());
        orr.setForceAction(false);

        orr.setMemberTypeCode(MemberType.PRINCIPAL.getCode());
        Person p = UserNameFixture.khuntley.getPerson();
        orr.setPrincipalMemberPrincipalName(p.getPrincipalName());
        orr.setPrincipalMemberPrincipalId(p.getPrincipalId());

        //orr.setActiveFromDate(null);
        //orr.setActiveToDate(null);
        return orr;
    }

    protected OrgReviewRole buildAcctOrgHierData() {
        OrgReviewRole orr = new OrgReviewRole();

        orr.setRoleId( orgHierRole.getId() );
        orr.setKimTypeId( orgHierRole.getKimTypeId() );
        orr.setRoleName(orgHierRole.getName());
        orr.setNamespaceCode(orgHierRole.getNamespaceCode());

        orr.setChartOfAccountsCode("BL");
        orr.setOrganizationCode("PSY");
        orr.setOverrideCode("");
        orr.setFromAmount(KualiDecimal.ZERO);
        orr.setToAmount(new KualiDecimal("5000.00"));
        orr.setFinancialSystemDocumentTypeCode("DI");

        orr.setActionTypeCode(ActionRequestType.APPROVE.getCode());
        orr.setPriorityNumber("1");
        orr.setActionPolicyCode(ActionRequestPolicy.FIRST.getCode());
        orr.setForceAction(false);

        orr.setMemberTypeCode(MemberType.PRINCIPAL.getCode());
        Person p = UserNameFixture.khuntley.getPerson();
        orr.setPrincipalMemberPrincipalName(p.getPrincipalName());
        orr.setPrincipalMemberPrincipalId(p.getPrincipalId());

        //orr.setActiveFromDate(null);
        //orr.setActiveToDate(null);
        return orr;
    }

    protected int getNumberOfResponsibilitiesWithRoleMemberLevelActions( Role role ) {
        List<RoleResponsibility> roleResponsibilities = roleService.getRoleResponsibilities(role.getId());
        assertNotNull( "There should have been responsibilities assigned to the role (was null)", roleResponsibilities );
        assertFalse( "There should have been responsibilities assigned to the role", roleResponsibilities.isEmpty() );
        int num = 0;
        for ( RoleResponsibility rr : roleResponsibilities ) {
            Responsibility r = responsibilityService.getResponsibility(rr.getResponsibilityId());
            if ( Boolean.parseBoolean( r.getAttributes().get(KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL) ) ) {
                num++;
            }
        }
        return num;
    }

    public void testSaveOrgReviewRoleToKim() throws Exception {
        OrgReviewRole orr = buildOrgHierData();

        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
        assertFalse( "OrgReviewRole roleMemberId should not have been blank.", orr.getRoleMemberId().equals("") );

        // now, look in KIM for the role
        RoleMemberQueryResults roleMembers = roleService.findRoleMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ID, orr.getRoleMemberId()))));
        assertNotNull( "Returned role members object should not have been null", roleMembers );
        assertEquals( "One result should have been returned", 1, roleMembers.getResults().size() );
        RoleMember rm = roleMembers.getResults().get(0);
        System.err.println( "RoleMember: " + rm );
        assertEquals( "RoleMember id should be that requested", orr.getRoleMemberId(), rm.getId() );
        assertEquals( "RoleMember's ID should match the inputs", orr.getPrincipalMemberPrincipalId(), rm.getMemberId() );
        assertEquals( "Role should be org hierarchy", orgHierRole.getId(), rm.getRoleId() );

        // and validate the data
        assertNotNull( "The role member attributes should not have been null", rm.getAttributes() );
        Map<String,String> attr = rm.getAttributes();
        assertEquals( "The number of attributes does not match", 3, attr.size() );
        assertEquals("Chart attrib is incorrect", "BL", attr.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
        assertEquals("Org attrib is incorrect", "PSY", attr.get(KfsKimAttributes.ORGANIZATION_CODE));
        assertEquals("Doc Type attrib is incorrect", "ACCT", attr.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));

        List<RoleResponsibilityAction> respActions = rm.getRoleRspActions();
        assertNotNull( "Role resp actions should not be null", respActions );
        assertFalse( "Role resp actions should not be empty", respActions.isEmpty() );
        RoleResponsibilityAction rra = respActions.get(0);
        assertEquals( "The responsibility should be for an approve.", ActionRequestType.APPROVE.getCode(), rra.getActionTypeCode() );
        assertEquals( "The responsibility policy should be first approve.", ActionRequestPolicy.FIRST.getCode(), rra.getActionPolicyCode() );
        assertEquals( "The role resp ID is not correct", "*", rra.getRoleResponsibilityId() );
//        assertEquals( "The number of role resp action records should be equal to the number of attached responsibilities with actionDetailsAtRoleMemberLevel=true", getNumberOfResponsibilitiesWithRoleMemberLevelActions(orgHierRole), respActions.size() );
        assertEquals( "The number of role resp action records should be 1", 1, respActions.size() );
    }

    // TODO: on save of delegates, ensure that other (existing) delegates are not removed

    public void testPopulateOrgReviewRoleFromRoleMember() {
        fail("Not yet implemented");
    }

    public void testPopulateOrgReviewRoleFromDelegationMember() {
        fail("Not yet implemented");
    }

    public void testPopulateObjectExtras() {
        fail("Not yet implemented");
    }

    public void testIsValidDocumentTypeForOrgReview() {
        assertTrue( "DI should have been valid", orgReviewRoleService.isValidDocumentTypeForOrgReview("DI") );
        assertTrue( "ACCT should have been valid", orgReviewRoleService.isValidDocumentTypeForOrgReview("ACCT") );
        assertFalse( "COAT should not have been valid", orgReviewRoleService.isValidDocumentTypeForOrgReview("COAT") );
    }

    public void testValidateDocumentType() {
        try {
            orgReviewRoleService.validateDocumentType("COAT");
            fail( "An exception should have been thrown for document type COAT" );
        } catch ( ValidationException ex ) {
            System.err.println( ex.getMessage() );
        }
    }

    public void testHasOrganizationHierarchy() {
        assertTrue( "ACCT should have org hierarchy", orgReviewRoleService.hasOrganizationHierarchy("ACCT"));
        assertFalse( "DI should not have org hierarchy", orgReviewRoleService.hasOrganizationHierarchy("DI"));
        assertFalse( "COAT should not have org hierarchy", orgReviewRoleService.hasOrganizationHierarchy("COAT"));
    }

    public void testHasAccountingOrganizationHierarchy() {
        assertFalse( "ACCT should not have accounting org hierarchy", orgReviewRoleService.hasAccountingOrganizationHierarchy("ACCT"));
        assertFalse( "DI should not have accounting org hierarchy", orgReviewRoleService.hasAccountingOrganizationHierarchy("DI"));
        assertFalse( "COAT should not have accounting org hierarchy", orgReviewRoleService.hasAccountingOrganizationHierarchy("COAT"));
    }

    public void testGetClosestOrgReviewRoleParentDocumentTypeName() {
        fail("Not yet implemented");
    }

    public void testGetRolesToConsider() {
        fail("Not yet implemented");
    }

//    public void testCurrentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles() {
//        assertTrue( "COAT should have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("COAT"));
//        assertFalse( "KFS should not have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("KFS"));
//        assertFalse( "KFST should not have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("KFST"));
//        assertFalse( "DI should not have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("DI"));
////        assertTrue( "FSSM should have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("FSSM"));
//    }

}
