/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.dao.jdbc;

import java.util.ArrayList;

import org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao;
import org.kuali.module.budget.BCParameterConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.KFSConstants;

import org.kuali.module.budget.document.BudgetConstructionDocument;


public class BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionMonthlyBudgetsCreateDeleteDao {

    private Object[] inRevenueObjectTypes                           = null;
    private Object[] inExpenditureObjectTypes                       = null;
    private StringBuilder deleteBuilder                             = new StringBuilder(800);
    private StringBuilder deleteRevenueBuilder                      = new StringBuilder(800);
    private StringBuilder benefitsExpenseObjectClassesMonthly       = new StringBuilder(300);
    private StringBuilder benefitsExpenseObjectClassesGeneralLedger = new StringBuilder(300);
    // SQL built on the first call from the StringBuilders created in the constructor
    private String deleteAllRowsSQL;
    private String deleteNoBenefitsRowsSQL;
    
    public BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc()
    {
        // this part of the DELETE SQL is always the same 
        deleteBuilder.append("DELETE FROM ld_bcnstr_month_t\n"); 
        deleteBuilder.append("WHERE (fs_origin_cd = ?)\n"); 
        deleteBuilder.append("  AND fdoc_nbr = ?)\n"); 
        deleteBuilder.append("  AND univ_fiscal_yr = ?)\n"); 
        deleteBuilder.append("  AND fin_coa_cd = ?)\n"); 
        deleteBuilder.append("  AND account_nbr = ?)\n"); 
        deleteBuilder.append("  AND sub_acct_nbr = ?)\n"); 
        deleteBuilder.append("  AND fin_obj_typ_cd IN ");
        
        // this is the common way we test for a benefits object class (LD_BCNSTR_MONTH_T/LD_PND_BCNSTR_GL_T)
        benefitsExpenseObjectClassesMonthly.append("NOT EXISTS (SELECT 1\n");
        benefitsExpenseObjectClassesMonthly.append("            FROM LD_BENEFITS_CALC_T\n");
        benefitsExpenseObjectClassesMonthly.append("            WHERE (LD_BENEFITS_CALC.UNIV_FISCAL_YR = ?)\n");
        benefitsExpenseObjectClassesMonthly.append("              AND (LD_BENEFITS_CALC.FIN_COA_CD = ?)\n");
        
        benefitsExpenseObjectClassesGeneralLedger.append(benefitsExpenseObjectClassesMonthly);
        benefitsExpenseObjectClassesMonthly.append("              AND (LD_BENEFITS_CALC.POS_FRNGBEN_OBJ_CD = LD_BCNSTR_MONTH_T.FIN_OBJECT_CD)))\n");
        benefitsExpenseObjectClassesGeneralLedger.append("              AND (LD_BENEFITS_CALC.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))\n");
    }
    
    private ParameterService parameterService;
    
    
    public void BudgetConstructionMonthlyBudgetsDeleteRevenue(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
        // TODO Auto-generated method stub

    }

    public void BudgetConstructionMonthlyBudgetsDeleteExpenditure(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
        // TODO Auto-generated method stub

    }

    public void BudgetConstructionMonthlyBudgetsSpreadRevenue(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
        // TODO Auto-generated method stub

    }

    public void BudgetConstructionMonthlyBudgetsSpreadExpenditure(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
        // TODO Auto-generated method stub

    }

    public boolean BudgetConstructionMonthlyBudgetContainsBenefitsExpenditure(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
        // TODO Auto-generated method stub
        return false;
    }
    /**
     * 
     * check to see whether the variables have been initialized from the parameterService.  if not, initialize them here on the first call.
     * by default, Spring beans are singletons, so the IN list should only be built once.
     * this also takes care of the fact that StringBuilder is not thread-safe
     */
    private synchronized void buildSqlInListIfNecessary()
    {
        // because this is synchronized, if two people working on different accounts try to 
        // access it at once, only one will build the SQL.  The other will see the SQL strings
        // that have already been built.
        if (inRevenueObjectTypes != null)
        {
            return;
        }

        StringBuilder inClauseBuilder = new StringBuilder(200);
        
        // get the values of the parameters
        inRevenueObjectTypes = ((ArrayList<String>) parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterConstants.REVENUE_OBJECT_TYPES)).toArray();
        inExpenditureObjectTypes = ((ArrayList<String>) parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterConstants.EXPENDITURE_OBJECT_TYPES)).toArray();
        // build the IN clause
        inClauseBuilder.append("(FIN_OBJ_TYP_CD IN ");
        inClauseBuilder.append(this.inString(inRevenueObjectTypes.length));
        inClauseBuilder.append(")");
        // build the SQL we need
        // (1) delete all monthly rows (including those with benefits object codes)
        StringBuilder deleteBuilder = new StringBuilder(1000);
        deleteBuilder.append(deleteBuilder);
        deleteBuilder.append(inClauseBuilder);
        deleteAllRowsSQL = deleteBuilder.toString();
        //  (1) delete only monthly rows that do not have benefit object codes
        deleteBuilder.append("\n");
        deleteBuilder.append(benefitsExpenseObjectClassesMonthly);
        deleteNoBenefitsRowsSQL = deleteBuilder.toString();
        
    }
    
    public void setParameterService(ParameterService parameterService)
    {
        this.parameterService = parameterService;
    }

}
