/*
 * Copyright 2005-2009 The Kuali Foundation
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
