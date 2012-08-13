/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PaymentRequestEditMode;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Struts Action Form for Payment Request document.
 */
public class PaymentRequestForm extends AccountsPayableFormBase {

    /**
     * Indicates whether tax has been calculated based on the tax area data.
     * Tax calculation is enforced before preq can be routed for tax approval.
     */
    protected boolean calculatedTax;

    /**
     * Constructs a PaymentRequestForm instance and sets up the appropriately casted document.
     */
    public PaymentRequestForm() {
        super();
        this.setNewPurchasingItemLine(setupNewPurchasingItemLine());
        //on PO, account distribution should be read only
        setReadOnlyAccountDistributionMethod(true);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "PREQ";
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
    public void populateHeaderFields(WorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        
        if (ObjectUtils.isNotNull(this.getPaymentRequestDocument().getPurapDocumentIdentifier())) {
            getDocInfo().add(new HeaderField("DataDictionary.PaymentRequestDocument.attributes.purapDocumentIdentifier", ((PaymentRequestDocument) this.getDocument()).getPurapDocumentIdentifier().toString()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.PaymentRequestDocument.attributes.purapDocumentIdentifier", "Not Available"));
        }

        String applicationDocumentStatus = "Not Available";
        
        if (ObjectUtils.isNotNull(this.getPaymentRequestDocument().getApplicationDocumentStatus())) {
            applicationDocumentStatus = workflowDocument.getApplicationDocumentStatus();
        }

        getDocInfo().add(new HeaderField("DataDictionary.PaymentRequestDocument.attributes.applicationDocumentStatus", applicationDocumentStatus));
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
     * 
     * KRAD Conversion: Performs customization of an extra button.
     * 
     * No data dictionary is involved.
     */
    @Override
    public List<ExtraButton> getExtraButtons() {        
        extraButtons.clear(); // clear out the extra buttons array
        PaymentRequestDocument paymentRequestDocument = this.getPaymentRequestDocument();
        String externalImageURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
        String appExternalImageURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        
        if (canContinue()) {
            addExtraButton("methodToCall.continuePREQ", externalImageURL + "buttonsmall_continue.gif", "Continue");
            addExtraButton("methodToCall.clearInitFields", externalImageURL + "buttonsmall_clear.gif", "Clear");
        }
        else {
            if (getEditingMode().containsKey(PaymentRequestEditMode.HOLD)) {
                addExtraButton("methodToCall.addHoldOnPayment", appExternalImageURL + "buttonsmall_hold.gif", "Hold");
            }

            if (getEditingMode().containsKey(PaymentRequestEditMode.REMOVE_HOLD) && paymentRequestDocument.isHoldIndicator()) {
                addExtraButton("methodToCall.removeHoldFromPayment", appExternalImageURL + "buttonsmall_removehold.gif", "Remove");
            }

            if (getEditingMode().containsKey(PaymentRequestEditMode.REQUEST_CANCEL)) {
                addExtraButton("methodToCall.requestCancelOnPayment", appExternalImageURL + "buttonsmall_requestcancel.gif", "Cancel");
            }

            if (getEditingMode().containsKey(PaymentRequestEditMode.REMOVE_REQUEST_CANCEL) && paymentRequestDocument.isPaymentRequestedCancelIndicator()) {
                addExtraButton("methodToCall.removeCancelRequestFromPayment", appExternalImageURL + "buttonsmall_remreqcanc.gif", "Remove");
            }

            if (canCalculate()) {
                addExtraButton("methodToCall.calculate", appExternalImageURL + "buttonsmall_calculate.gif", "Calculate");
            }
            
            if (getEditingMode().containsKey(PaymentRequestEditMode.ACCOUNTS_PAYABLE_PROCESSOR_CANCEL) ||
                    getEditingMode().containsKey(PaymentRequestEditMode.ACCOUNTS_PAYABLE_MANAGER_CANCEL)) {
                if (PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED.equals(paymentRequestDocument.getPurchaseOrderDocument().getApplicationDocumentStatus())) {
                    //if the PO is CLOSED, show the 'open order' button; but only if there are no pending actions on the PO
                    if (!paymentRequestDocument.getPurchaseOrderDocument().isPendingActionIndicator()) {
                        addExtraButton("methodToCall.reopenPo", appExternalImageURL + "buttonsmall_openorder.gif", "Reopen PO");
                    }
                }
                else {
                    if (!paymentRequestDocument.getFinancialSystemDocumentHeader().getWorkflowDocument().isDisapproved()) {
                        addExtraButton("methodToCall.cancel", externalImageURL + "buttonsmall_cancel.gif", "Cancel");
                    }
                }
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
        boolean can = PaymentRequestStatuses.APPDOC_INITIATE.equals(getPaymentRequestDocument().getApplicationDocumentStatus());
        
        // check user authorization
        if (can) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(getPaymentRequestDocument());
            can = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PAYMENT_REQUEST, GlobalVariables.getUserSession().getPerson());
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
        can = can && documentActions.containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT);

        //FIXME this is temporary so that calculate will show up at tax
        can = can || editingMode.containsKey(PaymentRequestEditMode.TAX_AREA_EDITABLE);
        
        return can;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (KRADConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && 
           ("changeUseTaxIndicator".equals(methodToCallParameterValue))) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

}

