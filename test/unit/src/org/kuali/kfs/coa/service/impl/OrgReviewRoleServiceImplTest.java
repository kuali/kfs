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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.krad.exception.ValidationException;

@ConfigureContext
public class OrgReviewRoleServiceImplTest extends OrgReviewRoleTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.getLogger(OrgReviewRoleServiceImpl.class).setLevel(Level.DEBUG);
        Logger.getLogger("log4j.logger.p6spy").setLevel(Level.INFO);
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
        dumpQueryResultsToErr("SELECT * FROM KRIM_DLGN_T WHERE role_id = '7'");

        // check that the delegate member exists
        DelegateMember dm = roleService.getDelegationMemberById(orr.getDelegationMemberId());
        assertNotNull( "Unable to retrieve delegation from KIM after save", dm );
        System.err.println( "Saved Delegation Member: " + dm );
        String originalDelegationMemberId = dm.getDelegationMemberId();

        // edit the delegation
        orr = new OrgReviewRole();
        orgReviewRoleService.populateOrgReviewRoleFromDelegationMember(orr, createdRoleMemberId, dm.getDelegationMemberId());
        orr.setEdit(true);
        assertEquals( "Document type on the retrieved DelegationMember was not correct","ACCT",orr.getFinancialSystemDocumentTypeCode());
        orr.setFinancialSystemDocumentTypeCode( "SACC" );
        orgReviewRoleService.saveOrgReviewRoleToKim(orr);
        assertNotNull( "OrgReviewRole delegationMemberId should not be null.", orr.getDelegationMemberId() );

        dm = roleService.getDelegationMemberById(orr.getDelegationMemberId());
        System.err.println( "Updated Delegation Member: " + dm );
        assertNotNull( "Unable to retrieve delegation from KIM after save", dm );
        dumpQueryResultsToErr("SELECT * FROM KRIM_DLGN_MBR_T WHERE dlgn_mbr_id IN ( '" + originalDelegationMemberId + "', '" + dm.getDelegationMemberId() + "' )");
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
        orgReviewRoleService.populateOrgReviewRoleFromDelegationMember(orr, createdRoleMemberId, dm.getDelegationMemberId());
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
