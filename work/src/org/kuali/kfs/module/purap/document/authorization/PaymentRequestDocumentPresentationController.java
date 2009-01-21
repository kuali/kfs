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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class PaymentRequestDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

//FIXME hjs KIM cleanup    move this to geteditmodes to remove the bank edit mode if extracted
//                //Set can edit bank to true if the document has not been extracted, for now without Kim (more changes when Kim is available).
//                if (!paymentRequestDocument.isExtracted()) {
//                    flags.setCanEditBank(true);
//                }

    
    
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

    //TODO try without this as super should be removing close when doc isnt' saved
//    @Override
//    protected boolean canClose(Document document) {
//        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
//        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PaymentRequestStatuses.INITIATE)) {
//            return false;
//        }
//        return super.canClose(document);
//    }

    
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
        
        //add state logic for when an AP processor can cancel the doc
        if (canProcessorCancel(paymentRequestDocument)) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.ACCOUNTS_PAYABLE_PROCESSOR_CANCEL);
        }
        
        //add state logic for when an AP manager can cancel the doc
        if (canManagerCancel(paymentRequestDocument)) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.ACCOUNTS_PAYABLE_MANAGER_CANCEL);
        }
        
        if (paymentRequestDocument.getStatusCode().equals(PurapConstants.PaymentRequestStatuses.INITIATE)) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB);
        }
        
        if (ObjectUtils.isNotNull(paymentRequestDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.LOCK_VENDOR_ENTRY);
        }
        
        // always show amount after full entry
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequestDocument)) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY);
        }
        else if (ObjectUtils.isNotNull(paymentRequestDocument.getPurchaseOrderDocument()) && PurapConstants.PurchaseOrderStatuses.OPEN.equals(paymentRequestDocument.getPurchaseOrderDocument().getStatusCode())) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.ALLOW_CLOSE_PURCHASE_ORDER);
        }

        if (!paymentRequestDocument.isExtracted() && 
            !workflowDocument.isAdHocRequested() && 
            !paymentRequestDocument.isPaymentRequestedCancelIndicator()) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_PRE_EXTRACT);
        }

        // if use tax, don't allow editing of tax fields
        if (paymentRequestDocument.isUseTaxIndicator()) {
            editModes.add(PurapAuthorizationConstants.CreditMemoEditMode.CLEAR_ALL_TAXES);
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.LOCK_TAX_AMOUNT_ENTRY);
        }

        // See if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PurapAuthorizationConstants.PURAP_TAX_ENABLED);
        }

        //FIXME is this the right status?  should it check to see if there is data too?
        // after tax is approved, the tax tab is viewable to everyone
        if (PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(paymentRequestDocument.getStatusCode())) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.TAX_INFO_VIEWABLE);
        }

        if (paymentRequestDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.ACCOUNT_REVIEW)) {
            // remove FULL_ENTRY because FO cannot edit rest of doc; only their own acct lines
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.RESTRICT_FISCAL_ENTRY);

            // expense_entry was already added in super, add amount edit mode
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY);

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
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.TAX_AREA_EDITABLE);
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

}
