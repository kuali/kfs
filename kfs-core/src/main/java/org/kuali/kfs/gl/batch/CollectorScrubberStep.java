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

import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.gl.service.impl.ScrubberStatus;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * A step to run the scrubber process.
 */
public class CollectorScrubberStep extends AbstractWrappedBatchStep {
    public static final String STEP_NAME = "collectorScrubberStep";
    private ScrubberStatus scrubberStatus;
    private CollectorBatch batch;
    private CollectorReportData collectorReportData;
    private ScrubberService scrubberService;

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                scrubberService.scrubCollectorBatch(scrubberStatus, batch, collectorReportData);
                return true;
            }
        };
    }
    
    public void setScrubberStatus(ScrubberStatus scrubberStatus) {
        this.scrubberStatus = scrubberStatus;
    }
    public void setBatch(CollectorBatch batch) {
        this.batch = batch;
    }
    public void setCollectorReportData(CollectorReportData collectorReportData) {
        this.collectorReportData = collectorReportData;
    }
    public void setScrubberService(ScrubberService scrubberService) {
        this.scrubberService = scrubberService;
    }
}
