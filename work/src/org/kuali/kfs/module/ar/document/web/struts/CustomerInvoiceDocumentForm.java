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
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.authorization.CustomerInvoiceDocumentPresentationController;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.util.KRADConstants;

public class CustomerInvoiceDocumentForm extends KualiAccountingDocumentFormBase {

    protected transient ConfigurationService configService;

    protected CustomerInvoiceDetail newCustomerInvoiceDetail;

    /**
     * Constructs a CustomerInvoiceDocumentForm.java. Also sets new customer invoice document detail to a newly constructed customer
     * invoice detail.
     */
    public CustomerInvoiceDocumentForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return KFSConstants.FinancialDocumentTypeCodes.CUSTOMER_INVOICE;
    }

    /**
     *
     * This method...
     * @return
     */
    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return (CustomerInvoiceDocument) getDocument();
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        SpringContext.getBean(CustomerInvoiceDocumentService.class).loadCustomerAddressesForCustomerInvoiceDocument(getCustomerInvoiceDocument());
    }

    /**
     * Reused to create new source accounting line (i.e customer invoice detail line) with defaulted values.
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#createNewSourceAccountingLine(org.kuali.kfs.sys.document.AccountingDocument)
     */
    @Override
    protected SourceAccountingLine createNewSourceAccountingLine(AccountingDocument financialDocument) {
        if (financialDocument == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        try {
            return SpringContext.getBean(CustomerInvoiceDetailService.class).getCustomerInvoiceDetailFromOrganizationAccountingDefaultForCurrentYear();
        }
        catch (Exception e) {
            throw new InfrastructureException("Unable to create a new customer invoice document accounting line", e);
        }
    }

    /**
     * By overriding this method, we can add the invoice total and open amount to the document header.
     *
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getDocInfo()
     *
     * KRAD Conversion: Performs the customization of the header fields.
     * No data dictionary is involved here.
     */
    @Override
    public void populateHeaderFields(WorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        getDocInfo().add(new HeaderField("DataDictionary.CustomerInvoiceDocument.attributes.sourceTotal", (String) new CurrencyFormatter().format(getCustomerInvoiceDocument().getSourceTotal())));
        getDocInfo().add(new HeaderField("DataDictionary.CustomerInvoiceDocument.attributes.openAmount", (String) new CurrencyFormatter().format(getCustomerInvoiceDocument().getOpenAmount())));
    }

    /**
     * Configure lookup for Invoice Item Code source accounting line
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getForcedLookupOptionalFields()
     */
    @Override
    public Map getForcedLookupOptionalFields() {
        Map forcedLookupOptionalFields = super.getForcedLookupOptionalFields();

        forcedLookupOptionalFields.put(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_CODE, ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_CODE + ";" + CustomerInvoiceItemCode.class.getName());
        forcedLookupOptionalFields.put(ArPropertyConstants.CustomerInvoiceDocumentFields.UNIT_OF_MEASURE_CODE, ArPropertyConstants.CustomerInvoiceDocumentFields.UNIT_OF_MEASURE_CODE + ";" + UnitOfMeasure.class.getName());

        return forcedLookupOptionalFields;
    }

    /**
     * Make amount and sales tax read only
     *
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
     */
    @Override
    public Map getForcedReadOnlyFields() {
        Map map = super.getForcedReadOnlyFields();
        map.put(KFSPropertyConstants.AMOUNT, Boolean.TRUE);
        map.put("invoiceItemTaxAmount", Boolean.TRUE);
        map.put("openAmount", Boolean.TRUE);
        return map;
    }

    /**
     * Build additional customer invoice specific buttons and set extraButtons list.
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

        //  get the edit modes from the preso controller
        CustomerInvoiceDocument invoiceDocument = (CustomerInvoiceDocument) getDocument();
        DocumentHelperService docHelperService = SpringContext.getBean(DocumentHelperService.class);
        CustomerInvoiceDocumentPresentationController presoController =
                (CustomerInvoiceDocumentPresentationController) docHelperService.getDocumentPresentationController(invoiceDocument);
        Set<String> editModes = presoController.getEditModes(invoiceDocument);

        //  draw the Print File button if appropriate
        if (editModes.contains(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.DISPLAY_PRINT_BUTTON)) {
            String printButtonURL = getConfigService().getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
            addExtraButton("methodToCall.print", printButtonURL + "buttonsmall_genprintfile.gif", "Print");
        }

        //  draw the Error Correction button if appropriate
        if (presoController.canErrorCorrect(invoiceDocument)) {
            String printButtonURL = getConfigService().getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
            addExtraButton("methodToCall.correct", printButtonURL + "buttonsmall_correction.gif", "Correct");
        }

        return extraButtons;
    }

    /**
     * Adds a new button to the extra buttons collection.
     *
     * @param property - property for button
     * @param source - location of image
     * @param altText - alternate text for button if images don't appear
     *
     * KRAD Conversion: Performs extra button customization.
     * No data dictionary is involved here.
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }

    protected ConfigurationService getConfigService() {
        if (configService == null) {
            configService = SpringContext.getBean(ConfigurationService.class);
        }
        return configService;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (KRADConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && "printInvoicePDF".equals(methodToCallParameterValue)) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }
}
