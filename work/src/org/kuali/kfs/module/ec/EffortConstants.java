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

    public class ExtractProcess{       
        public static final String DASH_CHART_OF_ACCOUNTS_CODE = "--";
        public static final String DASH_ACCOUNT_NUMBER = "-------";       
        public static final String EXPENSE_OBJECT_TYPE = "EXPENSE_OBJECT_TYPE";
    }
}
