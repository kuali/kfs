/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PurchaseOrderEditMode;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;


public class PurchaseOrderDocumentPresentationController extends PurchasingAccountsPayableDocumentPresentationController {

    @Override
    public boolean canEdit(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;

        if (!PurchaseOrderStatuses.APPDOC_IN_PROCESS.equals(poDocument.getApplicationDocumentStatus()) &&
                !PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT.equals(poDocument.getApplicationDocumentStatus()) &&
                !PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR.equals(poDocument.getApplicationDocumentStatus()) &&
                !PurchaseOrderStatuses.APPDOC_QUOTE.equals(poDocument.getApplicationDocumentStatus()) &&
                !PurchaseOrderStatuses.APPDOC_AWAIT_PURCHASING_REVIEW.equals(poDocument.getApplicationDocumentStatus()) &&
                !PurchaseOrderStatuses.APPDOC_AWAIT_NEW_UNORDERED_ITEM_REVIEW.equals(poDocument.getApplicationDocumentStatus()) &&
                !PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS.equals(poDocument.getApplicationDocumentStatus())) {
            return false;
        }
        return super.canEdit(document);
    }

    @Override
    public boolean canFyi(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) document;
        if (PurchaseOrderStatuses.APPDOC_PENDING_PRINT.equals(poDocument.getApplicationDocumentStatus())) {
            return false;
        }
        return super.canFyi(document);
    }

    @Override
    public boolean canCancel(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;

        if (poDocument.isPendingSplit() || poDocument.getAssigningSensitiveData()) {
            return false;
        }

        return super.canCancel(document);
    }

    @Override
    public boolean canClose(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;

        if (poDocument.isPendingSplit() || poDocument.getAssigningSensitiveData()) {
            return false;
        }

        return super.canClose(document);
    }

    @Override
    public boolean canReload(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;

        if (poDocument.isPendingSplit() || poDocument.getAssigningSensitiveData()) {
            return false;
        }

        return super.canReload(document);
    }

    @Override
    public boolean canSave(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;

        if (poDocument.isPendingSplit() || poDocument.getAssigningSensitiveData()) {
            return false;
        }

        return super.canSave(document);
    }

    @Override
    public boolean canRoute(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        String statusCode = poDocument.getApplicationDocumentStatus();

        if (StringUtils.equals(statusCode, PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT) || 
                StringUtils.equals(statusCode, PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR) || 
                StringUtils.equals(statusCode, PurchaseOrderStatuses.APPDOC_QUOTE)) {
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
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        
        WorkflowDocument workflowDocument = poDocument.getFinancialSystemDocumentHeader().getWorkflowDocument();
        
        String statusCode = poDocument.getApplicationDocumentStatus();

        editModes.add(PurchaseOrderEditMode.ASSIGN_SENSITIVE_DATA);

        //if the ENABLE_COMMODITY_CODE_IND system parameter is Y then add this edit mode so that the commodity code fields would display on the document.
        boolean enableCommodityCode = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_COMMODITY_CODE_IND);
        if (enableCommodityCode) {
            editModes.add(PurchaseOrderEditMode.ENABLE_COMMODITY_CODE);
        }

        if (canFirstTransmitPrintPo(poDocument)) {
            editModes.add(PurchaseOrderEditMode.PRINT_PURCHASE_ORDER);
        }

        if (canPreviewPrintPo(poDocument)) {
            editModes.add(PurchaseOrderEditMode.PREVIEW_PRINT_PURCHASE_ORDER);
        }

        if (canResendCxml(poDocument)) {
            editModes.add(PurchaseOrderEditMode.RESEND_PURCHASE_ORDER);
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
                (PurchaseOrderStatuses.APPDOC_IN_PROCESS.equals(statusCode) || 
                        PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR.equals(statusCode) ||
                        PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT.equals(statusCode) ||
                        PurchaseOrderStatuses.APPDOC_QUOTE.equals(statusCode) ||
                        PurchaseOrderStatuses.APPDOC_AWAIT_PURCHASING_REVIEW.equals(statusCode))) {
            editModes.add(PurchaseOrderEditMode.ALLOW_POSTING_YEAR_ENTRY);
        }

        // check if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);                
        if (salesTaxInd) {
            editModes.add(PurapAuthorizationConstants.PURAP_TAX_ENABLED);

            if (poDocument.isUseTaxIndicator()) {
                // if use tax, don't allow editing of tax fields
                editModes.add(PurchaseOrderEditMode.LOCK_TAX_AMOUNT_ENTRY);
            }
            else {
                // display the "clear all taxes" button if doc is not using use tax
                editModes.add(PurchaseOrderEditMode.CLEAR_ALL_TAXES);
            }
        }

        // set display mode for Receiving Address section according to parameter value
        boolean displayReceivingAddress = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_RECEIVING_ADDRESS_IND);                
        if (displayReceivingAddress) {
            editModes.add(PurchaseOrderEditMode.DISPLAY_RECEIVING_ADDRESS);
        }

        // PRE_ROUTE_CHANGEABLE mode is used for fields that are editable only before PO is routed
        // for ex, contract manager, manual status change, and APPDOC_QUOTE etc
        //if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
        if (PurchaseOrderStatuses.APPDOC_IN_PROCESS.equals(statusCode) || 
                PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR.equals(statusCode) ||
                PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT.equals(statusCode) ||
                PurchaseOrderStatuses.APPDOC_QUOTE.equals(statusCode)) {
            editModes.add(PurchaseOrderEditMode.PRE_ROUTE_CHANGEABLE);
        }

        // INTERNAL PURCHASING ROUTE LEVEL - Approvers can edit full detail on Purchase Order except they cannot change the CHART/ORG.
        if (poDocument.isDocumentStoppedInRouteNode(PurapConstants.PurchaseOrderStatuses.NODE_CONTRACT_MANAGEMENT)) {
            editModes.add(PurchaseOrderEditMode.LOCK_INTERNAL_PURCHASING_ENTRY);
        }

        //FIXME figure out how to get this to work
