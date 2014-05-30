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
import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;

/**
 * This step sorts the ICR Encumbrance file
 */
public class IcrEncumbranceSortStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IcrEncumbranceSortStep.class);
    private String batchFileDirectoryName;

    /**
     * This step sorts the ICR Encumbrance file
     *
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);

        String inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        BatchSortUtil.sortTextFileWithFields(inputFile, outputFile, new PosterSortComparator());

        stopWatch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("IcrEncumbranceSort step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return true;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     *
     * @param batchFileDirectoryName the batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
