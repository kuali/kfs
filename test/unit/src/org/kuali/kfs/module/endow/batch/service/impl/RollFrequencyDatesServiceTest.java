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

import java.sql.Date;
import java.util.Calendar;

import org.kuali.kfs.module.endow.batch.service.RollFrequencyDatesService;
import org.kuali.kfs.module.endow.document.service.FrequencyCodeService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext(session = khuntley)
public class RollFrequencyDatesServiceTest extends KualiTestBase {

    protected RollFrequencyDatesService rollFrequencyDatesService;
    protected FrequencyCodeService frequencyCodeService;
    protected KEMService kemService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception { 
    
        // Initialize service objects.
        rollFrequencyDatesService = SpringContext.getBean(RollFrequencyDatesService.class);
        frequencyCodeService = SpringContext.getBean(FrequencyCodeService.class);
        kemService = SpringContext.getBean(KEMService.class);
                        
        super.setUp();
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * check the invalid frequency code
     */
    public void testGetNextDueDate_InvalidFrequencyCode() {
        Date currentDate = kemService.getCurrentDate();
        assertNull("K is not a valid frequency code.", frequencyCodeService.calculateNextDueDate("K", currentDate));
    }
    
    /**
     * check the next due date 
     */
    public void testGetNextDueDate_ValidNextDueDate() {
        Calendar cal = Calendar.getInstance();  // target date - 2012 is a leap year
        cal.set(2012,Calendar.FEBRUARY,28);
        Calendar cal2 = Calendar.getInstance(); // expected date        
        cal2.set(2012,Calendar.FEBRUARY,29);
        assertEquals(new java.sql.Date(cal2.getTimeInMillis()), frequencyCodeService.calculateNextDueDate("D", new java.sql.Date(cal.getTimeInMillis())));        
    }
    
    /**
     * Tests  
     */
    public void testRollFrequencyDatesService() {
        
        //assertTrue(rollFrequencyDatesService.updateFrequencyDate());        
    }
}
