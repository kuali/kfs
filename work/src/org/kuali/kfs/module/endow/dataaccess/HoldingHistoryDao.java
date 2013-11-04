/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.rice.core.api.util.type.KualiInteger;

public interface HoldingHistoryDao {

    /**
     * Gets HoldingHistory
     * 
     * @param kemid
     * @param medId
     * @return
     */
    public List<HoldingHistory> getHoldingHistory(String kemid, KualiInteger medId);

    /**
     * Gets HoldingHistory records
     * 
     * @param kemid
     * @return List<HoldingHistory>
     */
    public List<HoldingHistory> getHoldingHistoryByKemid(String kemid);

    /**
     * Gets the sum of the given attribute
     * 
     * @param kemid
     * @param medId
     * @param securityId
     * @param ipInd
     * @param attributeName
     * @return
     */
    public BigDecimal getSumOfHoldginHistoryAttribute(String attributeName, String kemid, KualiInteger medId, String securityId, String ipInd);

    /**
     * Gets a list of HoldingHistory by kemid, medId, ipInd and with units > 0
     * 
     * @param kemid
     * @param monthEndId
     * @param ipInd
     * @return
     */
    public List<HoldingHistory> getHoldingHistoryByKemidIdAndMonthEndIdAndIpInd(String kemid, KualiInteger monthEndId, String ipInd);

    /**
     * Prepares the criteria and selects the records from END_HLDG_HIST_T table.
     * 
     * @param feeMethod
     * @param feeMethodCodeForSecurityClassCodes
     * @param feeMethodCodeForSecurityIds
     * @return
     */
    public Collection<HoldingHistory> getHoldingHistoryForBlance(FeeMethod feeMethod, String feeMethodCodeForSecurityClassCodes, String feeMethodCodeForSecurityIds);
}
