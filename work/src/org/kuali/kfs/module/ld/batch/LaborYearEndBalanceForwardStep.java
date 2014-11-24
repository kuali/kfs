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
package org.kuali.kfs.module.ld.batch;

import org.kuali.kfs.module.ld.batch.service.LaborYearEndBalanceForwardService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Labor End balance forward Batch Step.
 */
public class LaborYearEndBalanceForwardStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborYearEndBalanceForwardStep.class);
    private LaborYearEndBalanceForwardService laborYearEndBalanceForwardService;    

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                laborYearEndBalanceForwardService.forwardBalance();
                return true;
            }
        };
    }

    /**
     * Sets the laborYearEndBalanceForwardService attribute value.
     * 
     * @param laborYearEndBalanceForwardService The laborYearEndBalanceForwardService to set.
     * @return none
     */
    public void setLaborYearEndBalanceForwardService(LaborYearEndBalanceForwardService laborYearEndBalanceForwardService) {
        this.laborYearEndBalanceForwardService = laborYearEndBalanceForwardService;
    }
}
