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
package org.kuali.kfs.module.ar;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * Business Object Property Constants for KFS-AR.
 */
public class ArPropertyConstants{
    // CustomerInvoiceDocument
    public static class CustomerInvoiceDocumentFields {

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
        public static final String CUSTOMER_TYPE_CODE = "accountsReceivableDocumentHeader.customer.customerTypeCode";

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
        public static final String PROCESSING_ORGANIZATION_CODE = "accountsReceivableDocumentHeader.processingOrganizationCode";

        public static final String SHIP_TO_ADDRESS_IDENTIFIER = "customerShipToAddressIdentifier";
        public static final String BILL_TO_ADDRESS_IDENTIFIER = "customerBillToAddressIdentifier";
        public static final String OPEN_AMOUNT = "openAmount";

        public static final String OPEN_INVOICE_INDICATOR = "openInvoiceIndicator";

        public static final String INVOICE_DOCUMENT_RECURRENCE_BEGIN_DATE = "customerInvoiceRecurrenceDetails.documentRecurrenceBeginDate";
        public static final String INVOICE_DOCUMENT_RECURRENCE_END_DATE = "customerInvoiceRecurrenceDetails.documentRecurrenceEndDate";
        public static final String INVOICE_DOCUMENT_RECURRENCE_TOTAL_RECURRENCE_NUMBER = "customerInvoiceRecurrenceDetails.documentTotalRecurrenceNumber";
        public static final String INVOICE_DOCUMENT_RECURRENCE_INTERVAL_CODE = "customerInvoiceRecurrenceDetails.documentRecurrenceIntervalCode";
        public static final String INVOICE_DOCUMENT_RECURRENCE_INITIATOR = "customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName";
        public static final String INVOICE_DOCUMENT_RECURRENCE = "customerInvoiceRecurrenceDetails";
        public static final String INVOICE_DOCUMENT_RECURRENCE_ACTIVE = "customerInvoiceRecurrenceDetails.active";

        public static final String CG_ACCT_RESP_ID = "sourceAccountingLines.account.contractsAndGrantsAccountResponsibilityId";
        public static final String ACCOUNT_NUMBER = "sourceAccountingLines.accountNumber";
        public static final String FINANCIAL_DOCUMENT_REF_INVOICE_NUMBER = "financialDocumentReferenceInvoiceNumber";
    }

    public static class ContractsGrantsInvoiceDocumentFields {
        public static final String LETTER_OF_CREDIT_CREATION_TYPE = "invoiceGeneralDetail.letterOfCreditCreationType";
        public static final String LETTER_OF_CREDIT_FUND_CODE = "invoiceGeneralDetail.letterOfCreditFundCode";
        public static final String LETTER_OF_CREDIT_FUND_GROUP_CODE = "invoiceGeneralDetail.letterOfCreditFundGroupCode";
        public static final String FINAL_BILL = "invoiceGeneralDetail.finalBillIndicator";
        public static final String PROPOSAL_NUMBER = "invoiceGeneralDetail.proposalNumber";
    }

    // InvoiceRecurrence
    public static final class InvoiceRecurrenceFields {
        public static final String INVOICE_RECURRENCE_BEGIN_DATE = "documentRecurrenceBeginDate";
        public static final String INVOICE_RECURRENCE_END_DATE = "documentRecurrenceEndDate";
        public static final String INVOICE_RECURRENCE_TOTAL_RECURRENCE_NUMBER = "documentTotalRecurrenceNumber";
        public static final String INVOICE_RECURRENCE_INITIATOR_USER_ID = "documentInitiatorUserIdentifier";
    }

    // OrganizationAccountingDefaults
    public static class OrganizationAccountingDefaultFields {
        public static final String PROCESSING_CHART_OF_ACCOUNTS_CODE = "processingChartOfAccountCode";
        public static final String PROCESSING_ORGANIZATION_CODE = "processingOrganizationCode";
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
        public static final String CUSTOMER_TYPE_CODE = "customerTypeCode";
    }

