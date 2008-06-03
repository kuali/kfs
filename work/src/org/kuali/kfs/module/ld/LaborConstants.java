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
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.labor.bo.LaborOriginEntry;

/**
 * Global constancts for labor distribution.
 */
public class LaborConstants {
    public static class BalanceInquiries {
        public static final String ANNUAL_BALANCE = "ACLN_ANNL_BAL_AMT";
        public static final String BALANCE_TYPE_AC_AND_A21 = "AC&A2";
        public static final String CONTRACT_GRANT_BB_AMOUNT = "CONTR_GR_BB_AC_AMT";
        public static final String EMPLOYEE_FUNDING_EXPENSE_OBJECT_TYPE_CODE = "ES";
        public static final String EMPLOYEE_FUNDING_NORMAL_OP_EXPENSE_OBJECT_TYPE_CODE = "EX";
        public static final int laborHighValueObjectCode = 5000;
        public static final int laborLowValueObjectCode = 2000;
        public static final String LEDGER_ENTRY_LOOKUPABLE_FOR_EXPENSE_TRANSFER = "laborLedgerEntryForExpenseTransferLookupable";
        public static final String UnknownPersonName = "* Unknown Name *";
    }

    public static class BenefitExpenseTransfer {
        public static final String LABOR_LEDGER_BENEFIT_CODE = "F";
    }

    public static class DestinationNames {
        public static final String LABOR_GL_ENTRY = "LD_LBR_GL_ENTRY_T";
        public static final String LEDGER_BALANCE = "LD_LDGR_BAL_T";
        public static final String LEDGER_ENRTY = "LD_LDGR_ENTR_T";
        public static final String ORIGN_ENTRY = "LD_LBR_ORIGIN_ENTRY_T";
    }

    public enum JournalVoucherOffsetType {
        ACCRUAL("LJVA", "Accrual", "LLJV ACCRUALS OFFSET"), CASH("LJVC", "Cash", "LLJV CASH OFFSET"), ENCUMBRANCE("LJVE", "Encumbrance", "LLJV ENCUMBRANCE OFFSET"), NO_OFFSET("LLJV", "No Offset", "LABOR LEDGER JOUNAL VOUCHER");

        public String description;
        public String longDescription;
        public String typeCode;

        private JournalVoucherOffsetType(String typeCode, String description, String longDescription) {
            this.typeCode = typeCode;
            this.description = description;
            this.longDescription = longDescription;
        }
    }

    public static final class LABOR_LEDGER_PENDING_ENTRY_CODE {
        public static final String BLANK_EMPL_ID = "-----------"; // Max length is 11 for this field
        public static final String BLANK_OBJECT_CODE = "----"; // Max length is 4 for this field
        public static final String BLANK_OBJECT_TYPE_CODE = "--"; // Max length is 4 for this field
        public static final String BLANK_POSITION_NUMBER = "--------"; // Max length is 8 for this field
        public static final String BLANK_PROJECT_STRING = "----------"; // Max length is 10 for this field
        public static final String BLANK_SUB_ACCOUNT_NUMBER = "-----"; // Max length is 5 for this field
        public static final String BLANK_SUB_OBJECT_CODE = "---"; // Max length is 3 for this field
        public static final String LL_PE_OFFSET_STRING = "TP Generated Offset";
        public static final int LLPE_DESCRIPTION_MAX_LENGTH = 40;
        public static final String NO = "N";
        public static final String YES = "Y";
    }

    public static class LookupableBeanKeys {
        static final public String CSF_TRACKER = "laborCalculatedSalaryFoundationTrackerLookupable";
        static final public String PENDING_ENTRY = "laborPendingEntryLookupable";
    }

    public static class ParameterGroups {
        public static final String PAYROLL_ACCRUAL = "Labor.PayrollAccrual";
        public static final String POSTER = "Labor.Poster";
        public static final String YEAR_END = "Labor.YearEnd";
    }

    public static class PayrollDocumentTypeCode {
        public static final String ACCRUALS = "PAYA";
        public static final String ACCRUALS_REVERSAL = "PAYN";

