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

import org.kuali.kfs.gl.batch.service.SufficientFundsFullRebuildService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.TestingStep;

/**
 * A step to run the sufficient funds sync process. One typically doesn't need to do this - which is why it's marked as TestingStep -
 * as Account, Chart, and Object Code records, when saved, will populate the sufficient funds tables, making this task redundant.
 * However, if that information has not been built, this job will generate that information.
 */
public class SufficientFundsFullRebuildStep extends AbstractStep implements TestingStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsFullRebuildStep.class);
    private SufficientFundsFullRebuildService sufficientFundsFullRebuildService;

    /**
     * Runs the sufficient funds sync service.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        sufficientFundsFullRebuildService.syncSufficientFunds();
        return true;
    }

    /**
     * Sets the sufficientFundsFullRebuildService, allowing the injection of an implementation of that service
     * 
     * @param sufficientFundsFullRebuildService an implementation sufficientFundsFullRebuildService to set
     * @see org.kuali.kfs.gl.batch.service.SufficientFundsFullRebuildService
     */
    public void setSufficientFundsFullRebuildService(SufficientFundsFullRebuildService sfss) {
        sufficientFundsFullRebuildService = sfss;
    }
}
