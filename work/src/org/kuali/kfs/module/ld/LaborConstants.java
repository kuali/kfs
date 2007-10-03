/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.bo.OriginEntryFull;

/**
 * This class contains the constants used by Labor Distribution.
 */
public class LaborConstants {
    public static class PayrollDocumentTypeCode {
        public static final String NORMAL_PAY = "PAY";
        public static final String RETROACTIVE_ADJUSTMENT = "RETR";

        public static final String ENCUMBRANCE = "PAYE";
        public static final String CHECK_CANCELLATION = "PAYC";
        public static final String OVERPAYMENT = "OPAY";

        public static final String HAND_DRAWN_CHECK = "HDRW";
        public static final String ACCRUALS = "PAYA";
        public static final String ACCRUALS_REVERSAL = "PAYN";

        public static final String EXPENSE_TRANSFER_ST = "ST";
        public static final String EXPENSE_TRANSFER_BT = "BT";
        public static final String EXPENSE_TRANSFER_ET = "ET";
        public static final String EXPENSE_TRANSFER_SACH = "SACH";
        public static final String EXPENSE_TRANSFER_YEST = "YEST";
        public static final String EXPENSE_TRANSFER_YEBT = "YEBT";
    }

    public static class DestinationNames {
        public static final String LEDGER_BALANCE = "LD_LDGR_BAL_T";
        public static final String LEDGER_ENRTY = "LD_LDGR_ENTR_T";
        public static final String LABOR_GL_ENTRY = "LD_LBR_GL_ENTRY_T";
        public static final String ORIGN_ENTRY = "LD_LBR_ORIGIN_ENTRY_T";
    }

    public static class TransactionGeneratorNames {
        public static final String SALARY_ACCRUAL = "SALARY ACCRUAL";
        public static final String BENEFIT_ACCRUAL = "BENEFIT ACCRUAL";
        public static final String SALARY_REVERSAL = "SALARY REVERSAL";
        public static final String BENEFIT_REVERSAL = "BENEFIT REVERSAL";
        public static final String ENCUMBRANCE = "ENCUMBRANCE";
        public static final String DISENCUMBRANCE = "DISENCUMBRANCE";
    }

