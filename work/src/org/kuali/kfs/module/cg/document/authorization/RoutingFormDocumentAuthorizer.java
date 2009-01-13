/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import java.util.Map;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.document.ResearchDocument;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.module.cg.document.service.ResearchDocumentPermissionsService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class RoutingFormDocumentAuthorizer extends ResearchDocumentAuthorizer {

    @Override
    public Map getEditMode(Document d, Person u) {

        Map editModes = new HashMap();

        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        ResearchDocumentPermissionsService permissionsService = SpringContext.getBean(ResearchDocumentPermissionsService.class);
        RoutingFormDocument routingFormDocument = (RoutingFormDocument) d;
        String permissionCode = AuthorizationConstants.EditMode.UNVIEWABLE;
        KualiWorkflowDocument workflowDocument = routingFormDocument.getDocumentHeader().getWorkflowDocument();

        // Check initiator
        if (workflowDocument.getInitiatorNetworkId().equalsIgnoreCase(u.getPrincipalName())) {
            if (workflowDocument.stateIsEnroute()) {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
            }
            else {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
            }
            return this.finalizeEditMode(routingFormDocument, permissionCode);
        }

        // Check personnel
        for (RoutingFormPersonnel person : routingFormDocument.getRoutingFormPersonnel()) {
            if (u.getPrincipalId().equals(person.getPrincipalId())) {
                //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(), 
                //RoutingFormPersonnel does not have any updatable references                
                person.refreshNonUpdateableReferences();
                String role = person.getPersonRole().getPersonRoleCode();
                if (CGConstants.PROJECT_DIRECTOR_CODE.equals(role) || CGConstants.CO_PROJECT_DIRECTOR_CODE.equals(role)) {
                    if (workflowDocument.getRouteHeader().getDocRouteLevel() > CGConstants.projectDirectorRouteLevel) {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
                    }
                    else {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
                    }

                    return this.finalizeEditMode(routingFormDocument, permissionCode);
                }
                if (CGConstants.CONTACT_PERSON_ADMINISTRATIVE_CODE.equals(role) || CGConstants.CONTACT_PERSON_PROPOSAL_CODE.equals(role)) {
                    permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
                }
            }
        }

        // Org approvers are view-only
        if (permissionsService.isUserInOrgHierarchy(routingFormDocument.buildProjectDirectorReportXml(true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPrincipalId())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }

        if (permissionsService.isUserInOrgHierarchy(routingFormDocument.buildCostShareOrgReportXml(true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPrincipalId())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }

        if (permissionsService.isUserInOrgHierarchy(routingFormDocument.buildOtherOrgReportXml(true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPrincipalId())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }

        permissionCode = getPermissionCodeByPrecedence(permissionCode, getAdHocEditMode(routingFormDocument, u));

        return this.finalizeEditMode(routingFormDocument, permissionCode);
    }    
}

