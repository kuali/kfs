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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.util.Collection;
import java.util.Date;

import org.kuali.kfs.module.endow.batch.service.UpdateHoldingHistoryService;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.MonthEndDateService;
import org.kuali.kfs.module.endow.util.ValidateLastDayOfMonth;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class UpdateHoldingHistoryServiceImpl implements UpdateHoldingHistoryService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpdateHistoryCashServiceImpl.class);
    private KEMService kemService;
    private BusinessObjectService businessObjectService;
    private MonthEndDateService monthEndDateService;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.UpdateHoldingHistoryService#updateHoldingHistory()
     */
    public boolean updateHoldingHistory() {
        Date currentDate = kemService.getCurrentDate();
        if (ValidateLastDayOfMonth.validateLastDayOfMonth(currentDate)){
            KualiInteger monthEndDateId = monthEndDateService.getMonthEndId(currentDate);
            if (ObjectUtils.isNotNull(monthEndDateId)){
                //Append the records in the END_CRNT_TAX_LOT_BAL_T table to the END_HLDG_HIST_T table
                return appendNewHoldingHistoryRecords(monthEndDateId);
            }else {
                LOG.error(currentDate+" does not exist in END_MD_DT_T table. "+
                        "Can NOT append the records in the END_CRNT_TAX_LOT_BAL_T table to the END_HLDG_HIST_T table ");
                return false;
            }
        }
        else {            
            LOG.info(currentDate+" is NOT the last day of the month. Update Holding History batch process will do nothing.");
            return true;
        }  
    }
    
    
    private boolean appendNewHoldingHistoryRecords(KualiInteger monthEndDateId){
        Collection<CurrentTaxLotBalance> currentTaxLotBalanceRecords = 
                businessObjectService.findAll(CurrentTaxLotBalance.class);
        int counter = 0;
        for (CurrentTaxLotBalance currentTaxLotBalanceRecord : currentTaxLotBalanceRecords) {
            HoldingHistory newHoldingHistory = new HoldingHistory();            
            newHoldingHistory.setMonthEndDateId(monthEndDateId);
            newHoldingHistory.setKemid(currentTaxLotBalanceRecord.getKemid());
            newHoldingHistory.setSecurityId(currentTaxLotBalanceRecord.getSecurityId());
            newHoldingHistory.setRegistrationCode(currentTaxLotBalanceRecord.getRegistrationCode());
            newHoldingHistory.setLotNumber(currentTaxLotBalanceRecord.getLotNumber());
            newHoldingHistory.setIncomePrincipalIndicator(currentTaxLotBalanceRecord.getIncomePrincipalIndicator());
            newHoldingHistory.setUnits(currentTaxLotBalanceRecord.getUnits());
            newHoldingHistory.setCost(currentTaxLotBalanceRecord.getCost());
            newHoldingHistory.setEstimatedIncome(currentTaxLotBalanceRecord.getAnnualEstimatedIncome());
            newHoldingHistory.setRemainderOfFYEstimatedIncome(currentTaxLotBalanceRecord.getRemainderOfFYEstimatedIncome());
            newHoldingHistory.setNextFYEstimatedIncome(currentTaxLotBalanceRecord.getNextFYEstimatedIncome());
            newHoldingHistory.setSecurityUnitVal(currentTaxLotBalanceRecord.getSecurityUnitVal());
            newHoldingHistory.setAcquiredDate(currentTaxLotBalanceRecord.getAcquiredDate());
            newHoldingHistory.setPriorAccrual(currentTaxLotBalanceRecord.getPriorAccrual());
            newHoldingHistory.setCurrentAccrual(currentTaxLotBalanceRecord.getCurrentAccrual());
            newHoldingHistory.setLastTransactionDate(currentTaxLotBalanceRecord.getLastTransactionDate());
            newHoldingHistory.setMarketValue(currentTaxLotBalanceRecord.getMarketValue());
            businessObjectService.save(newHoldingHistory);
            counter++;            
        }
        LOG.info ("UpdateHoldingHistory batch process has appended "+counter+" records to the END_HLDG_HIST_T table.");
        return true;
  
    }
    
    /**
     * Sets the monthEndDateService.
     * 
     * @param monthEndDateService
     */
    public void setMonthEndDateService(MonthEndDateService monthEndDateService) {
        this.monthEndDateService = monthEndDateService;
    }
    
    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
    
    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    

}
