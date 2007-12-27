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

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.EffortCertificationTestConstants;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.EffortCertificationReportDefinitionFixture;

@ConfigureContext
public class EffortCertificationAutomaticReportPeriodUpdateServiceTest extends KualiTestBase {
    
    private EffortCertificationAutomaticReportPeriodUpdateService reportDefinitionService;
    private BusinessObjectService businessObjectService;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        reportDefinitionService = SpringContext.getBean(EffortCertificationAutomaticReportPeriodUpdateService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        List<EffortCertificationReportDefinition> reportDefinitions = reportDefinitionService.getAllReportDefinitions();
        for (EffortCertificationReportDefinition record : reportDefinitions) {
            businessObjectService.delete(record);
        }
    }
    
    /**
     * Tests report defintions with overlapping periods. Service method should return true.
     */
    public void testIsAnOverlappingReportDefinition_overlappingPeriods1() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_1.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_1_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        businessObjectService.save(control);
        assertTrue("report definition 'test' is expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
    
    /**
     * Tests report definitions
     */
    public void testIsAnOverlappingReportDefinition_overlappingPeriods2() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_3.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_3_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        businessObjectService.save(control);
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
    
    /**
     * This method...
     */
    public void testIsAnOverlappingReportDefinition_noOverlappingDates() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_2.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_2_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        businessObjectService.save(control);
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
    
    public void testIsAnOverlappingReportDefinition_boundry1() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_4.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_4_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        businessObjectService.save(control);
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
    
    public void testIsAnOverlappingReportDefinition_boundry2() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_5.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_5_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        businessObjectService.save(control);
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
    
    public void testIsAnOverlappingReportDefinition_boundry3() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_6.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_6_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        businessObjectService.save(control);
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
    
    public void testIsAnOverlappingReportDefinition_overlappingDates1() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_7.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_7_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        businessObjectService.save(control);
        assertTrue("report definition 'test' is expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
    
    public void testIsAnOverlappingReportDefinition_dateBoundry() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_8.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test= EffortCertificationReportDefinitionFixture.TEST_8_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        businessObjectService.save(control);
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
    public void testIsAnOverlappingReportDefinition_multipleRecords() {
        EffortCertificationReportDefinition control1 = EffortCertificationReportDefinitionFixture.CONTROL_9_1.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition control2 = EffortCertificationReportDefinitionFixture.CONTROL_9_2.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition control3 = EffortCertificationReportDefinitionFixture.CONTROL_9_3.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test= EffortCertificationReportDefinitionFixture.TEST_9_OVERLAP.createEffortCertificationReportDefinition();
        
        control1.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        businessObjectService.save(control1);
        control2.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_2000.getUniversityFiscalYear());
        businessObjectService.save(control2);
        control3.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_2001.getUniversityFiscalYear());
        businessObjectService.save(control3);
        
        assertTrue("report definition 'test' is expected to be an overlapping record", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
    
    public void testIsAnOverlappingReportDefinition_inactiveRecordTest() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_7.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_7_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        control.setActive(false);
        businessObjectService.save(control);
        
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control' because 'control' is inactive", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }
}
