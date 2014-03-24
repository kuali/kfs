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
 * Error Key Constants for KFS-AR.
 */
public class ArKeyConstants {

    // Customer Invoice Document errors:
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailTotalAmount";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailUnitPrice";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailQuantityPrice";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_INVALID_ITEM_CODE = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailItemCode";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_DISCOUNT_AMOUNT_GREATER_THAN_PARENT_AMOUNT = "error.document.customerInvoiceDocument.discountAmountGreaterThanParentAmount";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_SYSTEM_INFORMATION_DISCOUNT_DOES_NOT_EXIST = "error.document.customerInvoiceDocument.systemInformationDiscountDoesNotExist";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_CHART_OF_ACCOUNTS_CODE = "error.document.customerInvoiceDocument.invalidBilledByChartOfAccountsCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_ORGANIZATION_CODE = "error.document.customerInvoiceDocument.invalidBilledByOrganizationCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_MORE_THAN_X_DAYS = "error.document.customerInvoiceDocument.invalidInvoiceDueDateMoreThanXDays";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_BEFORE_OR_EQUAL_TO_BILLING_DATE = "error.document.customerInvoiceDocument.invalidInvoiceDueDateBeforeOrEqualBillingDate";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CUSTOMER_NUMBER = "error.document.customerInvoiceDocument.invalidCustomerNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_NO_CUSTOMER_INVOICE_DETAILS = "error.document.customerInvoiceDocument.noCustomerInvoiceDetails";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLING_PROCESSING_ORGANIZATION_IN_ORG_OPTIONS = "error.document.customerInvoiceDocument.invalidBilingProcessingOrgInOrgOptions";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_CHART_OF_ACCOUNTS_CODE = "error.document.customerInvoiceDocument.invalidPaymentChartOfAccountsCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_ACCOUNT_NUMBER = "error.document.customerInvoiceDocument.invalidPaymentAccountNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_ACCOUNT_NUMBER = "error.document.customerInvoiceDocument.invalidPaymentSubAccountNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_OBJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_OBJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentSubObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_PROJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentProjectCode";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SHIP_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.invalidShipToAddressIdentifier";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILL_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.invalidBillToAddressIdentifier";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INACTIVE_SHIP_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.inactiveShipToAddressIdentifier";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INACTIVE_BILL_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.inactiveBillToAddressIdentifier";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CHART_WITH_NO_AR_OBJ_CD = "error.document.customerInvoiceDocument.invalidChartWithNoARObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_WITH_NO_AR_OBJ_CD = "error.document.customerInvoiceDocument.invalidSubFundWithNoARObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_AR_OBJ_CD_IN_PARM = "error.document.customerInvoiceDocument.invalidSubFundARObjectCodeInParm";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_UNIT_OF_MEASURE_CD = "error.document.customerInvoiceDocument.invalidUnitOfMeasureCode";

