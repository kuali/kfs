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

import java.util.ArrayList;
import java.util.List;


public class ArConstants{


    public static final String AR_NAMESPACE_CODE = "KFS-AR";
    public static final String CG_NAMESPACE_CODE = "KFS-CG";
    public static final String ENABLE_CG_PRORATE_BILL_IND = "ENABLE_CG_PRORATE_BILL_IND";

    public static final String INVOICE_DOC_TYPE = "Invoice";
    public static final String CREDIT_MEMO_DOC_TYPE = "Credit Memo";
    public static final String PAYMENT_DOC_TYPE = "Payment";
    public static final String WRITEOFF_DOC_TYPE = "Writeoff";

    // System Parameters
    public static final String INSTITUTION_NAME = "INSTITUTION_NAME";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD = "GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD";
    public static final String GLPE_WRITEOFF_GENERATION_METHOD = "GLPE_WRITEOFF_GENERATION_METHOD";
    public static final String ALLOW_SALES_TAX_LIABILITY_ADJUSTMENT_IND = "ALLOW_SALES_TAX_LIABILITY_ADJUSTMENT_IND";
    public static final String MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE = "MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE";
    public static final String OBJECT_CONSOLIDATIONS = "OBJECT_CONSOLIDATIONS";
    public static final String OBJECT_LEVELS = "OBJECT_LEVELS";
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

    public static final String DEFAULT_PREFERRED_BILLING_FREQUENCY_PARAMETER = "DEFAULT_PREFERRED_BILLING_FREQUENCY";
    public static final String CG_INVOICE_FROM_EMAIL_ADDRESS = "CG_INVOICE_FROM_EMAIL_ADDRESS";
    public static final String CG_INVOICE_EMAIL_SUBJECT = "CG_INVOICE_EMAIL_SUBJECT";
    public static final String BASIS_OF_ACCOUNTING = "DEFAULT_BASIS_OF_ACCOUNTING_FOR_BILLING";
    public static final String BASIS_OF_ACCOUNTING_CASH = "1";
    public static final String BASIS_OF_ACCOUNTING_ACCRUAL = "2";
    public static final String CG_INVOICE_EMAIL_BODY = "CG_INVOICE_EMAIL_BODY";
    public static final String LETTER_TEMPLATE_UPLOAD = "document.letterTemplateUpload";

    // Valid number of days the invoice due date can be more than invoice creation date.
    public static final int VALID_NUMBER_OF_DAYS_INVOICE_DUE_DATE_PAST_INVOICE_DATE = 90;

    public static final String NEW_CUSTOMER_INVOICE_DETAIL_ERROR_PATH_PREFIX = "newCustomerInvoiceDetail";
    public static final String NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX = "newCashControlDetail";

    public static final String CUSTOMER_INVOICE_DOCUMENT_GL_POSTING_HELPER_BEAN_ID = "customerInvoiceDocumentGeneralLedgerPostingHelper";

    public static final String CUSTOMER_INVOICE_DETAIL_UOM_DEFAULT = "EA"; // TODO: System parameter?

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

    public static final String INV_DOCUMENT_TYPE = "INV";
    public static final String INV_DOCUMENT_DESCRIPTION = "Customer Invoice Document";
    public static final String DEFAULT_PROCESSING_CHART = "DEFAULT_PROCESSING_CHART";
    public static final String DEFAULT_PROCESSING_ORG = "DEFAULT_PROCESSING_ORG";

    public static final String NEW_COLLECTION_ACTIVITY_EVENT_ERROR_PATH_PREFIX = "newEvent";

    // Category Codes for Total fields.

    public static final String TOTAL_DIRECT_COST = "Total Direct Cost";
    public static final String TOTAL_DIRECT_COST_CD = "TDC";
    public static final String TOTAL_IN_DIRECT_COST = "Total Indirect Cost";
    public static final String TOTAL_IN_DIRECT_COST_CD = "TIDC";
    public static final String TOTAL_COST = "Total Cost";
    public static final String TOTAL_COST_CD = "TC";

