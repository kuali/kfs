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
package org.kuali.kfs.module.ar.document.service;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.report.util.CustomerStatementResultHolder;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;

public interface CustomerInvoiceDocumentService {

    /**
     * Converts discount lines on the customer invoice document to paidapplieds. This method is only intended to be used once the
     * document is at least in the Processed state, and will throw an error if used on a document in an earlier state. This method
     * is intended to be called from the CustomerInvoiceDocument.handleRouteStatusChange
     *
     * @param invoice A populated Invoice document that is at least PROCESSED.
     */
    public void convertDiscountsToPaidApplieds(CustomerInvoiceDocument invoice);

    /**
     * Retrieves all invoice documents that are Open with outstanding balances, including workflow headers.
     *
     * @return A collection of CustomerInvoiceDocument documents, or an empty list of no matches.
     */
    public Collection<CustomerInvoiceDocument> getAllOpenCustomerInvoiceDocuments();

    /**
     * Retrieves all invoice documents that are Open with outstanding balances. Will NOT retrieve workflow headers, so results of
     * this are not suitable for using to route, save, or otherwise perform workflow operations upon.
     *
     * @return
     */
    public Collection<CustomerInvoiceDocument> getAllOpenCustomerInvoiceDocumentsWithoutWorkflow();

    /**
     * Gets invoices without workflow headers, retrieves the workflow headers and returns invoices with workflow headers.
     *
     * @return
     */
    public Collection<CustomerInvoiceDocument> attachWorkflowHeadersToTheInvoices(Collection<CustomerInvoiceDocument> invoices);

    /**
     * Retrieves all Open Invoices for this given Customer Number. IMPORTANT - Workflow headers and status are not retrieved by this
     * method, only the raw Customer Invoice Document from the Database. If you need a full workflow document, you can do use
     * DocumentService to retrieve each by document number.
     *
     * @param customerNumber
     * @return
     */
    public Collection<CustomerInvoiceDocument> getOpenInvoiceDocumentsByCustomerNumber(String customerNumber);

    /**
     * Retrieves all Open Invoices for the given Customer Name and Customer Type Code Note that the customerName field is turned
     * into a 'LIKE customerName*' query. IMPORTANT - Workflow headers and status are not retrieved by this method, only the raw
     * Customer Invoice Document from the Database. If you need a full workflow document, you can do use DocumentService to retrieve
     * each by document number.
     *
     * @param customerName
     * @param customerTypeCode
     * @return
     */
    public Collection getOpenInvoiceDocumentsByCustomerNameByCustomerType(String customerName, String customerTypeCode);

    /**
     * Retrieves all Open Invoices for the given Customer Name. Note that this is a leading substring search, so whatever is entered
     * into the customerName field is turned into a 'LIKE customerName*' query. IMPORTANT - Workflow headers and status are not
     * retrieved by this method, only the raw Customer Invoice Document from the Database. If you need a full workflow document, you
     * can do use DocumentService to retrieve each by document number.
     *
     * @param customerName
     * @return
     */
    public Collection<CustomerInvoiceDocument> getOpenInvoiceDocumentsByCustomerName(String customerName);

    /**
     * Retrieves all Open Invoices for the given Customer Type Code. IMPORTANT - Workflow headers and status are not retrieved by
     * this method, only the raw Customer Invoice Document from the Database. If you need a full workflow document, you can do use
     * DocumentService to retrieve each by document number.
     *
     * @param customerTypeCode
     * @return
     */
    public Collection<CustomerInvoiceDocument> getOpenInvoiceDocumentsByCustomerType(String customerTypeCode);

    /**
     * This method sets up default values for customer invoice document on initiation.
     *
     * @param document
     */
    public void setupDefaultValuesForNewCustomerInvoiceDocument(CustomerInvoiceDocument document);