    // Customer
    public static class CustomerFields {
        public static final String CUSTOMER_TAB_GENERAL_INFORMATION = "customerGeneralInformation";
        public static final String CUSTOMER_TAB_ADDRESSES = "customerAddresses";
        public static final String CUSTOMER_ADDRESS = "customerAddress";
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
        public static final String CUSTOMER_INVOICE_TEMPLATE_CODE = "customerInvoiceTemplateCode";
        public static final String CUSTOMER_COPIES_TO_PRINT = "customerCopiesToPrint";
        public static final String CUSTOMER_ENVELOPES_TO_PRINT_QUANTITY = "customerEnvelopesToPrintQuantity";
    }

    // Customer Address
    public static class CustomerAddressFields {
        public static final String CUSTOMER_ADDRESS_NAME = "customerAddressName";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE = "customerAddressTypeCode";
        public static final String CUSTOMER_LINE1_STREET_ADDRESS = "customerLine1StreetAddress";
        public static final String CUSTOMER_LINE2_STREET_ADDRESS = "customerLine2StreetAddress";
        public static final String CUSTOMER_CITY_NAME = "customerCityName";
        public static final String CUSTOMER_COUNTRY_CODE = "customerCountryCode";
        public static final String CUSTOMER_STATE_CODE = "customerStateCode";
        public static final String CUSTOMER_ZIP_CODE = "customerZipCode";
        public static final String CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME = "customerAddressInternationalProvinceName";
        public static final String CUSTOMER_INTERNATIONAL_MAIL_CODE = "customerInternationalMailCode";
        public static final String CUSTOMER_EMAIL_ADDRESS = "customerEmailAddress";
    }

    // CustomerCreditMemoDocument
    public static class CustomerCreditMemoDocumentFields {
        public static final String CREDIT_MEMO_ITEM_QUANTITY = "creditMemoItemQuantity";
        public static final String CREDIT_MEMO_ITEM_TOTAL_AMOUNT = "creditMemoItemTotalAmount";
        public static final String CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER = "financialDocumentReferenceInvoiceNumber";
    }

    public static class CustomerStatementFields {
        public static final String CHART_CODE = "chartCode";
        public static final String ORG_CODE = "orgCode";
        public static final String STATEMENT_FORMAT = "statementFormat";
        public static final String INCLUDE_ZERO_BALANCE_CUSTOMERS = "includeZeroBalanceCustomers";
    }

    // CashControlDocument
    public static class CashControlDocumentFields {
        public static final String FINANCIAL_DOCUMENT_LINE_AMOUNT = "financialDocumentLineAmount";
        public static final String REFERENCE_FINANCIAL_DOC_NBR = "referenceFinancialDocumentNumber";
        public static final String APPLICATION_DOC_STATUS = "status";
        public static final String ORGANIZATION_DOC_NBR = "organizationDocumentNumber";
        public static final String CUSTOMER_PAYMENT_MEDIUM_CODE = "customerPaymentMediumCode";
        public static final String BANK_CODE = "document.bankCode";
        public static final String CUSTOMER_PAYMENT_MEDIUM = "customerPaymentMedium";
    }

    // CashControlDetail
    public static class CashControlDetailFields {
        public static final String CASH_CONTROL_DETAILS_TAB = "document.cashControlDetails";
        public static final String REFERENCE_FINANCIAL_DOC_NBR = "referenceFinancialDocumentNumber";
    }

    // CustomerInvoiceWriteoffDocument
    public static class CustomerInvoiceWriteoffDocumentFields {
        public static final String CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF = "customerInvoiceDetailsForWriteoff";
        public static final String CUSTOMER_INVOICE_WRITEOFF_EXPLANATION = "documentHeader.explanation";
        public static final String INVOICE_WRITEOFF_AMOUNT = "invoiceWriteoffAmount";

    }

    // CustomerInvoiceWriteoffLookupResult
    public static class CustomerInvoiceWriteoffLookupResultFields {
        public static final String CUSTOMER_NAME = "customerName";
        public static final String CUSTOMER_TYPE_CODE = "customerTypeCode";
        public static final String CUSTOMER_INVOICE_NUMBER = "customerInvoiceNumber";
        public static final String AGE = "age";
        public static final String CUSTOMER_NOTE = "customerNote";
    }

