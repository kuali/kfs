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

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * This class represents a point in time snapshot of a Bill associated with a Contracts & Grants Invoice Document.
 */
public class InvoiceBill extends BillBase {

    private String documentNumber;

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    @Override
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = super.toStringMapper_RICE20_REFACTORME();

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);

        return m;
    }
}
