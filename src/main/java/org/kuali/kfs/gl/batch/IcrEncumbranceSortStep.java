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
import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.springframework.util.StopWatch;

/**
 * This step sorts the ICR Encumbrance file
 */
public class IcrEncumbranceSortStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IcrEncumbranceSortStep.class);
    protected String batchFileDirectoryName;

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
        final boolean shouldRunIcrEncumbranceActivity = this.getParameterService().getParameterValueAsBoolean(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.USE_ICR_ENCUMBRANCE_PARAM, Boolean.FALSE);
        if (shouldRunIcrEncumbranceActivity) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start(jobName);

            String inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

            BatchSortUtil.sortTextFileWithFields(inputFile, outputFile, new PosterSortComparator());

            stopWatch.stop();
            if (LOG.isDebugEnabled()) {
                LOG.debug("IcrEncumbranceSort step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
            }
        } else {
            LOG.info("Skipping running of IcrEncumbranceSortStep because parameter KFS-GL / Encumbrance / USE_ICR_ENCUMBRANCE_IND has turned this functionality off.");
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