    // CustomerAgingReport
    public static class CustomerAgingReportFields {
        public static final String REPORT_RUN_DATE = "reportRunDate";
        public static final String PROCESSING_OR_BILLING_CHART_ACCOUNT_CODE = "processingOrBillingChartOfAccountsCode";
        public static final String ACCOUNT_CHART_CODE = "accountChartCode";
    }

    // Tickler Report fields
    public static class TicklersReportFields {
        public static final String COLLECTOR = "principalId";
        public static final String FOLLOWUP_DATE = "followupDate";
        public static final String ACTIVITY_CODE = "activityCode";
        public static final String AGENCY_NUMBER = "agencyNumber";
    }

    // Collection Event bo fields
    public static class CollectionEventFields {
        public static final String ID = "id";
        public static final String INVOICE_DOCUMENT_PROPOSAL_NUMBER = "invoiceDocument.invoiceGeneralDetails.proposalNumber";
        public static final String INVOICE_DOCUMENT_OPEN_INV_IND = "invoiceDocument." + ArPropertyConstants.OPEN_INVOICE_IND;
        public static final String FOLLOW_UP = "followup";
        public static final String FOLLOW_UP_DATE = "followupDate";
        public static final String COMPLETED_DATE = "completedDate";
        public static final String SELECTED_INVOICES = "selectedInvoiceDocumentNumberList";
    }

    public static class ContractsGrantsCollectionActivityDocumentFields {
        public static final String SELECTED_PROPOSAL_NUMBER = "selectedProposalNumber";
        public static final String SELECTED_INVOICE_DOCUMENT_NUMBER = "selectedInvoiceDocumentNumber";
        public static final String ACTIVITY_DATE = "activityDate";
        public static final String ACTIVITY_TEXT = "activityText";
        public static final String INVOICE_DETAILS = "invoiceDetails";
    }

    // OrganizationOptions
    public static class OrganizationOptionsFields {
        public static final String ORGANIZATION_CHECK_PAYABLE_TO_NAME = "organizationCheckPayableToName";
        public static final String ORGANIZATION_POSTAL_ZIP_CODE = "organizationPostalZipCode";
    }

    // SystemInformation (aka ProcessingOrg)
    public static class SystemInformationFields {
        public static final String LOCKBOX_NUMBER = "lockboxNumber";
        public static final String FEIN_NUMBER = "feinNumber";
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

    public static class SuspensionCategoryReportFields {
        public static final String SUSPENSION_CATEGORY_CODE = "suspensionCategoryCode";
        public static final String CONTRACTS_GRANTS_INVOICE_DOCUMENT_SUSPENSION_CATEGORY_CODE = "invoiceSuspensionCategories." + SUSPENSION_CATEGORY_CODE;
    }

    // Contracts & Grants Invoicing
    public static final String ACCOUNT_DETAILS_ACCOUNT_NUMBER = "accountDetails.accountNumber";
    public static final String INVOICE_DOCUMENT_NUMBER = "invoiceDocumentNumber";
    public static final String REPORT_OPTION = "reportOption";
    public static final String AWARD_FUND_MANAGERS = "awardFundManagers";
    public static final String BILLS = "bills";
    public static final String BILLING_FREQUENCY_CODE = "billingFrequencyCode";
    public static final String BILLING_FREQUENCY = "billingFrequency";
    public static final String BILLING_PERIOD = "billingPeriod";
    public static final String INSTRUMENT_TYPE_CODE = "instrumentTypeCode";
    public static final String INVOICING_OPTION_DESCRIPTION = "invoicingOptionDescription";
    public static final String FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH = KFSConstants.DOCUMENT_PROPERTY_NAME + ".invoiceEntries";
    public static final String OPEN_INVOICE_IND = "openInvoiceIndicator";
    public static final String DOCUMENT_STATUS_CODE = "documentHeader.financialDocumentStatusCode";
    public static final String DOCUMENT_HEADER_FINANCIAL_DOCUMENT_IN_ERROR_NUMBER = "documentHeader.financialDocumentInErrorNumber";
    public static final String CUSTOMER_NAME = "accountsReceivableDocumentHeader.customer.customerName";

    public static final String COLLECTOR_PRINC_NAME = "collector.principalName";

