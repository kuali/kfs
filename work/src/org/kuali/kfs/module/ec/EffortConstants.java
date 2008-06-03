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
package org.kuali.module.effort;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants.ChartApcParms;
import org.kuali.kfs.authorization.KfsAuthorizationConstants.TransactionalEditMode;
import org.kuali.rice.util.JSTLConstants;

/**
 * General constants for the effort reporting module.
 */
public class EffortConstants extends JSTLConstants {
    /**
     * hold contants used by extract process
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
     * Constants for the fixed period status code values.
     */
    public class PeriodStatusCodes {
        public static final String CLOSED = "C";
        public static final String NOT_OPEN = "N";
        public static final String OPEN = "O";
    }

    /**
     * hold all system parameter names of effort reporting module
     */
    public class SystemParameters {
        public static final String ACCOUNT_TYPE_CODE_BALANCE_SELECT = "ACCOUNT_TYPE_CODE_BALANCE_SELECT";
        public static final String CG_DENOTING_VALUE = ChartApcParms.ACCOUNT_CG_DENOTING_VALUE;
        public static final String COST_SHARE_SUB_ACCOUNT_TYPE_CODE = "COST_SHARE_SUB_ACCOUNT_TYPE_CODE";

        public static final String CREATE_FISCAL_YEAR = "CREATE_FISCAL_YEAR";
        public static final String CREATE_REPORT_NUMBER = "CREATE_REPORT_NUMBER";
        public static final String EXPENSE_SUB_ACCOUNT_TYPE_CODE = "EXPENSE_SUB_ACCOUNT_TYPE_CODE";
        public static final String FEDERAL_AGENCY_TYPE_CODE = "FEDERAL_AGENCY_TYPE_CODE";

        public static final String FEDERAL_ONLY_BALANCE_IND = "FEDERAL_ONLY_BALANCE_IND";
        public static final String FUND_GROUP_DENOTES_CG_IND = ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG;
        public static final String RUN_FISCAL_YEAR = "RUN_FISCAL_YEAR";

        public static final String RUN_IND = "RUN_IND";
        public static final String RUN_REPORT_NUMBER = "RUN_REPORT_NUMBER";

        public static final String ORGANIZATION_ROUTING_EDITABLE_IND = "ORGANIZATION_ROUTING_EDITABLE_IND";
        public static final String AWARD_ROUTING_EDITABLE_IND = "AWARD_ROUTING_EDITABLE_IND";
        public static final String ADMINISTRATOR_ROUTING_EDITABLE_IND = "ADMINISTRATOR_ROUTING_EDITABLE_IND";
        public static final String FEDERAL_ONLY_ROUTE_IND = "FEDERAL_ONLY_ROUTE_IND";
    }

    public static final String DASH_ACCOUNT_NUMBER = "-------";
    public static final String DASH_CHART_OF_ACCOUNTS_CODE = "--";

    public static final String LABOR_OBJECT_SALARY_CODE = "S";
    public static final String VALUE_SEPARATOR = ", ";

    public static final List<String> ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT = getEeligibleBalanceTypesForEffortReport();

    private static final List<String> getEeligibleBalanceTypesForEffortReport() {
        List<String> balanceTypeList = new ArrayList<String>();
        balanceTypeList.add(KFSConstants.BALANCE_TYPE_ACTUAL);
        balanceTypeList.add(KFSConstants.BALANCE_TYPE_A21);
        
        return balanceTypeList;
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
        public static final String UNVIEWABLE = TransactionalEditMode.UNVIEWABLE;
        public static final String VIEW_ONLY = TransactionalEditMode.VIEW_ONLY;
        public static final String FULL_ENTRY = TransactionalEditMode.FULL_ENTRY;
        public static final String EXPENSE_ENTRY = TransactionalEditMode.EXPENSE_ENTRY;

        public static final String PROJECT_ENTRY = "projectEntry";
    }
    
    public static final List<String> DETAIL_LINES_CONSOLIDATION_FILEDS = getDetailLinesConsolidationFields();
    private static final List<String> getDetailLinesConsolidationFields() {
        List<String> consolidationFields = new ArrayList<String>();
        consolidationFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        consolidationFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        consolidationFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        return consolidationFields;
    }
}
