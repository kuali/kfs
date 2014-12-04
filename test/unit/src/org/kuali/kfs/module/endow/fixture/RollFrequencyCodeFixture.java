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
