/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service;


import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsCategories;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionLookupResult;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * This class defines all the service methods for Contracts and Grants invoice Document.
 */
public interface ContractsGrantsInvoiceDocumentService extends CustomerInvoiceDocumentService {

    /**
     * This method creates Source Accounting lines enabling the creation of GLPEs in the document.
     *
     * @param document the cash control document
     * @throws WorkflowException
     */
    public void createSourceAccountingLinesAndGLPEs(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException;


    /**
     * This method creates Source Accounting lines enabling the creation of GLPEs in the document.
     *
     * @param document the cash control document
     * @throws WorkflowException
     */
    public void recalculateNewTotalBilled(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * @param contractsGrantsInvoiceDocument
     * @throws WorkflowException
     */
    public void prorateBill(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException;

    /**
     * @param invoiceDetailAccountObjectCodes
     */
    public void addToAccountObjectCodeBilledTotal(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes);

    /**
     * Returns the billed to date amount for the given Proposal Number for Milestones.
     *
     * @param proposalNumber
     * @return
     */
    public KualiDecimal getMilestonesBilledToDateAmount(Long proposalNumber);

    /**
     * Returns the billed to date amount for the given Proposal Number for Predetermined Billing.
     *
     * @param proposalNumber
     * @return
     */
    public KualiDecimal getPredeterminedBillingBilledToDateAmount(Long proposalNumber);

    /**
     * Returns the billed to date amount for the given Proposal Number.
     *
     * @param awardAccounts
     * @return
     */
    public List<AwardAccountObjectCodeTotalBilled> getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(List<ContractsAndGrantsBillingAwardAccount> awardAccounts);

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean adjustObjectCodeAmountsIfChanged(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * Retrieves all open Contracts and Grants Invoice Document.
     *
     * @param includeWorkflowHeaders
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> getAllOpenContractsGrantsInvoiceDocuments(boolean includeWorkflowHeaders);

    /**
     * This method removes the InvoiceDetailAccountObjectCodes where the current expenditure is zero.
     *
     * @param invoiceDetailAccountObjectCode
     */
    public void performInvoiceAccountObjectCodeCleanup(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes);

    /**
     * @param proposalNumber
     * @return
     */
    public KualiDecimal getAwardBilledToDateAmountByProposalNumber(Long proposalNumber);

    /**
     * This method retrieves the amount to draw for the award accounts
     *
     * @param awardAccounts
     * @return
     */
    public void setAwardAccountToDraw(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsAndGrantsBillingAward award);


    /**
     * This method retrieves the amount available to draw for the award accounts
     *
     * @param awardTotalAmount
     * @param awardAccount
     */
    public KualiDecimal getAmountAvailableToDraw(KualiDecimal awardTotalAmount, List<ContractsAndGrantsBillingAwardAccount> awardAccounts);


    /**
     * This method calculates the claim on cash balance for every award account.
     *
     * @param awardAccount
     */
    public KualiDecimal getClaimOnCashforAwardAccount(ContractsAndGrantsBillingAwardAccount awardAccount, java.sql.Date awardBeginningDate);


    /**
     * Returns all Contracts and Grants Invoice Documents in the system.
     *
     * @param includeWorkflowHeaders
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> getAllCGInvoiceDocuments(boolean includeWorkflowHeaders);


    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund
     *
     * @param locFund
     * @param errorFileName
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFund(String locFund, String errorFileName);

    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund group
     *
     * @param locFundGroup
     * @param errorFileName
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFundGroup(String locFundGroup, String errorFileName);


    /**
     * This method retrieves all invoices with open and with final status with param customer number
     *
     * @param customerNumber
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByCustomerNumber(String customerNumber, String errorFileName);

    /**
     * This method retrieves CG invoice documents that match the given field values
     *
     * @param fieldValues
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesByCriteria(Map fieldValues);

    /**
     * This method retrieves CG invoice documents that match the given field values and excludes
     * the given outside collection agency code
     *
     * @param fieldValues
     * @param outsideColAgencyCodeToExclude
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesForReferallExcludingOutsideCollectionAgency(Map fieldValues, String outsideColAgencyCodeToExclude);

    /**
     * This method retrieves CG invoice documents that match the given field values
     *
     * @param fieldValues field values to match
     * @param beginningInvoiceBillingDate Beginning invoice billing date for range
     * @param endingInvoicebillingDate Ending invoice billing date for range
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesByCriteriaAndBillingDateRange(Map fieldValues, java.sql.Date beginningInvoiceBillingDate, java.sql.Date endingInvoicebillingDate);

    /**
     * This method updates the Suspension Categories on the document
     *
     * @param contractsGrantsInvoiceDocument
     */
    public void updateSuspensionCategoriesOnDocument(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * This method would make sure the amounts of the currrent period are not included. So it calculates the cumulative and
     * subtracts the current period values. This would be done for Billing Frequencies - Monthly, Quarterly, Semi-Annual and Annual.
     *
     * @param glBalance
     * @return balanceAmount
     */
    public KualiDecimal retrieveAccurateBalanceAmount(java.sql.Date lastBilledDate, Balance glBalance);

    /**
     * This method get the milestones with the criteria defined and set value to isBilledIndicator.
     *
     * @param invoiceMilestones
     * @return
     */
    public void retrieveAndUpdateMilestones(List<InvoiceMilestone> invoiceMilestones, String string) throws Exception;

    /**
     * This method get the bills with the criteria defined and set value to isBilledIndicator.
     *
     * @param invoiceBills
     * @return
     */
    public void retrieveAndUpdateBills(List<InvoiceBill> invoiceBills, String value) throws Exception;

    /**
     * @param award
     * @return
     */
    public KualiDecimal calculateTotalPaymentsToDateByAward(ContractsAndGrantsBillingAward award);


    /**
     * This method calculates the Budget and cumulative amount for Award Account
     *
     * @param awardAccount
     * @return
     */
    public KualiDecimal getBudgetAndActualsForAwardAccount(ContractsAndGrantsBillingAwardAccount awardAccount, String balanceTypeCode, Date awardBeginningDate);


    /**
     * Check if the award is closed
     *
     * @param award
     * @return True if award's closing date is before the current day.
     */
    public boolean isAwardClosed(ContractsAndGrantsBillingAward award);


    /**
     * Check if Award Invoicing suspended by user.
     *
     * @return
     */
    public boolean isAwardInvoicingSuspendedByUser(ContractsAndGrantsBillingAward award);

    /**
     * Check if Award is past the stop date
     *
     * @return
     */
    public boolean isAwardPassedStopDate(ContractsAndGrantsBillingAward award);

    /**
     * Check if Award contains expired account or accounts
     *
     * @return
     */
    public boolean hasExpiredAccounts(ContractsAndGrantsBillingAward award);

    /**
     * Check if Award Invoicing Option is missing
     *
     * @return
     */
    public boolean isAwardInvoicingOptionMissing(ContractsAndGrantsBillingAward award);

    /**
     * Check if Award Organization is incomplete
     *
     * @return
     */
    public boolean isAwardOrganizationIncomplete(ContractsAndGrantsBillingAward award);


    /**
     * Get award accounts's control accounts
     *
     * @return
     */
    public Collection<Account> getContractControlAccounts(ContractsAndGrantsBillingAward award);


    /**
     * Check iF Award has no accounts assigned
     *
     * @return
     */
    public boolean hasNoActiveAccountsAssigned(ContractsAndGrantsBillingAward award);


    /**
     * Check if Preferred Billing Frequency is set correctly.
     *
     * @param award
     * @return False if preferred billing schedule is set as perdetermined billing schedule or milestone billing schedule, and award
     *         has no award account or more than 1 award accounts assigned.
     */
    public boolean isPreferredBillingFrequencySetCorrectly(ContractsAndGrantsBillingAward award);


    /**
     * Check if the value of PreferredBillingFrequency is in the value set.
     *
     * @param award
     * @return
     */
    public boolean isValueOfPreferredBillingFrequencyValid(ContractsAndGrantsBillingAward award);

    /**
     * Check if the final Invoice for all accounts in the invoice have already been built.
     *
     * @param award
     * @return
     */
    public boolean isAwardFinalInvoiceAlreadyBuilt(ContractsAndGrantsBillingAward award);


    /**
     * Retrive all the expired accounts of an award
     *
     * @return
     */
    public Collection<Account> getExpiredAccountsOfAward(ContractsAndGrantsBillingAward award);


    /**
     * Checks if the award has valid milestones to invoice.
     *
     * @return true if has valid milestones to invoice. false if not.
     */
    public boolean hasNoMilestonesToInvoice(ContractsAndGrantsBillingAward award);

    /**
     * Checks if the award has valid milestones to invoice.
     *
     * @return true if has valid milestones to invoice. false if not.
     */
    public boolean hasNoBillsToInvoice(ContractsAndGrantsBillingAward award);


    /**
     * To create a generic method to retrieve all active awards based on the criteria passed.
     *
     * @return
     */
    public List<ContractsAndGrantsBillingAward> getActiveAwardsByCriteria(Map<String, Object> criteria);

    /**
     * Check if agency owning award has no customer record
     *
     * @return
     */
    public boolean owningAgencyHasNoCustomerRecord(ContractsAndGrantsBillingAward award);


    /**
     * This method returns the invoices realted to the PaymentApplicationNumber.
     *
     * @param paymentApplicationNumberCorrecting
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> getContractsGrantsInvoiceDocumentAppliedByPaymentApplicationNumber(String paymentApplicationNumberCorrecting);


    /**
     * This method checks if the System Information and ORganization Accounting Default are setup for the Chart Code and Org Code
     * from the award accounts.
     *
     * @param award
     * @return
     */
    public boolean isChartAndOrgNotSetupForInvoicing(ContractsAndGrantsBillingAward award);

    /**
     * this method checks If all accounts of award has invoices in progress.
     *
     * @param award
     * @return
     */
    public boolean isInvoiceInProgress(ContractsAndGrantsBillingAward award);

    /**
     * This method checks if there is atleast one AR Invoice Account present when the GLPE is 3.
     *
     * @param award
     * @return
     */
    public boolean hasARInvoiceAccountAssigned(ContractsAndGrantsBillingAward award);

    /**
     * This method checks if the Offset Definition is setup for the Chart Code from the award accounts.
     *
     * @param award
     * @return
     */
    public boolean isOffsetDefNotSetupForInvoicing(ContractsAndGrantsBillingAward award);

    /**
     * To retrieve processing chart code and org code from the billing chart code and org code
     *
     * @param coaCode
     * @param orgCode
     * @return list of processing codes
     */
    public List<String> getProcessingFromBillingCodes(String coaCode, String orgCode);


    /**
     * To retrieve invoices matching the dunning letter distribution lookup values.
     *
     * @param fieldValues
     * @return collection of DunningLetterDistributionLookupResult
     */
    public Collection<DunningLetterDistributionLookupResult> getInvoiceDocumentsForDunningLetterLookup(Map<String, String> fieldValues);

    /**
     * To retrieve the list of ContractsGrantsInvoiceDocument from proposal number.
     *
     * @param proposalNumber
     * @param outputFileStream
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByProposalNumber(Long proposalNumber, String errorFileName);

    /**
     * To retrieve the payment amount by given document number.
     *
     * @param documentNumber The invoice number of the document.
     * @return Returns the total payment amount.
     */
    public KualiDecimal retrievePaymentAmountByDocumentNumber(String documentNumber);

    /**
     * To retrieve the first payment date by given document number.
     *
     * @param documentNumber The invoice number of the document.
     * @return Returns the first payment date.
     */
    public java.sql.Date retrievePaymentDateByDocumentNumber(String documentNumber);

    /**
     * Determine if the collectorPrincipalId can view the invoice, leverages role qualifiers
     * on the CGB Collector role to perform the check.
     *
     * @param invoice The invoice to check if the collector can view.
     * @param collectorPrincipalId The principal id of the collector to check permissions for.
     * @return Returns true if the collector can view the invoice, false otherwise.
     */
    public boolean canViewInvoice(ContractsGrantsInvoiceDocument invoice, String collectorPrincipalId);

    /**
     * This method retrieves the CGDocs with their workflow headers.
     *
     * @param invoices
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> attachWorkflowHeadersToCGInvoices(Collection<ContractsGrantsInvoiceDocument> invoices);

    /**
     * Gets the invoice documents based on field values.
     *
     * @param fieldValues The fields which needs to be put in criteria.
     * @return Returns the list of ReferralToCollectionsLookupResult.
     */
    public Collection<ReferralToCollectionsLookupResult> getInvoiceDocumentsForReferralToCollectionsLookup(Map<String, String> fieldValues);

    /**
     * This method sets the last billed date to Award and Award Account objects based on the status of the invoice.
     * If this is the final invoice, also sets Final Billed indicator on Award Account
     *
     * @param document ContractGrantsInvoiceDocument referencing the Award and Award Account objects to update
     */
    public void updateLastBilledDate(ContractsGrantsInvoiceDocument document);

    /**
     * This method updates the Bills and Milestone objects isItBilles Field.
     *
     * @param string
     */
    public void updateBillsAndMilestones(String string,List<InvoiceMilestone> invoiceMilestones,List<InvoiceBill> invoiceBills);

    /**
     * This method generates the attached invoices for the invoice addresses in the Contracts and Grants Invoice Document.
     */
    public void generateInvoicesForInvoiceAddresses(ContractsGrantsInvoiceDocument document);


    /**
     * This method updates AwardAccounts
     */
    public void updateUnfinalizationToAwardAccount(List<InvoiceAccountDetail> accountDetails,Long proposalNumber);

    /**
     * Corrects the Contracts and Grants Invoice Document.
     *
     * @throws WorkflowException
     */
    public void correctContractsGrantsInvoiceDocument(ContractsGrantsInvoiceDocument document) throws WorkflowException;

    /**
     * This method corrects the Maintenance Document for Predetermined Billing
     *
     * @throws WorkflowException
     */
    public void correctBills(List<InvoiceBill> invoiceBills) throws WorkflowException;

    /**
     * This method corrects the Maintenance Document for milestones
     *
     * @throws WorkflowException
     */
    public void correctMilestones(List<InvoiceMilestone> invoiceMilestones) throws WorkflowException;

    /**
     * This method takes all the applicable attributes from the associated award object and sets those attributes into their
     * corresponding invoice attributes.
     *
     * @param award The associated award that the invoice will be linked to.
     */
    public void populateInvoiceFromAward(ContractsAndGrantsBillingAward award, List<ContractsAndGrantsBillingAwardAccount> awardAccounts,ContractsGrantsInvoiceDocument document);

    /**
     * This method takes a ContractsAndGrantsCategory, retrieves the specified object code or object code range. It then parses this
     * string, and returns all the possible object codes specified by this range.
     *
     * @param category
     * @return Set<String> objectCodes
     */
    public Set<String> getObjectCodeArrayFromSingleCategory(ContractsAndGrantsCategories category,ContractsGrantsInvoiceDocument document) throws IllegalArgumentException;


    /**
     * Calculate the lastBilledDate for the Award based on it's AwardAccounts
     *
     * @param award ContractsAndGrantsBillingAward to calculate lastBilledDate for
     * @return the lastBilledDate
     */
    public java.sql.Date getLastBilledDate(ContractsAndGrantsBillingAward award);
}
