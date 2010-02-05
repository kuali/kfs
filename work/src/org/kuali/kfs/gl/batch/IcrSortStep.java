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
import org.kuali.kfs.gl.batch.service.BatchSortService;
import org.kuali.kfs.gl.businessobject.OriginEntryFieldUtil;
import org.kuali.kfs.gl.exception.LoadException;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.kns.util.GlobalVariables;
import org.springframework.util.StopWatch;
import org.kuali.kfs.gl.batch.PosterSortComparator;

/**
 * A step to run the scrubber process.
 */
public class IcrSortStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IcrSortStep.class);
    private String batchFileDirectoryName;
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
        String inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_TRANSACTIONS_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        
        BatchSortUtil.sortTextFileWithFields(inputFile, outputFile, new PosterSortComparator());

        stopWatch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("IcrSort step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return true;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
