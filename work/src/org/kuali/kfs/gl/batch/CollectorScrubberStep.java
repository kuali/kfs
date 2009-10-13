/*
 * Copyright 2005-2009 The Kuali Foundation
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
