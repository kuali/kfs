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

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PurchaseOrderEditMode;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class PurchaseOrderDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

//FIXME hjs: do we need this??
//  else if (PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.values().contains(statusCode)) {
//  if (SpringContext.getBean(PurApWorkflowIntegrationService.class).isActionRequestedOfUserAtNodeName(po.getDocumentNumber(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), GlobalVariables.getUserSession().getPerson())) {
//      /*
//       * code below for overriding workflow buttons has to do with hiding the workflow buttons but still allowing the
//       * actions... this is needed because document service calls this method (getDocumentActionFlags) before it will
//       * allow a workflow action to be performed
//       */
//      if (ObjectUtils.isNotNull(po.getOverrideWorkflowButtons()) && (po.getOverrideWorkflowButtons())) {
//          /*
//           * if document is in pending transmission status and current user has document transmission action request then
//           * assume that the transmit button/action whatever it might be will take associated workflow action for user
//           * automatically
//           */
//          flags.setCanApprove(false);
//          flags.setCanDisapprove(false);
//          flags.setCanAcknowledge(false);
//          flags.setCanFYI(false);
//      }
//  }
//}

    
    @Override
    protected boolean canCancel(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        
        if (poDocument.isPendingSplit()) {
            return false;
        }

        return super.canSave(document);
    }

    @Override
    protected boolean canClose(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        
        if (poDocument.isPendingSplit()) {
            return false;
        }

        return super.canSave(document);
    }

    @Override
    protected boolean canReload(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        
        if (poDocument.isPendingSplit()) {
            return false;
        }

        return super.canSave(document);
    }

    @Override
    protected boolean canSave(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        
        if (poDocument.isPendingSplit()) {
            return false;
        }

        return super.canSave(document);
    }
    
    @Override
    protected boolean canRoute(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        String statusCode = poDocument.getStatusCode();

        if (StringUtils.equals(statusCode, PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT) || 
                StringUtils.equals(statusCode, PurchaseOrderStatuses.WAITING_FOR_VENDOR) || 
                StringUtils.equals(statusCode, PurchaseOrderStatuses.QUOTE)) {
            return false;
        }
        
        if (poDocument.isPendingSplit()) {
            return false;
        }

        return super.canRoute(document);
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;

        if (canFirstTransmitPrintPo(poDocument)) {
            editModes.add(PurchaseOrderEditMode.PRINT_PURCHASE_ORDER);
        }
        
        // if vendor has been selected from DB, certain vendor fields are not allowed to be edited
        if (ObjectUtils.isNotNull(poDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(PurchaseOrderEditMode.LOCK_VENDOR_ENTRY);
        }
        
        // if B2B purchase order, certain fields are not allowed to be edited
        if (RequisitionSources.B2B.equals(poDocument.getRequisitionSourceCode())) {
            editModes.add(PurchaseOrderEditMode.LOCK_B2B_ENTRY);
        }

        // if not B2B requisition, users can edit the posting year if within a given amount of time set in a parameter
        if (!RequisitionSources.B2B.equals(poDocument.getRequisitionSourceCode()) && 
                SpringContext.getBean(PurapService.class).allowEncumberNextFiscalYear() && 
                (PurchaseOrderStatuses.IN_PROCESS.equals(poDocument.getStatusCode()) || 
                        PurchaseOrderStatuses.WAITING_FOR_VENDOR.equals(poDocument.getStatusCode()) ||
                        PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT.equals(poDocument.getStatusCode()) ||
                        PurchaseOrderStatuses.QUOTE.equals(poDocument.getStatusCode()) ||
                        PurchaseOrderStatuses.AWAIT_PURCHASING_REVIEW.equals(poDocument.getStatusCode()))) {
            editModes.add(PurchaseOrderEditMode.ALLOW_POSTING_YEAR_ENTRY);
        }

        // if use tax, don't allow editing of tax fields
        if (poDocument.isUseTaxIndicator()) {
            editModes.add(PurapAuthorizationConstants.CreditMemoEditMode.CLEAR_ALL_TAXES);
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
            editModes.add(PurchaseOrderEditMode.DISPLAY_RECEIVING_ADDRESS);
        }
            
        // PRE_ROUTE_CHANGEABLE mode is used for fields that are editable only before PO is routed
        // for ex, contract manager, manual status change, and quote etc
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            editModes.add(PurchaseOrderEditMode.PRE_ROUTE_CHANGEABLE);
        }
        
        // INTERNAL PURCHASING ROUTE LEVEL - Approvers can edit full detail on Purchase Order except they cannot change the CHART/ORG.
        if (poDocument.isDocumentStoppedInRouteNode(PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum.INTERNAL_PURCHASING_REVIEW)) {
            editModes.add(PurchaseOrderEditMode.LOCK_INTERNAL_PURCHASING_ENTRY);
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
            editModes.add(PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);
        }
 
        
        return editModes;
    }

    /**
     * Determines whether to display the button to print the pdf for the first time transmit. 
     * Conditions: PO status is Pending Print or the transmission method is changed to PRINT during the amendment. 
     * User is either in Purchasing group or an action is requested of them for the document transmission route node.
     * 
     * @return boolean true if the print first transmit button can be displayed.
     */
    private boolean canFirstTransmitPrintPo(PurchaseOrderDocument purchaseOrderDocument) {
        // status shall be Pending Print, or the transmission method is changed to PRINT during amendment, 
        boolean can = PurchaseOrderStatuses.PENDING_PRINT.equals(purchaseOrderDocument.getStatusCode());
        if (!can) {
            can = PurchaseOrderStatuses.OPEN.equals(purchaseOrderDocument.getStatusCode());
            can = can && purchaseOrderDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal();
            can = can && purchaseOrderDocument.getPurchaseOrderLastTransmitTimestamp() == null;
            can = can && PurapConstants.POTransmissionMethods.PRINT.equals(purchaseOrderDocument.getPurchaseOrderTransmissionMethodCode());
        }
        
//        // user is either authorized or an action is requested of them for the document transmission route node, 
//        if (can) {
//            PurApWorkflowIntegrationService service = SpringContext.getBean(PurApWorkflowIntegrationService.class);
//            can = service.isActionRequestedOfUserAtNodeName(purchaseOrderDocument.getDocumentNumber(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), GlobalVariables.getUserSession().getPerson());
//            if (!can) {
//                DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(purchaseOrderDocument);
//                can = documentAuthorizer.isAuthorized(purchaseOrderDocument, PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.PRINT_PO, GlobalVariables.getUserSession().getPerson().getPrincipalId());               
//            }
//        }
//        
        return can;
    }

}
