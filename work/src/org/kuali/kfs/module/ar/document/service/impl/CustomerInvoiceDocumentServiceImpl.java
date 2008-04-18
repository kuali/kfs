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
package org.kuali.module.ar.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.OrganizationOptions;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.CustomerInvoiceDocumentService;
import org.kuali.module.ar.service.ReceivableAccountingLineService;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

public class CustomerInvoiceDocumentServiceImpl implements CustomerInvoiceDocumentService {
    
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private ReceivableAccountingLineService receivableAccountingLineService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    
    /**
     * Refactor to have all the setters in here.
     * 
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#setupDefaultValuesForNewCustomerInvoiceDocument(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public void setupDefaultValuesForNewCustomerInvoiceDocument(CustomerInvoiceDocument document) {
        
        setupBasicDefaultValuesForCustomerInvoiceDocument(document);
        
        // set up the default values for the AR DOC Header
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);        
        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", document.getBillByChartOfAccountCode());
        criteria.put("organizationCode", document.getBilledByOrganizationCode());
        OrganizationOptions organizationOptions = (OrganizationOptions) businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);
        
        document.setPrintInvoiceIndicator(organizationOptions.getPrintInvoiceIndicator());
        document.setInvoiceTermsText(organizationOptions.getOrganizationPaymentTermsText());
        
        //If document is using receivable option, set receivable accounting line for customer invioce document
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals( receivableOffsetOption );
        if ( isUsingReceivableFAU ){
            receivableAccountingLineService.setReceivableAccountingLineForCustomerInvoiceDocument(document);
        }
    }    
    
    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#setupDefaultValuesForCopiedCustomerInvoiceDocument(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public void setupDefaultValuesForCopiedCustomerInvoiceDocument(CustomerInvoiceDocument document){
        
        setupBasicDefaultValuesForCustomerInvoiceDocument(document);
        
        //Save customer number since it will get overwritten when we retrieve the accounts receivable document header from service
        String customerNumber = document.getAccountsReceivableDocumentHeader().getCustomerNumber();
        
        //Set up the default values for the AR DOC Header
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        accountsReceivableDocumentHeader.setCustomerNumber(customerNumber);
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);        
    }
    
    private void setupBasicDefaultValuesForCustomerInvoiceDocument( CustomerInvoiceDocument document ){
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        if(currentUser != null) {
            document.setBillByChartOfAccountCode(currentUser.getChartOfAccountsCode());
            document.setBilledByOrganizationCode(currentUser.getOrganizationCode());
        }
        document.setInvoiceDueDate(getDefaultInvoiceDueDate());
        document.setWriteoffIndicator(false);
        document.setOpenInvoiceIndicator(true);
    }
    
    /**
     * This method sets due date equal to todays date +30 days by default
     * @param dateTimeService
     */
    private Date getDefaultInvoiceDueDate(){
        Calendar cal = dateTimeService.getCurrentCalendar();
        cal.add(Calendar.DATE, 30);
        Date sqlDueDate = null;
        try {
           sqlDueDate =  dateTimeService.convertToSqlDate(new Timestamp(cal.getTime().getTime()));
        } catch (ParseException e) {
            //TODO: throw an error here, but don't die
        }
        return sqlDueDate;
    }       
    
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public ReceivableAccountingLineService getReceivableAccountingLineService() {
        return receivableAccountingLineService;
    }

    public void setReceivableAccountingLineService(ReceivableAccountingLineService receivableAccountingLineService) {
        this.receivableAccountingLineService = receivableAccountingLineService;
    }

    public AccountsReceivableDocumentHeaderService getAccountsReceivableDocumentHeaderService() {
        return accountsReceivableDocumentHeaderService;
    }

    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }
}
