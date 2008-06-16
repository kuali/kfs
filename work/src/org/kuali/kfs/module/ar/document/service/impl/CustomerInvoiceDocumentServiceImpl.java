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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.dao.DocumentDao;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.bo.ChartOrgHolder;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.FinancialSystemUserService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.Customer;
import org.kuali.module.ar.bo.CustomerAddress;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.InvoicePaidApplied;
import org.kuali.module.ar.bo.NonInvoicedDistribution;
import org.kuali.module.ar.bo.OrganizationOptions;
import org.kuali.module.ar.dao.CustomerInvoiceDocumentDao;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.CustomerAddressService;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;
import org.kuali.module.ar.service.CustomerInvoiceDocumentService;
import org.kuali.module.ar.service.InvoicePaidAppliedService;
import org.kuali.module.ar.service.NonInvoicedDistributionService;
import org.kuali.module.ar.service.ReceivableAccountingLineService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerInvoiceDocumentServiceImpl implements CustomerInvoiceDocumentService {

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private ReceivableAccountingLineService receivableAccountingLineService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private CustomerAddressService customerAddressService;
    private CustomerInvoiceDocumentDao customerInvoiceDocumentDao;
    private DocumentService documentService;
    private DocumentDao documentDao;
    private InvoicePaidAppliedService invoicePaidAppliedService;
    private NonInvoicedDistributionService nonInvoicedDistributionService;
    private CustomerInvoiceDetailService customerInvoiceDetailService;

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getCustomerInvoiceDetailsForCustomerInvoiceDocument(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        return getCustomerInvoiceDetailsForCustomerInvoiceDocument(customerInvoiceDocument.getDocumentNumber());
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getCustomerInvoiceDetailsForCustomerInvoiceDocument(java.lang.String)
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(String customerInvoiceDocumentNumber) {
        return customerInvoiceDetailService.getCustomerInvoiceDetailsForInvoice(customerInvoiceDocumentNumber);
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getBalanceForCustomerInvoiceDocument(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public KualiDecimal getBalanceForCustomerInvoiceDocument(CustomerInvoiceDocument invoice) {
        return getTotalAmountForCustomerInvoiceDocument(invoice).subtract(getPaidAppliedTotalForInvoice(invoice));
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getBalanceForCustomerInvoiceDocument(java.lang.String)
     */
    public KualiDecimal getBalanceForCustomerInvoiceDocument(String customerInvoiceDocumentNumber) {
        return getBalanceForCustomerInvoiceDocument(getInvoiceByInvoiceDocumentNumber(customerInvoiceDocumentNumber));
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getTotalAmountForCustomerInvoiceDocument(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public KualiDecimal getTotalAmountForCustomerInvoiceDocument(CustomerInvoiceDocument invoice) {
        KualiDecimal total = new KualiDecimal(0);
        for(CustomerInvoiceDetail detail : customerInvoiceDetailService.getCustomerInvoiceDetailsForInvoice(invoice)) {
            total = total.add(detail.getAmount());
        }
        return total;
    }

    
    
    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getTotalAmountForCustomerInvoiceDocument(java.lang.String)
     */
    public KualiDecimal getTotalAmountForCustomerInvoiceDocument(String customerInvoiceDocumentNumber) {
        return getTotalAmountForCustomerInvoiceDocument(getInvoiceByInvoiceDocumentNumber(customerInvoiceDocumentNumber));
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getInvoicesByCustomerNumber(java.lang.String)
     */
    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByCustomerNumber(String customerNumber) {
        
        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();
        
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("customerNumber", customerNumber);
        
        Collection<AccountsReceivableDocumentHeader> documentHeaders = 
            businessObjectService.findMatching(AccountsReceivableDocumentHeader.class, fieldValues);
        
        List<String> documentHeaderIds = new ArrayList<String>();
        for(AccountsReceivableDocumentHeader header : documentHeaders) {
            String documentNumber = null;
            try {
                Long.parseLong(header.getDocumentHeader().getDocumentNumber());
                documentNumber = header.getDocumentHeader().getDocumentNumber();
                documentHeaderIds.add(documentNumber);
            } catch(NumberFormatException nfe) {
            }
        }
        
//        try {
        invoices = documentDao.findByDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);

            // invoices = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);
//        } catch(WorkflowException we) {
//            throw new InfrastructureException("Unable to retrieve CustomerInvoiceDocuments", we);
//        }
        
        return invoices;
    }
    
    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getCustomerByOrganizationInvoiceNumber(java.lang.String)
     */
    public Customer getCustomerByOrganizationInvoiceNumber(String organizationInvoiceNumber) {
        CustomerInvoiceDocument invoice = getInvoiceByOrganizationInvoiceNumber(organizationInvoiceNumber);
        return invoice.getAccountsReceivableDocumentHeader().getCustomer();
    }
    
    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getInvoiceByOrganizationInvoiceNumber(java.lang.String)
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber) {
        return customerInvoiceDocumentDao.getInvoiceByOrganizationInvoiceNumber(organizationInvoiceNumber);
    }

    /**
     * @param invoiceDocumentNumber
     * @return
     */
    public Customer getCustomerByInvoiceDocumentNumber(String invoiceDocumentNumber) {
        CustomerInvoiceDocument invoice = getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
        return invoice.getAccountsReceivableDocumentHeader().getCustomer();
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getInvoiceByInvoiceDocumentNumber(java.lang.String)
     */
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String invoiceDocumentNumber) {
        return customerInvoiceDocumentDao.getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
    }
    
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

        if (ObjectUtils.isNotNull(organizationOptions)) {
            document.setPrintInvoiceIndicator(organizationOptions.getPrintInvoiceIndicator());
            document.setInvoiceTermsText(organizationOptions.getOrganizationPaymentTermsText());
        }

        // If document is using receivable option, set receivable accounting line for customer invoice document
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        if (isUsingReceivableFAU) {
            receivableAccountingLineService.setReceivableAccountingLineForCustomerInvoiceDocument(document);
        }
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#loadCustomerAddressesForCustomerInvoiceDocument(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public void loadCustomerAddressesForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        // if address identifier is provided, try to refresh customer address data
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getAccountsReceivableDocumentHeader())) {
            CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
            CustomerAddress customerShipToAddress = customerAddressService.getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerInvoiceDocument.getCustomerShipToAddressIdentifier());
            CustomerAddress customerBillToAddress = customerAddressService.getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerInvoiceDocument.getCustomerBillToAddressIdentifier());

            if (ObjectUtils.isNotNull(customerShipToAddress)) {
                customerInvoiceDocument.setCustomerShipToAddress(customerShipToAddress);
            }

            if (ObjectUtils.isNotNull(customerBillToAddress)) {
                customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
            }
        }
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#setupDefaultValuesForCopiedCustomerInvoiceDocument(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public void setupDefaultValuesForCopiedCustomerInvoiceDocument(CustomerInvoiceDocument document) {

        setupBasicDefaultValuesForCustomerInvoiceDocument(document);

        // Save customer number since it will get overwritten when we retrieve the accounts receivable document header from service
        String customerNumber = document.getAccountsReceivableDocumentHeader().getCustomerNumber();

        // Set up the default values for the AR DOC Header
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        accountsReceivableDocumentHeader.setCustomerNumber(customerNumber);
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getInvoicePaidAppliedsForCustomerInvoiceDocument(java.lang.String)
     */
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(String documentNumber) {
//        return invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(documentNumber);
//    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getNonInvoicedDistributionsForInvoice(java.lang.String)
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber) {
        return nonInvoicedDistributionService.getNonInvoicedDistributionsForInvoice(documentNumber);
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getNonInvoicedTotalForInvoice(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(CustomerInvoiceDocument invoice) {
        Collection<NonInvoicedDistribution> payments = this.nonInvoicedDistributionService.getNonInvoicedDistributionsForInvoice(invoice);
        KualiDecimal total = new KualiDecimal(0);
        for(NonInvoicedDistribution payment : payments) {
            total = total.add(payment.getFinancialDocumentLineAmount());
        }
        return total;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getNonInvoicedTotalForInvoice(java.lang.String)
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(String documentNumber) {
        return getNonInvoicedTotalForInvoice(getInvoiceByInvoiceDocumentNumber(documentNumber));
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getPaidAppliedTotalForInvoice(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(CustomerInvoiceDocument invoice) {
        Collection<InvoicePaidApplied> payments = invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(invoice);
        KualiDecimal total = new KualiDecimal(0);
        for(InvoicePaidApplied payment : payments) {
            total = total.add(payment.getInvoiceItemAppliedAmount());
        }
        return total;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getPaidAppliedTotalForInvoice(java.lang.String)
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(String documentNumber) {
        return getPaidAppliedTotalForInvoice(getInvoiceByInvoiceDocumentNumber(documentNumber));
    }

    /**
     * @param document
     */
    private void setupBasicDefaultValuesForCustomerInvoiceDocument(CustomerInvoiceDocument document) {
        ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        if (currentUser != null) {
            document.setBillByChartOfAccountCode(currentUser.getChartOfAccountsCode());
            document.setBilledByOrganizationCode(currentUser.getOrganizationCode());
        }
        document.setInvoiceDueDate(getDefaultInvoiceDueDate());
        document.setWriteoffIndicator(false);
        document.setOpenInvoiceIndicator(true);
    }

    /**
     * This method sets due date equal to todays date +30 days by default
     * 
     * @param dateTimeService
     */
    private Date getDefaultInvoiceDueDate() {
        Calendar cal = dateTimeService.getCurrentCalendar();
        cal.add(Calendar.DATE, 30);
        Date sqlDueDate = null;
        try {
            sqlDueDate = dateTimeService.convertToSqlDate(new Timestamp(cal.getTime().getTime()));
        }
        catch (ParseException e) {
            // TODO: throw an error here, but don't die
        }
        return sqlDueDate;
    }

    public CustomerInvoiceDocumentDao getCustomerInvoiceDocumentDao() {
        return customerInvoiceDocumentDao;
    }

    public void setCustomerInvoiceDocumentDao(CustomerInvoiceDocumentDao customerInvoiceDocumentDao) {
        this.customerInvoiceDocumentDao = customerInvoiceDocumentDao;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }
    
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
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

    public CustomerAddressService getCustomerAddressService() {
        return customerAddressService;
    }

    public void setCustomerAddressService(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }

    public void setNonInvoicedDistributionService(NonInvoicedDistributionService nonInvoicedDistributionService) {
        this.nonInvoicedDistributionService = nonInvoicedDistributionService;
    }

    public void setCustomerInvoiceDetailService(CustomerInvoiceDetailService customerInvoiceDetailService) {
        this.customerInvoiceDetailService = customerInvoiceDetailService;
    }
    
}
