/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.batch.service.RollFrequencyDatesService;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.businessobject.lookup.CalculateProcessDateUsingFrequencyCodeService;
import org.kuali.kfs.module.endow.dataaccess.FeeMethodDao;
import org.kuali.kfs.module.endow.dataaccess.RecurringCashTransferDao;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.dataaccess.TicklerDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the RollFrequencyDatesService batch job.
 */
@Transactional
public class RollFrequencyDatesServiceImpl implements RollFrequencyDatesService {

    protected ParameterService parameterService;
    protected KEMService kemService;
    protected BusinessObjectService businessObjectService;
    protected CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService; 
    
    private SecurityDao securityDao;
    private FeeMethodDao feeMethodDao;
    private TicklerDao ticklerDao;
    private RecurringCashTransferDao recurringCashTransferDao;
    
    /**
     * Updates some date fields based on the frequency for the activity 
     * @return true if the fields are updated successfully; false otherwise
     */
    public boolean updateFrequencyDate() {
        
        System.out.println("RollFrequencyDatesServiceImpl----------------------------------------------------");

        // get the current date
        Date currentDate = kemService.getCurrentDate();
         
        // update SEC_INC_NXT_PAY_DT in END_SEC_T
        List<Security> securityRecords = securityDao.getAllSecuritiesWithNextPayDateEqualOrLessCurrentDate();
        for (Security security : securityRecords) {
            Date icomeNextPayDate = security.getIncomeNextPayDate();            
            if (icomeNextPayDate != null && icomeNextPayDate.compareTo(currentDate) <= 0 ) {
                String frequencyCode = security.getIncomePayFrequency();           
                //calculate the next date for the frequency code and then save it
                if (frequencyCode != null && !frequencyCode.isEmpty()) {
                    Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode);
                    if (nextDate != null) {
                        security.setIncomeNextPayDate(nextDate);
                        businessObjectService.save(security);
                    }
                }
            }
        }
                
        // update TKLR_NXT_DUE_DT in END_TKLR_T
        List<Tickler> TicklerRecords = ticklerDao.getAllTicklerWithNextPayDateEqualOrLessCurrentDate();
        for (Tickler tickler : TicklerRecords) {
            Date nextDueDate = tickler.getNextDueDate();            
            if (nextDueDate != null && nextDueDate.compareTo(currentDate) <= 0 ) {
                String frequencyCode = tickler.getFrequencyCode();           
                if (frequencyCode != null && !frequencyCode.isEmpty()) {
                    Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode); 
                    if (nextDate != null) {
                        tickler.setNextDueDate(nextDate);
                        businessObjectService.save(tickler);
                    }
                }
            }
        }
        
        // update FEE_NXT_PROC_DT, FEE_LST_PROC_DT in END_FEE_MTHD_T
        List<FeeMethod> feeMethodRecords = feeMethodDao.getAllFeeMethodWithNextPayDateEqualOrLessCurrentDate();
        for (FeeMethod feeMethod : feeMethodRecords) {
            Date feeNextProcessDate = feeMethod.getFeeNextProcessDate();            
            if (feeNextProcessDate != null && feeNextProcessDate.compareTo(currentDate) <= 0 ) {
                String frequencyCode = feeMethod.getFeeFrequencyCode();           
                if (frequencyCode != null && !frequencyCode.isEmpty()) {
                    feeMethod.setFeeLastProcessDate(feeNextProcessDate);
                    Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode); 
                    if (nextDate != null) {
                        feeMethod.setFeeNextProcessDate(nextDate);
                        businessObjectService.save(feeMethod);
                    }
                }
            }
        }
        
        // update NXT_PROC_DT, LST_PROC_DT in END_REC_CHS_XFR_T
        List<EndowmentRecurringCashTransfer> recurringCashTransferRecords = recurringCashTransferDao.getAllRecurringCashTransferWithNextPayDateEqualOrLessCurrentDate();
        for (EndowmentRecurringCashTransfer recurringCashTransfer : recurringCashTransferRecords) {
            Date nextProcessDate = recurringCashTransfer.getNextProcessDate();            
            if (nextProcessDate != null && nextProcessDate.compareTo(currentDate) <= 0 ) {
                String frequencyCode = recurringCashTransfer.getFrequencyCode();           
                if (frequencyCode != null && !frequencyCode.isEmpty()) {
                    recurringCashTransfer.setLastProcessDate(nextProcessDate);
                    Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode); 
                    if (nextDate != null) {
                        recurringCashTransfer.setNextProcessDate(nextDate);
                        businessObjectService.save(recurringCashTransfer);
                    }
                }
            }
        }
        
        return true;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) 
    {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the calculateProcessDateUsingFrequencyCodeService attribute value.
     * @param calculateProcessDateUsingFrequencyCodeService The calculateProcessDateUsingFrequencyCodeService to set.
     */
    public void setCalculateProcessDateUsingFrequencyCodeService(CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService) {
        this.calculateProcessDateUsingFrequencyCodeService = calculateProcessDateUsingFrequencyCodeService;
    }

    /**
     * Sets the securityDao attribute value.
     * @param securityDao The securityDao to set.
     */
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    /**
     * Sets the feeMethodDao attribute value.
     * @param feeMethodDao The feeMethodDao to set.
     */
    public void setFeeMethodDao(FeeMethodDao feeMethodDao) {
        this.feeMethodDao = feeMethodDao;
    }

    /**
     * Sets the ticklerDao attribute value.
     * @param ticklerDao The ticklerDao to set.
     */
    public void setTicklerDao(TicklerDao ticklerDao) {
        this.ticklerDao = ticklerDao;
    }

    /**
     * Sets the recurringCashTransferDao attribute value.
     * @param recurringCashTransferDao The recurringCashTransferDao to set.
     */
    public void setRecurringCashTransferDao(RecurringCashTransferDao recurringCashTransferDao) {
        this.recurringCashTransferDao = recurringCashTransferDao;
    }

    
}
