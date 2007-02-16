/*
 * Copyright 2006 The Kuali Foundation.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.module.chart.bo.ChartUser;

/**
 * Authorization permissions specific to the Advance Deposit document.
 * 
 * 
 */
public class AdvanceDepositDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    /**
     * Only need to allow initiator or supervisor the ability to edit in pre-route mode. All other situations only present in
     * non-edit mode. Since doc routes straight to final, no other editing is needed.
     * 
     * @see org.kuali.core.authorization.TransactionalDocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser, java.util.List, java.util.List)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        String editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if ((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) && (document.getDocumentHeader().getFinancialDocumentInErrorNumber() == null)) {
            if (workflowDocument.userIsInitiator(user)) {
                editMode = AuthorizationConstants.TransactionalEditMode.FULL_ENTRY;
            }
        }

        Map editModeMap = new HashMap();
        editModeMap.put(editMode, "TRUE");

        return editModeMap;
    }

    /**
     * Overrides to use the parent's implementation, with the exception that AD documents can never be error corrected.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        TransactionalDocumentActionFlags tflags = (TransactionalDocumentActionFlags) flags;
        tflags.setCanErrorCorrect(false); // CCR, AD, CR, DV, andd PCDO don't allow error correction

        return flags;
    }

    /**
     * Overrides to always return false because there is never FO routing or FO approval for AD docs.
     * 
     * @see org.kuali.module.financial.document.FinancialDocumentAuthorizer#userOwnsAnyAccountingLine(org.kuali.core.bo.user.KualiUser,
     *      java.util.List)
     */
    @Override
    protected boolean userOwnsAnyAccountingLine(ChartUser user, List accountingLines) {
        return false;
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the AD doc.
     * 
     * @see org.kuali.core.authorization.TransactionalDocumentAuthorizer#getEditableAccounts(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public Map getEditableAccounts(TransactionalDocument document, ChartUser user) {
        return new HashMap();
    }
}