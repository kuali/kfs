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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.authorization.PaymentRequestDocumentActionAuthorizer;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

/**
 * Struts Action Form for Electroinc Invoice Reject document.
 */
public class ElectronicInvoiceRejectForm extends FinancialSystemTransactionalDocumentFormBase {

//    private PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine;

    /**
     * Constructs a PaymentRequestForm instance and sets up the appropriately casted document.
     */
    public ElectronicInvoiceRejectForm() {
        super();
        setDocument(new ElectronicInvoiceRejectDocument());
    }

    public ElectronicInvoiceRejectDocument getElectronicInvoiceRejectDocument() {
        return (ElectronicInvoiceRejectDocument) getDocument();
    }

    public void setElectronicInvoiceRejectDocument(ElectronicInvoiceRejectDocument eirDocument) {
        setDocument(eirDocument);
    }

    /**
     * Build additional payment request specific buttons and set extraButtons list.
     * 
     * @return - list of extra buttons to be displayed to the user
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        // clear out the extra buttons array
        extraButtons.clear();

        ElectronicInvoiceRejectDocument eirDoc = this.getElectronicInvoiceRejectDocument();

        String externalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
        String appExternalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);

//      if (eirDoc.isResearchIndicator()) {
          addExtraButton("methodToCall.startResearch", appExternalImageURL + "buttonsmall_research.gif", "Research");
//      } else {
          addExtraButton("methodToCall.completeResearch", appExternalImageURL + "buttonsmall_complresearch.gif", "Complete Research");
//      }
          
        //
//        if (this.getEditingMode().containsKey(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB)) {
//            if (this.getEditingMode().get(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB).equals("TRUE")) {
//                addExtraButton("methodToCall.continuePREQ", externalImageURL + "buttonsmall_continue.gif", "Continue");
//                addExtraButton("methodToCall.clearInitFields", externalImageURL + "buttonsmall_clear.gif", "Clear");
//
//            }
//            else {
//                PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(preqDoc);
//
//                // if preq holdable and user can put on hold, show button
//                if (preqDocAuth.canHold()) {
//                    addExtraButton("methodToCall.addHoldOnPayment", appExternalImageURL + "buttonsmall_hold.gif", "Hold");
//                }
//
//                // if person can remove hold
//                if (preqDocAuth.canRemoveHold()) {
//                    addExtraButton("methodToCall.removeHoldFromPayment", appExternalImageURL + "buttonsmall_removehold.gif", "Remove");
//                }
//
//                // if preq can have a cancel request and user can submit request cancel, show button
//                if (preqDocAuth.canRequestCancel()) {
//                    addExtraButton("methodToCall.requestCancelOnPayment", appExternalImageURL + "buttonsmall_requestcancel.gif", "Cancel");
//                }
//
//                // if person can remove request cancel
//                if (preqDocAuth.canRemoveRequestCancel()) {
//                    addExtraButton("methodToCall.removeCancelRequestFromPayment", appExternalImageURL + "buttonsmall_remreqcanc.gif", "Remove");
//                }
//
//                // add the calculate button
//                if (preqDocAuth.canCalculate()) {
//                    addExtraButton("methodToCall.calculate", appExternalImageURL + "buttonsmall_calculate.gif", "Calculate");
//                }
//            }
//        }

        return extraButtons;
    }

    /**
     * Adds a new button to the extra buttons collection.
     * 
     * @param property - property for button
     * @param source - location of image
     * @param altText - alternate text for button if images don't appear
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }
}
