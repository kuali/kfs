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
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.module.purap.document.PaymentRequestDocument;

/**
 * Document Authorizer for the PREQ document.
 * 
 */
public class PaymentRequestDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    @Override
    public boolean hasInitiateAuthorization(Document document, UniversalUser user) {
        //String authorizedWorkgroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapRuleConstants.PURAP_ADMIN_GROUP, PurapRuleConstants.PURAP_DOCUMENT_PREQ_ACTIONS);
        String authorizedWorkgroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapRuleConstants.PURAP_ADMIN_GROUP, PurapRuleConstants.PURAP_DOCUMENT_PO_ACTIONS);
        try {
            return SpringContext.getBean(KualiGroupService.class).getByGroupName(authorizedWorkgroup).hasMember(user);
        }
        catch (GroupNotFoundException e) {
            throw new RuntimeException("Workgroup " + authorizedWorkgroup + " not found", e);
        }
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = super.getEditMode(document, user, sourceAccountingLines, targetAccountingLines);
        PaymentRequestDocument preq = (PaymentRequestDocument) document;

        //TODO: Chris - This class should be renamed since it's not just for actions
        PaymentRequestDocumentActionAuthorizer preqDocAuth  = new PaymentRequestDocumentActionAuthorizer(preq,user); 
        
        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(document, user)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        } else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
            editMode = AuthorizationConstants.EditMode.FULL_ENTRY;

            if (currentRouteLevels.contains(NodeDetailEnum.ACCOUNT_REVIEW.getName())) {
                editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
                //expense_entry was already added in super
                //add amount edit mode
                editMode = PurapAuthorizationConstants.PaymentRequestEditMode.ALLOW_ACCOUNT_AMOUNT_ENTRY;
            } else if (currentRouteLevels.contains(NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW.getName())){
                //is in ap review remove the view only that was added by super
                editModeMap.remove(AuthorizationConstants.EditMode.VIEW_ONLY);
            }
        } 

        editModeMap.put(editMode, "TRUE");

        //make sure ap user can edit certain fields 
        if (preqDocAuth.canEditPreExtractFields()) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_PRE_EXTRACT, "TRUE");
        }
        
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PurapConstants.PaymentRequestStatuses.INITIATE)) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB, "TRUE");
        }
        else {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB, "FALSE");
        }
        if (ObjectUtils.isNotNull(paymentRequestDocument.getVendorHeaderGeneratedIdentifier())) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.LOCK_VENDOR_ENTRY, "TRUE");
        }
        return editModeMap;
    }

    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PurapConstants.PaymentRequestStatuses.INITIATE)) {
            flags.setCanSave(false);
            flags.setCanClose(false);
            flags.setCanCancel(true);
            flags.setCanDisapprove(false);
            // If there was a way to add custom flags for our new buttons, we could avoid having the logic in jsp pag and have it here
            //flags.setCanContinue(true);

        }
        else {
            if (!getCurrentRouteLevels(workflowDocument).contains(NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW.getName())) {
                flags.setCanDisapprove(false);
            }

            PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(paymentRequestDocument, user);

            flags.setCanApprove(preqDocAuth.canApprove());
            flags.setCanCancel(preqDocAuth.canCancel());
            flags.setCanSave(preqDocAuth.canSave());
        }

        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
        this.setAnnotateFlag(flags);

        return flags;
    }

}