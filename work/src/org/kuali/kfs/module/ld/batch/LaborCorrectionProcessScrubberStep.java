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
package org.kuali.kfs.module.ld.batch;

import org.kuali.kfs.module.ld.batch.service.LaborScrubberService;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Labor scrubber Batch Step.
 */
public class LaborCorrectionProcessScrubberStep extends AbstractWrappedBatchStep {
    public static final String STEP_NAME = "laborCorrectionProcessScrubberStep";
    private String documentId;
    private LaborCorrectionDocumentService laborCorrectionDocumentService;
    private LaborScrubberService laborScrubberService;

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                laborScrubberService.scrubGroupReportOnly(laborCorrectionDocumentService.generateOutputOriginEntryFileName(documentId), documentId);
                return true;
            }
        };
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setLaborCorrectionDocumentService(LaborCorrectionDocumentService laborCorrectionDocumentService) {
        this.laborCorrectionDocumentService = laborCorrectionDocumentService;
    }

    public void setLaborScrubberService(LaborScrubberService laborScrubberService) {
        this.laborScrubberService = laborScrubberService;
    }
}
