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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;

public class OrganizationOptionsMaintenanceDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {
    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        OrganizationOptions organizationOptions = null;
        if (businessObject instanceof MaintenanceDocument) {
            organizationOptions = (OrganizationOptions) ((MaintenanceDocument) businessObject).getNewMaintainableObject().getBusinessObject();
        }
        else {
            organizationOptions = (OrganizationOptions) businessObject;
        }
        attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, organizationOptions.getChartOfAccountsCode());
        attributes.put(KfsKimAttributes.ORGANIZATION_CODE, organizationOptions.getOrganizationCode());
        attributes.put(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, organizationOptions.getProcessingChartOfAccountCode());
        attributes.put(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_ORGANIZATION_CODE, organizationOptions.getProcessingOrganizationCode());
    }
}
