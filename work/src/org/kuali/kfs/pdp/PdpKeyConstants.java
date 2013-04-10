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
package org.kuali.kfs.pdp;

/**
 * Contains error and message key constants for PDP.
 */
public class PdpKeyConstants {
    public static final String DISBURSEMENT_NUMBER_OUT_OF_RANGE_TOO_SMALL = "DisbursementNumberMaintenanceForm.endAssignedDisburseNbr.smaller";
    public static final String DISBURSEMENT_NUMBER_OUT_OF_RANGE_TOO_LARGE = "DisbursementNumberMaintenanceForm.lastAssignedDisburseNbr.outofrange";
    public static final String DISBURSEMENT_NUMBER_OUT_OF_RANGE = "DisbursementNumberMaintenanceForm.lastAssignedDisburseNbr.outofrange";

    public static final String ERROR_PAYMENT_LOAD_INVALID_CUSTOMER = "error.pdp.paymentLoad.invalidCustomer";
    public static final String ERROR_PAYMENT_LOAD_INACTIVE_CUSTOMER = "error.pdp.paymentLoad.inactiveCustomer";
    public static final String ERROR_PAYMENT_LOAD_PAYMENT_COUNT_MISMATCH = "error.pdp.paymentLoad.paymentCountMismatch";
    public static final String ERROR_PAYMENT_LOAD_PAYMENT_TOTAL_MISMATCH = "error.pdp.paymentLoad.paymentTotalMismatch";
    public static final String ERROR_PAYMENT_LOAD_DUPLICATE_BATCH = "error.pdp.paymentLoad.duplicateBatch";
    public static final String ERROR_PAYMENT_LOAD_NEGATIVE_GROUP_TOTAL = "error.pdp.paymentLoad.negativeGroupTotal";
    public static final String ERROR_PAYMENT_LOAD_MAX_NOTE_LINES = "error.pdp.paymentLoad.maxNoteLines";
    public static final String ERROR_PAYMENT_LOAD_DETAIL_TOTAL_MISMATCH = "error.pdp.paymentLoad.detailTotalMismatch";
    public static final String ERROR_PAYMENT_LOAD_INVALID_BANK_CODE = "error.pdp.paymentLoad.invalidBankCode";
    public static final String ERROR_PAYMENT_LOAD_INACTIVE_BANK_CODE = "error.pdp.paymentLoad.inactiveBankCode";
    public static final String ERROR_PAYMENT_LOAD_PAYEE_ID_REQUIRED = "error.pdp.paymentLoad.payeeIdRequired";
    public static final String ERROR_PAYMENT_LOAD_PAYEE_OWNER_CODE = "error.pdp.paymentLoad.payeeOwnerCodeRequired";
    public static final String ERROR_PAYMENT_LOAD_INVALID_ORIGIN_CODE = "error.pdp.paymentLoad.invalidOriginCode";
    public static final String ERROR_PAYMENT_LOAD_INVALID_DOC_TYPE = "error.pdp.paymentLoad.invalidDocType";
    public static final String ERROR_PAYMENT_LOAD_INVALID_PAYEE_ID_TYPE = "error.pdp.paymentLoad.invalidPayeeIdType";
    public static final String ERROR_PAYEE_LOOKUP_VENDOR_EMPLOYEE_CONFUSION = "error.pdp.payeeLookupVendorEmployeeConfusion";

