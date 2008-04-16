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
package org.kuali.module.gl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.AttributeDefinition;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.OriginEntryFull;


/**
 * This class holds constants specifically for use within the General Ledger module.
 */
public class GLConstants {

    public static final String INSERT_CODE = "I";
    public static final String UPDATE_CODE = "U";
    public static final String DELETE_CODE = "D";
    public static final String SELECT_CODE = "S";
    public static final String EMPTY_CODE = "";
    public static final String ERROR_CODE = "E";

    public static final String RETAIN_DAYS = "RETAIN_DAYS";

    public static class DummyBusinessObject {
        static final public String COST_SHARE_OPTION = "dummyBusinessObject.costShareOption";
        static final public String PENDING_ENTRY_OPTION = "dummyBusinessObject.pendingEntryOption";
        static final public String CONSOLIDATION_OPTION = "dummyBusinessObject.consolidationOption";
        static final public String LINK_BUTTON_OPTION = "dummyBusinessObject.linkButtonOption";
    }

    public static class PendingEntryOptions {
        static final public String APPROVED = "Approved";
        static final public String ALL = "All";
    }

    public static class ColumnNames {
        static final public String SUB_ACCOUNT_NUMBER = "SUB_ACCT_NBR";
        static final public String CURRENT_BDLN_BALANCE_AMOUNT = "CURR_BDLN_BAL_AMT";
        static final public String ACCOUNTING_LINE_ACTUALS_BALANCE_AMOUNT = "ACLN_ACTLS_BAL_AMT";
        static final public String ACCOUNTING_LINE_ENCUMBRANCE_BALANCE_AMOUNT = "ACLN_ENCUM_BAL_AMT";
        static final public String CONSOLIDATION_OBJECT_CODE = "FIN_CONS_OBJ_CD";
        static final public String REPORT_SORT_CODE = "FIN_REPORT_SORT_CD";
        static final public String CONSOLIDATION_REPORT_SORT_CODE = "CONS_FIN_REPORT_SORT_CD";
        static final public String OBJECT_LEVEL_CODE = "FIN_OBJ_LVL_CD";
        static final public String OBJECT_LEVEL_CODE2 = "FIN_OBJ_LEVEL_CD";
        static final public String OBJECT_CODE = "FIN_OBJECT_CD";
        // amounts in the balances table
        static final public String ANNUAL_BALANCE = "ACLN_ANNL_BAL_AMT";
        static final public String BEGINNING_BALANCE = "FIN_BEG_BAL_LN_AMT";

        static final public String UNIVERSITY_FISCAL_YEAR = "UNIV_FISCAL_YR";
        static final public String CHART_OF_ACCOUNTS_CODE = "FIN_COA_CD";
        static final public String ACCOUNT_NUMBER = "ACCOUNT_NBR";
        static final public String SUB_OBJECT_CODE = "FIN_SUB_OBJ_CD";
        static final public String OBJECT_TYPE_CODE = "FIN_OBJ_TYP_CD";
        static final public String BALANCE_TYPE_CODE = "FIN_BALANCE_TYP_CD";
        static final public String DEBIT_CREDIT_CODE = "TRN_DEBIT_CRDT_CD";
        static final public String OBJECT_TYPE_DEBIT_CREDIT_CODE = "FIN_OBJTYP_DBCR_CD";
        static final public String OFFSET_GENERATION_CODE = "FIN_OFFST_GNRTN_CD";
        static final public String TRANSACTION_LEDGER_ENTRY_AMOUNT = "TRN_LDGR_ENTR_AMT";

        static final public String TRANSACTION_DT = "TRANSACTION_DT";
        static final public String FIN_OBJ_TYP_CODE = "FIN_OBJ_TYP_CODE";
        static final public String NET_EXP_OBJECT_CD = "NET_EXP_OBJECT_CD";
        static final public String NET_REV_OBJECT_CD = "NET_REV_OBJECT_CD";
        static final public String FUND_BAL_OBJ_TYP_CD = "FUND_BAL_OBJ_TYP_CD";
        static final public String FUND_BAL_OBJECT_CD = "FUND_BAL_OBJECT_CD";
        static final public String UNIV_DT = "UNIV_DT";
    }

