/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch;

import java.util.Date;

import org.kuali.kfs.gl.batch.service.IcrEncumbranceService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This step builds a file of ICR Encumbrance Entries
 */
public class IcrEncumbranceFeedStep extends AbstractStep {
    private IcrEncumbranceService icrEncumbranceService;

    /**
     * This step builds a file of ICR Encumbrance Entries for posting to the General Ledger
     *
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        icrEncumbranceService.buildIcrEncumbranceFeed();
        return true;
    }

    /**
     * Sets the icrEncumbranceService attribute value.
     *
     * @param icrEncumbranceService the icrEncumbranceService to set.
     * @see org.kuali.kfs.gl.batch.service.IcrEncumbranceService
     */
    public void setIcrEncumbranceService(IcrEncumbranceService icrEncumbranceService) {
        this.icrEncumbranceService = icrEncumbranceService;
    }
}
