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
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.util.JSTLConstants;
import org.kuali.rice.kns.util.KualiDecimal;

public class TemConstants extends JSTLConstants {
    public static final String PRE_FILLED_DESCRIPTION = "(Description will be filled upon submit)";
    public static final String PRIMARY_DESTINATION_LOOKUPABLE = "primaryDestinationLookupable";

    public static final String PER_DIEM_LOOKUPABLE = "perDiemLookupable";
    public static final String KIM_PERSON_LOOKUPABLE = "kimPersonLookupable";
    public static final String TEM_PROFILE_LOOKUPABLE = "temProfileLookupable";
    
    public static final String PARAM_NAMESPACE = "KFS-TEM";

    public static final String CERTIFICATION_STATEMENT_ATTRIBUTE = "certificationStatement";
    public static final String CAN_CERTIFY_ATTRIBUTE = "certificationStatement";
    public static final String EMPLOYEE_TEST_ATTRIBUTE = "isEmployee";
    public static final String ENABLE_PRIMARY_DESTINATION_ATTRIBUTE = "enablePrimaryDestination";
    public static final String ENABLE_PER_DIEM_LOOKUP_LINKS_ATTRIBUTE = "enablePerDiemLookupLinks";
    public static final String REMAINING_DISTRIBUTION_ATTRIBUTE = "remainingDistribution";
    public static final String FOREIGN_CURRENCY_URL_ATTRIBUTE = "currencyUrl";
    public static final String DISPLAY_EMERGENCY_CONTACT_TAB = "displayEmergencyContactTab";
    public static final String AR_INVOICE_DOC_TYPE_NAME = "INV";
    public static final String COVERSHEET_FILENAME_FORMAT = "%s_cover_sheet.pdf";
    public static final String SHOW_REPORTS_ATTRIBUTE = "showReports";
    public static final String ALLOW_TRAVELER_ADDRESS_CHANGE_ATTRIBUTE = "allowTravelerAddressChange";
    public static final String TRAVEL_ARRANGER_TEST_ATTRIBUTE = "travelArranger";
    public static final String TRAVEL_MANAGER_TEST_ATTRIBUTE = "travelManager";
    public static final String FISCAL_OFFICER_TEST_ATTRIBUTE = "fiscalOfficer";
    public static final String DELINQUENT_TEST_ATTRIBUTE = "delinquent";

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

    public static final String REMOVE_HOLD_NOTE_PREFIX = "Note entered while removing a hold on Travel Authorization : ";
    public static final String RETURN_TO_FO_QUESTION = "ReturnToFiscalOfficer";
    public static final String RETURN_TO_FO_NOTE_PREFIX = "Note entered while returning Travel Reimbursement to fiscal officer : ";
    public static final String AMEND_NOTE_PREFIX = "Note entered while amending a Travel Authorization : ";
    public static final String AMEND_NOTE_SUFFIX = "(Previous Document Id is {0})";
    public static final String ORIGIN_CODE = "01";
    public static final String QUESTION_CONFIRMATION = "temSingleConfirmationQuestion";
    public static final String REMOVE_HOLD_TA_TEXT = "Remove the Hold on";
    public static final String HOLD_TA_TEXT = "Hold";
    public static final String AMEND_TA_TEXT = "Amend";
    public static final String CLOSE_TA_TEXT = "Close";
    public static final String RETURN_TO_FO_TEXT = "Return";

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
    public static final String SHOW_ENCUMBRANCE_ATTRIBUTE = "showIncumbrance";
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

    public static interface Report {
        public static final String TEMPLATE_CLASSPATH = "org/kuali/kfs/module/tem/report/";
        public static final String TEMPLATE_PATH_FORMAT = TEMPLATE_CLASSPATH + "%s";
        public static final String MESSAGES_CLASSPATH = TEMPLATE_CLASSPATH + "TravelExpenseReport";
        public static final String TRAVEL_REPORT_INSTITUTION_NAME = "TRAVEL_REPORT_INSTITUTION_NAME";
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
    }

