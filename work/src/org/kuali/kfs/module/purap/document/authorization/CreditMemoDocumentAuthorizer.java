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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the Credit Memo document.
 */
public class CreditMemoDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#hasInitiateAuthorization(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean hasInitiateAuthorization(Document document, Person user) {
        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        KimGroup group = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, authorizedWorkgroup);
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
        Map<String, String> editModeMap = super.getEditMode(document, user, sourceAccountingLines, targetAccountingLines);
        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(document, user)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            if (!SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((CreditMemoDocument) document)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        if (!editMode.equals(AuthorizationConstants.EditMode.FULL_ENTRY) || (! workflowDocument.isAdHocRequested())) {
            editModeMap.put(editMode, "TRUE");
        }
        CreditMemoDocument creditMemoDocument = (CreditMemoDocument) document;
        if (StringUtils.equals(creditMemoDocument.getStatusCode(), PurapConstants.CreditMemoStatuses.INITIATE)) {
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB, "TRUE");
        }
        else {
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB, "FALSE");
        }

        if (!creditMemoDocument.isSourceDocumentPaymentRequest()) {
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.LOCK_VENDOR_ENTRY, "TRUE");
        }

        String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);

        if (KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apGroup) && 
                (creditMemoDocument.getExtractedDate() == null) && 
                (! workflowDocument.isAdHocRequested()) &&
                (! PurapConstants.CreditMemoStatuses.CANCELLED_STATUSES.contains(creditMemoDocument.getStatusCode()))) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_PRE_EXTRACT, "TRUE");
        }

        if (!creditMemoDocument.isUseTaxIndicator() &&
            !SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((CreditMemoDocument) document) &&   
            KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apGroup) ) {
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.CLEAR_ALL_TAXES, "TRUE");
        }
        
        //Use tax indicator editing is enabled
        if(editModeMap.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY)){
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.USE_TAX_INDICATOR_CHANGEABLE, "TRUE");
        }
        
        //if full entry, and not use tax, allow editing
        if(editModeMap.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY) && creditMemoDocument.isUseTaxIndicator() == false){
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.TAX_AMOUNT_CHANGEABLE, "TRUE");
        }

        //See if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter("KFS-PURAP", "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);                
        if(salesTaxInd){
            editModeMap.put(PurapAuthorizationConstants.PURAP_TAX_ENABLED, "TRUE");
        }

        return editModeMap;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        CreditMemoDocument creditMemoDocument = (CreditMemoDocument) document;
        if (StringUtils.equals(creditMemoDocument.getStatusCode(), PurapConstants.CreditMemoStatuses.INITIATE)) {
            flags.setCanSave(false);
            flags.setCanClose(true);
            flags.setCanCancel(false);
        }
        else {
            String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
            if (StringUtils.equals(creditMemoDocument.getStatusCode(), PurapConstants.CreditMemoStatuses.IN_PROCESS) && KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apGroup)) {
                flags.setCanSave(true);
            }
            else {
                flags.setCanSave(false);
            }
            if (SpringContext.getBean(CreditMemoService.class).canCancelCreditMemo(creditMemoDocument, GlobalVariables.getUserSession().getPerson())) {
                flags.setCanCancel(true);
            }
            else {
                flags.setCanCancel(false);
            }
        }

        //Set can edit bank to true if the document has not been extracted, for now without Kim (more changes when Kim is available).
        if (!creditMemoDocument.isExtracted()) {
            flags.setCanEditBank(true);
        }
        
        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
        this.setAnnotateFlag(flags);

        return flags;
    }

}

