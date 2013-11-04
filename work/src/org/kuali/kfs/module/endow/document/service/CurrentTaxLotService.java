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
import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.rice.core.api.util.type.KualiInteger;

public interface CurrentTaxLotService {

    /**
     * Gets a current tax lot based on primary keys: kemid, security id, registration code, lot number and IP indicator.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param lotNumber
     * @param ipIndicator
     * @return the corresponding tax lot
     */
    public CurrentTaxLotBalance getByPrimaryKey(String kemid, String securityId, String registrationCode, KualiInteger lotNumber, String ipIndicator);

    /**
     * Gets matching records from END_CRNT_TAX_LOT_BAL_T table
     * 
     * @param securityId
     * @return currentTaxLotBal Records from the table matching the securityId
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalancesBySecurityId(String securityId);

    /**
     * Gets matching records from END_CRNT_TAX_LOT_BAL_T table
     * 
     * @param securityClassCode, securityId
     * @return currentTaxLotBal Records from the table matching the securityClassCode, securityId
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalancesForMatchingSecurityClassCode(String securityClassCode);


    /**
     * Gets matching records from END_CRNT_TAX_LOT_BAL_T table
     * 
     * @param securityClassCode, securityId
     * @return currentTaxLotBal Records from the table matching the securityClassCode, securityId
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalancesForMatchingSecurityClassCodeAndSecurityId(String securityClassCode, String securityId);

    /**
     * Gets matching records from END_CRNT_TAX_LOT_BAL_T table
     * 
     * @param incomePrincipalIndicator
     * @return currentTaxLotBalances Records from the table matching the incomePrincipalIndicator
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalancesByIncomePrincipalIndicator(String incomePrincipalIndicator);

    /**
     * updates a current tax lot
     * 
     * @param currentTaxLotBalances
     */
    public void updateCurrentTaxLotBalance(CurrentTaxLotBalance currentTaxLotBalance);

    /**
     * clears all the records from the CurrentTaxLotBalance table.
     */
    public void clearAllCurrentTaxLotRecords();

    /**
     * Method to copy HoldingTaxLot record to currentTaxLotBalance record
     * 
     * @param holdingTaxLot
     * @return currentTaxLotBalance
     */
    public CurrentTaxLotBalance copyHoldingTaxLotToCurrentTaxLotBalance(HoldingTaxLot holdingTaxLot);

    /**
     * Method to get the security unit value for the current balance tax lot record
     * 
     * @param securityId
     * @return securityUnitValue
     */
    public BigDecimal getCurrentTaxLotBalanceSecurityUnitValue(String securityId);

    /**
     * Method to calculate Next Twelve Months Estimated value
     * 
     * @param securityId
     * @return nextTwelveMonthsEstimatedValue
     */
    public BigDecimal getNextTwelveMonthsEstimatedValue(HoldingTaxLot holdingTaxLot, String securityId);

    /**
     * Method to calculate remainder of fiscal year estimated income
     * 
     * @param securityId
     * @return remainderOfFiscalYearEstimatedIncome
     */
    public BigDecimal getRemainderOfFiscalYearEstimatedIncome(HoldingTaxLot holdingTaxLot, String securityId);

    /**
     * Method to calculate next fiscal year investment income
     * 
     * @param securityId
     * @return nextFiscalyearInvestmentIncome
     */
    public BigDecimal getNextFiscalYearInvestmentIncome(HoldingTaxLot holdingTaxLot, String securityId);

    /**
     * Gets the Sum of the HLDG _MVAL for all records for the Security in END_CURR_TAX_LOT_BAL_T.
     * 
     * @param securityId the security for which we return the sum
     * @return the Sum of the HLDG _MVAL for all records for the Security
     */
    public BigDecimal getHoldingMarketValueSumForSecurity(String securityId);

    /**
     * Retrieves all the records from END_CURR_TAX_LOT_BAL_T
     * 
     * @return currentBalances
     */
    public Collection<CurrentTaxLotBalance> getAllCurrentTaxLotBalance();

    /**
     * Gets the holding market value as follows: Class type code = B => MV = Units x Unit value / 100 Class type code = A => Market
     * Valuation (END_SEC_T: SEC_VAL_BY_MKT) minus the total cash activity (income and principal) since the last value date
     * (END_SEC_T: SEC_VAL_DT) Class type code = O => Units x Unit value
     * 
     * @param holdingTaxLot
     * @param securityId
     * @return
     */
    public BigDecimal getHoldingMarketValue(HoldingTaxLot holdingTaxLot, String securityId);

    /**
     * Calculates the total Holding market value based on FEE_BAL_TYP_CD = CMV
     * 
     * @param feeMethod feeMethod object
     * @return totalHoldingMarketValue
     */
    public BigDecimal getCurrentTaxLotBalanceTotalHoldingMarketValue(FeeMethod feeMethod);

    /**
     * Calculates the total Holding market value based on FEE_BAL_TYP_CD = CU
     * 
     * @param feeMethod feeMethod object
     * @return totalHoldingUnits
     */
    public BigDecimal getCurrentTaxLotBalanceTotalHoldingUnits(FeeMethod feeMethod);
}
