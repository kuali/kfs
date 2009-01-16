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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.List;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Struts Action Form for Payment Request document.
 */
public class PaymentRequestForm extends AccountsPayableFormBase {

    /**
     * Indicates whether tax has been calculated based on the tax area data.
     * Tax calculation is enforced before preq can be routed for tax approval.
     */
    private boolean calculatedTax;

    /**
     * Constructs a PaymentRequestForm instance and sets up the appropriately casted document.
     */
    public PaymentRequestForm() {
        super();
        setDocument(new PaymentRequestDocument());
        this.setNewPurchasingItemLine(setupNewPurchasingItemLine());
    }
    
    public boolean isCalculatedTax() {
        return calculatedTax;
    }

    public void setCalculatedTax(boolean calculatedTax) {
        this.calculatedTax = calculatedTax;
    }

    public PaymentRequestDocument getPaymentRequestDocument() {
        return (PaymentRequestDocument) getDocument();
    }

    public void setPaymentRequestDocument(PaymentRequestDocument purchaseOrderDocument) {
        setDocument(purchaseOrderDocument);
    }

    @Override
    public void populateHeaderFields(KualiWorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        if (ObjectUtils.isNotNull(this.getPaymentRequestDocument().getPurapDocumentIdentifier())) {
            getDocInfo().add(new HeaderField("DataDictionary.PaymentRequestDocument.attributes.purapDocumentIdentifier", ((PaymentRequestDocument) this.getDocument()).getPurapDocumentIdentifier().toString()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.PaymentRequestDocument.attributes.purapDocumentIdentifier", "Not Available"));
        }
        if (ObjectUtils.isNotNull(this.getPaymentRequestDocument().getStatus())) {
            getDocInfo().add(new HeaderField("DataDictionary.PaymentRequestDocument.attributes.statusCode", ((PaymentRequestDocument) this.getDocument()).getStatus().getStatusDescription()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.PaymentRequestDocument.attributes.statusCode", "Not Available"));
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewPurchasingItemLine()
     */
    @Override
    public PurApItem setupNewPurchasingItemLine() {
        return new PurchaseOrderItem();
    }

    /**
     * Helper method to indicate if the current document has reached full document entry.
     * 
     * @return - true if document has reached full entry, false otherwise
     */
    //TODO hjs-should we consider making this an editmode instead of a method on the form?
    public boolean isFullDocumentEntryCompleted() {
        PaymentRequestDocument preq = (PaymentRequestDocument) this.getDocument();
        return SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(preq);
    }

    /**
     * Build additional payment request specific buttons and set extraButtons list.
     * 
     * @return - list of extra buttons to be displayed to the user
     */
    @Override
    public List<ExtraButton> getExtraButtons() {        
        extraButtons.clear(); // clear out the extra buttons array
        PaymentRequestDocument paymentRequestDocument = this.getPaymentRequestDocument();
        String externalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
        String appExternalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        
        if (canContinue()) {
            addExtraButton("methodToCall.continuePREQ", externalImageURL + "buttonsmall_continue.gif", "Continue");
            addExtraButton("methodToCall.clearInitFields", externalImageURL + "buttonsmall_clear.gif", "Clear");
        }
        else {
            if (canHold()) {
                addExtraButton("methodToCall.addHoldOnPayment", appExternalImageURL + "buttonsmall_hold.gif", "Hold");
            }

            // if person can remove hold
            if (canRemoveHold()) {
                addExtraButton("methodToCall.removeHoldFromPayment", appExternalImageURL + "buttonsmall_removehold.gif", "Remove");
            }

            // if preq can have a cancel request and user can submit request cancel, show button
            if (canRequestCancel()) {
                addExtraButton("methodToCall.requestCancelOnPayment", appExternalImageURL + "buttonsmall_requestcancel.gif", "Cancel");
            }

            // if person can remove request cancel
            if (canRemoveRequestCancel()) {
                addExtraButton("methodToCall.removeCancelRequestFromPayment", appExternalImageURL + "buttonsmall_remreqcanc.gif", "Remove");
            }

            // add the calculate button
            if (canCalculate()) {
                addExtraButton("methodToCall.calculate", appExternalImageURL + "buttonsmall_calculate.gif", "Calculate");
            }
            
            boolean canProcessorCancel = getEditingMode().containsKey(PurapAuthorizationConstants.PaymentRequestEditMode.ACCOUNTS_PAYABLE_PROCESSOR_CANCEL);
            boolean canManagerCancel = getEditingMode().containsKey(PurapAuthorizationConstants.PaymentRequestEditMode.ACCOUNTS_PAYABLE_MANAGER_CANCEL);
            if (canProcessorCancel || canManagerCancel) {
                addExtraButton("methodToCall.cancel", externalImageURL + "buttonsmall_cancel.gif", "Cancel");
            }
        }

        return extraButtons;
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
        boolean can = PaymentRequestStatuses.INITIATE.equals(getPaymentRequestDocument().getStatusCode());
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPaymentRequestDocument());
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PaymentRequestDocument.class);
            can = documentAuthorizer.canInitiate(documentTypeName, GlobalVariables.getUserSession().getPerson());
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
        boolean can = !SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(getPaymentRequestDocument());
        
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
        boolean can = !getPaymentRequestDocument().isHoldIndicator() && !getPaymentRequestDocument().isPaymentRequestedCancelIndicator() && !getPaymentRequestDocument().isExtracted();
        if (can) {
            can = getPaymentRequestDocument().getDocumentHeader().getWorkflowDocument().isAdHocRequested();
            can = can || !PaymentRequestStatuses.STATUSES_DISALLOWING_HOLD.contains(getPaymentRequestDocument().getStatusCode());
        }
        
        // check user authorization
        if (can) {
            can = getPaymentRequestDocument().getDocumentHeader().getWorkflowDocument().isApprovalRequested();
            if (!can) {
                DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPaymentRequestDocument());
                can = documentAuthorizer.isAuthorized(getPaymentRequestDocument(), PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.HOLD_PREQ, GlobalVariables.getUserSession().getPerson().getPrincipalId());                
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
        boolean can = getPaymentRequestDocument().isHoldIndicator();       

        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPaymentRequestDocument());
            can = documentAuthorizer.isAuthorized(getPaymentRequestDocument(), PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.REMOVE_HOLD_PREQ, GlobalVariables.getUserSession().getPerson().getPrincipalId());
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
        boolean can = !getPaymentRequestDocument().isPaymentRequestedCancelIndicator() && !getPaymentRequestDocument().isHoldIndicator() && !getPaymentRequestDocument().isExtracted();
        if (can) {
            can = getPaymentRequestDocument().getDocumentHeader().getWorkflowDocument().isAdHocRequested();
            can = can || !PaymentRequestStatuses.STATUSES_DISALLOWING_REQUEST_CANCEL.contains(getPaymentRequestDocument().getStatusCode());
        }

        // check user authorization
        can = can && getPaymentRequestDocument().getDocumentHeader().getWorkflowDocument().isApprovalRequested();

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
        boolean can = getPaymentRequestDocument().isPaymentRequestedCancelIndicator();

        // check user authorization
        if (can) {
            can = GlobalVariables.getUserSession().getPerson().getPrincipalId().equals(getPaymentRequestDocument().getLastActionPerformedByPersonId());
            if (!can) {
                DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPaymentRequestDocument());
                can = documentAuthorizer.isAuthorized(getPaymentRequestDocument(), PurapConstants.PURAP_NAMESPACE, PurapAuthorizationConstants.PermissionNames.REMOVE_CANCEL_PREQ, GlobalVariables.getUserSession().getPerson().getPrincipalId());
            }
        }

        return can;
    }
}

