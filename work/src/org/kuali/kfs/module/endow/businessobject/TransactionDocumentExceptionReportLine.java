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


public class TransactionDocumentExceptionReportLine extends TransactionDocumentForReportLineBase {

    protected String kemid;

    public TransactionDocumentExceptionReportLine() {
        this("", "");
    }
    
    public TransactionDocumentExceptionReportLine(String documentType, String documentId) {
        this(documentType, documentId, "", "");        
    }
    
    public TransactionDocumentExceptionReportLine(String documentType, String documentId, String securityId) {
        this(documentType, documentId, securityId, "");        
    }
    
    public TransactionDocumentExceptionReportLine(String documentType, String documentId, String securityId, String kemid) {
        this.documentType = documentType;
        this.documentId   = documentId;
        this.securityId   = securityId;
        this.kemid        = kemid;        
    }
    
    /**
     * Gets the kemid attribute. 
     * @return Returns the kemid.
     */
    public String getKemid() {
        return kemid;
    }
    
    /**
     * Sets the kemid attribute. 
     * @param the kemid.
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }
    
}
