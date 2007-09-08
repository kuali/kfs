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

    // system parameter security group constants
    public static String DV_PDP_EXTRACT_GROUP_NM = "DVPDPExtract";
    public static String DV_PAYMENT_REASON_CAMPUS_OVERRIDE = "DVPaymentReasonCampusOverride";
    public static String DV_DOCUMENT_PARAMETERS_GROUP_NM = "Kuali.FinancialTransactionProcessing.DisbursementVoucherDocument";
    public static String GLOBAL_FIELD_RESTRICTIONS_GROUP_NM = "DVGlobalFieldRestrictions";
    public static String PAYMENT_OBJECT_LEVEL_GROUP_NM = "DVPaymentObjectLevelRestrictions";
    public static String PAYMENT_OBJECT_CODE_GROUP_NM = "DVPaymentObjectCodeRestrictions";
    public static String OBJECT_CODE_PAYMENT_GROUP_NM = "DVObjectCodePaymentRestrictions";
    public static String OBJECT_LEVEL_PAYMENT_GROUP_NM = "DVObjectLevelPaymentRestrictions";
    public static String PAYEE_PAYMENT_GROUP_NM = "DVPayeePaymentTypeRestrictions";
    public static String ALIEN_INDICATOR_DOC_LOCATION_GROUP_NM = "DVAlienIndicatorDocLocationRestrictions";
    public static String ALIEN_INDICATOR_PAYMENT_GROUP_NM = "DVAlienIndicatorPaymentRestrictions";
    public static String CAMPUS_DOC_LOCATION_GROUP_NM = "DVCampusDocLocationRestrictions";
    public static String PAYMENT_DOC_LOCATION_GROUP_NM = "DVPaymentDocLocationRestrictions";
    public static String PAYMENT_SUB_FUND_GROUP_NM = "DVPaymentSubFundRestrictions";
    public static String SUB_FUND_OBJECT_SUB_TYPE_GROUP_NM = "DVSubFundObjectSubTypeRestrictions";
    public static String NRA_TAX_PARM_GROUP_NM = "DVNRATaxParameters";

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
        public static String DV_PDP_ORG_CODE = "DV.PDP.ORG.CODE";
        public static String DV_PDP_SBUNT_CODE = "DV.PDP.SBUNT.CODE";
        public static String DV_PDP_USER_ID = "DV.PDP.USER.ID";
    }

    // system parameter parameter constants
    public static String OBJECT_CODE_PARM_PREFIX = "OBJECT_CODE_";
    public static String OBJECT_LEVEL_PARM_PREFIX = "OBJECT_LEVEL_";
    public static String PAYMENT_PARM_PREFIX = "PAYMENT_REASON_";
    public static String OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_TYPE_RESTRICTIONS";
    public static String OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_LEVEL_RESTRICTIONS";
    public static String OBJECT_SUB_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_SUB_TYPE_RESTRICTIONS";
    public static String SUB_FUND_GLOBAL_RESTRICTION_PARM_NM = "SUB_FUND_RESTRICTIONS";
    public static String FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM = "FUNCTION_CODE_RESTRICTIONS";
    public static String EMPLOYEE_PAYEE_PAYMENT_PARM = "PAYEE_TYPE_E";
    public static String DVPAYEE_PAYEE_PAYMENT_PARM = "PAYEE_TYPE_P";
    public static String VENDOR_PAYEE_PAYMENT_PARM = "PAYEE_TYPE_V";
    public static String FEDERAL_TAX_ACCOUNT_PARM_NM = "FEDERAL_TAX_ACCOUNT";
    public static String STATE_TAX_ACCOUNT_PARM_NM = "STATE_TAX_ACCOUNT";
    public static String FEDERAL_TAX_CHART_PARM_NM = "FEDERAL_TAX_CHART";
    public static String STATE_TAX_CHART_PARM_NM = "STATE_TAX_CHART";
    public static String FEDERAL_OBJECT_CODE_PARM_PREFIX = "FEDERAL_OBJECT_CODE_";
    public static String STATE_OBJECT_CODE_PARM_PREFIX = "STATE_OBJECT_CODE_";
    public static String ALIEN_INDICATOR_CHECKED_PARM_NM = "ALIEN_INDICATOR_CHECKED";
    public static String CAMPUS_CODE_PARM_PREFIX = "CAMPUS_CODE_";
    public static String SUB_FUND_CODE_PARM_PREFIX = "SUB_FUND_";
    public static String TRAVEL_PER_DIEM_MESSAGE_PARM_NM = "TRAVEL_PER_DIEM_LINK_PAGE_MESSAGE";
    public static String DEFAULT_DOC_LOCATION_PARM_NM = "DEFAULT_DOCUMENTATION_LOCATION";
    public static String ALLOW_OBJECT_CODE_EDITS = "ALLOW_ROUTE_OBJECT_CODE_EDITS";
    public static String TAX_DOCUMENTATION_LOCATION_CODE_PARM_NM = "TAX_DOCUMENTATION_LOCATION_CODE";
    public static String W9_OWNERSHIP_TYPES_PARM_NM = "W9_OWNERSHIP_TYPES";
    public static String NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM = "NONEMPLOYEE_TRAVEL_PAYMENT_REASONS";
    public static String NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_PARM_NM = "NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_INDICATOR";
    public static String PREPAID_TRAVEL_PAY_REASONS_PARM_NM = "PREPAID_TRAVEL_PAYMENT_REASONS";
    public static String REVOLVING_FUND_PAY_REASONS_PARM_NM = "REVOLVING_FUND_PAYMENT_REASONS";
    public static String RESEARCH_PAY_REASONS_PARM_NM = "RESEARCH_PAYMENT_REASONS";
    public static String RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM = "RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT";
    public static String PERFORM_PREPAID_EMPL_PARM_NM = "PERFORM_PREPAID_ACTIVE_EMPLOPYEE_IND";
    public static String MOVING_PAY_REASONS_PARM_NM = "MOVING_PAYMENT_REASONS";

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