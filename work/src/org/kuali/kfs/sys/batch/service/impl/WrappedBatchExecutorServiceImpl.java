/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.springframework.transaction.annotation.Transactional;

public class WrappedBatchExecutorServiceImpl implements WrappedBatchExecutorService {
    private static final Logger LOG = Logger.getLogger(WrappedBatchExecutorServiceImpl.class);

    @Transactional
    public boolean execute(List<WrappingBatchService> wrappingBatchServices, CustomBatchExecutor customBatchExecutor) {
        boolean continueJob;
        try {
            for (WrappingBatchService wrappingBatchService : wrappingBatchServices) {
                wrappingBatchService.initialize();
            }
            continueJob = customBatchExecutor.execute();
        }
        finally {
            for (WrappingBatchService wrappingBatchService : wrappingBatchServices) {
                try {
                    wrappingBatchService.destroy();
                }
                catch (Exception e) {
                    LOG.error("Caught exception while destroying service: " + wrappingBatchService.getClass(), e);
                }
            }
        }
        return continueJob;
    }
}
