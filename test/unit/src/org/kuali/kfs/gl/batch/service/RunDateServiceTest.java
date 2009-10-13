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
package org.kuali.kfs.gl.batch.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;

/**
 * Tests the cutoff time functionality of RunDateService
 */
@ConfigureContext
public class RunDateServiceTest extends KualiTestBase {

    protected static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    protected RunDateService runDateService;

    /**
     * Initializes the RunDateService implementation to test
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        runDateService = SpringContext.getBean(RunDateService.class);
    }

    /**
     * Tests that the cutoff time is parsed correct and tests several times to see where they lie
     * against the cut off time
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCalculateCutoff() throws Exception {
        // cutoff time should be set to 10am in the master data source, see FS_PARM_T, script name GL.SCRUBBER, param name
        // SCRUBBER_CUTOFF_TIME
        // KFSP1/Scrubber+cutoff+time+configuration

        Map<String, String> expectedCurrentToRunTimeMappings = new LinkedHashMap<String, String>();

        // assuming cutoff time of 10am in this code
        expectedCurrentToRunTimeMappings.put("6/1/2006 10:35:00", "6/1/2006 10:35:00");
        expectedCurrentToRunTimeMappings.put("3/1/2006 9:59:00", "2/28/2006 23:59:59");
        expectedCurrentToRunTimeMappings.put("3/1/2004 9:59:00", "2/29/2004 23:59:59");
        expectedCurrentToRunTimeMappings.put("4/1/2004 1:59:00", "3/31/2004 23:59:59");
        expectedCurrentToRunTimeMappings.put("9/21/2005 19:13:14", "9/21/2005 19:13:14");
        expectedCurrentToRunTimeMappings.put("1/1/2009 9:59:14", "12/31/2008 23:59:59");
        expectedCurrentToRunTimeMappings.put("5/12/2009 21:59:14", "5/12/2009 21:59:14");
        expectedCurrentToRunTimeMappings.put("5/12/2050 21:59:14", "5/12/2050 21:59:14");
        // 2100 is not a leap year
        expectedCurrentToRunTimeMappings.put("3/1/2100 9:59:00", "2/28/2100 23:59:59");
        expectedCurrentToRunTimeMappings.put("3/1/2104 9:59:00", "2/29/2104 23:59:59");


        DateFormat parser = new SimpleDateFormat(DATE_FORMAT);
        for (Entry<String, String> entry : expectedCurrentToRunTimeMappings.entrySet()) {
            Date calculatedRunTime = runDateService.calculateRunDate(parser.parse(entry.getKey()));
            assertTrue(entry.getKey() + " " + entry.getValue() + " " + calculatedRunTime, parser.parse(entry.getValue()).equals(calculatedRunTime));
        }
    }

    /**
     * Tests an edge case where the cutoff time is at midnight
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCalculateCutoffDuringMidnightHour() throws Exception {
        TestUtils.setSystemParameter(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.SCRUBBER_CUTOFF_TIME, "0:05:00");
        Map<String, String> expectedCurrentToRunTimeMappings = new LinkedHashMap<String, String>();

        expectedCurrentToRunTimeMappings.put("6/1/2006 0:05:00", "6/1/2006 0:05:00");
        expectedCurrentToRunTimeMappings.put("3/1/2006 0:02:33", "2/28/2006 23:59:59");

        DateFormat parser = new SimpleDateFormat(DATE_FORMAT);
        for (Entry<String, String> entry : expectedCurrentToRunTimeMappings.entrySet()) {
            Date calculatedRunTime = runDateService.calculateRunDate(parser.parse(entry.getKey()));
            assertTrue(entry.getKey() + " " + entry.getValue() + " " + calculatedRunTime, parser.parse(entry.getValue()).equals(calculatedRunTime));
        }
    }
}
