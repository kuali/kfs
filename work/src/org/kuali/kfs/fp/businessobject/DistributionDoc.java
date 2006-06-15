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

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.document.DocumentHeader;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DistributionDoc extends BusinessObjectBase {

    private String financialDocumentNumber;
    private Integer finDocumentNextFromLineNbr;
    private Integer finDocumentNextToLineNumber;
    private Integer financialDocumentPostingYear;
    private String finDocumentPostingPeriodCode;
    private String finDocumentExplanationText;
    private DocumentHeader financialDocument;

    /**
     * Default no-arg constructor.
     */
    public DistributionDoc() {

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
     * Gets the finDocumentNextFromLineNbr attribute.
     * 
     * @return - Returns the finDocumentNextFromLineNbr
     * 
     */
    public Integer getFinDocumentNextFromLineNbr() {
        return finDocumentNextFromLineNbr;
    }


    /**
     * Sets the finDocumentNextFromLineNbr attribute.
     * 
     * @param finDocumentNextFromLineNbr The finDocumentNextFromLineNbr to set.
     * 
     */
    public void setFinDocumentNextFromLineNbr(Integer finDocumentNextFromLineNbr) {
        this.finDocumentNextFromLineNbr = finDocumentNextFromLineNbr;
    }

    /**
     * Gets the finDocumentNextToLineNumber attribute.
     * 
     * @return - Returns the finDocumentNextToLineNumber
     * 
     */
    public Integer getFinDocumentNextToLineNumber() {
        return finDocumentNextToLineNumber;
    }


    /**
     * Sets the finDocumentNextToLineNumber attribute.
     * 
     * @param finDocumentNextToLineNumber The finDocumentNextToLineNumber to set.
     * 
     */
    public void setFinDocumentNextToLineNumber(Integer finDocumentNextToLineNumber) {
        this.finDocumentNextToLineNumber = finDocumentNextToLineNumber;
    }

    /**
     * Gets the financialDocumentPostingYear attribute.
     * 
     * @return - Returns the financialDocumentPostingYear
     * 
     */
    public Integer getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }


    /**
     * Sets the financialDocumentPostingYear attribute.
     * 
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     * 
     */
    public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
    }

    /**
     * Gets the finDocumentPostingPeriodCode attribute.
     * 
     * @return - Returns the finDocumentPostingPeriodCode
     * 
     */
    public String getFinDocumentPostingPeriodCode() {
        return finDocumentPostingPeriodCode;
    }


    /**
     * Sets the finDocumentPostingPeriodCode attribute.
     * 
     * @param finDocumentPostingPeriodCode The finDocumentPostingPeriodCode to set.
     * 
     */
    public void setFinDocumentPostingPeriodCode(String finDocumentPostingPeriodCode) {
        this.finDocumentPostingPeriodCode = finDocumentPostingPeriodCode;
    }

    /**
     * Gets the finDocumentExplanationText attribute.
     * 
     * @return - Returns the finDocumentExplanationText
     * 
     */
    public String getFinDocumentExplanationText() {
        return finDocumentExplanationText;
    }


    /**
     * Sets the finDocumentExplanationText attribute.
     * 
     * @param finDocumentExplanationText The finDocumentExplanationText to set.
     * 
     */
    public void setFinDocumentExplanationText(String finDocumentExplanationText) {
        this.finDocumentExplanationText = finDocumentExplanationText;
    }

    /**
     * Gets the financialDocument attribute.
     * 
     * @return - Returns the financialDocument
     * 
     */
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }


    /**
     * Sets the financialDocument attribute.
     * 
     * @param financialDocument The financialDocument to set.
     * @deprecated
     */
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }
}
