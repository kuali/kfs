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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.GroupNotFoundException;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiGroupService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the PO document.
 */
public class PurchaseOrderDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#hasInitiateAuthorization(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Override
    public boolean hasInitiateAuthorization(Document document, UniversalUser user) {
        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(PurchaseOrderDocument.class, PurapParameterConstants.Workgroups.PURAP_DOCUMENT_PO_INITIATE_ACTION);
        try {
            return SpringContext.getBean(KualiGroupService.class).getByGroupName(authorizedWorkgroup).hasMember(user);
        }
        catch (GroupNotFoundException e) {
            throw new RuntimeException("Workgroup " + authorizedWorkgroup + " not found", e);
        }
    }

    /**
     * This is essentially the same getEditMode as in DocumentAuthorizerBase.java. In AccountingDocumentAuthorizerBase.java, which
     * is currently the superclass of this class, this method is being overriden. Unfortunately it will return view only edit mode
     * if the initiator of the document is different than the current user. Currently the initiators of Purchase Order Document are
     * all "Kuali System User" which is different than the users that we use to log in. Therefore here we have to re-override the
     * getEditMode to prevent the problem where the fields appear as read-only. There has been an addition to this method, which at
     * this point I'm not sure whether there would be any cases where the Purchase Order Document would have status "RETR". If so,
     * then when the status code is "RETR" (retransmit), the edit mode should be set to displayRetransmitTab because we want to hide
     * the other tabs and display the retransmit tab when the user clicks on the Retransmit button (is that what we want ?)
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Override
    public Map getEditMode(Document d, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = new HashMap();
        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = d.getDocumentHeader().getWorkflowDocument();        
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) d;
        
        //By default lock cams tab
        editModeMap.put(PurapAuthorizationConstants.CamsEditMode.LOCK_CAMS_ENTRY, "TRUE");
        
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved() || workflowDocument.stateIsEnroute()) {
            //users cannot edit vendor name if the vendor has been selected from the database
            if (ObjectUtils.isNotNull(poDocument.getVendorHeaderGeneratedIdentifier())) {
                editModeMap.put(PurapAuthorizationConstants.PurchaseOrderEditMode.LOCK_VENDOR_ENTRY, "TRUE");
            }

            //if not B2B, users can edit the posting year if within a given amount of time set in a parameter
            if (!PurapConstants.RequisitionSources.B2B.equals(poDocument.getRequisitionSourceCode())) {
                if (SpringContext.getBean(PurapService.class).allowEncumberNextFiscalYear() && 
                        (PurapConstants.PurchaseOrderStatuses.IN_PROCESS.equals(poDocument.getStatusCode()) ||
                        PurapConstants.PurchaseOrderStatuses.WAITING_FOR_VENDOR.equals(poDocument.getStatusCode()) ||
                        PurapConstants.PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT.equals(poDocument.getStatusCode()) ||
                        PurapConstants.PurchaseOrderStatuses.QUOTE.equals(poDocument.getStatusCode()) ||
                        PurapConstants.PurchaseOrderStatuses.AWAIT_PURCHASING_REVIEW.equals(poDocument.getStatusCode()))) {
                    editModeMap.put(PurapAuthorizationConstants.PurchaseOrderEditMode.ALLOW_POSTING_YEAR_ENTRY, "TRUE");
                }
            }
            
        }        
        
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(d, user)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
            
            String purchasingGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_PURCHASING);
            if (user.isMember(purchasingGroup)) {
                // PRE_ROUTE_CHANGEABLE mode is used for fields that are editable only before PO is routed
                // for ex, contract manager, manual status change, and quote etc
                editModeMap.put(PurapAuthorizationConstants.PurchaseOrderEditMode.PRE_ROUTE_CHANGEABLE, "TRUE");

                //if user is part of the purchasing group, allow edit while in process
                editModeMap.remove(PurapAuthorizationConstants.CamsEditMode.LOCK_CAMS_ENTRY);
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            List currentRouteLevels = getCurrentRouteLevels(workflowDocument);

            // Unlock Capital Assets tag during Amendment.
            if (PurapConstants.PurchaseOrderStatuses.CHANGE_IN_PROCESS.equals(poDocument.getStatusCode())) {
                String purchasingGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_PURCHASING);
                if (user.isMember(purchasingGroup)) {
                    editModeMap.remove(PurapAuthorizationConstants.CamsEditMode.LOCK_CAMS_ENTRY);
                }
            }
            
            /**
             * INTERNAL PURCHASING ROUTE LEVEL - Approvers can edit full detail on Purchase Order except they cannot change the
             * CHART/ORG.
             */
            if (((PurchaseOrderDocument) d).isDocumentStoppedInRouteNode(PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum.INTERNAL_PURCHASING_REVIEW)) {
                // FULL_ENTRY allowed; also set internal purchasing lock
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
                editModeMap.put(PurapAuthorizationConstants.PurchaseOrderEditMode.LOCK_INTERNAL_PURCHASING_ENTRY, "TRUE");

                //if user is part of the purchasing group, allow edit while awaiting internal purchasing review
                String purchasingGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_PURCHASING);
                if (user.isMember(purchasingGroup)) {
                    editModeMap.remove(PurapAuthorizationConstants.CamsEditMode.LOCK_CAMS_ENTRY);
                }
            }

            /**
             * CONTRACTS & GRANTS ROUTE LEVEL, BUDGET OFFICE ROUTE LEVEL, VENDOR TAX ROUTE LEVEL, DOCUMENT TRANSMISSION ROUTE LEVEL,
             * and Adhoc - Approvers in these route levels cannot edit any detail on PO.
             */
            else {
                // VIEW_ENTRY that is already being set is sufficient, but need to remove FULL_ENTRY
                editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
            }
        }
        editModeMap.put(editMode, "TRUE");
        
        // Set display modes for Receiving Address according to its parameter value. 
        String paramName = PurapParameterConstants.ENABLE_RECEIVING_ADDRESS_IND;
        String paramValue = SpringContext.getBean(KualiConfigurationService.class).getParameterValue("KFS-PURAP", "Document", paramName);
        editMode = PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RECEIVING_ADDRESS;
        if (paramValue.equals("Y")) 
            editModeMap.put(editMode, "TRUE");
        
        // Set display mode for Split PO.
        if(poDocument.isPendingSplit()) {
            editModeMap.put(PurapAuthorizationConstants.PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION, "TRUE");
        }
                       
        return editModeMap;
    }

    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        PurchaseOrderDocument po = (PurchaseOrderDocument) document;
        String statusCode = po.getStatusCode();

        if ((StringUtils.equals(statusCode, PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT)) || (StringUtils.equals(statusCode, PurchaseOrderStatuses.WAITING_FOR_VENDOR)) || StringUtils.equals(statusCode, PurchaseOrderStatuses.QUOTE)) {
            flags.setCanRoute(false);
        }
        else if (PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.values().contains(statusCode)) {
            if (SpringContext.getBean(PurApWorkflowIntegrationService.class).isActionRequestedOfUserAtNodeName(po.getDocumentNumber(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), GlobalVariables.getUserSession().getFinancialSystemUser())) {
                /*
                 * code below for overriding workflow buttons has to do with hiding the workflow buttons but still allowing the
                 * actions... this is needed because document service calls this method (getDocumentActionFlags) before it will
                 * allow a workflow action to be performed
                 */
                if (ObjectUtils.isNotNull(po.getOverrideWorkflowButtons()) && (po.getOverrideWorkflowButtons())) {
                    /*
                     * if document is in pending transmission status and current user has document transmission action request then
                     * assume that the transmit button/action whatever it might be will take associated workflow action for user
                     * automatically
                     */
                    flags.setCanApprove(false);
                    flags.setCanDisapprove(false);
                    flags.setCanAcknowledge(false);
                    flags.setCanFYI(false);
                }
            }
        }
        if (po.isPendingSplit()) {
            flags.setCanRoute(false);
            flags.setCanSave(false);
            flags.setCanReload(false);
            flags.setCanClose(false);
            flags.setCanCancel(false);
        }
        if (po.isDocumentStoppedInRouteNode(NodeDetailEnum.INTERNAL_PURCHASING_REVIEW)) {
            flags.setCanSave(true);
            // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
            this.setAnnotateFlag(flags);
        }

        return flags;
    }
}
