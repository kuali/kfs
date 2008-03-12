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
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao;
import org.kuali.module.budget.dao.ojb.GenesisDaoOjb;
import org.kuali.module.budget.BCParameterConstants;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.KFSConstants;

import org.kuali.module.budget.document.BudgetConstructionDocument;


public class BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionMonthlyBudgetsCreateDeleteDao {
    
    private ParameterService parameterService;

    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc.class);

    // list of object types allowed in budget construction
    // these come from the parameter table
    private ArrayList<Object> inRevenueObjectTypes;    
    private ArrayList<Object> inExpenditureObjectTypes;
    // SQL built on the first call to any method, based the StringBuilders created in the constructor
    private String deleteAllRevenueRowsSQL;
    private String deleteAllExpenditureRowsSQL;
    private String deleteNoBenefitsRevenueRowsSQL;
    private String deleteNoBenefitsExpenditureRowsSQL;
    private String expenditureBenefitsObjectClassesCheckSQL; 
    private String allocateGeneralLedgerExpenditureByMonthSQL;
    private String allocateGeneralLedgerRevenueByMonthSQL;
    
    @RawSQL
    public BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc(ParameterService parameterService)
    {
        /**
         *  we try to set up the SQL in the constructor.  We will make a DB call to find out how many expenditure object types and how
         *  many income object types are eligible for budget construction.  this could result in a DB exception, but doing it here makes
         *  the SQL more readable.
         */
        // set the parameter service
        this.parameterService = parameterService;

        // fetch the parameters (expenditure object types allowed in budget construction, revenue object types allowed in budget construction)
        // get the values of the object type parameters for expenditure and revenue allowed in budget construction.
        // (the implementation of ParameterService returns an ArrayList<String>, but the return type is List<String>.  We need to pass this to an SQL
        //  statement as part of a longer list of parameters, which are passed as Object... .   In order to do an addAll to a parameter ArrayList that
        //  will be converted to the Object[] passed to SQL, we need to have the parameters in an ArrayList.  A cast will not work, because List is
        //  a superclass of ArrayList, not a subclass.)
        inRevenueObjectTypes     = new ArrayList<Object>(parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterConstants.REVENUE_OBJECT_TYPES));
        inExpenditureObjectTypes = new ArrayList<Object>(parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterConstants.EXPENDITURE_OBJECT_TYPES));
        
        // build a string with placeholders for the revenue SQL IN list
        String revenueInString = inString(inRevenueObjectTypes.size());
        // build a string with placeholders for the revenue SQL IN list
        String expenditureInString = inString(inExpenditureObjectTypes.size());
        // string builders used to generate the SQL.  these are initialized as far as possible in the constructor
        StringBuilder deleteBuilderExpenditure                = new StringBuilder(800);
        StringBuilder deleteBuilderRevenue                    = new StringBuilder(800);  
        StringBuilder benefitsObjectClassesMonthly            = new StringBuilder(300);
        StringBuilder benefitsObjectClassesGeneralLedger      = new StringBuilder(300);
        StringBuilder expenditureBenefitsObjectClassesCheck   = new StringBuilder(1500);
        StringBuilder allocateGeneralLedgerRevenueByMonth     = new StringBuilder(5000);
        StringBuilder allocateGeneralLedgerExpenditureByMonth = new StringBuilder(5000);
        
        // this is the common part of the way we test in the WHERE clauses for a benefits object class (LD_BCNSTR_MONTH_T/LD_PND_BCNSTR_GL_T)
        benefitsObjectClassesMonthly.append("AND (NOT EXISTS (SELECT 1\n");
        benefitsObjectClassesMonthly.append("                 FROM LD_BENEFITS_CALC_T\n");
        benefitsObjectClassesMonthly.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
        benefitsObjectClassesMonthly.append("                   AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
        // now add the table-specific part to each pre-SQL string builder
        // the monthly clause is needed for the deletes
        // the general ledger claues is need for the inserts
        benefitsObjectClassesGeneralLedger.append(benefitsObjectClassesMonthly);
        benefitsObjectClassesMonthly.append("                    AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_BCNSTR_MONTH_T.FIN_OBJECT_CD)))\n");
        benefitsObjectClassesGeneralLedger.append("                  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))\n");

        // this part of the DELETE SQL is always the same 
        deleteBuilderRevenue.append("DELETE FROM ld_bcnstr_month_t\n"); 
        deleteBuilderRevenue.append("WHERE (fdoc_nbr = ?)\n"); 
        deleteBuilderRevenue.append("  AND (univ_fiscal_yr = ?)\n"); 
        deleteBuilderRevenue.append("  AND (fin_coa_cd = ?)\n"); 
        deleteBuilderRevenue.append("  AND (account_nbr = ?)\n"); 
        deleteBuilderRevenue.append("  AND (sub_acct_nbr = ?)\n"); 
        deleteBuilderRevenue.append("  AND (fin_obj_typ_cd IN ");
        // add to it to build the SQL to delete all revenue, or all expenditure, for a key
        deleteBuilderExpenditure.append(deleteBuilderRevenue);
        // add the IN clause for revenue to delete all revenue
        deleteBuilderRevenue.append(revenueInString);
        deleteBuilderRevenue.append(")\n");
        deleteAllRevenueRowsSQL = deleteBuilderRevenue.toString();
        // add the IN clause for expenditure to delete all expenditure
        deleteBuilderExpenditure.append(expenditureInString);
        deleteBuilderExpenditure.append(")\n");
        deleteAllExpenditureRowsSQL= deleteBuilderExpenditure.toString();
        // add to delete all revenue to get the delete SQL that deletes revenue that does not have a fringe benefit object class
        deleteBuilderRevenue.append(benefitsObjectClassesMonthly);
        deleteNoBenefitsRevenueRowsSQL = deleteBuilderRevenue.toString();
        // add to delete all expenditure to get the deleteSQL that deletes expenditure that does not have a fringe benefit object class
        deleteBuilderExpenditure.append(benefitsObjectClassesMonthly);
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
        expenditureBenefitsObjectClassesCheck.append(expenditureInString);
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
        
        allocateGeneralLedgerExpenditureByMonth.append(allocateGeneralLedgerRevenueByMonth);
        
        // SQL to allocate revenue by month
        allocateGeneralLedgerRevenueByMonth.append(revenueInString);
        allocateGeneralLedgerRevenueByMonth.append("))");
        allocateGeneralLedgerRevenueByMonthSQL = allocateGeneralLedgerRevenueByMonth.toString();
        // SQL to allocate expenditure by month
        allocateGeneralLedgerExpenditureByMonth.append(expenditureInString);
        allocateGeneralLedgerExpenditureByMonth.append("))");
        allocateGeneralLedgerExpenditureByMonthSQL = allocateGeneralLedgerExpenditureByMonth.toString();
    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void deleteBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        //run the delete-all SQL with the revenue object classes
        ArrayList<Object> sqlParameters = new ArrayList<Object>(Arrays.asList(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber));
        sqlParameters.addAll(inRevenueObjectTypes);
        int returnCount = getSimpleJdbcTemplate().update(deleteAllRevenueRowsSQL, sqlParameters.toArray());
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void deleteBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // run the delete-all SQL with the expenditure object classes
        ArrayList<Object> sqlParameters = new ArrayList<Object>(Arrays.asList(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber));
        sqlParameters.addAll(inExpenditureObjectTypes);
        int returnCount = getSimpleJdbcTemplate().update(deleteAllExpenditureRowsSQL, sqlParameters.toArray());
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void spreadBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // run the delete-all-except-benefits with the revenue object classes
        ArrayList<Object> deleteParameters = new ArrayList<Object>(Arrays.asList(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber));
        deleteParameters.addAll(inRevenueObjectTypes);
        deleteParameters.addAll(new ArrayList<Object>(Arrays.asList(fiscalYear, chartCode)));
        int returnCount = getSimpleJdbcTemplate().update(deleteNoBenefitsRevenueRowsSQL, deleteParameters.toArray());
        LOG.warn(String.format("\n%s\n RevenueSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        // run the create-monthly-budgets-from-GL SQL with the revenue object classes
        ArrayList<Object> insertParameters = new ArrayList<Object>(Arrays.asList(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode));
        insertParameters.addAll(inRevenueObjectTypes);
        returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerRevenueByMonthSQL, insertParameters.toArray());
        LOG.warn(String.format("\n%s\n RevenueSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void spreadBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // run the delete-all-except-benefits SQL with the expenditure object classes
        ArrayList<Object> deleteParameters = new ArrayList<Object>(Arrays.asList(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber));
        deleteParameters.addAll(inExpenditureObjectTypes);
        deleteParameters.addAll(new ArrayList<Object>(Arrays.asList(fiscalYear, chartCode)));
        int returnCount = getSimpleJdbcTemplate().update(deleteNoBenefitsExpenditureRowsSQL, deleteParameters.toArray());
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        // run the create-monthly-budgets-from-GL SQL with the expenditure object classes
        ArrayList<Object> insertParameters = new ArrayList<Object>(Arrays.asList(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode));
        insertParameters.addAll(inExpenditureObjectTypes);
        returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerExpenditureByMonthSQL, insertParameters.toArray());
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetContainsBenefitsExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public boolean budgetConstructionMonthlyBudgetContainsBenefitsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // build the parameter list
        ArrayList<Object> sqlParameters = new ArrayList<Object>(Arrays.asList(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber));
        sqlParameters.addAll(inExpenditureObjectTypes);     
        Long numberOfBenefitsEligibleRows = getSimpleJdbcTemplate().queryForLong(expenditureBenefitsObjectClassesCheckSQL,sqlParameters.toArray());
        return (numberOfBenefitsEligibleRows != 0);
    }

}
