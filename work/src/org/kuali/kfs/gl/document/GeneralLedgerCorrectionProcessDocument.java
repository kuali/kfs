/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.gl.document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.CorrectionProcessScrubberStep;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The General Ledger Correction Document, a document that allows editing and processing of origin entry groups and the origin
 * entries within them.
 */
public class GeneralLedgerCorrectionProcessDocument extends FinancialSystemTransactionalDocumentBase implements AmountTotaling {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerCorrectionProcessDocument.class);

    protected String correctionTypeCode; // CorrectionDocumentService.CORRECTION_TYPE_MANUAL or
    protected boolean correctionSelection; // false if all input rows should be in the output, true if only selected rows should be
    // in the output
    protected boolean correctionFileDelete; // false if the file should be processed by scrubber, true if the file should not be
    // processed by scrubber
    protected Integer correctionRowCount; // Row count in output group
    protected KualiDecimal correctionDebitTotalAmount; // Debit amount total in output group
    protected KualiDecimal correctionCreditTotalAmount; // Credit amount total in output group
    protected KualiDecimal correctionBudgetTotalAmount; // Budget amount total in output group
    protected String correctionInputFileName; // input file name
    protected String correctionOutputFileName; // output file name
    protected String correctionScriptText; // Not used
    protected Integer correctionChangeGroupNextLineNumber;

    protected List<CorrectionChangeGroup> correctionChangeGroup;

       public GeneralLedgerCorrectionProcessDocument() {
        super();
        correctionChangeGroupNextLineNumber = new Integer(0);

        correctionChangeGroup = new ArrayList<CorrectionChangeGroup>();
    }

    /**
     * Returns a Map representation of the primary key of this document
     * 
     * @return a Map that represents the database key of this document
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * Returns the editing method to use on the origin entries in the document, either "Manual Edit," "Using Criteria," "Remove
     * Group from Processing," or "Not Available"
     * 
     * @return the String representation of the method this document is using
     */
    public String getMethod() {
        if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionTypeCode)) {
            return "Manual Edit";
        }
        else if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionTypeCode)) {
            return "Using Criteria";
        }
        else if (CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionTypeCode)) {
            return "Remove Group from Processing";
        }
        else {
            return KFSConstants.NOT_AVAILABLE_STRING;
        }
    }

    /**
     * Returns the source of the origin entries this document uses: either an uploaded file of origin entries or the database
     * 
     * @return a String with the name of the system in use
     */
    public String getSystem() {
        if (correctionInputFileName != null) {
            return "File Upload";
        }
        else {
            return "Database";
        }
    }

    /**
     * This method...
     * 
     * @param ccg
     */
    public void addCorrectionChangeGroup(CorrectionChangeGroup ccg) {
        ccg.setDocumentNumber(documentNumber);
        ccg.setCorrectionChangeGroupLineNumber(correctionChangeGroupNextLineNumber++);
        correctionChangeGroup.add(ccg);
    }

    /**
     * This method...
     * 
     * @param changeNumber
     */
    public void removeCorrectionChangeGroup(int changeNumber) {
        for (Iterator iter = correctionChangeGroup.iterator(); iter.hasNext();) {
            CorrectionChangeGroup element = (CorrectionChangeGroup) iter.next();
            if (changeNumber == element.getCorrectionChangeGroupLineNumber().intValue()) {
                iter.remove();
            }
        }
    }

    /**
     * This method...
     * 
     * @param groupNumber
     * @return
     */
    public CorrectionChangeGroup getCorrectionChangeGroupItem(int groupNumber) {
        for (Iterator iter = correctionChangeGroup.iterator(); iter.hasNext();) {
            CorrectionChangeGroup element = (CorrectionChangeGroup) iter.next();
            if (groupNumber == element.getCorrectionChangeGroupLineNumber().intValue()) {
                return element;
            }
        }

        CorrectionChangeGroup ccg = new CorrectionChangeGroup(documentNumber, groupNumber);
        correctionChangeGroup.add(ccg);

        return ccg;
    }
    
    
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("GLCP Route status Change: " + statusChangeEvent);
            }
            CorrectionDocumentService correctionDocumentService = SpringContext.getBean(CorrectionDocumentService.class);
            OriginEntryGroupService originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);

            String docId = getDocumentHeader().getDocumentNumber();
            GeneralLedgerCorrectionProcessDocument doc = correctionDocumentService.findByCorrectionDocumentHeaderId(docId);
            
            String correctionType = doc.getCorrectionTypeCode();
            if (CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionType)) {

                String dataFileName = doc.getCorrectionInputFileName();
                String doneFileName = dataFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
                originEntryGroupService.deleteFile(doneFileName);

            }
            else if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionType) 
                    || CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionType)) {
                // KFSMI-5760 - apparently, this node can be executed more than once, which results in multiple
                // files being created.  We need to check for the existence of a file with the proper
                // name pattern and abort the rest of this if found
                synchronized ( CorrectionDocumentService.class ) {
                    if ( !checkForExistingOutputDocument( doc.getDocumentNumber() ) ) {
                        // save the output file to originEntry directory when correctionFileDelete is false
                        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                        Date today = dateTimeService.getCurrentDate();
    
                        // generate output file and set file name
                        String outputFileName = "";
                        if (!correctionFileDelete) {
                            outputFileName = correctionDocumentService.createOutputFileForProcessing(doc.getDocumentNumber(), today);
                        }
                        doc.setCorrectionOutputFileName(outputFileName);
                        Step step = BatchSpringContext.getStep(CorrectionProcessScrubberStep.STEP_NAME);
                        CorrectionProcessScrubberStep correctionStep = (CorrectionProcessScrubberStep) ProxyUtils.getTargetIfProxied(step);
                        correctionStep.setDocumentId(docId);
        
                        try {
                            step.execute(getClass().getName(), dateTimeService.getCurrentDate());
                        }
                        catch (Exception e) {
                            LOG.error("GLCP scrubber encountered error:", e);
                            throw new RuntimeException("GLCP scrubber encountered error:", e);
                        }
        
                        correctionStep = (CorrectionProcessScrubberStep) ProxyUtils.getTargetIfProxied(step);
                        correctionStep.setDocumentId(null);
        
                        correctionDocumentService.generateCorrectionReport(this);
                        correctionDocumentService.aggregateCorrectionDocumentReports(this);
                    } else {
                        LOG.warn( "Attempt to re-process final GLCP operations for document: " + doc.getDocumentNumber() + "  File with that document number already exists." );
                    }
                }
            }
            else {
                LOG.error("GLCP doc " + doc.getDocumentNumber() + " has an unknown correction type code: " + correctionType);
            }
        }
    }

    /**
     * Returns true if an existing document like "glcp_output.docNum" is found.
     */
    protected boolean checkForExistingOutputDocument( String documentNumber ) {
        CorrectionDocumentService correctionDocumentService = SpringContext.getBean(CorrectionDocumentService.class);
        String[] filenamesFound = correctionDocumentService.findExistingCorrectionOutputFilesForDocument(documentNumber);
        if ( LOG.isInfoEnabled() ) {
            LOG.info( "Scanned for output files for document: " + documentNumber );
            LOG.info( "Files Found: " + Arrays.toString(filenamesFound));
        }
        return filenamesFound != null && filenamesFound.length > 0;
    }
    

    /**
     * Waits for the event of the route level changing to "Approve" and at that point, saving all the entries as origin entries in a
     * newly created origin entry group, then scrubbing those entries
     * 
     * @param cahnge a representation of the route level changed that just occurred
     * @see org.kuali.rice.krad.document.DocumentBase#handleRouteLevelChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    @Override
    public void doRouteLevelChange(DocumentRouteLevelChange change) {
        super.doRouteLevelChange(change);
    }

    /**
     * Returns the total dollar amount associated with this document
     * 
     * @return if credit total is zero, debit total, otherwise credit total
     */
    public KualiDecimal getTotalDollarAmount() {
        return getCorrectionCreditTotalAmount().add(getCorrectionDebitTotalAmount());
    }

    /**
     * Sets this document's document number, but also sets the document number on all children objects
     * 
     * @param documentNumber the document number for this document
     * @see org.kuali.rice.krad.document.DocumentBase#setDocumentNumber(java.lang.String)
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        super.setDocumentNumber(documentNumber);

        for (Iterator iter = correctionChangeGroup.iterator(); iter.hasNext();) {
            CorrectionChangeGroup element = (CorrectionChangeGroup) iter.next();
            element.setDocumentNumber(documentNumber);
        }
    }

    public String getCorrectionTypeCode() {
        return correctionTypeCode;
    }

    public void setCorrectionTypeCode(String correctionTypeCode) {
        this.correctionTypeCode = correctionTypeCode;
    }

    public boolean getCorrectionSelection() {
        return correctionSelection;
    }

    public void setCorrectionSelection(boolean correctionSelection) {
        this.correctionSelection = correctionSelection;
    }

    public boolean getCorrectionFileDelete() {
        return correctionFileDelete;
    }

    public void setCorrectionFileDelete(boolean correctionFileDelete) {
        this.correctionFileDelete = correctionFileDelete;
    }

    public Integer getCorrectionRowCount() {
        return correctionRowCount;
    }

    public void setCorrectionRowCount(Integer correctionRowCount) {
        this.correctionRowCount = correctionRowCount;
    }

    public Integer getCorrectionChangeGroupNextLineNumber() {
        return correctionChangeGroupNextLineNumber;
    }

    public void setCorrectionChangeGroupNextLineNumber(Integer correctionChangeGroupNextLineNumber) {
        this.correctionChangeGroupNextLineNumber = correctionChangeGroupNextLineNumber;
    }

    public KualiDecimal getCorrectionDebitTotalAmount() {
        if (ObjectUtils.isNull(correctionDebitTotalAmount)) {
            return KualiDecimal.ZERO;
        }
        
        return correctionDebitTotalAmount;
    }

    public void setCorrectionDebitTotalAmount(KualiDecimal correctionDebitTotalAmount) {
        this.correctionDebitTotalAmount = correctionDebitTotalAmount;
    }

    public KualiDecimal getCorrectionCreditTotalAmount() {
        if (ObjectUtils.isNull(correctionCreditTotalAmount)) {
            return KualiDecimal.ZERO;
        }
        
        return correctionCreditTotalAmount;
    }

    public void setCorrectionCreditTotalAmount(KualiDecimal correctionCreditTotalAmount) {
        this.correctionCreditTotalAmount = correctionCreditTotalAmount;
    }

    public KualiDecimal getCorrectionBudgetTotalAmount() {
        return correctionBudgetTotalAmount;
    }

    public void setCorrectionBudgetTotalAmount(KualiDecimal correctionBudgetTotalAmount) {
        this.correctionBudgetTotalAmount = correctionBudgetTotalAmount;
    }

    public String getCorrectionInputFileName() {
        return correctionInputFileName;
    }

    public void setCorrectionInputFileName(String correctionInputFileName) {
        this.correctionInputFileName = correctionInputFileName;
    }

    public String getCorrectionOutputFileName() {
        return correctionOutputFileName;
    }

    public void setCorrectionOutputFileName(String correctionOutputFileName) {
        this.correctionOutputFileName = correctionOutputFileName;
    }

    public List<CorrectionChangeGroup> getCorrectionChangeGroup() {
        Collections.sort(correctionChangeGroup);
        return correctionChangeGroup;
    }

    public void setCorrectionChangeGroup(List<CorrectionChangeGroup> correctionChangeGroup) {
        this.correctionChangeGroup = correctionChangeGroup;
    }

    protected String buildFileExtensionWithDate(Date date) {
        String dateFormatStr = ".yyyy-MMM-dd.HH-mm-ss";
        DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);

        return dateFormat.format(date) + GeneralLedgerConstants.BatchFileSystem.EXTENSION;


    }
}
