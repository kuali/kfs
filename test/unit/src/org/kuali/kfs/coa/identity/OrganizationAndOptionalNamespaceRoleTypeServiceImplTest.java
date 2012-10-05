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
package org.kuali.kfs.coa.identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

@ConfigureContext
public class OrganizationAndOptionalNamespaceRoleTypeServiceImplTest extends KualiTestBase {

    protected static final String USER = "khuntley";
    protected static final String USER_COA_CHART = "BL";
    protected static final String USER_COA_ORG = "MATH";

    protected static final String USER_CHART = "UA";
    protected static final String USER_ORG = "VPIT";

    protected static final String TEST_ROLE_MEMBER_ID = "TEST_OAONRTSIT";
    protected static final String SQL_ADD_COA_ORG_1 = "INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, OBJ_ID, ver_nbr, ROLE_ID, MBR_ID, MBR_TYP_CD) VALUES (\n" +
    		"'" + TEST_ROLE_MEMBER_ID + "', '" + TEST_ROLE_MEMBER_ID + "', 1, (SELECT role_id FROM krim_role_t WHERE nmspc_cd = 'KFS-SYS' AND role_nm = 'User')" +
    				", (SELECT prncpl_id FROM krim_prncpl_t WHERE prncpl_nm = '" + USER + "' ), 'P' )";
    protected static final String SQL_ADD_COA_ORG_2 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, role_mbr_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" +
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_1', '"+TEST_ROLE_MEMBER_ID+"_1', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-SYS\' AND role_nm = \'User\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+KfsKimAttributes.CHART_OF_ACCOUNTS_CODE+"'), '" + USER_COA_CHART + "' )";
    protected static final String SQL_ADD_COA_ORG_3 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, role_mbr_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" +
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_2', '"+TEST_ROLE_MEMBER_ID+"_2', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-SYS\' AND role_nm = \'User\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+KfsKimAttributes.ORGANIZATION_CODE+"'), '" + USER_COA_ORG + "' )";
    protected static final String SQL_ADD_COA_ORG_4 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, role_mbr_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" +
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_3', '"+TEST_ROLE_MEMBER_ID+"_3', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-SYS\' AND role_nm = \'User\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+KimConstants.AttributeConstants.NAMESPACE_CODE+"'), '" + KFSConstants.CoreModuleNamespaces.CHART + "' )";


    private UnitTestSqlDao unitTestSqlDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }

    /**
     * TODO: Remove once other tests are fixed
     */
    public void testNothing() {

    }

    /**
     * TODO: fix this test, inserted data is not getting picked up
     */
    public void PATCHFIX_testDoRoleQualifiersMatchQualification() {

        // add an additional org to khuntley
        unitTestSqlDao.sqlCommand(SQL_ADD_COA_ORG_1);
        unitTestSqlDao.sqlCommand(SQL_ADD_COA_ORG_2);
        unitTestSqlDao.sqlCommand(SQL_ADD_COA_ORG_3);
        unitTestSqlDao.sqlCommand(SQL_ADD_COA_ORG_4);

        Map<String,String> qualification = new HashMap<String,String>();
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        String principalId = SpringContext.getBean(IdentityService.class).getPrincipalByPrincipalName(USER).getPrincipalId();

        RoleService roleService = KimApiServiceLocator.getRoleService();

        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, KFSConstants.CoreModuleNamespaces.CHART);
        List<Map<String,String>> roleQualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned exactly one Map<String,String>", 1, roleQualifiers.size() );
        assertEquals( "chart did not match", USER_COA_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
        assertEquals( "org did not match", USER_COA_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );

        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, KFSConstants.CoreModuleNamespaces.FINANCIAL);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, USER_CHART);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, USER_ORG);
        roleQualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned no Map<String,String>s", 0, roleQualifiers.size() );

        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, KFSConstants.CoreModuleNamespaces.CHART);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, USER_CHART);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, USER_ORG);
        roleQualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned no Map<String,String>s", 0, roleQualifiers.size() );

        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, KFSConstants.CoreModuleNamespaces.CHART);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, USER_COA_CHART);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, USER_COA_ORG);
        roleQualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned exactly one Map<String,String>", 1, roleQualifiers.size() );
        assertEquals( "chart did not match", USER_COA_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
        assertEquals( "org did not match", USER_COA_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );

        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, "");
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, USER_COA_CHART);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, USER_COA_ORG);
        roleQualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned no Map<String,String>s", 0, roleQualifiers.size() );

    }
}
