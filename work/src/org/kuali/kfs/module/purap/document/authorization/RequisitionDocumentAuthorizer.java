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
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.kfs.bo.FinancialSystemUser;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.service.FinancialSystemUserService;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.PurapService;

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
        FinancialSystemUser kfsUser = SpringContext.getBean(FinancialSystemUserService.class).convertUniversalUserToFinancialSystemUser(user);

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved() || workflowDocument.stateIsEnroute()) {
            if (ObjectUtils.isNotNull(reqDocument.getVendorHeaderGeneratedIdentifier())) {
                editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.LOCK_VENDOR_ENTRY, "TRUE");
            }
            
            //if not a B2B REQ, users can edit the posting year if within a given amount of time set in a parameter
            if (!PurapConstants.RequisitionSources.B2B.equals(reqDocument.getRequisitionSourceCode())) {
                if (SpringContext.getBean(PurapService.class).allowEncumberNextFiscalYear() && 
                        (PurapConstants.RequisitionStatuses.IN_PROCESS.equals(reqDocument.getStatusCode()) ||
                         PurapConstants.RequisitionStatuses.AWAIT_CONTENT_REVIEW.equals(reqDocument.getStatusCode()) ||
                         PurapConstants.RequisitionStatuses.AWAIT_FISCAL_REVIEW.equals(reqDocument.getStatusCode()))) {
                    editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.ALLOW_POSTING_YEAR_ENTRY, "TRUE");
                }
            }
        }

        if (workflowDocument.stateIsEnroute()) {
            List<String> currentRouteLevels = getCurrentRouteLevels(workflowDocument);
            String editMode = KfsAuthorizationConstants.TransactionalEditMode.VIEW_ONLY;

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

                if (userOwnsAnyAccountingLine(kfsUser, lineList)) {
                    // remove FULL_ENTRY because FO cannot edit rest of doc; only their own acct lines
                    editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
                    editMode = KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY;
                }
                
                editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.LOCK_ADDRESS_TO_VENDOR, "TRUE");
            }

            /**
             * SUB-ACCOUNT ROUTE LEVEL, BASE ORG REVIEW ROUTE LEVEL, SEPARATION OF DUTIES ROUTE LEVEL, and Adhoc - Approvers in
             * these route levels cannot edit any detail on REQ.
             */
            else {
                // VIEW_ENTRY that is already being set is sufficient, but need to remove FULL_ENTRY
                editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
            }
            
            editModeMap.put(editMode, "TRUE");
        }

        // Set display modes for Receiving Address and Address to Vendor sections according to their parameter values. 
        String paramName = PurapParameterConstants.ENABLE_RECEIVING_ADDRESS_IND;
        String paramValue = SpringContext.getBean(KualiConfigurationService.class).getParameterValue("KFS-PA", "Document", paramName);
        String editMode = PurapAuthorizationConstants.RequisitionEditMode.DISPLAY_RECEIVING_ADDRESS;
        if (paramValue.equals("Y") || paramValue.equals("y")) 
            editModeMap.put(editMode, "TRUE");
        paramName = PurapParameterConstants.ENABLE_ADDRESS_TO_VENDOR_SELECTION_IND;
        paramValue = SpringContext.getBean(KualiConfigurationService.class).getParameterValue("KFS-PA", "Requisition", paramName);
        editMode = PurapAuthorizationConstants.RequisitionEditMode.LOCK_ADDRESS_TO_VENDOR;
        if (paramValue.equals("N") || paramValue.equals("n")) 
            editModeMap.put(editMode, "TRUE");
                       
        return editModeMap;
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
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