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
