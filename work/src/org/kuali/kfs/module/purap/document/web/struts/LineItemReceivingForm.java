/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class LineItemReceivingForm extends ReceivingFormBase {

    protected Integer purchaseOrderId;
    protected LineItemReceivingItem newLineItemReceivingItemLine;
    protected boolean fromPurchaseOrder = false;
    protected Boolean hideAddUnorderedItem = true;

    /**
     * Constructs a LineItemReceivingForm instance and sets up the appropriately casted document.
     */
    public LineItemReceivingForm() {
        super();

        this.setNewLineItemReceivingItemLine(setupNewLineItemReceivingItemLine());
        newLineItemReceivingItemLine.setItemTypeCode(PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "RCVL";
    }

    public LineItemReceivingDocument getLineItemReceivingDocument() {
        return (LineItemReceivingDocument) getDocument();
    }

    public void setLineItemReceivingDocument(LineItemReceivingDocument lineItemReceivingDocument) {
        setDocument(lineItemReceivingDocument);
    }

    public Integer getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Integer purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public LineItemReceivingItem setupNewLineItemReceivingItemLine() {
        return new LineItemReceivingItem();
    }

    public LineItemReceivingItem getNewLineItemReceivingItemLine() {
        return newLineItemReceivingItemLine;
    }

    public void setNewLineItemReceivingItemLine(LineItemReceivingItem newLineItemReceivingItemLine) {
        this.newLineItemReceivingItemLine = newLineItemReceivingItemLine;
    }

    @Override
    public void populateHeaderFields(WorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        
        //leave the first field blank to match the other PURAP docs
        getDocInfo().add(new HeaderField());

        String applicationDocumentStatus = PurapConstants.PURAP_APPLICATION_DOCUMENT_STATUS_NOT_AVAILABLE;
        
        if (ObjectUtils.isNotNull(this.getLineItemReceivingDocument().getAppDocStatus())) {
            applicationDocumentStatus = workflowDocument.getApplicationDocumentStatus();
        }
        
        getDocInfo().add(new HeaderField("DataDictionary.LineItemReceivingDocument.attributes.applicationDocumentStatus", applicationDocumentStatus));
    }

    /**
     * Override the superclass method to add appropriate buttons for LineItemReceivingDocument.
     *
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        extraButtons.clear();
        Map buttonsMap = createButtonsMap();

        String displayInitTab = (String) getEditingMode().get(PurapAuthorizationConstants.LineItemReceivingEditMode.DISPLAY_INIT_TAB);
        if (ObjectUtils.isNotNull(displayInitTab) && displayInitTab.equalsIgnoreCase("true")) {
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.continueReceivingLine"));
            extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.clearInitFields"));
        }
        else {
            if (canCreateCorrection()) {
                extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.createReceivingCorrection"));
            }
        }

        return extraButtons;
    }

    protected boolean canCreateCorrection() {
        Person user = GlobalVariables.getUserSession().getPerson();
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(KFSConstants.FinancialDocumentTypeCodes.CORRECTION_RECEIVING);
        boolean isUserAuthorized = documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.CORRECTION_RECEIVING, user);
        return SpringContext.getBean(ReceivingService.class).canCreateCorrectionReceivingDocument(getLineItemReceivingDocument()) && isUserAuthorized;
    }

    /**
     * Creates a MAP for all the buttons to appear on the Receiving Line Form, and sets the attributes of these buttons.
     *
     * @return the button map created.
     */
    protected Map<String, ExtraButton> createButtonsMap() {
        HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();

        // Continue button
        ExtraButton continueButton = new ExtraButton();
        continueButton.setExtraButtonProperty("methodToCall.continueReceivingLine");
        continueButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_continue.gif");
        continueButton.setExtraButtonAltText("Continue");
        result.put(continueButton.getExtraButtonProperty(), continueButton);

        // Clear button
        ExtraButton clearButton = new ExtraButton();
        clearButton.setExtraButtonProperty("methodToCall.clearInitFields");
        clearButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_clear.gif");
        clearButton.setExtraButtonAltText("Clear");
        result.put(clearButton.getExtraButtonProperty(), clearButton);

        // Correction button
        ExtraButton correctionButton = new ExtraButton();
        correctionButton.setExtraButtonProperty("methodToCall.createReceivingCorrection");
        correctionButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_correction.gif");
        correctionButton.setExtraButtonAltText("Correction");
        result.put(correctionButton.getExtraButtonProperty(), correctionButton);

        return result;
    }

    /**
     * Returns the new Receiving Item Line and resets it to null.
     *
     * @return the new Receiving Item Line.
     */
    public LineItemReceivingItem getAndResetNewReceivingItemLine() {
        LineItemReceivingItem receivingItemLine = getNewLineItemReceivingItemLine();
        setNewLineItemReceivingItemLine(setupNewReceivingItemLine());
        return receivingItemLine;
    }

    /**
     * This method should be overriden (or see accountingLines for an alternate way of doing this with newInstance)
     */
    public LineItemReceivingItem setupNewReceivingItemLine() {
        LineItemReceivingItem lineItemReceivingItem = new LineItemReceivingItem((LineItemReceivingDocument)getDocument());
        newLineItemReceivingItemLine.setItemTypeCode(PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE);
        return lineItemReceivingItem;
    }

    /**
     * Indicates if the clear and load quantity buttons can be shown, according to the
     * value of a system parameter.
     *
     * @return
     */
    public boolean isAbleToShowClearAndLoadQtyButtons(){
        return SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(LineItemReceivingDocument.class, PurapParameterConstants.SHOW_CLEAR_AND_LOAD_QTY_BUTTONS);
    }

    /**
     * Indicates if a warning should be given when users click "add unordered item" button, according to the system parameter.
     *
     * @return true if the parameter says YES; otherwise faluse.
     */
    public boolean shouldGiveAddUnorderedItemWarning(){
        return SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(LineItemReceivingDocument.class, PurapParameterConstants.UNORDERED_ITEM_WARNING_IND);
    }

    public boolean isFromPurchaseOrder() {
        return fromPurchaseOrder;
    }

    public void setFromPurchaseOrder(boolean fromPurchaseOrder) {
        this.fromPurchaseOrder = fromPurchaseOrder;
    }

    public Boolean getHideAddUnorderedItem() {
        return hideAddUnorderedItem;
    }

    public void setHideAddUnorderedItem(Boolean hideAddUnorderedItem) {
        this.hideAddUnorderedItem = hideAddUnorderedItem;
    }

}
