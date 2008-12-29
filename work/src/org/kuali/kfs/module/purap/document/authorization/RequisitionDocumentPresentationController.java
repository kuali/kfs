/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.RequisitionEditMode;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;


public class RequisitionDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    @Override
    public Set<String> getEditModes(Document document) {
        RequisitionDocument reqDocument = (RequisitionDocument)document;
        Set<String> editModes = new HashSet<String>();

        // if vendor has been selected from DB, certain vendor fields are not allowed to be edited
        if (ObjectUtils.isNotNull(reqDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(RequisitionEditMode.LOCK_VENDOR_ENTRY);
        }
        
        // if B2B requisition, certain fields are not allowed to be edited
        if (RequisitionSources.B2B.equals(reqDocument.getRequisitionSourceCode())) {
            editModes.add(RequisitionEditMode.LOCK_B2B_ENTRY);
        }

        // if not B2B requisition, users can edit the posting year if within a given amount of time set in a parameter
        if (!RequisitionSources.B2B.equals(reqDocument.getRequisitionSourceCode()) && 
                SpringContext.getBean(PurapService.class).allowEncumberNextFiscalYear() && 
                (RequisitionStatuses.IN_PROCESS.equals(reqDocument.getStatusCode()) || 
                        RequisitionStatuses.AWAIT_CONTENT_REVIEW.equals(reqDocument.getStatusCode()) || 
                        RequisitionStatuses.AWAIT_FISCAL_REVIEW.equals(reqDocument.getStatusCode()))) {
            editModes.add(RequisitionEditMode.ALLOW_POSTING_YEAR_ENTRY);
        }

        //TODO add description of editmode (hjs)
        if (!reqDocument.isUseTaxIndicator() && 
                (RequisitionStatuses.IN_PROCESS.equals(reqDocument.getStatusCode()) || 
                        RequisitionStatuses.AWAIT_CONTENT_REVIEW.equals(reqDocument.getStatusCode()) || 
                        RequisitionStatuses.AWAIT_FISCAL_REVIEW.equals(reqDocument.getStatusCode()))) {
            editModes.add(PurapAuthorizationConstants.RequisitionEditMode.CLEAR_ALL_TAXES);
        }

//TODO hjs - how to handle FULL_ENTRY mode?
//        //if full entry, and not use tax, allow editing
//        if(editModeMap.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY) && !reqDocument.isUseTaxIndicator()){
//            editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.TAX_AMOUNT_CHANGEABLE, "TRUE");
//        }
        
        // check if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);                
        if(salesTaxInd){
            editModes.add(PurapAuthorizationConstants.PURAP_TAX_ENABLED);
        }

        // set display mode for Receiving Address section according to parameter value
        boolean displayReceivingAddress = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_RECEIVING_ADDRESS_IND);                
        if (displayReceivingAddress) {
            editModes.add(PurapAuthorizationConstants.RequisitionEditMode.DISPLAY_RECEIVING_ADDRESS);
        }
            
        // set display mode for Address to Vendor section according to parameter value 
        boolean lockAddressToVendor = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Requisition", PurapParameterConstants.ENABLE_ADDRESS_TO_VENDOR_SELECTION_IND);                
        if (lockAddressToVendor) {
            editModes.add(PurapAuthorizationConstants.RequisitionEditMode.LOCK_ADDRESS_TO_VENDOR);
        }

        // CONTENT ROUTE LEVEL - Approvers can edit full detail on Requisition except they cannot change the CHART/ORG.
        if (reqDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW)) {
            editModes.add(RequisitionEditMode.LOCK_CONTENT_ENTRY);
        }

//FIXME hjs-fix this so that FO's can't edit data besides the accts
         // FISCAL OFFICER ROUTE LEVEL - Approvers can edit only the accounting lines that they own and no other detail on REQ.
//        else if (reqDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.ACCOUNT_REVIEW)) {
//            List lineList = new ArrayList();
//            for (Iterator iter = reqDocument.getItems().iterator(); iter.hasNext();) {
//                RequisitionItem item = (RequisitionItem) iter.next();
//                lineList.addAll(item.getSourceAccountingLines());
//                // If FO has deleted the last accounting line for an item, set entry mode to full so they can add another one
//                
//                if (item.getSourceAccountingLines().size() == 0) {
//                    editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
//                    editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.ALLOW_ITEM_ENTRY, item.getItemIdentifier());
//                }
//            }
//
//            if (userOwnsAnyAccountingLine(kfsUser, lineList)) {
//                // remove FULL_ENTRY because FO cannot edit rest of doc; only their own acct lines
//                editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
//                editMode = KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY;
//            }
//            
//            editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.LOCK_ADDRESS_TO_VENDOR, "TRUE");
//        }

//         // SUB-ACCOUNT ROUTE LEVEL, BASE ORG REVIEW ROUTE LEVEL, SEPARATION OF DUTIES ROUTE LEVEL, and Adhoc - Approvers in these route levels cannot edit any detail on REQ.
//        else {
//            // VIEW_ENTRY that is already being set is sufficient, but need to remove FULL_ENTRY
//            editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);                
//        }
//        
//        editModeMap.put(editMode, "TRUE");

        return editModes;
    }

}
