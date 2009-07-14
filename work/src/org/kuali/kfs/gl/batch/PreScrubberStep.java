/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.BatchSortService;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryFileIterator;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;

/**
 * A step to run the scrubber process.
 */
public class PreScrubberStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreScrubberStep.class);
    private String batchFileDirectoryName;
    private PreScrubberService preScrubberService;
    
    /**
     * Runs the scrubber process.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);

        String inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.PRE_SCRUBBER_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        
        LineIterator oeIterator = null;
        try {
            oeIterator = FileUtils.lineIterator(new File(inputFile));
            preScrubberService.preprocessOriginEntry(oeIterator, outputFile);
        }
        catch (IOException e) {
            LOG.error("IO exception occurred during pre scrubbing.", e);
            throw new RuntimeException("IO exception occurred during pre scrubbing.", e);
        }
        finally {
            LineIterator.closeQuietly(oeIterator);
        }
        
        stopWatch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("scrubber step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return true;
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

}