    // Invoice Recurrence errors/warnings:
    public static final String ERROR_RECURRING_INVOICE_NUMBER_MUST_BE_APPROVED = "error.document.invoiceRecurrenceMaintenance.invoiceMustBeApproved";
    public static final String ERROR_MAINTENANCE_DOCUMENT_ALREADY_EXISTS = "error.document.invoiceRecurrenceMaintenance.maintenanceDocumentsExists";
    public static final String ERROR_INVOICE_RECURRENCE_BEGIN_DATE_IS_REQUIRED = "error.document.invoiceRecurrenceMaintenance.beginDateIsRequired";
    public static final String ERROR_INVOICE_RECURRENCE_BEGIN_DATE_EARLIER_THAN_TODAY = "error.document.invoiceRecurrenceMaintenance.beginDateMustBeEarlierThanToday";
    public static final String ERROR_INVOICE_DOES_NOT_EXIST = "error.document.invoiceRecurrenceMaintenance.invoiceDoesNotExist";
    public static final String ERROR_END_DATE_EARLIER_THAN_BEGIN_DATE = "error.document.invoiceRecurrenceMaintenance.endDateEarlierThanBeginDate";
    public static final String ERROR_END_DATE_OR_TOTAL_NUMBER_OF_RECURRENCES = "error.document.invoiceRecurrenceMaintenance.enterEndDateOrTotalNumberOfRecurrences";
    public static final String ERROR_TOTAL_NUMBER_OF_RECURRENCES_GREATER_THAN_ALLOWED = "error.document.invoiceRecurrenceMaintenance.totalRecurrencesMoreThanAllowed";
    public static final String ERROR_END_DATE_AND_TOTAL_NUMBER_OF_RECURRENCES_NOT_VALID = "error.document.invoiceRecurrenceMaintenance.endDateAndTotalNumberOfRecurrencesNotValid";
    public static final String ERROR_INVOICE_RECURRENCE_INTERVAL_CODE_IS_REQUIRED = "error.document.invoiceRecurrenceMaintenance.intervalCodeIsRequired";
    public static final String ERROR_INVOICE_RECURRENCE_INITIATOR_IS_REQUIRED = "error.document.invoiceRecurrenceMaintenance.initiatorIsRequired";
    public static final String ERROR_INVOICE_RECURRENCE_INITIATOR_DOES_NOT_EXIST = "error.document.invoiceRecurrenceMaintenance.initiatorDoesNotExist";
    public static final String ERROR_INVOICE_RECURRENCE_INITIATOR_IS_NOT_AUTHORIZED = "error.document.invoiceRecurrenceMainitenance.initiatorIsNotAuthorized";
    public static final String ERROR_INVOICE_RECURRENCE_DATA_SUFFICIENCY = "error.document.invoiceRecurrenceMaintenance.notEnoughDataToGenerateRecurrence";
    public static final String ERROR_INVOICE_RECURRENCE_ACTIVE_MUST_BE_TRUE = "error.document.invoiceRecurrenceMaintenance.activeIndicatorMustBeTrue";

    // Organization Accounting Defaults errors
    public static final class OrganizationAccountingDefaultErrors {
        public static final String WRITE_OFF_OBJECT_CODE_INVALID = "error.document.organizationAccountingDefaultMaintenance.writeOffObjectCodeInvalid";
        public static final String LATE_CHARGE_OBJECT_CODE_INVALID = "error.document.organizationAccountingDefaultMaintenance.lateChargeObjectCodeInvalid";
        public static final String DEFAULT_INVOICE_FINANCIAL_OBJECT_CODE_INVALID = "error.document.organizationAccountingDefaultMaintenance.defaultInvoiceFinancialObjectCodeInvalid";
        public static final String DEFAULT_INVOICE_FINANCIAL_OBJECT_CODE_INVALID_RESTRICTED = "error.document.organizationAccountingDefaultMaintenance.defaultInvoiceFinancialObjectCodeInvalidRestricted";
        public static final String DEFAULT_CHART_OF_ACCOUNTS_REQUIRED_IF_DEFAULT_OBJECT_CODE_EXISTS = "error.document.organizationAccountingDefaultMaintenance.defaultInvoiceChartOfAccountsCodeMustExist";
        public static final String ERROR_WRITEOFF_OBJECT_CODE_REQUIRED = "error.document.customerInvoiceDocument.writeoffFinancialObjectCodeRequired";
        public static final String ERROR_WRITEOFF_CHART_OF_ACCOUNTS_CODE_REQUIRED = "error.document.customerInvoiceDocument.writeoffChartOfAccountsCodeRequired";
        public static final String ERROR_WRITEOFF_ACCOUNT_NUMBER_REQUIRED = "error.document.customerInvoiceDocument.writeoffAccountNumberRequired";
        public static final String ERROR_PAYMENT_CHART_OF_ACCOUNTS_CODE_REQUIRED = "error.document.customerInvoiceDocument.paymentChartOfAccountsCodeRequired";
        public static final String ERROR_PAYMENT_ACCOUNT_NUMBER_REQUIRED = "error.document.customerInvoiceDocument.paymentAccountNumberRequired";
        public static final String ERROR_PAYMENT_OBJECT_CODE_REQUIRED = "error.document.customerInvoiceDocument.paymentPaymentObjectCodeRequired";
    }

