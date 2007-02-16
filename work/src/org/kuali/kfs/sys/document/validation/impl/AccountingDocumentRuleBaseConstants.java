/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.rules;


/**
 * Holds common constants for all Transaction Processing eDoc rule classes.
 */
public interface AccountingDocumentRuleBaseConstants {
    // Security grouping constants used to do application parameter lookups
    public static final class APPLICATION_PARAMETER_SECURITY_GROUP {
        public static final String KUALI_TRANSACTION_PROCESSING_GLOBAL_RULES_SECURITY_GROUPING = "Kuali.FinancialTransactionProcessing.GlobalRules";
    }

    // Application parameter lookup constants to be used in conjunction with the grouping constants above
    public static final class APPLICATION_PARAMETER {
        public static final String RESTRICTED_OBJECT_CODES = "RestrictedObjectCodes";
        public static final String RESTRICTED_OBJECT_TYPE_CODES = "RestrictedObjectTypeCodes";
        public static final String RESTRICTED_SUB_FUND_GROUP_CODES = "RestrictedSubFundGroupCodes";
        public static final String INCOME_OBJECT_TYPE_CODES = "IncomeObjectTypeCodes";
        public static final String EXPENSE_OBJECT_TYPE_CODES = "ExpenseObjectTypeCodes";
        public static final String MANDATORY_TRANSFER_SUBTYPE_CODES = "MandatoryTransferSubTypeCodes";
        public static final String NONMANDATORY_TRANSFER_SUBTYPE_CODES = "NonMandatoryTransferSubTypeCode";
        public static final String FUND_GROUP_BALANCING_SET = "FundGroupBalancingSet";
    }
    
    /**
     * Constant to statically define reusable error paths
     */
    public static final class ERROR_PATH {
        public static final String DELIMITER = ".";
        public static final String DOCUMENT_ERROR_PREFIX = "document" + DELIMITER;
    }

    // GLPE Constants
    public static final class GENERAL_LEDGER_PENDING_ENTRY_CODE {
        public static final String NO = "N";
        public static final String YES = "Y";
        public static final String BLANK_PROJECT_STRING = "----------"; // Max length is 10 for this field
        public static final String BLANK_SUB_OBJECT_CODE = "---"; // Max length is 3 for this field
        public static final String BLANK_SUB_ACCOUNT_NUMBER = "-----"; // Max length is 5 for this field
        public static final String BLANK_OBJECT_CODE = "----"; // Max length is 4 for this field
        public static final String BLANK_OBJECT_TYPE_CODE = "--"; // Max length is 4 for this field
        public static final String GL_PE_OFFSET_STRING = "TP Generated Offset";
        public static final int GLPE_DESCRIPTION_MAX_LENGTH = 40;
    }

    // Object Type Code Constants
    public static final class OBJECT_TYPE_CODE {
        public static final String DOCUMENT_TYPE_INTERNAL_BILLING = "IB";
        public static final String DOCUMENT_TYPE_DISTRIBUTION_OF_INCOME_AND_EXPENSE = "DI";
        public static final String DOCUMENT_TYPE_SERVICE_BILLING = "SB";
        public static final String DOCUMENT_TYPE_NON_CHECK_DISBURSEMENT = "NC";
        public static final String DOCUMENT_TYPE_INDIRECT_COST_ADJUSTMENT = "ICA";
    }