    public static class TravelParameters {
        public static final String ALLOW_TRAVELER_ADDRESS_CHANGE_IND = "ALLOW_TRAVELER_ADDRESS_CHANGE_IND";
        //CLEANUP update all the parameter usage
        public static final String DOCUMENT_DTL_TYPE = "Document";
        public static final String NON_EMPLOYEE_TRAVELER_TYPE_CODES = "NON_EMPLOYEE_TRAVELER_TYPE_CODES";
        public static final String EMPLOYEE_TRAVELER_TYPE_CODES = "EMPLOYEE_TRAVELER_TYPE_CODES";
        public static final String ENABLE_PER_DIEM_CATEGORIES = "ENABLE_PER_DIEM_CATEGORIES";
        public static final String TRAVEL_DOCUMENTATION_LOCATION_CODE = "TRAVEL_DOCUMENTATION_LOCATION_CODE";
        public static final String TRAVEL_COVERSHEET_INSTRUCTIONS = "TRAVEL_COVERSHEET_INSTRUCTIONS";
        public static final String EMPLOYEE_CERTIFICATION_STATEMENT = "EMPLOYEE_CERTIFICATION_STATEMENT";
        public static final String NON_EMPLOYEE_CERTIFICATION_STATEMENT = "NON_EMPLOYEE_CERTIFICATION_STATEMENT";
        public static final String CUMULATIVE_REIMBURSABLE_AMT_WITHOUT_DIV_APPROVAL = "CUMULATIVE_REIMBURSABLE_AMT_WITHOUT_DIV_APPROVAL";
        public static final String INCIDENTALS_WITH_MEALS_ONLY_IND = "INCIDENTALS_WITH_MEALS_ONLY_IND";
        public static final String QUARTER_DAY_TIME_TABLE = "QUARTER_DAY_TIME_TABLE";
        public static final String HOSTED_MEAL_EXPENSE_TYPES = "HOSTED_MEAL_EXPENSE_TYPES";
        public static final String NUMBER_OF_TR_DELINQUENT_DAYS = "NUMBER_OF_TR_DELINQUENT_DAYS";
        public static final String EXPENSE_TYPE_FOR_AIRFARE = "EXPENSE_TYPE_FOR_AIRFARE";
        public static final String EXPENSE_TYPE_FOR_MILEAGE = "EXPENSE_TYPE_FOR_MILEAGE";
        public static final String EXPENSE_TYPE_FOR_RENTAL_CAR = "EXPENSE_TYPE_FOR_RENTAL_CAR";
        public static final String EXPENSE_TYPE_FOR_LODGING = "EXPENSE_TYPE_FOR_LODGING";
        public static final String EXPENSE_TYPE_FOR_LODGING_ALLOWANCE = "EXPENSE_TYPE_FOR_LODGING_ALLOWANCE";
        public static final String EXPENSE_TYPES_REQUIRING_SPECIAL_REQUEST_APPROVAL = "EXPENSE_TYPES_REQUIRING_SPECIAL_REQUEST_APPROVAL";
        public static final String ALWAYS_REIMBURSABLE_CARD_TYPE = "ALWAYS_REIMBURSABLE_CARD_TYPE";
        public static final String ENABLE_AMOUNT_DUE_CORP_CARD_TOTAL_LINE_IND = "ENABLE_AMOUNT_DUE_CORP_CARD_TOTAL_LINE_IND";
        public static final String ENABLE_CORP_CARD_PAYMENT_DV_IND = "ENABLE_CORP_CARD_PAYMENT_DV_IND";
        public static final String SEPARATION_OF_DUTIES_ROUTING_OPTION = "SEPARATION_OF_DUTIES_ROUTING_OPTION";
        public static final String DISABLE_IMPORTED_EXPENSE_DETAIL_IND = "DISABLE_IMPORTED_EXPENSE_DETAIL_IND";
        public static final String VALIDATION_ACCOUNTING_LINE = "VALIDATION_ACCOUNTING_LINE";
        public static final String CORP_CARD_BANK_PAYMENT_REASON_CODE = "CORP_CARD_BANK_PAYMENT_REASON_CODE";

        public static final String ALLOW_TRAVEL_OFFICE_TO_MODIFY_ALL_IND = "ALLOW_TRAVEL_OFFICE_TO_MODIFY_ALL_IND";
        public static final String INTERNATIONAL_TRIP_TYPE_CODES = "INTERNATIONAL_TRIP_TYPE_CODES";

        public static final String TEM_BARCODE_STYLE = "TEM_BARCODE_STYLE";
        public static final String UPLOAD_PARSER_INSTRUCTIONS_URL = "UPLOAD_PARSER_INSTRUCTIONS_URL";

        public static final String TRAVEL_AUTHORIZATION_DOC_TYPES_FOR_ON_CHANGE_NOTIFICATION = "TRAVEL_AUTHORIZATION_DOC_TYPES_FOR_ON_CHANGE_NOTIFICATION";
        public static final String TRAVEL_EXPENSE_DOC_TYPES_FOR_ON_CHANGE_NOTIFICATION = "TRAVEL_EXPENSE_DOC_TYPES_FOR_ON_CHANGE_NOTIFICATION";

