/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kuali.core.service.ConfigurableDateService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.TestUtils;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.impl.ScrubberProcess;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class RunDateServiceTest extends KualiTestBase {

    protected static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    
    protected RunDateService runDateService;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        runDateService = SpringContext.getBean(RunDateService.class);
    }
    
    public void testCalculateCutoff() throws Exception {
        // cutoff time should be set to 10am in the master data source, see FS_PARM_T, script name GL.SCRUBBER, param name SCRUBBER_CUTOFF_TIME
        // https://test.kuali.org/confluence/display/KFSP1/Scrubber+cutoff+time+configuration
        
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
    
    public void testCalculateCutoffDuringMidnightHour() throws Exception {
        TestUtils.setSystemParameter(GLConstants.GL_NAMESPACE,GLConstants.Components.SCRUBBER_STEP, GLConstants.GlScrubberGroupParameters.SCRUBBER_CUTOFF_TIME,
                "0:05:00", false, false);
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
