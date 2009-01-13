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
import java.util.Arrays;
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
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class PaymentRequestDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

//FIXME hjs KIM cleanup    
//  PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(paymentRequestDocument);
//                flags.setCanApprove(preqDocAuth.canApprove());
//                
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

        if (paymentRequestDocument.getExtractedTimestamp() != null) {
            return false;
        }      
        
        return super.canSave(document);
    }

    @Override
    protected boolean canCancel(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        
        //FIXME hjs should all this logic be together (either all here or all in action authorizer)
        // if Payment Request is in INITIATE status, user cannot cancel doc
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PaymentRequestStatuses.INITIATE)) {
            return false;
        }
        
        // check Action Authorizer for ability to cancel doc
        String docStatus = paymentRequestDocument.getStatusCode();
        boolean requestCancelIndicator = paymentRequestDocument.getPaymentRequestedCancelIndicator();
        boolean holdIndicator = paymentRequestDocument.isHoldIndicator();        
        boolean extracted = paymentRequestDocument.getExtractedTimestamp() != null;
        
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
            can = isApUser() || isApSupervisor();
        }
        else if (enroute) {
            can = isApUser() && requestCancelIndicator || isApSupervisor();
        }
        else if (postroute) {
            can = (isApUser() || isApSupervisor()) && !requestCancelIndicator && !holdIndicator && !extracted;
        }

        return can && super.canCancel(document);
    }

    @Override
    protected boolean canClose(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PaymentRequestStatuses.INITIATE)) {
            return false;
        }
        return super.canClose(document);
    }

    
    /* TODO uncomment when ready
    @Override
    public boolean canApprove(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        return !paymentRequestDocument.isPaymentRequestedCancelIndicator() && !paymentRequestDocument.isHoldIndicator();
    }
    */

    @Override
    protected boolean canDisapprove(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        if (!getCurrentRouteLevels(workflowDocument).contains(NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW.getName()) || 
                StringUtils.equals(paymentRequestDocument.getStatusCode(), PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW)) {
            return false;
        }
        return super.canDisapprove(document);
    }

    
    /**
     * 
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
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument)document;
        Set<String> editModes = new HashSet<String>();
        
        // always show amount after full entry
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequestDocument)) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY);
        }

        // make sure ap user can edit certain fields
        if (paymentRequestDocument.getExtractedTimestamp() == null && //TODO && isApUser
            !paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isAdHocRequested() && 
            !paymentRequestDocument.isPaymentRequestedCancelIndicator()) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_PRE_EXTRACT);
        }

        if (paymentRequestDocument.getStatusCode().equals(PurapConstants.PaymentRequestStatuses.INITIATE)) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB);
        }
        
        if (ObjectUtils.isNotNull(paymentRequestDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.LOCK_VENDOR_ENTRY);
        }
        
        //TODO check this logic (used to check FULL_ENTRY)
        if(canEdit(paymentRequestDocument)){
            //Use tax indicator editing is enabled
            editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.USE_TAX_INDICATOR_CHANGEABLE);
            
            if (!paymentRequestDocument.isUseTaxIndicator()) {
                //if full entry, and not use tax, allow editing
                editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.TAX_AMOUNT_CHANGEABLE);
            }
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

        List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
        if (currentRouteLevels.contains(NodeDetailEnum.ACCOUNT_REVIEW.getName())) {
            // expense_entry was already added in super
            // add amount edit mode
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

        return editModes;
    }

    //TODO remove
    public boolean isApUser() {
        String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        Person user = GlobalVariables.getUserSession().getPerson();        
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apGroup);
    }

    //TODO remove
    public boolean isApSupervisor() {
        String apSupGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        Person user = GlobalVariables.getUserSession().getPerson();        
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apSupGroup);
    }
        
    /**
     * A helper method for determining the route levels for a given document.
     * 
     * @param workflowDocument
     * @return List
     */
    protected static List getCurrentRouteLevels(KualiWorkflowDocument workflowDocument) {
        try {
            return Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

}
