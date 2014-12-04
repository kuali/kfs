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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class TransactioneDocPostingDocumentExceptionReportLine extends TransientBusinessObjectBase {

    private String documentNumber;
    private String documentName;
    private String lineType;
    private String lineNumber;
    private String reason;
    
    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the documentName attribute. 
     * @return Returns the documentName.
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Sets the documentName attribute value.
     * @param documentName The documentName to set.
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    /**
     * Gets the lineType attribute. 
     * @return Returns the lineType.
     */
    public String getLineType() {
        return lineType;
    }

    /**
     * Sets the lineType attribute value.
     * @param lineType The lineType to set.
     */
    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    /**
     * Gets the lineNumber attribute. 
     * @return Returns the lineNumber.
     */
    public String getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the lineNumber attribute value.
     * @param lineNumber The lineNumber to set.
     */
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the reason attribute. 
     * @return Returns the reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason attribute value.
     * @param reason The reason to set.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

}
