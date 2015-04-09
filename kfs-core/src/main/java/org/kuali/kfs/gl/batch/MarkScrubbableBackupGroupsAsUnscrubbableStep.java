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

import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.TestingStep;

/**
 * This step will mark all backup groups in the database so that they will not be scrubbed when the nightly scrubber step runs
 * again.
 */
public class MarkScrubbableBackupGroupsAsUnscrubbableStep extends AbstractStep implements TestingStep {
    private OriginEntryGroupService originEntryGroupService;

    /**
     * Marks all scrubbable backup groups as unscrubbable
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job is being run
     * @return true if the step completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        //TODO:- This step is not running. 
        //originEntryGroupService.markScrubbableBackupGroupsAsUnscrubbable();
        return true;
    }

    /**
     * Sets the originEntryGroupSerivce, allowing the injection of an implementation of that service
     * 
     * @param originEntryGroupService an implementation originEntryGroupService to set
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
}
