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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.module.ar.identity.ArKimAttributes;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;

//@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
@ConfigureContext
public class CustomerServiceTest extends KualiTestBase {

    protected static final String PRINCIPAL_ID = "6162502038";
    protected static final String CUSTOMER_LAST_NAME_STARTING_LETTER = "A";
    protected static final String CUSTOMER_LAST_NAME_ENDING_LETTER = "B";

    protected static final String TEST_ROLE_MEMBER_ID = "TEST_CGBCOLLECTOR";
    protected static final String SQL_ADD_CGB_COLL_QUAL_1 = "INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, OBJ_ID, ver_nbr, ROLE_ID, MBR_ID, MBR_TYP_CD) VALUES (\n" +
    		"'" + TEST_ROLE_MEMBER_ID + "', '" + TEST_ROLE_MEMBER_ID + "', 1, (SELECT role_id FROM krim_role_t WHERE nmspc_cd = 'KFS-AR' AND role_nm = 'CGB Collector')" +
    				", '" + PRINCIPAL_ID + "', 'P' )";
    protected static final String SQL_ADD_CGB_COLL_QUAL_2 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, role_mbr_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" +
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_1', '"+TEST_ROLE_MEMBER_ID+"_1', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-AR\' AND role_nm = \'CGB Collector\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+ArKimAttributes.CUSTOMER_LAST_NAME_STARTING_LETTER+"'), '" + CUSTOMER_LAST_NAME_STARTING_LETTER + "' )";
    protected static final String SQL_ADD_CGB_COLL_QUAL_3 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, role_mbr_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" +
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_2', '"+TEST_ROLE_MEMBER_ID+"_2', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-AR\' AND role_nm = \'CGB Collector\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+ArKimAttributes.CUSTOMER_LAST_NAME_ENDING_LETTER+"'), '" + CUSTOMER_LAST_NAME_ENDING_LETTER + "' )";

    protected static final String SQL_DELETE_CGB_COLL_QUAL_1 = "DELETE FROM KRIM_ROLE_MBR_T WHERE ROLE_MBR_ID = '" + TEST_ROLE_MEMBER_ID + "'";
    protected static final String SQL_DELETE_CGB_COLL_QUAL_2 = "DELETE FROM KRIM_ROLE_MBR_ATTR_DATA_T WHERE ROLE_MBR_ID = '" + TEST_ROLE_MEMBER_ID + "'";

    private UnitTestSqlDao unitTestSqlDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        unitTestSqlDao.sqlCommand(SQL_ADD_CGB_COLL_QUAL_1);
        unitTestSqlDao.sqlCommand(SQL_ADD_CGB_COLL_QUAL_2);
        unitTestSqlDao.sqlCommand(SQL_ADD_CGB_COLL_QUAL_3);
        unitTestSqlDao.sqlCommand("commit");
    }

    @Override
    protected void tearDown() throws Exception {
        unitTestSqlDao.sqlCommand(SQL_DELETE_CGB_COLL_QUAL_1);
        unitTestSqlDao.sqlCommand(SQL_DELETE_CGB_COLL_QUAL_2);
        unitTestSqlDao.sqlCommand("commit");
    }

    public void testDoesCustomerMatchCollector() {
        CustomerService service = SpringContext.getBean(CustomerService.class);
        assertTrue(service.doesCustomerMatchCollector("ABERCROMBIE", PRINCIPAL_ID));
        assertFalse(service.doesCustomerMatchCollector("COLUMBIA", PRINCIPAL_ID));
    }
}
