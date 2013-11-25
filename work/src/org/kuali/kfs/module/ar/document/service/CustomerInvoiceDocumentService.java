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
     */
    public void loadCustomerAddressesForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * @param customerNumber
     * @return
     */
    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByCustomerNumber(String customerNumber);

    /**
     * @param customerInvoiceDocumentNumber
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(String customerInvoiceDocumentNumber);

    /**
     * @param customerInvoiceDocument
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);


    /**
     * Cached for better performance
     *
     * @param customerInvoiceDocument
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocumentWithCaching(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * @param invoiceNumber
     * @return
     */
    public Customer getCustomerByOrganizationInvoiceNumber(String invoiceNumber);

    /**
     * @param organizationInvoiceNumber
     * @return
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber);

    /**
     * @param documentNumber
     * @return
     */
    public Customer getCustomerByInvoiceDocumentNumber(String documentNumber);

    /**
     * @param invoiceDocumentNumber
     * @return
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
     * @param documentNumber
     * @return
     */
    // public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(String documentNumber);
    /**
     * @param documentNumber
     * @return
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber);

    /**
     * @param documentNumber
     * @return
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(String documentNumber);

    /**
     * @param invoice
     * @return
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(CustomerInvoiceDocument invoice);

    /**
     * @param documentNumber
     * @return
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(String documentNumber);

    /**
     * @param invoice
     * @return
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(CustomerInvoiceDocument invoice);

    /**
     * This method updates the open invoice indicator if amounts have been completely paid off
     *
     * @param invoice
     */
    public void closeCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * This method...
     *
     * @param customerInvoiceDocumentNumber
     * @return
     */
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(String customerInvoiceDocumentNumber);


    /**
     * This method...
     *
     * @param customerInvoiceDocumentNumber
     * @return
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
     * 
     * 
     * @param invDocumentNumber
     * @return
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
}
