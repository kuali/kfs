/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.math.BigDecimal;
import java.util.Collection;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.AvailableCashUpdateStep;
import org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService;
import org.kuali.kfs.module.endow.batch.service.KEMIDCurrentAvailableBalanceService;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.KemidCurrentCashService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the AvailableCashUpdateService batch job.
 */
@Transactional
public class AvailableCashUpdateServiceImpl implements AvailableCashUpdateService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AvailableCashUpdateServiceImpl.class);

    protected ParameterService parameterService;
    protected KemidCurrentCashService kemidCurrentCashService;
    protected HoldingTaxLotService holdingTaxLotService;
    protected KEMIDService kemidService;
    protected KEMIDCurrentAvailableBalanceService kemidCurrentAvailableBalanceService;
    /**
     * Constructs a AvailableCashUpdateServiceImpl instance
     */
    public AvailableCashUpdateServiceImpl() {

    }

    /**
     * This process will generate records at the end of each processing cycle to summarize the
     * available spendable funds for every KEMID that is not closed.
     */
    @Override
    public boolean summarizeAvailableSpendableFunds() {
        boolean success = true;

        if (systemParametersForSummarizeAvailableSpendableFundsJobExist()) {
            //Step 1: remove all the records from END_AVAIL_CSH_T table
            kemidCurrentAvailableBalanceService.clearAllAvailableCash();

            //Step 2: Retrieve all KEMID records where CLOSED_IND set to N
            Collection<KEMID> kemIdRecords = kemidService.getAllKemIdWithClosedIndicatorNo();
            //process the records in the collection
            for (KEMID kemIdRecord : kemIdRecords) {
                KEMIDCurrentAvailableBalance kemidCurrentAvailableBalance = new KEMIDCurrentAvailableBalance();
                String kemId = kemIdRecord.getKemid();
                kemidCurrentAvailableBalance.setKemid(kemId);
                LOG.info("Calculate sum for available income cash and available principal cash for the kemid: " + kemId);
                kemidCurrentAvailableBalance.setAvailableIncomeCash(getAvailableIncomeCash(kemId));
                kemidCurrentAvailableBalance.setAvailablePrincipalCash(getAvailablePrincipalCash(kemId, kemIdRecord.getPrincipalRestrictionCode()));
                kemidCurrentAvailableBalance.setAvailableTotalCash(kemidCurrentAvailableBalance.getAvailableIncomeCash().add(kemidCurrentAvailableBalance.getAvailablePrincipalCash()));
                kemidCurrentAvailableBalance.setObjectId(java.util.UUID.randomUUID().toString());
                kemidCurrentAvailableBalance.setVersionNumber(1L);
                kemidCurrentAvailableBalanceService.InsertAvailableCash(kemidCurrentAvailableBalance);
            }
        }

        LOG.info("Processed all KEMID records.  Summarized available spendable funds.");

        return success;
    }

    /**
     * This method checks if the System parameters have been set up for this batch job.
     * @result return true if the system parameters exist, else false
     */
    protected boolean systemParametersForSummarizeAvailableSpendableFundsJobExist() {
        LOG.debug("systemParametersForSummarizeAvailableSpendableFundsJobExist() started.");

        boolean systemParameterExists = true;

        // check to make sure the system parameter has been setup...
        if (!getParameterService().parameterExists(AvailableCashUpdateStep.class, EndowParameterKeyConstants.AvailableCashUpdateConstants.AVAILABLE_CASH_PERCENT)) {
          LOG.warn("AVAILABLE_CASH_PERCENT System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          return false;
        }

        return systemParameterExists;
    }

    /**
     * Method to calculate sum of available cash Income Cash
     * 1.   END_CRNT_CSH_T: CRNT_INC_CSH for the KEMID
     * 2.  The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of
     * Cash Equivalents (C), and with the HLDG_IP_IND equal to I.
     * 3.  The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of
     * Pooled Investment (P) and with the HLDG_IP_IND equal to I times the value in the
     * Available Cash Percent institutional parameter (accounts for only a percentage of the market
     * value allowing for pricing changes).
     * @param kemId
     * @return availableIncomeCash
     */
    protected BigDecimal getAvailableIncomeCash(String kemId) {
        BigDecimal availableIncomeCash = BigDecimal.ZERO;

        KemidCurrentCash kemidCurrentCash = kemidCurrentCashService.getByPrimaryKey(kemId);
        if (ObjectUtils.isNotNull(kemidCurrentCash)) {
            availableIncomeCash = availableIncomeCash.add(kemidCurrentCash.getCurrentIncomeCash().bigDecimalValue());
        }

        //get market value of the KEMID with class code type = C and IP indicator = I
        availableIncomeCash = availableIncomeCash.add(holdingTaxLotService.getMarketValueForCashEquivalentsForAvailableIncomeCash(kemId));

        //get market value of the KEMID with class code type = P and IP indicator = I
        availableIncomeCash = availableIncomeCash.add(holdingTaxLotService.getMarketValueForPooledInvestmentForAvailableIncomeCash(kemId));

        return availableIncomeCash;
    }

    /**
     * If the END_KEMID_T record has a TYP_PRIN_RESTR CD where END_TYP_RESTR_CD_T:  PERM is
     * equal to Y (Yes), the principal is Permanently Restricted and AVAIL_PRIN_CSH is zero (0.00).
     * Otherwise Principal Available Cash is the sum of all of the following :
     * END_CRNT_CSH: CRNT_PRIN_CSH
     * The Market Value of the END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of Cash Equivalents (C)
     * and with the HLDG_IP_IND equal to P.
     * The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of
     * Pooled Investment (P) and with the HLDG_IP_IND equal to P times the value in the
     * Available Cash Percent institutional parameter (accounts for only a percentage of
     * the market value allowing for pricing changes)
     *
     * @param kemId, typeRestrictionCode
     * @return availableIncomeCash
     */
    protected BigDecimal getAvailablePrincipalCash(String kemId, String typePrincipalRestrictedCode) {
        BigDecimal availablePrincipalCash = BigDecimal.ZERO;

        if (kemidService.isTrueEndowment(kemId)) {
            return availablePrincipalCash;
        }

        KemidCurrentCash kemidCurrentCash = kemidCurrentCashService.getByPrimaryKey(kemId);

        if (ObjectUtils.isNotNull(kemidCurrentCash)) {
            availablePrincipalCash = availablePrincipalCash.add(kemidCurrentCash.getCurrentPrincipalCash().bigDecimalValue());
        }

        //get market value of the KEMID with class code type = C and IP indicator = P
        availablePrincipalCash = availablePrincipalCash.add(holdingTaxLotService.getMarketValueForCashEquivalentsForAvailablePrincipalCash(kemId));

        //get market value of the KEMID with class code type = P and IP indicator = P
        availablePrincipalCash = availablePrincipalCash.add(holdingTaxLotService.getMarketValueForPooledInvestmentForAvailablePrincipalCash(kemId));

        return availablePrincipalCash;
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * gets the kemidCurrentCashService
     *
     * @param kemidCurrentCashService The kemidCurrentCashService to get.
     */
    protected KemidCurrentCashService getKemidCurrentCashOpenRecordsService() {
        return kemidCurrentCashService;
    }

    /**
     * Sets the kemidCurrentCashService
     *
     * @param kemidCurrentCashService The kemidCurrentCashService to set.
     */
    public void setKemidCurrentCashService(KemidCurrentCashService kemidCurrentCashService) {
        this.kemidCurrentCashService = kemidCurrentCashService;
    }

    /**
     * gets the holdingTaxLotService
     *
     * @param holdingTaxLotService The holdingTaxLotService to get.
     */
    protected HoldingTaxLotService getHoldingTaxLotService() {
        return holdingTaxLotService;
    }

    /**
     * Sets the holdingTaxLotService
     *
     * @param holdingTaxLotService The holdingTaxLotService to set.
     */
    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
    }

    /**
     * gets the kemidService
     *
     * @param kemidService The kemidService to get.
     */
    protected KEMIDService getkemidService() {
        return kemidService;
    }

    /**
     * gets the kemidService
     *
     * @param kemidService The kemidService to get.
     */
    public void setkemidService(KEMIDService kemidService) {
        this.kemidService = kemidService;
    }

    /**
     * Gets the kemidCurrentAvailableBalanceService attribute.
     * @return Returns the kemidCurrentAvailableBalanceService.
     */
    protected KEMIDCurrentAvailableBalanceService getkemidCurrentAvailableBalanceService() {
        return kemidCurrentAvailableBalanceService;
    }

    /**
     * Sets the kemidCurrentAvailableBalanceService attribute value.
     * @param kemidCurrentAvailableBalanceService The kemidCurrentAvailableBalanceService to set.
     */
    public void setkemidCurrentAvailableBalanceService(KEMIDCurrentAvailableBalanceService kemidCurrentAvailableBalanceService) {
        this.kemidCurrentAvailableBalanceService = kemidCurrentAvailableBalanceService;
    }
}
