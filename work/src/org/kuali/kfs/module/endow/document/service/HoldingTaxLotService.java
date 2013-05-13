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
package org.kuali.kfs.module.endow.document.service;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;

public interface HoldingTaxLotService {

    /**
     * Gets a holding tax lot based on primary keys: kemid, security id, registration code, lot number and IP indicator.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param lotNumber
     * @param ipIndicator
     * @return the corresponding tax lot
     */
    public HoldingTaxLot getByPrimaryKey(String kemid, String securityId, String registrationCode, int lotNumber, String ipIndicator);

    /**
     * Gets the holding tax lot based on the following criteria: kemid, security id, registration code, and IP indicator.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param ipIndicator
     * @return a list of tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getAllTaxLots(String kemid, String securityId, String registrationCode, String ipIndicator);

    /**
     * Gets all tax lots with positive units based on the following criteria: kemid, security id, registration code, and IP
     * indicator.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param ipIndicator
     * @return a list of tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getAllTaxLotsWithPositiveUnits(String kemid, String securityId, String registrationCode, String ipIndicator);

    /**
     * Gets the holding tax lot based on the following criteria: kemid, security id, registration code, and IP indicator and orders
     * them ascending or descending based on the acquired date.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param ipIndicator returned in the descending order
     * @return a list of tax lots that meet the criteria and in the right order
     */
    public List<HoldingTaxLot> getAllTaxLotsOrderByAcquiredDate(String kemid, String securityId, String registrationCode, String ipIndicator, boolean sortAscending);

    /**
     * Gets all tax lots with positive cost based on the following criteria: kemid, security id, registration code, and IP
     * indicator.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param ipIndicator
     * @return a list of tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getAllTaxLotsWithPositiveCost(String kemid, String securityId, String registrationCode, String ipIndicator);

    /**
     * Gets all tax lots on the following criteria: kemi and IPindicator.
     * 
     * @param kemid
     * @param ipIndicator
     * @return a list of tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getAllTaxLotsByKemIdAdndIPIndicator(String kemid, String ipIndicator);

    /**
     * Gets class code type based on securityId. Based on security ID, you search END_SEC_T Table to get END_SEC_T:SEC_CLS_CD, then,
     * based on class code, you search END_CLS_CD_T, to get END_CLS_CD_T:CLS_CD_TYP
     * 
     * @param id
     * @return class code type
     */
    public String getClassCodeType(String securityId);

    /**
     * The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of Cash Equivalents (C), and with the HLDG_IP_IND
     * equal to I.
     * 
     * @param kemId
     * @return marketValue
     */
    public BigDecimal getMarketValueForCashEquivalentsForAvailableIncomeCash(String kemId);

    /**
     * The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of Pooled Investment (P) and with the HLDG_IP_IND
     * equal to I times the value in the Available Cash Percent institutional parameter (accounts for only a percentage of the
     * market value allowing for pricing changes).
     * 
     * @return marketValue
     */
    public BigDecimal getMarketValueForPooledInvestmentForAvailableIncomeCash(String kemId);

    /**
     * The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of Cash Equivalents (C), and with the HLDG_IP_IND
     * equal to P.
     * 
     * @param kemId
     * @return marketValue
     */
    public BigDecimal getMarketValueForCashEquivalentsForAvailablePrincipalCash(String kemId);

    /**
     * The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of Pooled Investment (P) and with the HLDG_IP_IND
     * equal to P times the value in the Available Cash Percent institutional parameter (accounts for only a percentage of the
     * market value allowing for pricing changes).
     * 
     * @return marketValue
     */
    public BigDecimal getMarketValueForPooledInvestmentForAvailablePrincipalCash(String kemId);

    /**
     * Gets all tax lots.
     * 
     * @return a list of tax lots
     */
    public List<HoldingTaxLot> getAllTaxLots();

    /**
     * Get all tax lots for the given security that have an accrued income greater than zero.
     * 
     * @param securityId the id of the security for which to retrieve the tax lots
     * @return all tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(String securityId);

    /**
     * Gets all the tax lots for the given security that have units greater than zero.
     * 
     * @param securityId
     * @return all tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(String securityId);

    /**
     * Removes all HoldingTaxLot records from END_HLDG_TAX_LOT_T
     * @return true is successfully removed the records, else false
     */
    public boolean removeAllHoldingTaxLots();
}