    // CG Invoice Reports
    @Deprecated
    public static final String RANGE_LOWER_BOUND_KEY_PREFIX = KFSPropertyConstants.RANGE_LOWER_BOUND_KEY_PREFIX;

    public static final String INVOICE_REPORT_DELIVERY_PROPERTY_PATH = KFSConstants.DOCUMENT_PROPERTY_NAME + ".invoiceReportDelivery";

    public static class ContractsGrantsInvoiceDocumentErrorLogLookupFields {
        public static final String ACCOUNTS = "accounts";
        public static final String AWARD_BEGINNING_DATE_FROM = "rangeLowerBoundKeyPrefix_awardBeginningDate";
        public static final String AWARD_BEGINNING_DATE_TO = "awardBeginningDate";
        public static final String AWARD_ENDING_DATE_FROM = "rangeLowerBoundKeyPrefix_awardEndingDate";
        public static final String AWARD_ENDING_DATE_TO = "awardEndingDate";
        public static final String AWARD_TOTAL_AMOUNT = "awardTotalAmount";
        public static final String CUMULATIVE_EXPENSES_AMOUNT = "cumulativeExpensesAmount";
        public static final String ERROR_DATE_FROM = "rangeLowerBoundKeyPrefix_errorDate";
        public static final String ERROR_DATE_TO = "errorDate";
        public static final String PRIMARY_FUND_MANAGER_PRINCIPAL_NAME = "primaryFundManagerPrincipalName";
    }

    public static class TransmitContractsAndGrantsInvoicesLookupFields {
        public static final String INVOICE_INITIATOR_PRINCIPAL_NAME = "invoiceInitiatorPrincipalName";
        public static final String INVOICE_AMOUNT = "invoiceAmount";
        public static final String INVOICE_PRINT_DATE_TO = "invoicePrintDate";
        public static final String INVOICE_PRINT_DATE_FROM = "rangeLowerBoundKeyPrefix_invoicePrintDate";
        public static final String INVOICE_TRANSMISSION_METHOD_CODE = "invoiceAddressDetails." + ArPropertyConstants.INVOICE_TRANSMISSION_METHOD_CODE;
        public static final String INITIAL_TRANSMISSION_DATE = "invoiceAddressDetails." + ArPropertyConstants.INITIAL_TRANSMISSION_DATE;
        public static final String INVOICE_TRANSMISSION_METHOD_CODE_LABEL = "Method of Invoice Transmission";
    }

    public static final String INVOICE_TRANSMISSION_METHOD_CODE = "invoiceTransmissionMethodCode";
    public static final String INITIAL_TRANSMISSION_DATE = "initialTransmissionDate";
    public static final String LOOKUP_SECTION_ID = "lookup";

    // Collection Activity Type
    public static class CollectionActivityTypeFields {
        public static final String ACTIVITY_DESC = "activityDescription";
        public static final String ACTIVITY_CODE = "activityCode";
    }

    // Dunning Campaign & Templates
    public static class DunningCampaignFields {
        public static final String DUNNING_CAMPAIGN_ID = "campaignID";
        public static final String DUNNING_TEMPLATE = "dunningTemplate";
        public static final String DUNNING_LETTER_DISTRIBUTIONS = "dunningLetterDistributions";
    }

    public static class DunningLetterDistributionFields {
        public static final String DAYS_PAST_DUE = "daysPastDue";
        public static final String DUNNING_LETTER_TEMPLATE_SENT_DATE = "dunningLetterTemplateSentDate";
        public static final String BILLING_ORGANIZATION_CODE = "billingOrganizationCode";
        public static final String PROCESSING_CHART_CODE = "processingChartCode";
    }

    public static class DunningLetterTemplateFields {
        public static final String DUNNING_LETTER_TEMPLATE_CODE = "dunningLetterTemplateCode";
    }

    public static class InvoiceTemplateFields {
        public static final String INVOICE_TEMPLATE_CODE = "invoiceTemplateCode";
    }

