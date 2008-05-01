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
package org.kuali.module.ar.web.struts.form;

import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.ar.ArAuthorizationConstants;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.module.purap.PurapAuthorizationConstants;

public class CustomerCreditMemoDocumentForm extends KualiAccountingDocumentFormBase {
    public DateTimeService dateTimeService;
   
    public CustomerCreditMemoDocumentForm() {
        super();
        setDocument(new CustomerCreditMemoDocument());
        setupServices();
        //setupDefaultValues((CustomerInvoiceDocument)getDocument());
    }

    private void setupServices() {
        setDateTimeService(SpringContext.getBean(DateTimeService.class));

    }

    /**
     * 
     * This method sets up the default values for this document
     */
    private void setupDefaultValues(CustomerInvoiceDocument document) {
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        if(currentUser != null) {
            //Billing chart = user's chart
            document.setBillByChartOfAccountCode(currentUser.getChartOfAccountsCode());

            //Billing org = user's org
            document.setBilledByOrganizationCode(currentUser.getOrganizationCode());
        }

        Date today = dateTimeService.getCurrentSqlDate();

        //Invoice create date = current date
        document.setBillingDate(today);

        //Invoice due date = current date + 30 days
        Calendar cal = dateTimeService.getCurrentCalendar();
        cal.add(Calendar.DATE, 30);
        java.util.Date dueDate = cal.getTime();
        Date sqlDueDate = null;
        try {
            sqlDueDate =  dateTimeService.convertToSqlDate(dueDate.toString());
        } catch (ParseException e) {
            //TODO: throw an error here, but don't die
        }
        if(sqlDueDate != null) {
            document.setInvoiceDueDate(sqlDueDate);
        }

        //Write-off Indicator = 'Y'
        document.setWriteoffIndicator(true);

        //Print Invoice Indicator = "Y"
//        document.setPrintInvoiceIndicator(true);

        //Processing Chart = Processing Chart retrieved from Billing Org options
        //convert this into some kind of service maybe?
        //document.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode(processingChartOfAccountCode);

        //Processing Org = Processing Org retrieved from Billing Org Options
        //document.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode(processingOrganizationCode);

        //Print Invoice Detail = Print Invoice Detail retrieved from Billing Org Options
        //can't find this one

        //Payment Terms Text = Payment Terms Text retrieved from Billing Org Options
        //document.setInvoiceTermsText(invoiceTermsText);

    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
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
                addExtraButton("methodToCall.continueCreditMemo", externalImageURL + "buttonsmall_continue.gif", "Continue");
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


}
