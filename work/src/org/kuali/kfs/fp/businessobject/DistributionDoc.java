/*
 * Copyright 2005-2006 The Kuali Foundation
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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a distribution document.
 */
public class DistributionDoc extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer finDocumentNextFromLineNbr;
    private Integer finDocumentNextToLineNumber;
    private Integer financialDocumentPostingYear;
    private String finDocumentPostingPeriodCode;

    private DocumentHeader financialDocument;

    /**
     * Default no-arg constructor.
     */
    public DistributionDoc() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the finDocumentNextFromLineNbr attribute.
     * 
     * @return Returns the finDocumentNextFromLineNbr
     */
    public Integer getFinDocumentNextFromLineNbr() {
        return finDocumentNextFromLineNbr;
    }


    /**
     * Sets the finDocumentNextFromLineNbr attribute.
     * 
     * @param finDocumentNextFromLineNbr The finDocumentNextFromLineNbr to set.
     */
    public void setFinDocumentNextFromLineNbr(Integer finDocumentNextFromLineNbr) {
        this.finDocumentNextFromLineNbr = finDocumentNextFromLineNbr;
    }

    /**
     * Gets the finDocumentNextToLineNumber attribute.
     * 
     * @return Returns the finDocumentNextToLineNumber
     */
    public Integer getFinDocumentNextToLineNumber() {
        return finDocumentNextToLineNumber;
    }


    /**
     * Sets the finDocumentNextToLineNumber attribute.
     * 
     * @param finDocumentNextToLineNumber The finDocumentNextToLineNumber to set.
     */
    public void setFinDocumentNextToLineNumber(Integer finDocumentNextToLineNumber) {
        this.finDocumentNextToLineNumber = finDocumentNextToLineNumber;
    }

    /**
     * Gets the financialDocumentPostingYear attribute.
     * 
     * @return Returns the financialDocumentPostingYear
     */
    public Integer getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }


    /**
     * Sets the financialDocumentPostingYear attribute.
     * 
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     */
    public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
    }

    /**
     * Gets the finDocumentPostingPeriodCode attribute.
     * 
     * @return Returns the finDocumentPostingPeriodCode
     */
    public String getFinDocumentPostingPeriodCode() {
        return finDocumentPostingPeriodCode;
    }


    /**
     * Sets the finDocumentPostingPeriodCode attribute.
     * 
     * @param finDocumentPostingPeriodCode The finDocumentPostingPeriodCode to set.
     */
    public void setFinDocumentPostingPeriodCode(String finDocumentPostingPeriodCode) {
        this.finDocumentPostingPeriodCode = finDocumentPostingPeriodCode;
    }

    /**
     * Gets the financialDocument attribute.
     * 
     * @return Returns the financialDocument
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
