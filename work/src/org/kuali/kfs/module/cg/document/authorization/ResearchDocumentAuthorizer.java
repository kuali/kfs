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
package org.kuali.kfs.module.cg.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.AdhocPerson;
import org.kuali.kfs.module.cg.businessobject.AdhocWorkgroup;
import org.kuali.kfs.module.cg.document.ResearchDocument;
import org.kuali.kfs.module.cg.document.service.ResearchDocumentPermissionsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.service.AuthorizationService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class ResearchDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(ResearchDocumentAuthorizer.class);

    /**
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    protected String getAdHocEditMode(ResearchDocument researchDocument, Person u) {

        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        ResearchDocumentPermissionsService permissionsService = SpringContext.getBean(ResearchDocumentPermissionsService.class);
        String permissionCode = AuthorizationConstants.EditMode.UNVIEWABLE;
        KualiWorkflowDocument workflowDocument = researchDocument.getDocumentHeader().getWorkflowDocument();

        // Check ad-hoc user permissions
        AdhocPerson budgetAdHocPermission = permissionsService.getAdHocPerson(researchDocument.getDocumentNumber(), u.getPrincipalId());
        if (budgetAdHocPermission != null) {
            if (CGConstants.PERMISSION_MOD_CODE.equals(budgetAdHocPermission.getPermissionCode())) {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
            }
            else {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
            }
        }

        // check ad-hoc workgroup permissions
        List<AdhocWorkgroup> adhocWorkgroups = permissionsService.getAllAdHocWorkgroups(researchDocument.getDocumentNumber());
        WorkflowInfo info2 = new WorkflowInfo();

        List<? extends KimGroup> personGroups = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupsForPrincipal(u.getPrincipalId());

        for (AdhocWorkgroup adhocWorkgroup : adhocWorkgroups) {
            KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, adhocWorkgroup.getWorkgroupName());

            if (!ObjectUtils.isNull(group)) {
                if (kimGroupsContainWorkgroup(group, personGroups)) {
                    if (adhocWorkgroup.getPermissionCode().equals(CGConstants.PERMISSION_MOD_CODE)) {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
                        break;
                    }
                    else {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
                    }
                }
            }
        }

        // now check ad-hoc workgroups in route log
        ReportCriteriaDTO criteria = new ReportCriteriaDTO();
        try {
            criteria.setRouteHeaderId(workflowDocument.getRouteHeaderId());
            WorkflowInfo info = new WorkflowInfo();
            ActionRequestDTO[] requests = info.getActionRequests(workflowDocument.getRouteHeaderId());
            for (int i = 0; i < requests.length; i++) {
                ActionRequestDTO request = (ActionRequestDTO) requests[i];
                if (request.isWorkgroupRequest()) {
                    KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroup("" + request.getWorkgroupId());
                    if (kimGroupsContainWorkgroup(group, personGroups)) {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
                        break;
                    }
                }
            }
        }
        catch (WorkflowException ex) {
            throw new RuntimeException("Caught workflow exception: " + ex);
        }

        // Check ad-hoc org permissions (mod first, then read)
        if (permissionsService.isUserInOrgHierarchy(researchDocument.buildAdhocOrgReportXml(CGConstants.PERMISSION_MOD_CODE, true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPrincipalId())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
        }

        if (permissionsService.isUserInOrgHierarchy(researchDocument.buildAdhocOrgReportXml(CGConstants.PERMISSION_READ_CODE, true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPrincipalId())) {
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
        if (permissionCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY) && (researchDocument.getDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.APPROVED) || researchDocument.getDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.DISAPPROVED) || researchDocument.getDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.CANCELLED))) {
            permissionCode = AuthorizationConstants.EditMode.VIEW_ONLY;
        }

        Map editModeMap = new HashMap();
        editModeMap.put(permissionCode, "TRUE");
        return editModeMap;
    }

    private boolean kimGroupsContainWorkgroup(KimGroup groupToCheck, List<? extends KimGroup> groups) {
        for (KimGroup group : groups) {
            if (group.getGroupId().equals(groupToCheck.getGroupId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether user is a global modifier
     * 
     * @param documentTypeName
     * @param user
     * @return true if the given user is allowed to modify documents of the given document type
     */
    public boolean canModify(String documentTypeName, Person user) {
        return SpringContext.getBean(AuthorizationService.class).isAuthorized(user, KFSConstants.PERMISSION_MODIFY, documentTypeName);
    }

    /**
     * Check whether user is a global viewer
     * 
     * @param documentTypeName
     * @param user
     * @return true if the given user is allowed to view documents of the given document type
     */
    public boolean canView(String documentTypeName, Person user) {
        return SpringContext.getBean(AuthorizationService.class).isAuthorized(user, KFSConstants.PERMISSION_VIEW, documentTypeName);
    }
}

