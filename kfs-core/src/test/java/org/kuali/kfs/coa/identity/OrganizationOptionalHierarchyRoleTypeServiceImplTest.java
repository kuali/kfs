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
package org.kuali.kfs.coa.identity;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

@ConfigureContext//(session=UserNameFixture.hfore)
public class OrganizationOptionalHierarchyRoleTypeServiceImplTest extends KualiTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testPerformMatch() {
        OrganizationOptionalHierarchyRoleTypeServiceImpl roleTypeService = new OrganizationOptionalHierarchyRoleTypeServiceImpl();
        roleTypeService.setOrganizationService(SpringContext.getBean(OrganizationService.class));

        Map<String,String> roleQualifier = new HashMap<String,String>();
        roleQualifier.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
        roleQualifier.put(KFSPropertyConstants.ORGANIZATION_CODE, "PHYS");
        roleQualifier.put(KfsKimAttributes.DESCEND_HIERARCHY, "Y");

        Map<String,String> qualification = new HashMap<String,String>();
        qualification.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
        qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, "PHYS");
        qualification.put(KfsKimAttributes.DESCEND_HIERARCHY, "Y");
        assertTrue( "BL-PHYS - exact match - should have passed", roleTypeService.performMatch(qualification, roleQualifier));
        qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, "BCMI");
        assertTrue( "BL-BCMI- hierarchy match - should have passed", roleTypeService.performMatch(qualification, roleQualifier));
        qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, "ARSC");
        assertFalse( "BL-ARSC - higher-level org - should not have passed", roleTypeService.performMatch(qualification, roleQualifier));
    }

    public void testPerformMatch_PSY_HMNF() {
        OrganizationOptionalHierarchyRoleTypeServiceImpl roleTypeService = new OrganizationOptionalHierarchyRoleTypeServiceImpl();
        roleTypeService.setOrganizationService(SpringContext.getBean(OrganizationService.class));

        Map<String,String> roleQualifier = new HashMap<String,String>();
        roleQualifier.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
        roleQualifier.put(KFSPropertyConstants.ORGANIZATION_CODE, "PSY");
        roleQualifier.put(KfsKimAttributes.DESCEND_HIERARCHY, "Y");

        Map<String,String> qualification = new HashMap<String,String>();
        qualification.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
        qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, "HMNF");
        qualification.put(KfsKimAttributes.DESCEND_HIERARCHY, "Y");
        assertTrue( "BL-HMNF reports to BL-PSY - should have passed", roleTypeService.performMatch(qualification, roleQualifier));
    }

    // assumes data which may not be present in KULDBA - commenting out for now
//    public void testPrincipalHasRole() {
//        RoleService rs = KimApiServiceLocator.getRoleService();
//        ArrayList<String> roleIds = new ArrayList<String>( 1 );
//        roleIds.add( "28" );
//
//        Map<String,String> qualification = new HashMap<String,String>();
//        qualification.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
//        qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, "PHYS");
//        qualification.put(KfsKimAttributes.DESCEND_HIERARCHY, "Y");
//        assertTrue( "BL-PHYS - exact match - should have passed", rs.principalHasRole("3432106365", roleIds, qualification ) );
//        //qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, "BCMI");
//        //assertTrue( "BL-BCMI- hierarchy match - should have passed", rs.principalHasRole("3432106365", roleIds, qualification ) );
//        //qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, "ARSC");
//        //assertFalse( "BL-ARSC - higher-level org - should not have passed", rs.principalHasRole("3432106365", roleIds, qualification ) );
//    }

}
