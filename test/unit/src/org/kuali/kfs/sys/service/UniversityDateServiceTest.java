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
package org.kuali.module.financial.service;

import java.util.Calendar;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
public class UniversityDateServiceTest extends KualiTestBase {

    public final void testGetCurrentFiscalYear() {
        int currentFiscalYearAccordingToUniversityDateService = SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear();
        Calendar today = SpringServiceLocator.getDateTimeService().getCurrentCalendar();
        int currentFiscalYearAccordingToTest = today.get(Calendar.YEAR); 
        if (today.get(Calendar.MONTH) >= Calendar.JULY) {
            currentFiscalYearAccordingToTest++;
        }
        assertEquals("Test expected: " + currentFiscalYearAccordingToTest + ", but UniversityDateService said: " + currentFiscalYearAccordingToUniversityDateService, currentFiscalYearAccordingToTest, currentFiscalYearAccordingToUniversityDateService);
    }

    public final void testGetFiscalYear_nullDate() {
        boolean failedAsExpected = false;

        try {
            SpringServiceLocator.getUniversityDateService().getFiscalYear(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetFiscalYear_pastDate() throws Exception {
        java.sql.Timestamp badTimestamp = SpringServiceLocator.getDateTimeService().convertToSqlTimestamp("01/10/1989 12:00 AM");
        java.sql.Timestamp goodTimestamp = SpringServiceLocator.getDateTimeService().convertToSqlTimestamp("08/19/1993 12:00 AM");

        assertNull("This date shouldn't be in sh_univ_date_t", SpringServiceLocator.getUniversityDateService().getFiscalYear(badTimestamp));
        assertEquals("This date should be in sh_univ_date_t", new Integer(1994), SpringServiceLocator.getUniversityDateService().getFiscalYear(goodTimestamp));
    }
    
}
