/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.batch;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryFileIterator;
import org.kuali.kfs.gl.businessobject.GlSummary;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.report.PosterOutputSummaryReport;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.gl.service.ReversalService;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.FiscalYearAwareReportWriterService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.ReportWriterService;

/**
 * A step to generate summary reports from a recent poster run
 */
public class PosterSummaryReportStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterSummaryReportStep.class);
    private static final String DATE_FORMAT = "MMdd";
    private static final String BUD = "bud";
    private static final String ACT = "act";
    private static final String ENC = "enc";
    private OptionsService optionsService;
    private ReportWriterService posterOutputSummaryReportWriterService;
    private FiscalYearAwareReportWriterService posterActualBalanceSummaryReportWriterService;
    private FiscalYearAwareReportWriterService posterBudgetBalanceSummaryReportWriterService;
    private FiscalYearAwareReportWriterService posterEncumbranceSummaryReportWriterService;
    private ReversalService reversalService;
    private BalanceService balanceService;
    private String batchFileDirectoryName;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            /**
             * Runs the process that generates poster summary reports.
             * 
             * @return true if the step completed successfully, false if otherwise
             * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
             */
            public boolean execute() {
                synchronized(this) { // why the synchronization?
                    final String CURRENT_YEAR_LOWER = getCurrentYearLowerParameter();
                    final String CURRENT_YEAR_UPPER = getCurrentYearUpperParameter();
                    final String CURRENT_AND_LAST_YEAR = getCurrentAndLastYearParameter();
    
                    SystemOptions currentYear = optionsService.getCurrentYearOptions();
                    SystemOptions nextYear = optionsService.getOptions(currentYear.getUniversityFiscalYear() + 1);
                    SystemOptions previousYear = optionsService.getOptions(currentYear.getUniversityFiscalYear() - 1);
    
                    Date runDate = getDateTimeService().getCurrentDate();
                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                    String md = sdf.format(runDate);
                    generatePosterOutputReport(runDate);
                    if ((md.compareTo(CURRENT_YEAR_UPPER) > 0) || (md.compareTo(CURRENT_YEAR_LOWER) < 0)) {
                        // Current year
                        generateGlSummary(runDate, currentYear, BUD);
                        generateGlSummary(runDate, currentYear, ACT);
                        generateGlSummary(runDate, currentYear, ENC);
                    }
                    else if ((md.compareTo(CURRENT_AND_LAST_YEAR) > 0)) {
                        // Current year and Last year
                        generateGlSummary(runDate, currentYear, BUD);
                        generateGlSummary(runDate, previousYear, BUD);
                        generateGlSummary(runDate, currentYear, ACT);
                        generateGlSummary(runDate, previousYear, ACT);
                        generateGlSummary(runDate, currentYear, ENC);
                        generateGlSummary(runDate, previousYear, ENC);
                    }
                    else {
                        // Current year and next year
                        generateGlSummary(runDate, currentYear, BUD);
                        generateGlSummary(runDate, nextYear, BUD);
                        generateGlSummary(runDate, currentYear, ACT);
                        generateGlSummary(runDate, nextYear, ACT);
                        generateGlSummary(runDate, currentYear, ENC);
                        generateGlSummary(runDate, nextYear, ENC);
                    }
                }
                return true;
            }
        };
    }
    
    /**
     * Generates reports about the output of a poster run.
     * 
     * @param runDate the date the poster was run.
     */
    private void generatePosterOutputReport(Date runDate) {
        PosterOutputSummaryReport posterOutputSummaryReport = new PosterOutputSummaryReport();
        
        // summarize all the entries for the main poster
        File mainPosterFile = FileUtil.getNewestFile(new File(batchFileDirectoryName), new RegexFileFilter((GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE + "\\.[0-9_\\-]+\\" + GeneralLedgerConstants.BatchFileSystem.EXTENSION)));
        if (mainPosterFile != null && mainPosterFile.exists()) {
            OriginEntryFileIterator mainPosterIterator = new OriginEntryFileIterator(mainPosterFile);
            while (mainPosterIterator.hasNext()) {
                final OriginEntryInformation originEntry = mainPosterIterator.next();
                posterOutputSummaryReport.summarize(originEntry);
            }
        } else {
            LOG.warn("Could not Main Poster Input file with prefix "+GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE+" for tabulation in the Poster Output Summary Report");
        }

        // summarize today's reversals
        File reversalPosterFile = FileUtil.getNewestFile(new File(batchFileDirectoryName), new RegexFileFilter((GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_VALID_OUTPUT_FILE + "\\.[0-9_\\-]+\\" + GeneralLedgerConstants.BatchFileSystem.EXTENSION)));
        if (reversalPosterFile != null && reversalPosterFile.exists()) {
            OriginEntryFileIterator reversalOriginEntryIterator = new OriginEntryFileIterator(reversalPosterFile);
            while (reversalOriginEntryIterator.hasNext()) {
                final Reversal reversal = new Reversal (reversalOriginEntryIterator.next());
                posterOutputSummaryReport.summarize(reversal);
            }
        } else {
            LOG.warn("Could not Reversal Output file with prefix "+GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_VALID_OUTPUT_FILE+" for tabulation in the Poster Output Summary Report");
        }
        
        // summarize the icr poster
        File icrPosterFile = FileUtil.getNewestFile(new File(batchFileDirectoryName), new RegexFileFilter(GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_INPUT_FILE + "\\.[0-9_\\-]+\\" + GeneralLedgerConstants.BatchFileSystem.EXTENSION));
        if (icrPosterFile != null && icrPosterFile.exists()) {
            OriginEntryFileIterator icrIterator = new OriginEntryFileIterator(icrPosterFile);
            while (icrIterator.hasNext()) {
                final OriginEntryInformation originEntry = icrIterator.next();
                posterOutputSummaryReport.summarize(originEntry);
            }
        } else {
            LOG.warn("Could not Indirect Cost Recovery Poster Input file with prefix "+GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_INPUT_FILE+" for tabulation in the Poster Output Summary Report");
        }
        
        posterOutputSummaryReport.writeReport(posterOutputSummaryReportWriterService);
    }

    /**
     * Generates the GL Summary report
     * 
     * @param runDate the run date of the poster service that should be reported
     * @param options the options of the fiscal year the poster was run
     * @param reportType the type of the report that should be generated
     */
    protected void generateGlSummary(Date runDate, SystemOptions year, String reportType) {
        LOG.debug("generateGlSummary() started");

        FiscalYearAwareReportWriterService fiscalYearAwareReportWriterService;
        
        List<String> balanceTypeCodes = new ArrayList<String>();
        if (ACT.equals(reportType)) {
            fiscalYearAwareReportWriterService = posterActualBalanceSummaryReportWriterService;
            balanceTypeCodes.add(year.getActualFinancialBalanceTypeCd());
        }
        else if (BUD.equals(reportType)) {
            fiscalYearAwareReportWriterService = posterBudgetBalanceSummaryReportWriterService;
            balanceTypeCodes.add(year.getBudgetCheckingBalanceTypeCd());
            balanceTypeCodes.add(year.getBaseBudgetFinancialBalanceTypeCd());
            balanceTypeCodes.add(year.getMonthlyBudgetFinancialBalanceTypeCd());
        } else { // ENC
            fiscalYearAwareReportWriterService = posterEncumbranceSummaryReportWriterService;
            balanceTypeCodes.add(year.getExtrnlEncumFinBalanceTypCd());
            balanceTypeCodes.add(year.getIntrnlEncumFinBalanceTypCd());
            balanceTypeCodes.add(year.getPreencumbranceFinBalTypeCd());
            balanceTypeCodes.add(year.getCostShareEncumbranceBalanceTypeCd());
        }

        List<GlSummary> balances = balanceService.getGlSummary(year.getUniversityFiscalYear(), balanceTypeCodes);
        
        GlSummary totals = new GlSummary();
        for(GlSummary balance : balances) {
            totals.add(balance);
        }        
        totals.setFundGroup("Total");
        
        try {
            fiscalYearAwareReportWriterService.setFiscalYear(year.getUniversityFiscalYear());
            ((WrappingBatchService) fiscalYearAwareReportWriterService).initialize();
            
            fiscalYearAwareReportWriterService.writeSubTitle("Balance Type of " + balanceTypeCodes + " for Fiscal Year " + year.getUniversityFiscalYearName());
            fiscalYearAwareReportWriterService.writeNewLines(1);
            
            fiscalYearAwareReportWriterService.writeTableRowSeparationLine(totals);
            fiscalYearAwareReportWriterService.writeTable(balances, true, false);
            
            fiscalYearAwareReportWriterService.writeTableRowSeparationLine(totals);
            fiscalYearAwareReportWriterService.writeTableRow(totals);
        } finally {
            ((WrappingBatchService) fiscalYearAwareReportWriterService).destroy();
        }
    }
    
    /**
     * @return current year lower parameter for inner class
     */
    public String getCurrentYearLowerParameter() {
        return getParameterService().getParameterValueAsString(getClass(), GeneralLedgerConstants.GlSummaryReport.CURRENT_YEAR_LOWER);
    }
    
    /**
     * @return current year upper parameter for inner class
     */
    public String getCurrentYearUpperParameter() {
        return getParameterService().getParameterValueAsString(PosterSummaryReportStep.this.getClass(), GeneralLedgerConstants.GlSummaryReport.CURRENT_YEAR_UPPER);
    }
    
    /**
     * @return current and last year parameter for inner class
     */
    public String getCurrentAndLastYearParameter() {
        return getParameterService().getParameterValueAsString(PosterSummaryReportStep.this.getClass(), GeneralLedgerConstants.GlSummaryReport.CURRENT_AND_LAST_YEAR);
    }

    /**
     * Sets the optionsService attribute, allowing the injection of an implementation of that service
     * 
     * @param os the optionsService implementation to set
     * @see org.kuali.kfs.sys.service.OptionsService
     */
    public void setOptionsService(OptionsService os) {
        optionsService = os;
    }

    /**
     * Gets the posterOutputSummaryReportWriterService attribute. 
     * @return Returns the posterOutputSummaryReportWriterService.
     */
    public ReportWriterService getPosterOutputSummaryReportWriterService() {
        return posterOutputSummaryReportWriterService;
    }

    /**
     * Sets the posterOutputSummaryReportWriterService attribute value.
     * @param posterOutputSummaryReportWriterService The posterOutputSummaryReportWriterService to set.
     */
    public void setPosterOutputSummaryReportWriterService(ReportWriterService posterOutputSummaryReportWriterService) {
        this.posterOutputSummaryReportWriterService = posterOutputSummaryReportWriterService;
    }

    /**
     * Sets the posterActualBalanceSummaryReportWriterService attribute value.
     * @param posterActualBalanceSummaryReportWriterService The posterActualBalanceSummaryReportWriterService to set.
     */
    public void setPosterActualBalanceSummaryReportWriterService(FiscalYearAwareReportWriterService posterActualBalanceSummaryReportWriterService) {
        this.posterActualBalanceSummaryReportWriterService = posterActualBalanceSummaryReportWriterService;
    }

    /**
     * Sets the posterBudgetBalanceSummaryReportWriterService attribute value.
     * @param posterBudgetBalanceSummaryReportWriterService The posterBudgetBalanceSummaryReportWriterService to set.
     */
    public void setPosterBudgetBalanceSummaryReportWriterService(FiscalYearAwareReportWriterService posterBudgetBalanceSummaryReportWriterService) {
        this.posterBudgetBalanceSummaryReportWriterService = posterBudgetBalanceSummaryReportWriterService;
    }

    /**
     * Sets the posterEncumbranceSummaryReportWriterService attribute value.
     * @param posterEncumbranceSummaryReportWriterService The posterEncumbranceSummaryReportWriterService to set.
     */
    public void setPosterEncumbranceSummaryReportWriterService(FiscalYearAwareReportWriterService posterEncumbranceSummaryReportWriterService) {
        this.posterEncumbranceSummaryReportWriterService = posterEncumbranceSummaryReportWriterService;
    }

    /**
     * Gets the reversalService attribute. 
     * @return Returns the reversalService.
     */
    public ReversalService getReversalService() {
        return reversalService;
    }

    /**
     * Sets the reversalService attribute value.
     * @param reversalService The reversalService to set.
     */
    public void setReversalService(ReversalService reversalService) {
        this.reversalService = reversalService;
    }

    /**
     * Sets the balanceService attribute value.
     * @param balanceService The balanceService to set.
     */
    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * Gets the batchFileDirectoryName attribute. 
     * @return Returns the batchFileDirectoryName.
     */
    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
