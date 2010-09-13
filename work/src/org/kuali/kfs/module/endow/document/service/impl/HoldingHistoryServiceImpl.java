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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * This class provides service for Security maintenance
 */
public class HoldingHistoryServiceImpl implements HoldingHistoryService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundControlService#getHoldingHistoryBySecuritIdAndMonthEndId(java.lang.String, KualiInteger)
     */
    public Collection<HoldingHistory> getHoldingHistoryBySecuritIdAndMonthEndId(String securityId, KualiInteger monthEndId) {

        Collection<HoldingHistory> holdingHistory = new ArrayList();
        
        if (StringUtils.isNotBlank(securityId)) {
            Map criteria = new HashMap();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(HoldingHistory.class, EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID)) {
                securityId = securityId.toUpperCase();
            }
            
            criteria.put("securityId", securityId);
            criteria.put("monthEndDateId", monthEndId);            
            holdingHistory = businessObjectService.findMatching(HoldingHistory.class, criteria);
        }
        
        return holdingHistory;
        
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#saveHoldingHistory(HoldingHistory)
     */
    public boolean saveHoldingHistory(HoldingHistory holdingHistoryRecord) {
       boolean success = true;
       
       try {
           businessObjectService.save(holdingHistoryRecord);
       }
       catch (Exception ex) {
           success = false;
       }
       
       return success;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#getKemIdFromHoldingHistory(String)
     */
    public String getKemIdFromHoldingHistory(String securityId) {
        String kemId = "";
        
        Collection<HoldingHistory> holdingHistory = new ArrayList();
        
        if (StringUtils.isNotBlank(securityId)) {
            Map criteria = new HashMap();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(HoldingHistory.class, EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID)) {
                securityId = securityId.toUpperCase();
            }
            
            criteria.put("securityId", securityId);
            holdingHistory = businessObjectService.findMatching(HoldingHistory.class, criteria);
        }
        
        for (HoldingHistory holdingHistoryRecord : holdingHistory) {
            kemId = holdingHistoryRecord.getKemid();
        }
        
        return kemId;
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
