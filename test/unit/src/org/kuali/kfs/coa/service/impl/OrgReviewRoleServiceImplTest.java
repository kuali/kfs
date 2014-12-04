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
package org.kuali.kfs.coa.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class OrgReviewRoleServiceImplTest extends OrgReviewRoleTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.getLogger(OrgReviewRoleServiceImpl.class).setLevel(Level.DEBUG);
        Logger.getLogger("log4j.logger.p6spy").setLevel(Level.INFO);
    }

    public void testSaveOrgReviewRoleToKim_OrgReview_New() throws Exception {
        // don't run this test if there are any enroute documents since the test may fail in that case
        if (!enrouteDocumentsExist()) {
            OrgReviewRole orr = buildOrgHierData();

            orr.setEdit(false);
            orgReviewRoleService.saveOrgReviewRoleToKim(orr);
            assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
            assertFalse( "OrgReviewRole roleMemberId should not have been blank.", orr.getRoleMemberId().equals("") );

            // now, look in KIM for the role
            checkForMatchingRoleMember(orr, orgHierRole);
        }
    }

    public void testSaveOrgReviewRoleToKim_AcctReview_New() throws Exception {
        // don't run this test if there are any enroute documents since the test may fail in that case
        if (!enrouteDocumentsExist()) {
            OrgReviewRole orr = buildAcctOrgHierData();

            orr.setEdit(false);
            orgReviewRoleService.saveOrgReviewRoleToKim(orr);
            assertNotNull( "OrgReviewRole roleMemberId should not be null.", orr.getRoleMemberId() );
            assertFalse( "OrgReviewRole roleMemberId should not have been blank.", orr.getRoleMemberId().equals("") );

            // now, look in KIM for the role
            checkForMatchingRoleMember(orr, acctHierRole);
        }
    }

    public void testSaveOrgReviewRoleToKim_OrgReview_Edit() throws Exception {
        // don't run this test if there are any enroute documents since the test may fail in that case
        if (!enrouteDocumentsExist()) {
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
    }

    public void testSaveOrgReviewRoleToKim_AcctReview_Edit() throws Exception {
        // don't run this test if there are any enroute documents since the test may fail in that case
        if (!enrouteDocumentsExist()) {
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
    }

    public void testSaveOrgReviewRoleToKim_OrgReview_Delegate_New() throws Exception {
        // don't run this test if there are any enroute documents since the test may fail in that case
        if (!enrouteDocumentsExist()) {
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
    }

    public void testSaveOrgReviewRoleToKim_AcctReview_Delegate_New() throws Exception {
        // don't run this test if there are any enroute documents since the test may fail in that case
        if (!enrouteDocumentsExist()) {
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
    }

    public void testSaveOrgReviewRoleToKim_OrgReview_Delegate_Edit() throws Exception {
        // don't run this test if there are any enroute documents since the test may fail in that case
        if (!enrouteDocumentsExist()) {
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
    }

    public void testSaveOrgReviewRoleToKim_AcctReview_Delegate_Edit() throws Exception {
        // don't run this test if there are any enroute documents since the test may fail in that case
        if (!enrouteDocumentsExist()) {
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
    }

    public void testIsValidDocumentTypeForOrgReview() {
        assertTrue( "DI should have been valid", orgReviewRoleService.isValidDocumentTypeForOrgReview("DI") );
        assertTrue( "ACCT should have been valid", orgReviewRoleService.isValidDocumentTypeForOrgReview("ACCT") );
        assertFalse( "COAT should not have been valid", orgReviewRoleService.isValidDocumentTypeForOrgReview("COAT") );
    }

    public void testValidateDocumentType() {
        orgReviewRoleService.validateDocumentType("COAT");
        assertFalse( "An error should have been added to the MessageMap for the COAT document type: " + dumpMessageMapErrors(), GlobalVariables.getMessageMap().getErrorMessagesForProperty(OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME).isEmpty() );
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

    /**
     *  Look for any documents (created by optional unit tests) that may be at the accounting organization hierarchy or
     *  organization hierarchy route nodes so we can bypass running tests that may fail
     * @return whether there are any enroute documents or not
     */
    protected boolean enrouteDocumentsExist() {
        final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, DocumentStatus.ENROUTE.getCode());
        int count = boService.countMatching(FinancialSystemDocumentHeader.class, fieldValues);
        return count > 0;
    }

}
