/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.batch.service.impl;

import static org.kuali.kfs.gl.GeneralLedgerConstants.GlSummaryReport.CURRENT_AND_LAST_YEAR;
import static org.kuali.kfs.gl.GeneralLedgerConstants.GlSummaryReport.CURRENT_YEAR_LOWER;
import static org.kuali.kfs.gl.GeneralLedgerConstants.GlSummaryReport.CURRENT_YEAR_UPPER;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.PosterSummaryReportStep;
import org.kuali.kfs.gl.businessobject.GlSummary;
import org.kuali.kfs.module.ld.batch.service.LaborBalanceSummaryReportService;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceSummary;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;

/**
 * Implements a set of methods that can generate labor balance summary reports
 */
public class LaborBalanceSummaryReportServiceImpl implements LaborBalanceSummaryReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBalanceSummaryReportServiceImpl.class);

    private DateTimeService dateTimeService;
    private OptionsService optionsService;
    private ParameterService parameterService;
    
    private LaborLedgerBalanceService laborLedgerBalanceService;
    private ReportWriterService laborActualBalanceSummaryReportWriterService;
    private ReportWriterService laborBudgetBalanceSummaryReportWriterService;
    private ReportWriterService laborEncumbranceSummaryReportWriterService;

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborBalanceSummaryReportService#generateBalanceSummaryReports()
     */
    public void generateBalanceSummaryReports() {
        LOG.info("generateBalanceSummaryReports() started");

        Date runDate = dateTimeService.getCurrentSqlDate();
        this.generateBalanceSummaryReports(runDate);
    }

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborBalanceSummaryReportService#generateBalanceSummaryReports(java.sql.Date)
     */
    public void generateBalanceSummaryReports(Date runDate) {
        LOG.info("generateBalanceSummaryReports(Date) started");

        String yearEndPeriodLowerBound = parameterService.getParameterValue(PosterSummaryReportStep.class, CURRENT_YEAR_LOWER);
        String lastDayOfFiscalYear = parameterService.getParameterValue(PosterSummaryReportStep.class, CURRENT_AND_LAST_YEAR);
        String yearEndPeriodUpperBound = parameterService.getParameterValue(PosterSummaryReportStep.class, CURRENT_YEAR_UPPER);

        Integer currentYear = optionsService.getCurrentYearOptions().getUniversityFiscalYear();
        this.generateBalanceSummaryReports(currentYear, runDate);

        // if today is within the lower bound of the year end period, then generate reports for the next fiscal year
        if (this.isInYearEndLowerBound(runDate, yearEndPeriodLowerBound, lastDayOfFiscalYear)) {
            this.generateBalanceSummaryReports(currentYear + 1, runDate);
        }

        // if today is within the upper bound of the year end period, then generate reports for the last fiscal year
        if (this.isInYearEndUpperBound(runDate, yearEndPeriodUpperBound, lastDayOfFiscalYear)) {
            this.generateBalanceSummaryReports(currentYear - 1, runDate);
        }
    }

    // generate a set of balance summary reports for actual, budget and encumbrance balances
    private void generateBalanceSummaryReports(Integer fiscalYear, Date runDate) {
        SystemOptions options = optionsService.getOptions(fiscalYear);       
        if(options == null) {
            LOG.fatal("The data for " + fiscalYear + "have NOT been setup.");
            return;
        }
        
        List<String> actualsBalanceTypes = this.getActualBalanceTypes(fiscalYear);   
        this.writeSummaryReport(fiscalYear, actualsBalanceTypes, laborActualBalanceSummaryReportWriterService);

        List<String> budgetBalanceTypes = this.getBudgetBalanceTypes(fiscalYear);
        this.writeSummaryReport(fiscalYear, budgetBalanceTypes, laborBudgetBalanceSummaryReportWriterService);

        List<String> encumbranceBalanceTypes = this.getEncumbranceBalanceTypes(fiscalYear);
        this.writeSummaryReport(fiscalYear, encumbranceBalanceTypes, laborEncumbranceSummaryReportWriterService);
    }

    private void writeSummaryReport(Integer fiscalYear, List<String> balanceTypes, ReportWriterService reportWriterService) {
        List<LaborBalanceSummary> balanceSummary = laborLedgerBalanceService.findBalanceSummary(fiscalYear, balanceTypes);
        List<GlSummary> summaryList = new ArrayList<GlSummary>(balanceSummary);
        
        GlSummary totals = new LaborBalanceSummary();
        for(GlSummary summaryLine : summaryList) {
            totals.add(summaryLine);
        }        
        totals.setFundGroup("Total");
        
        try {
            ((WrappingBatchService)reportWriterService).initialize();
            reportWriterService.writeSubTitle("Balance Type of " + balanceTypes + " for Fiscal Year " + fiscalYear);
            reportWriterService.writeNewLines(1);
            
            reportWriterService.writeTableRowSeparationLine(totals);
            reportWriterService.writeTable(summaryList, true, false);
            
            reportWriterService.writeTableRowSeparationLine(totals);
            reportWriterService.writeTableRow(totals);
        }
        finally {
            ((WrappingBatchService)reportWriterService).destroy();
        }
    }

    /**
     * get the encumbrance balance type codes for the given fiscal year
     * 
     * @param fiscalYear the given fiscal year
     * @return the encumbrance balance type codes for the given fiscal year
     */
    private List<String> getEncumbranceBalanceTypes(Integer fiscalYear) {
        SystemOptions options = optionsService.getOptions(fiscalYear);

        List<String> balanceTypes = new ArrayList<String>();
        balanceTypes.add(options.getExtrnlEncumFinBalanceTypCd());
        balanceTypes.add(options.getIntrnlEncumFinBalanceTypCd());
        balanceTypes.add(options.getPreencumbranceFinBalTypeCd());
        balanceTypes.add(options.getCostShareEncumbranceBalanceTypeCd());
        return balanceTypes;
    }

    /**
     * get the actual balance type codes for the given fiscal year
     * 
     * @param fiscalYear the given fiscal year
     * @return the actual balance type codes for the given fiscal year
     */
    private List<String> getActualBalanceTypes(Integer fiscalYear) {
        SystemOptions options = optionsService.getOptions(fiscalYear);

        List<String> balanceTypes = new ArrayList<String>();
        balanceTypes.add(options.getActualFinancialBalanceTypeCd());
        return balanceTypes;
    }

    /**
     * get the budget balance type codes for the given fiscal year
     * 
     * @param fiscalYear the given fiscal year
     * @return the budget balance type codes for the given fiscal year
     */
    private List<String> getBudgetBalanceTypes(Integer fiscalYear) {
        SystemOptions options = optionsService.getOptions(fiscalYear);

        List<String> balanceTypes = new ArrayList<String>();
        balanceTypes.add(options.getBudgetCheckingBalanceTypeCd());
        balanceTypes.add(options.getBaseBudgetFinancialBalanceTypeCd());
        balanceTypes.add(options.getMonthlyBudgetFinancialBalanceTypeCd());
        return balanceTypes;
    }

    /**
     * determine if the given date is within the year end period
     * 
     * @param runDate the given date
     * @param yearEndPeriodLowerBound the lower bound date of year end period
     * @param yearEndPeriodUpperBound the upper bound date of year end period
     * @param lastDayOfFiscalYear the last day of the current fiscal year
     * @return true if the given date is within the lower bound of year end period; otherwise, false
     */
    private boolean isInYearEndPeriod(Date runDate, String yearEndPeriodLowerBound, String yearEndPeriodUpperBound, String lastDayOfFiscalYear) {
        return isInYearEndLowerBound(runDate, yearEndPeriodLowerBound, lastDayOfFiscalYear) || isInYearEndUpperBound(runDate, yearEndPeriodUpperBound, lastDayOfFiscalYear);
    }

    /**
     * determine if the given date is within the lower bound of year end period
     * 
     * @param runDate the given date
     * @param yearEndPeriodLowerBound the lower bound date of year end period
     * @param lastDayOfFiscalYear the last day of the current fiscal year
     * @return true if the given date is within the lower bound of year end period; otherwise, false
     */
    private boolean isInYearEndLowerBound(Date runDate, String yearEndPeriodLowerBound, String lastDayOfFiscalYear) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
        String today = sdf.format(runDate);
        return today.compareTo(yearEndPeriodLowerBound) >= 0 && today.compareTo(lastDayOfFiscalYear) <= 0;
    }

    /**
     * determine if the given date is within the upper bound of year end period
     * 
     * @param runDate the given date
     * @param yearEndPeriodUpperBound the upper bound date of year end period
     * @param lastDayOfFiscalYear the last day of the current fiscal year
     * @return true if the given date is within the upper bound of year end period; otherwise, false
     */
    private boolean isInYearEndUpperBound(Date runDate, String yearEndPeriodUpperBound, String lastDayOfFiscalYear) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
        String today = sdf.format(runDate);

        String month = StringUtils.mid(lastDayOfFiscalYear, 0, 2);
        String date = StringUtils.mid(lastDayOfFiscalYear, 2, 2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        calendar.set(Calendar.DATE, Integer.parseInt(date));
        calendar.add(Calendar.DATE, 1);
        String firstDayOfNewFiscalYear = sdf.format(calendar.getTime());

        return today.compareTo(yearEndPeriodUpperBound) <= 0 && today.compareTo(firstDayOfNewFiscalYear) >= 0;
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
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /**
     * Sets the laborLedgerBalanceService attribute value.
     * @param laborLedgerBalanceService The laborLedgerBalanceService to set.
     */
    public void setLaborLedgerBalanceService(LaborLedgerBalanceService laborLedgerBalanceService) {
        this.laborLedgerBalanceService = laborLedgerBalanceService;
    }

    /**
     * Sets the laborActualBalanceSummaryReportWriterService attribute value.
     * @param laborActualBalanceSummaryReportWriterService The laborActualBalanceSummaryReportWriterService to set.
     */
    public void setLaborActualBalanceSummaryReportWriterService(ReportWriterService laborActualBalanceSummaryReportWriterService) {
        this.laborActualBalanceSummaryReportWriterService = laborActualBalanceSummaryReportWriterService;
    }

    /**
     * Sets the laborBudgetBalanceSummaryReportWriterService attribute value.
     * @param laborBudgetBalanceSummaryReportWriterService The laborBudgetBalanceSummaryReportWriterService to set.
     */
    public void setLaborBudgetBalanceSummaryReportWriterService(ReportWriterService laborBudgetBalanceSummaryReportWriterService) {
        this.laborBudgetBalanceSummaryReportWriterService = laborBudgetBalanceSummaryReportWriterService;
    }

    /**
     * Sets the laborEncumbranceSummaryReportWriterService attribute value.
     * @param laborEncumbranceSummaryReportWriterService The laborEncumbranceSummaryReportWriterService to set.
     */
    public void setLaborEncumbranceSummaryReportWriterService(ReportWriterService laborEncumbranceSummaryReportWriterService) {
        this.laborEncumbranceSummaryReportWriterService = laborEncumbranceSummaryReportWriterService;
    }
}