        public static final String CAMPUS_TRAVEL_EMAIL_ADDRESS = "CAMPUS_TRAVEL_EMAIL_ADDRESS";
        public static final String ON_CHANGE_NOTIFICATION_ENABLED_IND = "ON_CHANGE_NOTIFICATION_ENABLED_IND";
        public static final String TRAVEL_DOCUMENT_NOTIFICATION_SUBJECT = "TRAVEL_DOCUMENT_NOTIFICATION_SUBJECT";
        public static final String TEM_EMAIL_SENDER_PARAM_NAME = "TEM_EMAIL_SENDER";

        public static final String VENDOR_PAYMENT_DV_REASON_CODE = "VENDOR_PAYMENT_DV_REASON_CODE";
    }

    public static class TravelAuthorizationParameters {
        public static final String PARAM_DTL_TYPE = "TravelAuthorization";
        public static final String TRAVEL_ADVANCE_PAYMENT_OBJECT_CODE = "TRAVEL_ADVANCE_PAYMENT_OBJECT_CODE";
        public static final String TRAVEL_ADVANCE_PAYMENT_CHART_CODE = "TRAVEL_ADVANCE_PAYMENT_CHART_CODE";
        public static final String TRAVEL_ADVANCE_PAYMENT_ACCOUNT_NBR = "TRAVEL_ADVANCE_PAYMENT_ACCOUNT_NBR";
        public static final String TRAVEL_ADVANCE_DV_PAYMENT_REASON_CODE = "TRAVEL_ADVANCE_DV_PAYMENT_REASON_CODE";
        // public static final String VALID_OBJECT_CODES = "VALID_OBJECT_CODES";
        public static final String ENABLE_DV_FOR_TRAVEL_ADVANCE_IND = "ENABLE_DV_FOR_TRAVEL_ADVANCE_IND";
        public static final String ENABLE_CONTACT_INFORMATION_IND = "ENABLE_CONTACT_INFORMATION_IND";
        public static final String TRAVELER_AR_CUSTOMER_TYPE = "TRAVELER_AR_CUSTOMER_TYPE";

        public static final String ENABLE_AR_INV_FOR_TRAVL_ADVANCE_IND = "ENABLE_AR_INV_FOR_TRAVL_ADVANCE_IND";
        public static final String FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE = "FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE";
        public static final String NUMBER_OF_DAYS_DUE = "NUMBER_OF_DAYS_DUE";
        public static final String TRAVEL_ADVANCE_INVOICE_ITEM_CODE = "TRAVEL_ADVANCE_INVOICE_ITEM_CODE";
        public static final String TRAVEL_ADVANCE_BILLING_ORG_CODE = "TRAVEL_ADVANCE_BILLING_ORG_CODE";
        public static final String TRAVEL_ADVANCE_BILLING_CHART_CODE = "TRAVEL_ADVANCE_BILLING_CHART_CODE";
        public static final String ENABLE_PER_DIEM_LOOKUP_LINKS_IND = "ENABLE_PER_DIEM_LOOKUP_LINKS_IND";
        public static final String ALLOW_FREE_FORMAT_PRIMARY_DESTINATION_IND = "ALLOW_FREE_FORMAT_PRIMARY_DESTINATION_IND";
        public static final String ENABLE_TA_PER_DIEM_AMOUNT_EDIT_IND = "ENABLE_TA_PER_DIEM_AMOUNT_EDIT_IND";
        public static final String TRAVEL_ADVANCES_POLICY_URL = "TRAVEL_ADVANCES_POLICY_URL";
        public static final String CASH_ADVANCE_CREDIT_CARD_TYPES = "CASH_ADVANCE_CREDIT_CARD_TYPES";
        public static final String ENABLE_CC_CASH_ADVANCE_WARNING_IND = "ENABLE_CC_CASH_ADVANCE_WARNING_IND";
        public static final String ENABLE_TRAVEL_ADVANCES_PAYMENT_METHOD_IND = "ENABLE_TRAVEL_ADVANCES_PAYMENT_METHOD_IND";
        public static final String MULTIPLE_CASH_ADVANCES_ALLOWED_IND = "MULTIPLE_CASH_ADVANCES_ALLOWED_IND";

        public static final String ENABLE_VENDOR_PAYMENT_BEFORE_TA_FINAL_APPROVAL_IND = "ENABLE_VENDOR_PAYMENT_BEFORE_TA_FINAL_APPROVAL_IND";
        public static final String HOLD_NEW_FY_ENCUMBRANCES_IND = "HOLD_NEW_FY_ENCUMBRANCES_IND";
    }

