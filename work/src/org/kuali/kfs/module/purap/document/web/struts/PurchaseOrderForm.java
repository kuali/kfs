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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchaseOrderRetransmitDocument;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurApWorkflowIntegrationService;

/**
 * Struts Action Form for Purchase Order document.
 */
public class PurchaseOrderForm extends PurchasingFormBase {
    private Integer purchaseOrderIdentifier;
    private PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine;
    private PurchaseOrderVendorQuote newPurchaseOrderVendorQuote;
    private Long awardedVendorNumber;

    // Retransmit.
    private String[] retransmitItemsSelected = {};
    private String retransmitTransmissionMethod;
    private String retransmitFaxNumber;
    private String retransmitHeader;

    // Need this for amendment for accounting line only
    protected Map accountingLineEditingMode;

    /**
     * Constructs a PurchaseOrderForm instance and sets up the appropriately casted document.
     */
    public PurchaseOrderForm() {
        super();
        setDocument(new PurchaseOrderDocument());

        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());
        setNewPurchaseOrderVendorQuote(new PurchaseOrderVendorQuote());
        this.accountingLineEditingMode = new HashMap();

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

    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    public String[] getRetransmitItemsSelected() {
        return retransmitItemsSelected;
    }

    public void setRetransmitItemsSelected(String[] retransmitItemsSelected) {
        this.retransmitItemsSelected = retransmitItemsSelected;
    }

