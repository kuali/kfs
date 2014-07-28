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
     * This method returns the Primary Organization for the User.
     *
     * @return ChartOrgHolder containing the chart/org for the Primary Organization for the User.
     */
    public ChartOrgHolder getPrimaryOrganization();

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
     * This method determines if the parameter controlling how the entries for the accounts receivable offset are determined
     * (GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD) is set to the FAU Offset Generation Method (currently option 3).
     *
     * @return true if using FAU Offset Generation Method, false otherwise
     */
    public boolean isUsingReceivableFAU();

    /**
     * This method sets the receivable accounting line for customer invoice.
     *
     * @param document Customer Invoice document to set the receivable accounting line on.
     */
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document);

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
     * This method returns the CustomerAddress specified as the primary address for a Customer.
     *
     * @param customerNumber
     * @return
     */
    public AccountsReceivableCustomerAddress getPrimaryAddress(String customerNumber);

    /**
     * This method returns the InvoiceTemplate corresponding to a given invoice template code.
     *
     * @param invoiceTemplateCode
     * @return
     */
    public AccountsReceivableInvoiceTemplate findInvoiceTemplate(String invoiceTemplateCode);

    /**
     * This method saves an InvoiceTemplate.
     *
     * @param invoiceTemplate
     */
    public void saveInvoiceTemplate(AccountsReceivableInvoiceTemplate invoiceTemplate);

    /**
     * This method returns a new instance of the MilestoneSchedule class.
     *
     * @return new MilestoneSchedule instance
     */
    public AccountsReceivableMilestoneSchedule getMilestoneSchedule();

    /**
     * This method sets the proposalNumber on a given MilestoneSchedule. This is used to associate the
     * MilestoneSchedule with an Award.
     *
     * @param milestoneSchedule
     * @param proposalNumber
     */
    public void setProposalNumber(AccountsReceivableMilestoneSchedule milestoneSchedule, Long proposalNumber);

    /**
     * This method returns a new instance of the PredeterminedBillingSchedule class.
     *
     * @return new PredeterminedBillingSchedule instance
     */
    public AccountsReceivablePredeterminedBillingSchedule getPredeterminedBillingSchedule();

    /**
     * This method sets the proposalNumber on a given PredeterminedBillingSchedule. This is used to associate the
     * PredeterminedBillingSchedule with an Award.
     *
     * @param predeterminedBillingSchedule
     * @param proposalNumber
     */
    public void setProposalNumber(AccountsReceivablePredeterminedBillingSchedule predeterminedBillingSchedule, Long proposalNumber);

    /**
     * Checks to see if the award corresponding to the passed in proposalNumber has a
     * MilestoneSchedule associated with it.
     *
     * @param proposalNumber proposalNumber for the Award use as key to look for MilestoneSchedule
     * @return true if there is an active MilestoneSchedule for this proposalNumber, false otherwise
     */
    public boolean hasMilestoneSchedule(Long proposalNumber);

    /**
     * Checks to see if the award corresponding to the passed in proposalNumber has a
     * PredeterminedBillingSchedule associated with it.
     *
     * @param proposalNumber proposalNumber for the Award use as key to look for PredeterminedBillingSchedule
     * @return true if there is an active PredeterminedBillingSchedule for this proposalNumber, false otherwise
     */
    public boolean hasPredeterminedBillingSchedule(Long proposalNumber);

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

    /**
     * Gets the Contracts & Grants Invoice Document Type
     *
     * @return Contracts & Grants Invoice Document Type
     */
    public String getContractsGrantsInvoiceDocumentType();

    /**
     * Determines whether the CG and Billing Enhancements are on from the system parameters
     *
     * @return true if Contracts and Grants Billing enhancement is enabled
     */
    public boolean isContractsGrantsBillingEnhancementActive();

    /**
     * Obtain list Kuali Coeus Award Status Codes that indicate the award should not be invoiced
     * from parameter. Used by Kuali Coeus module when Contracts and Grants Billing is enabled.
     *
     * @return list of award status codes
     */
    public Collection<String> getDoNotInvoiceStatuses();

}
