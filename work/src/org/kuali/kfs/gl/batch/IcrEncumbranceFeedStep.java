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

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.IcrEncumbranceService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

/**
 * This step builds a file of ICR Encumbrance Entries
 */
public class IcrEncumbranceFeedStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IcrEncumbranceFeedStep.class);
    protected IcrEncumbranceService icrEncumbranceService;

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
        final boolean shouldRunIcrEncumbranceActivity = this.getParameterService().getParameterValueAsBoolean(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.USE_ICR_ENCUMBRANCE_PARAM);
        if (shouldRunIcrEncumbranceActivity) {
            icrEncumbranceService.buildIcrEncumbranceFeed();
        } else {
            LOG.info("Skipping running of IcrEncumbranceFeedStep because parameter KFS-GL / Encumbrance / USE_ICR_ENCUMBRANCE_IND has turned this functionality off.");
        }
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
