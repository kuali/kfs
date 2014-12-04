/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import java.util.Calendar;

import org.kuali.kfs.module.endow.document.service.FrequencyDatesService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.fixture.RollFrequencyCodeFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;

@ConfigureContext(session = kfs)
public class RollFrequencyDatesServiceImplTest extends KualiTestBase {

    protected RollFrequencyDatesServiceImpl rollFrequencyDatesService;
    protected FrequencyDatesService frequencyDatesService;
    protected KEMService kemService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {        
        super.setUp();
        
        // Initialize service objects.
        frequencyDatesService = SpringContext.getBean(FrequencyDatesService.class);
        rollFrequencyDatesService = (RollFrequencyDatesServiceImpl) TestUtils.getUnproxiedService("mockRollFrequencyDatesService");       
        kemService = SpringContext.getBean(KEMService.class);
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        frequencyDatesService = null;
        rollFrequencyDatesService = null;        
        kemService = null;
        super.tearDown();
    }

    /**
     * validates the frequency code
     */
    public void testCalculateNextDueDate_InvalidFrequency() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.INVALID_FREQUENCY_CODE;
        Calendar cal = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        assertNull(frequencyCode + " must be invalid", frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(cal.getTimeInMillis())));        
    }
    
    /**
     * validates the next due date for daily code 
     */
    public void testCalculateNextDueDate_Daily() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.DAILY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalendar = RollFrequencyCodeFixture.DAILY_EXPECTED_DATE.getCalendar();
        assertEquals("The calculation of the next daily date is incorrect.", expectedCalendar.getTime(), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));
    }
  
    /**
     * validates the next due date for weekly code 
     */
    public void testCalculateNextDueDate_Weekly() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.WEEKLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalendar = RollFrequencyCodeFixture.WEEKLY_EXPECTED_DATE.getCalendar();
        assertEquals("The calculation of the next weekly date is incorrect.", expectedCalendar.getTime(), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
 
    /**
     * validates the next due date for semi-monthly code 
     */
    public void testCalculateNextDueDate_SemiMonthly() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.SEMI_MONTHLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalendar = RollFrequencyCodeFixture.SEMI_MONTHLY_EXPECTED_DATE.getCalendar();
        assertEquals("The calculation of the next semi-monthly date is incorrect.", expectedCalendar.getTime(), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
    
    /**
     * validates the next due date for monthly code 
     */
    public void testCalculateNextDueDate_Monthly() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.MONTHLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalendar = RollFrequencyCodeFixture.MONTHLY_EXPECTED_DATE.getCalendar();
        assertEquals("The calculation of the next monthly date is incorrect.", expectedCalendar.getTime(), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }

    /**
     * validates the next due date for quarterly code 
     */
    public void testCalculateNextDueDate_Quarterly() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.QUARTERLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalendar = RollFrequencyCodeFixture.QUARTERLY_EXPECTED_DATE.getCalendar();
        assertEquals("The calculation of the next quarterly date is incorrect.", expectedCalendar.getTime(), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
    
    /**
     * validates the next due date for semi-annual code 
     */
    public void testCalculateNextDueDate_SemiAnnually() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.SEMI_ANNUALLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalendar = RollFrequencyCodeFixture.SEMI_ANNUALLY_EXPECTED_DATE.getCalendar();
        assertEquals("The calculation of the next semi-annual date is incorrect.", expectedCalendar.getTime(), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
    
    /**
     * validates the next due date for annual code 
     */
    public void testCalculateNextDueDate_Annually() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.ANNUALLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalendar = RollFrequencyCodeFixture.ANNUALLY_EXPECTED_DATE.getCalendar();
        assertEquals("The calculation of the next annual date is incorrect.", expectedCalendar.getTime(), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
    
    /**
     * tests RollFrequencyDatesService#updateSecurityIncomeNextPayDates
     */
    public void testRollFrequencyDatesService_updateSecurityIncomeNextPayDates() {        
        assertTrue("updateSecurityIncomeNextPayDates() failed.", rollFrequencyDatesService.updateSecurityIncomeNextPayDates());        
    }

    /**
     * tests RollFrequencyDatesService#updateTicklerNextDueDates
     */   
    public void testRollFrequencyDatesService_updateTicklerNextDueDates() {        
        assertTrue("updateTicklerNextDueDates() failed.", rollFrequencyDatesService.updateTicklerNextDueDates());        
    }
    
    /**
     * tests RollFrequencyDatesService#updateFeeMethodProcessDates
     */
    public void testRollFrequencyDatesService_updateFeeMethodProcessDates() {        
        assertTrue("updateFeeMethodProcessDates failed.", rollFrequencyDatesService.updateFeeMethodProcessDates());        
    }
    
    /**
     * tests RollFrequencyDatesService#updateRecurringCashTransferProcessDates
     */
    public void testRollFrequencyDatesService_updateRecurringCashTransferProcessDates() {        
        assertTrue("updateRecurringCashTransferProcessDates failed.", rollFrequencyDatesService.updateRecurringCashTransferProcessDates());        
    }
    
    /**
     * tests RollFrequencyDatesService#updateCashSweepModelNextDueDates
     */
    public void testRollFrequencyDatesService_updateCashSweepModelNextDueDates() {        
        assertTrue("updateCashSweepModelNextDueDates() failed.", rollFrequencyDatesService.updateCashSweepModelNextDueDates());        
    }
    
    /**
     * tests RollFrequencyDatesService#updateAutomatedCashInvestmentModelNextDueDates
     */
    public void testRollFrequencyDatesService_updateAutomatedCashInvestmentModelNextDueDates() {        
        assertTrue("updateAutomatedCashInvestmentModelNextDueDates failed.", rollFrequencyDatesService.updateAutomatedCashInvestmentModelNextDueDates());        
    }
}
