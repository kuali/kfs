/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.service;

import java.util.List;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Org;
import org.kuali.test.ConfigureContext;

/**
 * This class tests the BC PermissionService class
 */
@ConfigureContext
public class PermissionServiceTest extends KualiTestBase {

    private PermissionService permissionService;
    private List<Org> orgs;

    private boolean runTests() { // change this to return false to prevent running tests
        return false;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        orgs = null;
        permissionService = SpringContext.getBean(PermissionService.class);

    }

    /**
     * This tests org.kuali.module.budget.service.PermissionService.getOrgReview() It depends on certain WorkFlow OrgReview rules to
     * exist for the BudgetConstructionDocument type. It verifies that khuntley is a BCD approver for one organization and it
     * is IU-UNIV It verifies that hsoucy is a BCD approver for two organizations in the review hierarchy (BL-BL,UA-UA) one as a
     * member of the workgroup KUALI-BLSPECIAL (BL-BL) and one as an individual (UA-UA)
     * 
     * @throws Exception
     */
    public void testGetOrgReview() throws Exception {

        if (!runTests())
            return;

        orgs = permissionService.getOrgReview("khuntley");
        assertEquals("Number of BC Approval Organizations returned is incorrect.", 1, orgs.size());
        if (orgs.size() == 1) {
            assertEquals("IU", orgs.get(0).getChartOfAccountsCode());
            assertEquals("UNIV", orgs.get(0).getOrganizationCode());
        }
        orgs = null;
        orgs = permissionService.getOrgReview("hsoucy");
        assertEquals("Number of BC Approval Organizations returned is incorrect.", 2, orgs.size());

    }

    /**
     * This tests org.kuali.module.budget.service.PermissionService.isOrgReviewApprover() It depends on certain WorkFlow OrgReview
     * rules to exist for the BudgetConstructionDocument type. It verfies that khuntley is a BCD approver for the organization
     * IU-UNIV It verfies that khuntley is *NOT* a BCD approver for the organization BL-BL It verfies that dfogle is a KBCD
     * approver for the organization BL-PSY
     * 
     * @throws Exception
     */
    public void testIsOrgReviewApprover() throws Exception {

        if (!runTests())
            return;

        assertTrue(permissionService.isOrgReviewApprover("khuntley", "IU", "UNIV"));
        assertFalse(permissionService.isOrgReviewApprover("khuntley", "BL", "BL"));
        assertTrue(permissionService.isOrgReviewApprover("dfogle", "BL", "PSY"));

    }
}
