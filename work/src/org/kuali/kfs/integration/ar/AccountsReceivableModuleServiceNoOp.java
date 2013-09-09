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
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
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
        // do nothing
    }

    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber) {
        return null;
    }

    @Override
    public Collection<AccountsReceivableCustomer> searchForCustomers(Map<String, String> fieldValues) {
        return null;
    }

    @Override
    public AccountsReceivableCustomer findCustomer(String customerNumber) {
        return null;
    }

    @Override
    public Collection<AccountsReceivableCustomerAddress> searchForCustomerAddresses(Map<String, String> fieldValues) {
        return null;
    }

    @Override
    public AccountsReceivableCustomerAddress findCustomerAddress(String customerNumber, String customerAddressIdentifer) {
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
    public void cancelInvoicesForTrip(String tripId, AccountsReceivableOrganizationOptions organizationOptions) {}

    @Override
    public AccountsReceivableCustomer createCustomer() {
        return null;
    }

    @Override
    public AccountsReceivableCustomerAddress createCustomerAddress() {
        return null;
    }

    @Override
    public String getNextCustomerNumber(AccountsReceivableCustomer newCustomer) {
        return null;
    }

    @Override
    public void saveCustomer(AccountsReceivableCustomer customer) {}

    @Override
    public List<AccountsReceivableCustomerType> findByCustomerTypeDescription(String customerTypeDescription) {
        return null;
    }

    @Override
    public AccountsReceivableOrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode) {
        return null;
    }

    @Override
    public void saveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {}

    @Override
    public Document blanketApproveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        return null;
    }

    @Override
    public AccountsReceivableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails() {
        return null;
    }

    @Override
    public AccountsReceivableDocumentHeader createAccountsReceivableDocumentHeader() {
        return null;
    }

    @Override
    public ChartOrgHolder getPrimaryOrganization() {
        return null;
    }

    @Override
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear) {
        return null;
    }

    @Override
    public boolean isUsingReceivableFAU() {
        return false;
    }

    @Override
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document) {}

    @Override
    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode) {
        return null;
    }

    @Override
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail) {
        return null;
    }

    @Override
    public void recalculateCustomerInvoiceDetail(AccountsReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail) {}

    @Override
    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountsReceivableCustomerInvoice customerInvoiceDocument) {}

    @Override
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice invoice) {
        return null;
    }

    @Override
    public Collection<AccountsReceivableCustomerInvoice> getOpenInvoiceDocumentsByCustomerNumberForTrip(String customerNumber, String travelDocId) {
        return null;
    }

    @Override
    public AccountsReceivableDocumentHeader getNewAccountsReceivableDocumentHeader(String processingChart, String processingOrg) {
        return null;
    }

    @Override
    public AccountsReceivableCustomerInvoice createCustomerInvoiceDocument() {
        return null;
    }

    @Override
    public AccountsReceivableCustomerCreditMemo createCustomerCreditMemoDocument() {
        return null;
    }

    @Override
    public Document blanketApproveCustomerCreditMemoDocument(AccountsReceivableCustomerCreditMemo creditMemoDocument, String annotation) throws WorkflowException {
        return null;
    }

    @Override
    public AccountsReceivableCustomerCreditMemo populateCustomerCreditMemoDocumentDetails(AccountsReceivableCustomerCreditMemo crmDocument, String invoiceNumber, KualiDecimal creditAmount) {
        return null;
    }

    @Override
    public String createAndSaveCustomer(String description, ContractsAndGrantsCGBAgency agency) throws WorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String retrieveGLPEReceivableParameterValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAwardBilledToDateByProposalNumber(java.lang.Long)
     */
    @Override
    public KualiDecimal getAwardBilledToDateByProposalNumber(Long proposalNumber) {
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
    public AccountsReceivableMilestoneSchedule getMilestoneSchedule() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setProposalNumber(AccountsReceivableMilestoneSchedule milestoneSchedule, Long proposalNumber) {
        // TODO Auto-generated method stub

    }

}
