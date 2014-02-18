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

import org.kuali.kfs.module.tem.identity.TemKimAttributes;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.impl.KimDocumentTypeAuthorizer;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.krad.util.KRADConstants;

/**
 *
 */
public class TravelWorkflowDocumentAuthorizer extends KimDocumentTypeAuthorizer {

    @Override
    public boolean canInitiate(String principalId, DocumentType documentType) {
        validatePrincipalId(principalId);
        validateDocumentType(documentType);

        Map<String, String> permissionDetails = buildDocumentTypePermissionDetails(documentType, null, null, null);
        Map<String, String> qualificationDetails = getRoleQualifiers(principalId);

        if (useKimPermission(KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, KewApiConstants.INITIATE_PERMISSION, permissionDetails, true)) {
            return getPermissionService().isAuthorizedByTemplate(principalId, KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE,
                    KewApiConstants.INITIATE_PERMISSION, permissionDetails, qualificationDetails);
        }
        return true;
    }

    @Override
    public boolean canRoute(String principalId, DocumentRouteHeaderValue document) {
        validatePrincipalId(principalId);
        validateDocument(document);
        String documentId = document.getDocumentId();
        DocumentType documentType = document.getDocumentType();
        String documentStatus = document.getDocRouteStatus();
        String initiatorPrincipalId = document.getInitiatorWorkflowId();
        validateDocumentType(documentType);
        validateDocumentStatus(documentStatus);
        validatePrincipalId(initiatorPrincipalId);

        if (!documentType.isPolicyDefined(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.INITIATOR_MUST_ROUTE)) {
            Map<String, String> permissionDetails = buildDocumentTypePermissionDetails(documentType, documentStatus, null, null);
            Map<String, String> roleQualifiers = buildDocumentRoleQualifiers(document, permissionDetails.get(KewApiConstants.ROUTE_NODE_NAME_DETAIL));
            roleQualifiers.put(TemKimAttributes.PROFILE_PRINCIPAL_ID, principalId);

            if (useKimPermission(KewApiConstants.KEW_NAMESPACE, KewApiConstants.ROUTE_PERMISSION, permissionDetails, true)) {
                return getPermissionService().isAuthorizedByTemplate(principalId, KewApiConstants.KEW_NAMESPACE,
                        KewApiConstants.ROUTE_PERMISSION, permissionDetails, roleQualifiers);
            }
        }

        if (documentType.getInitiatorMustRoutePolicy().getPolicyValue()) {
            return super.canRoute(principalId, document);
        }
        return true;
    }

    /**
     * Creates a role qualifiers map that will work with the KFS-TEM TEM Profile role
     *
     * @param principalId
     * @return
     */
    protected Map<String, String> getRoleQualifiers(String principalId) {
        Map<String, String> qualificationDetails = new HashMap<String, String>();
        qualificationDetails.put(TemKimAttributes.PROFILE_PRINCIPAL_ID, principalId);
        return qualificationDetails;
    }
}