    public static class BalanceInquiryDrillDowns {
        static final public String OBJECT_LEVEL_CODE = "financialObject.financialObjectLevel.financialObjectLevelCode";
        static final public String REPORTING_SORT_CODE = "financialObject.financialObjectLevel.financialReportingSortCode";
        static final public String CONSOLIDATION_OBJECT_CODE = "financialObject.financialObjectLevel.financialConsolidationObject.finConsolidationObjectCode";
    }

    public static class LookupableBeanKeys {
        static final public String PENDING_ENTRY = "glPendingEntryLookupable";
        public static final String SEGMENTED_LOOKUP_FLAG_NAME = "segmented";
    }

    public static final String GL_SCRUBBER_GROUP = "ScrubberStep";
    public static final String GL_ORGANIZATION_REVERSION_SELECTION_GROUP = "OrganizationReversion";

    public static final String ANNUAL_CLOSING_TRANSACTION_DATE_PARM = "ANNUAL_CLOSING_TRANSACTION_DATE";
    public static final String ANNUAL_CLOSING_FISCAL_YEAR_PARM = "ANNUAL_CLOSING_FISCAL_YEAR";
    public static final String ANNUAL_CLOSING_FUND_BALANCE_OBJECT_CODE_PARM = "ANNUAL_CLOSING_FUND_BALANCE_OBJECT_CODE";
    public static final String ANNUAL_CLOSING_FUND_BALANCE_OBJECT_TYPE_PARM = "ANNUAL_CLOSING_FUND_BALANCE_OBJECT_TYPE";
    public static final String ANNUAL_CLOSING_DOCUMENT_TYPE = "ANNUAL_CLOSING_DOCUMENT_TYPE";

    public static class GlAccountBalanceGroupParameters {
        static final public String EXPENSE_OBJECT_TYPE_CODES = "EXPENSE_OBJECT_TYPE_CODES";
        static final public String EXPENSE_TRANSFER_OBJECT_TYPE_CODES = "EXPENSE_TRANSFER_OBJECT_TYPE_CODES";
        static final public String INCOME_OBJECT_TYPE_CODES = "INCOME_OBJECT_TYPE_CODES";
        static final public String INCOME_TRANSFER_OBJECT_TYPE_CODES = "INCOME_TRANSFER_OBJECT_TYPE_CODES";
    }

    public static class GlScrubberGroupParameters {
        static final public String CAPITALIZATION_IND = "CAPITALIZATION_IND";
        static final public String CAPITALIZATION_SUBTYPE_OBJECT = "CAPITALIZATION_OBJECT_CODE_BY_OBJECT_SUB_TYPE";

        static final public String COST_SHARE_OBJECT_CODE_BY_LEVEL_PARM_NM = "COST_SHARE_OBJECT_CODE_BY_OBJECT_LEVEL";
        static final public String COST_SHARE_OBJECT_CODE_PARM_NM = "COST_SHARE_OBJECT_CODE";

        static final public String LIABILITY_IND = "LIABILITY_IND";
        static final public String LIABILITY_OBJECT_CODE = "LIABILITY_OBJECT_CODE";

        static final public String PLANT_INDEBTEDNESS_IND = "PLANT_INDEBTEDNESS_IND";

        static final public String SCRUBBER_CUTOFF_TIME = "CUTOFF_TIME";
    }

    public static class GlScrubberGroupRules {
        static final public String CAPITALIZATION_DOC_TYPE_CODES = "CAPITALIZATION_DOCUMENT_TYPES";
        static final public String CAPITALIZATION_FISCAL_PERIOD_CODES = "CAPITALIZATION_FISCAL_PERIODS";
        static final public String CAPITALIZATION_OBJ_SUB_TYPE_CODES = "CAPITALIZATION_OBJECT_SUB_TYPES";
        static final public String CAPITALIZATION_SUB_FUND_GROUP_CODES = "CAPITALIZATION_SUB_FUND_GROUPS";
        static final public String CAPITALIZATION_CHART_CODES = "CAPITALIZATION_CHARTS";

