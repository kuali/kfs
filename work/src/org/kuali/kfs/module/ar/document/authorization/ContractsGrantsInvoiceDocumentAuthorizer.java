/*
 * Copyright 2008 The Kuali Foundation
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
import java.util.Set;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class ContractsGrantsInvoiceDocumentAuthorizer extends CustomerInvoiceDocumentAuthorizer {

    /**
     * Overridden to pass in the proposal number for the CINV doc to help determine whether the current user
     * is a fund manager for this doc/award.
     *
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);

        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = (ContractsGrantsInvoiceDocument) businessObject;
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail()) && ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getProposalNumber())) {
            attributes.put(KFSPropertyConstants.PROPOSAL_NUMBER, contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getProposalNumber().toString());
        }
    }

    /**
     * Overridden to pass in the current user's principal id as a qualifier to help determine if they are a Fund Manager.
     *
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canInitiate(String documentTypeName, Person user) {
        String nameSpaceCode = KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE;
        Map<String, String> permissionDetails = new HashMap<String, String>();
        Map<String, String> qualificationDetails = new HashMap<String, String>();
        qualificationDetails.put(KimConstants.AttributeConstants.PRINCIPAL_ID, user.getPrincipalId());
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentTypeName);
        return getPermissionService().isAuthorizedByTemplate(user.getPrincipalId(), nameSpaceCode,
                KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails, qualificationDetails);
    }

    /**
     * Overriding to make sure KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT permission is not removed by super class
     *
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase#getDocumentActions(org.kuali.rice.krad.document.Document, org.kuali.rice.kim.api.identity.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        boolean canErrorCorrect = documentActionsFromPresentationController.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
        Set<String> documentActionsAfterProcessing = super.getDocumentActions(document, user, documentActionsFromPresentationController);

        if (canErrorCorrect && !documentActionsAfterProcessing.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)) {
            documentActionsAfterProcessing.add(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
        }

        return documentActionsAfterProcessing;
    }


}
