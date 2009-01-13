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
package org.kuali.kfs.module.purap.document.web.struts;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetLocation;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItemCapitalAsset;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetLocation;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.authorization.PurchaseOrderDocumentActionAuthorizer;
import org.kuali.kfs.module.purap.document.authorization.PurchasingDocumentActionAuthorizer;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Struts Action Form for Purchase Order document.
 */
public class PurchaseOrderForm extends PurchasingFormBase {
    private Integer purchaseOrderIdentifier;
    private PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine;
    private PurchaseOrderVendorQuote newPurchaseOrderVendorQuote;
    private Long awardedVendorNumber;
    PurchaseOrderDocumentActionAuthorizer auth;
    
    // Retransmit.
    private String[] retransmitItemsSelected = {};
    private String retransmitTransmissionMethod;
    private String retransmitFaxNumber;
    private String retransmitHeader;

    // Need this for amendment for accounting line only
    protected Map accountingLineEditingMode;
    
    private String splitNoteText;

    // Assign Sensitive Data related fields
    private boolean assigningSensitiveData = false; // flag to indicate whether the form is currently used for assigning sensitive data to the PO
    private String sensitiveDataAssignmentReason = null; // reason for current assignment of sensitive data to the PO
    private SensitiveDataAssignment lastSensitiveDataAssignment = null; // last sensitive data assignment info for the PO
    private SensitiveData newSensitiveDataLine = null; // new sensitive data entry to be added to the PO
    private List<SensitiveData> sensitiveDatasAssigned = null;  // sensitive data entries currently assigned to the PO

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
    
    @Override
    public PurchasingDocumentActionAuthorizer getAuth() {
        return auth;
    }

    public void setAuth(PurchaseOrderDocumentActionAuthorizer auth) {
        this.auth = auth;
    }

    public String getSplitNoteText() {
        return splitNoteText;
    }

    public void setSplitNoteText(String splitNoteText) {
        this.splitNoteText = splitNoteText;
    }       

    public boolean isAssigningSensitiveData() {
        return assigningSensitiveData;
    }

    public void setAssigningSensitiveData(boolean assigningSensitiveData) {
        this.assigningSensitiveData = assigningSensitiveData;
    }
    
    public String getSensitiveDataAssignmentReason() {
        return sensitiveDataAssignmentReason;
    }

    public void setSensitiveDataAssignmentReason(String sensitiveDataAssignmentReason) {
        this.sensitiveDataAssignmentReason = sensitiveDataAssignmentReason;
    }

    public SensitiveDataAssignment getLastSensitiveDataAssignment() {
        return lastSensitiveDataAssignment;
    }

    public void setLastSensitiveDataAssignment(SensitiveDataAssignment lastSensitiveDataAssignment) {
        this.lastSensitiveDataAssignment = lastSensitiveDataAssignment;
    }

    public SensitiveData getNewSensitiveDataLine() {
        return newSensitiveDataLine;
    }

    public void setNewSensitiveDataLine(SensitiveData newSensitiveDataLine) {
        this.newSensitiveDataLine = newSensitiveDataLine;
    }

    public List<SensitiveData> getSensitiveDatasAssigned() {
        return sensitiveDatasAssigned;
    }

    public void setSensitiveDatasAssigned(List<SensitiveData> poSensitiveData) {
        this.sensitiveDatasAssigned = poSensitiveData;
    }
    
    @Override
    public Class getCapitalAssetLocationClass() {
        return PurchaseOrderCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return PurchaseOrderItemCapitalAsset.class;
    }

