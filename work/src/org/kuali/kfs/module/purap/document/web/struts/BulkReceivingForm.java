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

import java.util.List;

import org.kuali.core.web.ui.ExtraButton;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.authorization.BulkReceivingDocumentActionAuthorizer;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

public class BulkReceivingForm extends FinancialSystemTransactionalDocumentFormBase {
    
    private Integer purchaseOrderId;
    private boolean isPOAvailable;
    
    /**
     * Constructs a BulkReceivingForm instance and sets up the appropriately casted document.
     */
    public BulkReceivingForm() {
        super();
        setDocument(new BulkReceivingDocument());

    }

    public BulkReceivingDocument getBulkReceivingDocument() {
        return (BulkReceivingDocument) getDocument();
    }

    public void setBulkReceivingDocument(BulkReceivingDocument bulkReceivingDocument) {
        setDocument(bulkReceivingDocument);
    }

    public Integer getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Integer purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public boolean isPOAvailable() {
        return isPOAvailable;
    }

    public void setPOAvailable(boolean isPOAvailable) {
        this.isPOAvailable = isPOAvailable;
    }
    
    /**
     * Override the superclass method to add appropriate buttons for
     * BulkReceivingDocument.
     * 
     * @see org.kuali.core.web.struts.form.KualiForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        extraButtons.clear();
        
        BulkReceivingDocumentActionAuthorizer auth = new BulkReceivingDocumentActionAuthorizer(this.getBulkReceivingDocument(), getEditingMode());        

        if (this.getEditingMode().containsKey(PurapAuthorizationConstants.BulkReceivingEditMode.DISPLAY_INIT_TAB)) {
            if (this.getEditingMode().get(PurapAuthorizationConstants.BulkReceivingEditMode.DISPLAY_INIT_TAB).equals("TRUE")) {
                extraButtons.add(createBulkReceivingContinueButton());                
                extraButtons.add(createClearInitFieldsButton());
            }
        }
        
        return extraButtons;
    }        

    private ExtraButton createBulkReceivingContinueButton(){
        ExtraButton continueButton = new ExtraButton();
        continueButton.setExtraButtonProperty("methodToCall.continueBulkReceiving");
        continueButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_continue.gif");
        continueButton.setExtraButtonAltText("Continue");
        return continueButton;
    }
    
    private ExtraButton createClearInitFieldsButton(){
        ExtraButton clearButton = new ExtraButton();
        clearButton.setExtraButtonProperty("methodToCall.clearInitFields");
        clearButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_clear.gif");
        clearButton.setExtraButtonAltText("Clear");
        return clearButton;
    }

    public boolean isStateFinal(){        
        return this.getDocument().getDocumentHeader().getWorkflowDocument().stateIsFinal();              
    }

}
