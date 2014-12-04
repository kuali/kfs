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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.util.Collection;
import java.util.Date;

import org.kuali.kfs.module.endow.batch.service.UpdateHistoryCashService;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.MonthEndDateService;
import org.kuali.kfs.module.endow.util.ValidateLastDayOfMonth;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;


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
            LOG.info("UpdateHistoryCash batch process has appended "+monthEndDateId+":"+currentDate+" to END_ME_DT_T table.");
            //Append the records in the END_CRNT_CSH_T table to the END_HIST_CSH_T table
            return appendNewHistoricalCashRecords(monthEndDateId); 
        }
        else {
            LOG.info(currentDate+" is NOT the last day of the month. Update History Cash batch process will do nothing.");
            return true;
        }        
    }    

    /**
     * calculates the next month end date id using the month end date service and uses
     * that value to insert a new record into the month end date table.
     * 
     * @param monthEndDate current date
     * @return KualiInteger
     */
    protected KualiInteger appendNewMonthEndDate(Date monthEndDate){
         KualiInteger newMonthEndDateId = monthEndDateService.getNextMonthEndIdForNewRecord();
         MonthEndDate newMonthEndDate = new MonthEndDate();
         newMonthEndDate.setMonthEndDateId(newMonthEndDateId);
         newMonthEndDate.setMonthEndDate(new java.sql.Date(monthEndDate.getTime()));         
         businessObjectService.save(newMonthEndDate);

         return newMonthEndDateId;
    }
    
    /**
     * For each current cash records, creates a record in historical cash table
     * 
     * @param monthEndDateId
     * @return true
     */
    protected boolean appendNewHistoricalCashRecords(KualiInteger monthEndDateId){
        Collection<KemidCurrentCash> kemidCurrentCashRecords = 
                businessObjectService.findAll(KemidCurrentCash.class);

        int counter = 0;
        for (KemidCurrentCash kemidCurrentCashRecord : kemidCurrentCashRecords) {
            KemidHistoricalCash newKemidHistoricalCash = new KemidHistoricalCash();            
            newKemidHistoricalCash.setMonthEndDateId(monthEndDateId);
            newKemidHistoricalCash.setKemid(kemidCurrentCashRecord.getKemid());
            newKemidHistoricalCash.setHistoricalIncomeCash(kemidCurrentCashRecord.getCurrentIncomeCash());
            newKemidHistoricalCash.setHistoricalPrincipalCash(kemidCurrentCashRecord.getCurrentPrincipalCash());        
            businessObjectService.save(newKemidHistoricalCash);
            counter++;            
        }
        LOG.info ("UpdateHistoryCash batch process has appended "+counter+" records to the END_HIST_CSH_T table.");

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
    
    /**
     * gets kemService
     * @return kemService
     */
    protected KEMService getKemService() {
        return kemService;
    }

    /**
     * gets businessObjectService
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * gets monthEndDateService
     * @return monthEndDateService
     */
    protected MonthEndDateService getMonthEndDateService() {
        return monthEndDateService;
    }
}
