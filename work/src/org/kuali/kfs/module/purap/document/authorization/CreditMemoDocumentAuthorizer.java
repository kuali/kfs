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
package org.kuali.module.purap.document.authorization;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.service.PurapService;

/**
 * Document Authorizer for the Credit Memo document.
 */
public class CreditMemoDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#hasInitiateAuthorization(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public boolean hasInitiateAuthorization(Document document, UniversalUser user) {
        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        try {
            return SpringContext.getBean(KualiGroupService.class).getByGroupName(authorizedWorkgroup).hasMember(user);
        }
        catch (GroupNotFoundException e) {
            throw new RuntimeException("Workgroup " + authorizedWorkgroup + " not found", e);
        }
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        Map<String, String> editModeMap = super.getEditMode(document, user, sourceAccountingLines, targetAccountingLines);
        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(document, user)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            if (!SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((CreditMemoDocument) document)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        if (!editMode.equals(AuthorizationConstants.EditMode.FULL_ENTRY) || (! workflowDocument.isAdHocRequested())) {
            editModeMap.put(editMode, "TRUE");
        }
        CreditMemoDocument creditMemoDocument = (CreditMemoDocument) document;
        if (StringUtils.equals(creditMemoDocument.getStatusCode(), PurapConstants.CreditMemoStatuses.INITIATE)) {
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB, "TRUE");
        }
        else {
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB, "FALSE");
        }

        if (!creditMemoDocument.isSourceDocumentPaymentRequest()) {
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.LOCK_VENDOR_ENTRY, "TRUE");
        }

        String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        if (user.isMember(apGroup) && (creditMemoDocument.getExtractedDate() == null) && (! workflowDocument.isAdHocRequested())) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_PRE_EXTRACT, "TRUE");
        }

        return editModeMap;
    }

    /**
     * @see org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        CreditMemoDocument creditMemoDocument = (CreditMemoDocument) document;
        if (StringUtils.equals(creditMemoDocument.getStatusCode(), PurapConstants.CreditMemoStatuses.INITIATE)) {
            flags.setCanSave(false);
            flags.setCanClose(true);
            flags.setCanCancel(false);
        }
        else {
            flags.setCanSave(true);

            if (SpringContext.getBean(CreditMemoService.class).canCancelCreditMemo(creditMemoDocument, GlobalVariables.getUserSession().getUniversalUser())) {
                flags.setCanCancel(true);
            }
            else {
                flags.setCanCancel(false);
            }
        }

        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
        this.setAnnotateFlag(flags);

        return flags;
    }

}