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

import org.kuali.kfs.gl.batch.service.CollectorReportService;
import org.kuali.kfs.gl.batch.service.CollectorService;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.sys.batch.AbstractBatchTransactionalCachingStep;
import org.kuali.kfs.sys.batch.service.BatchTransactionalCachingService.BatchTransactionExecutor;

/**
 * Batch step that controls the collector process. The basic steps in the collector process are the following: 1) Retrieves files
 * that need processed 2) Parses each file into a CollectorBatch object using the collector digester rules 3) Validation of contents
 * in CollectorService 4) Stores origin group, gl entries, and id billings for each batch 5) Sends email to workgroup listed in the
 * batch file header with process results 6) Cleans up .done files
 */
public class CollectorStep extends AbstractBatchTransactionalCachingStep {

    private CollectorService collectorService;
    private CollectorReportService collectorReportService;

    @Override
    protected BatchTransactionExecutor getBatchTransactionExecutor() {
        return new BatchTransactionExecutor() {
            public void executeCustom() {
                CollectorReportData collectorReportData = collectorService.performCollection();
                collectorReportService.sendEmails(collectorReportData);
                collectorReportService.generateCollectorRunReports(collectorReportData);
            }
        };
    }
    public void setCollectorService(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    public void setCollectorReportService(CollectorReportService collectorReportService) {
        this.collectorReportService = collectorReportService;
    }
}
