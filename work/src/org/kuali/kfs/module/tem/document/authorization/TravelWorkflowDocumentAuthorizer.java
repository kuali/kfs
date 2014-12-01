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

import org.kuali.kfs.sys.identity.KfsKimAttributes;
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
            roleQualifiers.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, principalId);

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
        qualificationDetails.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, principalId);
        return qualificationDetails;
    }
}
