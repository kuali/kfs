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
package org.kuali.kfs.module.ec;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;


/**
 * General constants for the effort reporting module.
 */
public class EffortConstants {

    public static final String EFFORT_NAMESPACE_CODE = "KFS-EC";

    /**
     * hold constants used by extract process
     */
    public class ExtractProcess {
        public static final String EXPENSE_OBJECT_TYPE = "EXPENSE_OBJECT_TYPE";

        // the following constants used as the key of the statistics entries for the working progress report
        public static final String NUM_BALANCES_READ = "numOfBalancesRead";
        public static final String NUM_BALANCES_SELECTED = "numOfBalancesSelected";
        public static final String NUM_CERTIFICATIONS_WRITTEN = "numOfCertificationWritten";
        public static final String NUM_DETAIL_LINES_WRITTEN = "numOfDetailLineWritten";
        public static final String NUM_EMPLOYEES_SELECTED = "numOfEmployees";

        public static final String NUM_ERRORS_FOUND = "numOfErrors";
    }

    /**
     * hold all system parameter names of effort reporting module
     */
    public class SystemParameters {
        public static final String ACCOUNT_TYPE_CODE_BALANCE_SELECT = "ACCOUNT_TYPE_CODE_BALANCE_SELECT";
        public static final String CREATE_FISCAL_YEAR = "CREATE_FISCAL_YEAR";
        public static final String CREATE_REPORT_NUMBER = "CREATE_REPORT_NUMBER";
        public static final String FEDERAL_AGENCY_TYPE_CODE = "FEDERAL_AGENCY_TYPE";

        public static final String FEDERAL_ONLY_BALANCE_IND = "FEDERAL_ONLY_BALANCE_IND";
        public static final String RUN_FISCAL_YEAR = "RUN_FISCAL_YEAR";

        public static final String RUN_IND = "RUN_IND";
        public static final String RUN_REPORT_NUMBER = "RUN_REPORT_NUMBER";

        public static final String FEDERAL_ONLY_ROUTE_IND = "FEDERAL_ONLY_ROUTE_IND";
    }

    public static final String DASH_ACCOUNT_NUMBER = "-------";
    public static final String DASH_CHART_OF_ACCOUNTS_CODE = "--";
    public static final String DASH_POSITION_NUMBER = "--------";

    public static final String LABOR_OBJECT_SALARY_CODE = "S";
    public static final String VALUE_SEPARATOR = ", ";

    public static final List<String> ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT = getEeligibleBalanceTypesForEffortReport();

    private static final List<String> getEeligibleBalanceTypesForEffortReport() {
        List<String> balanceTypeList = new ArrayList<String>();
        balanceTypeList.add(KFSConstants.BALANCE_TYPE_ACTUAL);
        balanceTypeList.add(KFSConstants.BALANCE_TYPE_A21);

        return balanceTypeList;
    }

    public static final List<String> ELIGIBLE_COST_SHARE_SUB_ACCOUNT_TYPE_CODES = getEligibleCostShareSubAccountTypeCodes();

    private static final List<String> getEligibleCostShareSubAccountTypeCodes(){
        List<String> costShareSubAccountTypeCodesList = new ArrayList<String>();
        costShareSubAccountTypeCodesList.add(KFSConstants.SubAccountType.COST_SHARE);
        return costShareSubAccountTypeCodesList;
    }

    public static final List<String> ELIGIBLE_EXPENSE_SUB_ACCOUNT_TYPE_CODES = getEligibleExpenseSubAccountTypeCodes();

    private static final List<String> getEligibleExpenseSubAccountTypeCodes(){
        List<String> expenseSubAccountTypeCodesList = new ArrayList<String>();
        expenseSubAccountTypeCodesList.add(KFSConstants.SubAccountType.EXPENSE);
        return expenseSubAccountTypeCodesList;
    }

    public static final double PERCENT_LIMIT_OF_LINE_SALARY_CHANGE = 0.005;
    public static final double AMOUNT_LIMIT_OF_TOTAL_SALARY_CHANGE = 0.009;

    public static final String EFFORT_DETAIL_IMPORT_ERRORS = "effortDetailImportError";
    public static final String REQUIRED_IMPORT_FIELDS_ERRORS = "requiredImportFieldError";
    public static final String DOCUMENT_PREFIX = "document.";

    public static final String EFFORT_CERTIFICATION_TAB_ERRORS = "newDetailLine.*,document.effortCertificationDetailLines*,document.summarizedDetailLines*";

    public static final String RECREATED_DOCUMENT_MESSAGE_KEY = "message.effort.recreatedDocumentMessage";

    public static final String GENERATE_EFFORT_CERTIFICATION_REPORT_DEFINITION_QUESTION_ID = "GenerateDefinitionQuestion";

    public static class EffortCertificationEditMode {
        public static final String DETAIL_TAB_ENTRY = "detailTabEntry";
        public static final String SUMMARY_TAB_ENTRY = "summaryTabEntry";
    }

    public static final List<String> DETAIL_LINES_CONSOLIDATION_FILEDS = getDetailLinesConsolidationFields();
    private static final List<String> getDetailLinesConsolidationFields() {
        List<String> consolidationFields = new ArrayList<String>();
        consolidationFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        consolidationFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        consolidationFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        return consolidationFields;
    }

    public static final List<String> DETAIL_LINES_GROUPING_FILEDS = getDetailLinesGroupingFields();
    private static final List<String> getDetailLinesGroupingFields() {
        List<String> groupingFields = new ArrayList<String>();
        groupingFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupingFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupingFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupingFields.add(EffortPropertyConstants.NEW_LINE_INDICATOR);

        return groupingFields;
    }

    public static final String SORT_DETAIL_LINE_BY_COLUMN_METHOD_NAME = "sortDetailLineByColumn";

    public static class EffortDocumentTypes {
        public static final String EFFORT_CERTIFICATION_DOCUMENT = "ECD";
    }

    public static class BalanceInquiries {
        public static final String BALANCE_TYPE_AC_AND_A21 = "AC&A2";
    }

}
