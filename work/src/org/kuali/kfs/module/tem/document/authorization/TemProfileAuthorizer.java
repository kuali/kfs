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
import org.kuali.rice.krad.util.ObjectUtils;

public class TemProfileAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#addRoleQualification(java.lang.Object, java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
        super.addRoleQualification(dataObject, attributes);
        if (dataObject instanceof FinancialSystemMaintenanceDocument) {
            FinancialSystemMaintenanceDocument maintDoc = (FinancialSystemMaintenanceDocument) dataObject;
            if (maintDoc.getNewMaintainableObject().getBusinessObject() instanceof TEMProfile) {
                final TEMProfile profile = (TEMProfile) maintDoc.getNewMaintainableObject().getBusinessObject();
                addRoleQualificationsFromProfile(profile, attributes);
            }
        } else if (dataObject instanceof TEMProfile) {
            addRoleQualificationsFromProfile((TEMProfile)dataObject, attributes);
        }
    }

    /**
     * Adds role qualifiers harvested from the TEMProfile to the attributes Map
     * @param profile the TEMProfile to harvest qualifiers from
     * @param attributes the Map of qualifiers to add into
     */
    protected void addRoleQualificationsFromProfile(TEMProfile profile, Map<String, String> attributes) {
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
