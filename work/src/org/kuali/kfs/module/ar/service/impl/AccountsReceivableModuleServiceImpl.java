/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.module.ar.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerCreditMemo;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerType;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.integration.ar.AccountsReceivableSystemInformation;
import org.kuali.kfs.integration.ar.AccountsRecievableCustomerInvoiceRecurrenceDetails;
import org.kuali.kfs.integration.ar.AccountsRecievableDocumentHeader;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants.CustomerTypeFields;
import org.kuali.kfs.module.ar.ArPropertyConstants.OrganizationOptionsFields;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceRecurrenceDetails;
import org.kuali.kfs.module.ar.businessobject.CustomerType;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.module.ar.document.service.impl.ReceivableAccountingLineService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The KFS AR module implementation of the AccountsReceivableModuleService
 */
@SuppressWarnings("deprecation")
public class AccountsReceivableModuleServiceImpl implements AccountsReceivableModuleService {
    protected static final String CASH_CONTROL_ELECTRONIC_PAYMENT_CLAIMING_DOCUMENT_GENERATION_STRATEGY_BEAN_NAME = "cashControlElectronicPaymentClaimingDocumentHelper";

    protected Lookupable customerLookupable;

    public void setCustomerLookupable(Lookupable customerLookupable) {
        this.customerLookupable = customerLookupable;
    }

