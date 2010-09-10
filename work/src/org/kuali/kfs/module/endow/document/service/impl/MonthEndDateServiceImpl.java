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
