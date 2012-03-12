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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.ui.ExtraButton;

public class CustomerInvoiceWriteoffDocumentForm extends FinancialSystemTransactionalDocumentFormBase {
    
    public CustomerInvoiceWriteoffDocumentForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "INVW";
    }
    
    /**
     * Setup workflow doc in the document.
     */
    @Override
    public void populate(HttpServletRequest request) {
        
        //populate document using request
        super.populate(request);
        
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument)getDocument();
        String customerInvoiceNumber = customerInvoiceWriteoffDocument.getFinancialDocumentReferenceInvoiceNumber();
        
        //this will make sure that every action has fully populated invoice
        if(StringUtils.isNotEmpty(customerInvoiceNumber)){
            customerInvoiceWriteoffDocument.refreshReferenceObject("customerInvoiceDocument");
        }        
    }

    /**
     * Build additional customer credit memo specific buttons and set extraButtons list.
     * 
     * @return - list of extra buttons to be displayed to the user
     * 
     * KRAD Conversion: Performs the creation of extra buttons. 
     * No data dictionary is involved here. 
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        
        // clear out the extra buttons array
        extraButtons.clear();

        CustomerInvoiceWriteoffDocument writeoffDoc = (CustomerInvoiceWriteoffDocument) getDocument();
        DocumentHelperService documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        TransactionalDocumentPresentationController presoController = 
                (TransactionalDocumentPresentationController) documentHelperService.getDocumentPresentationController(writeoffDoc);
        Set<String> editModes = presoController.getEditModes(writeoffDoc);

        if (editModes.contains(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB)) {
            String externalImageURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
            addExtraButton("methodToCall.continueCustomerInvoiceWriteoff", externalImageURL + "buttonsmall_continue.gif", "Continue");
            addExtraButton("methodToCall.clearInitTab", externalImageURL + "buttonsmall_clear.gif", "Clear");
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
