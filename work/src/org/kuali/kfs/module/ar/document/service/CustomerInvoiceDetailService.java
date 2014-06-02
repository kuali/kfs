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

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

/**
 * This class provides services related to the customer invoice document
 */
public interface CustomerInvoiceDetailService {

    /**
     * This method returns a customer invoice detail for use on the CustomerInvoiceDocumentAction. If corresponding organization
     * accounting default exists for billing chart and org, then the customer invoice detail is defaulted using the organization
     * accounting default values.
     *
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefault(Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode);

    /**
     * This method returns a customer invoice detail for current user and current fiscal year for use on the
     * CustomerInvoiceDocumentAction. If corresponding organization accounting default exists for billing chart and org, then the
     * customer invoice detail is defaulted using the organization accounting default values.
     *
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefaultForCurrentYear();

    /**
     * This method returns customer invoice document numbers for a given account number
     *
     * @param accountNumber used for the search criteria
     * @return List<String> of customer invoice document numbers
     */
    public List<String> getCustomerInvoiceDocumentNumbersByAccountNumber(String accountNumber);

    /**
     * This method returns a customer invoice detail from a customer invoice item code for a current user
     *
     * @param invoiceItemCode
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser(String invoiceItemCode);

    /**
     * This method returns a new CustomerInvoiceDetail based on the given invoice item code, chart code and org code.
     *
     * @param invoiceItemCode
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return CustomerInvoiceDetail based on the given parameters
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String chartOfAccountsCode, String organizationCode);

    /**
     * This method returns a discount customer invoice detail based on a customer invoice detail, the chart of accounts code
     *
     * @param customerInvoiceDetail
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode);

    /**
     * This method returns a discount customer invoice detail for the current year
     *
     * @param customerInvoiceDetail
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetailForCurrentYear(CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * This method returns a customer invoice detail based on invoice document number and invoice item sequence number
     *
     * @param documentNumber
     * @param sequenceNumber
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetail(String documentNumber, Integer sequenceNumber);

    /**
     * This method is used to recalculate a customer invoice detail based on updated values
     *
     * @param document
     * @param customerInvoiceDetail
     */
    public void recalculateCustomerInvoiceDetail(CustomerInvoiceDocument document, CustomerInvoiceDetail customerInvoiceDetail);

    /**
     * This method is used to update account for corresponding discount line based on parent line's account
     *
     * @param parentDiscountCustomerInvoiceDetail
     */
    public void updateAccountsForCorrespondingDiscount(CustomerInvoiceDetail parentDiscountCustomerInvoiceDetail);

    /**
     * This method is used to update the accounts receivable object code if receivable options 1 or 2 are selected.
     *
     * @param customerInvoiceDetail
     */
    public void updateAccountsReceivableObjectCode(CustomerInvoiceDetail customerInvoiceDetail);

    /**
     * This method returns the correct accounts receivable object code based on a receivable parameter.
     *
     * @param customerInvoiceDetail
     * @return
     */
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(CustomerInvoiceDetail customerInvoiceDetail);

    /**
     * This method is used to make sure the amounts are calculated correctly and the correct AR object code is in place
     *
     * @param customerInvoiceDetail
     * @param customerInvoiceDocument
     */
    public void prepareCustomerInvoiceDetailForAdd(CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * This method returns CustomerInvoiceDetails for a given CustomerInvoiceDocument.
     *
     * @param customerInvoiceDocument
     * @return Collection<CustomerInvoiceDetail> matching customer invoice details
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * This method returns CustomerInvoiceDetails for a given customer invoice document number.
     *
     * @param customerInvoiceDocumentNumber
     * @return Collection<CustomerInvoiceDetail> matching customer invoice details
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(String customerInvoiceDocumentNumber);

    /**
     * This method returns CustomerInvoiceDetails for a given customer invoice document number.
     * Cached for better performance...
     *
     * @param customerInvoiceDocumentNumber
     * @return List of customer invoice details
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoiceWithCaching(String customerInvoiceDocumentNumber);

}
