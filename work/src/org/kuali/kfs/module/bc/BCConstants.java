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
    
    public static final String DISABLE_SALARY_SETTING_FLAG = "DISABLE_SALARY_SETTING_FLAG";
    public static final String DISABLE_BENEFITS_CALCULATION_FLAG = "DISABLE_BENEFITS_CALCULATION_FLAG";

    public static final String BC_SELECTION_ACTION="budgetBudgetConstructionSelection.do";
    public static final String BC_SELECTION_REFRESH_METHOD="refresh";

    public static final String ORG_SEL_TREE_REFRESH_CALLER="BudgetConstruction";
    public static final String ORG_SEL_TREE_ACTION="budgetOrganizationSelectionTree.do";
    public static final String ORG_SEL_TREE_METHOD="loadExpansionScreen";

    public enum OrgSelOpMode {
      PULLUP, PUSHDOWN, REPORTS, SALSET, ACCOUNT
    }
    
    /**
     * This class represents Select control options mapping explicit Integer values
     * to an enum value. The explicit values can then be used in a database stored procedure
     * call in the event procedure calls are used instead of calls to a java method.
     */
    public enum OrgSelControlOption {
        NO(0,"No"), YES(1,"Yes"), 
        NOTSEL(0,"Not Sel"), ORG(1,"Org"), SUBORG(2,"Sub Org"), BOTH(3,"Both"),
        ORGLEV(1,"Org Lev"), MGRLEV(2,"Mgr Lev"), ORGMGRLEV(3,"Org+Mgr Lev"), LEVONE(4,"Lev One"), LEVZERO(5,"Lev Zero");
        private String label;
        private Integer key;
        private OrgSelControlOption(Integer key, String label){ this.key = key; this.label = label; }
        public String getLabel() {return label; }
        public Integer getKey() {return key; }
    }

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
