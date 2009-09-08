/*
 * Copyright 2007 The Kuali Foundation.
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

import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Struts Action Form for Electroinc Invoice Reject document.
 */
public class ElectronicInvoiceRejectForm extends FinancialSystemTransactionalDocumentFormBase {

//    private PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine;

    /**
     * Constructs a PaymentRequestForm instance and sets up the appropriately casted document.
     */
    public ElectronicInvoiceRejectForm() {
        super();
    }

    public ElectronicInvoiceRejectDocument getElectronicInvoiceRejectDocument() {
        return (ElectronicInvoiceRejectDocument) getDocument();
    }

    public void setElectronicInvoiceRejectDocument(ElectronicInvoiceRejectDocument eirDocument) {
        setDocument(eirDocument);
    }

    /**
     * Build additional electronic invoice specific buttons and set extraButtons list.
     * 
     * @return - list of extra buttons to be displayed to the user
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        // clear out the extra buttons array
        extraButtons.clear();

        ElectronicInvoiceRejectDocument eirDoc = this.getElectronicInvoiceRejectDocument();

        String externalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
        String appExternalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);

        if (eirDoc.getDocumentHeader().getWorkflowDocument().stateIsEnroute()){
          if (eirDoc.isInvoiceResearchIndicator()) {
              addExtraButton("methodToCall.completeResearch", appExternalImageURL + "buttonsmall_complresearch.gif", "Complete Research");
          } else {
              addExtraButton("methodToCall.startResearch", appExternalImageURL + "buttonsmall_research.gif", "Research");
          }
        }

        return extraButtons;
    }

    /**
     * Adds a new button to the extra buttons collection.
     * 
     * @param property - property for button
     * @param source - location of image
     * @param altText - alternate text for button if images don't appear
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }
    
}
