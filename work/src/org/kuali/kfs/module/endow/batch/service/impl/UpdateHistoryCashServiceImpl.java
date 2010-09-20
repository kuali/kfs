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

import java.util.Collection;
import java.util.Date;
import java.util.Calendar;

import org.kuali.kfs.module.endow.batch.service.UpdateHistoryCashService;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.MonthEndDateService;
import org.kuali.kfs.module.endow.util.ValidateLastDayOfMonth;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiInteger;


public class UpdateHistoryCashServiceImpl implements UpdateHistoryCashService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpdateHistoryCashServiceImpl.class);
    private KEMService kemService;
    private BusinessObjectService businessObjectService;
    private MonthEndDateService monthEndDateService;
    
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.UpdateHistoryCashService#updateHistoryCash()
     */
    public boolean updateHistoryCash() {
        Date currentDate = kemService.getCurrentDate();
        if (ValidateLastDayOfMonth.validateLastDayOfMonth(currentDate)){
            //Append a new record to the END_ME_DT_T TABLE
            KualiInteger monthEndDateId = appendNewMonthEndDate(currentDate);
            System.out.println (">>>Hello, has append "+monthEndDateId+":"+currentDate+" to END_ME_DT_T table.");
            //Append the records in the END_CRNT_CSH_T table to the END_HIST_CSH_T table
            return appendNewHistoricalCashRecord(monthEndDateId); 
        }
        else {
            System.out.println (">>>Hello, "+currentDate+" is NOT the last day of the month. Update History Cash batch process will do nothing.");
            LOG.info(currentDate+" is NOT the last day of the month. Update History Cash batch process will do nothing.");
            return true;
        }        
    }    

    private KualiInteger appendNewMonthEndDate(Date monthEndDate){
         KualiInteger newMonthEndDateId = monthEndDateService.getNextMonthEndIdForNewRecord();
         MonthEndDate newMonthEndDate = new MonthEndDate();
         newMonthEndDate.setMonthEndDateId(newMonthEndDateId);
         newMonthEndDate.setMonthEndDate(new java.sql.Date(monthEndDate.getTime()));         
         businessObjectService.save(newMonthEndDate);
         return newMonthEndDateId;
    }
    
    
    private boolean appendNewHistoricalCashRecord (KualiInteger monthEndDateId){
        Collection<KemidCurrentCash> kemidCurrentCashRecords = 
                businessObjectService.findAll(KemidCurrentCash.class);
        for (KemidCurrentCash kemidCurrentCashRecord : kemidCurrentCashRecords) {
            KemidHistoricalCash newKemidHistoricalCash = new KemidHistoricalCash();            
            newKemidHistoricalCash.setMonthEndDateId(monthEndDateId);
            newKemidHistoricalCash.setKemid(kemidCurrentCashRecord.getKemid());
            newKemidHistoricalCash.setHistoricalIncomeCash(kemidCurrentCashRecord.getCurrentIncomeCash());
            newKemidHistoricalCash.setHistoricalPrincipalCash(kemidCurrentCashRecord.getCurrentPrincipalCash());        
            businessObjectService.save(newKemidHistoricalCash);
        }
        return true;
  
    }

    
    /**
     * Sets the monthEndDateService.
     * 
     * @param monthEndDateService
     */
    public void setMonthEndDateService(MonthEndDateService monthEndDateService) {
        this.monthEndDateService = monthEndDateService;
    }
    
    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
    
    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
/*   
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
*/    

}
