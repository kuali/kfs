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
package org.kuali.kfs.module.bc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.module.bc.document.web.struts.BudgetConstructionRequestImportAction;
import org.kuali.kfs.module.bc.document.web.struts.MonthlyBudgetAction;
import org.kuali.kfs.module.bc.document.web.struts.OrganizationSelectionTreeAction;
import org.kuali.kfs.module.bc.document.web.struts.QuickSalarySettingAction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.ParameterValues;

public class BCConstants {
    public static final String BUDGET_CONSTRUCTION_NAMESPACE = "KFS-BC";

    // max depth to check runaway account organization hierarchy builds
    public final static Integer MAXIMUM_ORGANIZATION_TREE_DEPTH = new Integer(1000);

    // formkey prefix to use for all screens we will store in session
    public static final String FORMKEY_PREFIX = "BcDoc";
    public static final String BC_IN_PROGRESS_SESSIONFLAG = FORMKEY_PREFIX + "BCInProgress";
    public static final String BC_HEARTBEAT_SESSIONFLAG = FORMKEY_PREFIX + "BCHeartBeat";
    public static final String BC_DOC_AUTHORIZATION_STATUS_SESSIONKEY = FORMKEY_PREFIX + "EditStatus";

    // this are used in expansion screen session security cleanup management
    public static final String MAPPING_ATTRIBUTE_KUALI_FORM = "KualiForm";
    public static final String MAPPING_SCOPE_SESSION = "session";

    public static final String MAPPING_SAVE = "save";

    // screen tab error keys
    public static final String BUDGET_CONSTRUCTION_SALARY_SETTING_TAB_ERRORS = "document.budgetConstructionSalarySetting*";
    public static final String BUDGET_CONSTRUCTION_REVENUE_TAB_ERRORS = "document.pendingBudgetConstructionGeneralLedgerRevenueLines*,newRevenueLine*";
    public static final String BUDGET_CONSTRUCTION_EXPENDITURE_TAB_ERRORS = "document.pendingBudgetConstructionGeneralLedgerExpenditureLines*,newExpenditureLine*";
    public static final String BUDGET_CONSTRUCTION_MONTHLY_BUDGET_ERRORS = "budgetConstructionMonthly*";
    public static final String BUDGET_CONSTRUCTION_REPORTDUMP_TAB_ERRORS = "document.budgetConstructionDocumentReportModes*";
    public static final String BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS = "accountOrgHierLevels*";
    public static final String BUDGET_CONSTRUCTION_SELECTION_ERRORS = "budgetConstructionHeader*";

    public static final String NEW_EXPENDITURE_LINE_PROPERTY_NAME = "newExpenditureLine";
    public static final String NEW_REVENUE_LINE_PROPERTY_NAME = "newRevenueLine";

    public static final String DISABLE_SALARY_SETTING_FLAG = "DISABLE_SALARY_SETTING_FLAG";
    public static final String DISABLE_BENEFITS_CALCULATION_FLAG = "DISABLE_BENEFITS_CALCULATION_FLAG";

    public static final String BC_SELECTION_ACTION = "budgetBudgetConstructionSelection.do";
    public static final String BC_SELECTION_REFRESH_METHOD = "refresh";

    public static final String ORG_SEL_TREE_REFRESH_CALLER = OrganizationSelectionTreeAction.class.getName();
    public static final String ORG_SEL_TREE_ACTION = "budgetOrganizationSelectionTree.do";
    public static final String ORG_SEL_TREE_METHOD = "loadExpansionScreen";
    public static final String ORG_TEMP_LIST_LOOKUP = "budgetTempListLookup.do";
    public static final String ORG_REPORT_SELECTION_ACTION = "budgetOrganizationReportSelection.do";
    public static final String REPORT_EXPORT_ACTION = "budgetReportExport.do";
    public static final String REPORT_EXPORT_PATH = "budgetReportExport";
    public static final String REQUEST_IMPORT_ACTION = "budgetBudgetConstructionRequestImport.do";
    public static final String PAYRATE_IMPORT_EXPORT_ACTION = "budgetPayrateImportExport.do";

    public static final String REQUEST_IMPORT_REFRESH_CALLER = BudgetConstructionRequestImportAction.class.getName();
    public static final String REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC = "Bad file format at line";