    public static final String DOCUMENT_STATUS_FINAL = "F";
    // Agency Address
    public static final String AGENCY_DETAIL_TYPE_CODE = "Agency";
    public static final String AGENCY_PRIMARY_ADDRESSES_TYPE_CODE = "P";
    public static final String AGENCY_ALTERNATE_ADDRESSES_TYPE_CODE = "A";
    public static final String DEFAULT_NUMBER_OF_COPIES_TO_PRINT = "DEFAULT_NUMBER_OF_COPIES_TO_PRINT";

    /* Start TEM REFUND Merge */
    public static class ArRefunding {
        public static final String DV_ROUTE_PARAMETER_NAME = "DV_DOCUMENT_ROUTE_ACTION";
        public static final String DV_ROUTE_SAVE = "SAVE";
        public static final String DV_ROUTE_ROUTE = "ROUTE";
        public static final String DV_ROUTE_BLANKETAPPROVE = "BLANKETAPPROVE";
    }

    public static class SuspensionCategories {
        public static final String BILL_DATE_EXCEEDS_THE_AWARD_STOP_DATE = "1";
        public static final String NEW_TOTAL_BILLED_AMOUNT_EXCEEDS_AWARD_TOTAL = "2";
        public static final String INVOICE_AMOUNT_IS_LESS_THAN_INVOICE_MINIMUM_REQUIREMENT = "3";
        public static final String REPORTS_ARE_REQUIRED_TO_BE_ATTACHED = "4";

        public static final String AGENCY_PRIMARY_ADDRESS_NOT_COMPLETE = "5";
        public static final String AGENCY_ALTERNATE_ADDRESS_NOT_COMPLETE = "6";
        public static final String INVOICE_NOT_FINAL_AND_EXPIRATION_DATE_REACHED = "7";
        public static final String OBJECT_CODE_FOR_AN_ACCOUNT_MAY_BE_MISSING_FROM_CGB_CATEGORY_CODE = "8";
        public static final String LOC_REMAINING_AMOUNT_IS_NOT_SUFFICIENT = "9";

        public static final String AWARD_HAS_ACTIVE_BUT_EXPIRED_ACCOUNT = "10";
        public static final String AWARD_SUSPENDED_BY_USER = "11";
        public static final String INVOICE_TYPE_IS_MISSING = "12";
        public static final String AWARD_HAS_CLOSED_ACCOUNT_WITH_CURRENT_EXPENDITURES = "13";
        public static final String AWARD_HAS_STOP_WORK_MARKED = "14";
    }

    public static class TemplateUploadSystem {
        static final public String EXTENSION = ".pdf";
        static final public String TEMPLATE_MIME_TYPE = "application/pdf";
    }

    public static class PaymentMediumCode {
        public static final String CASH = "CA";
        public static final String CHECK = "CK";
        public static final String WIRE_TRANSFER = "WT";
        public static final String LOC_WIRE = "LW";
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


        public static final String AGENCY_SHORT_NAME = "Agency Short Name";
        public static final String UNAPPLIED_PAYMENTS = "Unapplied Payments";
        public static final String TOTAL_WRITEOFF = "Total Write-Off";
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

    // Probably refactor these two constants out to pull them from a service, system parameter or something.
    public static final String ACTUALS_BALANCE_TYPE_CODE = "AC";
    public static final String PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE = "APP";
    public static final String INVOICE_WRITEOFF_DOCUMENT_TYPE_CODE = "INVW";

    // Organization Options Section Ids
    public static class OrganizationOptionsSections {
        public static final String EDIT_ORGANIZATION_REMIT_TO_ADDRESS = "Edit Organization Remit To Address";
    }

    // Customer Billing Statement
    public static final String STATEMENT_FORMAT_SUMMARY = "Summary";
    public static final String STATEMENT_FORMAT_DETAIL = "Detail";
    public static final String INCLUDE_ZERO_BALANCE_YES = "Yes";
    public static final String INCLUDE_ZERO_BALANCE_NO = "No";

    public enum ArNotificationSelectionField{
        CHART("CHART"), ORGANIZATION("ORG"), ACCOUNT("ACCOUNT");

        public String fieldName;

