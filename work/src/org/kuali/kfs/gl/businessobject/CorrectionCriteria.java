/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.gl.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Represents a GLCP criteria
 */
public class CorrectionCriteria extends PersistableBusinessObjectBase implements Comparable {

    private String documentNumber;
    private Integer correctionChangeGroupLineNumber;
    private Integer correctionCriteriaLineNumber;
    private Integer correctionStartPosition;
    private Integer correctionEndPosition;
    private String correctionOperatorCode;
    private String correctionFieldValue;
    private String correctionFieldName;

    private CorrectionChangeGroup correctionChangeGroup;

    public CorrectionCriteria() {
        super();

    }

    public CorrectionCriteria(String documentNumber, Integer correctionChangeGroupLineNumber, Integer correctionCriteriaLineNumber) {
        this.documentNumber = documentNumber;
        this.correctionChangeGroupLineNumber = correctionChangeGroupLineNumber;
        this.correctionCriteriaLineNumber = correctionCriteriaLineNumber;
    }

    public boolean isEmpty() {
        return (versionNumber == null) && StringUtils.isEmpty(correctionFieldValue);
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getCorrectionChangeGroupLineNumber() {
        return correctionChangeGroupLineNumber;
    }

    public void setCorrectionChangeGroupLineNumber(Integer correctionChangeGroupLineNumber) {
        this.correctionChangeGroupLineNumber = correctionChangeGroupLineNumber;
    }

    public Integer getCorrectionCriteriaLineNumber() {
        return correctionCriteriaLineNumber;
    }

    public void setCorrectionCriteriaLineNumber(Integer correctionCriteriaLineNumber) {
        this.correctionCriteriaLineNumber = correctionCriteriaLineNumber;
    }

    public String getCorrectionOperatorCode() {
        return correctionOperatorCode;
    }

    public void setCorrectionOperatorCode(String correctionOperatorCode) {
        this.correctionOperatorCode = correctionOperatorCode;
    }

    public String getCorrectionFieldValue() {
        return correctionFieldValue;
    }

    public void setCorrectionFieldValue(String correctionFieldValue) {
        this.correctionFieldValue = correctionFieldValue;
    }

    public CorrectionChangeGroup getCorrectionChangeGroup() {
        return correctionChangeGroup;
    }

    public void setCorrectionChangeGroup(CorrectionChangeGroup correctionChangeGroup) {
        this.correctionChangeGroup = correctionChangeGroup;
    }

    public String getCorrectionFieldName() {
        return correctionFieldName;
    }

    public void setCorrectionFieldName(String correctionFieldName) {
        this.correctionFieldName = correctionFieldName;
    }

    /**
     * Compares this object with another CorrectionCriteria based on document number, 
     * correction change group line number, and correction criteria line number
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        CorrectionCriteria cc = (CorrectionCriteria) o;

        String thisFdocNbr = documentNumber == null ? "" : documentNumber;
        String thatFdocNbr = cc.documentNumber == null ? "" : cc.documentNumber;
        int c = thisFdocNbr.compareTo(thatFdocNbr);

        if (c == 0) {
            Integer thisGn = correctionChangeGroupLineNumber == null ? 0 : correctionChangeGroupLineNumber;
            Integer thatGn = cc.correctionChangeGroupLineNumber == null ? 0 : cc.correctionChangeGroupLineNumber;
            c = thisGn.compareTo(thatGn);
            if (c == 0) {
                Integer thisCln = correctionCriteriaLineNumber == null ? 0 : correctionCriteriaLineNumber;
                Integer thatCln = correctionCriteriaLineNumber == null ? 0 : cc.correctionCriteriaLineNumber;
                return c = thisCln.compareTo(thatCln);
            }
            else {
                return c;
            }
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
        if (this.correctionCriteriaLineNumber != null) {
            m.put("correctionCriteriaLineNumber", this.correctionCriteriaLineNumber.toString());
        }
        return m;
    }
}
