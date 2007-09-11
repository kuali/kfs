/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.service;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.sql.Date;
import java.util.Calendar;

import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
public class PurapServiceTest extends KualiTestBase {
    
    private Date currentDate;
    private Date compareDate;
    private int dayOffset = 60;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        currentDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        Calendar calendar = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        calendar.add(Calendar.DATE, dayOffset);
        compareDate = new Date(calendar.getTimeInMillis());
    }
    
    @Override
    protected void tearDown() throws Exception {
        currentDate = null;
        compareDate = null;
        super.tearDown();
    }
    
    public void testIsDateMoreThanANumberOfDaysAway_ManyFewerDays() {
        int daysAway = dayOffset - 5;
        assertTrue(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }
    
    public void testIsDateMoreThanANumberOfDaysAway_OneLessDays() {
        int daysAway = dayOffset - 1;
        assertTrue(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }
    
    public void testIsDateMoreThanANumberOfDaysAway_SameNumberOfDays() {
        int daysAway = dayOffset;
        assertFalse(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }
    
    public void testIsDateMoreThanANumberOfDaysAway_OneMoreDays() {
        int daysAway = dayOffset + 1;
        assertFalse(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }
    
    public void testIsDateMoreThanANumberOfDaysAway_ManyMoreDays() {
        int daysAway = dayOffset + 5;
        assertFalse(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }
}
