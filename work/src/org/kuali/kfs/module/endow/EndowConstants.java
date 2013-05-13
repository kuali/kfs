/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

public class EndowConstants {

    public static final String KFS_ENDOW_ROLE_NAMESPACE = "KFS-ENDOW";

    public static final String YES = "Y";
    public static final String NO = "N";

    public static final int NUMBER_OF_MONTHS_REMAINING = 6;
    public static final int NUMBER_OF_DAYS_IN_YEAR = 365;
    public static final int MAXIMUM_NUMBER_OF_LINES_PER_EDOC = 1000;
    public static final int MAXMUM_NUMBER_OF_EDOC_INITIALIZATION_TRY = 10;

    public static final int SHORT_VS_LONG_TERM_PERIOD = 12;

    public static final String NEW_SOURCE_TRAN_LINE_PROPERTY_NAME = "newSourceTransactionLine";
    public static final String NEW_TARGET_TRAN_LINE_PROPERTY_NAME = "newTargetTransactionLine";
    public static final String NEW_SOURCE_ACC_LINE_PROPERTY_NAME = "newSourceAccouningLine";
    public static final String NEW_TARGET_ACC_LINE_PROPERTY_NAME = "newTargetAccountingLine";

    public static final String SOURCE_TRANSACTION_LINE_GROUP_LABEL_NAME = "From";
    public static final String TARGET_TRANSACTION_LINE_GROUP_LABEL_NAME = "To";
    public static final String SOURCE_TAX_LOTS_LABEL_NAME = "From";
    public static final String TARGET_TAX_LOTS__LABEL_NAME = "To";

    // labels for transaction lines for Corpus Adjustment, Unit/Share Adjustment
    public static final String DECREASE_TRANSACTION_LINE_GROUP_LABEL_NAME = "Decrease";
    public static final String INCREASE_TRANSACTION_LINE_GROUP_LABEL_NAME = "Increase";

    public static final String DECREASE_TAX_LOTS_LABEL_NAME = "Decrease";
    public static final String INCREASE_TAX_LOTS_LABEL_NAME = "Increase";

    public static class SecurityReportingGroups {
        public static final String CASH_EQUIVALENTS = "CSHEQ";
    }

    public static class KemToGLInterfaceBatchProcess {
        public static final String DATA_FILE_SUFFIX = ".data";
        public static final String RECON_FILE_SUFFIX = ".recon";
        public static final String KEM_TO_GL_ACTIVITY_OUTPUT_DATA_FILE = "endow_endowentry_entp";
        public static final String KEM_TO_GL_ACTIVITY_OUTPUT_RECONCILE_FILE = "endow_endowentry_entp";
        public static final String SYSTEM_ORIGINATION_CODE_FOR_ENDOWMENT = "EM";
        public static final String DEBIT_CODE = "D";
        public static final String CREDIT_CODE = "C";
        public static final String SUB_TYPE_CASH = "Cash";
        public static final String SUB_TYPE_NON_CASH = "Non-Cash";
    }
    
    public static class EndowmentTransactionTypeCodes {
        public static final String ASSET_TYPE_CODE = "A";
        public static final String LIABILITY_TYPE_CODE = "L";
        public static final String INCOME_TYPE_CODE = "I";
        public static final String EXPENSE_TYPE_CODE = "E";
    }

    public static class FeeType {
        public static final String FEE_TYPE_CODE_FOR_PAYMENTS = "P";
    }

    public static class ClassCodeTypes {
        public static final String ALTERNATIVE_INVESTMENT = "A";
        public static final String CASH_EQUIVALENTS = "C";
        public static final String BOND = "B";
        public static final String LIABILITY = "L";
        public static final String POOLED_INVESTMENT = "P";
        public static final String STOCKS = "S";
        public static final String OTHER = "O";
    }

    // Frequency Codes
    public static class FrequencyCodes {
        public static final String SEMI_ANNUAL_FREQUENCY_CODE_FOR_NEXT_PROCESS_DUE_DATE = "IJME";
        public static final String QUARTERLY_FREQUENCY_CODE_FOR_NEXT_PROCESS_DUE_DATE = "QJME";
    }