        private ArNotificationSelectionField(String fieldName){
            this.fieldName = fieldName;
        }
    }

    public enum ArNotificationOptions{
        PROCESSING_ORG("PROCESSING_ORG"), BILLING_ORG("BILLING_ORG"), ACCOUNT("ACCOUNT");

        public String option;

        private ArNotificationOptions(String option){
            this.option = option;
        }
    }

    public static class BatchFileSystem {


        static final public String EXTENSION = ".log";

        static final public String CGINVOICE_VALIDATION_ERROR_OUTPUT_FILE = "cgin_batch_validation_err";
        static final public String CGINVOICE_CREATION_ERROR_OUTPUT_FILE = "cgin_batch_create_doc_err";
        static final public String ONDEMAND_VALIDATION_ERROR_OUTPUT_FILE = "cgin_onDemand_validation_err";
        static final public String ONDEMAND_CREATION_ERROR_OUTPUT_FILE = "cgin_onDemand_create_doc_err";
        static final public String LOC_REVIEW_VALIDATION_ERROR_OUTPUT_FILE = "cgin_locReview_validation_err";
        static final public String LOC_REVIEW_CREATION_ERROR_OUTPUT_FILE = "cgin_locReview_create_doc_err";

        static final public String LOC_CREATION_BY_AWARD_ERROR_OUTPUT_FILE = "cgin_loc_by_award_create_doc_err";
        static final public String LOC_CREATION_BY_LOCF_ERROR_OUTPUT_FILE = "cgin_loc_by_loc_fund_create_doc_err";
        static final public String INVOICE_REPORT_EMAIL_DELIVERY_ERROR_OUTPUT_FILE = "cgin_report_email_delivery_err";
        static final public String LOC_CREATION_BY_LOCFG_ERROR_OUTPUT_FILE = "cgin_loc_by_loc_fund_group_create_doc_err";
        static final public String LOC_CREATION_PMT_APP_ERROR_OUTPUT_FILE = "cgin_loc_pmt_app_create_doc_err";
        static final public String EVT_CREATION_CLN_ACT_ERROR_OUTPUT_FILE = "catd_evt_cln_act_create_doc_err";
        static final public String REFRL_TO_CLCTNS_ERROR_OUTPUT_FILE = "refrl_to_clctns_doc_err";

        static final public String CGINVOICE_DOCUMENT_DESCRIPTION_OF_BATCH_PROCESS = "Auto-generated Invoice Document";

