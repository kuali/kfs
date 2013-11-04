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
package org.kuali.kfs.module.tem;

import java.util.HashMap;
import java.util.Map;

/**
 * Travel and Entertainment module constants for business object properties
 *
 */
public class TemPropertyConstants {
    public static final String TRAVELER_PROFILE_LOOKUPABLE = "travelerProfileLookupable";
    public static final String NEW_ATTENDEE_LINE = "newAttendeeLine";
    public static final String NEW_EMERGENCY_CONTACT_LINE = "newEmergencyContactLine";
    public static final String NEW_ACCOUNT_LINE = "newAccountLine";
    public static final String NEW_TRAVEL_ADVANCE_LINE = "newTravelAdvanceLine";
    public static final String NEW_SOURCE_ACCTG_LINE = "newSourceLine";
    public static final String TRIP_INFO_UPDATE_TRIP_DTL = "tripInfoUpdateTripDetails";
    public static final String PER_DIEM = "perDiem";
    public static final String PER_DIEM_EXP = "perDiemExpenses";
    public static final String EM_CONTACT = "emergencyContact";
    public static final String EM_CONTACTS = "emergencyContacts";
    public static final String TRIP_OVERVIEW = "tripOverview";
    public static final String EMPLOYEE_CERTIFICATION = "employeeCertification";
    public static final String ACTUAL_EXPENSES = "actualExpenses";
    public static final String IMPORTED_EXPENSES = "importedExpenses";
    public static final String EXPENSES_DETAILS = "expenseDetails";
    public static final String TRVL_AUTH_TOTAL_ESTIMATE = "estimateAmount";
    public static final String TRVL_RELATED_DOCUMENT_NUM = "relDocumentNumber";
    public static final String SPECIAL_CIRCUMSTANCES = "specialCircumstances";
    public static final String TRAVELER = "traveler";
    public static final String TRVL_ADV = "travelAdvance";
    public static final String TRAVEL_ADVANCES = "travelAdvances";
    public static final String ADVANCE_ACCOUNTING_LINES = "advanceAccountingLines";
    public static final String NEW_ADVANCE_ACCOUNTING_LINE = "newAdvanceAccountingLine";
    public static final String NEW_ADVANCE_ACCOUNTING_LINE_GROUP_NAME = "AdvanceAccounting";
    public static final String TRAVELER_TYPE = "travelerType";
    public static final String TRAVELER_TYPE_CODE = "travelerTypeCode";
    public static final String CUSTOMER = "customer";
    public static final String BLANKET_IND = "blanketTravel";

    public static final String NEW_IMPORTED_EXPENSE_LINE = "newImportedExpenseLine";
    public static final String NEW_IMPORTED_EXPENSE_LINES = "newImportedExpenseLines";

    public static final String NEW_ACTUAL_EXPENSE_LINE = "newActualExpenseLine";
    public static final String NEW_ACTUAL_EXPENSE_LINES = "newActualExpenseLines";

    public static final String EXPENSE_DATE = "expenseDate";
    //TODO: this really should be named expenseDate in the PerDiem
    public static final String MILEAGE_DATE = "mileageDate";
    public static final String UNFILTERED_INCIDENTALS_VALUE = "unfilteredIncidentalsValue";

    public static final String TRAVEL_DOCUMENT_IDENTIFIER = "travelDocumentIdentifier";

    public static final String TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS = "actions";

    // fields for adding an emergency contact
    public static final String TRVL_AUTH_EM_CONTACT_CONTACT_NAME = "contactName";
    public static final String TRVL_AUTH_EM_CONTACT_PHONE_NUM = "phoneNumber";
    public static final String TRVL_AUTH_EM_CONTACT_REL_TYPE = "contactRelationTypeCode";

    public static final String CONTACT_CELL_PHONENUMBER = "cellPhoneNumber";

