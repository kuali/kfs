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
         * @@TODO remove this constructor once a decision has been made not to use it to build most of the SQL once.
         *  we try to set up the SQL in the constructor. 
         */
        
        // string builders used to generate the SQL.  these are initialized as far as possible in the constructor
        StringBuilder deleteBuilderRevenue                    = new StringBuilder(800);  

        //  
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
    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void deleteBudgetConstructionMonthlyBudgetsRevenueI(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {

        // SQL to delete all monthly revenue rows for this document
        deleteAllRevenueRowsSQL = this.buildDeleteBudgetConstructionMonthlyBudgetsRevenueSQLI();
        
        //run the delete-all SQL with the revenue object classes
        int returnCount = getSimpleJdbcTemplate().update(deleteAllRevenueRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void deleteBudgetConstructionMonthlyBudgetsExpenditureI(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber)throws IOException, NoSuchFieldException {

        // SQL to delete all monthly expenditure rows for this document
        deleteAllExpenditureRowsSQL = buildDeleteBudgetConstructionMonthlyBudgetsExpenditureSQLI();
        
        // run the delete-all SQL with the expenditure object classes
        int returnCount = getSimpleJdbcTemplate().update(deleteAllExpenditureRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void spreadBudgetConstructionMonthlyBudgetsRevenueI(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
    
        // for revenue, we delete all existing rows, and spread all the corresponding rows in the general ledger
        // if there is any revenue for benefits, it will be spread, not calculated based on non-benefits rows as expenditure benefits will be

        ArrayList<String> sqlStrings = this.buildAllocateGeneralLedgerRevenueByMonthSQLI(); 
        
        // delete what is there now for this document for the revenue object classes
        String deleteAllRevenueRowsSQL = sqlStrings.get(0);
        int returnCount = getSimpleJdbcTemplate().update(deleteAllRevenueRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n RevenueSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        // run the create-monthly-budgets-from-GL SQL with the revenue object classes
        String allocateGeneralLedgerRevenueByMonthSQL = sqlStrings.get(0);
        returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerRevenueByMonthSQL,documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber );
        LOG.warn(String.format("\n%s\n RevenueSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
    }
    
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public boolean spreadBudgetConstructionMonthlyBudgetsExpenditureI(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {

        ArrayList<String> allocateGeneralLedgerExpenditureByMonthSQL = buildAllocateGeneralLedgerExpenditureByMonthSQLI();
        
        // run the delete-all-except-benefits SQL with the expenditure object classes
        int returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerExpenditureByMonthSQL.get(0), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode);
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        // run the create-monthly-budgets-from-GL SQL with the expenditure object classes
        returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerExpenditureByMonthSQL.get(1), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode);
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        return(budgetConstructionMonthlyBudgetContainsBenefitsExpenditure(allocateGeneralLedgerExpenditureByMonthSQL.get(2), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber));
    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public BCConstants.benefitsResult deleteBudgetConstructionMonthlyBudgetsRevenueII(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        //run the delete-all SQL with the revenue object classes
        String deleteAllRevenueRowsSQL = buildDeleteBudgetConstructionMonthlyBudgetsRevenueSQLII();
        if (deleteAllRevenueRowsSQL.length() == 0)
        {
            return(BCConstants.benefitsResult.FAILED);
        }
        
        int returnCount = getSimpleJdbcTemplate().update(deleteAllRevenueRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        return(BCConstants.benefitsResult.NO_BENEFITS);
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public BCConstants.benefitsResult deleteBudgetConstructionMonthlyBudgetsExpenditureII(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // run the delete-all SQL with the expenditure object classes
        String deleteAllExpenditureRowsSQL = buildDeleteBudgetConstructionMonthlyBudgetsExpenditureSQLII();
        if (deleteAllExpenditureRowsSQL.length() == 0)
        {
            return(BCConstants.benefitsResult.FAILED);
        }
        
        int returnCount = getSimpleJdbcTemplate().update(deleteAllExpenditureRowsSQL, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n Expenditure (all) rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        return(BCConstants.benefitsResult.NO_BENEFITS);
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public BCConstants.benefitsResult spreadBudgetConstructionMonthlyBudgetsRevenueII(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
    
        ArrayList<String> allocateGeneralLedgerRevenueByMonthSQL = buildAllocateGeneralLedgerRevenueByMonthSQLII();
        if (allocateGeneralLedgerRevenueByMonthSQL.isEmpty())
        {
            return (BCConstants.benefitsResult.FAILED);
        }
        
        // for revenue, we delete all existing rows, and spread all the corresponding rows in the general ledger
        // if there is any revenue for benefits, it will be spread, not calculated based on non-benefits rows as expenditure benefits will be
        int returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerRevenueByMonthSQL.get(0), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        LOG.warn(String.format("\n%s\n RevenueSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        // run the create-monthly-budgets-from-GL SQL with the revenue object classes
        returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerRevenueByMonthSQL.get(1),documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber );
        LOG.warn(String.format("\n%s\n RevenueSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));

        return (BCConstants.benefitsResult.NO_BENEFITS);
    }
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public BCConstants.benefitsResult spreadBudgetConstructionMonthlyBudgetsExpenditureII(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        ArrayList<String> allocateGeneralLedgerExpenditureByMonthSQL = buildAllocateGeneralLedgerExpenditureByMonthSQLII();
        if (allocateGeneralLedgerExpenditureByMonthSQL.isEmpty())
        {
            return(BCConstants.benefitsResult.FAILED);
        }

        // run the delete-all-except-benefits SQL with the expenditure object classes
        int returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerExpenditureByMonthSQL.get(0), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode);
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows deleted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        
        // run the create-monthly-budgets-from-GL SQL with the expenditure object classes
        returnCount = getSimpleJdbcTemplate().update(allocateGeneralLedgerExpenditureByMonthSQL.get(1), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode);
        LOG.warn(String.format("\n%s\n ExpenditureSpread rows inserted for (%s,%d,%s,%s,%s) = %d",getDbPlatform().toString(),documentNumber, fiscalYear,chartCode,accountNumber, subAccountNumber, returnCount));
        
        // benefits need to be re-calculated (BENEFITS) if the third SQL query returns true
        return((budgetConstructionMonthlyBudgetContainsBenefitsExpenditure(allocateGeneralLedgerExpenditureByMonthSQL.get(2), documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber) ? BCConstants.benefitsResult.BENEFITS : BCConstants.benefitsResult.NO_BENEFITS));
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
    
    /**
     * 
     * build SQL string to evenly spread general ledger revenue into tweleve monthly periods
     * @return the SQL as a string
     * @throws NoSuchFieldException
     * @throws IOException
     */
    @RawSQL
    private ArrayList<String> buildAllocateGeneralLedgerRevenueByMonthSQLI() throws NoSuchFieldException, IOException
    {
       String inSQLString = this.getRevenueINList();
       
       // to avoid calling the DB twice to get the system parameter, we return an array list of SQL strings.
       ArrayList<String> returnSQL = new ArrayList();
       
       // here is the SQL to delete the revenue from the monthly table for this document
       returnSQL.add(buildDeleteBudgetConstructionMonthlyBudgets(inSQLString));
       
       // here is the SQL to re-distribute evenly over 12 months to the monthly table the revenue for this document in the general ledger
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
       allocateGeneralLedgerRevenueByMonth.append("          AND (fin_obj_typ_cd IN ");
       allocateGeneralLedgerRevenueByMonth.append(inSQLString);
       allocateGeneralLedgerRevenueByMonth.append(")");

       returnSQL.add(allocateGeneralLedgerRevenueByMonth.toString());
       
       return(returnSQL);
    }

    /**
     * 
     * build a SQL String to spread general ledger expenditures evenly over 12 monthly periods, and another to count benefits expenditure object classes in the document
     * @return an array list containing the first SQL string and the second
     * @throws NoSuchFieldException
     * @throws IOException
     */
    @RawSQL
    private ArrayList<String> buildAllocateGeneralLedgerExpenditureByMonthSQLI() throws NoSuchFieldException, IOException
    {  
       // before we allocate general ledger expenditure by month, we delete all existing monthly rows for this document which do not have a benefits object code. 
       // once we allocate general ledger expenditure by month, we return a count of the rows for this document that contain benefits object codes.
       // this is a signal that benefits need to be recalculated for this document.
       // to avoid getting the expenditure parameter twice from the DB, we return both sets of SQL in an ArrayList
       ArrayList<String> returnSQL = new ArrayList<String>();
       
       String inSQLString = this.getExpenditureINList();
       
       // we have to delete all current monthly rows for this document which do not have benefits object types
       returnSQL.add(this.buildDeleteNoBenefitsBudgetConstructionMonthlyBudgetsExpenditureSQL(inSQLString));
       
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
       
       returnSQL.add(allocateGeneralLedgerExpenditureByMonth.toString());
       
       StringBuffer expenditureBenefitsObjectClassesCheck = new StringBuffer(2000);
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
       expenditureBenefitsObjectClassesCheck.append(inSQLString);
       expenditureBenefitsObjectClassesCheck.append(")\n");

       returnSQL.add(expenditureBenefitsObjectClassesCheck.toString());
       
       
       return returnSQL;
    }
    
    /**
     * 
     * build SQL to delete all the monthly expenditure for this document
     * @return the SQL as a string
     * @throws IOException
     * @throws NoSuchFieldException
     */
    private String buildDeleteBudgetConstructionMonthlyBudgetsExpenditureSQLI() throws IOException, NoSuchFieldException
    {
       String inSQLString = this.getExpenditureINList();
       return (buildDeleteBudgetConstructionMonthlyBudgets(inSQLString));
    }
    
    /**
     * 
     * build SQL to delete all the monthly revenue for this document
     * @return the SQL as a string
     * @throws IOException
     * @throws NoSuchFieldException
     */
    private String buildDeleteBudgetConstructionMonthlyBudgetsRevenueSQLI() throws IOException, NoSuchFieldException
    {
        String inSQLString = this.getRevenueINList();
        return (buildDeleteBudgetConstructionMonthlyBudgets(inSQLString));
    }

    /**
     * 
     * build SQL string to evenly spread general ledger revenue into tweleve monthly periods
     * @return the SQL as a string
     * @throws NoSuchFieldException
     * @throws IOException
     */
    @RawSQL
    private ArrayList<String> buildAllocateGeneralLedgerRevenueByMonthSQLII() 
    {
       // to avoid calling the DB twice to get the system parameter, we return an array list of SQL strings.
       ArrayList<String> returnSQL = new ArrayList();

       String inSQLString = this.revenueINList();
       if (inSQLString.length() == 0)
       {
           return returnSQL;
       }
       
       // here is the SQL to delete the revenue from the monthly table for this document
       returnSQL.add(buildDeleteBudgetConstructionMonthlyBudgets(inSQLString));
       
       // here is the SQL to re-distribute evenly over 12 months to the monthly table the revenue for this document in the general ledger
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
       allocateGeneralLedgerRevenueByMonth.append("          AND (fin_obj_typ_cd IN ");
       allocateGeneralLedgerRevenueByMonth.append(inSQLString);
       allocateGeneralLedgerRevenueByMonth.append(")");
       returnSQL.add(allocateGeneralLedgerRevenueByMonth.toString());
       
       return returnSQL;
       
    }

    /**
     * 
     * build a SQL String to spread general ledger expenditures evenly over 12 monthly periods, and another to count benefits expenditure object classes in the document
     * @return an array list containing the first SQL string and the second
     * @throws NoSuchFieldException
     * @throws IOException
     */
    @RawSQL
    private ArrayList<String> buildAllocateGeneralLedgerExpenditureByMonthSQLII()
    {
       // once we allocate general ledger expenditure by month, we return a count of the rows for this document that contain benefits object codes.
       // this is a signal that benefits need to be recalculated for this document.
       // to avoid getting the expenditure parameter twice from the DB, we return both sets of SQL in an ArrayList
       ArrayList<String> returnSQL = new ArrayList<String>(); 
       String inSQLString = this.expenditureINList();
       if (inSQLString.length() == 0)
       {
           return returnSQL;
       }
       
       // we have to delete all current monthly rows for this document which do not have benefits object types
       returnSQL.add(this.buildDeleteNoBenefitsBudgetConstructionMonthlyBudgetsExpenditureSQL(inSQLString));
       
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
       returnSQL.add(allocateGeneralLedgerExpenditureByMonth.toString());
       
       StringBuffer expenditureBenefitsObjectClassesCheck = new StringBuffer(2000);
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
       expenditureBenefitsObjectClassesCheck.append(inSQLString);
       expenditureBenefitsObjectClassesCheck.append(")\n");
       returnSQL.add(expenditureBenefitsObjectClassesCheck.toString());
       
       // we have to delete all current monthly rows for this document which do not have benefits object types
       returnSQL.add(this.buildDeleteNoBenefitsBudgetConstructionMonthlyBudgetsExpenditureSQL(inSQLString));
       //
       return(returnSQL);
    }
    
    /**
     * 
     * build SQL to delete all the monthly expenditure for this document
     * @return the SQL as a string
     * @throws IOException
     * @throws NoSuchFieldException
     */
    private String buildDeleteBudgetConstructionMonthlyBudgetsExpenditureSQLII()
    {
        String inSQLString = this.expenditureINList();
        if (inSQLString.length() == 0)
        {
            return inSQLString;
        }
        return (buildDeleteBudgetConstructionMonthlyBudgets(inSQLString));
    }
    
    /**
     * 
     * build SQL to delete all the monthly expenditure for this document
     * @return the SQL as a string
     * @throws IOException
     * @throws NoSuchFieldException
     */
    private String buildDeleteBudgetConstructionMonthlyBudgetsRevenueSQLII()
    {
        String inSQLString = this.revenueINList();
        if (inSQLString.length() == 0)
        {
            return inSQLString;
        }
        return (buildDeleteBudgetConstructionMonthlyBudgets(inSQLString));
    }
    
    /**
     * 
     * build SQL to delete monthly expenditures for a document which do not have benefits object classes.
     * (use this method inside a SQL builder which returns an array list and makes a single DB call to get the system parameter) 
     * @param an SQL IN clause containing the expenditure object types
     * @return the SQL as a string
     * @throws IOException
     * @throws NoSuchFieldException
     */
    private String buildDeleteNoBenefitsBudgetConstructionMonthlyBudgetsExpenditureSQL(String inString)
    {
        StringBuffer deleteBuilder = new StringBuffer(2000);
        deleteBuilder.append("DELETE FROM ld_bcnstr_month_t\n"); 
        deleteBuilder.append("WHERE (fdoc_nbr = ?)\n"); 
        deleteBuilder.append("  AND (univ_fiscal_yr = ?)\n"); 
        deleteBuilder.append("  AND (fin_coa_cd = ?)\n"); 
        deleteBuilder.append("  AND (account_nbr = ?)\n"); 
        deleteBuilder.append("  AND (sub_acct_nbr = ?)\n"); 
        deleteBuilder.append("  AND (fin_obj_typ_cd IN ");
        deleteBuilder.append(inString);
        deleteBuilder.append(")\n");
        deleteBuilder.append("AND (NOT EXISTS (SELECT 1\n");
        deleteBuilder.append("                 FROM LD_BENEFITS_CALC_T\n");
        deleteBuilder.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
        deleteBuilder.append("                   AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
        deleteBuilder.append("                    AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_BCNSTR_MONTH_T.FIN_OBJECT_CD)))\n");

        return(deleteBuilder.toString());
    }
    
    /**
     * 
     * build SQL to delete all monthly budget rows for a document which have an object type specified in the IN list
     * @param inString contains the IN list
     * @return the SQL String
     */
    
    private String buildDeleteBudgetConstructionMonthlyBudgets(String inString)
    {
        StringBuffer deleteBuilder = new StringBuffer(2000);
        deleteBuilder.append("DELETE FROM ld_bcnstr_month_t\n"); 
        deleteBuilder.append("WHERE (fdoc_nbr = ?)\n"); 
        deleteBuilder.append("  AND (univ_fiscal_yr = ?)\n"); 
        deleteBuilder.append("  AND (fin_coa_cd = ?)\n"); 
        deleteBuilder.append("  AND (account_nbr = ?)\n"); 
        deleteBuilder.append("  AND (sub_acct_nbr = ?)\n"); 
        deleteBuilder.append("  AND (fin_obj_typ_cd IN ");
        deleteBuilder.append(inString);
        deleteBuilder.append(")");
        
        return(deleteBuilder.toString());
    }
  
}
