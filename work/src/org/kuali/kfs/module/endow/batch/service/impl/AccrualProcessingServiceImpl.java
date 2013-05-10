/*
4 * Copyright 2010 The Kuali Foundation.
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.service.AccrualProcessingService;
import org.kuali.kfs.module.endow.businessobject.AccrualsProcessingTotalReportLine;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AccrualProcessingServiceImpl implements AccrualProcessingService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccrualProcessingServiceImpl.class);

    private ClassCodeService classCodeService;
    private SecurityService securityService;
    private HoldingTaxLotService holdingTaxLotService;
    private KEMService kemService;
    private BusinessObjectService businessObjectService;

    private ReportWriterService accrualProcessingReportWriterService;
    private AccrualsProcessingTotalReportLine totalReportLine = null;

    private boolean isFistTimeForWritingTotalReport = true;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.AccrualProcessingService#processAccruals()
     */
    public boolean processAccruals() {

        boolean success = true;
        List<Security> securities = getSecuritiesToProcess();

        for (Security security : securities) {
            if (EndowConstants.AccrualMethod.AUTOMATED_CASH_MANAGEMENT.equals(security.getClassCode().getAccrualMethod().getCode())) {
                processAccrualForAutomatedCashManagement(security);
            }
            if (EndowConstants.AccrualMethod.TIME_DEPOSITS.equals(security.getClassCode().getAccrualMethod().getCode())) {
                processAccrualForTimeDeposits(security);
            }
            if (EndowConstants.AccrualMethod.TREASURY_NOTES_AND_BONDS.equals(security.getClassCode().getAccrualMethod().getCode())) {

                boolean skipEntry = false;

                // IF the END_SEC_T: SEC_MAT_DT is less than the current date, skip the record and do not accrue income.
                if (security.getMaturityDate() != null && security.getMaturityDate().before(kemService.getCurrentDate())) {
                    skipEntry = true;
                }

                if (!skipEntry) {
                    processAccrualForTreasuryNotesAndBonds(security);
                }
            }
            if (EndowConstants.AccrualMethod.DIVIDENDS.equals(security.getClassCode().getAccrualMethod().getCode())) {
                processAccrualForDividends(security);
            }
        }

        return success;
    }

    /**
     * Gets all the securities for which the class code has an accrual method of Automated Cash Management, Time Deposits, Treasury
     * Notes and Bonds or Dividends.
     * 
     * @return all securities that meet the criteria
     */
    protected List<Security> getSecuritiesToProcess() {
        LOG.info("Get all securities for which the class codes have an accrual method of Automated Cash Management, Time Deposits, Treasury Notes and Bonds or Dividends.");

        // get all class codes with an accrual method of Automated Cash Management, Time Deposits, Treasury Notes and Bonds,
        // Dividends
        Collection<ClassCode> classCodes = classCodeService.getClassCodesForAccrualProcessing();
        List<String> classCodesForAccrualProc = new ArrayList<String>();

        for (ClassCode classCode : classCodes) {
            classCodesForAccrualProc.add(classCode.getCode());
        }

        // get all securities with units greater than zero that have a class code in the above list
        List<Security> securities = securityService.getSecuritiesByClassCodeWithUnitsGreaterThanZero(classCodesForAccrualProc);

        LOG.info("Number of securities with class codes have an accrual method of Automated Cash Management, Time Deposits, Treasury Notes and Bonds or Dividends = " + securities.size());

        return securities;
    }

    /**
     * Processes the accrual for securities that have SEC_ACRL_MTHD equal to A. Captures the END_SEC_T: SEC_RT for the security
     * record. For each END_HLDG_TAX_LOT_T record for the security where the HLDG_UNITS are greater than zero calculates the accrual
     * amount as (END_HLDG_TAX_LOT_T: HLDG_UNITS times END_SEC_T: SEC_RT) divided by the number of days in the calendar year (365 or
     * 366) Add the calculated accrual amount to the value in END_HLDG_TAX_LOT_T: HLDG_ACCRD-INC_DUE.
     * 
     * @param security
     */
    protected void processAccrualForAutomatedCashManagement(Security security) {

        LOG.debug("Calculate accruals for securities that have accrual method Automated Cash Management.");

        BigDecimal securityRate = security.getIncomeRate();
        String accrualMethodName = security.getClassCode().getAccrualMethod().getName();
        List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(security.getId());

        if (holdingTaxLots != null) {
            // totals reporting
            initializeTotalReportLine(security.getId(), accrualMethodName);
            if (isFistTimeForWritingTotalReport) {
                accrualProcessingReportWriterService.writeTableHeader(totalReportLine);
                isFistTimeForWritingTotalReport = false;
            }

            // acrual processing
            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                // compute accrual amount= (security rate * holding units)/nr of days in year
                BigDecimal accrualAmount = securityRate.multiply(holdingTaxLot.getUnits());
                accrualAmount = KEMCalculationRoundingHelper.divide(accrualAmount, new BigDecimal(kemService.getNumberOfDaysInCalendarYear()), 5);
                // set holding tax lot new accrual amount
                holdingTaxLot.setCurrentAccrual(holdingTaxLot.getCurrentAccrual().add(accrualAmount));
                // save updated tax lot
                businessObjectService.save(holdingTaxLot);

                // update total reporting
                totalReportLine.addAccrualAmount(accrualAmount);
            }
            // write total report line
            accrualProcessingReportWriterService.writeTableRow(totalReportLine);

            if(LOG.isDebugEnabled()) {
                LOG.debug("Number of tax lots that have accrual amount updated for secirity id = " + security.getId() + " with accrual method = Automated Cash Management is " + holdingTaxLots.size());
            }
        }


    }

    /**
     * Processes accrual for securities with SEC_ACRL_MTHD equal to M. Capture the END_SEC_T: SEC_RT for the security record. For
     * each END_HLDG_TAX_LOT_T record for the security where the HLDG_UNITS are greater than zero calculate the accrual amount as
     * (END_HLDG_TAX_LOT_T: HLDG_UNITS times END_SEC_T: SEC_RT) divided by the number of days in the calendar year (365 or 366) Add
     * the calculated accrual amount to the value in END_HLDG_TAX_LOT_T: HLDG_ACCRD-INC_DUE.
     * 
     * @param security
     */
    protected void processAccrualForTimeDeposits(Security security) {

        LOG.debug("Calculate accruals for securities that have accrual method Time Deposits.");

        BigDecimal securityRate = security.getIncomeRate();
        String accrualMethodName = security.getClassCode().getAccrualMethod().getName();
        List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(security.getId());

        if (holdingTaxLots != null) {
            // totals reporting
            initializeTotalReportLine(security.getId(), accrualMethodName);
            if (isFistTimeForWritingTotalReport) {
                accrualProcessingReportWriterService.writeTableHeader(totalReportLine);
                isFistTimeForWritingTotalReport = false;
            }

            // accrual processing
            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                // compute accrual amount= (security rate * holding units)/nr of days in year
                BigDecimal accrualAmount = securityRate.multiply(holdingTaxLot.getUnits());
                accrualAmount = KEMCalculationRoundingHelper.divide(accrualAmount, new BigDecimal(kemService.getNumberOfDaysInCalendarYear()), 5);
                // set holding tax lot new accrual amount
                holdingTaxLot.setCurrentAccrual(holdingTaxLot.getCurrentAccrual().add(accrualAmount));
                // save updated tax lot
                businessObjectService.save(holdingTaxLot);
                // update total reporting
                totalReportLine.addAccrualAmount(accrualAmount);
            }
            // write total report line
            accrualProcessingReportWriterService.writeTableRow(totalReportLine);

            if(LOG.isDebugEnabled()) {
                LOG.debug("Number of tax lots that have accrual amount updated for secirity id = " + security.getId() + " with accrual method = Time Deposits is " + holdingTaxLots.size());
            }
        }

    }

    /**
     * Processes accrual for securities with SEC_ACRL_MTHD equal to T. From the END_SEC_T: record, 1. Capture the END_SEC_T: SEC_RT
     * for the security record 2. Establish the calculation interval by counting the number of days between the last date income was
     * paid until the next (END_SEC_T: SEC_INC_PAY_DT) using the frequency code for the security (END_SEC_T: SEC_INC_PAY_FREQ). For
     * each END_HLDG_TAX_LOT_T record for the security where the HLDG_UNITS are greater than zero calculate the accrual amount as
     * (END_HLDG_TAX_LOT_T: HLDG_UNITS) times [(END_SEC_T: SEC_RT) divided by 2) divided by the number of days in the calculation
     * interval. Add the calculated accrual amount to the value in END_HLDG_TAX_LOT_T: HLDG_ACCRD-INC_DUE.
     * 
     * @param security
     */
    protected void processAccrualForTreasuryNotesAndBonds(Security security) {

        LOG.debug("Calculate accruals for securities that have accrual method Treasury Notes and Bonds.");

        BigDecimal securityRate = security.getIncomeRate();
        Date nextIncomePayDate = security.getIncomeNextPayDate();
        String incomePayFrequency = security.getIncomePayFrequency();
        String accrualMethodName = security.getClassCode().getAccrualMethod().getName();

        // if security has an income pay frequency set
        if (incomePayFrequency != null && !incomePayFrequency.isEmpty()) {
            // compute the number of days since last time income was paid
            long nrOfDays = getNumberOfDaysSinceLastDateIncomeWasPaid(incomePayFrequency, nextIncomePayDate);
            // get all tax lots for security that have units greater than zero
            List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(security.getId());

            if (holdingTaxLots != null) {

                // totals reporting
                initializeTotalReportLine(security.getId(), accrualMethodName);
                if (isFistTimeForWritingTotalReport) {
                    accrualProcessingReportWriterService.writeTableHeader(totalReportLine);
                    isFistTimeForWritingTotalReport = false;
                }

                // accruals processing
                for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {

                    // compute accrual amount as ((holding units * security rate)/2)/number of days since last income paid date
                    BigDecimal accrualAmount = (holdingTaxLot.getUnits().multiply(securityRate));
                    accrualAmount = KEMCalculationRoundingHelper.divide(accrualAmount, new BigDecimal(2), 5);
                    accrualAmount = KEMCalculationRoundingHelper.divide(accrualAmount, new BigDecimal(nrOfDays), 5);

                    // set holding tax lot new accrual amount
                    holdingTaxLot.setCurrentAccrual(holdingTaxLot.getCurrentAccrual().add(accrualAmount));
                    // save updated tax lot
                    businessObjectService.save(holdingTaxLot);

                    // update total reporting
                    totalReportLine.addAccrualAmount(accrualAmount);
                }

                // write total reporting line
                accrualProcessingReportWriterService.writeTableRow(totalReportLine);

                if(LOG.isDebugEnabled()) {
                    LOG.debug("Number of tax lots that have accrual amount updated for secirity id = " + security.getId() + " with accrual method = Treasury Notes and Bonds is " + holdingTaxLots.size());
                }
            }

        }

    }

    /**
     * Gets the number of days since the last date the income was paid based on a frequency code and the next payment date.
     * 
     * @param frequencyCode
     * @param nextIncomePayDate
     * @return number of days since the last date the income was paid
     */
    protected long getNumberOfDaysSinceLastDateIncomeWasPaid(String incomePayFrequency, Date nextIncomePayDate) {
        long nrOfDays = 0;
        long milPerDay = 1000 * 60 * 60 * 24;

        String frequencyType = incomePayFrequency.substring(0, 1);

        // since this method is only used for treasury notes and bonds we only and Income is paid on these instruments semiannually
        // we are only interested in semi annually frequency type
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_ANNUALLY)) {

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(nextIncomePayDate);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(nextIncomePayDate);
            calendar2.add(Calendar.MONTH, -6);

            // where there is daylight savings time, one day is 23 hours long and another is 25 hours long so we need to include
            // offset to get the correct number of days; If the dates are in the spring around the "spring forward" date and in the
            // autumn around the "fall back" date we get inaccurate calculations without using the offset
            long endL = calendar1.getTimeInMillis() + calendar1.getTimeZone().getOffset(calendar1.getTimeInMillis());
            long startL = calendar2.getTimeInMillis() + calendar2.getTimeZone().getOffset(calendar2.getTimeInMillis());

            nrOfDays = (endL - startL) / milPerDay;
            return nrOfDays;
        }

        return nrOfDays;
    }

    /**
     * Processes accruals for securities with SEC_ACRL_MTHD equal to D. Select END_SEC_T: records where SEC_EX_DVDND_DT is equal to
     * the current date. From the END_SEC_T: record, capture the SEC_DVDND_AMT. For each END_HLDG_TAX_LOT_T record for the security
     * where the HLDG_UNITS are greater than zero calculate the accrual amount as (END_HLDG_TAX_LOT_T: HLDG_UNITS) times (END_SEC_T:
     * SEC_DVDND_AMT). Add the calculated accrual amount to the value in END_HLDG_TAX_LOT_T: HLDG_ACCRD-INC_DUE.
     * 
     * @param security
     * @return
     */
    protected void processAccrualForDividends(Security security) {

        LOG.debug("Calculate accruals for securities that have accrual method Dividends.");

        // get security ex divident date
        Date securityExDividendDate = security.getExDividendDate();
        String accrualMethodName = security.getClassCode().getAccrualMethod().getName();

        // if security ex dividend date is equal to current date process accruals, otherwise do nothing
        if (kemService.getCurrentDate().equals(securityExDividendDate)) {
            BigDecimal securityDividendAmount = security.getDividendAmount();
            List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(security.getId());

            if (holdingTaxLots != null) {

                // totals reporting
                initializeTotalReportLine(security.getId(), accrualMethodName);
                if (isFistTimeForWritingTotalReport) {
                    accrualProcessingReportWriterService.writeTableHeader(totalReportLine);
                    isFistTimeForWritingTotalReport = false;
                }

                // accrual processing
                for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                    // calculate the accrual amound as: holding units * security dividend amount
                    BigDecimal accrualAmount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), securityDividendAmount, 5);
                    // set holding tax lot new accrual amount
                    holdingTaxLot.setCurrentAccrual(holdingTaxLot.getCurrentAccrual().add(accrualAmount));
                    // save updated tax lot
                    businessObjectService.save(holdingTaxLot);

                    // update totals reporting
                    totalReportLine.addAccrualAmount(accrualAmount);
                }
                // write total reporting line
                accrualProcessingReportWriterService.writeTableRow(totalReportLine);

                if(LOG.isDebugEnabled()) {
                    LOG.debug("Number of tax lots that have accrual amount updated for security id = " + security.getId() + " with accrual method = Dividends is " + holdingTaxLots.size());
                }
            }
        }
    }

    /**
     * Sets the classCodeService.
     * 
     * @param classCodeService
     */
    public void setClassCodeService(ClassCodeService classCodeService) {
        this.classCodeService = classCodeService;
    }

    /**
     * Sets the holdingTaxLotService.
     * 
     * @param holdingTaxLotService
     */
    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
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

    /**
     * Sets the securityService.
     * 
     * @param securityService
     */
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Sets the accrualProcessingReportWriterService.
     * 
     * @param accrualProcessingReportWriterService
     */
    public void setAccrualProcessingReportWriterService(ReportWriterService accrualProcessingReportWriterService) {
        this.accrualProcessingReportWriterService = accrualProcessingReportWriterService;
    }

    /**
     * Creates a new AccrualsProcessingTotalReportLine.
     * 
     * @param securityId
     * @param accrualMethod
     */
    protected void initializeTotalReportLine(String securityId, String accrualMethod) {
        // create a new totalReportLine
        this.totalReportLine = new AccrualsProcessingTotalReportLine(securityId, accrualMethod);
    }
}
