/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.batch;

import java.util.Date;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.gl.service.PosterService;

/**
 * The step that runs the poster service on indirect cost recovery entries.
 */
public class PosterIndirectCostRecoveryEntriesStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterIndirectCostRecoveryEntriesStep.class);
    private PosterService posterService;

    /**
     * Runs the poster service on indirect cost recovery entries
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        posterService.postIcrEntries();
        return true;
    }

    /**
     * Sets the posterService attribute, allowing the injection of an implementation of the service
     * 
     * @param ps the implementation of the posterService to set
     * @see org.kuali.module.gl.service.PosterService
     */
    public void setPosterService(PosterService ps) {
        posterService = ps;
    }
}