        static final public String COST_SHARE_OBJ_TYPE_CODES = "COST_SHARE_OBJECT_TYPES";
        static final public String COST_SHARE_FISCAL_PERIOD_CODES = "COST_SHARE_FISCAL_PERIODS";

        static final public String COST_SHARE_ENC_BAL_TYP_CODES = "COST_SHARE_ENCUMBRANCE_BALANCE_TYPES";
        static final public String COST_SHARE_ENC_DOC_TYPE_CODES = "COST_SHARE_ENCUMBRANCE_DOCUMENT_TYPES";
        static final public String COST_SHARE_ENC_FISCAL_PERIOD_CODES = "COST_SHARE_ENCUMBRANCE_FISCAL_PERIODS";

        static final public String LIABILITY_CHART_CODES = "LIABILITY_CHARTS";
        static final public String LIABILITY_DOC_TYPE_CODES = "LIABILITY_DOCUMENT_TYPES";
        static final public String LIABILITY_FISCAL_PERIOD_CODES = "LIABILITY_FISCAL_PERIODS";
        static final public String LIABILITY_OBJ_SUB_TYPE_CODES = "LIABILITY_OBJECT_SUB_TYPES";
        static final public String LIABILITY_SUB_FUND_GROUP_CODES = "LIABILITY_SUB_FUND_GROUPS";

        static final public String OFFSET_DOC_TYPE_CODES = "OFFSET_GENERATION_DOCUMENT_TYPES";
        static final public String OFFSET_FISCAL_PERIOD_CODES = "OFFSET_GENERATION_FISCAL_PERIODS";

        static final public String PLANT_FUND_CAMPUS_OBJECT_SUB_TYPE_CODES = "PLANT_FUND_CAMPUS_OBJECT_SUB_TYPES";
        static final public String PLANT_FUND_ORG_OBJECT_SUB_TYPE_CODES = "PLANT_FUND_ORGANIZATION_OBJECT_SUB_TYPES";

        static final public String PLANT_INDEBTEDNESS_OBJ_SUB_TYPE_CODES = "PLANT_INDEBTEDNESS_OBJECT_SUB_TYPES";
        static final public String PLANT_INDEBTEDNESS_SUB_FUND_GROUP_CODES = "PLANT_INDEBTEDNESS_SUB_FUND_GROUPS";

        static final public String CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES = "CONTINUATION_ACCOUNT_BYPASS_ORIGINATIONS";
        static final public String CONTINUATION_ACCOUNT_BYPASS_BALANCE_TYPE_CODES = "CONTINUATION_ACCOUNT_BYPASS_BALANCE_TYPE_CODES";
        static final public String CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES = "CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPES";
    }

    public static class GlSummaryReport {
        static final public String CURRENT_YEAR_LOWER = "CURRENT_YEAR_LOWER";
        static final public String CURRENT_YEAR_UPPER = "CURRENT_YEAR_UPPER";
        static final public String CURRENT_AND_LAST_YEAR = "CURRENT_AND_LAST_YEAR";
    }

    public static class OrganizationReversionProcess {
        static final public String ORGANIZATION_REVERSION_COA = "ORGANIZATION_REVERSION_COA";
        static final public String CARRY_FORWARD_OBJECT_CODE = "CARRY_FORWARD_OBJECT_CODE";
        static final public String DEFAULT_FINANCIAL_SYSTEM_ORIGINATION_CODE = "MANUAL_FEED_ORIGINATION";
        static final public String DEFAULT_FINANCIAL_BALANCE_TYPE_CODE = "CASH_REVERSION_DEFAULT_BALANCE_TYPE";
        static final public String DEFAULT_FINANCIAL_BALANCE_TYPE_CODE_YEAR_END = "BUDGET_REVERSION_DEFAULT_BALANCE_TYPE";
        static final public String DEFAULT_DOCUMENT_NUMBER_PREFIX = "DEFAULT_DOCUMENT_NUMBER_PREFIX";
        static final public String UNALLOC_OBJECT_CODE_PARM = "UNALLOCATED_OBJECT_CODE";
    }

    public static class PosterService {
        static final public String SYMBOL_USE_EXPENDITURE_ENTRY = "@";
        static final public String SYMBOL_USE_IRC_FROM_ACCOUNT = "#";
    }

