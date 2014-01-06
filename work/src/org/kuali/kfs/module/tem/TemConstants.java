/*
 * Copyright 2010 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants.TransactionalEditMode;

public class TemConstants {
    public static final String PRE_FILLED_DESCRIPTION = "(Description will be filled upon submit)";
    public static final String PRIMARY_DESTINATION_LOOKUPABLE = "primaryDestinationLookupable";

    public static final String PER_DIEM_LOOKUPABLE = "perDiemLookupable";
    public static final String KIM_PERSON_LOOKUPABLE = "kimPersonLookupable";
    public static final String TEM_PROFILE_LOOKUPABLE = "temProfileLookupable";
    public static final String TRAVELER_PROFILE_DOC_LOOKUPABLE = "travelerProfileDocLookupable";
    public static final String GROUP_TRAVELER_FOR_LOOKUP_LOOKUPABLE = "groupTravelerForLookupLookupable";

    public static final String NAMESPACE = "KFS-TEM";
    public static final String PARAM_NAMESPACE = NAMESPACE;

    public static final String CERTIFICATION_STATEMENT_ATTRIBUTE = "certificationStatement";
    public static final String CAN_CERTIFY_ATTRIBUTE = "certificationStatement";
    public static final String EMPLOYEE_TEST_ATTRIBUTE = "isEmployee";
    public static final String ENABLE_PRIMARY_DESTINATION_ATTRIBUTE = "enablePrimaryDestination";
    public static final String ENABLE_PER_DIEM_LOOKUP_LINKS_ATTRIBUTE = "enablePerDiemLookupLinks";
    public static final String REMAINING_DISTRIBUTION_ATTRIBUTE = "remainingDistribution";
    public static final String DISPLAY_EMERGENCY_CONTACT_TAB = "displayEmergencyContactTab";
    public static final String AR_INVOICE_DOC_TYPE_NAME = "INV";
    public static final String COVERSHEET_FILENAME_FORMAT = "%s_cover_sheet.pdf";
    public static final String SHOW_REPORTS_ATTRIBUTE = "showReports";
    public static final String ALLOW_TRAVELER_ADDRESS_CHANGE_ATTRIBUTE = "allowTravelerAddressChange";
    public static final String TRAVEL_ARRANGER_TEST_ATTRIBUTE = "travelArranger";
    public static final String TRAVEL_MANAGER_TEST_ATTRIBUTE = "travelManager";
    public static final String FISCAL_OFFICER_TEST_ATTRIBUTE = "fiscalOfficer";
    public static final String DELINQUENT_TEST_ATTRIBUTE = "delinquent";
    public static final String TEM_DOCUMENT_IDENTIFER_NOT_AVAILABLE = "Not Available";

    public static final String AMENDMENT_TA_QUESTION = "AmendmentTa";
    public static final String CLOSE_TA_QUESTION = "CloseTa";
    public static final String CONFIRM_AMENDMENT_QUESTION = "ConfirmAmendment";
    public static final String CONFIRM_CLOSE_QUESTION = "ConfirmClose";
    public static final String AMENDMENT_NOTE_PREFIX = "Note entered while amending a Travel Authorization : ";
    public static final String HOLD_TA_QUESTION = "HoldTa";
    public static final String CONFIRM_HOLD_QUESTION = "ConfirmHold";
    public static final String HOLD_NOTE_PREFIX = "Note entered while placing Travel Authorization on hold : ";
    public static final String REMOVE_HOLD_TA_QUESTION = "RemoveHoldTa";
    public static final String CONFIRM_REMOVE_HOLD_QUESTION = "ConfirmRemoveHold";
    public static final String CONFIRM_CLOSE_QUESTION_TEXT = "When you close this Travel Authorization, remaining encumbrance will be liquidated.";
    public static final String CANCEL_TA_QUESTION = "CancelTa";
    public static final String CONFIRM_CANCEL_QUESTION = "ConfirmCancel";
    public static final String CONFIRM_CANCEL_QUESTION_TEXT = "When you cancel this Travel Authorization, remaining encumbrance will be liquidated.";

    public static final String REMOVE_HOLD_NOTE_PREFIX = "Note entered while removing a hold on Travel Authorization : ";
    public static final String RETURN_TO_FO_QUESTION = "ReturnToFiscalOfficer";
    public static final String RETURN_TO_FO_NOTE_PREFIX = "Note entered while returning Travel Reimbursement to fiscal officer : ";
    public static final String AMEND_NOTE_PREFIX = "Note entered while amending a Travel Authorization : ";
    public static final String AMEND_NOTE_SUFFIX = "(Previous Document Id is {0})";
    public static final String CANCEL_NOTE_PREFIX = "Note entered while canceling a Travel Authorization : ";
    public static final String ORIGIN_CODE = "01";
    public static final String QUESTION_CONFIRMATION = "temSingleConfirmationQuestion";
    public static final String REMOVE_HOLD_TA_TEXT = "Remove the Hold on";
    public static final String HOLD_TA_TEXT = "Hold";
    public static final String AMEND_TA_TEXT = "Amend";
    public static final String CLOSE_TA_TEXT = "Close";
    public static final String RETURN_TO_FO_TEXT = "Return";
    public static final String CANCEL_TA_TEXT = "Cancel";

    public static final String NEW_TEM_PROFILE_DESCRIPTION_PREFIX = "New Traveler Profile for ";

    // //////////////////////////////////////////////////////////////////////////////////////
    // Attributes used for the Reimbursement Expenses Totals //
    // //////////////////////////////////////////////////////////////////////////////////////
    public static final String TOTAL_EXPENSES_ATTRIBUTE = "totalExpenses";
    public static final String PER_DIEM_ADJUSTMENT_ATTRIBUTE = "lessManualPerDiemAdjustment";
    public static final String NON_REIMBURSABLE_ATTRIBUTE = "nonReimbursable";
    public static final String ELIGIBLE_FOR_REIMB_ATTRIBUTE = "eligibleForReimbursement";
    public static final String TOTAL_REIMBURSABLE_ATTRIBUTE = "totalReimbursable";
    public static final String EXPENSE_LIMIT_ATTRIBUTE = "expenseLimitEditable";
    public static final String LESS_ADVANCES_ATTRIBUTE = "lessAdvances";
    public static final String SHOW_ENCUMBRANCE_ATTRIBUTE = "showEncumbrance";
    public static final String SHOW_ADVANCES_ATTRIBUTE = "showAdvances";
    public static final String REIMBURSEMENT_ATTRIBUTE = "reimbursement";
    public static final String ENCUMBRANCE_AMOUNT_ATTRIBUTE = "encumbranceAmount";
    public static final String SHOW_ACCOUNT_DISTRIBUTION_ATTRIBUTE = "accountDistribution";
    // //////////////////////////////////////////////////////////////////////////////////////
    // End of Attributes used for the Reimbursement Expenses Totals //
    // //////////////////////////////////////////////////////////////////////////////////////

    public static final String MILEAGE_TOTAL_ATTRIBUTE = "mileageTotal";
    public static final String LODGING_TOTAL_ATTRIBUTE = "lodgingTotal";
    public static final String MEALS_AND_INC_TOTAL_ATTRIBUTE = "mealsAndIncidentalsTotal";
    public static final String DAILY_TOTAL = "dailyTotal";

    // TripType's Per Diem Calc Method
    public static final String PERCENTAGE = "P";
    public static final String QUARTER = "Q";

    public static class TripIdPrefix {
        public static final String TRIP_PREFIX = "T-";
        public static final String RELOCATION_PREFIX = "R-";
        public static final String ENTERTAINMENT_PREFIX = "E-";
    }

    public static interface Report {
        public static final String TEMPLATE_CLASSPATH = "org/kuali/kfs/module/tem/report/";
        public static final String TEMPLATE_PATH_FORMAT = TEMPLATE_CLASSPATH + "%s";
        public static final String MESSAGES_CLASSPATH = TEMPLATE_CLASSPATH + "TravelExpenseReport";

    }

    public static class TravelDocTypes {
        public static final String TRAVEL_AUTHORIZATION_DOCUMENT = "TA";
        public static final String TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT = "TAC";
        public static final String TRAVEL_AUTHORIZATION_AMEND_DOCUMENT = "TAA";
        public static final String TRAVEL_REIMBURSEMENT_DOCUMENT = "TR";
        public static final String TRAVEL_RELOCATION_DOCUMENT = "RELO";
        public static final String TRAVEL_ENTERTAINMENT_DOCUMENT = "ENT";
        public static final String TRAVEL_ARRANGER_DOCUMENT = "TTA";
        public static final String TRAVEL_PROFILE_DOCUMENT = "TTP";
        public static final String TRAVEL_AUTHORIZATION_CHECK_ACH_DOCUMENT = "TACA";
        public static final String TRAVEL_AUTHORIZATION_WIRE_OR_FOREIGN_DRAFT_DOCUMENT = "TAWF";
        public static final String TRAVEL_REIMBURSEMENT_CHECK_ACH_DOCUMENT = "TRCA";
        public static final String TRAVEL_REIMBURSEMENT_WIRE_OR_FOREIGN_DRAFT_DOCUMENT = "TRWF";
        public static final String TRAVEL_REIMBURSEMENT_TRAVEL_ADVANCES_DOCUMENT = "TRTA";
        public static final String ENTERTAINMENT_CHECK_ACH_DOCUMENT = "ENCA";
        public static final String ENTERTAINMENT_WIRE_OR_FOREIGN_DRAFT_DOCUMENT = "ENWF";
        public static final String RELOCATION_CHECK_ACH_DOCUMENT = "RECA";
        public static final String RELOCATION_WIRE_OR_FOREIGN_DRAFT_DOCUMENT = "REWF";
        public static final String REIMBURSABLE_CORPORATE_CARD_CHECK_ACH_DOCUMENT = "RCCA";
        public static final String TRAVEL_CTS_CARD_DOCUMENT = "CTAP";
        public static final String TRAVEL_CORP_CARD_DOCUMENT = "CCAP";
        public static final String TEM_TRANSACTIONAL_DOCUMENT = "TT";
        public static final String TRAVEL_TRANSACTIONAL_DOCUMENT = "TRV";

        public static List<String> getAuthorizationDocTypes(){
            List<String> authorizationDocTypes = new ArrayList<String>();
            authorizationDocTypes.add(TRAVEL_AUTHORIZATION_DOCUMENT);
            authorizationDocTypes.add(TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);
            authorizationDocTypes.add(TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);
            return authorizationDocTypes;
        }

        public static List<String> getReimbursementDocTypes(){
            List<String> authorizationDocTypes = new ArrayList<String>();
            authorizationDocTypes.add(TRAVEL_REIMBURSEMENT_DOCUMENT);
            authorizationDocTypes.add(TRAVEL_RELOCATION_DOCUMENT);
            authorizationDocTypes.add(TRAVEL_ENTERTAINMENT_DOCUMENT);
            return authorizationDocTypes;
        }

    }

    public static class TravelParameters {
        public static final String TRAVELER_ADDRESS_CHANGE_IND = "TRAVELER_ADDRESS_CHANGE_IND";
        //CLEANUP update all the parameter usage
        public static final String DOCUMENT_DTL_TYPE = "Document";
        public static final String NON_EMPLOYEE_TRAVELER_TYPES = "NON_EMPLOYEE_TRAVELER_TYPES";
        public static final String EMPLOYEE_TRAVELER_TYPE_CODES = "EMPLOYEE_TRAVELER_TYPE_CODES";
        public static final String PER_DIEM_CATEGORIES = "PER_DIEM_CATEGORIES";
        public static final String VALIDATE_DAILY_PER_DIEM_AND_INCIDENTALS_IND = "VALIDATE_DAILY_PER_DIEM_AND_INCIDENTALS_IND";
        public static final String DOCUMENTATION_LOCATION_CODE = "DOCUMENTATION_LOCATION_CODE";
        public static final String TRAVEL_COVERSHEET_INSTRUCTIONS = "TRAVEL_COVERSHEET_INSTRUCTIONS";
        public static final String EMPLOYEE_CERTIFICATION_STATEMENT = "EMPLOYEE_CERTIFICATION_STATEMENT";
        public static final String NON_EMPLOYEE_CERTIFICATION_STATEMENT = "NON_EMPLOYEE_CERTIFICATION_STATEMENT";
        public static final String CUMULATIVE_REIMBURSABLE_AMOUNT_WITHOUT_DIVISION_APPROVAL = "CUMULATIVE_REIMBURSABLE_AMOUNT_WITHOUT_DIVISION_APPROVAL";
        public static final String INCIDENTALS_WITH_MEALS_IND = "INCIDENTALS_WITH_MEALS_IND";
        public static final String QUARTER_DAY_TIME_TABLE = "QUARTER_DAY_TIME_TABLE";
        public static final String NUMBER_OF_DAYS_DELINQUENT = "NUMBER_OF_DAYS_DELINQUENT";

        public static final String ALWAYS_REIMBURSABLE_CARD_TYPE = "ALWAYS_REIMBURSABLE_CARD_TYPE";
        public static final String AMOUNT_DUE_CORPORATE_CARD_TOTAL_LINE_IND = "AMOUNT_DUE_CORPORATE_CARD_TOTAL_LINE_IND";
        public static final String CORPORATE_CARD_PAYMENT_IND = "CORPORATE_CARD_PAYMENT_IND";
        public static final String SEPARATION_OF_DUTIES_ROUTING_CHOICE = "SEPARATION_OF_DUTIES_ROUTING_CHOICE";
        public static final String IMPORTED_EXPENSE_DETAIL_IND = "IMPORTED_EXPENSE_DETAIL_IND";
        public static final String ACCOUNTING_LINE_VALIDATION = "ACCOUNTING_LINE_VALIDATION";

        public static final String ENTERTAINMENT_MANAGER_TO_EDIT_ALL_IND = "ENTERTAINMENT_MANAGER_TO_EDIT_ALL_IND";
        public static final String MOVING_RELOCATION_MANAGER_TO_EDIT_ALL_IND = "MOVING_RELOCATION_MANAGER_TO_EDIT_ALL_IND";
        public static final String INTERNATIONAL_TRIP_TYPES = "INTERNATIONAL_TRIP_TYPES";

        public static final String UPLOAD_PARSER_INSTRUCTIONS_URL = "UPLOAD_PARSER_INSTRUCTIONS_URL";

        public static final String SEND_NOTIFICATION_DOCUMENT_TYPES = "SEND_NOTIFICATION_DOCUMENT_TYPES";

        public static final String TRAVEL_EMAIL_ADDRESS = "TRAVEL_EMAIL_ADDRESS";
        public static final String SEND_NOTIFICATION_ON_WORKFLOW_STATUS_CHANGE_IND = "SEND_NOTIFICATION_ON_WORKFLOW_STATUS_CHANGE_IND";
        public static final String CHANGE_NOTIFICATION_SUBJECT = "CHANGE_NOTIFICATION_SUBJECT";
        public static final String FROM_EMAIL_ADDRESS_PARAM_NAME = "FROM_EMAIL_ADDRESS";

        public static final String VENDOR_PAYMENT_REASON_CODE = "VENDOR_PAYMENT_REASON_CODE";
        public static final String INCLUDE_ARRANGER_EXPENSE_IN_IMPORTED_EXPENSE_IND = "INCLUDE_ARRANGER_EXPENSE_IN_IMPORTED_EXPENSE_IND";
        public static final String INCLUDE_TRAVELER_TYPE_IN_TRIP_ID_IND = "INCLUDE_TRAVELER_TYPE_IN_TRIP_ID_IND";

        public static final String PER_DIEM_MILEAGE_RATE_EXPENSE_TYPE_CODE = "PER_DIEM_MILEAGE_RATE_EXPENSE_TYPE_CODE";

        public static final String INTERNATIONAL_TRIP_REQUIRES_ACCOMMODATION_IND = "INTERNATIONAL_TRIP_REQUIRES_ACCOMMODATION_IND";
    }

    public static class TravelAuthorizationParameters {
        public static final String TRAVEL_ADVANCE_OBJECT_CODE = "TRAVEL_ADVANCE_OBJECT_CODE";
        public static final String TRAVEL_ADVANCE_CHART = "TRAVEL_ADVANCE_CHART";
        public static final String TRAVEL_ADVANCE_ACCOUNT = "TRAVEL_ADVANCE_ACCOUNT";
        public static final String TRAVEL_ADVANCE_PAYMENT_REASON_CODE = "TRAVEL_ADVANCE_PAYMENT_REASON_CODE";
        public static final String DISPLAY_EMERGENCY_CONTACT_IND = "DISPLAY_EMERGENCY_CONTACT_IND";
        public static final String CUSTOMER_TYPE_CODE = "CUSTOMER_TYPE_CODE";

        public static final String GENERATE_INVOICE_FOR_TRAVEL_ADVANCE_IND = "GENERATE_INVOICE_INV_FOR_TRAVL_ADVANCE_IND";
        public static final String FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE = "FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE";
        public static final String DUE_DATE_DAYS = "DUE_DATE_DAYS";
        public static final String TRAVEL_ADVANCE_INVOICE_ITEM_CODE = "TRAVEL_ADVANCE_INVOICE_ITEM_CODE";
        public static final String TRAVEL_ADVANCE_BILLING_ORGANIZATION = "TRAVEL_ADVANCE_BILLING_ORGANIZATION";
        public static final String TRAVEL_ADVANCE_BILLING_CHART = "TRAVEL_ADVANCE_BILLING_CHART";
        public static final String DISPLAY_PER_DIEM_URL_IND = "DISPLAY_PER_DIEM_URL_IND";
        public static final String OVERRIDE_PRIMARY_DESTINATION_IND = "OVERRIDE_PRIMARY_DESTINATION_IND";
        public static final String PER_DIEM_AMOUNT_EDITABLE_IND = "PER_DIEM_AMOUNT_EDITABLE_IND";
        public static final String TRAVEL_ADVANCES_POLICY_URL = "TRAVEL_ADVANCES_POLICY_URL";
        public static final String CASH_ADVANCE_CREDIT_CARD_TYPES = "CASH_ADVANCE_CREDIT_CARD_TYPES";
        public static final String CASH_ADVANCE_WARNING_IND = "CASH_ADVANCE_WARNING_IND";
        public static final String MULTIPLE_CASH_ADVANCES_ALLOWED_IND = "MULTIPLE_CASH_ADVANCES_ALLOWED_IND";

        public static final String VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND = "VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND";
        public static final String HOLD_NEW_FISCAL_YEAR_ENCUMBRANCES_IND = "HOLD_NEW_FISCAL_YEAR_ENCUMBRANCES_IND";
        public static final String DISPLAY_IMPORTED_EXPENSE_IND = "DISPLAY_IMPORTED_EXPENSE_IND";
        public static final String DUPLICATE_TRIP_DATE_RANGE_DAYS  = "DUPLICATE_TRIP_DATE_RANGE_DAYS";
    }

    public static class TravelReimbursementParameters {
        public static final String FOREIGN_CURRENCY_URL = "FOREIGN_CURRENCY_URL";
        public static final String DISPLAY_ENCUMBRANCE_IND = "DISPLAY_ENCUMBRANCE_IND";
        public static final String DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND = "DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND";
        public static final String TRAVEL_PAYMENT_MEDIUM = "TRAVEL_PAYMENT_MEDIUM_TYPE_CODE";
        public static final String PER_DIEM_OBJECT_CODE = "PER_DIEM_OBJECT_CODE";
        public static final String LODGING_OBJECT_CODE = "LODGING_OBJECT_CODE";
        public static final String DEFAULT_CHART = "DEFAULT_CHART";
        public static final String LODGING_TYPE_CODES = "LODGING_TYPE_CODES";
        public static final String TRANSPORTATION_TYPE_CODES = "TRANSPORTATION_TYPE_CODES";
        public static final String APPLY_REIMBURSEMENT_AGAINST_MULTIPLE_INVOICES_IND = "APPLY_REIMBURSEMENT_AGAINST_MULTIPLE_INVOICES_IND";
        public static final String DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND = "DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND";
        public static final String REIMBURSEMENT_PERCENT_OVER_ENCUMBRANCE_AMOUNT = "REIMBURSEMENT_PERCENT_OVER_ENCUMBRANCE_AMOUNT";
        public static final String OBJECT_LEVELS = "OBJECT_LEVELS";
        public static final String PER_DIEM_AMOUNT_EDITABLE_IND = "PER_DIEM_AMOUNT_EDITABLE_IND";
        public static final String AUTOMATIC_APPROVALS_IND = "AUTOMATIC_APPROVALS_IND";
        public static final String DISPLAY_TRAVEL_AUTHORIZATION_ESTIMATE_IN_SUMMARY_REPORT_IND = "DISPLAY_TRAVEL_AUTHORIZATION_ESTIMATE_IN_SUMMARY_REPORT_IND";
        public static final String FAX_NUMBER = "FAX_NUMBER";
        public static final String VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND = "VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND";
        public static final String PAYMENT_REASON_CODE = "PAYMENT_REASON_CODE";
        public static final String PRETRIP_REIMBURSEMENT_IND = "PRETRIP_REIMBURSEMENT_IND";
        public static final String BARCODE_STYLE = "BARCODE_STYLE";
    }

    public static class TravelRelocationParameters {
        public static final String DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND = "DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND";
        public static final String DEFAULT_CHART = "DEFAULT_CHART";
        public static final String PAYMENT_REASON_CODE = "PAYMENT_REASON_CODE";
    }

    public static class TravelEntertainmentParameters {
        public static final String HOST_CERTIFICATION_REQUIRED_IND = "HOST_CERTIFICATION_REQUIRED_IND";
        public static final String PAYMENT_REASON_CODE = "PAYMENT_REASON_CODE";
        public static final String DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND = "DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND";
    }

    public static class TravelEntertainment {
        public static final String DOCUMENT_NAME = "Entertainment Reimbursement";
    }

    public static class TemProfileParameters {
        public static final String PARAM_DTL_TYPE = "TemProfile";
        public static final String VALID_TRAVELER_TYPE_BY_CUSTOMER_TYPE = "VALID_TRAVELER_TYPE_BY_CUSTOMER_TYPE";
        public static final String VALID_KIM_TYPE_AFFILIATION_BY_TRAVER_TYPE = "VALID_KIM_TYPE_AFFILIATION_BY_TRAVER_TYPE";
        public static final String EXPORT_FILE_FORMAT = "EXPORT_FILE_FORMAT";
    }

    public static final String TRAVEL_DOC_APP_DOC_STATUS_INIT = "Initiated";

    public static class TravelStatusCodeKeys {
        public static final String IN_PROCESS = "In Process";
        public static final String CANCELLED = "Cancelled";

        public static final String AWAIT_TRVLR = "Awaiting Traveler Review";
        public static final String AWAIT_FISCAL = "Awaiting Fiscal Officer Review";
        public static final String AWAIT_ORG = "Awaiting Organization Review";
        public static final String AWAIT_DIV = "Awaiting Division Review";
        public static final String AWAIT_INTL = "Awaiting International Travel Review";
        public static final String AWAIT_SUB = "Awaiting Sub-Fund Review";
        public static final String AWAIT_AWARD = "Awaiting Award Review";
        public static final String AWAIT_SPCL = "Awaiting Special Request Review";
        public static final String AWAIT_SEP_DUTIES = "Awaiting Separation Of Duties Review";
        public static final String AWAIT_DISBURSEMENT_METHOD = "Awaiting Disbursement Method Review";

        public static final String DAPRVD_TRVLR = "Disapproved - Traveler";
        public static final String DAPRVD_FISCAL = "Disapproved - Fiscal Officer";
        public static final String DAPRVD_ORG = "Disapproved - Organization";
        public static final String DAPRVD_DIV = "Disapproved - Division";
        public static final String DAPRVD_INTL = "Disapproved - International Travel";
        public static final String DAPRVD_SUB = "Disapproved - Sub-Fund";
        public static final String DAPRVD_AWARD = "Disapproved - Award";
        public static final String DAPRVD_SPCL = "Disapproved - Special Request";
        public static final String DAPRVD_SEP_DUTIES = "Disapproved - Separation Of Duties";
        public static final String DAPRVD_DISBURSEMENT_METHOD = "Disapproved - Disbursement Method";
    }

    public static class TravelAuthorizationStatusCodeKeys extends TravelStatusCodeKeys{

        public static final String AWAIT_RISK = "Awaiting Risk Management Review";
        public static final String AWAIT_TRVL_MGR = "Awaiting Travel Manager Review";
        public static final String DAPRVD_RISK = "Disapproved - Risk Management";
        public static final String DAPRVD_TRVL = "Disapproved - Travel";

        public static final String REIMB_HELD = "Reimbursement On Hold";
        public static final String CLOSED = "Closed";
        public static final String OPEN_REIMB = "Open For Reimbursement";
        public static final String PEND_AMENDMENT = "Pending Amendment";
        public static final String CHANGE_IN_PROCESS = "Change In Process";
        public static final String RETIRED_VERSION = "Retired Version";

        public static HashMap<String, String> getDisapprovedAppDocStatusMap() {
            HashMap<String, String> disapprovedAppDocStatusMap;

            disapprovedAppDocStatusMap = new HashMap<String, String>();
            disapprovedAppDocStatusMap.put(AWAIT_TRVLR,  DAPRVD_TRVLR);
            disapprovedAppDocStatusMap.put(AWAIT_FISCAL, DAPRVD_FISCAL);
            disapprovedAppDocStatusMap.put(AWAIT_ORG, DAPRVD_ORG);
            disapprovedAppDocStatusMap.put(AWAIT_DIV, DAPRVD_DIV);
            disapprovedAppDocStatusMap.put(AWAIT_INTL, DAPRVD_INTL);
            disapprovedAppDocStatusMap.put(AWAIT_RISK, DAPRVD_RISK);
            disapprovedAppDocStatusMap.put(AWAIT_SUB,  DAPRVD_SUB);
            disapprovedAppDocStatusMap.put(AWAIT_AWARD, DAPRVD_AWARD);
            disapprovedAppDocStatusMap.put(AWAIT_SPCL, DAPRVD_SPCL);
            disapprovedAppDocStatusMap.put(AWAIT_TRVL_MGR, DAPRVD_TRVL);
            disapprovedAppDocStatusMap.put(AWAIT_SEP_DUTIES, DAPRVD_SEP_DUTIES);
            disapprovedAppDocStatusMap.put(AWAIT_DISBURSEMENT_METHOD, DAPRVD_DISBURSEMENT_METHOD);

            return disapprovedAppDocStatusMap;
        }
    }
    public static final String TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE = "A";
    public static final String TRAVEL_ADVANCE_ACCOUNTING_LINE_GROUP_NAME = "advance";
    public static final String TRAVEL_ADVANCE_CLEARING_LINE_TYPE_CODE = "B";
    public static final String TRAVEL_ADVANCE_CREDITING_LINE_TYPE_CODE = "C";

    public static final String ALL_EXPENSE_TYPE_OBJECT_CODE_TRIP_TYPE = "All";
    public static final String ALL_EXPENSE_TYPE_OBJECT_CODE_TRAVELER_TYPE = "All";

    public static final Map<String, Class> uncopyableFieldsForTravelAuthorization() {
        Map<String, Class> returnMap = new HashMap<String, Class>();
        returnMap.put(KFSPropertyConstants.DOCUMENT_NUMBER, null);
        return returnMap;
    }

    public static class TravelReimbursementStatusCodeKeys extends TravelStatusCodeKeys{

        public static final String DEPT_APPROVED = "Department Approved";
        public static final String AWAIT_TRVL_ACCOUNTING = "Awaiting Travel Accounting Review";
        public static final String AWAIT_TAX_MANAGER = "Awaiting Tax Manager Review";
        public static final String AWAIT_TRVL_MGR = "Awaiting Travel Manager Review";

        public static final String DAPRVD_TRVL_ACCOUNTING = "Disapproved - Travel Accounting";
        public static final String DAPRVD_TAX_MANAGER = "Disapproved - Tax Manager";
        public static final String DAPRVD_TRVL = "Disapproved - Travel";

        public static HashMap<String, String> getDisapprovedAppDocStatusMap() {
            HashMap<String, String> disapprovedAppDocStatusMap;

            disapprovedAppDocStatusMap = new HashMap<String, String>();
            disapprovedAppDocStatusMap.put(AWAIT_TRVLR,  DAPRVD_TRVLR);
            disapprovedAppDocStatusMap.put(AWAIT_FISCAL, DAPRVD_FISCAL);
            disapprovedAppDocStatusMap.put(AWAIT_DIV,  DAPRVD_DIV);
            disapprovedAppDocStatusMap.put(AWAIT_INTL,  DAPRVD_INTL);
            disapprovedAppDocStatusMap.put(AWAIT_SUB,  DAPRVD_SUB);
            disapprovedAppDocStatusMap.put(AWAIT_AWARD, DAPRVD_AWARD);
            disapprovedAppDocStatusMap.put(AWAIT_SPCL, DAPRVD_SPCL);
            disapprovedAppDocStatusMap.put(AWAIT_TAX_MANAGER, DAPRVD_TAX_MANAGER);
            disapprovedAppDocStatusMap.put(AWAIT_TRVL_MGR, DAPRVD_TRVL);
            disapprovedAppDocStatusMap.put(AWAIT_DISBURSEMENT_METHOD, DAPRVD_DISBURSEMENT_METHOD);
            return disapprovedAppDocStatusMap;
        }
    }

    public static class TravelRelocationStatusCodeKeys extends TravelReimbursementStatusCodeKeys{
        public static final String AWAIT_RELO_MANAGER = "Awaiting Moving And Relocation Manager Review";
        public static final String AWAIT_EXECUTIVE = "Awaiting Executive Review";

        public static final String RELO_MANAGER_APPROVED = "Moving And Relocation Manager Approved";
        public static final String DAPRVD_RELO_MANAGER = "Disapproved - Moving And Relocation Manager";
        public static final String DAPRVD_EXECUTIVE = "Disapproved - Executive";

        public static HashMap<String, String> getDisapprovedAppDocStatusMap() {
            HashMap<String, String> disapprovedAppDocStatusMap;

            disapprovedAppDocStatusMap = new HashMap<String, String>();
            disapprovedAppDocStatusMap.put(AWAIT_TRVLR,  DAPRVD_TRVLR);
            disapprovedAppDocStatusMap.put(AWAIT_FISCAL, DAPRVD_FISCAL);
            disapprovedAppDocStatusMap.put(AWAIT_ORG, DAPRVD_ORG);
            disapprovedAppDocStatusMap.put(AWAIT_SUB,  DAPRVD_SUB);
            disapprovedAppDocStatusMap.put(AWAIT_AWARD, DAPRVD_AWARD);
            disapprovedAppDocStatusMap.put(AWAIT_EXECUTIVE, DAPRVD_EXECUTIVE);
            disapprovedAppDocStatusMap.put(AWAIT_TAX_MANAGER, DAPRVD_TAX_MANAGER);
            disapprovedAppDocStatusMap.put(AWAIT_SEP_DUTIES, DAPRVD_SEP_DUTIES);
            disapprovedAppDocStatusMap.put(AWAIT_DISBURSEMENT_METHOD, DAPRVD_DISBURSEMENT_METHOD);
            disapprovedAppDocStatusMap.put(AWAIT_RELO_MANAGER, DAPRVD_RELO_MANAGER);

            return disapprovedAppDocStatusMap;
        }
    }

    public static class EntertainmentStatusCodeKeys extends TravelReimbursementStatusCodeKeys{

        public static final String AWAIT_ENT_MANAGER = "Awaiting Entertainment Manager Review";
        public static final String ENT_MANAGER_APPROVED = "Entertainment Manager Approved";
        public static final String DAPRVD_ENT_MANAGER = "Disapproved - Entertainment Manager";

        public static HashMap<String, String> getDisapprovedAppDocStatusMap() {
            HashMap<String, String> disapprovedAppDocStatusMap;

            disapprovedAppDocStatusMap = new HashMap<String, String>();
            disapprovedAppDocStatusMap.put(AWAIT_TRVLR,  DAPRVD_TRVLR);
            disapprovedAppDocStatusMap.put(AWAIT_FISCAL, DAPRVD_FISCAL);
            disapprovedAppDocStatusMap.put(AWAIT_ORG, DAPRVD_ORG);
            disapprovedAppDocStatusMap.put(AWAIT_SUB,  DAPRVD_SUB);
            disapprovedAppDocStatusMap.put(AWAIT_AWARD, DAPRVD_AWARD);
            disapprovedAppDocStatusMap.put(AWAIT_SPCL, DAPRVD_SPCL);
            disapprovedAppDocStatusMap.put(AWAIT_TAX_MANAGER, DAPRVD_TAX_MANAGER);
            disapprovedAppDocStatusMap.put(AWAIT_SEP_DUTIES, DAPRVD_SEP_DUTIES);
            disapprovedAppDocStatusMap.put(AWAIT_DISBURSEMENT_METHOD, DAPRVD_DISBURSEMENT_METHOD);
            disapprovedAppDocStatusMap.put(AWAIT_ENT_MANAGER, DAPRVD_ENT_MANAGER);

            return disapprovedAppDocStatusMap;
        }
    }

    //Permission and Template names are same in the setup
    public class Permission {
        public static final String AMEND_TA = "Amend TA";
        public static final String HOLD_TA = "Hold TA";
        public static final String REMOVE_HOLD_TA = "Remove Hold TA";
        public static final String CANCEL_TA = "Cancel TA";
        public static final String CLOSE_TA = "Close TA";
        public static final String RETURN_TO_FO = "Return to Fiscal Officer";
        public static final String EDIT_TAXABLE_IND = "Edit Taxable Indicator";
        public static final String EDIT_OWN_PROFILE = "Edit My TEM Profile";
        public static final String EDIT_ANY_PROFILE = "Edit All TEM Profiles";
        public static final String CREATE_ANY_PROFILE = "Create All TEM Profiles";

        //Non-existing Permission
        public static final String HIDE_BUTTONS = "Hide Buttons TA";
    }

    public static final String TRVL_SPECHAND_TAB_ERRORS = "TravelPaymentSpecialHandlingErrors,document.travelPayment.specialHandlingPersonName,document.travelPayment.specialHandlingCityName,document.travelPayment.specialHandlingLine1Addr,document.travelPayment.specialHandlingStateCode," + "document.travelPayment.specialHandlingLine2Addr,document.travelPayment.specialHandlingZipCode,document.travelPayment.specialHandlingCountryName";
    public static final String MAPPING_TRAVEL_PAYMENT_CLOSE = "travelPaymentClose";
    public static final String TRAVEL_AUTHORIZATION_ACTION_NAME = "temTravelAuthorization";
    public static final String TRAVEL_REIMBURESMENT_ACTION_NAME = "temTravelReimbursement";
    public static final String TRAVEL_RELOCATION_ACTION_NAME = "temTravelRelocation";
    public static final String ENTERTAINMENT_ACTION_NAME = "temTravelEntertainment";

    public static final String DATE_CHANGED_MESSAGE = "The trip begin/end dates have been changed from %s - %s to %s - %s";
    public static final String TA_CANCELLED_MESSAGE = "Travel Authorization cancelled.";

    public static final String EMP_TRAVELER_TYP_CD = "EMP";
    public static final String NONEMP_TRAVELER_TYP_CD = "NON";
    public static final String LODGING = "LODGING";
    public static final String YES = "Y";
    public static final String MILEAGE = "MILEAGE";
    public static final String PER_DIEM = "PER_DIEM";
    public static final String DOLLAR_SIGN = "$";

    public static final String TEM_PROFILE_DOCUMENT = "TTP";
    public static final String TEM_PROFILE_ARRANGER_DOCUMENT = "TTPA";

    public static final String INT_PHONE_PATTERN = "\\+?[0-9]{1}([0-9.]*|\\-|\\s)*[0-9]((\\s[x][0-9]+)*)";
    public static final String US_PHONE_PATTERN = "[0-9]{3}\\-[0-9]{3}\\-[0-9]{4}((\\s[x][0-9]+)*)";

    public static final int CUSTOM_PER_DIEM_ID = Integer.MAX_VALUE;
    public static final int CUSTOM_PRIMARY_DESTINATION_ID = CUSTOM_PER_DIEM_ID;

    public static final int QUADRANT_PERCENT_VALUE = 25;

    public static final String TEM_PROFILE_SEQ_NAME = "TEM_PROFILE_ID_SEQ";
    public static final String TEM_PROFILE_ARRANGER_SEQ_NAME = "TEM_PROFILE_ARRANGER_ID_SEQ";
    public static final String TEM_PROFILE_ACCOUNT_SEQ_NAME = "TEM_PROFILE_ACCOUNT_ID_SEQ";
    public static final String TEM_TRAVELER_DETAIL_SEQ_NAME = "TEM_TRAVELER_DTL_ID_SEQ";
    public static final String TEM_TRAVELER_DETAIL_EM_CONTACT_SEQ_NAME = "TEM_EM_CONT_ID_SEQ";
    public static final String TEM_TRAVELER_DETAIL_ACCOUNT_SEQ_NAME = "TEM_TRVLR_DTL_ACCOUNT_ID_SEQ";
    public static final String TEM_CREDIT_CARD_AGENCY_SEQ_NAME = "TEM_CREDIT_CARD_AGENCY_ID_SEQ";
    public static final String TEM_ACTUAL_EXPENSE_SEQ_NAME = "TEM_TRVL_EXP_ID_SEQ";
    public static final String TEM_CORP_CARD_PSEUDO_NUM_SEQ_NAME = "TEM_CORP_CARD_PSEUDO_NUM_SEQ";

    public static final String TEM_PROFILE_HOME_DEPARTMENT = "homeDepartment";
    public static final String TEM_PROFILE_HOME_DEPT_ORG_CODE = "homeDeptOrgCode";
    public static final String TEM_PROFILE_HOME_DEPT_COA_CODE = "homeDeptChartOfAccountsCode";

    public static final String TEM_AGENCY_DATA_SEARCH_ACCOUNT = "searchAccountNumber";
    public static final String TEM_AGENCY_DATA_SEARCH_SUB_ACCOUNT = "searchSubAccountNumber";
    public static final String TEM_AGENCY_DATA_SEARCH_CHART_CODE = "searchChartOfAccountsCode";

    public static enum AgencyAuditSection{
        airline, lodging, rentalcar;
    }

    public static final class AttachmentTypeCodes {
        public static final String ATTACHMENT_TYPE_RECEIPT = "Receipt";
        public static final String ATTACHMENT_TYPE_W_9 = "W-9";
        public static final String ATTACHMENT_TYPE_ADA = "ADA";
        public static final String ATTACHMENT_TYPE_W8_BEN = "W8-BEN";
        public static final String ATTACHMENT_TYPE_ENT_HOST_CERT = "Entertainment Host Certification";
        public static final String ATTACHMENT_TYPE_ATTENDEE_LIST = "Attendee List";
        public static final String NON_EMPLOYEE_FORM = "Non Employee Form";
    }

    public static final class Attendee {
        public static final String ATTENDEES_GROUP_LABEL_NAME = "Attendees";
    }

    public static final String TEM_ORGANIZATION_PROFILE_ARRANGER = "TEM Organization Profile Arranger";
    public static final String TEM_ASSIGNED_PROFILE_ARRANGER = "TEM Assigned Profile Arranger";
    public static final String TEM_PROFILE_ADMIN = "TEM Profile Administrator";
    public static final String TRAVEL_MANAGER = "Travel Manager";
    public static final String RISK_MANAGEMENT = "Risk Management";

    public class PerDiemParameter {
        public static final String IN_STATE_TRIP_TYPE_CODE_PARAM_NAME = "IN_STATE_TRIP_TYPE_CODE";
        public static final String OUT_STATE_TRIP_TYPE_CODE_PARAM_NAME = "OUT_OF_STATE_TRIP_TYPE_CODE";
        public static final String INTERNATIONAL_TRIP_TYPE_CODE_PARAM_NAME = "INTERNATIONAL_TRIP_TYPE_CODE";
        public static final String INSTITUTION_STATE_PARAM_NAME = "INSTITUTION_STATE";
        public static final String OCONUS_MEAL_BREAKDOWN_PARAM_NAME = "OCONUS_MEAL_BREAKDOWN";
        public static final String CONUS_MEAL_BREAKDOWN = "CONUS_MEAL_BREAKDOWN";
        public static final String REJECT_FILE_IND = "REJECT_FILE_IND";
        public static final String INACTIVATE_PREVIOUS_PER_DIEM_IND = "INACTIVATE_PREVIOUS_PER_DIEM_IND";
        public static final String BYPASS_STATE_OR_COUNTRY_CODES = "BYPASS_STATE_OR_COUNTRY_CODES";
    }

    public static final class HostedMeals {
        public static final String HOSTED_DINNER = "dinner";
        public static final String HOSTED_LUNCH = "lunch";
        public static final String HOSTED_BREAKFAST = "breakfast";
    }

    public static final class ExpenseTypes {
        public static final String HOSTED_DINNER = "HD";
        public static final String HOSTED_LUNCH = "HL";
        public static final String HOSTED_BREAKFAST = "HB";
        public static final String LODGING = "L";
        public static final String AIRFARE = "A";
        public static final String RENTAL_CAR = "R";
        public static final String PREPAID_AIRFARE = "PA";
        public static final String MILEAGE = "MM";
        public static final String OTHER = "O";
    }

    public enum MEAL_CODE {
        BREAKFAST("B"), LUNCH("L"), DINNER("D"), INCIDENTALS("I");

        public String mealCode;

        private MEAL_CODE(String mealCode) {
            this.mealCode = mealCode;
        }
    }

    public static final String PER_DIEM_INPUT_FILE_TYPE_INDENTIFIER = "perDiemInputFileType";
    public static final String PER_DIEM_XML_INPUT_FILE_TYPE_INDENTIFIER = "perDiemXmlInputFileType";
    public static final String AGENCY_DATA_XML_INPUT_FILE_TYPE_INDENTIFIER = "agencyDataXmlInputFileType";
    public static final String CREDIT_CARD_DATA_XML_INPUT_FILE_TYPE_INDENTIFIER = "creditCardDataXmlInputFileType";

    public static final String FILE_NAME_PART_DELIMITER = "_";

    public static final String DONE_FILE_SUFFIX = ".done";
    public final static String TEXT_FILE_SUFFIX = ".txt";
    public final static String XML_FILE_SUFFIX = ".xml";

    public static final String DATE_FIELD_SEPARATOR = "/";
    public static final String DATE_FORMAT_STRING = "MM/dd/yyyy";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

    public static final String CUSTOMER_TRAVLER_TYPE_CODE = "Traveler";
    public static final String CUSTOMER_PRIMARY_ADDRESS_TYPE_CODE = "P";

    public static final Integer DEFAULT_NOTIFICATION_DAYS = 120;
    public static final Integer DEFAULT_DUPLICATE_TRIP_DATE_RANGE_DAYS = 3;

    public class TaxRamificationParameter{
        public static final String NOTIFICATION_DAYS_PARAM_NAME = "NOTIFICATION_DAYS";
        public static final String NOTIFICATION_TEXT_PARAM_NAME = "NOTIFICATION_TEXT";
        public static final String NOTIFICATION_SUBJECT_PARAM_NAME = "NOTIFICATION_SUBJECT";
        public static final String SEND_FYI_TO_FISCAL_OFFICER_IND = "SEND_FYI_TO_FISCAL_OFFICER_IND";
    }


    public static final String DOCUMENT_NUMBER = "Document Number";

    // delinquent code
    public static final String DELINQUENT_STOP = "S";
    public static final String DELINQUENT_WARN = "W";

    // Separation of duties codes
    public static final String SEP_OF_DUTIES_FO = "F";
    public static final String SEP_OF_DUTIES_DR = "D";

    // Separation of duties codes
    public static final String EXPENSE_IMPORTED = "I";
    public static final String EXPENSE_ACTUAL = "A";

    public static final String TRAVEL_TYPE_CTS = "CTS";
    public static final String TRAVEL_TYPE_CORP = "CORP";
    public static final String ACTUAL_EXPENSE = "OUT OF POCKET";
    public static final String ENCUMBRANCE = "ENCUMBRANCE";
    public static final String ADVANCE = "ADVANCE";

    //Expenses label
    public static final String ENCUMBRANCE_PREFIX = "Estimated ";
    public static final String PER_DIEM_EXPENSES_LABEL = "Per Diem Expenses";
    public static final String ACTUAL_EXPENSES_LABEL = "Actual Expenses";
    public static final String GENERAL_EXPENSES_LABEL = "Expenses";

    public static final String ACCOUNTING_LINE_INDEX = "-1";
    public static final String DIST_ACCOUNTING_LINE_INDEX = "-2";

    public static final String UNSELECT_ALL_INDEX = "1";
    public static final String SELECT_ALL_INDEX = "0";

    public static final String MILEAGE_EXPENSE = "MM";

    public static final String DISBURSEMENT_VOUCHER_DOCTYPE = "DV";
    public static final String REQUISITION_DOCTYPE = "REQS";

    public static final String ASSIGN_ACCOUNTS_DISABLED_MESSAGE = "All amounts have been assigned accounts.";

    public static final class AgencyMatchProcessParameter {
        public static final String AGENCY_MATCH_DTL_TYPE = "AgencyMatchProcess";
        public static final String TRAVEL_CREDIT_CARD_AIRFARE_OBJECT_CODE = "TRAVEL_CREDIT_CARD_AIRFARE_OBJECT_CODE";
        public static final String TRAVEL_CREDIT_CARD_RENTAL_CAR_OBJECT_CODE = "TRAVEL_CREDIT_CARD_RENTAL_CAR_OBJECT_CODE";
        public static final String TRAVEL_CREDIT_CARD_LODGING_OBJECT_CODE = "TRAVEL_CREDIT_CARD_LODGING_OBJECT_CODE";
        public static final String TRAVEL_CREDIT_CARD_CLEARING_CHART = "TRAVEL_CREDIT_CARD_CLEARING_CHART";
        public static final String TRAVEL_CREDIT_CARD_CLEARING_ACCOUNT = "TRAVEL_CREDIT_CARD_CLEARING_ACCOUNT";
        public static final String TRAVEL_CREDIT_CARD_CLEARING_OBJECT_CODE = "TRAVEL_CREDIT_CARD_CLEARING_OBJECT_CODE";
    }

    public static final class TemRoleNames {
        public static final String SPECIAL_REQUEST_REVIEWER = "Special Request Reviewer";
        public static final String INTERNATIONAL_TRAVEL_REVIEWER = "International Travel Reviewer";
        public static final String RISK_MANAGEMENT = "Risk Management";
        public static final String ACCOUNTING_REVIEWER = "Accounting Reviewer";
        public static final String DIVISION_REVIEWER = "Division Reviewer";
        public static final String CONTACT_REVIEWER = "Contact Reviewer";
        public static final String ENTERTAINMENT_MANAGER = "Entertainment Manager";
        public static final String MOVING_AND_RELOCATION_MANAGER = "Moving And Relocation Manager";
        public static final String EXECUTIVE_APPROVER = "Executive Approver";
        public static final String SEPARATION_OF_DUTIES_REVIEWER = "Separation Of Duties Reviewer";
        public static final String TRAVELER = "Traveler";
        public static final String TEM_PROFILE = "TemProfile";
        public static final String TEM_PROFILE_ADMINISTRATOR = "TEM Profile Administrator";
        public static final String TEM_ORGANIZATION_PROFILE_ARRANGER = "TEM Organization Profile Arranger";
        public static final String TEM_ASSIGNED_PROFILE_ARRANGER = "TEM Assigned Profile Arranger";

    }

    public static class AgencyStagingDataErrorCodes {
        public static final String AGENCY_NO_ERROR = "OK";
        public static final String AGENCY_INVALID_TRAVELER = "TRAV";
        public static final String AGENCY_INVALID_TRIPID = "TRIP";
        public static final String AGENCY_INVALID_ACCOUNT = "ACT";
        public static final String AGENCY_INVALID_SUBACCOUNT = "SACT";
        public static final String AGENCY_INVALID_PROJECT = "PROJ";
        public static final String AGENCY_INVALID_OBJECT = "OBJ";
        public static final String AGENCY_INVALID_SUBOBJECT = "SOBJ";
        public static final String AGENCY_MOVED_TO_HISTORICAL = "HIS";
        public static final String AGENCY_INVALID_CC_AGENCY = "CCA";
        public static final String AGENCY_REQUIRED_FIELDS = "REQ";
        public static final String AGENCY_DUPLICATE_DATA = "DUP";
        public static final String AGENCY_INVALID_EXPENSE_TYPE_OBJECT_CODE = "EXP";
        public static final String AGENCY_INVALID_DI_CD = "DI";
    }

    public static class AgencyStagingDataValidation {
        public static final String AGENCY_DATA_VALIDATION_DTL = "AgencyDataValidation";
        public static final String VALIDATE_ACCOUNT = "Account";
        public static final String VALIDATE_SUBACCOUNT = "Sub-Account";
        public static final String VALIDATE_OBJECT_CODE = "Object Code";
        public static final String VALIDATE_SUBOBJECT_CODE = "Sub-Object Code";
    }

    public static class CreditCardStagingDataErrorCodes {
        public static final String CREDIT_CARD_NO_ERROR = "OK";
        public static final String CREDIT_CARD_INVALID_TRAVELER = "TRAV";
        public static final String CREDIT_CARD_INVALID_CARD = "CRDC";
        public static final String CREDIT_CARD_MOVED_TO_HISTORICAL = "HIS";
        public static final String CREDIT_CARD_INVALID_CC_AGENCY = "CCA";
        public static final String CREDIT_CARD_INVALID_EXPENSE_TYPE_CODE = "ETC";
    }

    public static class ExpenseImportTypes {
        public static final String IMPORT_BY_TRIP = "TRP";
        public static final String IMPORT_BY_TRAVELLER = "TRV";
    }

    public enum ExpenseImport {
        trip(ExpenseImportTypes.IMPORT_BY_TRIP),
        traveler(ExpenseImportTypes.IMPORT_BY_TRAVELLER);

        public String code;

        private ExpenseImport(String code) {
            this.code = code;
        }

        private static final Map<String, ExpenseImport> lookup = new LinkedHashMap<String, ExpenseImport>();

        static {
          for (ExpenseImport type : EnumSet.allOf(ExpenseImport.class)) {
            lookup.put(type.code, type);
          }
        }

        /**
         * Retrieve Expense Import by the code
         *
         * @param code
         * @return
         */
        public static ExpenseImport getExpenseImportByCode(String code){
            return lookup.get(code);
        }
    }

    public static class ReconciledCodes {
        public static final String RECONCILED = "R";
        public static final String UNRECONCILED = "N";
        public static final String CLEARED = "C";
    }

    public static final Map<String, String> reconciledCodes() {
        Map<String, String> returnMap = new HashMap<String, String>();
        returnMap.put(ReconciledCodes.UNRECONCILED, "Unreconciled");
        returnMap.put(ReconciledCodes.RECONCILED,"Reconciled");
        returnMap.put(ReconciledCodes.CLEARED,"Cleared");
        return returnMap;
    }

    public static class TravelCustomSearchLinks {
        public static final String NEW_REIMBURSEMENT = "New&nbsp;Reimbursement";
        public static final String NEW_ENTERTAINMENT = "New&nbsp;Entertainment";
        public static final String NEW_RELOCATION = "New&nbsp;Relocation";
        public static final String VENDOR_PAYMENT = "Vendor&nbsp;Payment";
        public static final String REQUISITION = "Requisition";
        public static final String AGENCY_SITES = "Agency&nbsp;Sites";
        public static final String DV_URL = "temDV.do?methodToCall=docHandler&command=initiate&docTypeName=DV&temDocID=";
    }

    public static class TemTripTypes {
        public static final String DOMESTIC = "DOM";
        public static final String INTERNATIONAL = "INT";
        public static final String IN_STATE = "IN";
        public static final String OUT_OF_STATE = "OUT";
    }

    public static class TemExpenseTypes {
        public static final String PER_DIEM = "perDiemService";
        public static final String ACTUAL = "actualExpenseService";
        public static final String IMPORTED_CTS = "importedCTSExpenseService";
        public static final String IMPORTED_CORP_CARD = "importedCorporateCardExpenseService";
    }

    public enum ExpenseType {
        actual(TemExpenseTypes.ACTUAL),
        importedCTS(TemExpenseTypes.IMPORTED_CTS),
        importedCorpCard(TemExpenseTypes.IMPORTED_CORP_CARD),
        perDiem(TemExpenseTypes.PER_DIEM);

        public String service;

        private ExpenseType(String service) {
            this.service= service;
        }
    }

    public enum PerDiemType {
        mileage("Mileage"),
        meals("Meals"),
        lodging("Lodging"),
        incidentals("Incidentals"),
        breakfast("Breakfast"),
        lunch("Lunch"),
        dinner("Dinner");

        public String label;

        private PerDiemType(String label) {
            this.label = label;
        }
    }

    public static final String CONUS = "CONUS";
    public static final String OTHER_PRIMARY_DESTINATION = "[OTHER]";
    public static final String ALL_STATES = "ALL";

    public static final String ALL_DOCUMENT = "ALL";

    public static final String IMPORTED_FLAG = "-IMP";

    public static final String TEM_IMPORTED_SYS_ORIG_CD = "TM";
    public static final String TEM_IMPORTED_GLPE_DESC = "TEM Imported Expense Re-Distribution";

    public static final String TEM_PROFILE_RELO_ENT = "Requester";
    public static final String TEM_PROFILE_TRAVEL = "Traveler";

    public static final Map<String, String> documentProfileNames() {
        Map<String, String> returnMap = new HashMap<String, String>();
        returnMap.put(TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT, TEM_PROFILE_TRAVEL);
        returnMap.put(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT, TEM_PROFILE_TRAVEL);
        returnMap.put(TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT, TEM_PROFILE_TRAVEL);
        returnMap.put(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT, TEM_PROFILE_TRAVEL);
        returnMap.put(TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT, TEM_PROFILE_RELO_ENT);
        returnMap.put(TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT, TEM_PROFILE_RELO_ENT);
        return returnMap;
    }

    public static final String INQUIRY_URL = "inquiry.do";
    public static final String VIEW = "view";

    public static final String SOURCE_ANCHOR = "sourceAnchor";
    public static final String DISTRIBUTION_ANCHOR = "distributionAnchor";
    public static final String SUMMARY_ANCHOR = "summaryAnchor";

    public static final String PRIMARY_DESTINATION_CLASS_NAME = "PrimaryDestination";

    public enum NotificationPreference {
        TA_ON_FINAL(TravelAuthorizationDocument.class, "FINAL", "TA_ON_FINAL"),
        TA_ON_CHANGE(TravelAuthorizationDocument.class, "CHANGE", "TA_ON_CHANGE"),
        TER_ON_FINAL(TemParameterConstants.TEM_DOCUMENT.class, "FINAL", "TER_ON_FINAL"),
        TER_ON_CHANGE(TemParameterConstants.TEM_DOCUMENT.class, "CHANGE", "TER_ON_CHANGE"),
        NONE(TemParameterConstants.TEM_DOCUMENT.class, "", "");

        protected String parameterEventCode;
        protected Class<?> parameterComponentClass;
        protected String label;

        private NotificationPreference(Class<?> parameterComponentClass, String parameterEventCode, String label) {
            this.parameterEventCode = parameterEventCode;
            this.parameterComponentClass = parameterComponentClass;
            this.label = label;
        }

        public String getParameterEventCode() {
            return this.parameterEventCode;
        }

        public Class<?> getParameterComponentClass() {
            return parameterComponentClass;
        }

        public String getLabel() {
            return label;
        }
    }

    public static final String STATUS_CHANGE_DTO = "statusChangeDTO";
    public static final String CAMPUS_TRAVEL_EMAIL_ADDRESS = "campusTravelEmailAddress";
    public static final String NOTIFICATION_PREFERENCE = "notificationPreference";

    public class ImportedExpenseParameter{
        public static final String NOTIFICATION_SUBJECT_PARAM_NAME = "NOTIFICATION_SUBJECT";
        public static final String NOTIFICATION_TEXT_PARAM_NAME = "NOTIFICATION_TEXT";
    }

    public static final String TRAVEL_EXPENSES_KEY = "travelExpenses";
    public static final String TRAVELER_PROFILE_KEY = "travelerProfile";

    public static final class ExpenseTypeReimbursementCodes {
        public static final String ALL = "A";
        public static final String NON_REIMBURSABLE = "N";
        public static final String REIMBURSABLE = "R";
    }

    public static class TravelEditMode extends TransactionalEditMode {
        public static final String FULL_ENTRY = "fullEntry";
        public static final String ADVANCE_PAYMENT_ENTRY = "advancePaymentEntry";
        public static final String ADVANCE_POLICY_ENTRY = "advancePolicyEntry";
        public static final String CLEAR_ADVANCE_MODE = "clearAdvanceMode";
        public static final String TRAVELER_ENTRY = "travelerEntry";
    }

    public static final String GENERATE_CREDIT_CARD_NUMBER_IND = "GENERATE_CREDIT_CARD_NUMBER_IND";
    public static final String CORPORATE_CARD_CODE = "CORPORATE_CARD_CODE";
    public static final String CENTRAL_TRAVEL_SYSTEM_CARD_CODE = "CENTRAL_TRAVEL_SYSTEM_CARD_CODE";


