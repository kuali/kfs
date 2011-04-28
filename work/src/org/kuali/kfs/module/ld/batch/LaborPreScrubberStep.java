/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ld.batch;

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
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.springframework.util.StopWatch;

public class LaborPreScrubberStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborPreScrubberStep.class);
    private String batchFileDirectoryName;
    private PreScrubberService laborPreScrubberService;
    private ReportWriterService laborPreScrubberReportWriterService;
    
    /**
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return new ArrayList<String>() {{add(batchFileDirectoryName); }};
    }

    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {

            /**
             * @see org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor#execute()
             */
            public boolean execute() {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                String inputFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
                String outputFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.PRE_SCRUBBER_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
                
                PreScrubberReportData preScrubberReportData = null;
                LineIterator oeIterator = null;
                try {
                    oeIterator = FileUtils.lineIterator(new File(inputFile));
                    preScrubberReportData = laborPreScrubberService.preprocessOriginEntries(oeIterator, outputFile);
                }
                catch (IOException e) {
                    LOG.error("IO exception occurred during pre scrubbing.", e);
                    throw new RuntimeException("IO exception occurred during pre scrubbing.", e);
                }
                finally {
                    LineIterator.closeQuietly(oeIterator);
                }
                if (preScrubberReportData != null) {
                    ((WrappingBatchService) laborPreScrubberReportWriterService).initialize();
                    new PreScrubberReport().generateReport(preScrubberReportData, laborPreScrubberReportWriterService);
                    ((WrappingBatchService) laborPreScrubberReportWriterService).destroy();
                }
                
                stopWatch.stop();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("labor pre-scrubber scrubber step took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
                }
                return true;
            }
            
        };
    }
    
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public PreScrubberService getLaborPreScrubberService() {
        return laborPreScrubberService;
    }

    public void setLaborPreScrubberService(PreScrubberService preScrubberService) {
        this.laborPreScrubberService = preScrubberService;
    }

    /**
     * Sets the laborPreScrubberReportWriterService attribute value.
     * @param laborPreScrubberReportWriterService The laborPreScrubberReportWriterService to set.
     */
    public void setLaborPreScrubberReportWriterService(ReportWriterService laborPreScrubberReportWriterService) {
        this.laborPreScrubberReportWriterService = laborPreScrubberReportWriterService;
    }
}
