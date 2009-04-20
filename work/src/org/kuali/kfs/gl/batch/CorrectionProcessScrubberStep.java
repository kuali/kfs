/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.sys.batch.AbstractBatchTransactionalCachingStep;
import org.kuali.kfs.sys.batch.service.BatchTransactionalCachingService.BatchTransactionExecutor;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * A step to run the scrubber process.
 */
public class CorrectionProcessScrubberStep extends AbstractBatchTransactionalCachingStep {
    public static final String STEP_NAME = "correctionProcessScrubberStep";
    private String documentId;
    private CorrectionDocumentService correctionDocumentService;
    private ScrubberService scrubberService;

    @Override
    protected BatchTransactionExecutor getBatchTransactionExecutor() {
        return new BatchTransactionExecutor() {
            public void executeCustom() {
                scrubberService.scrubGroupReportOnly(correctionDocumentService.generateOutputOriginEntryFileName(documentId), documentId);
            }
        };
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setCorrectionDocumentService(CorrectionDocumentService correctionDocumentService) {
        this.correctionDocumentService = correctionDocumentService;
    }

    public void setScrubberService(ScrubberService scrubberService) {
        this.scrubberService = scrubberService;
    }
}