    public static List<String> consolidationAttributesOfOriginEntry() {
        List<String> consolidationAttributes = new ArrayList<String>();

        consolidationAttributes.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        consolidationAttributes.add(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);

        consolidationAttributes.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        consolidationAttributes.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        consolidationAttributes.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        consolidationAttributes.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        consolidationAttributes.add(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER);

        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        consolidationAttributes.add(KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        consolidationAttributes.add(KFSPropertyConstants.PROJECT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
        consolidationAttributes.add(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);

        consolidationAttributes.add(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);

        return consolidationAttributes;
    }

    public static class SalaryExpenseTransfer {
        public static final String LABOR_LEDGER_SALARY_CODE = "S";
        public static final String SET_ADMIN_WORKGROUP_PARM_NM = "SALARY_EXPENSE_TRANSFER_ADMINISTRATORS_GROUP";
        public static final String BENEFIT_CLEARING_CHART_PARM_NM = "BENEFIT_CLEARING_CHART_OF_ACCOUNTS";
        public static final String BENEFIT_CLEARING_ACCOUNT_PARM_NM = "BENEFIT_CLEARING_ACCOUNT_NUMBER";
    }

    public static class BenefitExpenseTransfer {
        public static final String LABOR_LEDGER_BENEFIT_CODE = "F";
    }

    public static class BalanceInquiries {
        public static final String ERROR_INVALID_LABOR_OBJECT_CODE = "error.labor.invalidLaborObjectCodeError";
        public static final String[] VALID_LABOR_OBJECT_CODES = { "2000", "2001", "2400", "2401", "2500" };
        public static final int laborLowValueObjectCode = 2000;
        public static final int laborHighValueObjectCode = 5000;
        public static final String UnknownPersonName = "* Unknown Name *";
        public static final String ANNUAL_BALANCE = "ACLN_ANNL_BAL_AMT";
        public static final String CONTRACT_GRANT_BB_AMOUNT = "CONTR_GR_BB_AC_AMT";
        public static final String ENCUMBERENCE_CODE = "IE";
        public static final String ACTUALS_CODE = "AC";
        public static final String BALANCE_CODE = "BB";
        public static final String EMPLOYEE_FUNDING_EXPENSE_OBJECT_TYPE_CODE = "ES";
        public static final String EMPLOYEE_FUNDING_NORMAL_OP_EXPENSE_OBJECT_TYPE_CODE = "EX";
        public static final String LEDGER_ENTRY_LOOKUPABLE_FOR_EXPENSE_TRANSFER = "laborLedgerEntryForExpenseTransferLookupable";
    }

    public static class ParameterGroups {
        public static final String PAYROLL_ACCRUAL = "Labor.PayrollAccrual";
        public static final String YEAR_END = "Labor.YearEnd";
        public static final String POSTER = "Labor.Poster";
    }

    public static class PayrollAccrual {
        public static final String ACCRUAL_DAYS = "NUMBER_OF_DAYS_TO_ACCRUE";
        public static final String WORK_DAYS = "PAY_PERIOD_WORK_DAYS";
        public static final String FISCAL_YEAR = "FISCAL_YEAR_SELECTION";
        public static final String FISCAL_PERIOD = "FISCAL_PERIOD_SELECTION";
        public static final String REVERSAL_FISCAL_YEAR = "FISCAL_YEAR_REVERSAL";
        public static final String REVERSAL_FISCAL_PERIOD = "FISCAL_PERIOD_REVERSAL";
        public static final String DOCUMENT_NUMBER = "DOCUMENT_NUMBER";
        public static final String EARN_CODES = "EARN_CODE_SELECTION";
        public static final String RUN_ID = "DOCUMENT_NUMBER_SELECTION";
        public static final String EXCLUDED_SUB_FUND_GROUPS = "SUB_FUND_GROUP_SELECTION";

        public static final String ORIGINATION_CODE = "ORIGINATION";
    }

    public static class Poster {
        public static final String PERIOD_CODES_NOT_PROCESSED = "SENT_TO_GL_PERIOD_CODES";
        public static final String OBJECT_CODES_NOT_PROCESSED = "SENT_TO_GL_OBJECT_CODES";
        public static final String BALANCE_TYPES_NOT_PROCESSED = "SENT_TO_GL_BALANCE_TYPES";
    }

    public static class Scrubber {
        public static final String PARAMETER_GROUP = "LaborScrubberStep";
        public static final String SUBFUND_WAGE_EXCLUSION_PARAMETER = "SUB_FUND_GROUPS_NOT_ACCEPTING_WAGES_IND";
        public static final String ACCOUNT_FRINGE_EXCLUSION_PARAMETER = "ACCOUNTS_NOT_ACCEPTING_FRINGES_IND";
        public static final String SUSPENSE_ACCOUNT_LOGIC_PARAMETER = "SUSPENSE_ACCOUNT_LOGIC_IND";
        public static final String CONTINUATION_ACCOUNT_LOGIC_PARAMETER = "CONTINUATION_ACCOUNT_LOGIC_IND";
    }

    public static class YearEnd {
        public static final String FUND_GROUP_PROCESSED = "FUND_GROUPS";
        public static final String SUB_FUND_GROUP_PROCESSED = "SUB_FUND_GROUPS";
        public static final String ORIGINATION_CODE = "ORIGINATION";
        public static final String OLD_FISCAL_YEAR = "FISCAL_YEAR_SELECTION";
    }

    public static final class LABOR_LEDGER_PENDING_ENTRY_CODE {
        public static final String NO = "N";
        public static final String YES = "Y";
        public static final String BLANK_PROJECT_STRING = "----------"; // Max length is 10 for this field
        public static final String BLANK_SUB_OBJECT_CODE = "---"; // Max length is 3 for this field
        public static final String BLANK_SUB_ACCOUNT_NUMBER = "-----"; // Max length is 5 for this field
        public static final String BLANK_OBJECT_CODE = "----"; // Max length is 4 for this field
        public static final String BLANK_OBJECT_TYPE_CODE = "--"; // Max length is 4 for this field
        public static final String BLANK_POSITION_NUMBER = "--------"; // Max length is 8 for this field
        public static final String BLANK_EMPL_ID = "-----------"; // Max length is 11 for this field
        public static final String LL_PE_OFFSET_STRING = "TP Generated Offset";
        public static final int LLPE_DESCRIPTION_MAX_LENGTH = 40;
    }

    public static final String LABOR_LEDGER_CHART_OF_ACCOUNT_CODE = "UA";
    public static final int TRANSACTION_DESCRIPTION_MAX_LENGTH = 40;

    public enum JournalVoucherOffsetType {
        NO_OFFSET("LLJV", "No Offset", "LABOR LEDGER JOUNAL VOUCHER"), ACCRUAL("LJVA", "Accrual", "LLJV ACCRUALS OFFSET"), CASH("LJVC", "Cash", "LLJV CASH OFFSET"), ENCUMBRANCE("LJVE", "Encumbrance", "LLJV ENCUMBRANCE OFFSET");

        public String typeCode;
        public String description;
        public String longDescription;

        private JournalVoucherOffsetType(String typeCode, String description, String longDescription) {
            this.typeCode = typeCode;
            this.description = description;
            this.longDescription = longDescription;
        }
    }

    public static class LookupableBeanKeys {
        static final public String PENDING_ENTRY = "laborPendingEntryLookupable";
        static final public String CSF_TRACKER = "laborCalculatedSalaryFoundationTrackerLookupable";
    }

    public static final Map<String, String> periodCodeMapping = new HashMap<String, String>();
    static {
        periodCodeMapping.put(KFSPropertyConstants.MONTH1_AMOUNT, KFSConstants.MONTH1);
        periodCodeMapping.put(KFSPropertyConstants.MONTH2_AMOUNT, KFSConstants.MONTH2);
        periodCodeMapping.put(KFSPropertyConstants.MONTH3_AMOUNT, KFSConstants.MONTH3);
        periodCodeMapping.put(KFSPropertyConstants.MONTH4_AMOUNT, KFSConstants.MONTH4);
        periodCodeMapping.put(KFSPropertyConstants.MONTH5_AMOUNT, KFSConstants.MONTH5);
        periodCodeMapping.put(KFSPropertyConstants.MONTH6_AMOUNT, KFSConstants.MONTH6);
        periodCodeMapping.put(KFSPropertyConstants.MONTH7_AMOUNT, KFSConstants.MONTH7);
        periodCodeMapping.put(KFSPropertyConstants.MONTH8_AMOUNT, KFSConstants.MONTH8);
        periodCodeMapping.put(KFSPropertyConstants.MONTH9_AMOUNT, KFSConstants.MONTH9);
        periodCodeMapping.put(KFSPropertyConstants.MONTH10_AMOUNT, KFSConstants.MONTH10);
        periodCodeMapping.put(KFSPropertyConstants.MONTH11_AMOUNT, KFSConstants.MONTH11);
        periodCodeMapping.put(KFSPropertyConstants.MONTH12_AMOUNT, KFSConstants.MONTH12);
        periodCodeMapping.put(KFSPropertyConstants.MONTH13_AMOUNT, KFSConstants.MONTH13);
    }

    public static final String LONG_ROW_TABLE_INRUIRY_ACTION = "laborLongRowTableInquiry.do";
    public static final String LABOR_INQUIRY_OPTIONS_SERVICE = "laborInquiryOptionsService";
    public static final String BASE_FUNDS_LOOKUP_HELPER_SRVICE_NAME = "BaseFundsLookupableHelperService";
    public static final String CSF_TRACKER_LOOKUP_HELPER_SRVICE_NAME = "laborCalculatedSalaryFoundationTrackerLookupableHelperService";
    public static final String CURRENT_FUNDS_LOOKUP_HELPER_SRVICE_NAME = "CurrentFundsLookupableHelperService";
    public static final String EXPENSE_TRANSFER_ACCOUNTING_LINE_SERVIOCE_NAME = "expenseTransferAccountingLineService";
    public static final String DASHES_DELETE_CODE = "-";
    public static final int LLCP_MAX_LENGTH = 294;
    private static String SPACE_TRANSACTION_DATE = null;

    public static String getSpaceTransactionDate() {
        if (SPACE_TRANSACTION_DATE == null) {
            SPACE_TRANSACTION_DATE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeSize(OriginEntryFull.class, KFSPropertyConstants.TRANSACTION_DATE), ' ');
        }
        return SPACE_TRANSACTION_DATE;
    }
}
