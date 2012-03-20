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

import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.krad.exception.ValidationException;

@ConfigureContext
public class OrgReviewRoleServiceImplTest extends KualiTestBase {

    OrgReviewRoleServiceImpl orgReviewRoleService;
    RoleService roleService;
    KimTypeInfoService kimTypeInfoService;
    PersonService personService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        orgReviewRoleService = (OrgReviewRoleServiceImpl) SpringContext.getBean(OrgReviewRoleService.class);
        roleService = KimApiServiceLocator.getRoleService();
        kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
        personService = KimApiServiceLocator.getPersonService();
    }

    protected OrgReviewRole buildOrgHierData() {
        OrgReviewRole orr = new OrgReviewRole();
        Role role = roleService.getRoleByNamespaceCodeAndName(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);

        orr.setRoleId( role.getId() );
        orr.setKimTypeId( role.getKimTypeId() );
        orr.setRoleName(role.getName());
        orr.setNamespaceCode(role.getNamespaceCode());

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
//        orr.setMemberId(p.getPrincipalId());

        //orr.setActiveFromDate(null);
        //orr.setActiveToDate(null);
        return orr;
    }

    public void testSaveOrgReviewRoleToKim() throws Exception {
        OrgReviewRole orr = buildOrgHierData();

        orr.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);

        // now, look in KIM for the role

        // and validate the data
    }

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
