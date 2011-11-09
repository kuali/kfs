/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.service;

import java.util.Calendar;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

@ConfigureContext
public class UniversityDateServiceTest extends KualiTestBase {

    public final void testGetCurrentFiscalYear() {
        int currentFiscalYearAccordingToUniversityDateService = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        Calendar today = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        int currentFiscalYearAccordingToTest = today.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) >= Calendar.JULY) {
            currentFiscalYearAccordingToTest++;
        }
        assertEquals("Test expected: " + currentFiscalYearAccordingToTest + ", but UniversityDateService said: " + currentFiscalYearAccordingToUniversityDateService, currentFiscalYearAccordingToTest, currentFiscalYearAccordingToUniversityDateService);
    }

    public final void testGetFiscalYear_nullDate() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(UniversityDateService.class).getFiscalYear(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

}
