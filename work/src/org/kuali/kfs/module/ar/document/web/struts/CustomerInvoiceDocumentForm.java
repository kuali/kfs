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
package org.kuali.module.ar.web.struts.form;

import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;

import org.kuali.core.document.Document;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

public class CustomerInvoiceDocumentForm extends KualiAccountingDocumentFormBase {
    public DateTimeService dateTimeService;
    
    public CustomerInvoiceDocumentForm() {
        super();
        setDocument(new CustomerInvoiceDocument());
        setupServices();
        setupDefaultValues((CustomerInvoiceDocument)getDocument());
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
        document.setPrintInvoiceIndicator(true);
        
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
    
    
    
}
