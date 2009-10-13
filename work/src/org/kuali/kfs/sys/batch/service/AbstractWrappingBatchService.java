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
package org.kuali.kfs.sys.batch.service;

import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractWrappingBatchService implements WrappedBatchExecutorService {
    @Transactional
    public void execute(CustomBatchExecutor batchTransactionExecutor) {
        try {
            initialize();
            batchTransactionExecutor.execute();
        }
        finally {
            destroy();
        }
    }
    
    protected abstract void initialize();

    protected abstract void destroy();
}
