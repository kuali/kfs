/*
 * Copyright 2006 The Kuali Foundation
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
