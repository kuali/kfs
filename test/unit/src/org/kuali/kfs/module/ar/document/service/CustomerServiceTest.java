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

    // prncpl_id = 6162502038
    protected static final String USER = "khuntley";
    protected static final String CUSTOMER_LAST_NAME_STARTING_LETTER = "A";
    protected static final String CUSTOMER_LAST_NAME_ENDING_LETTER = "B";

    protected static final String TEST_ROLE_MEMBER_ID = "TEST_CGBCOLLECTOR";
    protected static final String SQL_ADD_CGB_COLL_QUAL_1 = "INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, OBJ_ID, ver_nbr, ROLE_ID, MBR_ID, MBR_TYP_CD) VALUES (\n" +
    		"'" + TEST_ROLE_MEMBER_ID + "', '" + TEST_ROLE_MEMBER_ID + "', 1, (SELECT role_id FROM krim_role_t WHERE nmspc_cd = 'KFS-AR' AND role_nm = 'CGB Collector')" +
    				", (SELECT prncpl_id FROM krim_prncpl_t WHERE prncpl_nm = '" + USER + "' ), 'P' )";
    protected static final String SQL_ADD_CGB_COLL_QUAL_2 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, role_mbr_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" +
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_1', '"+TEST_ROLE_MEMBER_ID+"_1', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-AR\' AND role_nm = \'CGB Collector\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+ArKimAttributes.CUSTOMER_LAST_NAME_STARTING_LETTER+"'), '" + CUSTOMER_LAST_NAME_STARTING_LETTER + "' )";
    protected static final String SQL_ADD_CGB_COLL_QUAL_3 = "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, ver_nbr, role_mbr_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) \n" +
    "    VALUES('"+TEST_ROLE_MEMBER_ID+"_2', '"+TEST_ROLE_MEMBER_ID+"_2', 1, '" + TEST_ROLE_MEMBER_ID + "', (SELECT kim_typ_id FROM krim_role_t WHERE nmspc_cd = \'KFS-AR\' AND role_nm = \'CGB Collector\')" +
            ", (SELECT kim_attr_defn_id FROM krim_attr_defn_t WHERE nm = '"+ArKimAttributes.CUSTOMER_LAST_NAME_ENDING_LETTER+"'), '" + CUSTOMER_LAST_NAME_ENDING_LETTER + "' )";

    protected static final String SQL_DELETE_CGB_COLL_QUAL_1 = "DELETE FROM KRIM_ROLE_MBR_T WHERE ROLE_MBR_ID = '" + TEST_ROLE_MEMBER_ID + "'";
    protected static final String SQL_DELETE_CGB_COLL_QUAL_2 = "DELETE FROM KRIM_ROLE_MBR_ATTR_DATA_T WHERE ROLE_MBR_ID = '" + TEST_ROLE_MEMBER_ID + "'";

//    Insert into "krim_role_mbr_t" (ROLE_MBR_ID,VER_NBR,OBJ_ID,ROLE_ID,MBR_ID,MBR_TYP_CD,ACTV_FRM_DT,ACTV_TO_DT,LAST_UPDT_DT) values ('10003',3,'f5e10021-f196-4c53-a62e-e35785584327','KFSCGBPII1','1204808831','P',null,null,null);
//    Insert into "krim_role_mbr_t" (ROLE_MBR_ID,VER_NBR,OBJ_ID,ROLE_ID,MBR_ID,MBR_TYP_CD,ACTV_FRM_DT,ACTV_TO_DT,LAST_UPDT_DT) values ('10004',3,'f4f1030f-2677-41cb-8230-4a71729eb801','KFSCGBPII1','1204808831','P',null,null,null);
//    Insert into "krim_role_mbr_t" (ROLE_MBR_ID,VER_NBR,OBJ_ID,ROLE_ID,MBR_ID,MBR_TYP_CD,ACTV_FRM_DT,ACTV_TO_DT,LAST_UPDT_DT) values ('10006',3,'79065604-1673-49c1-8f22-3a10cdaf1d02','KFSCGBPII1','1509103107','P',null,null,null);
//    Insert into "krim_role_mbr_t" (ROLE_MBR_ID,VER_NBR,OBJ_ID,ROLE_ID,MBR_ID,MBR_TYP_CD,ACTV_FRM_DT,ACTV_TO_DT,LAST_UPDT_DT) values ('10005',3,'e542dd4a-a8c5-4991-896d-e7d238a28d4b','KFSCGBPII1','2264204055','P',null,null,null);

