/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.kra.budget.document;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.authorization.DocumentActionFlags;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.DocumentAuthorizerBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.bo.BudgetAdHocPermission;
import org.kuali.module.kra.budget.service.BudgetPermissionsService;

/**
 * DocumentAuthorizer class for KRA Budget Documents.
 */
public class BudgetDocumentAuthorizer extends DocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(BudgetDocumentAuthorizer.class);
    
    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getEditMode(Document d, UniversalUser u) {
        
        KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
        BudgetPermissionsService permissionsService = SpringServiceLocator.getBudgetPermissionsService();
        BudgetDocument budgetDocument = (BudgetDocument) d;
        String permissionCode = AuthorizationConstants.EditMode.UNVIEWABLE;
        KualiWorkflowDocument workflowDocument = budgetDocument.getDocumentHeader().getWorkflowDocument();
        
        // Check default user permissions
        if (workflowDocument.getInitiatorNetworkId().equalsIgnoreCase(u.getPersonUserIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
        }
        
        if (u.getPersonUniversalIdentifier().equals(budgetDocument.getBudget().getBudgetProjectDirectorSystemId())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, kualiConfigurationService.getApplicationParameterValue(
                    KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.PROJECT_DIRECTOR_BUDGET_PERMISSION));
        }
            
        // Check ad-hoc user permissions
        BudgetAdHocPermission budgetAdHocPermission = permissionsService.getBudgetAdHocPermission(budgetDocument.getDocumentNumber(), u.getPersonUniversalIdentifier());
        if (budgetAdHocPermission != null) {
            if (KraConstants.PERMISSION_MOD_CODE.equals(budgetAdHocPermission.getBudgetPermissionCode())) {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
            } else {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
            }
        }
        
        // Check default org permissions - project director
        if (!budgetDocument.getBudget().getPersonnel().isEmpty()) {
            if (permissionsService.isUserInOrgHierarchy(budgetDocument.buildProjectDirectorReportXml(true), u.getPersonUniversalIdentifier())) {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, kualiConfigurationService.getApplicationParameterValue(
                        KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.PROJECT_DIRECTOR_ORG_BUDGET_PERMISSION));
            }
        }
        
        // Check default org permissions - cost sharing orgs
        if (permissionsService.isUserInOrgHierarchy(budgetDocument.buildCostShareOrgReportXml(true), u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, kualiConfigurationService.getApplicationParameterValue(
                    KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.COST_SHARE_ORGS_BUDGET_PERMISSION));
        }
        
        // Check ad-hoc org permissions (mod first, then read)
        if (permissionsService.isUserInOrgHierarchy(budgetDocument.buildAdhocOrgReportXml(KraConstants.PERMISSION_MOD_CODE, true), u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
        }
        
        if (permissionsService.isUserInOrgHierarchy(budgetDocument.buildAdhocOrgReportXml(KraConstants.PERMISSION_READ_CODE, true), u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }
        
        // Check global document type permissions
        if (canModify(workflowDocument.getDocumentType(), u)) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
        }
        
        if (canView(workflowDocument.getDocumentType(), u)) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }
        
        // If doc is approved, full entry should become view only
        if (permissionCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY) 
                && budgetDocument.getDocumentHeader().getFinancialDocumentStatusCode().equals(Constants.DocumentStatusCodes.APPROVED)) {
            permissionCode = AuthorizationConstants.EditMode.VIEW_ONLY;
        }
        
        Map editModeMap = new HashMap();
        editModeMap.put(permissionCode, "TRUE");
        return editModeMap;
    }
    
    /**
     * Overrides most of the inherited flags so that the buttons behave exactly like they used to in the obsoleted
     * budgetDocumentControls.tag
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        LOG.debug("calling BudgetDocumentAuthorizer.getDocumentActionFlags");

        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        flags.setCanAcknowledge(false);
        flags.setCanApprove(false);
        flags.setCanBlanketApprove(false);
        flags.setCanCancel(false);
        flags.setCanDisapprove(false);
        flags.setCanFYI(false);
        flags.setCanClose(false);
        flags.setCanSave(true);

        BudgetDocument budgetDocument = (BudgetDocument) document;
        
        // use inherited canRoute, canAnnotate, and canReload values

        return flags;
    }
    
    /**
     * Set the permission code to the "higher-precedent" value, based on the 2 values passed in
     * 
     * @param String orgXml
     * @param String uuid
     * @return boolean
     */
    private String getPermissionCodeByPrecedence(String currentCode, String candidateCode) {
        if (currentCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY) || candidateCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY)) {
            return AuthorizationConstants.EditMode.FULL_ENTRY;
        }
        if (currentCode.equals(AuthorizationConstants.EditMode.VIEW_ONLY) || candidateCode.equals(AuthorizationConstants.EditMode.VIEW_ONLY)) {
            return AuthorizationConstants.EditMode.VIEW_ONLY;
        }
        return AuthorizationConstants.EditMode.UNVIEWABLE;
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
