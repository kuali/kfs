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

import java.util.List;

import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * A step to run the scrubber process.
 */
public class CorrectionProcessScrubberStep extends AbstractWrappedBatchStep {
    public static final String STEP_NAME = "correctionProcessScrubberStep";
    private String documentId;
    private CorrectionDocumentService correctionDocumentService;
    private ScrubberService scrubberService;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return correctionDocumentService.getRequiredDirectoryNames();
    }

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                scrubberService.scrubGroupReportOnly(correctionDocumentService.generateOutputOriginEntryFileName(documentId), documentId);
                return true;
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