    // System Information errors
    public static final class SystemInformation {
        // public static final String SALES_TAX_OBJECT_CODE_INVALID = "error.SystemInformation.salesTaxObjectCodeInvalid";
        public static final String ERROR_CLEARING_ACCOUNT_INACTIVE = "error.SystemInformation.clearingAccountInactive";
        public static final String ERROR_WIRE_ACCOUNT_INACTIVE = "error.SystemInformation.wireAccountInactive";
        public static final String ERROR_LOCKBOX_NUMBER_NOT_UNIQUE = "error.SystemInformation.lockboxNumberNotUnique";
    }

    // Invoice Item Code errors
    public static final class InvoiceItemCode {
        public static final String NONPOSITIVE_ITEM_DEFAULT_PRICE = "error.invoiceItemCode.nonPositiveNumericValue";
        public static final String NONPOSITIVE_ITEM_DEFAULT_QUANTITY = "error.invoiceItemCode.nonPositiveNumericValue";
        public static final String ORG_OPTIONS_DOES_NOT_EXIST_FOR_CHART_AND_ORG = "error.invoiceItemCode.orgOptionsDoesNotExistForChartAndOrg";
        public static final String ERROR_INVALID_CHART_OF_ACCOUNTS_CODE = "error.invoiceItemCode.errorInvalidChartOfAccountsCode";
        public static final String ERROR_INVALID_ORGANIZATION_CODE = "error.invoiceItemCode.errorInvalidOrganizationCode";
    }

    // Customer Type errors
    public static class CustomerTypeConstants {
        public static final String ERROR_CUSTOMER_TYPE_DUPLICATE_VALUE = "error.document.customerType.duplicateValue";
    }

    // Customer Load messages
    public static final class CustomerLoad {
        public static final String MESSAGE_BATCH_UPLOAD_XML_TITLE_CUSTOMER = "message.ar.customerLoad.batchUpload.xml.title";
        public static final String MESSAGE_BATCH_UPLOAD_CSV_TITLE_CUSTOMER = "message.ar.customerLoad.batchUpload.csv.title";
    }

    // Customer errors
    public static class CustomerConstants {
        public static final String MESSAGE_CUSTOMER_WITH_SAME_NAME_EXISTS = "message.document.customerMaintenance.customerWithSameNameExists";
        public static final String GENERATE_CUSTOMER_QUESTION_ID = "GenerateCustomerQuestionID";
        public static final String ERROR_AT_LEAST_ONE_ADDRESS = "error.document.customer.addressRequired";
        public static final String ERROR_ONLY_ONE_PRIMARY_ADDRESS = "error.document.customer.oneAndOnlyOnePrimaryAddressRequired";
        public static final String ERROR_CUSTOMER_NAME_LESS_THAN_THREE_CHARACTERS = "error.document.customer.customerNameLessThanThreeCharacters";
        public static final String ERROR_CUSTOMER_NAME_NO_SPACES_IN_FIRST_THREE_CHARACTERS = "error.document.customer.customerNameNoSpacesInFirstThreeCharacters";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY = "P";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE = "A";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE_US = "US";
        public static final String ERROR_CUSTOMER_ADDRESS_STATE_CODE_REQUIRED_WHEN_COUNTTRY_US = "error.document.customer.stateCodeIsRequiredWhenCountryUS";
        public static final String ERROR_CUSTOMER_ADDRESS_ZIP_CODE_REQUIRED_WHEN_COUNTTRY_US = "error.document.customer.zipCodeIsRequiredWhenCountryUS";
        public static final String ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US = "error.document.customer.addressInternationalProvinceNameIsRequiredWhenCountryNonUS";
        public static final String ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US = "error.document.customer.internationalMailCodeIsRequiredWhenCountryNonUS";
        public static final String ERROR_TAX_NUMBER_IS_REQUIRED = "error.document.customer.taxNumberRequired";
        public static final String ACTIONS_REPORT = "report";
        public static final String ERROR_CUSTOMER_ADDRESS_END_DATE_MUST_BE_FUTURE_DATE = "error.document.customer.endDateMustBeFutureDate";
        public static final String ERROR_CUSTOMER_ADDRESS_END_DATE_MUST_BE_CURRENT_OR_FUTURE_DATE = "error.document.customer.endDateMustBeCurrenOrFutureDate";
        public static final String ERROR_CUSTOMER_PRIMARY_ADDRESS_MUST_HAVE_FUTURE_END_DATE = "error.document.customer.primaryAddressMustHaveFutureEndDate";
    }

