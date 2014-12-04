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
