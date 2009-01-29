/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.coa.identity;

import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;

@ConfigureContext
public class OrganizationAndOptionalNamespaceRoleTypeServiceImplTest extends KualiTestBase {
    
    protected static final String USER = "khuntley";
    protected static final String USER_CHART = "UA";
    protected static final String USER_ORG = "VPIT";

    protected static final String USER_COA_CHART = "BL";
    protected static final String USER_COA_ORG = "MATH";
    
    protected static final String TEST_ROLE_MEMBER_ID = "TEST_OAONRTSIT";
    protected static final String SQL_ADD_COA_ORG_1 = "INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, OBJ_ID, ver_nbr, ROLE_ID, MBR_ID, MBR_TYP_CD) VALUES (\n" + 
    		"'" + TEST_ROLE_MEMBER_ID + "', '" + TEST_ROLE_MEMBER_ID + "', 1, (SELECT role_id FROM krim_role_t WHERE nmspc_cd = 'KFS-SYS' AND role_nm = 'User')" +
    				", (SELECT prncpl_id FROM krim_prncpl_t WHERE prncpl_nm = '" + USER + "' ), 'P' )";
    protected static final String SQL_ADD_COA_ORG_2 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" + 
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_1', '"+TEST_ROLE_MEMBER_ID+"_1', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-SYS\' AND role_nm = \'User\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+KfsKimAttributes.CHART_OF_ACCOUNTS_CODE+"'), '" + USER_COA_CHART + "' )";
    protected static final String SQL_ADD_COA_ORG_3 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" + 
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_2', '"+TEST_ROLE_MEMBER_ID+"_2', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-SYS\' AND role_nm = \'User\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+KfsKimAttributes.ORGANIZATION_CODE+"'), '" + USER_COA_ORG + "' )";
    protected static final String SQL_ADD_COA_ORG_4 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" + 
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_3', '"+TEST_ROLE_MEMBER_ID+"_3', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-SYS\' AND role_nm = \'User\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+KfsKimAttributes.NAMESPACE_CODE+"'), '" + KFSConstants.ParameterNamespaces.CHART + "' )";
    
    
    private UnitTestSqlDao unitTestSqlDao;
    
    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }
    
    public void testDoRoleQualifiersMatchQualification() {
        
        // add an additional org to khuntley
        unitTestSqlDao.sqlCommand(SQL_ADD_COA_ORG_1);
        unitTestSqlDao.sqlCommand(SQL_ADD_COA_ORG_2);
        unitTestSqlDao.sqlCommand(SQL_ADD_COA_ORG_3);
        unitTestSqlDao.sqlCommand(SQL_ADD_COA_ORG_4);

        AttributeSet qualification = new AttributeSet();
        qualification.put(KfsKimAttributes.NAMESPACE_CODE, "");
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        String principalId = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(USER).getPrincipalId();

        // test lookup with no qualifiers
//        List<AttributeSet> roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, null);
//        assertEquals( "roleQualifiers should have returned exactly one AttributeSet", 1, roleQualifiers.size() );
//        assertEquals( "chart did not match", USER_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
//        assertEquals( "org did not match", USER_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );

        // test lookup with blank
        List<AttributeSet> roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned exactly one AttributeSet", 1, roleQualifiers.size() );
        assertEquals( "chart did not match", USER_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
        assertEquals( "org did not match", USER_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );

        // test lookup with KFS-SYS
        roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned exactly one AttributeSet", 1, roleQualifiers.size() );
        assertEquals( "chart did not match", USER_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
        assertEquals( "org did not match", USER_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );

        // test lookup with KFS-FP (will not exist - should get default (blank))
        qualification.put(KfsKimAttributes.NAMESPACE_CODE, KFSConstants.ParameterNamespaces.FINANCIAL);
        roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned exactly one AttributeSet", 1, roleQualifiers.size() );
        assertEquals( "chart did not match", USER_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
        assertEquals( "org did not match", USER_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );

        // test lookup with KFS-COA (will not exist - should get default (blank))
        qualification.put(KfsKimAttributes.NAMESPACE_CODE, KFSConstants.ParameterNamespaces.CHART);
        roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned exactly one AttributeSet", 1, roleQualifiers.size() );
        assertEquals( "chart did not match", USER_COA_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
        assertEquals( "org did not match", USER_COA_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );
        
        // add test for match on user's chart/org
        qualification.put(KfsKimAttributes.NAMESPACE_CODE, "");
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, USER_CHART);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, USER_ORG);
        roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned exactly one AttributeSet", 1, roleQualifiers.size() );
        assertEquals( "chart did not match", USER_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
        assertEquals( "org did not match", USER_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );

        qualification.put(KfsKimAttributes.NAMESPACE_CODE, KFSConstants.ParameterNamespaces.FINANCIAL);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, USER_CHART);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, USER_ORG);
        roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned exactly one AttributeSet", 1, roleQualifiers.size() );
        assertEquals( "chart did not match", USER_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
        assertEquals( "org did not match", USER_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );

        qualification.put(KfsKimAttributes.NAMESPACE_CODE, KFSConstants.ParameterNamespaces.CHART);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, USER_CHART);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, USER_ORG);
        roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned no AttributeSets", 0, roleQualifiers.size() );
        
        qualification.put(KfsKimAttributes.NAMESPACE_CODE, KFSConstants.ParameterNamespaces.CHART);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, USER_COA_CHART);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, USER_COA_ORG);
        roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned exactly one AttributeSet", 1, roleQualifiers.size() );
        assertEquals( "chart did not match", USER_COA_CHART, roleQualifiers.get(0).get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
        assertEquals( "org did not match", USER_COA_ORG, roleQualifiers.get(0).get(KfsKimAttributes.ORGANIZATION_CODE) );
        
        qualification.put(KfsKimAttributes.NAMESPACE_CODE, "");
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, USER_COA_CHART);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, USER_COA_ORG);
        roleQualifiers = KIMServiceLocator.getRoleService().getRoleQualifiersForPrincipal(principalId, KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME, qualification);
        assertEquals( "roleQualifiers should have returned no AttributeSets", 0, roleQualifiers.size() );

    }
}
