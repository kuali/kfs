/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionObjectSummaryReportDao;

public class BudgetConstructionObjectSummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionObjectSummaryReportDao {

    protected static ArrayList<SQLForStep> objectSummarySql = new ArrayList<SQLForStep>(5);

    public BudgetConstructionObjectSummaryReportDaoJdbc() {

        StringBuilder sqlBuilder = new StringBuilder(1500);
        ArrayList<Integer> insertionPoints = new ArrayList<Integer>(10);


       // build the INSERT SQL for the main table
       sqlBuilder.append("INSERT INTO LD_BCN_OBJT_SUMM_T\n");
       sqlBuilder.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD,\n");
       sqlBuilder.append("INC_EXP_CD, FIN_CONS_SORT_CD, FIN_LEV_SORT_CD, FIN_OBJECT_CD, ACLN_ANNL_BAL_AMT,\n");
       sqlBuilder.append("FIN_BEG_BAL_LN_AMT, FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, APPT_RQCSF_FTE_QTY,\n");
       sqlBuilder.append("APPT_RQST_FTE_QTY, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY)\n");
       sqlBuilder.append("SELECT\n");
       sqlBuilder.append("?,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_ORG_FIN_COA,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_ORG_CD,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_SUB_FUND_GRP,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.FIN_COA_CD,'A',\n");
       sqlBuilder.append("CA_OBJ_CONSOLDTN_T.FIN_REPORT_SORT_CD,\n");
       sqlBuilder.append("CA_OBJ_LEVEL_T.FIN_REPORT_SORT_CD,\n");
       sqlBuilder.append("LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD,\n");
       sqlBuilder.append("sum(LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT),\n");
       sqlBuilder.append("sum(LD_PND_BCNSTR_GL_T.FIN_BEG_BAL_LN_AMT),\n");
       sqlBuilder.append("CA_OBJ_LEVEL_T.FIN_CONS_OBJ_CD,\n");
       sqlBuilder.append("CA_OBJECT_CODE_T.FIN_OBJ_LEVEL_CD,\n");
       sqlBuilder.append("0,0,0,0\n");
       sqlBuilder.append("   FROM LD_BCN_SUBFUND_PICK_T,\n");
       sqlBuilder.append("        LD_BCN_CTRL_LIST_T,\n");
       sqlBuilder.append("        LD_PND_BCNSTR_GL_T,\n");
       sqlBuilder.append("        CA_OBJECT_CODE_T,\n");
       sqlBuilder.append("        CA_OBJ_LEVEL_T,\n");
       sqlBuilder.append("        CA_OBJ_CONSOLDTN_T\n");
       sqlBuilder.append("  WHERE (LD_BCN_SUBFUND_PICK_T.PERSON_UNVL_ID = ?)\n");
       sqlBuilder.append("    AND (LD_BCN_SUBFUND_PICK_T.REPORT_FLAG > 0)\n");
       sqlBuilder.append("    AND (LD_BCN_SUBFUND_PICK_T.SUB_FUND_GRP_CD = LD_BCN_CTRL_LIST_T.SEL_SUB_FUND_GRP)\n");
       sqlBuilder.append("    AND (LD_BCN_SUBFUND_PICK_T.PERSON_UNVL_ID = LD_BCN_CTRL_LIST_T.PERSON_UNVL_ID)\n");
       sqlBuilder.append("    AND (CA_OBJ_CONSOLDTN_T.FIN_COA_CD = CA_OBJ_LEVEL_T.FIN_COA_CD)\n");
       sqlBuilder.append("    AND (CA_OBJ_CONSOLDTN_T.FIN_CONS_OBJ_CD = CA_OBJ_LEVEL_T.FIN_CONS_OBJ_CD)\n");
       sqlBuilder.append("    AND (LD_PND_BCNSTR_GL_T.FDOC_NBR = LD_BCN_CTRL_LIST_T.FDOC_NBR)\n");
       sqlBuilder.append("    AND (CA_OBJECT_CODE_T.UNIV_FISCAL_YR = LD_BCN_CTRL_LIST_T.UNIV_FISCAL_YR)\n");
       sqlBuilder.append("    AND (CA_OBJECT_CODE_T.FIN_COA_CD = LD_BCN_CTRL_LIST_T.FIN_COA_CD)\n");
       sqlBuilder.append("    AND (CA_OBJECT_CODE_T.FIN_OBJECT_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)\n");
       sqlBuilder.append("    AND (CA_OBJ_LEVEL_T.FIN_COA_CD = CA_OBJECT_CODE_T.FIN_COA_CD)\n");
       sqlBuilder.append("    AND (CA_OBJ_LEVEL_T.FIN_OBJ_LEVEL_CD = CA_OBJECT_CODE_T.FIN_OBJ_LEVEL_CD)\n");
       sqlBuilder.append("    AND (LD_PND_BCNSTR_GL_T.FIN_OBJ_TYP_CD IN ");
       // income object type IN list
       insertionPoints.add(sqlBuilder.length());
       sqlBuilder.append(")\n");
       sqlBuilder.append("GROUP BY LD_BCN_CTRL_LIST_T.SEL_ORG_FIN_COA,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_ORG_CD,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_SUB_FUND_GRP,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.FIN_COA_CD,\n");
       sqlBuilder.append("CA_OBJ_CONSOLDTN_T.FIN_REPORT_SORT_CD,\n");
       sqlBuilder.append("CA_OBJ_LEVEL_T.FIN_REPORT_SORT_CD,\n");
       sqlBuilder.append("LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD,\n");
       sqlBuilder.append("CA_OBJ_LEVEL_T.FIN_CONS_OBJ_CD,\n");
       sqlBuilder.append("CA_OBJECT_CODE_T.FIN_OBJ_LEVEL_CD\n");
       sqlBuilder.append("UNION ALL\n");
       sqlBuilder.append("SELECT\n");
       sqlBuilder.append("?,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_ORG_FIN_COA,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_ORG_CD,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_SUB_FUND_GRP,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.FIN_COA_CD,");
       sqlBuilder.append("'B',\n");
       sqlBuilder.append("CA_OBJ_CONSOLDTN_T.FIN_REPORT_SORT_CD,\n");
       sqlBuilder.append("CA_OBJ_LEVEL_T.FIN_REPORT_SORT_CD,\n");
       sqlBuilder.append("LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD,\n");
       sqlBuilder.append("sum(LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT),\n");
       sqlBuilder.append("sum(LD_PND_BCNSTR_GL_T.FIN_BEG_BAL_LN_AMT),\n");
       sqlBuilder.append("CA_OBJ_LEVEL_T.FIN_CONS_OBJ_CD,\n");
       sqlBuilder.append("CA_OBJECT_CODE_T.FIN_OBJ_LEVEL_CD,\n");
       sqlBuilder.append("0,0,0,0\n");
       sqlBuilder.append("   FROM LD_BCN_SUBFUND_PICK_T,\n");
       sqlBuilder.append("        LD_BCN_CTRL_LIST_T,\n");
       sqlBuilder.append("        LD_PND_BCNSTR_GL_T,\n");
       sqlBuilder.append("        CA_OBJECT_CODE_T,\n");
       sqlBuilder.append("        CA_OBJ_LEVEL_T,\n");
       sqlBuilder.append("        CA_OBJ_CONSOLDTN_T\n");
       sqlBuilder.append("  WHERE (LD_BCN_SUBFUND_PICK_T.PERSON_UNVL_ID = ?)\n");
       sqlBuilder.append("    AND (LD_BCN_SUBFUND_PICK_T.REPORT_FLAG > 0)\n");
       sqlBuilder.append("    AND (LD_BCN_SUBFUND_PICK_T.SUB_FUND_GRP_CD = LD_BCN_CTRL_LIST_T.SEL_SUB_FUND_GRP)\n");
       sqlBuilder.append("    AND (LD_BCN_SUBFUND_PICK_T.PERSON_UNVL_ID = LD_BCN_CTRL_LIST_T.PERSON_UNVL_ID)\n");
       sqlBuilder.append("    AND (CA_OBJ_CONSOLDTN_T.FIN_COA_CD = CA_OBJ_LEVEL_T.FIN_COA_CD)\n");
       sqlBuilder.append("    AND (CA_OBJ_CONSOLDTN_T.FIN_CONS_OBJ_CD = CA_OBJ_LEVEL_T.FIN_CONS_OBJ_CD)\n");
       sqlBuilder.append("    AND (LD_PND_BCNSTR_GL_T.FDOC_NBR = LD_BCN_CTRL_LIST_T.FDOC_NBR)\n");
       sqlBuilder.append("    AND (CA_OBJECT_CODE_T.UNIV_FISCAL_YR = LD_BCN_CTRL_LIST_T.UNIV_FISCAL_YR)\n");
       sqlBuilder.append("    AND (CA_OBJECT_CODE_T.FIN_COA_CD = LD_BCN_CTRL_LIST_T.FIN_COA_CD)\n");
       sqlBuilder.append("    AND (CA_OBJECT_CODE_T.FIN_OBJECT_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)\n");
       sqlBuilder.append("    AND (CA_OBJ_LEVEL_T.FIN_COA_CD = CA_OBJECT_CODE_T.FIN_COA_CD)\n");
       sqlBuilder.append("    AND (CA_OBJ_LEVEL_T.FIN_OBJ_LEVEL_CD = CA_OBJECT_CODE_T.FIN_OBJ_LEVEL_CD)\n");
       sqlBuilder.append("    AND (LD_PND_BCNSTR_GL_T.FIN_OBJ_TYP_CD IN ");
       // expenditure object type IN list
       insertionPoints.add(sqlBuilder.length());
       sqlBuilder.append(")\n");
       sqlBuilder.append("GROUP BY LD_BCN_CTRL_LIST_T.SEL_ORG_FIN_COA,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_ORG_CD,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.SEL_SUB_FUND_GRP,\n");
       sqlBuilder.append("LD_BCN_CTRL_LIST_T.FIN_COA_CD,\n");
       sqlBuilder.append("CA_OBJ_CONSOLDTN_T.FIN_REPORT_SORT_CD,\n");
       sqlBuilder.append("CA_OBJ_LEVEL_T.FIN_REPORT_SORT_CD,\n");
       sqlBuilder.append("LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD,\n");
       sqlBuilder.append("CA_OBJ_LEVEL_T.FIN_CONS_OBJ_CD,\n");
       sqlBuilder.append("CA_OBJECT_CODE_T.FIN_OBJ_LEVEL_CD\n");

       objectSummarySql.add(new SQLForStep(sqlBuilder,insertionPoints));
       sqlBuilder.delete(0,sqlBuilder.length());
       insertionPoints.clear();

       // SQL to get the FTE amounts from appointment funding that match with the expenditure
      sqlBuilder.append("INSERT INTO LD_BCN_BUILD_OBJTSUMM01_MT\n");
      sqlBuilder.append("(SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD,\n");
      sqlBuilder.append(" INC_EXP_CD, FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, FIN_OBJECT_CD,\n");
      sqlBuilder.append(" APPT_RQCSF_FTE_QTY, APPT_RQST_FTE_QTY)\n");
      sqlBuilder.append("(SELECT\n");
      sqlBuilder.append(" ?,\n");
      sqlBuilder.append(" ctrl.sel_org_fin_coa,\n");
      sqlBuilder.append(" ctrl.sel_org_cd,\n");
      sqlBuilder.append(" ctrl.sel_sub_fund_grp,\n");
      sqlBuilder.append(" ctrl.fin_coa_cd,\n");
      sqlBuilder.append(" 'B',\n");
      sqlBuilder.append( "objl.fin_cons_obj_cd,\n");
      sqlBuilder.append(" objt.fin_obj_level_cd,\n");
      sqlBuilder.append(" bcaf.fin_object_cd,\n");
      sqlBuilder.append(" SUM(bcaf.appt_rqcsf_fte_qty),\n");
      sqlBuilder.append(" SUM(bcaf.appt_rqst_fte_qty)\n");
      sqlBuilder.append(" FROM LD_BCN_SUBFUND_PICK_T pick,\n");
      sqlBuilder.append("      LD_BCN_CTRL_LIST_T ctrl,\n");
      sqlBuilder.append("      LD_PNDBC_APPTFND_T bcaf,\n");
      sqlBuilder.append("      CA_OBJECT_CODE_T objt,\n");
      sqlBuilder.append("      CA_OBJ_LEVEL_T objl\n");
      sqlBuilder.append(" WHERE pick.person_unvl_id = ?\n");
      sqlBuilder.append("   AND pick.report_flag > 0\n");
      sqlBuilder.append("   AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp\n");
      sqlBuilder.append("   AND pick.person_unvl_id = ctrl.person_unvl_id\n");
      sqlBuilder.append("   AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr\n");
      sqlBuilder.append("   AND bcaf.fin_coa_cd = ctrl.fin_coa_cd\n");
      sqlBuilder.append("   AND bcaf.account_nbr = ctrl.account_nbr\n");
      sqlBuilder.append("   AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr\n");
      sqlBuilder.append("   AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr\n");
      sqlBuilder.append("   AND objt.fin_coa_cd = ctrl.fin_coa_cd\n");
      sqlBuilder.append("   AND objt.fin_object_cd = bcaf.fin_object_cd\n");
      sqlBuilder.append("   AND objl.fin_coa_cd = objt.fin_coa_cd\n");
      sqlBuilder.append("   AND objl.fin_obj_level_cd = objt.fin_obj_level_cd\n");
      sqlBuilder.append(" GROUP BY ctrl.sel_org_fin_coa,\n");
      sqlBuilder.append("          ctrl.sel_org_cd,\n");
      sqlBuilder.append("          ctrl.sel_sub_fund_grp,\n");
      sqlBuilder.append("          ctrl.fin_coa_cd,\n");
      sqlBuilder.append("          objl.fin_cons_obj_cd,\n");
      sqlBuilder.append("          objt.fin_obj_level_cd,\n");
      sqlBuilder.append("          bcaf.fin_object_cd)");
      objectSummarySql.add(new SQLForStep(sqlBuilder));
      sqlBuilder.delete(0,sqlBuilder.length());

      // update the original lines using the FTE generated above. (PostGreSQL supposedly does not allow the target table in an UPDATE to be aliased.  Gennick, p.159.)
      sqlBuilder.append("UPDATE LD_BCN_OBJT_SUMM_T\n");
      sqlBuilder.append("SET appt_rqcsf_fte_qty =\n");
      sqlBuilder.append("  (SELECT SUM(fq.appt_rqcsf_fte_qty)\n");
      sqlBuilder.append("   FROM LD_BCN_BUILD_OBJTSUMM01_MT fq\n");
      sqlBuilder.append("   WHERE LD_BCN_OBJT_SUMM_T.PERSON_UNVL_ID = ?\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.ORG_FIN_COA_CD = fq.org_fin_coa_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.ORG_CD = fq.org_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.SUB_FUND_GRP_CD = fq.sub_fund_grp_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.FIN_COA_CD = fq.fin_coa_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.INC_EXP_CD = fq.inc_exp_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.FIN_CONS_OBJ_CD = fq.fin_cons_obj_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.FIN_OBJ_LEVEL_CD = fq.fin_obj_level_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.FIN_OBJECT_CD = fq.fin_object_cd\n");
      sqlBuilder.append("     AND fq.sesid = ?),\n");
      sqlBuilder.append("   appt_rqst_fte_qty =\n");
      sqlBuilder.append("  (SELECT  SUM(fq.appt_rqst_fte_qty)\n");
      sqlBuilder.append("   FROM LD_BCN_BUILD_OBJTSUMM01_MT fq\n");
      sqlBuilder.append("   WHERE LD_BCN_OBJT_SUMM_T.PERSON_UNVL_ID = ?\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.ORG_FIN_COA_CD = fq.org_fin_coa_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.ORG_CD = fq.org_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.SUB_FUND_GRP_CD = fq.sub_fund_grp_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.FIN_COA_CD = fq.fin_coa_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.INC_EXP_CD = fq.inc_exp_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.FIN_CONS_OBJ_CD = fq.fin_cons_obj_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.FIN_OBJ_LEVEL_CD = fq.fin_obj_level_cd\n");
      sqlBuilder.append("     AND LD_BCN_OBJT_SUMM_T.FIN_OBJECT_CD = fq.fin_object_cd\n");
      sqlBuilder.append("     AND fq.sesid = ?)\n");
      sqlBuilder.append("WHERE LD_BCN_OBJT_SUMM_T.PERSON_UNVL_ID = ?\n");
      sqlBuilder.append("  AND EXISTS (SELECT 1\n");
      sqlBuilder.append("              FROM LD_BCN_BUILD_OBJTSUMM01_MT fq2\n");
      sqlBuilder.append("              WHERE LD_BCN_OBJT_SUMM_T.PERSON_UNVL_ID = ?\n");
      sqlBuilder.append("                AND LD_BCN_OBJT_SUMM_T.ORG_FIN_COA_CD = fq2.org_fin_coa_cd\n");
      sqlBuilder.append("                AND LD_BCN_OBJT_SUMM_T.ORG_CD = fq2.org_cd\n");
      sqlBuilder.append("                AND LD_BCN_OBJT_SUMM_T.SUB_FUND_GRP_CD = fq2.sub_fund_grp_cd\n");
      sqlBuilder.append("                AND LD_BCN_OBJT_SUMM_T.FIN_COA_CD = fq2.fin_coa_cd\n");
      sqlBuilder.append("                AND LD_BCN_OBJT_SUMM_T.INC_EXP_CD = fq2.inc_exp_cd\n");
      sqlBuilder.append("                AND LD_BCN_OBJT_SUMM_T.FIN_CONS_OBJ_CD = fq2.fin_cons_obj_cd\n");
      sqlBuilder.append("                AND LD_BCN_OBJT_SUMM_T.FIN_OBJ_LEVEL_CD = fq2.fin_obj_level_cd\n");
      sqlBuilder.append("                AND LD_BCN_OBJT_SUMM_T.FIN_OBJECT_CD = fq2.fin_object_cd\n");
      sqlBuilder.append("                AND fq2.sesid = ?)");

      objectSummarySql.add(new SQLForStep(sqlBuilder));
      sqlBuilder.delete(0,sqlBuilder.length());

      // sum the base (CSF for the current year) FTE into a holding table
      sqlBuilder.append("INSERT INTO LD_BCN_BUILD_OBJTSUMM02_MT\n");
      sqlBuilder.append("(SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD,\n");
      sqlBuilder.append(" FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, FIN_OBJECT_CD, POS_CSF_FNDSTAT_CD,\n");
      sqlBuilder.append(" POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY)\n");
      sqlBuilder.append("SELECT\n");
      sqlBuilder.append("  ?,\n");
      sqlBuilder.append("  ctrl.sel_org_fin_coa,\n");
      sqlBuilder.append("  ctrl.sel_org_cd,\n");
      sqlBuilder.append("  ctrl.sel_sub_fund_grp,\n");
      sqlBuilder.append("  ctrl.fin_coa_cd,\n");
      sqlBuilder.append("  'B',\n");
      sqlBuilder.append("  objl.fin_cons_obj_cd,\n");
      sqlBuilder.append("  objt.fin_obj_level_cd,\n");
      sqlBuilder.append("  bcsf.fin_object_cd,\n");
      sqlBuilder.append("  NULL,\n");
      sqlBuilder.append("  SUM(bcsf.pos_csf_fte_qty),\n");
      sqlBuilder.append("  0\n");
      sqlBuilder.append("FROM LD_BCN_SUBFUND_PICK_T pick,\n");
      sqlBuilder.append("  LD_BCN_CTRL_LIST_T ctrl,\n");
      sqlBuilder.append("  LD_BCN_CSF_TRCKR_T bcsf,\n");
      sqlBuilder.append("  CA_OBJECT_CODE_T objt,\n");
      sqlBuilder.append("  CA_OBJ_LEVEL_T objl\n");
      sqlBuilder.append("WHERE pick.person_unvl_id = ?\n");
      sqlBuilder.append("  AND pick.report_flag > 0\n");
      sqlBuilder.append("  AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp\n");
      sqlBuilder.append("  AND pick.person_unvl_id = ctrl.person_unvl_id\n");
      sqlBuilder.append("  AND bcsf.univ_fiscal_yr = ctrl.univ_fiscal_yr\n");
      sqlBuilder.append("  AND bcsf.fin_coa_cd = ctrl.fin_coa_cd\n");
      sqlBuilder.append("  AND bcsf.account_nbr = ctrl.account_nbr\n");
      sqlBuilder.append("  AND bcsf.sub_acct_nbr = ctrl.sub_acct_nbr\n");
      sqlBuilder.append("  AND (bcsf.pos_csf_fndstat_cd <> '");
      // CSF LEAVE funding status
      insertionPoints.add(sqlBuilder.length());
      sqlBuilder.append("' OR bcsf.pos_csf_fndstat_cd IS NULL)\n");
      sqlBuilder.append("  AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr\n");
      sqlBuilder.append("  AND objt.fin_coa_cd = ctrl.fin_coa_cd\n");
      sqlBuilder.append("  AND objt.fin_object_cd = bcsf.fin_object_cd\n");
      sqlBuilder.append("  AND objl.fin_coa_cd = objt.fin_coa_cd\n");
      sqlBuilder.append("  AND objl.fin_obj_level_cd = objt.fin_obj_level_cd\n");
      sqlBuilder.append("GROUP BY ctrl.sel_org_fin_coa,\n");
      sqlBuilder.append("         ctrl.sel_org_cd,\n");
      sqlBuilder.append("         ctrl.sel_sub_fund_grp,\n");
      sqlBuilder.append("         ctrl.fin_coa_cd,\n");
      sqlBuilder.append("         objl.fin_cons_obj_cd,\n");
      sqlBuilder.append("         objt.fin_obj_level_cd,\n");
      sqlBuilder.append("         bcsf.fin_object_cd\n");
      sqlBuilder.append("UNION ALL\n");
      sqlBuilder.append("SELECT\n");
      sqlBuilder.append("?,\n");
      sqlBuilder.append("ctrl.sel_org_fin_coa,\n");
      sqlBuilder.append("ctrl.sel_org_cd,\n");
      sqlBuilder.append("ctrl.sel_sub_fund_grp,\n");
      sqlBuilder.append("ctrl.fin_coa_cd,\n");
      sqlBuilder.append("'B',\n");
      sqlBuilder.append("objl.fin_cons_obj_cd,\n");
      sqlBuilder.append("objt.fin_obj_level_cd,\n");
      sqlBuilder.append("bcsf.fin_object_cd,\n");
      sqlBuilder.append("'");
      // CSF LEAVE funding status
      insertionPoints.add(sqlBuilder.length());
      sqlBuilder.append("',\n");
      sqlBuilder.append("0,\n");
      sqlBuilder.append("    SUM(bcsf.pos_csf_fte_qty)\n");
      sqlBuilder.append("FROM LD_BCN_SUBFUND_PICK_T pick,\n");
      sqlBuilder.append("    LD_BCN_CTRL_LIST_T ctrl,\n");
      sqlBuilder.append("    LD_BCN_CSF_TRCKR_T bcsf,\n");
      sqlBuilder.append("    CA_OBJECT_CODE_T objt,\n");
      sqlBuilder.append("    CA_OBJ_LEVEL_T objl\n");
      sqlBuilder.append("WHERE pick.person_unvl_id = ?\n");
      sqlBuilder.append("  AND pick.report_flag > 0\n");
      sqlBuilder.append("  AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp\n");
      sqlBuilder.append("  AND pick.person_unvl_id = ctrl.person_unvl_id\n");
      sqlBuilder.append("  AND bcsf.univ_fiscal_yr = ctrl.univ_fiscal_yr\n");
      sqlBuilder.append("  AND bcsf.fin_coa_cd = ctrl.fin_coa_cd\n");
      sqlBuilder.append("  AND bcsf.account_nbr = ctrl.account_nbr\n");
      sqlBuilder.append("  AND bcsf.sub_acct_nbr = ctrl.sub_acct_nbr\n");
      sqlBuilder.append("  AND bcsf.pos_csf_fndstat_cd = '");
      // CSF LEAVE funding status
      insertionPoints.add(sqlBuilder.length());
      sqlBuilder.append("'\n");
      sqlBuilder.append("  AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr\n");
      sqlBuilder.append("  AND objt.fin_coa_cd = ctrl.fin_coa_cd\n");
      sqlBuilder.append("  AND objt.fin_object_cd = bcsf.fin_object_cd\n");
      sqlBuilder.append("  AND objl.fin_coa_cd = objt.fin_coa_cd\n");
      sqlBuilder.append("  AND objl.fin_obj_level_cd = objt.fin_obj_level_cd\n");
      sqlBuilder.append("GROUP BY ctrl.sel_org_fin_coa,\n");
      sqlBuilder.append("    ctrl.sel_org_cd,\n");
      sqlBuilder.append("    ctrl.sel_sub_fund_grp,\n");
      sqlBuilder.append("    ctrl.fin_coa_cd,\n");
      sqlBuilder.append("    objl.fin_cons_obj_cd,\n");
      sqlBuilder.append("    objt.fin_obj_level_cd,\n");
      sqlBuilder.append("    bcsf.fin_object_cd\n");

      objectSummarySql.add(new SQLForStep(sqlBuilder,insertionPoints));
      sqlBuilder.delete(0,sqlBuilder.length());
      insertionPoints.clear();

      // update the base FTE in the reporting table using the holding table values. (PostGreSQL supposedly does not allow the target table in an UPDATE to be aliased.  Gennick, p.159.)
      sqlBuilder.append("UPDATE LD_BCN_OBJT_SUMM_T\n");
      sqlBuilder.append("SET LD_BCN_OBJT_SUMM_T.POS_CSF_FTE_QTY =\n");
      sqlBuilder.append("        (SELECT SUM(fq.pos_csf_fte_qty)\n");
      sqlBuilder.append("         FROM LD_BCN_BUILD_OBJTSUMM02_MT fq\n");
      sqlBuilder.append("         WHERE LD_BCN_OBJT_SUMM_T.PERSON_UNVL_ID = ?\n");
      sqlBuilder.append("           AND LD_BCN_OBJT_SUMM_T.ORG_FIN_COA_CD = fq.org_fin_coa_cd\n");
      sqlBuilder.append("           AND LD_BCN_OBJT_SUMM_T.ORG_CD = fq.org_cd\n");
      sqlBuilder.append("           AND LD_BCN_OBJT_SUMM_T.SUB_FUND_GRP_CD = fq.sub_fund_grp_cd\n");
      sqlBuilder.append("           AND LD_BCN_OBJT_SUMM_T.FIN_COA_CD = fq.fin_coa_cd\n");
      sqlBuilder.append("           AND LD_BCN_OBJT_SUMM_T.INC_EXP_CD = fq.inc_exp_cd\n");
      sqlBuilder.append("           AND LD_BCN_OBJT_SUMM_T.FIN_CONS_OBJ_CD = fq.fin_cons_obj_cd\n");
      sqlBuilder.append("           AND LD_BCN_OBJT_SUMM_T.FIN_OBJ_LEVEL_CD = fq.fin_obj_level_cd\n");
      sqlBuilder.append("           AND LD_BCN_OBJT_SUMM_T.FIN_OBJECT_CD = fq.fin_object_cd\n");
      sqlBuilder.append("           AND fq.sesid = ?),\n");
      sqlBuilder.append("     LD_BCN_OBJT_SUMM_T.POS_CSF_LV_FTE_QTY =\n");
      sqlBuilder.append("         (SELECT SUM(fq.pos_csf_lv_fte_qty)\n");
      sqlBuilder.append("          FROM LD_BCN_BUILD_OBJTSUMM02_MT fq\n");
      sqlBuilder.append("          WHERE LD_BCN_OBJT_SUMM_T.PERSON_UNVL_ID = ?\n");
      sqlBuilder.append("            AND LD_BCN_OBJT_SUMM_T.ORG_FIN_COA_CD = fq.org_fin_coa_cd\n");
      sqlBuilder.append("            AND LD_BCN_OBJT_SUMM_T.ORG_CD = fq.org_cd\n");
      sqlBuilder.append("            AND LD_BCN_OBJT_SUMM_T.SUB_FUND_GRP_CD = fq.sub_fund_grp_cd\n");
      sqlBuilder.append("            AND LD_BCN_OBJT_SUMM_T.FIN_COA_CD = fq.fin_coa_cd\n");
      sqlBuilder.append("            AND LD_BCN_OBJT_SUMM_T.INC_EXP_CD = fq.inc_exp_cd\n");
      sqlBuilder.append("            AND LD_BCN_OBJT_SUMM_T.FIN_CONS_OBJ_CD = fq.fin_cons_obj_cd\n");
      sqlBuilder.append("            AND LD_BCN_OBJT_SUMM_T.FIN_OBJ_LEVEL_CD = fq.fin_obj_level_cd\n");
      sqlBuilder.append("            AND LD_BCN_OBJT_SUMM_T.FIN_OBJECT_CD = fq.fin_object_cd\n");
      sqlBuilder.append("            AND fq.sesid = ?)\n");
      sqlBuilder.append("    WHERE LD_BCN_OBJT_SUMM_T.PERSON_UNVL_ID = ?\n");
      sqlBuilder.append("      AND EXISTS (SELECT 1\n");
      sqlBuilder.append("                  FROM LD_BCN_BUILD_OBJTSUMM02_MT fq2\n");
      sqlBuilder.append("                  WHERE LD_BCN_OBJT_SUMM_T.PERSON_UNVL_ID = ?\n");
      sqlBuilder.append("                    AND LD_BCN_OBJT_SUMM_T.ORG_FIN_COA_CD = fq2.org_fin_coa_cd\n");
      sqlBuilder.append("                    AND LD_BCN_OBJT_SUMM_T.ORG_CD = fq2.org_cd\n");
      sqlBuilder.append("                    AND LD_BCN_OBJT_SUMM_T.SUB_FUND_GRP_CD = fq2.sub_fund_grp_cd\n");
      sqlBuilder.append("                    AND LD_BCN_OBJT_SUMM_T.FIN_COA_CD = fq2.fin_coa_cd\n");
      sqlBuilder.append("                    AND LD_BCN_OBJT_SUMM_T.INC_EXP_CD = fq2.inc_exp_cd\n");
      sqlBuilder.append("                    AND LD_BCN_OBJT_SUMM_T.FIN_CONS_OBJ_CD = fq2.fin_cons_obj_cd\n");
      sqlBuilder.append("                    AND LD_BCN_OBJT_SUMM_T.FIN_OBJ_LEVEL_CD = fq2.fin_obj_level_cd\n");
      sqlBuilder.append("                    AND LD_BCN_OBJT_SUMM_T.FIN_OBJECT_CD = fq2.fin_object_cd\n");
      sqlBuilder.append("                    AND fq2.sesid = ?)");

        objectSummarySql.add(new SQLForStep(sqlBuilder));
        sqlBuilder.delete(0, sqlBuilder.length());


    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionObjectSummaryReportDao#cleanGeneralLedgerObjectSummaryTable(java.lang.String)
     */
    @Override
    public void cleanGeneralLedgerObjectSummaryTable(String principalName) {
        this.clearTempTableByUnvlId("LD_BCN_OBJT_SUMM_T", "PERSON_UNVL_ID", principalName);
    }


    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionObjectSummaryReportDao#updateGeneralLedgerObjectSummaryTable(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void updateGeneralLedgerObjectSummaryTable(String principalName, String revenueINList, String expenditureINList) {
        String idForSession = java.util.UUID.randomUUID().toString();
        ArrayList<String> inLists = new ArrayList<String>(2);
        inLists.add(revenueINList);
        inLists.add(expenditureINList);

        // get rid of anything left over from the last time this user ran this report
        cleanGeneralLedgerObjectSummaryTable(principalName);

        // insert the general ledger amounts into the report table, with 0 placeholders for the FTE
        getSimpleJdbcTemplate().update(objectSummarySql.get(0).getSQL(inLists), principalName, principalName, principalName, principalName);

        // sum up the FTE from the appointment funding and stick it in a holding table
        getSimpleJdbcTemplate().update(objectSummarySql.get(1).getSQL(), idForSession, principalName);

        // set the FTE in the report table using the appointment funding FTE from the holding table
        getSimpleJdbcTemplate().update(objectSummarySql.get(2).getSQL(), principalName, idForSession, principalName, idForSession, principalName, principalName, idForSession);

        // sum up the FTE from the CSF tracker (base funding) table and stick it in a holding table
        ArrayList<String> csfLeaveIndicator = new ArrayList<String>(3);
        csfLeaveIndicator.add(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue());
        csfLeaveIndicator.add(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue());
        csfLeaveIndicator.add(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue());
        getSimpleJdbcTemplate().update(objectSummarySql.get(3).getSQL(csfLeaveIndicator), idForSession, principalName, idForSession, principalName);

        // set the CSF FTE in the report table using the FTE from the holding table
        getSimpleJdbcTemplate().update(objectSummarySql.get(4).getSQL(), principalName, idForSession, principalName, idForSession, principalName, principalName, idForSession);

        // clean out this session's rows from the holding tables used
        this.clearTempTableBySesId("LD_BCN_BUILD_OBJTSUMM01_MT", "SESID", idForSession);
        this.clearTempTableBySesId("LD_BCN_BUILD_OBJTSUMM02_MT", "SESID", idForSession);

    }

}
