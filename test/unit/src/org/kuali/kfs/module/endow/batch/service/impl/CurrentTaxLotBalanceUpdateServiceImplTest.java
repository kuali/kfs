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

import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.CurrentCashFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = kfs)
public class CurrentTaxLotBalanceUpdateServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CurrentTaxLotBalanceUpdateServiceImplTest.class);

    private CurrentTaxLotBalanceUpdateServiceImpl currentTaxLotBalanceUpdateService;    
    
    private BusinessObjectService businessObjectService;
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
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

        Collection<CurrentTaxLotBalance> currentTaxLotRecords = businessObjectService.findAll(CurrentTaxLotBalance.class);
        
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
        SecurityReportingGroup srg = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        ClassCode classCode = ClassCodeFixture.LIABILITY_CLASS_CODE_CURRENT_TAX_LOT.createClassCodeRecord();
        Security security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();
        KEMID kemid = KemIdFixture.CLOSED_KEMID_RECORD.createKemidRecord();

      //setup a record in END_CRNT_CSH_T record
        KemidCurrentCash kemidCurrentCash = CurrentCashFixture.CURRENT_CASH_RECORD.createKemidCurrentCashRecord();
        
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();
        
      //need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_LIABILITY.createHoldingTaxLotRebalanceRecord();
      
      //remove all records from the table so we can insert 1 record...
        SpringContext.getBean(HoldingTaxLotService.class).removeAllHoldingTaxLots();
        
      //setup records in END_HLDG_TAX_LOT_T to get the totals by Income or Principal indicators.
        HoldingTaxLot htl = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_LIABILITY.createHoldingTaxLotRecord();
        
      //remove all the records from END_CRNT_TAX_LOT_BAL_T table        
        currentTaxLotBalanceUpdateService.currentTaxLotService.clearAllCurrentTaxLotRecords();

        Collection<CurrentTaxLotBalance> currentTaxLotRecords = businessObjectService.findAll(CurrentTaxLotBalance.class);
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
          
          BigDecimal remainderOfFYEstimatedIncome = BigDecimal.ZERO;
          currentTaxLotBalance.setRemainderOfFYEstimatedIncome(currentTaxLotBalanceUpdateService.currentTaxLotService.getRemainderOfFiscalYearEstimatedIncome(holdingTaxLot, securityId));
          assertTrue("Remainder of FY Estimated Income value should be 16.44.", (remainderOfFYEstimatedIncome.compareTo(currentTaxLotBalance.getRemainderOfFYEstimatedIncome()) == 0));
          
          currentTaxLotBalance.setNextFYEstimatedIncome(currentTaxLotBalanceUpdateService.currentTaxLotService.getNextFiscalYearInvestmentIncome(holdingTaxLot, securityId));
          assertTrue("Next FY Estimated Income value should be 2004.00.", ((new BigDecimal("2004.00")).compareTo(currentTaxLotBalance.getNextFYEstimatedIncome()) == 0));
              
          currentTaxLotBalanceUpdateService.saveCurrentTaxLotRecord(currentTaxLotBalance);
          currentTaxLotRecords = businessObjectService.findAll(CurrentTaxLotBalance.class);
          
          assertTrue("Records in END_CRNT_TAX_LOT_BAL_T table should be just 1", currentTaxLotRecords.size() == 1);
          if(LOG.isDebugEnabled()) {
              LOG.debug("Updated current tax lot balance for Security Id: " + securityId + " and kemid: " + holdingTaxLot.getKemid());    
          }
      }
      
      LOG.info("testUpdateCurrentTaxLotBalances() method finished.");
    }
}
