/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar;

/**
 * Business Object Property Constants for KFS-AR.
 */
public class ArPropertyConstants {

    // CustomerInvoiceDocument
    public static class CustomerInvoiceDocumentFields {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String PAYMENT_CHART_OF_ACCOUNTS_CODE = "paymentChartOfAccountsCode";
        public static final String PAYMENT_FINANCIAL_OBJECT_CODE = "paymentFinancialObjectCode";
        public static final String PAYMENT_FINANCIAL_SUB_OBJECT_CODE = "paymentFinancialSubObjectCode";
        public static final String PAYMENT_ACCOUNT_NUMBER = "paymentAccountNumber";
        public static final String PAYMENT_SUB_ACCOUNT_NUMBER = "paymentSubAccountNumber";
        public static final String PAYMENT_PROJECT_CODE = "paymentProjectCode";

        public static final String PAYMENT_CHART_OF_ACCOUNTS = "paymentChartOfAccounts";
        public static final String PAYMENT_FINANCIAL_OBJECT = "paymentFinancialObject";
        public static final String PAYMENT_FINANCIAL_SUB_OBJECT = "paymentFinancialSubObject";
        public static final String PAYMENT_ACCOUNT = "paymentAccount";
        public static final String PAYMENT_SUB_ACCOUNT = "paymentSubAccount";
        public static final String PAYMENT_PROJECT = "paymentProject";

        public static final String CUSTOMER_INVOICE_DETAILS = "accountingLines";
        public static final String INVOICE_ITEM_CODE = "invoiceItemCode";
        public static final String UNIT_OF_MEASURE_CODE = "invoiceItemUnitOfMeasureCode";

        public static final String CUSTOMER = "customer";
        public static final String CUSTOMER_NUMBER = "accountsReceivableDocumentHeader.customerNumber";

        public static final String INVOICE_DUE_DATE = "invoiceDueDate";
        public static final String BILLING_DATE = "billingDate";
        public static final String SOURCE_TOTAL = "sourceTotal";
        public static final String AGE = "age";
        public static final String BILLED_BY_ORGANIZATION = "billedByOrganization";
        public static final String BILLED_BY_ORGANIZATION_CODE = "billedByOrganizationCode";

        public static final String BILL_BY_CHART_OF_ACCOUNT = "billByChartOfAccount";
        public static final String BILL_BY_CHART_OF_ACCOUNT_CODE = "billByChartOfAccountCode";

        public static final String INVOICE_ITEM_UNIT_PRICE = "invoiceItemUnitPrice";
        public static final String INVOICE_ITEM_QUANTITY = "invoiceItemQuantity";
        public static final String INVOICE_ITEM_SERVICE_DATE = "invoiceItemServiceDate";
        public static final String INVOICE_ITEM_DESCRIPTION = "invoiceItemDescription";
        public static final String INVOICE_ITEM_TAXABLE_INDICATOR = "taxableIndicator";

        public static final String PROCESSING_CHART_OF_ACCOUNT_CODE = "accountsReceivableDocumentHeader.processingChartOfAccountCode";

        public static final String SHIP_TO_ADDRESS_IDENTIFIER = "customerShipToAddressIdentifier";
        public static final String BILL_TO_ADDRESS_IDENTIFIER = "customerBillToAddressIdentifier";
        public static final String OPEN_AMOUNT = "openAmount";

        public static final String INVOICE_DOCUMENT_RECURRENCE_BEGIN_DATE = "customerInvoiceRecurrenceDetails.documentRecurrenceBeginDate";
        public static final String INVOICE_DOCUMENT_RECURRENCE_END_DATE = "customerInvoiceRecurrenceDetails.documentRecurrenceEndDate";
        public static final String INVOICE_DOCUMENT_RECURRENCE_TOTAL_RECURRENCE_NUMBER = "customerInvoiceRecurrenceDetails.documentTotalRecurrenceNumber";
        public static final String INVOICE_DOCUMENT_RECURRENCE_INTERVAL_CODE = "customerInvoiceRecurrenceDetails.documentRecurrenceIntervalCode";
        public static final String INVOICE_DOCUMENT_RECURRENCE_INITIATOR = "customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName";
        public static final String INVOICE_DOCUMENT_RECURRENCE = "customerInvoiceRecurrenceDetails";
        public static final String INVOICE_DOCUMENT_RECURRENCE_ACTIVE = "customerInvoiceRecurrenceDetails.active";
    }

