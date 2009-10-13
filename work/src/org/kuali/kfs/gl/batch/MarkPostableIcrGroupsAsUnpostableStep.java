/*
 * Copyright 2007 The Kuali Foundation
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
import org.kuali.kfs.sys.batch.TestingStep;

/**
 * This step, which would only be run in testing or extraordinary production circumstances, stops the posting of all indirect cost
 * recovery groups.
 */
public class MarkPostableIcrGroupsAsUnpostableStep extends AbstractStep implements TestingStep {
    private OriginEntryGroupService originEntryGroupService;

    /**
     * Marks all postable ICR groups as unpostable
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job is being run
     * @return true if the step completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {

        //TODO:- need to check
        //originEntryGroupService.markPostableIcrGroupsAsUnpostable();
        return true;
    }

    /**
     * Sets the originEntryGroupSerivce, allowing the injection of an implementation of that service
     * 
     * @param originEntryGroupService the originEntryGroupService to set
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
}