    @Override
    public CapitalAssetLocation setupNewPurchasingCapitalAssetLocationLine() {
        CapitalAssetLocation location = new RequisitionCapitalAssetLocation();
        return location;
    }    
    
    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewPurchasingItemLine()
     */
    @Override
    public PurApItem setupNewPurchasingItemLine() {
        return new PurchaseOrderItem();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewPurchasingAccountingLine()
     */
    @Override
    public PurchaseOrderAccount setupNewPurchasingAccountingLine() {
        return new PurchaseOrderAccount();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewAccountDistributionAccountingLine()
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
        aPurchaseOrderVendorStipulationLine.setVendorStipulationAuthorEmployeeIdentifier(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

        return aPurchaseOrderVendorStipulationLine;
    }


    @Override
    public void populateHeaderFields(KualiWorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument().getPurapDocumentIdentifier())) {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", ((PurchaseOrderDocument) this.getDocument()).getPurapDocumentIdentifier().toString()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.purapDocumentIdentifier", "Not Available"));
        }
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument().getStatus())) {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.statusCode", ((PurchaseOrderDocument) this.getDocument()).getStatus().getStatusDescription()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.PurchaseOrderDocument.attributes.statusCode", "Not Available"));
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
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

        for (org.kuali.rice.kns.bo.Note note : (java.util.List<org.kuali.rice.kns.bo.Note>) po.getDocumentBusinessObject().getBoNotes()) {
            note.refreshReferenceObject("attachment");
        }        
    }
    
    /**
     * Override the superclass method to add appropriate buttons for
     * PurchaseOrderDocument.
     * 
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        // always refresh auth, to accommodate the change from request to scope obj, so that auth always gets refreshed; 
        // otherwise extra buttons won't show correctly
        PurchaseOrderDocument purchaseOrder = (PurchaseOrderDocument) this.getDocument();
        auth = new PurchaseOrderDocumentActionAuthorizer(purchaseOrder, getEditingMode(), getDocumentActions());
            
        //add buttons from PurapFormBase
        super.getExtraButtons();        
        String documentType = this.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentType();
        Map buttonsMap = createButtonsMap();                    
        
        // no other extra buttons except the following shall appear on "Assign Sensitive Data" page
        // and these buttons use the same permissions as the "Assign Sensitive Data" button
        if (isAssigningSensitiveData() && auth.canAssignSensitiveData()) {
            extraButtons.clear();
            ExtraButton submitSensitiveDataButton = (ExtraButton) buttonsMap.get("methodToCall.submitSensitiveData");
            extraButtons.add(submitSensitiveDataButton);
            ExtraButton cancelSensitiveDataButton = (ExtraButton) buttonsMap.get("methodToCall.cancelSensitiveData");
            extraButtons.add(cancelSensitiveDataButton);
            return extraButtons;
        }
                
        if (auth.canRetransmit()) {
            ExtraButton retransmitButton = (ExtraButton) buttonsMap.get("methodToCall.retransmitPo");    
            extraButtons.add(retransmitButton);
        }
        
        if (auth.canPrintRetransmit()) {
            ExtraButton printingRetransmitButton = (ExtraButton) buttonsMap.get("methodToCall.printingRetransmitPo");
            extraButtons.add(printingRetransmitButton);
        }

        if (auth.canPreviewPrintPo()) {
            ExtraButton printingPreviewButton = (ExtraButton) buttonsMap.get("methodToCall.printingPreviewPo");
            extraButtons.add(printingPreviewButton);
        }
        
        if (auth.canFirstTransmitPrintPo()) {
            ExtraButton printButton = (ExtraButton) buttonsMap.get("methodToCall.firstTransmitPrintPo");
            extraButtons.add(printButton);
        }

        if (auth.canReopen()) {
            ExtraButton reopenButton = (ExtraButton) buttonsMap.get("methodToCall.reopenPo");
            extraButtons.add(reopenButton);
        }

        if (auth.canClose()) {
            ExtraButton closeButton = (ExtraButton) buttonsMap.get("methodToCall.closePo");
            extraButtons.add(closeButton);
        }
        
        if (auth.canHoldPayment()) {
            ExtraButton paymentHoldButton = (ExtraButton) buttonsMap.get("methodToCall.paymentHoldPo");
            extraButtons.add(paymentHoldButton);
        }

        if (auth.canAmend()) {
            ExtraButton amendButton = (ExtraButton) buttonsMap.get("methodToCall.amendPo");
            extraButtons.add(amendButton);             
        }
        
        if (auth.canVoid()) {
            ExtraButton voidButton = (ExtraButton) buttonsMap.get("methodToCall.voidPo");
            extraButtons.add(voidButton);
        }
        
        if (auth.canRemoveHold()) {
            ExtraButton removeHoldButton = (ExtraButton) buttonsMap.get("methodToCall.removeHoldPo");
            extraButtons.add(removeHoldButton);
        }
        
        if (auth.canResendCxml()) {
            ExtraButton resendPoCxmlButton = (ExtraButton) buttonsMap.get("methodToCall.resendPoCxml");
            extraButtons.add(resendPoCxmlButton);
        }
        
        if (auth.canCreateReceiving()){
            ExtraButton receivingButton = (ExtraButton) buttonsMap.get("methodToCall.createReceivingLine");
            extraButtons.add(receivingButton);
        }
        
        if (auth.canSplitPo()){
            ExtraButton splitPoButton = (ExtraButton) buttonsMap.get("methodToCall.splitPo");
            extraButtons.add(splitPoButton);
        }
        
        if (auth.canContinuePoSplit()){
            ExtraButton continueButton = (ExtraButton) buttonsMap.get("methodToCall.continuePurchaseOrderSplit");
            extraButtons.add(continueButton);
            ExtraButton cancelSplitButton = (ExtraButton) buttonsMap.get("methodToCall.cancelPurchaseOrderSplit");
            extraButtons.add(cancelSplitButton);
        }
        
        if (auth.canAssignSensitiveData()){
            ExtraButton assignSensitiveDataButton = (ExtraButton) buttonsMap.get("methodToCall.assignSensitiveData");
            extraButtons.add(assignSensitiveDataButton);
        }

        return extraButtons;
    }        
    
    
    /**
     * Creates a MAP for all the buttons to appear on the Purchase Order Form, and sets the attributes of these buttons.
     * 
     * @return the button map created.
     */
    private Map<String, ExtraButton> createButtonsMap() {
        HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();
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

        // Printing Preview button
        ExtraButton printingPreviewButton = new ExtraButton();
        printingPreviewButton.setExtraButtonProperty("methodToCall.printingPreviewPo");
        printingPreviewButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_previewpf.gif");
        printingPreviewButton.setExtraButtonAltText("PrintingPreview");
        
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

        // Resend PO Cxml button
        ExtraButton resendPoCxmlButton = new ExtraButton();
        resendPoCxmlButton.setExtraButtonProperty("methodToCall.resendPoCxml");
        resendPoCxmlButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_resendpo.gif");
        resendPoCxmlButton.setExtraButtonAltText("Resend PO CXML");

        // Receiving button
        ExtraButton receivingButton = new ExtraButton();
        receivingButton.setExtraButtonProperty("methodToCall.createReceivingLine");
        receivingButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_receiving.gif");
        receivingButton.setExtraButtonAltText("Receiving");
        
        // Split PO button
        ExtraButton splitPoButton = new ExtraButton();
        splitPoButton.setExtraButtonProperty("methodToCall.splitPo");
        splitPoButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_splitorder.gif");
        splitPoButton.setExtraButtonAltText("Split this PO");
        
        // Continue button
        ExtraButton continueButton = new ExtraButton();
        continueButton.setExtraButtonProperty("methodToCall.continuePurchaseOrderSplit");
        continueButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_continue.gif");
        continueButton.setExtraButtonAltText("Continue");
        
        // Cancel Split button
        ExtraButton cancelSplitButton = new ExtraButton();
        cancelSplitButton.setExtraButtonProperty("methodToCall.cancelPurchaseOrderSplit");
        cancelSplitButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_cancelsplit.gif");
        cancelSplitButton.setExtraButtonAltText("Cancel Splitting the PO");
        
        // Assign Sensitive Data button
        ExtraButton assignSensitiveDataButton = new ExtraButton();
        assignSensitiveDataButton.setExtraButtonProperty("methodToCall.assignSensitiveData");
        assignSensitiveDataButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_sensitivedata.gif ");
        assignSensitiveDataButton.setExtraButtonAltText("Assign sensitive data to the PO");
        
        // Submit Sensitive Data Assignment button
        ExtraButton submitSensitiveDataButton = new ExtraButton();
        submitSensitiveDataButton.setExtraButtonProperty("methodToCall.submitSensitiveData");
        submitSensitiveDataButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_submit.gif");
        submitSensitiveDataButton.setExtraButtonAltText("Submit sensitive data assignment");
        
        // Cancel Sensitive Data Assignment button
        ExtraButton cancelSensitiveDataButton = new ExtraButton();
        cancelSensitiveDataButton.setExtraButtonProperty("methodToCall.cancelSensitiveData");
        cancelSensitiveDataButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_cancel.gif");
        cancelSensitiveDataButton.setExtraButtonAltText("Cancel sensitive data assignment");
        
        result.put(retransmitButton.getExtraButtonProperty(), retransmitButton);
        result.put(printingRetransmitButton.getExtraButtonProperty(), printingRetransmitButton);
        result.put(printingPreviewButton.getExtraButtonProperty(), printingPreviewButton);
        result.put(printButton.getExtraButtonProperty(), printButton);
        result.put(reopenButton.getExtraButtonProperty(), reopenButton);
        result.put(closeButton.getExtraButtonProperty(), closeButton);
        result.put(voidButton.getExtraButtonProperty(), voidButton);
        result.put(paymentHoldButton.getExtraButtonProperty(), paymentHoldButton);
        result.put(amendButton.getExtraButtonProperty(), amendButton);
        result.put(removeHoldButton.getExtraButtonProperty(), removeHoldButton);
        result.put(receivingButton.getExtraButtonProperty(), receivingButton);
        result.put(splitPoButton.getExtraButtonProperty(), splitPoButton);
        result.put(continueButton.getExtraButtonProperty(), continueButton);
        result.put(cancelSplitButton.getExtraButtonProperty(), cancelSplitButton);
        result.put(assignSensitiveDataButton.getExtraButtonProperty(), assignSensitiveDataButton);
        result.put(submitSensitiveDataButton.getExtraButtonProperty(), submitSensitiveDataButton);
        result.put(cancelSensitiveDataButton.getExtraButtonProperty(), cancelSensitiveDataButton);
        result.put(resendPoCxmlButton.getExtraButtonProperty(), resendPoCxmlButton);
        
        return result;
    }

    public String getStatusChange() {
        if (StringUtils.isNotEmpty(getPurchaseOrderDocument().getStatusChange())){
            return getPurchaseOrderDocument().getStatusChange();
        } else {
            if (StringUtils.equals(getPurchaseOrderDocument().getStatusCode(),PurapConstants.PurchaseOrderStatuses.IN_PROCESS)){
                return PurapConstants.PurchaseOrderStatuses.IN_PROCESS;
            } else if (StringUtils.equals(getPurchaseOrderDocument().getStatusCode(),PurapConstants.PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT)){
                return PurapConstants.PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT;
            }else if (StringUtils.equals(getPurchaseOrderDocument().getStatusCode(),PurapConstants.PurchaseOrderStatuses.WAITING_FOR_VENDOR)){
                return PurapConstants.PurchaseOrderStatuses.WAITING_FOR_VENDOR;   
            }else{
                return null;
            }
        }
    }

    public void setStatusChange(String statusChange) {
        getPurchaseOrderDocument().setStatusChange(statusChange);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (KNSConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && 
           ("printPurchaseOrderPDFOnly".equals(methodToCallParameterValue) || "printingRetransmitPoOnly".equals(methodToCallParameterValue))) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }
}

