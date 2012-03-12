/*
 * Copyright 2007 The Kuali Foundation
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

import java.util.List;

import org.kuali.kfs.gl.batch.service.CollectorReportService;
import org.kuali.kfs.gl.batch.service.CollectorService;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Batch step that controls the collector process. The basic steps in the collector process are the following: 1) Retrieves files
 * that need processed 2) Parses each file into a CollectorBatch object using the collector digester rules 3) Validation of contents
 * in CollectorService 4) Stores origin group, gl entries, and id billings for each batch 5) Sends email to workgroup listed in the
 * batch file header with process results 6) Cleans up .done files
 */
public class CollectorStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorStep.class);

    private CollectorService collectorService;
    private CollectorReportService collectorReportService;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return collectorService.getRequiredDirectoryNames();
    }
    
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                CollectorReportData collectorReportData = collectorService.performCollection();
                collectorReportService.sendEmails(collectorReportData);
                collectorReportService.generateCollectorRunReports(collectorReportData);
                // remove done files and create done file for collector output
                collectorService.finalizeCollector(collectorReportData);
                return true;
            }
        };
    }

    /**
     * Gets the collectorService attribute.
     * 
     * @return Returns the collectorService.
     */
    public CollectorService getCollectorService() {
        return collectorService;
    }

    /**
     * Sets the collectorService attribute value.
     * 
     * @param collectorService The collectorService to set.
     */
    public void setCollectorService(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    /**
     * Gets the collectorReportService attribute.
     * 
     * @return Returns the collectorReportService.
     */
    public CollectorReportService getCollectorReportService() {
        return collectorReportService;
    }

    /**
     * Sets the collectorReportService attribute value.
     * 
     * @param collectorReportService The collectorReportService to set.
     */
    public void setCollectorReportService(CollectorReportService collectorReportService) {
        this.collectorReportService = collectorReportService;
    }
}