    public static class TravelReimbursementParameters {
        public static final String PARAM_DTL_TYPE = "TravelReimbursement";
        public static final String FOREIGN_CURRENCY_URL = "FOREIGN_CURRENCY_CONVERSION_URL";
        public static final String DISPLAY_ENCUMBRANCE_IND = "DISPLAY_ENCUMBRANCE_IND";
        public static final String DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND = "DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND";
        public static final String TRAVEL_PAYMENT_MEDIUM = "TRAVEL_PAYMENT_MEDIUM_TYPE_CODE";
        public static final String ALLOW_TR_WITHOUT_TA_IND = "ALLOW_TR_WITHOUT_TA_IND";
        public static final String AR_REFUND_CHART_CODE = "AR_REFUND_CHART_CODE";
        public static final String AR_REFUND_ACCOUNT_NBR = "AR_REFUND_ACCOUNT_NBR";
        public static final String AR_REFUND_OBJECT_CODE = "AR_REFUND_OBJECT_CODE";
        public static final String PER_DIEM_OBJECT_CODE = "PER_DIEM_OBJECT_CODE";
        public static final String LODGING_OBJECT_CODE = "LODGING_OBJECT_CODE";
        public static final String DEFAULT_CHART_CODE = "DEFAULT_CHART_CODE";
        public static final String LODGING_TYPE_CODES = "LODGING_TYPE_CODES";
        public static final String TRANSPORTATION_TYPE_CODES = "TRANSPORTATION_TYPE_CODES";
        public static final String APPLY_REIMBURSEMENT_AGAINST_MULTIPLE_INVOICES_IND = "APPLY_REIMBURSEMENT_AGAINST_MULTIPLE_INVOICES_IND";
        public static final String ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND = "ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND";
        public static final String REIMBURSEMENT_PERCENT_OVER_ENCUMBRANCE_AMT = "REIMBURSEMENT_PERCENT_OVER_ENCUMBRANCE_AMT";
        public static final String VALID_OBJECT_LEVELS = "VALID_OBJECT_LEVELS";
        public static final String VALID_OBJECT_CODES = "VALID_OBJECT_CODES";
        public static final String HOSTED_MEAL_EXPENSE_TYPES = "HOSTED_MEAL_EXPENSE_TYPES";
        public static final String ENABLE_TR_PER_DIEM_AMOUNT_EDIT_IND = "ENABLE_TR_PER_DIEM_AMOUNT_EDIT_IND";
        public static final String ENABLE_AUTOMATIC_TR_IND = "ENABLE_AUTOMATIC_TR_IND";
        public static final String SHOW_TA_ESTIMATE_IN_SUMMARY_REPORT_IND = "SHOW_TA_ESTIMATE_IN_SUMMARY_REPORT_IND";
        public static final String TEM_FAX_NUMBER = "TEM_FAX_NUMBER";
        public static final String ENABLE_VENDOR_PAYMENT_BEFORE_FINAL_TR_APPROVAL_IND = "ENABLE_VENDOR_PAYMENT_BEFORE_FINAL_TR_APPROVAL_IND";
        public static final String DEFAULT_REFUND_PAYMENT_REASON_CODE = "DEFAULT_REFUND_PAYMENT_REASON_CODE";
    }

    public static class TravelRelocationParameters {
        public static final String PARAM_DTL_TYPE = "TravelRelocation";
        public static final String ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND = "ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND";
        public static final String DEFAULT_CHART_CODE = "DEFAULT_CHART_CODE";
        public static final String RELOCATION_DOCUMENTATION_LOCATION_CODE = "RELOCATION_DOCUMENTATION_LOCATION_CODE";
        public static final String RELO_REIMBURSEMENT_DV_REASON_CODE = "RELO_REIMBURSEMENT_DV_REASON_CODE";
    }

    public static class TravelEntertainmentParameters {
        public static final String PARAM_DTL_TYPE = "TravelEntertainment";
        public static final String HOST_CERTIFICATION_REQUIRED_IND = "HOST_CERTIFICATION_REQUIRED_IND";
        public static final String ENT_REIMBURSEMENT_DV_REASON_CODE = "ENT_REIMBURSEMENT_DV_REASON_CODE";
        public static final String ENTERTAINMENT_DOCUMENT_LOCATION = "ENTERTAINMENT_DOCUMENT_LOCATION";
        public static final String ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND = "ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND";
    }

    public static class TravelEntertainment {
        public static final String DOCUMENT_NAME = "Entertainment Reimbursement";
    }

    public static class TemProfileParameters {
        public static final String PARAM_DTL_TYPE = "TemProfile";
        public static final String AR_CUSTOMER_TYPE_TO_TRAVELER_TYPE_CROSSWALK = "AR_CUSTOMER_TYPE_TO_TRAVELER_TYPE_CROSSWALK";
        public static final String KIM_AFFILIATION_TYPE_TO_TRAVELER_TYPE_CROSSWALK = "KIM_AFFILIATION_TYPE_TO_TRAVELER_TYPE_CROSSWALK";
        public static final String EXPORT_FILE_FORMAT = "EXPORT_FILE_FORMAT";
    }

