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
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;

@ConfigureContext(session = kfs)
public class CurrentTaxLotBalanceUpdateServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CurrentTaxLotBalanceUpdateServiceImplTest.class);

    private UnitTestSqlDao unitTestSqlDao;
    private CurrentTaxLotBalanceUpdateServiceImpl currentTaxLotBalanceUpdateService;    
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        currentTaxLotBalanceUpdateService = (CurrentTaxLotBalanceUpdateServiceImpl) TestUtils.getUnproxiedService("mockCurrentTaxLotBalanceUpdateService");
    }
    
    /**
     * Test to see if clearAllCurrentTaxLotRecords() method is working correctly
     * Call the method first to clear the END_CURR_TAX_LOT_BAL_T and check if there are any records in t
     * If there are records in the table, then the method failed.
     */
    public final void testClearAllCurrentTaxLotRecords() {
        LOG.info("testClearAllCurrentTaxLotRecords() method entered");
        
      //remove all the records from END_CRNT_TAX_LOT_BAL_T table        
        currentTaxLotBalanceUpdateService.currentTaxLotService.clearAllCurrentTaxLotRecords();

        List currentTaxLotRecords = unitTestSqlDao.sqlSelect("select * from END_CRNT_TAX_LOT_BAL_T");
        assertTrue("Records in END_CRNT_TAX_LOT_BAL_T table were not deleted by clearAllCurrentTaxLotRecords() method.", currentTaxLotRecords.size() == 0);

        LOG.info("testClearAllCurrentTaxLotRecords() method finished.");
    }
    
    /**
     * Test to see if updateCurrentTaxLotBalances() from the service impl class
     */
    public final void testUpdateCurrentTaxLotBalances() {
        LOG.info("testUpdateCurrentTaxLotBalances() method entered.");
        
        //setup dummy data so the method can test the method getAvailableIncomeCash()
        //setup dummy kemid record
        unitTestSqlDao.sqlCommand("insert into END_SEC_T columns(SEC_ID, SEC_CLS_CD, SEC_UNIT_VAL, SEC_INC_PAY_FREQ, SEC_INC_NEXT_PAY_DT, SEC_RT, ROW_ACTV_IND, OBJ_ID, VER_NBR, NXT_FSCL_YR_DSTRB_AMT) values ('TESTSECID', '700', '1', 'M01', TO_DATE('08/01/2010', 'mm/dd/yyyy'), '20', 'Y', sys_guid(), '1', '100.20')");
        unitTestSqlDao.sqlCommand("insert into END_KEMID_T columns (KEMID, SHRT_TTL, LONG_TTL, OPND_DT, ESTBL_DT, TYP_CD, PRPS_CD, INC_CAE_CD, PRIN_CAE_CD, RESP_ADMIN_CD, TRAN_RESTR_CD, CSH_SWEEP_MDL_ID, INC_ACI_MDL_ID, PRIN_ACI_MDL_ID, DORMANT_IND, CLOSED_IND, CLOSED_TO_KEMID, CLOSE_CD, FND_DISP, CLOSE_DT, OBJ_ID, TYP_INC_RESTR_CD, TYP_PRIN_RESTR_CD) values ('TESTKEMID', 'Gift Annuity Trust 3', 'Gift Annuity Trust 3', TO_DATE('2/23/2006', 'mm/dd/yyyy'), TO_DATE('2/23/2006', 'mm/dd/yyyy'), '046', 'MR', '9', 'Q', 'TRST', 'NTRAN', '1', null, null, 'N', 'Y', '038B011179', 'T', 'Matured trust created new KEMID', TO_DATE('07/25/2007', 'mm/dd/yyyy'),sys_guid(), 'TRU', 'TRU')");

        //setup a record in END_CRNT_CSH_T record
        unitTestSqlDao.sqlCommand("insert into END_CRNT_CSH_T values ('TESTKEMID', 1250.80, 1000.21, sys_guid(), 1)");

        //need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_REBAL_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_IP_IND, TOT_HLDG_LOT_NBR, TOT_HLDG_UNITS, TOT_HLDG_COST, OBJ_ID) values ('TESTKEMID', 'TESTSECID', '0NI', 'I', '1', '20', '10000', sys_guid())");
        
        //remove all records from the table so we can insert 1 record...
        unitTestSqlDao.sqlCommand("delete from END_HLDG_TAX_LOT_T");
        
        //setup records in END_HLDG_TAX_LOT_T to get the totals by Income or Principal indicators.
        unitTestSqlDao.sqlCommand("insert into END_HLDG_TAX_LOT_T columns (KEMID, SEC_ID, REGIS_CD, HLDG_LOT_NBR, HLDG_IP_IND, HLDG_ACQD_DT, HLDG_UNITS, HLDG_COST, HLDG_ACRD_INC_DUE, HLDG_PRIOR_ACRD_INC, LAST_TRAN_DT, OBJ_ID) values ('TESTKEMID', 'TESTSECID', '0NI', '1', 'I', TO_DATE('11/1/2005', 'mm/dd/yyyy'), '20', '10000', '0', '0',  TO_DATE('6/27/2002', 'mm/dd/yyyy'),sys_guid())");
        
        //remove all the records from END_CRNT_TAX_LOT_BAL_T table        
        currentTaxLotBalanceUpdateService.currentTaxLotService.clearAllCurrentTaxLotRecords();

        List currentTaxLotRecords = unitTestSqlDao.sqlSelect("select * from END_CRNT_TAX_LOT_BAL_T");
        assertTrue("Records in END_CRNT_TAX_LOT_BAL_T table were not deleted by clearAllCurrentTaxLotRecords() method.", currentTaxLotRecords.size() == 0);
        
        List<HoldingTaxLot> holdingTaxLots = currentTaxLotBalanceUpdateService.holdingTaxLotService.getAllTaxLots();
        assertTrue("Records in END_HLDG_TAX_LOT_T table not equal to 1.", holdingTaxLots.size() == 1);
        
        for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
            CurrentTaxLotBalance currentTaxLotBalance = currentTaxLotBalanceUpdateService.currentTaxLotService.copyHoldingTaxLotToCurrentTaxLotBalance(holdingTaxLot); 

            String securityId = currentTaxLotBalance.getSecurityId();
            assertTrue("Security Ids do not match.", securityId.equals("TESTSECID"));

            BigDecimal holdingMarketValue = new BigDecimal(20);
            currentTaxLotBalance.setHoldingMarketValue(currentTaxLotBalanceUpdateService.currentTaxLotService.getHoldingMarketValue(holdingTaxLot, securityId));
            assertTrue("Holding Market Value does not match with record in table.", (holdingMarketValue.compareTo(currentTaxLotBalance.getHoldingMarketValue()) == 0));
            
            BigDecimal securityUnitValue = new BigDecimal(1);
            currentTaxLotBalance.setSecurityUnitVal(currentTaxLotBalanceUpdateService.currentTaxLotService.getCurrentTaxLotBalanceSecurityUnitValue(securityId));
            assertTrue("Security Unit value does not match with record in table.", (securityUnitValue.compareTo(currentTaxLotBalance.getSecurityUnitVal()) == 0));
            
            currentTaxLotBalance.setAnnualEstimatedIncome(currentTaxLotBalanceUpdateService.currentTaxLotService.getNextTwelveMonthsEstimatedValue(holdingTaxLot, securityId));
            assertTrue("Annual Estimated Income value should be 400.00", ((new BigDecimal(400)).compareTo(currentTaxLotBalance.getAnnualEstimatedIncome())== 0));
            
            BigDecimal remainderOfFYEstimatedIncome = new BigDecimal("16.44");
            currentTaxLotBalance.setRemainderOfFYEstimatedIncome(currentTaxLotBalanceUpdateService.currentTaxLotService.getRemainderOfFiscalYearEstimatedIncome(holdingTaxLot, securityId));
      //      assertTrue("Remainder of FY Estimated Income value should be 16.44.", (remainderOfFYEstimatedIncome.compareTo(currentTaxLotBalance.getRemainderOfFYEstimatedIncome()) == 0));
            
            currentTaxLotBalance.setNextFYEstimatedIncome(currentTaxLotBalanceUpdateService.currentTaxLotService.getNextFiscalYearInvestmentIncome(holdingTaxLot, securityId));
            assertTrue("Next FY Estimated Income value should be 2004.00.", ((new BigDecimal("2004.00")).compareTo(currentTaxLotBalance.getNextFYEstimatedIncome()) == 0));
                
            currentTaxLotBalanceUpdateService.saveCurrentTaxLotRecord(currentTaxLotBalance);
            
            currentTaxLotRecords = unitTestSqlDao.sqlSelect("select * from END_CRNT_TAX_LOT_BAL_T");
            assertTrue("Records in END_CRNT_TAX_LOT_BAL_T table should be just 1", currentTaxLotRecords.size() == 1);

            LOG.info("Updated current tax lot balance for Security Id: " + securityId + " and kemid: " + holdingTaxLot.getKemid());
        }
        
        LOG.info("testUpdateCurrentTaxLotBalances() method finished.");
    }
}