//    public static final String CTS_CARD_APPLICATION = "TemCTSCardApplication";
//    public static final String CORP_CARD_APPLICATION = "TemCorporateCardApplication";
    public static final String CARD_EXISTS_QUESTION = "Card Exists Question";

    public static class EditModes {
        public static final String CHECK_AMOUNT_ENTRY = "checkAmountEntry";
        public static final String ACTUAL_EXPENSE_TAXABLE_MODE = "actualExpenseTaxableEntry";
        public static final String BLANKET_TRAVEL_ENTRY = "blanketTravelEntry";
        public static final String EXPENSE_LIMIT_ENTRY = "expenseLimitEntry";
        public static final String CONVERSION_RATE_ENTRY = "conversionRateEntry";
        public static final String REQUESTER_LOOKUP_MODE = "requesterLooupMode";
    }

    public static class TravelAuthorizationActions {
        public static final String CAN_AMEND = "canAmend";
        public static final String CAN_HOLD = "canHold";
        public static final String CAN_REMOVE_HOLD = "canRemoveHold";
        public static final String CAN_CLOSE_TA = "canCloseTA";
        public static final String CAN_CANCEL_TA = "canCancelTA";
        public static final String CAN_PAY_VENDOR = "canPayVendor";
    }

    public static final String TRAVEL_PAYMENT_TAB_ERRORS = "TravelPaymentErrors,document.travelPayment.checkTotalAmount,document.travelPayment.dueDate,document.travelPayment.alienPaymentCode,document.travelPayment.payeeEmployeeCode,document.travelPayment.attachmentCode,document.travelPayment.specialHandlingCode,document.travelPayment.payeeW9CompleteCode,document.travelPayment.paymentMethodCode,document.travelPayment.paymentDocumentationLocationCode,document.travelPayment.checkStubText";
    public static final String ADVANCE_TRAVEL_PAYMENT_TAB_ERRORS = "TravelPaymentErrors,document.advanceTravelPayment.checkTotalAmount,document.advanceTravelPayment.dueDate,document.advanceTravelPayment.alienPaymentCode,document.advanceTravelPayment.payeeEmployeeCode,document.advanceTravelPayment.attachmentCode,document.advanceTravelPayment.specialHandlingCode,document.advanceTravelPayment.payeeW9CompleteCode,document.advanceTravelPayment.paymentMethodCode,document.advanceTravelPayment.paymentDocumentationLocationCode,document.travelPayment.checkStubText";
    public static final String ADVANCE_TRVL_SPECHAND_TAB_ERRORS = "TravelPaymentSpecialHandlingErrors,document.advanceTravelPayment.specialHandlingPersonName,document.advanceTravelPayment.specialHandlingCityName,document.advanceTravelPayment.specialHandlingLine1Addr,document.advanceTravelPayment.specialHandlingStateCode,document.advanceTravelPayment.specialHandlingLine2Addr,document.advanceTravelPayment.specialHandlingZipCode,document.advanceTravelPayment.specialHandlingCountryName";
    public static final String GENERAL_TRAVEL_PAYMENT_TAB_KEY = "TravelPaymentErrors";

    public enum PermissionAttributeValue {
        ADVANCE_ACCOUNTING_LINES("advanceAccountingLines");

        public final String value;

        private PermissionAttributeValue(String value) {
            this.value = value;
        }
    }

    public final static String AUTHORIZATION_PAYMENT_SOURCE_EXTRACTION_SERVICE = "travelAuthorizationsExtractService";
    public final static String REIMBURSABLE_PAYMENT_SOURCE_EXTRACTION_SERVICE = "travelReimbursementsExtractService";

    public static class TabTitles {
        public static final String EMERGENCY_CONTACT_INFORMATION_TAB_TITLE = "Emergency Contact Information";
    }

    /**
     * Categories of expense types that cause special reactions from the system, as described below
     */
    public enum ExpenseTypeMetaCategory {
        AIRFARE("A"), // forces extra airfare information to be filled on as part of an expense
        BREAKFAST("B"), // expenses with this expense type meta category cannot have a per diem breakfast on the same day
        LUNCH("J"), // expenses with this expense type meta category cannot have a per diem lunch on the same day ("J" for deJeneur which I just liked better than "U")
        DINNER("D"),  // expenses with this expense type meta category cannot have a per diem dinner on the same day
        INCIDENTALS("I"),
        LODGING("L"),
        LODGING_ALLOWANCE("W"),
        MILEAGE("M"), // changes the expense type entry to accept mileage rate and miles driven instead of a lump amount
        RENTAL_CAR("R");

        private String code;

        ExpenseTypeMetaCategory(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        public String getName() {
            return WordUtils.capitalizeFully(StringUtils.replace(this.toString(), "_", " "));
        }

        /**
         * Lookup ExpenseTypeMetaCategory value based on code
         * @param c the code to look up
         * @return the matching ExpenseTypeMetaCategory, or null if the code does not match an existing category
         */
        public static ExpenseTypeMetaCategory forCode(String c) {
            for (ExpenseTypeMetaCategory category : ExpenseTypeMetaCategory.values()) {
                if (StringUtils.equals(category.getCode(), c)) {
                    return category;
                }
            }
            return null;
        }
    }

    /**
     * Enumeration which represents the types of group travelers
     */
    public enum GroupTravelerType {
        EMPLOYEE("EMP"),
        STUDENT("STU"),
        CUSTOMER("CST"),
        OTHER("OTH");

        private String code;

        GroupTravelerType(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        public String getName() {
            return StringUtils.capitalize(this.toString().toLowerCase());
        }

        /**
        * Lookup GroupTravelerType value based on code
        * @param c the code to look up
        * @return the GroupTravelerType, or null if the code does not match an existing traveler type
        */
       public static GroupTravelerType forCode(String c) {
           for (GroupTravelerType travelerType : GroupTravelerType.values()) {
               if (StringUtils.equals(travelerType.getCode(), c)) {
                   return travelerType;
               }
           }
           return null;
       }
    }
}