    public static final String EXEPENSE_TYPE_OBJECT_CODE_ID = "expenseTypeObjectCodeId";
    public static final String EXPENSE_TYPE_OBJECT_CODE = "expenseTypeObjectCode";
    public static final String EXPENSE_AMOUNT = "expenseAmount";
    public static final String CURRENCY_RATE = "currencyRate";
    public static final String TRVL_AUTH_OTHER_EXP_DATE = "expenseDate";
    public static final String TEM_ACTUAL_EXPENSE_MILES = "miles";
    public static final String TEM_ACTUAL_EXPENSE_MILE_OTHER_RATE = "mileageOtherRate";
    public static final String TEM_ACTUAL_EXPENSE_MILE_RATE = "mileageRateId";
    public static final String TEM_ACTUAL_EXPENSE_NOTCE = "description";
    public static final String TEM_ACTUAL_EXPENSE_DETAIL = "detail";
    public static final String MAXIMUM_AMOUNT = "maximumAmount";
    public static final String MAXIMUM_AMOUNT_SUMMATION_CODE = "maximumAmountSummationCode";

    public static final String TRVL_DOC_TRAVELER_TYP_CD = "travelerTypeCode";
    public static final String TRVL_DOC_JOB_CLASSIFICATION_CD = "jobClsCode";

    public static final String PAYMENT_CHART_OF_ACCOUNTS_CODE = "paymentChartOfAccountsCode";
    public static final String SOURCE_ACCOUNTING_LINES = "sourceAccountingLines";
    public static final String SOURCE_ACCOUNTING_LINE = "sourceAccountingLine";

    public static final String GROUP_TRAVELER_EMP_ID = "groupTravelerEmpId";

    public static final String PER_DIEM_EXPENSE_DISABLED = "document.perDiemExpenses[%d].%s";
    public static final String AIRFARE_EXPENSE_DISABLED = "document.airfare";

    public static final String PRIMARY_DESTINATION_NAME = "primaryDestinationName";

    public static final String PER_DIEM_NAME = "primaryDestination";
    public static final String PER_DIEM_COUNTRY_STATE = "countryState";
    //Temporary property key for lookup use
    public static final String PER_DIEM_LOOKUP_DATE = "perDiemDate";

    public static final String PER_DIEM_EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String PER_DIEM_EFFECTIVE_TO_DATE = "effectiveToDate";

    public static final String ATTACHMENT_FILE = "attachmentFile";

    public static final String ACCOUNT_DISTRIBUTION_NEW_SRC_LINE = "accountDistributionnewSourceLine";
    public static final String ACCOUNT_DISTRIBUTION_SRC_LINES = "accountDistributionsourceAccountingLines";
    public static final String ACCOUNT_LINE_PERCENT = "accountLinePercent";

    public static final String PROFILE = "profile";
    public static final String RESIGN = "resign";

    //credit card agency data properties
    public static final String CREDIT_CARD_AGENCY = "creditCardAgency";
    public static final String AGENCY_STAGING_DATA = "agencyStagingData";
    public static final String CREDIT_CARD_STAGING_DATA = "creditCardStagingData";
    public static final String TRAVEL_CARD_TYPE = "travelCardType";
    //profile accounts
    public static final String CREDIT_CARD_AGENCY_ID = "creditCardAgencyId";

    public static final String RATE = "rate";

    public static class TravelAuthorizationFields {
        public static final String TRVL_ADV_REQUESTED = "travelAdvanceRequested";
        public static final String TRIP_BEGIN_DT = "tripBegin";
        public static final String TRIP_END_DT = "tripEnd";
        public static final String TRIP_TYPE = "tripTypeCode";
        public static final String TRAVELER_TYPE = "travelerTypeCode";
        public static final String CELL_PHONE_NUMBER = "cellPhoneNumber";
        public static final String REGION_FAMILIARITY = "regionFamiliarity";
        public static final String ACCOMM_TYPE = "accommodationTypeCode";
        public static final String ACCOMM_NAME = "accommodationName";
        public static final String ACCOMM_ADDRESS = "accommodationAddress";
        public static final String ACCOMM_PHONENUMBER = "accommodationPhoneNum";
        public static final String TRAVELER_ADDRESS = "traveler.streetAddressLine1";
        public static final String TRAVELER_CITY = "traveler.cityName";
        public static final String TRAVELER_PHONENUMBER = "traveler.phoneNumber";
        public static final String TRAVELER_FIRST_NAME = "traveler.firstName";
        public static final String TRAVELER_LAST_NAME = "traveler.lastName";
        public static final String TRAVELER_PRINCIPAL_ID = "traveler.principalId";
        public static final String GROUP_TRVL_TYPE_CODE = "travelerTypeCode";
        public static final String GROUP_TRAVELER_NAME = "name";
        public static final String CITIZENSHIP_CODE = "citizenship";
        public static final String MODE_OF_TRANSPORT = "modeOfTransportation";
        public static final String TRAVEL_EXPENSE_NOTES = "notes";
        public static final String FIN_OBJ_CD = "financialObjectCode";
        public static final String TRVL_ADV = "travelAdvance";
        public static final String TRVL_ADV_POLICY = "travelAdvancePolicy";
        public static final String TRVL_ADV_ADD_JUST = "additionalJustification";
        public static final String TRVL_ADV_PAY_RSN = "advancePaymentReasonCode";
        public static final String TRVL_ADV_DUE_DATE = "dueDate";
    }

