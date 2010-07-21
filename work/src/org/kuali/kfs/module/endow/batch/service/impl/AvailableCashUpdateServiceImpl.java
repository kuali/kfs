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
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.AvailableCashUpdateStep;
import org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.TypeRestrictionCode;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KemidCurrentCashOpenRecordsService;
import org.kuali.kfs.module.endow.document.service.TypeRestrictionCodeService;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.Guid;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the AvailableCashUpdateService batch job.
 */
@Transactional
public class AvailableCashUpdateServiceImpl implements AvailableCashUpdateService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AvailableCashUpdateServiceImpl.class);
    
    protected DateTimeService dateTimeService;
    protected ParameterService parameterService;
    protected BusinessObjectService businessObjectService;
    protected KemidCurrentCashOpenRecordsService kemidCurrentCashOpenRecordsService;
    protected HoldingTaxLotService holdingTaxLotService;
    protected TypeRestrictionCodeService typeRestrictionCodeService;

    /**
     * Constructs a AvailableCashUpdateServiceImpl instance
     */
    public AvailableCashUpdateServiceImpl() {
        
    }

    /**
     * This process will generate records at the end of each processing cycle to summarize the 
     * available spendable funds for every KEMID that is not closed.  
     */
    public boolean summarizeAvailableSpendableFunds() {
        boolean success = true;
        
        if (systemParametersForSummarizeAvailableSpendableFundsJobExist()) {
            //Step 1: remove all the records from END_AVAIL_CSH_T table
            clearAllAvailableCash();
           
            //Step 2: Retrieve all KEMID records where CLOSED_IND set to N
            Collection<KEMID> kemIdRecords = getAllKemIdWithClosedIndicatorNo();
            //process the records in the collection
            for (KEMID kemIdRecord : kemIdRecords) {
                KEMIDCurrentAvailableBalance kEMIDCurrentAvailableBalance = new KEMIDCurrentAvailableBalance();
                String kemId = kemIdRecord.getKemid();
                kEMIDCurrentAvailableBalance.setKemid(kemId);
                LOG.info("Calculate sum for available income cash and available principal cash for the kemid: " + kemId);                        
                kEMIDCurrentAvailableBalance.setAvailableIncomeCash(getAvailableIncomeCash(kemId));
                kEMIDCurrentAvailableBalance.setAvailablePrincipalCash(getAvailablePrincipalCash(kemId, kemIdRecord.getPrincipalRestrictionCode()));
                kEMIDCurrentAvailableBalance.setAvailableTotalCash(kEMIDCurrentAvailableBalance.getAvailableIncomeCash().add(kEMIDCurrentAvailableBalance.getAvailablePrincipalCash()));
                kEMIDCurrentAvailableBalance.setObjectId(new Guid().toString());
                kEMIDCurrentAvailableBalance.setVersionNumber(1L);
                InsertAvailableCash(kEMIDCurrentAvailableBalance);
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
        LOG.info("systemParametersForSummarizeAvailableSpendableFundsJobExist() started.");
        
        boolean systemParameterExists = true;
        
        // check to make sure the system parameter has been setup...
        if (!getParameterService().parameterExists(AvailableCashUpdateStep.class, EndowParameterKeyConstants.AvailableCashUpdateConstants.AVAILABLE_CASH_PERCENT)) {
          LOG.warn("AVAILABLE_CASH_PERCENT System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          return false;
        }
        
        return systemParameterExists;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService#getAllKemIdWithClosedIndicatorNo()
     * Retrieves all kemId records where closed indicator = 'N'
     */
    public Collection<KEMID> getAllKemIdWithClosedIndicatorNo() {
        LOG.info("Getting all KEMIDs with Closed Indicator = 'N'");
        
        Map fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.KEMID_CLOSED, EndowConstants.NO);
        
        Collection<KEMID> kemIdRecords = businessObjectService.findMatching(KEMID.class, fieldValues);
        LOG.info("Number of KEMIDs with Closed Indicator = '" + kemIdRecords.size());
        
        return kemIdRecords;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService#clearAvailableCash()
     * Method to clear all the records in the kEMIDCurrentAvailableBalance table
     */
    public void clearAllAvailableCash() {
        LOG.info("Step1: Clearing all available cash records");
        
        Collection<KEMIDCurrentAvailableBalance> KEMIDCurrentAvailableBalances = businessObjectService.findAll(KEMIDCurrentAvailableBalance.class);

        for (KEMIDCurrentAvailableBalance kEMIDCurrentAvailableBalance : KEMIDCurrentAvailableBalances) {
            businessObjectService.delete(kEMIDCurrentAvailableBalance);
        }
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService#InsertAvailableCash(KemidCurrentCash)
     * Method to clear all the records in the kEMIDCurrentAvailableBalance table
     */
    public void InsertAvailableCash(KEMIDCurrentAvailableBalance kEMIDCurrentAvailableBalance) {
        if (kEMIDCurrentAvailableBalance == null) {
            throw new IllegalArgumentException("invalid (null) kEMIDCurrentAvailableBalance");
        }
        
        businessObjectService.save(kEMIDCurrentAvailableBalance);
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
        
        KemidCurrentCash kemidCurrentCash = kemidCurrentCashOpenRecordsService.getByPrimaryKey(kemId);
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
        
        TypeRestrictionCode typeRestrictionCode = typeRestrictionCodeService.getByPrimaryKey(typePrincipalRestrictedCode);
        if (typeRestrictionCode.getPermanentIndicator() == true) {
            return availablePrincipalCash;
        }
        
        KemidCurrentCash kemidCurrentCash = kemidCurrentCashOpenRecordsService.getByPrimaryKey(kemId);
        
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
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the BusinessObjectService
     * 
     * @param businessObjectService The BusinessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * gets the kemidCurrentCashOpenRecordsService
     * 
     * @param kemidCurrentCashOpenRecordsService The kemidCurrentCashOpenRecordsService to get.
     */
    protected KemidCurrentCashOpenRecordsService getKemidCurrentCashOpenRecordsService() {
        return kemidCurrentCashOpenRecordsService;
    }

    /**
     * Sets the kemidCurrentCashOpenRecordsService
     * 
     * @param kemidCurrentCashOpenRecordsService The kemidCurrentCashOpenRecordsService to set.
     */
    public void setKemidCurrentCashOpenRecordsService(KemidCurrentCashOpenRecordsService kemidCurrentCashOpenRecordsService) {
        this.kemidCurrentCashOpenRecordsService = kemidCurrentCashOpenRecordsService;
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
     * gets the typeRestrictionCodeService
     * 
     * @param typeRestrictionCodeService The typeRestrictionCodeService to get.
     */    
    protected TypeRestrictionCodeService getTypeRestrictionCodeService() {
        return typeRestrictionCodeService;
    }

    /**
     * Sets the typeRestrictionCodeService
     * 
     * @param typeRestrictionCodeService The typeRestrictionCodeService to set.
     */    
    public void setTypeRestrictionCodeService(TypeRestrictionCodeService typeRestrictionCodeService) {
        this.typeRestrictionCodeService = typeRestrictionCodeService;
    }
}
