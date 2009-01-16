/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.ExtraButton;

public class LineItemReceivingForm extends ReceivingFormBase {
    
    private Integer purchaseOrderId;
    private LineItemReceivingItem newLineItemReceivingItemLine;
    private boolean fromPurchaseOrder = false;
    
    /**
     * Constructs a LineItemReceivingForm instance and sets up the appropriately casted document.
     */
    public LineItemReceivingForm() {
        super();
        setDocument(new LineItemReceivingDocument());

        this.setNewLineItemReceivingItemLine(setupNewLineItemReceivingItemLine());
        newLineItemReceivingItemLine.setItemTypeCode(PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE);
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

    private boolean canCreateCorrection() {
        Person user = GlobalVariables.getUserSession().getPerson();
        String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(CorrectionReceivingDocument.class);
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(documentTypeName);
        boolean isUserAuthorized = documentAuthorizer.canInitiate(documentTypeName, user);
        return SpringContext.getBean(ReceivingService.class).canCreateCorrectionReceivingDocument(getLineItemReceivingDocument()) && isUserAuthorized;
    }

    /**
     * Creates a MAP for all the buttons to appear on the Receiving Line Form, and sets the attributes of these buttons.
     * 
     * @return the button map created.
     */
    private Map<String, ExtraButton> createButtonsMap() {
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
        return SpringContext.getBean(ParameterService.class).getIndicatorParameter(LineItemReceivingDocument.class, PurapParameterConstants.SHOW_CLEAR_AND_LOAD_QTY_BUTTONS);        
    }

    public boolean isFromPurchaseOrder() {
        return fromPurchaseOrder;
    }

    public void setFromPurchaseOrder(boolean fromPurchaseOrder) {
        this.fromPurchaseOrder = fromPurchaseOrder;
    }

}