    public static class TravelRelocationFields {
        public static final String TRIP_ID_PROPERTY = "tripId";
        public static final String FROM_STATE = "fromStateCode";
        public static final String TO_STATE = "toStateCode";
        public static final String ATTACHMENT_FILE = "attachmentFile";
    }

    public static class EntertainmentFields {
        public static final String ENTERTAINMENT_ID = "entertainmentID";
        public static final String HOST_ID = "hostID";
        public static final String HOST_NAME = "document.hostName";
        public static final String COUNTRY = "country";
        public static final String POSTAL_CODE = "postalCode";
        public static final String STATE_CODE = "stateCode";
        public static final String HOST_CERTIFIED = "hostCertified";
        public static final String FROM_DOCUMENT_NUMBER = "fromDocumentNumber";
    }

    public static class ArrangerFields {
        public static final String TRAVELER_NAME = "travelerName";
        public static final String PRIMARY_ARRANGER = "primaryInd";
        public static final String RESIGN = "resign";
    }

    public class TEMProfileProperties {
        public static final String PROFILE_ID = "profileId";
        public static final String PRINCIPAL_ID = "principalId";
        public static final String EMPLOYEE_ID = "employeeId";
        public static final String DATE_OF_BIRTH = "dateOfBirth";
        public static final String CITIZENSHIP = "citizenship";
        public static final String DRIVERS_LICENSE_EXP_DATE = "driversLicenseExpDate";
        public static final String DRIVERS_LICENSE_STATE = "driversLicenseState";
        public static final String DRIVERS_LICENSE_NUMBER = "driversLicenseNumber";
        public static final String UPDATED_BY = "updatedBy";
        public static final String LAST_UPDATE = "lastUpdate";
        public static final String GENDER = "gender";
        public static final String NON_RES_ALIEN = "nonResidentAlien";
        public static final String HOME_DEPT = "homeDepartment";
        public static final String TRAVELER_TYPE = "travelerType";
        public static final String TRAVELER_TYPE_CODE = "travelerTypeCode";
        public static final String ARRANGERS = "arrangers";
        public static final String EMERGENCY_CONTACTS = "emergencyContacts";

        public static final String DEFAULT_ACCOUNT_NUMBER = "defaultAccount";
        public static final String DEFAULT_SUB_ACCOUNT_NUMBER = "defaultSubAccount";
        public static final String DEFAULT_CHART_CODE = "defaultChartCode";
        public static final String DEFAULT_PROJECT_CODE = "defaultProjectCode";

        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String MIDDLE_NAME = "middleName";
        public static final String HOME_DEPARTMENT = "homeDepartment";
        public static final String ADDRESS_1 = "temProfileAddress.streetAddressLine1";
        public static final String ADDRESS_2 = "temProfileAddress.streetAddressLine2";
        public static final String CITY_NAME = "temProfileAddress.cityName";
        public static final String STATE_CODE = "temProfileAddress.stateCode";
        public static final String ZIP_CODE = "temProfileAddress.zipCode";
        public static final String COUNTRY_CODE = "temProfileAddress.countryCode";
        public static final String EMAIL_ADDRESS = "emailAddress";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String CUSTOMER_NUMBER = "customerNumber";

        public static final String EXPIRATION_DATE = "expirationDate";
        public static final String EFFECTIVE_DATE = "effectiveDate";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String ACCOUNTS = "accounts";

        public static final String TEM_PROFILE = "TemProfile";
        public static final String TEM_PROFILE_ADMINISTRATOR = "TEMProfileAdministrator";
    }

