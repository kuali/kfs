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
package org.kuali.module.purap.document.authorization;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapService;

/**
 * This class determines permissions for a user of the Payment Request document
 */
public class PaymentRequestDocumentActionAuthorizer {

    private String docStatus;
    private boolean requestCancelIndicator;
    private boolean holdIndicator;
    private boolean extracted;
    private boolean canRemoveHold;
    private boolean canRemoveRequestCancel;
    private boolean canHold;
    private boolean canRequestCancel;
    private boolean fullEntryCompleted;
    private boolean adHocRequested;
    
    private boolean apUser;
    private boolean apSupervisor;
    private boolean fiscalOfficerDelegateUser;
    private boolean approver;

    
    /**
     * Constructs a PaymentRequestDocumentActionAuthorizer.
     * 
     * @param preq A PaymentRequestDocument
     */
    public PaymentRequestDocumentActionAuthorizer(PaymentRequestDocument preq) {

        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();

        // doc indicators
        this.docStatus = preq.getStatusCode();
        this.requestCancelIndicator = preq.getPaymentRequestedCancelIndicator();
        this.holdIndicator = preq.isHoldIndicator();
        this.extracted = (preq.getExtractedDate() == null ? false : true);
        this.fullEntryCompleted = SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(preq);
        this.adHocRequested = preq.getDocumentHeader().getWorkflowDocument().isAdHocRequested();
        
        // special indicators
        if (SpringContext.getBean(PaymentRequestService.class).canHoldPaymentRequest(preq, user)) {
            canHold = true;
        }

        if (SpringContext.getBean(PaymentRequestService.class).canUserRequestCancelOnPaymentRequest(preq, user)) {
            canRequestCancel = true;
        }

        if (SpringContext.getBean(PaymentRequestService.class).canRemoveHoldPaymentRequest(preq, user)) {
            canRemoveHold = true;
        }

        if (SpringContext.getBean(PaymentRequestService.class).canUserRemoveRequestCancelOnPaymentRequest(preq, user)) {
            canRemoveRequestCancel = true;
        }

        // user indicators
        this.approver = preq.getDocumentHeader().getWorkflowDocument().isApprovalRequested();

        String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        if (user.isMember(apGroup)) {
            this.apUser = true;
        }

        String apSupGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        if (user.isMember(apSupGroup)) {
            this.apSupervisor = true;
        }

        if (PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(getDocStatus()) && isApprover()) {
            this.fiscalOfficerDelegateUser = true;
        }
        
    }

    private String getDocStatus() {
        return docStatus;
    }

    private boolean isRequestCancelIndicator() {
        return requestCancelIndicator;
    }

    private boolean isHoldIndicator() {
        return holdIndicator;
    }

    private boolean isExtracted() {
        return extracted;
    }

    public boolean isCanRemoveHold() {
        return canRemoveHold;
    }

    public boolean isCanRemoveRequestCancel() {
        return canRemoveRequestCancel;
    }

    public boolean isCanHold() {
        return canHold;
    }

    public boolean isCanRequestCancel() {
        return canRequestCancel;
    }

    private boolean isApUser() {
        return apUser;
    }

    public boolean isApSupervisor() {
        return apSupervisor;
    }

    public void setApSupervisor(boolean apSupervisor) {
        this.apSupervisor = apSupervisor;
    }

    private boolean isFiscalOfficerDelegateUser() {
        return fiscalOfficerDelegateUser;
    }

    private boolean isApprover() {
        return approver;
    }

    public boolean isFullEntryCompleted() {
        return fullEntryCompleted;
    }

    public void setFullEntryCompleted(boolean fullEntryCompleted) {
        this.fullEntryCompleted = fullEntryCompleted;
    }

    /**
     * Predicate to determine whether the current user can calculate the PREQ.
     * 
     * @return True if the current user can calculate
     */
    public boolean canCalculate() {
        boolean hasPermission = false;

        // Phase 2B Rule: (PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals( getDocStatus() ) && isApprover()) ||

        if (isFullEntryCompleted() == false && isApUser()) {
            hasPermission = true;
        }

        return hasPermission;
    }