    public static final String MESSAGE_BATCH_UPLOAD_TITLE_PAYMENT = "message.batchUpload.title.payment";
    public static final String MESSAGE_PAYMENT_LOAD_FILE_THRESHOLD = "message.pdp.paymentLoad.fileThreshold";
    public static final String MESSAGE_PAYMENT_LOAD_PAYDATE_OVER_30_DAYS_PAST = "message.pdp.paymentLoad.paydateOver30DaysPast";
    public static final String MESSAGE_PAYMENT_LOAD_PAYDATE_OVER_30_DAYS_OUT = "message.pdp.paymentLoad.paydateOver30DaysOut";
    public static final String MESSAGE_PAYMENT_LOAD_DETAIL_THRESHOLD = "message.pdp.paymentLoad.detailThreshold";
    public static final String MESSAGE_PAYMENT_LOAD_INVALID_ACCOUNT = "message.pdp.paymentLoad.invalidAccount";
    public static final String MESSAGE_PAYMENT_LOAD_INVALID_SUB_ACCOUNT = "message.pdp.paymentLoad.invalidSubAccount";
    public static final String MESSAGE_PAYMENT_LOAD_INVALID_OBJECT = "message.pdp.paymentLoad.invalidObject";
    public static final String MESSAGE_PAYMENT_LOAD_INVALID_SUB_OBJECT = "message.pdp.paymentLoad.invalidSubObject";
    public static final String MESSAGE_PAYMENT_LOAD_INVALID_PROJECT = "message.pdp.paymentLoad.invalidProject";
    public static final String MESSAGE_PAYMENT_EMAIL_BAD_FILE_PARSE = "message.pdp.paymentLoad.email.badFileParse";
    public static final String MESSAGE_PAYMENT_EMAIL_INVALID_CUSTOMER = "message.pdp.paymentLoad.email.invalidCustomer";
    public static final String MESSAGE_PAYMENT_EMAIL_FILE_NOT_LOADED = "message.pdp.paymentLoad.email.fileNotLoaded";
    public static final String MESSAGE_PAYMENT_EMAIL_ERROR_MESSAGES = "message.pdp.paymentLoad.email.errorMessages";
    public static final String MESSAGE_PAYMENT_EMAIL_FILE_LOADED = "message.pdp.paymentLoad.email.fileLoaded";
    public static final String MESSAGE_PAYMENT_EMAIL_WARNING_MESSAGES = "message.pdp.paymentLoad.email.warningMessages";
    public static final String MESSAGE_PAYMENT_EMAIL_FILE_THRESHOLD = "message.pdp.paymentLoad.email.fileThreshold";
    public static final String MESSAGE_PAYMENT_EMAIL_DETAIL_THRESHOLD = "message.pdp.paymentLoad.email.detailThreshold";
    public static final String MESSAGE_PAYMENT_EMAIL_FILE_TAX_LOADED = "message.pdp.paymentLoad.email.fileTaxLoaded";
    public static final String MESSAGE_PAYMENT_EMAIL_GO_TO_PDP = "message.pdp.paymentLoad.email.goToPdp";
    public static final String MESSAGE_PURAP_EXTRACT_MAX_NOTES_SUBJECT = "message.purap.extract.maxNotes.subject";
    public static final String MESSAGE_PURAP_EXTRACT_MAX_NOTES_MESSAGE = "message.purap.extract.maxNotes.Message";
    public static final String MESSAGE_PDP_ACH_SUMMARY_EMAIL_DISB_DATE = "message.pdp.achSummary.email.disbDate";
    public static final String MESSAGE_PDP_ACH_SUMMARY_EMAIL_UNIT_TOTAL = "message.pdp.achSummary.email.unitTotal";
    public static final String MESSAGE_PDP_ACH_SUMMARY_EMAIL_EXTRACT_TOTALS = "message.pdp.achSummary.email.extractTotals";
    public static final String MESSAGE_PDP_ACH_SUMMARY_EMAIL_COMPLETE = "message.pdp.achSummary.email.complete";
    public static final String MESSAGE_PDP_ACH_ADVICE_EMAIL_TOFROM = "message.pdp.achAdvice.email.toFrom";
    public static final String MESSAGE_PDP_ACH_ADVICE_EMAIL_BANKAMOUNT = "message.pdp.achAdvice.email.bankAmount";
    public static final String MESSAGE_PDP_ACH_ADVICE_EMAIL_NONOTES = "message.pdp.achAdvice.email.noNotes";
    public static final String MESSAGE_PDP_ACH_ADVICE_INVALID_EMAIL_ADDRESS = "message.pdp.achAdvice.email.invalidEmailAddress";
    public static final String MESSAGE_PDP_ACH_PAYEE_LOOKUP_NO_PAYEE_TYPE = "message.pdp.payeeLookup.noPayeeTypeSelection";

