/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.batch.service.impl.AvailableCashUpdateServiceImpl;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.kfs.sys.context.TestUtils;

@ConfigureContext(session = kfs)
public class AvailableCashUpdateServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AvailableCashUpdateServiceImplTest.class);

    private UnitTestSqlDao unitTestSqlDao;
    private AvailableCashUpdateServiceImpl availableCashUpdateService;    
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        availableCashUpdateService = (AvailableCashUpdateServiceImpl) TestUtils.getUnproxiedService("mockAvailableCashUpdateService");
    }
    
    /**
     * Test to see if AVAILABLE_CASH_PERCENT system parameter is setup
     */
    public final void testCheckSystemParameterExists() {
        LOG.info("testCheckSystemParameterExists() method entered");
        
        List systemParameters = unitTestSqlDao.sqlSelect("select * from krns_parm_t where PARM_DTL_TYP_CD = 'AvailableCashUpdateStep' and parm_nm = 'AVAILABLE_CASH_PERCENT'");
        
        if (!systemParameters.isEmpty()) {
            boolean parameterExists = availableCashUpdateService.systemParametersForSummarizeAvailableSpendableFundsJobExist();
            assertTrue("AVAILABLE_CASH_PERCENT System parameter does not exist.", parameterExists);
        }

        LOG.info("testCheckSystemParameterExists() method completed.");
    }
    
    /**
     * Test to see if clearAllAvailableCash() method is working correctly
     * Call the method first to clear the END_AVAIL_CSH_T and check if there are any records in t
     * If there are records in the table, then the method failed.
     */
    public final void testClearAllAvailableCash() {
        LOG.info("testClearAllAvailableCash() method entered");
        
      //Step 1: remove all the records from END_AVAIL_CSH_T table        
        availableCashUpdateService.kEMIDCurrentAvailableBalanceService.clearAllAvailableCash();

        List availableCashRecords = unitTestSqlDao.sqlSelect("select * from END_AVAIL_CSH_T");
        assertTrue("Records in END_AVAIL_CSH_T table were not deleted by clearAllAvailableCash() method.", availableCashRecords.size() == 0);

        LOG.info("testClearAllAvailableCash() method finished.");
    }
    
    /**
     * Test to see if getAllKemIdWithClosedIndicatorNo() correctly retrieves the records from END_KEMID_T
     */
    public final void testGetAllKemidWithClosedIndicatorNo() {
        LOG.info("testGetAllKemidWithClosedIndicatorNo() method entered.");
        
        unitTestSqlDao.sqlCommand("Update END_KEMID_T set CLOSED_IND = 'N'");
        List kemidRecordsInTable = unitTestSqlDao.sqlSelect("select * from END_KEMID_T WHERE CLOSED_IND = 'N'");
        
        //Step 2: Retrieve all KEMID records where CLOSED_IND set to N
        Collection<KEMID> kemIdRecords = availableCashUpdateService.kEMIDService.getAllKemIdWithClosedIndicatorNo();
        assertTrue("KEMID record total retrived from getAllKemIdWithClosedIndicatorNo() does not match with records in the table END_KEMID_T with closed indicator = 'N'", kemidRecordsInTable.size() == kemIdRecords.size());
        
        unitTestSqlDao.sqlCommand("Update END_KEMID_T set CLOSED_IND = 'Y'");
        kemidRecordsInTable = unitTestSqlDao.sqlSelect("select * from END_KEMID_T WHERE CLOSED_IND = 'Y'");
        kemIdRecords = availableCashUpdateService.kEMIDService.getAllKemIdWithClosedIndicatorNo();
        assertFalse("getAllKemIdWithClosedIndicatorNo() method should not have retrieved any records. ", kemidRecordsInTable.size() == kemIdRecords.size());

        LOG.info("testGetAllKemidWithClosedIndicatorNo() method finished.");
    }
    
    /**
     * Test to see the method getAvailableIncomeCash() works correctly.
     * Setup the records in the corresponding tables and then call the method to
     * if the retrieved available cash income equals the manually calculated total here.
     */
    public final void testGetAvilableIncomeCash() {
        LOG.info("testGetAvilableIncomeCash() method entered.");
        
        //setup dummy data so the method can test the method getAvailableIncomeCash()
        //setup dummy kemid record
        unitTestSqlDao.sqlCommand("insert into END_KEMID_T columns (KEMID, SHRT_TTL, LONG_TTL, OPND_DT, ESTBL_DT, TYP_CD, PRPS_CD, INC_CAE_CD, PRIN_CAE_CD, RESP_ADMIN_CD, TRAN_RESTR_CD, CSH_SWEEP_MDL_ID, INC_ACI_MDL_ID, PRIN_ACI_MDL_ID, DORMANT_IND, CLOSED_IND, CLOSED_TO_KEMID, CLOSE_CD, FND_DISP, CLOSE_DT, OBJ_ID, TYP_INC_RESTR_CD, TYP_PRIN_RESTR_CD) values ('TESTKEMID', 'Gift Annuity Trust 3', 'Gift Annuity Trust 3', TO_DATE('2/23/2006', 'mm/dd/yyyy'), TO_DATE('2/23/2006', 'mm/dd/yyyy'), '046', 'MR', '9', 'Q', 'TRST', 'NTRAN', '1', null, null, 'N', 'Y', '038B011179', 'T', 'Matured trust created new KEMID', TO_DATE('07/25/2007', 'mm/dd/yyyy'),sys_guid(), 'TRU', 'TRU')");

        //setup a record in END_CRNT_CSH_T record
        unitTestSqlDao.sqlCommand("insert into END_CRNT_CSH_T values ('TESTKEMID', 1250.80, 1000.21, sys_guid(), 1)");

        //need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0NI', 'I', '1', '20', '10000', sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', 'I', '2', '282586', '282586', sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', 'P', '3', '23123', '23123', sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', 'REI', 'P', '4', '10000', '10000', sys_guid())");
        
        //setup records in END_HLDG_TAX_LOT_T to get the totals by Income or Principal indicators.
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0NI', '1', 'I', TO_DATE('11/1/2005', 'mm/dd/yyyy'), '20', '10000', '0', '0',  TO_DATE('6/27/2002', 'mm/dd/yyyy'),sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '2', 'I', TO_DATE('11/23/2009', 'mm/dd/yyyy'), '282586', '282586', '0', '0',  TO_DATE('11/23/2009', 'mm/dd/yyyy'),sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '3', 'P', TO_DATE('12/10/2009', 'mm/dd/yyyy'), '23123', '23123', '0', '1.2',  TO_DATE('12/10/2009', 'mm/dd/yyyy'),sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', 'REI', '4', 'P', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '10000', '10000', '0', '0', TO_DATE('9/30/2008', 'mm/dd/yyyy'),sys_guid())");
        
        //insert into current tax lot balance table...
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0NI', '1', 'I', '20', '10000', '0', '0', '0', '500', TO_DATE('11/1/2005', 'mm/dd/yyyy'), '0', '0', TO_DATE('6/27/2002', 'mm/dd/yyyy'), '10000',sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '2', 'I', '282586', '282586', '0', '0', '0', '1', TO_DATE('11/23/2009', 'mm/dd/yyyy'), '0', '0', TO_DATE('11/23/2009', 'mm/dd/yyyy'), '282586',sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '3', 'P', '23123', '23123', '0', '0', '0', '1', TO_DATE('12/10/2009', 'mm/dd/yyyy'), '1.2', '0', TO_DATE('12/10/2009', 'mm/dd/yyyy'), '23123',sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', 'REI', '4', 'P', '10000', '10000', '0', '0', '0', '1', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '0', '0', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '10000',sys_guid())");
        
        BigDecimal availableCashIncomeTotal = new BigDecimal("1250.80");
        availableCashIncomeTotal = availableCashIncomeTotal.add(new BigDecimal("10000").add(new BigDecimal("282586")));
        
        assertTrue("Available Income Cash does not match with Amounts in the database tables", availableCashIncomeTotal.compareTo(availableCashUpdateService.getAvailableIncomeCash("TESTKEMID")) == 0);

        LOG.info("testGetAvilableIncomeCash() method finished.");
    }
    
    /**
     * Test to see the method getAvailablePrincipalCash() works correctly.
     * Setup the records in the corresponding tables and then call the method to
     * if the retrieved available cash principal equals the manually calculated total here.
     */
    public final void testGetAvailablePrincipalCash() {
        LOG.info("testGetAvailablePrincipalCash() method entered.");
        
        //setup dummy data so the method can test the method getAvailablePrincipalCash()
        //setup dummy kemid record
        unitTestSqlDao.sqlCommand("insert into END_KEMID_T columns (KEMID, SHRT_TTL, LONG_TTL, OPND_DT, ESTBL_DT, TYP_CD, PRPS_CD, INC_CAE_CD, PRIN_CAE_CD, RESP_ADMIN_CD, TRAN_RESTR_CD, CSH_SWEEP_MDL_ID, INC_ACI_MDL_ID, PRIN_ACI_MDL_ID, DORMANT_IND, CLOSED_IND, CLOSED_TO_KEMID, CLOSE_CD, FND_DISP, CLOSE_DT, OBJ_ID, TYP_INC_RESTR_CD, TYP_PRIN_RESTR_CD) values ('TESTKEMID', 'Gift Annuity Trust 3', 'Gift Annuity Trust 3', TO_DATE('2/23/2006', 'mm/dd/yyyy'), TO_DATE('2/23/2006', 'mm/dd/yyyy'), '046', 'MR', '9', 'Q', 'TRST', 'NTRAN', '1', null, null, 'N', 'Y', '038B011179', 'T', 'Matured trust created new KEMID', TO_DATE('07/25/2007', 'mm/dd/yyyy'),sys_guid(), 'TRU', 'TRU')");

        //setup a record in END_CRNT_CSH_T record
        unitTestSqlDao.sqlCommand("insert into END_CRNT_CSH_T values ('TESTKEMID', 1250.80, 1000.21, sys_guid(), 1)");

        //need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0NI', 'I', '1', '20', '10000', sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', 'I', '2', '282586', '282586', sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', 'P', '3', '23123', '23123', sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', 'REI', 'P', '4', '10000', '10000', sys_guid())");
        
        //setup records in END_HLDG_TAX_LOT_T to get the totals by Income or Principal indicators.
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0NI', '1', 'I', TO_DATE('11/1/2005', 'mm/dd/yyyy'), '20', '10000', '0', '0',  TO_DATE('6/27/2002', 'mm/dd/yyyy'),sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '2', 'I', TO_DATE('11/23/2009', 'mm/dd/yyyy'), '282586', '282586', '0', '0',  TO_DATE('11/23/2009', 'mm/dd/yyyy'),sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '3', 'P', TO_DATE('12/10/2009', 'mm/dd/yyyy'), '23123', '23123', '0', '1.2',  TO_DATE('12/10/2009', 'mm/dd/yyyy'),sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', 'REI', '4', 'P', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '10000', '10000', '0', '0', TO_DATE('9/30/2008', 'mm/dd/yyyy'),sys_guid())");
        
        //insert into current tax lot balance table...
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0NI', '1', 'I', '20', '10000', '0', '0', '0', '500', TO_DATE('11/1/2005', 'mm/dd/yyyy'), '0', '0', TO_DATE('6/27/2002', 'mm/dd/yyyy'), '10000',sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '2', 'I', '282586', '282586', '0', '0', '0', '1', TO_DATE('11/23/2009', 'mm/dd/yyyy'), '0', '0', TO_DATE('11/23/2009', 'mm/dd/yyyy'), '282586',sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '3', 'P', '23123', '23123', '0', '0', '0', '1', TO_DATE('12/10/2009', 'mm/dd/yyyy'), '1.2', '0', TO_DATE('12/10/2009', 'mm/dd/yyyy'), '23123',sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', 'REI', '4', 'P', '10000', '10000', '0', '0', '0', '1', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '0', '0', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '10000',sys_guid())");
        
        BigDecimal availableCashIncomeTotal = new BigDecimal("1000.21"); //principal amount...
        availableCashIncomeTotal = availableCashIncomeTotal.add(new BigDecimal("23123").add(new BigDecimal("10000")));
        
        assertTrue("Available Principal Cash does not match with Amounts in the database tables", availableCashIncomeTotal.compareTo(availableCashUpdateService.getAvailablePrincipalCash("TESTKEMID", "TRU")) == 0);

        LOG.info("testGetAvailablePrincipalCash() method finished.");        
    }
    

    /**
     * Test to make sure the summarize of available spendable funds works correctly in the implementation
     * Set all KEMID records CLOSED_IND to Y and then insert a record with indicator = N and then
     * call the implementation method to make sure that 1 row is added into END_AVAIL_CSH_T table.
     */
    public final void testSummarizeAvailableSpendableFunds() {
        LOG.info("testSummarizeAvailableSpendableFunds() method entered.");
        
        //update END_KEMID_T table CLOSED_IND = 'Y' so we can test what we are adding to the table...
        unitTestSqlDao.sqlCommand("UPDATE END_KEMID_T SET CLOSED_IND = 'Y'");
        
        //setup dummy data so the method can test the method getAvailablePrincipalCash()
        //setup dummy kemid record
        unitTestSqlDao.sqlCommand("insert into END_KEMID_T columns (KEMID, SHRT_TTL, LONG_TTL, OPND_DT, ESTBL_DT, TYP_CD, PRPS_CD, INC_CAE_CD, PRIN_CAE_CD, RESP_ADMIN_CD, TRAN_RESTR_CD, CSH_SWEEP_MDL_ID, INC_ACI_MDL_ID, PRIN_ACI_MDL_ID, DORMANT_IND, CLOSED_IND, CLOSED_TO_KEMID, CLOSE_CD, FND_DISP, CLOSE_DT, OBJ_ID, TYP_INC_RESTR_CD, TYP_PRIN_RESTR_CD) values ('TESTKEMID', 'Gift Annuity Trust 3', 'Gift Annuity Trust 3', TO_DATE('2/23/2006', 'mm/dd/yyyy'), TO_DATE('2/23/2006', 'mm/dd/yyyy'), '046', 'MR', '9', 'Q', 'TRST', 'NTRAN', '1', null, null, 'N', 'N', '038B011179', 'T', 'Matured trust created new KEMID', TO_DATE('07/25/2007', 'mm/dd/yyyy'),sys_guid(), 'TRU', 'TRU')");

        //setup a record in END_CRNT_CSH_T record
        unitTestSqlDao.sqlCommand("insert into END_CRNT_CSH_T values ('TESTKEMID', 1250.80, 1000.21, sys_guid(), 1)");

        //need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0NI', 'I', '1', '20', '10000', sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', 'I', '2', '282586', '282586', sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', 'P', '3', '23123', '23123', sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', '99PETTY12', 'REI', 'P', '4', '10000', '10000', sys_guid())");
        
        //setup records in END_HLDG_TAX_LOT_T to get the totals by Income or Principal indicators.
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0NI', '1', 'I', TO_DATE('11/1/2005', 'mm/dd/yyyy'), '20', '10000', '0', '0',  TO_DATE('6/27/2002', 'mm/dd/yyyy'),sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '2', 'I', TO_DATE('11/23/2009', 'mm/dd/yyyy'), '282586', '282586', '0', '0',  TO_DATE('11/23/2009', 'mm/dd/yyyy'),sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '3', 'P', TO_DATE('12/10/2009', 'mm/dd/yyyy'), '23123', '23123', '0', '1.2',  TO_DATE('12/10/2009', 'mm/dd/yyyy'),sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', '99PETTY12', 'REI', '4', 'P', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '10000', '10000', '0', '0', TO_DATE('9/30/2008', 'mm/dd/yyyy'),sys_guid())");
        
        //insert into current tax lot balance table...
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0NI', '1', 'I', '20', '10000', '0', '0', '0', '500', TO_DATE('11/1/2005', 'mm/dd/yyyy'), '0', '0', TO_DATE('6/27/2002', 'mm/dd/yyyy'), '10000',sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '2', 'I', '282586', '282586', '0', '0', '0', '1', TO_DATE('11/23/2009', 'mm/dd/yyyy'), '0', '0', TO_DATE('11/23/2009', 'mm/dd/yyyy'), '282586',sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', '0AI', '3', 'P', '23123', '23123', '0', '0', '0', '1', TO_DATE('12/10/2009', 'mm/dd/yyyy'), '1.2', '0', TO_DATE('12/10/2009', 'mm/dd/yyyy'), '23123',sys_guid())");
        unitTestSqlDao.sqlCommand("insert into END_CRNT_TAX_LOT_BAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_UNITS, HLDG_COST, HLDG_ANNL_INC_EST, HLDG_FY_REM_EST_INC, HLDG_NEXT_FY_EST_INC, SEC_UNIT_VAL, HLDG_ACQD_DT, HLDG_PRIOR_ACRD_INC, HLDG_ACRD_INC_DUE, LAST_TRAN_DT, HLDG_MVAL, OBJ_ID) values ('TESTKEMID', '99PETTY12', 'REI', '4', 'P', '10000', '10000', '0', '0', '0', '1', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '0', '0', TO_DATE('9/30/2008', 'mm/dd/yyyy'), '10000',sys_guid())");
        
        //The count of rows in the table should be 1 
        List kemidRecords = unitTestSqlDao.sqlSelect("SELECT * FROM END_KEMID_T WHERE CLOSED_IND = 'N'");
        
        availableCashUpdateService.summarizeAvailableSpendableFunds();
        
      //The count of rows in the table should be 1 
        List availableCashRecords = unitTestSqlDao.sqlSelect("SELECT * FROM END_AVAIL_CSH_T");
        
        assertTrue("Total Records in END_AVAIL_CSH_T should be equal to Total Open Records in END_KEMID_T", availableCashRecords.size() == kemidRecords.size());
        
        LOG.info("testSummarizeAvailableSpendableFunds() method finished.");        
    }
}
