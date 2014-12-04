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
package org.kuali.kfs.module.endow.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * This class represents the PresentationController for SecurityReportingGroup Maintenance Document
 */
public class SecurityReportingGroupDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {

        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        SecurityReportingGroup reportingGroup = (SecurityReportingGroup) document.getNewMaintainableObject().getBusinessObject();

        // if reporting group is cash equivalents active indicator and group order are read only
        if (EndowConstants.SecurityReportingGroups.CASH_EQUIVALENTS.equals(reportingGroup.getCode())) {
            readOnlyPropertyNames.add(EndowPropertyConstants.SECURITY_REPORTING_GROUP_ACTIVE_INFICATOR);
            readOnlyPropertyNames.add(EndowPropertyConstants.SECURITY_REPORTING_GROUP_ORDER);
        }

        return readOnlyPropertyNames;
    }

}
