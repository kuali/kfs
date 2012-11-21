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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

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

        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(CustomerInvoiceDocument.class, customerInvoiceDocumentNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getCustomerInvoiceOpenAmount(java.util.List, java.lang.Integer, java.sql.Date)
     */
    @Override
    public Map<String, KualiDecimal> getCustomerInvoiceOpenAmount(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );

        Map<String, KualiDecimal> customerInvoiceOpenAmountMap = new HashMap<String, KualiDecimal>();

        Collection<? extends AccountsReceivableCustomerInvoice> customerInvoiceDocuments = this.getOpenCustomerInvoices(customerTypeCodes, customerInvoiceAge, invoiceBillingDateFrom);
        if(ObjectUtils.isNull(customerInvoiceDocuments)){
            return customerInvoiceOpenAmountMap;
        }

        for(AccountsReceivableCustomerInvoice invoiceDocument : customerInvoiceDocuments){
            KualiDecimal openAmount = invoiceDocument.getOpenAmount();

            if(ObjectUtils.isNotNull(openAmount) && openAmount.isPositive()){
                customerInvoiceOpenAmountMap.put(invoiceDocument.getDocumentNumber(), openAmount);
            }
        }

        return customerInvoiceOpenAmountMap;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenCustomerInvoices(java.util.List, java.lang.Integer, java.sql.Date)
     */
    @Override
    public Collection<? extends AccountsReceivableCustomerInvoice> getOpenCustomerInvoices(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );

        CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);

        return customerInvoiceDocumentService.getAllAgingInvoiceDocumentsByCustomerTypes(customerTypeCodes, customerInvoiceAge, invoiceBillingDateFrom);
    }

    @Override
    public AccountsReceivableCustomer createCustomer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableCustomerAddress createCustomerAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNextCustomerNumber(AccountsReceivableCustomer newCustomer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveCustomer(AccountsReceivableCustomer customer) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<AccountsReceivableCustomerType> findByCustomerTypeDescription(String customerTypeDescription) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableOrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableOrganizationOptions getOrganizationOptionsByPrimaryKey(Map<String, String> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    @Override
    public Document blanketApproveCustomerInvoiceDocument(AccountsReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsRecievableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsRecievableDocumentHeader createAccountsReceivableDocumentHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChartOrgHolder getPrimaryOrganization() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isUsingReceivableFAU() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document) {
        // TODO Auto-generated method stub

    }

    @Override
    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void recalculateCustomerInvoiceDetail(AccountsReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail) {
        // TODO Auto-generated method stub

    }

    @Override
    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountsReceivableCustomerInvoice customerInvoiceDocument) {
        // TODO Auto-generated method stub

    }

    @Override
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(AccountsReceivableCustomerInvoice invoice) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<AccountsReceivableCustomerInvoice> getOpenInvoiceDocumentsByCustomerNumberForTrip(String customerNumber, String travelDocId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsRecievableDocumentHeader getNewAccountsReceivableDocumentHeader(String processingChart, String processingOrg) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableCustomerInvoice createCustomerInvoiceDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableCustomerCreditMemo createCustomerCreditMemoDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document blanketApproveCustomerCreditMemoDocument(AccountsReceivableCustomerCreditMemo creditMemoDocument, String annotation) throws WorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableCustomerCreditMemo populateCustomerCreditMemoDocumentDetails(AccountsReceivableCustomerCreditMemo crmDocument, String invoiceNumber, KualiDecimal creditAmount) {
        // TODO Auto-generated method stub
        return null;
    }
}
