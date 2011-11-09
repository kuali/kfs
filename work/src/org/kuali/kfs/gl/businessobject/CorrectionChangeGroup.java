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

package org.kuali.kfs.gl.businessobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents a GLCP correction change group
 */
public class CorrectionChangeGroup extends PersistableBusinessObjectBase implements Comparable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionChangeGroup.class);

    private String documentNumber;
    private Integer correctionChangeGroupLineNumber;
    private Integer correctionCriteriaNextLineNumber;
    private Integer correctionChangeNextLineNumber;
    private List<CorrectionCriteria> correctionCriteria;
    private List<CorrectionChange> correctionChange;

    public CorrectionChangeGroup(String documentNumber, Integer correctionChangeGroupLineNumber) {
        setCorrectionChangeGroupLineNumber(correctionChangeGroupLineNumber);

        correctionCriteria = new ArrayList();
        correctionChange = new ArrayList();
        correctionCriteriaNextLineNumber = new Integer(0);
        correctionChangeNextLineNumber = new Integer(0);

        setDocumentNumber(documentNumber);
    }

    public CorrectionChangeGroup() {
        super();
        correctionCriteria = new ArrayList();
        correctionChange = new ArrayList();
        correctionCriteriaNextLineNumber = new Integer(0);
        correctionChangeNextLineNumber = new Integer(0);
    }

    /**
     * Add correction change to this correction change group
     * 
     * @param cc correction change to add
     */
    public void addCorrectionChange(CorrectionChange cc) {
        LOG.debug("addCorrectionChange() started");

        cc.setDocumentNumber(documentNumber);
        cc.setCorrectionChangeGroupLineNumber(correctionChangeGroupLineNumber);
        cc.setCorrectionChangeLineNumber(correctionChangeNextLineNumber++);
        correctionChange.add(cc);
    }

    /**
     * Add correction criteria to this correction change group
     * 
     * @param cc correction criteria to add to this correction change group
     */
    public void addCorrectionCriteria(CorrectionCriteria cc) {
        cc.setDocumentNumber(documentNumber);
        cc.setCorrectionChangeGroupLineNumber(correctionChangeGroupLineNumber);
        cc.setCorrectionCriteriaLineNumber(correctionCriteriaNextLineNumber++);
        correctionCriteria.add(cc);
    }

    /**
     * Remove correction change item
     * 
     * @param changeNumber correction change line number used to determine which correction change item to remove
     */
    public void removeCorrectionChangeItem(int changeNumber) {
        for (Iterator iter = correctionChange.iterator(); iter.hasNext();) {
            CorrectionChange element = (CorrectionChange) iter.next();
            if (changeNumber == element.getCorrectionChangeLineNumber().intValue()) {
                iter.remove();
            }
        }
    }

    /**
     * Remove correction criteria item 
     * 
     * @param criteriaNumber correction criteria line number used to determine which correction change to remove
     */
    public void removeCorrectionCriteriaItem(int criteriaNumber) {
        for (Iterator iter = correctionCriteria.iterator(); iter.hasNext();) {
            CorrectionCriteria element = (CorrectionCriteria) iter.next();
            if (criteriaNumber == element.getCorrectionCriteriaLineNumber().intValue()) {
                iter.remove();
            }
        }
    }

    /**
     * Get correction change item 
     * 
     * @param changeNumber correction change line number of object to return
     * @return CorrectionChange correction change object with specified line number to return
     */
    public CorrectionChange getCorrectionChangeItem(int changeNumber) {
        for (Iterator iter = correctionChange.iterator(); iter.hasNext();) {
            CorrectionChange element = (CorrectionChange) iter.next();
            if (changeNumber == element.getCorrectionChangeLineNumber().intValue()) {
                return element;
            }
        }

        CorrectionChange cc = new CorrectionChange(getDocumentNumber(), correctionChangeGroupLineNumber, changeNumber);
        correctionChange.add(cc);

        return cc;
    }
    
    
    /**
     * Get correction criteria item 
     * 
     * @param criteriaNumber correction change line number of object to return
     * @return CorrectionChange correction change object with specified line number to return
     */
    public CorrectionCriteria getCorrectionCriteriaItem(int criteriaNumber) {
        for (Iterator iter = correctionCriteria.iterator(); iter.hasNext();) {
            CorrectionCriteria element = (CorrectionCriteria) iter.next();
            if (criteriaNumber == element.getCorrectionCriteriaLineNumber().intValue()) {
                return element;
            }
        }

        CorrectionCriteria cc = new CorrectionCriteria(getDocumentNumber(), correctionChangeGroupLineNumber, criteriaNumber);
        correctionCriteria.add(cc);
        return cc;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Set document number for this correction change group.  This also sets the document number for this correction change group's 
     * correction criteria and correction change
     * 
     * @param documentNumber new document number
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;

        for (Iterator iter = correctionCriteria.iterator(); iter.hasNext();) {
            CorrectionCriteria element = (CorrectionCriteria) iter.next();
            element.setDocumentNumber(documentNumber);
        }
        for (Iterator iter = correctionChange.iterator(); iter.hasNext();) {
            CorrectionChange element = (CorrectionChange) iter.next();
            element.setDocumentNumber(documentNumber);
        }
    }

    public Integer getCorrectionChangeGroupLineNumber() {
        return correctionChangeGroupLineNumber;
    }

    public void setCorrectionChangeGroupLineNumber(Integer correctionChangeGroupLineNumber) {
        this.correctionChangeGroupLineNumber = correctionChangeGroupLineNumber;
    }

    public Integer getCorrectionCriteriaNextLineNumber() {
        return correctionCriteriaNextLineNumber;
    }

    public void setCorrectionCriteriaNextLineNumber(Integer correctionCriteriaNextLineNumber) {
        this.correctionCriteriaNextLineNumber = correctionCriteriaNextLineNumber;
    }

    public Integer getCorrectionChangeNextLineNumber() {
        return correctionChangeNextLineNumber;
    }

    public void setCorrectionChangeNextLineNumber(Integer correctionChangeNextLineNumber) {
        this.correctionChangeNextLineNumber = correctionChangeNextLineNumber;
    }

    public List<CorrectionCriteria> getCorrectionCriteria() {
        Collections.sort(correctionCriteria);
        return correctionCriteria;
    }

    public void setCorrectionCriteria(List<CorrectionCriteria> correctionCriteria) {
        this.correctionCriteria = correctionCriteria;
    }

    public List<CorrectionChange> getCorrectionChange() {
        Collections.sort(correctionChange);
        return correctionChange;
    }

    public void setCorrectionChange(List<CorrectionChange> correctionChange) {
        this.correctionChange = correctionChange;
    }

    /**
     * Compares this correction change group to another correction change group object by comparing document number and correction group line number
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        CorrectionChangeGroup other = (CorrectionChangeGroup) o;

        String thisFdocNbr = documentNumber == null ? "" : documentNumber;
        String thatFdocNbr = other.documentNumber == null ? "" : other.documentNumber;

        int c = thisFdocNbr.compareTo(thatFdocNbr);
        if (c == 0) {
            Integer thisNbr = correctionChangeGroupLineNumber == null ? 0 : correctionChangeGroupLineNumber;
            Integer thatNbr = other.correctionChangeGroupLineNumber == null ? 0 : other.correctionChangeGroupLineNumber;
            return thisNbr.compareTo(thatNbr);
        }
        else {
            return c;
        }
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.correctionChangeGroupLineNumber != null) {
            m.put("correctionChangeGroupLineNumber", this.correctionChangeGroupLineNumber.toString());
        }
        return m;
    }
    
    /**
     * A comparator that compares to GLCP correction change groups based on their group line numbers
     * within the GLCP document
     */
    public static class CorrectionGroupLineNumberComparator implements Comparator {

        /**
         * Constructs a CorrectionGroupLineNumberComparator instance
         */
        public CorrectionGroupLineNumberComparator() {
        }

        /**
         * Compares two CorrectionChangeGroups based on thier line numbers within a GLCP document
         *
         * @param c1 a correction change group to compare
         * @param c2 another correction change group to compare the first one to
         * @return a negative integer if c1 has a lower line number than c2, 0 if the two line numbers are equal, a positive number if c1 has a greater line number than c2 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object c1, Object c2) {

            CorrectionChangeGroup ccg1 = (CorrectionChangeGroup) c1;
            CorrectionChangeGroup ccg2 = (CorrectionChangeGroup) c2;

            return ccg1.getCorrectionChangeGroupLineNumber().compareTo(ccg2.getCorrectionChangeGroupLineNumber());
        }

    }
}
