/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.web.struts.form;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * This class is the form class for the PurchaseOrder document.
 */
public class PurchaseOrderForm extends PurchasingFormBase {

    private PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine;
    private PurchaseOrderVendorQuote newPurchaseOrderVendorQuote;
    private Long awardedVendorNumber;
    
    // Retransmit.
    private String[] retransmitItemsSelected = {};
    private String retransmitTransmissionMethod;
    private String retransmitFaxNumber;
    private String retransmitHeader;
    
    
    //Need this for amendment for accounting line only
    protected Map accountingLineEditingMode;
    
    /**
     * Constructs a PurchaseOrderForm instance and sets up the appropriately casted document.
     */
    public PurchaseOrderForm() {
        super();
        setDocument(new PurchaseOrderDocument());
        this.setNewPurchasingItemLine(setupNewPurchasingItemLine());
        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());
        setNewPurchaseOrderVendorQuote(new PurchaseOrderVendorQuote());
        this.accountingLineEditingMode = new HashMap();
        this.accountingLineEditingMode.put(AuthorizationConstants.EditMode.FULL_ENTRY, "TRUE");
    }

    /**
     * @return Returns the PurchaseOrderDocument.
     */
    public PurchaseOrderDocument getPurchaseOrderDocument() {
        return (PurchaseOrderDocument) getDocument();
    }

    /**
     * @param purchaseOrderDocument The PurchaseOrderDocument to set.
     */
    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        setDocument(purchaseOrderDocument);
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo1()
     */
    public KeyLabelPair getAdditionalDocInfo1() {
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument().getPurapDocumentIdentifier())) {
            return new KeyLabelPair("DataDictionary.KualiPurchaseOrderDocument.attributes.purapDocumentIdentifier", ((PurchaseOrderDocument) this.getDocument()).getPurapDocumentIdentifier().toString());
        }
        else {
            return new KeyLabelPair("DataDictionary.KualiPurchaseOrderDocument.attributes.purapDocumentIdentifier", "Not Available");
        }
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo2()
     */
    public KeyLabelPair getAdditionalDocInfo2() {
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument().getStatus())) {
            return new KeyLabelPair("DataDictionary.KualiPurchaseOrderDocument.attributes.statusCode", ((PurchaseOrderDocument) this.getDocument()).getStatus().getStatusDescription());
        }
        else {
            return new KeyLabelPair("DataDictionary.KualiPurchaseOrderDocument.attributes.statusCode", "Not Available");
        }
    }

    /**
     * @see org.kuali.module.purap.web.struts.form.PurchasingFormBase#setupNewPurchasingItemLine()
     */
    @Override
    public PurchasingApItem setupNewPurchasingItemLine() {
        return new PurchaseOrderItem();
    }

    /**
     * @see org.kuali.module.purap.web.struts.form.PurchasingFormBase#setupNewPurchasingAccountingLine()
     */
    @Override
    public PurchaseOrderAccount setupNewPurchasingAccountingLine() {
        return new PurchaseOrderAccount();
    }

    public PurchaseOrderVendorStipulation getAndResetNewPurchaseOrderVendorStipulationLine() {
        PurchaseOrderVendorStipulation aPurchaseOrderVendorStipulationLine = getNewPurchaseOrderVendorStipulationLine();
        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());

        aPurchaseOrderVendorStipulationLine.setDocumentNumber(getPurchaseOrderDocument().getDocumentNumber());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationAuthorEmployeeIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationCreateDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());

        return aPurchaseOrderVendorStipulationLine;
    }

    public PurchaseOrderVendorStipulation getNewPurchaseOrderVendorStipulationLine() {
        return newPurchaseOrderVendorStipulationLine;
    }

    public void setNewPurchaseOrderVendorStipulationLine(PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine) {
        this.newPurchaseOrderVendorStipulationLine = newPurchaseOrderVendorStipulationLine;
    }

    public PurchaseOrderVendorQuote getNewPurchaseOrderVendorQuote() {
        return newPurchaseOrderVendorQuote;
    }

    public void setNewPurchaseOrderVendorQuote(PurchaseOrderVendorQuote newPurchaseOrderVendorQuote) {
        this.newPurchaseOrderVendorQuote = newPurchaseOrderVendorQuote;
    }

    public void addButtons() {
        // TODO: Find out and add logic about which buttons to appear in
        // which condition e.g. we might not want to display the close button
        // on a PO with status CLOSE or the open button on a PO with status OPEN, etc.


        String documentType = this.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentType();
        PurchaseOrderDocument purchaseOrder = (PurchaseOrderDocument) this.getDocument();

        
        if ((purchaseOrder.isPurchaseOrderCurrentIndicator() && purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN)) || 
             (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT) &&
              (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) || purchaseOrder.getDocumentHeader().getWorkflowDocument().stateIsEnroute()))) {

            ExtraButton retransmitButton = new ExtraButton();
            retransmitButton.setExtraButtonProperty("methodToCall.retransmitPo");
            retransmitButton.setExtraButtonSource("${kr.externalizable.images.url}buttonsmall_retransmit.gif");
            this.getExtraButtons().add(retransmitButton);
        }
        if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_PRINT_DOCUMENT)) {
            ExtraButton printButton = new ExtraButton();
            printButton.setExtraButtonProperty("methodToCall.printPo");
            printButton.setExtraButtonSource("${externalizable.images.url}buttonsmall_print.gif");
            this.getExtraButtons().add(printButton);
            ExtraButton paymentHoldButton = new ExtraButton();
            paymentHoldButton.setExtraButtonProperty("methodToCall.paymentHoldPo");
            paymentHoldButton.setExtraButtonSource("${externalizable.images.url}buttonsmall_paymenthold.gif");
            this.getExtraButtons().add(paymentHoldButton);
        }
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CLOSED) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator()) {
            ExtraButton reopenButton = new ExtraButton();
            reopenButton.setExtraButtonProperty("methodToCall.reopenPo");
            reopenButton.setExtraButtonSource("${externalizable.images.url}buttonsmall_openorder.gif");
            this.getExtraButtons().add(reopenButton);
        }
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator()) {
            ExtraButton closeButton = new ExtraButton();
            closeButton.setExtraButtonProperty("methodToCall.closePo");
            closeButton.setExtraButtonSource("${externalizable.images.url}buttonsmall_closeorder.gif");
            this.getExtraButtons().add(closeButton);
            ExtraButton voidButton = new ExtraButton();
            voidButton.setExtraButtonProperty("methodToCall.voidPo");
            voidButton.setExtraButtonSource("${externalizable.images.url}buttonsmall_voidorder.gif");
            this.getExtraButtons().add(voidButton);
            ExtraButton paymentHoldButton = new ExtraButton();
            paymentHoldButton.setExtraButtonProperty("methodToCall.paymentHoldPo");
            paymentHoldButton.setExtraButtonSource("${externalizable.images.url}buttonsmall_paymenthold.gif");
            this.getExtraButtons().add(paymentHoldButton);
            ExtraButton amendButton = new ExtraButton();
            amendButton.setExtraButtonProperty("methodToCall.amendPo");
            amendButton.setExtraButtonSource("${externalizable.images.url}buttonsmall_amend.gif");
            this.getExtraButtons().add(amendButton);
        }
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator()) {
            ExtraButton removeHoldButton = new ExtraButton();
            removeHoldButton.setExtraButtonProperty("methodToCall.removeHoldPo");
            removeHoldButton.setExtraButtonSource("${externalizable.images.url}buttonsmall_removehold.gif");
            this.getExtraButtons().add(removeHoldButton);
        }

    }

    /**
     * @return Returns the retransmitItemsSelected.
     */
    public String[] getRetransmitItemsSelected() {
      return retransmitItemsSelected;
    }
    /**
     * @param retransmitItemsSelected The retransmitItemsSelected to set.
     */
    public void setRetransmitItemsSelected(String[] retransmitItemsSelected) {
      this.retransmitItemsSelected = retransmitItemsSelected;
    }

    public Map getAccountingLineEditingMode() {
        return accountingLineEditingMode;
    }

    public void setAccountingLineEditingMode(Map accountingLineEditingMode) {
        this.accountingLineEditingMode = accountingLineEditingMode;
    }

    public Long getAwardedVendorNumber() {
        return awardedVendorNumber;
    }

    public void setAwardedVendorNumber(Long awardedVendorNumber) {
        this.awardedVendorNumber = awardedVendorNumber;
    }
    
    
}