/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.Calendar;

import org.kuali.kfs.module.endow.batch.service.RollFrequencyDatesService;
import org.kuali.kfs.module.endow.document.service.FrequencyDatesService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.fixture.RollFrequencyCodeFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext(session = khuntley)
public class RollFrequencyDatesServiceTest extends KualiTestBase {

    protected RollFrequencyDatesService rollFrequencyDatesService;
    protected FrequencyDatesService frequencyDatesService;
    protected KEMService kemService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception { 
    
        // Initialize service objects.
        frequencyDatesService = SpringContext.getBean(FrequencyDatesService.class);
        rollFrequencyDatesService = SpringContext.getBean(RollFrequencyDatesService.class);        
        kemService = SpringContext.getBean(KEMService.class);
                        
        super.setUp();
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

    public void testCalculateNextDueDate_InvalidFrequency() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.INVALID_FREQUENCY_CODE;
        Calendar cal = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        assertNull(frequencyCode + " must be invalid", frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(cal.getTimeInMillis())));        
    }
    
    /**
     * checks the next due date for daily code 
     */
    public void testCalculateNextDueDate_Daily() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.DAILY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalednar = RollFrequencyCodeFixture.DAILY_EXPECTED_DATE.getCalendar();
        assertEquals(new java.sql.Date(expectedCalednar.getTimeInMillis()), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
  
    /**
     * checks the next due date for weekly code 
     */
    public void testCalculateNextDueDate_Weekly() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.WEEKLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalednar = RollFrequencyCodeFixture.WEEKLY_EXPECTED_DATE.getCalendar();
        assertEquals(new java.sql.Date(expectedCalednar.getTimeInMillis()), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
 
    /**
     * checks the next due date for semi-monthly code 
     */
    public void testCalculateNextDueDate_SemiMonthly() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.SEMI_MONTHLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalednar = RollFrequencyCodeFixture.SEMI_MONTHLY_EXPECTED_DATE.getCalendar();
        assertEquals(new java.sql.Date(expectedCalednar.getTimeInMillis()), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
    
    /**
     * checks the next due date for monthly code 
     */
    public void testCalculateNextDueDate_Monthly() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.MONTHLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalednar = RollFrequencyCodeFixture.MONTHLY_EXPECTED_DATE.getCalendar();
        assertEquals(new java.sql.Date(expectedCalednar.getTimeInMillis()), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }

    /**
     * checks the next due date for quarterly code 
     */
    public void testCalculateNextDueDate_Quarterly() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.QUARTERLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalednar = RollFrequencyCodeFixture.QUARTERLY_EXPECTED_DATE.getCalendar();
        assertEquals(new java.sql.Date(expectedCalednar.getTimeInMillis()), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
    
    /**
     * checks the next due date for semi-annual code 
     */
    public void testCalculateNextDueDate_SemiAnnually() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.SEMI_ANNUALLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalednar = RollFrequencyCodeFixture.SEMI_ANNUALLY_EXPECTED_DATE.getCalendar();
        assertEquals(new java.sql.Date(expectedCalednar.getTimeInMillis()), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
    
    /**
     * checks the next due date for annual code 
     */
    public void testCalculateNextDueDate_Annually() {
        RollFrequencyCodeFixture rollFrequencyCodeFixture = RollFrequencyCodeFixture.ANNUALLY_TARGET_DATE;
        Calendar targetCalendar = rollFrequencyCodeFixture.getCalendar();
        String frequencyCode = rollFrequencyCodeFixture.getFrequencyCode();
        Calendar expectedCalednar = RollFrequencyCodeFixture.ANNUALLY_EXPECTED_DATE.getCalendar();
        assertEquals(new java.sql.Date(expectedCalednar.getTimeInMillis()), frequencyDatesService.calculateNextDueDate(frequencyCode, new java.sql.Date(targetCalendar.getTimeInMillis())));        
    }
    
    /**
     * tests RollFrequencyDatesService
     */
    public void testRollFrequencyDatesService() {        
        //assertTrue(rollFrequencyDatesService.updateFrequencyDate());        
    }
}
