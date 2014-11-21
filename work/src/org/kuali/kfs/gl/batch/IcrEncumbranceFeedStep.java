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
        final boolean shouldRunIcrEncumbranceActivity = this.getParameterService().getParameterValueAsBoolean(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.USE_ICR_ENCUMBRANCE_PARAM, Boolean.FALSE);
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
