/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ec.fixture;

import static org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod.EIGHT;
import static org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod.FOUR;
import static org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod.NINE;
import static org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod.ONE;
import static org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod.SEVEN;
import static org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod.TEN;
import static org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod.THREE;
import static org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod.TWELVE;
import static org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod.TWO;

import org.kuali.kfs.module.ec.EffortCertificationTestConstants;
import org.kuali.kfs.module.ec.EffortCertificationTestConstants.EffortCertificationFiscalPeriod;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;

/**
 * Encapsulates test data for EffortCertificationAutomaticReportPeriodUpdateServiceTest
 */
public enum EffortCertificationReportDefinitionFixture {

    CONTROL_1(2008, 2008, ONE, TWELVE), TEST_1_OVERLAP(2008, 2008, TWO, FOUR),
    
    CONTROL_2(2002, 2003, ONE, TWELVE), TEST_2_NO_OVERLAP(2000, 2001, ONE, TWELVE),

    CONTROL_3(2000, 2001, ONE, TWELVE), TEST_3_NO_OVERLAP(2002, 2003, ONE, TWELVE),

    CONTROL_4(2008, 2008, FOUR, SEVEN), TEST_4_NO_OVERLAP(2008, 2008, ONE, THREE),

    CONTROL_5(2008, 2008, FOUR, SEVEN), TEST_5_NO_OVERLAP(2008, 2008, EIGHT, NINE),

    CONTROL_6(2008, 2008, FOUR, SEVEN), TEST_6_NO_OVERLAP(2008, 2008, EIGHT, TEN),

    CONTROL_7(2002, 2004, ONE, TWO), TEST_7_OVERLAP(2001, 2003, ONE, TWO),

    CONTROL_8(2002, 2004, TWO, TWO), TEST_8_NO_OVERLAP(2002, 2002, ONE, ONE),

    CONTROL_9_1(2002, 2003, ONE, TWELVE), 
    CONTROL_9_2(2003, 2004, ONE, TWELVE), 
    CONTROL_9_3(2004, 2005, ONE, TWELVE), TEST_9_OVERLAP(2002, 2005, ONE, TWELVE);

    private Integer startDate;
    private Integer endDate;
    private String startPeriod;
    private String endPeriod;

    /**
     * Constructs a EffortCertificationReportDefinitionFixture.java.
     * 
     * @param startDate
     * @param endDate
     * @param startPeriod
     * @param endPeriod
     */
    private EffortCertificationReportDefinitionFixture(Integer startDate, Integer endDate, EffortCertificationFiscalPeriod startPeriod, EffortCertificationFiscalPeriod endPeriod) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.endPeriod = endPeriod.getFiscalPeriod();
        this.startPeriod = startPeriod.getFiscalPeriod();
    }

    /**
     * Creates EffortCertificationReportDefinitions based on the criteria of a particular enum.
     * 
     * @return EffortCertificationReportDefinition
     */
    public EffortCertificationReportDefinition createEffortCertificationReportDefinition() {
        EffortCertificationReportDefinition report = new EffortCertificationReportDefinition();

        report.setEffortCertificationReportTypeCode(EffortCertificationTestConstants.EffortCertificationReportType.REPORT_TYPE_VALID.getReportType());
        report.setEffortCertificationReportBeginFiscalYear(this.startDate);
        report.setEffortCertificationReportEndFiscalYear(this.endDate);
        report.setEffortCertificationReportBeginPeriodCode(this.startPeriod);
        report.setEffortCertificationReportEndPeriodCode(this.endPeriod);
        report.setUniversityFiscalYear(EffortCertificationTestConstants.EffortCertificationUniversityFiscalYear.YEAR_1990.getUniversityFiscalYear());
        report.setEffortCertificationReportNumber(EffortCertificationTestConstants.REPORT_NUMBER);
        report.setActive(true);

        return report;
    }
}