    public static class TravelAuthorizationStatusCodeKeys {
        public static final String IN_PROCESS = "In Process";
        public static final String AWAIT_ORG = "Awaiting Organization Review";
        public static final String AWAIT_FISCAL = "Awaiting Fiscal Officer Review";
        public static final String AWAIT_FISCAL_APP = "Awaiting Fiscal Officer Approver";
        public static final String AWAIT_TRVLR = "Awaiting Traveler Review";
        public static final String AWAIT_DIV = "Awaiting Division Review";
        public static final String AWAIT_INTL = "Awaiting International Travel Review";
        public static final String AWAIT_RISK = "Awaiting Risk Management Review";
        public static final String AWAIT_SUB = "Awaiting Sub-Fund Review";
        public static final String AWAIT_AWARD = "Awaiting Award Review";
        public static final String AWAIT_SPCL = "Awaiting Special Request Review";
        public static final String AWAIT_TRVL_MGR = "Awaiting Travel Manager Review";
        public static final String REIMB_HELD = "Reimbursement On Hold";
        public static final String CANCELLED = "Cancelled";
        public static final String CLOSED = "Closed";
        public static final String DAPRVD_ORG = "Disapproved - Org Review";
        public static final String DAPRVD_FISCAL = "Disapproved - Fiscal Officer";
        public static final String DAPRVD_DIV = "Disapproved - Division";
        public static final String DAPRVD_INTL = "Disapproved - International Travel";
        public static final String DAPRVD_RISK = "Disapproved - Risk Management";
        public static final String DAPRVD_SUB = "Disapproved - Sub-Fund";
        public static final String DAPRVD_SPCL = "Disapproved - Special Request";
        public static final String DAPRVD_AWARD = "Disapproved - Award";
        public static final String DAPRVD_TRVL = "Disapproved - Travel";
        public static final String OPEN_REIMB = "Open For Reimbursement";
        public static final String PEND_AMENDMENT = "Pending Amendment";
        public static final String CHANGE_IN_PROCESS = "Change In Process";
        public static final String RETIRED_VERSION = "Retired Version";
    }

    public static final Map<String, Class> uncopyableFieldsForTravelAuthorization() {
        Map<String, Class> returnMap = new HashMap<String, Class>();
        returnMap.put(KFSPropertyConstants.DOCUMENT_NUMBER, null);
        return returnMap;
    }

    public static class TravelReimbursementStatusCodeKeys {
        public static final String AWAIT_TRAVELER = "Awaiting Traveler Review";
        public static final String AWAIT_FISCAL = "Awaiting Fiscal Officer Review";
        public static final String AWAIT_ORG = "Awaiting Organization Review";
        public static final String AWAIT_DIV = "Awaiting Division Review";
        public static final String AWAIT_INTL = "Awaiting International Travel Review";
        public static final String AWAIT_SUB = "Awaiting Sub-Fund Review";
        public static final String AWAIT_AWARD = "Awaiting Award Review";
        public static final String DEPT_APPROVED = "Department Approved";
        
        public static final String DAPRVD_TRAVELER = "Disapproved - Traveler";
        public static final String DAPRVD_FISCAL = "Disapproved - Fiscal Officer";
        public static final String DAPRVD_ORG = "Disapproved - Organization";
        public static final String DAPRVD_DIV = "Disapproved - Division";
        public static final String DAPRVD_INTL = "Disapproved - International Travel Reviewer";
        public static final String DAPRVD_SUB = "Disapproved - Sub-Fund Review";
        public static final String DAPRVD_AWARD = "Disapproved - Award Review";
        
        public static final String IN_PROCESS = "In Process";
        public static final String CANCELLED = "Cancelled";
    }

    public static class TravelRelocationStatusCodeKeys {
        public static final String AWAIT_RELO_MANAGER = "Awaiting Moving And Relocation Manager Review";
        public static final String AWAIT_FISCAL = "Awaiting Fiscal Officer Review";
        public static final String AWAIT_ORG = "Awaiting Organization Review";
        public static final String AWAIT_SUB = "Awaiting Sub-Fund Review";
        public static final String AWAIT_AWARD = "Awaiting Award Review";
        public static final String AWAIT_TAX_MANAGER = "Awaiting Tax Manager Review";
        public static final String AWAIT_EXECUTIVE = "Awaiting Executive Review";
        
