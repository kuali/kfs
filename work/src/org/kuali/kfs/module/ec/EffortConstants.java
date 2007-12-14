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

import org.kuali.core.JstlConstants;
import org.kuali.kfs.KFSConstants;

/**
 * General constants for the effort reporting module.
 */
public class EffortConstants extends JstlConstants {

    public static final String LABOR_OBJECT_SALARY_CODE = "S";

    public static final List<String> ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT = getEeligibleBalanceTypesForEffortReport();

    private static final List<String> getEeligibleBalanceTypesForEffortReport() {
        List<String> balanceTypeList = new ArrayList<String>();
        balanceTypeList.add(KFSConstants.BALANCE_TYPE_ACTUAL);
        balanceTypeList.add(KFSConstants.BALANCE_TYPE_A21);
        return balanceTypeList;
    }

    /**
     * hold contants used by extract process
     */
    public class ExtractProcess{       
        public static final String DASH_CHART_OF_ACCOUNTS_CODE = "--";
        public static final String DASH_ACCOUNT_NUMBER = "-------";       
        public static final String EXPENSE_OBJECT_TYPE = "EXPENSE_OBJECT_TYPE";
              
        public static final String DERIVED_PAYROLL_CODE = "N";
        public static final String COST_SHARING_CODE = "S";
        public static final String CALCULATED_OVERALL_CODE = "N";
        public static final String UPDATED_OVERALL_CODE = "S";
        public static final String PRORATED_CODE = "S";
    }
    
    /**
     * hold all system parameter names of effort reporting module
     */
    public class SystemParameters {
        public static final String ACCOUNT_TYPE_CODE_BALANCE_SELECT = "ACCOUNT_TYPE_CODE_BALANCE_SELECT";
        public static final String CG_DENOTING_VALUE = "CG_DENOTING_VALUE";
        public static final String COST_SHARE_SUB_ACCOUNT_TYPE_CODE = "COST_SHARE_SUB_ACCOUNT_TYPE_CODE"; 
        
        public static final String EXPENSE_SUB_ACCOUNT_TYPE_CODE = "EXPENSE_SUB_ACCOUNT_TYPE_CODE";
        public static final String FEDERAL_AGENCY_TYPE_CODE = "FEDERAL_AGENCY_TYPE_CODE";
        public static final String FEDERAL_ONLY_BALANCE_IND = "FEDERAL_ONLY_BALANCE_IND";     
        public static final String FUND_GROUP_DENOTES_CG_IND = "FUND_GROUP_DENOTES_CG_IND";
        
        public static final String RUN_FISCAL_YEAR  = "RUN_FISCAL_YEAR";
        public static final String RUN_IND = "RUN_IND";
        public static final String RUN_REPORT_NUMBER  = "RUN_REPORT_NUMBER";
    }
}
