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

import java.util.List;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.service.CurrentTaxLotBalanceUpdateService;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.document.service.CurrentTaxLotService;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the CurrentTaxLotBalanceUpdateService batch job.
 */
@Transactional
public class CurrentTaxLotBalanceUpdateServiceImpl implements CurrentTaxLotBalanceUpdateService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CurrentTaxLotBalanceUpdateServiceImpl.class);
    
    protected ParameterService parameterService;
    protected HoldingTaxLotService holdingTaxLotService;
    protected CurrentTaxLotService currentTaxLotService;

    /**
     * Constructs a CurrentTaxLotBalanceUpdateServiceImpl instance
     */
    public CurrentTaxLotBalanceUpdateServiceImpl() {
        
    }

    /**
     * the system will compile the information in the Holding Tax Lot Table along with 
     * calculations of market value and estimated income into a new table called the 
     * Current Tax Lot Balances 
     */
    public boolean updateCurrentTaxLotBalances() {
        LOG.debug("updateCurrentTaxLotBalances() - Processed all HoldingTaxLot records started."); 
        
        //Step 1: remove all the records from END_CURR_TAX_LOT_BAL_T table
        clearAllCurrentTaxLotRecords();
           
        //Step 2: Retrieve all HoldingTaxLot records
        List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getAllTaxLots();
            
        //process the records in the list
        for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
            CurrentTaxLotBalance currentTaxLotBalance = currentTaxLotService.copyHoldingTaxLotToCurrentTaxLotBalance(holdingTaxLot); 

            String securityId = currentTaxLotBalance.getSecurityId();

            currentTaxLotBalance.setHoldingMarketValue(currentTaxLotService.getHoldingMarketValue(holdingTaxLot, securityId));
            currentTaxLotBalance.setSecurityUnitVal(currentTaxLotService.getCurrentTaxLotBalanceSecurityUnitValue(securityId));
            currentTaxLotBalance.setAnnualEstimatedIncome(currentTaxLotService.getNextTwelveMonthsEstimatedValue(holdingTaxLot, securityId));
            currentTaxLotBalance.setRemainderOfFYEstimatedIncome(currentTaxLotService.getRemainderOfFiscalYearEstimatedIncome(holdingTaxLot, securityId));
            currentTaxLotBalance.setNextFYEstimatedIncome(currentTaxLotService.getNextFiscalYearInvestmentIncome(holdingTaxLot, securityId));
                
            saveCurrentTaxLotRecord(currentTaxLotBalance);
            if(LOG.isDebugEnabled()) {
                LOG.debug("Updated current tax lot balance for Security Id: " + securityId + " and kemid: " + holdingTaxLot.getKemid());    
            }
        }
        
        LOG.debug("updateCurrentTaxLotBalances() - Updated  Current Tax Lot Balances."); 
        
        return true;
    }
    
    /**
     * This method checks if the System parameter has been set up for this batch job.
     * @result return true if the system parameter exists, else false
     */
    protected boolean systemParametersForCurrentTaxLotBalanceUpdateStepJob() {
        LOG.info("systemParametersForCurrentTaxLotBalanceUpdateStepJob() started.");
        
        boolean systemParameterExists = true;
        
        // check to make sure the system parameter has been setup...
        if (!getParameterService().parameterExists(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowParameterKeyConstants.FISCAL_YEAR_END_MONTH_AND_DAY)) {
          LOG.warn("FISCAL_YEAR_END_MONTH_AND_DAY System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          return false;
        }
        
        LOG.info("systemParametersForCurrentTaxLotBalanceUpdateStepJob() ended.");
        
        return systemParameterExists;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.CurrentTaxLotBalanceUpdateService#clearAllCurrentTaxLotRecords()
     * Method to clear all the records in the currentTaxLotBalance table
     */
    public void clearAllCurrentTaxLotRecords() {
        currentTaxLotService.clearAllCurrentTaxLotRecords();
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.CurrentTaxLotBalanceUpdateService#saveCurrentTaxLotRecord(CurrentTaxLotBalance)
     * Saves the CurrentTaxLot to the table (END_CURR_TAX_LOT_BAL_T).
     */
    public void saveCurrentTaxLotRecord(CurrentTaxLotBalance currentTaxLotBalance) {
        currentTaxLotService.updateCurrentTaxLotBalance(currentTaxLotBalance);
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
     * gets the currentTaxLotService
     * 
     * @param currentTaxLotService The currentTaxLotService to get.
     */    
    public CurrentTaxLotService getCurrentTaxLotService() {
        return currentTaxLotService;
    }

    /**
     * gets the currentTaxLotService
     * 
     * @param currentTaxLotService The currentTaxLotService to get.
     */    
    public void setCurrentTaxLotService(CurrentTaxLotService currentTaxLotService) {
        this.currentTaxLotService = currentTaxLotService;
    }
}
