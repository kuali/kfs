/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.gl.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.document.DocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryGroupService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CorrectionDocument extends DocumentBase {

    // private String financialDocumentNumber;
    
    private String correctionTypeCode; // Manual or Criteria?
    private boolean correctionSelectionCode; // File or System?
    private boolean correctionFileDeleteCode; // Delete Output?
    private Integer correctionRowCount;
    private KualiDecimal correctionDebitTotalAmount;
    private KualiDecimal correctionCreditTotalAmount;
    private String correctionInputFileName; //Should be integer?
    private String correctionOutputFileName; //Should be integer?
    private String correctionScriptText;

    // private DocumentHeader financialDocument;

    /**
     * Each CorrectionGroup is indexed within the Document. correctionChangeGroupNextLineNumber keeps track of what the next index
     * should be. It functions as a memory-resident counter. An alternative approach would have been to use a database sequence.
     */
    private Integer correctionChangeGroupNextLineNumber;

    /**
     * A List of CorrectionGroup Objects.
     */
    private List correctionChangeGroup;

    /**
     * All OriginEntries corrected by this document are owned by OriginEntryGroups with this OriginEntrySource.
     */
    private OriginEntryGroup originEntryGroup;
    private String originEntryGroupId;

    /**
     * Default constructor.
     */
    public CorrectionDocument() {
        super();
        correctionChangeGroupNextLineNumber = new Integer(0);

        correctionChangeGroup = new ArrayList();

        // create a default correction group
        addCorrectionGroup(new CorrectionChangeGroup());


    }

    /**
     * Gets the financialDocumentNumber attribute.
     * 
     * @return - Returns the financialDocumentNumber
     * 
     */
    // public String getFinancialDocumentNumber() {
    // return financialDocumentNumber;
    // }
    /**
     * Sets the financialDocumentNumber attribute.
     * 
     * @param financialDocumentNumber The financialDocumentNumber to set.
     * 
     */
    // public void setFinancialDocumentNumber(String financialDocumentNumber) {
    // this.financialDocumentNumber = financialDocumentNumber;
    // }

    /**
     * Gets the correctionTypeCode attribute.
     * 
     * @return - Returns the correctionTypeCode
     * 
     */
    public String getCorrectionTypeCode() {
        return correctionTypeCode;
    }

    /**
     * Sets the correctionTypeCode attribute.
     * 
     * @param correctionTypeCode The correctionTypeCode to set.
     * 
     */
    public void setCorrectionTypeCode(String correctionTypeCode) {
        this.correctionTypeCode = correctionTypeCode;
    }


    /**
     * Gets the correctionSelectionCode attribute.
     * 
     * @return - Returns the correctionSelectionCode
     * 
     */
    public boolean getCorrectionSelectionCode() {
        return correctionSelectionCode;
    }

    /**
     * Sets the correctionSelectionCode attribute.
     * 
     * @param correctionSelectionCode The correctionSelectionCode to set.
     * 
     */
    public void setCorrectionSelectionCode(boolean correctionSelectionCode) {
        this.correctionSelectionCode = correctionSelectionCode;
    }


    /**
     * Gets the correctionFileDeleteCode attribute.
     * 
     * @return - Returns the correctionFileDeleteCode
     * 
     */
    public boolean getCorrectionFileDeleteCode() {
        return correctionFileDeleteCode;
    }

    /**
     * Sets the correctionFileDeleteCode attribute.
     * 
     * @param correctionFileDeleteCode The correctionFileDeleteCode to set.
     * 
     */
    public void setCorrectionFileDeleteCode(boolean correctionFileDeleteCode) {
        this.correctionFileDeleteCode = correctionFileDeleteCode;
    }


    /**
     * Gets the correctionRowCount attribute.
     * 
     * @return - Returns the correctionRowCount
     * 
     */
    public Integer getCorrectionRowCount() {
        return correctionRowCount;
    }

    /**
     * Sets the correctionRowCount attribute.
     * 
     * @param correctionRowCount The correctionRowCount to set.
     * 
     */
    public void setCorrectionRowCount(Integer correctionRowCount) {
        this.correctionRowCount = correctionRowCount;
    }


    /**
     * Gets the correctionChangeGroupNextLineNumber attribute.
     * 
     * @return - Returns the correctionChangeGroupNextLineNumber
     * 
     */
    public Integer getCorrectionChangeGroupNextLineNumber() {
        return correctionChangeGroupNextLineNumber;
    }

    /**
     * Sets the correctionChangeGroupNextLineNumber attribute.
     * 
     * @param correctionChangeGroupNextLineNumber The correctionChangeGroupNextLineNumber to set.
     * 
     */
    public void setCorrectionChangeGroupNextLineNumber(Integer correctionChangeGroupNextLineNumber) {
        this.correctionChangeGroupNextLineNumber = correctionChangeGroupNextLineNumber;
    }


    /**
     * Gets the correctionDebitTotalAmount attribute.
     * 
     * @return - Returns the correctionDebitTotalAmount
     * 
     */
    public KualiDecimal getCorrectionDebitTotalAmount() {
        return correctionDebitTotalAmount;
    }

    /**
     * Sets the correctionDebitTotalAmount attribute.
     * 
     * @param correctionDebitTotalAmount The correctionDebitTotalAmount to set.
     * 
     */
    public void setCorrectionDebitTotalAmount(KualiDecimal correctionDebitTotalAmount) {
        this.correctionDebitTotalAmount = correctionDebitTotalAmount;
    }


    /**
     * Gets the correctionCreditTotalAmount attribute.
     * 
     * @return - Returns the correctionCreditTotalAmount
     * 
     */
    public KualiDecimal getCorrectionCreditTotalAmount() {
        return correctionCreditTotalAmount;
    }

    /**
     * Sets the correctionCreditTotalAmount attribute.
     * 
     * @param correctionCreditTotalAmount The correctionCreditTotalAmount to set.
     * 
     */
    public void setCorrectionCreditTotalAmount(KualiDecimal correctionCreditTotalAmount) {
        this.correctionCreditTotalAmount = correctionCreditTotalAmount;
    }


    /**
     * Gets the correctionInputFileName attribute.
     * 
     * @return - Returns the correctionInputFileName
     * 
     */
    public String getCorrectionInputFileName() {
        return correctionInputFileName;
    }

    /**
     * Sets the correctionInputFileName attribute.
     * 
     * @param correctionInputFileName The correctionInputFileName to set.
     * 
     */
    public void setCorrectionInputFileName(String correctionInputFileName) {
        this.correctionInputFileName = correctionInputFileName;
    }


    /**
     * Gets the correctionOutputFileName attribute.
     * 
     * @return - Returns the correctionOutputFileName
     * 
     */
    public String getCorrectionOutputFileName() {
        return correctionOutputFileName;
    }

    /**
     * Sets the correctionOutputFileName attribute.
     * 
     * @param correctionOutputFileName The correctionOutputFileName to set.
     * 
     */
    public void setCorrectionOutputFileName(String correctionOutputFileName) {
        this.correctionOutputFileName = correctionOutputFileName;
    }


    /**
     * Gets the correctionScriptText attribute.
     * 
     * @return - Returns the correctionScriptText
     * 
     */
    public String getCorrectionScriptText() {
        return correctionScriptText;
    }

    /**
     * Sets the correctionScriptText attribute.
     * 
     * @param correctionScriptText The correctionScriptText to set.
     * 
     */
    public void setCorrectionScriptText(String correctionScriptText) {
        this.correctionScriptText = correctionScriptText;
    }


    /**
     * Gets the financialDocument attribute.
     * 
     * @return - Returns the financialDocument
     * 
     */
    // public DocumentHeader getFinancialDocument() {
    // return financialDocument;
    // }
    /**
     * Sets the financialDocument attribute.
     * 
     * @param financialDocument The financialDocument to set.
     * @deprecated
     */
    // public void setFinancialDocument(DocumentHeader financialDocument) {
    // this.financialDocument = financialDocument;
    // }
    /**
     * Gets the correctionChangeGroup list.
     * 
     * @return - Returns the correctionChangeGroup list
     * 
     */
    public List getCorrectionChangeGroup() {
        return correctionChangeGroup;
    }

    /**
     * Sets the correctionChangeGroup list.
     * 
     * @param correctionChangeGroup The correctionChangeGroup list to set.
     * 
     */
    public void setCorrectionChangeGroup(List correctionChangeGroup) {
        this.correctionChangeGroup = correctionChangeGroup;
    }

    /**
     * 
     * @param groupNumber
     * @return the CorrectionGroup with the given groupNumber
     */
    public CorrectionChangeGroup getCorrectionGroup(Integer groupNumber) {
        CorrectionChangeGroup selected = null;
        for (Iterator i = correctionChangeGroup.iterator(); i.hasNext();) {
            CorrectionChangeGroup group = (CorrectionChangeGroup) i.next();
            if (groupNumber.equals(group.getCorrectionChangeGroupLineNumber())) {
                selected = group;
                break;
            }
        }
        return selected;
    }

    /**
     * Assign the correct group number and add the group to the list of groups.
     * 
     * @param group
     */
    public void addCorrectionGroup(CorrectionChangeGroup group) {
        group.setFinancialDocumentNumber(getFinancialDocumentNumber());

        Integer currentNextGroupNumber = getCorrectionChangeGroupNextLineNumber();
        group.setCorrectionChangeGroupLineNumber(currentNextGroupNumber);
        group.setFinancialDocumentNumber(getFinancialDocumentNumber());

        setCorrectionChangeGroupNextLineNumber(new Integer(currentNextGroupNumber.intValue() + 1));
        correctionChangeGroup.add(group);
    }

    /**
     * Remove the group with the given groupNumber.
     * 
     * @param groupNumber
     */
    public void removeCorrectionGroup(Integer groupNumber) {
        for (Iterator i = correctionChangeGroup.iterator(); i.hasNext();) {
            CorrectionChangeGroup group = (CorrectionChangeGroup) i.next();
            if (groupNumber.equals(group.getCorrectionChangeGroupLineNumber())) {
                group.setCorrectionChangeGroupLineNumber(null);
                correctionChangeGroup.remove(group);
                break;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.document.Document#getDocumentTitle()
     */
    public String getDocumentTitle() {
        return super.getDocumentTitle();
    }

    /**
     * Gets the originEntryGroup attribute.
     * 
     * @return Returns the originEntryGroup.
     */
    public OriginEntryGroup getOriginEntryGroup() {
        return originEntryGroup;
    }

    /**
     * Sets the originEntryGroup attribute value.
     * 
     * @param originEntryGroup The originEntryGroup to set.
     */
    public void setOriginEntryGroup(OriginEntryGroup originEntryGroup) {
        this.originEntryGroup = originEntryGroup;
    }

    /**
     * Gets the originEntryGroupId attribute.
     * 
     * @return Returns the originEntryGroupId.
     */
    public String getOriginEntryGroupId() {
        return originEntryGroupId;
    }

    /**
     * Sets the originEntryGroupId attribute value.
     * 
     * @param originEntryGroupId The originEntryGroupId to set.
     */
    public void setOriginEntryGroupId(String originEntryGroupId) {
        this.originEntryGroupId = originEntryGroupId;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }

    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        if (getDocumentHeader().getWorkflowDocument().stateIsApproved()) {
            String docId = getDocumentHeader().getFinancialDocumentNumber();
            CorrectionDocumentService correctionDocumentService = (CorrectionDocumentService) SpringServiceLocator.getBeanFactory().getBean("glCorrectionDocumentService");
            CorrectionDocument oldDoc = correctionDocumentService.findByCorrectionDocumentHeaderId(docId);
            String groupId = oldDoc.getCorrectionOutputFileName();
            
            OriginEntryGroupService originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
            OriginEntryGroup approvedGLCP = originEntryGroupService.getExactMatchingEntryGroup(Integer.parseInt(groupId));
            approvedGLCP.setScrub(true);
            originEntryGroupService.save(approvedGLCP);
            
        }
        
        
    }
    
    
}
