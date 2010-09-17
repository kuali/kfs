/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.util.Date;
import java.util.Calendar;

import org.kuali.kfs.module.endow.batch.service.UpdateHistoryCashService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.service.ParameterService;

public class UpdateHistoryCashServiceImpl implements UpdateHistoryCashService {

    private KEMService kemService;
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.UpdateHistoryCashService#updateHistoryCash()
     */
    public boolean updateHistoryCash() {
        Date currentDate = kemService.getCurrentDate();
        if (currentDate.equals(LastDayOfMonth())){
            System.out.println (">>today is the last day of the month!");
        }
        else
            System.out.println (">>today is NOT the last day of the month. Do nothing.");
        return false;
    }    

    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
    
    
    public Date LastDayOfMonth(){
        // Get a calendar instance         
        Calendar calendar = Calendar.getInstance(); 
        
        // Get the last date of the current month. To get the last date for a 
        // specific month you can set the calendar month using calendar object 
        // calendar.set(Calendar.MONTH, theMonth) method. 
        int lastDate = calendar.getActualMaximum(Calendar.DATE); 
        
        // Set the calendar date to the last date of the month so then we can 
        // get the last day of the month    
        calendar.set(Calendar.DATE, lastDate); 
        int lastDay = calendar.get(Calendar.DAY_OF_WEEK); 
        
        // Print the current date and the last date of the month 
        System.out.println("Last Date: " + calendar.getTime());
        
        // The lastDay will be in a value from 1 to 7 where 1 = Monday and 7 = 
        // Saturday respectively. 
        System.out.println("Last Day : " + lastDay); 
        return calendar.getTime();
    }  
        

}
