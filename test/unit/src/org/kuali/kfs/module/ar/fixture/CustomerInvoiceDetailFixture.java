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
package org.kuali.kfs.module.ar.fixture;

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for customer invoice details
 */
public enum CustomerInvoiceDetailFixture {

    BASE_CUSTOMER_INVOICE_DETAIL(null, // documentNumber
            "BL", // chartOfAccountsCode
            "1031400", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            null, // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            new BigDecimal(10), // invoiceItemUnitPrice
            new KualiDecimal(10), // amount
            new KualiDecimal(0) // invoiceItemTaxAmount
            , null),

    CUSTOMER_INVOICE_DETAIL_AMOUNT_EQUALS_ZERO(null, // documentNumber
            "BL", // chartOfAccountsCode
            "1031400", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            null, // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(0), // invoiceItemQuantity
            new BigDecimal(0), // invoiceItemUnitPrice
            new KualiDecimal(0), // amount
            new KualiDecimal(0) // invoiceItemTaxAmount
            , null),

    CUSTOMER_INVOICE_DETAIL_WITH_NEGATIVE_AMOUNT(null, // documentNumber
            "BL", // chartOfAccountsCode
            "1031400", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            null, // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            new BigDecimal(-1), // invoiceItemUnitPrice
            new KualiDecimal(-1), // amount
            new KualiDecimal(0) // invoiceItemTaxAmount
            , null),

    CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_NEGATIVE_AMOUNT(null, // documentNumber
            "BL", // chartOfAccountsCode
            "1031400", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            null, // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            new BigDecimal(-1), // invoiceItemUnitPrice
            new KualiDecimal(-1), // amount
            new KualiDecimal(0) // invoiceItemTaxAmount
            , new CustomerInvoiceDetail()),

    CUSTOMER_INVOICE_DETAIL_DISCOUNT_WITH_POSITIVE_AMOUNT(null, // documentNumber
            "BL", // chartOfAccountsCode
            "1031400", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            null, // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            new BigDecimal(1), // invoiceItemUnitPrice
            new KualiDecimal(1), // amount
            new KualiDecimal(0) // invoiceItemTaxAmount
            , new CustomerInvoiceDetail()),

    CUSTOMER_INVOICE_DETAIL_SUBFUND_RECEIVABLE(null, // documentNumber
            "BL", // chartOfAccountsCode
            "1031400", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            "8110", // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            new BigDecimal(1), // invoiceItemUnitPrice
            new KualiDecimal(1), // amount
            new KualiDecimal(0) // invoiceItemTaxAmount
            , null),

    CUSTOMER_INVOICE_DETAIL_SUBFUND_RECEIVABLE_2(null, // documentNumber
            "BL", // chartOfAccountsCode
            "2336320", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            "8118", // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            new BigDecimal(1), // invoiceItemUnitPrice
            new KualiDecimal(1), // amount
            new KualiDecimal(0) // invoiceItemTaxAmount
            , null),

    CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE(null, // documentNumber
            "BL", // chartOfAccountsCode
            "1031400", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            "8118", // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            new BigDecimal(1), // invoiceItemUnitPrice
            new KualiDecimal(1), // amount
            new KualiDecimal(0), // invoiceItemTaxAmount
            null),
    CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE_2(null, // documentNumber
            "BL", // chartOfAccountsCode
            "2336320", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            "8118", // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            new BigDecimal(1), // invoiceItemUnitPrice
            new KualiDecimal(1), // amount
            new KualiDecimal(0), // invoiceItemTaxAmount
            null),
    ZERO_DOLLAR_INVOICE_DETAIL(KualiDecimal.ZERO),
    ONE_DOLLAR_INVOICE_DETAIL(new KualiDecimal(1)),
    TEN_DOLLAR_INVOICE_DETAIL(new KualiDecimal(10)),
    FIFTY_DOLLAR_INVOICE_DETAIL(new KualiDecimal(50)),
    ONE_HUNDRED_DOLLAR_INVOICE_DETAIL(new KualiDecimal(100)),
    ONE_THOUSAND_DOLLAR_INVOICE_DETAIL(null, // documentNumber
            "BL", // chartOfAccountsCode
            "1031400", // accountNumber
            null, // subAccountNumber
            "5000", // financialObjectCode
            null, // financialSubObjectCode
            null, // organizationRefId
            null, // projectCode
            null, // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            new BigDecimal("1000.00"), // invoiceItemUnitPrice
            new KualiDecimal("1000.00"), // amount
            new KualiDecimal(0) // invoiceItemTaxAmount
            , null),
    FIVE_HUNDRED_DOLLAR_INVOICE_DETAIL(null, // documentNumber
                    "BL", // chartOfAccountsCode
                    "1031400", // accountNumber
                    null, // subAccountNumber
                    "5000", // financialObjectCode
                    null, // financialSubObjectCode
                    null, // organizationRefId
                    null, // projectCode
                    null, // accountsReceivableObjectCode
                    Date.valueOf("2008-01-01"), // invoiceItemServiceDate
                    new BigDecimal(1), // invoiceItemQuantity
                    new BigDecimal("500.00"), // invoiceItemUnitPrice
                    new KualiDecimal("500.00"), // amount
                    new KualiDecimal(0) // invoiceItemTaxAmount
                    , null),

    ;

    public String documentNumber;
    public String chartOfAccountsCode;
    public String accountNumber;
    public String subAccountNumber;
    public String financialObjectCode;
    public String financialSubObjectCode;
    public String projectCode;
    public String organizationReferenceId;
    public String accountsReceivableObjectCode;
    public Date invoiceItemServiceDate;
    public BigDecimal invoiceItemQuantity;
    public BigDecimal invoiceItemUnitPrice;
    public KualiDecimal amount;
    public KualiDecimal invoiceItemTaxAmount;
    public CustomerInvoiceDetail parentCustomerInvoiceDetail;

