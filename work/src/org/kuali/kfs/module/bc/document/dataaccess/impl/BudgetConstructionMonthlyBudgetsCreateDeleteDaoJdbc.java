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

import java.lang.Exception;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao;
import org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService;
import org.kuali.module.budget.dao.ojb.GenesisDaoOjb;
import org.kuali.module.budget.BCParameterKeyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.KFSConstants;

import org.kuali.module.budget.document.BudgetConstructionDocument;


public class BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionMonthlyBudgetsCreateDeleteDao {
    


    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc.class);


    private static ArrayList<SQLForStep> deleteAllSql         = new ArrayList<SQLForStep>(2);
    private static ArrayList<SQLForStep> spreadRevenueSql     = new ArrayList<SQLForStep>(2);
    private static ArrayList<SQLForStep> spreadExpenditureSql = new ArrayList<SQLForStep>(3);
    
    @RawSQL
    public BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc()
    {

        StringBuilder sqlBuilder = new StringBuilder(5000);
        ArrayList<Integer> insertionPoints = new ArrayList<Integer>();
        // delete all rows for a given key from the budget construction monthly table  
        sqlBuilder.append("DELETE FROM ld_bcnstr_month_t\n"); 
        sqlBuilder.append("WHERE (fdoc_nbr = ?)\n"); 
        sqlBuilder.append("  AND (univ_fiscal_yr = ?)\n"); 
        sqlBuilder.append("  AND (fin_coa_cd = ?)\n"); 
        sqlBuilder.append("  AND (account_nbr = ?)\n"); 
        sqlBuilder.append("  AND (sub_acct_nbr = ?)\n"); 
        sqlBuilder.append("  AND (fin_obj_typ_cd IN ");
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append(")");
        // revenue
        deleteAllSql.add(new SQLForStep(sqlBuilder,insertionPoints));
        // expenditure (exact same thing at present)
        deleteAllSql.add(new SQLForStep(sqlBuilder,insertionPoints));
        sqlBuilder.delete(0,sqlBuilder.length());
        insertionPoints.clear();
        
        // SQL needed to spread revenue
        // delete existing revenue for this key, so it can be spread again
        sqlBuilder.append("DELETE FROM ld_bcnstr_month_t\n"); 
        sqlBuilder.append("WHERE (fdoc_nbr = ?)\n"); 
        sqlBuilder.append("  AND (univ_fiscal_yr = ?)\n"); 
        sqlBuilder.append("  AND (fin_coa_cd = ?)\n"); 
        sqlBuilder.append("  AND (account_nbr = ?)\n"); 
        sqlBuilder.append("  AND (sub_acct_nbr = ?)\n"); 
        sqlBuilder.append("  AND (fin_obj_typ_cd IN ");
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append(")");
        spreadRevenueSql.add(new SQLForStep(sqlBuilder,insertionPoints));
        insertionPoints.clear();

        sqlBuilder.delete(0,sqlBuilder.length());
        // insert ALL revenue (since we do not re-calculate benefits on revenue, any revenue benefits object classes should be spread along with the other object classes
        sqlBuilder.append("INSERT INTO LD_BCNSTR_MONTH_T\n"); 
        sqlBuilder.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n");
        sqlBuilder.append(" FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT,\n");
        sqlBuilder.append(" FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT)\n");
        sqlBuilder.append("(SELECT ?, ?, ?, ?, ?, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n"); 
        sqlBuilder.append("        ROUND((acln_annl_bal_amt / 12), 0) + \n");
        sqlBuilder.append("        (acln_annl_bal_amt - (ROUND((acln_annl_bal_amt / 12), 0) * 12)),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0)\n");
        sqlBuilder.append("        FROM ld_pnd_bcnstr_gl_t\n"); 
        sqlBuilder.append("        WHERE (fdoc_nbr = ?)\n");
        sqlBuilder.append("          AND (univ_fiscal_yr = ?)\n");
        sqlBuilder.append("          AND (fin_coa_cd = ?)\n");
        sqlBuilder.append("          AND (account_nbr = ?)\n");
        sqlBuilder.append("          AND (sub_acct_nbr = ?)\n");
        sqlBuilder.append("          AND (fin_obj_typ_cd IN ");
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("))");
        spreadRevenueSql.add(new SQLForStep(sqlBuilder,insertionPoints));
        insertionPoints.clear();

        sqlBuilder.delete(0,sqlBuilder.length());
        
        // SQL to spread expenditure
        // delete existing monthly expenditure (except for actual benefits objects--the benefits will be recalulated and spread later from the GL) 
        sqlBuilder.append("DELETE FROM ld_bcnstr_month_t\n"); 
        sqlBuilder.append("WHERE (fdoc_nbr = ?)\n"); 
        sqlBuilder.append("  AND (univ_fiscal_yr = ?)\n"); 
        sqlBuilder.append("  AND (fin_coa_cd = ?)\n"); 
        sqlBuilder.append("  AND (account_nbr = ?)\n"); 
        sqlBuilder.append("  AND (sub_acct_nbr = ?)\n"); 
        sqlBuilder.append("  AND (fin_obj_typ_cd IN ");
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append(")\n");
        sqlBuilder.append("AND (NOT EXISTS (SELECT 1\n");
        sqlBuilder.append("                 FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("                   AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("                    AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_BCNSTR_MONTH_T.FIN_OBJECT_CD)))\n");
        spreadExpenditureSql.add(new SQLForStep(sqlBuilder,insertionPoints));
        insertionPoints.clear();
        
        sqlBuilder.delete(0,sqlBuilder.length());
        // spread the general ledger expenditure anew over the 12 months
        sqlBuilder.append("INSERT INTO LD_BCNSTR_MONTH_T\n"); 
        sqlBuilder.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n");
        sqlBuilder.append(" FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT,\n");
        sqlBuilder.append(" FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT)\n");
        sqlBuilder.append("(SELECT ?, ?, ?, ?, ?, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n"); 
        sqlBuilder.append("        ROUND((acln_annl_bal_amt / 12), 0) + \n");
        sqlBuilder.append("        (acln_annl_bal_amt - (ROUND((acln_annl_bal_amt / 12), 0) * 12)),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        sqlBuilder.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0)\n");
        sqlBuilder.append("        FROM ld_pnd_bcnstr_gl_t\n"); 
        sqlBuilder.append("        WHERE (fdoc_nbr = ?)\n");
        sqlBuilder.append("          AND (univ_fiscal_yr = ?)\n");
        sqlBuilder.append("          AND (fin_coa_cd = ?)\n");
        sqlBuilder.append("          AND (account_nbr = ?)\n");
        sqlBuilder.append("          AND (sub_acct_nbr = ?)\n");
        sqlBuilder.append("AND (NOT EXISTS (SELECT 1\n");
        sqlBuilder.append("                 FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("                   AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("                  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))\n");
        sqlBuilder.append("          AND (fin_obj_typ_cd IN ");
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("))");
        spreadExpenditureSql.add(new SQLForStep(sqlBuilder,insertionPoints));
        insertionPoints.clear();

        sqlBuilder.delete(0,sqlBuilder.length());
        // count the number of object classes eligible for fringe benefits, to signal the caller that benefits need to be recalculated and spread.
        sqlBuilder.append("SELECT COUNT(*)\n");
        sqlBuilder.append("FROM (LD_BCNSTR_MONTH_T INNER JOIN LD_LBR_OBJ_BENE_T\n");
        sqlBuilder.append("  ON ((LD_BCNSTR_MONTH_T.UNIV_FISCAL_YR = LD_LBR_OBJ_BENE_T.UNIV_FISCAL_YR) AND\n");
        sqlBuilder.append("      (LD_BCNSTR_MONTH_T.FIN_COA_CD = LD_LBR_OBJ_BENE_T.FIN_COA_CD) AND\n");
        sqlBuilder.append("      (LD_BCNSTR_MONTH_T.FIN_OBJECT_CD = LD_LBR_OBJ_BENE_T.FIN_OBJECT_CD)))\n");
        sqlBuilder.append("WHERE (LD_BCNSTR_MONTH_T.FDOC_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_BCNSTR_MONTH_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (LD_BCNSTR_MONTH_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("  AND (LD_BCNSTR_MONTH_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_BCNSTR_MONTH_T.SUB_ACCT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_BCNSTR_MONTH_T.FIN_OBJ_TYP_CD IN ");
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append(")\n");
        spreadExpenditureSql.add(new SQLForStep(sqlBuilder,insertionPoints));
        insertionPoints.clear();
        
    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void deleteBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {

        // get the revenue object types as an SQL IN list
        ArrayList<String> inSqlString = new ArrayList<String>();
        inSqlString.add(getRevenueINList());
        
        //run the delete-all SQL with the revenue object classes
        int returnCount = getSimpleJdbcTemplate().update(deleteAllSql.get(0).getSQL(inSqlString), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void deleteBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber)throws IOException, NoSuchFieldException {

        // get the expenditure object types as an SQL IN list
        ArrayList<String> inSqlString = new ArrayList<String>();
        inSqlString.add(getExpenditureINList());
        
        // run the delete-all SQL with the expenditure object classes
        int returnCount = getSimpleJdbcTemplate().update(deleteAllSql.get(1).getSQL(inSqlString), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void spreadBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
    
        // for revenue, we delete all existing rows, and spread all the corresponding rows in the general ledger
        // if there is any revenue for benefits, it will be spread, not calculated based on non-benefits rows as expenditure benefits will be
        // get the revenue IN list
        ArrayList<String> inSqlString = new ArrayList<String>();
        inSqlString.add(getRevenueINList());

        
        // delete what is there now for this document for the revenue object classes
        int returnCount = getSimpleJdbcTemplate().update(spreadRevenueSql.get(0).getSQL(inSqlString), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n RevenueSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        // run the create-monthly-budgets-from-GL SQL with the revenue object classes
        returnCount = getSimpleJdbcTemplate().update(spreadRevenueSql.get(1).getSQL(inSqlString),documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber );
        LOG.warn(String.format("\n%s\n RevenueSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }
    
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public boolean spreadBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {

        // spread general ledger expenditures across 12 months, excluding benefits object types.  benefits object expenditure will be recalculated and spread later, because several object codes eligible for benefits can target the same fringe benefit object
        // get the expenditure object types as an SQL IN list
        ArrayList<String> inSqlString = new ArrayList<String>();
        inSqlString.add(getExpenditureINList());
        
        // run the delete-all-except-benefits SQL with the expenditure object classes
        int returnCount = getSimpleJdbcTemplate().update(spreadExpenditureSql.get(0).getSQL(inSqlString), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode);
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        // run the create-monthly-budgets-from-GL SQL with the expenditure object classes
        returnCount = getSimpleJdbcTemplate().update(spreadExpenditureSql.get(1).getSQL(inSqlString), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode);
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        // tell the caller whether there were any benefits-eligible object classes with non-zero amounts
        return(budgetConstructionMonthlyBudgetContainsBenefitsExpenditure(spreadExpenditureSql.get(2).getSQL(inSqlString), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber));
    }

    /**
     * 
     *  return true if there are benefits object codes in the general ledger for the document, false otherwise
     */
    @RawSQL
    private boolean budgetConstructionMonthlyBudgetContainsBenefitsExpenditure(String BenefitsObjectsCheckSQL, String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        Long numberOfBenefitsEligibleRows = getSimpleJdbcTemplate().queryForLong(BenefitsObjectsCheckSQL,documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        return (numberOfBenefitsEligibleRows != 0);
    }
    
  
}