    public static final String REQUEST_IMPORT_OUTPUT_FILE = "budgetImportLog.pdf";
    public static final String PAYRATE_IMPORT_LOG_FILE = "payrate_import_log.pdf";
    public static final String PAYRATE_EXPORT_FILE = "payrate_export.txt";

    public static final String LABOR_OBJECT_FRINGE_CODE = "F";
    public static final String MAPPING_IMPORT_EXPORT = "import_export";

    public static final String POSITION_NUMBER_NOT_FOUND = "NotFnd";

    public static final String IS_ORG_REPORT_REQUEST_PARAMETER = "orgReport";

    public enum OrgSelOpMode {
        PULLUP, PUSHDOWN, REPORTS, SALSET, ACCOUNT
    }

    /**
     * This class represents Select control options mapping explicit Integer values to an enum value. The explicit values can then
     * be used in a database stored procedure call in the event procedure calls are used instead of calls to a java method.
     */
    public enum OrgSelControlOption {
        NO(0, "No"), YES(1, "Yes"), NOTSEL(0, "Not Sel"), ORG(1, "Org"), SUBORG(2, "Sub Org"), BOTH(3, "Both"), ORGLEV(1, "Org Lev"), MGRLEV(2, "Mgr Lev"), ORGMGRLEV(3, "Org+Mgr Lev"), LEVONE(4, "Lev One"), LEVZERO(5, "Lev Zero");
        private String label;
        private Integer key;

        private OrgSelControlOption(Integer key, String label) {
            this.key = key;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public Integer getKey() {
            return key;
        }
    }

    // type of month spread delete used in rules checking when deleting monthly spreads
    public enum MonthSpreadDeleteType {
        NONE, REVENUE, EXPENDITURE
    }

    // the reason a document (account) is allowed only salary setting lines
    public enum AccountSalarySettingOnlyCause {
        MISSING_PARAM, NONE, FUND, SUBFUND, FUND_AND_SUBFUND
    }


    // some constants used in rule checking - may eventually move these to system parameters

    // latest period and number of fiscal years before active bcfy that an account becomes not budgetable
    public static final String NO_BUDGET_ALLOWED_EXPIRE_ACCOUNTING_PERIOD = "06";
    public static final Integer NO_BUDGET_ALLOWED_FY_OFFSET = Integer.valueOf(2);

    // latest period and number of fiscal years before active bcfy that an account expire warning message gets issued
    public static final String ACCOUNT_EXPIRE_WARNING_ACCOUNTING_PERIOD = "12";
    public static final Integer ACCOUNT_EXPIRE_WARNING_FY_OFFSET = Integer.valueOf(1);

    // account budgetRecordingLevelCode where no budget is allowed
    public static final String BUDGET_RECORDING_LEVEL_N = "N";

    // by default employees are assumed to work the entire 12-month year
    public static final Integer DEFAULT_NORMAL_WORK_MONTHS = 12;

    public static final String BC_DOCUMENT_REFRESH_CALLER = "BudgetConstruction";
    public static final String BC_DOCUMENT_ACTION = "budgetBudgetConstruction.do";
    public static final String BC_DOCUMENT_REFRESH_METHOD = "refresh";
    public static final String BC_DOCUMENT_METHOD = "docHandler";
    public static final String BC_DOCUMENT_PULLUP_METHOD = "performAccountPullup";
    public static final String BC_DOCUMENT_PUSHDOWN_METHOD = "performAccountPushdown";

    public static final String MONTHLY_BUDGET_REFRESH_CALLER = MonthlyBudgetAction.class.getName();
    public static final String MONTHLY_BUDGET_ACTION = "budgetMonthlyBudget.do";
    public static final String MONTHLY_BUDGET_METHOD = "loadExpansionScreen";

    public static final String QUICK_SALARY_SETTING_REFRESH_CALLER = QuickSalarySettingAction.class.getName();
    public static final String QUICK_SALARY_SETTING_ACTION = "budgetQuickSalarySetting.do";
    public static final String QUICK_SALARY_SETTING_METHOD = "loadExpansionScreen";

    public static final String POSITION_SALARY_SETTING_REFRESH_CALLER = "PositionSalarySetting";
    public static final String POSITION_SALARY_SETTING_ACTION = "budgetPositionSalarySetting.do";
    public static final String POSITION_SALARY_SETTING_METHOD = "loadExpansionScreen";
    public static final String POSITION_SALARY_SETTING_TITLE = "Salary Setting by Position";

