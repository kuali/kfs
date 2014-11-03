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
