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

import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.exception.ValidationException;

@ConfigureContext
public class OrgReviewRoleServiceImplTest extends KualiTestBase {

    OrgReviewRoleServiceImpl orgReviewRoleService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        orgReviewRoleService = (OrgReviewRoleServiceImpl) SpringContext.getBean(OrgReviewRoleService.class);
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
