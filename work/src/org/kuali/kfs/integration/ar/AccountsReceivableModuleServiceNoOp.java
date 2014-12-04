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

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
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
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
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
}
