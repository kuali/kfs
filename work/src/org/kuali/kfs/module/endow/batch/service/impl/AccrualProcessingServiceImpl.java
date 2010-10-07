/*
4 * Copyright 2010 The Kuali Foundation.
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
import org.kuali.rice.kns.service.BusinessObjectService;
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
                processAccrualForTreasuryNotesAndBonds(security);
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
    private List<Security> getSecuritiesToProcess() {
        LOG.info("Get all securities for which the class codes have an accrual method of Automated Cash Management, Time Deposits, Treasury Notes and Bonds or Dividends.");

        Collection<ClassCode> classCodes = classCodeService.getClassCodesForAccrualProcessing();
        List<String> classCodesForAccrualProc = new ArrayList<String>();

        for (ClassCode classCode : classCodes) {
            classCodesForAccrualProc.add(classCode.getCode());
        }
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
    private void processAccrualForAutomatedCashManagement(Security security) {

        LOG.info("Calculate accruals for securities that have accrual method Automated Cash Management.");

        BigDecimal securityRate = security.getIncomeRate();
        String accrualMethodName = security.getClassCode().getAccrualMethod().getName();
        List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(security.getId());

        if (holdingTaxLots != null) {

            initializeTotalReportLine(security.getId(), accrualMethodName);
            if (isFistTimeForWritingTotalReport) {
                accrualProcessingReportWriterService.writeTableHeader(totalReportLine);
                isFistTimeForWritingTotalReport = false;
            }

            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                BigDecimal accrualAmount = securityRate.multiply(holdingTaxLot.getUnits());
                accrualAmount = KEMCalculationRoundingHelper.divide(accrualAmount, new BigDecimal(kemService.getNumberOfDaysInCalendarYear()), 5);
                holdingTaxLot.setCurrentAccrual(holdingTaxLot.getCurrentAccrual().add(accrualAmount));
                businessObjectService.save(holdingTaxLot);

                totalReportLine.addAccrualAmount(accrualAmount);
            }

            accrualProcessingReportWriterService.writeTableRow(totalReportLine);

            LOG.info("Number of tax lots that have accrual amount updated for secirity id = " + security.getId() + " with accrual method = Automated Cash Management is " + holdingTaxLots.size());
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
    private void processAccrualForTimeDeposits(Security security) {

        LOG.info("Calculate accruals for securities that have accrual method Time Deposits.");

        BigDecimal securityRate = security.getIncomeRate();
        String accrualMethodName = security.getClassCode().getAccrualMethod().getName();
        List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(security.getId());

        if (holdingTaxLots != null) {
            initializeTotalReportLine(security.getId(), accrualMethodName);
            if (isFistTimeForWritingTotalReport) {
                accrualProcessingReportWriterService.writeTableHeader(totalReportLine);
                isFistTimeForWritingTotalReport = false;
            }

            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                BigDecimal accrualAmount = securityRate.multiply(holdingTaxLot.getUnits());
                accrualAmount = KEMCalculationRoundingHelper.divide(accrualAmount, new BigDecimal(kemService.getNumberOfDaysInCalendarYear()), 5);
                holdingTaxLot.setCurrentAccrual(holdingTaxLot.getCurrentAccrual().add(accrualAmount));
                businessObjectService.save(holdingTaxLot);

                totalReportLine.addAccrualAmount(accrualAmount);
            }

            accrualProcessingReportWriterService.writeTableRow(totalReportLine);

            LOG.info("Number of tax lots that have accrual amount updated for secirity id = " + security.getId() + " with accrual method = Time Deposits is " + holdingTaxLots.size());
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
    private void processAccrualForTreasuryNotesAndBonds(Security security) {

        LOG.info("Calculate accruals for securities that have accrual method Treasury Notes and Bonds.");

        BigDecimal securityRate = security.getIncomeRate();
        Date nextIncomePayDate = security.getIncomeNextPayDate();
        String incomePayFrequency = security.getIncomePayFrequency();
        String accrualMethodName = security.getClassCode().getAccrualMethod().getName();

        if (incomePayFrequency != null && !incomePayFrequency.isEmpty()) {
            int nrOfDays = getNumberOfDaysSinceLastDateIncomeWasPaid(incomePayFrequency, nextIncomePayDate);
            List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(security.getId());

            if (holdingTaxLots != null) {

                initializeTotalReportLine(security.getId(), accrualMethodName);
                if (isFistTimeForWritingTotalReport) {
                    accrualProcessingReportWriterService.writeTableHeader(totalReportLine);
                    isFistTimeForWritingTotalReport = false;
                }

                for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {

                    BigDecimal accrualAmount = (holdingTaxLot.getUnits().multiply(securityRate));
                    accrualAmount = KEMCalculationRoundingHelper.divide(accrualAmount, new BigDecimal(2), 5);
                    accrualAmount = KEMCalculationRoundingHelper.divide(accrualAmount, new BigDecimal(nrOfDays), 5);
                    holdingTaxLot.setCurrentAccrual(holdingTaxLot.getCurrentAccrual().add(accrualAmount));
                    businessObjectService.save(holdingTaxLot);

                    totalReportLine.addAccrualAmount(accrualAmount);
                }

                accrualProcessingReportWriterService.writeTableRow(totalReportLine);

                LOG.info("Number of tax lots that have accrual amount updated for secirity id = " + security.getId() + " with accrual method = Treasury Notes and Bonds is " + holdingTaxLots.size());
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
    private int getNumberOfDaysSinceLastDateIncomeWasPaid(String incomePayFrequency, Date nextIncomePayDate) {
        int nrOfDays = 0;

        String frequencyType = incomePayFrequency.substring(0, 1);

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.DAILY)) {
            return 1;
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.WEEKLY)) {
            return 7;
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_MONTHLY)) {
            return 15;
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.MONTHLY)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nextIncomePayDate);
            calendar.add(Calendar.MONTH, -1);
            return calendar.getMaximum(Calendar.MONTH);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.QUARTERLY)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nextIncomePayDate);
            int days = 0;
            for (int i = 0; i < 3; i++) {
                calendar.add(Calendar.MONTH, -1);
                days += calendar.getMaximum(Calendar.MONTH);
            }

            return days;

        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_ANNUALLY)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nextIncomePayDate);
            int days = 0;
            for (int i = 0; i < 6; i++) {
                calendar.add(Calendar.MONTH, -1);
                java.util.Date time = calendar.getTime();
                days += calendar.getMaximum(Calendar.MONTH);
            }

            return days;
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.ANNUALLY)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nextIncomePayDate);
            int days = 0;
            for (int i = 0; i < 12; i++) {
                calendar.add(Calendar.MONTH, -1);
                days += calendar.getMaximum(Calendar.MONTH);
            }

            return days;
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
    private void processAccrualForDividends(Security security) {

        LOG.info("Calculate accruals for securities that have accrual method Dividends.");

        Date securityExDividendDate = security.getExDividendDate();
        String accrualMethodName = security.getClassCode().getAccrualMethod().getName();

        if (kemService.getCurrentDate().equals(securityExDividendDate)) {
            BigDecimal securityDividendAmount = security.getDividendAmount();
            List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(security.getId());

            if (holdingTaxLots != null) {

                initializeTotalReportLine(security.getId(), accrualMethodName);
                if (isFistTimeForWritingTotalReport) {
                    accrualProcessingReportWriterService.writeTableHeader(totalReportLine);
                    isFistTimeForWritingTotalReport = false;
                }

                for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                    BigDecimal accrualAmount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), securityDividendAmount, 5);
                    holdingTaxLot.setCurrentAccrual(holdingTaxLot.getCurrentAccrual().add(accrualAmount));
                    businessObjectService.save(holdingTaxLot);

                    totalReportLine.addAccrualAmount(accrualAmount);
                }

                accrualProcessingReportWriterService.writeTableRow(totalReportLine);

                LOG.info("Number of tax lots that have accrual amount updated for security id = " + security.getId() + " with accrual method = Dividends is " + holdingTaxLots.size());
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
     * This method...
     * 
     * @param securityId
     * @param accrualMethod
     */
    private void initializeTotalReportLine(String securityId, String accrualMethod) {
        // create a new totalReportLine
        this.totalReportLine = new AccrualsProcessingTotalReportLine(securityId, accrualMethod);
    }
}
