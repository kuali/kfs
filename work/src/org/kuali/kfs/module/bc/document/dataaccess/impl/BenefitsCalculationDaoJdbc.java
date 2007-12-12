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
package org.kuali.module.budget.dao.jdbc;

import java.lang.String;
import java.lang.StringBuilder;

import org.kuali.core.util.Guid;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.core.dao.jdbc.PlatformAwareDaoBaseJdbc;

import org.kuali.kfs.bo.Options;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;

import org.kuali.module.labor.LaborConstants.LABOR_LEDGER_PENDING_ENTRY_CODE;

import org.kuali.module.budget.dao.BenefitsCalculationDao;
import org.kuali.module.budget.dao.jdbc.BudgetConstructionDaoJdbcBase;

public class BenefitsCalculationDaoJdbc extends BudgetConstructionDaoJdbcBase implements BenefitsCalculationDao {

    private static String[] sqlAnnualTemplates;
    private static String[] sqlMonthlyTemplates;

    @RawSQL
    public BenefitsCalculationDaoJdbc() {
        
        StringBuilder sqlBuilder = new StringBuilder(1500);
        /*
         *   get rid of fringe benefits objects with no base
         */
        sqlBuilder.append("DELETE FROM LD_PND_BCSNTR_GL_T\nWHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n  AND (LD_PND_BL_BCSNTR_GL_T.UNIV_FISCAL_YR =?)\n ");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = ?)\n  AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n   AND (LD_PND_BCNSTR_GL_T.FIN_BEG_BAL_LN_AMT = 0)\n");
        sqlBuilder.append("  AND (EXISTS (SELECT 1 FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.FIN_COA_CD = LD_PND_BCNSTR_GL_T.FIN_COA_CD)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))");
        String  sqlFirst = sqlBuilder.toString();
        sqlBuilder.delete(0, sqlBuilder.length() - 1);
        /*
         *   set the request to 0 for fringe benefits objects with base
         */
        sqlBuilder.append("UPDATE LD_PND_BCNSTR_GL_T\nSET LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT =0\n");
        sqlBuilder.append("WHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n  AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = ?)\n  AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n AND (EXISTS (SELECT 1 FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.FIN_COA_CD = LD_PND_BCNSTR_GL_T.FIN_COA_CD)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))");
        String sqlSecond = sqlBuilder.toString();
        /*
         *   sum the amounts in benefits-eligible objects and attach the appropriate benefits object code
         */
        sqlBuilder.delete(0, sqlBuilder.length() - 1);
        sqlBuilder.append("INSERT INTO LD_BCN_BENEFITS_RECALC01_MT\n(SELECT ?,LD_BENEFTIS_CALC_T.POS_FRNGBEN_OBJ_CD,\n");
        sqlBuilder.append(" ROUND(SUM(LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT),0)\n FROM LD_PND_BCNSTR_GL_T,\n");
        sqlBuilder.append("      LD_LBR_OBJ_BENE_T,\n      LD_BENEFITS_CALC_T\n WHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = ?)\n   AND (LD_PND_BCNSTR_BL_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n   AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT != 0)\n   AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = LD_LBR_OBJ_BENE_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_coa_cd = LD_LBR_OBJ_BENE_T.fin_coa_cd)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_object_cd = LD_LBR_OBJ_BENE_T.fin_object_cd)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.univ_fiscal_yr = LD_BENEFITS_CALC_T.univ_fiscal_yr)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.fin_coa_cd = LD_BENEFITS_CALC_T.fin_coa_cd)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.finobj_bene_typ_cd = LD_BENEFITS_CALC_T.pos_benefit_typ_cd)\n");
        sqlBuilder.append(" GROUP BY LD_BENEFITS_CALC_T.pos_frngben_obj_cd)");
        String sqlThird = sqlBuilder.toString();
        /*
         *    re-set the request amount for the appropriate benefits code
         */
        sqlBuilder.delete(0, sqlBuilder.length() - 1);
        sqlBuilder.append("UPDATE ld_pnd_bcnstr_gl_t\n");
        sqlBuilder.append("SET ld_pnd_bcnstr_gl_t.acln_annl_bal_amt =\n");
        sqlBuilder.append("        (SELECT ld_bcn_benefits_recalc01_mt.fb_sum\n");
        sqlBuilder.append("         FROM ld_benefits_recalc01_mt\n");
        sqlBuilder.append("        WHERE (ld_bcn_benefits_recalc01_mt.sesid = ?)\n");
        sqlBuilder.append("          AND (ld_pnd_bcnstr_gl_t.fin_object_cd = ld_bcn_benefits_recalc01_mt.pos_frngben_obj_cd))\n");
        sqlBuilder.append("WHERE (ld_pnd_bcnstr_gl_t.fdoc_nbr = ?)\n");
        sqlBuilder.append("  AND (ld_pnd_bcnstr_gl_t.univ_fiscal_yr = ?)\n");
        sqlBuilder.append("  AND (ld_pnd_bcnstr_gl_t.fin_coa_cd = ?)\n");
        sqlBuilder.append("  AND (ld_pnd_bcnstr_gl_t.account_nbr = ?)\n");
        sqlBuilder.append("  AND (ld_pnd_bcnstr_gl_t.sub_acct_nbr = ?)\n");
        sqlBuilder.append("  AND (ld_pnd_bcnstr_gl_t.fin_sub_obj_cd = ");
        sqlBuilder.append(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        sqlBuilder.append(")\n");
        sqlBuilder.append("  AND ld_pnd_bcnstr_gl_t.fin_balance_typ_cd = ");
        sqlBuilder.append(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        sqlBuilder.append(")\n");
        sqlBuilder.append("  AND ld_pnd_bcnstr_gl_t.fin_obj_typ_cd = ?)\n");
        sqlBuilder.append("  AND EXISTS (SELECT 1\n");
        sqlBuilder.append("              FROM ld_benefits_recalc01_mt\n");
        sqlBuilder.append("              WHERE (sesid = ?)\n");
        sqlBuilder.append("                AND (ld_pnd_bcnstr_gl_t.fin_object_cd = ld_benefits_recalc01_mt.pos_frngben_obj_cd))");
        String sqlFourth = sqlBuilder.toString();
        /*
         *   now re-insert rows with zero base which still have benefits-eligible object codes in pending BC GL
         */
        sqlBuilder.delete(0, sqlBuilder.length() - 1);
        sqlBuilder.append("INSERT INTO ld_pnd_bcnstr_gl_t\n");
        sqlBuilder.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCOUNT_NBR, FIN_OBJECT_CD,\n");
        sqlBuilder.append(" FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT)");
        sqlBuilder.append("SELECT ?, ?, ?, ?, ?,\n");
        sqlBuilder.append("ld_benefits_recalc01_mt.pos_frngben_obj_cd,\n");
        sqlBuilder.append(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        sqlBuilder.append(",\n");
        sqlBuilder.append(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        sqlBuilder.append(",?,\n");
        sqlBuilder.append("ld_benefits_recalc01_mt.fb_sum, 0\n");
        sqlBuilder.append("FROM ld_benefits_recalc01_mt\n");
        sqlBuilder.append("WHERE NOT EXISTS\n");
        sqlBuilder.append("(SELECT *\n");
        sqlBuilder.append(" FROM ld_pnd_bcnstr_gl_t\n");
        sqlBuilder.append(" WHERE ld_pnd_bcnstr_gl_t.fdoc_nbr = ?\n");
        sqlBuilder.append("   AND ld_pnd_bcnstr_gl_t.univ_fiscal_yr = ?\n");
        sqlBuilder.append("   AND ld_pnd_bcnstr_gl_t.fin_coa_cd = ?\n");
        sqlBuilder.append("   AND ld_pnd_bcsntr_gl_t.account_nbr = ?\n");
        sqlBuilder.append("   AND ld_pnd_bcsntr_gl_t.sub_acct_nbr = ?\n");
        sqlBuilder.append("   AND ld_pnd_bcsntr_gl_t.fin_object_cd = ld_benefits_recalc01_mt.pos_frngben_obj_cd\n");
        sqlBuilder.append("   AND ld_pnd_bcsntr_gl_t.fin_sub_obj_cd = ");
        sqlBuilder.append(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        sqlBuilder.append("\n   AND ld_pnd_bcsntr_gl_t.fin_balance_typ_cd = ");
        sqlBuilder.append(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        sqlBuilder.append("\n  AND ld_pnd_bcsntr_gl_t.fin_obj_typ_cd = ?");
        sqlBuilder.append("  AND ld_benefits_recalc01_mt.sesid = ?)");
        String sqlFifth = sqlBuilder.toString();
        sqlBuilder.delete(0, sqlBuilder.length() - 1);
        sqlAnnualTemplates = new String[] {sqlFirst, sqlSecond, sqlThird, sqlFourth, sqlFifth};
        /*
         * this is the SQL for the monthly budget benefits. any rounding amount is added to the amount for month 1
         */
        /* 
         * cleanup by deleting any existing monthly benefit recs 
         */
        sqlBuilder.append("DELETE FROM ld_bcnstr_month_t\n");
        sqlBuilder.append("WHERE ld_bcnstr_month_t.fdoc_nbr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.univ_fiscal_yr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.fin_coa_cd = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.account_nbr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.sub_acct_nbr = ?\n");
        sqlBuilder.append("  AND EXISTS\n");
        sqlBuilder.append("        (SELECT 1\n");
        sqlBuilder.append("         FROM ld_benefits_calc_t\n");
        sqlBuilder.append("        WHERE ld_benefits_calc_t.univ_fiscal_yr = p_univ_fiscal_yr");
        sqlBuilder.append("          AND ld_benefits_calc_t.fin_coa_cd = p_fin_coa_cd\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fin_object_cd = ld_benefits_calc_t.pos_frngben_obj_cd)");
        String mnthly1 = sqlBuilder.toString();
        sqlBuilder.delete(0, sqlBuilder.length() - 1);
        /* 
         * calc benefits for source objects and sum to target objects 
         */
        sqlBuilder.append("INSERT INTO ld_bcnstr_month_t\n");
        sqlBuilder.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD,\n");
        sqlBuilder.append("FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT,\n");
        sqlBuilder.append(" FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT, FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT,\n");
        sqlBuilder.append(" FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT)\n");
        sqlBuilder.append("SELECT  ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("ld_benefits_calc_t.pos_frngben_obj_cd,\n    ");
        sqlBuilder.append(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        sqlBuilder.append(",\n    ");
        sqlBuilder.append(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        sqlBuilder.append(",?,\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo1_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo2_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo3_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo4_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo5_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo6_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo7_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo8_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo9_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo10_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo11_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0)),\n");
        sqlBuilder.append("   SUM(COALESCE(ROUND(ld_bcnstr_month_t.fdoc_ln_mo12_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0),0))\n");
        sqlBuilder.append("FROM ld_bcnstr_month_t,\n");
        sqlBuilder.append("    ld_benefits_calc_t,\n");
        sqlBuilder.append("    ld_lbr_obj_bene_t\n");
        sqlBuilder.append("WHERE ld_bcnstr_month_t.fdoc_nbr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.univ_fiscal_yr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.fin_coa_cd = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.account_nbr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.sub_acct_nbr = ?\n");
        sqlBuilder.append("  AND ld_lbr_obj_bene_t.univ_fiscal_yr = ld_bcnstr_month_t.univ_fiscal_yr\n");
        sqlBuilder.append("  AND ld_lbr_obj_bene_t.fin_coa_cd = ld_bcnstr_month_t.fin_coa_cd\n");
        sqlBuilder.append("  AND ld_lbr_obj_bene_t.fin_object_cd = ld_bcnstr_month_t.fin_object_cd\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.univ_fiscal_yr = ld_lbr_obj_bene_t.univ_fiscal_yr\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.fin_coa_cd = ld_lbr_obj_bene_t.fin_coa_cd\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.pos_benefit_typ_cd = ld_lbr_obj_bene_t.finobj_bene_typ_cd\n");
        sqlBuilder.append("GROUP BY ld_benefits_calc_t.pos_frngben_obj_cd\n");
        String mnthly2 = sqlBuilder.toString();
        sqlBuilder.delete(0, sqlBuilder.length() - 1);


        /* 
         * adjust the month 1 totals for rounding error 
        */
        sqlBuilder.append("UPDATE ld_bcnstr_month_t\n");
        sqlBuilder.append("SET fdoc_ln_mo1_amt =\n");
        sqlBuilder.append("    (SELECT (ld_bcnstr_month_t.fdoc_ln_mo1_amt +\n");
        sqlBuilder.append("            (ld_pnd_bcnstr_gl_t.acln_annl_bal_amt -\n");
        sqlBuilder.append("            (ld_bcnstr_month_t.fdoc_ln_mo1_amt + ld_bcnstr_month_t.fdoc_ln_mo2_amt +\n");
        sqlBuilder.append("             ld_bcnstr_month_t.fdoc_ln_mo3_amt + ld_bcnstr_month_t.fdoc_ln_mo4_amt +\n");
        sqlBuilder.append("             ld_bcnstr_month_t.fdoc_ln_mo5_amt + ld_bcnstr_month_t.fdoc_ln_mo6_amt +\n");
        sqlBuilder.append("             ld_bcnstr_month_t.fdoc_ln_mo7_amt + ld_bcnstr_month_t.fdoc_ln_mo8_amt +\n");
        sqlBuilder.append("             ld_bcnstr_month_t.fdoc_ln_mo9_amt + ld_bcnstr_month_t.fdoc_ln_mo10_amt +\n");
        sqlBuilder.append("             ld_bcnstr_month_t.fdoc_ln_mo11_amt + ld_bcnstr_month_t.fdoc_ln_mo12_amt)))\n");
        sqlBuilder.append("    FROM ld_pnd_bcnstr_gl_t\n"); 
        sqlBuilder.append("    WHERE ld_bcnstr_month_t.fs_origin_cd = ld_pnd_bcnstr_gl_t.fs_origin_cd\n");
        sqlBuilder.append("      AND ld_bcnstr_month_t.fdoc_nbr = ld_pnd_bcnstr_gl_t.fdoc_nbr\n");
        sqlBuilder.append("      AND ld_bcnstr_month_t.univ_fiscal_yr = ld_pnd_bcnstr_gl_t.univ_fiscal_yr\n");
        sqlBuilder.append("      AND ld_bcnstr_month_t.fin_coa_cd = ld_pnd_bcnstr_gl_t.fin_coa_cd\n");
        sqlBuilder.append("      AND ld_bcnstr_month_t.account_nbr = ld_pnd_bcnstr_gl_t.account_nbr\n");
        sqlBuilder.append("      AND ld_bcnstr_month_t.sub_acct_nbr = ld_pnd_bcnstr_gl_t.sub_acct_nbr\n");
        sqlBuilder.append("      AND ld_bcnstr_month_t.fin_object_cd = ld_pnd_bcnstr_gl_t.fin_object_cd\n");
        sqlBuilder.append("      AND ld_bcnstr_month_t.fin_sub_obj_cd = ld_pnd_bcnstr_gl_t.fin_sub_obj_cd\n");
        sqlBuilder.append("      AND ld_bcnstr_month_t.fin_balance_typ_cd = ld_pnd_bcnstr_gl_t.fin_balance_typ_cd\n");
        sqlBuilder.append("      AND ld_bcnstr_month_t.fin_obj_typ_cd = ld_pnd_bcnstr_gl_t.fin_obj_typ_cd)\n");
        sqlBuilder.append("WHERE ld_bcnstr_month_t.fdoc_nbr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.univ_fiscal_yr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.fin_coa_cd = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.account_nbr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.sub_acct_nbr = ?\n");
        sqlBuilder.append("  AND EXISTS \n");
        sqlBuilder.append("        (SELECT 1\n");
        sqlBuilder.append("        FROM ld_benefits_calc_t\n"); 
        sqlBuilder.append("        WHERE ld_benefits_calc_t.univ_fiscal_yr = ?\n");
        sqlBuilder.append("          AND ld_benefits_calc_t.fin_coa_cd = ?\n");
        sqlBuilder.append("          AND ld_benefits_calc_t.pos_frngben_obj_cd = ld_bcnstr_month_t.fin_object_cd)\n");;
        String mnthly3 = sqlBuilder.toString();
        sqlMonthlyTemplates = new String[] {mnthly1, mnthly2, mnthly3};
    }

    @RawSQL
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd) {
        String idForSession = (new Guid()).toString();

        getSimpleJdbcTemplate().update(sqlAnnualTemplates[0], documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlAnnualTemplates[1], documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlAnnualTemplates[2], idForSession, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlAnnualTemplates[3], idForSession, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, idForSession);
        getSimpleJdbcTemplate().update(sqlAnnualTemplates[4], documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, idForSession);
        clearTempTableBySesId("LD_BCN_BENEFITS_RECALC01_MT", "SESID", idForSession);
    }

    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd) {
        String idForSession = (new Guid()).toString();
        
        getSimpleJdbcTemplate().update(sqlMonthlyTemplates[0], documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlMonthlyTemplates[1],  documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlMonthlyTemplates[2],  documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, fiscalYear, chartOfAccounts);
    }
}
