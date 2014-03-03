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
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerCreditMemo;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceRecurrenceDetails;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerType;
import org.kuali.kfs.integration.ar.AccountsReceivableDocumentHeader;
import org.kuali.kfs.integration.ar.AccountsReceivableInvoiceTemplate;
import org.kuali.kfs.integration.ar.AccountsReceivableMilestoneSchedule;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.integration.ar.AccountsReceivablePredeterminedBillingSchedule;
import org.kuali.kfs.integration.ar.AccountsReceivableSystemInformation;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants.CustomerTypeFields;
import org.kuali.kfs.module.ar.ArPropertyConstants.OrganizationOptionsFields;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceRecurrenceDetails;
import org.kuali.kfs.module.ar.businessobject.CustomerType;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.ReceivableAccountingLineService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.module.ar.service.CustomerDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.krad.UserSession;
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
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivableModuleServiceImpl.class);

    protected AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    protected BusinessObjectService businessObjectService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected CustomerCreditMemoDetailService customerCreditMemoDetailService;
    protected CustomerDocumentService customerDocumentService;
    protected CustomerInvoiceDetailService customerInvoiceDetailService;
    protected CustomerInvoiceDocumentService customerInvoiceDocumentService;
    protected Lookupable customerLookupable;
    protected CustomerService customerService;
    protected CustomerAddressService customerAddressService;
    protected DocumentService documentService;
    protected FinancialSystemUserService financialSystemUserService;
    protected KualiModuleService kualiModuleService;
    protected ParameterService parameterService;
    protected ReceivableAccountingLineService receivableAccountingLineService;
    protected SystemInformationService systemInformationService;

    public CustomerAddressService getCustomerAddressService() {
        return customerAddressService;
    }

    public void setCustomerAddressService(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    public CustomerCreditMemoDetailService getCustomerCreditMemoDetailService() {
        return customerCreditMemoDetailService;
    }

    public void setCustomerCreditMemoDetailService(CustomerCreditMemoDetailService customerCreditMemoDetailService) {
        this.customerCreditMemoDetailService = customerCreditMemoDetailService;
    }

    public ReceivableAccountingLineService getReceivableAccountingLineService() {
        return receivableAccountingLineService;
    }

    public void setReceivableAccountingLineService(ReceivableAccountingLineService receivableAccountingLineService) {
        this.receivableAccountingLineService = receivableAccountingLineService;
    }

    public SystemInformationService getSystemInformationService() {
        return systemInformationService;
    }

    public void setSystemInformationService(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    /**
     * Gets the customerInvoiceDetailService attribute.
     *
     * @return Returns the customerInvoiceDetailService.
     */
    public CustomerInvoiceDetailService getCustomerInvoiceDetailService() {
        return customerInvoiceDetailService;
    }

    /**
     * Sets the customerInvoiceDetailService attribute value.
     *
     * @param customerInvoiceDetailService The customerInvoiceDetailService to set.
     */

    public void setCustomerInvoiceDetailService(CustomerInvoiceDetailService customerInvoiceDetailService) {
        this.customerInvoiceDetailService = customerInvoiceDetailService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }

    /**
     * Sets the accountsReceivableDocumentHeaderService attribute value.
     *
     * @param accountsReceivableDocumentHeaderService The accountsReceivableDocumentHeaderService to set.
     */
    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public CustomerDocumentService getCustomerDocumentService() {
        return customerDocumentService;
    }

    public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
        this.customerDocumentService = customerDocumentService;
    }

    public void setCustomerLookupable(Lookupable customerLookupable) {
        this.customerLookupable = customerLookupable;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the contractsGrantsInvoiceDocumentService attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocumentService.
     */
    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    /**
     * Sets the contractsGrantsInvoiceDocumentService attribute value.
     *
     * @param contractsGrantsInvoiceDocumentService The contractsGrantsInvoiceDocumentService to set.
     */
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
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
        Customer customer = getKualiModuleService().getResponsibleModuleService(Customer.class).getExternalizableBusinessObject(Customer.class, primaryKey);
        return customer;
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
    public Map<String, KualiDecimal> getCustomerInvoiceOpenAmount(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceDueDateFrom) {
        Map<String, KualiDecimal> customerInvoiceOpenAmountMap = new HashMap<String, KualiDecimal>();

        Collection<? extends AccountsReceivableCustomerInvoice> customerInvoiceDocuments = this.getOpenCustomerInvoices(customerTypeCodes, customerInvoiceAge, invoiceDueDateFrom);
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
    public Collection<? extends AccountsReceivableCustomerInvoice> getOpenCustomerInvoices(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceDueDateFrom) {
        return customerInvoiceDocumentService.getAllAgingInvoiceDocumentsByCustomerTypes(customerTypeCodes, customerInvoiceAge, invoiceDueDateFrom);
    }

    /**
     *
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#cancelInvoicesForTrip(java.lang.String)
     */
    @Override
    public void cancelInvoicesForTrip(String tripId, AccountsReceivableOrganizationOptions organizationOptions) {
        for (CustomerInvoiceDocument invoice : getInvoicesForTrip(tripId)) {
            final CustomerCreditMemoDocument creditMemo = createCreditMemoForTripInvoice(invoice, tripId, organizationOptions);
            blanketApproveCreditMemoForTripInvoice(creditMemo, tripId);
        }
    }

    /**
     * Finds all the customer invoice documents associated with the given trip id
     * @param tripId the trip id to find customer invoice documetns for
     * @return a Collection of invoice documents
     */
    protected Collection<CustomerInvoiceDocument> getInvoicesForTrip(String tripId) {
        Map<String, String> query = new HashMap<String, String>();
        query.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, tripId);

        return getBusinessObjectService().findMatching(CustomerInvoiceDocument.class, query);
    }

    /**
     * Creates a credit memo to reverse the effects of a customer invoice that paid off an advance
     * @param invoice the invoice to reverse
     * @param tripId the trip id of the trip to reverse
     * @param organizationOptions the organization options from the travel module
     * @return a CustomerCreditMemo document which will reverse the effect the given invoice
     */
    protected CustomerCreditMemoDocument createCreditMemoForTripInvoice(CustomerInvoiceDocument invoice, String tripId, AccountsReceivableOrganizationOptions organizationOptions) {
        CustomerCreditMemoDocument creditMemo;
        try {
            creditMemo = (CustomerCreditMemoDocument)getDocumentService().getNewDocument(CustomerCreditMemoDocument.class);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Could not create Customer Credit Memo for trip invoice, trip: "+tripId+"; invoice document #"+invoice.getDocumentNumber(), we);
        }

        AccountsReceivableDocumentHeader arDocHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeader(organizationOptions.getProcessingChartOfAccountCode(), organizationOptions.getProcessingOrganizationCode());
        arDocHeader.setDocumentNumber(creditMemo.getDocumentNumber());
        arDocHeader.setCustomerNumber(invoice.getAccountsReceivableDocumentHeader().getCustomerNumber());
        creditMemo.setAccountsReceivableDocumentHeader(arDocHeader);
        creditMemo.getFinancialSystemDocumentHeader().setDocumentDescription(invoice.getDocumentHeader().getDocumentDescription());
        creditMemo.getFinancialSystemDocumentHeader().setOrganizationDocumentNumber(tripId);
        populateCustomerCreditMemoDocumentDetails(creditMemo, invoice.getDocumentNumber(), invoice.getTotalDollarAmount());

        return creditMemo;
    }

    /**
     * Blanket approves the given credit memo as the KFS system user
     * @param creditMemo the credit memo to blanket approve
     * @param tripId the trip id of the credit memo
     */
    protected void blanketApproveCreditMemoForTripInvoice(final CustomerCreditMemoDocument creditMemo, final String tripId) {
        final String blanketApproveAnnotation= String.format("Blanket Approved CRM Doc # %s by Trip ID # %s", creditMemo.getDocumentNumber(), tripId);
        try {
        GlobalVariables.doInNewGlobalVariables(new UserSession(KFSConstants.SYSTEM_USER), new Callable<Object>() {
                /**
                 * Blanket approves the credit memo
                 * @see java.util.concurrent.Callable#call()
                 */
                @Override
                public Object call() {
                    try {
                    blanketApproveCustomerCreditMemoDocument(creditMemo, blanketApproveAnnotation);
                    return null;
                    } catch (WorkflowException we) {
                        throw new RuntimeException("Could not blanket approve credit memo (doc #"+creditMemo.getDocumentNumber()+")to reverse invoice for trip: "+tripId, we);
                    }
                }});
        } catch (Exception e) {
            throw new RuntimeException("Could not blanket approve credit memo (doc #"+creditMemo.getDocumentNumber()+")to reverse invoice for trip: "+tripId, e);
        }
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
        return customerService.getNextCustomerNumber((Customer) customer);
    }

    @Override
    public String createAndSaveCustomer(String description, ContractsAndGrantsBillingAgency agency) throws WorkflowException {
        return customerDocumentService.createAndSaveCustomer(description, agency);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#saveCustomer(org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public void saveCustomer(AccountsReceivableCustomer customer) {
        getBusinessObjectService().save((Customer) customer);
    }

    /*
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
    public AccountsReceivableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails() {
        return new CustomerInvoiceRecurrenceDetails();
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#createAccountsReceivableDocumentHeader()
     */
    @Override
    public AccountsReceivableDocumentHeader createAccountsReceivableDocumentHeader() {
        return new org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader();
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getPrimaryOrganization()
     */
    @Override
    public ChartOrgHolder getPrimaryOrganization() {
        return financialSystemUserService.getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getSystemInformationByProcessingChartOrgAndFiscalYear(java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear) {
        return systemInformationService.getByProcessingChartOrgAndFiscalYear(chartOfAccountsCode, organizationCode, currentFiscalYear);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#isUsingReceivableFAU()
     */
    @Override
    public boolean isUsingReceivableFAU() {
        String receivableOffsetOption = parameterService.getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        return receivableOffsetOption != null ? ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption) : false;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#setReceivableAccountingLineForCustomerInvoiceDocument(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice)
     */
    @Override
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document) {
        receivableAccountingLineService.setReceivableAccountingLineForCustomerInvoiceDocument((CustomerInvoiceDocument) document);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getCustomerInvoiceDetailFromCustomerInvoiceItemCode(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode) {
        return customerInvoiceDetailService.getCustomerInvoiceDetailFromCustomerInvoiceItemCode(invoiceItemCode, processingChartCode, processingOrgCode);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAccountsReceivableObjectCodeBasedOnReceivableParameter(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail)
     */
    @Override
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail) {
        return customerInvoiceDetailService.getAccountsReceivableObjectCodeBasedOnReceivableParameter((CustomerInvoiceDetail) customerInvoiceDetail);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#recalculateCustomerInvoiceDetail(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice, org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail)
     */
    @Override
    public void recalculateCustomerInvoiceDetail(AccountsReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail) {
        customerInvoiceDetailService.recalculateCustomerInvoiceDetail((CustomerInvoiceDocument) customerInvoiceDocument, (CustomerInvoiceDetail) detail);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#prepareCustomerInvoiceDetailForAdd(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail, org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice)
     */
    @Override
    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountsReceivableCustomerInvoice customerInvoiceDocument) {
        customerInvoiceDetailService.prepareCustomerInvoiceDetailForAdd((CustomerInvoiceDetail) detail, (CustomerInvoiceDocument) customerInvoiceDocument);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenAmountForCustomerInvoiceDocument(org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice)
     */
    @Override
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice invoice) {
        return customerInvoiceDocumentService.getOpenAmountForCustomerInvoiceDocument((CustomerInvoiceDocument) invoice);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenInvoiceDocumentsByCustomerNumberForTrip(java.lang.String, java.lang.String)
     */
    @Override
    public Collection<AccountsReceivableCustomerInvoice> getOpenInvoiceDocumentsByCustomerNumberForTrip(String customerNumber, String travelDocId) {

        Collection<AccountsReceivableCustomerInvoice> invoices = new ArrayList<AccountsReceivableCustomerInvoice>();
        Collection<CustomerInvoiceDocument> result = customerInvoiceDocumentService.getOpenInvoiceDocumentsByCustomerNumber(customerNumber);

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
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeader(String processingChart, String processingOrg) {
        return accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeader(processingChart, processingOrg);
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
            LOG.error("problem during AccountsReceivableModuleServiceImpl.createCustomerInvoiceDocument()", ex);
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
            LOG.error("problem during AccountsReceivableModuleServiceImpl.createCustomerCreditMemoDocument()", ex);
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

        customerCreditMemoDetailService.recalculateCustomerCreditMemoDetail(detail, crmDocument);

        return crmDocument;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#blanketApproveCustomerCreditMemoDocument(org.kuali.kfs.integration.ar.AccountsReceivableCustomerCreditMemo, java.lang.String)
     */
    @Override
    public Document blanketApproveCustomerCreditMemoDocument(AccountsReceivableCustomerCreditMemo creditMemoDocument, String annotation) throws WorkflowException {
        return getDocumentService().blanketApproveDocument((CustomerCreditMemoDocument)creditMemoDocument, annotation, null);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#retrieveGLPEReceivableParameterValue() This method
     *      retrieves the value of the Parameter GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD
     * @param parameterName
     * @return
     */

    @Override
    public String retrieveGLPEReceivableParameterValue() {

        String parameterValue = parameterService.getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        return parameterValue;

    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAwardBilledToDateAmountByProposalNumber(java.lang.Long) This
     *      method gets the award billed to date using ContractsGrantsInvoiceDocumentService
     * @param roposalNumber
     * @return
     */
    @Override
    public KualiDecimal getAwardBilledToDateAmountByProposalNumber(Long proposalNumber) {
        return contractsGrantsInvoiceDocumentService.getAwardBilledToDateAmountByProposalNumber(proposalNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#calculateTotalPaymentsToDateByAward(java.lang.Long) This
     *      method calculates total payments to date by Award using ContractsGrantsInvoiceDocumentService
     * @param proposalNumber
     * @return
     */
    @Override
    public KualiDecimal calculateTotalPaymentsToDateByAward(Long proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        ContractsAndGrantsBillingAward award = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);

        return contractsGrantsInvoiceDocumentService.calculateTotalPaymentsToDateByAward(award);

    }

    @Override
    public AccountsReceivableCustomerAddress getPrimaryAddress(String customerNumber) {
        return customerAddressService.getPrimaryAddress(customerNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#ffindInvoiceTemplate(java.lang.String)
     */
    @Override
    public AccountsReceivableInvoiceTemplate findInvoiceTemplate(String invoiceTemplateCode) {
        Map<String, Object> primaryKey = new HashMap<String, Object>();
        primaryKey.put(KFSPropertyConstants.INVOICE_TEMPLATE_CODE, invoiceTemplateCode);
        InvoiceTemplate invoiceTemplate = getKualiModuleService().getResponsibleModuleService(InvoiceTemplate.class).getExternalizableBusinessObject(InvoiceTemplate.class, primaryKey);
        return invoiceTemplate;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#saveCustomer(org.kuali.kfs.integration.ar.AccountsReceivableCustomer)
     */
    @Override
    public void saveInvoiceTemplate(AccountsReceivableInvoiceTemplate invoiceTemplate) {
        getBusinessObjectService().save((InvoiceTemplate) invoiceTemplate);
    }

    @Override
    public AccountsReceivableMilestoneSchedule getMilestoneSchedule() {
        return new MilestoneSchedule();
    }

    @Override
    public void setProposalNumber(AccountsReceivableMilestoneSchedule milestoneSchedule, Long proposalNumber) {
        ((MilestoneSchedule) milestoneSchedule).setProposalNumber(proposalNumber);
    }

    @Override
    public AccountsReceivablePredeterminedBillingSchedule getPredeterminedBillingSchedule() {
        return new PredeterminedBillingSchedule();
    }

    @Override
    public void setProposalNumber(AccountsReceivablePredeterminedBillingSchedule predeterminedBillingSchedule, Long proposalNumber) {
        ((PredeterminedBillingSchedule) predeterminedBillingSchedule).setProposalNumber(proposalNumber);
    }

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    @NonTransactional
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getLastBilledDate(java.lang.Long)
     */
    @Override
    public Date getLastBilledDate(ContractsAndGrantsBillingAward award) {
        return contractsGrantsInvoiceDocumentService.getLastBilledDate(award);
    }

    public FinancialSystemUserService getFinancialSystemUserService() {
        return financialSystemUserService;
    }

    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }

    @Override
    public List<String> checkAwardContractControlAccounts(ContractsAndGrantsBillingAward award) {
        return contractsGrantsInvoiceDocumentService.checkAwardContractControlAccounts(award);
    }

    @Override
    public boolean hasPredeterminedBillingSchedule(Long proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        PredeterminedBillingSchedule schedule = getBusinessObjectService().findByPrimaryKey(PredeterminedBillingSchedule.class, map);
        if (ObjectUtils.isNotNull(schedule)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasMilestoneSchedule(Long proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        MilestoneSchedule schedule = getBusinessObjectService().findByPrimaryKey(MilestoneSchedule.class, map);
        if (ObjectUtils.isNotNull(schedule)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getContractsGrantsInvoiceDocumentType() {
        return ArConstants.CGIN_DOCUMENT_TYPE;
    }

}