//      /**
//      * CONTRACTS & GRANTS ROUTE LEVEL, BUDGET OFFICE ROUTE LEVEL, VENDOR TAX ROUTE LEVEL, DOCUMENT TRANSMISSION ROUTE LEVEL,
//      * and Adhoc - Approvers in these route levels cannot edit any detail on PO.
//      */
//      else {
//      // VIEW_ENTRY that is already being set is sufficient, but need to remove FULL_ENTRY
//      editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
//      }

        // Set display mode for Split PO.
        if (poDocument.isPendingSplit()) {
            editModes.add(PurchaseOrderEditMode.SPLITTING_ITEM_SELECTION);
        }

        return editModes;
    }

    /**
     * Determines whether to display the button to print the pdf for the first time transmit. 
     * Conditions: PO status is Pending Print or the transmission method is changed to PRINT during the amendment. 
     * 
     * @return boolean true if the print first transmit button can be displayed.
     */
    protected boolean canFirstTransmitPrintPo(PurchaseOrderDocument poDocument) {
        // status shall be Pending Print, or the transmission method is changed to PRINT during amendment, 
        boolean can = PurchaseOrderStatuses.APPDOC_PENDING_PRINT.equals(poDocument.getApplicationDocumentStatus());
        if (!can) {
            can = PurchaseOrderStatuses.APPDOC_OPEN.equals(poDocument.getApplicationDocumentStatus());
            can = can && poDocument.getFinancialSystemDocumentHeader().getWorkflowDocument().isFinal();
            can = can && poDocument.getPurchaseOrderLastTransmitTimestamp() == null;
            can = can && PurapConstants.POTransmissionMethods.PRINT.equals(poDocument.getPurchaseOrderTransmissionMethodCode());
        }

        return can;
    }

    /**
     * Determines whether to display the print preview button for the first time transmit. Conditions are:
     * available while the document is saved or enroute;
     * available for only a certain number of PO transmission types which are stored in a parameter (default to PRIN and FAX)
     * 
     * @return boolean true if the preview print button can be displayed.
     */
    protected boolean canPreviewPrintPo(PurchaseOrderDocument poDocument) {
        // PO is saved or enroute
        boolean can = poDocument.getFinancialSystemDocumentHeader().getWorkflowDocument().isSaved() || poDocument.getFinancialSystemDocumentHeader().getWorkflowDocument().isEnroute();

        // transmission method must be one of those specified by the parameter
        if (can) {
            List<String> methods = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(PurchaseOrderDocument.class, PurapParameterConstants.PURAP_PO_PRINT_PREVIEW_TRANSMISSION_METHOD_TYPES) );
            String method = poDocument.getPurchaseOrderTransmissionMethodCode();
            can = (methods == null || methods.contains(method));
        }

        return can;
    }

    /**
     * Determines whether to display the resend po button for the purchase order document.
     * Conditions: PO status must be error sending cxml and must be current and not pending.
     * 
     * @return boolean true if the resend po button shall be displayed.
     */
    protected boolean canResendCxml(PurchaseOrderDocument poDocument) {
        // check PO status etc
        boolean can = PurchaseOrderStatuses.APPDOC_CXML_ERROR.equals(poDocument.getApplicationDocumentStatus());
        can = can && poDocument.isPurchaseOrderCurrentIndicator() && !poDocument.isPendingActionIndicator();

        return can;
    }
}
