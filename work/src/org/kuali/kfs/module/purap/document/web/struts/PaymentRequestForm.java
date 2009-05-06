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

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PaymentRequestEditMode;
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
            if (getEditingMode().containsKey(PaymentRequestEditMode.HOLD)) {
                addExtraButton("methodToCall.addHoldOnPayment", appExternalImageURL + "buttonsmall_hold.gif", "Hold");
            }

            if (getEditingMode().containsKey(PaymentRequestEditMode.REMOVE_HOLD)) {
                addExtraButton("methodToCall.removeHoldFromPayment", appExternalImageURL + "buttonsmall_removehold.gif", "Remove");
            }

            if (getEditingMode().containsKey(PaymentRequestEditMode.REQUEST_CANCEL)) {
                addExtraButton("methodToCall.requestCancelOnPayment", appExternalImageURL + "buttonsmall_requestcancel.gif", "Cancel");
            }

            if (getEditingMode().containsKey(PaymentRequestEditMode.REMOVE_REQUEST_CANCEL)) {
                addExtraButton("methodToCall.removeCancelRequestFromPayment", appExternalImageURL + "buttonsmall_remreqcanc.gif", "Remove");
            }

            if (canCalculate()) {
                addExtraButton("methodToCall.calculate", appExternalImageURL + "buttonsmall_calculate.gif", "Calculate");
            }
            
            if (getEditingMode().containsKey(PaymentRequestEditMode.ACCOUNTS_PAYABLE_PROCESSOR_CANCEL) ||
                    getEditingMode().containsKey(PaymentRequestEditMode.ACCOUNTS_PAYABLE_MANAGER_CANCEL)) {
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

        //FIXME this is temporary so that calculate will show up at tax
        can = can || editingMode.containsKey(PaymentRequestEditMode.TAX_AREA_EDITABLE);
        
        return can;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (KNSConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && 
           ("changeUseTaxIndicator".equals(methodToCallParameterValue))) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

}

