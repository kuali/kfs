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
import java.util.UUID;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLevelSummaryReportDao;

/**
 * report general ledger amounts and FTE from the pending budget by object level
 */

public class BudgetConstructionLevelSummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionLevelSummaryReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionLevelSummaryReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsLevelSummaryTable = new ArrayList<SQLForStep>(7);
    protected ArrayList<Integer> insertionPoints = new ArrayList<Integer>(10);


    public BudgetConstructionLevelSummaryReportDaoJdbc() {

        // builds and updates LevelSummaryReports
        StringBuilder sqlText = new StringBuilder(1500);

        /* insert the income records */
        sqlText.append("INSERT INTO LD_BCN_LEVL_SUMM_T \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_SORT_CD, \n");
        sqlText.append(" FIN_LEV_SORT_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, APPT_RQCSF_FTE_QTY, \n");
        sqlText.append(" APPT_RQST_FTE_QTY, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'A', objc.fin_report_sort_cd, \n");
        sqlText.append(" objl.fin_report_sort_cd, sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), objl.fin_cons_obj_cd, objt.fin_obj_level_cd, \n");
        sqlText.append(" 0, 0, 0, 0 \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, LD_BCN_CTRL_LIST_T ctrl, LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T objt, CA_OBJ_LEVEL_T objl, CA_OBJ_CONSOLDTN_T objc \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.fin_obj_typ_cd in ");
        // IN list of revenue object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append(" AND objc.fin_coa_cd = objl.fin_coa_cd \n");
        sqlText.append(" AND objc.fin_cons_obj_cd = objl.fin_cons_obj_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objc.fin_report_sort_cd, \n");
        sqlText.append(" objl.fin_report_sort_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* insert expenditure records with FTE place holders */
        sqlText.append("INSERT INTO LD_BCN_LEVL_SUMM_T \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_SORT_CD, \n");
        sqlText.append(" FIN_LEV_SORT_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, APPT_RQCSF_FTE_QTY, \n");
        sqlText.append(" APPT_RQST_FTE_QTY, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'B', objc.fin_report_sort_cd, \n");
        sqlText.append("  objl.fin_report_sort_cd, sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), objl.fin_cons_obj_cd, objt.fin_obj_level_cd, 0, \n");
        sqlText.append(" 0, 0, 0 \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, LD_BCN_CTRL_LIST_T ctrl, LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T objt, CA_OBJ_LEVEL_T objl, CA_OBJ_CONSOLDTN_T objc \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.fin_obj_typ_cd in ");
        // IN list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append(" AND objc.fin_coa_cd = objl.fin_coa_cd \n");
        sqlText.append(" AND objc.fin_cons_obj_cd = objl.fin_cons_obj_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objc.fin_report_sort_cd, objl.fin_report_sort_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* get the BCAF FTE values */
        sqlText.append("INSERT INTO LD_BCN_BUILD_LEVLSUMM03_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_OBJ_CD, \n");
        sqlText.append(" FIN_OBJ_LEVEL_CD, APPT_RQCSF_FTE_QTY, APPT_RQST_FTE_QTY) \n");
        sqlText.append("SELECT  ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'B', objl.fin_cons_obj_cd, \n");
        sqlText.append(" objt.fin_obj_level_cd, SUM(bcaf.APPT_RQCSF_FTE_QTY), SUM(bcaf.APPT_RQST_FTE_QTY) \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, CA_OBJECT_CODE_T objt, CA_OBJ_LEVEL_T objl \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = bcaf.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* SQL-92 does not allow the target table of an UPDATE to be aliased. Supposedly, PostgreSQL enforces this (Gennick, p.156) */

        /* copy the fte values to the report tables */
        sqlText.append("UPDATE LD_BCN_LEVL_SUMM_T \n");
        sqlText.append("SET appt_rqcsf_fte_qty =\n");
        sqlText.append("(SELECT SUM(fq.appt_rqcsf_fte_qty) \n");
        sqlText.append("FROM LD_BCN_BUILD_LEVLSUMM03_MT fq \n");
        sqlText.append("WHERE LD_BCN_LEVL_SUMM_T.person_unvl_id = ? \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.org_fin_coa_cd = fq.org_fin_coa_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.org_cd = fq.org_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.sub_fund_grp_cd = fq.sub_fund_grp_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.fin_coa_cd = fq.fin_coa_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.inc_exp_cd = fq.inc_exp_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.fin_cons_obj_cd = fq.fin_cons_obj_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.fin_obj_level_cd = fq.fin_obj_level_cd \n");
        sqlText.append(" AND fq.sesid = ?), \n");
        sqlText.append("    appt_rqst_fte_qty = \n");
        sqlText.append("(SELECT SUM(fq.appt_rqst_fte_qty) \n");
        sqlText.append("FROM LD_BCN_BUILD_LEVLSUMM03_MT fq \n");
        sqlText.append("WHERE LD_BCN_LEVL_SUMM_T.person_unvl_id = ? \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.org_fin_coa_cd = fq.org_fin_coa_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.org_cd = fq.org_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.sub_fund_grp_cd = fq.sub_fund_grp_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.fin_coa_cd = fq.fin_coa_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.inc_exp_cd = fq.inc_exp_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.fin_cons_obj_cd = fq.fin_cons_obj_cd \n");
        sqlText.append(" AND LD_BCN_LEVL_SUMM_T.fin_obj_level_cd = fq.fin_obj_level_cd \n");
        sqlText.append(" AND fq.sesid = ?) \n");
        sqlText.append("WHERE LD_BCN_LEVL_SUMM_T.person_unvl_id = ? \n");
        sqlText.append(" AND EXISTS (SELECT * FROM LD_BCN_BUILD_LEVLSUMM03_MT fq2 \n");
        sqlText.append(" WHERE LD_BCN_LEVL_SUMM_T.person_unvl_id = ? \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.org_fin_coa_cd = fq2.org_fin_coa_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.org_cd = fq2.org_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.sub_fund_grp_cd = fq2.sub_fund_grp_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_coa_cd = fq2.fin_coa_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.inc_exp_cd = fq2.inc_exp_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_cons_obj_cd = fq2.fin_cons_obj_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_obj_level_cd = fq2.fin_obj_level_cd \n");
        sqlText.append("  AND fq2.sesid = ? ) \n");

        updateReportsLevelSummaryTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* get the CSF regular FTE */
        sqlText.append("INSERT INTO LD_BCN_BUILD_LEVLSUMM02_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_OBJ_CD, \n");
        sqlText.append(" FIN_OBJ_LEVEL_CD, POS_CSF_FNDSTAT_CD, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'B', objl.fin_cons_obj_cd, \n");
        sqlText.append(" objt.fin_obj_level_cd, NULL, SUM(pos_csf_fte_qty), 0 \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_CSF_TRCKR_T bcsf, CA_OBJECT_CODE_T objt, CA_OBJ_LEVEL_T objl \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND bcsf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcsf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcsf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcsf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND (bcsf.pos_csf_fndstat_cd <> '");
        // CSF funding status code for leave
        insertionPoints.add(sqlText.length());
        sqlText.append("' OR bcsf.pos_csf_fndstat_cd IS NULL) \n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = bcsf.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* get the CSF leave FTE */
        sqlText.append("INSERT INTO LD_BCN_BUILD_LEVLSUMM02_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_OBJ_CD, \n");
        sqlText.append(" FIN_OBJ_LEVEL_CD, POS_CSF_FNDSTAT_CD, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'B', objl.fin_cons_obj_cd, \n");
        sqlText.append(" objt.fin_obj_level_cd, '");
        // CSF funding status code for leave
        insertionPoints.add(sqlText.length());
        sqlText.append("', 0, SUM(pos_csf_fte_qty) \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_CSF_TRCKR_T bcsf, CA_OBJECT_CODE_T objt, CA_OBJ_LEVEL_T objl \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND bcsf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcsf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcsf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcsf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcsf.pos_csf_fndstat_cd = '");
        // CSF funding status code for leave
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = bcsf.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* copy the fte values to the report table */
        sqlText.append("UPDATE LD_BCN_LEVL_SUMM_T \n");
        sqlText.append("SET pos_csf_fte_qty = ");
        sqlText.append("(SELECT SUM(pos_csf_fte_qty) \n");
        sqlText.append(" FROM LD_BCN_BUILD_LEVLSUMM02_MT fq \n");
        sqlText.append(" WHERE LD_BCN_LEVL_SUMM_T.person_unvl_id = ? \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.org_fin_coa_cd = fq.org_fin_coa_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.org_cd = fq.org_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.sub_fund_grp_cd = fq.sub_fund_grp_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_coa_cd = fq.fin_coa_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.inc_exp_cd = fq.inc_exp_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_cons_obj_cd = fq.fin_cons_obj_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_obj_level_cd = fq.fin_obj_level_cd \n");
        sqlText.append("  AND fq.sesid = ?), \n");
        sqlText.append("  pos_csf_lv_fte_qty =\n");
        sqlText.append("(SELECT  SUM(pos_csf_lv_fte_qty)\n");
        sqlText.append(" FROM LD_BCN_BUILD_LEVLSUMM02_MT fq \n");
        sqlText.append(" WHERE LD_BCN_LEVL_SUMM_T.person_unvl_id = ? \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.org_fin_coa_cd = fq.org_fin_coa_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.org_cd = fq.org_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.sub_fund_grp_cd = fq.sub_fund_grp_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_coa_cd = fq.fin_coa_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.inc_exp_cd = fq.inc_exp_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_cons_obj_cd = fq.fin_cons_obj_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_obj_level_cd = fq.fin_obj_level_cd \n");
        sqlText.append("  AND fq.sesid = ?) \n");
        sqlText.append("WHERE LD_BCN_LEVL_SUMM_T.person_unvl_id = ? \n");
        sqlText.append(" AND EXISTS (SELECT 1 FROM LD_BCN_BUILD_LEVLSUMM02_MT fq2 \n");
        sqlText.append(" WHERE LD_BCN_LEVL_SUMM_T.person_unvl_id = ? \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.org_fin_coa_cd = fq2.org_fin_coa_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.org_cd = fq2.org_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.sub_fund_grp_cd = fq2.sub_fund_grp_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_coa_cd = fq2.fin_coa_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.inc_exp_cd = fq2.inc_exp_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_cons_obj_cd = fq2.fin_cons_obj_cd \n");
        sqlText.append("  AND LD_BCN_LEVL_SUMM_T.fin_obj_level_cd = fq2.fin_obj_level_cd \n");
        sqlText.append("  AND fq2.sesid = ?) \n");

        updateReportsLevelSummaryTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());
    }

    @Override
    public void cleanReportsLevelSummaryTable(String principalName) {
        clearTempTableByUnvlId("LD_BCN_LEVL_SUMM_T", "PERSON_UNVL_ID", principalName);
    }

    @Override
    public void updateReportsLevelSummaryTable(String principalName, String expenditureINList, String revenueINList) {

        String idForSession = UUID.randomUUID().toString();

        ArrayList<String> stringsToInsert = new ArrayList<String>(10);

        cleanReportsLevelSummaryTable(principalName);

        // insert revenue by object level from pending budget construction general ledger into the report-by-level table
        stringsToInsert.add(revenueINList);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable.get(0).getSQL(stringsToInsert), principalName, principalName);
        // insert expenditure by object level from pending budget construction general ledger into the report-by-level table
        stringsToInsert.clear();
        stringsToInsert.add(expenditureINList);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable.get(1).getSQL(stringsToInsert), principalName, principalName);
        // sum the FTE from appointment funding
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable.get(2).getSQL(), idForSession, principalName);
        // update the appointment FTE in the report-by-level table
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable.get(3).getSQL(), principalName, idForSession, principalName, idForSession, principalName, principalName, idForSession);
        // sum the non-leave FTE from the CSF
        stringsToInsert.clear();
        stringsToInsert.add(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue());
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable.get(4).getSQL(stringsToInsert), idForSession, principalName);
        // sum the FTE for leaves from the CSF (the leave flag is used twice in this SQL, so just add it again to stringsToInsert)
        stringsToInsert.add(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue());
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable.get(5).getSQL(stringsToInsert), idForSession, principalName);
        // update all the CSF FTE fields in the report-by-level table
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable.get(6).getSQL(), principalName, idForSession, principalName, idForSession, principalName, principalName, idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_LEVLSUMM02_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_LEVLSUMM03_MT", "SESID", idForSession);

    }

}
