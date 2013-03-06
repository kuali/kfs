/*
 * Copyright 2007 The Kuali Foundation
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


public class ArConstants {

    public static final String AR_NAMESPACE_CODE = "KFS-AR";

    public static final String INVOICE_DOC_TYPE = "Invoice";
    public static final String CREDIT_MEMO_DOC_TYPE = "Credit Memo";
    public static final String PAYMENT_DOC_TYPE = "Payment";
    public static final String WRITEOFF_DOC_TYPE = "Writeoff";

    //System Parameters
    public static final String INSTITUTION_NAME = "INSTITUTION_NAME";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD = "GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD";
    public static final String GLPE_WRITEOFF_GENERATION_METHOD = "GLPE_WRITEOFF_GENERATION_METHOD";
    public static final String ALLOW_SALES_TAX_LIABILITY_ADJUSTMENT_IND = "ALLOW_SALES_TAX_LIABILITY_ADJUSTMENT_IND";
    public static final String MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE = "MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE";
    public static final String REMIT_TO_ADDRESS_EDITABLE_IND = "REMIT_TO_ADDRESS_EDITABLE_IND";
    public static final String REMIT_TO_NAME_EDITABLE_IND = "REMIT_TO_NAME_EDITABLE_IND";
    public static final String GLPE_RECEIVABLE_OFFSET_OBJECT_CODE_BY_SUB_FUND = "GLPE_RECEIVABLE_OFFSET_OBJECT_CODE_BY_SUB_FUND";
    public static final String INVOICE_RECURRENCE_INTERVALS = "INVOICE_RECURRENCE_INTERVALS";
    public static final String MAXIMUM_RECURRENCES_BY_INTERVAL = "MAXIMUM_RECURRENCES_BY_INTERVAL";
    public static final String ENABLE_SALES_TAX_IND = "ENABLE_SALES_TAX_IND";
    public static final String CUSTOMER_INVOICE_AGE = "CUSTOMER_INVOICE_AGE";
    public static final String WRITEOFF_APPROVAL_THRESHOLD = "APPROVAL_THRESHOLD";
    public static final String DEFAULT_FORMAT = "DEFAULT_FORMAT";
    public static final String DUE_DATE_DAYS = "DUE_DATE_DAYS";
    public static final String INCLUDE_ZERO_BALANCE_CUSTOMERS = "INCLUDE_ZERO_BALANCE_CUSTOMERS";

    //Valid number of days the invoice due date can be more than invoice creation date.
    public static final int VALID_NUMBER_OF_DAYS_INVOICE_DUE_DATE_PAST_INVOICE_DATE = 90;

    public static final String NEW_CUSTOMER_INVOICE_DETAIL_ERROR_PATH_PREFIX = "newCustomerInvoiceDetail";
    public static final String NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX = "newCashControlDetail";

    public static final String CUSTOMER_INVOICE_DOCUMENT_GL_POSTING_HELPER_BEAN_ID = "customerInvoiceDocumentGeneralLedgerPostingHelper";

    public static final String CUSTOMER_INVOICE_DETAIL_UOM_DEFAULT = "EA"; //TODO: System parameter?

    public static final String LOOKUP_CUSTOMER_NAME = "customerName";
    public static final String LOOKUP_CUSTOMER_NUMBER = "customerNumber";
    public static final String LOOKUP_INVOICE_NUMBER = "invoiceNumber";

    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_CHART = "1";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_SUBFUND = "2";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU = "3";
    public static final String GLPE_WRITEOFF_GENERATION_METHOD_CHART = "1";
    public static final String GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT = "2";
    public static final String ALLOW_SALES_TAX_LIABILITY_ADJUSTMENT_IND_NO = "N";
    public static final String COPY_CUSTOMER_INVOICE_DOCUMENT_WITH_DISCOUNTS_QUESTION = "ConfirmationForCopyingInvoiceWithDiscounts";

    public static final String CUSTOMER_INVOICE_DETAIL_DEFAULT_DISCOUNT_DESCRIPTION_PREFIX = "LINE ITEM DISCOUNT";

    public static class PaymentMediumCode {
        public static final String CASH = "CA";
        public static final String CHECK = "CK";
        public static final String WIRE_TRANSFER = "WT";
        public static final String CREDIT_CARD = "CR";
    }

    public static class CustomerAgingReportFields {
        // Report Options
        public static final String PROCESSING_ORG = "Processing Organization";
        public static final String BILLING_ORG = "Billing Organization";
        public static final String ACCT = "Account";

        public static final String TOTAL_0_TO_30 = "total0to30";
        public static final String TOTAL_31_TO_60 = "total31to60";
        public static final String TOTAL_61_TO_90 = "total61to90";
        public static final String TOTAL_91_TO_SYSPR = "total90toSYSPR";
        public static final String TOTAL_SYSPR_PLUS_1_OR_MORE = "totalSYSPRplus1orMore";
        public static final String TOTAL_AMOUNT_DUE = "totalAmountDue";
    }

    public static class OrganizationAccountingOptionsConstants {
        public static final String SHOW_EDIT_PAYMENTS_DEFAULTS_TAB = GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU;
        public static final String ORG_ACCT_DEFAULT_RECEIVABLE_TAB_NAME = "Edit Organization Receivable Account Defaults";
        public static final String ORG_ACCT_DEFAULT_WRITEOFF_TAB_NAME = "Edit Organization Writeoff Account Defaults";
    }

    public static class CustomerCreditMemoStatuses {
        public static final String INITIATE = "INIT";
        public static final String IN_PROCESS = "INPR";
    }

    public static class CustomerInvoiceWriteoffStatuses {
        public static final String INITIATE = "INIT";
        public static final String IN_PROCESS = "INPR";
    }

    public static class CustomerCreditMemoConstants {
        public static final String CUSTOMER_CREDIT_MEMO_ITEM_QUANTITY = "qty";
        public static final String CUSTOMER_CREDIT_MEMO_ITEM_TOTAL_AMOUNT = "itemAmount";
        public static final String BOTH_QUANTITY_AND_ITEM_TOTAL_AMOUNT_ENTERED = "both";
        public static final String GENERATE_CUSTOMER_CREDIT_MEMO_DOCUMENT_QUESTION_ID = "GenerateCustomerCreditMemoDocumentQuestionID";
    }

    public static final class CustomerLoad {
        public static final String CUSTOMER_LOAD_FILE_TYPE_IDENTIFIER = "customerLoadInputFileType";
        public static final String CUSTOMER_CSV_LOAD_FILE_TYPE_IDENTIFIER = "customerLoadCSVInputFileType";
        public static final String CUSTOMER_LOAD_REPORT_SUBFOLDER = "ar";
        public static final String BATCH_REPORT_BASENAME = "ar_customer_load";
    }

    public static final class CustomerInvoiceWriteoff {
        public static final String CUSTOMER_INVOICE_WRITEOFF_FILE_TYPE_IDENTIFIER = "customerInvoiceWriteoffInputFileType";
        public static final String CUSTOMER_INVOICE_WRITEOFF_REPORT_SUBFOLDER = "ar";
        public static final String BATCH_REPORT_BASENAME = "customer_invoice_writeoff";
    }

    public static final class Lockbox {
        public static final String LOCKBOX_REPORT_SUBFOLDER = "ar";
        public static final String BATCH_REPORT_BASENAME = "lockbox_batch";
        public static final String SUMMARY_AND_ERROR_NOTIFICATION_EMAIL_SUBJECT = "SUMMARY_AND_ERROR_NOTIFICATION_EMAIL_SUBJECT";
        public static final String SUMMARY_AND_ERROR_NOTIFICATION_TO_EMAIL_ADDRESSES = "SUMMARY_AND_ERROR_NOTIFICATION_TO_EMAIL_ADDRESSES";
        public static final String CONTACTS_TEXT = "CONTACTS_TEXT";
    }

    public static final String ORGANIZATION_RECEIVABLE_ACCOUNT_DEFAULTS = "Organization Receivable Account Defaults";
    public static final String DISCOUNT_PREFIX = "DISCOUNT - ";
    public static final String GLPE_WRITEOFF_OBJECT_CODE_BY_CHART = "GLPE_WRITEOFF_OBJECT_CODE_BY_CHART";
    public static final String NO_COLLECTION_STATUS_STRING = "No Collection Status";
    public static final Object CUSTOMER_INVOICE_WRITEOFF_SUMMARY_ACTION = "viewSummary";
    public static final String CUSTOMER_INVOICE_WRITEOFF_DOCUMENT_DESCRIPTION = "Writeoff for ";

    public static class PrintInvoiceOptions {
        public static final String PRINT_BY_PROCESSING_ORG = "Q";
        public static final String PRINT_BY_USER = "U";
        public static final String PRINT_BY_BILLING_ORG = "B";
        public static final String PRINT_INVOICE_NOW = "Y";
        public static final String PRINT_DO_NOT_PRINT = "N";
    }

    public static final String LOCKBOX_DOCUMENT_DESCRIPTION = "Created by Lockbox ";
    public static final String LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER = "Lockbox: Remittance for INVALID invoice number ";
    public static final String LOCKBOX_REMITTANCE_FOR_CLOSED_INVOICE_NUMBER = "Lockbox: Remittance for CLOSED invoice number ";
    public static final String LOCKBOX_REMITTANCE_FOR_INVOICE_NUMBER = "Lockbox: Remittance for invoice number ";

    public static final String PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE = "APP";
    public static final String INVOICE_WRITEOFF_DOCUMENT_TYPE_CODE = "INVW";

    // Organization Options Section Ids
    public static class OrganizationOptionsSections {
        public static final String  EDIT_ORGANIZATION_REMIT_TO_ADDRESS = "Edit Organization Remit To Address";
    }

    // Customer Billing Statement
    public static final String STATEMENT_FORMAT_SUMMARY = "Summary";
    public static final String STATEMENT_FORMAT_DETAIL = "Detail";
    public static final String INCLUDE_ZERO_BALANCE_YES = "Yes";
    public static final String INCLUDE_ZERO_BALANCE_NO = "No";

}