    // Object Sub-Type Code Constants
    public static final class OBJECT_SUB_TYPE_CODE {
        public static final String NON_MANDATORY_TRANSFER = "TN";
        public static final String MANDATORY_TRANSFER = "MT";
        public static final String ART_AND_MUSEUM = "AM";
        public static final String ASSESSMENT = "AS";
        public static final String ACCOUNT_SUMMARY_OBJECT_CODE = "AC";
        public static final String ACCOUNTS_RECEIVABLE = "AR";
        public static final String BLDG = "BD";
        public static final String BLDG_FED_FUNDED = "BF";
        public static final String BUDGET_ONLY = "BU";
        public static final String CASH = "CA";
        public static final String COST_RECOVERY_EXPENSE = "CE";
        public static final String CAP_MOVE_EQUIP_FED_FUND = "CF";
        public static final String CAP_LEASE_PURCHASE = "CL";
        public static final String CAP_MOVE_EQUIP = "CM";
        public static final String CAP_MOVE_EQUIP_OTHER_OWN = "CO";
        public static final String CONSTRUCTION_IN_PROG = "CP";
        public static final String CAPITAL_LEASE = "C1";
        public static final String DEPRECIATION = "DR";
        public static final String EQUIP_STARTUP_COSTS = "ES";
        public static final String SUBTYPE_FUND_BALANCE = "FB";
        public static final String FRINGE_BEN = "FR";
        public static final String HOURLY_WAGES = "HW";
        public static final String INFRASTRUCTURE = "IF";
        public static final String INVEST = "IV";
        public static final String LAND = "LA";
        public static final String LOSS_DISPOSAL_ASSETS = "LD";
        public static final String LOSS_ON_DISPOSAL_OF_ASSETS = "LD";
        public static final String LEASE_IMPROVEMENTS = "LE";
        public static final String LIBRARY_ACQ = "LI";
        public static final String LIBRARY_ACQ_FED_FUND = "LF";
        public static final String LOSS_ON_RETIREMENT_OF_ASSETS = "LO";
        public static final String NOT_APPLICABLE = "NA";
        public static final String OTHER_PROVISIONS = "OP";
        public static final String PLANT_INDEBT = "PI";
        public static final String PLANT = "PL";
        public static final String RESERVES = "RE";
        public static final String SALARIES = "SA";
        public static final String STATE_APP = "ST";
        public static final String SALARIES_WAGES = "SW";
        public static final String TRANSFER_OF_FUNDS = "TF";
        public static final String UNIV_CONSTRUCTED = "UC";
        public static final String UNIV_CONSTRUCTED_FED_FUND = "UF";
        public static final String UNIV_CONSTRUCTED_FED_OWN = "UO";
        public static final String VALUATIONS_AND_ADJUSTMENTS = "VA";
        public static final String WRITE_OFF = "WO";
        public static final String STUDENT_FEES = "SF";
    }

    // Object Level Code Constants
    public static final class OBJECT_LEVEL_CODE {
        public static final String VALUATIONS_ADJUSTMENTS = "VADJ";
        public static final String CONTRACT_GRANTS = "C&G";
    }

    // Fund Group Code Constants
    public static final class FUND_GROUP_CODE {
        public static final String LOAN_FUND = "LF";
    }

    // Sub Fund Group Code Constants
    public static final class SUB_FUND_GROUP_CODE {
        public static final String CONTINUE_EDUC = "DCEDU";
        public static final String CODE_EXTAGY = "EXTAGY";
        public static final String RENEWAL_AND_REPLACEMENT = "PFRR";
        public static final String CODE_RETIRE_INDEBT = "PFRI";
        public static final String CODE_INVESTMENT_PLANT = "PFIP";
    }

    // Consolidated Object Code Constants
    public static final class CONSOLIDATED_OBJECT_CODE {
        public static final String ASSETS = "ASST";
        public static final String LIABILITIES = "LIAB";
        public static final String FUND_BALANCE = "FDBL";
    }

    // Object Code Constants
    public static final class OBJECT_CODE {
        public static final String BURSAR_ACCOUNTS_RECEIVABLES = "8160";
        public static final String PAYROLL_DEDUCTION_INTERNAL_BENEFITS_ACCOUNTS_RECEIVABLES = "8116";
        public static final String NON_STUDENT_ACCOUNTS_RECEIVABLES = "8118";
        public static final String UNAPPLIED_NON_STUDENT_ACCOUNTS_RECEIVABLES = "8015";
        public static final String NIH_MODULAR_DIRECT_COSTS = "5019";
    }

    // account constants
    public static final class ACCOUNT_NUMBER {
        public static final String BUDGET_LEVEL_NO_BUDGET = "N";
    }


    public static final class EXCEPTIONS {
        public static final String NULL_OBJECT_SUBTYPE_MESSAGE = "An illegal argument has been passed. Cannot allow (null) subtypes.";
    }
}
