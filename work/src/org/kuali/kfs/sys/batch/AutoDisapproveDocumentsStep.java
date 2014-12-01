/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
