/*
 * Copyright 2009 The Kuali Foundation.
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



