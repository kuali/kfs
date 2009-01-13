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

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class determines permissions for a user of the Payment Request document
 */
public class PaymentRequestDocumentActionAuthorizer implements Serializable {

    private String docStatus;
    private boolean requestCancelIndicator;
    private boolean holdIndicator;
    private boolean extracted;
    private boolean adHocRequested;
    
    private PaymentRequestDocument paymentRequest;
    private Map documentActions;
    private Person user;
    
    /**
     * Constructs a PaymentRequestDocumentActionAuthorizer.
     * 
     * @param paymentRequest A PaymentRequestDocument
     */
    public PaymentRequestDocumentActionAuthorizer(PaymentRequestDocument paymentRequest, Map documentActions) {
        docStatus = paymentRequest.getStatusCode();
        requestCancelIndicator = paymentRequest.getPaymentRequestedCancelIndicator();
        holdIndicator = paymentRequest.isHoldIndicator();        
        extracted = paymentRequest.getExtractedTimestamp() != null;
        adHocRequested = paymentRequest.getDocumentHeader().getWorkflowDocument().isAdHocRequested();
        
        this.paymentRequest = paymentRequest;
        this.documentActions = documentActions;
        user = GlobalVariables.getUserSession().getPerson();        
    }

    /**
     * Determines whether the current user can continue creating or clear fields of the payment request in initial status. Conditions:
     * - the Payment Request must be in the INITIATE state; and
     * - the user must have the authorization to initiate a Payment Request.
     * 
     * @return True if the current user can continue creating or clear fields of the initiated Payment Request.
     */
    public boolean canContinue() {
        // preq must be in initiated status
        boolean can = docStatus.equals(PaymentRequestStatuses.INITIATE);
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentTypeService.class).getDocumentAuthorizer(paymentRequest);
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PaymentRequestDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, user);
        }
              
        return can;        
    }    

    /**
     * Determine whether the current user can calculate the paymentRequest. Conditions:
     * - Payment Request is not FullDocumentEntryCompleted, and
     * - current user has the permission to edit the document.
     * 
     * @return True if the current user can calculate the Payment Request.
     */
    public boolean canCalculate() {
        // preq must not be FullDocumentEntryCompleted
        boolean can = !SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequest);
        
        // check user authorization: whoever can edit can calculate
        can = can && documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT);

        return can;
    }

    /**
     * Determines whether the PaymentRequest Hold button shall be available. Conditions:
     * - Payment Request is not already on hold, and
     * - Payment Request is not already being requested to be canceled, and
     * - Payment Request has not already been extracted to PDP, and
     * - Payment Request status is not in the list of "STATUSES_DISALLOWING_HOLD" or document is being adhoc routed; and
     * - current user has an active approval request, or
     * - current user has the permission for the default template in KIM.
     * 
     * @return True if the current user can place the Payment Request on hold.
     */
    public boolean canHold() {
        // check preq status
        boolean can = !holdIndicator && !requestCancelIndicator && !extracted;
        if (can) {
            can = adHocRequested;
            can = can || !PaymentRequestStatuses.STATUSES_DISALLOWING_HOLD.contains(docStatus);
        }
        
        // check user authorization
        if (can) {
            can = paymentRequest.getDocumentHeader().getWorkflowDocument().isApprovalRequested();
            if (!can) {
                DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentTypeService.class).getDocumentAuthorizer(paymentRequest);
                can = documentAuthorizer.isAuthorized(paymentRequest, PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.HOLD_PREQ, user.getPrincipalId());                
            }
        }
        
        return can;
    }

    /**
     * Determines whether the Remove Hold button shall be available. Conditions:
     * - the hold indicator is set to true; and
     * - the user has permission to use the button.  
     * Because the state of the Payment Request cannot be changed while the document is on hold, 
     * we should not have to check the state of the document to remove the hold.  
     * For example, the document should not be allowed to be approved or extracted while on hold.
     * 
     * @return True if the current user can remove the Payment Request from hold.
     */
    public boolean canRemoveHold() {
        // preq must be on hold
        boolean can = holdIndicator;       

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentTypeService.class).getDocumentAuthorizer(paymentRequest);
            can = documentAuthorizer.isAuthorized(paymentRequest, PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.REMOVE_HOLD_PREQ, user.getPrincipalId());
        }

        return can;
    }

    /**
     * Determines whether the Request Cancel PaymentRequest button shall be available. Conditions:
     * - Payment Request is not already on hold, and
     * - Payment Request is not already being requested to be canceled, and
     * - Payment Request has not already been extracted to PDP, and
     * - Payment Request status is not in the list of "STATUSES_DISALLOWING_REQUEST_CANCEL" or document is being adhoc routed; and
     * - current user has an active approval request.
     * 
     * @return True if the current user can request that the Payment Request be canceled.
     */
    public boolean canRequestCancel() {
        // check preq status
        boolean can = !requestCancelIndicator && !holdIndicator && !extracted;
        if (can) {
            can = adHocRequested;
            can = can || !PaymentRequestStatuses.STATUSES_DISALLOWING_REQUEST_CANCEL.contains(docStatus);
        }

        // check user authorization
        can = can && paymentRequest.getDocumentHeader().getWorkflowDocument().isApprovalRequested();

        return can;
    }

    /**
     * Determines whether the Remove Request Cancel button shall be available. Conditions:
     * - the request cancel indicator is set to true;  and 
     * - the user has permission to use the button (either from the default template in KIM or that 
     * - the user is the one that requested the cancel.  
     * Because the state of the Payment Request cannot be changed while the document is set to request cancel, 
     * we should not have to check the state of the document to remove the request cancel.  
     * For example, the document should not be allowed to be approved or extracted while set to request cancel.
     *  
     * @return True if the current user can remove a request that the Payment Request be canceled.
     */
    public boolean canRemoveRequestCancel() {
        // preq must have request cancel
        boolean can = requestCancelIndicator;

        // check user authorization
        if (can) {
            can = user.getPrincipalId().equals(paymentRequest.getLastActionPerformedByPersonId());
            if (!can) {
                DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentTypeService.class).getDocumentAuthorizer(paymentRequest);
                can = documentAuthorizer.isAuthorized(paymentRequest, PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.REMOVE_CANCEL_PREQ, user.getPrincipalId());
            }
        }

        return can;
    }

    //TODO remove
    /**
     * Predicate to determine whether the current user can cancel the paymentRequest.
     * 
     * @return True if the current user can cancel.
     */
    public boolean canCancel() {
        boolean can = !StringUtils.equals(paymentRequest.getStatusCode(), PaymentRequestStatuses.INITIATE);
        
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
        
        if (preroute) {
            can = isApUser() || isApSupervisor();
        }
        else if (enroute) {
            can = isApUser() && requestCancelIndicator || isApSupervisor();
        }
        else if (postroute) {
            can = (isApUser() || isApSupervisor()) && !requestCancelIndicator && !holdIndicator && !extracted;
        }
        
        return can;
    }
    
    //TODO move to presentation controller
    /**
     * Determines whether the current user can edit the pre-extract fields.
     * 
     * @return True if the current user can edit the fields
     */
    public boolean canEditPreExtractFields() {
        return !extracted && isApUser();
    }
    
    //TODO remove
    public boolean isApUser() {
        String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apGroup);
    }

    //TODO remove
    public boolean isApSupervisor() {
        String apSupGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apSupGroup);
    }
    
}

