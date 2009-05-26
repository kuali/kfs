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
package org.kuali.kfs.module.ld.document;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.batch.LaborCorrectionProcessScrubberStep;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * labor Document class for the Labor Ledger Correction Process.
 */
public class LaborCorrectionDocument extends GeneralLedgerCorrectionProcessDocument implements AmountTotaling {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCorrectionDocument.class);

    /**
     * Constructs a LaborCorrectionDocument.java.
     */
    public LaborCorrectionDocument() {
        super();
    }

    /**
     * @param change
     * @see org.kuali.rice.kns.document.DocumentBase#handleRouteLevelChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    @Override
    public void doRouteLevelChange(DocumentRouteLevelChangeDTO change) {
        //super.doRouteLevelChange(change);
        if (StringUtils.equals(change.getNewNodeName(), AUTO_APPROVE_ROUTE_LEVEL_NAME)) {
            String docId = getDocumentHeader().getDocumentNumber();
            // this code is performed asynchronously
            // First, save the origin entries to the origin entry table
            LaborCorrectionDocumentService laborCorrectionDocumentService = SpringContext.getBean(LaborCorrectionDocumentService.class);
            OriginEntryGroupService originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);
            LaborCorrectionDocument doc = laborCorrectionDocumentService.findByCorrectionDocumentHeaderId(docId);

            String correctionType = doc.getCorrectionTypeCode();
            if (LaborCorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionType)) {
                String dataFileName = doc.getCorrectionInputFileName();
                String doneFileName = dataFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
                originEntryGroupService.deleteFile(doneFileName);
            }

            else if (LaborCorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionType) || LaborCorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionType)) {

                // TODO: Shawn - need to save the output file to originEntry directory when correctionFileDelete is false
                DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                Date today = dateTimeService.getCurrentDate();
                String outputFileName = "";
                if (!doc.getCorrectionFileDelete()) {
                    outputFileName = laborCorrectionDocumentService.createOutputFileForProcessing(doc.getDocumentNumber(), today);
                }

                doc.setCorrectionOutputFileName(outputFileName);
                LaborCorrectionProcessScrubberStep step = (LaborCorrectionProcessScrubberStep) BatchSpringContext.getStep(LaborCorrectionProcessScrubberStep.STEP_NAME);
                step.setDocumentId(docId);
                step.execute(getClass().getName(), dateTimeService.getCurrentDate());
                step.setDocumentId(null);
                
                laborCorrectionDocumentService.generateCorrectionReport(this);
            }
            else {
                LOG.error("LLCP doc " + doc.getDocumentNumber() + " has an unknown correction type code: " + correctionType);
            }
        }
    }

}