    public static class FrequencyTypes {
        public static final String DAILY = "D";
        public static final String WEEKLY = "W";
        public static final String SEMI_MONTHLY = "S";
        public static final String MONTHLY = "M";
        public static final String QUARTERLY = "Q";
        public static final String SEMI_ANNUALLY = "I";
        public static final String ANNUALLY = "A";
    }

    public static class FrequencyMonths {
        public static final String JANUARY = "J";
        public static final String FEBRUARY = "F";
        public static final String MARCH = "M";
        public static final String APRIL = "A";
        public static final String MAY = "Y";
        public static final String JUNE = "U";
        public static final String JULY = "L";
        public static final String AUGUST = "G";
        public static final String SEPTEMBER = "S";
        public static final String OCTOBER = "O";
        public static final String NOVEMBER = "N";
        public static final String DECEMBER = "D";
    }

    public static class FrequencyWeekDays {
        public static final String MONDAY = "MON";
        public static final String TUESDAY = "TUE";
        public static final String WEDNESDAY = "WED";
        public static final String THURSDAY = "THU";
        public static final String FRIDAY = "FRI";
    }

    public static class FrequencyMonthly {
        public static final String DATE = "DT";
        public static final String MONTH_END = "ME";
        public static final String THIRD_FRIDAY = "TH";
    }

    // Acrrual Method values
    public static class AccrualMethod {
        public static final String AUTOMATED_CASH_MANAGEMENT = "A";
        public static final String MORTGAGE_30 = "3";
        public static final String MORTGAGE_60 = "6";
        public static final String DISCOUNT_BONDS = "B";
        public static final String TIME_DEPOSITS = "M";
        public static final String TREASURY_NOTES_AND_BONDS = "T";
        public static final String DIVIDENDS = "D";
        public static final String NONR = "N";
    }

    public static class ValuationMethod {
        public static final String UNITS = "U";
        public static final String MARKET = "M";
    }

    public static class Scale {
        public static final int SECURITY_UNIT_VALUE = 5;
        public static final int SECURITY_MARKET_VALUE = 2;
        public static final int SECURITY_INCOME_RATE = 5;
    }

    public static class IncomePrincipalIndicator {
        public static final String INCOME = "I";
        public static final String PRINCIPAL = "P";
    }

    public static final String LOOKUP_LINK = "Lookup";

    public static class KemidValueOptions {
        public static final String MANUAL = "Manual";
        public static final String AUTOMATIC = "Automatic";
    }

    public static class TaxLotsAccountingMethodOptions {
        public static final String AVERAGE_BALANCE = "Average Balance";
        public static final String FIFO = "FIFO";
        public static final String LIFO = "LIFO";
    }


    public final static KualiInteger ZERO = new KualiInteger(0);
    public final static KualiInteger ONE = new KualiInteger(1);

    /**
     * Preset Values These will be here until we can come up with a better solution.
     */

    // Agreement Status Code
    public static class AgreementStatusCode {
        public static final String AGRMNT_STAT_CD_COMP = "COMP";
        public static final String AGRMNT_STAT_CD_NONE = "NONE";
        public static final String AGRMNT_STAT_CD_PEND = "PEND";
    }

    // Agreement Special Instruction Code
    public static class AgreementSpecialInstructionCode {
        public static final String AGRMNT_SPCL_INSTRC_CD_NONE = "0";
    }

    // Transaction Restriction Code
    public static class TransactionRestrictionCode {
        public static final String TRAN_RESTR_CD_NDISB = "NDISB";
        public static final String TRAN_RESTR_CD_NTRAN = "NTRAN";
        public static final String TRAN_RESTR_CD_NONE = "NONE";
    }

    // Type Restriction Code for preset values
    public static class TypeRestrictionPresetValueCodes {
        public static final String PERMANENTLY_RESTRICTED_DEFAULT_INDICATOR = "Y";
        public static final String PERMANENT_TYPE_RESTRICTION_CODE = "P";
        public static final String INCOME_TYPE_RESTRICTION_CODE = "I";
        public static final String UNRESTRICTED_TYPE_RESTRICTION_CODE = "U";
        public static final String NOT_APPLICABLE_TYPE_RESTRICTION_CODE = "NA";
        public static final String TEMPORARY_RESTRICTED_TYPE_RESTRICTION_CODE = "T";
        public static final String TYPE_RESTRICTION_PERM = "permanentIndicator";
        public static final String TYPE_RESTRICTION_ACTIVE_INDICATOR = "active";
        public static final String DEFAULT_PERMANENT_INDICATOR = "N";
    }