    /**
     * Create a customer invoice detail where all you really care about is the amount.
     *
     * @param amount
     */
    CustomerInvoiceDetailFixture(KualiDecimal amount) {
        this(null, // documentNumber
            "BL", // chartOfAccountsCode
            "1031400", // accountNumber
            "ADV", // subAccountNumber
            "5000", // financialObjectCode
            "SAC", // financialSubObjectCode
            "CID", // organizationRefId
            "BOB", // projectCode
            null, // accountsReceivableObjectCode
            Date.valueOf("2008-01-01"), // invoiceItemServiceDate
            new BigDecimal(1), // invoiceItemQuantity
            amount.bigDecimalValue(), // invoiceItemUnitPrice
            amount, // amount
            new KualiDecimal(0), // invoiceItemTaxAmount
            null);
    }

    /**
     * Create a customer invoice detail with specific values for all fields.
     *
     * @param documentNumber
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param financialObjectCode
     * @param financialSubObjectCode
     * @param organizationReferenceId
     * @param projectCode
     * @param accountsReceivableObjectCode
     * @param invoiceItemServiceDate
     * @param invoiceItemQuantity
     * @param invoiceItemUnitPrice
     * @param amount
     * @param invoiceItemTaxAmount
     * @param parentCustomerInvoiceDetail
     */
    CustomerInvoiceDetailFixture(String documentNumber, String chartOfAccountsCode, String accountNumber, String subAccountNumber, String financialObjectCode, String financialSubObjectCode, String organizationReferenceId, String projectCode, String accountsReceivableObjectCode, Date invoiceItemServiceDate, BigDecimal invoiceItemQuantity, BigDecimal invoiceItemUnitPrice, KualiDecimal amount, KualiDecimal invoiceItemTaxAmount, CustomerInvoiceDetail parentCustomerInvoiceDetail) {
        this.documentNumber = documentNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.subAccountNumber = subAccountNumber;
        this.financialObjectCode = financialObjectCode;
        this.financialSubObjectCode = financialSubObjectCode;
        this.projectCode = projectCode;
        this.organizationReferenceId = organizationReferenceId;
        this.accountsReceivableObjectCode = accountsReceivableObjectCode;
        this.invoiceItemServiceDate = invoiceItemServiceDate;
        this.invoiceItemQuantity = invoiceItemQuantity;
        this.invoiceItemUnitPrice = invoiceItemUnitPrice;
        this.amount = amount;
        this.invoiceItemTaxAmount = invoiceItemTaxAmount;
        this.parentCustomerInvoiceDetail = parentCustomerInvoiceDetail;
    }

    /**
     * This method creates a customer invoice detail based on the information for this enum
     *
     * @return
     */
    public CustomerInvoiceDetail createCustomerInvoiceDetail() {
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setDocumentNumber(documentNumber);
        customerInvoiceDetail.setChartOfAccountsCode(chartOfAccountsCode);
        customerInvoiceDetail.setAccountNumber(accountNumber);
        customerInvoiceDetail.setSubAccountNumber(subAccountNumber);
        customerInvoiceDetail.setFinancialObjectCode(financialObjectCode);
        customerInvoiceDetail.setFinancialSubObjectCode(financialSubObjectCode);
        customerInvoiceDetail.setProjectCode(projectCode);
        customerInvoiceDetail.setOrganizationReferenceId(organizationReferenceId);
        customerInvoiceDetail.setAccountsReceivableObjectCode(accountsReceivableObjectCode);
        customerInvoiceDetail.setInvoiceItemServiceDate(invoiceItemServiceDate);
        customerInvoiceDetail.setInvoiceItemUnitPrice(invoiceItemUnitPrice);
        customerInvoiceDetail.setInvoiceItemQuantity(invoiceItemQuantity);
        customerInvoiceDetail.setAmount(amount);
        customerInvoiceDetail.setInvoiceItemTaxAmount(invoiceItemTaxAmount);
        customerInvoiceDetail.setParentDiscountCustomerInvoiceDetail(parentCustomerInvoiceDetail);

        return customerInvoiceDetail;
    }

    /**
     * Add this customer invoice detail to the passed in document
     *
     * @param customerInvoiceDocument
     */
    public CustomerInvoiceDetail addTo(CustomerInvoiceDocument customerInvoiceDocument) {
        CustomerInvoiceDetail customerInvoiceDetail = this.createCustomerInvoiceDetail();
        customerInvoiceDetail.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        customerInvoiceDetail.setAccountNumber(accountNumber);
        customerInvoiceDetail.setAmount(amount);
        customerInvoiceDetail.setChartOfAccountsCode(chartOfAccountsCode);
        customerInvoiceDetail.setFinancialObjectCode(financialObjectCode);
        customerInvoiceDetail.setFinancialSubObjectCode(financialSubObjectCode);
        customerInvoiceDetail.setInvoiceItemQuantity(invoiceItemQuantity);
        customerInvoiceDetail.setInvoiceItemServiceDate(invoiceItemServiceDate);
        customerInvoiceDetail.setInvoiceItemTaxAmount(invoiceItemTaxAmount);
        customerInvoiceDetail.setInvoiceItemUnitPrice(invoiceItemUnitPrice);
        customerInvoiceDetail.setOrganizationReferenceId(organizationReferenceId);
        customerInvoiceDetail.setProjectCode(projectCode);
        customerInvoiceDetail.setSubAccountNumber(subAccountNumber);
        customerInvoiceDocument.addSourceAccountingLine(customerInvoiceDetail);
        return customerInvoiceDetail;
    }

}
