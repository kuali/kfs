/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants;
import org.apache.commons.beanutils.PropertyUtils;
 

/**
 * Holds constants for disbursement voucher and payee documents.
 */
// TODO: after DisbursementVoucherDocumentRule is remove, it is good to change this interface into a class
public interface DisbursementVoucherConstants {
    public static final String DOCUMENT_TYPE_CODE = "DV";

    // Text limits
    public static final int MAX_NOTE_LINE_SIZE = 90;

    // payment methods
    public static String PAYMENT_METHOD_CHECK = "P";
    public static String PAYMENT_METHOD_WIRE = "W";
    public static String PAYMENT_METHOD_DRAFT = "F";

    // payee types
    public static final String DV_PAYEE_TYPE_EMPLOYEE = "E";
    public static final String DV_PAYEE_TYPE_VENDOR = "V";
    public static final String DV_PAYEE_TYPE_SUBJECT_PAYMENT_VENDOR = "VSP";
    public static final String DV_PAYEE_TYPE_REVOLVING_FUND_VENDOR = "VRF";

    public static final List<String> VENDOR_PAYEE_TYPE_CODES = Arrays.asList(DV_PAYEE_TYPE_VENDOR, DV_PAYEE_TYPE_SUBJECT_PAYMENT_VENDOR, DV_PAYEE_TYPE_REVOLVING_FUND_VENDOR);

    // document location
    public static final String NO_DOCUMENTATION_LOCATION = "N";

    public static final String TAX_CONTROL_CODE_ALLOWS_EMPLOYEES = "A";
    public static final String TAX_CONTROL_CODE_BEGIN_WITHHOLDING = "B";
    public static final String TAX_CONTROL_CODE_HOLD_PAYMENT = "H";

    public static class DocumentStatusCodes {
        public static final String APPROVED = "A";
        public static final String EXTRACTED = "E";
    }


     public static class DvPdpExtractGroup {
        public static final String DV_PDP_ORG_CODE = "PRE_DISBURSEMENT_EXTRACT_ORGANIZATION";
        public static final String DV_PDP_SBUNT_CODE = "PRE_DISBURSEMENT_EXTRACT_SUB_UNIT";
    }

    public static class TabKey {
        public static final String NON_EMPLOYEE_TRAVEL_EXPENSE = "NonEmployeeTravelExpense";
        public static final String PRE_PAID_TRAVEL_EXPENSES = "PrePaidTravelExpenses";
    }

    public enum TabByReasonCode{
        NON_EMPLOYEE_TRAVEL_TAB(NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM, TabKey.NON_EMPLOYEE_TRAVEL_EXPENSE, KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL,
                KFSPropertyConstants.DISB_VCHR_NON_EMP_TRAVELER_NAME,KFSKeyConstants.WARNING_DV_NON_EMPLOYEE_TRAVEL_TAB),
        PREPAID_TRAVEL_TAB(PREPAID_TRAVEL_PAYMENT_REASONS_PARM_NM, TabKey.PRE_PAID_TRAVEL_EXPENSES, KFSPropertyConstants.DV_PRE_CONFERENCE_DETAIL,
                KFSPropertyConstants.DV_CONFERENCE_DESTINATION_NAME,KFSKeyConstants.WARNING_DV_PREPAID_TRAVEL_TAB);

        public String paymentReasonParameterName;
        public String tabKey;
        public String propertyName;
        public String reprentingFieldName;
        public String messageKey;

        private TabByReasonCode(String paymentReasonParameterName, String tabKey, String propertyName, String reprentingFieldName, String messageKey) {
            this.paymentReasonParameterName = paymentReasonParameterName;
            this.tabKey = tabKey;
            this.propertyName = propertyName;
            this.reprentingFieldName = reprentingFieldName;
            this.messageKey = messageKey;
        }

        private static ParameterService parameterService;
        private static ParameterService getParameterService() {
            if (parameterService == null) {
                parameterService = SpringContext.getBean(ParameterService.class);
            }
            return parameterService;
        }

        public static TabByReasonCode getTabByReasonCode(String paymentReasonCode) {
            for(TabByReasonCode tab : TabByReasonCode.values()) {
                if(/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, tab.paymentReasonParameterName, paymentReasonCode).evaluationSucceeds()){
                    return tab;
                }
            }
            return null;
        }

        public static List<String> getAllTabKeys() {
            List<String> tabKeys = new ArrayList<String>();
            for(TabByReasonCode tab : TabByReasonCode.values()) {
                tabKeys.add(tab.tabKey);
            }
            return tabKeys;
        }

        public static List<String> getAllDocumentPropertyKeys(){
            List<String> documentPropertyKeys = new ArrayList<String>();

            for(TabByReasonCode tab : TabByReasonCode.values()) {
                String propertyKey = tab.getDocumentPropertyKey();
                documentPropertyKeys.add(propertyKey);
            }

            return documentPropertyKeys;
        }