//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10000','98dddfe1-af6a-4cfd-83ed-19c5eacc8acb',1,'10003','KFSMI10824-TYP1','KFSMI10824-ATTRDEF1','BA');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10001','3f258a29-2578-4dc2-93a8-e5e7a693d76b',1,'10003','KFSMI10824-TYP1','KFSMI10824-ATTRDEF2','ACAC');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10004','8caaed07-2621-46ec-827e-a987ba2ce977',1,'10003','KFSMI10824-TYP1','KFSMI10824-ATTRDEF5','A');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10005','40a69f2a-2022-4796-a1bc-6641310ad2a0',1,'10003','KFSMI10824-TYP1','KFSMI10824-ATTRDEF6','H');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10006','eabcc03c-8348-434d-94a3-57acb0c5f333',1,'10004','KFSMI10824-TYP1','KFSMI10824-ATTRDEF1','BL');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10007','9d2972e1-8b64-4dd1-a2d8-e9c47769e8d2',1,'10004','KFSMI10824-TYP1','KFSMI10824-ATTRDEF2','ACAD');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10010','284e2554-d909-429b-af96-77f676b11cac',1,'10004','KFSMI10824-TYP1','KFSMI10824-ATTRDEF5','I');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10011','de1c2c90-d284-49ad-9220-44306db91865',1,'10004','KFSMI10824-TYP1','KFSMI10824-ATTRDEF6','L');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10012','9c18cac6-1714-4b63-b268-f7c5c18b60b0',1,'10005','KFSMI10824-TYP1','KFSMI10824-ATTRDEF1','BL');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10013','47bb3fbf-d915-44a3-93d1-b47c7135da51',1,'10005','KFSMI10824-TYP1','KFSMI10824-ATTRDEF2','ACAD');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10016','35e2dabc-2830-4083-b54a-353280074988',1,'10005','KFSMI10824-TYP1','KFSMI10824-ATTRDEF5','A');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10017','59fe1ef7-94d5-43db-8292-de958ef068c3',1,'10005','KFSMI10824-TYP1','KFSMI10824-ATTRDEF6','L');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10018','3fc00bfe-6a67-4d5b-a8c2-5fa2e072c591',1,'10006','KFSMI10824-TYP1','KFSMI10824-ATTRDEF1','BL');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10019','eb5e25b6-8215-4ebb-8a09-27ee9cad3412',1,'10006','KFSMI10824-TYP1','KFSMI10824-ATTRDEF2','ACAD');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10022','947af558-1de3-4caa-b08f-e3410dcc2373',1,'10006','KFSMI10824-TYP1','KFSMI10824-ATTRDEF5','V');
//    Insert into "krim_role_mbr_attr_data_t" (ATTR_DATA_ID,OBJ_ID,VER_NBR,ROLE_MBR_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) values ('10023','4c34014d-a3aa-4f55-8645-89fe435103ef',1,'10006','KFSMI10824-TYP1','KFSMI10824-ATTRDEF6','V');

    private UnitTestSqlDao unitTestSqlDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        System.out.println(unitTestSqlDao.sqlCommand(SQL_ADD_CGB_COLL_QUAL_1));
        System.out.println(unitTestSqlDao.sqlCommand(SQL_ADD_CGB_COLL_QUAL_2));
        System.out.println(unitTestSqlDao.sqlCommand(SQL_ADD_CGB_COLL_QUAL_3));
        System.out.println(unitTestSqlDao.sqlCommand("commit"));
    }

    @Override
    protected void tearDown() throws Exception {
        System.out.println(unitTestSqlDao.sqlCommand(SQL_DELETE_CGB_COLL_QUAL_1));
        System.out.println(unitTestSqlDao.sqlCommand(SQL_DELETE_CGB_COLL_QUAL_2));
        System.out.println(unitTestSqlDao.sqlCommand("commit"));
    }

    public void testDoesCustomerMatchCollector() {
        // add CGB Collector role qualifiers to khuntley

        CustomerService service = SpringContext.getBean(CustomerService.class);
        assertTrue(service.doesCustomerMatchCollector("ABERCROMBIE", "6162502038"));
        assertFalse(service.doesCustomerMatchCollector("COLUMBIA", "6162502038"));
    }
}
