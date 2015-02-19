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