        public String getDocumentPropertyKey() {
            return KFSPropertyConstants.DOCUMENT + PropertyUtils.NESTED_DELIM + this.propertyName + PropertyUtils.NESTED_DELIM + this.reprentingFieldName;
        }
    }

    // system parameter parameter constants
    public static final String OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_TYPES";
    public static final String OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_LEVELS";
    public static final String OBJECT_SUB_TYPE_GLOBAL_RESTRICTION_PARM_NM = "OBJECT_SUB_TYPES";
    public static final String SUB_FUND_GLOBAL_RESTRICTION_PARM_NM = "SUB_FUND_GROUPS";
    public static final String FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM = "HIGHER_ED_FUNCTIONS";

    public static final String VALID_DOC_LOC_BY_PAYMENT_REASON_PARM = "VALID_DOCUMENTATION_LOCATIONS_BY_PAYMENT_REASON";
    public static final String VALID_DOC_LOC_BY_CAMPUS_PARM = "VALID_DOCUMENTATION_LOCATIONS_BY_CAMPUS";
    public static final String VALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM = "VALID_OBJECT_LEVELS_BY_PAYMENT_REASON";
    public static final String VALID_OBJ_CODE_BY_PAYMENT_REASON_PARM = "VALID_OBJECT_CODES_BY_PAYMENT_REASON";
    public static final String VALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM = "VALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP";
    public static final String VALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM = "VALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON";
    public static final String VALID_PAYEE_TYPES_BY_PAYMENT_REASON_PARM = "VALID_PAYEE_TYPES_BY_PAYMENT_REASON";
    public static final String VALID_VENDOR_OWNERSHIP_TYPES_BY_PAYMENT_REASON = "VALID_VENDOR_OWNERSHIP_TYPES_BY_PAYMENT_REASON";

    public static final String INVALID_DOC_LOC_BY_PAYMENT_REASON_PARM = "INVALID_DOCUMENTATION_LOCATIONS_BY_PAYMENT_REASON";
    public static final String INVALID_DOC_LOC_BY_CAMPUS_PARM = "INVALID_DOCUMENTATION_LOCATIONS_BY_CAMPUS";
    public static final String INVALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM = "INVALID_OBJECT_LEVELS_BY_PAYMENT_REASON";
    public static final String INVALID_OBJ_CODE_BY_PAYMENT_REASON_PARM = "INVALID_OBJECT_CODES_BY_PAYMENT_REASON";
    public static final String INVALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM = "INVALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP";
    public static final String INVALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM = "INVALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON";
    public static final String INVALID_PAYEE_TYPES_BY_PAYMENT_REASON_PARM = "INVALID_PAYEE_TYPES_BY_PAYMENT_REASON";

    public static final String FEDERAL_TAX_PARM_PREFIX = "NON_RESIDENT_ALIEN_TAX_FEDERAL_";
    public static final String STATE_TAX_PARM_PREFIX = "NON_RESIDENT_ALIEN_TAX_STATE_";
    public static final String TAX_PARM_ACCOUNT_SUFFIX = "ACCOUNT";
    public static final String TAX_PARM_CHART_SUFFIX = "CHART";
    public static final String TAX_PARM_OBJECT_BY_INCOME_CLASS_SUFFIX = "OBJECT_CODE_BY_INCOME_CLASS";
    public static final String ALIEN_INDICATOR_CHECKED_PARM_NM = "NON_RESIDENT_ALIEN_DOCUMENTATION_LOCATIONS";
    public static final String ALIEN_PAYMENT_REASONS_PARM_NM = "NON_RESIDENT_ALIEN_PAYMENT_REASONS";
    public static final String TRAVEL_PER_DIEM_MESSAGE_PARM_NM = "TRAVEL_PER_DIEM_LINK_PAGE_MESSAGE";
    public static final String DEFAULT_DOC_LOCATION_PARM_NM = "DEFAULT_DOCUMENTATION_LOCATION";
    public static final String ALLOW_OBJECT_CODE_EDITS = "ALLOW_ENROUTE_EDIT_OBJECT_CODES_IND";
    public static final String TAX_DOCUMENTATION_LOCATION_CODE_PARM_NM = "TAX_DOCUMENTATION_LOCATION";
    public static final String NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM = "NONEMPLOYEE_TRAVEL_PAYMENT_REASONS";
    public static final String NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_PARM_NM = "NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_IND";
    public static final String PREPAID_TRAVEL_PAYMENT_REASONS_PARM_NM = "PREPAID_TRAVEL_PAYMENT_REASONS";
    public static final String REVOLVING_FUND_PAYMENT_REASONS_PARM_NM = "REVOLVING_FUND_PAYMENT_REASONS";
    public static final String RESEARCH_PAYMENT_REASONS_PARM_NM = "RESEARCH_PAYMENT_REASONS";
    public static final String RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT_PARM_NM = "RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT";
    public static final String PERFORM_PREPAID_EMPL_PARM_NM = "CHECK_PREPAID_ACTIVE_EMPLOYEE_IND";
    public static final String CHECK_EMPLOYEE_PAID_OUTSIDE_PAYROLL_PARM_NM = "CHECK_EMPLOYEE_PAID_OUTSIDE_PAYROLL_IND";
    public static final String MOVING_PAYMENT_REASONS_PARM_NM = "MOVING_PAYMENT_REASONS";
    public static final String DECEDENT_COMPENSATION_PAYMENT_REASONS_PARM_NM = "DECEDENT_COMPENSATION_PAYMENT_REASONS";

    public static final String NON_VENDOR_EMPLOYEE_PAYEE_TYPE_LABEL_PARM_NM = "NON_VENDOR_EMPLOYEE_PAYEE_TYPE_LABEL";
    public static final String PO_AND_DV_PAYEE_TYPE_LABEL_PARM_NM = "PO_AND_DV_PAYEE_TYPE_LABEL";
    public static final String INDIVIDUAL_OWNERSHIP_TYPES_PARM_NM = "INDIVIDUAL_OWNERSHIP_TYPES";
    public static final String PAYMENT_REASONS_REQUIRING_TAX_REVIEW_PARM_NM = "PAYMENT_REASONS_REQUIRING_TAX_REVIEW";
    public static final String CAMPUSES_TAXED_FOR_MOVING_REIMBURSEMENTS_PARM_NM = "CAMPUSES_TAXED_FOR_MOVING_REIMBURSEMENTS";

    public static final String IMMEDIATE_EXTRACT_FROM_ADDRESS_PARM_NM = "IMMEDIATE_EXTRACT_NOTIFICATION_FROM_EMAIL_ADDRESS";
    public static final String IMMEDIATE_EXTRACT_TO_ADDRESSES_PARM_NM = "IMMEDIATE_EXTRACT_NOTIFICATION_TO_EMAIL_ADDRESSES";

    public static final String TAX_TYPE_SSN = "1";
    public static final String TAX_TYPE_FEIN = "0";

    public static final String TAX_ID_TYPE_SSN = KimConstants.PersonExternalIdentifierTypes.TAX;
    //public static final String TAX_ID_TYPE_FEIN = "F";

    public static final String NRA_TAX_INCOME_CLASS_FELLOWSHIP = "F";
    public static final String NRA_TAX_INCOME_CLASS_INDEPENDENT_CONTRACTOR = "I";
    public static final String NRA_TAX_INCOME_CLASS_ROYALTIES = "R";
    public static final String NRA_TAX_INCOME_CLASS_NON_REPORTABLE = "N";

    public static final String FEDERAL_TAX_TYPE_CODE = "F";
    public static final String STATE_TAX_TYPE_CODE = "S";

    public static final String DOCUMENT_TYPE_CHECKACH = "DVCA";
    public static final String DOCUMENT_TYPE_WTFD = "DVWF";

    public static final String DV_COVER_SHEET_TEMPLATE_LINES_PARM_NM = "COVER_SHEET_TEMPLATE_LINES";
    public static final String DV_COVER_SHEET_TEMPLATE_RLINES_PARM_NM = "COVER_SHEET_TEMPLATE_RLINES";
    public static final String DV_COVER_SHEET_TEMPLATE_ALIEN_PARM_NM = "COVER_SHEET_TEMPLATE_NON_RESIDENT_ALIEN";
    public static final String DV_COVER_SHEET_TEMPLATE_ATTACHMENT_PARM_NM = "COVER_SHEET_TEMPLATE_ATTACHMENT";
    public static final String DV_COVER_SHEET_TEMPLATE_HANDLING_PARM_NM = "COVER_SHEET_TEMPLATE_HANDLING";
    public static final String DV_COVER_SHEET_TEMPLATE_BAR_PARM_NM = "COVER_SHEET_TEMPLATE_BAR";
    public static final String DV_COVER_SHEET_TEMPLATE_NM = "disbursementVoucherCoverSheetTemplate.pdf";

    public static final String PAYMENT_REASON_CODE_ROYALTIES_PARM_NM = "PAYMENT_REASON_CODE_ROYALTIES";
    public static final String PAYMENT_REASON_CODE_RENTAL_PAYMENT_PARM_NM = "PAYMENT_REASON_CODE_RENTAL_PAYMENT";
    public static final String PAYMENT_REASON_CODE_TRAVEL_HONORARIUM_PARM_NM = "PAYMENT_REASON_CODE_TRAVEL_HONORARIUM";


    public static class RouteLevelNames {
        public static final String PURCHASING = "Purchasing";
        public static final String ACCOUNT = "Account";
        public static final String ACCOUNTING_ORGANIZATION_HIERARCHY = "AccountingOrganizationHierarchy";
        public static final String TAX = "Tax";
        public static final String AWARD = "Award";
        public static final String TRAVEL = "Travel";
        public static final String CAMPUS = "Campus";
        public static final String PAYMENT_METHOD = "PaymentMethod";
    }

    public static final String DV_DOC_NAME = "Disbursement Voucher";

}
