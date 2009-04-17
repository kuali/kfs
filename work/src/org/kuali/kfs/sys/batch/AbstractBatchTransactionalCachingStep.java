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
package org.kuali.kfs.sys.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.service.BatchTransactionalCachingService;
import org.springframework.util.StopWatch;

import uk.ltd.getahead.dwr.util.Logger;


public abstract class AbstractBatchTransactionalCachingStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(AbstractBatchTransactionalCachingStep.class);
    private BatchTransactionalCachingService batchTransactionalCachingService;
    
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);
        batchTransactionalCachingService.execute(getBatchTransactionExecutor());
        stopWatch.stop();
        LOG.info(new StringBuffer(getName()).append(" of ").append(jobName).append(" took ").append(stopWatch.getTotalTimeSeconds()/60.0).append(" minutes to complete").toString());
        return true;
    }
    
    protected abstract BatchTransactionalCachingService.BatchTransactionExecutor getBatchTransactionExecutor();

    public void setBatchTransactionalCachingService(BatchTransactionalCachingService batchTransactionalCachingService) {
        this.batchTransactionalCachingService = batchTransactionalCachingService;
    }
}
