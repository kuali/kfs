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

import org.kuali.kfs.gl.batch.service.SufficientFundsAccountUpdateService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * A step to run the process that rebuilds information that supports sufficient funds inquiries
 */
public class SufficientFundsAccountUpdateStep extends AbstractWrappedBatchStep {
    private SufficientFundsAccountUpdateService sufficientFundsAccountUpdateService;

    /**
     * Return a proper batch executor
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            /**
             * Runs the sufficient funds rebuilder step.
             * 
             * @return true if the job completed successfully, false if otherwise
             * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
             */
            public boolean execute() {
                sufficientFundsAccountUpdateService.rebuildSufficientFunds();
                return true;
            }
        };
    }


    /**
     * Sets the sufficientFundsAccountUpdateService, allowing the injection of an implementation of that service
     * 
     * @param sufficientFundsAccountUpdateService an implementation sufficientFundsAccountUpdateService to set
     * @see org.kuali.kfs.gl.batch.service.SufficientFundsAccountUpdateService
     */
    public void setSufficientFundsAccountUpdateService(SufficientFundsAccountUpdateService sfrs) {
        sufficientFundsAccountUpdateService = sfrs;
    }
}