        static final public String CGINVOICE_CREATION_AWARD_START_DATE_MISSING_ERROR = "error.cginvoice.award.startDate.missing";
        static final public String CGINVOICE_CREATION_USER_SUSPENDED_ERROR = "error.cginvoice.user.suspended";
        static final public String CGINVOICE_CREATION_AWARD_INACTIVE_ERROR = "error.cginvoice.award.inactive";
        static final public String CGINVOICE_CREATION_AWARD_CLOSED_ERROR = "error.cginvoice.award.closed";
        static final public String CGINVOICE_CREATION_AWARD_PAST_STOP_DATE_ERROR = "error.cginvoice.award.past.stop.date";
        static final public String CGINVOICE_CREATION_INVOICING_OPTION_MISSING_ERROR = "error.cginvoice.award.missing";
        static final public String CGINVOICE_CREATION_BILLING_FREQUENCY_MISSING_ERROR = "error.cginvoice.billing.missing.frequency";
        static final public String CGINVOICE_CREATION_NO_ACCOUNT_ASSIGNED_ERROR = "error.cginvoice.no.account.assigned";
        static final public String CGINVOICE_CREATION_AWARD_FINAL_BILLED_ERROR = "error.cginvoice.already.billed";
        static final public String CGINVOICE_CREATION_CONAINS_EXPIRED_ACCOUNTS_ERROR = "error.cginvoice.award.contain.expired.account";
        static final public String CGINVOICE_CREATION_AWARD_TOO_STALE_ERROR = "error.cginvice.award.too.stale";
        static final public String CGINVOICE_CREATION_SINGLE_ACCOUNT_ERROR = "error.cginvoice.award.single.account";
        static final public String LOC_CREATION_ERROR_INVOICE_NOT_FINAL = "error.loc.not.final";
        static final public String LOC_CREATION_ERROR__CSH_CTRL_IN_PROGRESS = "error.loc.csh.ctrl.in.progress";
        static final public String CGINVOICE_CREATION_AWARD_INVALID_BILLING_PERIOD = "error.cginvoice.award.not.eligible.invoice";
        static final public String CGINVOICE_CREATION_AWARD_NO_VALID_MILESTONES = "Award has no valid Milestones to invoice.";
        static final public String CGINVOICE_CREATION_AWARD_NO_VALID_BILLS = "error.cginvoice.award.not.valid.bills";
        static final public String CGINVOICE_CREATION_AWARD_NO_VALID_ACCOUNTS = "error.cgivoice.award.not.valid.accounts";
        static final public String CGINVOICE_CREATION_AWARD_AGENCY_NO_CUSTOMER_RECORD = "error.cginvoice.award.not.valid.customer";
        static final public String CGINVOICE_CREATION_SYS_INFO_OADF_NOT_SETUP = "error.cginvoice.sys.info.not.setup";
        static final public String CGINVOICE_CREATION_AWARD_NO_AR_INV_ACCOUNT = "error.cginvoice.award.not.ar.account";
        static final public String CGINVOICE_CREATION_AWARD_INVOICES_IN_PROGRESS = "error.cginvoice.award.invoice.progress";
        static final public String CGINVOICE_CREATION_AWARD_OFFSET_DEF_NOT_SETUP = "error.cginvoice.award.offset.not.setup";
    }

    public static class ReportsConstants {
        public static final List<String> awardBalancesReportSubtotalFieldsList = new ArrayList<String>();

        static {
            awardBalancesReportSubtotalFieldsList.add("agency.fullName");
            awardBalancesReportSubtotalFieldsList.add("awardStatusCode");
            awardBalancesReportSubtotalFieldsList.add("awardPrimaryProjectDirector.projectDirector.name");
            awardBalancesReportSubtotalFieldsList.add("awardPrimaryFundManager.fundManager.name");
        }

        public static final List<String> reportSearchCriteriaExceptionList = new ArrayList<String>();

        static {
            reportSearchCriteriaExceptionList.add("backLocation");
            reportSearchCriteriaExceptionList.add("docFormKey");
            reportSearchCriteriaExceptionList.add("dummyBusinessObject.invoiceReportOption");
        }

        public static final List<String> cgInvoiceReportSubtotalFieldsList = new ArrayList<String>();

        static {
            cgInvoiceReportSubtotalFieldsList.add("proposalNumber");
        }

        public static final List<String> cgPaymentHistoryReportSubtotalFieldsList = new ArrayList<String>();

        static {
            cgPaymentHistoryReportSubtotalFieldsList.add("awardNumber");
            cgPaymentHistoryReportSubtotalFieldsList.add("customerName");
            cgPaymentHistoryReportSubtotalFieldsList.add("paymentNumber");
            cgPaymentHistoryReportSubtotalFieldsList.add("paymentDate");
        }

        public static final List<String> cgLOCDrawDetailsReportSubtotalFieldsList = new ArrayList<String>();

        public static final List<String> cgLOCAmountsNotDrawnReportSubtotalFieldsList = new ArrayList<String>();

        public static final List<String> cgSuspendedInvoiceReportSubtotalFieldsList = new ArrayList<String>();

        public static final String INVOICE_INDICATOR_OPEN = "Open";
        public static final String INVOICE_INDICATOR_CLOSE = "Close";

    }

    public static class InvoiceIndicator {
        static final public String MAIL = "MAIL";
        static final public String EMAIL = "EMAIL";
        static final public String S2S = "S2S";
        static final public String PAY_WEB = "PayWeb";
        static final public String WAWF = "WAWF";
        static final public String VIPERS = "Vipers";
    }

