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
import java.util.Map;

import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.PurApWorkflowIntegrationService;

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

        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());
        setNewPurchaseOrderVendorQuote(new PurchaseOrderVendorQuote());
        this.accountingLineEditingMode = new HashMap();
        
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
            return new KeyLabelPair("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", ((PurchaseOrderDocument) this.getDocument()).getPurapDocumentIdentifier().toString());
        }
        else {
            return new KeyLabelPair("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", "Not Available");
        }
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo2()
     */
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
     * @see org.kuali.module.purap.web.struts.form.PurchasingFormBase#setupNewPurchasingAccountingLine()
     */
    @Override
    public PurchaseOrderAccount setupNewAccountDistributionAccountingLine() {
        PurchaseOrderAccount account = setupNewPurchasingAccountingLine();
        account.setAccountLinePercent(new BigDecimal(100));
        return account;
    }
    
    public PurchaseOrderVendorStipulation getAndResetNewPurchaseOrderVendorStipulationLine() {
        PurchaseOrderVendorStipulation aPurchaseOrderVendorStipulationLine = getNewPurchaseOrderVendorStipulationLine();
        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());

        aPurchaseOrderVendorStipulationLine.setDocumentNumber(getPurchaseOrderDocument().getDocumentNumber());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationAuthorEmployeeIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

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
        
        // TODO: Think about using a MAP with key of 'methodToCall' string and value of ExtraButton object so that button duplication never occurs
        //       Only problem would be if same 'methodToCall' string was used on two different button images... in which case an if-else could be fix

        String documentType = this.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentType();
        PurchaseOrderDocument purchaseOrder = (PurchaseOrderDocument) this.getDocument();

        String authorizedWorkgroup = SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.Workgroups.PURAP_DOCUMENT_PO_ACTIONS);
        boolean isUserAuthorized = false;
        try {
            isUserAuthorized = SpringContext.getBean(KualiGroupService.class).getByGroupName(authorizedWorkgroup).hasMember(GlobalVariables.getUserSession().getUniversalUser());
        }
        catch (GroupNotFoundException e) {
            throw new RuntimeException("Workgroup " + authorizedWorkgroup + " not found",e);
        }
        
        if (isUserAuthorized &&(purchaseOrder.isPurchaseOrderCurrentIndicator() && purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN)) || 
             (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT) &&
              purchaseOrder.isPurchaseOrderCurrentIndicator() &&       
              !this.getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB) &&
              (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) || purchaseOrder.getDocumentHeader().getWorkflowDocument().stateIsEnroute()))) {

            ExtraButton retransmitButton = new ExtraButton();
            retransmitButton.setExtraButtonProperty("methodToCall.retransmitPo");
            retransmitButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_retransmit.gif");
            retransmitButton.setExtraButtonAltText("Retransmit");
            this.getExtraButtons().add(retransmitButton);
        }
        
        //This is the button to print the pdf on a retransmit document. We're currently sharing the same button image as 
        //the button for creating a retransmit document but this may change someday.
        if (isUserAuthorized && this.getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB)) {
            ExtraButton printingRetransmitButton = new ExtraButton();
            printingRetransmitButton.setExtraButtonProperty("methodToCall.printingRetransmitPo");
            printingRetransmitButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_retransmit.gif");
            printingRetransmitButton.setExtraButtonAltText("PrintingRetransmit");
            this.getExtraButtons().add(printingRetransmitButton);
        }
        
        // show the PO Print button if the status is Pending Print and the user is either authorized or an action is requested of them
        // for the document transmission route node
        boolean isDocumentTransmissionActionRequested = SpringContext.getBean(PurApWorkflowIntegrationService.class).isActionRequestedOfUserAtNodeName(purchaseOrder.getDocumentNumber(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), GlobalVariables.getUserSession().getUniversalUser());
        if ( PurapConstants.PurchaseOrderStatuses.PENDING_PRINT.equals(purchaseOrder.getStatusCode()) &&
                (isUserAuthorized ||  isDocumentTransmissionActionRequested) ) {
            ExtraButton printButton = new ExtraButton();
            printButton.setExtraButtonProperty("methodToCall.firstTransmitPrintPo");
            printButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_print.gif");
            printButton.setExtraButtonAltText("Print");
            this.getExtraButtons().add(printButton);
        }
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CLOSED) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            ExtraButton reopenButton = new ExtraButton();
            reopenButton.setExtraButtonProperty("methodToCall.reopenPo");
            reopenButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_openorder.gif");
            reopenButton.setExtraButtonAltText("Reopen");
            this.getExtraButtons().add(reopenButton);
        }
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            ExtraButton closeButton = new ExtraButton();
            closeButton.setExtraButtonProperty("methodToCall.closePo");
            closeButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_closeorder.gif");
            closeButton.setExtraButtonAltText("Close PO");
            this.getExtraButtons().add(closeButton);
            ExtraButton voidButton = new ExtraButton();
            voidButton.setExtraButtonProperty("methodToCall.voidPo");
            voidButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_voidorder.gif");
            voidButton.setExtraButtonAltText("Void PO");
            this.getExtraButtons().add(voidButton);
            ExtraButton paymentHoldButton = new ExtraButton();
            paymentHoldButton.setExtraButtonProperty("methodToCall.paymentHoldPo");
            paymentHoldButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_paymenthold.gif");
            paymentHoldButton.setExtraButtonAltText("Payment Hold");
            this.getExtraButtons().add(paymentHoldButton);
            ExtraButton amendButton = new ExtraButton();
            amendButton.setExtraButtonProperty("methodToCall.amendPo");
            amendButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_amend.gif");
            amendButton.setExtraButtonAltText("Amend");
            this.getExtraButtons().add(amendButton);
        }
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            ExtraButton removeHoldButton = new ExtraButton();
            removeHoldButton.setExtraButtonProperty("methodToCall.removeHoldPo");
            removeHoldButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_removehold.gif");
            removeHoldButton.setExtraButtonAltText("Remove Hold");
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