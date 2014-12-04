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
package org.kuali.kfs.module.endow.document.service;

import java.math.BigDecimal;
import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.rice.core.api.util.type.KualiInteger;

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
     * 
     * @param securityId
     * @return kemid
     */
    public String getKemIdFromHoldingHistory(String securityId);
    
    /**
     * gets holding history records matching securityClassCode 
     * 
     * @param securityClassCode
     * @return List<HoldingHistory> List of HoldingHistory records matching securityClassCode
     */
    public Collection<HoldingHistory> getHoldingHistoryForMatchingSecurityClassCode(String securityClassCode);
    
    /**
     * gets holding history records matching securityId
     * 
     * @param securityId
     * @return List<HoldingHistory> List of HoldingHistory records matching securityId
     */
    public Collection<HoldingHistory> getHoldingHistoryBySecurityId(String securityId);

    /**
     * gets holding history records matching securityClassCode, securityId
     * 
     * @param securityClassCode, securityId
     * @return List<HoldingHistory> List of HoldingHistory records matching securityClassCode, securityId
     */
    public Collection<HoldingHistory> getHoldingHistoryForMatchingSecurityClassCodeAndSecurityId(String securityClassCode, String securityId);
    
    /**
     * gets holding history records matching incomePrincipalIndicator
     * 
     * @param incomePrincipalIndicator
     * @return List<HoldingHistory> List of HoldingHistory records matching incomePrincipalIndicator
     */
    public Collection<HoldingHistory> getHoldingHistoryByIncomePrincipalIndicator(String incomePrincipalIndicator);

    /**
     * gets holding history records
     * 
     * @return List<HoldingHistory> List of HoldingHistory records
     */
    public Collection<HoldingHistory> getAllHoldingHistory();
    
    /**
     * Calculates the total Holding Units based on FEE_BAL_TYP_CD = AU OR MU
     * 
     * @param feeMethod feeMethod object
     * @param feeMethodCodeForSecurityClassCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeClassCode DD definition for fee method code
     * @param feeMethodCodeForSecurityIdsthe feeMethod code that should be passed in as uppercase or not depending on FeeSecurity DD
     *        definition for fee method code
     * @return totalHoldingUnits
     */
    public BigDecimal getHoldingHistoryTotalHoldingUnits(FeeMethod feeMethod, String feeMethodCodeForSecurityClassCodes, String feeMethodCodeForSecurityIds);

    /**
     * Calculates the total Holding market value based on FEE_BAL_TYP_CD = AMV OR MMV
     * 
     * @param feeMethod feeMethod object
     * @param feeMethodCodeForSecurityClassCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeClassCode DD definition for fee method code
     * @param feeMethodCodeForSecurityIdsthe feeMethod code that should be passed in as uppercase or not depending on FeeSecurity DD
     *        definition for fee method code
     * @return totalHoldingMarketValue
     */
    public BigDecimal getHoldingHistoryTotalHoldingMarketValue(FeeMethod feeMethod, String feeMethodCodeForSecurityClassCodes, String feeMethodCodeForSecurityIds);

}
