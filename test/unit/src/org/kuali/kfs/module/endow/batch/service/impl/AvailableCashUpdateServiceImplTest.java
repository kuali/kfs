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
import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.fixture.CurrentCashFixture;
import org.kuali.kfs.module.endow.fixture.CurrentTaxLotBalanceFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kns.util.KualiInteger;

@ConfigureContext(session = kfs)
public class AvailableCashUpdateServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AvailableCashUpdateServiceImplTest.class);

    private UnitTestSqlDao unitTestSqlDao;
    private AvailableCashUpdateServiceImpl availableCashUpdateService;    
    private KEMID kemid;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        availableCashUpdateService = (AvailableCashUpdateServiceImpl) TestUtils.getUnproxiedService("mockAvailableCashUpdateService");
    }
    
    private void createDataFixtures() {
        //setup dummy data so the method can test the method getAvailableIncomeCash()
        //setup dummy kemid record
        kemid = KemIdFixture.KEMID_RECORD.createKemidRecord();
        
        //setup a record in END_CRNT_CSH_T record
        CurrentCashFixture.CURRENT_CASH_RECORD.createKemidCurrentCashRecord();
        
        //need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD.createHoldingTaxLotRebalanceRecord("TESTKEMID", "99PETTY12", "0AI", "I", new KualiInteger(2), BigDecimal.valueOf(282586.00), BigDecimal.valueOf(282586.00));
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD.createHoldingTaxLotRebalanceRecord("TESTKEMID", "99PETTY12", "0AI", "P", new KualiInteger(3), BigDecimal.valueOf(23123.00), BigDecimal.valueOf(23123.00));
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD.createHoldingTaxLotRebalanceRecord("TESTKEMID", "99PETTY12", "REI", "P", new KualiInteger(4), BigDecimal.valueOf(10000.00), BigDecimal.valueOf(10000.00));

        HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD.createHoldingTaxLotRecord();
        HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD.createHoldingTaxLotRecord("TESTKEMID", "99PETTY12", "0AI", "I", new KualiInteger(2), Date.valueOf("2009-11-23"), BigDecimal.valueOf(282586.00), BigDecimal.valueOf(282586.00), BigDecimal.ZERO, BigDecimal.ZERO,  Date.valueOf("2009-11-23"));
        HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD.createHoldingTaxLotRecord("TESTKEMID", "99PETTY12", "0AI", "P", new KualiInteger(3), Date.valueOf("2009-11-23"), BigDecimal.valueOf(23123.00), BigDecimal.valueOf(23123.00), BigDecimal.ZERO, BigDecimal.valueOf(1.2),  Date.valueOf("2009-11-23"));
        HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD.createHoldingTaxLotRecord("TESTKEMID", "99PETTY12", "REI", "P", new KualiInteger(4), Date.valueOf("2009-11-23"), BigDecimal.valueOf(10000.00), BigDecimal.valueOf(10000.00), BigDecimal.ZERO, BigDecimal.ZERO,  Date.valueOf("2009-11-23"));
        
        //insert into current tax lot balance table...
        CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD.createCurrentTaxLotBalanceRecord();       
        CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD.createCurrentTaxLotBalanceRecord("TESTKEMID", "99PETTY12", "0AI", new KualiInteger(2), "I", BigDecimal.valueOf(282586.00), BigDecimal.valueOf(282586.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(1.00), Date.valueOf("2009-11-23"), BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00), Date.valueOf("2009-11-23"), BigDecimal.valueOf(282586.00));
        CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD.createCurrentTaxLotBalanceRecord("TESTKEMID", "99PETTY12", "0AI", new KualiInteger(3), "P", BigDecimal.valueOf(23123.00), BigDecimal.valueOf(23123.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(1.00), Date.valueOf("2009-12-10"), BigDecimal.valueOf(1.20), BigDecimal.valueOf(0.00), Date.valueOf("2009-12-10"), BigDecimal.valueOf(23123.00));
        CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD.createCurrentTaxLotBalanceRecord("TESTKEMID", "99PETTY12", "REI", new KualiInteger(4), "P", BigDecimal.valueOf(10000.00), BigDecimal.valueOf(10000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(1.00), Date.valueOf("2009-09-30"), BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00), Date.valueOf("2008-09-30"), BigDecimal.valueOf(10000.00));
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
        
        //create records to test...
        createDataFixtures();
        
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
        
        //create records to test...
        createDataFixtures();
        
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
        
        //create records to test...
        createDataFixtures();
        
        //The count of rows in the table should be 1 
        List kemidRecords = unitTestSqlDao.sqlSelect("SELECT * FROM END_KEMID_T WHERE CLOSED_IND = 'N'");
        
        availableCashUpdateService.summarizeAvailableSpendableFunds();
        
      //The count of rows in the table should be 1 
        List availableCashRecords = unitTestSqlDao.sqlSelect("SELECT * FROM END_AVAIL_CSH_T");
        
        assertTrue("Total Records in END_AVAIL_CSH_T should be equal to Total Open Records in END_KEMID_T", availableCashRecords.size() == kemidRecords.size());
        
        LOG.info("testSummarizeAvailableSpendableFunds() method finished.");        
    }
}