    public PurchaseOrderDocument getPurchaseOrderDocument() {
        return (PurchaseOrderDocument) getDocument();
    }

    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        setDocument(purchaseOrderDocument);
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo1()
     */
    @Override
    public KeyLabelPair getAdditionalDocInfo1() {
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument().getPurapDocumentIdentifier())) {
            return new KeyLabelPair("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", ((PurchaseOrderDocument) this.getDocument()).getPurapDocumentIdentifier().toString());
        }
        else {
            return new KeyLabelPair("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", "Not Available");
        }
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo2()
     */
    @Override
    public KeyLabelPair getAdditionalDocInfo2() {
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument().getStatus())) {
            return new KeyLabelPair("DataDictionary.PurchaseOrderDocument.attributes.statusCode", ((PurchaseOrderDocument) this.getDocument()).getStatus().getStatusDescription());
        }
        else {
            return new KeyLabelPair("DataDictionary.PurchaseOrderDocument.attributes.statusCode", "Not Available");
        }
    }

    /**
     * @see org.kuali.module.purap.web.struts.form.PurchasingFormBase#setupNewPurchasingItemLine()
     */
    @Override
    public PurApItem setupNewPurchasingItemLine() {
        return new PurchaseOrderItem();
    }

    /**
     * @see org.kuali.module.purap.web.struts.form.PurchasingFormBase#setupNewPurchasingAccountingLine()
     */
    @Override
    public PurchaseOrderAccount setupNewPurchasingAccountingLine() {
        return new PurchaseOrderAccount();
    }

    /**
     * @see org.kuali.module.purap.web.struts.form.PurchasingFormBase#setupNewAccountDistributionAccountingLine()
     */
    @Override
    public PurchaseOrderAccount setupNewAccountDistributionAccountingLine() {
        PurchaseOrderAccount account = setupNewPurchasingAccountingLine();
        account.setAccountLinePercent(new BigDecimal(100));
        return account;
    }

    /**
     * Returns the new Purchase Order Vendor Stipulation Line and resets it.
     * 
     * @return the new Purchase Order Vendor Stipulation Line.
     */
    public PurchaseOrderVendorStipulation getAndResetNewPurchaseOrderVendorStipulationLine() {
        PurchaseOrderVendorStipulation aPurchaseOrderVendorStipulationLine = getNewPurchaseOrderVendorStipulationLine();
        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());

        aPurchaseOrderVendorStipulationLine.setDocumentNumber(getPurchaseOrderDocument().getDocumentNumber());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationAuthorEmployeeIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

        return aPurchaseOrderVendorStipulationLine;
    }

    /**
     * Adds buttons to appear on the Purchase Order Form according to certain conditions.
     */
    public void addButtons() {
        Map buttonsMap = createButtonsMap();

        String documentType = this.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentType();
        PurchaseOrderDocument purchaseOrder = (PurchaseOrderDocument) this.getDocument();

        List<PaymentRequestDocument> pReqs = SpringContext.getBean(PaymentRequestService.class).getPaymentRequestsByPurchaseOrderId(purchaseOrder.getPurapDocumentIdentifier());
        boolean hasPaymentRequest = ((pReqs != null && pReqs.size() > 0) ? true : false);

        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(PurchaseOrderDocument.class, PurapParameterConstants.Workgroups.PURAP_DOCUMENT_PO_ACTIONS);
        boolean isUserAuthorized = false;

        try {
            isUserAuthorized = SpringContext.getBean(KualiGroupService.class).getByGroupName(authorizedWorkgroup).hasMember(GlobalVariables.getUserSession().getUniversalUser());
        }
        catch (GroupNotFoundException e) {
            throw new RuntimeException("Workgroup " + authorizedWorkgroup + " not found", e);
        }

        if (purchaseOrder.getPurchaseOrderLastTransmitDate() != null && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && (isUserAuthorized || purchaseOrder.getPurchaseOrderAutomaticIndicator())) {
            ExtraButton retransmitButton = (ExtraButton) buttonsMap.get("methodToCall.retransmitPo");
            this.getExtraButtons().add(retransmitButton);
        }

        // This is the button to print the pdf on a retransmit document. We're currently sharing the same button image as
        // the button for creating a retransmit document but this may change someday. It should only appear on Retransmit
        // Document.
        if ((isUserAuthorized || purchaseOrder.getPurchaseOrderAutomaticIndicator()) && this.getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB) && (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)) && purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CHANGE_IN_PROCESS)) {
            ExtraButton printingRetransmitButton = (ExtraButton) buttonsMap.get("methodToCall.printingRetransmitPo");
            this.getExtraButtons().add(printingRetransmitButton);
        }

        // show the PO Print button if the status is Pending Print and the user is either authorized
        // or an action is requested of them for the document transmission route node
        boolean isDocumentTransmissionActionRequested = SpringContext.getBean(PurApWorkflowIntegrationService.class).isActionRequestedOfUserAtNodeName(purchaseOrder.getDocumentNumber(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), GlobalVariables.getUserSession().getUniversalUser());
        if (PurapConstants.PurchaseOrderStatuses.PENDING_PRINT.equals(purchaseOrder.getStatusCode()) && (isUserAuthorized || isDocumentTransmissionActionRequested)) {
            ExtraButton printButton = (ExtraButton) buttonsMap.get("methodToCall.firstTransmitPrintPo");
            this.getExtraButtons().add(printButton);
        }

        //This is so that the user can still do the print po if the transmission method is changed to PRINT during amendment, so that
        //we can fill in the last transmit date to some dates.
        if (purchaseOrder.getPurchaseOrderTransmissionMethodCode() != null && purchaseOrder.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.POTransmissionMethods.PRINT) && purchaseOrder.getPurchaseOrderLastTransmitDate() == null && purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && purchaseOrder.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
            ExtraButton printButton = (ExtraButton) buttonsMap.get("methodToCall.firstTransmitPrintPo");
            this.getExtraButtons().add(printButton);
        }
        
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CLOSED) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            ExtraButton reopenButton = (ExtraButton) buttonsMap.get("methodToCall.reopenPo");
            this.getExtraButtons().add(reopenButton);
        }

        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator()) {
            // To display the close PO button, the PO must be in Open status, the PO must have at least 1 Payment Request in any
            // other
            // statuses than "In Process" status, and the PO cannot have any Payment Requests in "In Process" status. This button
            // is available to all faculty/staff (everyone ?)
            boolean validForDisplayingCloseButton = true;

            if (ObjectUtils.isNotNull(pReqs)) {
                if (pReqs.size() == 0) {
                    validForDisplayingCloseButton = false;
                }
                else {
                    // None of the PREQs against this PO may be in 'In Process' status.
                    for (PaymentRequestDocument pReq : pReqs) {
                        if (StringUtils.equalsIgnoreCase(pReq.getStatusCode(), PaymentRequestStatuses.IN_PROCESS)) {
                            validForDisplayingCloseButton = false;
                        }
                    }
                }
            }

            if (validForDisplayingCloseButton) {
                ExtraButton closeButton = (ExtraButton) buttonsMap.get("methodToCall.closePo");
                this.getExtraButtons().add(closeButton);
            }

            // These buttons are only available to members of kuali purap purchasing.
            if (isUserAuthorized) {
                ExtraButton paymentHoldButton = (ExtraButton) buttonsMap.get("methodToCall.paymentHoldPo");
                this.getExtraButtons().add(paymentHoldButton);
                ExtraButton amendButton = (ExtraButton) buttonsMap.get("methodToCall.amendPo");
                this.getExtraButtons().add(amendButton);
            }
        }

        // Conditions for displaying Void button (in addition to the purchaseOrder current indicator is true and
        // pending indicator is false and the user is member of kuali purap purchasing):
        // 1. If the PO is in Pending Print status, or
        // 2. If the PO is in Open status and has no PREQs against it.
        if (purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.PENDING_PRINT) || (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && !hasPaymentRequest)) {
                ExtraButton voidButton = (ExtraButton) buttonsMap.get("methodToCall.voidPo");
                this.getExtraButtons().add(voidButton);
            }
        }

        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            ExtraButton removeHoldButton = (ExtraButton) buttonsMap.get("methodToCall.removeHoldPo");
            this.getExtraButtons().add(removeHoldButton);
        }
    }

    /**
     * Creates a MAP for all the buttons to appear on the Purchase Order Form, and sets the attributes of these buttons.
     * 
     * @return the button map created.
     */
    private Map<String, ExtraButton> createButtonsMap() {
        HashMap<String, ExtraButton> result = new HashMap();
        // Retransmit button
        ExtraButton retransmitButton = new ExtraButton();
        retransmitButton.setExtraButtonProperty("methodToCall.retransmitPo");
        retransmitButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_retransmit.gif");
        retransmitButton.setExtraButtonAltText("Retransmit");

        // Printing Retransmit button
        ExtraButton printingRetransmitButton = new ExtraButton();
        printingRetransmitButton.setExtraButtonProperty("methodToCall.printingRetransmitPo");
        printingRetransmitButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_retransmit.gif");
        printingRetransmitButton.setExtraButtonAltText("PrintingRetransmit");

        // Print button
        ExtraButton printButton = new ExtraButton();
        printButton.setExtraButtonProperty("methodToCall.firstTransmitPrintPo");
        printButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_print.gif");
        printButton.setExtraButtonAltText("Print");

        // Reopen PO button
        ExtraButton reopenButton = new ExtraButton();
        reopenButton.setExtraButtonProperty("methodToCall.reopenPo");
        reopenButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_openorder.gif");
        reopenButton.setExtraButtonAltText("Reopen");

        // Close PO button
        ExtraButton closeButton = new ExtraButton();
        closeButton.setExtraButtonProperty("methodToCall.closePo");
        closeButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_closeorder.gif");
        closeButton.setExtraButtonAltText("Close PO");

        // Void PO button
        ExtraButton voidButton = new ExtraButton();
        voidButton.setExtraButtonProperty("methodToCall.voidPo");
        voidButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_voidorder.gif");
        voidButton.setExtraButtonAltText("Void PO");

        // Payment Hold PO button
        ExtraButton paymentHoldButton = new ExtraButton();
        paymentHoldButton.setExtraButtonProperty("methodToCall.paymentHoldPo");
        paymentHoldButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_paymenthold.gif");
        paymentHoldButton.setExtraButtonAltText("Payment Hold");

        // Amend button
        ExtraButton amendButton = new ExtraButton();
        amendButton.setExtraButtonProperty("methodToCall.amendPo");
        amendButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_amend.gif");
        amendButton.setExtraButtonAltText("Amend");

        // Remove Hold button
        ExtraButton removeHoldButton = new ExtraButton();
        removeHoldButton.setExtraButtonProperty("methodToCall.removeHoldPo");
        removeHoldButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_removehold.gif");
        removeHoldButton.setExtraButtonAltText("Remove Hold");

        result.put(retransmitButton.getExtraButtonProperty(), retransmitButton);
        result.put(printingRetransmitButton.getExtraButtonProperty(), printingRetransmitButton);
        result.put(printButton.getExtraButtonProperty(), printButton);
        result.put(reopenButton.getExtraButtonProperty(), reopenButton);
        result.put(closeButton.getExtraButtonProperty(), closeButton);
        result.put(voidButton.getExtraButtonProperty(), voidButton);
        result.put(paymentHoldButton.getExtraButtonProperty(), paymentHoldButton);
        result.put(amendButton.getExtraButtonProperty(), amendButton);
        result.put(removeHoldButton.getExtraButtonProperty(), removeHoldButton);

        return result;
    }

    /**
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        PurchaseOrderDocument po = (PurchaseOrderDocument) this.getDocument();

        // call this to make sure it's refreshed from the database if need be since the populate setter doesn't do that
        po.getDocumentBusinessObject();

        super.populate(request);

        if (ObjectUtils.isNotNull(po.getPurapDocumentIdentifier())) {
            po.refreshDocumentBusinessObject();
        }

        for (org.kuali.core.bo.Note note : (java.util.List<org.kuali.core.bo.Note>) po.getDocumentBusinessObject().getBoNotes()) {
            note.refreshReferenceObject("attachment");
        }
    }
}