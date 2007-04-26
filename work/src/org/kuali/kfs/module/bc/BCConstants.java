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

import org.kuali.core.JstlConstants;

public class BCConstants extends JstlConstants {

    public static final String BC_SELECTION_ACTION="budgetBudgetConstructionSelection.do";
    public static final String BC_SELECTION_REFRESH_METHOD="refresh";

    public static final String BC_DOCUMENT_REFRESH_CALLER="BudgetConstruction";
    public static final String BC_DOCUMENT_ACTION="budgetBudgetConstruction.do";
    public static final String BC_DOCUMENT_REFRESH_METHOD="refresh";
    public static final String BC_DOCUMENT_METHOD="docHandler";
    
    public static final String MONTHLY_BUDGET_REFRESH_CALLER="MonthlyBudget";
    public static final String MONTHLY_BUDGET_ACTION="budgetMonthlyBudget.do";
    public static final String MONTHLY_BUDGET_METHOD="loadExpansionScreen";

    public static final String SALARY_SETTING_REFRESH_CALLER="SalarySetting";
    public static final String SALARY_SETTING_ACTION="budgetSalarySetting.do";
    public static final String SALARY_SETTING_METHOD="loadExpansionScreen";

    public static final String POSITION_SALARY_SETTING_REFRESH_CALLER="PositionSalarySetting";
    public static final String POSITION_SALARY_SETTING_ACTION="budgetPositionSalarySetting.do";
    public static final String POSITION_SALARY_SETTING_METHOD="loadExpansionScreen";

    public static final String INCUMBENT_SALARY_SETTING_REFRESH_CALLER="IncumbentSalarySetting";
    public static final String INCUMBENT_SALARY_SETTING_ACTION="budgetIncumbentSalarySetting.do";
    public static final String INCUMBENT_SALARY_SETTING_METHOD="loadExpansionScreen";

    public static final String RETURN_ANCHOR="returnAnchor";
    public static final String RETURN_FORM_KEY="returnFormKey";
    
    public static final String INSERT_REVENUE_LINE_METHOD = "insertRevenueLine";
    public static final String INSERT_EXPENDITURE_LINE_METHOD = "insertExpenditureLine";
    public static final String FINANCIAL_BALANCE_TYPE_CODE_BB = "BB";
    public static final String FINANCIAL_OBJECT_TYPE_CODE_REV = "IN";
    public static final String FINANCIAL_OBJECT_TYPE_CODE_EXP = "EX";
    public static final String APPOINTMENT_FUNDING_DURATION_DEFAULT = "NONE";

}
