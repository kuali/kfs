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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.impl.KimDocumentTypeAuthorizer;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 *
 */
public class ContractsGrantsInvoiceWorkflowDocumentAuthorizer extends KimDocumentTypeAuthorizer {

    /**
     * Overridden to pass in the proposal number for the CINV doc to help determine whether the current user
     * is a fund manager for this doc/award.
     *
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    protected Map<String, String> buildDocumentRoleQualifiers(DocumentRouteHeaderValue document, String routeNodeName) {
        Map<String, String> qualifiers = super.buildDocumentRoleQualifiers(document, routeNodeName);

        String documentNumber = document.getDocumentId();
        if (StringUtils.isNotBlank(documentNumber)) {
            ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = null;
            try {
                contractsGrantsInvoiceDocument = (ContractsGrantsInvoiceDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
            }
            catch (WorkflowException ex) {
                // Couldn't get the document, just continue on
            }
            if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument)) {
                ContractsAndGrantsBillingAward award = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAward();
                qualifiers.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber().toString());
            }
        }

        return qualifiers;
    }

    /**
     * Overridden to pass in the current user's principal id as a qualifier to help determine if they are a Fund Manager.
     *
     * @see org.kuali.rice.kew.doctype.service.impl.DocumentActionsPermissionBase#canInitiate(java.lang.String, org.kuali.rice.kew.doctype.bo.DocumentType)
     */
    @Override
    public boolean canInitiate(String principalId, DocumentType documentType) {
        validatePrincipalId(principalId);
        validateDocumentType(documentType);

        Map<String, String> permissionDetails = buildDocumentTypePermissionDetails(documentType, null, null, null);
        Map<String, String> qualificationDetails = new HashMap<String, String>();
        qualificationDetails.put(KimConstants.AttributeConstants.PRINCIPAL_ID, principalId);

        if (useKimPermission(KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, KewApiConstants.INITIATE_PERMISSION, permissionDetails, true)) {
            return getPermissionService().isAuthorizedByTemplate(principalId, KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE,
                    KewApiConstants.INITIATE_PERMISSION, permissionDetails, qualificationDetails);
        }
        return true;
    }

}
