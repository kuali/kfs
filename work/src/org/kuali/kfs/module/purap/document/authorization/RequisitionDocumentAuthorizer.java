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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the Requisition document.
 */
public class RequisitionDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#hasInitiateAuthorization(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Override
    public boolean hasInitiateAuthorization(Document document, UniversalUser user) {
        // anyone with access to the system can complete a REQ document
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.UniversalUser, java.util.List, java.util.List)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = super.getEditMode(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        RequisitionDocument reqDocument = (RequisitionDocument) document;
        FinancialSystemUser kfsUser = SpringContext.getBean(FinancialSystemUserService.class).convertUniversalUserToFinancialSystemUser(user);

        //by default lock cams tab
        editModeMap.put(PurapAuthorizationConstants.CamsEditMode.LOCK_CAMS_ENTRY, "TRUE");

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
            
            //If not routed, anyone can edit cams data
            editModeMap.remove(PurapAuthorizationConstants.CamsEditMode.LOCK_CAMS_ENTRY);
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
                
                //if enroute, only content approvers can edit
                if(workflowDocument.isApprovalRequested()){
                    editModeMap.remove(PurapAuthorizationConstants.CamsEditMode.LOCK_CAMS_ENTRY);
                }

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
        String paramValue = SpringContext.getBean(KualiConfigurationService.class).getParameterValue("KFS-PURAP", "Document", paramName);
        String editMode = PurapAuthorizationConstants.RequisitionEditMode.DISPLAY_RECEIVING_ADDRESS;
        if (paramValue.equals("Y") || paramValue.equals("y")) 
            editModeMap.put(editMode, "TRUE");
        paramName = PurapParameterConstants.ENABLE_ADDRESS_TO_VENDOR_SELECTION_IND;
        paramValue = SpringContext.getBean(KualiConfigurationService.class).getParameterValue("KFS-PURAP", "Requisition", paramName);
        editMode = PurapAuthorizationConstants.RequisitionEditMode.LOCK_ADDRESS_TO_VENDOR;
        if (paramValue.equals("N") || paramValue.equals("n")) 
            editModeMap.put(editMode, "TRUE");
                               
        //if full entry, and not use tax, allow editing
        if(editModeMap.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY) && reqDocument.isUseTaxIndicator() == false){
            editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.TAX_AMOUNT_CHANGEABLE, "TRUE");
        }
        
        //See if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter("KFS-PURAP", "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);                
        if(salesTaxInd){
            editModeMap.put(PurapAuthorizationConstants.PURAP_TAX_ENABLED, "TRUE");
        }
        
        return editModeMap;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
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
