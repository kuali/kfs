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


/**
 * Holds constants for disbursement voucher and payee documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
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

    // apc security group constants
    public static String DV_DOCUMENT_PARAMETERS_GROUP_NM = "DVDocumentParameters";
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

    // apc parameter constants
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
    public static String PREPAID_TRAVEL_PAY_REASONS_PARM_NM = "PREPAID_TRAVEL_PAYMENT_REASONS";
    public static String REVOLVING_FUND_PAY_REASONS_PARM_NM = "REVOLVING_FUND_PAYMENT_REASONS";
    public static String RESEARCH_PAY_REASONS_PARM_NM = "RESEARCH_PAYMENT_REASONS";
    public static String RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM = "RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT";
    public static String PERFORM_PREPAID_EMPL_PARM_NM = "PERFORM_PREPAID_ACTIVE_EMPLOPYEE_IND";
    public static String PERFORM_EMPL_OUTSIDE_PAYROLL_PARM_NM = "RFORM_EMPL_PAID_OUTSIDE_PAYROLL_IND";
    public static String MOVING_PAY_REASONS_PARM_NM = "MOVING_PAYMENT_REASONS";
    public static String SWITCH_DEBIT_CREDIT_ACCOUNT_TYPES_PARM_NM = "SWITCH_DEBIT_CREDIT_ACCOUNT_TYPES";

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