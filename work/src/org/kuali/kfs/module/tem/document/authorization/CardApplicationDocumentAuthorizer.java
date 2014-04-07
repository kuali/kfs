/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Generic document authorizer for travel card application documents
 */
public class CardApplicationDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {
    protected static volatile TemProfileService temProfileService;

    /**
     * Adds role qualifications about the applying user
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#addRoleQualification(java.lang.Object, java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> qualification) {
        super.addRoleQualification(dataObject, qualification);
        addCurrentUserQualifiers(qualification);
    }

    /**
     * Adds role qualifications based on the current user; if the current user has a profile, gleans more information from that
     * @param qualification the qualification to add information about the current user to
     */
    protected void addCurrentUserQualifiers(Map<String, String> qualification) {
        final String currentUserPrincipalId = GlobalVariables.getUserSession().getPrincipalId();
        qualification.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, currentUserPrincipalId);

        final TemProfile profile = getTemProfileService().findTemProfileByPrincipalId(currentUserPrincipalId);
        if (profile != null) {
            if (!qualification.containsKey(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)) {
                qualification.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, profile.getHomeDeptChartOfAccountsCode());
            }
            if (!qualification.containsKey(KFSPropertyConstants.ORGANIZATION_CODE)) {
                qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, profile.getHomeDeptOrgCode());
            }
        }
    }

    /**
     * Overridden to pass in profile principal id as the current user's principal id
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canInitiate(String documentTypeName, Person user) {
        String nameSpaceCode = KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE;
        Map<String, String> permissionDetails = new HashMap<String, String>();
        Map<String, String> qualificationDetails = new HashMap<String, String>();
        addCurrentUserQualifiers(qualificationDetails);
        qualificationDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentTypeName);
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentTypeName);
        return getPermissionService().isAuthorizedByTemplate(user.getPrincipalId(), nameSpaceCode,
                KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails, qualificationDetails);
    }

    /**
     * @return the default implementation of the TemProfileService
     */
    public TemProfileService getTemProfileService() {
        if (temProfileService == null) {
            temProfileService = SpringContext.getBean(TemProfileService.class);
        }
        return temProfileService;
    }
}