    // Fee Method values...
    public static class FeeMethod {
        public static final String FEE_TYPE_CODE_VALUE_FOR_TRANSACTIONS = "T";
        public static final String FEE_TYPE_CODE_VALUE_FOR_PAYMENTS = "P";
        public static final String FEE_TYPE_CODE_VALUE_FOR_BALANCES = "B";
        public static final String FEE_METHOD_TAB_ID = "Edit Fee Method";
        public static final String CLASS_CODES_TAB_ID = "Class Codes";
        public static final String SECURITY_TAB_ID = "Security";
        public static final String PAYMENT_TYPES_TAB_ID = "Payment Types";
        public static final String TRANSACTION_TYPES_TAB_ID = "Transaction Types";
        public static final String ENDOWMENT_TRANSACTION_CODES_TAB_ID = "Endowment Transaction Codes";
        public static final String FEE_BASE_CD_VALUE = "I";
        public static final String FEE_RATE_DEFINITION_CODE_FOR_COUNT = "C";
        public static final String FEE_RATE_DEFINITION_CODE_FOR_VALUE = "V";
        public static final KualiDecimal FEE_RATE_DEFAULT_VALUE = new KualiDecimal("99999999999999999.99");
        public static final int FEE_RATE_MAX_SCALE = 4;
        public static final String ENDOWMENT_HISTORY_VALUE_ADJUSTMENT = "EHVA";
        public static final String FEE_BASE_CODE_VALUE_FOR_INCOME = "I";
        public static final String FEE_BASE_CODE_VALUE_FOR_PRINCIPAL = "P";
        public static final String FEE_BASE_CODE_VALUE_FOR_INCOME_AND_PRINCIPAL = "B";
    }

    // Fee Balances Types values...
    public static class FeeBalanceTypes {
        public static final String FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS = "AU";
        public static final String FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_UNITS = "CU";
        public static final String FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_UNITS = "MU";
        public static final String FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE = "AMV";
        public static final String FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_MARKET_VALUE = "CMV";
        public static final String FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_MARKET_VALUE = "MMV";
    }

    public static class Sequences {
        public static final String END_KEMID_SEQ = "END_KEMID_SEQ";
        public static final String END_TICKLER_SEQ = "END_TKLR_SEQ";
        public static final String END_ACI_SEQ = "END_ACI_SEQ";
        public static final String END_REC_CSH_XFR_SEQ = "END_REC_CSH_XFR_SEQ";
        public static final String END_CASH_SWEEP_SEQ = "END_CASH_SWEEP_SEQ";
    }

    // Transactions

    public static class TransactionSourceTypeCode {
        public static final String MANUAL = "M";
        public static final String AUTOMATED = "A";
        public static final String RECURRING = "R";
    }

    public static final String PRINCIPAL = "Principal";
    public static final String INCOME = "Income";

    public static final String UNITS_TOTALING_EDITING_MODE = "unitsTotaling";

    public static final String EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME = "sourceTransactionLine";
    public static final String EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME = "targetTransactionLine";

    // public static class TransactionTabErrors {
    public static final String SECURITY_ERRORS = "securityErrors";
    // }
    public static final String TRANSACTION_LINE_TYPE_SOURCE = "F";
    public static final String TRANSACTION_LINE_TYPE_TARGET = "T";

    public static final String TRANSACTION_SECURITY_TYPE_SOURCE = "F";
    public static final String TRANSACTION_SECURITY_TYPE_TARGET = "T";

    public static final class TransactionSubTypeCode {
        public static final String CASH = "C";
        public static final String NON_CASH = "N";
    }

    public static final String TRANSACTION_DETAILS_ERRORS = "document.transactionSubTypeCode";
    public static final String SOURCE_TRANSACTION_LINES_ERRORS = "newSourceTransactionLine*,document.sourceTransactionLines*";
    public static final String TARGET_TRANSACTION_LINES_ERRORS = "newTargetTransactionLine*,document.targetTransactionLines*";
    public static final String TRANSACTION_LINE_ERRORS = "document.transactionLines";
    public static final String TRANSACTION_SECURITY_TAB_ERRORS = "document.sourceTransactionSecurity*,document.targetTransactionSecurity*,*registrationCode";
    public static final String ACCOUNTING_LINE_ERRORS = "document.accountingLines";
    public static final String SOURCE_ACCOUNTING_LINES_ERRORS = "newSourceAccountingLine*,document.sourceAccountingLines*";
    public static final String TARGET_ACCOUNTING_LINES_ERRORS = "newTargetAccountingLine*,document.targetAccountingLines*";

