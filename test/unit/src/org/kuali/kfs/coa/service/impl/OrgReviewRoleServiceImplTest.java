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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.identity.KfsKimDocDelegateMember;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
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

    protected OrgReviewRoleServiceImpl orgReviewRoleService;
    protected RoleService roleService;
    protected ResponsibilityService responsibilityService;
    protected KimTypeInfoService kimTypeInfoService;
    protected PersonService personService;
    protected Role orgHierRole;
    protected Role acctHierRole;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        orgReviewRoleService =  (OrgReviewRoleServiceImpl) TestUtils.getUnproxiedService( "orgReviewRoleService" );
        roleService = KimApiServiceLocator.getRoleService();
        responsibilityService = KimApiServiceLocator.getResponsibilityService();
        kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
        personService = KimApiServiceLocator.getPersonService();
        orgHierRole = roleService.getRoleByNamespaceCodeAndName(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
        acctHierRole = roleService.getRoleByNamespaceCodeAndName(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
        Logger.getLogger(OrgReviewRoleServiceImpl.class).setLevel(Level.DEBUG);
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

        orr.setRoleId( acctHierRole.getId() );
        orr.setKimTypeId( acctHierRole.getKimTypeId() );
        orr.setRoleName(acctHierRole.getName());
        orr.setNamespaceCode(acctHierRole.getNamespaceCode());

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

    protected OrgReviewRole buildOrgHierDelegateTypeData( DelegationType delegationType ) {
        OrgReviewRole orr = new OrgReviewRole();
        orr.setDelegate(true);
        orr.setRoleId( orgHierRole.getId() );
        orr.setKimTypeId( orgHierRole.getKimTypeId() );
        orr.setRoleName(orgHierRole.getName());
        orr.setNamespaceCode(orgHierRole.getNamespaceCode());
        orr.setDelegationTypeCode(delegationType.getCode());

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

        orr.setDelegationMemberRole( new KfsKimDocDelegateMember( orr.getRoleId(), MemberType.ROLE ) );
        orr.setDelegationMemberGroup( new KfsKimDocDelegateMember( orr.getRoleId(), MemberType.GROUP ) );
        orr.setDelegationMemberPerson( new KfsKimDocDelegateMember( orr.getRoleId(), MemberType.PRINCIPAL ) );

        orr.setMemberTypeCode(MemberType.PRINCIPAL.getCode());
        Person p = UserNameFixture.rorenfro.getPerson();
        orr.setPrincipalMemberPrincipalName(p.getPrincipalName());
        orr.setPrincipalMemberPrincipalId(p.getPrincipalId());

        //orr.setActiveFromDate(null);
        //orr.setActiveToDate(null);
        return orr;
    }

    protected OrgReviewRole buildAcctHierDelegateTypeData( DelegationType delegationType ) {
        OrgReviewRole orr = new OrgReviewRole();
        orr.setDelegate(true);
        orr.setRoleId( orgHierRole.getId() );
        orr.setKimTypeId( orgHierRole.getKimTypeId() );
        orr.setRoleName(orgHierRole.getName());
        orr.setNamespaceCode(orgHierRole.getNamespaceCode());
        orr.setDelegationTypeCode(delegationType.getCode());

        orr.setChartOfAccountsCode("BL");
        orr.setOrganizationCode("PSY");
        orr.setOverrideCode("");
        // delegates don't have amounts
//        orr.setFromAmount(KualiDecimal.ZERO);
//        orr.setToAmount(new KualiDecimal("5000.00"));
        orr.setFinancialSystemDocumentTypeCode("DI");

        orr.setActionTypeCode(ActionRequestType.APPROVE.getCode());
        orr.setPriorityNumber("");
        orr.setActionPolicyCode(ActionRequestPolicy.FIRST.getCode());
        orr.setForceAction(false);

        orr.setDelegationMemberRole( new KfsKimDocDelegateMember( orr.getRoleId(), MemberType.ROLE ) );
        orr.setDelegationMemberGroup( new KfsKimDocDelegateMember( orr.getRoleId(), MemberType.GROUP ) );
        orr.setDelegationMemberPerson( new KfsKimDocDelegateMember( orr.getRoleId(), MemberType.PRINCIPAL ) );

        orr.setMemberTypeCode(MemberType.PRINCIPAL.getCode());
        Person p = UserNameFixture.rorenfro.getPerson();
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

    protected void checkForMatchingRoleMember( OrgReviewRole orr, Role role ) {
        RoleMemberQueryResults roleMembers = roleService.findRoleMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ID, orr.getRoleMemberId()))));
        assertNotNull( "Returned role members object should not have been null", roleMembers );
        assertEquals( "One result should have been returned", 1, roleMembers.getResults().size() );
        RoleMember rm = roleMembers.getResults().get(0);
        matchOrgReviewRoleToRoleMember(orr, rm, role);
    }

    protected void checkForMatchingDelegationMember( OrgReviewRole orr, Role role ) {
        DelegateMember dm = roleService.getDelegationMemberById(orr.getDelegationMemberId());
        assertNotNull( "Returned delegation member object should not have been null", dm );
        matchOrgReviewRoleToDelegationMember(orr, dm, role);
    }

    protected void matchOrgReviewRoleToRoleMember( OrgReviewRole orr, RoleMember rm, Role role ) {
        System.err.println( "RoleMember: " + rm );
        System.err.println( "OrgReviewRole: " + orr );

        assertEquals( "RoleMember id should be that requested", orr.getRoleMemberId(), rm.getId() );
        assertEquals( "RoleMember's ID should match the inputs", orr.getPrincipalMemberPrincipalId(), rm.getMemberId() );
        assertEquals( "Role should be " + role.getName(), role.getId(), rm.getRoleId() );

        Map<String,String> attr = rm.getAttributes();
        assertNotNull( "The role member attributes should not have been null", rm.getAttributes() );
        assertEquals("Chart attrib is incorrect", orr.getChartOfAccountsCode(), attr.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
        assertEquals("Org attrib is incorrect", orr.getOrganizationCode(), attr.get(KfsKimAttributes.ORGANIZATION_CODE));
        assertEquals("Doc Type attrib is incorrect", orr.getFinancialSystemDocumentTypeCode(), attr.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
        assertEquals("From amount is incorrect", orr.getFromAmountStr(), attr.get(KfsKimAttributes.FROM_AMOUNT));
        assertEquals("To amount is incorrect", orr.getToAmountStr(), attr.get(KfsKimAttributes.TO_AMOUNT));

        List<RoleResponsibilityAction> respActions = rm.getRoleRspActions();
        assertNotNull( "Role resp actions should not be null", respActions );
        assertFalse( "Role resp actions should not be empty", respActions.isEmpty() );
        assertEquals( "There should only be one RoleRespAction", 1, respActions.size() );

        RoleResponsibilityAction rra = respActions.get(0);
        assertEquals( "The responsibility type does not match", orr.getActionTypeCode(), rra.getActionTypeCode() );
        assertEquals( "The responsibility policy does not match", orr.getActionPolicyCode(), rra.getActionPolicyCode() );
        assertEquals( "The role resp ID is not correct", "*", rra.getRoleResponsibilityId() );
    }

    protected void matchOrgReviewRoleToDelegationMember( OrgReviewRole orr, DelegateMember dm, Role role ) {
        System.err.println( "DelegateMember: " + dm );
        System.err.println( "OrgReviewRole: " + orr );

        assertEquals( "DelegateMember id should be that requested", orr.getDelegationMemberId(), dm.getDelegationMemberId() );
        assertEquals( "DelegateMember's ID should match the inputs", orr.getPrincipalMemberPrincipalId(), dm.getMemberId() );
        assertEquals( "DelegateMember's role member ID should match the inputs", orr.getRoleMemberId(), dm.getRoleMemberId() );

        Map<String,String> attr = dm.getAttributes();
        assertNotNull( "The delegate member's attributes should not have been null", dm.getAttributes() );
        assertEquals("Chart attrib is incorrect", orr.getChartOfAccountsCode(), attr.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
        assertEquals("Org attrib is incorrect", orr.getOrganizationCode(), attr.get(KfsKimAttributes.ORGANIZATION_CODE));
        assertEquals("Doc Type attrib is incorrect", orr.getFinancialSystemDocumentTypeCode(), attr.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
        assertEquals("From amount is incorrect", orr.getFromAmountStr(), attr.get(KfsKimAttributes.FROM_AMOUNT));
        assertEquals("To amount is incorrect", orr.getToAmountStr(), attr.get(KfsKimAttributes.TO_AMOUNT));
    }

    public void testSaveOrgReviewRoleToKim_OrgReview_New() throws Exception {
        OrgReviewRole orr = buildOrgHierData();

        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
        assertFalse( "OrgReviewRole roleMemberId should not have been blank.", orr.getRoleMemberId().equals("") );

        // now, look in KIM for the role
        checkForMatchingRoleMember(orr, orgHierRole);
    }

    public void testSaveOrgReviewRoleToKim_AcctReview_New() throws Exception {
        OrgReviewRole orr = buildAcctOrgHierData();

        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
        assertFalse( "OrgReviewRole roleMemberId should not have been blank.", orr.getRoleMemberId().equals("") );

        // now, look in KIM for the role
        checkForMatchingRoleMember(orr, acctHierRole);
    }

    public void testSaveOrgReviewRoleToKim_OrgReview_Edit() throws Exception {
        System.err.println( "Creating Role Entry to edit");
        OrgReviewRole orr = buildOrgHierData();
        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
        assertFalse( "OrgReviewRole roleMemberId should not have been blank.", orr.getRoleMemberId().equals("") );
        String createdRoleMemberId = orr.getRoleMemberId();

        System.err.println("Checking created role");
        checkForMatchingRoleMember(orr, orgHierRole);

        System.err.println("Creating update to just-created record");
        orr = buildOrgHierData();
        orr.setEdit(true);
        orr.setORMId(createdRoleMemberId);
        orr.setRoleMemberId(createdRoleMemberId);

        orr.setActionTypeCode(ActionRequestType.ACKNOWLEDGE.getCode());
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertEquals( "Role member ID should not have changed: ", createdRoleMemberId, orr.getRoleMemberId() );
        System.err.println("Retrieving role member record");
        checkForMatchingRoleMember(orr, orgHierRole);
    }

    public void testSaveOrgReviewRoleToKim_AcctReview_Edit() throws Exception {
        System.err.println( "Creating Role Entry to edit");
        OrgReviewRole orr = buildAcctOrgHierData();
        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
        assertFalse( "OrgReviewRole roleMemberId should not have been blank.", orr.getRoleMemberId().equals("") );
        String createdRoleMemberId = orr.getRoleMemberId();

        System.err.println("Checking created role");
        checkForMatchingRoleMember(orr, acctHierRole);

        System.err.println("Creating update to just-created record");
        orr = buildAcctOrgHierData();
        orr.setEdit(true);
        orr.setORMId(createdRoleMemberId);
        orr.setRoleMemberId(createdRoleMemberId);

        orr.setActionTypeCode(ActionRequestType.ACKNOWLEDGE.getCode());
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertEquals( "Role member ID should not have changed: ", createdRoleMemberId, orr.getRoleMemberId() );
        System.err.println("Retrieving role member record");
        checkForMatchingRoleMember(orr, acctHierRole);
    }

    public void testSaveOrgReviewRoleToKim_OrgReview_Delegate_New() throws Exception {
        // FIRST - create a known role member
        OrgReviewRole orr = buildOrgHierData();

        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
        assertFalse( "OrgReviewRole roleMemberId should not have been blank.", orr.getRoleMemberId().equals("") );

        // now, look in KIM for the role
        checkForMatchingRoleMember(orr, orgHierRole);
        String createdRoleMemberId = orr.getRoleMemberId();

        orr = buildOrgHierDelegateTypeData( DelegationType.PRIMARY );
        orr.setEdit(false);
        orr.setRoleMemberId(createdRoleMemberId);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole delegationMemberId should not be null.", orr.getDelegationMemberId() );
        assertFalse( "OrgReviewRole delegationMemberId should not have been blank.", orr.getDelegationMemberId().equals("") );

        // ensure the delegate type now exists
        DelegateType existingDelegateType = roleService.getDelegateTypeByRoleIdAndDelegateTypeCode(orr.getRoleId(), DelegationType.fromCode(orr.getDelegationTypeCode()));
        assertNotNull( "Unable to retrieve delegate type object from the KIM Service", existingDelegateType );
        DelegateMember dm = roleService.getDelegationMemberById(orr.getDelegationMemberId());
        assertNotNull( "Unable to retrieve delegate member with given ID: " + dm.getDelegationMemberId(), dm );

        checkForMatchingDelegationMember(orr, orgHierRole);
    }

    public void testSaveOrgReviewRoleToKim_AcctReview_Delegate_New() throws Exception {
        // FIRST - create a known role member
        OrgReviewRole orr = buildAcctOrgHierData();

        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
        assertFalse( "OrgReviewRole roleMemberId should not have been blank.", orr.getRoleMemberId().equals("") );

        // now, look in KIM for the role
        checkForMatchingRoleMember(orr, acctHierRole);
        String createdRoleMemberId = orr.getRoleMemberId();

        orr = buildAcctHierDelegateTypeData( DelegationType.PRIMARY );
        orr.setEdit(false);
        orr.setRoleMemberId(createdRoleMemberId);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole delegationMemberId should not be null.", orr.getDelegationMemberId() );
        assertFalse( "OrgReviewRole delegationMemberId should not have been blank.", orr.getDelegationMemberId().equals("") );

        // ensure the delegate type now exists
        DelegateType existingDelegateType = roleService.getDelegateTypeByRoleIdAndDelegateTypeCode(orr.getRoleId(), DelegationType.fromCode(orr.getDelegationTypeCode()));
        assertNotNull( "Unable to retrieve delegate type object from the KIM Service", existingDelegateType );
        DelegateMember dm = roleService.getDelegationMemberById(orr.getDelegationMemberId());
        assertNotNull( "Unable to retrieve delegate member with given ID: " + dm.getDelegationMemberId(), dm );

        checkForMatchingDelegationMember(orr, acctHierRole);
    }

    public void testSaveOrgReviewRoleToKim_OrgReview_Delegate_Edit() throws Exception {
        // Create a org role and delegation
        OrgReviewRole orr = buildOrgHierData();
        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
        // add a delegation
        String createdRoleMemberId = orr.getRoleMemberId();
        orr = buildOrgHierDelegateTypeData( DelegationType.PRIMARY );
        orr.setEdit(false);
        orr.setRoleMemberId(createdRoleMemberId);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole delegationMemberId should not be null.", orr.getDelegationMemberId() );

        // check that the delegate member exists
        DelegateMember dm = roleService.getDelegationMemberById(orr.getDelegationMemberId());
        assertNotNull( "Unable to retrieve delegation from KIM after save", dm );
        String originalDelegationMemberId = dm.getDelegationMemberId();

        // edit the delegation
        orr = new OrgReviewRole();
        orgReviewRoleService.populateOrgReviewRoleFromDelegationMember(orr, dm.getDelegationMemberId());
        orr.setEdit(true);
        assertEquals( "Document type on the retrieved DelegationMember was not correct","ACCT",orr.getFinancialSystemDocumentTypeCode());
        orr.setFinancialSystemDocumentTypeCode( "SACC" );
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole delegationMemberId should not be null.", orr.getDelegationMemberId() );

        dm = roleService.getDelegationMemberById(orr.getDelegationMemberId());
        assertNotNull( "Unable to retrieve delegation from KIM after save", dm );
        assertEquals( "The delegationMemberId should not have changed", originalDelegationMemberId, dm.getDelegationMemberId() );
        assertEquals( "Document type on the retrieved DelegationMember was not correct","SACC",orr.getFinancialSystemDocumentTypeCode());
    }

    public void testSaveOrgReviewRoleToKim_AcctReview_Delegate_Edit() throws Exception {
        // Create a org role and delegation
        OrgReviewRole orr = buildAcctOrgHierData();
        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
        // add a delegation
        String createdRoleMemberId = orr.getRoleMemberId();
        orr = buildAcctHierDelegateTypeData( DelegationType.PRIMARY );
        orr.setEdit(false);
        orr.setRoleMemberId(createdRoleMemberId);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole delegationMemberId should not be null.", orr.getDelegationMemberId() );

        // check that the delegate member exists
        DelegateMember dm = roleService.getDelegationMemberById(orr.getDelegationMemberId());
        assertNotNull( "Unable to retrieve delegation from KIM after save", dm );
        String originalDelegationMemberId = dm.getDelegationMemberId();

        // edit the delegation
        orr = new OrgReviewRole();
        orgReviewRoleService.populateOrgReviewRoleFromDelegationMember(orr, dm.getDelegationMemberId());
        orr.setEdit(true);
        assertEquals( "Document type on the retrieved DelegationMember was not correct","DI",orr.getFinancialSystemDocumentTypeCode());
        orr.setFinancialSystemDocumentTypeCode( "GEC" );
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole delegationMemberId should not be null.", orr.getDelegationMemberId() );

        dm = roleService.getDelegationMemberById(orr.getDelegationMemberId());
        assertNotNull( "Unable to retrieve delegation from KIM after save", dm );
        assertEquals( "The delegationMemberId should not have changed", originalDelegationMemberId, dm.getDelegationMemberId() );
        assertEquals( "Document type on the retrieved DelegationMember was not correct","GEC",orr.getFinancialSystemDocumentTypeCode());
    }


//    public void testPopulateOrgReviewRoleFromRoleMember() {
//        fail("Not yet implemented");
//    }
//
//    public void testPopulateOrgReviewRoleFromDelegationMember() {
//        fail("Not yet implemented");
//    }
//
//    public void testPopulateObjectExtras() {
//        fail("Not yet implemented");
//    }

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
        assertTrue( "DI should have accounting org hierarchy", orgReviewRoleService.hasAccountingOrganizationHierarchy("DI"));
        assertFalse( "COAT should not have accounting org hierarchy", orgReviewRoleService.hasAccountingOrganizationHierarchy("COAT"));
    }

//    public void testGetClosestOrgReviewRoleParentDocumentTypeName() {
//        fail("Not yet implemented");
//    }
//
//    public void testGetRolesToConsider() {
//        fail("Not yet implemented");
//    }

//    public void testCurrentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles() {
//        assertTrue( "COAT should have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("COAT"));
//        assertFalse( "KFS should not have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("KFS"));
//        assertFalse( "KFST should not have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("KFST"));
//        assertFalse( "DI should not have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("DI"));
////        assertTrue( "FSSM should have reported zero child docs with org review", orgReviewRoleService.currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles("FSSM"));
//    }

}
