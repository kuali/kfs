/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.document.service.MonthEndDateService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class provides service for MonthEndDateService
 */
public class MonthEndDateServiceImpl implements MonthEndDateService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.MonthEndDateService#getMonthEndIdDate(KualiInteger)
     */
    public java.sql.Date getByPrimaryKey(KualiInteger monthEndId) {
        java.sql.Date monthEndDate = null;
        
        Map primaryKeys = new HashMap();
        
        primaryKeys.put(EndowPropertyConstants.MONTH_END_DATE_ID, monthEndId);
        MonthEndDate monthEndDateRecord = (MonthEndDate) businessObjectService.findByPrimaryKey(MonthEndDate.class, primaryKeys);
        
        return monthEndDateRecord.getMonthEndDate();
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.MonthEndDateService#getMostRecentDate()
     */
    public java.sql.Date getMostRecentDate() {
        java.sql.Date mostRecentDate = null;
        
        Map primaryKeys = new HashMap();
        Collection<MonthEndDate> monthEndDateRecords = businessObjectService.findAll(MonthEndDate.class);
        
        for (MonthEndDate monthEndDateRecord : monthEndDateRecords) {
            mostRecentDate = monthEndDateRecord.getMonthEndDate();
        }        
        
        return (java.sql.Date) mostRecentDate;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.MonthEndDateService#getMonthEndId(Date)
     */
    public KualiInteger getMonthEndId(Date monthEndDate) {

        KualiInteger monthEndId = new KualiInteger("0");
        
        if (ObjectUtils.isNotNull(monthEndDate)) {
            Map criteria = new HashMap();
            
            criteria.put("monthEndDate", monthEndDate);
            Collection<MonthEndDate> monthEndDateRecords = businessObjectService.findMatching(MonthEndDate.class, criteria);

            for (MonthEndDate monthEndDateRecord : monthEndDateRecords) {
                monthEndId = monthEndDateRecord.getMonthEndDateId();
            }
        }
        
        return monthEndId;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.MonthEndDateService#getNextMonthEndIdForNewRecord()
     */
    public KualiInteger getNextMonthEndIdForNewRecord(){
        //Search END_ME_DT_T, get all records, find the last one with the biggest MonthEndId.
        //Increase it by one and then return this as the next new MonthEndId that should be used
        //for inserting a new record.
        KualiInteger largestMonthEndId= new KualiInteger("0");
        KualiInteger monthEndId = new KualiInteger("0");
        Collection<MonthEndDate> monthEndDateRecords = businessObjectService.findAll(MonthEndDate.class);
        for (MonthEndDate monthEndDateRecord : monthEndDateRecords) {
            monthEndId = monthEndDateRecord.getMonthEndDateId();
            if (monthEndId.isGreaterThan(largestMonthEndId)){
                largestMonthEndId = monthEndId;
            }                
        }
        return largestMonthEndId.add(new KualiInteger("1"));
    }
    
    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