    // Contracts & Grants Aging Report
    public static class ContractsGrantsAgingReportFields {
        public static final String REPORT_RUN_DATE = "reportRunDate";
        public static final String PDF_SORT_PROPERTY = "award.agency.agencyNumber";
        public static final String LIST_SORT_PROPERTY = "award.agency.reportingName";
        public static final String ACCOUNT_CHART_CODE = "accountChartOfAccountsCode";
        public static final String FUND_MANAGER = "lookupFundMgrPerson.principalName";
        public static final String AWARD_DOCUMENT_NUMBER = "awardDocumentNumber";
        public static final String AWARD_END_DATE = "awardEndDate";
        public static final String MARKED_AS_FINAL = "markedAsFinal";
        public static final String INVOICE_AMT_FROM = "invoiceAmountFrom";
        public static final String INVOICE_AMT_TO = "invoiceAmountTo";
        public static final String INVOICE_DATE_FROM = "rangeLowerBoundKeyPrefix_invoiceDate";
        public static final String INVOICE_DATE_TO = "invoiceDate";
        public static final String AWARD_END_DATE_FROM = "rangeLowerBoundKeyPrefix_awardEndDate";
        public static final String AWARD_END_DATE_TO = "awardEndDate";
        public static final String CG_ACCT_RESP_ID = "contractsAndGrantsAccountResponsibilityId";
    }

    // Collection Activity Report fields
    public static class CollectionActivityReportFields {
        public static final String COLLECTOR = "principalId";
        public static final String ACTIVITY_TYPE = "activityType";
        public static final String AGENCY_NUMBER = "agencyNumber";

    }

    // CustomerInvoiceDetail Fields
    public static class CustomerInvoiceDetailFields {
        public static final String BILLING_DATE = "customerInvoiceDocument.billingDate";
        public static final String DOCUMENT_STATUS_CODE = "customerInvoiceDocument.documentHeader.financialDocumentStatusCode";
        public static final String ACCOUNTS_RECEIVABLE_CHART_CODE = "customerInvoiceDocument.accountsReceivableDocumentHeader.processingChartOfAccountCode";
        public static final String ACCOUNTS_RECEIVABLE_ORG_CODE = "customerInvoiceDocument.accountsReceivableDocumentHeader.processingOrganizationCode";
        public static final String OPEN_INVOICE_IND = "customerInvoiceDocument.openInvoiceIndicator";
        public static final String ACCOUNTS_RECEIVABLE_CUSTOMER_NAME = "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName";
        public static final String ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER = "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber";
        public static final String AMOUNT = "amount";
        public static final String INVOICE_ITEM_APPLIED_AMOUNT = "invoiceItemAppliedAmount";
        public static final String INVOICE_ITEM_DISCOUNT_LINE_NUMBER = "invoiceItemDiscountLineNumber";
        public static final String BILL_BY_CHART_OF_ACCOUNT_CODE = "customerInvoiceDocument.billByChartOfAccountCode";
        public static final String BILLED_BY_ORGANIZATION_CODE = "customerInvoiceDocument.billedByOrganizationCode";
        public static final String WRITEOFF_CUSTOMER_NUMBER = "customerInvoiceWriteoffDocument.customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "invoiceDetail.chartOfAccountsCode";
        public static final String ACCOUNT_NUMBER = "invoiceDetail.accountNumber";
    }

    public static class PredeterminedBillingScheduleFields {
        public static final String BILLS = "bills";
        public static final String ESTIMATED_AMOUNT = "estimatedAmount";
    }

    public static class BillFields {
        public static final String BILL_IDENTIFIER = "billIdentifier";
        public static final String BILL_NUMBER = "billNumber";
    }

    public static class MilestoneScheduleFields {
        public static final String MILESTONES = "milestones";
    }

    public static class MilestoneFields {
        public static final String MILESTONE_IDENTIFIER = "milestoneIdentifier";
        public static final String MILESTONE_NUMBER = "milestoneNumber";
        public static final String MILESTONE_DESCRIPTION = "milestoneDescription";
        public static final String MILESTONE_AMOUNT = "milestoneAmount";
        public static final String MILESTONE_ACTUAL_COMPLETION_DATE = "milestoneActualCompletionDate";
        public static final String MILESTONE_EXPECTED_COMPLETION_DATE = "milestoneExpectedCompletionDate";
    }

