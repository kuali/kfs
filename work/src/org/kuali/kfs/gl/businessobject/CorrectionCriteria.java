/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/businessobject/CorrectionCriteria.java,v $
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

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class CorrectionCriteria extends BusinessObjectBase implements Comparable {

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

    public CorrectionCriteria(String documentNumber,Integer correctionChangeGroupLineNumber,Integer correctionCriteriaLineNumber) {
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

    public int compareTo(Object o) {
        CorrectionCriteria cc = (CorrectionCriteria)o;

        String thisFdocNbr = documentNumber == null ? "" : documentNumber;
        String thatFdocNbr = cc.documentNumber == null ? "" : cc.documentNumber;
        int c = thisFdocNbr.compareTo(thatFdocNbr);

        if ( c == 0 ) {
            Integer thisGn = correctionChangeGroupLineNumber == null ? 0 : correctionChangeGroupLineNumber;
            Integer thatGn = cc.correctionChangeGroupLineNumber == null ? 0 : cc.correctionChangeGroupLineNumber;
            c = thisGn.compareTo(thatGn);
            if( c == 0 ) {
                Integer thisCln = correctionCriteriaLineNumber == null ? 0 : correctionCriteriaLineNumber;
                Integer thatCln = correctionCriteriaLineNumber == null ? 0 : cc.correctionCriteriaLineNumber;
                return c = thisCln.compareTo(thatCln);
            } else {
                return c;
            }
        } else {
            return c;
        }
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.correctionChangeGroupLineNumber != null) {
            m.put("correctionChangeGroupLineNumber", this.correctionChangeGroupLineNumber.toString());
        }
        if (this.correctionCriteriaLineNumber != null) {
            m.put("correctionCriteriaLineNumber", this.correctionCriteriaLineNumber.toString());
        }
        return m;
    }
}
