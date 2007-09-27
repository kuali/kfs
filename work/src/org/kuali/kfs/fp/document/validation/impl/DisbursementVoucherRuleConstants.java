/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.rules;


/**
 * Holds constants for disbursement voucher and payee documents.
 * 
 * 
 */
public interface DisbursementVoucherRuleConstants {
    
    // payment methods
    public static String PAYMENT_METHOD_CHECK = "P";
    public static String PAYMENT_METHOD_WIRE = "W";
    public static String PAYMENT_METHOD_DRAFT = "F";

    // payee types
    public static final String DV_PAYEE_TYPE_PAYEE = "P";
    public static final String DV_PAYEE_TYPE_EMPLOYEE = "E";
    public static final String DV_PAYEE_TYPE_VENDOR = "V";

    // ownership type
    public static String OWNERSHIP_TYPE_CORPORATION = "C";
    public static String OWNERSHIP_TYPE_GOVERNMENT = "G";
    public static String OWNERSHIP_TYPE_MEDICAL = "H";
    public static String OWNERSHIP_TYPE_INDIVIDUAL = "I";
    public static String OWNERSHIP_TYPE_LEGAL_SERVICES = "L";
    public static String OWNERSHIP_TYPE_PARTNERSHIP = "P";
    public static String OWNERSHIP_TYPE_NONPROFIT_TRUST = "T";

    // document location
    public static String NO_DOCUMENTATION_LOCATION = "N";


    public static class DocumentStatusCodes {
        public static String APPROVED = "A";
        public static String EXTRACTED = "E";
    }

    public static class PaymentReasonCodes {
        public static String PRIZE = "A";
        public static String OUT_OF_POCKET = "B";
        public static String RESEARCH_PARTICIPANT = "C";
        public static String DECEDENT = "D";
        public static String SERVICES = "E";
        public static String REFUND = "F";
        public static String UTILITIES = "G";
        public static String MEDICAL = "H";
        public static String REVOLVING_FUND = "K";
        public static String CONTRACTUAL_AGREEMENTS = "L";
        public static String MOVING = "M";
        public static String TRAVEL_NONEMPLOYEE = "N";
        public static String TRAVEL_PREPAID = "P";
        public static String ROYALTIES = "R";
        public static String RENTAL_PAYMENT = "T";
        public static String SUBSCRIPTIONS = "W";
        public static String TRAVEL_HONORARIUM = "X";
        public static String CLAIMS = "Z";
    }

    public static class DvPdpExtractGroup {
        public static String DV_PDP_ORG_CODE = "PRE_DISBURSEMENT_EXTRACT_ORGANIZATION";
        public static String DV_PDP_SBUNT_CODE = "PRE_DISBURSEMENT_EXTRACT_SUB_UNIT";
        public static String DV_PDP_USER_ID = "PRE_DISBURSEMENT_EXTRACT_USER";
    }

    // system parameter parameter constants
    public static final String OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_TYPES";
    public static final String OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_LEVELS";
    public static final String OBJECT_SUB_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_SUB_TYPES";
    public static final String SUB_FUND_GLOBAL_RESTRICTION_PARM_NM = "SUB_FUND_GROUPS";
    public static final String FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM = "FUNCTION_CODE_RESTRICTIONS";
    
    public static final String INVALID_DOC_LOC_BY_PAYMENT_REASON_PARM = "INVALID_DOCUMENTATION_LOCATIONS_BY_PAYMENT_REASON";
    public static final String INVALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM = "INVALID_OBJECT_LEVELS_BY_PAYMENT_REASON";
    public static final String INVALID_OBJ_CODE_BY_PAYMENT_REASON_PARM = "INVALID_OBJECTS_BY_PAYMENT_REASON";
    public static final String INVALID_DOC_LOC_BY_CAMPUS_PARM = "INVALID_DOCUMENTATION_LOCATIONS_BY_CAMPUS";    
    public static final String INVALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM = "INVALID_PAYMENT_REASONS_BY_PAYEE_TYPE";
    public static final String INVALID_PAYMENT_REASONS_BY_OBJ_LEVEL_PARM = "INVALID_PAYMENT_REASONS_BY_OBJECT_LEVEL";
    public static final String INVALID_PAYMENT_REASONS_BY_OBJ_CODE_PARM = "INVALID_PAYMENT_REASONS_BY_OBJECT_CODE";
    public static final String INVALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM = "INVALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP";
    public static final String INVALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM = "INVALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON";

