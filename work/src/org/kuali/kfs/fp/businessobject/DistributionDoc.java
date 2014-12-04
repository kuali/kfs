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
