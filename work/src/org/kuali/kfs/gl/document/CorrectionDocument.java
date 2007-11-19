/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.gl.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.ScrubberService;

import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;

/**
 * 
 * The General Ledger Correction Document, a document that allows editing and processing of 
 * origin entry groups and the origin entries within them.
 * 
 */
public class CorrectionDocument extends TransactionalDocumentBase implements AmountTotaling {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionDocument.class);

    private String correctionTypeCode; // CorrectionDocumentService.CORRECTION_TYPE_MANUAL or
    // CorrectionDocumentService.CORRECTION_TYPE_CRITERIA or
    // CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING
    private boolean correctionSelection; // false if all input rows should be in the output, true if only selected rows should be
    // in the output
    private boolean correctionFileDelete; // false if the file should be processed by scrubber, true if the file should not be
    // processed by scrubber
    private Integer correctionRowCount; // Row count in output group
    private KualiDecimal correctionDebitTotalAmount; // Debit amount total in output group
    private KualiDecimal correctionCreditTotalAmount; // Credit amount total in output group
    private KualiDecimal correctionBudgetTotalAmount; // Budget amount total in output group
    private String correctionInputFileName; // File name if uploaded
    private String correctionOutputFileName; // Not used
    private String correctionScriptText; // Not used
    private Integer correctionInputGroupId; // Group ID that has input data
    private Integer correctionOutputGroupId; // Group ID that has output data
    private Integer correctionChangeGroupNextLineNumber;

    private List<CorrectionChangeGroup> correctionChangeGroup;

    public CorrectionDocument() {
        super();
        correctionChangeGroupNextLineNumber = new Integer(0);

        correctionChangeGroup = new ArrayList<CorrectionChangeGroup>();
    }

    /**
     * Returns a Map representation of the primary key of this document
     * 
     * @return a Map that represents the database key of this document
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * Returns the editing method to use on the origin entries in the document, either "Manual Edit,"
     * "Using Criteria," "Remove Group from Processing," or "Not Available"
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
     * Returns the source of the origin entries this document uses: either an uploaded file of origin entries
     * or the database  
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
     * 
     * This method...
     * @param ccg
     */
    public void addCorrectionChangeGroup(CorrectionChangeGroup ccg) {
        ccg.setDocumentNumber(documentNumber);
        ccg.setCorrectionChangeGroupLineNumber(correctionChangeGroupNextLineNumber++);
        correctionChangeGroup.add(ccg);
    }

    /**
     * 
     * This method...
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
     * 
     * This method...
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

    /**
     * If the document final, change the process flag on the output origin entry group (if necessary)
     * 
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();

        CorrectionDocumentService correctionDocumentService = SpringContext.getBean(CorrectionDocumentService.class);
        OriginEntryGroupService originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);

        String docId = getDocumentHeader().getDocumentNumber();
        CorrectionDocument doc = correctionDocumentService.findByCorrectionDocumentHeaderId(docId);

        if (getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
            String correctionType = doc.getCorrectionTypeCode();
            if (CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionType)) {
                SpringContext.getBean(OriginEntryGroupService.class).dontProcessGroup(doc.getCorrectionInputGroupId());
            }
            else if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionType) || CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionType)) {
                OriginEntryGroup outputGroup = originEntryGroupService.getExactMatchingEntryGroup(doc.getCorrectionOutputGroupId().intValue());
                if (!doc.getCorrectionFileDelete()) {
                    LOG.debug("handleRouteStatusChange() Mark group as to be processed");
                    outputGroup.setProcess(true);
                    originEntryGroupService.save(outputGroup);
                }
            }
            else {
                LOG.error("GLCP doc " + doc.getDocumentNumber() + " has an unknown correction type code: " + correctionType);
            }
        }
    }


    /**
     * Constant for the workgroup approval routing level
     */
    private static final Integer WORKGROUP_APPROVAL_ROUTE_LEVEL = new Integer(1);

    /**
     * Waits for the event of the route level changing to "Approve" and at that point, saving all the entries as origin entries in a newly created
     * origin entry group, then scrubbing those entries
     * 
     * @param cahnge a representation of the route level changed that just occurred
     * @see org.kuali.core.document.DocumentBase#handleRouteLevelChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO)
     */
    @Override
    public void handleRouteLevelChange(DocumentRouteLevelChangeVO change) {
        super.handleRouteLevelChange(change);
        if (WORKGROUP_APPROVAL_ROUTE_LEVEL.equals(change.getNewRouteLevel())) {
            String correctionType = getCorrectionTypeCode();
            if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionType) || CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionType)) {
                String docId = getDocumentHeader().getDocumentNumber();
                // this code is performed asynchronously

                // First, save the origin entries to the origin entry table
                DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                OriginEntryService originEntryService = SpringContext.getBean(OriginEntryService.class);
                CorrectionDocumentService correctionDocumentService = SpringContext.getBean(CorrectionDocumentService.class);

                Iterator<OriginEntryFull> outputEntries = correctionDocumentService.retrievePersistedOutputOriginEntriesAsIterator(this);

                // Create output group
                java.sql.Date today = dateTimeService.getCurrentSqlDate();
                // Scrub is set to false when the document is initiated. When the document is final, it will be changed to true
                OriginEntryGroup oeg = originEntryService.copyEntries(today, OriginEntrySource.GL_CORRECTION_PROCESS_EDOC, true, false, true, outputEntries);

                // Now, run the reports
                ReportService reportService = SpringContext.getBean(ReportService.class);
                ScrubberService scrubberService = SpringContext.getBean(ScrubberService.class);

                setCorrectionOutputGroupId(oeg.getId());
                // not using the document service to save because it touches workflow, just save the doc BO as a regular BO
                SpringContext.getBean(BusinessObjectService.class).save(this);

                LOG.debug("handleRouteStatusChange() Run reports");

                reportService.correctionOnlineReport(this, today);

                // Run the scrubber on this group to generate a bunch of reports. The scrubber won't save anything when running it
                // this way.
                scrubberService.scrubGroupReportOnly(oeg, docId);
            }
        }
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
     * @see org.kuali.core.document.DocumentBase#setDocumentNumber(java.lang.String)
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
        return correctionDebitTotalAmount;
    }

    public void setCorrectionDebitTotalAmount(KualiDecimal correctionDebitTotalAmount) {
        this.correctionDebitTotalAmount = correctionDebitTotalAmount;
    }

    public KualiDecimal getCorrectionCreditTotalAmount() {
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

    public Integer getCorrectionInputGroupId() {
        return correctionInputGroupId;
    }

    public void setCorrectionInputGroupId(Integer correctionInputGroupId) {
        this.correctionInputGroupId = correctionInputGroupId;
    }

    public Integer getCorrectionOutputGroupId() {
        return correctionOutputGroupId;
    }

    public void setCorrectionOutputGroupId(Integer correctionOutputGroupId) {
        this.correctionOutputGroupId = correctionOutputGroupId;
    }
}
