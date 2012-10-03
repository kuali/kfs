/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

@ConfigureContext
public class AccountsReceivableProcessingOrganizationRoleTypeServiceImplTest extends KualiTestBase {

    protected static final String AR_NAMESPACE = "KFS-AR";
    protected static final String AR_BILLER_ROLE = "Biller";
    protected static final String AR_PROCESSOR_ROLE = "Processor";

    // test data set 1
    protected static final String AR_DOC_USER = "besnead"; // user with KFS-SYS User role for org BA-MOTR
    protected static final String AR_DOC_CHART = "BA";
    protected static final String AR_DOC_ORG = "MOTR";
    protected static final String AR_DOC_PROCESSING_CHART = "UA";
    protected static final String AR_DOC_PROCESSING_ORG = "AR";

    // test data set 2
    protected static final String AR_DOC_USER_2 = "rginn"; // user with KFS-SYS User role for org IN-ACCT
    protected static final String AR_DOC_CHART_2 = "IN";
    protected static final String AR_DOC_ORG_2 = "LART";
    protected static final String AR_DOC_PROCESSING_CHART_2 = "IN";
    protected static final String AR_DOC_PROCESSING_ORG_2 = "ACCT";

    private static RoleService roleManagementService;
    private String arUserPrincipalId;
    private String arUserPrincipalId2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    protected Map<String,String> buildDocQualifier() {
        Map<String,String> qualification = new HashMap<String,String>();
        qualification.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, AR_DOC_CHART );
        qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, AR_DOC_ORG );
        return qualification;
    }

    protected Map<String,String> buildDocQualifier_2() {
        Map<String,String> qualification = new HashMap<String,String>();
        qualification.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, AR_DOC_CHART_2 );
        qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, AR_DOC_ORG_2 );
        return qualification;
    }

    protected String getArUserPrincipalId() {
        if ( arUserPrincipalId == null ) {
            arUserPrincipalId = SpringContext.getBean(IdentityManagementService.class).getPrincipalByPrincipalName(AR_DOC_USER).getPrincipalId();
        }
        return arUserPrincipalId;
    }

    protected String getArUserPrincipalId_2() {
        if ( arUserPrincipalId2 == null ) {
            arUserPrincipalId2 = SpringContext.getBean(IdentityManagementService.class).getPrincipalByPrincipalName(AR_DOC_USER_2).getPrincipalId();
        }
        return arUserPrincipalId2;
    }

    //TODO Andrew 2/5/2009 - Commented out this whole test, as the roleTypeService now explicitly
    // returns exactly what was passed in, which is the exact opposite of what this test was
    // testing.  So something upstream changed, and this test is no longer relevant.  I'm leaving
    // it in in case we ever need to quickly recover this test case.
//    public void testConvertQualificationForMemberRoles() {
//        AccountsReceivableOrganizationDerivedRoleTypeServiceImpl roleTypeService =
//                new AccountsReceivableOrganizationDerivedRoleTypeServiceImpl();
//        roleTypeService.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
//        Map<String,String> qualification = buildDocQualifier();
//        Map<String,String> result = roleTypeService.convertQualificationForMemberRoles(AR_NAMESPACE, AR_PROCESSOR_ROLE, null, null, qualification);
//        assertNotSame( "should not have returned the same object", qualification, result );
//        assertEquals( "charts did not match", AR_DOC_PROCESSING_CHART, result.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE ));
//        assertEquals( "orgs did not match", AR_DOC_PROCESSING_ORG, result.get(KFSPropertyConstants.ORGANIZATION_CODE));
//    }

    public void testPrincipalHasRole_Data1() {
        List<String> tempRoleIdList = new ArrayList<String>( 1 );



        // qualification passed for an AR document for the BA-MOTR organization
        Map<String,String> qualification = buildDocQualifier();

        // find the AR Biller Role
        String billerRoleId = getRoleService().getRoleIdByNamespaceCodeAndName(AR_NAMESPACE, AR_BILLER_ROLE);
        assertNotNull("unable to find biller role", billerRoleId);
        tempRoleIdList.add( billerRoleId );

        boolean result = getRoleService().principalHasRole(getArUserPrincipalId(), tempRoleIdList, qualification);
        assertTrue( "exact match on billing org should have passed", result );

        // find the AR Processor Role
        String processorRoleId = getRoleService().getRoleIdByNamespaceCodeAndName(AR_NAMESPACE, AR_PROCESSOR_ROLE);
        assertNotNull("unable to find processor role", processorRoleId);
        tempRoleIdList.clear();
        tempRoleIdList.add( processorRoleId );

        result = getRoleService().principalHasRole(getArUserPrincipalId(), tempRoleIdList, qualification);
        assertFalse( "test on main user for processing org should have failed - does not have processing org on KFS-SYS User role", result );

    }

    public void testPrincipalHasRole_Data2() {
        List<String> tempRoleIdList = new ArrayList<String>( 1 );

        // qualification passed for an AR document for the BA-MOTR organization
        Map<String,String> qualification = buildDocQualifier_2();

        // find the AR Biller Role
        String billerRoleId = getRoleService().getRoleIdByNamespaceCodeAndName(AR_NAMESPACE, AR_BILLER_ROLE);
        assertNotNull("unable to find biller role", billerRoleId);
        tempRoleIdList.add( billerRoleId );

        boolean result = getRoleService().principalHasRole(getArUserPrincipalId_2(), tempRoleIdList, qualification);
        assertTrue( "exact match on billing org should have passed - user has processing org, not billing org, but all processors are also billers", result );

        // find the AR Processor Role
        String processorRoleId = getRoleService().getRoleIdByNamespaceCodeAndName(AR_NAMESPACE, AR_PROCESSOR_ROLE);
        assertNotNull("unable to find processor role", processorRoleId);
        tempRoleIdList.clear();
        tempRoleIdList.add( processorRoleId );

        result = getRoleService().principalHasRole(getArUserPrincipalId_2(), tempRoleIdList, qualification);
        assertTrue( "principalHasRole test for processor role should have passed - user has processing org on KFS-SYS User role", result );

    }

    /**
     * @return the roleService
     */
    public RoleService getRoleService() {
        if (roleManagementService == null ) {
            roleManagementService = KimApiServiceLocator.getRoleService();
        }
        return roleManagementService;
    }

}
