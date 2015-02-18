/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.report.PreScrubberReport;
import org.kuali.kfs.gl.report.PreScrubberReportData;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.springframework.util.StopWatch;

/**
 * A step to run the scrubber process.
 */
public class PreScrubberStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreScrubberStep.class);
    private String batchFileDirectoryName;
    private PreScrubberService preScrubberService;
    private ReportWriterService preScrubberReportWriterService;
    
    /**
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return new ArrayList<String>(){{add(batchFileDirectoryName);}};
    }

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                String inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
                String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.PRE_SCRUBBER_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
                
                PreScrubberReportData preScrubberReportData = null;
                LineIterator oeIterator = null;
                try {
                    oeIterator = FileUtils.lineIterator(new File(inputFile));
                    preScrubberReportData = preScrubberService.preprocessOriginEntries(oeIterator, outputFile);
                }
                catch (IOException e) {
                    LOG.error("IO exception occurred during pre scrubbing.", e);
                    throw new RuntimeException("IO exception occurred during pre scrubbing.", e);
                }
                finally {
                    LineIterator.closeQuietly(oeIterator);
                }

                if (preScrubberReportData != null) {
                    new PreScrubberReport().generateReport(preScrubberReportData, preScrubberReportWriterService);
                }
                
                stopWatch.stop();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("scrubber step of took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
                }
                return true;
            }
        };
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public PreScrubberService getPreScrubberService() {
        return preScrubberService;
    }

    public void setPreScrubberService(PreScrubberService preScrubberService) {
        this.preScrubberService = preScrubberService;
    }
    
    public void setPreScrubberReportWriterService(ReportWriterService preScrubberReportWriterService) {
        this.preScrubberReportWriterService = preScrubberReportWriterService;
    }
}
