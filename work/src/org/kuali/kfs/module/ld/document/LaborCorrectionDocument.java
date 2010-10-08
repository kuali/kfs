/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.document;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.batch.LaborCorrectionProcessScrubberStep;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryGroupService;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * labor Document class for the Labor Ledger Correction Process.
 */
public class LaborCorrectionDocument extends GeneralLedgerCorrectionProcessDocument implements AmountTotaling {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCorrectionDocument.class);

    /**
     * Constructs a LaborCorrectionDocument.java.
     */
    public LaborCorrectionDocument() {
        super();
    }

    /**
     * @param change
     * @see org.kuali.rice.kns.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            String docId = getDocumentHeader().getDocumentNumber();
            // this code is performed asynchronously
            // First, save the origin entries to the origin entry table
            LaborCorrectionDocumentService laborCorrectionDocumentService = SpringContext.getBean(LaborCorrectionDocumentService.class);
            OriginEntryGroupService originEntryGroupService = (OriginEntryGroupService) SpringContext.getBean(LaborOriginEntryGroupService.class);; 
            LaborCorrectionDocument doc = laborCorrectionDocumentService.findByCorrectionDocumentHeaderId(docId);

            String correctionType = doc.getCorrectionTypeCode();
            if (LaborCorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionType)) {
                String dataFileName = doc.getCorrectionInputFileName();
                String doneFileName = dataFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
                originEntryGroupService.deleteFile(doneFileName);
            }

            else if (LaborCorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionType) 
                    || LaborCorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionType)) {
                synchronized ( LaborCorrectionDocumentService.class ) {
                    if ( !checkForExistingOutputDocument( doc.getDocumentNumber() ) ) {
                        // TODO:- need to save the output file to originEntry directory when correctionFileDelete is false
                        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                        Date today = dateTimeService.getCurrentDate();
                        String outputFileName = "";
                        if (!doc.getCorrectionFileDelete()) {
                            outputFileName = laborCorrectionDocumentService.createOutputFileForProcessing(doc.getDocumentNumber(), today);
                        }
        
                        doc.setCorrectionOutputFileName(outputFileName);
                        Step step = BatchSpringContext.getStep(LaborCorrectionProcessScrubberStep.STEP_NAME);
                        
                        LaborCorrectionProcessScrubberStep correctionStep = (LaborCorrectionProcessScrubberStep) ProxyUtils.getTargetIfProxied(step);
                        correctionStep.setDocumentId(docId);
                        try {
                            step.execute(getClass().getName(), dateTimeService.getCurrentDate());
                        }
                        catch (Exception e) {
                            LOG.error("LLCP scrubber encountered error:", e);
                            throw new RuntimeException("LLCP scrubber encountered error:", e);
                        }
                        
                        correctionStep = (LaborCorrectionProcessScrubberStep) ProxyUtils.getTargetIfProxied(step);
                        correctionStep.setDocumentId(null);
                        
                        laborCorrectionDocumentService.generateCorrectionReport(this);
                        laborCorrectionDocumentService.aggregateCorrectionDocumentReports(this);
                    } else {
                        LOG.warn( "Attempt to re-process final LLCP operations for document: " + doc.getDocumentNumber() + "  File with that document number already exists." );
                    }
                }
            }
            else {
                LOG.error("LLCP doc " + doc.getDocumentNumber() + " has an unknown correction type code: " + correctionType);
            }
        }
    }

}