    public static final String INCUMBENT_SALARY_SETTING_REFRESH_CALLER = "IncumbentSalarySetting";
    public static final String INCUMBENT_SALARY_SETTING_ACTION = "budgetIncumbentSalarySetting.do";
    public static final String INCUMBENT_SALARY_SETTING_METHOD = "loadExpansionScreen";
    public static final String INCUMBENT_SALARY_SETTING_TITLE = "Salary Setting by Incumbent";

    public static final String LOAD_EXPANSION_SCREEN_METHOD = "loadExpansionScreen";
    public static final String SECOND_WINDOW_TARGET_NAME = "BCSecondWindow";

    public static final String REPORT_RUNNER_ACTION = "budgetReportRunner.do";

    public static final String RETURN_ANCHOR = "returnAnchor";
    public static final String RETURN_FORM_KEY = "returnFormKey";

    public static final String INSERT_REVENUE_LINE_METHOD = "insertRevenueLine";
    public static final String INSERT_EXPENDITURE_LINE_METHOD = "insertExpenditureLine";

    public final static String SELECTION_SUB_TREE_ORGS = "selectionSubTreeOrgs";
    public final static String SHOW_INITIAL_RESULTS = "showInitialResults";
    public final static String CURRENT_POINT_OF_VIEW_KEYCODE = "currentPointOfViewKeyCode";
    public final static String FORCE_TO_ACCOUNT_LIST_SCREEN = "forceToAccountListScreen";

    public final static String LOCK_STRING_DELIMITER = "!";


    /*
     * fund groups and subfund groups that are NOT loaded to the GL from budget construction (these are the constants used, not the
     * ones in KFSConstants/BudgetConstructionConstants)
     */
    public final static List<String> NO_BC_GL_LOAD_SUBFUND_GROUPS = Arrays.asList("SIDC");
    public final static List<String> NO_BC_GL_LOAD_FUND_GROUPS = Arrays.asList("CG");

    /*
     * values for the CSF funding status flag
     */
    public enum csfFundingStatusFlag {
        LEAVE("L"), VACANT("V"), UNFUNDED("U"), ACTIVE("-");

        private csfFundingStatusFlag(String flagValue) {
            this.flagValue = flagValue;
        }

        public String getFlagValue() {
            return flagValue;
        }

        private String flagValue;
    }

    /*
     * value of employee ID field in a vacant line in budget construction appointment funding or budget construction CSF
     */
    public final static String VACANT_EMPLID = "VACANT";

    // the transaction ledger description for the general ledger budget load
    public final static String BC_TRN_LDGR_ENTR_DESC = "Beginning Budget Load";

    // this is a pairing of the OJB "property" for the monthly amount and its corresponding accounting period
    public final static ArrayList<String[]> BC_MONTHLY_AMOUNTS = buildMonthlyProperties();

