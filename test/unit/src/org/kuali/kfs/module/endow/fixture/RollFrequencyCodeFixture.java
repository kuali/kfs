/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.fixture;

import java.util.Calendar;

public enum RollFrequencyCodeFixture {
    
    INVALID_FREQUENCY_CODE(2010,Calendar.FEBRUARY,28, "ZZZ"),   // "ZZZ" is invalid
    
    DAILY_TARGET_DATE(2012,Calendar.FEBRUARY,28, "D"),   // leap year
    DAILY_EXPECTED_DATE(2012,Calendar.FEBRUARY,29, "D"), // daily - D
    
    WEEKLY_TARGET_DATE(2010,Calendar.FEBRUARY,28, "WMON"),
    WEEKLY_EXPECTED_DATE(2010,Calendar.MARCH,1, "WMON"),    // weekly Monday - WMON
    
    SEMI_MONTHLY_TARGET_DATE(2012,Calendar.FEBRUARY,21, "S02"),
    SEMI_MONTHLY_EXPECTED_DATE(2012,Calendar.MARCH,2, "S02"), // semi-monthly - S10 

    MONTHLY_TARGET_DATE(2010,Calendar.FEBRUARY,28, "M12"),
    MONTHLY_EXPECTED_DATE(2010,Calendar.MARCH,12, "M12"),      // monthly - M12 

    QUARTERLY_TARGET_DATE(2010,Calendar.FEBRUARY,28, "QFME"),
    QUARTERLY_EXPECTED_DATE(2010,Calendar.MAY,31, "QFME"),     // quarterly - QFME
    
    SEMI_ANNUALLY_TARGET_DATE(2010,Calendar.MAY,28, "IM20"),
    SEMI_ANNUALLY_EXPECTED_DATE(2010,Calendar.SEPTEMBER,20, "IM20"),   // semi-annually - IM20
    
    ANNUALLY_TARGET_DATE(2010,Calendar.FEBRUARY,28, "AY31"),
    ANNUALLY_EXPECTED_DATE(2010,Calendar.MAY,31, "AY31");      // annually - AY31
    
    private Calendar cal;
    private String frequencyCcode;
    
    private RollFrequencyCodeFixture(int year, int month, int day, String frequencyCode) {
        cal = Calendar.getInstance(); 
        cal.clear();
        cal.set(year,month,day);
        this.frequencyCcode = frequencyCode;
    }
    
    public Calendar getCalendar() {
        return cal;
    }
    
    public String getFrequencyCode() {
        return frequencyCcode; 
    }
    
}
