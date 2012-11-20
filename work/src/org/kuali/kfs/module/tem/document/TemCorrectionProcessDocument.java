/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AgencyCorrectionChangeGroup;
import org.kuali.kfs.module.tem.document.service.TemCorrectionDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class TemCorrectionProcessDocument extends FinancialSystemTransactionalDocumentBase implements AmountTotaling {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TemCorrectionProcessDocument.class);

    protected String correctionTypeCode; // CorrectionDocumentService.CORRECTION_TYPE_MANUAL or
    protected boolean correctionSelection; // false if all input rows should be in the output, true if only selected rows should be
    // in the output
    protected boolean correctionFileDelete; // false if the file should be processed by scrubber, true if the file should not be
    // processed by agency upload process
    protected Integer correctionRowCount; // Row count in output group
    protected String correctionInputFileName; // input file name
    protected String correctionOutputFileName; // output file name
    protected String correctionScriptText; // Not used
    protected Integer correctionChangeGroupNextLineNumber;

    protected List<AgencyCorrectionChangeGroup> correctionChangeGroup;
    protected KualiDecimal correctionTripTotalAmount = KualiDecimal.ZERO;

    
    public TemCorrectionProcessDocument() {
        super();
        correctionChangeGroupNextLineNumber = new Integer(0);

        correctionChangeGroup = new ArrayList<AgencyCorrectionChangeGroup>();
    }
    
    /**
     * Returns the editing method to use on the origin entries in the document, either "Manual Edit," "Using Criteria," "Remove
     * Group from Processing," or "Not Available"
     * 
     * @return the String representation of the method this document is using
     */
    public String getMethod() {
        if (TemCorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionTypeCode)) {
            return "Manual Edit";
        }
        else if (TemCorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionTypeCode)) {
            return "Using Criteria";
        }
        else if (TemCorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionTypeCode)) {
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
        return "Database";
    }
    
    /**
     * This method...
     * 
     * @param ccg
     */
    public void addCorrectionChangeGroup(AgencyCorrectionChangeGroup ccg) {
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
            AgencyCorrectionChangeGroup element = (AgencyCorrectionChangeGroup) iter.next();
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
    public AgencyCorrectionChangeGroup getCorrectionChangeGroupItem(int groupNumber) {
        for (Iterator iter = correctionChangeGroup.iterator(); iter.hasNext();) {
            AgencyCorrectionChangeGroup element = (AgencyCorrectionChangeGroup) iter.next();
            if (groupNumber == element.getCorrectionChangeGroupLineNumber().intValue()) {
                return element;
            }
        }

        AgencyCorrectionChangeGroup ccg = new AgencyCorrectionChangeGroup(documentNumber, groupNumber);
        correctionChangeGroup.add(ccg);

        return ccg;
    }

    @Override
    public KualiDecimal getTotalDollarAmount() {
          return correctionTripTotalAmount;
    }

    /**
     * Gets the correctionTypeCode attribute. 
     * @return Returns the correctionTypeCode.
     */
    public String getCorrectionTypeCode() {
        return correctionTypeCode;
    }

    /**
     * Sets the correctionTypeCode attribute value.
     * @param correctionTypeCode The correctionTypeCode to set.
     */
    public void setCorrectionTypeCode(String correctionTypeCode) {
        this.correctionTypeCode = correctionTypeCode;
    }

    /**
     * Gets the correctionSelection attribute. 
     * @return Returns the correctionSelection.
     */
    public boolean isCorrectionSelection() {
        return correctionSelection;
    }

    /**
     * Sets the correctionSelection attribute value.
     * @param correctionSelection The correctionSelection to set.
     */
    public void setCorrectionSelection(boolean correctionSelection) {
        this.correctionSelection = correctionSelection;
    }

    /**
     * Gets the correctionFileDelete attribute. 
     * @return Returns the correctionFileDelete.
     */
    public boolean isCorrectionFileDelete() {
        return correctionFileDelete;
    }

    /**
     * Sets the correctionFileDelete attribute value.
     * @param correctionFileDelete The correctionFileDelete to set.
     */
    public void setCorrectionFileDelete(boolean correctionFileDelete) {
        this.correctionFileDelete = correctionFileDelete;
    }

    /**
     * Gets the correctionRowCount attribute. 
     * @return Returns the correctionRowCount.
     */
    public Integer getCorrectionRowCount() {
        return correctionRowCount;
    }

    /**
     * Sets the correctionRowCount attribute value.
     * @param correctionRowCount The correctionRowCount to set.
     */
    public void setCorrectionRowCount(Integer correctionRowCount) {
        this.correctionRowCount = correctionRowCount;
    }

    /**
     * Gets the correctionInputFileName attribute. 
     * @return Returns the correctionInputFileName.
     */
    public String getCorrectionInputFileName() {
        return correctionInputFileName;
    }

    /**
     * Sets the correctionInputFileName attribute value.
     * @param correctionInputFileName The correctionInputFileName to set.
     */
    public void setCorrectionInputFileName(String correctionInputFileName) {
        this.correctionInputFileName = correctionInputFileName;
    }

    /**
     * Gets the correctionOutputFileName attribute. 
     * @return Returns the correctionOutputFileName.
     */
    public String getCorrectionOutputFileName() {
        return correctionOutputFileName;
    }

    /**
     * Sets the correctionOutputFileName attribute value.
     * @param correctionOutputFileName The correctionOutputFileName to set.
     */
    public void setCorrectionOutputFileName(String correctionOutputFileName) {
        this.correctionOutputFileName = correctionOutputFileName;
    }

    /**
     * Gets the correctionScriptText attribute. 
     * @return Returns the correctionScriptText.
     */
    public String getCorrectionScriptText() {
        return correctionScriptText;
    }

    /**
     * Sets the correctionScriptText attribute value.
     * @param correctionScriptText The correctionScriptText to set.
     */
    public void setCorrectionScriptText(String correctionScriptText) {
        this.correctionScriptText = correctionScriptText;
    }

    /**
     * Gets the correctionChangeGroupNextLineNumber attribute. 
     * @return Returns the correctionChangeGroupNextLineNumber.
     */
    public Integer getCorrectionChangeGroupNextLineNumber() {
        return correctionChangeGroupNextLineNumber;
    }

    /**
     * Sets the correctionChangeGroupNextLineNumber attribute value.
     * @param correctionChangeGroupNextLineNumber The correctionChangeGroupNextLineNumber to set.
     */
    public void setCorrectionChangeGroupNextLineNumber(Integer correctionChangeGroupNextLineNumber) {
        this.correctionChangeGroupNextLineNumber = correctionChangeGroupNextLineNumber;
    }

    /**
     * Gets the correctionTripTotalAmount attribute. 
     * @return Returns the correctionTripTotalAmount.
     */
    public KualiDecimal getCorrectionTripTotalAmount() {
        return correctionTripTotalAmount;
    }

    /**
     * Sets the correctionTripTotalAmount attribute value.
     * @param correctionTripTotalAmount The correctionTripTotalAmount to set.
     */
    public void setCorrectionTripTotalAmount(KualiDecimal correctionTripTotalAmount) {
        if(ObjectUtils.isNull(correctionTripTotalAmount)) {
            this.correctionTripTotalAmount = KualiDecimal.ZERO;
        } else {
            this.correctionTripTotalAmount = correctionTripTotalAmount;
        }
        
    }
    
    public List<AgencyCorrectionChangeGroup> getCorrectionChangeGroup() {
        Collections.sort(correctionChangeGroup);
        return correctionChangeGroup;
    }

    public void setCorrectionChangeGroup(List<AgencyCorrectionChangeGroup> correctionChangeGroup) {
        this.correctionChangeGroup = correctionChangeGroup;
    }
    

}
