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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;

public class CustomerInvoiceWriteoffDocumentForm extends FinancialSystemTransactionalDocumentFormBase {
    
    private boolean hideDetails;
    protected FormFile sourceFile;
    private List baselineSourceAccountingLines;
    private Map forcedLookupOptionalFields;

    public CustomerInvoiceWriteoffDocumentForm() {
        super();
        setDocument(new CustomerInvoiceWriteoffDocument());
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
        
        //Doing this to display source accounting lines
        baselineSourceAccountingLines = new ArrayList();
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

        String externalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
 
        if (getEditingMode().containsKey(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB)) {
            if (getEditingMode().get(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB).equals("TRUE")) {
                addExtraButton("methodToCall.continueCustomerInvoiceWriteoff", externalImageURL + "buttonsmall_continue.gif", "Continue");
                addExtraButton("methodToCall.clearInitTab", externalImageURL + "buttonsmall_clear.gif", "Clear");
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
    
    /**
     * @return current Map of editableAccounts
     */
    public Map getEditableAccounts() {
        return new HashMap();
    }
    
    /**
     * @return String
     */
    public String getAccountingLineImportInstructionsUrl() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY) + SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.FINANCIAL_SYSTEM_DOCUMENT.class, KFSConstants.FinancialApcParms.ACCOUNTING_LINE_IMPORT_HELP);
    }
    
    
    /**
     * @return hideDetails attribute
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * @return hideDetails attribute
     * @see #isHideDetails()
     */
    public boolean getHideDetails() {
        return isHideDetails();
    }

    /**
     * @param hideDetails
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }
    
    /**
     * @return Returns the sourceFile.
     */
    public FormFile getSourceFile() {
        return sourceFile;
    }

    /**
     * @param sourceFile The sourceFile to set.
     */
    public void setSourceFile(FormFile sourceFile) {
        this.sourceFile = sourceFile;
    }    
    
    /**
     * @return current List of baseline SourceAccountingLines for use in update-event generation
     */
    public List getBaselineSourceAccountingLines() {
        return baselineSourceAccountingLines;
    }

    /**
     * Sets the current List of baseline SourceAccountingLines to the given List
     * 
     * @param baselineSourceAccountingLines
     */
    public void setBaselineSourceAccountingLines(List baselineSourceAccountingLines) {
        this.baselineSourceAccountingLines = baselineSourceAccountingLines;
    }

    /**
     * @param index
     * @return true if a baselineSourceAccountingLine with the given index exists
     */
    public boolean hasBaselineSourceAccountingLine(int index) {
        boolean has = false;

        if ((index >= 0) && (index <= baselineSourceAccountingLines.size())) {
            has = true;
        }

        return has;
    }

    /**
     * Implementation creates empty SourceAccountingLines as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * @return baseline SourceAccountingLine at the given index
     */
    public SourceAccountingLine getBaselineSourceAccountingLine(int index) {
        try {
            while (baselineSourceAccountingLines.size() <= index) {
                
                AccountingDocument customerInvoiceDocument = ((CustomerInvoiceWriteoffDocument)getDocument()).getCustomerInvoiceDocument();
                baselineSourceAccountingLines.add(customerInvoiceDocument.getSourceAccountingLineClass().newInstance());
            }
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to get new source line instance for document" + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to get new source line instance for document" + e.getMessage());
        }

        return (SourceAccountingLine) baselineSourceAccountingLines.get(index);
    }   
    
    
    /**
     * A <code>{@link Map}</code> of names of optional accounting line fields that require a quickfinder.
     * 
     * @return a Map of fields
     */
    public void setForcedLookupOptionalFields(Map fieldMap) {
        forcedLookupOptionalFields = fieldMap;
    }

    /**
     * A <code>{@link Map}</code> of names of optional accounting line fields that require a quickfinder.
     * 
     * @return a Map of fields
     */
    public Map getForcedLookupOptionalFields() {
        return forcedLookupOptionalFields;
    }    
    
    
    /**
     * Retrieves the source accounting lines total in a currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedSourceTotal() {
        AccountingDocument customerInvoiceDocument = ((CustomerInvoiceWriteoffDocument)getDocument()).getCustomerInvoiceDocument();
        return (String) new CurrencyFormatter().format(customerInvoiceDocument.getSourceTotal());
    }

}
