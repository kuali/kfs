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
import java.util.List;

import org.kuali.kfs.module.endow.batch.service.RollFrequencyDatesService;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentTotalReportLine;
import org.kuali.kfs.module.endow.businessobject.lookup.CalculateProcessDateUsingFrequencyCodeService;
import org.kuali.kfs.module.endow.dataaccess.FeeMethodDao;
import org.kuali.kfs.module.endow.dataaccess.RecurringCashTransferDao;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.dataaccess.TicklerDao;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the RollFrequencyDatesService batch job.
 */
@Transactional
public class RollFrequencyDatesServiceImpl implements RollFrequencyDatesService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RollFrequencyDatesServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService; 
    
    protected SecurityDao securityDao;
    protected FeeMethodDao feeMethodDao;
    protected TicklerDao ticklerDao;
    protected RecurringCashTransferDao recurringCashTransferDao;
    
    protected ReportWriterService rollFrequencyDatesReportWriterService;
    
    /**
     * Updates some date fields based on the frequency for the activity 
     * @return true if the fields are updated successfully; false otherwise
     */
    public boolean updateFrequencyDate() {
        
        LOG.info("Begin the batch Roll Frequncy Dates ...");
        
        // update Security Income Next Pay Dates
        updateSecurityIncomeNextPayDates();
                       
        // update Tickler Next Due Dates
        updateTicklerNextDueDates();
        
        // update Fee Next Process Dates
        updateFeeProcessDates();
        
        // update Next Process Dates
        updateProcessDates();
        
        LOG.info("The batch Roll Frequncy Dates was finished.");
        
        return true;
    }

    /**
     * This method updates the income next pay dates in Security
     */
    protected void updateSecurityIncomeNextPayDates() {
        
        int counter = 0;
        List<Security> securityRecords = securityDao.getSecuritiesWithNextPayDateEqualToCurrentDate();
        for (Security security : securityRecords) {
            String frequencyCode = security.getIncomePayFrequency();           
            if (frequencyCode != null && !frequencyCode.isEmpty()) {
                Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode);
                if (nextDate != null) {
                    security.setIncomeNextPayDate(nextDate);
                    businessObjectService.save(security);
                    counter++;
                }
            }
        }
        generateReport("END_SEC_T", counter, true);
        LOG.info("Total Security Income Next Pay Dates updated: " + counter); 
    }
    
    /**
     * This method updates the next due dates in Tickler
     */
    protected void updateTicklerNextDueDates() {
        
        int counter = 0;
        List<Tickler> TicklerRecords = ticklerDao.getTicklerWithNextPayDateEqualToCurrentDate();
        for (Tickler tickler : TicklerRecords) {
            String frequencyCode = tickler.getFrequencyCode();           
            if (frequencyCode != null && !frequencyCode.isEmpty()) {
                Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    tickler.setNextDueDate(nextDate);
                    businessObjectService.save(tickler);
                    counter++;
                }
            }
        }
        generateReport("END_TKLR_T", counter, false);
        LOG.info("Total Tickler Next Due Dates updated: " + counter);
    }
    
    /**
     * This method updates the next process dates in FeeMethod
     */
    protected void updateFeeProcessDates() {
        
        int counter = 0;
        List<FeeMethod> feeMethodRecords = feeMethodDao.getFeeMethodWithNextPayDateEqualToCurrentDate();
        for (FeeMethod feeMethod : feeMethodRecords) {                        
            String frequencyCode = feeMethod.getFeeFrequencyCode();           
            if (frequencyCode != null && !frequencyCode.isEmpty()) {                
                Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    feeMethod.setFeeLastProcessDate(feeMethod.getFeeNextProcessDate());
                    feeMethod.setFeeNextProcessDate(nextDate);
                    businessObjectService.save(feeMethod);
                    counter++;
                }
            }
        }
        generateReport("END_FEE_MTHD_T", counter, false);
        LOG.info("Total Fee Next Process Dates and Fee Last Process Dates updated: " + counter);
    }
    
    /**
     * This method updates the next process dates in EndowmentRecurringCashTransfer
     */
    protected void updateProcessDates() {
        
        int counter = 0;
        List<EndowmentRecurringCashTransfer> recurringCashTransferRecords = recurringCashTransferDao.getRecurringCashTransferWithNextPayDateEqualToCurrentDate();
        for (EndowmentRecurringCashTransfer recurringCashTransfer : recurringCashTransferRecords) {                       
            String frequencyCode = recurringCashTransfer.getFrequencyCode();           
            if (frequencyCode != null && !frequencyCode.isEmpty()) {                
                Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    recurringCashTransfer.setLastProcessDate(recurringCashTransfer.getNextProcessDate());
                    recurringCashTransfer.setNextProcessDate(nextDate);
                    businessObjectService.save(recurringCashTransfer);
                    counter++;
                }
            }
        }
        generateReport("END_REC_CSH_XFR_T", counter, false);
        LOG.info("Total Next Process Dates and Last Process Dates updated: " + counter);
    }

    protected <T extends EndowmentSecurityDetailsDocumentBase >void generateReport(String tableName, int counter, boolean isFirstReport) {
        
        try {
            if (isFirstReport) {
                rollFrequencyDatesReportWriterService.writeSubTitle("<rollFrequencyDatesJob> Number of Records Updated");
                rollFrequencyDatesReportWriterService.writeNewLines(1);
                isFirstReport = false;
            }
            rollFrequencyDatesReportWriterService.writeFormattedMessageLine(tableName + ": %s", counter);
            
        } catch (Exception e) {
            LOG.error("Failed to generate the statistic report: " + e.getMessage());
            rollFrequencyDatesReportWriterService.writeFormattedMessageLine("Failed to generate the statistic report: " + e.getMessage());
        }
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

    /**
     * Sets the rollFrequencyDatesReportWriterService attribute value.
     * @param rollFrequencyDatesReportWriterService The rollFrequencyDatesReportWriterService to set.
     */
    public void setRollFrequencyDatesReportWriterService(ReportWriterService rollFrequencyDatesReportWriterService) {
        this.rollFrequencyDatesReportWriterService = rollFrequencyDatesReportWriterService;
    }
    
}
