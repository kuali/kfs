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
package org.kuali.module.purap.web.struts.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.web.ui.ExtraButton;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.bo.ReceivingLineItem;
import org.kuali.module.purap.document.ReceivingLineDocument;

public class ReceivingLineForm extends ReceivingFormBase {
    
    private String purchaseOrderDocId;
    private ReceivingLineItem newReceivingLineItemLine;
    
    /**
     * Constructs a ReceivingLineForm instance and sets up the appropriately casted document.
     */
    public ReceivingLineForm() {
        super();
        setDocument(new ReceivingLineDocument());

        this.setNewReceivingLineItemLine(setupNewReceivingLineItemLine());
        newReceivingLineItemLine.setItemTypeCode("ITEM");
    }

    public ReceivingLineDocument getReceivingLineDocument() {
        return (ReceivingLineDocument) getDocument();
    }

    public void setReceivingLineDocument(ReceivingLineDocument receivingLineDocument) {
        setDocument(receivingLineDocument);
    }

    public String getPurchaseOrderDocId() {
        return purchaseOrderDocId;
    }

    public void setPurchaseOrderDocId(String purchaseOrderDocId) {
        this.purchaseOrderDocId = purchaseOrderDocId;
    }

    public ReceivingLineItem setupNewReceivingLineItemLine() {
        return new ReceivingLineItem();
    }

    public ReceivingLineItem getNewReceivingLineItemLine() {
        return newReceivingLineItemLine;
    }

    public void setNewReceivingLineItemLine(ReceivingLineItem newReceivingLineItemLine) {
        this.newReceivingLineItemLine = newReceivingLineItemLine;
    }

    /**
     * Override the superclass method to add appropriate buttons for
     * ReceivingLineDocument.
     * 
     * @see org.kuali.core.web.struts.form.KualiForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        extraButtons.clear();
        Map buttonsMap = createButtonsMap();

        if (this.getEditingMode().containsKey(PurapAuthorizationConstants.ReceivingLineEditMode.DISPLAY_INIT_TAB)) {
            if (this.getEditingMode().get(PurapAuthorizationConstants.ReceivingLineEditMode.DISPLAY_INIT_TAB).equals("TRUE")) {
                ExtraButton continueButton = (ExtraButton) buttonsMap.get("methodToCall.continueReceivingLine");
                extraButtons.add(continueButton);                

                ExtraButton clearButton = (ExtraButton) buttonsMap.get("methodToCall.clearInitFields");
                extraButtons.add(clearButton);                
                
            }else{
                ExtraButton correctionButton = (ExtraButton) buttonsMap.get("methodToCall.createReceivingCorrection");
                extraButtons.add(correctionButton);                
            }
        }
        
        return extraButtons;
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

}
