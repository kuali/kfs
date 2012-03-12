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

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.batch.LaborCorrectionProcessScrubberStep;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

/**
 * labor Document class for the Labor Ledger Correction Process.
 */
public class LaborCorrectionDocument extends GeneralLedgerCorrectionProcessDocument {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCorrectionDocument.class);

    /**
     * Constructs a LaborCorrectionDocument.java.
     */
    public LaborCorrectionDocument() {
        super();
    }

    /**
     * @param change
     * @see org.kuali.rice.krad.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        // NOTE: DO NOT call the super implementation.  It has the GLCP processing logic which should not be run here.
        // The code below is copied from the super.super implementation:
        if (getDocumentHeader().getWorkflowDocument().isCanceled()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
        } else if (getDocumentHeader().getWorkflowDocument().isEnroute()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.ENROUTE);
        }
        if (getDocumentHeader().getWorkflowDocument().isDisapproved()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.DISAPPROVED);
        }
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Document: " + statusChangeEvent.getDocumentId() + " -- Status is: " + getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
        }
        // End of super.super code
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            String docId = getDocumentNumber();
            if ( LOG.isInfoEnabled() ) {
                LOG.info( "Document " + docId + " moving to Processed Status - starting final processing");
            }
            // this code is performed asynchronously
            // First, save the origin entries to the origin entry table
            LaborCorrectionDocumentService laborCorrectionDocumentService = SpringContext.getBean(LaborCorrectionDocumentService.class);
            OriginEntryGroupService originEntryGroupService = (OriginEntryGroupService) SpringContext.getBean(LaborOriginEntryGroupService.class);
            
            // QUESTION - since this *is* the labor correction document - why are we loading it again?
            LaborCorrectionDocument doc = laborCorrectionDocumentService.findByCorrectionDocumentHeaderId(docId);

            String correctionType = doc.getCorrectionTypeCode();
            if (LaborCorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionType)) {
                
                String dataFileName = doc.getCorrectionInputFileName();
                String doneFileName = dataFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
                originEntryGroupService.deleteFile(doneFileName);
                if ( LOG.isInfoEnabled() ) {
                    LOG.info( "Document " + docId + " : deleted done file to remove from processing: " + doneFileName );
                }
                
            } else if (LaborCorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionType) 
                    || LaborCorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionType)) {
                
                synchronized ( LaborCorrectionDocumentService.class ) {
                    if ( !checkForExistingOutputDocument( docId ) ) {

                        Date today = SpringContext.getBean(DateTimeService.class).getCurrentDate();

                        String outputFileName = "";
                        if (!doc.getCorrectionFileDelete()) {
                            outputFileName = laborCorrectionDocumentService.createOutputFileForProcessing(docId, today);
                        } else {
                            if ( LOG.isInfoEnabled() ) {
                                LOG.info( "Document " + docId + " : set to delete output file - no file will be created" );
                            }
                        }
                        doc.setCorrectionOutputFileName(outputFileName);
                        
                        if ( LOG.isInfoEnabled() ) {
                            LOG.info( "Document " + docId + " : about to run scrubber -- output file: " + outputFileName );
                        }                        
                        Step step = BatchSpringContext.getStep(LaborCorrectionProcessScrubberStep.STEP_NAME);                        
                        LaborCorrectionProcessScrubberStep correctionStep = (LaborCorrectionProcessScrubberStep) ProxyUtils.getTargetIfProxied(step);
                        correctionStep.setDocumentId(docId);
                        try {
                            step.execute(getClass().getName(), today);
                        } catch (Exception e) {
                            LOG.error("LLCP scrubber encountered error:", e);
                            throw new RuntimeException("LLCP scrubber encountered error:", e);
                        }
                        correctionStep.setDocumentId(null);
                        if ( LOG.isInfoEnabled() ) {
                            LOG.info( "Document " + docId + " : completed scrubber run -- generating reports" );
                        }                        
                        
                        laborCorrectionDocumentService.generateCorrectionReport(this);
                        laborCorrectionDocumentService.aggregateCorrectionDocumentReports(this);
                    } else {
                        LOG.warn( "Attempt to re-process final LLCP operations for document: " + docId + "  File with that document number already exists." );
                    }
                }
            } else {
                LOG.error("LLCP doc " + docId + " has an unknown correction type code: " + correctionType);
            }
            if ( LOG.isInfoEnabled() ) {
                LOG.info( "Document " + docId + " moving to Processed Status - completed final processing");
            }
        }
    }

}