    public static final class HistoryHoldingValueAdjustmentValuationCodes {
        public static final String HISTORY_VALUE_ADJUSTMENT_VALUATION_METHOD_FOR_UNIT_VALUE = "U";
        public static final String HISTORY_VALUE_ADJUSTMENT_VALUATION_METHOD_FOR_MARKET_VALUE = "M";
        public static final String HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS = "document.holdingHistoryValueAdjustment.";
    }

    // Consolidated Object Code Constants
    public static final class ConsolidatedObjectCode {
        public static final String ASSETS = "ASST";
        public static final String LIABILITIES = "LIAB";
        public static final String FUND_BALANCE = "FDBL";
    }

    public static final class ObjectTypeCode {
        public static final String EXPENSE_NOT_EXPENDITURE = "ES";
        public static final String INCOME_NOT_CASH = "IC";
    }

    public static final class HoldingTaxLotRebalanceCodes {
        public static final String RESULTS_ACTIONS_LINK = "Rebalance";
    }

    // Transaction Types in Recurring Cash Transfer doc
    public static final String ENDOWMENT_CASH_TRANSFER_TRANSACTION_TYPE = "ECT";
    public static final String ENDOWMENT_GENERAL_LEDGER_CASH_TRANSFER_TRANSACTION_TYPE = "EGLT";
    public static final String ENDOWMENT_RECURRING_CASH_TRANSFER_DOCUMENT_TYPES = "DOCUMENT_TYPES";


    // HoldingHistoryValueAdjustmentDocument constants
    public static final class HoldingHistoryValueAdjustmentDocument {
        public static final String TRANSACTION_POSTED_NO = "N";
        public static final String TRANSACTION_POSTED_YES = "Y";
    }

    // Document Type Codes
    public static final class DocumentTypeNames {
        public static final String ENDOWMENT_ASSET_INCREASE = "EAI";
        public static final String ENDOWMENT_ASSET_DECREASE = "EAD";
        public static final String ENDOWMENT_CASH_INCREASE = "ECI";
        public static final String ENDOWMENT_CASH_DECREASE = "ECDD";
        public static final String ENDOWMENT_CASH_TRANSFER = "ECT";
        public static final String ENDOWMENT_LIABILITY_INCREASE = "ELI";
        public static final String ENDOWMENT_LIABILITY_DECREASE = "ELD";
        public static final String ENDOWMENT_HOLDING_ADJUSTMENT = "EHA";
        public static final String ENDOWMENT_SECURITY_TRANSFER = "EST";
        public static final String ENDOWMENT_CORPORATE_REORGANZATION = "ECR";
        public static final String ENDOWNENT_HOLDING_VALUE_ADJUSTMENT = "EHVA";
        public static final String ENDOWMENT_TO_GENERAL_LEDGER_TRANSFER = "EGLT";
        public static final String GENERAL_LEDGER_TO_ENDOWMENT_TRANSFER = "GLET";
        public static final String ENDOWMENT_CORPUS_ADJUSTMENT = "ECA";
        public static final String ENDOWMENT_UNIT_SHARE_ADJUSTMENT = "EUSA";
        
    }
    
    public static final String KUALI_FREQUENCY_LOOKUPABLE_IMPL = "frequencyCodeLookupable";
    
    public static final class EndowmentReport {
        public static final String ENDOWMENT = "Endowment";
        public static final String NON_ENDOWED = "Non-Endowed";
        public static final String BOTH_ENDOWMENT_OPTION = "Both";
        public static final String DETAIL_REPORT = "Detail";
        public static final String TOTAL_REPORT = "Total";
        public static final String BOTH_REPORT_OPTION = "Both";
        public static final String DETAIL = "D";
        public static final String TOTAL = "T";
        public static final String BOTH = "B";
    }
}