    public static class PosterOutputSummaryEntry {
        static final public String ASSET_EXPENSE_OBJECT_TYPE_CODES = "ASSET_EXPENSE_OBJECT_TYPE_CODES";
    }

    public static class BalanceForwardRule {
        static final public String BALANCE_TYPES_TO_ROLL_FORWARD_FOR_BALANCE_SHEET = "BALANCE_TYPES_TO_ROLL_FORWARD_FOR_BALANCE_SHEET";
        static final public String BALANCE_TYPES_TO_ROLL_FORWARD_FOR_INCOME_EXPENSE = "BALANCE_TYPES_TO_ROLL_FORWARD_FOR_INCOME_EXPENSE";
        static final public String SUB_FUND_GROUPS_FOR_INCEPTION_TO_DATE_REPORTING = "SUB_FUND_GROUPS_FOR_INCEPTION_TO_DATE_REPORTING";
    }

    public static class EncumbranceClosingOriginEntry {
        static final public String GENERATED_TRANSACTION_LEDGER_ENTRY_DESCRIPTION = "GENERATED_TRANSACTION_LEDGER_ENTRY_DESCRIPTION";
        static final public String BEGINNING_FUND_BALANCE_TRANSACTION_LEDGER_ENTRY_DESCRIPTION = "BEGINNING_FUND_BALANCE_TRANSACTION_LEDGER_ENTRY_DESCRIPTION";
        static final public String OFFSET_OBJECT_CODE_FOR_INTERNAL_ENCUMBRANCE = "OFFSET_OBJECT_CODE_FOR_INTERNAL_ENCUMBRANCE";
        static final public String OFFSET_OBJECT_CODE_FOR_PRE_ENCUMBRANCE = "OFFSET_OBJECT_CODE_FOR_PRE_ENCUMBRANCE";
        static final public String OFFSET_OBJECT_CODE_FOR_EXTERNAL_ENCUMBRANCE = "OFFSET_OBJECT_CODE_FOR_EXTERNAL_ENCUMBRANCE";
    }

    // Some static method calls below that could be done in static variables instead but isn't safe to do during class loading
    // w/SpringContext.
    private static String SPACE_UNIVERSITY_FISCAL_PERIOD_CODE = null;

