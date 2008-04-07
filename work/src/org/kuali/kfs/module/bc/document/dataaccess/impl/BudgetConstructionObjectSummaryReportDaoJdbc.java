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
import java.util.List;
import java.lang.StringBuilder;

import org.kuali.core.dbplatform.RawSQL;
import org.kuali.core.util.Guid;


import org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao;
import org.kuali.module.budget.BCConstants;

public class BudgetConstructionObjectSummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionObjectSummaryReportDao {
    
    private static ArrayList<SQLForStep>  objectSummarySql = new ArrayList<SQLForStep>(5);
    
    public BudgetConstructionObjectSummaryReportDaoJdbc()
    {
        
        
        StringBuilder sqlBuilder   = new StringBuilder(1500);
        ArrayList<Integer> insertionPoints = new ArrayList<Integer>(10);
       
       
       // build the INSERT SQL for the main table
       sqlBuilder.append("INSERT INTO ld_bcn_objt_summ_t\n");
       sqlBuilder.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD,\n");
       sqlBuilder.append("INC_EXP_CD, FIN_CONS_SORT_CD, FIN_LEV_SORT_CD, FIN_OBJECT_CD, ACLN_ANNL_BAL_AMT,\n");
       sqlBuilder.append("FIN_BEG_BAL_LN_AMT, FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, APPT_RQCSF_FTE_QTY,\n");
       sqlBuilder.append("APPT_RQST_FTE_QTY, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY)\n");
       sqlBuilder.append("(SELECT\n"); 
       sqlBuilder.append("?,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_org_fin_coa,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_org_cd,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_sub_fund_grp,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.fin_coa_cd,'A',\n");
       sqlBuilder.append("ca_obj_consoldtn_t.fin_report_sort_cd,\n");
       sqlBuilder.append("ca_obj_level_t.fin_report_sort_cd,\n");
       sqlBuilder.append("ld_pnd_bcnstr_gl_t.fin_object_cd,\n");
       sqlBuilder.append("sum(ld_pnd_bcnstr_gl_t.acln_annl_bal_amt),\n");
       sqlBuilder.append("sum(ld_pnd_bcnstr_gl_t.fin_beg_bal_ln_amt),\n");
       sqlBuilder.append("ca_obj_level_t.fin_cons_obj_cd,\n");
       sqlBuilder.append("ca_object_code_t.fin_obj_level_cd,\n");
       sqlBuilder.append("0,0,0,0\n");
       sqlBuilder.append("   FROM ld_bcn_subfund_pick_t,\n");
       sqlBuilder.append("        ld_bcn_ctrl_list_t,\n");
       sqlBuilder.append("        ld_pnd_bcnstr_gl_t,\n");
       sqlBuilder.append("        ca_object_code_t,\n");
       sqlBuilder.append("        ca_obj_level_t,\n");
       sqlBuilder.append("        ca_obj_consoldtn_t\n");
       sqlBuilder.append("  WHERE (ld_bcn_subfund_pick_t.person_unvl_id = ?\n)"); 
       sqlBuilder.append("    AND (ld_bcn_subfund_pick_t.report_flag > 0)\n");
       sqlBuilder.append("    AND (ld_bcn_subfund_pick_t.sub_fund_grp_cd = ld_bcn_ctrl_list_t.sel_sub_fund_grp)\n"); 
       sqlBuilder.append("    AND (ld_bcn_subfund_pick_t.person_unvl_id = ld_bcn_ctrl_list_t.person_unvl_id)\n");
       sqlBuilder.append("    AND (ca_obj_consoldtn_t.fin_coa_cd = ca_obj_level_t.fin_coa_cd)\n");
       sqlBuilder.append("    AND (ca_obj_consoldtn_t.fin_cons_obj_cd = ca_obj_level_t.fin_cons_obj_cd)\n");
       sqlBuilder.append("    AND (ld_pnd_bcnstr_gl_t.fdoc_nbr = ld_bcn_ctrl_list_t.fdoc_nbr)\n");
       sqlBuilder.append("    AND (ca_object_code_t.univ_fiscal_yr = ld_bcn_ctrl_list_t.univ_fiscal_yr)\n");
       sqlBuilder.append("    AND (ca_object_code_t.fin_coa_cd = ld_bcn_ctrl_list_t.fin_coa_cd)\n");
       sqlBuilder.append("    AND (ca_object_code_t.fin_object_cd = ld_pnd_bcnstr_gl_t.fin_object_cd)\n");
       sqlBuilder.append("    AND (ca_obj_level_t.fin_coa_cd = ca_object_code_t.fin_coa_cd)\n");
       sqlBuilder.append("    AND (ca_obj_level_t.fin_obj_level_cd = ca_object_code_t.fin_obj_level_cd)\n");
       sqlBuilder.append("    AND (ld_pnd_bcnstr_gl_t.fin_obj_typ_cd IN ");
       // income object type IN list
       insertionPoints.add(sqlBuilder.length());
       sqlBuilder.append(")\n");
       sqlBuilder.append("GROUP BY ld_bcn_ctrl_list_t.sel_org_fin_coa,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_org_cd,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_sub_fund_grp,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.fin_coa_cd,\n");
       sqlBuilder.append("ca_obj_consoldtn_t.fin_report_sort_cd,\n");
       sqlBuilder.append("ca_obj_level_t.fin_report_sort_cd,\n"); 
       sqlBuilder.append("ld_pnd_bcnstr_gl_t.fin_object_cd,\n");
       sqlBuilder.append("ca_obj_level_t.fin_cons_obj_cd,\n");
       sqlBuilder.append("ca_object_code_t.fin_obj_level_cd\n");
       sqlBuilder.append("UNION ALL\n");
       sqlBuilder.append("SELECT\n");
       sqlBuilder.append("?,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_org_fin_coa,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_org_cd,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_sub_fund_grp,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.fin_coa_cd,");
       sqlBuilder.append("'B',\n");
       sqlBuilder.append("ca_obj_consoldtn_t.fin_report_sort_cd,\n");
       sqlBuilder.append("ca_obj_level_t.fin_report_sort_cd,\n");
       sqlBuilder.append("ld_pnd_bcnstr_gl_t.fin_object_cd,\n");
       sqlBuilder.append("sum(ld_pnd_bcnstr_gl_t.acln_annl_bal_amt),\n");
       sqlBuilder.append("sum(ld_pnd_bcnstr_gl_t.fin_beg_bal_ln_amt),\n");
       sqlBuilder.append("ca_obj_level_t.fin_cons_obj_cd,\n");
       sqlBuilder.append("ca_object_code_t.fin_obj_level_cd,\n");
       sqlBuilder.append("0,0,0,0\n");
       sqlBuilder.append("   FROM ld_bcn_subfund_pick_t,\n");
       sqlBuilder.append("        ld_bcn_ctrl_list_t,\n");
       sqlBuilder.append("        ld_pnd_bcnstr_gl_t,\n");
       sqlBuilder.append("        ca_object_code_t,\n");
       sqlBuilder.append("        ca_obj_level_t,\n");
       sqlBuilder.append("        ca_obj_consoldtn_t\n");
       sqlBuilder.append("  WHERE (ld_bcn_subfund_pick_t.person_unvl_id = ?)\n"); 
       sqlBuilder.append("    AND (ld_bcn_subfund_pick_t.report_flag > 0)\n");
       sqlBuilder.append("    AND (ld_bcn_subfund_pick_t.sub_fund_grp_cd = ld_bcn_ctrl_list_t.sel_sub_fund_grp)\n"); 
       sqlBuilder.append("    AND (ld_bcn_subfund_pick_t.person_unvl_id = ld_bcn_ctrl_list_t.person_unvl_id)\n");
       sqlBuilder.append("    AND (ca_obj_consoldtn_t.fin_coa_cd = ca_obj_level_t.fin_coa_cd)\n");
       sqlBuilder.append("    AND (ca_obj_consoldtn_t.fin_cons_obj_cd = ca_obj_level_t.fin_cons_obj_cd)\n");
       sqlBuilder.append("    AND (ld_pnd_bcnstr_gl_t.fdoc_nbr = ld_bcn_ctrl_list_t.fdoc_nbr)\n");
       sqlBuilder.append("    AND (ca_object_code_t.univ_fiscal_yr = ld_bcn_ctrl_list_t.univ_fiscal_yr)\n");
       sqlBuilder.append("    AND (ca_object_code_t.fin_coa_cd = ld_bcn_ctrl_list_t.fin_coa_cd)\n");
       sqlBuilder.append("    AND (ca_object_code_t.fin_object_cd = ld_pnd_bcnstr_gl_t.fin_object_cd)\n");
       sqlBuilder.append("    AND (ca_obj_level_t.fin_coa_cd = ca_object_code_t.fin_coa_cd)\n");
       sqlBuilder.append("    AND (ca_obj_level_t.fin_obj_level_cd = ca_object_code_t.fin_obj_level_cd)\n");
       sqlBuilder.append("    AND (ld_pnd_bcnstr_gl_t.fin_obj_typ_cd IN ");
       // expenditure object type IN list
       insertionPoints.add(sqlBuilder.length());
       sqlBuilder.append(")\n");
       sqlBuilder.append("GROUP BY ld_bcn_ctrl_list_t.sel_org_fin_coa,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_org_cd,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.sel_sub_fund_grp,\n");
       sqlBuilder.append("ld_bcn_ctrl_list_t.fin_coa_cd,\n");
       sqlBuilder.append("ca_obj_consoldtn_t.fin_report_sort_cd,\n");
       sqlBuilder.append("ca_obj_level_t.fin_report_sort_cd,\n"); 
       sqlBuilder.append("ld_pnd_bcnstr_gl_t.fin_object_cd,\n");
       sqlBuilder.append("ca_obj_level_t.fin_cons_obj_cd,\n");
       sqlBuilder.append("ca_object_code_t.fin_obj_level_cd)\n");
       
       objectSummarySql.add(new SQLForStep(sqlBuilder,insertionPoints));
       sqlBuilder.delete(0,sqlBuilder.length());
       insertionPoints.clear();
       
       // SQL to get the FTE amounts from appointment funding that match with the expenditure
      sqlBuilder.append("INSERT INTO ld_build_objtsumm01_mt\n");
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
      sqlBuilder.append(" FROM ld_subfund_pick_t pick,\n");
      sqlBuilder.append("      ld_bcn_ctrl_list_t ctrl,\n");
      sqlBuilder.append("      ld_pndbc_apptfnd_t bcaf,\n");
      sqlBuilder.append("      ca_object_code_t objt,\n");
      sqlBuilder.append("      ca_obj_level_t objl\n");
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
      sqlBuilder.append("UPDATE ld_bcn_objt_summ_t\n");
      sqlBuilder.append("SET appt_rqcsf_fte_qty =\n");
      sqlBuilder.append("  (SELECT SUM(fq.appt_rqcsf_fte_qty)\n");
      sqlBuilder.append("   FROM ld_build_objtsumm01_mt fq\n");
      sqlBuilder.append("   WHERE ld_bcn_objt_summ_t.person_unvl_id = ?\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.org_fin_coa_cd = fq.org_fin_coa_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.org_cd = fq.org_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.sub_fund_grp_cd = fq.sub_fund_grp_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.fin_coa_cd = fq.fin_coa_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.inc_exp_cd = fq.inc_exp_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.fin_cons_obj_cd = fq.fin_cons_obj_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.fin_obj_level_cd = fq.fin_obj_level_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.fin_object_cd = fq.fin_object_cd\n");
      sqlBuilder.append("     AND fq.sesid = ?),\n"); 
      sqlBuilder.append("   appt_rqst_fte_qty =\n");
      sqlBuilder.append("  (SELECT  SUM(fq.appt_rqst_fte_qty)\n");
      sqlBuilder.append("   FROM ld_build_objtsumm01_mt fq\n");
      sqlBuilder.append("   WHERE ld_bcn_objt_summ_t.person_unvl_id = ?\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.org_fin_coa_cd = fq.org_fin_coa_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.org_cd = fq.org_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.sub_fund_grp_cd = fq.sub_fund_grp_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.fin_coa_cd = fq.fin_coa_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.inc_exp_cd = fq.inc_exp_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.fin_cons_obj_cd = fq.fin_cons_obj_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.fin_obj_level_cd = fq.fin_obj_level_cd\n");
      sqlBuilder.append("     AND ld_bcn_objt_summ_t.fin_object_cd = fq.fin_object_cd\n");
      sqlBuilder.append("     AND fq.sesid = ?)\n");
      sqlBuilder.append("WHERE ld_bcn_objt_summ_t.person_unvl_id = ?\n");
      sqlBuilder.append("  AND EXISTS (SELECT 1\n");
      sqlBuilder.append("              FROM ld_build_objtsumm01_mt fq2\n");
      sqlBuilder.append("              WHERE ld_bcn_objt_summ_t.person_unvl_id = ?\n");
      sqlBuilder.append("                AND ld_bcn_objt_summ_t.org_fin_coa_cd = fq2.org_fin_coa_cd\n");
      sqlBuilder.append("                AND ld_bcn_objt_summ_t.org_cd = fq2.org_cd\n");
      sqlBuilder.append("                AND ld_bcn_objt_summ_t.sub_fund_grp_cd = fq2.sub_fund_grp_cd\n");
      sqlBuilder.append("                AND ld_bcn_objt_summ_t.fin_coa_cd = fq2.fin_coa_cd\n");
      sqlBuilder.append("                AND ld_bcn_objt_summ_t.inc_exp_cd = fq2.inc_exp_cd\n");
      sqlBuilder.append("                AND ld_bcn_objt_summ_t.fin_cons_obj_cd = fq2.fin_cons_obj_cd\n");
      sqlBuilder.append("                AND ld_bcn_objt_summ_t.fin_obj_level_cd = fq2.fin_obj_level_cd\n");
      sqlBuilder.append("                AND ld_bcn_objt_summ_t.fin_object_cd = fq2.fin_object_cd\n");
      sqlBuilder.append("                AND fq2.sesid = ?)");
 
      objectSummarySql.add(new SQLForStep(sqlBuilder));
      sqlBuilder.delete(0,sqlBuilder.length());
      
      // sum the base (CSF for the current year) FTE into a holding table
      sqlBuilder.append("INSERT INTO ld_build_objtsumm02_mt\n");
      sqlBuilder.append("(SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD,\n");
      sqlBuilder.append(" FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, FIN_OBJECT_CD, POS_CSF_FNDSTAT_CD,\n");
      sqlBuilder.append(" POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY)\n");
      sqlBuilder.append("(SELECT\n"); 
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
      sqlBuilder.append("FROM ld_subfund_pick_t pick,\n");
      sqlBuilder.append("  ld_bcn_ctrl_list_t ctrl,\n");
      sqlBuilder.append("  ld_bcn_csf_trckr_t bcsf,\n");
      sqlBuilder.append("  ca_object_code_t objt,\n");
      sqlBuilder.append("  ca_obj_level_t objl\n");
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
      sqlBuilder.append("FROM ld_subfund_pick_t pick,\n");
      sqlBuilder.append("    ld_bcn_ctrl_list_t ctrl,\n");
      sqlBuilder.append("    ld_bcn_csf_trckr_t bcsf,\n");
      sqlBuilder.append("    ca_object_code_t objt,\n");
      sqlBuilder.append("    ca_obj_level_t objl\n");
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
      sqlBuilder.append("    bcsf.fin_object_cd)\n");
      
      objectSummarySql.add(new SQLForStep(sqlBuilder,insertionPoints));
      sqlBuilder.delete(0,sqlBuilder.length());
      insertionPoints.clear();
      
      // update the base FTE in the reporting table using the holding table values. (PostGreSQL supposedly does not allow the target table in an UPDATE to be aliased.  Gennick, p.159.) 
      sqlBuilder.append("UPDATE ld_bcn_objt_summ_t\n"); 
      sqlBuilder.append("SET ld_bcn_objt_summ_t.pos_csf_fte_qty =\n");
      sqlBuilder.append("        (SELECT SUM(fq.pos_csf_fte_qty)\n");
      sqlBuilder.append("         FROM ld_build_objtsumm02_mt fq\n");
      sqlBuilder.append("         WHERE ld_bcn_objt_summ_t.person_unvl_id = ?\n");
      sqlBuilder.append("           AND ld_bcn_objt_summ_t.org_fin_coa_cd = fq.org_fin_coa_cd\n");
      sqlBuilder.append("           AND ld_bcn_objt_summ_t.org_cd = fq.org_cd\n");
      sqlBuilder.append("           AND ld_bcn_objt_summ_t.sub_fund_grp_cd = fq.sub_fund_grp_cd\n");
      sqlBuilder.append("           AND ld_bcn_objt_summ_t.fin_coa_cd = fq.fin_coa_cd\n");
      sqlBuilder.append("           AND ld_bcn_objt_summ_t.inc_exp_cd = fq.inc_exp_cd\n");
      sqlBuilder.append("           AND ld_bcn_objt_summ_t.fin_cons_obj_cd = fq.fin_cons_obj_cd\n");
      sqlBuilder.append("           AND ld_bcn_objt_summ_t.fin_obj_level_cd = fq.fin_obj_level_cd\n");
      sqlBuilder.append("           AND ld_bcn_objt_summ_t.fin_object_cd = fq.fin_object_cd\n");
      sqlBuilder.append("           AND fq.sesid = ?),\n"); 
      sqlBuilder.append("     ld_bcn_objt_summ_t.pos_csf_lv_fte_qty) =\n");
      sqlBuilder.append("         (SELECT SUM(fq.pos_csf_lv_fte_qty)\n");
      sqlBuilder.append("          FROM ld_build_objtsumm02_mt fq\n");
      sqlBuilder.append("          WHERE ld_bcn_objt_summ_t.person_unvl_id = ?\n");
      sqlBuilder.append("            AND ld_bcn_objt_summ_t.org_fin_coa_cd = fq.org_fin_coa_cd\n");
      sqlBuilder.append("            AND ld_bcn_objt_summ_t.org_cd = fq.org_cd\n");
      sqlBuilder.append("            AND ld_bcn_objt_summ_t.sub_fund_grp_cd = fq.sub_fund_grp_cd\n");
      sqlBuilder.append("            AND ld_bcn_objt_summ_t.fin_coa_cd = fq.fin_coa_cd\n");
      sqlBuilder.append("            AND ld_bcn_objt_summ_t.inc_exp_cd = fq.inc_exp_cd\n");
      sqlBuilder.append("            AND ld_bcn_objt_summ_t.fin_cons_obj_cd = fq.fin_cons_obj_cd\n");
      sqlBuilder.append("            AND ld_bcn_objt_summ_t.fin_obj_level_cd = fq.fin_obj_level_cd\n");
      sqlBuilder.append("            AND ld_bcn_objt_summ_t.fin_object_cd = fq.fin_object_cd\n");
      sqlBuilder.append("            AND fq.sesid = ?)\n"); 
      sqlBuilder.append("    WHERE ld_bcn_objt_summ_t.person_unvl_id = ?\n");
      sqlBuilder.append("      AND EXISTS (SELECT 1\n");
      sqlBuilder.append("                  FROM ld_build_objtsumm02_mt fq2\n");
      sqlBuilder.append("                  WHERE ld_bcn_objt_summ_t.person_unvl_id = ?\n");
      sqlBuilder.append("                    AND ld_bcn_objt_summ_t.org_fin_coa_cd = fq2.org_fin_coa_cd\n");
      sqlBuilder.append("                    AND ld_bcn_objt_summ_t.org_cd = fq2.org_cd\n");
      sqlBuilder.append("                    AND ld_bcn_objt_summ_t.sub_fund_grp_cd = fq2.sub_fund_grp_cd\n");
      sqlBuilder.append("                    AND ld_bcn_objt_summ_t.fin_coa_cd = fq2.fin_coa_cd\n");
      sqlBuilder.append("                    AND ld_bcn_objt_summ_t.inc_exp_cd = fq2.inc_exp_cd\n");
      sqlBuilder.append("                    AND ld_bcn_objt_summ_t.fin_cons_obj_cd = fq2.fin_cons_obj_cd\n");
      sqlBuilder.append("                    AND ld_bcn_objt_summ_t.fin_obj_level_cd = fq2.fin_obj_level_cd\n");
      sqlBuilder.append("                    AND ld_bcn_objt_summ_t.fin_object_cd = fq2.fin_object_cd\n");
      sqlBuilder.append("                    AND fq2.sesid = ?)");

      objectSummarySql.add(new SQLForStep(sqlBuilder));
      sqlBuilder.delete(0,sqlBuilder.length());
 
      
    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao#cleanGeneralLedgerObjectSummaryTable(java.lang.String)
     */
    public void cleanGeneralLedgerObjectSummaryTable(String personUserIdentifier) {
        this.clearTempTableByUnvlId("LD_BCN_OBJT_SUMM_T","PERSON_UNVL_ID",personUserIdentifier);
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao#updateGeneralLedgerObjectSummaryTable(java.lang.String)
     */
    @RawSQL
    public void updateGeneralLedgerObjectSummaryTable(String personUserIdentifier) {
        String  idForSession      = (new Guid()).toString();
        ArrayList<String> inLists = new ArrayList<String>(2);
        inLists.add(this.getRevenueINList());
        inLists.add(this.getExpenditureINList());
        
        // get rid of anything left over from the last time this user ran this report
        cleanGeneralLedgerObjectSummaryTable(personUserIdentifier);

        // insert the general ledger amounts into the report table, with 0 placeholders for the FTE
        getSimpleJdbcTemplate().update(objectSummarySql.get(0).getSQL(inLists),personUserIdentifier,personUserIdentifier,personUserIdentifier,personUserIdentifier);

        // sum up the FTE from the appointment funding and stick it in a holding table
        getSimpleJdbcTemplate().update(objectSummarySql.get(1).getSQL(),idForSession,personUserIdentifier);

        // set the FTE in the report table using the appointment funding FTE from the holding table
        getSimpleJdbcTemplate().update(objectSummarySql.get(2).getSQL(),personUserIdentifier,idForSession,personUserIdentifier,idForSession,personUserIdentifier,personUserIdentifier,idForSession);

        // sum up the FTE from the CSF tracker (base funding) table and stick it in a holding table
        ArrayList<String> csfLeaveIndicator = new ArrayList<String>(3);
        csfLeaveIndicator.add(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue());
        csfLeaveIndicator.add(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue());
        csfLeaveIndicator.add(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue());
        getSimpleJdbcTemplate().update(objectSummarySql.get(3).getSQL(csfLeaveIndicator),idForSession,personUserIdentifier,idForSession,personUserIdentifier);
        
        // set the CSF FTE in the report table using the FTE from the holding table
        getSimpleJdbcTemplate().update(objectSummarySql.get(4).getSQL(),personUserIdentifier,idForSession,personUserIdentifier,idForSession,personUserIdentifier,personUserIdentifier,idForSession);
        
        // clean out this session's rows from the holding tables used
        this.clearTempTableBySesId("LD_BUILD_OBJTSUMM01_MT","SESID",idForSession);
        this.clearTempTableBySesId("LD_BUILD_OBJTSUMM02_MT","SESID",idForSession);
        
    }

}
