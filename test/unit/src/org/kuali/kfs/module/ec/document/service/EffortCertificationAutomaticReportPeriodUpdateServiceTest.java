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
package org.kuali.kfs.module.ec.document.service;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ec.EffortCertificationTestConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.dataaccess.MockEffortCertificationReportDefinitionDaoOjb;
import org.kuali.kfs.module.ec.fixture.EffortCertificationReportDefinitionFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Contains methods that test the EffortCertificationAutomaticReportPeriodUpdateService.
 */
@ConfigureContext
public class EffortCertificationAutomaticReportPeriodUpdateServiceTest extends KualiTestBase {

    private EffortCertificationAutomaticReportPeriodUpdateService reportDefinitionService;
    private BusinessObjectService businessObjectService;
    private MockEffortCertificationReportDefinitionDaoOjb mockDao;
    private List<EffortCertificationReportDefinition> testReportDefinitions;

    /**
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockDao = new MockEffortCertificationReportDefinitionDaoOjb();
        reportDefinitionService = SpringContext.getBean(EffortCertificationAutomaticReportPeriodUpdateService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        reportDefinitionService.setEffortCertificationReportDefinitionDao(mockDao);
    }

    /**
     * Tests report defintions with overlapping periods. Service method should return true.
     */
    public void testIsAnOverlappingReportDefinition_overlappingPeriods1() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_1.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_1_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        assertTrue("report definition 'test' is expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }

    /**
     * Tests report defintions without overlapping periods. Service method should return false.
     */
    public void testIsAnOverlappingReportDefinition_overlappingPeriods2() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_3.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_3_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }

    /**
     * Tests report defintions without overlapping dates. Service method should return false.
     */
    public void testIsAnOverlappingReportDefinition_noOverlappingDates() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_2.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_2_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }

    /**
     * 
     * Tests report defintions without overlapping periods. Service method should return false.
     */
    public void testIsAnOverlappingReportDefinition_boundry1() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_4.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_4_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }

    /**
     * 
     * Tests report defintions without overlapping periods. Service method should return false.
     */
    public void testIsAnOverlappingReportDefinition_boundry2() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_5.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_5_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }

    /**
     * Tests report defintions without overlapping periods. Service method should return false.
     */
    public void testIsAnOverlappingReportDefinition_boundry3() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_6.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_6_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }

    /**
     * 
     * Tests report definitions with overlapping dates. Service method should return true.
     */
    public void testIsAnOverlappingReportDefinition_overlappingDates1() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_7.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_7_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        assertTrue("report definition 'test' is expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }

    /**
     * Tests report definitions without overlapping dates. Service method should return false.
     */
    public void testIsAnOverlappingReportDefinition_dateBoundry() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_8.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_8_NO_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control'", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }

    /**
     * Tests multiple report definitions where at least one contains an overlapping period. Service method should return true.
     */
    public void testIsAnOverlappingReportDefinition_multipleRecords() {
        EffortCertificationReportDefinition control1 = EffortCertificationReportDefinitionFixture.CONTROL_9_1.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition control2 = EffortCertificationReportDefinitionFixture.CONTROL_9_2.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition control3 = EffortCertificationReportDefinitionFixture.CONTROL_9_3.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_9_OVERLAP.createEffortCertificationReportDefinition();
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        
        control1.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        testReportDefinitions.add(control1);
        control2.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_2000.getUniversityFiscalYear());
        testReportDefinitions.add(control2);
        control3.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_2001.getUniversityFiscalYear());
        testReportDefinitions.add(control3);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        assertTrue("report definition 'test' is expected to be an overlapping record", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }

    /**
     * 
     * Tests report definition where there are no active records of that type. Service method should return false.
     */
    /*public void testIsAnOverlappingReportDefinition_inactiveRecordTest() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_7.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_7_OVERLAP.createEffortCertificationReportDefinition();
        control.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1999.getUniversityFiscalYear());
        control.setActive(false);
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        reportDefinitionService.setEffortCertificationReportDefinitionDao(mockDao);
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control' because 'control' is inactive", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }*/

    /**
     * 
     * Tests report defintion where no overlapping records exist of its report type. Service method should return false.
     */
    /*public void testIsOverlappingReportDefinition_reportTypeTest() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_7.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_7_OVERLAP.createEffortCertificationReportDefinition();
        
        test.setEffortCertificationReportTypeCode(EffortCertificationTestConstants.EffortCertificationReportType.REPORT_TYPE_INVALID.getReportType());
        
        testReportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
        testReportDefinitions.add(control);
        mockDao.setReportDefinitionList(testReportDefinitions);
        
        reportDefinitionService.setEffortCertificationReportDefinitionDao(mockDao);
        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control' because they do not have the same report type", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }*/

    /**
     * 
     * Tests that the same record is not included in the list of overlapping records (when a record is being updated). Service method should return false
     */
    /*public void testIsOverlappingReportDefinition_sameRecordTest() {
        EffortCertificationReportDefinition control = EffortCertificationReportDefinitionFixture.CONTROL_7.createEffortCertificationReportDefinition();
        EffortCertificationReportDefinition test = EffortCertificationReportDefinitionFixture.TEST_7_OVERLAP.createEffortCertificationReportDefinition();
        businessObjectService.save(control);

        assertFalse("report definition 'test' is not expected to overlap with report definintion 'control' they are the same record", reportDefinitionService.isAnOverlappingReportDefinition(test));
    }*/
}
