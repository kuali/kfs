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
package org.kuali.module.ar.web.struts.action;

import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.web.struts.form.CustomerInvoiceDocumentForm;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.module.financial.bo.CashReceiptHeader;

import edu.iu.uis.eden.exception.WorkflowException;

public class CustomerInvoiceDocumentAction extends KualiAccountingDocumentActionBase {
    
    private DateTimeService dateTimeService;
    
    public CustomerInvoiceDocumentAction() {
        super();
        setupServices();
    }
    
    /**
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) kualiDocumentFormBase;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        setupDefaultValues(customerInvoiceDocument);
    }

    private void setupServices() {
        setDateTimeService(SpringContext.getBean(DateTimeService.class));
    }

    /**
     * This method sets up the default values for the customer invoice document
     * 
     * @param document
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
        Date sqlDueDate = null;
        try {
           sqlDueDate =  dateTimeService.convertToSqlDate(cal.getTime().toString());
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
        
        //Set AR document header
        document.setAccountsReceivableDocumentHeader(new AccountsReceivableDocumentHeader());
        document.getAccountsReceivableDocumentHeader().setDocumentNumber(document.getDocumentNumber());
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }    

}