    // InvoiceRecurrence
    public static final class InvoiceRecurrenceFields {
        public static final String RECURRING_INVOICE_NUMBER = "invoiceNumber";
        public static final String INVOICE_RECURRENCE_BEGIN_DATE = "documentRecurrenceBeginDate";
        public static final String INVOICE_RECURRENCE_END_DATE = "documentRecurrenceEndDate";
        public static final String INVOICE_RECURRENCE_TOTAL_RECURRENCE_NUMBER = "documentTotalRecurrenceNumber";
        public static final String INVOICE_RECURRENCE_INITIATOR_USER_ID = "documentInitiatorUserIdentifier";
    }

    // OrganizationAccountingDefaults
    public static class OrganizationAccountingDefaultFields {

        public static final String LATE_CHARGE_OBJECT_CODE = "organizationLateChargeObjectCode";
        public static final String INVOICE_CHART_OF_ACCOUNTS_CODE = "defaultInvoiceChartOfAccountsCode";
        public static final String PAYMENT_CHART_OF_ACCOUNTS_CODE = "defaultPaymentChartOfAccountsCode";
        public static final String PAYMENT_ACCOUNT_NUMBER = "defaultPaymentAccountNumber";
        public static final String PAYMENT_FINANCIAL_OBJECT_CODE = "defaultPaymentFinancialObjectCode";

        public static final String WRITEOFF_FINANCIAL_OBJECT_CODE = "writeoffFinancialObjectCode";
        public static final String WRITEOFF_CHART_OF_ACCOUNTS_CODE = "writeoffChartOfAccountsCode";
        public static final String WRITEOFF_ACCOUNT_NUMBER = "writeoffAccountNumber";
    }

    // CustomerType
    public static class CustomerTypeFields {
        public static final String CUSTOMER_TYPE_DESC = "customerTypeDescription";
    }

    // Customer
    public static class CustomerFields {
        public static final String CUSTOMER_TAB_GENERAL_INFORMATION = "customerGeneralInformation";
        public static final String CUSTOMER_TAB_ADDRESSES = "customerAddresses";
        public static final String CUSTOMER_TAB_ADDRESSES_ADD_NEW_ADDRESS = "add.customerAddresses";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE = "customerAddressTypeCode";
        public static final String CUSTOMER_ADDRESS_IDENTIFIER = "customerAddressIdentifier";
        public static final String CUSTOMER_NUMBER = "customerNumber";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String CUSTOMER_ADDRESS_STATE_CODE = "customerStateCode";
        public static final String CUSTOMER_ADDRESS_ZIP_CODE = "customerZipCode";
        public static final String CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME = "customerAddressInternationalProvinceName";
        public static final String CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE = "customerInternationalMailCode";
        public static final String CUSTOMER_SOCIAL_SECURITY_NUMBER = "customerSocialSecurityNumberIdentifier";
        public static final String CUSTOMER_ADDRESS_END_DATE = "customerAddressEndDate";
    }

    // CustomerCreditMemoDocument
    public static class CustomerCreditMemoDocumentFields {
        public static final String CREDIT_MEMO_ITEM_QUANTITY = "creditMemoItemQuantity";
        public static final String CREDIT_MEMO_ITEM_TOTAL_AMOUNT = "creditMemoItemTotalAmount";
        public static final String CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER = "financialDocumentReferenceInvoiceNumber";
    }

    // CashControlDocument
    public static class CashControlDocumentFields {
        public static final String FINANCIAL_DOCUMENT_LINE_AMOUNT = "financialDocumentLineAmount";
        public static final String REFERENCE_FINANCIAL_DOC_NBR = "referenceFinancialDocumentNumber";
        public static final String APPLICATION_DOC_STATUS = "status";
        public static final String ORGANIZATION_DOC_NBR = "organizationDocumentNumber";
        public static final String CUSTOMER_PAYMENT_MEDIUM_CODE = "customerPaymentMediumCode";
        public static final String CUSTOMER_NUMBER = "customerNumber";
        public static final String BANK_CODE = "document.bankCode";
    }

    // CashControlDetail
    public static class CashControlDetailFields {
        public static final String CASH_CONTROL_DETAILS_TAB = "document.cashControlDetails";
        public static final String REFERENCE_FINANCIAL_DOC_NBR = "referenceFinancialDocumentNumber";
    }

    // CustomerInvoiceWriteoffDocument
    public static class CustomerInvoiceWriteoffDocumentFields {
        public static final String CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF = "customerInvoiceDetailsForWriteoff";
        public static final String CUSTOMER_INVOICE_WRITEOFF_EXPLANATION ="documentHeader.explanation";
    }

