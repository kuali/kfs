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
package org.kuali.module.effort.service;

import java.util.List;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.EffortCertificationReportDefinitionFixture;

@ConfigureContext
public class EffortCertificationAutomaticReportPeriodUpdateServiceTest extends KualiTestBase {
    
    private EffortCertificationAutomaticReportPeriodUpdateService reportDefinitionService;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        reportDefinitionService = SpringContext.getBean(EffortCertificationAutomaticReportPeriodUpdateService.class);
        List<EffortCertificationReportDefinition> reportDefinitions = reportDefinitionService.getAllReportDefinitions();
        for (EffortCertificationReportDefinition record : reportDefinitions) {
            reportDefinitionService.deleteReportDefinition(record);
        }
    }
    
    public void testIsAnOverlappingReportDefinition() {
        //first test case
        EffortCertificationReportDefinition control1 = EffortCertificationReportDefinitionFixture.CONTROL_1.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test1 = EffortCertificationReportDefinitionFixture.TEST_1_OVERLAP.createEffortCertificationReportDefinition();
        control1.setUniversityFiscalYear(1999);
        reportDefinitionService.addReportDefinition(control1);
        assertTrue(reportDefinitionService.isAnOverlappingReportDefinition(test1));
        reportDefinitionService.deleteReportDefinition(control1);
        
        //second test case
        EffortCertificationReportDefinition control2 = EffortCertificationReportDefinitionFixture.CONTROL_2.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test2 = EffortCertificationReportDefinitionFixture.TEST_2_NO_OVERLAP.createEffortCertificationReportDefinition();
        reportDefinitionService.addReportDefinition(control2);
        assertFalse(reportDefinitionService.isAnOverlappingReportDefinition(test2));
        reportDefinitionService.deleteReportDefinition(control2);
        
        //third test case
        EffortCertificationReportDefinition control3 = EffortCertificationReportDefinitionFixture.CONTROL_3.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test3 = EffortCertificationReportDefinitionFixture.TEST_3_NO_OVERLAP.createEffortCertificationReportDefinition();
        reportDefinitionService.addReportDefinition(control3);
        assertFalse(reportDefinitionService.isAnOverlappingReportDefinition(test3));
        reportDefinitionService.deleteReportDefinition(control3);
        
        //fourth test case
        EffortCertificationReportDefinition control4 = EffortCertificationReportDefinitionFixture.CONTROL_4.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test4 = EffortCertificationReportDefinitionFixture.TEST_4_NO_OVERLAP.createEffortCertificationReportDefinition();
        reportDefinitionService.addReportDefinition(control4);
        assertFalse(reportDefinitionService.isAnOverlappingReportDefinition(test4));
        reportDefinitionService.deleteReportDefinition(control4);
        
        //fifth test case
        EffortCertificationReportDefinition control5 = EffortCertificationReportDefinitionFixture.CONTROL_5.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test5 = EffortCertificationReportDefinitionFixture.TEST_5_NO_OVERLAP.createEffortCertificationReportDefinition();
        reportDefinitionService.addReportDefinition(control5);
        assertFalse(reportDefinitionService.isAnOverlappingReportDefinition(test5));
        reportDefinitionService.deleteReportDefinition(control5);
        
        //sixth test case
        EffortCertificationReportDefinition control6 = EffortCertificationReportDefinitionFixture.CONTROL_6.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test6 = EffortCertificationReportDefinitionFixture.TEST_6_NO_OVERLAP.createEffortCertificationReportDefinition();
        reportDefinitionService.addReportDefinition(control6);
        assertFalse(reportDefinitionService.isAnOverlappingReportDefinition(test6));
        reportDefinitionService.deleteReportDefinition(control6);
        
        //seventh test case
        EffortCertificationReportDefinition control7 = EffortCertificationReportDefinitionFixture.CONTROL_7.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test7 = EffortCertificationReportDefinitionFixture.TEST_7_NO_OVERLAP.createEffortCertificationReportDefinition();
        reportDefinitionService.addReportDefinition(control7);
        assertFalse(reportDefinitionService.isAnOverlappingReportDefinition(test7));
        reportDefinitionService.deleteReportDefinition(control7);
    }
}
