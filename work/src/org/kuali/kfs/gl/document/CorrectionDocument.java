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

import org.kuali.PropertyConstants;
import org.kuali.core.document.DocumentBase;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.ScrubberService;

/**
 * 
 */
public class CorrectionDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionDocument.class);

    private String correctionTypeCode; // CorrectionDocumentService.CORRECTION_TYPE_MANUAL or
                                        // CorrectionDocumentService.CORRECTION_TYPE_CRITERIA
    private boolean correctionSelection; // false if all input rows should be in the output, true if only selected rows should be
                                            // in the output
    private boolean correctionFileDelete; // false if the file should be processed by scrubber, true if the file should not be
                                            // processed by scrubber
    private Integer correctionRowCount; // Row count in output group
    private KualiDecimal correctionDebitTotalAmount; // Debit/Budget amount total in output group
    private KualiDecimal correctionCreditTotalAmount; // Credit amount total in output group
    private String correctionInputFileName; // File name if uploaded
    private String correctionOutputFileName; // Not used
    private String correctionScriptText; // Not used
    private Integer correctionInputGroupId; // Group ID that has input data
    private Integer correctionOutputGroupId; // Group ID that has output data
    private Integer correctionChangeGroupNextLineNumber;

    private List correctionChangeGroup;

    public CorrectionDocument() {
        super();
        correctionChangeGroupNextLineNumber = new Integer(0);

        correctionChangeGroup = new ArrayList();
        addCorrectionChangeGroup(new CorrectionChangeGroup());
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    public String getMethod() {
        if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionTypeCode)) {
            return "Manual Edit";
        }
        else {
            return "Using Criteria";
        }
    }

    public String getSystem() {
        if (correctionInputFileName != null) {
            return "File Upload";
        }
        else {
            return "Database";
        }
    }

    public void addCorrectionChangeGroup(CorrectionChangeGroup ccg) {
        ccg.setDocumentNumber(documentNumber);
        ccg.setCorrectionChangeGroupLineNumber(correctionChangeGroupNextLineNumber++);
        correctionChangeGroup.add(ccg);
    }

    public void removeCorrectionChangeGroup(int changeNumber) {
        for (Iterator iter = correctionChangeGroup.iterator(); iter.hasNext();) {
            CorrectionChangeGroup element = (CorrectionChangeGroup) iter.next();
            if (changeNumber == element.getCorrectionChangeGroupLineNumber().intValue()) {
                iter.remove();
            }
        }
    }

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

        ReportService reportService = SpringServiceLocator.getReportService();
        ScrubberService scrubberService = SpringServiceLocator.getScrubberService();
        CorrectionDocumentService correctionDocumentService = SpringServiceLocator.getCorrectionDocumentService();
        OriginEntryGroupService originEntryGroupService = SpringServiceLocator.getOriginEntryGroupService();

        String docId = getDocumentHeader().getDocumentNumber();
        CorrectionDocument doc = correctionDocumentService.findByCorrectionDocumentHeaderId(docId);

        if (getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
            OriginEntryGroup outputGroup = originEntryGroupService.getExactMatchingEntryGroup(doc.getCorrectionOutputGroupId().intValue());
            if (!doc.getCorrectionFileDelete()) {
                LOG.debug("handleRouteStatusChange() Mark group as to be processed");
                outputGroup.setProcess(true);
                originEntryGroupService.save(outputGroup);
            }
        }

        if (getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            if (doc.getCorrectionOutputGroupId() != null) {
                LOG.debug("handleRouteStatusChange() Run reports");

                OriginEntryGroup outputGroup = originEntryGroupService.getExactMatchingEntryGroup(doc.getCorrectionOutputGroupId().intValue());
                DateTimeService dateTimeService = SpringServiceLocator.getDateTimeService();
                java.sql.Date today = dateTimeService.getCurrentSqlDate();

                reportService.correctionOnlineReport(doc, today);

                // Run the scrubber on this group to generate a bunch of reports. The scrubber won't save anything when running it
                // this way.
                scrubberService.scrubGroupReportOnly(outputGroup, docId);
            }
        }
    }

    /**
     * We need to make sure this is set on all the child objects also
     * 
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

    public List getCorrectionChangeGroup() {
        Collections.sort(correctionChangeGroup);
        return correctionChangeGroup;
    }

    public void setCorrectionChangeGroup(List correctionChangeGroup) {
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
