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
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.OriginEntryFieldUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;
import org.kuali.kfs.gl.batch.ScrubberSortComparator;

/**
 * A step to run the scrubber process.
 */
public class ScrubberSortStep extends AbstractStep {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberSortStep.class);
    protected String batchFileDirectoryName;
    
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
        String inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.PRE_SCRUBBER_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        if ( new File( inputFile ).exists() ) {
            BatchSortUtil.sortTextFileWithFields(inputFile, outputFile, new ScrubberSortComparator());
        } else {
            LOG.warn( "Unable to find " + inputFile + ", sorting skipped.  No output file created." );
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

}
