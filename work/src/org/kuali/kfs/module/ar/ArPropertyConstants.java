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

import org.kuali.kfs.sys.KFSConstants;

/**
 * Business Object Property Constants for KFS-AR.
 */
public class ArPropertyConstants{

    // CustomerInvoiceDocument
    public static class CustomerInvoiceDocumentFields {

        public static final String DOCUMENT_TYPE = "INV";
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

        public static final String INVOICE_DOCUMENT_FINAL_BILL = "invoiceGeneralDetail.finalBill";
        public static final String CG_ACCT_RESP_ID = "sourceAccountingLines.account.contractsAndGrantsAccountResponsibilityId";
        public static final String ACCOUNT_NUMBER = "sourceAccountingLines.accountNumber";
        public static final String FINANCIAL_DOCUMENT_REF_INVOICE_NUMBER = "financialDocumentReferenceInvoiceNumber";
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
        public static final String CUSTOMER_TYPE_CODE = "customerTypeCode";
    }

    // Customer
    public static class CustomerFields {
        public static final String CUSTOMER_TAB_GENERAL_INFORMATION = "customerGeneralInformation";
        public static final String CUSTOMER_TAB_ADDRESSES = "customerAddresses";
        public static final String CUSTOMER_TAB_ADDRESSES_ADD_NEW_ADDRESS = "add.customerAddresses";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE = "customerAddressTypeCode";
        public static final String CUSTOMER_ADDRESS_IDENTIFIER = "customerAddressIdentifier";
        public static final String CUSTOMER_NOTE_IDENTIFIER = "customerNoteIdentifier";
        public static final String CUSTOMER_NUMBER = "customerNumber";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String CUSTOMER_ADDRESS_STATE_CODE = "customerStateCode";
        public static final String CUSTOMER_ADDRESS_ZIP_CODE = "customerZipCode";
        public static final String CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME = "customerAddressInternationalProvinceName";
        public static final String CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE = "customerInternationalMailCode";
        public static final String CUSTOMER_SOCIAL_SECURITY_NUMBER = "customerSocialSecurityNumberIdentifier";
        public static final String CUSTOMER_ADDRESS_END_DATE = "customerAddressEndDate";
        public static final String CUSTOMER_NOTE_TAB = "customerNotes";
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
        public static final String CUSTOMER_INVOICE_WRITEOFF_EXPLANATION = "documentHeader.explanation";
        public static final String INVOICE_WRITEOFF_AMOUNT = "invoiceWriteoffAmount";

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
        public static final String REPORT_OPTION = "reportOption";
        public static final String PROCESSING_OR_BILLING_CHART_ACCOUNT_CODE = "processingOrBillingChartOfAccountsCode";
        public static final String PROCESSING_OR_BILLING_CHART_CODE = "processingOrBillingChartCode";
        public static final String ACCOUNT_CHART_CODE = "accountChartCode";
    }

    // Tickler Report fields
    public static class TicklersReportFields {
        public static final String COLLECTOR = "principalId";
        public static final String FOLLOWUP_DATE = "followupDate";
        public static final String ACTIVITY_CODE = "activityCode";
        public static final String PROPOSAL_NUMBER = "proposalNumber";
        public static final String AGENCY_NUMBER = "agencyNumber";
        public static final String INVOICE_NUMBER = "invoiceNumber";
        public static final String COMPLETED = "completed";
    }

    // Event bo fields
    public static class EventFields {
        public static final String INVOICE_DOCUMENT_PROPOSAL_NUMBER = "invoiceDocument.proposalNumber";
        public static final String INVOICE_DOCUMENT_AGENCY_NUMBER = "invoiceDocument.award.agency.agencyNumber";
        public static final String INVOICE_DOCUMENT_OPEN_INV_IND = "invoiceDocument." + ArPropertyConstants.OPEN_INVOICE_IND;
        public static final String EVENT_CODE = "eventCode";
        public static final String EVENT_IDENTIFIER = "eventIdentifier";
        public static final String INVOICE_NUMBER = "invoiceNumber";
        public static final String FOLLOW_UP_IND = "followupInd";
        public static final String FOLLOW_UP_DATE = "followupDate";
        public static final String COMPLETED_DATE = "completedDate";
        public static final String COMPLETED = "completedInd";
        public static final String EVENT_ROUTE_STATUS = "eventRouteStatus";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String SELECTED_INVOICES = "selectedInvoiceDocumentNumberList";
    }

