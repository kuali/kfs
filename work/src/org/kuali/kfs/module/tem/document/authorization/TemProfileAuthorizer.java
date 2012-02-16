/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.identity.TemKimAttributes;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.util.ObjectUtils;

public class TemProfileAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        FinancialSystemMaintenanceDocument maintDoc = (FinancialSystemMaintenanceDocument) businessObject;
        TEMProfile profile = (TEMProfile) maintDoc.getNewMaintainableObject().getBusinessObject();
        
        // Add the principalId from the profile to grant permission to users modifying their own profile.
        if (!StringUtils.isBlank(profile.getPrincipalId())) {
            attributes.put(TemKimAttributes.PROFILE_PRINCIPAL_ID, profile.getPrincipalId());
        }
        
        // OrgCode and COACode are needed for the org descending hierarchy qualification
        if (!StringUtils.isBlank(profile.getHomeDeptOrgCode())) {
            attributes.put(KfsKimAttributes.ORGANIZATION_CODE, profile.getHomeDeptOrgCode());
        }
        if (!StringUtils.isBlank(profile.getHomeDeptChartOfAccountsCode())) {
            attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, profile.getHomeDeptChartOfAccountsCode());
        }
        
        // Add the profileId from the profile to grant permission to the assigned arrangers modifying the profile.
        if (ObjectUtils.isNotNull(profile.getProfileId())) {
            attributes.put(TemPropertyConstants.TEMProfileProperties.PROFILE_ID, profile.getProfileId().toString());
        }
    } 

}
