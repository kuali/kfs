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
package org.kuali.kfs.module.endow.document.service;

import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * HoldingHistoryService interface to provide the method to get holding history records
 */
public interface HoldingHistoryService {

    /**
     * gets holding history records matching security id and month end id
     * 
     * @param securityId, monthEndId
     * @return List<HoldingHistory> List of HoldingHistory records matched on securityId and monthEndId
     */
    public Collection<HoldingHistory> getHoldingHistoryBySecuritIdAndMonthEndId(String securityId, KualiInteger monthEndId);

    /**
     * saves holding history records
     * 
     * @param List<HoldingHistory> List of HoldingHistory record to save
     * @return boolean true is successful else false
     */
    public boolean saveHoldingHistory(HoldingHistory holdingHistoryRecord);
    
    /**
     * gets the distinct kemid from Holding History records for a given security id
     * @param securityId
     * @return kemid
     */
    public String getKemIdFromHoldingHistory(String securityId);
}
