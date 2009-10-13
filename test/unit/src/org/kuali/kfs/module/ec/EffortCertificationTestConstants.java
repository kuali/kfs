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
