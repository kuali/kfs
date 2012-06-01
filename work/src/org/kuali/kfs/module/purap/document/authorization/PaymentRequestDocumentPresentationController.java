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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PaymentRequestEditMode;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemWorkflowHelperService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;


public class PaymentRequestDocumentPresentationController extends PurchasingAccountsPayableDocumentPresentationController {

    
    @Override
    public boolean canSave(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        
        if (StringUtils.equals(paymentRequestDocument.getApplicationDocumentStatus(), PaymentRequestStatuses.APPDOC_INITIATE)) {
            return false;
        }

        if (canEditPreExtraction(paymentRequestDocument)) {
            return true;
        }
        
        return super.canSave(document);
    }
    
    @Override
    public boolean canReload(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;

        if (StringUtils.equals(paymentRequestDocument.getApplicationDocumentStatus(), PaymentRequestStatuses.APPDOC_INITIATE)) {
            return false;
        }
        
        if (canEditPreExtraction(paymentRequestDocument)) {
            return true;
        }

        return super.canReload(document);
    }

    @Override
    public boolean canCancel(Document document) {
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
    public boolean canDisapprove(Document document) {
        //disapprove is never allowed for PREQ
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean canEdit(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        boolean fullDocEntryCompleted = SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequestDocument);

        // if the hold or cancel indicator is true, don't allow editing
        if (paymentRequestDocument.isHoldIndicator() || paymentRequestDocument.isPaymentRequestedCancelIndicator()) {
            return false;
        }
        if (fullDocEntryCompleted) {
            //  after fullDocEntry is completed, only fiscal officer reviewers can edit
            if (paymentRequestDocument.isDocumentStoppedInRouteNode(PaymentRequestStatuses.NODE_ACCOUNT_REVIEW)) {
                return true;
            }
            return false;
        } else {
            //before fullDocEntry is completed, document can be edited (could be preroute or enroute)
            return true;
        }
    }

    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        
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
        
        if (canProcessorInit(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.DISPLAY_INIT_TAB);
        }
        
        if (ObjectUtils.isNotNull(paymentRequestDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(PaymentRequestEditMode.LOCK_VENDOR_ENTRY);
        }
        
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.FULL_DOCUMENT_ENTRY_COMPLETED);
        }
        else if (ObjectUtils.isNotNull(paymentRequestDocument.getPurchaseOrderDocument()) && PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN.equals(paymentRequestDocument.getPurchaseOrderDocument().getApplicationDocumentStatus())) {
            editModes.add(PaymentRequestEditMode.ALLOW_CLOSE_PURCHASE_ORDER);
        }

        //FIXME hjs: alter to restrict what AP shouldn't be allowed to edit
        if (canEditPreExtraction(paymentRequestDocument)) {
            editModes.add(PaymentRequestEditMode.EDIT_PRE_EXTRACT);
        }

        // See if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PaymentRequestEditMode.PURAP_TAX_ENABLED);

