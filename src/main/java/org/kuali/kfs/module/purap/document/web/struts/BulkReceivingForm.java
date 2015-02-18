/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