    public static class FederalFormReportFields {
        public static final String INDIRECT_EXPENSE_TYPE = "Indirect Expense Type";
        public static final String INDIRECT_EXPENSE_RATE = "Indirect Expense Rate";
        public static final String INDIRECT_EXPENSE_PERIOD_FROM = "Indirect Expense Period From";
        public static final String INDIRECT_EXPENSE_PERIOD_TO = "Indirect Expense Period To";
        public static final String INDIRECT_EXPENSE_BASE = "Indirect Expense Base";
        public static final String INDIRECT_EXPENSE_AMOUNT = "Indirect Expense Amount";
        public static final String INDIRECT_EXPENSE_FEDERAL = "Indirect Expense Federal";
        public static final String INDIRECT_EXPENSE_BASE_SUM = "Indirect Expense Base Sum";
        public static final String INDIRECT_EXPENSE_AMOUNT_SUM = "Indirect Expense Amount Sum";
        public static final String INDIRECT_EXPENSE_FEDERAL_SUM = "Indirect Expense Federal Sum";
        public static final String RECIPIENT_ORGANIZATION = "Recipient Organization";
        public static final String ZWEI = "EIN";
        public static final String FEDERAL_AGENCY = "Federal Agency";
        public static final String FEDERAL_GRANT_NUMBER = "Federal Grant Number";
        public static final String RECIPIENT_ACCOUNT_NUMBER = "Recipient Account Number";
        public static final String GRANT_PERIOD_FROM = "Grant Period From";
        public static final String GRANT_PERIOD_TO = "Grant Period To";
        public static final String CASH_RECEIPTS = "Cash Receipts";
        public static final String TOTAL_FEDERAL_FUNDS_AUTHORIZED = "Total Federal Funds Authorized";
        public static final String REPORTING_PERIOD_END_DATE = "Reporting Period End Date";
        public static final String CASH_DISBURSEMENTS = "Cash Disbursements";
        public static final String CASH_ON_HAND = "Cash on Hand";
        public static final String FEDERAL_SHARE_OF_EXPENDITURES = "Federal Share of Expenditures";
        public static final String TOTAL_FEDERAL_SHARE = "Total Federal Share";
        public static final String UNOBLIGATED_BALANCE_OF_FEDERAL_FUNDS = "Unobligated Balance of Federal Funds";
        public static final String FEDERAL_SHARE_OF_UNLIQUIDATED_OBLIGATION = "Federal Share of Unliquidated Obligations";
        public static final String TOTAL_FEDERAL_INCOME_EARNED = "Total Federal Income Earned";
        public static final String INCOME_EXPENDED_DEDUCATION_ALTERNATIVE = "Income Expended in Accordance to Deduction Alternative";
        public static final String INCOME_EXPENDED_ADDITION_ALTERNATIVE = "Income Expended in Accordance to Addition Alternative";
        public static final String UNEXPECTED_PROGRAM_INCOME = "Unexpended Program Income";
        public static final String NAME = "Name";
        public static final String TELEPHONE = "Telephone";
        public static final String EMAIL_ADDRESS = "Email Address";
        public static final String DATE_REPORT_SUBMITTED = "Date Report Submitted";
        public static final String QUARTERLY = "Quaterly";
        public static final String SEMI_ANNUAL = "Semi Annual";
        public static final String ANNUAL = "Annual";
        public static final String FINAL = "Final";
        public static final String CASH = "Cash";
        public static final String ACCRUAL = "Accrual";
        public static final String FEDERAL_CASH_DISBURSEMENT = "Federal Cash Disbursement";
        public static final String TOTAL_PAGES = "totalPages";
        public static final String PAGE_NUMBER = "pageNumber";
        public static final String TOTAL = "Total";
    };

