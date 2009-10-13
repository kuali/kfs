/*
 * Copyright 2007-2009 The Kuali Foundation
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
