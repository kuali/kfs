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
package org.kuali.kfs.integration.ar;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
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

    /**
     *
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public AccountsReceivableOrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode);

    /**
     *
     * @param customerInvoiceDocument
     * @throws WorkflowException
     */
    public void saveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException;

    /**
     *
     * @param customerInvoiceDocument
     * @return
     * @throws WorkflowException
     */
    public Document blanketApproveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException;

    /**
     *
     * @return
     */
    public AccountsReceivableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails();

    /**
     *
     * @return
     */
    public AccountsReceivableDocumentHeader createAccountsReceivableDocumentHeader();

    /**
     *
     * @return
     */
    public ChartOrgHolder getPrimaryOrganization();

    /**
     *
     * @param chartOfAccountsCode
     * @param organizationCode
     * @param currentFiscalYear
     * @return
     */
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear);

    /**
     *
     * @return
     */
    public boolean isUsingReceivableFAU();

    /**
     *
     * @param document
     */
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document);

    /**
     *
     * @param invoiceItemCode
     * @param processingChartCode
     * @param processingOrgCode
     * @return
     */
    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode);

    /**
     *
     * @param customerInvoiceDetail
     * @return
     */
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail);

    /**
     *
     * @param customerInvoiceDocument
     * @param detail
     */
    public void recalculateCustomerInvoiceDetail(AccountsReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail);

    /**
     *
     * @param detail
     * @param customerInvoiceDocument
     */
    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountsReceivableCustomerInvoice customerInvoiceDocument);

    /**
     *
     * @param invoice
     * @return
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
     * Create cusotmer invoice document
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

    /**
     * This method retrieves the value of the Parameter GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD
     *
     * @param parameterName
     * @return
     */
    public String retrieveGLPEReceivableParameterValue();

    /**
     * This method gets the award billed to date amount using ContractsGrantsInvoiceDocumentService
     *
     * @param roposalNumber
     * @return
     */
    public KualiDecimal getAwardBilledToDateAmountByProposalNumber(Long proposalNumber);

    /**
     * This method calculates total payments to date by Award using ContractsGrantsInvoiceDocumentService
     *
     * @param proposalNumber
     * @return
     */
    public KualiDecimal calculateTotalPaymentsToDateByAward(Long proposalNumber);

    /**
     *
     * This method returns the CustomerAddress specified as the primary address for a Customer.
     * @param customerNumber
     * @return
     */

    public AccountsReceivableCustomerAddress getPrimaryAddress(String customerNumber);

    public AccountsReceivableInvoiceTemplate findInvoiceTemplate(String invoiceTemplateCode);

    public void saveInvoiceTemplate(AccountsReceivableInvoiceTemplate invoiceTemplate);

    public AccountsReceivableMilestoneSchedule getMilestoneSchedule();

    public void setProposalNumber(AccountsReceivableMilestoneSchedule milestoneSchedule, Long proposalNumber);

    public AccountsReceivablePredeterminedBillingSchedule getPredeterminedBillingSchedule();

    public void setProposalNumber(AccountsReceivablePredeterminedBillingSchedule predeterminedBillingSchedule, Long proposalNumber);

    /**
     * Calculate the lastBilledDate for the Award based on it's AwardAccounts
     *
     * @param award the Award used to calculate lastBilledDate
     * @return the lastBilledDate
     */
    public Date getLastBilledDate(ContractsAndGrantsBillingAward award);

    /**
     * This method checks the Contract Control account set for Award Account based on award's invoicing option.
     *
     * @return errorString
     */
    public List<String> checkAwardContractControlAccounts(ContractsAndGrantsBillingAward award);


}
