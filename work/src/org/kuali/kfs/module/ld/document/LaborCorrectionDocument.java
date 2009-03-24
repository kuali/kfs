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
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.batch.service.LaborReportService;
import org.kuali.kfs.module.ld.batch.service.LaborScrubberService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.util.ReportRegistry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kns.service.BusinessObjectService;
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
    public void handleRouteLevelChange(DocumentRouteLevelChangeDTO change) {
        if (StringUtils.equals(AUTO_APPROVE_ROUTE_LEVEL_NAME, change.getNewNodeName())) {
            String correctionType = getCorrectionTypeCode();
            if (LaborCorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionType) || LaborCorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionType)) {
                String docId = getDocumentHeader().getDocumentNumber();
                // this code is performed asynchronously
                // First, save the origin entries to the origin entry table
                
                // TODO: Shawn - don't need this part from here
                DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                LaborOriginEntryService laborOriginEntryService = SpringContext.getBean(LaborOriginEntryService.class);
                LaborCorrectionDocumentService laborCorrectionDocumentService = SpringContext.getBean(LaborCorrectionDocumentService.class);
                Iterator<LaborOriginEntry> outputEntries = laborCorrectionDocumentService.retrievePersistedOutputOriginEntriesAsIterator(this);
                // Create output group
                java.sql.Date today = dateTimeService.getCurrentSqlDate();
                // Scrub is set to false when the document is initiated. When the document is final, it will be changed to true
                String fileNameWithPath = laborCorrectionDocumentService.generateOutputOriginEntryFileName(docId);
                OriginEntryGroup oeg = laborOriginEntryService.copyEntries(today, OriginEntrySource.LABOR_CORRECTION_PROCESS_EDOC, true, false, true, outputEntries);
                
                // TODO: Shawn - don't need until here
                // Now, run the reports
                LaborReportService reportService = SpringContext.getBean(LaborReportService.class);
                LaborScrubberService laborScrubberService = SpringContext.getBean(LaborScrubberService.class);
                
                String outputFileName = OriginEntrySource.LABOR_CORRECTION_PROCESS_EDOC + "_uploaded_file";
                //build file name with time information
                outputFileName += buildFileExtensionWithDate(today);
                setCorrectionOutputFileName(outputFileName);
                String reportsDirectory = ReportRegistry.getReportsDirectory();
                reportService.generateCorrectionOnlineReport(this, reportsDirectory, today);
                
                laborScrubberService.scrubGroupReportOnly(fileNameWithPath, docId);
            }
        }
    }

    /**
     * If the document final, change the process flag on the output origin entry group (if necessary)
     * 
     * @see org.kuali.rice.kns.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
        }
        else if (getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.ENROUTE);
        }
        if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.DISAPPROVED);
        }
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }

        LaborCorrectionDocumentService laborCorrectionDocumentService = SpringContext.getBean(LaborCorrectionDocumentService.class);
        OriginEntryGroupService originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);

        String docId = getDocumentHeader().getDocumentNumber();
        LaborCorrectionDocument doc = laborCorrectionDocumentService.findByCorrectionDocumentHeaderId(docId);

        if (getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
            String correctionType = doc.getCorrectionTypeCode();
            if (LaborCorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionType)) {
                //originEntryGroupService.dontProcessGroup(doc.getCorrectionInputGroupId());
                String dataFileName = doc.getCorrectionInputFileName();
                String doneFileName = dataFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
                originEntryGroupService.deleteLaborFile(doneFileName);            
                }
            
            else if (LaborCorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionType) || LaborCorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionType)) {
        
                //TODO: Shawn - need to save the output file to originEntry directory when correctionFileDelete is false
                DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                Date today = dateTimeService.getCurrentDate();
                if (!doc.getCorrectionFileDelete()){
                    laborCorrectionDocumentService.createOutputFileForProcessing(doc.getDocumentNumber(), today);
                }
                
                
                // should call scrubber here
                String fileNameWithPath = laborCorrectionDocumentService.generateOutputOriginEntryFileName(docId);
                LaborScrubberService laborScrubberService = SpringContext.getBean(LaborScrubberService.class);
                laborScrubberService.scrubGroupReportOnly(fileNameWithPath, docId);
                }
            else {
                LOG.error("GLCP doc " + doc.getDocumentNumber() + " has an unknown correction type code: " + correctionType);
            }
            
            
        }
    }
}
