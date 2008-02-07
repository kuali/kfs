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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.web.struts.form.CustomerInvoiceDocumentForm;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.module.financial.bo.AdvanceDepositDetail;
import org.kuali.module.financial.bo.CashReceiptHeader;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.document.AdvanceDepositDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.rule.event.AddCheckEvent;
import org.kuali.module.financial.web.struts.form.AdvanceDepositForm;
import org.kuali.module.financial.web.struts.form.CashReceiptForm;

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
    
    /**
     * Adds a CustomerInvoiceDetail instance created from the current "new customer invoice detail" line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addCustomerInvoiceDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();

        CustomerInvoiceDetail newCustomerInvoiceDetail = customerInvoiceDocumentForm.getNewCustomerInvoiceDetail();
        newCustomerInvoiceDetail.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        

        // check business rules
        //boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddCheckEvent(KFSConstants.NEW_CHECK_PROPERTY_NAME, crDoc, newCheck));
        boolean rulePassed = true;
        if (rulePassed) {
            // add check
            customerInvoiceDocument.addCustomerInvoiceDetail(newCustomerInvoiceDetail);

            // clear the used customer invoice detail
            customerInvoiceDocumentForm.setNewCustomerInvoiceDetail(new CustomerInvoiceDetail());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Deletes the selected customer invoice detail line from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteCustomerInvoiceDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();

        int indexOfLineToDelete = getLineToDelete(request);
        // delete advanceDeposit
        customerInvoiceDocument.deleteCustomerInvoiceDetail(indexOfLineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }    

}