    /**
     * This method sets up default values for customer invoice document when copied.
     *
     * @param customerInvoiceDocument
     */
    public void setupDefaultValuesForCopiedCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * If the customer number and address identifiers are present, display customer information
     *
     * @param customerInvoiceDocument
     */
    public void loadCustomerAddressesForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * This method returns a Collection of CustomerInvoiceDocuments corresponding to a customerNumber.
     *
     * @param customerNumber used to find invoices
     * @return Collection<CustomerInvoiceDocument> invoice documents
     */
    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByCustomerNumber(String customerNumber);

    /**
     * This method returns a Collection of CustomerInvoiceDetails for a customer invoice document number.
     *
     * @param customerInvoiceDocumentNumber used to find customer invoice details for a customer invoice document
     * @return Collection<CustomerInvoiceDetail> customer invoice details
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(String customerInvoiceDocumentNumber);

    /**
     * This method returns a Collection of CustomerInvoiceDetails for a customer invoice document.
     *
     * @param customerInvoiceDocument used to find customer invoice details
     * @return Collection<CustomerInvoiceDetail> customer invoice details
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * This method returns a Collection of CustomerInvoiceDetails for a customer invoice document.
     * Cached for better performance
     *
     * @param customerInvoiceDocument used to find customer invoice details
     * @return Collection<CustomerInvoiceDetail> customer invoice details
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocumentWithCaching(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * This method returns a Customer for an invoice based on organizationInvoiceNumber.
     *
     * @param invoiceNumber organizationInvoiceNumber used to find invoice and get the customer
     * @return Customer for the invoice corresponding to the organizationInvoiceNumber
     */
    public Customer getCustomerByOrganizationInvoiceNumber(String invoiceNumber);

    /**
     * This method returns a CustomerInvoiceDocument based on organizationInvoiceNumber.
     *
     * @param organizationInvoiceNumber used to find invoice and get the customer
     * @return CustomerInvoiceDocument corresponding to the organizationInvoiceNumber
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber);

    /**
     * This method returns a Customer for an invoice based on invoice document number.
     *
     * @param documentNumber used to find the invoice and get the customer
     * @return Customer for the invoice corresponding to the documentNumber
     */
    public Customer getCustomerByInvoiceDocumentNumber(String documentNumber);

