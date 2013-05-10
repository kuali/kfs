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

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.module.endow.batch.service.RollFrequencyDatesService;
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.dataaccess.AutomatedCashInvestmentModelDao;
import org.kuali.kfs.module.endow.dataaccess.CashSweepModelDao;
import org.kuali.kfs.module.endow.dataaccess.FeeMethodDao;
import org.kuali.kfs.module.endow.dataaccess.RecurringCashTransferDao;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.dataaccess.TicklerDao;
import org.kuali.kfs.module.endow.document.service.FrequencyDatesService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the RollFrequencyDatesService batch job.
 */
@Transactional
public class RollFrequencyDatesServiceImpl implements RollFrequencyDatesService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RollFrequencyDatesServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected KEMService kemService;
    protected FrequencyDatesService frequencyDatesService;

    protected SecurityDao securityDao;
    protected FeeMethodDao feeMethodDao;
    protected TicklerDao ticklerDao;
    protected RecurringCashTransferDao recurringCashTransferDao;
    protected AutomatedCashInvestmentModelDao automatedCashInvestmentModelDao;
    protected CashSweepModelDao cashSweepModelDao;

    protected ReportWriterService rollFrequencyDatesTotalReportWriterService;
    protected ReportWriterService rollFrequencyDatesExceptionReportWriterService;

    /**
     * Updates some date fields based on the frequency for the activity
     * 
     * @return true if the fields are updated successfully; false otherwise
     */
    public boolean updateFrequencyDate() {

        LOG.info("Begin the batch Roll Frequncy Dates ...");

        // update Security Income Next Pay Dates
        updateSecurityIncomeNextPayDates();

        // update Tickler Next Due Dates
        updateTicklerNextDueDates();

        // update Fee Method Next Process Dates
        updateFeeMethodProcessDates();

        // update Recurring Cash Transfer Next Process Dates
        updateRecurringCashTransferProcessDates();

        // update Cash Sweep Model Next Due Dates
        updateCashSweepModelNextDueDates();

        // update Cash Investment Model Next Due Dates
        updateAutomatedCashInvestmentModelNextDueDates();

        LOG.info("The batch Roll Frequncy Dates was finished.");

        return true;
    }

    /**
     * This method updates the income next pay dates in Security
     */
    protected boolean updateSecurityIncomeNextPayDates() {

        boolean success = true;

        int counter = 0;
        // get all the active security records whose next income pay date is equal to the current date
        List<Security> securityRecords = securityDao.getSecuritiesWithNextPayDateEqualToCurrentDate(kemService.getCurrentDate());
        if (securityRecords != null) {
            for (Security security : securityRecords) {

                Date incomeNextPayDate = security.getIncomeNextPayDate();

                // if maturity date is equals to income next pay date, do nothing
                Date maturityDate = security.getMaturityDate();
                if (ObjectUtils.isNotNull(maturityDate) && ObjectUtils.isNotNull(incomeNextPayDate)) {
                    if (maturityDate.compareTo(incomeNextPayDate) == 0) {
                        continue;
                    }
                }

                // replace income next date
                // first, with the next date calculated based on the frequency code
                // if it is invalid, with the dividend pay date
                String frequencyCode = security.getIncomePayFrequency();
                Date nextDate = frequencyDatesService.calculateNextDueDate(frequencyCode, kemService.getCurrentDate());
                if (nextDate == null) {
                    nextDate = security.getDividendPayDate();
                    if (ObjectUtils.isNull(nextDate) || (ObjectUtils.isNotNull(incomeNextPayDate) && nextDate.compareTo(incomeNextPayDate) == 0)) {
                        // we don't need to update income next pay date
                        continue;
                    }
                }
                // update income next pay date
                security.setIncomeNextPayDate(nextDate);
                if (updateBusinessObject(security)) {
                    counter++;
                    generateTotalReport("END_SEC_T", counter);
                }
                else {
                    LOG.error("Failed to update Security " + security.getId());
                    generateExceptionReport("END_SEC_T", security.getId());
                    success = false;
                }
            }
        }

        LOG.info("Total Security Income Next Pay Dates updated in END_SEC_T: " + counter);

        return success;
    }

    /**
     * This method updates the next due dates in Tickler
     */
    protected boolean updateTicklerNextDueDates() {

        boolean success = true;

        int counter = 0;
        List<Tickler> ticklerRecords = ticklerDao.getTicklerWithNextPayDateEqualToCurrentDate(kemService.getCurrentDate());
        if (ticklerRecords != null) {
            for (Tickler tickler : ticklerRecords) {
                String frequencyCode = tickler.getFrequencyCode();
                Date nextDate = frequencyDatesService.calculateNextDueDate(frequencyCode, kemService.getCurrentDate());
                if (nextDate != null) {
                    tickler.setNextDueDate(nextDate);
                    if (updateBusinessObject(tickler)) {
                        counter++;
                        generateTotalReport("END_TKLR_T", counter);
                    }
                    else {
                        LOG.error("Failed to update Tickler " + tickler.getNumber());
                        generateExceptionReport("END_TKLR_T", tickler.getNumber());
                        success = false;
                    }
                }
            }
        }

        LOG.info("Total Tickler Next Due Dates updated in END_TKLR_T: " + counter);

        return success;
    }

    /**
     * This method updates the next process dates in FeeMethod
     */
    protected boolean updateFeeMethodProcessDates() {

        boolean success = true;

        int counter = 0;
        List<FeeMethod> feeMethodRecords = feeMethodDao.getFeeMethodWithNextPayDateEqualToCurrentDate(kemService.getCurrentDate());
        if (feeMethodRecords != null) {
            for (FeeMethod feeMethod : feeMethodRecords) {
                String frequencyCode = feeMethod.getFeeFrequencyCode();
                Date nextDate = frequencyDatesService.calculateNextDueDate(frequencyCode, kemService.getCurrentDate());
                if (nextDate != null) {
                    feeMethod.setFeeLastProcessDate(feeMethod.getFeeNextProcessDate());
                    feeMethod.setFeeNextProcessDate(nextDate);
                    if (updateBusinessObject(feeMethod)) {
                        counter++;
                        generateTotalReport("END_FEE_MTHD_T", counter);
                    }
                    else {
                        LOG.error("Failed to update FeeMethod " + feeMethod.getCode());
                        generateExceptionReport("END_FEE_MTHD_T", feeMethod.getCode());
                        success = false;
                    }
                }
            }
        }

        LOG.info("Total Fee Next Process Dates and Fee Last Process Dates updated in END_FEE_MTHD_T: " + counter);

        return success;
    }

    /**
     * This method updates the next process dates in EndowmentRecurringCashTransfer
     */
    protected boolean updateRecurringCashTransferProcessDates() {

        boolean success = true;

        int counter = 0;
        List<EndowmentRecurringCashTransfer> recurringCashTransferRecords = recurringCashTransferDao.getRecurringCashTransferWithNextPayDateEqualToCurrentDate(kemService.getCurrentDate());
        if (recurringCashTransferRecords != null) {
            for (EndowmentRecurringCashTransfer recurringCashTransfer : recurringCashTransferRecords) {
                String frequencyCode = recurringCashTransfer.getFrequencyCode();
                Date nextDate = frequencyDatesService.calculateNextDueDate(frequencyCode, kemService.getCurrentDate());
                if (nextDate != null) {
                    recurringCashTransfer.setLastProcessDate(recurringCashTransfer.getNextProcessDate());
                    recurringCashTransfer.setNextProcessDate(nextDate);
                    if (updateBusinessObject(recurringCashTransfer)) {
                        counter++;
                        generateTotalReport("END_REC_CSH_XFR_T", counter);
                    }
                    else {
                        LOG.error("Failed to update EndowmentRecurringCashTransfer " + recurringCashTransfer.getTransferNumber());
                        generateExceptionReport("END_REC_CSH_XFR_T", recurringCashTransfer.getTransferNumber());
                        success = false;
                    }
                }
            }
        }

        LOG.info("Total Next Process Dates and Last Process Dates updated in END_REC_CSH_XFR_T: " + counter);

        return success;
    }

    protected boolean updateCashSweepModelNextDueDates() {

        boolean success = true;

        int counter = 0;
        List<CashSweepModel> csmRecords = cashSweepModelDao.getCashSweepModelWithNextPayDateEqualToCurrentDate(kemService.getCurrentDate());
        if (csmRecords != null) {
            for (CashSweepModel csm : csmRecords) {
                String frequencyCode = csm.getCashSweepFrequencyCode();
                Date nextDate = frequencyDatesService.calculateNextDueDate(frequencyCode, kemService.getCurrentDate());
                if (nextDate != null) {
                    csm.setCashSweepNextDueDate(nextDate);
                    if (updateBusinessObject(csm)) {
                        counter++;
                        generateTotalReport("END_CSH_SWEEP_MDL_T", counter);
                    }
                    else {
                        LOG.error("Failed to update FeeMethod " + csm.getCashSweepModelID());
                        generateExceptionReport("END_CSH_SWEEP_MDL_T", csm.getCashSweepModelID().toString());
                        success = false;
                    }
                }
            }
        }

        LOG.info("Total Cash Sweep Model Next Due Dates updated in END_CSH_SWEEP_MDL_T: " + counter);

        return success;
    }

    protected boolean updateAutomatedCashInvestmentModelNextDueDates() {

        boolean success = true;

        int counter = 0;
        List<AutomatedCashInvestmentModel> aciRecords = automatedCashInvestmentModelDao.getAutomatedCashInvestmentModelWithNextPayDateEqualToCurrentDate(kemService.getCurrentDate());
        if (aciRecords != null) {
            for (AutomatedCashInvestmentModel aci : aciRecords) {
                String frequencyCode = aci.getAciFrequencyCode();
                Date nextDate = frequencyDatesService.calculateNextDueDate(frequencyCode, kemService.getCurrentDate());
                if (nextDate != null) {
                    aci.setAciNextDueDate(nextDate);
                    if (updateBusinessObject(aci)) {
                        counter++;
                        generateTotalReport("END_AUTO_CSH_INVEST_MDL_T", counter);
                    }
                    else {
                        LOG.error("Failed to update FeeMethod " + aci.getAciModelID());
                        generateExceptionReport("END_AUTO_CSH_INVEST_MDL_T", aci.getAciModelID().toString());
                        success = false;
                    }
                }
            }
        }

        LOG.info("Total ACI Next Due Dates updated in END_AUTO_CSH_INVEST_MDL_T: " + counter);

        return success;
    }

    /**
     * Generates the statistic report for updated tables
     * 
     * @param tableName
     * @param counter
     */
    protected void generateTotalReport(String tableName, int counter) {

        try {
            rollFrequencyDatesTotalReportWriterService.writeFormattedMessageLine(tableName + ": %s", counter);
        }
        catch (Exception e) {
            LOG.error("Failed to generate the statistic report: " + e.getMessage());
            rollFrequencyDatesExceptionReportWriterService.writeFormattedMessageLine("Failed to generate the total report: " + e.getMessage());
        }
    }

    /**
     * Generates the exception report
     * 
     * @param tableName
     * @param counter
     */
    protected void generateExceptionReport(String tableName, String errorMessage) {

        try {
            rollFrequencyDatesExceptionReportWriterService.writeFormattedMessageLine(tableName + ": %s", errorMessage);            
        } catch (Exception e) {
            LOG.error("Failed to generate the exception report.",e);
        }
    }

    protected void initializeReports() {
        rollFrequencyDatesTotalReportWriterService.writeSubTitle("<rollFrequencyDatesJob> Number of Records Updated");
        rollFrequencyDatesTotalReportWriterService.writeNewLines(1);

        rollFrequencyDatesExceptionReportWriterService.writeSubTitle("<rollFrequencyDatesJob> Records Failed for update");
        rollFrequencyDatesExceptionReportWriterService.writeNewLines(1);
    }

    /**
     * Updates business object
     * 
     * @param businessObject
     * @return boolean
     */
    protected boolean updateBusinessObject(PersistableBusinessObject businessObject) {

        boolean result = true;
        try {
            businessObjectService.save(businessObject);
        } catch (Exception e) { // such as IllegalArgumentException
            LOG.error("Unable to save " + businessObject, e);
            result = false;
        }

        return result;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the kemService attribute value.
     * 
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the securityDao attribute value.
     * 
     * @param securityDao The securityDao to set.
     */
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    /**
     * Sets the feeMethodDao attribute value.
     * 
     * @param feeMethodDao The feeMethodDao to set.
     */
    public void setFeeMethodDao(FeeMethodDao feeMethodDao) {
        this.feeMethodDao = feeMethodDao;
    }

    /**
     * Sets the ticklerDao attribute value.
     * 
     * @param ticklerDao The ticklerDao to set.
     */
    public void setTicklerDao(TicklerDao ticklerDao) {
        this.ticklerDao = ticklerDao;
    }

    /**
     * Sets the recurringCashTransferDao attribute value.
     * 
     * @param recurringCashTransferDao The recurringCashTransferDao to set.
     */
    public void setRecurringCashTransferDao(RecurringCashTransferDao recurringCashTransferDao) {
        this.recurringCashTransferDao = recurringCashTransferDao;
    }

    /**
     * Sets the rollFrequencyDatesTotalReportWriterService attribute value.
     * 
     * @param rollFrequencyDatesTotalReportWriterService The rollFrequencyDatesTotalReportWriterService to set.
     */
    public void setRollFrequencyDatesTotalReportWriterService(ReportWriterService rollFrequencyDatesTotalReportWriterService) {
        this.rollFrequencyDatesTotalReportWriterService = rollFrequencyDatesTotalReportWriterService;
    }

    /**
     * Sets the rollFrequencyDatesExceptionReportWriterService attribute value.
     * 
     * @param rollFrequencyDatesExceptionReportWriterService The rollFrequencyDatesExceptionReportWriterService to set.
     */
    public void setRollFrequencyDatesExceptionReportWriterService(ReportWriterService rollFrequencyDatesExceptionReportWriterService) {
        this.rollFrequencyDatesExceptionReportWriterService = rollFrequencyDatesExceptionReportWriterService;
    }

    /**
     * Sets the automatedCashInvestmentModelDao attribute value.
     * 
     * @param automatedCashInvestmentModelDao The automatedCashInvestmentModelDao to set.
     */
    public void setAutomatedCashInvestmentModelDao(AutomatedCashInvestmentModelDao automatedCashInvestmentModelDao) {
        this.automatedCashInvestmentModelDao = automatedCashInvestmentModelDao;
    }

    /**
     * Sets the cashSweepModelDao attribute value.
     * 
     * @param cashSweepModelDao The cashSweepModelDao to set.
     */
    public void setCashSweepModelDao(CashSweepModelDao cashSweepModelDao) {
        this.cashSweepModelDao = cashSweepModelDao;
    }

    /**
     * Gets the frequencyDatesService attribute.
     * 
     * @return Returns the frequencyDatesService.
     */
    protected FrequencyDatesService getFrequencyDatesService() {
        return frequencyDatesService;
    }

    /**
     * Sets the frequencyDatesService attribute value.
     * 
     * @param frequencyDatesService The frequencyDatesService to set.
     */
    public void setFrequencyDatesService(FrequencyDatesService frequencyDatesService) {
        this.frequencyDatesService = frequencyDatesService;
    }
}