    public static final String DAILY_REPORT_SERVICE_FILE_PREFIX = "pdp.dailyReportService.dailyReportFilePrefix";
    public static final String DAILY_REPORT_SERVICE_REPORT_TITLE = "pdp.dailyReportService.reportTitle";
    public static final String DAILY_REPORT_SERVICE_TOTAL_SUBTITLE = "pdp.dailyReportService.totalSubtitle";
    public static final String DAILY_REPORT_SERVICE_TOTAL_FOR_SUBTITLE = "pdp.dailyReportService.totalForSubtitle";
    public static final String DAILY_REPORT_SERVICE_SORT_ORDER_SUBTITLE = "pdp.dailyReportService.sortOrderSubtitle";
    public static final String DAILY_REPORT_SERVICE_CUSTOMER_SUBTITLE = "pdp.dailyReportService.customerSubtitle";
    public static final String DAILY_REPORT_SERVICE_AMOUNT_OF_PAYMENTS_SUBTITLE = "pdp.dailyReportService.amountOfPaymentRecordsSubtitle";
    public static final String DAILY_REPORT_SERVICE_NUMBER_OF_PAYMENT_RECORDS_SUBTITLE = "pdp.dailyReportService.numberOfPaymentRecordsSubtitle";
    public static final String DAILY_REPORT_SERVICE_NUMBER_OF_PAYEES_SUBTITLE = "pdp.dailyReportService.numberOfPayeesSubtitle";

    public static final String EXTRACT_TRANSACTION_SERVICE_REPORT_TITLE = "pdp.extractTransactions.reportTitle";
    public static final String EXTRACT_TRANSACTION_SERVICE_REPORT_FILENAME = "pdp.extractTransactions.reportFilename";

    public static final String ERROR_ONE_BANK_PER_DISBURSEMENT_TYPE_CODE = "error.pdp.customerProfile.duplicateBankPerDisburementTypeCode";
    public static final String ERROR_PDP_CHECK_BANK_REQUIRED = "error.pdp.customerProfile.checkBank.required";
    public static final String ERROR_PDP_ACH_BANK_REQUIRED = "error.pdp.customerProfile.achBank.required";
    public static final String ERROR_CUSTOMER_PROFILE_CHART_UNIT_SUB_UNIT_NOT_UNIQUE = "error.pdp.customerProfile.chartUnitSubUnit.notUnique";
    public static final String ERROR_PDP_ACH_BANK_NOT_ALLOWED = "error.pdp.customerProfile.achBank.notAllowed";
    public static final String ERROR_PDP_CHECK_BANK_NOT_ALLOWED = "error.pdp.customerProfile.checkBank.notAllowed";

    public static class BatchConstants{
        public static class ErrorMessages{
            public static final String ERROR_BATCH_CRITERIA_NONE_ENTERED ="batchSearchForm.batchcriteria.noneEntered";
            public static final String ERROR_BATCH_CRITERIA_NO_DATE = "batchSearchForm.batchcriteria.noDate";
            public static final String ERROR_BATCH_CRITERIA_SOURCE_MISSING = "batchSearchForm.batchcriteria.sourcemissing";
            public static final String ERROR_BATCH_ID_IS_NOT_NUMERIC = "error.batch.batchId.notNumeric";
            public static final String ERROR_NOTE_EMPTY = "paymentMaintenanceForm.changeText.empty";
            public static final String ERROR_NOTE_TOO_LONG = "paymentMaintenanceForm.changeText.over250";
            public static final String ERROR_PENDING_PAYMNET_GROUP_NOT_FOUND ="error.batch.pendingPaymentGroupsNotFound";
            public static final String ERROR_NOT_ALL_PAYMENT_GROUPS_OPEN_CANNOT_CANCEL = "error.batch.notAllPaymentGroupsOpenCannotCancel";
            public static final String ERROR_NOT_ALL_PAYMENT_GROUPS_OPEN_CANNOT_HOLD = "error.batch.notAllPaymentGroupsOpenCannotHold";
            public static final String ERROR_NOT_ALL_PAYMENT_GROUPS_OPEN_CANNOT_REMOVE_HOLD = "error.batch.notAllPaymentGroupsOpenCannotRemoveHold";
        }

        public static class Messages {
            public static final String BATCH_SUCCESSFULLY_CANCELED = "message.batch.successfullyCanceled";
            public static final String BATCH_SUCCESSFULLY_HOLD = "message.batch.successfullyHold";
            public static final String HOLD_SUCCESSFULLY_REMOVED_ON_BATCH = "message.batch.successfullyRemoveHold";
        }

        public static class LinkText{
            public static final String CANCEL_BATCH = "batchLookup.cancelBatch.text";
            public static final String HOLD_BATCH = "batchLookup.holdBatch.text";
            public static final String REMOVE_BATCH_HOLD = "batchLookup.removeBatchHold.text";
        }