    /**
     * Predicate to determine whether the current user can approve the PREQ.
     * 
     * @return True if the current user can approve.
     */
    public boolean canApprove() {
        boolean hasPermission = false;

        if ((PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW.equals(getDocStatus()) || PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(getDocStatus()) || PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW.equals(getDocStatus()) || PaymentRequestStatuses.AWAITING_ORG_REVIEW.equals(getDocStatus()) || PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals(getDocStatus()) || isAdHocRequested()) && (isApprover() && isRequestCancelIndicator() == false && isHoldIndicator() == false)) {
            hasPermission = true;
        }
        return hasPermission;
    }

    /**
     * Predicate to determine whether the current user can save the PREQ.
     * 
     * @return True if the current user can save.
     */
    public boolean canSave() {
        boolean hasPermission = false;

        if (isApUser() && isExtracted() == false) {
            hasPermission = true;
        }

        return hasPermission;
    }

    /**
     * Predicate to determine whether the current user can place the PREQ on hold.
     * 
     * @return True if the current user can place the PREQ on hold.
     */
    public boolean canHold() {
        boolean hasPermission = false;

        if (isCanHold() || (((PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(getDocStatus()) || PaymentRequestStatuses.AUTO_APPROVED.equals(getDocStatus())) && (isApUser() && isHoldIndicator() == false && isHoldIndicator() == false && isExtracted() == false)))) {

            hasPermission = true;
        }

        return hasPermission;
    }

    /**
     * Predicate to determine whether the current user can remove the PREQ from being on hold.
     * 
     * @return True if the current user can remove the PREQ from hold.
     */
    public boolean canRemoveHold() {
        boolean hasPermission = false;

        if (isCanRemoveHold() || (

        ((PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(getDocStatus()) || PaymentRequestStatuses.AUTO_APPROVED.equals(getDocStatus())) && (isApSupervisor() && isHoldIndicator() == true && isExtracted() == false)))) {

            hasPermission = true;
        }

        return hasPermission;
    }

    /**
     * Predicate to determine whether the current user can cancel the PREQ.
     * 
     * @return True if the current user can cancel.
     */
    public boolean canCancel() {
        boolean hasPermission = false;

        if (((PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW.equals(getDocStatus()) || PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(getDocStatus()) || PaymentRequestStatuses.AWAITING_ORG_REVIEW.equals(getDocStatus()) || PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals(getDocStatus())) && ((isApUser() && isRequestCancelIndicator()) || isApSupervisor())) ||

        ((PaymentRequestStatuses.IN_PROCESS.equals(getDocStatus()) || PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW.equals(getDocStatus())) && (isApUser() || isApSupervisor())) ||

        ((PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(getDocStatus()) || PaymentRequestStatuses.AUTO_APPROVED.equals(getDocStatus())) && ((isApUser() || isApSupervisor()) && isRequestCancelIndicator() == false && isHoldIndicator() == false && isExtracted() == false))) {

            hasPermission = true;
        }

        return hasPermission;
    }

    /**
     * Predicate to determine whether the current user can request that the PREQ be canceled.
     * 
     * @return True if the current user can request that the PREQ be canceled
     */
    public boolean canRequestCancel() {
        boolean hasPermission = false;

        if (isCanRequestCancel()) {
            hasPermission = true;
        }

        return hasPermission;
    }

    /**
     * Predicate to determine whether the current user can remove a request that the PREQ be canceled.
     * 
     * @return True if the current user can remove a request that the PREQ be canceled.
     */
    public boolean canRemoveRequestCancel() {
        boolean hasPermission = false;

        if (isCanRemoveRequestCancel()) {
            hasPermission = true;
        }

        return hasPermission;
    }

    /**
     * Predicate to determine whether the current user can edit the pre-extract fields.
     * 
     * @return True if the current user can edit the fields
     */
    public boolean canEditPreExtractFields() {
        return !this.isExtracted() && this.apUser;
    }

    /**
     * Predicate to determine whether the current user can exit.
     * 
     * @return True if the current user can exit.
     */
    public boolean canExit() {
        boolean hasPermission = true;
        return hasPermission;
    }

    public boolean isAdHocRequested() {
        return adHocRequested;
    }

    public void setAdHocRequested(boolean adHocRequested) {
        this.adHocRequested = adHocRequested;
    }
    
}