    private static ArrayList<String[]> buildMonthlyProperties() {
        ArrayList<String[]> monthlyProperties = new ArrayList<String[]>(12);
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT, KFSConstants.MONTH1 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT, KFSConstants.MONTH2 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT, KFSConstants.MONTH3 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT, KFSConstants.MONTH4 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT, KFSConstants.MONTH5 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT, KFSConstants.MONTH6 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT, KFSConstants.MONTH7 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT, KFSConstants.MONTH8 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT, KFSConstants.MONTH9 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT, KFSConstants.MONTH10 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT, KFSConstants.MONTH11 }));
        monthlyProperties.add((new String[] { KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT, KFSConstants.MONTH12 }));
        return monthlyProperties;
    }

    // budget construction report constants
    public static class Report {
        public final static String INCOME_EXP_TYPE_A = "A";
        public final static String INCOME_EXP_TYPE_E = "E";
        public final static String INCOME_EXP_TYPE_T = "T";
        public final static String INCOME_EXP_TYPE_X = "X";
        public final static String VACANT = VACANT_EMPLID;
        public final static String BLANK = KFSConstants.EMPTY_STRING;
        public final static String DELETE_MARK = "*";
        public final static String DIVIDER = "/";
        public final static String TOTAL_REVENUES = "Total Revenues";
        public final static String TOTAL_EXPENDITURES_MARGIN = "Total Expenditures & Margin";
        public final static String REVENUE = "REVENUE";
        public final static String EXPENDITURE = "EXPENDITURE";
        public final static String CONSOLIIDATED = "Consolidated";
        public final static String NONE = "NONE";
        public final static String PLUS = "+";
        public final static String UNDF = "UnDf";
        public final static String YES = "Y";
        public final static String NO = "N";
        public final static String THRESHOLD = "Threshold: ";
        public final static String SELECTED_REASONS = "Selected Reasons: ";
        public final static String THRESHOLD_GREATER = "greater then or equal to ";
        public final static String THRESHOLD_LESS = "less than or equal to ";
        public final static String PERCENT = "%";
        public final static String MSG_REPORT_NO_DATA = "No data found.";
        // selection screen
        public final static String NONE_SELECTION_TITLE = "";
        public final static String SUB_FUND_SELECTION_TITLE = "Sub-Fund List Selection";
        public final static String OBJECT_CODE_SELECTION_TITLE = "Salary Object List Selection";
        public final static String REASON_CODE_SELECTION_TITLE = "Reason Code Selection";
        public final static String REPORT_MODE = "reportMode";
        public final static String BUILD_CONTROL_LIST = "buildControlList";
        public final static String REPORT_CONSOLIDATION = "reportConsolidation";
        public final static String CONTROL_BUILD_HELPER_SESSION_NAME = "controlBuildHelper";
        public final static String SUB_FUND_LIST_EMPTY_MESSAGE_KEY = "error.budget.report.emptySubFundList";
        public final static String OBJECT_CODE_LIST_EMPTY_MESSAGE_KEY = "error.budget.report.emptyObjectCodeList";
        public final static String REASON_CODE_LIST_EMPTY_MESSAGE_KEY = "error.budget.report.emptyReasonCodeList";
        public final static String THRESHOLD_SELECTION_MESSAGE_KEY = "message.budget.thresholdSelection";
        public final static String SELECTED_OBJECT_CODES_MESSAGE_KEY = "message.budget.selectedObjectCodes";
        public final static String NOT_DEFINED = " not defined";
        public final static String CHART = "Chart";
        public final static String OBJECT = "Object";

        // report file name
        public static final String REPORT_TEMPLATE_CLASSPATH = "org/kuali/kfs/module/bc/report/";
        public static final String REPORT_MESSAGES_CLASSPATH = REPORT_TEMPLATE_CLASSPATH + "BudgetOrgReport";

        // Represents the three modes in which report data is restricted
        public enum BuildMode {
            PBGL(), MONTH(), BCAF();
        }

        // Represents the varoius reports criteria selections
        public enum ReportSelectMode {
            ACCOUNT(), OBJECT_CODE(), SUBFUND(), REASON();
        }
    }

    // constants that represent a mode for the TempListLookupAction.
    public static class TempListLookupMode {
        public final static String TEMP_LIST_LOOKUP_MODE = "tempListLookupMode";

        public final static int DEFAULT_LOOKUP_MODE = 0;
        public final static int INTENDED_INCUMBENT_SELECT = 1;
        public final static int POSITION_SELECT = 2;
        public final static int ACCOUNT_SELECT_ABOVE_POV = 3;
        public final static int ACCOUNT_SELECT_BUDGETED_DOCUMENTS = 4;
        public final static int LOCK_MONITOR = 5;
        public final static int CSF_TRACKER_POSITION_LOOKUP = 6;
        public final static int ACCOUNT_SELECT_PULLUP_DOCUMENTS = 7;
        public final static int ACCOUNT_SELECT_PUSHDOWN_DOCUMENTS = 8;
        public final static int ACCOUNT_SELECT_MANAGER_DELEGATE = 9;
        public final static int BUDGET_POSITION_LOOKUP = 10;
        public final static int INTENDED_INCUMBENT = 11;
        public final static int SHOW_BENEFITS = 12;
    }

    public final static String BC_BO_CLASSPATH = "org.kuali.kfs.module.bc.businessobject";
    public final static String REQUEST_BENEFITS_BO = BC_BO_CLASSPATH+".RequestBenefits";

    public enum RequestImportFileType {
        MONTHLY("MONTHLY"), ANNUAL("ANNUAL");

        private String fileType;

        private RequestImportFileType(String fileType) {
            this.fileType = fileType;
        }
    }

    public enum RequestImportFieldSeparator {
        COMMA("COMMA"), TAB("TAB"), OTHER("OTHER");

        private String separator;

        private RequestImportFieldSeparator(String separator) {
            this.separator = separator;
        }

        public String getSeparator() {
            if (this.equals(COMMA)) {
                return ",";
            }
            if (this.equals(TAB)) {
                return "\t";
            }

            return this.toString();
        }

    }

    public enum RequestImportTextFieldDelimiter {
        QUOTE("QUOTE"), NOTHING("NOTHING"), OTHER("OTHER");

        private String separator;

        private RequestImportTextFieldDelimiter(String separator) {
            this.separator = separator;
        }

        public String getDelimiter() {
            if (this.equals(QUOTE)) {
                return "\"";
            }
            if (this.equals(NOTHING)) {
                return "";
            }

            return this.toString();
        }
    }

    // budget construction request move data validation error codes
    public enum RequestImportErrorCode {
        DATA_VALIDATION_NO_BUDGETED_ACCOUNT_SUB_ACCOUNT_ERROR_CODE("ACSA", "Error: No budgeted account/sub-account found"), DATA_VALIDATION_ACCOUNT_CLOSED_ERROR_CODE("CLAC", "Error: Account is closed"), DATA_VALIDATION_ACCOUNT_EXPIRED_ERROR_CODE("EXAC", "Error: Account is expired"), DATA_VALIDATION_SUB_ACCOUNT_INVALID_ERROR_CODE("NOSA", "Error: Sub-account is invalid"), DATA_VALIDATION_SUB_ACCOUNT_INACTIVE_ERROR_CODE("INSA", "Error: Sub-account is inactive"), DATA_VALIDATION_OBJECT_TYPE_NULL_ERROR_CODE("NOOB", "Error: Null object code"), DATA_VALIDATION_OBJECT_TYPE_INVALID_ERROR_CODE("NOOB", "Error: Invalid object code"), DATA_VALIDATION_OBJECT_CODE_INACTIVE_ERROR_CODE("INOB", "Error: Inactive object code"), DATA_VALIDATION_SUB_OBJECT_INACTIVE_ERROR_CODE("INSO", "Error: Inactive sub-object code"), DATA_VALIDATION_SUB_OBJECT_INVALID_ERROR_CODE("NOSO", "Error: Invalid sub-object code"), DATA_VALIDATION_NO_WAGE_ACCOUNT_ERROR_CODE("CMPA", "Error: Wage object in no wages account"), DATA_VALIDATION_COMPENSATION_OBJECT_CODE_ERROR_CODE("COMP", "Error: Compensation object code"), UPDATE_ERROR_CODE_MONTHLY_BUDGET_DELETED(
                "MNTH", "Warning: Monthly budget deleted"), UPDATE_ERROR_CODE_BUDGET_ACCOUNT_LOCKED("LOCK", "Error: Budgeted account locked"), UPDATE_ERROR_CODE_NO_ACCESS_TO_BUDGET_ACCOUNT("ACCE", "Error: No update access to budgeted account");

        private String errorCode;
        private String message;

        private RequestImportErrorCode(String errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }

        public String getErrorCode() {
            return this.errorCode;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public enum SalaryAdjustmentMeasurement {
        PERCENT("Percent", "%"), AMOUNT("FlatAmount", "flat");

        public String measurement;
        public String label;

        private SalaryAdjustmentMeasurement(String measurement, String label) {
            this.measurement = measurement;
            this.label = label;
        }
    }

    public final static String POSITION_CODE_INACTIVE = "I";
    public final static String DOCUMENT_TYPE_CODE_ALL = "ALL";

    public static class LockTypes {
        public final static String ACCOUNT_LOCK = "account lock";
        public final static String FUNDING_LOCK = "orphan funding lock";
        public final static String POSITION_FUNDING_LOCK = "position/funding lock";
        public final static String POSITION_LOCK = "position lock";
        public final static String TRANSACTION_LOCK = "transaction lock";
    }

    public static final String PICK_LIST_MODE = "pickListMode";

    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    // constants for lock monitor
    public static final String TEMP_LIST_UNLOCK_METHOD = "unlock";
    public static final String UNLOCK_BUTTON_NAME = "tinybutton-unlock.gif";
    public static final String UNLOCK_CONFIRMATION_QUESTION = "UnlockConfirmationQuestion";

    public static final String SHOW_SALARY_BY_POSITION_ACTION = "showSalaryByPositionAction";
    public static final String REFRESH_POSITION_BEFORE_SALARY_SETTING = "refreshPositionBeforeSalarySetting";
    public static final String TEMP_LIST_REFRESH_POSITION_METHOD = "refreshPosition";
    public static final String REFRESH_POSITION_BUTTON_NAME = "tinybutton-sync.gif";

    public static final String SHOW_SALARY_BY_INCUMBENT_ACTION = "showSalaryByIncumbentAction";
    public static final String REFRESH_INCUMBENT_BEFORE_SALARY_SETTING = "refreshIncumbentBeforeSalarySetting";
    public static final String TEMP_LIST_REFRESH_INCUMBENT_METHOD = "refreshIncumbent";
    public static final String REFRESH_INCUMBENT_BUTTON_NAME = "tinybutton-sync.gif";
    public static final String MAPPING_ORGANIZATION_SALARY_SETTING_RETURNING = "organizationSalarySettingReturning";
    public static final String MAPPING_LOST_SESSION_RETURNING = "lostSessionReturning";



    public enum LockStatus {
        SUCCESS, BY_OTHER, NO_DOOR, OPTIMISTIC_EX, FLOCK_FOUND
    }

    public static final int maxLockRetry = 20;


    /* KFSConstants for the budget construction flag names */
    private static int NUMBER_OF_CTRL_FLAGS = 8;

    /* state for current year budget construction flags after genesis */
    private static HashMap<String, String> buildCurrentYear() {
        HashMap<String, String> mapSLF;
        mapSLF = new HashMap<String, String>(NUMBER_OF_CTRL_FLAGS, (float) 1.00);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_ADMINSTRATION_ACTIVE, ParameterValues.YES);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BASE_BUDGET_UPDATES_OK, ParameterValues.YES);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_BATCH_SYNCHRONIZATION_OK, ParameterValues.NO);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.CSF_UPDATES_OK, ParameterValues.NO);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE, ParameterValues.NO);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING, ParameterValues.NO);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_UPDATES_OK, ParameterValues.NO);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_ON_LINE_SYNCHRONIZATION_OK, ParameterValues.NO);
        return mapSLF;
    }

    public final static HashMap<String, String> CURRENT_FSCL_YR_CTRL_FLAGS = buildCurrentYear();

    /* state for next year budget construction flags after genesis */
    private static HashMap<String, String> buildNextYear() {
        HashMap<String, String> mapSLF;
        mapSLF = new HashMap<String, String>(NUMBER_OF_CTRL_FLAGS, (float) 1.00);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_ADMINSTRATION_ACTIVE, ParameterValues.NO);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BASE_BUDGET_UPDATES_OK, ParameterValues.NO);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_BATCH_SYNCHRONIZATION_OK, ParameterValues.YES);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.CSF_UPDATES_OK, ParameterValues.YES);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE, ParameterValues.YES);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING, ParameterValues.NO);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_UPDATES_OK, ParameterValues.NO);
        mapSLF.put(KFSConstants.BudgetConstructionConstants.BUDGET_ON_LINE_SYNCHRONIZATION_OK, ParameterValues.YES);
        return mapSLF;
    }

    public final static HashMap<String, String> NEXT_FSCL_YR_CTRL_FLAGS_AFTER_GENESIS = buildNextYear();

    /* constants for the budget construction header */
    public final static String DEFAULT_BUDGET_HEADER_LOCK_IDS = null;
    public final static Integer INITIAL_ORGANIZATION_LEVEL_CODE = new Integer(0);
    public final static String INITIAL_ORGANIZATION_LEVEL_CHART_OF_ACCOUNTS_CODE = null;
    public final static String INITIAL_ORGANIZATION_LEVEL_ORGANIZATION_CODE = null;

    /* Budget Construction document type */
    public final static String BUDGET_CONSTRUCTION_DOCUMENT_TYPE = "BC";
    public final static String BUDGET_CONSTRUCTION_BEGINNING_BALANCE_DOCUMENT_TYPE = "BCBB";
    public final static String BUDGET_CONSTRUCTION_DOCUMENT_NAME = "BC";
    public final static String BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION = "Budget Construction";
    public final static String BUDGET_CONSTRUCTION_DOCUMENT_INITIAL_STATUS = "$";
    public final static String ORG_REVIEW_RULE_TEMPLATE = "KualiOrgReviewTemplate";

    /*
     * initial sizes for hash maps used in genesis supposedly starting the map out with about the right amount of space makes
     * look-ups more efficient these numbers shouldn't need to be very precise
     */
    public final static Integer AVERAGE_REPORTING_TREE_SIZE = 4;

    /**
     *  value indicating that a CSF row is Active
     */
    public final static String ACTIVE_CSF_DELETE_CODE = "-";



    /**
     * enumerate the leave duration code
     */
    public enum AppointmentFundingDurationCodes {
        NONE("NONE", "No Leave"), LWP1("LWP1", "LWOP:  First Semester"), LWP2("LWP2", "LWOP:  Second Semester"), LWPA("LWPA", "LWOP:  10 months"), LWPF("LWPF", "LWOP:  12 months"), LWPH("LWPH", "LWOP:  6 months"), LWPX("LWPX", "LWOP:  ACROSS FISCAL YEARS"), SAB1("SAB1", "Sabbatical Leave:  First Semester"), SAB2("SAB2", "Sabbatical Leave:  Second Semester"), SABA("SABA", "Sabbatical Leave:  Academic Year"), SABF("SABF", "Sabbatical Leave:  12 months"), SABH("SABH", "Sabbatical Leave:  6 months"), SABX("SABX", "Sabbatical Leave:  ACROSS FISCAL YEARS");

        public String durationCode;
        public String durationDescription;

        private AppointmentFundingDurationCodes(String durationCode, String durationDescription) {
            this.durationCode = durationCode;
            this.durationDescription = durationDescription;
        }

        /**
         * Gets the durationCode attribute.
         * @return Returns the durationCode.
         */
        public String getDurationCode() {
            return durationCode;
        }

        /**
         * Gets the durationDescription attribute.
         * @return Returns the durationDescription.
         */
        public String getDurationDescription() {
            return durationDescription;
        }
    }

    public class EditModes {
        public static final String SYSTEM_VIEW_ONLY = "systemViewOnly";
    }

    public class KimApiConstants {
        public static final String BC_PROCESSOR_ROLE_NAME = "Processor";
        public static final String USE_ORG_SALARY_SETTING_PERMISSION_NAME = "Use Organization Salary Setting";
        public static final String EDIT_BCAF_PERMISSION_NAME = "Edit Appointment Funding";
        public static final String VIEW_BCAF_AMOUNTS_PERMISSION_NAME = "View Appointment Funding Amounts";
        public static final String IMPORT_EXPORT_PAYRATE_PERMISSION_NAME = "Import / Export Payrate";
        public static final String UNLOCK_PERMISSION_NAME = "Unlock";
        public static final String DOCUMENT_VIEWER_ROLE_NAME = "Document Viewer";
        public static final String DOCUMENT_EDITOR_ROLE_NAME = "Document Editor";
    }

    /**
     * enumerate the synchronization check type
     */
    public enum SynchronizationCheckType {
        NONE("NONE", "No Sync Check"), POSN("POSN", "Snyc by Position"), EID("EID", "Snyc by Employee"), ALL("ALL", "Snyc by Position and Employee");

        public String typeCode;
        public String typeDescription;

        private SynchronizationCheckType(String typeCode, String typeDescription) {
            this.typeCode = typeCode;
            this.typeDescription = typeDescription;
        }
    }

    public class ErrorKey {
        public static final String DETAIL_SALARY_SETTING_TAB_ERRORS = "newBCAFLine*,budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding*,budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding*";
        public static final String QUICK_SALARY_SETTING_TAB_ERRORS = "salarySettingExpansion*,salarySettingExpansion.pendingBudgetConstructionAppointmentFunding*,adjustment*,newBCAFLine*";
        public static final String RETURNED_DETAIL_SALARY_SETTING_TAB_ERRORS = "newBCAFLine*";
        public static final String ORGANIZATION_REPORTS_SELECTION_ERRORS = "budgetConstructionReportThresholdSettings*";
    }
}
