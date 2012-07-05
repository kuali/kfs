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
package org.kuali.kfs.integration.ar;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Methods which allow core KFS modules to interact with the Accounts Receivable module.
 */
public interface AccountsReceivableModuleService {
    /**
     * A method that returns an implementation of the ElectronicPaymentClaimingDocumentGenerationStrategy interface which will claim
     * electronic payments for the Accounts Receivable module.
     * 
     * @return an appropriate implementation of ElectronicPaymentClaimingDocumentGenerationStrategy
     */
    public abstract ElectronicPaymentClaimingDocumentGenerationStrategy getAccountsReceivablePaymentClaimingStrategy();

    /**
     * When refund DV is disapproved, a note needs to be added to the related payment request document
     * 
     * @param relatedDocumentNumber - document number for the related document (dv)
     * @param noteText - text for the new note
     */
    public void addNoteToRelatedPaymentRequestDocument(String relatedDocumentNumber, String noteText);

    /**
     * Returns the processing organization associated with the payment request given by the related document number
     * 
     * @param relatedDocumentNumber - document number for the related document (dv)
     * @return Organization instance for processing org
     */
    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber);

    /**
     * Performs a search against AR customers with the given criteria
     * 
     * @param fieldValues - Map of criteria to use (field name as map key, value as map value), supports standard lookup wildcards
     * @return Collection of matching Customers
     */
    public Collection<AccountsReceivableCustomer> searchForCustomers(Map<String, String> fieldValues);
    
    /**
     * Returns the AccountsReceivableCustomer for the given customer number
     * 
     * @param customerNumber - number of customer to find
     * @return AccountsReceivableCustomer instance with the customer information
     */
    public AccountsReceivableCustomer findCustomer(String customerNumber);
    
    /**
     * Performs a search against AR customer addresses with the given criteria
     * 
     * @param fieldValues - Map of criteria to use (field name as map key, value as map value), supports standard lookup wildcards
     * @return Collection of matching Customer Addresses
     */
    public Collection<AccountsReceivableCustomerAddress> searchForCustomerAddresses(Map<String, String> fieldValues);
    
    /**
     * Returns the AccountsReceivableCustomerAddress for the given customer address identifier
     * 
     * @param customerNumber - number of customer for address
     * @param customerAddressIdentifer - id for the customer address to find
     * @return AccountsReceivableCustomerAddress instance with the customer address information
     */
    public AccountsReceivableCustomerAddress findCustomerAddress(String customerNumber, String customerAddressIdentifer);
    
    /**
     * get the open customer invoice document with the given document number
     * 
     * @param customerInvoiceDocumentNumber the given customer invoice document number
     * @return the open customer invoice document with the given document number
     */
    public AccountsReceivableCustomerInvoice getOpenCustomerInvoice(String customerInvoiceDocumentNumber);
    
    /**
     * get the open amount of the customer invoice document with the given search criteria
     * 
     * @param customerTypeCodes the given customer type codes
     * @param customerInvoiceAge the given customer invoice age
     * @param invoiceBillingDateFrom the from date of billing date of the invoice 
     * 
     * @return a set of the open amounts indexed by the customer invoice numbers
     */
    public Map<String, KualiDecimal> getCustomerInvoiceOpenAmount(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom);

    /**
     * get the open customer invoice documents with the given search criteria
     * 
     * @param customerTypeCodes the given customer type codes
     * @param customerInvoiceAge the given customer invoice age
     * @param invoiceBillingDateFrom the from date of billing date of the invoice 
     * 
     * @return a set of the open customer invoices
     */
    public Collection<? extends AccountsReceivableCustomerInvoice> getOpenCustomerInvoices(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom);
    
    /**
     * Create new Customer object
     * 
     * @return AccountsReceivableCustomer instance with the new customer
     */
    public AccountsReceivableCustomer createCustomer();
    
    /**
     * Create new CustomerAddress object
     * 
     * @return AccountsReceivableCustomerAddress instance with the new customer address
     */
    public AccountsReceivableCustomerAddress createCustomerAddress();
    
    /**
     * This method builds the new customer number
     * @param newCustomer the new customer
     * @return the new customer number
     */
    public String getNextCustomerNumber(AccountsReceivableCustomer newCustomer);
    
    /**
     * 
     * This method saves customer
     * @param customer
     * @return
     */
    public void saveCustomer(AccountsReceivableCustomer customer);
    
    /**
     * 
     * This method returns the AccountsReceivableCustomerType for the given customerTypeDescription
     * @param customerTypeDescription
     * @return
     */
    public List<AccountsReceivableCustomerType> findByCustomerTypeDescription(String customerTypeDescription);

    public AccountsReceivableOrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode);

    public AccountsReceivableOrganizationOptions getOrganizationOptionsByPrimaryKey(Map<String, String> criteria);

    public void saveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException;

    public Document blanketApproveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException;

    public AccountsRecievableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails();

    public AccountsRecievableDocumentHeader createAccountsReceivableDocumentHeader();
    
    public ChartOrgHolder getPrimaryOrganization();

    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear);

    public boolean isUsingReceivableFAU();
    
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document);

    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode);

    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail);

    public void recalculateCustomerInvoiceDetail(AccountsReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail);

    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountsReceivableCustomerInvoice customerInvoiceDocument);

    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice invoice);

    /**
     * Look for open invoice documents by customer number and travel doc Id 
     * 
     * @param customerNumber
     * @param tripId
     * @return
     */
    public Collection<AccountsReceivableCustomerInvoice> getOpenInvoiceDocumentsByCustomerNumberForTrip(String customerNumber, String travelDocId);

    public AccountsReceivableNonInvoiced createNonInvoiced();

    public AccountsReceivableInvoicePaidApplied createInvoicePaidApplied();

    public AccountsRecievableDocumentHeader getNewAccountsReceivableDocumentHeader(String processingChart, String processingOrg);

    public AccountsReceivablePaymentApplicationDocument createPaymentApplicationDocument();

    public AccountsReceivableCashControlDetail createCashControlDetail();

    public AccountsReceivableCashControlDocument createCashControlDocument();
    
    public Document blanketApprovePaymentApplicationDocument(AccountsReceivablePaymentApplicationDocument paymentApplicationDocument, String travelDocumentIdentifier) throws WorkflowException;
    
    public AccountsReceivableCustomerInvoice createCustomerInvoiceDocument();
    
    /**
     * Create new {@link CustomerCreditMemoDocument} document
     * 
     * @return
     */
    public AccountsReceivableCustomerCreditMemo createCustomerCreditMemoDocument();
    
    /**
     * Blanket approve CRM doc
     * 
     * @param creditMemoDocument
     * @param annotation
     * @return
     * @throws WorkflowException
     */
    @SuppressWarnings("restriction")
    public Document blanketApproveCustomerCreditMemoDocument(AccountsReceivableCustomerCreditMemo creditMemoDocument, String annotation) throws WorkflowException;

    /**
     * Populate Customer Credit Memo document detail using the document own method
     * 
     * @param crmDocument
     * @param invoiceNumber
     * @param creditAmount
     * @return
     */
    public AccountsReceivableCustomerCreditMemo populateCustomerCreditMemoDocumentDetails(AccountsReceivableCustomerCreditMemo crmDocument, String invoiceNumber, KualiDecimal creditAmount);
    
    
}