    public static class CollectionActivityDocumentFields {
        public static final String SELECTED_PROPOSAL_NUMBER = "selectedProposalNumber";
        public static final String SELECTED_INVOICE_DOCUMENT_NUMBER = "selectedInvoiceDocumentNumber";
        public static final String PROPOSAL_NUMBER = "proposalNumber";
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

    // SystemInformation (aka ProcessingOrg)
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
        /* Start TEM REFUND Merge */
        public static final String REFUND_DOCUMENT_NUMBER = "refundDocumentNumber";
        /* End TEM REFUND Merge */
    }

    // Suspension Category
    public static class SuspensionCategory {
        public static final String SUSPENSION_CATEGORY_CODE = "suspensionCategoryCode";
    }

    // Contracts and Grants Invoicing
    public static final String AWARD_FUND_MANAGERS = "awardFundManagers";
    public static final String PREDETERMINED_BILLING_SCHEDULE_CODE = "PDBS";
    public static final String MILESTONE_BILLING_SCHEDULE_CODE = "MS";
    public static final String WEEKLY_BILLING_SCHEDULE_CODE = "WEEKLY";
    public static final String MONTHLY_BILLING_SCHEDULE_CODE = "MON";
    public static final String QUATERLY_BILLING_SCHEDULE_CODE = "QUAR";
    public static final String SEMI_ANNUALLY_BILLING_SCHEDULE_CODE = "SEMI-ANN";
    public static final String ANNUALLY_BILLING_SCHEDULE_CODE = "ANNUALLY";
    public static final String LOC_BILLING_SCHEDULE_CODE = "LOCB";
    public static final String BILLED_AT_TERM = "AT_TERM";
    public static final String BILLING_SCHEDULE_SECTION = "Predetermined Schedule";
    public static final String INVOICE_ACCOUNT_SECTION = "Invoice Accounts";
    public static final String INCOME_ACCOUNT = "Income";
    public static final String AR_ACCOUNT = "Accounts Receivable";
    public static final String INV_AWARD = "Invoice by Award";
    public static final String INV_ACCOUNT = "Invoice by Account";
    public static final String INV_CONTRACT_CONTROL_ACCOUNT = "Invoice by Contract Control Account";
    public static final String PREFERRED_BILLING_FREQUENCY = "preferredBillingFrequency";
    public static final String AWARD_FUND_TYPE = "fundsType";
    public static final String AWARD_CONTRACT_GRANT_TYPE = "contractGrantType";
    public static final String AWARD_INVOICING_OPTIONS = "invoicingOptions";
    public static final String LOC_CRTN_TYPE = "locCreationType";
    public static final String AWARD_INVOICE_ACCOUNTS = "awardInvoiceAccounts";
    public static final String INCOME_OBJECT_TYPE = "IN";
    public static final String EXPENSE_OBJECT_TYPE = "EX";
    public static final String BUDGET_BALANCE_TYPE = "CB";
    public static final String ACTUAL_BALANCE_TYPE = "AC";
    public static final String FINAL_INVOICE_REVERSAL_ENTRIES_PROPERTY_PATH = KFSConstants.DOCUMENT_PROPERTY_NAME + ".invoiceEntries";
    public static final String OPEN_INVOICE_IND = "openInvoiceIndicator";
    public static final String DOCUMENT_STATUS_CODE = "documentHeader.financialDocumentStatusCode";
    public static final String DOCUMENT_HEADER_FINANCIAL_DOCUMENT_IN_ERROR_NUMBER = "documentHeader.financialDocumentInErrorNumber";
    public static final String CUSTOMER_NAME = "accountsReceivableDocumentHeader.customer.customerName";
    public static final String COLLECTOR = "accountsReceivableDocumentHeader.customer.customerCollector.principalId";
    public static final String CONTRACTS_GRANTS_INV_DOC_TYPE = "CGIN";

