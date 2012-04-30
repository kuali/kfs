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
package org.kuali.kfs.module.purap.batch;

import org.kuali.kfs.module.purap.batch.service.MigratePurapStatCodeToWorkflowDocumentService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * step to move status code from purap documents to applicationdocumentstatus on workflow documents side.
 */
public class MigratePurapStatCodeToWorkflowDocumentStep extends AbstractWrappedBatchStep {

    protected MigratePurapStatCodeToWorkflowDocumentService migratePurapStatCodeToWorkflowDocumentService;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MigratePurapStatCodeToWorkflowDocumentStep.class);
    protected String batchFileDirectoryName;

    /**
     * Overridden to run the auto disapprove process.
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = migratePurapStatCodeToWorkflowDocumentService.migratePurapStatCodeToWorkflowDocuments();                
                
                return success;            
            }
        };
    }
        
    /**
     * This method sets the batchFileDirectoryName
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
    
    /**
     * Gets the migratePurapStatCodeToWorkflowDocumentService attribute.
     * 
     * @return Returns the migratePurapStatCodeToWorkflowDocumentService
     */
    
    public MigratePurapStatCodeToWorkflowDocumentService getMigratePurapStatCodeToWorkflowDocumentService() {
        return migratePurapStatCodeToWorkflowDocumentService;
    }

    /** 
     * Sets the migratePurapStatCodeToWorkflowDocumentService attribute.
     * 
     * @param migratePurapStatCodeToWorkflowDocumentService The migratePurapStatCodeToWorkflowDocumentService to set.
     */
    public void setMigratePurapStatCodeToWorkflowDocumentService(MigratePurapStatCodeToWorkflowDocumentService migratePurapStatCodeToWorkflowDocumentService) {
        this.migratePurapStatCodeToWorkflowDocumentService = migratePurapStatCodeToWorkflowDocumentService;
    }
}
