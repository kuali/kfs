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

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * A class that represents a change to any origin entry that was altered within a GLCP Document
 */
public class CorrectionChange extends PersistableBusinessObjectBase implements Comparable {

    private String documentNumber;
    private Integer correctionChangeGroupLineNumber;
    private Integer correctionChangeLineNumber;
    private Integer correctionStartPosition;
    private Integer correctionEndPosition;
    private String correctionFieldValue;
    private String correctionFieldName;

    public CorrectionChange() {
        super();

    }

    public CorrectionChange(String documentNumber, Integer correctionChangeGroupLineNumber, Integer correctionChangeLineNumber) {
        super();
        this.documentNumber = documentNumber;
        this.correctionChangeGroupLineNumber = correctionChangeGroupLineNumber;
        this.correctionChangeLineNumber = correctionChangeLineNumber;
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

    public Integer getCorrectionChangeLineNumber() {
        return correctionChangeLineNumber;
    }

    public void setCorrectionChangeLineNumber(Integer correctionChangeLineNumber) {
        this.correctionChangeLineNumber = correctionChangeLineNumber;
    }

    public String getCorrectionFieldValue() {
        return correctionFieldValue;
    }

    public void setCorrectionFieldValue(String correctionFieldValue) {
        this.correctionFieldValue = correctionFieldValue;
    }

    public String getCorrectionFieldName() {
        return correctionFieldName;
    }

    public void setCorrectionFieldName(String correctionFieldName) {
        this.correctionFieldName = correctionFieldName;
    }

    public int compareTo(Object o) {
        CorrectionChange cc = (CorrectionChange) o;

        String thisFdocNbr = documentNumber == null ? "" : documentNumber;
        String thatFdocNbr = cc.documentNumber == null ? "" : cc.documentNumber;
        int c = thisFdocNbr.compareTo(thatFdocNbr);

        if (c == 0) {
            Integer thisGn = correctionChangeGroupLineNumber == null ? 0 : correctionChangeGroupLineNumber;
            Integer thatGn = cc.correctionChangeGroupLineNumber == null ? 0 : cc.correctionChangeGroupLineNumber;
            c = thisGn.compareTo(thatGn);
            if (c == 0) {
                Integer thisCln = correctionChangeLineNumber == null ? 0 : correctionChangeLineNumber;
                Integer thatCln = correctionChangeLineNumber == null ? 0 : cc.correctionChangeLineNumber;
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
        if (this.correctionChangeLineNumber != null) {
            m.put("correctionChangeLineNumber", this.correctionChangeLineNumber.toString());
        }
        return m;
    }

}