    public static final String REFERRAL_TO_COLL_DOC_TYPE = "RTCL";
    public static final String COLLECTION_ACTIVTY_DOC_TYPE = "CATD";

    public static final String COLLECTOR_HEAD = "principalId";
    public static final String COLLECTOR_PRINC_NAME = "collector.principalName";

    // CG Invoice Reports
    public static final String INVOICE_REPORT_DELIVERY_PROPERTY_PATH = KFSConstants.DOCUMENT_PROPERTY_NAME + ".invoiceReportDelivery";

    // Collection Activity Type
    public static class CollectionActivityTypeFields {
        public static final String ACTIVITY_DESC = "activityDescription";
        public static final String ACTIVITY_CODE = "activityCode";
        public static final String REFERRAL_INDICATOR = "referralIndicator";
        public static final String ACTIVE = "active";
    }

    // Referral Types
    public static final String REFER_TO_CENTRAL_COLLECTIONS = "Refer To Central Collections";
    public static final String REFER_TO_CENTRAL_COLLECTIONS_CODE = "RCC";
    public static final String REFER_TO_OUTSIDE_COLLECTIONS_AGENCY = "Refer To Outside Collections Agency";
    public static final String REFER_TO_OUTSIDE_COLLECTIONS_AGENCY_CODE = "ROCA";
    public static final String ROUTE_TO_RESEARCH_ADMIN = "Route To Research Admin";
    public static final String ROUTE_TO_RESEARCH_ADMIN_CODE = "RRA";

    // Collection Activity Types
    public static final String COLLECTION_ACTIVITY_TYPE_REFERRAL_TO_COLLECTIONS_CODE = "RTC";

    // Dunning Campaign & Templates
    public static class DunningCampaignFields {

        public static final String DUNNING_CAMPAIGN_ID = "campaignID";
        public static final String DUNNING_TEMPLATE = "dunningTemplate";
        public static final String DUNNING_LETTER_DISTRIBUTIONS = "dunningLetterDistributions";
    }

    public static class DunningLetterDistributionFields {
        public static final String DAYS_PAST_DUE = "daysPastDue";
        public static final String DUNNING_LETTER_TAMPLATE_SENT_DATE = "dunningLetterTemplateSentDate";
    }

    // Contracts And Grants Aging Report
    public static class ContractsGrantsAgingReportFields {
        public static final String PROCESSING_CHART_OF_ACCOUNT_CODE = "processingChartOfAccountCode";
        public static final String PROCESSING_ORGANIZATION_CODE = "processingOrganizationCode";
        public static final String REPORT_RUN_DATE = "reportRunDate";
        public static final String REPORT_OPTION = "reportOption";
        public static final String PROCESSING_OR_BILLING_CHART_CODE = "processingOrBillingChartCode";
        public static final String FORM_ORGANIZATION_CODE = "organizationCode";
        public static final String FORM_CHART_CODE = "billingChartCode";
        public static final String PDF_SORT_PROPERTY = "award.agency.agencyNumber";
        public static final String LIST_SORT_PROPERTY = "award.agency.reportingName";
        public static final String ACCOUNT_CHART_CODE = "accountChartOfAccountsCode";
        public static final String FUND_MANAGER = "fundManager";
        public static final String AWARD_DOCUMENT_NUMBER = "awardDocumentNumber";
        public static final String AWARD_END_DATE = "awardEndDate";
        public static final String MARKED_AS_FINAL = "markedAsFinal";
        public static final String INVOICE_AMT_FROM = "invoiceAmountFrom";
        public static final String INVOICE_AMT_TO = "invoiceAmountTo";
        public static final String INVOICE_NUMBER = "invoiceNumber";
        public static final String INVOICE_DATE_FROM = "rangeLowerBoundKeyPrefix_invoiceDate";
        public static final String INVOICE_DATE_TO = "invoiceDate";
        public static final String AWARD_END_DATE_FROM = "rangeLowerBoundKeyPrefix_awardEndDate";
        public static final String AWARD_END_DATE_TO = "awardEndDate";
        public static final String CG_ACCT_RESP_ID = "contractsAndGrantsAccountResponsibilityId";
    }

