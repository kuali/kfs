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

import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Generates a balancing report if data is present in the history tables. Instructions on how to test this process for General Ledger:<br>
 * 1) Place an acceptable GL_SORTPOST.data / GL_POSTERRS.data into batchFileDirectoryName (see spring-gl.xml)<br>
 * 2) Run BatchStepRunner for posterEntriesStep<br>
 * 3) Run BatchStepRunner for fileRenameStep<br>
 * 4) Run BatchStepRunner for posterBalancingStep<br>
 * 5) Evaluate GL batch directory for results
 */
public class PosterBalancingStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterBalancingStep.class);
    
    private BalancingService balancingService;
    
    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                balancingService.runBalancing();
                return true;
            }
        };
    }

    /**
     * Sets the BalancingService
     * 
     * @param balancingService The BalancingService to set.
     */
    public void setBalancingService(BalancingService balancingService) {
        this.balancingService = balancingService;
    }
}