    public static String getSpaceUniversityFiscalPeriodCode() {
        if (SPACE_UNIVERSITY_FISCAL_PERIOD_CODE == null) {
            SPACE_UNIVERSITY_FISCAL_PERIOD_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE), ' ');
        }
        return SPACE_UNIVERSITY_FISCAL_PERIOD_CODE;
    }

    private static String SPACE_BALANCE_TYPE_CODE = null;

    public static String getSpaceBalanceTypeCode() {
        if (SPACE_BALANCE_TYPE_CODE == null) {
            SPACE_BALANCE_TYPE_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(BalanceTyp.class, KFSPropertyConstants.CODE), ' ');
        }
        return SPACE_BALANCE_TYPE_CODE;
    }

    private static String SPACE_FINANCIAL_SYSTEM_ORIGINATION_CODE = null;

    public static String getSpaceFinancialSystemOriginationCode() {
        if (SPACE_FINANCIAL_SYSTEM_ORIGINATION_CODE == null) {
            SPACE_FINANCIAL_SYSTEM_ORIGINATION_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginationCode.class, KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE), ' ');
        }
        return SPACE_FINANCIAL_SYSTEM_ORIGINATION_CODE;
    }

    private static String SPACE_DEBIT_CREDIT_CODE = null;

    public static String getSpaceDebitCreditCode() {
        if (SPACE_DEBIT_CREDIT_CODE == null) {
            SPACE_DEBIT_CREDIT_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE), ' ');
        }
        return SPACE_DEBIT_CREDIT_CODE;
    }

    private static String SPACE_FINANCIAL_OBJECT_CODE = null;

    public static String getSpaceFinancialObjectCode() {
        if (SPACE_FINANCIAL_OBJECT_CODE == null) {
            SPACE_FINANCIAL_OBJECT_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE), ' ');
        }
        return SPACE_FINANCIAL_OBJECT_CODE;
    }

    private static String SPACE_TRANSACTION_DATE = null;

    public static String getSpaceTransactionDate() {
        if (SPACE_TRANSACTION_DATE == null) {
            SPACE_TRANSACTION_DATE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.TRANSACTION_DATE), ' ');
        }
        return SPACE_TRANSACTION_DATE;
    }

    private static String SPACE_UNIVERSITY_FISCAL_YEAR = null;

    public static String getSpaceUniversityFiscalYear() {
        if (SPACE_UNIVERSITY_FISCAL_YEAR == null) {
            SPACE_UNIVERSITY_FISCAL_YEAR = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), ' ');
        }
        return SPACE_UNIVERSITY_FISCAL_YEAR;
    }

    private static String SPACE_TRANSACTION_ENTRY_SEQUENCE_NUMBER = null;

    public static String getSpaceTransactionEntrySequenceNumber() {
        if (SPACE_TRANSACTION_ENTRY_SEQUENCE_NUMBER == null) {
            SPACE_TRANSACTION_ENTRY_SEQUENCE_NUMBER = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER), ' ');
        }
        return SPACE_TRANSACTION_ENTRY_SEQUENCE_NUMBER;
    }

    private static String SPACE_TRANSACTION_LEDGER_ENTRY_DESCRIPTION = null;

    public static String getSpaceTransactionLedgetEntryDescription() {
        if (SPACE_TRANSACTION_LEDGER_ENTRY_DESCRIPTION == null) {
            SPACE_TRANSACTION_LEDGER_ENTRY_DESCRIPTION = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC), ' ');
        }
        return SPACE_TRANSACTION_LEDGER_ENTRY_DESCRIPTION;
    }

    private static String SPACE_SUB_ACCOUNT_TYPE_CODE = null;

    public static String getSpaceSubAccountTypeCode() {
        if (SPACE_SUB_ACCOUNT_TYPE_CODE == null) {
            SPACE_SUB_ACCOUNT_TYPE_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(A21SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_TYPE_CODE), ' ');
        }
        return SPACE_SUB_ACCOUNT_TYPE_CODE;
    }

    private static String SPACE_ALL_ORIGIN_ENTRY_FIELDS = null;

    public static String getSpaceAllOriginEntryFields() {
        if (SPACE_ALL_ORIGIN_ENTRY_FIELDS == null) {
            List<AttributeDefinition> attributes = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(OriginEntryFull.class.getName()).getAttributes();

            int totalLength = 0;

            for ( AttributeDefinition attributeDefinition : attributes ) {

                if (KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT.equals(attributeDefinition.getName())) {
                    totalLength += OriginEntryFull.SPACE_TRANSACTION_LEDGER_ENTRY_AMOUNT.length();
                }
                else if (!(KFSPropertyConstants.ENTRY_GROUP_ID.equals(attributeDefinition.getName()) || KFSPropertyConstants.ENTRY_ID.equals(attributeDefinition.getName()))) {
                    totalLength += attributeDefinition.getMaxLength();
                }
            }

            SPACE_ALL_ORIGIN_ENTRY_FIELDS = StringUtils.rightPad("", totalLength, ' ');
        }

        return SPACE_ALL_ORIGIN_ENTRY_FIELDS;
    }

    private static String ZERO_TRANSACTION_ENTRY_SEQUENCE_NUMBER = null;

    public static String getZeroTransactionEntrySequenceNumber() {
        if (ZERO_TRANSACTION_ENTRY_SEQUENCE_NUMBER == null) {
            ZERO_TRANSACTION_ENTRY_SEQUENCE_NUMBER = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER), '0');
        }
        return ZERO_TRANSACTION_ENTRY_SEQUENCE_NUMBER;
    }

    private static String DASH_ORGANIZATION_REFERENCE_ID = null;

    public static String getDashOrganizationReferenceId() {
        if (DASH_ORGANIZATION_REFERENCE_ID == null) {
            DASH_ORGANIZATION_REFERENCE_ID = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.ORGANIZATION_REFERENCE_ID), '-');
        }
        return DASH_ORGANIZATION_REFERENCE_ID;
    }
}
