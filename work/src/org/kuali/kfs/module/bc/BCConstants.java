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
package org.kuali.module.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.core.JstlConstants;
import org.kuali.core.util.KualiDecimal;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;

public class BCConstants extends JstlConstants {

    // formkey prefix to use for all screens we will store in session
    public static final String FORMKEY_PREFIX = "BcDoc";

    // screen tab error keys
    public static final String BUDGET_CONSTRUCTION_SALARY_SETTING_TAB_ERRORS = "document.budgetConstructionSalarySetting*";
    public static final String BUDGET_CONSTRUCTION_REVENUE_TAB_ERRORS = "document.pendingBudgetConstructionGeneralLedgerRevenueLines*,newRevenueLine*";
    public static final String BUDGET_CONSTRUCTION_EXPENDITURE_TAB_ERRORS = "document.pendingBudgetConstructionGeneralLedgerExpenditureLines*,newExpenditureLine*";
    public static final String BUDGET_CONSTRUCTION_MONTHLY_BUDGET_ERRORS = "document.budgetConstructionMonthlyBudget*";
    public static final String BUDGET_CONSTRUCTION_REPORTDUMP_TAB_ERRORS = "document.budgetConstructionDocumentReportModes*";

    public static final String NEW_EXPENDITURE_LINE_PROPERTY_NAME = "newExpenditureLine";
    public static final String NEW_REVENUE_LINE_PROPERTY_NAME = "newRevenueLine";


    public static final String DISABLE_SALARY_SETTING_FLAG = "DISABLE_SALARY_SETTING_FLAG";
    public static final String DISABLE_BENEFITS_CALCULATION_FLAG = "DISABLE_BENEFITS_CALCULATION_FLAG";

    public static final String BC_SELECTION_ACTION = "budgetBudgetConstructionSelection.do";
    public static final String BC_SELECTION_REFRESH_METHOD = "refresh";

    public static final String ORG_SEL_TREE_REFRESH_CALLER = "BudgetConstruction";
    public static final String ORG_SEL_TREE_ACTION = "budgetOrganizationSelectionTree.do";
    public static final String ORG_SEL_TREE_METHOD = "loadExpansionScreen";
    public static final String ORG_TEMP_LIST_LOOKUP = "budgetTempListLookup.do";
    public static final String ORG_REPORT_SELECTION_ACTION = "budgetOrganizationReportSelection.do";
    public static final String REPORT_DUMP_ACTION = "budgetReportDump.do";
    public static final String REQUEST_IMPORT_ACTION = "budgetBudgetConstructionRequestImport.do";
    
    public static final String REQUEST_IMPORT_REFRESH_CALLER = "RequestImport";
    public static final String REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC = "Bad file format at line";
    
    public static final String REQUEST_IMPORT_OUTPUT_FILE = "budgetImportLog.pdf";
    
    public static final String LABOR_OBJECT_FRINGE_CODE = "F";
    public static final String MAPPING_IMPORT_EXPORT = "import_export";
    
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
    public static final String SUB_ACCOUNT_TYPE_COST_SHARE = "CS";
    
    
    public static final String BC_DOCUMENT_REFRESH_CALLER = "BudgetConstruction";
    public static final String BC_DOCUMENT_ACTION = "budgetBudgetConstruction.do";
    public static final String BC_DOCUMENT_REFRESH_METHOD = "refresh";
    public static final String BC_DOCUMENT_METHOD = "docHandler";

    public static final String MONTHLY_BUDGET_REFRESH_CALLER = "MonthlyBudget";
    public static final String MONTHLY_BUDGET_ACTION = "budgetMonthlyBudget.do";
    public static final String MONTHLY_BUDGET_METHOD = "loadExpansionScreen";

    public static final String SALARY_SETTING_REFRESH_CALLER = "SalarySetting";
    public static final String SALARY_SETTING_ACTION = "budgetSalarySetting.do";
    public static final String SALARY_SETTING_METHOD = "loadExpansionScreen";

    public static final String POSITION_SALARY_SETTING_REFRESH_CALLER = "PositionSalarySetting";
    public static final String POSITION_SALARY_SETTING_ACTION = "budgetPositionSalarySetting.do";
    public static final String POSITION_SALARY_SETTING_METHOD = "loadExpansionScreen";

    public static final String INCUMBENT_SALARY_SETTING_REFRESH_CALLER = "IncumbentSalarySetting";
    public static final String INCUMBENT_SALARY_SETTING_ACTION = "budgetIncumbentSalarySetting.do";
    public static final String INCUMBENT_SALARY_SETTING_METHOD = "loadExpansionScreen";

    public static final String RETURN_ANCHOR = "returnAnchor";
    public static final String RETURN_FORM_KEY = "returnFormKey";

    public static final String INSERT_REVENUE_LINE_METHOD = "insertRevenueLine";
    public static final String INSERT_EXPENDITURE_LINE_METHOD = "insertExpenditureLine";
    public static final String APPOINTMENT_FUNDING_DURATION_DEFAULT = "NONE";

    public final static String SELECTION_SUB_TREE_ORGS = "selectionSubTreeOrgs";
    public final static String SHOW_INITIAL_RESULTS = "showInitialResults";
    public final static String CURRENT_POINT_OF_VIEW_KEYCODE = "currentPointOfViewKeyCode";


