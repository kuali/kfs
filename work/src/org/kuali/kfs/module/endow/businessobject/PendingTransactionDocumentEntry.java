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

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class PendingTransactionDocumentEntry extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String documentType;
    private Date approvedDate;
    
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
   
    /**
     * Gets the documentNumber
     * 
     * @return documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber
     * 
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    /**
     * Gets the documentType
     * 
     * @return documentType
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType
     * 
     * @param documentType
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    /**
     * Gets the approvedDate
     * 
     * @return approvedDate
     */
    public Date getApprovedDate() {
        return approvedDate;
    }

    /**
     * Sets the approvedDate
     * 
     * @param approvedDate
     */
    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

}