    /**
     * This method returns a CustomerInvoiceDocument based on invoice document number.
     *
     * @param invoiceDocumentNumber used to find the invoice
     * @return CustomerInvoiceDocument corresponding to the invoiceDocumentNumber
     */
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String invoiceDocumentNumber);

    /**
     * Gets the printable customer invoice for the specified initiatorPrincipalName.
     *
     * @param initiatorPrincipalName the specified initiatorPrincipalName
     * @return the printable customer invoice for the specified initiatorPrincipalName
     */
    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsByInitiatorPrincipalName(String initiatorPrincipalName);

    /**
     * Gets the printable customer invoice for the specified chartOfAccountsCode and organizationCode.
     *
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return the printable customer invoice for the specified chartOfAccountsCode and organizationCode.
     */
    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Gets the printable customer invoice for the BillingStatement by chartOfAccountsCode and organizationCode.
     *
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return the printable customer invoice for the BillingStatement.
     */
    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsForBillingStatementByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Gets the printable customer invoice for the specified chartOfAccountsCode and organizationCode.
     *
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return the printable customer invoice
     */
    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Gets customer invoice for the specified chartOfAccountsCode and organizationCode.
     *
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return customer invoice
     */
    public List<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Get list of customer invoice by processing chartOfAccountsCode and organizationCode.
     *
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return list of customer invoice
     */
    public List<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Gets customer invoice by account number.
     *
     * @param accountNumber
     * @return collection of customer invoice.
     */
    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByAccountNumber(String accountNumber);

    /**
     * This method returns the NonInvoicedDistributions for an invoice based on documentNumber.
     *
     * @param documentNumber used to find the invoice
     * @return Collection<NonInvoicedDistribution> non invoiced distributions for the invoice corresponding to the documentNumber
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber);

    /**
     * This method returns the total NonInvoicedDistribution amount for an invoice based on documentNumber.
     *
     * @param documentNumber used to find the invoice
     * @return KualiDecimal non invoiced distribution total for the invoice
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(String documentNumber);

    /**
     * This method returns the total NonInvoicedDistribution amount for an invoice.
     *
     * @param invoice used to get the non invoiced distributions to total
     * @return KualiDecimal non invoiced distribution total for the invoice
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(CustomerInvoiceDocument invoice);

    /**
     * This method returns the total InvoicePaidApplied amount for an invoice based on documentNumber.
     *
     * @param documentNumber used to find the invoice
     * @return KualiDecimal paid applied total for the invoice
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(String documentNumber);

    /**
     * This method returns the total InvoicePaidApplied amount for an invoice.
     *
     * @param invoice used to get the invoice paid applied objects to total
     * @return KualiDecimal paid applied total for the invoice
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(CustomerInvoiceDocument invoice);

    /**
     * This method updates the open invoice indicator if amounts have been completely paid off
     *
     * @param invoice
     */
    public void closeCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * This method returns the total open amount for an invoice based on customerInvoiceDocumentNumber.
     *
     * @param customerInvoiceDocumentNumber used to find the invoice
     * @return KualiDecimal total open amount for the invoice
     */
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(String customerInvoiceDocumentNumber);


    /**
     * This method returns the total open amount for an invoice based on customerInvoiceDocumentNumber.
     *
     * @param customerInvoiceDocumentNumber invoice used to calculate total open amount
     * @return KualiDecimal total open amount for the invoice
     */
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * get total amount of customer invoice by customer invoice.
     *
     * @param customerInvoiceDocument
     * @return
     */
    public KualiDecimal getOriginalTotalAmountForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * This method checks if the invoice is final.
     *
     * @param invDocumentNumber invoice to check
     * @return true if invoice is final, false otherwise
     */
    public boolean checkIfInvoiceNumberIsFinal(String invDocumentNumber);

    /**
     * Updates report date
     *
     * @param docNumber
     */
    public void updateReportedDate(String docNumber);

    /**
     * Updates statement info
     *
     * @param data
     */
    public void updateReportedInvoiceInfo(CustomerStatementResultHolder data);

    /**
     * get all customer invoice documents that are open and with the given age
     *
     * @param charts the selected charts of accounts
     * @param organizations the selected organization codes
     * @param invoiceAge the given invoice document age
     *
     * @return all customer invoice documents that are open and with the given age
     */
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByBilling(List<String> charts, List<String> organizations, Integer invoiceAge);

    /**
     * get all customer invoice documents that are open and with the given age
     *
     * @param charts the selected charts of accounts
     * @param organizations the selected organization codes
     * @param invoiceAge the given invoice document age
     *
     * @return all customer invoice documents that are open and with the given age
     */
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByProcessing(List<String> charts, List<String> organizations, Integer invoiceAge);

    /**
     * get all customer invoice documents that are open and with the given age
     *
     * @param charts the selected charts of accounts
     * @param accounts the selected account numbers
     * @param invoiceAge the given invoice document age
     *
     * @return all customer invoice documents that are open and with the given age
     */
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByAccounts(List<String> charts, List<String> accounts, Integer invoiceAge);

    /**
     * get all customer invoice documents that are open and with the given age and customer types
     *
     * @param customerTypes the given customer types
     * @param invoiceAge the given invoice document age
     * @param invoiceBillingDateFrom the given invoice billing from date
     *
     * @return all customer invoice documents that are open and with the given age
     */
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByCustomerTypes(List<String> customerTypes, Integer invoiceAge, Date invoiceDueDateFrom);

    /**
     * Adds a note to the CustomerInvoiceDocument about what person and document numbers closed it. This only occurs if
     * openInvoiceIndicator=true at the time of calling this method.
     *
     * @param documentToClose- the document about to be closed which we want to add the note too
     * @param closingDocument- the closing document
     */
    public void addCloseNote(CustomerInvoiceDocument documentToClose, WorkflowDocument closingDocument);
}
