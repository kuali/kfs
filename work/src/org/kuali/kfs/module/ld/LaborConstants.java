/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;

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

    public static class ColumnNames {
        public static final String UNIVERSITY_FISCAL_YEAR = "UNIV_FISCAL_YR";
        public static final String TRANSACTION_LEDGER_ENTRY_AMOUNT = "TRN_LDGR_ENTR_AMT";
        public static final String POSITION_NUMBER = "POSITION_NBR";
        public static final String EMPLOYEE_IDENTIFIER = "EMPLID";
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

    public static class LookupableBeanKeys {
        static final public String CSF_TRACKER = "laborCalculatedSalaryFoundationTrackerLookupable";
        static final public String PENDING_ENTRY = "laborPendingEntryLookupable";
    }

    public static class ParameterGroups {
        public static final String PAYROLL_ACCRUAL = "Labor.PayrollAccrual";
        public static final String POSTER = "Labor.Poster";
        public static final String YEAR_END = "Labor.YearEnd";
    }

    public static class Poster {
        public static final String BALANCE_TYPES_NOT_PROCESSED = "SENT_TO_GL_BALANCE_TYPES";
        public static final String PERIOD_CODES_NOT_PROCESSED = "SENT_TO_GL_PERIOD_CODES";
    }

    public static class Balancing {
        public static final String NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE = "NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE";
        public static final String NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT = "NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT";
    }

    public static class SalaryExpenseTransfer {
        public static final String BENEFIT_CLEARING_ACCOUNT_PARM_NM = "BENEFIT_CLEARING_ACCOUNT_NUMBER";
        public static final String BENEFIT_CLEARING_CHART_PARM_NM = "BENEFIT_CLEARING_CHART_OF_ACCOUNTS";
        public static final String LABOR_LEDGER_SALARY_CODE = "S";
        public static final String EFFORT_VALIDATION_OVERRIDE_QUESTION = "EffortValidationOverrideQuestion";
        public static final String VALIDATE_AGAINST_EFFORT_PARM_NM = "VALIDATE_AGAINST_EFFORT_CERTIFICATION_IND";
    }

    public static class Scrubber {
        public static final String ACCOUNT_FRINGE_EXCLUSION_PARAMETER = "ACCOUNTS_NOT_ACCEPTING_FRINGES_IND";
        public static final String CONTINUATION_ACCOUNT_LOGIC_PARAMETER = "CONTINUATION_ACCOUNT_LOGIC_IND";
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

    public static class BatchFileSystem {
        static final public String EXTENSION = ".data";
        static final public String DONE_FILE_EXTENSION = ".done"; 

        static final public String NIGHTLY_OUT_FILE = "ld_labentry_kfs";
        static final public String BACKUP_FILE = "ld_ldbackup";
        static final public String PRE_SCRUBBER_FILE = "ld_prescrub";
        static final public String SCRUBBER_INPUT_FILE = "ld_sortscrb";

        static final public String SCRUBBER_VALID_OUTPUT_FILE = "ld_scrbout1";
        static final public String SCRUBBER_ERROR_OUTPUT_FILE = "ld_scrberr1";
        static final public String SCRUBBER_EXPIRED_OUTPUT_FILE = "ld_expaccts";

        static final public String SCRUBBER_ERROR_SORTED_FILE = "ld_sorterr1";
        static final public String DEMERGER_VAILD_OUTPUT_FILE = "ld_scrbout2";
        static final public String DEMERGER_ERROR_OUTPUT_FILE = "ld_scrberr2";

        static final public String POSTER_INPUT_FILE = "ld_sortpost";
        static final public String POSTER_VALID_OUTPUT_FILE = "ld_postout";
        static final public String POSTER_ERROR_OUTPUT_FILE = "ld_posterrs";

        static final public String LABOR_GL_ENTRY_FILE = "gl_glentry_lab";
        static final public String BALANCE_FORWARDS_FILE = "ld_balance_forwards";
        static final public String LABOR_ENTERPRISE_FEED = "ld_ldentry_entp";
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

    public static class PurgeJob {
        public static final String PURGE_LEDGER_BALANCE_YEAR = "PRIOR_TO_YEAR";
        public static final String PURGE_LEDGER_ENTRY_YEAR = "PRIOR_TO_YEAR";
    }
    
    public static final String BASE_FUNDS_LOOKUP_HELPER_SRVICE_NAME = "BaseFundsLookupableHelperService";

    public static final String CSF_TRACKER_LOOKUP_HELPER_SRVICE_NAME = "laborCalculatedSalaryFoundationTrackerLookupableHelperService";

    public static final String CURRENT_FUNDS_LOOKUP_HELPER_SRVICE_NAME = "CurrentFundsLookupableHelperService";
    public static final String DASHES_DELETE_CODE = "-";

    public static final String DOCUMENT_EMPLOYEE_ID_ERRORS = "document.emplid";
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
        return SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE);
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

    public static String getSpaceAllLaborOriginEntryFields() {

        List<AttributeDefinition> attributes = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(LaborOriginEntry.class.getName()).getAttributes();
        int totalLength = 0;
        for (AttributeDefinition attributeDefinition : attributes) {
            if (!(KFSPropertyConstants.ENTRY_GROUP_ID.equals(attributeDefinition.getName()) || KFSPropertyConstants.ENTRY_ID.equals(attributeDefinition.getName()))) {
                totalLength += attributeDefinition.getMaxLength();
            }
        }

        return StringUtils.rightPad("", totalLength, ' ');
    }


    public static final String LABOR_MODULE_CODE = "KFS-LD";

    public static class PermissionNames {
        public static final String OVERRIDE_TRANSFER_IMPACTING_EFFORT_CERTIFICATION = "Override Transfer Impacting Open Effort Certification";
    }
    
    public static class BenefitCalculation {
        public static final String ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_PARAMETER = "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND";
        public static final String MAX_NUMBER_OF_ERRORS_ALLOWED_PARAMETER = "MAX_NUMBER_OF_ERRORS_ALLOWED";
        public static final String GENERATE_FRINGE_BENEFIT_PARAMETER = "GENERATE_FRINGE_BENEFIT_IND";
        public static final String GENERATE_FRINGE_BENEFIT_ENCUMBRANCE_PARAMETER = "GENERATE_FRINGE_BENEFIT_ENCUMBRANCE_IND";
        public static final String DEFAULT_BENEFIT_RATE_CATEGORY_CODE_PARAMETER = "DEFAULT_BENEFIT_RATE_CATEGORY_CODE";
        // Constants for the Salary Benefit Offset
        public static final String LABOR_BENEFIT_CALCULATION_OFFSET_IND = "LABOR_BENEFIT_CALCULATION_OFFSET_IND";
        public static final String LABOR_BENEFIT_OFFSET_DOCTYPE = "LABOR_BENEFIT_OFFSET_DOCTYPE";
        public static final String ACCOUNT_CODE_OFFSET_PROPERTY_NAME = "accountCodeOffset";
        public static final String OBJECT_CODE_OFFSET_PROPERTY_NAME = "objectCodeOffset";
 
    }
}