    // Customer Credit Memo errors/warnings:
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT = "error.document.customerCreditMemoDocument.invalidDataInput";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerCreditMemoDocument.invalidCustomerCreditMemoItemQuantity";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerCreditMemoDocument.invalidCustomerCreditMemoItemAmount";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_GREATER_THAN_INVOICE_ITEM_QUANTITY = "error.document.customerCreditMemoDocument.itemQuantityGreaterThanParentItemQuantity";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_GREATER_THAN_INVOICE_ITEM_AMOUNT = "error.document.customerCreditMemoDocument.itemAmountGreaterThanParentItemAmount";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVALID_INVOICE_DOCUMENT_NUMBER = "error.document.customerCreditMemoDocument.invalidInvoiceDocumentNumber";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_ONE_CRM_IN_ROUTE_PER_INVOICE = "error.document.customerCreditMemoDocument.onlyOneCRMInRoutePerInvoice";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT__INVOICE_DOCUMENT_NUMBER_IS_REQUIRED = "error.document.customerCreditMemoDocument.invRefNumberIsRequired";
    public static final String WARNING_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVOICE_HAS_DISCOUNT = "warning.documnet.customerCreditMemoDocument.invoiceHasAppliedDiscount";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_NO_DATA_TO_SUBMIT = "error.document.customerCreditMemoDocument.noDataToSubmit";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_CORRECTED_INVOICE = "error.document.customerCreditMemoDocument.invoiceHasBeenCorrected";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_CORRECTING_INVOICE = "error.document.customerCreditMemoDocument.invoiceCorrectsAnotherInvoice";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_NOT_FINAL = "error.document.customerInvoiceDocument.notFinal";

    // Cash Control Document errors
    public static final String ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL_FOR_PAYMENT_MEDIUM_CASH = "error.ar.ReferenceDocNumberCannotBeNullforPaymentMediumCash";
    public static final String ERROR_REFERENCE_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH = "error.ar.ReferenceDocNumberMustBeValidforPaymentMediumCash";
    public static final String ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG = "error.ar.OrganizationOptionsMustBeSet";
    public static final String ERROR_SYSTEM_INFORMATION_MUST_BE_SET_FOR_USER_ORG = "error.ar.SystemInformationMustBeSet";
    public static final String ERROR_PAYMENT_MEDIUM_IS_NOT_VALID = "error.ar.CustomerPaymentMediumIsNotValid";
    public static final String ERROR_ALL_APPLICATION_DOCS_MUST_BE_APPROVED = "error.ar.AllApplicationDocumentsMustBeApproved";
    public static final String ERROR_DELETE_ADD_APP_DOCS_NOT_ALLOWED_AFTER_GLPES_GEN = "error.ar.DeleteAddApplicationDocNotAllowedAfterGLPEsGenerated";
    public static final String ERROR_NO_LINES_TO_PROCESS = "error.ar.NoLinesToProcess";
    public static final String ERROR_LINE_AMOUNT_CANNOT_BE_ZERO = "error.ar.LineAmountCannotBeZero";
    public static final String ERROR_LINE_AMOUNT_CANNOT_BE_NEGATIVE = "error.ar.LineAmountCannotBeNegative";
    public static final String ERROR_GLPES_NOT_CREATED = "error.ar.GLPEsNotCreated";
    public static final String ERROR_CUSTOMER_NUMBER_IS_NOT_VALID = "error.ar.CustomerNumberIsNotValid";
    public static final String ERROR_CUSTOMER_IS_INACTIVE = "error.ar.CustomerIsInactive";
    public static final String ERROR_CANT_CANCEL_CASH_CONTROL_DOC_WITH_ASSOCIATED_APPROVED_PAYMENT_APPLICATION = "error.ar.CantCancelCashControlDocWithAssociatedApprovedPaymentApplicationDoc";
    public static final String ERROR_INVALID_BANK_CODE = "error.ar.invalidBankCode";
    public static final String ERROR_BANK_NOT_ELIGIBLE_FOR_DEPOSIT_ACTIVITY = "error.ar.bankNotEligibleForDepositActivity";
    public static final String ERROR_BANK_CODE_REQUIRED = "error.ar.bankCodeRequired";
    public static final String CASH_CTRL_DOC_CREATED_BY_BATCH = "message.ar.cashControlDocCreatedByLOC";
    public static final String CASH_CTRL_DOC_CORRECTION = "message.ar.cashControlDocCorrection";
    public static final String CREATED_BY_CASH_CTRL_DOC = "message.ar.createdByCashControlDocument";
    public static final String DOCUMENT_DELETED_FROM_CASH_CTRL_DOC = "message.ar.documentDeletedFromCashControl";
    public static final String ELECTRONIC_PAYMENT_CLAIM = "message.ar.electronicPaymentClaim";
    public static final String ERROR_CASH_CTRL_DTL_TO_REVERSE_NOT_SELECTED = "error.ar.cashControlDocToReverseNotSelected";