        public static final String RELO_MANAGER_APPROVED = "Moving And Relocation Manager Approved";
        public static final String DAPRVD_RELO_MANAGER = "Disapproved - Moving And Relocation Manager";
        public static final String DAPRVD_FISCAL = "Disapproved - Fiscal Officer";
        public static final String DAPRVD_ORG = "Disapproved - Organization";
        public static final String DAPRVD_EXECUTIVE = "Disapproved - Executive";
        public static final String DAPRVD_TAX_MANAGER = "Disapproved - Tax Manager";
        public static final String DAPRVD_SUB = "Disapproved - Sub-Fund Review";
        public static final String DAPRVD_AWARD = "Disapproved - Award Review";

        public static final String IN_PROCESS = "In Process";
        public static final String CANCELLED = "Cancelled";
        
        public static HashMap<String, String> getDisapprovedAppDocStatusMap() {
            HashMap<String, String> disapprovedAppDocStatusMap;
            
            disapprovedAppDocStatusMap = new HashMap<String, String>();
            disapprovedAppDocStatusMap.put(AWAIT_FISCAL, DAPRVD_FISCAL);
            disapprovedAppDocStatusMap.put(AWAIT_ORG, DAPRVD_ORG);
            disapprovedAppDocStatusMap.put(AWAIT_SUB,  DAPRVD_SUB);
            disapprovedAppDocStatusMap.put(AWAIT_AWARD, DAPRVD_AWARD); 
            disapprovedAppDocStatusMap.put(AWAIT_EXECUTIVE, DAPRVD_EXECUTIVE); 
            disapprovedAppDocStatusMap.put(AWAIT_TAX_MANAGER, DAPRVD_TAX_MANAGER);
            disapprovedAppDocStatusMap.put(AWAIT_RELO_MANAGER, DAPRVD_RELO_MANAGER); 
            
            return disapprovedAppDocStatusMap;
        }
    }

    public static class EntertainmentStatusCodeKeys {
        public static final String AWAIT_FISCAL = "Awaiting Fiscal Officer Review";
        public static final String AWAIT_ORG = "Awaiting Organization Review";
        public static final String AWAIT_SPCL = "Awaiting Special Request Review";
        public static final String AWAIT_SUB = "Awaiting Sub-Fund Review";
        public static final String AWAIT_AWARD = "Awaiting Award Review";
        public static final String AWAIT_TAX_MANAGER = "Awaiting Tax Manager Review";
        public static final String AWAIT_ENT_MANAGER = "Awaiting Entertainment Manager Review";
        
        public static final String ENT_MANAGER_APPROVED = "Entertainment Manager Approved";
        public static final String DAPRVD_FISCAL = "Disapproved - Fiscal Officer";
        public static final String DAPRVD_ORG = "Disapproved - Organization";
        public static final String DAPRVD_SPCL = "Disapproved - Special Request";
        public static final String DAPRVD_SUB = "Disapproved - Sub-Fund Review";
        public static final String DAPRVD_AWARD = "Disapproved - Award Review";
        public static final String DAPRVD_TAX_MANAGER = "Disapproved - Tax Manager";
        public static final String DAPRVD_ENT_MANAGER = "Disapproved - Entertainment Manager";
        public static final String IN_PROCESS = "In Process";
        public static final String CANCELLED = "Cancelled";
        
        public static HashMap<String, String> getDisapprovedAppDocStatusMap() {
            HashMap<String, String> disapprovedAppDocStatusMap;
            
            disapprovedAppDocStatusMap = new HashMap<String, String>();
            disapprovedAppDocStatusMap.put(AWAIT_FISCAL, DAPRVD_FISCAL);
            disapprovedAppDocStatusMap.put(AWAIT_ORG, DAPRVD_ORG);
            disapprovedAppDocStatusMap.put(AWAIT_SUB,  DAPRVD_SUB);
            disapprovedAppDocStatusMap.put(AWAIT_AWARD, DAPRVD_AWARD); 
            disapprovedAppDocStatusMap.put(AWAIT_SPCL, DAPRVD_SPCL); 
            disapprovedAppDocStatusMap.put(AWAIT_TAX_MANAGER, DAPRVD_TAX_MANAGER);
            disapprovedAppDocStatusMap.put(AWAIT_ENT_MANAGER, DAPRVD_ENT_MANAGER); 
            
            return disapprovedAppDocStatusMap;
        }
    }

    public class PermissionNames {
        public static final String AMEND_TA = "Amend TA";
        public static final String HOLD_TA = "Hold TA";
        public static final String REMOVE_HOLD_TA = "Remove Hold TA";
        public static final String CANCEL_TA = "Cancel TA";
        public static final String CLOSE_TA = "Close TA";
        public static final String HIDE_BUTTONS = "Hide Buttons TA";
        public static final String RETURN_TO_FO = "Return to Fiscal Officer";
        public static final String CLOSE_RELO = "Close RELO";
        public static final String CANCEL_RELO = "Cancel RELO";
        public static final String EDIT_TAXABLE_IND = "Edit Taxable Indicator";
    }

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

