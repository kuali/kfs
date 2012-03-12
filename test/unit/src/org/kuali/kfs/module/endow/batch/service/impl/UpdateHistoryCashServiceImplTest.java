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

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.fixture.CurrentCashFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = kfs)
public class UpdateHistoryCashServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpdateHistoryCashServiceImplTest.class);

    private UpdateHistoryCashServiceImpl updateHistoryCashService;    
    private BusinessObjectService businessObjectService;
    private KemidCurrentCash kemidCurrentCash;
    private KEMService kemService;
    private KEMID kemid;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        updateHistoryCashService = (UpdateHistoryCashServiceImpl) SpringContext.getService("updateHistoryCashService");
        kemService = SpringContext.getBean(KEMService.class);
        
        clearAllCurrentTaxLotRecords();
        
        kemid = KemIdFixture.OPEN_KEMID_RECORD.createKemidRecord();
        //insert our test fixture in to the current cash table.
        kemidCurrentCash = CurrentCashFixture.CURRENT_CASH_RECORD.createKemidCurrentCashRecord();      
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        businessObjectService = null;        
        kemService = null;
        updateHistoryCashService = null;
        
        super.tearDown();
    }
    
    /**
     * helper method to create a current date which we will set to test our test methods.
     */
    private String setCurrentDate(String dayAndMonth) {
        //prepare a generic date string without year. The year is calculated from the current system date.
        Calendar calendar = Calendar.getInstance();
        dayAndMonth =  dayAndMonth.substring(0, 2).concat("/").concat(dayAndMonth.substring(2, 4)).concat("/") + calendar.get(Calendar.YEAR);
        
        return dayAndMonth;
    }
    
    /**
     * Update the system parameters to test if updateKemidFeeWaivedYearToDateAmount method will work
     * @param useProcessDate value for setting USE_PROCESS_DATE system parameter
     * @param currentProcessDate value for setting CURRENT_PROCESS_DATE system parameter
     * @param fiscalYearEndMonthDay value for setting FISCAL_YEAR_END_MONTH_AND_DAY system parameter
     */
    private void setSystemParameters(String useProcessDate, String currentProcessDate) {
        //set USE_PROCESS_DATE_IND system parameter
        TestUtils.setSystemParameter(KfsParameterConstants.ENDOWMENT_ALL.class, EndowParameterKeyConstants.USE_PROCESS_DATE, useProcessDate);
        //set USE_PROCESS_DATE_IND system parameter
        TestUtils.setSystemParameter(KfsParameterConstants.ENDOWMENT_ALL.class, EndowParameterKeyConstants.CURRENT_PROCESS_DATE, currentProcessDate);
    }
    
    /**
     * clear all the current tax lot records and insert our test fixture so
     * this record can be tested whether it is added to the historical cash table.
     */
    private void clearAllCurrentTaxLotRecords() {
        LOG.info("method clearAllCurrentTaxLotRecords() entered.");
        
        Collection<KemidCurrentCash> kemidCurrentCashRecords = businessObjectService.findAll(KemidCurrentCash.class);
        for (KemidCurrentCash kemidCurrentCashRecord : kemidCurrentCashRecords) {
            businessObjectService.delete(kemidCurrentCashRecord);
        }
        
        LOG.info("method clearAllCurrentTaxLotRecords() exited.");
    }
    
    /**
     * test the method appendNewMonthEndDate() in the impl class
     * @see org.kuali.kfs.module.endow.batch.service.impl.UpdateHistoryCashServiceImpl#appendNewMonthEndDate(java.util.Date)
     */
    public final void testAppendNewMonthEndDate() {
        LOG.info("method testAppendNewMonthEndDate() entered.");
        
        //prepare test current date and set it as system parameter.
        String currentDateForSystemParameter = setCurrentDate("0701");
        setSystemParameters("Y", currentDateForSystemParameter);
        
        Date currentDate = kemService.getCurrentDate();
        KualiInteger monthEndDateId = updateHistoryCashService.appendNewMonthEndDate(currentDate);
        
        Map primaryKeyValues = new HashMap(); 
        primaryKeyValues.put(EndowPropertyConstants.MONTH_END_DATE_ID, monthEndDateId);
        
        MonthEndDate newMonthEndDate  = (MonthEndDate) businessObjectService.findByPrimaryKey(MonthEndDate.class, primaryKeyValues);
        
        assertTrue("Record in Month End Date table is not added.", newMonthEndDate.getMonthEndDateId().equals(monthEndDateId));

        LOG.info("method testAppendNewMonthEndDate() exited.");
    }
    
   /**
    * test the method appendNewHistoricalCashRecords in the impl class to add the 
    * current cash record to cash history table.
    * @see org.kuali.kfs.module.endow.batch.service.impl.UpdateHistoryCashServiceImpl#appendNewHistoricalCashRecords(KualiInteger)
    */
    public final void testAppendNewHistoricalCashRecords() {
        LOG.info("method testAppendNewHistoricalCashRecords() entered.");
        
        //prepare test current date and set it as system parameter.
        String currentDateForSystemParameter = setCurrentDate("0701");
        setSystemParameters("Y", currentDateForSystemParameter);
        
        Date currentDate = kemService.getCurrentDate();
        KualiInteger monthEndDateId = updateHistoryCashService.appendNewMonthEndDate(currentDate);
        
        Map primaryKeyValues = new HashMap(); 
        primaryKeyValues.put(EndowPropertyConstants.MONTH_END_DATE_ID, monthEndDateId);
        
        MonthEndDate newMonthEndDate  = (MonthEndDate) businessObjectService.findByPrimaryKey(MonthEndDate.class, primaryKeyValues);
        
        boolean addedRecord = updateHistoryCashService.appendNewHistoricalCashRecords(monthEndDateId);
        
        primaryKeyValues.clear();
        primaryKeyValues.put(EndowPropertyConstants.KEMID, kemidCurrentCash.getKemid());
        primaryKeyValues.put(EndowPropertyConstants.MONTH_END_DATE_ID, monthEndDateId);
        KemidHistoricalCash newKemidHistoricalCash = (KemidHistoricalCash) businessObjectService.findByPrimaryKey(KemidHistoricalCash.class, primaryKeyValues);
        
        assertTrue("Record to Historical Cash table is not added.", newKemidHistoricalCash.getKemid().equals(kemidCurrentCash.getKemid()));

        LOG.info("method testAppendNewHistoricalCashRecords() exited.");
    }
    
    /**
     * test the main method updateHistoryCash() in the impl class
     * @see org.kuali.kfs.module.endow.batch.service.impl.UpdateHistoryCashServiceImpl#updateHistoryCash()
     */
    public final void testUpdateHistoryCash() {
        LOG.info("method testUpdateHistoryCash() entered.");
        
        //prepare test current date and set it as system parameter.
        String currentDateForSystemParameter = setCurrentDate("0731");
        setSystemParameters("Y", currentDateForSystemParameter);
        
        assertTrue("Update History Cash did not run.", updateHistoryCashService.updateHistoryCash());

        LOG.info("method testUpdateHistoryCash() exited.");
    }
}
