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
package org.kuali.kfs.gl.batch;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryFileIterator;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.report.PosterOutputSummaryReport;
import org.kuali.kfs.gl.service.ReportService;
import org.kuali.kfs.gl.service.ReversalService;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.kuali.kfs.sys.businessobject.SystemOptions;
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
    private ReportService reportService;
    private ReportWriterService posterOutputSummaryReportWriterService;
    private ReversalService reversalService;
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
                    this.generatePosterOutputReport(runDate);
                    if ((md.compareTo(CURRENT_YEAR_UPPER) > 0) || (md.compareTo(CURRENT_YEAR_LOWER) < 0)) {
                        // Current year
                        reportService.generateGlSummary(runDate, currentYear, BUD);
                        reportService.generateGlSummary(runDate, currentYear, ACT);
                        reportService.generateGlEncumbranceSummary(runDate, currentYear, ENC);
                    }
                    else if ((md.compareTo(CURRENT_AND_LAST_YEAR) > 0)) {
                        // Current year and Last year
                        reportService.generateGlSummary(runDate, currentYear, BUD);
                        reportService.generateGlSummary(runDate, previousYear, BUD);
                        reportService.generateGlSummary(runDate, currentYear, ACT);
                        reportService.generateGlSummary(runDate, previousYear, ACT);
                        reportService.generateGlEncumbranceSummary(runDate, currentYear, ENC);
                        reportService.generateGlEncumbranceSummary(runDate, previousYear, ENC);
                    }
                    else {
                        // Current year and next year
                        reportService.generateGlSummary(runDate, currentYear, BUD);
                        reportService.generateGlSummary(runDate, nextYear, BUD);
                        reportService.generateGlSummary(runDate, currentYear, ACT);
                        reportService.generateGlSummary(runDate, nextYear, ACT);
                        reportService.generateGlEncumbranceSummary(runDate, currentYear, ENC);
                        reportService.generateGlEncumbranceSummary(runDate, nextYear, ENC);
                    }
                }
                return true;
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
                OriginEntryFileIterator mainPosterIterator = new OriginEntryFileIterator(mainPosterFile);
                while (mainPosterIterator.hasNext()) {
                    final OriginEntry originEntry = mainPosterIterator.next();
                    posterOutputSummaryReport.summarize(originEntry);
                }
                // summarize today's reverals
                Iterator<?> reversalsIterator = getReversalService().getByDate(runDate);
                while (reversalsIterator.hasNext()) {
                    final Reversal reversal = (Reversal)reversalsIterator.next();
                    posterOutputSummaryReport.summarize(reversal);
                }
                // summarize the icr poster
                File icrPosterFile = FileUtil.getNewestFile(new File(batchFileDirectoryName), new RegexFileFilter(GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_INPUT_FILE + "\\.[0-9_\\-]+\\" + GeneralLedgerConstants.BatchFileSystem.EXTENSION));
                OriginEntryFileIterator icrIterator = new OriginEntryFileIterator(icrPosterFile);
                while (icrIterator.hasNext()) {
                    final OriginEntry originEntry = icrIterator.next();
                    posterOutputSummaryReport.summarize(originEntry);
                }
                
                posterOutputSummaryReport.writeReport(posterOutputSummaryReportWriterService);
            }
        };
    }

    /**
     * @return current year lower parameter for inner class
     */
    public String getCurrentYearLowerParameter() {
        return getParameterService().getParameterValue(getClass(), GeneralLedgerConstants.GlSummaryReport.CURRENT_YEAR_LOWER);
    }
    
    /**
     * @return current year upper parameter for inner class
     */
    public String getCurrentYearUpperParameter() {
        return getParameterService().getParameterValue(PosterSummaryReportStep.this.getClass(), GeneralLedgerConstants.GlSummaryReport.CURRENT_YEAR_UPPER);
    }
    
    /**
     * @return current and last year parameter for inner class
     */
    public String getCurrentAndLastYearParameter() {
        return getParameterService().getParameterValue(PosterSummaryReportStep.this.getClass(), GeneralLedgerConstants.GlSummaryReport.CURRENT_AND_LAST_YEAR);
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
     * Gets the reportService attribute. 
     * @return Returns the reportService.
     */
    public ReportService getReportService() {
        return reportService;
    }

    /**
     * Sets the reportService attribute value.
     * @param reportService The reportService to set.
     */
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
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