    public static class ContractsAndGrantsBillingAwardFields {
        public static final String AWARD_ADDENDUM_NUMBER = "awardAddendumNumber";
        public static final String AWARD_ALLOCATED_UNIVERSITY_COMPUTING_SERVICES_AMOUNT = "awardAllocatedUniversityComputingServicesAmount";
        public static final String AGENCY_FUTURE_1_AMOUNT = "agencyFuture1Amount";
        public static final String AGENCY_FUTURE_2_AMOUNT = "agencyFuture2Amount";
        public static final String AGENCY_FUTURE_3_AMOUNT = "agencyFuture3Amount";
        public static final String AWARD_LAST_UPDATE_DATE = "awardLastUpdateDate";
        public static final String OLD_PROPOSAL_NUMBER = "oldProposalNumber";
        public static final String AWARD_DIRECT_COST_AMOUNT = "awardDirectCostAmount";
        public static final String AWARD_INDIRECT_COST_AMOUNT = "awardIndirectCostAmount";
        public static final String FEDERAL_FUNDED_AMOUNT = "federalFundedAmount";
        public static final String AWARD_CREATE_TIMESTAMP = "awardCreateTimestamp";
        public static final String AWARD_CLOSING_DATE = "awardClosingDate";
        public static final String PROPOSAL_AWARD_TYPE_CODE = "proposalAwardTypeCode";
        public static final String GRANT_NUMBER = "grantNumber";
        public static final String AGENCY_ANALYST_NAME = "agencyAnalystName";
        public static final String ANALYST_TELEPHONE_NUMBER = "analystTelephoneNumber";
        public static final String AWARD_PURPOSE_CODE = "awardPurposeCode";
        public static final String KIM_GROUP_NAMES = "kimGroupNames";
        public static final String ROUTING_ORG = "routingOrg";
        public static final String ROUTING_CHART = "routingChart";
        public static final String EXCLUDED_FROM_INVOICING = "excludedFromInvoicing";
        public static final String ADDITIONAL_FORMS_REQUIRED = "additionalFormsRequired";
        public static final String ADDITIONAL_FORMS_DESCRIPTION = "additionalFormsDescription";
        public static final String MIN_INVOICE_AMOUNT = "minInvoiceAmount";
        public static final String AUTO_APPROVE = "autoApprove";
        public static final String LOOKUP_PERSON_UNIVERSAL_IDENTIFIER = "lookupPersonUniversalIdentifier";
        public static final String LOOKUP_PERSON = "lookupPerson";
        public static final String USER_LOOKUP_ROLE_NAMESPACE_CODE = "userLookupRoleNamespaceCode";
        public static final String USER_LOOKUP_ROLE_NAME = "userLookupRoleName";
        public static final String FUNDING_EXPIRATION_DATE = "fundingExpirationDate";
        public static final String STOP_WORK_INDICATOR = "stopWorkIndicator";
        public static final String PRIMARY_FUND_MANAGER = "primaryFundManager";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
    }

    public static class SectionId {
        public static final String CUSTOMER_COLLECTIONS_SECTION_ID = "collectionsSection";
    }

    public static final String INVOICE_NUMBER = "invoiceNumber";
    public static final String CUSTOMER_INVOICE_DOCUMENT = "customerInvoiceDocument";
    public static final String ORGANIZATION_OPTIONS = "organizationOptions";
    public static final String AGING_REPORT_SENT_TIME = "agingReportSentTime";
    public static final String INVOICE_SEQUENCE_NUMBER ="invoiceSequenceNumber";
    public static final String SEQUENCE_NUMBER= "sequenceNumber";
    public static final String BILLING_CHART_CODE = "billingChartCode";
    public static final String PROCESSING_OR_BILLING_CHART_CODE = "processingOrBillingChartCode";
    public static final String COMPLETED = "completed";
    public static final String NOTE = "note";
    public static final String SUBMITTED_ON ="submittedOn";
    public static final String SUBMITTED_BY_PRINCIPAL_ID = "submittedByPrincipalId";