    public static class TravelAgencyAuditReportFields {
        public static final String ACCOUNTING_INFO = "tripAccountingInformation";
        public static final String TRIP_ID = "tripId";
        public static final String LODGING_NUMBER = "lodgingItineraryNumber";
        public static final String TRAVELER_DATA = "travelerId";
        public static final String TRIP_CHART_CODE = "tripChartCode";
        public static final String TRIP_ACCOUNT_NUMBER = "tripAccountNumber";
        public static final String TRIP_EXPENSE_AMOUNT = "tripExpenseAmount";
        public static final String TRANSACTION_POSTING_DATE = "transactionPostingDate";
        public static final String TRIP_INVOICE_NUMBER = "tripInvoiceNumber";
        public static final String CREDIT_CARD_OR_AGENCY_CODE = "creditCardOrAgencyCode";
    }

    @SuppressWarnings("rawtypes")
    public static final Map<String, Class> KIMReadOnly() {
        Map<String, Class> returnMap = new HashMap<String, Class>();
        returnMap.put(TemPropertyConstants.TEMProfileProperties.FIRST_NAME, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.LAST_NAME, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.MIDDLE_NAME, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.HOME_DEPARTMENT, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.ADDRESS_1, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.ADDRESS_2, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.CITY_NAME, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.STATE_CODE, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.ZIP_CODE, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.COUNTRY_CODE, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.EMAIL_ADDRESS, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.PHONE_NUMBER, null);
        return returnMap;
    }

    public static final Map<String, String> encryptedFields() {
        Map<String, String> returnMap = new HashMap<String, String>();
        returnMap.put(TemPropertyConstants.TEMProfileProperties.DRIVERS_LICENSE_EXP_DATE, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.DRIVERS_LICENSE_STATE, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.DRIVERS_LICENSE_NUMBER, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.DATE_OF_BIRTH, null);
        returnMap.put(TemPropertyConstants.TEMProfileProperties.ACCOUNT_NUMBER, null);
        return returnMap;
    }

    public static final Map<String, String> travelerProfileDiffMap() {
        Map<String, String> returnMap = new HashMap<String, String>();
        returnMap.put("firstName", "First Name");
        returnMap.put("lastName", "Last Name");
        returnMap.put("streetAddressLine1", "Street Address Line 1");
        returnMap.put("streetAddressLine2", "Street Address Line 2");
        returnMap.put("cityName", "City");
        returnMap.put("stateCode", "State Code");
        returnMap.put("countryCode", "Country Code");
        returnMap.put("zipCode", "Zip Code");
        returnMap.put("emailAddress", "Email Address");
        returnMap.put("phoneNumber", "Phone Number");
        return returnMap;
    }

    public class TEMProfileArrangerProperties {

    }

    public class AttendeeProperties {
        public static final String ATTENDEE_TYPE = "attendeeType";
        public static final String NAME = "name";
        public static final String COMPANY = "company";
        public static final String TITLE = "title";

    }

    public static final String SEASON_END_DATE = "seasonEndDate";
    public static final String SEASON_BEGIN_DATE = "seasonBeginDate";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String PRIMARY_DESTINATION = "primaryDestination";
    public static final String PRIMARY_DESTINATION_ID = "primaryDestinationId";
    public static final String COUNTY = "county";
    public static final String COUNTRY_STATE = "countryState";
    public static final String TRIP_TYPE = "tripType";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";

    public static final String INCIDENTALS = "incidentals";
    public static final String BREAKFAST = "breakfast";
    public static final String DINNER = "dinner";
    public static final String LUNCH = "lunch";
    public static final String MEALS_AND_INCIDENTALS = "mealsAndIncidentals";
    public static final String MEAL_WITHOUT_LODGING_REASON = "mealWithoutLodgingReason";
    public static final String TRIP_TYPE_CODE = "tripTypeCode";
    public static final String SEASON_BEGIN_MONTH_AND_DAY = "seasonBeginMonthAndDay";

