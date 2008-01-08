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

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * Document Authorizer for the Requisition document.
 */
public class RequisitionDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#hasInitiateAuthorization(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public boolean hasInitiateAuthorization(Document document, UniversalUser user) {
        // anyone with access to the system can complete a REQ document
        return true;
    }

    /**
     * @see org.kuali.kfs.document.authorization.AccountingDocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser, java.util.List, java.util.List)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = super.getEditMode(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        RequisitionDocument reqDocument = (RequisitionDocument) document;

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved() || workflowDocument.stateIsEnroute()) {
            if (ObjectUtils.isNotNull(reqDocument.getVendorHeaderGeneratedIdentifier())) {
                editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.LOCK_VENDOR_ENTRY, "TRUE");
            }
        }

        if (workflowDocument.stateIsEnroute()) {
            List<String> currentRouteLevels = getCurrentRouteLevels(workflowDocument);
            String editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;

            /**
             * CONTENT ROUTE LEVEL - Approvers can edit full detail on Requisition except they cannot change the CHART/ORG.
             */
            if (reqDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW)) {
                // FULL_ENTRY will be set by super which is fine; also set content lock
                editMode = PurapAuthorizationConstants.RequisitionEditMode.LOCK_CONTENT_ENTRY;
            }

            /**
             * FISCAL OFFICER ROUTE LEVEL - Approvers can edit only the accounting lines that they own and no other detail on REQ.
             */
            else if (reqDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.ACCOUNT_REVIEW)) {
                List lineList = new ArrayList();
                for (Iterator iter = reqDocument.getItems().iterator(); iter.hasNext();) {
                    RequisitionItem item = (RequisitionItem) iter.next();
                    lineList.addAll(item.getSourceAccountingLines());
                    // If FO has deleted the last accounting line for an item, set entry mode to full so they can add another one
                    
                    if (item.getSourceAccountingLines().size() == 0) {
                        editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
                        editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.ALLOW_ITEM_ENTRY, item.getItemIdentifier());
                    }
                }

                if (userOwnsAnyAccountingLine((ChartUser) user.getModuleUser(ChartUser.MODULE_ID), lineList)) {
                    // remove FULL_ENTRY because FO cannot edit rest of doc; only their own acct lines
                    editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
                    editMode = AuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY;
                }
            }

            /**
             * SEP of DUTIES ROUTE LEVEL - Approvers can only approve or disapprove.
             */
            else if (reqDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.SEPARATION_OF_DUTIES_REVIEW)) {
                editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
                editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
            }

            /**
             * SUB-ACCOUNT ROUTE LEVEL - Approvers cannot edit any detail on REQ. BASE ORG REVIEW ROUTE LEVEL - Approvers cannot
             * edit any detail on REQ. SEPARATION OF DUTIES ROUTE LEVEL - Approvers cannot edit any detail on REQ.
             */
            else {
                // VIEW_ENTRY that is already being set is sufficient.
            }

            editModeMap.put(editMode, "TRUE");
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

        RequisitionDocument requisitionDocument = (RequisitionDocument) document;
        if (requisitionDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW)) {
            flags.setCanSave(true);
            // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
            this.setAnnotateFlag(flags);
        }

        return flags;
    }

}