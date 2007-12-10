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

    public class extractProcess {
        public static final String FUND_GROUP_DENOTES_CG_IND = "FUND_GROUP_DENOTES_CG_IND";
        public static final String CG_DENOTING_VALUE = "CG_DENOTING_VALUE";
        public static final String COST_SHARE_SUB_ACCT_TYPE_CODE = "COST_SHARE_SUB_ACCT_TYPE_CODE";
        public static final String EXPENSE_SUB_ACCT_TYPE_CODE = "EXPENSE_SUB_ACCT_TYPE_CODE";
        public static final String FISCAL_YEAR = "FISCAL_YEAR";
        public static final String REPORT_NUMBER = "REPORT_NUMBER";
        public static final String RUN_ID = "RUN_ID";
        
        public static final String ACCOUNT_TYPE_CD_BALANCE_SELECT = "ACCOUNT_TYPE_CD_BALANCE_SELECT";
        public static final String FEDERAL_ONLY_BALANCE_IND = "FEDERAL_ONLY_BALANCE_IND";
        public static final String FEDERAL_AGENCY_TYPE_CD = "FEDERAL_AGENCY_TYPE_CD";
             
        public static final String EXPENSE_OBJECT_TYPE = "EXPENSE_OBJECT_TYPE";
    }
}