    public static final String EMPLOYEE_ID = "employeeId";
    public static final String CUSTOMER_NUMBER = "customerNumber";
    public static final String TRAVELER_ID = "travelerId";
    public static final String TRIP_ID = "tripId";
    public static final String AIR_TICKET_NUMBER = "airTicketNumber";
    public static final String LODGING_ITINERARY_NUMBER = "lodgingItineraryNumber";
    public static final String RENTAL_CAR_ITINERARY_NUMBER = "rentalCarItineraryNumber";
    public static final String AGENCY = "agency";
    public static final String AGENCY_DATA_ID = "agencyDataId";
    public static final String TRANSACTION_POSTING_DATE = "transactionPostingDate";
    public static final String TRIP_EXPENSE_AMOUNT = "tripExpenseAmount";
    public static final String TRIP_INVOICE_NUMBER = "tripInvoiceNumber";
    public static final String AGENCY_ERROR_CODE = "errorCode";
    public static final String CREDIT_CARD_AGENCY_CODE = "creditCardOrAgencyCode";
    public static final String TRAVEL_CARD_TYPE_CODE = "travelCardTypeCode";
    public static final String OTHER_COMPANY_NAME = "otherCompanyName";
    public static final String TRIP_TRAVELER_TYPE_ID = " tripTravelerTypeId";
    public static final String OTHER_AMOUNT = "otherAmount";
    public static final String TRAVELER_NAME = "travelerName";
    public static final String TRAVELER_NETWORK_ID = "travelerNetworkId";
    public static final String TRIP_ARRANGER_NAME = "tripArrangerName";

    public static final String CREDIT_CARD_NUMBER = "creditCardNumber";
    public static final String CREDIT_CARD_KEY = "creditCardKey";
    public static final String REFERENCE_NUMBER = "referenceNumber";
    public static final String TRANSACTION_AMOUNT = "transactionAmount";
    public static final String TRANSACTION_DATE = "transactionDate";
    public static final String BANK_POSTED_DATE = "bankPostDate";
    public static final String MERCHANT_NAME = "merchantName";
    public static final String ACCOUNT_NUMBER = "accountNumber";
    public static final String TICKET_NUMBER = "ticketNumber";
    public static final String SERVICE_FEE_NUMBER = "serviceFeeNumber";
    public static final String ITINERARY_NUMBER = "itineraryNumber";
    public static final String DISTRIBUTION_CODE = "diCode";
    public static final String IMPORT_BY = "importBy";
    public static final String ACTIVE_IND = "active";

    // delinquent actions ranking
    public static final Map<String, Integer> delinquentActionsRank() {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        returnMap.put("W", 1);
        returnMap.put("S", 2);
        return returnMap;
    }

    public static final String TRAVEL_ADVANCE_DOCUMENT_NUMBER = "travelAdvanceDocumentNumber";
    public static final String ARRANGER_PROFILE_ID = "arrangerProfileId";
    public static final String AR_INVOICE_DOC_NUMBER = "arInvoiceDocNumber";
    public static final String TAXABLE_RAMIFICATION_NOTIFICATION_DATE = "taxRamificationNotificationDate";
    public static final String TRAVELER_DETAIL = "travelerDetail";
    public static final String CUSTOMER_TYPE_CODE = "customerTypeCode";
    public static final String CUSTOMER_ADDRESS_ADDRESS_TYPE_CODE = "customerAddresses.customerAddressTypeCode";
    public static final String CUSTOMER_EMAIL_ADDRESS =  "customerEmailAddress";
    public static final String NEW_ROUTE_STATUS = "newRouteStatus";
    public static final String OLD_ROUTE_STATUS = "oldRouteStatus";

    public static final String EXPENSE_NOTIFICATION_DATE = "expenseNotificationDate";

    public static final String EXPENSE_TYPE_CODE = "expenseTypeCode";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String NAME = "name";
    public static final String EXPENSE_TYPE_META_CATEGORY_CODE = "expenseTypeMetaCategoryCode";
    public static final String CATEGORY_DEFAULT = "categoryDefault";

    public static final String TRAVEL_PAYMENT = "travelPayment";
    public static final String ADVANCE_TRAVEL_PAYMENT = "advanceTravelPayment";
    public class TravelPaymentProperties {
        public static final String SPECIAL_HANDLING_PERSON_NAME = "specialHandlingPersonName";
        public static final String SPECIAL_HANDLING_STATE_CODE = "specialHandlingStateCode";
    }
}
