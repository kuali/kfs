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
package org.kuali.module.effort;

public class EffortCertificationTestConstants {
    
    public enum EffortCertificationFiscalPeriod {
        ONE("01"), 
        TWO("02"), 
        THREE("03"), 
        FOUR("04"), 
        FIVE("05"), 
        SIX("06"), 
        SEVEN("07"), 
        EIGHT("08"), 
        NINE("09"), 
        TEN("10"), 
        ELEVEN("11"), 
        TWELVE("12");
        
        private String fiscalPeriod;
        
        private EffortCertificationFiscalPeriod(String fiscalPeriod) {
            this.fiscalPeriod = fiscalPeriod;
        }
        
        public String getFiscalPeriod() {
            return this.fiscalPeriod;
        }
    }
    
    public enum EffortCertificationUniversityFiscalYear {
        YEAR_1990(1990),
        YEAR_1999(1999),
        YEAR_2000(2000),
        YEAR_2001(2001);
        private Integer year;
        
        private EffortCertificationUniversityFiscalYear(Integer year) {
            this.year = year;
        }
        
        public Integer getUniversityFiscalYear() {
            return this.year;
        }
        
    }
    
    public static final String REPORT_TYPE = "10";
    public static final String REPORT_NUMBER = "A01";
    
    
}