    // Customer Invoice Writeoff Document errors
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_CHART_WRITEOFF_OBJECT_DOESNT_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffObjectDoesntExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_MUST_EXIST = "error.document.customerInvoiceWriteoff.writeoffFAUMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_CHART_MUST_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffFAUChartMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_ACCOUNT_MUST_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffFAUAccountMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_OBJECT_CODE_MUST_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffFAUObjectMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_INVOICE_HAS_CREDIT_BALANCE = "error.document.customerInvoiceWriteoff.invoiceHasCreditBalance";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_INVALID_EXPLANATION = "error.document.customerInvoiceWriteoff.invalidExplanation";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_EMPTY_EXPLANATION = "error.document.customerInvoiceWriteoff.emptyExplanation";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_NO_INVOICES_SELECTED = "error.document.customerInvoiceWriteoff.noInvoicesSelected";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_BATCH_SENT = "error.document.customerInvoiceWriteoff.batchSent";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_CUSTOMER_NOTE_REQUIRED = "error.document.customerInvoiceWriteoff.emptyCustomerNote";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_CUSTOMER_NOTE_INVALID = "error.document.customerInvoiceWriteoff.invalidCustomerNote";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_ONE_WRITEOFF_IN_ROUTE_PER_INVOICE = "error.document.customerInvoiceWriteoff.onlyOneWriteoffInRoutePerInvoice";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_INVOICE_NOT_FINAL = "error.document.customerInvoiceWriteoff.invoiceNotFinal";


    // Organization Options errors
    public static class OrganizationOptionsErrors {
        public static final String SYS_INFO_DOES_NOT_EXIST_FOR_PROCESSING_CHART_AND_ORG = "error.document.organizationOptions.sysInfoDoesNotExistForProcessingChartAndOrg";
        public static final String ERROR_ORG_OPTIONS_ZIP_CODE_REQUIRED = "error.document.organizationOptions.orgOptionsZipCodeRequired";
    }