    public static final String TEM_PROFILE_HOME_DEPARTMENT = "homeDepartment";
    public static final String TEM_PROFILE_HOME_DEPT_ORG_CODE = "homeDeptOrgCode";
    public static final String TEM_PROFILE_HOME_DEPT_COA_CODE = "homeDeptChartOfAccountsCode";
    
    public static final String TEM_AGENCY_DATA_SEARCH_ACCOUNT = "searchAccountNumber";
    public static final String TEM_AGENCY_DATA_SEARCH_SUB_ACCOUNT = "searchSubAccountNumber";
    public static final String TEM_AGENCY_DATA_SEARCH_CHART_CODE = "searchChartOfAccountsCode";

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
        public static final String OUT_STATE_TRIP_TYPE_CODE_PARAM_NAME = "OUT_STATE_TRIP_TYPE_CODE";
        public static final String INTERNATIONAL_TRIP_TYPE_CODE_PARAM_NAME = "INTERNATIONAL_TRIP_TYPE_CODE";
        public static final String INSTITUTION_STATE_PARAM_NAME = "INSTITUTION_STATE";
        public static final String OCONUS_MEAL_BREAKDOWN_PARAM_NAME = "OCONUS_MEAL_BREAKDOWN";
        public static final String DEFAULT_CONUS_MIE_BREAKDOWN_PARAM_NAME = "DEFAULT_CONUS_MIE_BREAKDOWN";
        public static final String REJECT_FILE_WHEN_ERROR_IND_PARAM_NAME = "REJECT_FILE_WHEN_ERROR_IND";
        public static final String PREVIOUS_PER_DIEM_DEACTIVATION_IND_PARAM_NAME = "PREVIOUS_PER_DIEM_DEACTIVATION_IND";
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

    public static final Integer DEFAULT_NOTIFICATION_DAYS = 120;
    
    public class TaxRamificationParameter{
        public static final String TAX_RAMIFICATION_NOTIFICATION_DAYS_PARAM_NAME = "TAX_RAMIFICATION_NOTIFICATION_DAYS";
        public static final String TAX_RAMIFICATION_NOTIFICATION_TEXT_PARAM_NAME = "TAX_RAMIFICATION_NOTIFICATION_TEXT";
        public static final String TAX_RAMIFICATION_NOTIFICATION_SUBJECT_PARAM_NAME = "TAX_RAMIFICATION_NOTIFICATION_SUBJECT";
        public static final String SEND_TAX_NOTIFICATION_TO_FISCAL_OFFICER_IND_PARAM_NAME = "SEND_TAX_NOTIFICATION_TO_FISCAL_OFFICER_IND";
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

    public static final String CARD_TYPE_CTS = "CTS";
    public static final String ACTUAL_EXPENSE = "OUT OF POCKET";
    public static final String ENCUMBRANCE = "ENCUMBRANCE";
    
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

    public static final String ASSIGN_ACCOUNTS_DISABLED_MESSAGE = "All amounts have been assigned accounts.";

    public static final class AgencyMatchProcessParameter {
        public static final String AGENCY_MATCH_DTL_TYPE = "AgencyMatchProcess";
        public static final String CTS_AIR_OBJECT_CODE = "CTS_AIR_OBJECT_CODE";
        public static final String CTS_RENTAL_CAR_OBJECT_CODE = "CTS_RENTAL_CAR_OBJECT_CODE";
        public static final String CTS_LODGING_OBJECT_CODE = "CTS_LODGING_OBJECT_CODE";
        public static final String AP_CLEARING_CTS_PAYMENT_CHART = "AP_CLEARING_CTS_PAYMENT_CHART";
        public static final String AP_CLEARING_CTS_PAYMENT_ACCOUNT = "AP_CLEARING_CTS_PAYMENT_ACCOUNT";
        public static final String AP_CLEARING_CTS_PAYMENT_SUB_ACCOUNT = "AP_CLEARING_CTS_PAYMENT_SUB_ACCOUNT";
        public static final String AP_CLEARING_CTS_PAYMENT_OBJECT_CODE = "AP_CLEARING_CTS_PAYMENT_OBJECT_CODE";
    }

    public static final class TEMRoleNames {
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
    }
    
    public static class ExpenseImportTypes {
        public static final String IMPORT_BY_TRIP = "TRP";
        public static final String IMPORT_BY_TRAVELLER = "TRV";
    }
    
