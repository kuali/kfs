/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys;

/**
 * Parameter name constants for system parameters used by the kfs sys.
 */
public class KFSParameterKeyConstants {

    public static final String ENABLE_BANK_SPECIFICATION_IND = "ENABLE_BANK_SPECIFICATION_IND";
    public static final String DEFAULT_BANK_BY_DOCUMENT_TYPE = "DEFAULT_BANK_BY_DOCUMENT_TYPE";
    public static final String BANK_CODE_DOCUMENT_TYPES = "BANK_CODE_DOCUMENT_TYPES";
    public static final String DEFAULT_CHART_CODE_METHOD = "DEFAULT_CHART_CODE_METHOD";
    public static final String DEFAULT_CHART_CODE = "DEFAULT_CHART_CODE";

    public static class YearEndAutoDisapprovalConstants {
        public static final String YEAR_END_AUTO_DISAPPROVE_ANNOTATION = "YEAR_END_AUTO_DISAPPROVE_ANNOTATION";
        public static final String YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE = "YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE";
        public static final String YEAR_END_AUTO_DISAPPROVE_DOCUMENT_STEP_RUN_DATE = "YEAR_END_AUTO_DISAPPROVE_DOCUMENT_RUN_DATE";
        public static final String YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES = "YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES";
        public static final String YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE = "YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE";
    }

    public static class FpParameterConstants {
    	public static final String FP_BUDGET_BALANCE_TYPES = "BUDGET_BALANCE_TYPES";
        public static final String NEGATIVE_ACCOUNTING_LINES_IND = "NEGATIVE_ACCOUNTING_LINES_IND";
        public static final String ALLOW_ADDITIONAL_FROM_LINE_IND = "ALLOW_ADDITIONAL_FROM_LINE_IND";
        public static final String DISPLAY_CASH_RECEIPT_DENOMINATION_DETAIL_IND = "DISPLAY_CASH_RECEIPT_DENOMINATION_DETAIL_IND"; 
    }

    public static class PopulateFinancialSystemDocumentHeaderParameterNames {
        public static final String POPULATION_LIMIT = "POPULATION_LIMIT";
        public static final String BATCH_SIZE = "BATCH_SIZE";
        public static final String DOCUMENT_STATUSES_TO_POPULATE = "DOCUMENT_STATUSES_TO_POPULATE";
    }

    public static class PurapPdpParameterConstants {
        public static final String PURAP_PDP_ORG_CODE = "PRE_DISBURSEMENT_EXTRACT_ORGANIZATION";
        public static final String PURAP_PDP_SUB_UNIT_CODE = "PRE_DISBURSEMENT_EXTRACT_SUB_UNIT";
    }

    public static class GlParameterConstants {
        public static final String PLANT_INDEBTEDNESS_OFFSET_CODE = "PLANT_INDEBTEDNESS_OFFSET_CODE";
        public static final String CAPITALIZATION_OFFSET_CODE = "CAPITALIZATION_OFFSET_CODE";
        public static final String LIABILITY_OFFSET_CODE = "LIABILITY_OFFSET_CODE";
        public static final String EXPENSE_OBJECT_TYPE = "EXPENSE_OBJECT_TYPE";
        public static final String INCOME_OBJECT_TYPE = "INCOME_OBJECT_TYPE";
        public static final String CASH_BUDGET_RECORD_LEVEL = "CASH_BUDGET_RECORD_LEVEL";
        public static final String FUND_BALANCE_OBJECT_CODE = "FUND_BALANCE_OBJECT_CODE";
        public static final String CURRENT_ASSET_OBJECT_CODE = "CURRENT_ASSET_OBJECT_CODE";
        public static final String CURRENT_LIABILITY_OBJECT_CODE = "CURRENT_LIABILITY_OBJECT_CODE";
        public static final String ENCUMBRANCE_BALANCE_TYPE = "ENCUMBRANCE_BALANCE_TYPE";
    }

    public static class LdParameterConstants {
        public static final String DEMERGE_DOCUMENT_TYPES = "DEMERGE_DOCUMENT_TYPES";
        public static final String ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND = "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND";
        public static final String DEFAULT_BENEFIT_RATE_CATEGORY_CODE = "DEFAULT_BENEFIT_RATE_CATEGORY_CODE";
    }

    public static class CamParameterConstants {
        public static final String OBJECT_SUB_TYPE_GROUPS = "OBJECT_SUB_TYPE_GROUPS";
    }

    public static final class GeneralLedgerSysParmeterKeys {
        public static final String TRANSACTION_DATE_BYPASS_ORIGINATIONS = "TRANSACTION_DATE_BYPASS_ORIGINATIONS";
    };

    public static class InvalidSubFundsByObjCdParameterConstant {
        public static final String INVALID_SUBFUND_GROUPS_BY_OBJ_TYPE = "INVALID_SUBFUND_GROUPS_BY_OBJ_TYPE";
    }

    public static class PdpExtractBatchParameters {
        public static final String PDP_ORG_CODE = "PRE_DISBURSEMENT_EXTRACT_ORGANIZATION";
        public static final String PDP_SBUNT_CODE = "PRE_DISBURSEMENT_EXTRACT_SUB_UNIT";
        public static final String IMMEDIATE_EXTRACT_FROM_ADDRESS_PARM_NM = "IMMEDIATE_EXTRACT_NOTIFICATION_FROM_EMAIL_ADDRESS";
        public static final String IMMEDIATE_EXTRACT_TO_ADDRESSES_PARM_NM = "IMMEDIATE_EXTRACT_NOTIFICATION_TO_EMAIL_ADDRESSES";
    }

    public static class CoaParameterConstants {
        public final static String DISPLAY_SOURCE_OF_FUNDS_IND = "DISPLAY_SOURCE_OF_FUNDS_IND";
    }
}
