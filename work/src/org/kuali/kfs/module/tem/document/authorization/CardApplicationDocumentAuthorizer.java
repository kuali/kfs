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
