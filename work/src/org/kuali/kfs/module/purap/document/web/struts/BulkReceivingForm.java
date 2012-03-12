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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class BulkReceivingForm extends FinancialSystemTransactionalDocumentFormBase {
    
    protected static final Logger LOG = Logger.getLogger(BulkReceivingForm.class); 
    protected Integer purchaseOrderId;

    public BulkReceivingForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "RCVB";
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

    /**
     * Override the superclass method to add appropriate buttons for
     * BulkReceivingDocument.
     * 
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getExtraButtons()
     * 
     * KRAD Conversion: Performs customization of extra buttons.
     * 
     * No data dictionary is involved.
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        extraButtons.clear();
        
        String displayInitTab = (String)getEditingMode().get(PurapAuthorizationConstants.BulkReceivingEditMode.DISPLAY_INIT_TAB);
        if (ObjectUtils.isNotNull(displayInitTab) && displayInitTab.equalsIgnoreCase("true")) {
            extraButtons.add(createBulkReceivingContinueButton());
            extraButtons.add(createClearInitFieldsButton());
        }
        else if (getBulkReceivingDocument().getDocumentHeader().getWorkflowDocument().isEnroute() || 
                getBulkReceivingDocument().getDocumentHeader().getWorkflowDocument().isProcessed() || 
                getBulkReceivingDocument().getDocumentHeader().getWorkflowDocument().isFinal()) {
            extraButtons.add(createPrintReceivingTicketButton());
        }
            
        return extraButtons;
    }        

    /**
    * KRAD Conversion: Performs customization of an extra button.
    * 
    * No data dictionary is involved.
    */
    protected ExtraButton createBulkReceivingContinueButton(){
        ExtraButton continueButton = new ExtraButton();
        continueButton.setExtraButtonProperty("methodToCall.continueBulkReceiving");
        continueButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_continue.gif");
        continueButton.setExtraButtonAltText("Continue");
        return continueButton;
    }
    
    /**
     * KRAD Conversion: Performs customization of an extra button.
     * 
     * No data dictionary is involved.
     */
    protected ExtraButton createClearInitFieldsButton(){
        ExtraButton clearButton = new ExtraButton();
        clearButton.setExtraButtonProperty("methodToCall.clearInitFields");
        clearButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_clear.gif");
        clearButton.setExtraButtonAltText("Clear");
        return clearButton;
    }
    
    /**
     * KRAD Conversion: Performs customization of an extra button.
     * 
     * No data dictionary is involved.
     */
    protected ExtraButton createPrintReceivingTicketButton(){
        ExtraButton printButton = new ExtraButton();
        printButton.setExtraButtonProperty("methodToCall.printReceivingTicketPDF");
        printButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_print.gif");
        printButton.setExtraButtonAltText("Print");
        return printButton;
    }

    public String getGoodsDeliveredByLabel(){
        return PurapKeyConstants.MESSAGE_BULK_RECEIVING_GOODSDELIVEREDBY_LABEL;
    }

    
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (KRADConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && "printReceivingTicket".equals(methodToCallParameterValue)) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }
}
