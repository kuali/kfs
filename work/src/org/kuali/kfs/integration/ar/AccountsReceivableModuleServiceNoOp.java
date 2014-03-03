/*
 * Copyright 2009 The Kuali Foundation
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

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;

public class AccountsReceivableModuleServiceNoOp implements AccountsReceivableModuleService {

    private Logger LOG = Logger.getLogger(getClass());

    @Override
    public ElectronicPaymentClaimingDocumentGenerationStrategy getAccountsReceivablePaymentClaimingStrategy() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return new ElectronicPaymentClaimingDocumentGenerationStrategy() {
            @Override
            public boolean userMayUseToClaim(Person claimingUser) {
                return false;
            }
            @Override
            public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user) {
                return null;
            }
            @Override
            public String getClaimingDocumentWorkflowDocumentType() {
                return null;
            }
            @Override
            public String getDocumentLabel() {
                return "AR NoOp Module Service";
            }
            @Override
            public boolean isDocumentReferenceValid(String referenceDocumentNumber) {
                return false;
            }

        };
    }

    public void addNoteToRelatedPaymentRequestDocument(String paymentRequestDocumentNumber, String noteText) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        // do nothing
    }

    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public Collection<AccountsReceivableCustomer> searchForCustomers(Map<String, String> fieldValues) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableCustomer findCustomer(String customerNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public Collection<AccountsReceivableCustomerAddress> searchForCustomerAddresses(Map<String, String> fieldValues) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableCustomerAddress findCustomerAddress(String customerNumber, String customerAddressIdentifer) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenCustomerInvoice(java.lang.String)
     */
    @Override
    public AccountsReceivableCustomerInvoice getOpenCustomerInvoice(String customerInvoiceDocumentNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getCustomerInvoiceOpenAmount(java.util.List, java.lang.Integer, java.sql.Date)
     */
    @Override
    public Map<String, KualiDecimal> getCustomerInvoiceOpenAmount(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenCustomerInvoices(java.util.List, java.lang.Integer, java.sql.Date)
     */
    @Override
    public Collection<? extends AccountsReceivableCustomerInvoice> getOpenCustomerInvoices(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#cancelInvoicesForTrip(java.lang.String)
     */
    @Override
    public void cancelInvoicesForTrip(String tripId, AccountsReceivableOrganizationOptions organizationOptions) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public AccountsReceivableCustomer createCustomer() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableCustomerAddress createCustomerAddress() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public String getNextCustomerNumber(AccountsReceivableCustomer newCustomer) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public void saveCustomer(AccountsReceivableCustomer customer) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public List<AccountsReceivableCustomerType> findByCustomerTypeDescription(String customerTypeDescription) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableOrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public void saveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public Document blanketApproveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableDocumentHeader createAccountsReceivableDocumentHeader() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public ChartOrgHolder getPrimaryOrganization() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public boolean isUsingReceivableFAU() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public void recalculateCustomerInvoiceDetail(AccountsReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountsReceivableCustomerInvoice customerInvoiceDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice invoice) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public Collection<AccountsReceivableCustomerInvoice> getOpenInvoiceDocumentsByCustomerNumberForTrip(String customerNumber, String travelDocId) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeader(String processingChart, String processingOrg) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableCustomerInvoice createCustomerInvoiceDocument() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableCustomerCreditMemo createCustomerCreditMemoDocument() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public Document blanketApproveCustomerCreditMemoDocument(AccountsReceivableCustomerCreditMemo creditMemoDocument, String annotation) throws WorkflowException {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableCustomerCreditMemo populateCustomerCreditMemoDocumentDetails(AccountsReceivableCustomerCreditMemo crmDocument, String invoiceNumber, KualiDecimal creditAmount) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public String createAndSaveCustomer(String description, ContractsAndGrantsBillingAgency agency) throws WorkflowException {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public String retrieveGLPEReceivableParameterValue() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAwardBilledToDateByProposalNumber(java.lang.Long)
     */
    @Override
    public KualiDecimal getAwardBilledToDateAmountByProposalNumber(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#calculateTotalPaymentsToDateByAward(java.lang.Long)
     */
    @Override
    public KualiDecimal calculateTotalPaymentsToDateByAward(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableCustomerAddress getPrimaryAddress(String customerNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableInvoiceTemplate findInvoiceTemplate(String invoiceTemplateCode) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public void saveInvoiceTemplate(AccountsReceivableInvoiceTemplate invoiceTemplate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public AccountsReceivableMilestoneSchedule getMilestoneSchedule() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public void setProposalNumber(AccountsReceivableMilestoneSchedule milestoneSchedule, Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public AccountsReceivablePredeterminedBillingSchedule getPredeterminedBillingSchedule() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public void setProposalNumber(AccountsReceivablePredeterminedBillingSchedule predeterminedBillingSchedule, Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public Date getLastBilledDate(ContractsAndGrantsBillingAward award) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public List<String> checkAwardContractControlAccounts(ContractsAndGrantsBillingAward award) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public boolean hasPredeterminedBillingSchedule(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public boolean hasMilestoneSchedule(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public String getContractsGrantsInvoiceDocumentType() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

}
