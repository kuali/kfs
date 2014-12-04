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
package org.kuali.kfs.integration.cg.dto;

import java.io.Serializable;
import java.util.List;

public class BudgetAdjustmentCreationStatusDTO implements Serializable {

    private static final long serialVersionUID = -3058053637490790154L;
    
    protected List<String> errorMessages;
    protected String documentNumber;
    protected String status;

    public BudgetAdjustmentCreationStatusDTO() {}

    /**
     * Gets the errorCodes attribute. 
     * @return Returns the errorCodes.
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Sets the errorCodes attribute value.
     * @param errorCodes The errorCodes to set.
     */
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

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
     * Gets the status attribute. 
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status attribute value.
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }


}
