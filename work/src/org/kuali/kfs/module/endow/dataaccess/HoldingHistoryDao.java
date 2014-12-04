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
