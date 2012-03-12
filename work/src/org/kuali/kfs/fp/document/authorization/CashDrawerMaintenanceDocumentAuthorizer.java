/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
