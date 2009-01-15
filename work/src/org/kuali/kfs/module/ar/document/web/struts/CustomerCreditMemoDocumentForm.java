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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.web.ui.ExtraButton;

public class CustomerCreditMemoDocumentForm extends FinancialSystemTransactionalDocumentFormBase {
   
    public CustomerCreditMemoDocumentForm() {
        super();
        setDocument(new CustomerCreditMemoDocument());
    }
    
    /**
     * Setup workflow doc in the document.
     */
    @Override
    public void populate(HttpServletRequest request) {
        
        //populate document using request
        super.populate(request);
        
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)getDocument();
        String customerInvoiceNumber = customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber();
        
        //this will make sure that every action has fully populated invoice
        if(StringUtils.isNotEmpty(customerInvoiceNumber)){
            customerCreditMemoDocument.refreshReferenceObject("invoice");
        }        
    }

    /**
     * Build additional customer credit memo specific buttons and set extraButtons list.
     * 
     * @return - list of extra buttons to be displayed to the user
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        
        // clear out the extra buttons array
        extraButtons.clear();

        CustomerCreditMemoDocument creditMemoDoc = (CustomerCreditMemoDocument) getDocument();
        DocumentHelperService docHelperService = SpringContext.getBean(DocumentHelperService.class);
        TransactionalDocumentPresentationController presoController = 
                (TransactionalDocumentPresentationController) docHelperService.getDocumentPresentationController(creditMemoDoc);
        Set<String> editModes = presoController.getEditModes(creditMemoDoc);

        //  special buttons for the first 'init' screen
        if (editModes.contains(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB)) {
            String externalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
            addExtraButton("methodToCall.continueCreditMemo", externalImageURL + "buttonsmall_continue.gif", "Continue");
            addExtraButton("methodToCall.clearInitTab", externalImageURL + "buttonsmall_clear.gif", "Clear");
        }
        
        //  show the print button if appropriate 
        if (editModes.contains(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_PRINT_BUTTON)) {
            String printButtonURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
            addExtraButton("methodToCall.print", printButtonURL + "buttonsmall_genprintfile.gif", "Print");
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