        public static final String CHECK_CANCELLATION = "PAYC";
        public static final String ENCUMBRANCE = "PAYE";
        public static final String EXPENSE_TRANSFER_BT = "BT";

        public static final String EXPENSE_TRANSFER_ET = "ET";
        public static final String EXPENSE_TRANSFER_SACH = "SACH";
        public static final String EXPENSE_TRANSFER_ST = "ST";

        public static final String EXPENSE_TRANSFER_YEBT = "YEBT";
        public static final String EXPENSE_TRANSFER_YEST = "YEST";
        public static final String HAND_DRAWN_CHECK = "HDRW";
        public static final String NORMAL_PAY = "PAY";
        public static final String OVERPAYMENT = "OPAY";
        public static final String RETROACTIVE_ADJUSTMENT = "RETR";
    }

    public static class Poster {
        public static final String BALANCE_TYPES_NOT_PROCESSED = "SENT_TO_GL_BALANCE_TYPES";
        public static final String OBJECT_CODES_NOT_PROCESSED = "SENT_TO_POSTER_OBJECT_CODES";
        public static final String PERIOD_CODES_NOT_PROCESSED = "SENT_TO_GL_PERIOD_CODES";
    }

    public static class SalaryExpenseTransfer {
        public static final String BENEFIT_CLEARING_ACCOUNT_PARM_NM = "BENEFIT_CLEARING_ACCOUNT_NUMBER";
        public static final String BENEFIT_CLEARING_CHART_PARM_NM = "BENEFIT_CLEARING_CHART_OF_ACCOUNTS";
        public static final String LABOR_LEDGER_SALARY_CODE = "S";
        public static final String SET_ADMIN_WORKGROUP_PARM_NM = "SALARY_EXPENSE_TRANSFER_ADMINISTRATORS_GROUP";
        public static final String EFFORT_VALIDATION_OVERRIDE_QUESTION = "EffortValidationOverrideQuestion";
        public static final String VALIDATE_AGAINST_EFFORT_PARM_NM = "VALIDATE_AGAINST_EFFORT_CERTIFICATION_IND";
        public static final String EFFORT_ADMIN_WORKGROUP_PARM_NM = "EFFORT_CERTIFICATION_ADMIN_GROUP";
    }

    public static class Scrubber {
        public static final String ACCOUNT_FRINGE_EXCLUSION_PARAMETER = "ACCOUNTS_NOT_ACCEPTING_FRINGES_IND";
        public static final String CONTINUATION_ACCOUNT_LOGIC_PARAMETER = "CONTINUATION_ACCOUNT_LOGIC_IND";
        public static final String PARAMETER_GROUP = "LaborScrubberStep";
        public static final String SUBFUND_WAGE_EXCLUSION_PARAMETER = "SUB_FUND_GROUPS_NOT_ACCEPTING_WAGES_IND";
        public static final String SUSPENSE_ACCOUNT_LOGIC_PARAMETER = "SUSPENSE_ACCOUNT_LOGIC_IND";
        public static final String SUSPENSE_ACCOUNT = "SUSPENSE_ACCOUNT";
        public static final String SUSPENSE_CHART = "SUSPENSE_CHART";
        public static final String SUSPENSE_SUB_ACCOUNT = "SUSPENSE_SUB_ACCOUNT";
        public static final String CLOSED_FISCAL_PERIOD_BYPASS_BALANCE_TYPES = "CLOSED_FISCAL_PERIOD_BYPASS_BALANCE_TYPES";
        public static final String CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES = "CONTINUATION_ACCOUNT_BYPASS_ORIGINATIONS";
        public static final String CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES = "CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPES";
        public static final String NON_FRINGE_ACCOUNT_BYPASS_ORIGINATIONS = "NON_FRINGE_ACCOUNT_BYPASS_ORIGINATIONS";
        public static final String NON_WAGE_SUB_FUND_BYPASS_ORIGINATIONS = "NON_WAGE_SUB_FUND_BYPASS_ORIGINATIONS";
    }

