/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.integration.ar;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;

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
     * This method Creates and Saves a customer when CG Agency document does to final.
     *
     * @param description
     * @param agency
     * @return customerNumber
     * @throws WorkflowException
     */
    public String createAndSaveCustomer(String description, ContractsAndGrantsBillingAgency agency) throws WorkflowException;

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
     * @param invoiceDueDateFrom the from date of due date of the invoice
     *
     * @return a set of the open amounts indexed by the customer invoice numbers
     */
    public Map<String, KualiDecimal> getCustomerInvoiceOpenAmount(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceDueDateFrom);

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
     * Undoes the actions for any invoices which were created for the given trip id (since KFS TEM uses invoices to handle accounting for travel advances)
     * @param tripId the id of the trip to remove entries for
     * @organizationOptions the organization options which the trip used
     */
    public void cancelInvoicesForTrip(String tripId, AccountsReceivableOrganizationOptions organizationOptions);

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
     *
     * @param newCustomer the new customer
     * @return the new customer number
     */
    public String getNextCustomerNumber(AccountsReceivableCustomer newCustomer);

    /**
     * This method saves customer
     *
     * @param customer
     * @return
     */
    public void saveCustomer(AccountsReceivableCustomer customer);

    /**
     * This method returns the AccountsReceivableCustomerType for the given customerTypeDescription
     *
     * @param customerTypeDescription
     * @return
     */
    public List<AccountsReceivableCustomerType> findByCustomerTypeDescription(String customerTypeDescription);

    /**
     * This method returns Organization Options for the given chart/org.
     *
     * @param chartOfAccountsCode chart used to retrieve the Organization Options
     * @param organizationCode org used to retrieve the Organization Options
     * @return Organziation Options corresponding to the given chart/org.
     */
    public AccountsReceivableOrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode);

    /**
     * This method saves the Customer Invoice Document.
     *
     * @param customerInvoiceDocument document to save
     * @throws WorkflowException
     */
    public void saveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException;

    /**
     * This method blanket approves the Customer Invoice Document.
     *
     * @param customerInvoiceDocument document to blanket approve
     * @return document that has been blanket approved
     * @throws WorkflowException
     */
    public Document blanketApproveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException;

    /**
     * This method returns a new instance of the CustomerInvoiceRecurrenceDetails class.
     *
     * @return new CustomerInvoiceRecurrenceDetails object.
     */
    public AccountsReceivableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails();

    /**
     * This method returns a new instnace of the AccountsReceivableDocumentHeader class.
     *
     * @return new AccountsReceivableDocumentHeader object.
     */
    public AccountsReceivableDocumentHeader createAccountsReceivableDocumentHeader();

    /**
     * This method returns the System Information corresponding to a given chart/org/fiscal year parameters.
     *
     * @param chartOfAccountsCode chart code used to find System Information
     * @param organizationCode org code used to find System Information
     * @param currentFiscalYear fiscal year used to find System Information
     * @return System Information for given parameters
     */
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear);

    /**
     * This method returns a Customer Invoice Detail for the given invoice item code, chart and org.
     *
     * @param invoiceItemCode invoice item code used to search for a Customer Invoice Detail
     * @param processingChartCode chart code used to search for a Customer Invoice Detail
     * @param processingOrgCode org code used to search for a Customer Invoice Detail
     *
     * @return Customer Invoice Detail corresponding to the given parameters
     */
    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode);

    /**
     * This method returns an Object code based on the value of the parameter controlling how the entries for
     * the accounts receivable offset are determined.
     *
     * @param customerInvoiceDetail AccountsReceivableCustomerInvoiceDetail used to determine the object code
     *
     * @return Object Code based on the offset generation parameter and the given customerInvoiceDetail
     */
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail);

    /**
     * This method is used to recalculate a customer invoice detail based on updated values
     *
     * @param customerInvoiceDocument
     * @param detail
     */
    public void recalculateCustomerInvoiceDetail(AccountsReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail);

    /**
     * This method is used to make sure the amounts are calculated correctly and the correct AR object code is in place
     *
     * @param detail
     * @param customerInvoiceDocument
     */
    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountsReceivableCustomerInvoice customerInvoiceDocument);

    /**
     * This method returns the total open amount for a given Customer Invoice document.
     *
     * @param invoice Customer Invoice document used to calculate the open amount
     * @return open amount for the invoice
     */
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice invoice);

    /**
     * Look for open invoice documents by customer number and travel doc Id
     *
     * @param customerNumber
     * @param tripId
     * @return
     */
    public Collection<AccountsReceivableCustomerInvoice> getOpenInvoiceDocumentsByCustomerNumberForTrip(String customerNumber, String travelDocId);

    /**
     * Get account receivable doc header
     *
     * @param processingChart
     * @param processingOrg
     * @return
     */
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeader(String processingChart, String processingOrg);

    /**
     * Create customer invoice document
     *
     * @return
     */
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
