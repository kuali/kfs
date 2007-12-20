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
package org.kuali.test.fixtures;

import org.kuali.module.effort.bo.EffortCertificationReportDefinition;

public enum EffortCertificationReportDefinitionFixture {
    
    CONTROL_1(2008, 2008, "1", "12"),
    TEST_1_OVERLAP(2008, 2008, "2", "4"), 
    
    CONTROL_2(2002, 2003, "1", "12"),
    TEST_2_NO_OVERLAP(2000, 2001, "1", "12"),
    
    CONTROL_3(2008, 2008, "4", "7"),
    TEST_3_NO_OVERLAP(2008, 2008, "2", "4"),
    
    CONTROL_4(2008, 2008, "4", "7"),
    TEST_4_NO_OVERLAP(2008, 2008, "1", "3"),
    
    CONTROL_5(2008, 2008, "4", "7"),
    TEST_5_NO_OVERLAP(2008, 2008, "7", "9"), 
    
    CONTROL_6(2008, 2008, "4", "7"),
    TEST_6_NO_OVERLAP(2008, 2008, "8", "10"), 
    
    CONTROL_7(2002, 2004, "1", "2"),
    TEST_7_OVERLAP(2001, 2003, "1", "2"),
    
    CONTROL_8(2002, 2004, "1", "2"),
    TEST_8_NO_OVERLAP(2002, 2002, "1", "1");
    
    private Integer startDate;
    private Integer endDate;
    private String startPeriod;
    private String endPeriod;
    
    private EffortCertificationReportDefinitionFixture(Integer startDate, Integer endDate, String startPeriod, String endPeriod) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.endPeriod = endPeriod;
        this.startPeriod = startPeriod;
    }
    
    public EffortCertificationReportDefinition createEffortCertificationReportDefinition() {
        EffortCertificationReportDefinition report = new EffortCertificationReportDefinition();
        
        report.setEffortCertificationReportTypeCode("10");
        report.setEffortCertificationReportBeginFiscalYear(this.startDate);
        report.setEffortCertificationReportEndFiscalYear(this.endDate);
        report.setEffortCertificationReportBeginPeriodCode(this.startPeriod);
        report.setEffortCertificationReportEndPeriodCode(this.endPeriod);
        report.setUniversityFiscalYear(1990);
        report.setEffortCertificationReportNumber("ZZZ");
        
        return report;
    }
}
