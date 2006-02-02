/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import java.util.Set;

import org.kuali.core.rules.RulesUtils;


/**
 * Holds constants for disbursement voucher and payee documents.
 * @author Nervous System Team (kualidev@oncourse.iu.edu)
 */
public interface DisbursementVoucherRuleConstants {

    public static String GENERAL_PAYEE_TAB_ERRORS = "DVPayeeErrors";
    public static String GENERAL_PAYMENT_TAB_ERRORS = "DVPaymentErrors";
    public static String GENERAL_NRATAX_TAB_ERRORS = "DVNRATaxErrors";
    public static String GENERAL_FOREIGNDRAFTS_TAB_ERRORS = "DVForeignDraftErrors";
    public static String GENERAL_CONTACT_TAB_ERRORS = "DVContactErrors";
    public static String GENERAL_SPECHAND_TAB_ERRORS = "DVSpecialHandlingErrors";
    public static String GENERAL_WIRETRANSFER_TAB_ERRORS = "DVWireTransfersErrors";

    // payment methods
    public static String PAYMENT_METHOD_CHECK = "P";
    public static String PAYMENT_METHOD_WIRE = "W";
    public static String PAYMENT_METHOD_DRAFT = "F";

    // payment reasons
    public static String PAYMENT_REASON_PRIZE_AWARD = "A";
    public static String PAYMENT_REASON_OUT_OF_POCKET = "B";
    public static String PAYMENT_REASON_RESEARCH = "C";
    public static String PAYMENT_REASON_COMPENSATION_DECEDENT = "D";
    public static String PAYMENT_REASON_COMP_SERVICES = "E";
    public static String PAYMENT_REASON_REFUND_INDIVIDUAL = "F";
    public static String PAYMENT_REASON_UTIL_POST = "G";
    public static String PAYMENT_REASON_MEDICAL = "H";
    public static String PAYMENT_REASON_REVL_FUND = "K";
    public static String PAYMENT_REASON_CONTRACTS = "L";
    public static String PAYMENT_REASON_MOVING = "M";
    public static String PAYMENT_REASON_TRAVEL_NONEMPL = "N";
    public static String PAYMENT_REASON_PREPAID_TRAVEL = "P";
    public static String PAYMENT_REASON_ROYALTIES = "R";
    public static String PAYMENT_REASON_RENTAL_PAYMENT = "T";
    public static String PAYMENT_REASON_SUBSCRIPTIONS = "W";
    public static String PAYMENT_REASON_TRAVEL_HONORARIUM = "X";
    public static String PAYMENT_REASON_CLAIMS = "Z";

    // ownership type
    public static String OWNERSHIP_TYPE_CORPORATION = "C";
    public static String OWNERSHIP_TYPE_GOVERNMENT = "G";
    public static String OWNERSHIP_TYPE_MEDICAL = "H";
    public static String OWNERSHIP_TYPE_INDIVIDUAL = "I";
    public static String OWNERSHIP_TYPE_LEGAL_SERVICES = "L";
    public static String OWNERSHIP_TYPE_PARTNERSHIP = "P";
    public static String OWNERSHIP_TYPE_NONPROFIT_TRUST = "T";

    // country
    public static String COUNTRY_UNITED_STATES = "UNITED STATES";

    // document location
    public static String NO_DOCUMENTATION_LOCATION = "N";

    public static String[] PAYMENT_REASON_CODES = new String[] { PAYMENT_REASON_PRIZE_AWARD, PAYMENT_REASON_OUT_OF_POCKET,
            PAYMENT_REASON_RESEARCH, PAYMENT_REASON_COMPENSATION_DECEDENT, PAYMENT_REASON_COMP_SERVICES,
            PAYMENT_REASON_REFUND_INDIVIDUAL, PAYMENT_REASON_UTIL_POST, PAYMENT_REASON_MEDICAL, PAYMENT_REASON_REVL_FUND,
            PAYMENT_REASON_CONTRACTS, PAYMENT_REASON_MOVING, PAYMENT_REASON_TRAVEL_NONEMPL, PAYMENT_REASON_PREPAID_TRAVEL,
            PAYMENT_REASON_ROYALTIES, PAYMENT_REASON_RENTAL_PAYMENT, PAYMENT_REASON_SUBSCRIPTIONS,
            PAYMENT_REASON_TRAVEL_HONORARIUM, PAYMENT_REASON_CLAIMS };

    public static String INCLUSION_CHARACTER = "+";
    public static String EXCLUSION_CHARACTER = "-";
    
    // apc security group constants
    public static String GLOBAL_FIELD_RESTRICTIONS_GROUP_NM = "DVGlobalFieldRestrictions";
    public static String PAYMENT_OBJECT_LEVEL_GROUP_NM = "DVPaymentObjectLevelRestrictions";
    public static String PAYMENT_OBJECT_CODE_GROUP_NM = "DVPaymentObjectCodeRestrictions";
    public static String OBJECT_CODE_PAYMENT_GROUP_NM = "DVObjectCodePaymentRestrictions";
    public static String PAYEE_PAYMENT_GROUP_NM = "DVPayeePaymentTypeRestrictions";
    public static String ALIEN_INDICATOR_DOC_LOCATION_GROUP_NM = "DVAlienIndicatorDocLocationRestrictions";
    public static String ALIEN_INDICATOR_PAYMENT_GROUP_NM = "DVAlienIndicatorPaymentRestrictions";
    public static String CAMPUS_DOC_LOCATION_GROUP_NM = "DVCampusDocLocationRestrictions";
    public static String PAYMENT_DOC_LOCATION_GROUP_NM = "DVPaymentDocLocationRestrictions";
    public static String PAYMENT_SUB_FUND_GROUP_NM = "DVPaymentSubFundRestrictions";
    public static String SUB_FUND_OBJECT_SUB_TYPE_GROUP_NM = "DVSubFundObjectSubTypeRestrictions";
    public static String NRA_TAX_PARM_GROUP_NM = "DVNRATaxParameters";
    
    
    // apc parameter constants 
    public static String OBJECT_CODE_PARM_PREFIX = "OBJECT_CODE_";
    public static String PAYMENT_PARM_PREFIX = "PAYMENT_REASON_";
    public static String OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_TYPE_RESTRICTIONS";
    public static String OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_LEVEL_RESTRICTIONS";
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
    
    public static String TAX_TYPE_SSN = "1";
    public static String TAX_TYPE_FEIN = "0";
    public static String UNITED_STATES_COUNTRY_NAME = "USA";

    public static Set W9_OWN_TYPS = RulesUtils.makeSet(new String[] { "M", "I", "P", "S" });
    
    public static String NRA_TAX_INCOME_CLASS_FELLOWSHIP = "F";
    public static String NRA_TAX_INCOME_CLASS_INDEPENDENT_CONTRACTOR = "I";
    public static String NRA_TAX_INCOME_CLASS_ROYALTIES = "R";
    public static String NRA_TAX_INCOME_CLASS_NON_REPORTABLE = "N";
    
    public static String FEDERAL_TAX_TYPE_CODE = "F";
    public static String STATE_TAX_TYPE_CODE = "S";
}