    public static class PaymentApplicationDocumentErrors {
        public static final String AMOUNT_TO_BE_APPLIED_CANNOT_BE_ZERO = "error.document.paymentApplication.amountToBeAppliedCannotBeZero";
        public static final String AMOUNT_TO_BE_APPLIED_EXCEEDS_AMOUNT_OUTSTANDING = "error.document.paymentApplication.amountToBeAppliedExceedsAmountOutstanding";
        public static final String AMOUNT_TO_BE_APPLIED_MUST_BE_GREATER_THAN_ZERO = "error.document.paymentApplication.amountToBeAppliedCannotBeZero";
        public static final String FULL_AMOUNT_NOT_APPLIED = "error.document.paymentApplication.fullAmountNotApplied";
        public static final String AMOUNT_TO_BE_APPLIED_EXCEEDS_OPEN_INVOICE_DETAIL_AMOUNT = "error.document.paymentApplication.amountToBeAppliedExceedsOpenInvoiceDetailAmount";
        public static final String AMOUNT_TO_BE_APPLIED_MUST_BE_POSTIIVE = "error.document.paymentApplication.amountToBeAppliedMustBePositive";
        public static final String NON_AR_CHART_REQUIRED = "error.document.paymentApplication.nonArLineChartRequired";
        public static final String NON_AR_ACCOUNT_REQUIRED = "error.document.paymentApplication.nonArLineAccountRequired";
        public static final String NON_AR_OBJECT_CODE_REQUIRED = "error.document.paymentApplication.nonArLineObjectCodeRequired";
        public static final String NON_AR_CHART_INVALID = "error.document.paymentApplication.nonArLineChartInvalid";
        public static final String NON_AR_ACCOUNT_INVALID = "error.document.paymentApplication.nonArLineAccountInvalid";
        public static final String NON_AR_SUB_ACCOUNT_INVALID = "error.document.paymentApplication.nonArLineSubAccountInvalid";
        public static final String NON_AR_OBJECT_CODE_INVALID = "error.document.paymentApplication.nonArLineObjectCodeInvalid";
        public static final String NON_AR_SUB_OBJECT_CODE_INVALID = "error.document.paymentApplication.nonArLineSubObjectCodeInvalid";
        public static final String NON_AR_PROJECT_CODE_INVALID = "error.document.paymentApplication.nonArLineProjectCodeInvalid";
        public static final String NON_AR_AMOUNT_REQUIRED = "error.document.paymentApplication.nonArLineAmountRequired";
        public static final String NON_AR_AMOUNT_MUST_BE_POSITIVE = "error.document.paymentApplication.nonArLineAmountMustBePositive";
        public static final String CANNOT_APPLY_MORE_THAN_CASH_CONTROL_TOTAL_AMOUNT = "error.document.paymentApplication.quickAppliedExceedsCashControlTotalAmount";
        public static final String INVOICE_DETAIL_CANNOT_APPLY_MORE_THAN_CONTROL_TOTAL_AMOUNT = "error.document.paymentApplication.detailAppliedExceedsCashControlTotalAmount";
        public static final String CANNOT_APPLY_MORE_THAN_BALANCE_TO_BE_APPLIED = "error.document.paymentApplication.AppliedAmountExceedsBalanceToBeApplied";
        public static final String UNAPPLIED_AMOUNT_CANNOT_EXCEED_AVAILABLE_AMOUNT = "error.document.paymentApplication.unappliedAmountCannotExceedAvailableAmount";
        public static final String UNAPPLIED_AMOUNT_CANNOT_EXCEED_BALANCE_TO_BE_APPLIED = "error.document.paymentApplication.unappliedAmountCannotExceedBalanceToBeApplied";
        public static final String UNAPPLIED_AMOUNT_CANNOT_BE_EMPTY_OR_ZERO = "error.document.paymentApplication.unappliedAmountCannotBeEmptyOrZero";
        public static final String UNAPPLIED_AMOUNT_CANNOT_BE_NEGATIVE = "error.document.paymentApplication.unappliedAmountCannotBeNegative";
        public static final String UNAPPLIED_CUSTOMER_NUMBER_CANNOT_BE_LEFT_BLANK = "error.document.paymentApplication.unappliedCustomerNumberCannotBeBlank";
        public static final String NON_AR_AMOUNT_EXCEEDS_SELECTED_INVOICE_BALANCE = "error.document.paymentApplication.nonArLineAmountExceedsInvoiceBalance";
        public static final String NON_AR_AMOUNT_EXCEEDS_BALANCE_TO_BE_APPLIED = "error.document.paymentApplication.nonArLineAmountExceedsBalanceToBeApplied";
        public static final String NON_INVOICED_CHART_IS_REQUIRED = "error.document.paymentApplication.nonInvoicedChartRequired=Chart is required on non-ar lines.";
        public static final String NON_INVOICED_ACCOUNT_IS_REQUIRED = "error.document.paymentApplication.nonInvoicedAccountRequired=Account is required on non-ar lines.";
        public static final String NON_INVOICED_FINANCIAL_OBJECT_IS_REQUIRED = "error.document.paymentApplication.nonInvoicedFinancialObjectRequired=Object code is required on non-ar lines.";
        public static final String CANNOT_QUICK_APPLY_ON_INVOICE_WITH_ZERO_OPEN_AMOUNT = "error.document.paymentApplication.cannotQuickApplyOnInvoiceWithZeroOpenAmount";
        public static final String ENTERED_INVOICE_CUSTOMER_NUMBER_INVALID = "error.document.paymentApplication.enteredInvoiceCustomerNumberInvalid";
        public static final String ENTERED_INVOICE_NUMBER_INVALID = "error.document.paymentApplication.enteredInvoiceNumberInvalid";
        public static final String ERROR_DOCUMENT_PAYMENT_APPLICATION_MISSING_SYSTEM_INFORMATION = "error.document.paymentApplication.missing.system.information";
        public static final String ERROR_SYSTEM_INFORMATION_IS_MISSING_REFUND_PAYMENT_REASON = "error.system.information.missing.refund.payment.reason";
        public static final String ERROR_SYSTEM_INFORMATION_IS_MISSING_REFUND_DOCUMENTATION_LOCATION = "error.system.information.missing.refund.documentation.location";
    }

