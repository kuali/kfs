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
package org.kuali.kfs.gl.batch;

import java.util.Date;

import org.kuali.kfs.gl.batch.service.PosterService;
import org.kuali.kfs.sys.batch.AbstractBatchTransactionalCachingStep;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.service.BatchTransactionalCachingService.BatchTransactionExecutor;

/**
 * The step that runs the poster service on indirect cost recovery entries.
 */
public class PosterIndirectCostRecoveryEntriesStep extends AbstractBatchTransactionalCachingStep {
    private PosterService posterService;

    @Override
    protected BatchTransactionExecutor getBatchTransactionExecutor() {
        return new BatchTransactionExecutor() {
            public void executeCustom() {
                posterService.postIcrEntries();
            }
        };
    }

    public void setPosterService(PosterService posterService) {
        this.posterService = posterService;
    }
}