    public static final String VALID_DOC_LOC_BY_PAYMENT_REASON_PARM = "VALID_DOCUMENTATION_LOCATIONS_BY_PAYMENT_REASON";
    public static final String VALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM = "VALID_OBJECT_LEVELS_BY_PAYMENT_REASON";
    public static final String VALID_OBJ_CODE_BY_PAYMENT_REASON_PARM = "VALID_OBJECTS_BY_PAYMENT_REASON";
    public static final String VALID_DOC_LOC_BY_CAMPUS_PARM = "VALID_DOCUMENTATION_LOCATIONS_BY_CAMPUS";    
    public static final String VALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM = "VALID_PAYMENT_REASONS_BY_PAYEE_TYPE";
    public static final String VALID_PAYMENT_REASONS_BY_OBJ_LEVEL_PARM = "VALID_PAYMENT_REASONS_BY_OBJECT_LEVEL";
    public static final String VALID_PAYMENT_REASONS_BY_OBJ_CODE_PARM = "VALID_PAYMENT_REASONS_BY_OBJECT_CODE";
    public static final String VALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM = "VALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP";
    public static final String VALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM = "VALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON";
    
    public static final String FEDERAL_TAX_PARM_PREFIX = "NRA_TAX_FEDERAL_";
    public static final String STATE_TAX_PARM_PREFIX = "NRA_TAX_STATE_";
    public static final String TAX_PARM_ACCOUNT_SUFFIX = "ACCOUNT";
    public static final String TAX_PARM_CHART_SUFFIX = "CHART";
    public static final String TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX = "OBJECT_CODE_BY_INCOME_CLASS";
    public static final String ALIEN_INDICATOR_CHECKED_PARM_NM = "ALIEN_DOCUMENTATION_LOCATIONS";
    public static final String ALIEN_PAYMENT_REASONS_PARM_NM = "ALIEN_PAYMENT_REASONS";
    public static final String TRAVEL_PER_DIEM_MESSAGE_PARM_NM = "TRAVEL_PER_DIEM_LINK_PAGE_MESSAGE";
    public static final String DEFAULT_DOC_LOCATION_PARM_NM = "DEFAULT_DOCUMENTATION_LOCATION";
    public static final String ALLOW_OBJECT_CODE_EDITS = "ALLOW_ROUTE_OBJECT_CODE_EDITS";
    public static final String TAX_DOCUMENTATION_LOCATION_CODE_PARM_NM = "TAX_DOCUMENTATION_LOCATION_CODE";
    public static final String W9_OWNERSHIP_TYPES_PARM_NM = "W9_OWNERSHIP_TYPES";
    public static final String NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM = "NONEMPLOYEE_TRAVEL_PAYMENT_REASONS";
    public static final String NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_PARM_NM = "NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_IND";
    public static final String PREPAID_TRAVEL_PAY_REASONS_PARM_NM = "PREPAID_TRAVEL_PAYMENT_REASONS";
    public static final String REVOLVING_FUND_PAY_REASONS_PARM_NM = "REVOLVING_FUND_PAYMENT_REASONS";
    public static final String RESEARCH_PAY_REASONS_PARM_NM = "RESEARCH_PAYMENT_REASONS";
    public static final String RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM = "RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT";
    public static final String PERFORM_PREPAID_EMPL_PARM_NM = "CHECK_PREPAID_ACTIVE_EMPLOYEE_IND";
    public static final String CHECK_EMPLOYEE_PAID_OUTSIDE_PAYROLL_PARM_NM = "CHECK_EMPLOYEE_PAID_OUTSIDE_PAYROLL_IND";
    public static final String MOVING_PAY_REASONS_PARM_NM = "MOVING_PAYMENT_REASONS";

    public static String TAX_TYPE_SSN = "1";
    public static String TAX_TYPE_FEIN = "0";

    public static String NRA_TAX_INCOME_CLASS_FELLOWSHIP = "F";
    public static String NRA_TAX_INCOME_CLASS_INDEPENDENT_CONTRACTOR = "I";
    public static String NRA_TAX_INCOME_CLASS_ROYALTIES = "R";
    public static String NRA_TAX_INCOME_CLASS_NON_REPORTABLE = "N";

    public static String FEDERAL_TAX_TYPE_CODE = "F";
    public static String STATE_TAX_TYPE_CODE = "S";

    public static String DOCUMENT_TYPE_CHECKACH = "DVCA";
    public static String DOCUMENT_TYPE_WTFD = "DVWF";

}