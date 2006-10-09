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

package org.kuali.module.gl.bo;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class CorrectionChange extends BusinessObjectBase implements Comparable {

    private String financialDocumentNumber;
    private Integer correctionChangeGroupLineNumber;
    private Integer correctionChangeLineNumber;
    private Integer correctionStartPosition;
    private Integer correctionEndPosition;
    private String correctionFieldValue;
    private String correctionFieldName;

    public CorrectionChange() {
        super();

    }

    public CorrectionChange(String financialDocumentNumber,Integer correctionChangeGroupLineNumber,Integer correctionChangeLineNumber) {
        super();
        this.financialDocumentNumber = financialDocumentNumber;
        this.correctionChangeGroupLineNumber = correctionChangeGroupLineNumber;
        this.correctionChangeLineNumber = correctionChangeLineNumber;
    }

    public boolean isEmpty() {
        return (versionNumber == null) && StringUtils.isEmpty(correctionFieldValue);
    }

    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
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
        CorrectionChange cc = (CorrectionChange)o;

        String thisFdocNbr = financialDocumentNumber == null ? "" : financialDocumentNumber;
        String thatFdocNbr = cc.financialDocumentNumber == null ? "" : cc.financialDocumentNumber;
        int c = thisFdocNbr.compareTo(thatFdocNbr);

        if ( c == 0 ) {
            Integer thisGn = correctionChangeGroupLineNumber == null ? 0 : correctionChangeGroupLineNumber;
            Integer thatGn = cc.correctionChangeGroupLineNumber == null ? 0 : cc.correctionChangeGroupLineNumber;
            c = thisGn.compareTo(thatGn);
            if( c == 0 ) {
                Integer thisCln = correctionChangeLineNumber == null ? 0 : correctionChangeLineNumber;
                Integer thatCln = correctionChangeLineNumber == null ? 0 : cc.correctionChangeLineNumber;
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
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        if (this.correctionChangeGroupLineNumber != null) {
            m.put("correctionChangeGroupLineNumber", this.correctionChangeGroupLineNumber.toString());
        }
        if (this.correctionChangeLineNumber != null) {
            m.put("correctionChangeLineNumber", this.correctionChangeLineNumber.toString());
        }
        return m;
    }

}
