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

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.PosterService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

/**
 * The step that runs the poster service on indirect cost recovery encumbrance entries.
 */
public class PosterIcrEncumbranceEntriesStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterIcrEncumbranceEntriesStep.class);
    protected PosterService posterService;

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            @Override
            public boolean execute() {
                final boolean shouldRunIcrEncumbranceActivity = getParameterService().getParameterValueAsBoolean(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.USE_ICR_ENCUMBRANCE_PARAM, Boolean.FALSE);
                if (shouldRunIcrEncumbranceActivity) {
                    posterService.postIcrEncumbranceEntries();
                } else {
                    LOG.info("Skipping running of PosterIcrEncumbranceEntriesStep because parameter KFS-GL / Encumbrance / USE_ICR_ENCUMBRANCE_IND has turned this functionality off.");
                }
                return true;
            }
        };
    }

    /**
     * Sets the posterService attribute value.
     *
     * @param posterService the posterService to set.
     */
    public void setPosterService(PosterService posterService) {
        this.posterService = posterService;
    }
}
