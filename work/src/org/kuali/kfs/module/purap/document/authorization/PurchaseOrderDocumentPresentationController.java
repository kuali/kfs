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
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.RequisitionEditMode;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class PurchaseOrderDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    @Override
    public Set<String> getEditModes(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        Set<String> editModes = new HashSet<String>();

        // if vendor has been selected from DB, certain vendor fields are not allowed to be edited
        if (ObjectUtils.isNotNull(poDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(RequisitionEditMode.LOCK_VENDOR_ENTRY);
        }
        
        // if B2B purchase order, certain fields are not allowed to be edited
        if (RequisitionSources.B2B.equals(poDocument.getRequisitionSourceCode())) {
            editModes.add(RequisitionEditMode.LOCK_B2B_ENTRY);
        }

        // if not B2B requisition, users can edit the posting year if within a given amount of time set in a parameter
        if (!RequisitionSources.B2B.equals(poDocument.getRequisitionSourceCode()) && 
                SpringContext.getBean(PurapService.class).allowEncumberNextFiscalYear() && 
                (PurchaseOrderStatuses.IN_PROCESS.equals(poDocument.getStatusCode()) || 
                        PurchaseOrderStatuses.WAITING_FOR_VENDOR.equals(poDocument.getStatusCode()) ||
                        PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT.equals(poDocument.getStatusCode()) ||
                        PurchaseOrderStatuses.QUOTE.equals(poDocument.getStatusCode()) ||
                        PurchaseOrderStatuses.AWAIT_PURCHASING_REVIEW.equals(poDocument.getStatusCode()))) {
            editModes.add(RequisitionEditMode.ALLOW_POSTING_YEAR_ENTRY);
        }

        //TODO add description of editmode (hjs)
        if (!poDocument.isUseTaxIndicator() && 
                (PurchaseOrderStatuses.IN_PROCESS.equals(poDocument.getStatusCode()) || 
                        PurchaseOrderStatuses.WAITING_FOR_VENDOR.equals(poDocument.getStatusCode()) ||
                        PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT.equals(poDocument.getStatusCode()) ||
                        PurchaseOrderStatuses.QUOTE.equals(poDocument.getStatusCode()) ||
                        PurchaseOrderStatuses.AWAIT_PURCHASING_REVIEW.equals(poDocument.getStatusCode()))) {
            editModes.add(PurapAuthorizationConstants.RequisitionEditMode.CLEAR_ALL_TAXES);
        }

        //if use tax, don't allow editing of tax fields
        if(poDocument.isUseTaxIndicator()){
            editModes.add(PurapAuthorizationConstants.PurchaseOrderEditMode.LOCK_TAX_AMOUNT_ENTRY);
        }
        
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
            
        // PRE_ROUTE_CHANGEABLE mode is used for fields that are editable only before PO is routed
        // for ex, contract manager, manual status change, and quote etc
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            editModes.add(PurapAuthorizationConstants.PurchaseOrderEditMode.PRE_ROUTE_CHANGEABLE);
        }
        
        // INTERNAL PURCHASING ROUTE LEVEL - Approvers can edit full detail on Purchase Order except they cannot change the CHART/ORG.
        if (poDocument.isDocumentStoppedInRouteNode(PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum.INTERNAL_PURCHASING_REVIEW)) {
            editModes.add(PurapAuthorizationConstants.PurchaseOrderEditMode.LOCK_INTERNAL_PURCHASING_ENTRY);
        }

        //FIXME figure out how to get this to work
//      /**
//      * CONTRACTS & GRANTS ROUTE LEVEL, BUDGET OFFICE ROUTE LEVEL, VENDOR TAX ROUTE LEVEL, DOCUMENT TRANSMISSION ROUTE LEVEL,
//      * and Adhoc - Approvers in these route levels cannot edit any detail on PO.
//      */
//     else {
//         // VIEW_ENTRY that is already being set is sufficient, but need to remove FULL_ENTRY
//         editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
//     }

        // Set display mode for Split PO.
        if (poDocument.isPendingSplit()) {
            editModes.add(PurapAuthorizationConstants.PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);
        }
 
        
        return editModes;
    }
}
