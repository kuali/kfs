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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the PREQ document.
 */
public class PaymentRequestDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#hasInitiateAuthorization(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean hasInitiateAuthorization(Document document, Person user) {
        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        KimGroup group = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName("KFS", authorizedWorkgroup);
        if (group == null) {
            throw new RuntimeException("Workgroup " + authorizedWorkgroup + " not found");
        }
        return org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), group.getGroupId());    
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public Map getEditMode(Document document, Person user, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = super.getEditMode(document, user, sourceAccountingLines, targetAccountingLines);
        PaymentRequestDocument preq = (PaymentRequestDocument) document;

        PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(preq);

        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        boolean fullDocumentEntryCompleted = SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((PaymentRequestDocument) document);

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(document, user)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
            // only allow full entry if status allows it

            if (!fullDocumentEntryCompleted) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }

            if (currentRouteLevels.contains(NodeDetailEnum.ACCOUNT_REVIEW.getName())) {
                editModeMap.remove(AuthorizationConstants.EditMode.FULL_ENTRY);
                
                // expense_entry was already added in super
                // add amount edit mode
                editMode = PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY;

                //only do line item check if the hold/cancel indicator is false, otherwise
                // document editing should be turned off.
                if (preq.isHoldIndicator() == false && preq.isPaymentRequestedCancelIndicator() == false){
                    List lineList = new ArrayList();
                    for (Iterator iter = preq.getItems().iterator(); iter.hasNext();) {
                        PaymentRequestItem item = (PaymentRequestItem) iter.next();
                        lineList.addAll(item.getSourceAccountingLines());
                        // If FO has deleted the last accounting line for an item, set entry mode to full so they can add another one
                        if (item.getItemType().isLineItemIndicator() && item.getSourceAccountingLines().size() == 0) {
                            editModeMap.remove(AuthorizationConstants.EditMode.VIEW_ONLY);
                            editModeMap.put(KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY, "TRUE");
                        }
                    }
                }else{
                    editMode = AuthorizationConstants.EditMode.VIEW_ONLY;
                }
            }
            else if( preq.isPaymentRequestedCancelIndicator() ) {
                editMode = AuthorizationConstants.EditMode.VIEW_ONLY;
            }
            //This is for ad hoc approval
            if (preqDocAuth.isAdHocRequested()) {
                editMode = AuthorizationConstants.EditMode.VIEW_ONLY;
            }
        }

        editModeMap.put(editMode, "TRUE");

        
        // always show amount after full entry
        if (fullDocumentEntryCompleted) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY, "TRUE");
        }

        // make sure ap user can edit certain fields
        if (preqDocAuth.canEditPreExtractFields() && ! preqDocAuth.isAdHocRequested() && ! preq.isPaymentRequestedCancelIndicator()) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_PRE_EXTRACT, "TRUE");
        }

        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PurapConstants.PaymentRequestStatuses.INITIATE)) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB, "TRUE");
        }
        else {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB, "FALSE");
        }
        if (ObjectUtils.isNotNull(paymentRequestDocument.getVendorHeaderGeneratedIdentifier())) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.LOCK_VENDOR_ENTRY, "TRUE");
        }
        
        String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        
        if (!paymentRequestDocument.isUseTaxIndicator() &&
            !fullDocumentEntryCompleted && user.isMember(apGroup) ) {
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.CLEAR_ALL_TAXES, "TRUE");
        }
        
        //Use tax indicator editing is enabled
        if(editModeMap.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY)){
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.USE_TAX_INDICATOR_CHANGEABLE, "TRUE");
        }
        
        //if full entry, and not use tax, allow editing
        if(editModeMap.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY) && paymentRequestDocument.isUseTaxIndicator() == false){
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.TAX_AMOUNT_CHANGEABLE, "TRUE");
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
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (KFSConstants.SYSTEM_USER.equalsIgnoreCase(user.getPrincipalName())) {
            flags.setCanBlanketApprove(true);
        }

        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PurapConstants.PaymentRequestStatuses.INITIATE)) {
            flags.setCanSave(false);
            flags.setCanClose(true);
            flags.setCanCancel(false);
            flags.setCanDisapprove(false);
        }
        else {
            if (!getCurrentRouteLevels(workflowDocument).contains(NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW.getName()) ||
                 StringUtils.equals(paymentRequestDocument.getStatusCode(),PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW)) {
                flags.setCanDisapprove(false);
            }

            PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(paymentRequestDocument);

            flags.setCanApprove(preqDocAuth.canApprove());
            flags.setCanCancel(preqDocAuth.canCancel());
            flags.setCanSave(preqDocAuth.canSave());
            
            //Set can edit bank to true if the document has not been extracted, for now without Kim (more changes when Kim is available).
            if (!paymentRequestDocument.isExtracted()) {
                flags.setCanEditBank(true);
            }
        }

        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
        this.setAnnotateFlag(flags);

        return flags;
    }

}

