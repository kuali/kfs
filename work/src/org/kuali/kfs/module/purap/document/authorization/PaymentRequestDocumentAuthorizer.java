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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.PurapService;

/**
 * Document Authorizer for the PREQ document.
 */
public class PaymentRequestDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

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
        Map editModeMap = super.getEditMode(document, user, sourceAccountingLines, targetAccountingLines);
        PaymentRequestDocument preq = (PaymentRequestDocument) document;

        PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(preq);

        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        boolean fullDocumentEntryCompleted = SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((PaymentRequestDocument) document);

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(document, user)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
            // only allow full entry if status allows it

            if (!fullDocumentEntryCompleted) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }

            if (currentRouteLevels.contains(NodeDetailEnum.ACCOUNT_REVIEW.getName())) {
                editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
                // expense_entry was already added in super
                // add amount edit mode
                editMode = PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY;

                List lineList = new ArrayList();
                for (Iterator iter = preq.getItems().iterator(); iter.hasNext();) {
                    PaymentRequestItem item = (PaymentRequestItem) iter.next();
                    lineList.addAll(item.getSourceAccountingLines());
                    // If FO has deleted the last accounting line for an item, set entry mode to full so they can add another one
                    if (item.getItemType().isItemTypeAboveTheLineIndicator() && item.getSourceAccountingLines().size() == 0) {
                        editModeMap.remove(AuthorizationConstants.EditMode.VIEW_ONLY);
                        editModeMap.put(KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY, "TRUE");
                    }
                }
            }
            //This is for ad hoc approval
            if (preqDocAuth.isAdHocRequested()) {
                editMode = AuthorizationConstants.EditMode.VIEW_ONLY;
            }
        }

        editModeMap.put(editMode, "TRUE");

        
        // always show amount after full entry
        if (fullDocumentEntryCompleted) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY, "TRUE");
        }

        // make sure ap user can edit certain fields
        if (preqDocAuth.canEditPreExtractFields() && ! preqDocAuth.isAdHocRequested()) {
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

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (KFSConstants.SYSTEM_USER.equalsIgnoreCase(user.getPersonUserIdentifier())) {
            flags.setCanBlanketApprove(true);
        }

        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PurapConstants.PaymentRequestStatuses.INITIATE)) {
            flags.setCanSave(false);
            flags.setCanClose(true);
            flags.setCanCancel(false);
            flags.setCanDisapprove(false);
        }
        else {
            if (!getCurrentRouteLevels(workflowDocument).contains(NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW.getName()) ||
                 StringUtils.equals(paymentRequestDocument.getStatusCode(),PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW)) {
                flags.setCanDisapprove(false);
            }

            PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(paymentRequestDocument);

            flags.setCanApprove(preqDocAuth.canApprove());
            flags.setCanCancel(preqDocAuth.canCancel());
            flags.setCanSave(preqDocAuth.canSave());
        }

        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
        this.setAnnotateFlag(flags);

        return flags;
    }

}