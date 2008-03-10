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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.core.JstlConstants;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;

public class BCConstants extends JstlConstants {

    // screen tab error keys
    public static final String BUDGET_CONSTRUCTION_SALARY_SETTING_TAB_ERRORS = "document.budgetConstructionSalarySetting*";
    public static final String BUDGET_CONSTRUCTION_REVENUE_TAB_ERRORS = "document.pendingBudgetConstructionGeneralLedgerRevenueLines*,newRevenueLine*";
    public static final String BUDGET_CONSTRUCTION_EXPENDITURE_TAB_ERRORS = "document.pendingBudgetConstructionGeneralLedgerExpenditureLines*,newExpenditureLine*";
    public static final String BUDGET_CONSTRUCTION_MONTHLY_BUDGET_ERRORS = "document.budgetConstructionMonthlyBudget*";
    
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
    public static final String FINANCIAL_BALANCE_TYPE_CODE_BB = "BB";
    public static final String FINANCIAL_OBJECT_TYPE_CODE_REV = "IN";
    public static final String FINANCIAL_OBJECT_TYPE_CODE_EXP = "EX";
    public static final String APPOINTMENT_FUNDING_DURATION_DEFAULT = "NONE";
    
    public final static String SELECTION_SUB_TREE_ORGS = "selectionSubTreeOrgs";
    public final static String SHOW_INITIAL_RESULTS = "showInitialResults";
    public final static String CURRENT_POINT_OF_VIEW_KEYCODE = "currentPointOfViewKeyCode";
    
    
    
    /*
     * fund groups and subfund groups that are NOT loaded to the GL from budget construction  
     * (these are the constants used, not the ones in KFSConstants/BudgetConstructionConstants
     */
    public final static List<String> NO_BC_GL_LOAD_SUBFUND_GROUPS = Arrays.asList("SIDC");
    public final static List<String> NO_BC_GL_LOAD_FUND_GROUPS = Arrays.asList("CG");
    
    // the transaction ledger description for the general ledger budget load
    public final static String BC_TRN_LDGR_ENTR_DESC = "Beginning Budget Load";
    
    // this is a pairing of the OJB "property" for the monthly amount and its corresponding accounting period
    public final static ArrayList<String[]> BC_MONTHLY_AMOUNTS = buildMonthlyProperties();
    private static ArrayList<String[]>  buildMonthlyProperties ()
    {
        ArrayList<String[]> monthlyProperties = new ArrayList<String[]>(12);
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT,KFSConstants.MONTH1}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT,KFSConstants.MONTH2}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT,KFSConstants.MONTH3}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT,KFSConstants.MONTH4}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT,KFSConstants.MONTH5}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT,KFSConstants.MONTH6}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT,KFSConstants.MONTH7}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT,KFSConstants.MONTH8}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT,KFSConstants.MONTH9}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT,KFSConstants.MONTH10}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT,KFSConstants.MONTH11}));
        monthlyProperties.add((new String[] {KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT,KFSConstants.MONTH12}));
        return monthlyProperties;
    }
    
    public static class Report{
        public final static String INCOME_EXP_TYPE_A = "A";
        public final static String INCOME_EXP_TYPE_E = "E";
        public final static String INCOME_EXP_TYPE_T = "T";
        public final static String INCOME_EXP_TYPE_X = "X";
        
        //selection screen
        public final static String SELECTION_OPMODE_TITLE = "Sub-Fund List Selection";
        public final static String SESSION_NAME_SELECTED_ORGS = "selectedOrgs";
        public final static String REPORT_MODE = "reportMode";
        public final static String BUILD_CONTROL_LIST = "buildControlList";
        public final static String REPORT_CONSOLIDATION = "reportConsolidation";
        
        //report file name
        public final static String FILE_NAME_ORG_ACCOUNT_SUMMARY = "BudgetOrgAccountSummary";
        public final static String FILE_NAME_ORG_SUBFUND_SUMMARY = "BudgetOrgSubFundSummary";
        public final static String FILE_EXTENSION_PDF = ".pdf";
        public final static String FILE_EXTENSION_JASPER = ".jasper";
        public final static String FILE_EXTENSION_JASPER_XML = ".jrxml";
        public static final String FILE_LOCATION_JASPER = "/java/projects/kuali_project/work/src/org/kuali/module/budget/report/";
        public static final String JASPER_FILE_NAME = "BudgetOrgAccountSummary";
        
        //report type
        public static final String ACCOUNT_SUMMARY_REPORT = "AccountSummaryReport";
        public static final String SUBFUND_SUMMARY_REPORT = "SubFundSummaryReport";
        
        public static final String ACCOUNT_OBJECT_DETAIL_REPORT = "accountObjectDetailReport";
        public static final String MONTH_OBJECT_SUMMARY_REPORT = "monthObjectSummaryReport"; 
        
        public static final List<String> reportModeOnlySubfundCodeSelectionMapping = new ArrayList();
        static {
            reportModeOnlySubfundCodeSelectionMapping.add(ACCOUNT_SUMMARY_REPORT);
            reportModeOnlySubfundCodeSelectionMapping.add(SUBFUND_SUMMARY_REPORT);
        }
    }
   
}