    public static final String ACCOUNT_DETAILS = "accountDetails";
    public static final String ACCOUNTS_RECEIVABLE_DOCUMENT_HEADER = "accountsReceivableDocumentHeader";
    public static final String ACTIVE_AWARD_ACCOUNTS = "activeAwardAccounts";
    public static final String ADVANCE_FLAG = "advanceFlag";
    public static final String AGE_IN_DAYS = "ageInDays";
    public static final String AGING_BUCKET = "agingBucket";
    public static final String AMOUNT_AVAILABLE_TO_DRAW = "amountAvailableToDraw";
    public static final String AMOUNT_REMAINING_TO_BILL = "amountRemainingToBill";
    public static final String AMOUNT_TO_DRAW = "amountToDraw";
    public static final String APPLIED_INDICATOR = "appliedIndicator";
    public static final String AWARD_BUDGET_AMOUNT = "awardBudgetAmount";
    public static final String BILLED = "billed";
    public static final String BILLINGS = "billings"; // er, a town in Montana?
    public static final String BUDGET_REMAINING = "budgetRemaining";
    public static final String CATEGORIES = "categories";
    public static final String CATEGORY = "CATEGORY";
    public static final String CATEGORY_CODE = "categoryCode";
    public static final String COST_CATEGORY = "costCategory";
    public static final String CLAIM_ON_CASH_BALANCE = "claimOnCashBalance";
    public static final String COLUMN_TITLE = "columnTitle";
    public static final String CUMULATIVE_EXPENDITURES = "cumulativeExpenditures";
    public static final String DIRECT_COST_INVOICE_DETAIL = "directCostInvoiceDetail";
    public static final String DISPLAY_TABBED_PAGE_URL = "displayTabbedPageUrl";
    public static final String END_DATE = "endDate";
    public static final String ESTIMATED_COST = "estimatedCost";
    public static final String FINAL_STATUS_DATE = "finalStatusDate";
    public static final String FULL_ADDRESS = "fullAddress";
    public static final String FUNDS_NOT_DRAWN = "fundsNotDrawn";
    public static final String IN_DIRECT_COST_INVOICE_DETAIL = "inDirectCostInvoiceDetail";
    public static final String INVOICE_AMOUNT = "invoiceAmount";
    public static final String INVOICE_DETAIL = "invoiceDetail";
    public static final String INVOICE_DETAIL_IDENTIFIER = "invoiceDetailIdentifier";
    public static final String INVOICE_DOCUMENT_TYPE = "invoiceDocumentType";
    public static final String INVOICE_GENERAL_DETAIL = "invoiceGeneralDetail";
    public static final String INVOICE_TYPE = "invoiceType";
    public static final String LETTER_OF_CREDIT_AMOUNT = "letterOfCreditAmount";
    public static final String LETTER_OF_CREDIT_FUND = "letterOfCreditFund";
    public static final String LETTER_OF_CREDIT_FUND_GROUP = "letterOfCreditFundGroup";
    public static final String LETTER_OF_CREDIT_FUND_CODE = "letterOfCreditFundCode";
    public static final String LETTER_OF_CREDIT_FUND_GROUP_CODE = "letterOfCreditFundGroupCode";
    public static final String LETTER_OF_CREDIT_REVIEW_CREATE_DATE = "letterOfCreditReviewCreateDate";
    public static final String HEADER_REVIEW_DETAILS = "headerReviewDetails";
    public static final String ACCOUNT_REVIEW_DETAILS = "accountReviewDetails";
    public static final String OBJECT_CODES = "objectCodes";
    public static final String OBJECT_CONSOLIDATION = "objectConsolidation";
    public static final String OBJECT_CONSOLIDATIONS = "objectConsolidations";
    public static final String OBJECT_LEVEL = "objectLevel";
    public static final String OBJECT_LEVELS = "objectLevels";
    public static final String PAYMENT_AMOUNT = "paymentAmount";
    public static final String PAYMENT_DATE = "paymentDate";
    public static final String PAYMENT_NUMBER = "paymentNumber";
    public static final String PAYMENTS = "payments";
    public static final String PRINT_PDF_URL = "printPDFUrl";
    public static final String PRINT_LABEL = "printLabel";
    public static final String RECEIVABLES = "receivables";
    public static final String REIMBURSEMENT_FLAG = "reimbursementFlag";
    public static final String REMAINING_AMOUNT = "remainingAmount";
    public static final String REVERSED_INDICATOR = "reversedIndicator";
    public static final String START_DATE = "startDate";
    public static final String SYSTEM_INFORMATION = "systemInformation";
    public static final String TOTAL_AMOUNT_BILLED_TO_DATE = "totalAmountBilledToDate";
    public static final String TOTAL_AMOUNT_DUE = "totalAmountDue";
    public static final String TOTAL_BUDGET = "totalBudget";
    public static final String TOTAL_INVOICE_DETAIL = "totalInvoiceDetail";
    public static final String TOTAL_PREVIOUSLY_BILLED = "totalPreviouslyBilled";
}
