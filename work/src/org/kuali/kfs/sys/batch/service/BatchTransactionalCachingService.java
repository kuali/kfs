/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch.service;


/**
 * This class CANNOT be used by 2 processes simultaneously. It is for very specific batch processes that should not run at the same
 * time, and initialize and destroy must be called and the beginning and end of each process that uses it.
 */
public interface BatchTransactionalCachingService {
    interface BatchTransactionExecutor {
        void executeCustom();
    }
    
    public void execute(BatchTransactionExecutor batchTransactionExecutor);
}
