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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines a Contracts & Grants Billing Invoice Document Error Message.
 */
public class ContractsGrantsInvoiceDocumentErrorMessage extends PersistableBusinessObjectBase {

    private Long errorLogIdentifier;
    private Long errorMessageIdentifier;
    private String errorMessageText;

    public Long getErrorLogIdentifier() {
        return errorLogIdentifier;
    }

    public void setErrorLogIdentifier(Long errorLogIdentifier) {
        this.errorLogIdentifier = errorLogIdentifier;
    }

    public Long getErrorMessageIdentifier() {
        return errorMessageIdentifier;
    }

    public void setErrorMessageIdentifier(Long errorMessageIdentifier) {
        this.errorMessageIdentifier = errorMessageIdentifier;
    }

    public String getErrorMessageText() {
        return errorMessageText;
    }

    public void setErrorMessageText(String errorMessageText) {
        this.errorMessageText = errorMessageText;
    }

    /**
     * This can be displayed by ContractsGrantsInvoiceDocumentErrorLog lookup results.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return errorMessageText;
    }

}