            if (paymentRequestDocument.isUseTaxIndicator()) {
                // if use tax, don't allow editing of tax fields
                editModes.add(PaymentRequestEditMode.LOCK_TAX_AMOUNT_ENTRY);
            }
            else {
                // display the "clear all taxes" button if doc is not using use tax
                editModes.add(PaymentRequestEditMode.CLEAR_ALL_TAXES);
                
            }
        }

        // tax area tab is editable while waiting for tax review
        if (paymentRequestDocument.isDocumentStoppedInRouteNode(PaymentRequestStatuses.NODE_VENDOR_TAX_REVIEW)) {
            editModes.add(PaymentRequestEditMode.TAX_AREA_EDITABLE);
        }
        /*
        if (PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals(paymentRequestDocument.getStatusCode())) {
            editModes.add(PaymentRequestEditMode.TAX_AREA_EDITABLE);
        }
        */
        
        // the tax tab is viewable to everyone after tax is approved
        if (PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED.equals(paymentRequestDocument.getApplicationDocumentStatus()) &&
                // if and only if the preq has gone through tax review would TaxClassificationCode be non-empty
                !StringUtils.isEmpty(paymentRequestDocument.getTaxClassificationCode())) {
            editModes.add(PaymentRequestEditMode.TAX_INFO_VIEWABLE);
        }

        if (paymentRequestDocument.isDocumentStoppedInRouteNode(PaymentRequestStatuses.NODE_ACCOUNT_REVIEW)) {
            // remove FULL_ENTRY because FO cannot edit rest of doc; only their own acct lines
            editModes.add(PaymentRequestEditMode.RESTRICT_FISCAL_ENTRY);

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

        // Remove editBank edit mode if the document has been extracted
        if (paymentRequestDocument.isExtracted()) {
            editModes.remove(KFSConstants.BANK_ENTRY_EDITABLE_EDITING_MODE);
        }

        return editModes;
    }

    protected boolean canProcessorInit(PaymentRequestDocument paymentRequestDocument) {
        // if Payment Request is in INITIATE status or NULL returned from getAppDocStatus
        String status = paymentRequestDocument.getApplicationDocumentStatus();
        if (StringUtils.equals(status, PaymentRequestStatuses.APPDOC_INITIATE)) {
            return true;
        }
         return false;
    }
    
    
    protected boolean canProcessorCancel(PaymentRequestDocument paymentRequestDocument) {
        // if Payment Request is in INITIATE status, user cannot cancel doc
        if (canProcessorInit(paymentRequestDocument)) {
            return false;
        }
        
        String docStatus = paymentRequestDocument.getApplicationDocumentStatus();
        boolean requestCancelIndicator = paymentRequestDocument.getPaymentRequestedCancelIndicator();
        boolean holdIndicator = paymentRequestDocument.isHoldIndicator();        
        boolean extracted = paymentRequestDocument.isExtracted();
        
        boolean preroute = 
            PaymentRequestStatuses.APPDOC_IN_PROCESS.equals(docStatus) || 
            PaymentRequestStatuses.APPDOC_AWAITING_ACCOUNTS_PAYABLE_REVIEW.equals(docStatus);
        boolean enroute = 
            PaymentRequestStatuses.APPDOC_AWAITING_SUB_ACCT_MGR_REVIEW.equals(docStatus) ||
            PaymentRequestStatuses.APPDOC_AWAITING_FISCAL_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.APPDOC_AWAITING_ORG_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW.equals(docStatus);
        boolean postroute = 
            PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED.equals(docStatus) || 
            PaymentRequestStatuses.APPDOC_AUTO_APPROVED.equals(docStatus);
        
        boolean can = false;
        if (PaymentRequestStatuses.STATUSES_PREROUTE.contains(docStatus)) {
            can = true;
        }
        else if (PaymentRequestStatuses.STATUSES_ENROUTE.contains(docStatus)) {
            can = requestCancelIndicator;
        }
        else if (PaymentRequestStatuses.STATUSES_POSTROUTE.contains(docStatus)) {
            can = !requestCancelIndicator && !holdIndicator && !extracted;
        }

        return can;
    }

    protected boolean canManagerCancel(PaymentRequestDocument paymentRequestDocument) {
        // if Payment Request is in INITIATE status, user cannot cancel doc
        if (canProcessorInit(paymentRequestDocument)) {
            return false;
        }
        
        String docStatus = paymentRequestDocument.getApplicationDocumentStatus();
        boolean requestCancelIndicator = paymentRequestDocument.getPaymentRequestedCancelIndicator();
        boolean holdIndicator = paymentRequestDocument.isHoldIndicator();        
        boolean extracted = paymentRequestDocument.isExtracted();
        
        boolean preroute = 
            PaymentRequestStatuses.APPDOC_IN_PROCESS.equals(docStatus) || 
            PaymentRequestStatuses.APPDOC_AWAITING_ACCOUNTS_PAYABLE_REVIEW.equals(docStatus);
        boolean enroute = 
            PaymentRequestStatuses.APPDOC_AWAITING_SUB_ACCT_MGR_REVIEW.equals(docStatus) ||
            PaymentRequestStatuses.APPDOC_AWAITING_FISCAL_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.APPDOC_AWAITING_ORG_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW.equals(docStatus);
        boolean postroute = 
            PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED.equals(docStatus) || 
            PaymentRequestStatuses.APPDOC_AUTO_APPROVED.equals(docStatus);
        
        boolean can = false;
        if (PaymentRequestStatuses.STATUSES_PREROUTE.contains(docStatus) || 
                PaymentRequestStatuses.STATUSES_ENROUTE.contains(docStatus)) {
            can = true;
        }
        else if (PaymentRequestStatuses.STATUSES_POSTROUTE.contains(docStatus)) {
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
    protected boolean canHold(PaymentRequestDocument paymentRequestDocument) {
        boolean can = !paymentRequestDocument.isHoldIndicator() && !paymentRequestDocument.isPaymentRequestedCancelIndicator() && !paymentRequestDocument.isExtracted();
        if (can) {
            can = SpringContext.getBean(FinancialSystemWorkflowHelperService.class).isAdhocApprovalRequestedForPrincipal(paymentRequestDocument.getFinancialSystemDocumentHeader().getWorkflowDocument(), GlobalVariables.getUserSession().getPrincipalId());            
            can = can || !PaymentRequestStatuses.STATUSES_DISALLOWING_HOLD.contains(paymentRequestDocument.getApplicationDocumentStatus());
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
    protected boolean canRequestCancel(PaymentRequestDocument paymentRequestDocument) {
        boolean can = !paymentRequestDocument.isPaymentRequestedCancelIndicator() && !paymentRequestDocument.isHoldIndicator() && !paymentRequestDocument.isExtracted();
        if (can) {
            can = SpringContext.getBean(FinancialSystemWorkflowHelperService.class).isAdhocApprovalRequestedForPrincipal(paymentRequestDocument.getFinancialSystemDocumentHeader().getWorkflowDocument(), GlobalVariables.getUserSession().getPrincipalId());
            can = can || !PaymentRequestStatuses.STATUSES_DISALLOWING_REQUEST_CANCEL.contains(paymentRequestDocument.getApplicationDocumentStatus());
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
    protected boolean canRemoveHold(PaymentRequestDocument paymentRequestDocument) {
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
    protected boolean canRemoveRequestCancel(PaymentRequestDocument paymentRequestDocument) {
        return paymentRequestDocument.isPaymentRequestedCancelIndicator();
    }

    protected boolean canEditPreExtraction(PaymentRequestDocument paymentRequestDocument) {
        return (!paymentRequestDocument.isExtracted() &&
                !SpringContext.getBean(FinancialSystemWorkflowHelperService.class).isAdhocApprovalRequestedForPrincipal(paymentRequestDocument.getFinancialSystemDocumentHeader().getWorkflowDocument(), GlobalVariables.getUserSession().getPrincipalId()) &&
                !PurapConstants.PaymentRequestStatuses.CANCELLED_STATUSES.contains(paymentRequestDocument.getApplicationDocumentStatus()));
    }

}