    public static class CollectionActivityDocumentErrors {
        public static final String ERROR_FOLLOW_UP_DATE_REQUIRED = "error.collectionActivity.followupDateRequired";
        public static final String ERROR_COMPLETED_DATE_REQUIRED = "error.collectionActivity.completedDateRequired";
        public static final String ERROR_INVOICE_REQUIRED = "error.collectionActivity.invoiceRequired";
        public static final String SELECTED_INVOICES = "error.collectionActivity.selectedInvoiceDocumentNumberListRequired";
    }

    public static class CollectionActivityDocumentConstants {
        public static final String ENTERED_INVOICE_CUSTOMER_NUMBER_INVALID = "error.document.paymentApplication.enteredInvoiceCustomerNumberInvalid";
        public static final String COLLECTION_ACTIVITY_TITLE_PROPERTY = "message.inquiry.collectionActivity.title";
        public static final String CREATED_BY_COLLECTION_ACTIVITY_DOC = "message.ar.createdByCollectionActivityDocument";
    }

    // Contracts Grants Invoice constants and errors
    public static class ContractsGrantsInvoiceConstants {
        public static final String MESSAGE_CONTRACTS_GRANTS_INVOICE_BATCH_SENT = "message.document.contractsGrantsInvoice.batchSent";
        public static final String WARNING_PRORATE_VALUE_IS_LESS_THAN_ELIGIBLE_FOR_BILLING = "warning.prorate.value.is.less.than.eligible.for.billing";
        public static final String WARNING_PRORATE_VALUE_IS_MORE_THAN_ELIGIBLE_FOR_BILLING = "warning.prorate.value.is.more.than.eligible.for.billing";
        public static final String ERROR_NO_AWARDS_RETRIEVED = "error.document.no.awards.retrieved";
        public static final String ERROR_AWARDS_INVALID = "error.document.awards.invalid";
        public static final String ERROR_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO = "error.total.amount.less.than.equal.to.zero";
        public static final String ERROR_DOCUMENT_GLPE_GENERATION_FAILED = "error.document.glpe.generation.failed";
        public static final String ERROR_DOCUMENT_AMOUNT_TO_DRAW_INVALID = "error.document.amount.to.draw.invalid";
    }

    // Final Billed Indicator Error
    public static final String FINAL_BILLED_INDICATOR_EDOC_ERROR_KEY = "error.custom";

    /* Start TEM REFUND Merge */
    // ar refunding
    public static final String MESSAGE_REFUND_DV_DOCUMENT_DESCRIPTION = "message.document.refundDV.description";
    public static final String MESSAGE_REFUND_DV_CHECK_STUB_TEXT = "message.document.refundDV.checkStubText";

    /* End TEM REFUND Merge */

    // Collection Activity Type errors
    public static class CollectionActivityTypeConstants {
        public static final String ERROR_COLLECTION_ACTIVITY_TYPE_DUPLICATE_VALUE = "error.document.collectionActivityType.duplicateValue";


    }

    public static class DunningCampaignConstantsAndErrors {
        public static final String MESSAGE_DUNNING_CAMPAIGN_BATCH_NOT_SENT = "message.document.dunningCampaign.batchNotSent";

    }

    public static class DunningLetterDistributionErrors {
        public static final String ERROR_DAYS_PAST_DUE_DUPLICATE = "error.document.daysPastDue.duplicateValue";
        public static final String ERROR_BILLING_CHART_CODE_REQUIRED = "error.document.billingChartCode.required";
        public static final String ERROR_BILLING_ORGANIZATION_CODE_REQUIRED = "error.document.billingOrganizationCode.required";
        public static final String ERROR_PROCESSING_CHART_CODE_REQUIRED = "error.document.processingChartCode.required";
        public static final String ERROR_PROCESSING_ORGANIZATION_CODE_REQUIRED = "error.document.processingOrganizationCode.required";
    }

