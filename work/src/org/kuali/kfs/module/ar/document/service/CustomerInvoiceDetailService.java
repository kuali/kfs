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
     * This method ...
     * 
     * @param accountNumber
     * @return
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
     * This method...
     * 
     * @param invoiceItemCode
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
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
     * Updates the specified customerInvoiceDetail as part of the process needed when the associated customerInvoiceDocument is being corrected.
     * 
     * @param customerInvoiceDetail the specified customerInvoiceDetail to be updated.
     * @param customerInvoiceDocument the associated customerInvoiceDocument being corrected.
     *
    public void prepareCustomerInvoiceDetailForErrorCorrection(CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument);
     */
        
    /**
     * @param customerInvoiceDocument
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * @param customerInvoiceDocumentNumber
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(String customerInvoiceDocumentNumber);

    /**
     * Cached for better performance...
     * 
     * @param customerInvoiceDocumentNumber
     * @return List of customer invoice details
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoiceWithCaching(String customerInvoiceDocumentNumber);

    /**
     * Gets the financial object code for the specified customerCreditMemoDetail.   
     *
     * @param customerInvoiceDetail the specified customerCreditMemoDetail
     * @return the financial object code for the specified customerCreditMemoDetail
     *
    public String getFinancialObjectCode(CustomerCreditMemoDetail customerCreditMemoDetail);
     */
    
    /**
     * Updates the financial object code for the specified customerCreditMemoDetail if it's for a prior year(s) customer invoice.
     *
     * @param customerInvoiceDetail the specified customerCreditMemoDetail
     *
    public void updateFinancialObjectCode(CustomerCreditMemoDetail customerCreditMemoDetail);
     */
}
