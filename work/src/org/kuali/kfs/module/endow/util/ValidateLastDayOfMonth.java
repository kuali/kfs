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
package org.kuali.kfs.module.endow.util;

import java.util.Calendar;


public class ValidateLastDayOfMonth {

    public static boolean validateLastDayOfMonth (java.sql.Date sqlDate) {
        boolean isLastDayOfMonth = true;
        
        Calendar calendar = Calendar.getInstance(); 
        calendar.setTime(sqlDate);
        int theDay = calendar.get(Calendar.DAY_OF_MONTH);
        int lastDate = calendar.getActualMaximum(Calendar.DATE); 
        if (theDay == lastDate)
            isLastDayOfMonth = true;
        else
            isLastDayOfMonth = false;
        
        return isLastDayOfMonth;
    }
    
    public static boolean validateLastDayOfMonth (java.util.Date utilDate){
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        return validateLastDayOfMonth(sqlDate);        
    }
}



