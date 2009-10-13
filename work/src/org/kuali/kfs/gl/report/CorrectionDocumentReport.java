/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.gl.report;

import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.CorrectionChange;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.businessobject.CorrectionCriteria;
import org.kuali.kfs.gl.businessobject.options.SearchOperatorsFinder;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;

/**
 * Generates a text file report for the GLCP and LLCP listing out chosen document parameters
 */
public class CorrectionDocumentReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionDocumentReport.class);
    
    /**
     * Initializes report, writes out summary lines, and finalizes
     * 
     * @param reportWriterService report writer than handles the file io
     * @param document GLCP or LLCP document instance to report on
     */
    public void generateReport(DocumentNumberAwareReportWriterService reportWriterService, GeneralLedgerCorrectionProcessDocument document) {
        LOG.debug("generateReport() started");
        
        try {
            reportWriterService.setDocumentNumber(document.getDocumentNumber());
            ((WrappingBatchService) reportWriterService).initialize();
            
            reportWriterService.writeSubTitle(document.getDocumentNumber());
            reportWriterService.writeNewLines(1);
            
            reportWriterService.writeSubTitle("Summary of Input Group");
            reportWriterService.writeFormattedMessageLine("Total Debits/Blanks: " + document.getCorrectionDebitTotalAmount().toString());
            reportWriterService.writeFormattedMessageLine("Total Credits: " + document.getCorrectionCreditTotalAmount().toString());
            reportWriterService.writeFormattedMessageLine("Total No DB/CR: " + document.getCorrectionBudgetTotalAmount().toString());
            reportWriterService.writeFormattedMessageLine("Row Count: " + document.getCorrectionRowCount());
            reportWriterService.writeNewLines(1);
            
            reportWriterService.writeSubTitle("System and Edit Method");
            reportWriterService.writeFormattedMessageLine("System: " + document.getSystem());
            reportWriterService.writeFormattedMessageLine("Edit Method: " + document.getMethod());
            reportWriterService.writeNewLines(1);
            
            reportWriterService.writeSubTitle("Input and Output File");
            reportWriterService.writeFormattedMessageLine("Input File Name:" + document.getCorrectionInputFileName());
            reportWriterService.writeFormattedMessageLine("Output File Name: " + document.getCorrectionOutputFileName());
            reportWriterService.writeNewLines(1);
            
            reportWriterService.writeSubTitle("Edit Options and Action");
            
            String processBatch = "Yes";
            if (document.getCorrectionFileDelete()) {
                processBatch = "No";
            }
            reportWriterService.writeFormattedMessageLine("Process In Batch: " + processBatch);
    
            String outputOnly = "No";
            if (document.getCorrectionSelection()) {
                outputOnly = "Yes";
            }
            reportWriterService.writeFormattedMessageLine("Output only records which match criteria: " + outputOnly);
            
            if (document.getCorrectionTypeCode().equals(CorrectionDocumentService.CORRECTION_TYPE_CRITERIA)) {
                reportWriterService.writeNewLines(1);
                reportWriterService.writeSubTitle("Search Criteria and Modification Criteria");
                reportWriterService.writeNewLines(1);
                
                SearchOperatorsFinder sof = new SearchOperatorsFinder();
    
                for (Iterator ccgi = document.getCorrectionChangeGroup().iterator(); ccgi.hasNext();) {
                    CorrectionChangeGroup ccg = (CorrectionChangeGroup) ccgi.next();
                    
                    reportWriterService.writeFormattedMessageLine("Group " + ccg.getCorrectionChangeGroupLineNumber().toString());
                    
                    reportWriterService.writeSubTitle("Search Criteria");
                    for (Iterator ccri = ccg.getCorrectionCriteria().iterator(); ccri.hasNext();) {
                        CorrectionCriteria cc = (CorrectionCriteria) ccri.next();
                        reportWriterService.writeFormattedMessageLine("Field: " + cc.getCorrectionFieldName() + ", Operator: " + sof.getKeyLabelMap().get(cc.getCorrectionOperatorCode()) + ", Value: " + cc.getCorrectionFieldValue());
                    }
                    
                    reportWriterService.writeSubTitle("Modification Criteria");
                    for (Iterator cchi = ccg.getCorrectionChange().iterator(); cchi.hasNext();) {
                        CorrectionChange cc = (CorrectionChange) cchi.next();
                        reportWriterService.writeFormattedMessageLine("Field: " + cc.getCorrectionFieldName() + ", Replacement Value: " + cc.getCorrectionFieldValue());
                    }
                }    
            }
        } finally {
            ((WrappingBatchService) reportWriterService).destroy();
        }
    }    
    
}
