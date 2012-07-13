/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.document.dataaccess.impl;

import java.util.ArrayList;
import java.util.UUID;

import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BenefitsCalculationDao;
import org.kuali.kfs.sys.KFSConstants;

/**
 * implements the SQL procedures to calculate benefits for the personnel object codes in the budget. apply the appropriate
 * percentage to each object type in the general ledger, and split the result out into the monthly budget lines if monthly budgets
 * exist for the accounting key.
 */

public class BenefitsCalculationDaoJdbc extends BudgetConstructionDaoJdbcBase implements BenefitsCalculationDao {

    private static ArrayList<SQLForStep> sqlAnnualSteps = new ArrayList<SQLForStep>(6);
    private static ArrayList<SQLForStep> sqlMonthlySteps = new ArrayList<SQLForStep>(4);


    /**
     * these will be set to constant values in the constructor and used throughout SQL for the various steps.
     */

    public BenefitsCalculationDaoJdbc() {

        // this is a bean constructor, so it is dangerous to access static constants defined in other classes here. the other
        // classes may not have been loaded yet.
        // so, we use insertion points to indicate where such constants should be placed in the SQL, and we splice them in a run
        // time. we also use insertion points to splice in run time constants from SH_PARM_T.
        StringBuilder sqlBuilder = new StringBuilder(2500);
        ArrayList<Integer> insertionPoints = new ArrayList<Integer>();
        /**
         * this needs to be done before we can get rid of annual fringe benefits objects with no base. LD_BNCSTR_MNTH_T has an RI
         * child constraint on LD_PND_BCNSTR_GL_T. So, before we eliminate any Budget Construction general ledger rows, we have to
         * get rid of any dependent Budget Construction Monthly rows. If we call this set of queries to rebuild budgeted benefits
         * for the general ledger, the next set of queries will also have to be called if monthly budgets exist. If no monthly
         * budgets exist, the query below will not do anything. In that case, calling the Budget Construction general ledger
         * benefits calculation routine without calling the monthly benefits calculation routine will be acceptable.
         */
        sqlBuilder.append("DELETE FROM LD_BCNSTR_MONTH_T\n");
        sqlBuilder.append("WHERE (LD_BCNSTR_MONTH_T.FDOC_NBR = ?)\n");
        sqlBuilder.append("AND (LD_BCNSTR_MONTH_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("AND (LD_BCNSTR_MONTH_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("AND (LD_BCNSTR_MONTH_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("AND (LD_BCNSTR_MONTH_T.SUB_ACCT_NBR = ?)\n");
        sqlBuilder.append("AND (EXISTS (SELECT 1\n");
        sqlBuilder.append("       FROM (LD_PND_BCNSTR_GL_T INNER JOIN LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("       ON ((LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = LD_BENEFITS_CALC_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("           AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = LD_BENEFITS_CALC_T.FIN_COA_CD)\n");
        sqlBuilder.append("           AND (LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD = LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD)))\n");
        sqlBuilder.append("       WHERE (LD_BCNSTR_MONTH_T.UNIV_FISCAL_YR = LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("         AND (LD_BCNSTR_MONTH_T.FDOC_NBR = LD_PND_BCNSTR_GL_T.FDOC_NBR)\n");
        sqlBuilder.append("         AND (LD_BCNSTR_MONTH_T.FIN_COA_CD = LD_PND_BCNSTR_GL_T.FIN_COA_CD)\n");
        sqlBuilder.append("         AND (LD_BCNSTR_MONTH_T.ACCOUNT_NBR = LD_PND_BCNSTR_GL_T.ACCOUNT_NBR)\n");
        sqlBuilder.append("         AND (LD_BCNSTR_MONTH_T.SUB_ACCT_NBR = LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR)\n");
        sqlBuilder.append("         AND (LD_BCNSTR_MONTH_T.FIN_OBJECT_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)\n");
        sqlBuilder.append("         AND (LD_PND_BCNSTR_GL_T.FIN_BEG_BAL_LN_AMT = 0)))\n");
        sqlAnnualSteps.add(new SQLForStep(sqlBuilder));
        sqlBuilder.delete(0, sqlBuilder.length());
        /**
         * get rid of fringe benefits objects with no base
         */
        sqlBuilder.append("DELETE FROM LD_PND_BCNSTR_GL_T\n");
        sqlBuilder.append("WHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR =?)\n ");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.FIN_BEG_BAL_LN_AMT = 0)\n");
        sqlBuilder.append("  AND (EXISTS (SELECT 1 FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.FIN_COA_CD = LD_PND_BCNSTR_GL_T.FIN_COA_CD)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))");
        sqlAnnualSteps.add(new SQLForStep(sqlBuilder));
        sqlBuilder.delete(0, sqlBuilder.length());
        /**
         * set the request to 0 for fringe benefits objects with base
         */
        sqlBuilder.append("UPDATE LD_PND_BCNSTR_GL_T\n");
        sqlBuilder.append("SET ACLN_ANNL_BAL_AMT =0\n");
        sqlBuilder.append("WHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n");
        sqlBuilder.append("  AND (EXISTS (SELECT 1 FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("               WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("                 AND (LD_BENEFITS_CALC_T.FIN_COA_CD = LD_PND_BCNSTR_GL_T.FIN_COA_CD)\n");
        sqlBuilder.append("                 AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))");
        sqlAnnualSteps.add(new SQLForStep(sqlBuilder));
        sqlBuilder.delete(0, sqlBuilder.length());
        /**
         * sum the amounts in benefits-eligible objects and attach the appropriate benefits object code
         */
        sqlBuilder.append("INSERT INTO LD_BCN_BENEFITS_RECALC01_MT\n(SESID, POS_FRNGBEN_OBJ_CD, FB_SUM)\n");
        sqlBuilder.append("(SELECT ?,LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD,\n");
        sqlBuilder.append(" ROUND(SUM(LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT * (LD_BENEFITS_CALC_T.POS_FRNG_BENE_PCT/100.0)),0)\n ");
        sqlBuilder.append(" FROM LD_PND_BCNSTR_GL_T,\n");
        sqlBuilder.append("      LD_LBR_OBJ_BENE_T,\n");
        sqlBuilder.append("      LD_BENEFITS_CALC_T\n");
        sqlBuilder.append(" WHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT <> 0)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = LD_LBR_OBJ_BENE_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_coa_cd = LD_LBR_OBJ_BENE_T.fin_coa_cd)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_object_cd = LD_LBR_OBJ_BENE_T.fin_object_cd)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.actv_ind = ?)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.univ_fiscal_yr = LD_BENEFITS_CALC_T.univ_fiscal_yr)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.fin_coa_cd = LD_BENEFITS_CALC_T.fin_coa_cd)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.finobj_bene_typ_cd = LD_BENEFITS_CALC_T.pos_benefit_typ_cd)\n");
        sqlBuilder.append("   AND (LD_BENEFITS_CALC_T.actv_ind = ?)\n");
        sqlBuilder.append(" GROUP BY LD_BENEFITS_CALC_T.pos_frngben_obj_cd)");
        sqlAnnualSteps.add(new SQLForStep(sqlBuilder));
        sqlBuilder.delete(0, sqlBuilder.length());
        /**
         * re-set the request amount for the appropriate benefits code
         */
        sqlBuilder.append("UPDATE LD_PND_BCNSTR_GL_T\n");
        sqlBuilder.append("SET acln_annl_bal_amt =\n");
        sqlBuilder.append("        (SELECT LD_BCN_BENEFITS_RECALC01_MT.fb_sum\n");
        sqlBuilder.append("         FROM LD_BCN_BENEFITS_RECALC01_MT\n");
        sqlBuilder.append("        WHERE (LD_BCN_BENEFITS_RECALC01_MT.sesid = ?)\n");
        sqlBuilder.append("          AND (LD_PND_BCNSTR_GL_T.fin_object_cd = LD_BCN_BENEFITS_RECALC01_MT.pos_frngben_obj_cd))\n");
        sqlBuilder.append("WHERE (LD_PND_BCNSTR_GL_T.fdoc_nbr = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.univ_fiscal_yr = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.fin_coa_cd = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.account_nbr = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.sub_acct_nbr = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.fin_sub_obj_cd = '");
        // default sub object code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("')\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.fin_balance_typ_cd = '");
        // general ledger budget balance type code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("')\n");
        sqlBuilder.append("  AND EXISTS (SELECT 1\n");
        sqlBuilder.append("              FROM LD_BCN_BENEFITS_RECALC01_MT\n");
        sqlBuilder.append("              WHERE (sesid = ?)\n");
        sqlBuilder.append("                AND (LD_PND_BCNSTR_GL_T.fin_object_cd = LD_BCN_BENEFITS_RECALC01_MT.pos_frngben_obj_cd))\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.fin_obj_typ_cd IN ");
        // expenditure object types
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append(")");
        sqlAnnualSteps.add(new SQLForStep(sqlBuilder, insertionPoints));
        sqlBuilder.delete(0, sqlBuilder.length());
        insertionPoints.clear();
        /**
         * now re-insert rows with zero base which still have benefits-eligible object codes in pending BC GL. all budget
         * construction GL lines added by the budget construction application have an object type code of
         * FinObjTypeExpenditureexpCd, which we pass at run time as a parameter. we have an IN clause to check for other object
         * types which may have been loaded in the base from the general ledger. the request for such lines will not have this
         * object type.
         */
        sqlBuilder.append("INSERT INTO LD_PND_BCNSTR_GL_T\n");
        sqlBuilder.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD,\n");
        sqlBuilder.append(" FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT)\n");
        sqlBuilder.append("(SELECT ?, ?, ?, ?, ?,\n");
        sqlBuilder.append("LD_BCN_BENEFITS_RECALC01_MT.pos_frngben_obj_cd,\n");
        sqlBuilder.append(" '");
        // default sub object code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("', '");
        // general ledger budget balance type code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("', ");
        sqlBuilder.append("CA_OBJECT_CODE_T.fin_obj_typ_cd,\n");
        sqlBuilder.append("LD_BCN_BENEFITS_RECALC01_MT.fb_sum, 0\n");
        sqlBuilder.append("FROM LD_BCN_BENEFITS_RECALC01_MT,\n");
        sqlBuilder.append("     CA_OBJECT_CODE_T\n");
        sqlBuilder.append("WHERE (LD_BCN_BENEFITS_RECALC01_MT.sesid = ?)\n");
        sqlBuilder.append("  AND (CA_OBJECT_CODE_T.univ_fiscal_yr = ?)\n");
        sqlBuilder.append("  AND (CA_OBJECT_CODE_T.fin_coa_cd = ?)\n");
        sqlBuilder.append("  AND (CA_OBJECT_CODE_T.fin_object_cd = LD_BCN_BENEFITS_RECALC01_MT.pos_frngben_obj_cd)\n");
        sqlBuilder.append("  AND (NOT EXISTS\n");
        sqlBuilder.append("(SELECT 1\n");
        sqlBuilder.append(" FROM LD_PND_BCNSTR_GL_T\n");
        sqlBuilder.append(" WHERE (LD_PND_BCNSTR_GL_T.fdoc_nbr = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.univ_fiscal_yr = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_coa_cd = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.account_nbr = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.sub_acct_nbr = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_object_cd = LD_BCN_BENEFITS_RECALC01_MT.pos_frngben_obj_cd)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_sub_obj_cd = '");
        // default sub object code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("')\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_balance_typ_cd = '");
        // general ledger budget balance type code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("')\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_obj_typ_cd IN ");
        // expenditure object types
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("))))");
        sqlAnnualSteps.add(new SQLForStep(sqlBuilder, insertionPoints));
        sqlBuilder.delete(0, sqlBuilder.length());
        insertionPoints.clear();

        /********************************************
         * Added new statements to check labor benefit rate category code. Used only when
         * ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND system parameter is set to "Y"
         ********************************************/
        /**
         * set the request to 0 for fringe benefits objects with base
         */
        sqlBuilder.append("UPDATE LD_PND_BCNSTR_GL_T\n");
        sqlBuilder.append("SET ACLN_ANNL_BAL_AMT =0\n");
        sqlBuilder.append("WHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n");
        sqlBuilder.append("  AND (EXISTS (SELECT 1 FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("               WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("                 AND (LD_BENEFITS_CALC_T.FIN_COA_CD = LD_PND_BCNSTR_GL_T.FIN_COA_CD)\n");
        sqlBuilder.append("                 AND (LD_BENEFITS_CALC_T.LBR_BEN_RT_CAT_CD = ?)\n");
        sqlBuilder.append("                 AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))");
        sqlAnnualSteps.add(new SQLForStep(sqlBuilder));
        sqlBuilder.delete(0, sqlBuilder.length());
        /**
         * sum the amounts in benefits-eligible objects and attach the appropriate benefits object code
         */
        sqlBuilder.append("INSERT INTO LD_BCN_BENEFITS_RECALC01_MT\n(SESID, POS_FRNGBEN_OBJ_CD, FB_SUM)\n");
        sqlBuilder.append("(SELECT ?,LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD,\n");
        sqlBuilder.append(" ROUND(SUM(LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT * (LD_BENEFITS_CALC_T.POS_FRNG_BENE_PCT/100.0)),0)\n ");
        sqlBuilder.append(" FROM LD_PND_BCNSTR_GL_T,\n");
        sqlBuilder.append("      LD_LBR_OBJ_BENE_T,\n");
        sqlBuilder.append("      LD_BENEFITS_CALC_T\n");
        sqlBuilder.append(" WHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT <> 0)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = LD_LBR_OBJ_BENE_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_coa_cd = LD_LBR_OBJ_BENE_T.fin_coa_cd)\n");
        sqlBuilder.append("   AND (LD_PND_BCNSTR_GL_T.fin_object_cd = LD_LBR_OBJ_BENE_T.fin_object_cd)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.univ_fiscal_yr = LD_BENEFITS_CALC_T.univ_fiscal_yr)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.fin_coa_cd = LD_BENEFITS_CALC_T.fin_coa_cd)\n");
        sqlBuilder.append("   AND (LD_BENEFITS_CALC_T.LBR_BEN_RT_CAT_CD = ?)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.finobj_bene_typ_cd = LD_BENEFITS_CALC_T.pos_benefit_typ_cd)\n");
        sqlBuilder.append("   AND (LD_LBR_OBJ_BENE_T.actv_ind = ?)\n");
        sqlBuilder.append("   AND (LD_BENEFITS_CALC_T.actv_ind = ?)\n");
        sqlBuilder.append(" GROUP BY LD_BENEFITS_CALC_T.pos_frngben_obj_cd)");
        sqlAnnualSteps.add(new SQLForStep(sqlBuilder));
        sqlBuilder.delete(0, sqlBuilder.length());


        /**
         * this is the SQL for the monthly budget benefits. any rounding amount is added to the amount for month 1
         */
        /**
         * Cleanup the rare case where annual request goes to zero with existing monthly buckets. This gives monthly calc benefits
         * problems from constraints since the annual benefit target row might be non-existent when it inserts the new results from
         * the left over monthly buckets This is usually the case since annual benefits are usually calculated first.
         */
        sqlBuilder.append("DELETE FROM LD_BCNSTR_MONTH_T\n");
        sqlBuilder.append("WHERE LD_BCNSTR_MONTH_T.fdoc_nbr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.univ_fiscal_yr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.fin_coa_cd = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.account_nbr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.sub_acct_nbr = ?\n");
        sqlBuilder.append("  AND NOT (LD_BCNSTR_MONTH_T.fdoc_ln_mo1_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo2_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo3_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo4_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo5_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo6_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo7_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo8_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo9_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo10_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo11_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo12_amt = 0) \n");
        sqlBuilder.append("  AND EXISTS\n");
        sqlBuilder.append("        (SELECT 1\n");
        sqlBuilder.append("         FROM LD_PND_BCNSTR_GL_T\n");
        sqlBuilder.append("        WHERE LD_PND_BCNSTR_GL_T.fdoc_nbr = LD_BCNSTR_MONTH_T.fdoc_nbr\n");
        sqlBuilder.append("          AND LD_PND_BCNSTR_GL_T.univ_fiscal_yr = LD_BCNSTR_MONTH_T.univ_fiscal_yr\n");
        sqlBuilder.append("          AND LD_PND_BCNSTR_GL_T.fin_coa_cd = LD_BCNSTR_MONTH_T.fin_coa_cd\n");
        sqlBuilder.append("          AND LD_PND_BCNSTR_GL_T.account_nbr = LD_BCNSTR_MONTH_T.account_nbr\n");
        sqlBuilder.append("          AND LD_PND_BCNSTR_GL_T.sub_acct_nbr = LD_BCNSTR_MONTH_T.sub_acct_nbr\n");
        sqlBuilder.append("          AND LD_PND_BCNSTR_GL_T.fin_object_cd = LD_BCNSTR_MONTH_T.fin_object_cd\n");
        sqlBuilder.append("          AND LD_PND_BCNSTR_GL_T.fin_sub_obj_cd = LD_BCNSTR_MONTH_T.fin_sub_obj_cd\n");
        sqlBuilder.append("          AND LD_PND_BCNSTR_GL_T.fin_balance_typ_cd = LD_BCNSTR_MONTH_T.fin_balance_typ_cd\n");
        sqlBuilder.append("          AND LD_PND_BCNSTR_GL_T.fin_obj_typ_cd = LD_BCNSTR_MONTH_T.fin_obj_typ_cd\n");
        sqlBuilder.append("          AND LD_PND_BCNSTR_GL_T.acln_annl_bal_amt = 0)");
        sqlMonthlySteps.add(new SQLForStep(sqlBuilder));
        sqlBuilder.delete(0, sqlBuilder.length());


        /**
         * cleanup by deleting any existing monthly benefit recs
         */
        sqlBuilder.append("DELETE FROM LD_BCNSTR_MONTH_T\n");
        sqlBuilder.append("WHERE LD_BCNSTR_MONTH_T.fdoc_nbr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.univ_fiscal_yr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.fin_coa_cd = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.account_nbr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.sub_acct_nbr = ?\n");
        sqlBuilder.append("  AND EXISTS\n");
        sqlBuilder.append("        (SELECT 1\n");
        sqlBuilder.append("         FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("        WHERE LD_BENEFITS_CALC_T.univ_fiscal_yr = ?\n");
        sqlBuilder.append("          AND LD_BENEFITS_CALC_T.fin_coa_cd = ?\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fin_object_cd = LD_BENEFITS_CALC_T.pos_frngben_obj_cd)");
        sqlMonthlySteps.add(new SQLForStep(sqlBuilder));
        sqlBuilder.delete(0, sqlBuilder.length());
        /**
         * calc benefits for source objects and sum to target objects. all budget construction GL lines added by the budget
         * construction application have an object type code of FinObjTypeExpenditureexpCd, which we pass at run time as a
         * parameter. we have an IN clause to check for other object types which may have been loaded in the base from the general
         * ledger. the request for such lines will not have this object type.
         */
        sqlBuilder.append("INSERT INTO LD_BCNSTR_MONTH_T\n");
        sqlBuilder.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD,\n");
        sqlBuilder.append("FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT,\n");
        sqlBuilder.append(" FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT, FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT,\n");
        sqlBuilder.append(" FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT)\n");
        sqlBuilder.append("SELECT ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("   LD_BENEFITS_CALC_T.pos_frngben_obj_cd,");
        sqlBuilder.append(" '");
        // default sub object code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("', '");
        // general ledger budget balance type code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("', ");
        sqlBuilder.append("CA_OBJECT_CODE_T.fin_obj_typ_cd, \n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo1_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo2_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo3_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo4_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo5_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo6_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo7_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo8_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo9_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo10_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo11_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(LD_BCNSTR_MONTH_T.fdoc_ln_mo12_amt * (LD_BENEFITS_CALC_T.pos_frng_bene_pct/100.0),0)),0)\n");
        sqlBuilder.append("FROM LD_BCNSTR_MONTH_T,\n");
        sqlBuilder.append("     LD_BENEFITS_CALC_T,\n");
        sqlBuilder.append("     LD_LBR_OBJ_BENE_T,\n");
        sqlBuilder.append("     CA_OBJECT_CODE_T\n");
        sqlBuilder.append("WHERE LD_BCNSTR_MONTH_T.fdoc_nbr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.univ_fiscal_yr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.fin_coa_cd = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.account_nbr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.sub_acct_nbr = ?\n");
        sqlBuilder.append("  AND NOT (LD_BCNSTR_MONTH_T.fdoc_ln_mo1_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo2_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo3_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo4_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo5_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo6_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo7_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo8_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo9_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo10_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo11_amt = 0\n");
        sqlBuilder.append("          AND LD_BCNSTR_MONTH_T.fdoc_ln_mo12_amt = 0) \n");
        sqlBuilder.append("  AND LD_LBR_OBJ_BENE_T.univ_fiscal_yr = LD_BCNSTR_MONTH_T.univ_fiscal_yr\n");
        sqlBuilder.append("  AND LD_LBR_OBJ_BENE_T.fin_coa_cd = LD_BCNSTR_MONTH_T.fin_coa_cd\n");
        sqlBuilder.append("  AND LD_LBR_OBJ_BENE_T.fin_object_cd = LD_BCNSTR_MONTH_T.fin_object_cd\n");
        sqlBuilder.append("  AND LD_LBR_OBJ_BENE_T.actv_ind = ?\n");
        sqlBuilder.append("  AND LD_BENEFITS_CALC_T.univ_fiscal_yr = LD_LBR_OBJ_BENE_T.univ_fiscal_yr\n");
        sqlBuilder.append("  AND LD_BENEFITS_CALC_T.fin_coa_cd = LD_LBR_OBJ_BENE_T.fin_coa_cd\n");
        sqlBuilder.append("  AND LD_BENEFITS_CALC_T.pos_benefit_typ_cd = LD_LBR_OBJ_BENE_T.finobj_bene_typ_cd\n");
        sqlBuilder.append("  AND LD_BENEFITS_CALC_T.univ_fiscal_yr = CA_OBJECT_CODE_T.univ_fiscal_yr\n");
        sqlBuilder.append("  AND LD_BENEFITS_CALC_T.fin_coa_cd = CA_OBJECT_CODE_T.fin_coa_cd\n");
        sqlBuilder.append("  AND LD_BENEFITS_CALC_T.pos_frngben_obj_cd = CA_OBJECT_CODE_T.fin_object_cd\n");
        sqlBuilder.append("  AND LD_BENEFITS_CALC_T.actv_ind = ?\n");
        sqlBuilder.append("GROUP BY LD_BENEFITS_CALC_T.pos_frngben_obj_cd, CA_OBJECT_CODE_T.fin_obj_typ_cd");
        sqlMonthlySteps.add(new SQLForStep(sqlBuilder, insertionPoints));
        sqlBuilder.delete(0, sqlBuilder.length());
        insertionPoints.clear();


        /**
         * adjust the month 1 totals for rounding error
         */
        sqlBuilder.append("UPDATE LD_BCNSTR_MONTH_T\n");
        sqlBuilder.append("SET fdoc_ln_mo1_amt =\n");
        sqlBuilder.append("    (SELECT (LD_BCNSTR_MONTH_T.fdoc_ln_mo1_amt +\n");
        sqlBuilder.append("            (LD_PND_BCNSTR_GL_T.acln_annl_bal_amt -\n");
        sqlBuilder.append("            (LD_BCNSTR_MONTH_T.fdoc_ln_mo1_amt + LD_BCNSTR_MONTH_T.fdoc_ln_mo2_amt +\n");
        sqlBuilder.append("             LD_BCNSTR_MONTH_T.fdoc_ln_mo3_amt + LD_BCNSTR_MONTH_T.fdoc_ln_mo4_amt +\n");
        sqlBuilder.append("             LD_BCNSTR_MONTH_T.fdoc_ln_mo5_amt + LD_BCNSTR_MONTH_T.fdoc_ln_mo6_amt +\n");
        sqlBuilder.append("             LD_BCNSTR_MONTH_T.fdoc_ln_mo7_amt + LD_BCNSTR_MONTH_T.fdoc_ln_mo8_amt +\n");
        sqlBuilder.append("             LD_BCNSTR_MONTH_T.fdoc_ln_mo9_amt + LD_BCNSTR_MONTH_T.fdoc_ln_mo10_amt +\n");
        sqlBuilder.append("             LD_BCNSTR_MONTH_T.fdoc_ln_mo11_amt + LD_BCNSTR_MONTH_T.fdoc_ln_mo12_amt)))\n");
        sqlBuilder.append("    FROM LD_PND_BCNSTR_GL_T\n");
        sqlBuilder.append("    WHERE LD_BCNSTR_MONTH_T.fdoc_nbr = LD_PND_BCNSTR_GL_T.fdoc_nbr\n");
        sqlBuilder.append("      AND LD_BCNSTR_MONTH_T.univ_fiscal_yr = LD_PND_BCNSTR_GL_T.univ_fiscal_yr\n");
        sqlBuilder.append("      AND LD_BCNSTR_MONTH_T.fin_coa_cd = LD_PND_BCNSTR_GL_T.fin_coa_cd\n");
        sqlBuilder.append("      AND LD_BCNSTR_MONTH_T.account_nbr = LD_PND_BCNSTR_GL_T.account_nbr\n");
        sqlBuilder.append("      AND LD_BCNSTR_MONTH_T.sub_acct_nbr = LD_PND_BCNSTR_GL_T.sub_acct_nbr\n");
        sqlBuilder.append("      AND LD_BCNSTR_MONTH_T.fin_object_cd = LD_PND_BCNSTR_GL_T.fin_object_cd\n");
        sqlBuilder.append("      AND LD_BCNSTR_MONTH_T.fin_sub_obj_cd = LD_PND_BCNSTR_GL_T.fin_sub_obj_cd\n");
        sqlBuilder.append("      AND LD_BCNSTR_MONTH_T.fin_balance_typ_cd = LD_PND_BCNSTR_GL_T.fin_balance_typ_cd\n");
        sqlBuilder.append("      AND LD_BCNSTR_MONTH_T.fin_obj_typ_cd = LD_PND_BCNSTR_GL_T.fin_obj_typ_cd)\n");
        sqlBuilder.append("WHERE LD_BCNSTR_MONTH_T.fdoc_nbr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.univ_fiscal_yr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.fin_coa_cd = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.account_nbr = ?\n");
        sqlBuilder.append("  AND LD_BCNSTR_MONTH_T.sub_acct_nbr = ?\n");
        sqlBuilder.append("  AND EXISTS \n");
        sqlBuilder.append("        (SELECT 1\n");
        sqlBuilder.append("        FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("        WHERE LD_BENEFITS_CALC_T.univ_fiscal_yr = ?\n");
        sqlBuilder.append("          AND LD_BENEFITS_CALC_T.fin_coa_cd = ?\n");
        sqlBuilder.append("          AND LD_BENEFITS_CALC_T.pos_frngben_obj_cd = LD_BCNSTR_MONTH_T.fin_object_cd\n");
        sqlBuilder.append("          AND LD_BENEFITS_CALC_T.actv_ind = ?)\n");
        ;
        sqlMonthlySteps.add(new SQLForStep(sqlBuilder));

        /********************************************
         * Added new statements to check labor benefit rate category code. Used only when
         * ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND system parameter is set to "Y"
         ********************************************/
        sqlBuilder.delete(0, sqlBuilder.length());
        insertionPoints.clear();

        /**
         * calc benefits for source objects and sum to target objects. all budget construction GL lines added by the budget
         * construction application have an object type code of FinObjTypeExpenditureexpCd, which we pass at run time as a
         * parameter. we have an IN clause to check for other object types which may have been loaded in the base from the general
         * ledger. the request for such lines will not have this object type.
         */
        sqlBuilder.append("INSERT INTO ld_bcnstr_month_t\n");
        sqlBuilder.append("(FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD,\n");
        sqlBuilder.append("FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT,\n");
        sqlBuilder.append(" FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT, FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT,\n");
        sqlBuilder.append(" FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT)\n");
        sqlBuilder.append("SELECT ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("    ?,\n");
        sqlBuilder.append("   ld_benefits_calc_t.pos_frngben_obj_cd,");
        sqlBuilder.append(" '");
        // default sub object code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("', '");
        // general ledger budget balance type code
        insertionPoints.add(sqlBuilder.length());
        sqlBuilder.append("', ");
        sqlBuilder.append("CA_OBJECT_CODE_T.fin_obj_typ_cd, \n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo1_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo2_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo3_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo4_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo5_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo6_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo7_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo8_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo9_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo10_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo11_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0),\n");
        sqlBuilder.append("   ROUND(SUM(COALESCE(ld_bcnstr_month_t.fdoc_ln_mo12_amt * (ld_benefits_calc_t.pos_frng_bene_pct/100.0),0)),0)\n");
        sqlBuilder.append("FROM ld_bcnstr_month_t,\n");
        sqlBuilder.append("     ld_benefits_calc_t,\n");
        sqlBuilder.append("     ld_lbr_obj_bene_t,\n");
        sqlBuilder.append("     CA_OBJECT_CODE_T\n");
        sqlBuilder.append("WHERE ld_bcnstr_month_t.fdoc_nbr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.univ_fiscal_yr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.fin_coa_cd = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.account_nbr = ?\n");
        sqlBuilder.append("  AND ld_bcnstr_month_t.sub_acct_nbr = ?\n");
        sqlBuilder.append("  AND NOT (ld_bcnstr_month_t.fdoc_ln_mo1_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo2_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo3_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo4_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo5_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo6_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo7_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo8_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo9_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo10_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo11_amt = 0\n");
        sqlBuilder.append("          AND ld_bcnstr_month_t.fdoc_ln_mo12_amt = 0) \n");
        sqlBuilder.append("  AND ld_lbr_obj_bene_t.univ_fiscal_yr = ld_bcnstr_month_t.univ_fiscal_yr\n");
        sqlBuilder.append("  AND ld_lbr_obj_bene_t.fin_coa_cd = ld_bcnstr_month_t.fin_coa_cd\n");
        sqlBuilder.append("  AND ld_lbr_obj_bene_t.fin_object_cd = ld_bcnstr_month_t.fin_object_cd\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.univ_fiscal_yr = ld_lbr_obj_bene_t.univ_fiscal_yr\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.fin_coa_cd = ld_lbr_obj_bene_t.fin_coa_cd\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.pos_benefit_typ_cd = ld_lbr_obj_bene_t.finobj_bene_typ_cd\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.lbr_ben_rt_cat_cd = ?\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.univ_fiscal_yr = CA_OBJECT_CODE_T.univ_fiscal_yr\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.fin_coa_cd = CA_OBJECT_CODE_T.fin_coa_cd\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.pos_frngben_obj_cd = CA_OBJECT_CODE_T.fin_object_cd\n");
        sqlBuilder.append("  AND ld_lbr_obj_bene_t.actv_ind = ?\n");
        sqlBuilder.append("  AND ld_benefits_calc_t.actv_ind = ?\n");
        sqlBuilder.append("GROUP BY ld_benefits_calc_t.pos_frngben_obj_cd, CA_OBJECT_CODE_T.fin_obj_typ_cd");
        sqlMonthlySteps.add(new SQLForStep(sqlBuilder, insertionPoints));
        sqlBuilder.delete(0, sqlBuilder.length());
        insertionPoints.clear();


        /**
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
        sqlBuilder.append("    WHERE ld_bcnstr_month_t.fdoc_nbr = ld_pnd_bcnstr_gl_t.fdoc_nbr\n");
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
        sqlBuilder.append("          AND ld_benefits_calc_t.lbr_ben_rt_cat_cd = ?\n");
        sqlBuilder.append("          AND ld_benefits_calc_t.pos_frngben_obj_cd = ld_bcnstr_month_t.fin_object_cd\n");
        sqlBuilder.append("          AND ld_benefits_calc_t.actv_ind = ?)\n");
        sqlMonthlySteps.add(new SQLForStep(sqlBuilder));

    }


    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BenefitsCalculationDao#calculateAnnualBudgetConstructionGeneralLedgerBenefits(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.ArrayList)
     */
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd, String expenditureINList){

        // original non rate category code version

        // the first thing to do is get the SQL IN list of expenditure object code types allowed in budget construction.
        // if this parameter is ill-formed, we can't calculate benefits. we will blow the user out of the water as a consequence.
        // if the benefits portion of budget construction is not in use at a particular site, then doing it this way will have no
        // impact.

        ArrayList<String> stringsToInsert = new ArrayList<String>();
        stringsToInsert.add(KFSConstants.getDashFinancialSubObjectCode());
        stringsToInsert.add(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        stringsToInsert.add(expenditureINList);
        String idForSession = UUID.randomUUID().toString();

        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(0).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(1).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(2).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(3).getSQL(), idForSession, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, KFSConstants.ACTIVE_INDICATOR, KFSConstants.ACTIVE_INDICATOR);
        // re-set general ledger amount for existing fringe benefits object codes
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(4).getSQL(stringsToInsert), idForSession, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, idForSession);
        // insert general ledger lines for new fringe benefits object codes.
        stringsToInsert.add(2, stringsToInsert.get(0));
        stringsToInsert.add(3, stringsToInsert.get(1));
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(5).getSQL(stringsToInsert), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, idForSession, fiscalYear, chartOfAccounts, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        clearTempTableBySesId("LD_BCN_BENEFITS_RECALC01_MT", "SESID", idForSession);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BenefitsCalculationDao#calculateAnnualBudgetConstructionGeneralLedgerBenefits(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.ArrayList, java.lang.String)
     */
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd, String expenditureINList, String laborBenefitRateCategoryCode) {

        // new rate category code version

        // the first thing to do is get the SQL IN list of expenditure object code types allowed in budget construction.
        // if this parameter is ill-formed, we can't calculate benefits. we will blow the user out of the water as a consequence.
        // if the benefits portion of budget construction is not in use at a particular site, then doing it this way will have no
        // impact.

        ArrayList<String> stringsToInsert = new ArrayList<String>();
        stringsToInsert.add(KFSConstants.getDashFinancialSubObjectCode());
        stringsToInsert.add(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        stringsToInsert.add(expenditureINList);
        String idForSession = UUID.randomUUID().toString();

        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(0).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(1).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(6).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, laborBenefitRateCategoryCode);
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(7).getSQL(), idForSession, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, laborBenefitRateCategoryCode, KFSConstants.ACTIVE_INDICATOR, KFSConstants.ACTIVE_INDICATOR);
        // re-set general ledger amount for existing fringe benefits object codes
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(4).getSQL(stringsToInsert), idForSession, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, idForSession);
        // insert general ledger lines for new fringe benefits object codes.
        stringsToInsert.add(2, stringsToInsert.get(0));
        stringsToInsert.add(3, stringsToInsert.get(1));
        getSimpleJdbcTemplate().update(sqlAnnualSteps.get(5).getSQL(stringsToInsert), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, idForSession, fiscalYear, chartOfAccounts, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        clearTempTableBySesId("LD_BCN_BENEFITS_RECALC01_MT", "SESID", idForSession);
        /**
         * this is necessary to clear any rows for the tables we have just updated from the OJB cache. otherwise, subsequent calls
         * to OJB will fetch the old, unupdated cached rows.
         */
        // persistenceService.clearCache();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BenefitsCalculationDao#calculateMonthlyBudgetConstructionGeneralLedgerBenefits(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd) {

        // original non rate category code version

        String idForSession = UUID.randomUUID().toString();

        ArrayList<String> stringsToInsert = new ArrayList<String>();
        stringsToInsert.add(KFSConstants.getDashFinancialSubObjectCode());
        stringsToInsert.add(KFSConstants.BALANCE_TYPE_BASE_BUDGET);

        // get rid of monthly buckets for any rows with annual zero request
        getSimpleJdbcTemplate().update(sqlMonthlySteps.get(0).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        // get rid of existing monthly budgets for this key
        getSimpleJdbcTemplate().update(sqlMonthlySteps.get(1).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, fiscalYear, chartOfAccounts);
        // spread the budgeted general ledger fringe beneftis amounts for this key equally into the twelve months
        getSimpleJdbcTemplate().update(sqlMonthlySteps.get(2).getSQL(stringsToInsert), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, KFSConstants.ACTIVE_INDICATOR, KFSConstants.ACTIVE_INDICATOR);
        // add any rounding errors to the first month
        getSimpleJdbcTemplate().update(sqlMonthlySteps.get(3).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, fiscalYear, chartOfAccounts, KFSConstants.ACTIVE_INDICATOR);
    }


    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BenefitsCalculationDao#calculateMonthlyBudgetConstructionGeneralLedgerBenefits(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     * @@ -420,6 +615,31 @@ persistenceService.clearCache(); } /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BenefitsCalculationDao#calculateMonthlyBudgetConstructionGeneralLedgerBenefits(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd, String laborBenefitRateCategoryCode) {

        // new rate category code version

        String idForSession = UUID.randomUUID().toString();

        ArrayList<String> stringsToInsert = new ArrayList<String>();
        stringsToInsert.add(KFSConstants.getDashFinancialSubObjectCode());
        stringsToInsert.add(KFSConstants.BALANCE_TYPE_BASE_BUDGET);

        // get rid of monthly buckets for any rows with annual zero request
        getSimpleJdbcTemplate().update(sqlMonthlySteps.get(0).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
        // get rid of existing monthly budgets for this key
        getSimpleJdbcTemplate().update(sqlMonthlySteps.get(1).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, fiscalYear, chartOfAccounts);
        // spread the budgeted general ledger fringe beneftis amounts for this key equally into the twelve months
        getSimpleJdbcTemplate().update(sqlMonthlySteps.get(4).getSQL(stringsToInsert), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, laborBenefitRateCategoryCode, KFSConstants.ACTIVE_INDICATOR, KFSConstants.ACTIVE_INDICATOR);
        // add any rounding errors to the first month
        getSimpleJdbcTemplate().update(sqlMonthlySteps.get(5).getSQL(), documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, fiscalYear, chartOfAccounts, laborBenefitRateCategoryCode, KFSConstants.ACTIVE_INDICATOR);
        /**
         * this is necessary to clear any rows for the tables we have just updated from the OJB cache. otherwise, subsequent calls
         * to OJB will fetch the old, unupdated cached rows.
         */
        //persistenceService.clearCache();
    }
}
