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
package org.kuali.module.financial.document.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.exceptions.DocumentTypeAuthorizationException;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.workflow.KualiWorkflowUtils.RouteLevelNames;

/**
 * Document Authorizer for the Procurement Card document.
 */
public class ProcurementCardDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * Overrides to call super and then blanketly reset the actions not allowed on the procurment card document.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        TransactionalDocumentActionFlags flags = new TransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));

        flags.setCanErrorCorrect(false); // PCDO doesn't allow error correction

        flags.setCanCancel(false); // PCDO cannot be cancelled

        flags.setCanDisapprove(false); // PCDO cannot be disapproved

        flags.setCanCopy(false); // PCDO cannot be copied

        return flags;
    }

    /**
     * Override to set the editMode to fullEntry if the routing is at the first account review node (PCDO has 2), second account
     * review functions as normal.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user, List sourceLines, List targetLines) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        List activeNodes = getCurrentRouteLevels(workflowDocument);

        List lineList = new ArrayList();
        lineList.addAll(sourceLines);
        lineList.addAll(targetLines);

        Map editModeMap = new HashMap();
        // FULL_ENTRY only if: a) person has an approval request, b) we are at the correct level, c) it's not a correction
        // document, d) it is not an ADHOC request (important so that ADHOC don't get full entry).
        if (workflowDocument.isApprovalRequested() && activeNodes.contains(RouteLevelNames.ACCOUNT_REVIEW_FULL_EDIT) && (document.getDocumentHeader().getFinancialDocumentInErrorNumber() == null) && !document.getDocumentHeader().getWorkflowDocument().isAdHocRequested()) {
            editModeMap.put(KfsAuthorizationConstants.TransactionalEditMode.FULL_ENTRY, "TRUE");
        }
        else {
            editModeMap = super.getEditMode(document, user, sourceLines, targetLines);
        }

        return editModeMap;
    }

    /**
     * Override to only allow the SYSTEM user to initiate a PCDO document
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) {
        if (!KFSConstants.SYSTEM_USER.equalsIgnoreCase(user.getPersonUserIdentifier())) {
            throw new DocumentTypeAuthorizationException(user.getPersonUserIdentifier(), "initiate", documentTypeName);
        }
    }

}