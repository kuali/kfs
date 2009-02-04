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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PaymentRequestEditMode;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class PaymentRequestDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    
    @Override
    protected boolean canSave(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PaymentRequestStatuses.INITIATE)) {
            return false;
        }

        if (paymentRequestDocument.isExtracted()) {
            return false;
        }      
        
        return super.canSave(document);
    }

    @Override
    protected boolean canCancel(Document document) {
        //controlling the cancel button through getExtraButtons in PaymentRequestForm
        return false;
    }

    @Override
    public boolean canApprove(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        
        if (paymentRequestDocument.isPaymentRequestedCancelIndicator() || paymentRequestDocument.isHoldIndicator()) {
            return false;
        }
        
        return super.canApprove(document);
    }

    @Override
    protected boolean canDisapprove(Document document) {
        //disapprove is never allowed for PREQ
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canEdit(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;

        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequestDocument)) {
            return false;
        }

        // if the hold or cancel indicator is true, don't allow editing
        if (paymentRequestDocument.isHoldIndicator() || paymentRequestDocument.isPaymentRequestedCancelIndicator()) {
            return false;
        }

        if (paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isAdHocRequested()) {
            return false;
        }

        return super.canEdit(document);
    }

    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument)document;
        
        if (canProcessorCancel(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.ACCOUNTS_PAYABLE_PROCESSOR_CANCEL);
        }
        
        if (canManagerCancel(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.ACCOUNTS_PAYABLE_MANAGER_CANCEL);
        }
        
        if (canHold(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.HOLD);
        }

        if (canRequestCancel(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.REQUEST_CANCEL);
        }

        if (canRemoveHold(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.REMOVE_HOLD);
        }

        if (canRemoveRequestCancel(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.REMOVE_REQUEST_CANCEL);
        }

        if (paymentRequestDocument.getStatusCode().equals(PurapConstants.PaymentRequestStatuses.INITIATE)) {
            editModes.add(PaymentRequestEditMode.DISPLAY_INIT_TAB);
        }
        
        if (ObjectUtils.isNotNull(paymentRequestDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(PaymentRequestEditMode.LOCK_VENDOR_ENTRY);
        }
        
        // always show amount after full entry
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.SHOW_AMOUNT_ONLY);
        }
        else if (ObjectUtils.isNotNull(paymentRequestDocument.getPurchaseOrderDocument()) && PurapConstants.PurchaseOrderStatuses.OPEN.equals(paymentRequestDocument.getPurchaseOrderDocument().getStatusCode())) {
            editModes.add(PaymentRequestEditMode.ALLOW_CLOSE_PURCHASE_ORDER);
        }

        if (!paymentRequestDocument.isExtracted() && 
            !workflowDocument.isAdHocRequested() && 
            !paymentRequestDocument.isPaymentRequestedCancelIndicator()) {
            editModes.add(PaymentRequestEditMode.EDIT_PRE_EXTRACT);
        }

        // if use tax, don't allow editing of tax fields
        if (paymentRequestDocument.isUseTaxIndicator()) {
            editModes.add(PaymentRequestEditMode.CLEAR_ALL_TAXES);
            editModes.add(PaymentRequestEditMode.LOCK_TAX_AMOUNT_ENTRY);
        }

        // See if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PaymentRequestEditMode.PURAP_TAX_ENABLED);
        }

        // tax area tab editable while waiting for tax review
        if (PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals(paymentRequestDocument.getStatusCode())) {
            editModes.add(PaymentRequestEditMode.TAX_AREA_EDITABLE);
        }

        // after tax is approved, the tax tab is viewable to everyone
        if (PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(paymentRequestDocument.getStatusCode())) {
            editModes.add(PaymentRequestEditMode.TAX_INFO_VIEWABLE);
        }

        if (paymentRequestDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.ACCOUNT_REVIEW)) {
            // remove FULL_ENTRY because FO cannot edit rest of doc; only their own acct lines
            editModes.add(PaymentRequestEditMode.RESTRICT_FISCAL_ENTRY);

            // expense_entry was already added in super, add amount edit mode
            editModes.add(PaymentRequestEditMode.SHOW_AMOUNT_ONLY);

            // only do line item check if the hold/cancel indicator is false, otherwise document editing should be turned off.
            if (!paymentRequestDocument.isHoldIndicator() && !paymentRequestDocument.isPaymentRequestedCancelIndicator()) {
                List lineList = new ArrayList();
                for (Iterator iter = paymentRequestDocument.getItems().iterator(); iter.hasNext();) {
                    PaymentRequestItem item = (PaymentRequestItem) iter.next();
                    lineList.addAll(item.getSourceAccountingLines());
                    // If FO has deleted the last accounting line for an item, set entry mode to full so they can add another one
                    if (item.getItemType().isLineItemIndicator() && item.getSourceAccountingLines().size() == 0) {
                        editModes.add(KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY);
                    }
                }
            }
        }

        if (paymentRequestDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.VENDOR_TAX_REVIEW)) {
            editModes.add(PaymentRequestEditMode.TAX_AREA_EDITABLE);
        }

        // Remove editBank edit mode if the document has been extracted
        if (paymentRequestDocument.isExtracted()) {
            editModes.remove(KFSConstants.BANK_ENTRY_EDITABLE_EDITING_MODE);
        }

        return editModes;
    }


    private boolean canProcessorCancel(PaymentRequestDocument paymentRequestDocument) {
        // if Payment Request is in INITIATE status, user cannot cancel doc
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PaymentRequestStatuses.INITIATE)) {
            return false;
        }
        
        String docStatus = paymentRequestDocument.getStatusCode();
        boolean requestCancelIndicator = paymentRequestDocument.getPaymentRequestedCancelIndicator();
        boolean holdIndicator = paymentRequestDocument.isHoldIndicator();        
        boolean extracted = paymentRequestDocument.isExtracted();
        
        //FIXME hjs-cleanup: we should probably move this to the purapconstants like the other statuses
        boolean preroute = 
            PaymentRequestStatuses.IN_PROCESS.equals(docStatus) || 
            PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW.equals(docStatus);
        boolean enroute = 
            PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW.equals(docStatus) ||
            PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.AWAITING_ORG_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals(docStatus);
        boolean postroute = 
            PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(docStatus) || 
            PaymentRequestStatuses.AUTO_APPROVED.equals(docStatus);
        
        boolean can = false;
        if (preroute) {
            can = true;
        }
        else if (enroute) {
            can = requestCancelIndicator;
        }
        else if (postroute) {
            can = !requestCancelIndicator && !holdIndicator && !extracted;
        }

        return can;
    }

    private boolean canManagerCancel(PaymentRequestDocument paymentRequestDocument) {
        // if Payment Request is in INITIATE status, user cannot cancel doc
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PaymentRequestStatuses.INITIATE)) {
            return false;
        }
        
        String docStatus = paymentRequestDocument.getStatusCode();
        boolean requestCancelIndicator = paymentRequestDocument.getPaymentRequestedCancelIndicator();
        boolean holdIndicator = paymentRequestDocument.isHoldIndicator();        
        boolean extracted = paymentRequestDocument.isExtracted();
        
        //FIXME hjs-cleanup: we should probably move this to the purapconstants like the other statuses
        boolean preroute = 
            PaymentRequestStatuses.IN_PROCESS.equals(docStatus) || 
            PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW.equals(docStatus);
        boolean enroute = 
            PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW.equals(docStatus) ||
            PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.AWAITING_ORG_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals(docStatus);
        boolean postroute = 
            PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(docStatus) || 
            PaymentRequestStatuses.AUTO_APPROVED.equals(docStatus);
        
        boolean can = false;
        if (preroute || enroute) {
            can = true;
        }
        else if (postroute) {
            can = !requestCancelIndicator && !holdIndicator && !extracted;
        }

        return can;
    }

    /**
     * Determines whether the PaymentRequest Hold button shall be available. Conditions:
     * - Payment Request is not already on hold, and
     * - Payment Request is not already being requested to be canceled, and
     * - Payment Request has not already been extracted to PDP, and
     * - Payment Request status is not in the list of "STATUSES_DISALLOWING_HOLD" or document is being adhoc routed; and
     * 
     * @return True if the document state allows placing the Payment Request on hold.
     */
    private boolean canHold(PaymentRequestDocument paymentRequestDocument) {
        boolean can = !paymentRequestDocument.isHoldIndicator() && !paymentRequestDocument.isPaymentRequestedCancelIndicator() && !paymentRequestDocument.isExtracted();
        if (can) {
            can = paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isAdHocRequested();
            can = can || !PaymentRequestStatuses.STATUSES_DISALLOWING_HOLD.contains(paymentRequestDocument.getStatusCode());
        }
        
        return can;
    }

    /**
     * Determines whether the Request Cancel PaymentRequest button shall be available. Conditions:
     * - Payment Request is not already on hold, and
     * - Payment Request is not already being requested to be canceled, and
     * - Payment Request has not already been extracted to PDP, and
     * - Payment Request status is not in the list of "STATUSES_DISALLOWING_REQUEST_CANCEL" or document is being adhoc routed; and
     * 
     * @return True if the document state allows placing the request that the Payment Request be canceled.
     */
    private boolean canRequestCancel(PaymentRequestDocument paymentRequestDocument) {
        boolean can = !paymentRequestDocument.isPaymentRequestedCancelIndicator() && !paymentRequestDocument.isHoldIndicator() && !paymentRequestDocument.isExtracted();
        if (can) {
            can = paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isAdHocRequested();
            can = can || !PaymentRequestStatuses.STATUSES_DISALLOWING_REQUEST_CANCEL.contains(paymentRequestDocument.getStatusCode());
        }

        return can;
    }

    /**
     * Determines whether the Remove Hold button shall be available. Conditions:
     * - the hold indicator is set to true
     * 
     * Because the state of the Payment Request cannot be changed while the document is on hold, 
     * we should not have to check the state of the document to remove the hold.  
     * For example, the document should not be allowed to be approved or extracted while on hold.
     * 
     * @return True if the document state allows removing the Payment Request from hold.
     */
    private boolean canRemoveHold(PaymentRequestDocument paymentRequestDocument) {
        return paymentRequestDocument.isHoldIndicator();       
    }

    /**
     * Determines whether the Remove Request Cancel button shall be available. Conditions:
     * - the request cancel indicator is set to true;  and 
     *   
     * Because the state of the Payment Request cannot be changed while the document is set to request cancel, 
     * we should not have to check the state of the document to remove the request cancel.  
     * For example, the document should not be allowed to be approved or extracted while set to request cancel.
     *  
     * @return True if the document state allows removing a request that the Payment Request be canceled.
     */
    private boolean canRemoveRequestCancel(PaymentRequestDocument paymentRequestDocument) {
        return paymentRequestDocument.isPaymentRequestedCancelIndicator();
    }

}
