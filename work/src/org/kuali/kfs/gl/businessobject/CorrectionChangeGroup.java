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

package org.kuali.module.gl.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * 
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

    public void addCorrectionChange(CorrectionChange cc) {
        LOG.debug("addCorrectionChange() started");

        cc.setDocumentNumber(documentNumber);
        cc.setCorrectionChangeGroupLineNumber(correctionChangeGroupLineNumber);
        cc.setCorrectionChangeLineNumber(correctionChangeNextLineNumber++);
        correctionChange.add(cc);
    }

    public void addCorrectionCriteria(CorrectionCriteria cc) {
        cc.setDocumentNumber(documentNumber);
        cc.setCorrectionChangeGroupLineNumber(correctionChangeGroupLineNumber);
        cc.setCorrectionCriteriaLineNumber(correctionCriteriaNextLineNumber++);
        correctionCriteria.add(cc);
    }

    public void removeCorrectionChangeItem(int changeNumber) {
        for (Iterator iter = correctionChange.iterator(); iter.hasNext();) {
            CorrectionChange element = (CorrectionChange) iter.next();
            if (changeNumber == element.getCorrectionChangeLineNumber().intValue()) {
                iter.remove();
            }
        }
    }

    public void removeCorrectionCriteriaItem(int criteriaNumber) {
        for (Iterator iter = correctionCriteria.iterator(); iter.hasNext();) {
            CorrectionCriteria element = (CorrectionCriteria) iter.next();
            if (criteriaNumber == element.getCorrectionCriteriaLineNumber().intValue()) {
                iter.remove();
            }
        }
    }

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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.correctionChangeGroupLineNumber != null) {
            m.put("correctionChangeGroupLineNumber", this.correctionChangeGroupLineNumber.toString());
        }
        return m;
    }
}
