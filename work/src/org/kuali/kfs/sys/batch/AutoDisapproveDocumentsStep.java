/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.sys.batch.service.AutoDisapproveDocumentsService;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Runs the batch job that gathers all documents that are in ENROUTE status and cancels them.
 */
public class AutoDisapproveDocumentsStep extends AbstractWrappedBatchStep {
    private AutoDisapproveDocumentsService autoDisapproveDocumentsService;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoDisapproveDocumentsStep.class);
    private String batchFileDirectoryName;
    /**
     * Overridden to run the auto disapprove process.
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = autoDisapproveDocumentsService.autoDisapproveDocumentsInEnrouteStatus();                
                
                return success;            
            }
        };
    }
        
    /**
     * Sets the autoDisapproveEDocsService attribute value.
     * 
     * @param autoDisapproveEDocsService The autoDisapproveEDocsService to set.
     * @see org.kuali.kfs.sys.service.AutoDisapproveEDocsService
     */
    public void setAutoDisapproveDocumentsService(AutoDisapproveDocumentsService autoDisapproveDocumentsService) {
        this.autoDisapproveDocumentsService = autoDisapproveDocumentsService;
    }
    
    /**
     * This method sets the batchFileDirectoryName
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