    public static class ReferralToCollectionsFields {
        // other constants
        public static final String INVOICE_DOCUMENT_NUMBER = "invoiceDocumentNumber";
        public static final String AWARD_DOCUMENT_NUMBER = "awardDocumentNumber";
        public static final String AGENCY_ADDRESS_DETAILS_AGENCY_NUMBER = "agencyAddressDetails.agencyNumber";
        public static final String ACCOUNT_DETAILS_ACCOUNT_NUMBER = "accountDetails.accountNumber";
        public static final String REFERRAL_TYPE = "referralType";
        public static final String ACCOUNTS_RECEIVABLE_CUSTOMER_NAME = "accountsReceivableDocumentHeader.customer.customerName";
        public static final String PROPOSAL_NUMBER = "referralToCollectionsDetails.proposalNumber";
        public static final String AGENCY_NUMBER = "agencyNumber";
        public static final String INVOICE_NUMBER = "referralToCollectionsDetails.invoiceNumber";
        public static final String ACCOUNT_NUMBER = "referralToCollectionsDetails.accountNumber";
    }

    // Collection Activity Report fields
    public static class CollectionActivityReportFields {
        public static final String COLLECTOR = "principalId";
        public static final String ACTIVITY_TYPE = "activityType";
        public static final String PROPOSAL_NUMBER = "proposalNumber";
        public static final String AGENCY_NUMBER = "agencyNumber";
        public static final String INVOICE_NUMBER = "invoiceNumber";
        public static final String ACCOUNT_NUMBER = "accountNumber";
    }

    // Referral To Collections Report fields
    public static class ReferralToCollectionsReportFields {
        public static final String COLLECTOR = "principalId";
        public static final String PROPOSAL_NUMBER = "proposalNumber";
        public static final String AGENCY_NUMBER = "agencyNumber";
        public static final String INVOICE_NUMBER = "invoiceNumber";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String PDF_SORT_PROPERTY = "agencyNumber";
        public static final String LIST_SORT_PROPERTY = "agencyNumber";
    }

    // Collection Status
    public static class CollectionStatusFields {
        public static final String COLLECTION_STATUS_CODE = "statusCode";
    }

    // Final Disposition
    public static class FinalDispositionFields {
        public static final String FINAL_DISPOSITION_CODE = "dispositionCode";
    }

    // Collector Hierarchy
    public static class CollectorHierarchyFields {
        public static final String COLLECTOR = "principalId";
        public static final String COLLECTOR_HEAD_ACTIVE = "collectorHead.active";
        public static final String COLLECTOR_PRINC_NAME = "collector.principalName";
        public static final String COLLECTOR_NAME = "collector.name";
        public static final String COLLECTOR_INFORMATIONS = "collectorInformations";

    }

    // Referral Type
    public static class ReferralTypeFields {
        public static final String REFERRAL_TYPE_CODE = "referralTypeCode";
        public static final String OUTSIDE_COLLECTION_AGENCY = "outsideCollectionAgency";
        public static final String OUTSIDE_COLLECTION_AGENCY_IND = "outsideCollectionAgencyInd";
        public static final String ACTIVE = "active";
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

    // ContractsAndGrantsAgencyAddress Fields
    public static class ContractsAndGrantsAgencyAddressFields {
        public static final String AGENCY_ADDRESS_IDENTIFIER = "agencyAddressIdentifier";
    }


    public static final String CUSTOMER_INVOICE_DOCUMENT = "customerInvoiceDocument";
    public static final String ORGANIZATION_OPTIONS = "organizationOptions";
    public static final String AGING_REPORT_SENT_TIME = "agingReportSentTime";

}
