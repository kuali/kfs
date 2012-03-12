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
package org.kuali.kfs.sys.batch;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;


public abstract class AbstractWrappedBatchStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(AbstractWrappedBatchStep.class);
    private WrappedBatchExecutorService wrappedBatchExecutorService;
    private List<WrappingBatchService> wrappingBatchServices;

    public boolean execute(String jobName, Date jobRunDate) {
        return wrappedBatchExecutorService.execute(wrappingBatchServices, getCustomBatchExecutor());
    }

    protected abstract WrappedBatchExecutorService.CustomBatchExecutor getCustomBatchExecutor();

    public void setWrappedBatchExecutorService(WrappedBatchExecutorService wrappedBatchExecutorService) {
        this.wrappedBatchExecutorService = wrappedBatchExecutorService;
    }

    public void setWrappingBatchServices(List<WrappingBatchService> wrappingBatchServices) {
        this.wrappingBatchServices = wrappingBatchServices;
    }
}
