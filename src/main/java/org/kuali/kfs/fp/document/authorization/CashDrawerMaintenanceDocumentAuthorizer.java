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
package org.kuali.kfs.fp.document.authorization;

import java.util.Map;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.api.KimConstants;

/**
 * Overridden to add extra role qualifications
 */
public class CashDrawerMaintenanceDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    /**
     * Overridden to add the cash drawer's campus code to the qualification
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.krad.bo.BusinessObject, java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> qualifications) {
        super.addRoleQualification(dataObject, qualifications);
        
        if (dataObject instanceof CashDrawer) {
            final CashDrawer cashDrawer = (CashDrawer)dataObject;
            qualifications.put(KimConstants.AttributeConstants.CAMPUS_CODE, cashDrawer.getCampusCode());
        }
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentAuthorizerBase#addPermissionDetails(org.kuali.rice.krad.bo.BusinessObject, java.util.Map)
     */
    @Override
    protected void addPermissionDetails(Object dataObject, Map<String, String> permissionDetails) {
        super.addPermissionDetails(dataObject, permissionDetails);
        if (dataObject instanceof CashDrawer) {
            permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, "CDS");
        }
    }

}