    public static class ReconciledCodes {
        public static final String RECONCILED = "R";
        public static final String UNRECONCILED = "N";
        public static final String AUTO_RECONCILED = "A";
    }

    public static final Map<String, String> reconciledCodes() {
        Map<String, String> returnMap = new HashMap<String, String>();
        returnMap.put(ReconciledCodes.AUTO_RECONCILED, "Auto Reconciled");
        returnMap.put(ReconciledCodes.UNRECONCILED, "Unreconciled");
        returnMap.put(ReconciledCodes.RECONCILED,"Reconciled");
        return returnMap;
    }
    
    public static class TravelCustomSearchLinks {
        public static final String NEW_REIMBURSEMENT = "New&nbsp;Reimbursement";
        public static final String NEW_ENTERTAINMENT = "New&nbsp;Entertainment";
        public static final String NEW_RELOCATION = "New&nbsp;Relocation";
        public static final String PRE_TRIP_VENDOR_PAYMENT = "Pre-Trip&nbsp;Vendor&nbsp;Payment";
        public static final String VENDOR_PAYMENT = "Vendor&nbsp;Payment";
        public static final String REQUISITION = "Requisition";
        public static final String AGENCY_SITES = "Agency&nbsp;Sites";
        public static final String DV_URL = "temDV.do?methodToCall=docHandler&command=initiate&docTypeName=DV&temDocID=";
        public static final String REQ_URL = "temREQS.do?methodToCall=docHandler&command=initiate&docTypeName=REQS&temDocID=";
    }

    public static final String IMPORTED_EXPENSE_DOCUMENT = "DI";
    
    public static class TEMTripTypes {
        public static final String DOMESTIC = "DOM";
        public static final String INTERNATIONAL = "INT";
    }
    
    public static class TEMExpenseTypes {
        public static final String PER_DIEM = "perDiemService";
        public static final String ACTUAL = "actualExpenseService";
        public static final String IMPORTED_CTS = "importedCTSExpenseService";
        public static final String IMPORTED_CORP_CARD = "importedCorporateCardExpenseService";
    }

    public enum ExpenseType {
        actual(TEMExpenseTypes.ACTUAL), 
        importedCTS(TEMExpenseTypes.IMPORTED_CTS), 
        importedCorpCard(TEMExpenseTypes.IMPORTED_CORP_CARD), 
        perDiem(TEMExpenseTypes.PER_DIEM); 

        public String service;

        private ExpenseType(String service) {
            this.service= service;
        }
    }

    public enum DisburseType {
        corpCard, 
        reimbursable; 
    }
    
    public static final String CONUS = "CONUS";
    public static final String OTHER_PRIMARY_DESTINATION = "[OTHER]";
    public static final String BLANKET_IN_STATE = "BLN";
    public static final String ALL_STATES = "ALL";
    
    public static final String ALL_DOCUMENT = "ALL";

    public static final String IMPORTED_FLAG = "-IMP";

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
        TA_ON_FINAL("TA_ON_FINAL"), 
        TA_ON_CHANGE("TA_ON_CHANGE"), 
        TER_ON_FINAL("TER_ON_FINAL"), 
        TER_ON_CHANGE("TER_ON_CHANGE"), 
        NONE("");

        public String code;

        private NotificationPreference(String code) {
            this.code = code;
        }
    }
    
    public static final String STATUS_CHANGE_DTO = "statusChangeDTO";
    public static final String CAMPUS_TRAVEL_EMAIL_ADDRESS = "campusTravelEmailAddress";
    public static final String NOTIFICATION_PREFERENCE = "notificationPreference";
    
    public class ImportedExpenseParameter{
        public static final String IMPORTED_EXPENSE_NOTIFICATION_SUBJECT_PARAM_NAME = "IMPORTED_EXPENSE_NOTIFICATION_SUBJECT";
        public static final String IMPORTED_EXPENSE_NOTIFICATION_TEXT_PARAM_NAME = "IMPORTED_EXPENSE_NOTIFICATION_TEXT";
    }
    
    public static final String TRAVEL_EXPENSES_KEY = "travelExpenses";
    public static final String TRAVELER_PROFILE_KEY = "travelerProfile";
    
    public static final class DisbursementVoucherPaymentMethods {
        public static final String CHECK_ACH_PAYMENT_METHOD_CODE = "P";
        public static final String WIRE_TRANSFER_PAYMENT_METHOD_CODE = "W";
        public static final String FOREIGN_DRAFT_PAYMENT_METHOD_CODE = "F";
    }
    
    public static final class ExpenseTypeReimbursementCodes {
        public static final String ALL = "A";
        public static final String NON_REIMBURSABLE = "N";
        public static final String REIMBURSABLE = "R";
    }

}
