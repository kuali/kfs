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
package org.kuali.module.gl;


/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class GLConstants {
    public static final String DASH_ORGANIZATION_REFERENCE_ID = "--------";

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
        static final public String PENDING_ENTRY = "glPendingEntryLookupableImpl";
        static final public String COLLECTOR_FILE_PARSER = "glCollectorFileParser";
    }

    public static final String GL_ACCOUNT_BALANCE_SERVICE_GROUP = "GL.ACCOUNT_BALANCE_SERVICE";
    public static final String GL_SCRUBBER_GROUP = "GL.SCRUBBER";

    public static class GlAccountBalanceGroupParameters {
        static final public String EXPENSE_OBJECT_TYPE_CODES = "EXPENSE_OBJECT_TYPE_CODES";
        static final public String EXPENSE_TRANSFER_OBJECT_TYPE_CODES = "EXPENSE_TRANSFER_OBJECT_TYPE_CODES";
        static final public String INCOME_OBJECT_TYPE_CODES = "INCOME_OBJECT_TYPE_CODES";
        static final public String INCOME_TRANSFER_OBJECT_TYPE_CODES = "INCOME_TRANSFER_OBJECT_TYPE_CODES";
    }

    public static class GlScrubberGroupParameters {
        static final public String CAPITALIZATION_IND = "CAPITALIZATION.IND";
        static final public String CAPITALIZATION_SUBTYPE_OBJECT_PREFIX = "CAPITALIZATION.SUBTYPE_OBJECT_";

        static final public String COST_SHARE_LEVEL_OBJECT_PREFIX = "COST_SHARE.LEVEL_OBJECT_";
        static final public String COST_SHARE_LEVEL_OBJECT_DEFAULT = "COST_SHARE.LEVEL_OBJECT_DEFAULT";
        static final public String COST_SHARE_OBJECT_CODE = "COST_SHARE.OBJECT_CODE";

        static final public String LIABILITY_IND = "LIABILITY.IND";
        static final public String LIABILITY_OBJECT_CODE = "LIABILITY.OBJECT_CODE";

        static final public String PLANT_INDEBTEDNESS_IND = "PLANT_INDEBTEDNESS.IND";
    }

    public static class GlScrubberGroupRules {
        static final public String CAPITALIZATION_DOC_TYPE_CODES = "CAPITALIZATION.DOC_TYPE_CODES";
        static final public String CAPITALIZATION_FISCAL_PERIOD_CODES = "CAPITALIZATION.FISCAL_PERIOD_CODES";
        static final public String CAPITALIZATION_OBJ_SUB_TYPE_CODES = "CAPITALIZATION.OBJ_SUB_TYPE_CODES";
        static final public String CAPITALIZATION_SUB_FUND_GROUP_CODES = "CAPITALIZATION.SUB_FUND_GROUP_CODES";
        static final public String CAPITALIZATION_CHART_CODES = "CAPITALIZATION.CHART_CODES";

        static final public String CAP_LIAB_PLANT_DOC_TYPE_CODES = "CAP_LIAB_PLANT.DOC_TYPE_CODES";

        static final public String COST_SHARE_BAL_TYP_CODES = "COST_SHARE.BAL_TYP_CODES";
        static final public String COST_SHARE_OBJ_TYPE_CODES = "COST_SHARE.OBJ_TYPE_CODES";
        static final public String COST_SHARE_FISCAL_PERIOD_CODES = "COST_SHARE.FISCAL_PERIOD_CODES";

        static final public String COST_SHARE_ENC_BAL_TYP_CODES = "COST_SHARE_ENC.BAL_TYP_CODES";
        static final public String COST_SHARE_ENC_DOC_TYPE_CODES = "COST_SHARE_ENC.DOC_TYPE_CODES";
        static final public String COST_SHARE_ENC_FISCAL_PERIOD_CODES = "COST_SHARE_ENC.FISCAL_PERIOD_CODES";

        static final public String LIABILITY_CHART_CODES = "LIABILITY.CHART_CODES";
        static final public String LIABILITY_DOC_TYPE_CODES = "LIABILITY.DOC_TYPE_CODES";
        static final public String LIABILITY_FISCAL_PERIOD_CODES = "LIABILITY.FISCAL_PERIOD_CODES";
        static final public String LIABILITY_OBJ_SUB_TYPE_CODES = "LIABILITY.OBJ_SUB_TYPE_CODES";
        static final public String LIABILITY_SUB_FUND_GROUP_CODES = "LIABILITY.SUB_FUND_GROUP_CODES";

        static final public String OFFSET_DOC_TYPE_CODES = "OFFSET.DOC_TYPE_CODES";
        static final public String OFFSET_FISCAL_PERIOD_CODES = "OFFSET.FISCAL_PERIOD_CODES";

        static final public String PLANT_FUND_CAMPUS_OBJECT_SUB_TYPE_CODES = "PLANT_FUND.CAMPUS_OBJECT_SUB_TYPE_CODES";
        static final public String PLANT_FUND_ORG_OBJECT_SUB_TYPE_CODES = "PLANT_FUND.ORG_OBJECT_SUB_TYPE_CODES";

        static final public String PLANT_INDEBTEDNESS_OBJ_SUB_TYPE_CODES = "PLANT_INDEBTEDNESS.OBJ_SUB_TYPE_CODES";
        static final public String PLANT_INDEBTEDNESS_SUB_FUND_GROUP_CODES = "PLANT_INDEBTEDNESS.SUB_FUND_GROUP_CODES";
    }
}