        public static class Confirmation{
            public static final String CANCEL_BATCH_MESSAGE = "message.batch.cancel";
            public static final String CANCEL_BATCH_QUESTION = "CancelBatch";
            public static final String HOLD_BATCH_MESSAGE = "message.batch.hold";
            public static final String HOLD_BATCH_QUESTION = "HoldBatch";
            public static final String REMOVE_HOLD_BATCH_MESSAGE = "message.batch.removeHold";
            public static final String REMOVE_HOLD_BATCH_QUESTION = "RemoveHoldBatch";
            public static final Integer NOTE_TEXT_MAX_LENGTH = 250;
        }
    }

    public static class PaymentDetail{
        public static class ErrorMessages{
            public static final String ERROR_PAYMENT_DETAIL_CRITERIA_NOT_ENTERED = "paymentDetailLookup.criteria.noneEntered";
            public static final String ERROR_PAYMENT_DETAIL_PAYEE_ID_TYPE_CODE_NULL_WITH_PAYEE_ID = "paymentDetailLookup.payeeIdTypeCd.nullWithPayeeId";
            public static final String ERROR_PAYMENT_DETAIL_PAYEE_ID_NULL_WITH_PAYEE_ID_TYPE_CODE = "paymentDetailLookup.payeeId.nullWithPayeeIdTypeCd";
            public static final String ERROR_PAYMENT_NOT_FOUND = "error.paymentDetail.pendingPaymentNotFound";
            public static final String ERROR_PAYMENT_INVALID_STATUS_TO_CANCEL = "error.paymentDetail.invalidStatusToCancelPayment";
            public static final String ERROR_DISBURSEMENT_NOT_FOUND = "error.paymentDetail.disbursementNotFound";
            public static final String ERROR_DISBURSEMENT_INVALID_TO_CANCEL = "error.paymentDetail.invalidDisbursementToCancel";
            public static final String ERROR_DISBURSEMENT_INVALID_TO_CANCEL_AND_REISSUE = "error.paymentDetail.invalidDisbursementToCancelAndReissue";
            public static final String ERROR_PAYMENT_INVALID_STATUS_TO_HOLD = "error.paymentDetail.invalidStatusToHoldPayment";
            public static final String ERROR_PAYMENT_INVALID_STATUS_TO_REMOVE_HOLD = "error.paymentDetail.invalidStatusToRemoveHold";
        }

        public static class Messages {
            public static final String PAYMENT_SUCCESSFULLY_CANCELED = "message.payment.successfullyCanceled";
            public static final String PAYMENT_SUCCESSFULLY_HOLD = "message.payment.successfullyHold";
            public static final String HOLD_SUCCESSFULLY_REMOVED_ON_PAYMENT = "message.payment.successfullyRemoveHold";
            public static final String DISBURSEMENT_SUCCESSFULLY_CANCELED = "message.disbursement.successfullyCanceled";
            public static final String DISBURSEMENT_SUCCESSFULLY_REISSUED = "message.disbursement.successfullyReissued";
            public static final String PAYMENT_SUCCESSFULLY_SET_AS_IMMEDIATE = "message.payment.successfullySetAsImmediate";
            public static final String IMMEDIATE_SUCCESSFULLY_REMOVED_ON_PAYMENT = "message.payment.immediateSuccessfullyRemoved";
        }

        public static class LinkText {
            public static final String CANCEL_PAYMENT = "paymentLookup.cancelPayment.text";
            public static final String HOLD_PAYMENT = "paymentLookup.holdPayment.text";
            public static final String REMOVE_PAYMENT_HOLD = "paymentLookup.removePaymentHold.text";
            public static final String REMOVE_HTXN_HOLD = "paymentLookup.removeHtxnHold.text";
            public static final String REMOVE_IMMEDIATE_PRINT = "paymentLookup.removeImmediatePrint.text";
            public static final String SET_IMMEDIATE_PRINT = "paymentLookup.setImmediatePrint.text";
            public static final String CANCEL_DISBURSEMENT = "paymentLookup.cancelDisbursement.text";
            public static final String REISSUE_CANCEL = "paymentLookup.reIssueCancel.text";
            public static final String REISSUE = "paymentLookup.reIssue.text";
        }