    // CustomerInvoiceWriteoffLookupResult
    public static class CustomerInvoiceWriteoffLookupResultFields {
        public static final String CUSTOMER_NUMBER = "customerNumber";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String CUSTOMER_TYPE_CODE = "customerTypeCode";
        public static final String CUSTOMER_INVOICE_NUMBER = "customerInvoiceNumber";
        public static final String AGE = "age";
        public static final String CUSTOMER_NOTE = "customerNote";
    }

    // CustomerAgingReport
    public static class CustomerAgingReportFields {
        public static final String REPORT_RUN_DATE = "reportRunDate";
        public static final String REPORT_OPTION= "reportOption";
        public static final String PROCESSING_OR_BILLING_CHART_CODE = "processingOrBillingChartCode";
        public static final String ACCOUNT_CHART_CODE = "accountChartCode";
    }

    // OrganizationOptions
    public static class OrganizationOptionsFields {
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ORGANIZATION_CODE = "organizationCode";
        public static final String PROCESSING_CHART_OF_ACCOUNTS_CODE = "processingChartOfAccountCode";
        public static final String PROCESSING_ORGANIZATION_CODE = "processingOrganizationCode";
        public static final String ORGANIZATION_CHECK_PAYABLE_TO_NAME = "organizationCheckPayableToName";
        public static final String ORGANIZATION_REMIT_TO_ADDRESS_SECTION_ID = "Edit Organization Options";
        public static final String ORGANIZATION_REMIT_TO_ADDRESS_NAME = "organizationRemitToAddressName";
        public static final String ORGANIZATION_REMIT_TO_LINE1_STREET_ADDRESS = "organizationRemitToLine1StreetAddress";
        public static final String ORGANIZATION_REMIT_TO_LINE2_STREET_ADDRESS = "organizationRemitToLine2StreetAddress";
        public static final String ORGANIZATION_REMIT_TO_CITY_NAME = "organizationRemitToCityName";
        public static final String ORGANIZATION_REMIT_TO_STATE_CODE = "organizationRemitToStateCode";
        public static final String ORGANIZATION_REMIT_TO_ZIP_CODE = "organizationRemitToZipCode";
        public static final String ORGANIZATION_POSTAL_ZIP_CODE = "organizationPostalZipCode";
    }

    //  SystemInformation (aka ProcessingOrg)
    public static class SystemInformationFields {
        public static final String PROCESSING_CHART_OF_ACCOUNTS_CODE = "processingChartOfAccountCode";
        public static final String PROCESSING_ORGANIZATION_CODE = "processingOrganizationCode";
        public static final String LOCKBOX_NUMBER = "lockboxNumber";
        public static final String ORGANIZATION_REMIT_TO_STATE_CODE = "organizationRemitToStateCode";
        public static final String ORGANIZATION_REMIT_TO_ZIP_CODE = "organizationRemitToZipCode";
        public static final String ACTIVE = "active";
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    }

    // CustomerInvoiceItemCodes
    public static class CustomerInvoiceItemCodes {
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ORGANIZATION_CODE = "organizationCode";
    }

    // PaymentApplicationDocument
    public static class PaymentApplicationDocumentFields {
        public static final String AMOUNT_TO_BE_APPLIED = "customerInvoiceDetail[0].amountToBeApplied";
        public static final String AMOUNT_TO_BE_APPLIED_LINE_N = "customerInvoiceDetail[{0}].amountToBeApplied";
        public static final String NON_INVOICED_LINE_AMOUNT = "nonInvoicedAddLine.financialDocumentLineAmount";
        public static final String NON_INVOICED_LINE_CHART = "nonInvoicedAddLine.chartOfAccountCode";
        public static final String NON_INVOICED_LINE_ACCOUNT = "nonInvoicedAddLine.accountNumber";
        public static final String NON_INVOICED_LINE_SUBACCOUNT = "nonInvoicedAddLine.subAccountNumber";
        public static final String NON_INVOICED_LINE_OBJECT = "nonInvoicedAddLine.financialObjectCode";
        public static final String NON_INVOICED_LINE_SUBOBJECT = "nonInvoicedAddLine.financialSubObjectCode";
        public static final String NON_INVOICED_LINE_PROJECT = "nonInvoicedAddLine.projectCode";
        public static final String UNAPPLIED_CUSTOMER_NUMBER = "document.nonAppliedHolding.customerNumber";
        public static final String UNAPPLIED_AMOUNT = "document.nonAppliedHolding.financialDocumentLineAmount";
        public static final String DELETE_NON_INVOICED_LINE_PREFIX = "methodToCall.deleteNonArLine.line";
        public static final String ENTERED_INVOICE_CUSTOMER_NUMBER = "selectedCustomerNumber";
        public static final String ENTERED_INVOICE_NUMBER = "enteredInvoiceDocumentNumber";
    }

}
