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
package org.kuali.kfs.sec.document.authorization;

import java.util.Set;

import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;


/**
 * Presentation controller for the security definition maintenance document, sets conditional read only fields
 */
public class SecurityDefinitionMaintenanceDocumentPresentationController extends MaintenanceDocumentPresentationControllerBase {


    /**
     * Don't allow editing of definition name on edit
     *
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyFields = super.getConditionallyReadOnlyPropertyNames(document);

        if (!document.isNew()) {
            readOnlyFields.add(KFSPropertyConstants.NAME);
            readOnlyFields.add(SecPropertyConstants.SECURITY_ATTRIBUTE_NAME_NESTED);
        }

        return readOnlyFields;
    }

}