    public KualiModuleService getKualiModuleService() {
        return SpringContext.getBean(KualiModuleService.class);
    }

    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    public DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    /**
     * @see org.kuali.kfs.integration.service.AccountsReceivableModuleService#getAccountsReceivablePaymentClaimingStrategy()
     */
    @Override
    public ElectronicPaymentClaimingDocumentGenerationStrategy getAccountsReceivablePaymentClaimingStrategy() {
        return SpringContext.getBean(ElectronicPaymentClaimingDocumentGenerationStrategy.class, AccountsReceivableModuleServiceImpl.CASH_CONTROL_ELECTRONIC_PAYMENT_CLAIMING_DOCUMENT_GENERATION_STRATEGY_BEAN_NAME);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#searchForCustomers(java.util.Map)
     */
    @Override
    public Collection<AccountsReceivableCustomer> searchForCustomers(Map<String, String> fieldValues) {
        customerLookupable.setBusinessObjectClass(Customer.class);
        List<? extends BusinessObject> results = customerLookupable.getSearchResults(fieldValues);

        Collection<AccountsReceivableCustomer> customers = new ArrayList<AccountsReceivableCustomer>();
        for (BusinessObject result : results) {
            customers.add((AccountsReceivableCustomer) result);
        }

        return customers;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#findCustomer(java.lang.String)
     */
    @Override
    public AccountsReceivableCustomer findCustomer(String customerNumber) {
    	Map<String, Object> primaryKey = new HashMap<String, Object>();
    	primaryKey.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);

        return getKualiModuleService().getResponsibleModuleService(Customer.class).getExternalizableBusinessObject(Customer.class, primaryKey);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#searchForCustomerAddresses(java.util.Map)
     */
    @Override
    public Collection<AccountsReceivableCustomerAddress> searchForCustomerAddresses(Map<String, String> fieldValues) {
        Collection<AccountsReceivableCustomerAddress> arCustomerAddresses = new ArrayList<AccountsReceivableCustomerAddress>();
        Collection<CustomerAddress> customerAddresses = getBusinessObjectService().findMatching(CustomerAddress.class, fieldValues);
        for (CustomerAddress customerAddress : customerAddresses){
            arCustomerAddresses.add(customerAddress);
        }
    	return arCustomerAddresses;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#findCustomerAddress(java.lang.String)
     */
    @Override
    public AccountsReceivableCustomerAddress findCustomerAddress(String customerNumber, String customerAddressIdentifer) {
        Map<String, String> addressKey = new HashMap<String, String>();
        addressKey.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
        addressKey.put(KFSPropertyConstants.CUSTOMER_ADDRESS_IDENTIFIER, customerAddressIdentifer);

        return getBusinessObjectService().findByPrimaryKey(CustomerAddress.class, addressKey);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenCustomerInvoice(java.lang.String)
     */
    @Override
    public AccountsReceivableCustomerInvoice getOpenCustomerInvoice(String customerInvoiceDocumentNumber) {
        return getBusinessObjectService().findBySinglePrimaryKey(CustomerInvoiceDocument.class, customerInvoiceDocumentNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getCustomerInvoiceOpenAmount(java.util.List, java.lang.Integer, java.sql.Date)
     */
    @Override
    public Map<String, KualiDecimal> getCustomerInvoiceOpenAmount(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom) {
        Map<String, KualiDecimal> customerInvoiceOpenAmountMap = new HashMap<String, KualiDecimal>();

        Collection<? extends AccountsReceivableCustomerInvoice> customerInvoiceDocuments = this.getOpenCustomerInvoices(customerTypeCodes, customerInvoiceAge, invoiceBillingDateFrom);
        if (ObjectUtils.isNull(customerInvoiceDocuments)) {
            return customerInvoiceOpenAmountMap;
        }

        for (AccountsReceivableCustomerInvoice invoiceDocument : customerInvoiceDocuments) {
            KualiDecimal openAmount = invoiceDocument.getOpenAmount();

            if (ObjectUtils.isNotNull(openAmount) && openAmount.isPositive()) {
                customerInvoiceOpenAmountMap.put(invoiceDocument.getDocumentNumber(), openAmount);
            }
        }

        return customerInvoiceOpenAmountMap;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenCustomerInvoices(java.util.List, java.lang.Integer, java.sql.Date)
     */
    @Override
    public Collection<? extends AccountsReceivableCustomerInvoice> getOpenCustomerInvoices(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom) {
        CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);

        return customerInvoiceDocumentService.getAllAgingInvoiceDocumentsByCustomerTypes(customerTypeCodes, customerInvoiceAge, invoiceBillingDateFrom);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#createCustomer()
     */
    @Override
    public AccountsReceivableCustomer createCustomer() {
        return new Customer();
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#createCustomerAddress()
     */
    @Override
    public AccountsReceivableCustomerAddress createCustomerAddress() {
        return new CustomerAddress();
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getNextCustomerNumber(org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public String getNextCustomerNumber(AccountsReceivableCustomer customer) {
        return SpringContext.getBean(CustomerService.class).getNextCustomerNumber((Customer) customer);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#saveCustomer(org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public void saveCustomer(AccountsReceivableCustomer customer) {
        getBusinessObjectService().save((Customer) customer);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#findByCustomerTypeDescription(java.lang.String)
     */
    @Override
    public List<AccountsReceivableCustomerType> findByCustomerTypeDescription(String customerTypeDescription) {
        Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put(CustomerTypeFields.CUSTOMER_TYPE_DESC, customerTypeDescription);

        List<AccountsReceivableCustomerType> arCustomerTypes = new ArrayList<AccountsReceivableCustomerType>();
        Collection<CustomerType> customerTypes = getBusinessObjectService().findMatching(CustomerType.class, fieldMap);
        for (CustomerType customerType : customerTypes){
            arCustomerTypes.add(customerType);
        }
        return arCustomerTypes;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOrgOptionsIfExists(java.lang.String, java.lang.String)
     */
    @Override
    public AccountsReceivableOrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(OrganizationOptionsFields.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.put(OrganizationOptionsFields.ORGANIZATION_CODE, organizationCode);
        return getBusinessObjectService().findByPrimaryKey(OrganizationOptions.class, criteria);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOrganizationOptionsByPrimaryKey(java.util.Map)
     */
    @Override
    public AccountsReceivableOrganizationOptions getOrganizationOptionsByPrimaryKey(Map<String, String> criteria) {
        return getBusinessObjectService().findByPrimaryKey(OrganizationOptions.class, criteria);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#saveCustomerInvoiceDocument(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice)
     */
    @Override
    public void saveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        getDocumentService().saveDocument((CustomerInvoiceDocument) customerInvoiceDocument);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#blanketApproveCustomerInvoiceDocument(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice)
     */
    @Override
    public Document blanketApproveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        return getDocumentService().blanketApproveDocument((CustomerInvoiceDocument) customerInvoiceDocument, null, null);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#createCustomerInvoiceRecurrenceDetails()
     */
    @Override
    public AccountsRecievableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails() {
        return new CustomerInvoiceRecurrenceDetails();
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#createAccountsReceivableDocumentHeader()
     */
    @Override
    public AccountsRecievableDocumentHeader createAccountsReceivableDocumentHeader() {
        return new org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader();
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getPrimaryOrganization()
     */
    @Override
    public ChartOrgHolder getPrimaryOrganization() {
        return SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getSystemInformationByProcessingChartOrgAndFiscalYear(java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear) {
        return SpringContext.getBean(SystemInformationService.class).getByProcessingChartOrgAndFiscalYear(chartOfAccountsCode, organizationCode, currentFiscalYear);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#isUsingReceivableFAU()
     */
    @Override
    public boolean isUsingReceivableFAU() {
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        return receivableOffsetOption != null ? ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption) : false;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#setReceivableAccountingLineForCustomerInvoiceDocument(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice)
     */
    @Override
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document) {
        SpringContext.getBean(ReceivableAccountingLineService.class).setReceivableAccountingLineForCustomerInvoiceDocument((CustomerInvoiceDocument) document);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getCustomerInvoiceDetailFromCustomerInvoiceItemCode(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode) {
        return SpringContext.getBean(CustomerInvoiceDetailService.class).getCustomerInvoiceDetailFromCustomerInvoiceItemCode(invoiceItemCode, processingChartCode, processingOrgCode);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAccountsReceivableObjectCodeBasedOnReceivableParameter(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail)
     */
    @Override
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail) {
        return SpringContext.getBean(CustomerInvoiceDetailService.class).getAccountsReceivableObjectCodeBasedOnReceivableParameter((CustomerInvoiceDetail) customerInvoiceDetail);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#recalculateCustomerInvoiceDetail(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice, org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail)
     */
    @Override
    public void recalculateCustomerInvoiceDetail(AccountsReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail) {
        SpringContext.getBean(CustomerInvoiceDetailService.class).recalculateCustomerInvoiceDetail((CustomerInvoiceDocument) customerInvoiceDocument, (CustomerInvoiceDetail) detail);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#prepareCustomerInvoiceDetailForAdd(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail, org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice)
     */
    @Override
    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountsReceivableCustomerInvoice customerInvoiceDocument) {
        SpringContext.getBean(CustomerInvoiceDetailService.class).prepareCustomerInvoiceDetailForAdd((CustomerInvoiceDetail) detail, (CustomerInvoiceDocument) customerInvoiceDocument);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenAmountForCustomerInvoiceDocument(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice)
     */
    @Override
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice invoice) {
        return SpringContext.getBean(CustomerInvoiceDocumentService.class).getOpenAmountForCustomerInvoiceDocument((CustomerInvoiceDocument) invoice);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenInvoiceDocumentsByCustomerNumberForTrip(java.lang.String, java.lang.String)
     */
    @Override
    public Collection<AccountsReceivableCustomerInvoice> getOpenInvoiceDocumentsByCustomerNumberForTrip(String customerNumber, String travelDocId) {

        Collection<AccountsReceivableCustomerInvoice> invoices = new ArrayList<AccountsReceivableCustomerInvoice>();
        Collection<CustomerInvoiceDocument> result = SpringContext.getBean(CustomerInvoiceDocumentService.class).getOpenInvoiceDocumentsByCustomerNumber(customerNumber);

        // if the trip id is not blank, perform a comparison with the Org Doc Num, otherwise add the entire collection into the result
        if (StringUtils.isNotBlank(travelDocId)){
            for (CustomerInvoiceDocument invoice : result){
                if (invoice.getDocumentHeader().getOrganizationDocumentNumber().equals(travelDocId)){
                    invoices.add(invoice);
                }
            }
        }else{
          invoices.addAll(result);
        }

        return invoices;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getNewAccountsReceivableDocumentHeader(java.lang.String, java.lang.String)
     */
    @Override
    public AccountsRecievableDocumentHeader getNewAccountsReceivableDocumentHeader(String processingChart, String processingOrg) {
        return SpringContext.getBean(AccountsReceivableDocumentHeaderService.class).getNewAccountsReceivableDocumentHeader(processingChart, processingOrg);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#createCustomerInvoiceDocument()
     */
    @Override
    public AccountsReceivableCustomerInvoice createCustomerInvoiceDocument() {
        try {
            return (CustomerInvoiceDocument) getDocumentService().getNewDocument(CustomerInvoiceDocument.class);
        }
        catch (WorkflowException ex) {
            ex.printStackTrace();
        }

        return new CustomerInvoiceDocument();
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#createCustomerCreditMemoDocument()
     */
    @Override
    public AccountsReceivableCustomerCreditMemo createCustomerCreditMemoDocument() {
        CustomerCreditMemoDocument crmDocument = new CustomerCreditMemoDocument();
        try {
            crmDocument = (CustomerCreditMemoDocument)getDocumentService().getNewDocument(CustomerCreditMemoDocument.class);
        }
        catch (WorkflowException ex) {
            ex.printStackTrace();
        }
        return crmDocument;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#populateCustomerCreditMemoDocumentDetails(org.kuali.kfs.integration.ar.AccountsReceivableCustomerCreditMemo, java.lang.String, org.kuali.rice.kns.util.KualiDecimal)
     */
    @Override
    public AccountsReceivableCustomerCreditMemo populateCustomerCreditMemoDocumentDetails(AccountsReceivableCustomerCreditMemo arCrmDocument, String invoiceNumber, KualiDecimal creditAmount) {

        CustomerCreditMemoDocument crmDocument = (CustomerCreditMemoDocument)arCrmDocument;

        //set invoice number
        crmDocument.setFinancialDocumentReferenceInvoiceNumber(invoiceNumber);
        //force to refresh invoice;
        crmDocument.getInvoice();
        crmDocument.populateCustomerCreditMemoDetails();

        //update the amount on the credit memo detail, there should only be one item for Travel item so we will update that amount
        CustomerCreditMemoDetail detail = crmDocument.getCreditMemoDetails().get(0);
        detail.setCreditMemoItemTotalAmount(creditAmount);

        CustomerCreditMemoDetailService service = SpringContext.getBean(CustomerCreditMemoDetailService.class);
        service.recalculateCustomerCreditMemoDetail(detail, crmDocument);

        return crmDocument;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#blanketApproveCustomerCreditMemoDocument(org.kuali.kfs.integration.ar.AccountsReceivableCustomerCreditMemo, java.lang.String)
     */
    @Override
    public Document blanketApproveCustomerCreditMemoDocument(AccountsReceivableCustomerCreditMemo creditMemoDocument, String annotation) throws WorkflowException {
        return getDocumentService().blanketApproveDocument((CustomerCreditMemoDocument)creditMemoDocument, annotation, null);
    }
}