    public static class ReferralToCollectionsDocumentErrors {
        public static final String ERROR_EMPTY_REQUIRED_FIELDS = "error.document.referralToCollections.emptyRequiredFields";
    }

    public static class CollectionHierarchyDocumentErrors {
        public static final String ERROR_COLLINFO_SAME_AS_COLLHEAD = "error.document.collectorInformations.sameAsCollectorHead";
        public static final String ERROR_DUPLICATE_COLL_INFO = "error.document.collectorInformations.duplicate";
    }

    public static final String ACTIONS_UPLOAD = "upload";
    public static final String ACTIONS_DOWNLOAD = "download";

     public static final class LockboxLoad {

        public static final String ERROR_LOCKBOX_INVALID_FIRST_RECORD = "error.lockbox.invalid.first.record";
        public static final String ERROR_LOCKBOX_INVALID_HDR_TRANS_BATCH_TOT = "error.lockbox.invalid.hdr.trans.batch.tot";
        public static final String ERROR_LOCKBOX_INVALID_HDR_TRANS_BATCH_CNT = "error.lockbox.invalid.hdr.trans.batch.cnt";
        public static final String ERROR_LOCKBOX_NON_NUMERIC_INVOICE_AMOUNT = "error.lockbox.non.numeric.invoice.amount";
        public static final String ERROR_LOCKBOX_NON_NUMERIC_INVOICE_PAID_AMOUNT = "error.lockbox.non.numeric.invoice.paid.amount";
        public static final String MESSAGE_BATCH_UPLOAD_TITLE_LOCKBOX = "message.batchUpload.title.lockboxLoad";
    }

     public static class ContractsGrantsCategoryConstants {
         public static final String ERROR_ANY_ONE_REQUIRED = "error.any.one.required";
         public static final String CATEGORY_INFO = "error.cgcategory.info";
     }

     public static final String MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_1 = "message.cg.upcoming.milestones.email.line1";
     public static final String MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_2 = "message.cg.upcoming.milestones.email.line2";

     // Kim Type Services error messages
     public static final String ERROR_BILLINGCHART_OR_BILLINGORG_NOTEMPTY_ALL_REQUIRED = "error.billingchart.or.billingorg.notempty.all.required";
     public static final String ERROR_EITHER_BILLINGCHART_OR_PROCESSCHART_REQUIRED_NOT_BOTH = "error.either.billingchart.or.processchart.required.not.both";
     public static final String ERROR_PROCESSCHART_OR_PROCESSORG_NOTEMPTY_ALL_REQUIRED = "error.processchart.or.processorg.notempty.all.required";
     public static final String ERROR_STARTLETTER_AFTER_ENDLETTER = "error.startletter.after.endletter";
     public static final String ERROR_STARTLETTER_OR_ENDLETTER_NOTEMPTY_ALL_REQUIRED = "error.startletter.or.endletter.notempty.all.required";

     // Award Constants and errors
     public static class AwardConstants {
         public static final String ERROR_NO_CTRL_ACCT = "error.cg.no.control.account";
         public static final String ERROR_MULTIPLE_CTRL_ACCT = "error.cg.multiple.control.account";
     }

     public static final String NO_VALUES_RETURNED = "message.no.values.returned";

     public static final String ERROR_AWARD_MILESTONE_SCHEDULE_EXISTS = "error.cg.award.milestone.schedule.exists";
     public static final String ERROR_AWARD_MILESTONE_SCHEDULE_INCORRECT_BILLING_FREQUENCY = "error.cg.award.milestone.schedule.incorrect.billing.frequency";
     public static final String ERROR_AWARD_PREDETERMINED_BILLING_SCHEDULE_EXISTS = "error.cg.award.predetermined.billing.schedule.exists";
     public static final String ERROR_AWARD_PREDETERMINED_BILLING_SCHEDULE_INCORRECT_BILLING_FREQUENCY = "error.cg.award.predetermined.billing.schedule.incorrect.billing.frequency";

}