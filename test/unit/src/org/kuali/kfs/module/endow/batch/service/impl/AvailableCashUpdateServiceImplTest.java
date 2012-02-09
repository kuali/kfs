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
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.kfs.module.endow.fixture.CurrentCashFixture;
import org.kuali.kfs.module.endow.fixture.CurrentTaxLotBalanceFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = kfs)
public class AvailableCashUpdateServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AvailableCashUpdateServiceImplTest.class);

    private BusinessObjectService businessObjectService;
    private AvailableCashUpdateServiceImpl availableCashUpdateService;    
    private KEMID kemid;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        availableCashUpdateService = (AvailableCashUpdateServiceImpl) TestUtils.getUnproxiedService("mockAvailableCashUpdateService");
    }
    
    private void createDataFixtures() {
        //setup dummy data so the method can test the method getAvailableIncomeCash()
        //setup dummy kemid record
        kemid = KemIdFixture.CLOSED_KEMID_RECORD.createKemidRecord();
        
        //setup a record in END_CRNT_CSH_T record
        CurrentCashFixture.CURRENT_CASH_RECORD.createKemidCurrentCashRecord();
        
        //need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_2.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_3.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_4.createHoldingTaxLotRebalanceRecord();
        
        HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD.createHoldingTaxLotRecord();
        HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_2.createHoldingTaxLotRecord();
        HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_3.createHoldingTaxLotRecord();
        HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_4.createHoldingTaxLotRecord();
        
        //insert into current tax lot balance table...
        CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD.createCurrentTaxLotBalanceRecord();
        CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD_2.createCurrentTaxLotBalanceRecord();
        CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD_3.createCurrentTaxLotBalanceRecord();
        CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD_4.createCurrentTaxLotBalanceRecord();
    }
    
    /**
     * Test to see if AVAILABLE_CASH_PERCENT system parameter is setup
     */
    public final void testCheckSystemParameterExists() {
        LOG.info("testCheckSystemParameterExists() method entered");
        
        boolean parameterExists = availableCashUpdateService.systemParametersForSummarizeAvailableSpendableFundsJobExist();
        assertTrue("AVAILABLE_CASH_PERCENT System parameter does not exist.", parameterExists);

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
        availableCashUpdateService.kemidCurrentAvailableBalanceService.clearAllAvailableCash();

        Collection availableCashRecords = businessObjectService.findAll(KEMIDCurrentAvailableBalance.class);
        assertTrue("Records in END_AVAIL_CSH_T table were not deleted by clearAllAvailableCash() method.", availableCashRecords.size() == 0);

        LOG.info("testClearAllAvailableCash() method finished.");
    }
    
    private void updateKemIdClosedIndicator(String closedIndicator) {
        boolean close = (closedIndicator.equals(EndowConstants.YES) ? true : false);
        
        Map fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.KEMID_CLOSED, closedIndicator);
        
        Collection<KEMID> kemIdRecords = businessObjectService.findMatching(KEMID.class, fieldValues);
        for (KEMID kemId : kemIdRecords) {
            kemId.setClose(close);
            businessObjectService.save(kemId);
        }
    }
    
    /**
     * Test to see if getAllKemIdWithClosedIndicatorNo() correctly retrieves the records from END_KEMID_T
     */
    public final void testGetAllKemidWithClosedIndicatorNo() {
        LOG.info("testGetAllKemidWithClosedIndicatorNo() method entered.");
        
    //    unitTestSqlDao.sqlCommand("Update END_KEMID_T set CLOSED_IND = 'N'");
        updateKemIdClosedIndicator(EndowConstants.NO);
        Map fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.KEMID_CLOSED, EndowConstants.NO);
        Collection<KEMID> kemidRecordsInTable = businessObjectService.findMatching(KEMID.class, fieldValues);
        
        //Step 2: Retrieve all KEMID records where CLOSED_IND set to N
        Collection<KEMID> kemIdRecords = availableCashUpdateService.kemidService.getAllKemIdWithClosedIndicatorNo();
        assertTrue("KEMID record total retrived from getAllKemIdWithClosedIndicatorNo() does not match with records in the table END_KEMID_T with closed indicator = 'N'", kemidRecordsInTable.size() == kemIdRecords.size());
        
        updateKemIdClosedIndicator(EndowConstants.YES);
        fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.KEMID_CLOSED, EndowConstants.YES);
        kemidRecordsInTable = businessObjectService.findMatching(KEMID.class, fieldValues);
        
        kemIdRecords = availableCashUpdateService.kemidService.getAllKemIdWithClosedIndicatorNo();
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
        Map fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.KEMID_CLOSED, EndowConstants.NO);
        Collection<KEMID> kemidRecords = businessObjectService.findMatching(KEMID.class, fieldValues);
        
        availableCashUpdateService.summarizeAvailableSpendableFunds();
        
      //The count of rows in the table should be 1 
        Collection availableCashRecords = businessObjectService.findAll(KEMIDCurrentAvailableBalance.class);
        assertTrue("Total Records in END_AVAIL_CSH_T should be equal to Total Open Records in END_KEMID_T", availableCashRecords.size() == kemidRecords.size());
        
        LOG.info("testSummarizeAvailableSpendableFunds() method finished.");        
    }
}