    public static class DunningLetters {
        public static final String DYS_PST_DUE_FINAL_PARM = "DUNNING_LETTERS_FINAL_DAYS_PAST_DUE";
        public static final String DYS_PST_DUE_STATE_AGENCY_FINAL_PARM = "DUNNING_LETTERS_STATE_AGENCY_FINAL_DAYS_PAST_DUE";
        public static final String DYS_PST_DUE_CURRENT = "Current";
        public static final String DYS_PST_DUE_31_60 = "31-60";
        public static final String DYS_PST_DUE_61_90 = "61-90";
        public static final String DYS_PST_DUE_91_120 = "91-120";
        public static final String DYS_PST_DUE_121 = "121+";
        public static final String DYS_PST_DUE_FINAL = "FINAL";
        public static final String DYS_PST_DUE_STATE_AGENCY_FINAL = "State Agency FINAL";
        public static final String DUNNING_LETTER_SENT_TXT = "Dunning Letter has been sent to sponsor.";
    }

    public static class ContractsGrantsAgingReportFields {

        public static final String TOTAL_0_TO_30 = "total0to30";
        public static final String TOTAL_31_TO_60 = "total31to60";
        public static final String TOTAL_61_TO_90 = "total61to90";
        public static final String TOTAL_91_TO_SYSPR = "total90toSYSPR";
        public static final String TOTAL_SYSPR_PLUS_1_OR_MORE = "totalSYSPRplus1orMore";
        public static final String TOTAL_AMOUNT_DUE = "totalAmountDue";
        public static final String OPEN_INVOCE_REPORT_NAME = "Contracts And Grants Open Invoices Report";


        public static final String AGENCY_SHORT_NAME = "Agency Short Name";
        public static final String UNAPPLIED_PAYMENTS = "Unapplied Payments";
        public static final String TOTAL_WRITEOFF = "Total Write-Off";
        public static final String TOTAL_CREDITS = "Total Credits";
    }

    // Award
    public static final String LOC_BY_AWARD = "LOC By Award";
    public static final String LOC_BY_LOC_FUND = "LOC By Letter of Credit Fund";
    public static final String LOC_BY_LOC_FUND_GRP = "LOC By Letter of Credit Fund Group";
    public static final String AWARD_MILESTONE_CHECK_LIMIT_DAYS = "AWARD_MILESTONE_CHECK_LIMIT_DAYS";

    public static final String PRORATE_WARNING = "document.prorateWarning";
    public static final String MINIMUM_INVOICE_AMOUNT = "MINIMUM_INVOICE_AMOUNT";

    public static final String INVOICE_REPORT_OPTION = "dummyBusinessObject.invoiceReportOption";
    public static final String OUTSTANDING_INVOICES = "Outstanding Invoices";
    public static final String PAST_DUE_INVOICES = "Past Due Invoices";

    public static final String QUATER1 = "q1";
    public static final String QUATER2 = "q2";
    public static final String QUATER3 = "q3";
    public static final String QUATER4 = "q4";
    public static final String SEMI_ANNUAL = "Sa";
    public static final String ANNUAL = "An";
    public static final String FINAL = "F";
    public static final String ZERO = "0";

    // CG Invoice Document
    public static final String CONTRACTS_AND_GRANTS_INVOICE_CATEGORIES = "CONTRACTS_AND_GRANTS_INVOICE_CATEGORIES";

    public static final String CGIN_DOCUMENT_TYPE = "CGIN";
    public static final String CGIN_DOCUMENT_DESCRIPTION = "Contracts Grants Invoice Document";
    public static final String ACCOUNT = "Account";
    public static final String CONTRACT_CONTROL_ACCOUNT = "Contract Control Account";
    public static final String INV_RPT_PRCS_IN_PROGRESS = "IN PROGRESS";
    public static final String INV_RPT_PRCS_SENT = "EMAILS SENT";
    
    private static final String ACTIVE_INVOICE_TEMPLATE_ERROR = "error.invoice.associate.active.templete";
    private static final String INVOICE_TEMPLATE_NOT_FOUND_ERROR = "error.invoice.templete.not.found";
    private static final String CORRECTED_INVOICE_NOT_FOUND_ERROR = "error.invoice.not.correct";

}
