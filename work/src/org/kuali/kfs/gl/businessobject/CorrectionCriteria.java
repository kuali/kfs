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

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CorrectionCriteria extends BusinessObjectBase implements Comparable {

    private String financialDocumentNumber;
    private Integer correctionChangeGroupLineNumber;
    private Integer correctionCriteriaLineNumber;
    private Integer correctionStartPosition;
    private Integer correctionEndPosition;
    private String correctionOperatorCode;
    private String correctionFieldValue;

    private String operator;
    private String correctionFieldName;

    private CorrectionChangeGroup correctionChangeGroup;

    /**
     * Default constructor.
     */
    public CorrectionCriteria() {
        super();

    }

    /**
     * Gets the financialDocumentNumber attribute.
     * 
     * @return - Returns the financialDocumentNumber
     * 
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    /**
     * Sets the financialDocumentNumber attribute.
     * 
     * @param financialDocumentNumber The financialDocumentNumber to set.
     * 
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }


    /**
     * Gets the correctionChangeGroupLineNumber attribute.
     * 
     * @return - Returns the correctionChangeGroupLineNumber
     * 
     */
    public Integer getCorrectionChangeGroupLineNumber() {
        return correctionChangeGroupLineNumber;
    }

    /**
     * Sets the correctionChangeGroupLineNumber attribute.
     * 
     * @param correctionChangeGroupLineNumber The correctionChangeGroupLineNumber to set.
     * 
     */
    public void setCorrectionChangeGroupLineNumber(Integer correctionChangeGroupLineNumber) {
        this.correctionChangeGroupLineNumber = correctionChangeGroupLineNumber;
    }


    /**
     * Gets the correctionCriteriaLineNumber attribute.
     * 
     * @return - Returns the correctionCriteriaLineNumber
     * 
     */
    public Integer getCorrectionCriteriaLineNumber() {
        return correctionCriteriaLineNumber;
    }

    /**
     * Sets the correctionCriteriaLineNumber attribute.
     * 
     * @param correctionCriteriaLineNumber The correctionCriteriaLineNumber to set.
     * 
     */
    public void setCorrectionCriteriaLineNumber(Integer correctionCriteriaLineNumber) {
        this.correctionCriteriaLineNumber = correctionCriteriaLineNumber;
    }


    /**
     * Gets the correctionStartPosition attribute.
     * 
     * @return - Returns the correctionStartPosition
     * 
     */
    public Integer getCorrectionStartPosition() {
        return correctionStartPosition;
    }

    /**
     * Sets the correctionStartPosition attribute.
     * 
     * @param correctionStartPosition The correctionStartPosition to set.
     * 
     */
    public void setCorrectionStartPosition(Integer correctionStartPosition) {
        this.correctionStartPosition = correctionStartPosition;
    }


    /**
     * Gets the correctionEndPosition attribute.
     * 
     * @return - Returns the correctionEndPosition
     * 
     */
    public Integer getCorrectionEndPosition() {
        return correctionEndPosition;
    }

    /**
     * Sets the correctionEndPosition attribute.
     * 
     * @param correctionEndPosition The correctionEndPosition to set.
     * 
     */
    public void setCorrectionEndPosition(Integer correctionEndPosition) {
        this.correctionEndPosition = correctionEndPosition;
    }


    /**
     * Gets the correctionOperatorCode attribute.
     * 
     * @return - Returns the correctionOperatorCode
     * 
     */
    public String getCorrectionOperatorCode() {
        return correctionOperatorCode;
    }

    /**
     * Sets the correctionOperatorCode attribute.
     * 
     * @param correctionOperatorCode The correctionOperatorCode to set.
     * 
     */
    public void setCorrectionOperatorCode(String correctionOperatorCode) {
        this.correctionOperatorCode = correctionOperatorCode;
    }


    /**
     * Gets the correctionFieldValue attribute.
     * 
     * @return - Returns the correctionFieldValue
     * 
     */
    public String getCorrectionFieldValue() {
        return correctionFieldValue;
    }

    /**
     * Sets the correctionFieldValue attribute.
     * 
     * @param correctionFieldValue The correctionFieldValue to set.
     * 
     */
    public void setCorrectionFieldValue(String correctionFieldValue) {
        this.correctionFieldValue = correctionFieldValue;
    }


    /**
     * Gets the correctionChangeGroup attribute.
     * 
     * @return - Returns the correctionChangeGroup
     * 
     */
    public CorrectionChangeGroup getCorrectionChangeGroup() {
        return correctionChangeGroup;
    }

    /**
     * Sets the correctionChangeGroup attribute.
     * 
     * @param correctionChangeGroup The correctionChangeGroup to set.
     * @deprecated
     */
    public void setCorrectionChangeGroup(CorrectionChangeGroup correctionChangeGroup) {
        this.correctionChangeGroup = correctionChangeGroup;
    }

    /**
     * Gets the correctionFieldName attribute.
     * 
     * @return Returns the correctionFieldName.
     */
    public String getCorrectionFieldName() {
        return correctionFieldName;
    }

    /**
     * Sets the correctionFieldName attribute value.
     * 
     * @param correctionFieldName The correctionFieldName to set.
     */
    public void setCorrectionFieldName(String correctionFieldName) {
        this.correctionFieldName = correctionFieldName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        CorrectionCriteria criterion = (CorrectionCriteria) o;

        int c = correctionCriteriaLineNumber.compareTo(criterion.getCorrectionCriteriaLineNumber());
        if (0 != c) {
            return c;
        }

        c = correctionChangeGroupLineNumber.compareTo(criterion.getCorrectionChangeGroupLineNumber());
        if (0 != c) {
            return c;
        }

        c = financialDocumentNumber.compareTo(criterion.getFinancialDocumentNumber());

        return c;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof CorrectionCriteria) {
            return 0 == compareTo(obj);
        }

        return super.equals(obj);
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
        if (this.correctionCriteriaLineNumber != null) {
            m.put("correctionCriteriaLineNumber", this.correctionCriteriaLineNumber.toString());
        }
        return m;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

}
