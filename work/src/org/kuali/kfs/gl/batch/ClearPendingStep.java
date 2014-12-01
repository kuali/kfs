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

import java.util.Date;

import org.kuali.kfs.gl.service.NightlyOutService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * A step to clear pending ledger entries.
 */
public class ClearPendingStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClearPendingStep.class);
    private NightlyOutService nightlyOutService;

    /**
     * Runs the process of deleting copied ledger entries
     * 
     * @param jobName the name of the job that this step is being run as part of
     * @param jobRunDate the time/date the job is run
     * @return that the job completed successfully
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        nightlyOutService.deleteCopiedPendingLedgerEntries();
        return true;
    }

    /**
     * Sets the nightlyOutService attribute value.
     * 
     * @param nightlyOutService The nightlyOutService to set.
     * @see org.kuali.kfs.gl.service.NightlyOutService
     */
    public void setNightlyOutService(NightlyOutService nightlyOutService) {
        this.nightlyOutService = nightlyOutService;
    }
}
