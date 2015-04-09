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