    /*
     * fund groups and subfund groups that are NOT loaded to the GL from budget construction (these are the constants used, not the
     * ones in KFSConstants/BudgetConstructionConstants)
     */
    public final static List<String> NO_BC_GL_LOAD_SUBFUND_GROUPS = Arrays.asList("SIDC");
    public final static List<String> NO_BC_GL_LOAD_FUND_GROUPS = Arrays.asList("CG");

    /*
     *  values for the CSF funding status flag
     */
    public enum csfFundingStatusFlag {
        LEAVE("L"), VACANT("V"), UNFUNDED("U"), ACTIVE("-");
        
        private csfFundingStatusFlag(String flagValue) {this.flagValue = flagValue;}
        public String getFlagValue() {return flagValue;}
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
        public final static String VACANT = "VACANT";
        public final static String BLANK_SUB_OBJECT_CODE = "---";
        public final static String BLANK = "";
        public final static String DELETE_MARK = "*"; 
        public final static String REVENUE = "REVENUE";
        public final static String EXPENDITURE = "EXPENDITURE";
        public final static String CONSOLIIDATED = "Consolidated";
        public final static String NONE = "NONE";
        public final static String PLUS = "+";
        public final static String UNDF = "UnDf";
        public final static String YES = "Y";
        public final static String NO = "N";
        public final static String THRESHOLD = "Threshold: ";
        public final static String THRESHOLD_GREATER = "greater then or equal to ";
        public final static String THRESHOLD_LESS = "less than or equal to ";
        public final static String PERCENT = "%";
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

        // report file name
        public static final String REPORT_TEMPLATE_CLASSPATH = "org/kuali/module/budget/report/";
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

        public final static int INTENDED_INCUMBENT_SELECT = 1;
        public final static int POSITION_SELECT = 2;
        public final static int ACCOUNT_SELECT_ABOVE_POV = 3;
        public final static int ACCOUNT_SELECT_BUDGETED_DOCUMENTS = 4;
    }

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
            if ( this.equals(COMMA) ) return ",";
            if ( this.equals(TAB) ) return "\t";
            
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
            if ( this.equals(QUOTE) ) return "\"";
            if ( this.equals(NOTHING) ) return "";
            
            return this.toString();
        }
    }
    
    //budget construction request move data validation error codes
    public enum RequestImportErrorCode {
        DATA_VALIDATION_NO_BUDGETED_ACCOUNT_SUB_ACCOUNT_ERROR_CODE("ACSA", "Error: No budgeted account/sub-account found"),
        DATA_VALIDATION_ACCOUNT_CLOSED_ERROR_CODE("CLAC", "Error: Account is closed"),
        DATA_VALIDATION_ACCOUNT_EXPIRED_ERROR_CODE("EXAC", "Error: Account is expired"),
        DATA_VALIDATION_SUB_ACCOUNT_INACTIVE_ERROR_CODE("INSA", "Error: Sub-account is inactive"),
        DATA_VALIDATION_OBJECT_TYPE_NULL_ERROR_CODE("NOOB", "Error: Null object code"),
        DATA_VALIDATION_OBJECT_TYPE_INVALID_ERROR_CODE("NOOB", "Error: Invalid object code"),
        DATA_VALIDATION_OBJECT_CODE_INACTIVE_ERROR_CODE("INOB", "Error: Inactive object code"),
        DATA_VALIDATION_SUB_OBJECT_INACTIVE_ERROR_CODE("INSO", "Error: Inactive sub-object code"),
        DATA_VALIDATION_SUB_OBJECT_INVALID_ERROR_CODE("NOSO", "Error: Invalid sub-object code"),
        DATA_VALIDATION_NO_WAGE_ACCOUNT_ERROR_CODE("CMPA", "Error: Wage object in no wages account"),
        DATA_VALIDATION_COMPENSATION_OBJECT_CODE_ERROR_CODE("COMP", "Error: Compensation object code"),
        UPDATE_ERROR_CODE_MONTHLY_BUDGET_DELETED("MNTH", "Warning: Monthly budget deleted"),
        UPDATE_ERROR_CODE_BUDGET_ACCOUNT_LOCKED("LOCK", "Error: Budgeted account locked"),
        UPDATE_ERROR_CODE_NO_ACCESS_TO_BUDGET_ACCOUNT("ACCE", "Error: No update access to budgeted account");
        
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
        PERCENT("Percent"), AMOUNT("Flat Amount");
        
        public String measurement;
        
        private SalaryAdjustmentMeasurement(String measurement) {
            this.measurement = measurement;
        }
    }
    
    public final static String POSITION_CODE_INACTIVE = "I";   
    public final static String DOCUMENT_TYPE_CODE_ALL = "ALL";

    public static final String BUDGET_BY_ACCOUNT_MODE = "budgetByAccountMode";
    
    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    public static final BigDecimal STANDARD_WEEKLY_WORK_HOUR = BigDecimal.valueOf(40);
    public static final BigDecimal STANDARD_WEEKLY_WORK_HOUR_AS_DECIMAL = STANDARD_WEEKLY_WORK_HOUR.divide(ONE_HUNDRED, 2, KualiDecimal.ROUND_BEHAVIOR);
}
