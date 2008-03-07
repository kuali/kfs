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
    private Object[] inRevenueObjectTypes                        = null;
    private Object[] inExpenditureObjectTypes                    = null;
    // string builders used to generate the SQL.  these are initialized as far as possible in the constructor
    private StringBuilder deleteBuilder                          = new StringBuilder(800);
    private StringBuilder benefitsObjectClassesMonthly           = new StringBuilder(300);
    private StringBuilder benefitsObjectClassesGeneralLedger     = new StringBuilder(300);
    private StringBuilder expenditureBenefitsObjectClassesCheck  = new StringBuilder(1500);
    private StringBuilder allocateGeneralLedgerByMonth           = new StringBuilder(5000);
    // SQL built on the first call to any method, based the StringBuilders created in the constructor
    private String deleteAllRevenueRowsSQL;
    private String deleteAllExpenditureRowsSQL;
    private String deleteNoBenefitsRevenueRowsSQL;
    private String deleteNoBenefitsExpenditureRowsSQL;
    private String expenditureBenefitsObjectClassesCheckSQL; 
    private String allocateGeneralLedgerExpenditureByMonthSQL;
    private String allocateGeneralLedgerRevenueByMonthSQL;
    
    @RawSQL
    public BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc()
    {
        /**
         * the constructor for BudgetConstructionMonthlyBudgetsCreateDeleteDaoJdbc sets up all the static portions of the SQL used
         * in the class once, when the Spring Bean is created.
         * (the SQL has IN subclauses that vary in structure depending on parameters in the DB, and so cannot be initialized in 
         *  the constructor.)
         */
        // this part of the DELETE SQL is always the same 
        deleteBuilder.append("DELETE FROM ld_bcnstr_month_t\n"); 
        deleteBuilder.append("WHERE (fdoc_nbr = ?)\n"); 
        deleteBuilder.append("  AND (univ_fiscal_yr = ?)\n"); 
        deleteBuilder.append("  AND (fin_coa_cd = ?)\n"); 
        deleteBuilder.append("  AND (account_nbr = ?)\n"); 
        deleteBuilder.append("  AND (sub_acct_nbr = ?)\n"); 
        deleteBuilder.append("  AND (fin_obj_typ_cd IN )");
        
        // this is the common part of the way we test for a benefits object class (LD_BCNSTR_MONTH_T/LD_PND_BCNSTR_GL_T)
        benefitsObjectClassesMonthly.append("AND (NOT EXISTS (SELECT 1\n");
        benefitsObjectClassesMonthly.append("                 FROM LD_BENEFITS_CALC_T\n");
        benefitsObjectClassesMonthly.append("                 WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = ?)\n");
        benefitsObjectClassesMonthly.append("                 AND (LD_BENEFITS_CALC_T.FIN_COA_CD = ?)\n");
        // now add the table-specific part to each pre-SQL string builder
        benefitsObjectClassesGeneralLedger.append(benefitsObjectClassesMonthly);
        benefitsObjectClassesMonthly.append("                 AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_BCNSTR_MONTH_T.FIN_OBJECT_CD)))\n");
        benefitsObjectClassesGeneralLedger.append("                  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))\n");
        
        // we do a COUNT(*) to decide whether any benefits expense objects occur in LD_BCNSTR_MONTH_T
        expenditureBenefitsObjectClassesCheck.append("SELECT COUNT(*)\n");
        expenditureBenefitsObjectClassesCheck.append("FROM (LD_BCNSTR_MONTH_T INNER JOIN LD_LBR_OBJ_BENE_T)\n");
        expenditureBenefitsObjectClassesCheck.append("  ON ((LD_BCNSTR_MONTH_T.UNIV_FISCAL_YR = LD_LBR_OBJ_BENE_T.UNIV_FISCAL_YR) AND\n");
        expenditureBenefitsObjectClassesCheck.append("      (LD_BCNSTR_MONTH_T.FIN_COA_CD = LD_LBR_OBJ_BENE_T.FIN_COA_CD) AND\n");
        expenditureBenefitsObjectClassesCheck.append("      (LD_BCNSTR_MONTH_T.FIN_OBJECT_CD = LD_LBR_OBJ_BENE_T.FIN_OBJECT_CD))\n");
        expenditureBenefitsObjectClassesCheck.append("WHERE (LD_BCNSTR_MONTH_T.FDOC_NBR = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.UNIV_FISCAL_YR = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.FIN_COA_CD = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.ACCOUNT_NBR = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.SUB_ACCT_NBR = ?)\n");
        expenditureBenefitsObjectClassesCheck.append("  AND (LD_BCNSTR_MONTH_T.FIN_OBJ_TYP_CD IN )");
        
        // build common part of the SQL that spreads the pending general ledger equally among 12 months.  each month is allocated
        // the total amount / 12, rounded to whole dollars.  the error is added to the first month, so summing all months gives the total
        allocateGeneralLedgerByMonth.append("INSERT INTO LD_BCNSTR_MONTH_T\n"); 
        allocateGeneralLedgerByMonth.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n");
        allocateGeneralLedgerByMonth.append(" FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT,\n");
        allocateGeneralLedgerByMonth.append(" FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT)\n");
        allocateGeneralLedgerByMonth.append("(SELECT ?, ?, ?, ?, ?, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD,\n"); 
        allocateGeneralLedgerByMonth.append("        ROUND((acln_annl_bal_amt / 12), 0) + \n");
        allocateGeneralLedgerByMonth.append("        (acln_annl_bal_amt - (ROUND((acln_annl_bal_amt / 12), 0) * 12)),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0),\n");
        allocateGeneralLedgerByMonth.append("        ROUND((ld_pnd_bcnstr_gl_t.acln_annl_bal_amt / 12), 0)\n");
        allocateGeneralLedgerByMonth.append("        FROM ld_pnd_bcnstr_gl_t\n"); 
        allocateGeneralLedgerByMonth.append("        WHERE (fdoc_nbr = ?)\n");
        allocateGeneralLedgerByMonth.append("          AND (univ_fiscal_yr = ?)\n");
        allocateGeneralLedgerByMonth.append("          AND (fin_coa_cd = ?)\n");
        allocateGeneralLedgerByMonth.append("          AND (account_nbr = ?)\n");
        allocateGeneralLedgerByMonth.append("          AND (sub_acct_nbr = ?)\n");
        allocateGeneralLedgerByMonth.append(benefitsObjectClassesGeneralLedger);
        allocateGeneralLedgerByMonth.append("          AND (fin_obj_typ_cd IN ))");

    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void BudgetConstructionMonthlyBudgetsDeleteRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // check to see whether we need to build the SQL from the static values initialized in the constructor
        buildSqlIfNecessary();
        //run the delete-all SQL with the revenue object classes
        getSimpleJdbcTemplate().update(deleteAllRevenueRowsSQL, sqlParameters(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, inRevenueObjectTypes));
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsDeleteExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void BudgetConstructionMonthlyBudgetsDeleteExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // check to see whether we need to build the SQL from the static values initialized in the constructor
        buildSqlIfNecessary();
        // run the delete-all SQL with the expenditure object classes
        getSimpleJdbcTemplate().update(deleteAllExpenditureRowsSQL, sqlParameters(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, inExpenditureObjectTypes));

    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void BudgetConstructionMonthlyBudgetsSpreadRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // check to see whether we need to build the SQL from the static values initialized in the constructor
        buildSqlIfNecessary();
        // run the delete-all-except-benefits with the revenue object classes
        getSimpleJdbcTemplate().update(deleteNoBenefitsRevenueRowsSQL, sqlParameters(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, inRevenueObjectTypes, fiscalYear, chartCode));
        // run the create-monthly-budgets-from-GL SQL with the revenue object classes
        getSimpleJdbcTemplate().update(allocateGeneralLedgerRevenueByMonthSQL, sqlParameters(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode, inRevenueObjectTypes));
        
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetsSpreadExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void BudgetConstructionMonthlyBudgetsSpreadExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // check to see whether we need to build the SQL from the static values initialized in the constructor
        buildSqlIfNecessary();
        // run the delete-all-except-benefits SQL with the expenditure object classes
        getSimpleJdbcTemplate().update(deleteNoBenefitsExpenditureRowsSQL, sqlParameters(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, inExpenditureObjectTypes, fiscalYear, chartCode));
        // run the create-monthly-budgets-from-GL SQL with the expenditure object classes
        getSimpleJdbcTemplate().update(allocateGeneralLedgerExpenditureByMonthSQL, sqlParameters(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, fiscalYear, chartCode, inExpenditureObjectTypes));
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao#BudgetConstructionMonthlyBudgetContainsBenefitsExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public boolean BudgetConstructionMonthlyBudgetContainsBenefitsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {

        // check to see whether we need to build the SQL from the static values initialized in the constructor
        buildSqlIfNecessary();
        // build the parameter list
        Long numberOfBenefitsEligibleRows = getSimpleJdbcTemplate().queryForLong(expenditureBenefitsObjectClassesCheckSQL,sqlParameters(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, inExpenditureObjectTypes));
        return (numberOfBenefitsEligibleRows != 0);
    }
    /**
     * 
     * check to see whether the variables have been initialized from the parameterService.  if not, initialize them here on the first call.
     * by default, Spring beans are singletons, so the IN list should only be built once, and the SQL strings should only be built once.
     * this also takes care of the fact that StringBuilder, which we use to create the SQL strings,  is not thread-safe
     */
    @RawSQL
    private synchronized void buildSqlIfNecessary()
    {
        // because this is synchronized, if two people working on different accounts try to 
        // access it at once, only one will build the SQL.  The other will see the SQL strings
        // that have already been built.
        if (inRevenueObjectTypes != null)
        {
            return;
        }

        //@@TODO: print for testing
        LOG.warn("\n buildSqlInListIfNecessary is generating the final SQL strings:\n");
        
        StringBuilder inClauseBuilderRevenue     = new StringBuilder(200);
        StringBuilder inClauseBuilderExpenditure = new StringBuilder(200);
        
        // get the values of the object type parameters for expenditure and revenue allowed in budget construction
        inRevenueObjectTypes = (Object[]) parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterConstants.REVENUE_OBJECT_TYPES).toArray();
        inExpenditureObjectTypes = (Object []) parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterConstants.EXPENDITURE_OBJECT_TYPES).toArray();

        // build the IN clauses (the number of object types could differ between revenue and expenditure
        inClauseBuilderRevenue.append(inString(inRevenueObjectTypes.length));
        inClauseBuilderExpenditure.append(inString(inExpenditureObjectTypes.length));

        // build the SQL we need for the revenue deletes
        // (1) delete all monthly revenue rows (including those with benefits object codes)
        StringBuilder deleteQueryCommon = new StringBuilder(1000);
        deleteQueryCommon.append(deleteBuilder);
        //     IN subclause is inserted before the last character
        deleteQueryCommon.insert(deleteQueryCommon.length()-1,inClauseBuilderRevenue);
        deleteAllRevenueRowsSQL = deleteQueryCommon.toString();
        //  (2) delete only monthly revenue rows that do not have benefit object codes
        deleteQueryCommon.append("\n");
        deleteQueryCommon.append(benefitsObjectClassesMonthly);
        deleteNoBenefitsRevenueRowsSQL = deleteQueryCommon.toString();

        // build the SQL we need for the expenditure deletes
        // (1) delete all monthly revenue rows (including those with benefits object codes)
        deleteQueryCommon.delete(0,deleteQueryCommon.length());
        deleteQueryCommon.append(deleteBuilder);
        //     IN sub clause is inserted before the last character
        deleteQueryCommon.insert(deleteQueryCommon.length()-1,inClauseBuilderExpenditure);
        deleteAllExpenditureRowsSQL = deleteQueryCommon.toString();
        //  (2) delete only monthly revenue rows that do not have benefit object codes
        deleteQueryCommon.append("\n");
        deleteQueryCommon.append(benefitsObjectClassesMonthly);
        deleteNoBenefitsExpenditureRowsSQL = deleteQueryCommon.toString();
        
        // build the SQL for detecting expenditure rows which require a benefits calculation
        expenditureBenefitsObjectClassesCheck.insert(expenditureBenefitsObjectClassesCheck.length()-1,inClauseBuilderExpenditure);
        expenditureBenefitsObjectClassesCheckSQL = expenditureBenefitsObjectClassesCheck.toString();
        
        // build the SQL for building monthly budgets from the annual budget in the general ledger
        // (1) revenue
        StringBuilder allocateGeneralLedgerRevenueByMonth = new StringBuilder(5500);
        allocateGeneralLedgerRevenueByMonth.append(allocateGeneralLedgerByMonth);
        allocateGeneralLedgerRevenueByMonth.insert(allocateGeneralLedgerRevenueByMonth.length()-2,inClauseBuilderRevenue);
        allocateGeneralLedgerRevenueByMonthSQL = allocateGeneralLedgerRevenueByMonth.toString();
        //  (2) expenditure
        allocateGeneralLedgerByMonth.insert(allocateGeneralLedgerByMonth.length()-2,inClauseBuilderExpenditure);
        allocateGeneralLedgerExpenditureByMonthSQL = allocateGeneralLedgerByMonth.toString();
    }
    
    private Object[] sqlParameters(Object... inParameters)
    {
        // we want preparedStatement to recognize the object classes as type String, instead of hard-coding them as constant parameters into an 
        // IN statement with a delimiter that may not be standard over all databases.  we also apparently can't pass them in as an array of
        // Object[] embedded in an outer Object[].  So, we have to flatten the arguments out.
        int objectTypeListSize = (inRevenueObjectTypes.length > inExpenditureObjectTypes.length ? inRevenueObjectTypes.length-1 : inExpenditureObjectTypes.length - 1);
        ArrayList<Object> parmList = new ArrayList<Object>(inParameters.length+objectTypeListSize);
        for (int idx = 0; idx < inParameters.length; idx++)
        {
           if (inParameters[idx] instanceof Object[])
           {
               for (int kdx = 0; kdx < ((Object[]) inParameters[idx]).length; kdx++)
               {
                   parmList.add(((Object[]) inParameters[idx])[kdx]);
               }
           }
           else
           {
               parmList.add(inParameters[idx]);
           }
            
        }
        return ((Object[]) parmList.toArray());
    }
    
    public void setParameterService(ParameterService parameterService)
    {
        this.parameterService = parameterService;
    }

}
