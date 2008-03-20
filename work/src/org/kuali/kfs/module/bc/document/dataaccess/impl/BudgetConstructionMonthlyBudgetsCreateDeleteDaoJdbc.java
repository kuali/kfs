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
import org.kuali.module.budget.BCParameterConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.KFSConstants;

import org.kuali.module.budget.document.BudgetConstructionDocument;


public class BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionMonthlyBudgetsCreateDeleteDao {
    


    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc.class);

    // SQL built on the first call to any method, based the StringBuilders created in the constructor
    private String deleteAllRevenueRowsSQL;
    private String deleteAllExpenditureRowsSQL;
    private String deleteNoBenefitsRevenueRowsSQL;
    private String deleteNoBenefitsExpenditureRowsSQL;
    private String expenditureBenefitsObjectClassesCheckSQL; 
    private String allocateGeneralLedgerExpenditureByMonthSQL;
    private String allocateGeneralLedgerRevenueByMonthSQL;
    
    private int deleteBuilderRevenueInLocation;
    private int deleteBuilderExpenditureInLocation;
    
    @RawSQL
    public BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc()
    {
        /**
         *  we try to set up the SQL in the constructor. 
         */
        
        // string builders used to generate the SQL.  these are initialized as far as possible in the constructor
        StringBuilder deleteBuilderExpenditure                = new StringBuilder(800);
        StringBuilder deleteBuilderRevenue                    = new StringBuilder(800);  
        StringBuilder benefitsObjectClassesMonthly            = new StringBuilder(300);
        StringBuilder benefitsObjectClassesGeneralLedger      = new StringBuilder(300);
        StringBuilder expenditureBenefitsObjectClassesCheck   = new StringBuilder(1500);
        StringBuilder allocateGeneralLedgerRevenueByMonth     = new StringBuilder(5000);
        StringBuilder allocateGeneralLedgerExpenditureByMonth = new StringBuilder(5000);
        
        // this is the  way we test in the WHERE clauses for a benefits object class (LD_BCNSTR_MONTH_T/LD_PND_BCNSTR_GL_T)
        // the monthly EXISTS clause is used for the expenditure deletes
        benefitsObjectClassesMonthly.append("AND (NOT EXISTS (SELECT 1\n");
        benefitsObjectClassesMonthly.append("                 FROM LD_BENEFITS_CALC_T\n");
        benefitsObjectClassesMonthly.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
        benefitsObjectClassesMonthly.append("                   AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
        benefitsObjectClassesMonthly.append("                    AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_BCNSTR_MONTH_T.FIN_OBJECT_CD)))\n");
        // the general ledger EXISTS clause is used for the expenditure inserts
        benefitsObjectClassesGeneralLedger.append("AND (NOT EXISTS (SELECT 1\n");
        benefitsObjectClassesGeneralLedger.append("                 FROM LD_BENEFITS_CALC_T\n");
        benefitsObjectClassesGeneralLedger.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
        benefitsObjectClassesGeneralLedger.append("                   AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
        benefitsObjectClassesGeneralLedger.append("                  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))\n");

        // this part of the DELETE SQL is always the same, but we repeat it so it's "readable" 
        deleteBuilderRevenue.append("DELETE FROM ld_bcnstr_month_t\n"); 
        deleteBuilderRevenue.append("WHERE (fdoc_nbr = ?)\n"); 
        deleteBuilderRevenue.append("  AND (univ_fiscal_yr = ?)\n"); 
        deleteBuilderRevenue.append("  AND (fin_coa_cd = ?)\n"); 
        deleteBuilderRevenue.append("  AND (account_nbr = ?)\n"); 
        deleteBuilderRevenue.append("  AND (sub_acct_nbr = ?)\n"); 
        deleteBuilderRevenue.append("  AND (fin_obj_typ_cd IN ");
        // save the IN clause location to delete all revenue
        deleteBuilderRevenueInLocation = deleteBuilderRevenue.length();
        deleteBuilderRevenue.append(")");
        // repeat all the code for the expenditure
        deleteBuilderExpenditure.append("DELETE FROM ld_bcnstr_month_t\n"); 
        deleteBuilderExpenditure.append("WHERE (fdoc_nbr = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (univ_fiscal_yr = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (fin_coa_cd = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (account_nbr = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (sub_acct_nbr = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (fin_obj_typ_cd IN ");
        // save the IN clause location to delete all expenditure
        this.deleteBuilderExpenditureInLocation = deleteBuilderExpenditure.length();
        deleteBuilderExpenditure.append(")");
        // add to the code delete all expenditure to get the deleteSQL that deletes expenditure that does not have a fringe benefit object class
        deleteBuilderExpenditure.delete(0,deleteBuilderExpenditure.length());
        deleteBuilderExpenditure.append("DELETE FROM ld_bcnstr_month_t\n"); 
        deleteBuilderExpenditure.append("WHERE (fdoc_nbr = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (univ_fiscal_yr = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (fin_coa_cd = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (account_nbr = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (sub_acct_nbr = ?)\n"); 
        deleteBuilderExpenditure.append("  AND (fin_obj_typ_cd IN ");
//        deleteBuilderExpenditure.append(expenditureInString);
        deleteBuilderExpenditure.append(")\n");
        deleteBuilderExpenditure.append("AND (NOT EXISTS (SELECT 1\n");
        deleteBuilderExpenditure.append("                 FROM LD_BENEFITS_CALC_T\n");
        deleteBuilderExpenditure.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
        deleteBuilderExpenditure.append("                   AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
        deleteBuilderExpenditure.append("                    AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_BCNSTR_MONTH_T.FIN_OBJECT_CD)))\n");
        deleteNoBenefitsExpenditureRowsSQL = deleteBuilderExpenditure.toString();
        
        // we do a COUNT(*) to decide whether any benefits expense objects occur in LD_BCNSTR_MONTH_T
        expenditureBenefitsObjectClassesCheck.append("SELECT COUNT(*)\n");
        expenditureBenefitsObjectClassesCheck.append("FROM (LD_BCNSTR_MONTH_T INNER JOIN LD_LBR_OBJ_BENE_T\n");
        expenditureBenefitsObjectClassesCheck.append("  ON ((LD_BCNSTR_MONTH_T.UNIV_FISCAL_YR = LD_LBR_OBJ_BENE_T.UNIV_FISCAL_YR) AND\n");
        expenditureBenefitsObjectClassesCheck.append("      (LD_BCNSTR_MONTH_T.FIN_COA_CD = LD_LBR_OBJ_BENE_T.FIN_COA_CD) AND\n");
        expenditureBenefitsObjectClassesCheck.append("      (LD_BCNSTR_MONTH_T.FIN_OBJECT_CD = LD_LBR_OBJ_BENE_T.FIN_OBJECT_CD)))\n");
        expenditureBenefitsObjectClassesCheck.append("WHERE (LD_BCNSTR_MONTH_T.FDOC_NBR = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.UNIV_FISCAL_YR = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.FIN_COA_CD = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.ACCOUNT_NBR = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.SUB_ACCT_NBR = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.FIN_OBJ_TYP_CD IN ");
//        expenditureBenefitsObjectClassesCheck.append(expenditureInString);
        expenditureBenefitsObjectClassesCheck.append(")\n");
        expenditureBenefitsObjectClassesCheckSQL = expenditureBenefitsObjectClassesCheck.toString();
        
        // build common part of the SQL that spreads the pending general ledger equally among 12 months.  each month is allocated
        // the total amount / 12, rounded to whole dollars.  the rounding error is added to the first month, so summing all months gives the original amount
        allocateGeneralLedgerRevenueByMonth.append("INSERT INTO LD_BCNSTR_MONTH_T\n"); 
        allocateGeneralLedgerRevenueByMonth.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n");
        allocateGeneralLedgerRevenueByMonth.append(" FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT,\n");
        allocateGeneralLedgerRevenueByMonth.append(" FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT)\n");
        allocateGeneralLedgerRevenueByMonth.append("(SELECT ?, ?, ?, ?, ?, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n"); 
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((acln_annl_bal_amt / 12), 0) + \n");
        allocateGeneralLedgerRevenueByMonth.append("        (acln_annl_bal_amt - (ROUND((acln_annl_bal_amt / 12), 0) * 12)),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0)\n");
        allocateGeneralLedgerRevenueByMonth.append("        FROM ld_pnd_bcnstr_gl_t\n"); 
        allocateGeneralLedgerRevenueByMonth.append("        WHERE (fdoc_nbr = ?)\n");
        allocateGeneralLedgerRevenueByMonth.append("          AND (univ_fiscal_yr = ?)\n");
        allocateGeneralLedgerRevenueByMonth.append("          AND (fin_coa_cd = ?)\n");
        allocateGeneralLedgerRevenueByMonth.append("          AND (account_nbr = ?)\n");
        allocateGeneralLedgerRevenueByMonth.append("          AND (sub_acct_nbr = ?)\n");
        allocateGeneralLedgerRevenueByMonth.append(benefitsObjectClassesGeneralLedger);
        allocateGeneralLedgerRevenueByMonth.append("          AND (fin_obj_typ_cd IN ");
        // use the exact same SQL pattern in the query for expenditure
        allocateGeneralLedgerExpenditureByMonth.append(allocateGeneralLedgerRevenueByMonth);
        
        // SQL to allocate revenue by month
        // add the revenue IN clause to the common SQL pattern 
//        allocateGeneralLedgerRevenueByMonth.append(revenueInString);
        allocateGeneralLedgerRevenueByMonth.append("))");
        allocateGeneralLedgerRevenueByMonthSQL = allocateGeneralLedgerRevenueByMonth.toString();
        //
        // SQL to allocate expenditure by month
        // add the expenditure IN clause to the common SQL pattern
//        allocateGeneralLedgerExpenditureByMonth.append(expenditureInString);
        allocateGeneralLedgerExpenditureByMonth.append("))");
        allocateGeneralLedgerExpenditureByMonthSQL = allocateGeneralLedgerExpenditureByMonth.toString();
    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public BCConstants.benefitsResult deleteBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        //run the delete-all SQL with the revenue object classes
        int returnCount = getSimpleJdbcTemplate().update(deleteAllRevenueRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        return(BCConstants.benefitsResult.NO_BENEFITS);
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public BCConstants.benefitsResult deleteBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // run the delete-all SQL with the expenditure object classes
        int returnCount = getSimpleJdbcTemplate().update(deleteAllExpenditureRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        return(BCConstants.benefitsResult.NO_BENEFITS);
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public BCConstants.benefitsResult spreadBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
    
        // for revenue, we delete all existing rows, and spread all the corresponding rows in the general ledger
        // if there is any revenue for benefits, it will be spread, not calculated based on non-benefits rows as expenditure benefits will be
        int returnCount = getSimpleJdbcTemplate().update(deleteAllRevenueRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n RevenueSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        // run the create-monthly-budgets-from-GL SQL with the revenue object classes
        returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerRevenueByMonthSQL,documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber );
        LOG.warn(String.format("\n%s\n RevenueSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        return (BCConstants.benefitsResult.NO_BENEFITS);
    }

    @RawSQL
    public void distributeBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
    
        // for revenue, we delete all existing rows, and spread all the corresponding rows in the general ledger
        // if there is any revenue for benefits, it will be spread, not calculated based on non-benefits rows as expenditure benefits will be
        int returnCount = getSimpleJdbcTemplate().update(deleteAllRevenueRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n RevenueSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        // run the create-monthly-budgets-from-GL SQL with the revenue object classes
        String allocateGeneralLedgerRevenueByMonthSQL = buildAllocateGeneralLedgerRevenueByMonthSQL();
        returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerRevenueByMonthSQL,documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber );
        LOG.warn(String.format("\n%s\n RevenueSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }
    
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public BCConstants.benefitsResult spreadBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // run the delete-all-except-benefits SQL with the expenditure object classes
        int returnCount = getSimpleJdbcTemplate().update(deleteNoBenefitsExpenditureRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode);
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        // run the create-monthly-budgets-from-GL SQL with the expenditure object classes
        String allocateGeneralLedgerExpenditureByMonthSQL = buildAllocateGeneralLedgerExpenditureByMonthSQL();
        if (allocateGeneralLedgerExpenditureByMonthSQL.length() == 0)
        {
            return(BCConstants.benefitsResult.FAILED);
        }
        returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerExpenditureByMonthSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode);
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        return(BCConstants.benefitsResult.NO_BENEFITS);
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetContainsBenefitsExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public BCConstants.benefitsResult budgetConstructionMonthlyBudgetContainsBenefitsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        Long numberOfBenefitsEligibleRows = getSimpleJdbcTemplate().queryForLong(expenditureBenefitsObjectClassesCheckSQL,documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        return (((numberOfBenefitsEligibleRows == 0) ? BCConstants.benefitsResult.NO_BENEFITS : BCConstants.benefitsResult.BENEFITS));
    }

    private String buildAllocateGeneralLedgerExpenditureByMonthSQL()
    {
       String inSQLString = this.expenditureINList();
       if (inSQLString.length() == 0)
       {
           return inSQLString;
       }
       StringBuffer allocateGeneralLedgerExpenditureByMonth = new StringBuffer(5000);  
       allocateGeneralLedgerExpenditureByMonth.append("INSERT INTO LD_BCNSTR_MONTH_T\n"); 
       allocateGeneralLedgerExpenditureByMonth.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n");
       allocateGeneralLedgerExpenditureByMonth.append(" FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT,\n");
       allocateGeneralLedgerExpenditureByMonth.append(" FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT)\n");
       allocateGeneralLedgerExpenditureByMonth.append("(SELECT ?, ?, ?, ?, ?, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n"); 
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((acln_annl_bal_amt / 12), 0) + \n");
       allocateGeneralLedgerExpenditureByMonth.append("        (acln_annl_bal_amt - (ROUND((acln_annl_bal_amt / 12), 0) * 12)),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerExpenditureByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0)\n");
       allocateGeneralLedgerExpenditureByMonth.append("        FROM ld_pnd_bcnstr_gl_t\n"); 
       allocateGeneralLedgerExpenditureByMonth.append("        WHERE (fdoc_nbr = ?)\n");
       allocateGeneralLedgerExpenditureByMonth.append("          AND (univ_fiscal_yr = ?)\n");
       allocateGeneralLedgerExpenditureByMonth.append("          AND (fin_coa_cd = ?)\n");
       allocateGeneralLedgerExpenditureByMonth.append("          AND (account_nbr = ?)\n");
       allocateGeneralLedgerExpenditureByMonth.append("          AND (sub_acct_nbr = ?)\n");
       allocateGeneralLedgerExpenditureByMonth.append("AND (NOT EXISTS (SELECT 1\n");
       allocateGeneralLedgerExpenditureByMonth.append("                 FROM LD_BENEFITS_CALC_T\n");
       allocateGeneralLedgerExpenditureByMonth.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
       allocateGeneralLedgerExpenditureByMonth.append("                   AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
       allocateGeneralLedgerExpenditureByMonth.append("                  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))\n");
       allocateGeneralLedgerExpenditureByMonth.append("          AND (fin_obj_typ_cd IN ");
       allocateGeneralLedgerExpenditureByMonth.append(inSQLString);
       allocateGeneralLedgerExpenditureByMonth.append(")");
       return allocateGeneralLedgerExpenditureByMonth.toString();
       
    }
    

    private String buildAllocateGeneralLedgerRevenueByMonthSQL() throws NoSuchFieldException, IOException
    {
       String inSQLString = this.getRevenueINList();
       StringBuffer allocateGeneralLedgerRevenueByMonth = new StringBuffer(5000);  
       allocateGeneralLedgerRevenueByMonth.append("INSERT INTO LD_BCNSTR_MONTH_T\n"); 
       allocateGeneralLedgerRevenueByMonth.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n");
       allocateGeneralLedgerRevenueByMonth.append(" FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT,\n");
       allocateGeneralLedgerRevenueByMonth.append(" FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT)\n");
       allocateGeneralLedgerRevenueByMonth.append("(SELECT ?, ?, ?, ?, ?, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n"); 
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((acln_annl_bal_amt / 12), 0) + \n");
       allocateGeneralLedgerRevenueByMonth.append("        (acln_annl_bal_amt - (ROUND((acln_annl_bal_amt / 12), 0) * 12)),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
       allocateGeneralLedgerRevenueByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0)\n");
       allocateGeneralLedgerRevenueByMonth.append("        FROM ld_pnd_bcnstr_gl_t\n"); 
       allocateGeneralLedgerRevenueByMonth.append("        WHERE (fdoc_nbr = ?)\n");
       allocateGeneralLedgerRevenueByMonth.append("          AND (univ_fiscal_yr = ?)\n");
       allocateGeneralLedgerRevenueByMonth.append("          AND (fin_coa_cd = ?)\n");
       allocateGeneralLedgerRevenueByMonth.append("          AND (account_nbr = ?)\n");
       allocateGeneralLedgerRevenueByMonth.append("          AND (sub_acct_nbr = ?)\n");
       allocateGeneralLedgerRevenueByMonth.append("AND (NOT EXISTS (SELECT 1\n");
       allocateGeneralLedgerRevenueByMonth.append("                 FROM LD_BENEFITS_CALC_T\n");
       allocateGeneralLedgerRevenueByMonth.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
       allocateGeneralLedgerRevenueByMonth.append("                   AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
       allocateGeneralLedgerRevenueByMonth.append("                  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))\n");
       allocateGeneralLedgerRevenueByMonth.append("          AND (fin_obj_typ_cd IN ");
       allocateGeneralLedgerRevenueByMonth.append(inSQLString);
       allocateGeneralLedgerRevenueByMonth.append(")");
       return allocateGeneralLedgerRevenueByMonth.toString();
       
    }

    
}
