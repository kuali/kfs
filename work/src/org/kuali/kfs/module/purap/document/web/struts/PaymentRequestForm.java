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
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.authorization.PaymentRequestDocumentActionAuthorizer;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KualiConfigurationService;
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
        
        //FIXME hjs-can we get rid of this? check user logic
        PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(paymentRequestDocument, getDocumentActions());

        if (preqDocAuth.canContinue()) {
            addExtraButton("methodToCall.continuePREQ", externalImageURL + "buttonsmall_continue.gif", "Continue");
            addExtraButton("methodToCall.clearInitFields", externalImageURL + "buttonsmall_clear.gif", "Clear");
        }
        else {
            if (preqDocAuth.canHold()) {
                addExtraButton("methodToCall.addHoldOnPayment", appExternalImageURL + "buttonsmall_hold.gif", "Hold");
            }

            // if person can remove hold
            if (preqDocAuth.canRemoveHold()) {
                addExtraButton("methodToCall.removeHoldFromPayment", appExternalImageURL + "buttonsmall_removehold.gif", "Remove");
            }

            // if preq can have a cancel request and user can submit request cancel, show button
            if (preqDocAuth.canRequestCancel()) {
                addExtraButton("methodToCall.requestCancelOnPayment", appExternalImageURL + "buttonsmall_requestcancel.gif", "Cancel");
            }

            // if person can remove request cancel
            if (preqDocAuth.canRemoveRequestCancel()) {
                addExtraButton("methodToCall.removeCancelRequestFromPayment", appExternalImageURL + "buttonsmall_remreqcanc.gif", "Remove");
            }

            // add the calculate button
            if (preqDocAuth.canCalculate()) {
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
    
}

