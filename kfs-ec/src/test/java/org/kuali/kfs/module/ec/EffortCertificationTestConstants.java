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
package org.kuali.kfs.module.ec;

/**
 * Contains constants that are used in effort certification test cases
 */
public class EffortCertificationTestConstants {

    /**
     * Contains fiscal period values used in Effort Certification junit tests
     */
    public enum EffortCertificationFiscalPeriod {
        ONE("01"), TWO("02"), THREE("03"), FOUR("04"), FIVE("05"), SIX("06"), SEVEN("07"), EIGHT("08"), NINE("09"), TEN("10"), ELEVEN("11"), TWELVE("12");

        private String fiscalPeriod;

        private EffortCertificationFiscalPeriod(String fiscalPeriod) {
            this.fiscalPeriod = fiscalPeriod;
        }

        public String getFiscalPeriod() {
            return this.fiscalPeriod;
        }
    }

    /**
     * contains university fiscal year values used in Effort Certification junit tests
     */
    public enum EffortCertificationUniversityFiscalYear {
        YEAR_1990(1990), YEAR_1999(1999), YEAR_2000(2000), YEAR_2001(2001);
        private Integer year;

        private EffortCertificationUniversityFiscalYear(Integer year) {
            this.year = year;
        }

        public Integer getUniversityFiscalYear() {
            return this.year;
        }

    }

    /**
     * contains effort certification report type values that are used in effort certification junit tests
     */
    public enum EffortCertificationReportType {
        REPORT_TYPE_VALID("10"), REPORT_TYPE_INVALID("XX");

        private String reportType;

        private EffortCertificationReportType(String reportType) {
            this.reportType = reportType;
        }

        public String getReportType() {
            return this.reportType;
        }

    }

    /**
     * valid report number for effort certification junit tests
     */
    public static final String REPORT_NUMBER = "A01";


}