    public static class TransactionGeneratorNames {
        public static final String BENEFIT_ACCRUAL = "BENEFIT ACCRUAL";
        public static final String BENEFIT_REVERSAL = "BENEFIT REVERSAL";
        public static final String DISENCUMBRANCE = "DISENCUMBRANCE";
        public static final String ENCUMBRANCE = "ENCUMBRANCE";
        public static final String SALARY_ACCRUAL = "SALARY ACCRUAL";
        public static final String SALARY_REVERSAL = "SALARY REVERSAL";
    }

    public static class YearEnd {
        public static final String FUND_GROUP_PROCESSED = "FUND_GROUPS";
        public static final String OLD_FISCAL_YEAR = "FISCAL_YEAR_SELECTION";
        public static final String ORIGINATION_CODE = "ORIGINATION";
        public static final String SUB_FUND_GROUP_PROCESSED = "SUB_FUND_GROUPS";
    }
    
    public static class PurgeJob{
        public static final String PURGE_LEDGER_BALANCE_YEAR= "PRIOR_TO_YEAR";
        public static final String PURGE_LEDGER_ENTRY_YEAR= "PRIOR_TO_YEAR";
    }

    public static final String BASE_FUNDS_LOOKUP_HELPER_SRVICE_NAME = "BaseFundsLookupableHelperService";

    public static final String CSF_TRACKER_LOOKUP_HELPER_SRVICE_NAME = "laborCalculatedSalaryFoundationTrackerLookupableHelperService";

    public static final String CURRENT_FUNDS_LOOKUP_HELPER_SRVICE_NAME = "CurrentFundsLookupableHelperService";
    public static final String DASHES_DELETE_CODE = "-";

    public static final String EMPLOYEE_LOOKUP_ERRORS = "document.employeeLookups";
    public static final String EXPENSE_TRANSFER_ACCOUNTING_LINE_SERVIOCE_NAME = "expenseTransferAccountingLineService";
    public static final String LABOR_INQUIRY_OPTIONS_SERVICE = "laborInquiryOptionsService";
    public static final int LLCP_MAX_LENGTH = 294;
    public static final String LONG_ROW_TABLE_INRUIRY_ACTION = "laborLongRowTableInquiry.do";
    public static final Map<String, String> periodCodeMapping = new HashMap<String, String>();
    private static String SPACE_TRANSACTION_DATE = null;

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
        consolidationAttributes.add(KFSPropertyConstants.PROJECT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
        consolidationAttributes.add(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);

        // Reference Document Type, Reference Origin Code, Reference Document Number, Reversal Date, and Encumbrance Update Code.
        consolidationAttributes.add(KFSPropertyConstants.REFERENCE_FINANCIAL_DOCUMENT_TYPE_CODE);
        consolidationAttributes.add(KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        consolidationAttributes.add(KFSPropertyConstants.REFERENCE_FINANCIAL_DOCUMENT_NUMBER);
        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE);
        consolidationAttributes.add(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD);

        consolidationAttributes.add(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);

        return consolidationAttributes;
    }

    public static String getSpaceTransactionDate() {
        if (SPACE_TRANSACTION_DATE == null) {
            SPACE_TRANSACTION_DATE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeSize(OriginEntryFull.class, KFSPropertyConstants.TRANSACTION_DATE), ' ');
        }
        return SPACE_TRANSACTION_DATE;
    }
    
    public static final String ANNUAL_CLOSING_DOCUMENT_TYPE_CODE = getAnnualClosingDocumentType();   
    private static String getAnnualClosingDocumentType() {
        return SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE);
    }

    public static final String[] ACCOUNT_FIELDS = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER };
    
    public static final String LABOR_OBJECT_SALARY_CODE = "S";
    
    private static String DASH_POSITION_NUMBER = null;

    public static String getDashPositionNumber() {
        if (DASH_POSITION_NUMBER == null) {
            DASH_POSITION_NUMBER = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(LaborOriginEntry.class, KFSPropertyConstants.POSITION_NUMBER), '-');
        }
        return DASH_POSITION_NUMBER;
    }

    private static String DASH_EMPLID = null;

    public static String getDashEmplId() {
        if (DASH_EMPLID == null) {
            DASH_EMPLID = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(LaborOriginEntry.class, KFSPropertyConstants.EMPLID), '-');
        }
        return DASH_EMPLID;
    }
}
