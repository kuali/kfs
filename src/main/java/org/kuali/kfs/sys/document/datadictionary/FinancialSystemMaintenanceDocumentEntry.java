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
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.document.Document;

/**
 * This class allows the system to define a custom document base class for
 */
public class FinancialSystemMaintenanceDocumentEntry extends MaintenanceDocumentEntry {

    /**
     * @see org.kuali.rice.kns.datadictionary.DocumentEntry#setDocumentClass(java.lang.Class)
     */
    public Class<? extends Document> getStandardDocumentBaseClass() {
        return FinancialSystemMaintenanceDocument.class;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.DocumentEntry#setDocumentClass(java.lang.Class)
     */
    @Override
    public void setDocumentClass(Class<? extends Document> documentClass) {
        if (!FinancialSystemMaintenanceDocument.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("document class '" + documentClass + "' needs to have a superclass of '" + FinancialSystemMaintenanceDocument.class + "'");
        }
        super.setDocumentClass(documentClass);
    }

}
