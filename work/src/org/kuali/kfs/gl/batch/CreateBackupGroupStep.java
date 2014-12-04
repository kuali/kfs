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

/**
 * A step to create a backup group for entries about to be processed by the scrubber and poster
 */
public class CreateBackupGroupStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateBackupGroupStep.class);
    private OriginEntryGroupService originEntryGroupService;

    /**
     * Runs the backup group creation process
     * 
     * @param jobName the name of the job that this step is being run as part of
     * @param jobRunDate the time/date when the job was started
     * @return true if this job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        originEntryGroupService.createBackupGroup();
        return true;
    }

    /**
     * Sets the originEntryGroupService attribute, allowing the injection of an implementation of the service
     * 
     * @param oegs
     * @see org.kuali.module.gl.OriginEntryGroupService
     */
    public void setOriginEntryGroupService(OriginEntryGroupService oegs) {
        originEntryGroupService = oegs;
    }
}
