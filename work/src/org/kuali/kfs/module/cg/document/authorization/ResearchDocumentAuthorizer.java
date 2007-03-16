/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.kra.document;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentAuthorizerBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.bo.AdhocPerson;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.document.BudgetDocumentAuthorizer;
import org.kuali.module.kra.service.ResearchDocumentPermissionsService;

public class ResearchDocumentAuthorizer extends DocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(ResearchDocumentAuthorizer.class);
    
    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    protected String getAdHocEditMode(ResearchDocument researchDocument, UniversalUser u) {
        
        KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
        ResearchDocumentPermissionsService permissionsService = SpringServiceLocator.getResearchDocumentPermissionsService();
        String permissionCode = AuthorizationConstants.EditMode.UNVIEWABLE;
        KualiWorkflowDocument workflowDocument = researchDocument.getDocumentHeader().getWorkflowDocument();
        
        // Check ad-hoc user permissions
        AdhocPerson budgetAdHocPermission = permissionsService.getAdHocPerson(researchDocument.getDocumentNumber(), u.getPersonUniversalIdentifier());
        if (budgetAdHocPermission != null) {
            if (KraConstants.PERMISSION_MOD_CODE.equals(budgetAdHocPermission.getPermissionCode())) {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
            } else {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
            }
        }
        
        // Check ad-hoc org permissions (mod first, then read)
        if (permissionsService.isUserInOrgHierarchy(researchDocument.buildAdhocOrgReportXml(KraConstants.PERMISSION_MOD_CODE, true), u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
        }
        
        if (permissionsService.isUserInOrgHierarchy(researchDocument.buildAdhocOrgReportXml(KraConstants.PERMISSION_READ_CODE, true), u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }
        
        // Check global document type permissions
        if (canModify(workflowDocument.getDocumentType(), u)) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
        }
        
        if (canView(workflowDocument.getDocumentType(), u)) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }
        
        return permissionCode;
    }
    
    /**
     * Set the permission code to the "higher-precedent" value, based on the 2 values passed in
     * 
     * @param String orgXml
     * @param String uuid
     * @return boolean
     */
    protected String getPermissionCodeByPrecedence(String currentCode, String candidateCode) {
        if (currentCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY) || candidateCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY)) {
            return AuthorizationConstants.EditMode.FULL_ENTRY;
        }
        if (currentCode.equals(AuthorizationConstants.EditMode.VIEW_ONLY) || candidateCode.equals(AuthorizationConstants.EditMode.VIEW_ONLY)) {
            return AuthorizationConstants.EditMode.VIEW_ONLY;
        }
        return AuthorizationConstants.EditMode.UNVIEWABLE;
    }
    
    /**
     * Finalize the permission code & the map and return
     * 
     * @param ResearchDocument researchDocument
     * @param String permissionCode
     * @return Map
     */
    protected Map finalizeEditMode(ResearchDocument researchDocument, String permissionCode) {
        // If doc is approved, full entry should become view only
        if (permissionCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY) 
                && researchDocument.getDocumentHeader().getFinancialDocumentStatusCode().equals(Constants.DocumentStatusCodes.APPROVED)) {
            permissionCode = AuthorizationConstants.EditMode.VIEW_ONLY;
        }
        
        Map editModeMap = new HashMap();
        editModeMap.put(permissionCode, "TRUE");
        return editModeMap;
    }
    
    /**
     * Check whether user is a global modifier
     * 
     * @param documentTypeName
     * @param user
     * @return true if the given user is allowed to modify documents of the given document type
     */
    public boolean canModify(String documentTypeName, UniversalUser user) {
        return SpringServiceLocator.getAuthorizationService().isAuthorized(user, Constants.PERMISSION_MODIFY, documentTypeName);
    }
    
    /**
     * Check whether user is a global viewer
     * 
     * @param documentTypeName
     * @param user
     * @return true if the given user is allowed to view documents of the given document type
     */
    public boolean canView(String documentTypeName, UniversalUser user) {
        return SpringServiceLocator.getAuthorizationService().isAuthorized(user, Constants.PERMISSION_VIEW, documentTypeName);
    }
}