        public static class Confirmation {
            public static final String CANCEL_PAYMENT_MESSAGE = "message.paymentDetail.cancel";
            public static final String CANCEL_PAYMENT_QUESTION = "CancelPayment";
            public static final String HOLD_PAYMENT_MESSAGE = "message.paymentDetail.hold";
            public static final String HOLD_PAYMENT_QUESTION = "HoldPayment";
            public static final String REMOVE_HOLD_PAYMENT_MESSAGE = "message.paymentDetail.removeHold";
            public static final String REMOVE_HOLD_PAYMENT_QUESTION = "RemoveHoldPayment";
            public static final String CANCEL_DISBURSEMENT_MESSAGE = "message.paymentDetail.cancelDisbursement";
            public static final String CANCEL_DISBURSEMENT_QUESTION = "CancelDisbursement";
            public static final String CANCEL_REISSUE_DISBURSEMENT_MESSAGE = "message.paymentDetail.cancelReissueDisbursement";
            public static final String CANCEL_REISSUE_DISBURSEMENT_QUESTION = "CancelReissueDisbursement";
            public static final String REISSUE_DISBURSEMENT_MESSAGE = "message.paymentDetail.reissueDisbursement";
            public static final String REISSUE_DISBURSEMENT_QUESTION="ReissueDisbursement";
            public static final String CHANGE_IMMEDIATE_PAYMENT_MESSAGE = "message.paymentDetail.changeImmediate";
            public static final String CHANGE_IMMEDIATE_PAYMENT_QUESTION = "ChangeImmediatePayment";
        }
    }

    public static class FormatProcess {
        public static final String CLEAR_UNFINISHED_FORMAT_PROCESS = "formatProcess.clearUnfinishedFormat.text";
    }

    public static final String SORT_GROUP_SELECTION_PARAMETER_PREFIX = "pdp.sortGroupSelectionParameterPrefix";
    public static final String DEFAULT_SORT_GROUP_ID_PARAMETER = "pdp.defaultSortGroupIdParameter";
    public static final String DEFAULT_GROUP_NAME_OTHER = "pdp.defaultGroupNameOther";

    //PaymentMaintenanceServiceImpl email messages
    public static final String MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_1 = "message.pdp.paymentMaintenance.email.line1";
    public static final String MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_2 = "message.pdp.paymentMaintenance.email.line2";
    public static final String MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_3 = "message.pdp.paymentMaintenance.email.line3";
    public static final String MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_4 = "message.pdp.paymentMaintenance.email.line4";
    public static final String MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_BATCH_INFORMATION_HEADER = "message.pdp.paymentMaintenance.email.batchInformationHeader";

    public static final String ERROR_ACH_ACCOUNT_NOT_INACTIVABLE = "error.pdp.achAccount.accountNotInactivable";

    public static class Format {
        public static final String ERROR_PDP_FORMAT_PROCESS_ALREADY_RUNNING = "error.pdp.format.alreadyRunning";
        public static final String ERROR_PDP_NO_MATCHING_PAYMENT_FOR_FORMAT = "error.pdp.format.NoMatchingPayments";

        public static final class ErrorMessages{
            public static final String ERROR_FORMAT_BANK_MISSING = "format.bank.missing";
            public static final String ERROR_FORMAT_DISBURSEMENT_EXHAUSTED = "format.disb.exhausted";
            public static final String ERROR_FORMAT_DISBURSEMENT_MISSING = "format.disb.missing";

        }
    }

    public static class ExtractPayment {
        public static final String ACH_FILENAME = "pdp.extract.achFilename";
        public static final String CHECK_FILENAME = "pdp.extract.checkFilename";
        public static final String CHECK_CANCEL_FILENAME = "pdp.extract.checkCancelFilename";
    }

    //Research Participant Upload
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_RESEARCH_PARTICIPANT_FILE = "message.batchUpload.title.researchParticipant.file";
    public static final String MESSAGE_BATCH_UPLOAD_SUCCESSFUL = "message.batchUpload.uploadSuccessful";
    public static final String ERROR_BATCH_UPLOAD_BAD_FORMAT = "error.batchUpload.bad.format";
    public static final String ERROR_RESEARCH_PAYMENT_LOAD_INVALID_PROJECT_CODE = "error.pdp.research.paymentLoad.invalidProjectCode";
    public static final String ERROR_RESEARCH_PAYMENT_LOAD_INACTIVE_PROJECT_CODE = "error.pdp.research.paymentLoad.inactiveProjectCode";

}
